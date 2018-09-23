package webcrab;

import webcrab.pipeline.CrawTaobaoUploadFxgPipeline;
import webcrab.pipeline.Pipeline;

public class Main {

    public static void main(String[] args) {
        Pipeline pipeline = new CrawTaobaoUploadFxgPipeline();
        pipeline.doAllSteps();
        //FangxingouService.runTest();
    }
}
