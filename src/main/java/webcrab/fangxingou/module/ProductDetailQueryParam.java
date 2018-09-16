package webcrab.fangxingou.module;

public class ProductDetailQueryParam {
    private String product_id;// 商品id，和接入方的out_product_id二选一
    private String out_product_id;// 接入方商品ID(例如淘宝的商品id)

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getOut_product_id() {
        return out_product_id;
    }

    public void setOut_product_id(String out_product_id) {
        this.out_product_id = out_product_id;
    }
}
