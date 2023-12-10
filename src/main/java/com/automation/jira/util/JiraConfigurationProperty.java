package com.automation.jira.util;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Configuration
@ConfigurationProperties(prefix = "jira")
@Data
public class JiraConfigurationProperty {

	private String userName;

	private String url;

	private String token;

	private Map<String,String> projectKey=new HashMap<>();

	private String assigneeId;

	private String reporterId;
}
