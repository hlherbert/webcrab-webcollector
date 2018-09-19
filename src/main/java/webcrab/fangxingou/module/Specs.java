package webcrab.fangxingou.module;

import java.util.List;

/**
 * 复合规格
 */
public class Specs {
    private Integer id;
    private String name;
    private List<Spec> Specs;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Spec> getSpecs() {
        return Specs;
    }

    public void setSpecs(List<Spec> specs) {
        Specs = specs;
    }
}
