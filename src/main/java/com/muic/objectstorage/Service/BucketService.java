package com.muic.objectstorage.Service;

import com.muic.objectstorage.DTO.BucketDTO;
import com.muic.objectstorage.DTO.ObjectDTO;
import com.muic.objectstorage.Entity.*;
import com.muic.objectstorage.Entity.Object;
import com.muic.objectstorage.Repository.BucketRepository;
import com.muic.objectstorage.Repository.MetadataRepository;
import com.muic.objectstorage.Repository.ObjectRepository;
import com.muic.objectstorage.Repository.PartRepository;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        if (isBucketExist(bucketname)) {
            throw new RuntimeException("Bucket already exist");
        }

        if (!isValidBucketName(bucketname)) {
            throw new RuntimeException("Invalid bucket name");
        }

        try {
            Path path = Paths.get(BASE_PATH + bucketname);
            Files.createDirectories(path);
            long currentTime = new Date().getTime();
            Bucket bucket = new Bucket(currentTime, currentTime, bucketname);
            bucketRepository.save(bucket);
            return bucket;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void drop(String bucketname) {
        try {
            FileUtils.deleteDirectory(new File(BASE_PATH + bucketname));
            bucketRepository.delete(bucketRepository.findByName(bucketname));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void createTicket(String bucketname, String objectname) {
        if (!isBucketExist(bucketname)) {
            throw new RuntimeException("Bucket not exist");
        }

        if (isObjectExist(bucketname, objectname)) {
            throw new RuntimeException("Object already exist");
        }

        if (objectRepository.findByName(objectname) != null && objectRepository.findByName(objectname).getName().equals(objectname)) {
            throw new RuntimeException("Object already exist");
        }

        long currentTime = new Date().getTime();
        Object object = new Object();
        object.setName(objectname);
        object.setCreated(currentTime);
        object.setModified(currentTime);
        object.setBucket(bucketRepository.findByName(bucketname));
        object.setComplete(false);
        objectRepository.save(object);
    }

    public void deleteObject(String bucketname, String objectname) {

        if (!isBucketExist(bucketname)) {
            throw new RuntimeException("Bucket not exist");
        }

//        if (!isObjectExist(bucketname, objectname)) {
//            throw new RuntimeException("Object not exist");
//        }

        try {
            Object object = objectRepository.findByName(objectname);
            List<Part> parts = partRepository.findByObjectId(object.getId());
            for (Integer i = 1; i <= parts.size(); i++) {
                String filename = getFilename(object.getName(), i);
                Path objectPath = Paths.get(BASE_PATH + bucketname + "/" + filename);
                if (Files.deleteIfExists(objectPath)) {
                    partRepository.delete(parts.get(i-1));
                }
            }
            objectRepository.delete(objectRepository.findByName(objectname));

        } catch (IOException e) {
            throw new RuntimeException("Could not delete object");
        }

    }

    public void addUpdateMetadataByKey(String bucketname, String objectname, String key, String value) {

        if (!isBucketExist(bucketname)) {
            throw new RuntimeException("Bucket not exist");
        }

        if (!isObjectExist(bucketname, objectname)) {
            throw new RuntimeException("Object not exist");
        }
        Object object = objectRepository.findByName(objectname);
        metadataRepository.save(new Metadata(new ObjectMetadataComposite(object.getId(), key), value));
    }

    public void deleteMetadataByKey(String bucketname, String objectname, String key) {

        if (!isBucketExist(bucketname)) {
            throw new RuntimeException("Bucket not exist");
        }

        if (!isObjectExist(bucketname, objectname)) {
            throw new RuntimeException("Object not exist");
        }

        Object object = objectRepository.findByName(objectname);
        Metadata metadata = metadataRepository.findById(new ObjectMetadataComposite(object.getId(), key)).get();
        metadataRepository.deleteById(metadata.getId());
    }

    public HashMap<String, String> getMetadataByKey(String bucketname, String objectname, String key) {

        if (!isBucketExist(bucketname)) {
            throw new RuntimeException("Bucket not exist");
        }

        if (!isObjectExist(bucketname, objectname)) {
            throw new RuntimeException("Object not exist");
        }

        Object object = objectRepository.findByName(objectname);
        Metadata metadata = metadataRepository.findById(new ObjectMetadataComposite(object.getId(), key)).get();
        return new HashMap<String, String>(){{
            put(key, metadata.getValue());
        }};
    }

    public HashMap<String, String> getAllMetadata(String bucketname, String objectname) {

        if (!isBucketExist(bucketname)) {
            throw new RuntimeException("Bucket not exist");
        }

        if (!isObjectExist(bucketname, objectname)) {
            throw new RuntimeException("Object not exist");
        }

        Object object = objectRepository.findByName(objectname);
        List<Metadata> metadatas = metadataRepository.findByObjectId(object.getId());
        HashMap<String, String> ret = new HashMap<>();
        metadatas.forEach((metadata) -> ret.put(metadata.getId().getMetadataName(), metadata.getValue()));
        return ret;

    }

    public BucketDTO listObjectsInBucket(String bucketname) {

        if (!isBucketExist(bucketname)) {
            throw new RuntimeException("Bucket not exist");
        }

        List<Object> objects = getAllObject(bucketname);
        List<ObjectDTO> objectDTOS = new ArrayList<>();
        objects.forEach((object) -> objectDTOS.add(new ObjectDTO(object.getName(), object.geteTag(), Long.toString(object.getCreated()), Long.toString(object.getModified()))));
        Bucket bucket = bucketRepository.findByName(bucketname);
        return new BucketDTO(Long.toString(bucket.getCreated()), Long.toString(bucket.getModified()), bucket.getName(), objectDTOS);
    }

    public void deletePart(String bucketname, String objectname, Integer partNumber) {
        try {

            String fileName = StringUtils.cleanPath(String.format("%05d", partNumber) + "_" + objectname);

            if (!isBucketExist(bucketname)) {
                throw new RuntimeException("bucket not exist");
            }

            if (!isPartExist(bucketname, fileName)) {
                throw new RuntimeException("Part not exist");
            }

            if (!isValidPartNumberRange(partNumber)) {
                throw new RuntimeException("Invalid part number");
            }

            if (isObjectComplete(objectname)) {
                throw new RuntimeException("Object is already marked as complete");
            }

            Path partPath = Paths.get(BASE_PATH + bucketname + "/" + fileName);
            Object object = objectRepository.findByName(objectname);
            if (Files.deleteIfExists(partPath)) {
                partRepository.delete(partRepository.findById(new ObjectPartComposite(object.getId(), partNumber)).get());
                updateObjectETag(objectname);
            }
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public HashMap<String, String> completeUpload(String bucketname, String objectname) {
        if (!isBucketExist(bucketname)) {
            throw new RuntimeException("InvalidBucket");
        }

        if (!isValidObjectName(objectname)) {
            throw new RuntimeException("InvalidObjectName");
        }

        Object object = objectRepository.findByName(objectname);
        List<Part> parts = partRepository.findByObjectId(object.getId());
        long currentTime = new Date().getTime();
        Integer length = 0;
        List<String> md5List = new ArrayList<>();

        for (Part part : parts) {
            md5List.add(part.getMd5());
            length += part.getLength();
        }

        String eTag = Utils.computeETag(md5List);

        object.setModified(currentTime);
        object.seteTag(eTag);
        object.setComplete(true);
        objectRepository.save(object);

        HashMap<String, String> ret = new HashMap<>();
        ret.put("eTag", eTag);
        ret.put("length", length.toString());
        ret.put("name", objectname);
        return ret;
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

    private Boolean isValidPartNumberRange(Integer partNumber) {
        return partNumber > 0 && partNumber <= 10000;
    }

    private Boolean isObjectComplete(String objectname) {
        Object object = objectRepository.findByName(objectname);
        return object.getComplete() != null && object.getComplete();
    }

    private Boolean isPartExist(String bucketname, String partName) {
        Path partPath = Paths.get(BASE_PATH + bucketname + "/" + partName);
        return Files.exists(partPath);
    }

    private Boolean isValidObjectName(String objectname) {
        String regex = "^[A-Za-z0-9-_]+[A-Za-z0-9.-_]*[A-Za-z0-9_-]";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(objectname);
        return matcher.matches();
    }

    private Boolean isValidBucketName(String bucketname) {
        String regex = "^[A-Za-z0-9-_]*$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(bucketname);
        return matcher.matches();
    }

    public String getObjectETag(String bucketname, String objectname) {
        Bucket bucket = bucketRepository.findByName(bucketname);
        if (bucket == null) {
            return "";
        }
        Object object = objectRepository.findByNameAndBucketId(objectname, bucket.getId());
        if (object == null) {
            return "";
        }
        return object.geteTag();
    }

    public Long getObjectLength(String bucketname, String objectname) {
        Bucket bucket = bucketRepository.findByName(bucketname);
        if (bucket == null) {
            return 0L;
        }
        Object object = objectRepository.findByNameAndBucketId(objectname, bucket.getId());
        if (object == null) {
            return 0L;
        }

        List<Part> parts = partRepository.findByObjectId(object.getId());
        if (parts.isEmpty()) {
            return 0L;
        }

        long length = 0;
        for (Part part : parts) {
            length += part.getLength();
        }
        return length;
    }

    private void updateObjectETag(String objectname) {
        Object object = objectRepository.findByName(objectname);
        List<Part> parts = partRepository.findByObjectId(object.getId());
        long currentTime = new Date().getTime();
        List<String> md5List = new ArrayList<>();

        for (Part part : parts) {
            md5List.add(part.getMd5());
        }

        String eTag = Utils.computeETag(md5List);

        object.setModified(currentTime);
        object.seteTag(eTag);
        objectRepository.save(object);
    }

    public List getAllBucket() {
        List<String> buckets = new ArrayList<>();
        bucketRepository.findAll().iterator().forEachRemaining(bucket -> buckets.add(bucket.getName()));
        return buckets;
    }

    private String getFilename(String objectname, Integer partNumber) {
        return StringUtils.cleanPath(String.format("%05d", partNumber) + "_" + objectname);
    }
}
