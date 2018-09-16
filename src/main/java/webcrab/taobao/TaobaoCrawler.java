package webcrab.taobao;

import cn.edu.hfut.dmic.webcollector.model.CrawlDatum;
import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.plugin.berkeley.BreadthCrawler;
import org.jsoup.select.Elements;
import webcrab.taobao.dao.TaobaoItemFileDao;
import webcrab.taobao.model.TaobaoItem;
import webcrab.util.URLParser;

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
    private static final String OUTPUT_DIR = "out";
    private static final String ITEM_PAGE="ITEM";//商品首页
    private static final String DETAIL_PAGE="DETAIL";//详情页
    private static final String AMP_PAGE="AMP";//商品lite,可以看到优惠价

    private TaobaoItemFileDao dao;
    private Map<String, TaobaoItem> items = new ConcurrentHashMap<>();

    // javascript engine
    //private ScriptEngine jsEngine;

    /**
     * @param crawlPath 爬虫唯一名
     * @param autoParse 是否自动加入下一轮的URL
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

        //ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
        //jsEngine = engine;

    }

    @Override
    public void visit(Page page, CrawlDatums next) {
        String url = page.url();

        /*if page is item page*/
        if (page.matchType(ITEM_PAGE)) {
            String id = URLParser.fromURL(url).getParameter("id");
            //String id = page.url().replace("https://item.taobao.com/item.htm?id=","");
            System.out.println("id="+id);

            // 宝贝一般信息
            String title = page.selectText("div#J_Title>h3");
            Integer stock = page.selectInt("span#J_SpanStock");
            Double price = page.selectDouble("strong#J_StrPrice>em.tb-rmb-num");
            Elements basicInfoEles = page.select("div#attributes>ul>li");

            // 宝贝详情
            // 是通过javascript加载的，不能直接从html获取到，因此需要先得到详情URL，然后发请求再获取
            String descUrlJson = page.regex(".*\\bdescUrl\\s*:.*,");
            int startDescHttpUrl = descUrlJson.lastIndexOf("?");
            int endDescHttpsUrl = descUrlJson.lastIndexOf(":");
            String descHttpUri = descUrlJson.substring(startDescHttpUrl,endDescHttpsUrl);
            startDescHttpUrl = descHttpUri.indexOf("'");
            endDescHttpsUrl = descHttpUri.lastIndexOf("'");
            descHttpUri = descHttpUri.substring(startDescHttpUrl+1, endDescHttpsUrl);
            descHttpUri = "http:"+descHttpUri;
            String detail = descHttpUri;

            // 组装宝贝对象
            TaobaoItem taobaoItem = new TaobaoItem();
            taobaoItem.setId(id);
            taobaoItem.setTaobaoUrl(url);
            taobaoItem.setTitle(title);
            taobaoItem.setPrice(price);
            //taobaoItem.setPricePromote();
            taobaoItem.setStock(stock);

            // 基本信息
            StringBuffer buf = new StringBuffer();
            for (String basicInfo : basicInfoEles.eachText()) {
                buf.append(basicInfo + "\n");
            }
            taobaoItem.setBasicInfo(buf.toString());
            taobaoItem.setDetail(detail);
            items.put(id, taobaoItem);

            //next.add("http://xxxxxx.com");
            //next为下次迭代

            // 下次爬amp页，获取优惠价
            String ampUrl = MessageFormat.format( "https://www.taobao.com/list/item-amp/{0}.html",id);
            CrawlDatum descDatum = new CrawlDatum();
            descDatum.url(ampUrl);
            descDatum.type(AMP_PAGE);
            descDatum.meta("id", id);
            next.add(descDatum);

            // 下次爬详情URL
            descDatum = new CrawlDatum();
            descDatum.url(descHttpUri);
            descDatum.type(DETAIL_PAGE);
            descDatum.meta("id", id);
            next.add(descDatum);
            // 如果开启了autoParse, 则这里不用手动添加url到next, Crawler会根据设置的规则，自动提取page中的url加入next.
            // 在此基础上，也可以手动加入url,但是必须符合之前设置的规则，否则会被过滤。
        } else if (page.matchType(AMP_PAGE)) {
            // lite page, 包含优惠价
            //String content = page.html();//page.select("div#artibody", 0).text();
            String pricePromote = page.selectText("div.price-contianer>div.price");
            pricePromote = pricePromote.replace("¥", "");

            String id = page.meta("id");
            TaobaoItem item = items.get(id);
            if (item == null) {
                return;
            }
            System.out.println("content:\n" + pricePromote);
            item.setPricePromote(Double.valueOf(pricePromote));
        } else if (page.matchType(DETAIL_PAGE)) {
            // 详情页
            String id = page.meta("id");
            TaobaoItem item = items.get(id);
            if (item == null) {
                return;
            }
            String desc = page.html(); // var desc='.....'
            int start = desc.indexOf("'");
            int end = desc.lastIndexOf("'");
            desc = desc.substring(start+1,end);
            item.setDetail(desc);
        }
    }

    /**
     * 输出结果
     */
    public void outputResults() {
        dao.writeHtmlPages(this.items.values(), OUTPUT_DIR);
    }

    public static void run() throws Exception {
        TaobaoItemFileDao dao = new TaobaoItemFileDao();
        TaobaoCrawler crawler = new TaobaoCrawler("taobaoCraw", false, dao);
        crawler.setThreads(50);
        //crawler.getConf().setTopN(100);
        //crawler.setResumable(true);
        /*start crawl with depth of 4*/
        crawler.start(2);
        crawler.outputResults();
    }

}