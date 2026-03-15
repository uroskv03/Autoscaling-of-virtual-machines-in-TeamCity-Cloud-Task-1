package com.etf.remote.repository;

import com.etf.remote.model.ExecutionTask;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends MongoRepository<ExecutionTask, String>{

}
