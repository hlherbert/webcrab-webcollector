package webcrab.conf;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 卖家属性，从配置文件seller.properties中读取
 */
public class SellerProperties {
    private String mobile;
    private String recommendRemark;
    private String extra;

    public static SellerProperties getInstance() {
        InputStream in = SellerProperties.class.getResourceAsStream("/seller.properties");
        Properties props = new Properties();
        SellerProperties sellerProperties = new SellerProperties();
        try {
            props.load(in);
            sellerProperties.mobile = props.getProperty("mobile");
            sellerProperties.recommendRemark = props.getProperty("recommendRemark");
            sellerProperties.extra = props.getProperty("extra");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sellerProperties;
    }

    protected SellerProperties() {

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
}
