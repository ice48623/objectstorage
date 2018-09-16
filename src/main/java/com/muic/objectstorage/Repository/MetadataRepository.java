package com.muic.objectstorage.Repository;

import com.muic.objectstorage.Entity.Metadata;
import com.muic.objectstorage.Entity.ObjectMetadataComposite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MetadataRepository extends JpaRepository<Metadata, ObjectMetadataComposite> {
    List<Metadata> findByObjectId(Integer objectId);
}
