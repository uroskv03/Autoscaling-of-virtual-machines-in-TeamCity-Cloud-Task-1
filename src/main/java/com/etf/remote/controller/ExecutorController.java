package com.etf.remote.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.etf.remote.model.ExecutionTask;
import com.etf.remote.repository.TaskRepository;
import com.etf.remote.service.DockerService;



@RestController
@RequestMapping("/api")
public class ExecutorController {

    @Autowired
    private TaskRepository repository;

    @Autowired
    private DockerService dockerService;


    @PostMapping("/execute")
    public ExecutionTask createExecution(@RequestBody ExecutionTask task) {
        task.setStatus("QUEUED");

        if (task.getCpuCount() == null || task.getCpuCount() <= 0) {
            task.setCpuCount(1);
        }      
        ExecutionTask saved = repository.save(task);
        dockerService.handleNewTask(saved);
        return saved;
    }

    @GetMapping("/status/{id}")
    public ExecutionTask getStatus(@PathVariable String id) {
        return repository.findById(id).orElseThrow();
    }
    
}
