package com.muic.objectstorage.Controller;

import com.muic.objectstorage.DTO.CreateBucketDTO;
import com.muic.objectstorage.Service.BucketService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
public class MainController {

    @RequestMapping(value = "/{bucketname}", method = RequestMethod.POST)
    public ResponseEntity<CreateBucketDTO> createBucket(
            @PathVariable("bucketname") String bucketname,
            @RequestParam("create") String createAction
    ) {
        try {
            if (!BucketService.create(bucketname)) {
                return ResponseEntity.badRequest().build();
            }
            long currentTime = new Date().getTime();
            return ResponseEntity.ok(new CreateBucketDTO(currentTime, currentTime, bucketname));
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
            if (BucketService.drop(bucketname)) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
