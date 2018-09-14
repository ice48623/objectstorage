package com.muic.objectstorage.Repository;

import com.muic.objectstorage.Entity.Bucket;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BucketRepository extends CrudRepository<Bucket, Integer> {
    Bucket findByName(String name);
}
