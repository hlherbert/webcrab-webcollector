package webcrab.fangxingou.module.po;

/**
 * 商品分类查询参数
 */
public class ProductCategoryQueryParam {
    private String cid;// 父分类id,根据父id可以获取子分类，一级分类cid=0,必填。

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }
}
