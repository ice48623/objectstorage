package com.muic.objectstorage.Repository;

import com.muic.objectstorage.Entity.Metadata;
import com.muic.objectstorage.Entity.ObjectMetadataComposite;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MetadataRepository extends CrudRepository<Metadata, Integer> {
    List<Metadata> findByObjectId(Integer objectId);

    Metadata findByName(String name);

    Metadata findByNameAndObjectId(String name, Integer objectId);
}
