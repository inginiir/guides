package com.kalita.repositories;

import com.kalita.entities.Profiles;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface ProfileRepo extends CrudRepository<Profiles, Long> {

    List<Profiles> findAllBySegmentIdAndVersionId(Long segmentId, UUID versionId);
    Page<Profiles> findAllBySegmentIdAndVersionId(Long segmentId, UUID versionId, Pageable pageable);
}
