package com.muic.objectstorage.Service;

import com.muic.objectstorage.Entity.Object;
import com.muic.objectstorage.Entity.Part;
import com.muic.objectstorage.Exception.FileStorageException;
import com.muic.objectstorage.Repository.BucketRepository;
import com.muic.objectstorage.Repository.ObjectRepository;
import com.muic.objectstorage.Repository.PartRepository;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.rmi.CORBA.Util;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

    public String storeFile(HttpServletRequest request, String bucketname, String objectname, Integer partNumber, Integer partSize, String partMd5) {
        try {

            ServletInputStream file = request.getInputStream();
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

                if (partSize != request.getContentLength()) {
                    throw new FileStorageException("LengthMismatched");
                }

                File targetFile = new File(BASE_PATH + bucketname + "/" + fileName);
                FileUtils.copyInputStreamToFile(file, targetFile);
                String md5 = Utils.calculateMd5(new File(BASE_PATH + bucketname + "/" + fileName));
                if (!partMd5.equals(md5)) {
                    // TODO: 19/9/2018 AD Clean up file if mismatch
                    System.out.println(md5);
                    throw new FileStorageException("MD5Mismatched");
                }
                savePart(objectname, partNumber, partSize, partMd5);
                return md5;
            }
            return null;
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

    private void savePart(String objectname, Integer partNumber, Integer partSize, String partMd5) {
        try {
            Object object = objectRepository.findByName(objectname);
            partRepository.save(new Part(partNumber, partSize, partMd5, object));
        } catch (Exception e) {
            throw new RuntimeException("Unable to save part to database");
        }

    }
}
