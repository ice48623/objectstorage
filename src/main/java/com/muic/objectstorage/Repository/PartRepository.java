package com.muic.objectstorage.Repository;

import com.muic.objectstorage.Entity.ObjectPartComposite;
import com.muic.objectstorage.Entity.Part;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PartRepository extends CrudRepository<Part, ObjectPartComposite> {
    List<Part> findByObjectId(Integer objectId);

    @Query("SELECT SUM(p.length) FROM Part p")
    Long getObjectLength();
}
