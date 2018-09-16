package webcrab.crawler;

import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.plugin.berkeley.BreadthCrawler;
import org.jsoup.nodes.Document;

import java.text.MessageFormat;

/**
 * Crawling news from github news
 *
 * @author hu
 */
public class GithubCrawler extends BreadthCrawler {

    /**
     * @param crawlPath crawlPath is the path of the directory which maintains
     *                  information of this crawler
     * @param autoParse if autoParse is true,BreadthCrawler will auto extract
     *                  links which match regex rules from pag
     */
    public GithubCrawler(String crawlPath, boolean autoParse) {
        super(crawlPath, autoParse);
        /*start page*/

        //加入种子URL　-- depth 1
        this.addSeed("https://blog.github.com/");

        for (int pageIndex = 2; pageIndex <= 5; pageIndex++) {
            String seedUrl = MessageFormat.format("https://blog.github.com/page/{0}/", pageIndex);
            this.addSeed(seedUrl);
        }

        // 设置需要爬的URL
        /*fetch url like http://news.hfut.edu.cn/show-xxxxxxhtml*/
        this.addRegex("https://blog.github.com/[0-9]{4}-[0-9]{2}-[0-9]{2}-[^/]+/");
        /*do not fetch jpg|png|gif*/
        //this.addRegex("-.*\\.(jpg|png|gif).*");
        /*do not fetch url contains #*/
        //this.addRegex("-.*#.*");
    }

    public static void run() throws Exception {
        boolean autoParse = true;
        GithubCrawler crawler = new GithubCrawler("crawl", autoParse);
        crawler.setThreads(50);
        crawler.getConf().setTopN(100);

        //crawler.setResumable(true); //支持断线续爬. 历史数据存到Berkley数据库里了，每个crawPath是个目录，存放历史数据库。
        //如果不开启断点爬取，则每次启动任务后都重建数据库。
        //RamCrawl不支持断点续爬

        /*start crawl with depth of 4*/
        crawler.start(4);
    }

    @Override
    public void visit(Page page, CrawlDatums next) {
        // 从第一轮开始
        String url = page.url();

        /*if page is news page*/
        if (page.matchUrl("https://blog.github.com/[0-9]{4}-[0-9]{2}-[0-9]{2}[^/]+/")) {

            //将url对应的网页获取
            /*we use jsoup to parse page*/
            Document doc = page.doc();

            // 提取网页中的特征元素，输出
            /*extract title and content of news by css selector*/
            String title = page.select("h1[class=1h-condensed]").text();
            String content = page.select("div.content.markdown-body", 0).text();

            System.out.println("URL:\n" + url);
            System.out.println("title:\n" + title);
            System.out.println("content:\n" + content);

            // 对当前网页分析，按照之前配置的url规则，继续寻找新的url.

            /*If you want to add urls to crawl,add them to nextLink*/
            /*WebCollector automatically filters links that have been fetched before*/
        /*If autoParse is true and the link you add to nextLinks does not
          match the regex rules,the link will also been filtered.*/
            //next.add("http://xxxxxx.com");
            //next为下次迭代

            // 如果开启了autoParse, 则这里不用手动添加url到next, Crawler会根据设置的规则，自动提取page中的url加入next.
            // 在此基础上，也可以手动加入url,但是必须符合之前设置的规则，否则会被过滤。

        }
    }

}