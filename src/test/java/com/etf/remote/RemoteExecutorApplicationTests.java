package com.etf.remote;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.etf.remote.model.ExecutionTask;
import com.etf.remote.repository.TaskRepository;


@SpringBootTest
class RemoteExecutorApplicationTests {

	@Test
	void contextLoads() {
	}


	@Autowired
	private TaskRepository repository;

    @Test
    void testSaveToDatabase() {
        ExecutionTask task = new ExecutionTask();
        task.setCommand("echo 'Test'");
        task.setCpuCount(1);
        task.setStatus("QUEUED");
        
        repository.save(task);
        System.out.println("Task saved: " + task.getId());
    }

}


