package com.automation.jira.pojo;

import java.io.Serializable;

import lombok.Data;

@Data
public class JiraRequest implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = -6249335678742958143L;
	public Fields fields;
}

