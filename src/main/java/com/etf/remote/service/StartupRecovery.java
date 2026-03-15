package com.etf.remote.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.etf.remote.model.ExecutionTask;
import com.etf.remote.repository.TaskRepository;



@Component
public class StartupRecovery implements CommandLineRunner {
    
    @Autowired 
    private TaskRepository repository;

    @Override
    public void run(String... args) {
        System.out.println(">>> Startup Recovery: Checking database...");
        
        List<ExecutionTask> tasks = repository.findAll();
        
        tasks.stream()
            .filter(t -> "IN_PROGRESS".equals(t.getStatus()) || "QUEUED".equals(t.getStatus()))
            .forEach(t -> {
                t.setStatus("FAILED");
                String oldOutput = (t.getOutput() != null) ? t.getOutput() : "";
                t.setOutput(oldOutput + "\n[RECOVERY] App crashed/restarted during execution.");
                repository.save(t);
                System.out.println(">>> Recovered task: " + t.getId());
            });
    }
}
