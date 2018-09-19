package com.muic.objectstorage.Controller;

import com.muic.objectstorage.DTO.BucketDTO;
import com.muic.objectstorage.DTO.CreateBucketDTO;
import com.muic.objectstorage.DTO.FileUploadResponse;
import com.muic.objectstorage.Entity.Bucket;
import com.muic.objectstorage.Exception.FileStorageException;
import com.muic.objectstorage.Service.BucketService;
import com.muic.objectstorage.Service.StorageService;
import com.muic.objectstorage.Service.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.support.HttpRequestHandlerServlet;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.IOUtils;

import javax.rmi.CORBA.Util;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

@RestController
public class MainController {

    @Autowired
    BucketService bucketService;

    @Autowired
    StorageService storageService;

    @RequestMapping(value = "/{bucketname}", method = RequestMethod.POST)
    public ResponseEntity<CreateBucketDTO> createBucket(
            @PathVariable("bucketname") String bucketname,
            @RequestParam("create") String createAction
    ) {
        try {
            Bucket bucket = bucketService.create(bucketname);
            if (bucket == null) {
                return ResponseEntity.badRequest().build();
            }
            return ResponseEntity.ok(new CreateBucketDTO(bucket.getCreated(), bucket.getModified(), bucket.getName()));
        } catch (Exception e) {

            return ResponseEntity.badRequest().build();
        }
    }

    @RequestMapping(value = "/{bucketname}", method = RequestMethod.DELETE)
    public ResponseEntity<String> dropBucket(
            @PathVariable("bucketname") String bucketname,
            @RequestParam("delete") String deleteAction
    ) {
        try {
            if (bucketService.drop(bucketname)) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @RequestMapping(value = "/{bucketname}/{objectname}", method = RequestMethod.POST)
    public ResponseEntity<String> createTicket(
            @PathVariable("bucketname") String bucketname,
            @PathVariable("objectname") String objectname,
            @RequestParam("create") String createAction
    ) {
        try {
            if (bucketService.createTicket(bucketname, objectname)) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @RequestMapping(value = "/{bucketname}/{objectname}", params = "delete", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteObject(
            @PathVariable("bucketname") String bucketname,
            @PathVariable("objectname") String objectname,
            @RequestParam("delete") String deleteAction
    ) {
        if (bucketService.deleteObject(bucketname, objectname)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @RequestMapping(value = "/{bucketname}/{objectname}", method = RequestMethod.PUT)
    public ResponseEntity<String> addUpdateMetadataByKey(
            @PathVariable("bucketname") String bucketname,
            @PathVariable("objectname") String objectname,
            @RequestParam("metadata") String metadataAction,
            @RequestParam("key") String key,
            @RequestBody String value
    ) {
        if (bucketService.addUpdateMetadataByKey(bucketname, objectname, key, value)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }

    }

    @RequestMapping(value = "/{bucketname}/{objectname}", params = "metadata", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteMetadataByKey(
            @PathVariable("bucketname") String bucketname,
            @PathVariable("objectname") String objectname,
            @RequestParam("metadata") String metadataAction,
            @RequestParam("key") String key
    ) {
        if (bucketService.deleteMetadataByKey(bucketname, objectname, key)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @RequestMapping(value = "/{bucketname}/{objectname}", params = "key", method = RequestMethod.GET)
    public ResponseEntity<HashMap<String, String>> getMetadataByKey(
            @PathVariable("bucketname") String bucketname,
            @PathVariable("objectname") String objectname,
            @RequestParam("metadata") String metadataAction,
            @RequestParam("key") String key
    ) {
        HashMap<String, String> ret = bucketService.getMetadataByKey(bucketname, objectname, key);
        if (ret != null) {
            return ResponseEntity.ok(ret);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @RequestMapping(value = "/{bucketname}/{objectname}", method = RequestMethod.GET)
    public ResponseEntity<HashMap<String, String>> getAllMetadata(
            @PathVariable("bucketname") String bucketname,
            @PathVariable("objectname") String objectname,
            @RequestParam("metadata") String metadataAction
    ) {
        HashMap<String, String> ret = bucketService.getAllMetadata(bucketname, objectname);
        if (ret != null) {
            return ResponseEntity.ok(ret);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @RequestMapping(value = "/{bucketname}", method = RequestMethod.GET)
    public ResponseEntity<BucketDTO> getObjectsInBucket(
            @PathVariable("bucketname") String bucketname,
            @RequestParam("list") String list
    ) {
        if (bucketService.isBucketExist(bucketname)) {
            BucketDTO bucketDTO = bucketService.listObjectsInBucket(bucketname);
            System.out.println(bucketDTO);
            return ResponseEntity.ok(bucketDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // TODO: 19/9/2018 AD Clean up return (type)
    @RequestMapping(value = "/{bucketname}/{objectname}", params = "partNumber", method = RequestMethod.PUT)
    public ResponseEntity<HashMap<String, String>> handleUploadPart(
            @PathVariable("bucketname") String bucketname,
            @PathVariable("objectname") String objectname,
            @RequestParam("partNumber") Integer partNumber,
            @RequestHeader("Content-Length") Integer partSize,
            @RequestHeader("Content-MD5") String partMd5,
            HttpServletRequest requestServlet
    ) {
        try {
            String md5 = storageService.storeFile(requestServlet, bucketname, objectname, partNumber, partSize, partMd5);
            HashMap<String, String> ret = new HashMap<>();
            ret.put("md5", md5);
            ret.put("length", partSize.toString());
            ret.put("partNumber", partNumber.toString());
            return ResponseEntity.ok(ret);
        } catch (FileStorageException e) {
            HashMap<String, String> ret = new HashMap<>();
            ret.put("md5", "md5");
            ret.put("length", partSize.toString());
            ret.put("partNumber", partNumber.toString());
            ret.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(ret);
        }

    }
}
