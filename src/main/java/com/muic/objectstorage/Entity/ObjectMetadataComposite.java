package com.muic.objectstorage.Entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.lang.Object;
import java.util.Objects;

@Embeddable
public class ObjectMetadataComposite implements Serializable {

    @Column(name = "object_id")
    private Integer objectId;

    @Column(name = "metadata_name")
    private String metadataName;

    public ObjectMetadataComposite() {}

    public ObjectMetadataComposite(Integer objectId, String metadataName) {
        this.objectId = objectId;
        this.metadataName = metadataName;
    }

    public Integer getObjectId() {
        return objectId;
    }

    public void setObjectId(Integer objectId) {
        this.objectId = objectId;
    }

    public String getMetadataName() {
        return metadataName;
    }

    public void setMetadataName(String metadataName) {
        this.metadataName = metadataName;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof ObjectMetadataComposite)) return false;
        ObjectMetadataComposite that = (ObjectMetadataComposite) obj;
        return Objects.equals(getObjectId(), that.getObjectId()) &&
                Objects.equals(getMetadataName(), that.getMetadataName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getObjectId(), getMetadataName());
    }
}
