package finchina.demo.dto;

import java.io.Serializable;

/**
 * @Author: kongxr
 * @Date: 2022-10-12 17:29
 * @Description:
 */
public class Person implements Serializable {

    private Long id;

    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
