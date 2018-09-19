package com.muic.objectstorage.Controller;

import com.muic.objectstorage.DTO.BucketDTO;
import com.muic.objectstorage.DTO.CompleteUploadResponse;
import com.muic.objectstorage.DTO.CreateBucketDTO;
import com.muic.objectstorage.DTO.FileUploadResponse;
import com.muic.objectstorage.Entity.Bucket;
import com.muic.objectstorage.Exception.FileStorageException;
import com.muic.objectstorage.Service.BucketService;
import com.muic.objectstorage.Service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
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
            bucketService.drop(bucketname);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            System.out.println(e.getMessage());
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
            bucketService.createTicket(bucketname, objectname);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @RequestMapping(value = "/{bucketname}/{objectname}", params = "delete", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteObject(
            @PathVariable("bucketname") String bucketname,
            @PathVariable("objectname") String objectname,
            @RequestParam("delete") String deleteAction
    ) {
        try {
            bucketService.deleteObject(bucketname, objectname);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
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
        try {
            bucketService.addUpdateMetadataByKey(bucketname, objectname, key, value);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
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
        try {
            bucketService.deleteMetadataByKey(bucketname, objectname, key);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            System.out.println(e.getMessage());
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
        try {
            HashMap<String, String> ret = bucketService.getMetadataByKey(bucketname, objectname, key);
            return ResponseEntity.ok(ret);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            switch (e.getMessage()) {
                case "No value present":
                    return ResponseEntity.ok(new HashMap<>());

                default: return ResponseEntity.notFound().build();
            }
        }
    }

    @RequestMapping(value = "/{bucketname}/{objectname}", method = RequestMethod.GET)
    public ResponseEntity<HashMap<String, String>> getAllMetadata(
            @PathVariable("bucketname") String bucketname,
            @PathVariable("objectname") String objectname,
            @RequestParam("metadata") String metadataAction
    ) {
        try {
            HashMap<String, String> ret = bucketService.getAllMetadata(bucketname, objectname);
            return ResponseEntity.ok(ret);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @RequestMapping(value = "/{bucketname}", method = RequestMethod.GET)
    public ResponseEntity<BucketDTO> getObjectsInBucket(
            @PathVariable("bucketname") String bucketname,
            @RequestParam("list") String list
    ) {
        try {
            BucketDTO bucketDTO = bucketService.listObjectsInBucket(bucketname);
            return ResponseEntity.ok(bucketDTO);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @RequestMapping(value = "/{bucketname}/{objectname}", params = "partNumber", method = RequestMethod.PUT)
    public ResponseEntity<?> handleUploadPart(
            @PathVariable("bucketname") String bucketname,
            @PathVariable("objectname") String objectname,
            @RequestParam("partNumber") Integer partNumber,
            @RequestHeader("Content-Length") Integer partSize,
            @RequestHeader("Content-MD5") String partMd5,
            HttpServletRequest requestServlet
    ) {
        try {
            String md5 = storageService.storeFile(requestServlet, bucketname, objectname, partNumber, partSize, partMd5);
            return ResponseEntity.ok(new FileUploadResponse.normal(md5, partSize, partNumber));
        } catch (FileStorageException e) {
            return ResponseEntity.badRequest().body(new FileUploadResponse.withError(partMd5, partSize, partNumber, e.getMessage()));
        }
    }

    @RequestMapping(value = "/{bucketname}/{objectname}", method = RequestMethod.DELETE)
    public ResponseEntity<String> deletePart(
            @PathVariable("bucketname") String bucketname,
            @PathVariable("objectname") String objectname,
            @RequestParam("partNumber") Integer partNumber
    ) {
        try {
            bucketService.deletePart(bucketname, objectname, partNumber);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    // TODO: 19/9/2018 AD Verify with AJ about "eTag" and "length" of error response
    @RequestMapping(value = "/{bucketname}/{objectname}", params = "complete", method = RequestMethod.POST)
    public ResponseEntity<?> completeUpload(
            @PathVariable("bucketname") String bucketname,
            @PathVariable("objectname") String objectname,
            @RequestParam("complete") String complete
    ) {

        try {
            HashMap<String, String> ret = bucketService.completeUpload(bucketname, objectname);
            return ResponseEntity.ok(new CompleteUploadResponse.normal(ret.get("eTag"), Integer.valueOf(ret.get("length")), ret.get("name")));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
