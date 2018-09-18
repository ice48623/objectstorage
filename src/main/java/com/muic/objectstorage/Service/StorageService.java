package com.muic.objectstorage.Service;

import com.muic.objectstorage.Entity.Object;
import com.muic.objectstorage.Entity.Part;
import com.muic.objectstorage.Exception.FileStorageException;
import com.muic.objectstorage.Repository.BucketRepository;
import com.muic.objectstorage.Repository.ObjectRepository;
import com.muic.objectstorage.Repository.PartRepository;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class StorageService {

    @Autowired
    BucketRepository bucketRepository;

    @Autowired
    ObjectRepository objectRepository;

    @Autowired
    PartRepository partRepository;

    private static final String BASE_PATH = "./bucket/";

    public void storeFile(MultipartFile file, String bucketname, String objectname, Integer partNumber, Integer partSize, String partMd5) {
        // Normalize file name
        try {
            String fileName = StringUtils.cleanPath(objectname + "-" + partNumber);

            if (!Files.exists(Paths.get(BASE_PATH + bucketname))) {
                throw new FileStorageException("InvalidBucket");
            }

            if ((objectRepository.findByName(objectname).getComplete() == null || !objectRepository.findByName(objectname).getComplete()) && !Files.exists(Paths.get(BASE_PATH + bucketname + "/" + objectname))) {
                if (!isValidPartNumberRange(partNumber)) {
                    throw new FileStorageException("InvalidPartNumber");
                }

                if (!isValidObjectName(objectname)) {
                    throw new FileStorageException("InvalidObjectName");
                }

                if (partSize != file.getSize()) {
                    throw new FileStorageException("LengthMismatched");
                }

                if (!partMd5.equals(calculateMd5(file))) {
                    throw new FileStorageException("MD5Mismatched");
                }

                // Copy file to the target location (Replacing existing file with the same name)
                Path targetLocation = Paths.get(BASE_PATH + bucketname + "/" + fileName);
                Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
                savePart(objectname, partNumber, partSize, partMd5);
            }
        } catch (IOException ex) {
            throw new FileStorageException("Unable to save file");
        }
    }

    private Boolean isValidPartNumberRange(Integer partNumber) {
        return partNumber > 0 && partNumber <= 10000;
    }

    private Boolean isValidObjectName(String objectname) {
        String regex = "^[A-Za-z0-9-_]+[A-Za-z0-9.-_]*[A-Za-z0-9_-]";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(objectname);
        return matcher.matches();
    }

    private String calculateMd5(MultipartFile file) {
        try {
            return DigestUtils.md5Hex(file.getBytes());
        } catch (Exception e) {
            throw new RuntimeException("Unable to calculate md5 for " + file.getName());
        }
    }

    private void savePart(String objectname, Integer partNumber, Integer partSize, String partMd5) {
        try {
            Object object = objectRepository.findByName(objectname);
            partRepository.save(new Part(partNumber, partSize, partMd5, object));
        } catch (Exception e) {
            throw new RuntimeException("Unable to save part to database");
        }

    }
}
