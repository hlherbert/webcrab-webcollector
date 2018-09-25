package webcrab.pipeline;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webcrab.convert.TaobaoeFxgConvert;
import webcrab.fangxingou.FangxingouService;
import webcrab.fangxingou.module.Product;
import webcrab.fangxingou.module.SpecConstant;
import webcrab.fangxingou.module.Specs;
import webcrab.fangxingou.module.po.SpecAddResult;
import webcrab.fangxingou.module.po.SpecListResult;
import webcrab.storage.ProductRepository;
import webcrab.taobao.TaobaoCrawler;
import webcrab.taobao.dao.TaobaoItemFileDao;
import webcrab.taobao.model.TaobaoItem;
import webcrab.util.JsonUtils;

import java.text.MessageFormat;
import java.util.List;

/**
 * 爬淘宝商品，并上传到放心购的流水线
 */
public class CrawTaobaoUploadFxgPipeline implements Pipeline {

    /**
     * 上次规格时，规格名前缀
     */
    private static final String SPEC_PREFIX = SpecConstant.FXG_SPEC_NAME_PREFIX;
    private static Logger logger = LoggerFactory.getLogger(CrawTaobaoUploadFxgPipeline.class);
    private TaobaoCrawler taobaoCrawler;
    private ProductRepository productRepository = ProductRepository.getInstance();
    private FangxingouService fangxingouService = FangxingouService.getInstance();

    /**
     * 为淘宝商品取规格名
     *
     * @param outProductId 产品淘宝ID
     * @param nRetry       重试次数
     * @return {PREFIX}_{outProductId}_{nRetry}
     */
    private static String makeSpecName(String outProductId, int nRetry) {
        return SPEC_PREFIX + outProductId + "_" + nRetry;
    }

    /**
     * 从淘宝爬产品
     */
    private void stepCrawItemsAndSave() {
        TaobaoItemFileDao dao = new TaobaoItemFileDao();
        taobaoCrawler = new TaobaoCrawler("taobaoCraw", false, dao);
        taobaoCrawler.setThreads(50);
        //crawler.getConf().setTopN(100);
        //crawler.setResumable(true);
        /*start crawl with depth of 4*/
        try {
            taobaoCrawler.start(2);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        taobaoCrawler.outputResults();
        productRepository.setItems(taobaoCrawler.getItemList());
    }

    /**
     * 列出当前放心购当前商品信息
     */
    private void stepGetFxgProducts() {
        // 存入当前规格列表
        SpecListResult specList = fangxingouService.specList(); //规格
        productRepository.setSpecIndexList(specList.getData());

        //fangxingouService.specDetail("3240788"); //规格详情
        //fangxingouService.productCategoryTree();
        //fangxingouService.productCategory("0");

        //详细商品
        String outProductId = "574378898908";
        fangxingouService.productDetail(outProductId);

        //SKU
        fangxingouService.skuList(outProductId);
    }

    /**
     * 上传产品到放心购
     */
    private void stepUploadToFxg() {
        List<TaobaoItem> items = productRepository.getItems();
        if (items.isEmpty()) {
            return;
        }
        TaobaoItem item = items.get(0);


        String outProductId = item.getId();
        boolean isProductExist = fangxingouService.isProductExist(outProductId);
        if (isProductExist) {
            // 产品已经有了，就不上传了
            logger.error(MessageFormat.format("product {0} has already exist", outProductId));
            return;
        }

        // 按照一定的规则构造规则名称
        String specName = makeSpecName(outProductId, 0);
        Specs specsAdded = null;
        int nRetry = 0; //遇到重名的spec次数，如果规格重名了，则后缀增加_1, _2 等
        while (productRepository.isSpecNameExist(specName)) {
            //规格已经有了，就重命名规格
            logger.error(MessageFormat.format("spec {0} has already exist. create new name", specName));
            specName = makeSpecName(outProductId, ++nRetry);
        }

        // 规格没有，则创建规格上传
        String specsUpload = TaobaoeFxgConvert.taobao2FxgSpecs(item);
        System.out.println(specsUpload);
        SpecAddResult specAddResult = fangxingouService.specAdd(specsUpload, specName);
        specsAdded = specAddResult.getData();


        Product product = TaobaoeFxgConvert.taobao2FxgProduct(item, specsAdded);
        fangxingouService.productAdd(product);
        System.out.println(JsonUtils.toJson(product));
    }

    @Override
    public void doAllSteps() {
        stepGetFxgProducts();
        stepCrawItemsAndSave();
        stepUploadToFxg();
    }
}
