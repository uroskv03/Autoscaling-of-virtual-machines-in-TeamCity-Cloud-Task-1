package com.etf.remote.model;

import org.springframework.data.annotation.Id;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "tasks123")  
public class ExecutionTask {
    @Id
    private String id;
    private String command;
    private Integer cpuCount;
    private String status; // QUEUED, IN_PROGRESS, FINISHED
    private String output;
}
