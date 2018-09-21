package webcrab.fangxingou.module;

import java.util.List;

/**
 * 具体规格
 */
public class Spec {
    private Integer id;
    private Integer spec_id; //规格ID
    private String name; //规格名称。 颜色， 或者黑色
    private Integer pid; //父规格ID
    private Integer is_leaf; //0-是叶子节点，1-不是
    private List<Spec> values; //子节点集，is_leaf=0时才有

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSpec_id() {
        return spec_id;
    }

    public void setSpec_id(Integer spec_id) {
        this.spec_id = spec_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPid() {
        return pid;
    }

    public void setPid(Integer pid) {
        this.pid = pid;
    }

    public Integer getIs_leaf() {
        return is_leaf;
    }

    public void setIs_leaf(Integer is_leaf) {
        this.is_leaf = is_leaf;
    }

    public List<Spec> getValues() {
        return values;
    }

    public void setValues(List<Spec> values) {
        this.values = values;
    }
}
