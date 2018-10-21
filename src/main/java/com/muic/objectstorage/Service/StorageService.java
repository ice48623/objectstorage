package com.muic.objectstorage.Service;

import com.muic.objectstorage.Entity.Object;
import com.muic.objectstorage.Entity.ObjectPartComposite;
import com.muic.objectstorage.Entity.Part;
import com.muic.objectstorage.Exception.FileStorageException;
import com.muic.objectstorage.Repository.BucketRepository;
import com.muic.objectstorage.Repository.ObjectRepository;
import com.muic.objectstorage.Repository.PartRepository;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.input.BoundedInputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
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
        String md5 = "";
        try {
            ServletInputStream file = request.getInputStream();
            String fileName = getFilename(objectname, partNumber);
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
                md5 = Utils.calculateMd5(new File(BASE_PATH + bucketname + "/" + fileName));
                if (!partMd5.equals(md5)) {
                    // TODO: 19/9/2018 AD Clean up file if mismatch
                    System.out.println(md5);
                    throw new FileStorageException("MD5Mismatched");
                }
                savePart(objectname, partNumber, request.getContentLength(), md5);
            }
            return md5;
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
            partRepository.save(new Part(new ObjectPartComposite(object.getId(), partNumber), partSize, partMd5, object));
            updateObjectETag(objectname);
        } catch (Exception e) {
            throw new RuntimeException("Unable to save part to database");
        }

    }

    private String getFilename(String objectname, Integer partNumber) {
        return StringUtils.cleanPath(String.format("%05d", partNumber) + "_" + objectname);
    }

    public SequenceInputStream getObjectWithRange(String bucketname, String objectname, String range, FileInputStream input) {
        HashMap<String, Long> ranges = parseRange(range);
        long start = ranges.get("start") - 1;
        long end = ranges.get("end");

        if (end < start) {
            throw new RuntimeException("End < Start");
        }

        Object object = objectRepository.findByName(objectname);
        List<Part> parts = partRepository.findByObjectId(object.getId());
        Collections.sort(parts);

        long objectLength = partRepository.getObjectLength();
        System.out.println("object length: " + objectLength);

        if (end > objectLength) {
            throw new RuntimeException("Invalid Range");
        }

        Boolean started = false;
        List<InputStream> filesStream = new ArrayList<>();
        Integer readSoFar = 0;
        List<Part> filteredParts = new ArrayList<>();
        Integer cutout = 0;
        try {
            for (Part part : parts) {
                readSoFar += part.getLength();
                if (readSoFar > start) {
                    filteredParts.add(part);
                } else {
                    cutout += part.getLength();
                }
            }

            readSoFar = cutout;
            for (Part part : filteredParts) {
                System.out.println("Reading part: " + part.getId().getPartNumber());
                input = new FileInputStream(BASE_PATH + bucketname + "/" + getFilename(objectname, part.getId().getPartNumber()));
                if (!started) {
                    System.out.println("Read from start to EOF");
                    Integer skip = (int) start - readSoFar;
                    input.skip(skip);

                    if (end < part.getLength()) {
                        filesStream.add(new BoundedInputStream(input, part.getLength() - skip - (part.getLength() - end)));
                        break;
                    } else {
                        filesStream.add(new BoundedInputStream(input, part.getLength() - skip));
                    }
                } else if (end < readSoFar + part.getLength()) {
                    System.out.println("Read from SOF to end");
                    System.out.println(((readSoFar + part.getLength()) - end));
                    filesStream.add(new BoundedInputStream(input, part.getLength() -  ((readSoFar + part.getLength()) - end)));
                    break;
                } else {
                    System.out.println("Read Whole File");
                    filesStream.add(new BoundedInputStream(input, part.getLength()));
                }

                readSoFar += part.getLength();
                started = true;
            }
            return new SequenceInputStream(Collections.enumeration(filesStream));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public SequenceInputStream getFile(String bucketname, String objectname, FileInputStream input) {

        Object object = objectRepository.findByName(objectname);
        List<Part> parts = partRepository.findByObjectId(object.getId());
        Collections.sort(parts);

        long objectLength = 0;
        for (Part part : parts) {
            objectLength += part.getLength();
        }

        System.out.println("Object Length: " + objectLength);

        List<InputStream> filesStream = new ArrayList<>();

        try {
            for (Part part : parts) {
                System.out.println("Reading part: " + part.getId().getPartNumber());
                input = new FileInputStream(BASE_PATH + bucketname + "/" + getFilename(objectname, part.getId().getPartNumber()));
                filesStream.add(input);
            }
            return new SequenceInputStream(Collections.enumeration(filesStream));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getETag(String objectname) {
        return objectRepository.findByName(objectname).geteTag();
    }

    public HashMap<String, Long> parseRange(String range) {
        System.out.println(range);
        try {
            String[] ranges = range.split("-");
            return new HashMap<String, Long>(){{
                put("start", Long.valueOf(ranges[0]));
                put("end", Long.valueOf(ranges[1]));
            }};
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
}
