package com.muic.objectstorage.DTO;

import java.util.HashMap;

public class ObjectDTO {
    private String name;
    private HashMap<String, String> metadata;

    public ObjectDTO() {}

    public ObjectDTO(String name, HashMap<String, String> metadata) {
        this.name = name;
        this.metadata = metadata;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public HashMap<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(HashMap<String, String> metadata) {
        this.metadata = metadata;
    }
}
