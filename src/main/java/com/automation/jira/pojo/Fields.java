package com.automation.jira.pojo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class Fields implements Serializable{
	
	
    /**
	 * 
	 */
	private static final long serialVersionUID = 6830904179344850442L;
	
	public Project project;
    public String summary;
    public String description;
    public Assignee assignee;
    public Reporter reporter;
    public Issuetype issuetype;
    public Priority priority;
    public List<String> labels=new ArrayList<>();
}
