package io.pms.api.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import io.pms.api.model.Common;

@Repository
public interface CommonRepository extends MongoRepository<Common, String> {

}
