package com.muic.objectstorage.Service;

import com.muic.objectstorage.DTO.BucketDTO;
import com.muic.objectstorage.DTO.ObjectDTO;
import com.muic.objectstorage.Entity.*;
import com.muic.objectstorage.Entity.Object;
import com.muic.objectstorage.Repository.BucketRepository;
import com.muic.objectstorage.Repository.MetadataRepository;
import com.muic.objectstorage.Repository.ObjectRepository;
import com.muic.objectstorage.Repository.PartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
public class BucketService {

    @Autowired
    BucketRepository bucketRepository;

    @Autowired
    ObjectRepository objectRepository;

    @Autowired
    MetadataRepository metadataRepository;

    @Autowired
    PartRepository partRepository;

    private static final String BASE_PATH = "./bucket/";

    public Bucket create(String bucketname) {
        Path path = Paths.get(BASE_PATH + bucketname);
        //if directory exists?
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
                long currentTime = new Date().getTime();
                Bucket bucket = new Bucket(currentTime, currentTime, bucketname);
                bucketRepository.save(bucket);
                return bucket;
            } catch (IOException e) {
                //fail to create directory
                e.printStackTrace();
            }
        }
        return null;
    }

    // TODO: 18/9/2018 AD Delete bucket even it is not empty?
    public Boolean drop(String bucketname) {
        try {
            Path path = Paths.get(BASE_PATH + bucketname);
            if (Files.deleteIfExists(path)) {
                bucketRepository.delete(bucketRepository.findByName(bucketname));
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Boolean createTicket(String bucketname, String objectname) {
        Path bucketPath = Paths.get(BASE_PATH + bucketname);
        Path objectPath = Paths.get(BASE_PATH + bucketname + "/" + objectname);
        Object object = objectRepository.findByName(objectname);
        object.setComplete(false);
        objectRepository.save(object);
        return Files.exists(bucketPath) && !Files.exists(objectPath);
    }

    public Boolean deleteObject(String bucketname, String objectname) {
        try {
            Path objectPath = Paths.get(BASE_PATH + bucketname + "/" + objectname);
            if (Files.deleteIfExists(objectPath)) {
                objectRepository.delete(objectRepository.findByName(objectname));
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    public Boolean addUpdateMetadataByKey(String bucketname, String objectname, String key, String value) {
        try {
            if (!isObjectExist(bucketname, objectname)) {
                return false;
            }
            Object object = objectRepository.findByName(objectname);
            metadataRepository.save(new Metadata(new ObjectMetadataComposite(object.getId(), key), value));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Boolean deleteMetadataByKey(String bucketname, String objectname, String key) {
        if (!isObjectExist(bucketname, objectname)) {
            return false;
        }
        Object object = objectRepository.findByName(objectname);
        Metadata metadata = metadataRepository.findById(new ObjectMetadataComposite(object.getId(), key)).get();
        metadataRepository.deleteById(metadata.getId());
        return true;
    }

    public HashMap<String, String> getMetadataByKey(String bucketname, String objectname, String key) {
        try {
            if (!isObjectExist(bucketname, objectname)) {
                return new HashMap<>();
            }
            Object object = objectRepository.findByName(objectname);
            Metadata metadata = metadataRepository.findById(new ObjectMetadataComposite(object.getId(), key)).get();
            return new HashMap<String, String>(){{
                put(key, metadata.getValue());
            }};
        } catch (Exception e) {
            return new HashMap<>();
        }
    }

    public HashMap<String, String> getAllMetadata(String bucketname, String objectname) {
        try {
            if (!isObjectExist(bucketname, objectname)) {
                return new HashMap<>();
            }
            Object object = objectRepository.findByName(objectname);
            List<Metadata> metadatas = metadataRepository.findByObjectId(object.getId());
            HashMap<String, String> ret = new HashMap<>();
            metadatas.forEach((metadata) -> ret.put(metadata.getId().getMetadataName(), metadata.getValue()));
            return ret;
        } catch (Exception e) {
            return new HashMap<>();
        }
    }

    public BucketDTO listObjectsInBucket(String bucketname) {
        try {
            List<Object> objects = getAllObject(bucketname);
            List<ObjectDTO> objectDTOS = new ArrayList<>();
            objects.forEach((object) -> objectDTOS.add(new ObjectDTO(object.getName(), object.geteTag(), Long.toString(object.getCreated()), Long.toString(object.getModified()))));
            Bucket bucket = bucketRepository.findByName(bucketname);
            return new BucketDTO(Long.toString(bucket.getCreated()), Long.toString(bucket.getModified()), bucket.getName(), objectDTOS);
        } catch (Exception e) {
            return new BucketDTO();
        }
    }

    private List<Object> getAllObject(String bucketname) {
        return objectRepository.findByBucketId(getBucketIdByName(bucketname));
    }

    private Integer getBucketIdByName(String bucketname) {
        return bucketRepository.findByName(bucketname).getId();
    }

    private Boolean isObjectExist(String bucketname, String objectname) {
        Path objectPath = Paths.get(BASE_PATH + bucketname + "/" + objectname);
        return Files.exists(objectPath);
    }

    public Boolean isBucketExist(String bucketname) {
        Path bucketPath = Paths.get(BASE_PATH + bucketname);
        return Files.exists(bucketPath);
    }
}
