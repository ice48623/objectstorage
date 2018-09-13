package com.muic.objectstorage.DTO;

public class CreateBucketDTO {
    private final long created;
    private final long modified;
    private final String name;

    public CreateBucketDTO(long created, long modified, String name) {
        this.created = created;
        this.modified = modified;
        this.name = name;
    }

    public long getCreated() {
        return created;
    }

    public long getModified() {
        return modified;
    }

    public String getName() {
        return name;
    }
}
