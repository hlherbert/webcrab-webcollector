package webcrab.convert;

import org.springframework.stereotype.Component;
import webcrab.conf.SellerProperties;
import webcrab.fangxingou.module.*;
import webcrab.taobao.model.TaobaoItem;
import webcrab.taobao.model.TaobaoSpec;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 淘宝转放心购
 */
@Component
public class TaobaoeFxgConvert {

    private static SellerProperties sellerProperties = SellerProperties.getInstance();

    /**
     * 淘宝规格转放心狗
     *
     * @param taobaoItem 淘宝商品信息
     * @return 放心购的商品信息格式 颜色|黑色,白色,黄色^尺码|S,M,L
     * 多规格用^分隔，父规格与子规格用|分隔，子规格用,分隔
     */
    public static String taobao2FxgSpecs(TaobaoItem taobaoItem) {
        List<TaobaoSpec> specs = taobaoItem.getSpecs();
        List<String> specStrs = new ArrayList<>();
        for (TaobaoSpec spec : specs) {
            List<TaobaoSpec> children = spec.getChildSpecs();
            List<String> childrenSpecNames = new ArrayList<>();
            for (TaobaoSpec child : children) {
                childrenSpecNames.add(child.getName());
            }
            String specStr = spec.getName() + "|" + String.join(",", childrenSpecNames);
            specStrs.add(specStr);
        }
        return String.join("^", specStrs);
    }

    /**
     * 淘宝商品转为放心购商品
     *
     * @param item  taobao商品
     * @param specs 生成的放心购商品规格
     * @return 放心购商品
     */
    public static Product taobao2FxgProduct(TaobaoItem item, Specs specs) {
        Product product = new Product();
        product.setName(item.getTitle());

        String pics = String.join("|", item.getPics());
        String descImgs = String.join("|", item.getDetailImgs());

        product.setPic(pics);
        product.setDescription(descImgs);
        product.setOutProductId(item.getId());
        product.setMarketPrice(String.valueOf((long) (item.getPrice() * 100))); //rmb --> fen
        product.setDiscountPrice(String.valueOf((long) (item.getPricePromote() * 100)));
        product.setCosRatio("0"); //佣金率 TODO:不知道佣金怎么填,先填0

        //TODO: 现在不知道原商品分类，设置为其他
        product.setFirstCid(CategoryEnum.OTHER_CID1.getStrId());
        product.setSecondCid(CategoryEnum.OTHER_CID2.getStrId());
        product.setThirdCid(CategoryEnum.OTHER_CID3.getStrId());

        //TODO: 支付方式不确定，暂时只支持在线支付
        product.setPayType(PayTypeEnum.ONLINE.getStrVal());
        product.setSpecId(String.valueOf(specs.getId()));

        //寻找specs中的主规格
        TaobaoSpec taobaoMainSpec = item.getMainSpec();
        String taobaoMainSpecName = taobaoMainSpec.getName();

        List<Spec> childSpecs = specs.getSpecs();
        Spec mainSpec = null;
        for (Spec childSpec : childSpecs) {
            // 比较规格名称是否和主规格一致
            if (taobaoMainSpecName.equals(childSpec.getName())) {
                mainSpec = childSpec;
                break;
            }
        }

        if (mainSpec != null) {
            // 如果有主规格，则设置图片
            List<TaobaoSpec> taobaoSubSpecs = taobaoMainSpec.getChildSpecs();
            HashMap<String, TaobaoSpec> taobaoSubSpecMap = new HashMap<>();
            for (TaobaoSpec taobaoSubSpec : taobaoSubSpecs) {
                taobaoSubSpecMap.put(taobaoSubSpec.getName(), taobaoSubSpec);
            }

            List<Spec> subSpecs = mainSpec.getValues();//红 黄 蓝 等
            List<String> specPicStrs = new ArrayList<>();
            for (Spec subSpec : subSpecs) {
                String subSpecName = subSpec.getName();
                TaobaoSpec taobaoSubSpec = taobaoSubSpecMap.get(subSpecName);
                String subSpecId = String.valueOf(subSpec.getId());
                String subSpecImg = taobaoSubSpec.getImg();
                String specPicStr = subSpecId + "|" + subSpecImg;
                specPicStrs.add(specPicStr);
            }
            String specPic = String.join("^", specPicStrs);
            product.setSpecPic(specPic);
        }

        product.setMobile(sellerProperties.getMobile());

        //读取属性
        Map<String, String> basicInfoMap = item.getBasicInfoMap();
        List<String> props = new ArrayList<>();
        for (Map.Entry<String, String> entry : basicInfoMap.entrySet()) {
            String propName = entry.getKey();
            String propValue = entry.getValue();
            String prop = propName + "|" + propValue;
            props.add(prop);
        }
        product.setProductFormat(String.join("^", props));
        product.setUsp(null); // TODO: 商品卖点，暂时不设置

        product.setRecommendRemark(sellerProperties.getRecommendRemark());
        product.setExtra(sellerProperties.getExtra());

        return product;
    }
}
