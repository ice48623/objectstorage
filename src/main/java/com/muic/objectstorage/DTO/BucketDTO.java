package com.muic.objectstorage.DTO;

import java.util.List;

public class BucketDTO {
    private String created;
    private String modified;
    private String name;
    private List<ObjectDTO> objects;

    public BucketDTO() {}

    public BucketDTO(String created, String modified, String name, List<ObjectDTO> objects) {
        this.created = created;
        this.modified = modified;
        this.name = name;
        this.objects = objects;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ObjectDTO> getObjects() {
        return objects;
    }

    public void setObjects(List<ObjectDTO> objects) {
        this.objects = objects;
    }
}
