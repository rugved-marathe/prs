package io.pms.api.repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import io.pms.api.common.Status;
import io.pms.api.model.RoleTag;

@Repository
public interface RoleTagRepository extends MongoRepository<RoleTag, String> {

	public List<RoleTag> findByTagNameAndStatus(String tagName, Status status);
}
