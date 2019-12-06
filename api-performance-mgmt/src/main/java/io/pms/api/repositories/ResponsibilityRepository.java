package io.pms.api.repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import io.pms.api.model.Responsibility;

@Repository
public interface ResponsibilityRepository extends MongoRepository<Responsibility, String> {
    List<Responsibility> findByRoleId(String roleId);
    Responsibility findByRoleIdAndResponsibilityDescription(String roleId, String responsibilityDescription);
}
