package webcrab.storage;

import webcrab.taobao.model.TaobaoItem;

import java.util.ArrayList;
import java.util.List;

/**
 * 产品存储
 */
public class ProductRepository {

    private static ProductRepository instance;
    static {
        instance = new ProductRepository();
    }

    protected ProductRepository() {
    }

    public static ProductRepository getInstance() {
        return instance;
    }
    /**
     * 爬虫爬下来商品
     */
    private List<TaobaoItem> items = new ArrayList<>();

    public List<TaobaoItem> getItems() {
        return items;
    }

    public void setItems(List<TaobaoItem> items) {
        this.items = items;
    }


}
