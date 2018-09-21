package webcrab.convert;

import webcrab.taobao.model.TaobaoItem;

import java.util.Map;

/**
 * 淘宝转放心购
 */
public class TaobaoeFxgConvert {

    /**
     * 淘宝规格转放心狗
     *
     * @param taobaoItem 淘宝商品信息
     * @return 放心购的商品信息格式 颜色|黑色,白色,黄色^尺码|S,M,L
     * 多规格用^分隔，父规格与子规格用|分隔，子规格用,分隔
     */
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
