package webcrab;

import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.plugin.berkeley.BreadthCrawler;

import java.text.MessageFormat;

/**
 * 每轮迭代，手动增加下轮URL
 *
 * @author hu
 */
public class ManualNewsCrawler extends BreadthCrawler {

    /**
     * @param crawlPath crawlPath is the path of the directory which maintains
     *                  information of this crawler
     * @param autoParse if autoParse is true,BreadthCrawler will auto extract
     *                  links which match regex rules from pag
     */
    public ManualNewsCrawler(String crawlPath, boolean autoParse) {
        super(crawlPath, autoParse);
        /*start page*/

        //加入种子URL　-- depth 1
        //this.addSeed("https://blog.github.com/");
        //this.addSeed("https://blog.github.com/", "list");
        this.addSeedAndReturn("https://blog.github.com/").type("list");

        for (int pageIndex = 2; pageIndex <= 5; pageIndex++) {
            String seedUrl = MessageFormat.format("https://blog.github.com/page/{0}/", pageIndex);
            this.addSeed(seedUrl, "list");
        }


        // 设置需要爬的URL
        /*fetch url like http://news.hfut.edu.cn/show-xxxxxxhtml*/
        //this.addRegex("https://blog.github.com/[0-9]{4}-[0-9]{2}-[0-9]{2}-[^/]+/");
        /*do not fetch jpg|png|gif*/
        //this.addRegex("-.*\\.(jpg|png|gif).*");
        /*do not fetch url contains #*/
        //this.addRegex("-.*#.*");
    }

    public static void run() throws Exception {
        boolean autoParse = false;
        ManualNewsCrawler crawler = new ManualNewsCrawler("manual_crawl", autoParse);
        crawler.setThreads(50);
        //crawler.getConf().setExecuteInterval(5000); //每一轮的间隔

        crawler.getConf().setTopN(100);
        crawler.getConf().set("title_prefix", "TITLE_");
        crawler.getConf().set("content_length_limit", 50);

        //crawler.setResumable(true); //支持断线续爬

        /*start crawl with depth of 4*/
        crawler.start(4);
    }

    @Override
    public void visit(Page page, CrawlDatums next) {

        String url = page.url();
        /*if page is seed page*/
        if (page.matchType("list")) {
            next.add(page.links("h1.lh-condensed>a")).type("content");
        } else if (page.matchType("content")) {
            String title = page.select("h1[class=lh-condensed]").first().text();
            String content = page.selectText("div.content.markdown-body");

            // 正文自动抽取(启发式算法，具有一定不准确性）
//            String content = null;
//            try {
//                content = ContentExtractor.getContentByUrl(url);
//            } catch (Exception e) {
//                content = "ERROR: "+e;
//            }

            title = this.getConf().get("title_prefix") + title;
            //content = content.substring(0, getConf().get("content_length_limit"));

            System.out.println("URL:" + url);
            System.out.println("title:" + title);
            System.out.println("content:" + content);
        }
    }

}