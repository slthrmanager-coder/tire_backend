

package com.example.tire_management.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.tire_management.model.TireRequest;

public interface TireRequestRepository extends MongoRepository<TireRequest, String> {

    List<TireRequest> findByStatusIn(List<String> statuses);
}
