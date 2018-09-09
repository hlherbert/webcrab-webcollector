package webcrab;

import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.plugin.berkeley.BreadthCrawler;
import webcrab.dao.TaobaoItemFileDao;
import webcrab.model.TaobaoItem;

import java.text.MessageFormat;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * taobao商品抓取
 * https://item.taobao.com/item.htm?id=574378898908
 * https://www.taobao.com/list/item-amp/574378898908.htm
 * 标题、价格、淘宝价、尺寸、分类、库存、宝贝详情、宝贝描述
 *
 * @author hu
 */
public class TaobaoCrawler extends BreadthCrawler {
    private static final String ITEM_PAGE="ITEM";
    private static final String AMP_PAGE="AMP";

    private TaobaoItemFileDao dao;
    private Map<Integer, TaobaoItem> items = new ConcurrentHashMap<>();
    /**
     * @param crawlPath crawlPath is the path of the directory which maintains
     *                  information of this crawler
     * @param autoParse if autoParse is true,BreadthCrawler will auto extract
     *                  links which match regex rules from pag
     */
    public TaobaoCrawler(String crawlPath, boolean autoParse, TaobaoItemFileDao dao) {
        super(crawlPath, autoParse);

        this.dao = dao;
        /*start page*/
        this.addSeed("https://item.taobao.com/item.htm?id=574378898908", ITEM_PAGE);

        /*fetch url like http://news.hfut.edu.cn/show-xxxxxxhtml*/
        //this.addRegex("https://www.taobao.com/list/item-amp/.*htm");
        /*do not fetch jpg|png|gif*/
        //this.addRegex("-.*\\.(jpg|png|gif).*");
        /*do not fetch url contains #*/
        //this.addRegex("-.*#.*");
    }

    @Override
    public void visit(Page page, CrawlDatums next) {
        String url = page.url();

        /*if page is item page*/
        if (page.matchType(ITEM_PAGE)) {
            String id = page.url().replace("https://item.taobao.com/item.htm?id=","");
            System.out.println("id="+id);

            /*we use jsoup to parse page*/
            //Document doc = page.doc();

            /*extract title and content of news by css selector*/
            String title = page.selectText("div[id=J_Title]>h3");
            Integer stock = page.selectInt("span[id=J_SpanStock]");
            Double price = page.selectDouble("strong[id=J_StrPrice]>em[class=tb-rmb-num]");


            String content = page.html();//page.select("div#artibody", 0).text();
            TaobaoItem taobaoItem = new TaobaoItem();
            taobaoItem.setId(id);
            taobaoItem.setTaobaoUrl(url);
            taobaoItem.setTitle(title);
            taobaoItem.setPrice(price);
            //taobaoItem.setPricePromote();
            taobaoItem.setStock(stock);
            //taobaoItem.setBasicInfo();
            //taobaoItem.setDetail();


            dao.write(taobaoItem);

            /*If you want to add urls to crawl,add them to nextLink*/
            /*WebCollector automatically filters links that have been fetched before*/
        /*If autoParse is true and the link you add to nextLinks does not
          match the regex rules,the link will also been filtered.*/
            //next.add("http://xxxxxx.com");
            //next为下次迭代

            String ampUrl = MessageFormat.format( "https://www.taobao.com/list/item-amp/{0}.html",id);
            next.add(ampUrl,AMP_PAGE);

            // 如果开启了autoParse, 则这里不用手动添加url到next, Crawler会根据设置的规则，自动提取page中的url加入next.
            // 在此基础上，也可以手动加入url,但是必须符合之前设置的规则，否则会被过滤。
        } else if (page.matchType(AMP_PAGE)) {
            // lite page
            String content = page.html();//page.select("div#artibody", 0).text();
            System.out.println("content:\n" + content);
        }
    }

    public static void run() throws Exception {
        TaobaoItemFileDao dao = new TaobaoItemFileDao();
        TaobaoCrawler crawler = new TaobaoCrawler("taobaoCraw", false, dao);
        crawler.setThreads(50);
        //crawler.getConf().setTopN(100);
        //crawler.setResumable(true);
        /*start crawl with depth of 4*/

        dao.open("taobao.txt");
        crawler.start(2);
        dao.close();
    }

}