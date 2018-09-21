package webcrab.pipeline;

import webcrab.convert.TaobaoeFxgConvert;
import webcrab.fangxingou.FangxingouService;
import webcrab.fangxingou.module.po.SpecAddResult;
import webcrab.storage.ProductRepository;
import webcrab.taobao.TaobaoCrawler;
import webcrab.taobao.dao.TaobaoItemFileDao;
import webcrab.taobao.model.TaobaoItem;

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
     * 列出当前放心购SPEC列表
     */
    private void stepFxgSpecList() {
        fangxingouService.specList();
        fangxingouService.specDetail("3240788");
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
        SpecAddResult result = fangxingouService.specAdd(specsUpload, "AAA测试规格");
    }

    @Override
    public void doAllSteps() {
        stepFxgSpecList();
        stepCrawItemsAndSave();
        //stepUploadToFxg();
    }
}
