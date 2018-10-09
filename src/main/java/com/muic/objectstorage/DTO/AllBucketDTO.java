package com.muic.objectstorage.DTO;

import java.util.List;

public class AllBucketDTO {
    private List<String> buckets;

    public AllBucketDTO(List<String> buckets) {
        this.buckets = buckets;
    }

    public List<String> getBuckets() {
        return buckets;
    }

    public void setBuckets(List<String> buckets) {
        this.buckets = buckets;
    }
}
