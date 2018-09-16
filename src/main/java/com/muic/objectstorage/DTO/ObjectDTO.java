package com.muic.objectstorage.DTO;

public class ObjectDTO {
    private String name;
    private String eTag;
    private String created;
    private String modified;

    public ObjectDTO() {}

    public ObjectDTO(String name, String eTag, String created, String modified) {
        this.name = name;
        this.eTag = eTag;
        this.created = created;
        this.modified = modified;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String geteTag() {
        return eTag;
    }

    public void seteTag(String eTag) {
        this.eTag = eTag;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }
}
