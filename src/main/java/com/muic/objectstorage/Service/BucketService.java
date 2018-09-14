package com.muic.objectstorage.Service;


import com.muic.objectstorage.Entity.Bucket;
import com.muic.objectstorage.Repository.BucketRepository;
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
}
