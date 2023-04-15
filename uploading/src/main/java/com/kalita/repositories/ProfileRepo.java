package com.kalita.repositories;

import com.kalita.entities.Profiles;
import org.springframework.data.repository.CrudRepository;

public interface ProfileRepo extends CrudRepository<Profiles, Long> {
}
