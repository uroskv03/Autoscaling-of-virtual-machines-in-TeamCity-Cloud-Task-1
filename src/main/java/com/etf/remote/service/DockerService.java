package com.etf.remote.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.etf.remote.model.ExecutionTask;
import com.etf.remote.repository.TaskRepository;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.api.model.HostConfig;

import jakarta.annotation.PreDestroy;

@Service
public class DockerService {

    @Autowired
    private DockerClient dockerClient;

    @Autowired
    private TaskRepository repository;

    @Async
    public void handleNewTask(ExecutionTask task){
        try{

            dockerClient.pullImageCmd("alpine").withTag("latest").start().awaitCompletion();
            
            //long cpuLimit = ((task.getCpuCount()!= null && task.getCpuCount() > 0) ? task.getCpuCount() : 1) * 1000000000L;  
            //long cpuLimit = (task.getCpuCount()!= null ? task.getCpuCount() : 1) * 1000000000L;  
            long cpuLimit = task.getCpuCount() * 1000000000L;
            HostConfig hostConfig = HostConfig.newHostConfig().withNanoCPUs(cpuLimit);
            
            var container = dockerClient.createContainerCmd("alpine")
                        .withHostConfig(hostConfig)
                        .withCmd("/bin/sh", "-c", task.getCommand())
                        .exec();


            task.setStatus("IN_PROGRESS");
            task = repository.save(task);


            String containerId = container.getId();

            dockerClient.startContainerCmd(containerId).exec();


            StringBuilder outputBuilder = new StringBuilder();

            dockerClient.logContainerCmd(containerId)
                        .withStdOut(true)
                        .withStdErr(true)
                        .withFollowStream(true)
                        .exec(new ResultCallback.Adapter<Frame>(){
                            @Override
                            public void onNext(Frame item){
                                outputBuilder.append(new String(item.getPayload()));
                            }
                        }).awaitCompletion();
                     
            task.setOutput(outputBuilder.toString());

            dockerClient.waitContainerCmd(containerId).start().awaitCompletion();

            dockerClient.removeContainerCmd(containerId).exec();
            task.setStatus("FINISHED");
        } catch (Exception e) {
            e.printStackTrace();
            task.setStatus("FAILED");
        }
        repository.save(task);
    }

    public void runCommand(String command) {
        
        CreateContainerResponse container = dockerClient.createContainerCmd("alpine")
                .withCmd("/bin/sh", "-c", command)
                .exec();

        String containerId = container.getId();
        dockerClient.startContainerCmd(containerId).exec();
        System.out.println("Container started: " + containerId);
    }

    // @PreDestroy
    // public void cleanUpOnExit() {
    //     System.out.println("Finished!");

    //     try {
    //         List<ExecutionTask> activeTasks = repository.findAll().stream()
    //             .filter(t -> "QUEUED".equals(t.getStatus()) || "IN_PROGRESS".equals(t.getStatus()))
    //             .toList();

    //     for (ExecutionTask task : activeTasks) {
    //         task.setStatus("FAILED");
    //         task.setOutput((task.getOutput() != null ? task.getOutput() : "") 
    //                        + "\n[SYSTEM] Application shutdown interrupted this task.");
    //         repository.save(task);
    //     }
        
    //     System.out.println("All actice tasks (" + activeTasks.size() + ") are marked to FAILED.");
        
    //     } catch (Exception e) {
    //         System.err.println("Error: " + e.getMessage());
    //     }
    // }
    
}
