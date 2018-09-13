package com.muic.objectstorage.Controller;

import com.muic.objectstorage.DTO.CreateBucketDTO;
import com.muic.objectstorage.Service.BucketService;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
public class MainController {

    @RequestMapping(value = "/{bucketname}", method = RequestMethod.POST)
    public ResponseEntity<CreateBucketDTO> createBucket(@PathVariable("bucketname") String bucketname,
                                           @RequestParam("create") String createAction
    ) {
        try {
            BucketService.create(bucketname);
            long currentTime = new Date().getTime();
            return ResponseEntity.ok(new CreateBucketDTO(currentTime, currentTime, bucketname));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
