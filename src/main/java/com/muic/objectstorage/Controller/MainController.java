package com.muic.objectstorage.Controller;

import com.muic.objectstorage.DTO.CreateBucketDTO;
import com.muic.objectstorage.Entity.Bucket;
import com.muic.objectstorage.Service.BucketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
public class MainController {

    @Autowired
    BucketService bucketService;

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
            @RequestParam("key") String key
    ) {
        if (bucketService.addUpdateMetadataByKey(bucketname, objectname, key)) {
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

    @RequestMapping(value = "/{bucketname}/{objectname}", method = RequestMethod.GET)
    public ResponseEntity<HashMap<String, String>> getMetadataByKey(
            @PathVariable("bucketname") String bucketname,
            @PathVariable("objectname") String objectname,
            @RequestParam("metadata") String metadataAction,
            @RequestParam("key") String key
    ) {
        HashMap<String, String> ret = bucketService.getMetadataByKey(bucketname, objectname, key);
        if (!ret.isEmpty()) {
            return ResponseEntity.ok(ret);
        } else {
            return ResponseEntity.notFound().build();
        }

    }
}
