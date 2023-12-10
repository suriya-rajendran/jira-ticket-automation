package com.automation.jira.pojo;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class JiraSuccessResponse implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4903220495310219548L;

	public String id;
	
	public String key;
	
	public String self;
	
	public String filename;
	
	
}
