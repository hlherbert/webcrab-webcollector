package webcrab;

import webcrab.core.pipeline.CrawTaobaoUploadFxgPipeline;
import webcrab.core.pipeline.Pipeline;

public class Main {

    public static void main(String[] args) {
        Pipeline pipeline = new CrawTaobaoUploadFxgPipeline();
        pipeline.doAllSteps();
        //FangxingouService.runTest();
    }
}
