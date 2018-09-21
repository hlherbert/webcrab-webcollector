package webcrab.convert;

import webcrab.taobao.model.TaobaoItem;

import java.util.Map;

public class TaobaoeFxgConvert {

    public static String taobao2FxgSpecs(TaobaoItem taobaoItem) {
        Map<String, String> basicInfoMap = taobaoItem.getBasicInfoMap();
        if (basicInfoMap == null) {
            return null;
        }
        StringBuffer specs = new StringBuffer();
        for (Map.Entry<String,String> entry: basicInfoMap.entrySet()) {
            String spec = entry.getKey()+"|"+entry.getValue().replace(" ",",");
            specs.append(spec+"^");
        }
        return specs.substring(0,specs.length()-1); // 去掉最后的^
    }
}
