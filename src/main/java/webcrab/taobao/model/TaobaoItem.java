package webcrab.taobao.model;

import java.util.List;
import java.util.Map;

public class TaobaoItem {
    //标题、价格、淘宝价、尺寸、分类、库存、宝贝详情、宝贝描述

    // taobao url
    private String taobaoUrl;
    //id
    private String id;
    //标题
    private String title;

    //商品轮播图
    private List<String> pics;

    //原价
    private Double price;
    //优惠价(淘宝价)
    private Double pricePromote;
    //库存
    private Integer stock;
    //基本信息(分类、尺寸等）
    private String basicInfo;
    //基本信息表
    private Map<String, String> basicInfoMap;

    //详情
    private String detail;
    //详情图片集
    private List<String> detailImgs;

    public String getTaobaoUrl() {
        return taobaoUrl;
    }

    public void setTaobaoUrl(String taobaoUrl) {
        this.taobaoUrl = taobaoUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getPics() {
        return pics;
    }

    public void setPics(List<String> pics) {
        this.pics = pics;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getPricePromote() {
        return pricePromote;
    }

    public void setPricePromote(Double pricePromote) {
        this.pricePromote = pricePromote;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public String getBasicInfo() {
        return basicInfo;
    }

    public void setBasicInfo(String basicInfo) {
        this.basicInfo = basicInfo;
    }

    public Map<String, String> getBasicInfoMap() {
        return basicInfoMap;
    }

    public void setBasicInfoMap(Map<String, String> basicInfoMap) {
        this.basicInfoMap = basicInfoMap;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public List<String> getDetailImgs() {
        return detailImgs;
    }

    public void setDetailImgs(List<String> detailImgs) {
        this.detailImgs = detailImgs;
    }


}
