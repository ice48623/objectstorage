package com.muic.objectstorage.Repository;

import com.muic.objectstorage.Entity.Metadata;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MetadataRepository extends CrudRepository<Metadata, Integer> {
}
