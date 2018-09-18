package com.muic.objectstorage.Entity;

import javax.persistence.*;

@Entity
public class Metadata {
    @Id
    private Integer id;
    private String name;
    private String value;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Object object;

    public Metadata() {}

    public Metadata(String name, String value) {
        this.name = name;
        this.value = value;
    }

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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }
}
