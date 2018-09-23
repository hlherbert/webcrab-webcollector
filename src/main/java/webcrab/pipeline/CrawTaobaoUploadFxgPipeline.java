package webcrab.pipeline;

import webcrab.convert.TaobaoeFxgConvert;
import webcrab.fangxingou.FangxingouService;
import webcrab.fangxingou.module.Product;
import webcrab.storage.ProductRepository;
import webcrab.taobao.TaobaoCrawler;
import webcrab.taobao.dao.TaobaoItemFileDao;
import webcrab.taobao.model.TaobaoItem;
import webcrab.util.JsonUtils;

import java.util.List;

/**
 * 爬淘宝商品，并上传到放心购的流水线
 */
public class CrawTaobaoUploadFxgPipeline implements Pipeline{
    TaobaoCrawler taobaoCrawler;
    ProductRepository productRepository = ProductRepository.getInstance();
    FangxingouService fangxingouService = FangxingouService.getInstance();

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
        fangxingouService.specList(); //规格
        fangxingouService.specDetail("3240788"); //规格详情
        //fangxingouService.productCategoryTree();
        fangxingouService.productCategory("0");
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
        String specsUpload = TaobaoeFxgConvert.taobao2FxgSpecs(item);

        //SpecAddResult result = fangxingouService.specAdd(specsUpload, "AAA测试规格");
        String outProductId = item.getId();

        boolean isProductExist = fangxingouService.isProductExist(outProductId);
        if (isProductExist) {
            // 产品已经有了，就不上传了
            return;
        }

        Product product = TaobaoeFxgConvert.taobao2FxgProduct(item);
        //ProductAddParam productParam = new ProductAddParam();
        //fangxingouService.productAdd(productParam);
        System.out.println(JsonUtils.toJson(product));
    }

    @Override
    public void doAllSteps() {
        //stepGetFxgProducts();
        stepCrawItemsAndSave();
        stepUploadToFxg();
    }
}
