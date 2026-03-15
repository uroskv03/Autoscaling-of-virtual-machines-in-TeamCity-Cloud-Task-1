package com.etf.remote.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.api.DockerClient;

@Configuration
public class DockerConfig {

    @Bean
    public DockerClient dockerClient(){

    //     DefaultDockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
    //             .withDockerHost("tcp://localhost:2375")
    //             .build();
                
    //     return DockerClientBuilder.getInstance(config).build();
    // }
        String dockerHost = "npipe:////./pipe/docker_engine";


        DefaultDockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                                    .withDockerHost(dockerHost)
                                    .build();
  
        ApacheDockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
                                        .dockerHost(config.getDockerHost())
                                        .sslConfig(config.getSSLConfig())
                                        .build();

        return DockerClientBuilder.getInstance(config)
                .withDockerHttpClient(httpClient)
                .build();

    }
    
}
