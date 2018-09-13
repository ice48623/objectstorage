package com.muic.objectstorage.Service;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class BucketService {

    private static final String BASE_PATH = "./bucket/";

    public static Boolean create(String bucketname) {
        Path path = Paths.get(BASE_PATH + bucketname);
        //if directory exists?
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
                return true;
            } catch (IOException e) {
                //fail to create directory
                e.printStackTrace();
            }
        }
        return false;
    }

    public static Boolean drop(String bucketname) {
        Path path = Paths.get(BASE_PATH + bucketname);
        try {
            return Files.deleteIfExists(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
