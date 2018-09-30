package webcrab.convert;

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
public class TaobaoeFxgConvert {

    /**
     * 商品轮播图数量限制
     */
    private static final int PRODUCT_PIC_LIMIT = ProductConstant.PRODUCT_PIC_LIMIT;

    /**
     * 卖家信息配置
     */
    private static SellerProperties sellerProperties = SellerProperties.getInstance();

    /**
     * 淘宝规格转放心购规格
     *
     * @param taobaoItem 淘宝商品信息
     * @return 放心购的商品信息格式 颜色|黑色,白色,黄色^尺码|S,M,L
     * 多规格用^分隔，父规格与子规格用|分隔，子规格用,分隔
     */
    public static String taobao2FxgSpecs(TaobaoItem taobaoItem) {
        List<TaobaoSpec> specs = taobaoItem.getSpecs();
        if (specs == null || specs.isEmpty()) {
            return SpecConstant.DEFAULT_SPEC;
        }
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

        // 最多5张
        List<String> picsLimit = item.getPics().subList(0, PRODUCT_PIC_LIMIT);
        String pics = String.join("|", picsLimit);
        String descImgs = String.join("|", item.getDetailImgs());

        product.setPic(pics);
        product.setDescription(descImgs);
        product.setOutProductId(item.getId());

        if (item.getPrice() != null) {
            product.setMarketPrice(String.valueOf((long) (item.getPrice() * 100))); //rmb --> fen
        }
        if (item.getPricePromote() != null) {
            product.setDiscountPrice(String.valueOf((long) (item.getPricePromote() * 100)));
        }
        product.setCosRatio("0"); //佣金率 TODO:不知道佣金怎么填,先填0

        //TODO: 现在不知道原商品分类，设置为其他
        product.setFirstCid(CategoryEnum.OTHER_CID1.getStrId());
        product.setSecondCid(CategoryEnum.OTHER_CID2.getStrId());
        product.setThirdCid(CategoryEnum.OTHER_CID3.getStrId());

        //TODO: 支付方式不确定，暂时只支持在线支付
        product.setPayType(PayTypeEnum.ONLINE.getStrVal());
        product.setSpecId(String.valueOf(specs.getId()));

        // 将规格设置到放心购产品里面，并解析出主规格和图片
        extractTaobaoSpecs2FxgProduct(item, specs, product);

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
        product.setUsp(item.getHotDesc()); // 商品卖点=热门描述

        product.setRecommendRemark(sellerProperties.getRecommendRemark());
        product.setExtra(sellerProperties.getExtra());

        return product;
    }

    /**
     * 将规格设置到放心购产品里面，并解析出主规格和图片
     *
     * @param item    淘宝产品
     * @param specs   放心购规格，应该是通过新增规格API返回的规格对象
     * @param product 放心购产品
     */
    private static void extractTaobaoSpecs2FxgProduct(TaobaoItem item, Specs specs, Product product) {
        //寻找specs中的主规格
        Spec mainSpec = null;
        TaobaoSpec taobaoMainSpec = item.getMainSpec();
        if (taobaoMainSpec != null) {
            String taobaoMainSpecName = taobaoMainSpec.getName();
            List<Spec> childSpecs = specs.getSpecs();
            for (Spec childSpec : childSpecs) {
                // 比较规格名称是否和主规格一致
                if (taobaoMainSpecName.equals(childSpec.getName())) {
                    mainSpec = childSpec;
                    break;
                }
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

                // 有可能朱规格没有图片
                if (subSpecImg == null) {
                    continue;
                }

                String specPicStr = subSpecId + "|" + subSpecImg;
                specPicStrs.add(specPicStr);
            }
            if (specPicStrs != null && !specPicStrs.isEmpty()) {
                String specPic = String.join("^", specPicStrs);
                product.setSpecPic(specPic);
            }
        }
    }

    /**
     * 淘宝产品中找到sku，并转换为fxg的sku
     * @param item
     * @param product
     * @param specs
     * @return
     */
    public static List<Sku> taobao2FxgSku(TaobaoItem item, Product product, Specs specs) {
        List<Sku> skuList = new ArrayList<Sku>();
//        Sku sku = new Sku();
//        sku.setOutProductId(product.getOutProductId());
//        sku.setOutSkuId();
        return skuList;
    }
}
