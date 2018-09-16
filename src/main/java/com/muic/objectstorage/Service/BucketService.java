package com.muic.objectstorage.Service;


import com.muic.objectstorage.Entity.Bucket;
import com.muic.objectstorage.Entity.Metadata;
import com.muic.objectstorage.Entity.Object;
import com.muic.objectstorage.Entity.ObjectMetadataComposite;
import com.muic.objectstorage.Repository.BucketRepository;
import com.muic.objectstorage.Repository.MetadataRepository;
import com.muic.objectstorage.Repository.ObjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

@Service
public class BucketService {

    @Autowired
    BucketRepository bucketRepository;

    @Autowired
    ObjectRepository objectRepository;

    @Autowired
    MetadataRepository metadataRepository;

    private static final String BASE_PATH = "./bucket/";

    public Bucket create(String bucketname) {
        Path path = Paths.get(BASE_PATH + bucketname);
        //if directory exists?
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
                long currentTime = new Date().getTime();
                Bucket bucket = new Bucket(currentTime, currentTime, bucketname);
                save(bucket);
                return bucket;
            } catch (IOException e) {
                //fail to create directory
                e.printStackTrace();
            }
        }
        return null;
    }

//    public void save(long created, long modified, String bucketname) {
//        bucketRepository.save(new Bucket(created, modified, bucketname));
//    }

    public void save(Bucket bucket) {
        bucketRepository.save(bucket);
    }

    public Boolean drop(String bucketname) {
        Path path = Paths.get(BASE_PATH + bucketname);
        try {
            return Files.deleteIfExists(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public Boolean createTicket(String bucketname, String objectname) {
        Path bucketPath = Paths.get(BASE_PATH + bucketname);
        Path objectPath = Paths.get(BASE_PATH + bucketname + "/" + objectname);
        return Files.exists(bucketPath) && !Files.exists(objectPath);
    }

    public Boolean deleteObject(String bucketname, String objectname) {
        try {
            Path objectPath = Paths.get(BASE_PATH + bucketname + "/" + objectname);
            return Files.deleteIfExists(objectPath);
        } catch (Exception e) {
            return false;
        }

    }

    // TODO: 16/9/2018 AD parse value from request body
    public Boolean addUpdateMetadataByKey(String bucketname, String objectname, String key) {
        try {
            Path objectPath = Paths.get(BASE_PATH + bucketname + "/" + objectname);
            if (!Files.exists(objectPath)) {
                return false;
            }
            Object object = objectRepository.findByName(objectname);
            metadataRepository.save(new Metadata(new ObjectMetadataComposite(object.getId(), key), "aaaa"));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Boolean deleteMetadataByKey(String bucketname, String objectname, String key) {
        try {
            Path objectPath = Paths.get(BASE_PATH + bucketname + "/" + objectname);
            if (!Files.exists(objectPath)) {
                return false;
            }
            Object object = objectRepository.findByName(objectname);
            Metadata metadata = metadataRepository.findById(new ObjectMetadataComposite(object.getId(), key)).get();
            metadataRepository.delete(metadata);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
