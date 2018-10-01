package webcrab.conf;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 卖家属性，从配置文件seller.properties中读取
 */
public class SellerProperties {
    private String appkey;
    private String appsecret;
    private String brand;
    private String mobile;
    private String recommendRemark;
    private String extra;
    private String stock;

    public static SellerProperties getInstance() {
        InputStream in = SellerProperties.class.getResourceAsStream("/seller.properties");
        Properties props = new Properties();
        SellerProperties sellerProperties = new SellerProperties();
        try {
            props.load(in);
            sellerProperties.appkey = props.getProperty("appkey");
            sellerProperties.appsecret = props.getProperty("appsecret");
            sellerProperties.brand = props.getProperty("brand");
            sellerProperties.mobile = props.getProperty("mobile");
            sellerProperties.recommendRemark = props.getProperty("recommendRemark");
            sellerProperties.extra = props.getProperty("extra");
            sellerProperties.stock = props.getProperty("stock");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sellerProperties;
    }

    protected SellerProperties() {

    }

    public String getAppkey() {
        return appkey;
    }

    public void setAppkey(String appkey) {
        this.appkey = appkey;
    }

    public String getAppsecret() {
        return appsecret;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public void setAppsecret(String appsecret) {
        this.appsecret = appsecret;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getRecommendRemark() {
        return recommendRemark;
    }

    public void setRecommendRemark(String recommendRemark) {
        this.recommendRemark = recommendRemark;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public String getStock() {
        return stock;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }
}
