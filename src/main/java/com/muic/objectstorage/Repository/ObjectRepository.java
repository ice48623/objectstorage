package com.muic.objectstorage.Repository;

import com.muic.objectstorage.Entity.Object;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ObjectRepository extends CrudRepository<Object, Integer> {
    Object findByName(String name);
    List<Object> findByBucketId(Integer bucketId);
}
