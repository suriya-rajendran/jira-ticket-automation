package com.automation.jira.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.automation.jira.pojo.JiraData;
import com.automation.jira.service.JiraService;

@RestController
public class JiraController {
	
	@Autowired
	JiraService jiraService;
	
	@PostMapping(value="/rest/jira/create-ticket")
	public ResponseEntity<?> createTicket(@RequestBody JiraData data){
		return jiraService.createTicket(data);
	}

}
