package webcrab;

import webcrab.fangxingou.FangxingouService;
import webcrab.taobao.TaobaoCrawler;

public class Main {
    public static void main(String[] args) throws Exception {
        //GithubCrawler.run();
        TaobaoCrawler.run();
        FangxingouService.runTest();
    }
}
