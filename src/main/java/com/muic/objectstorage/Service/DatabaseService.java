package com.muic.objectstorage.Service;

import com.google.gson.Gson;
import com.muic.objectstorage.Entity.Bucket;
import com.muic.objectstorage.Repository.BucketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DatabaseService {

    public void addBucketMetadata(long created, long modified, String bucketname) {
    }
}
