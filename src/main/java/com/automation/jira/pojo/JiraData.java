package com.automation.jira.pojo;

import java.io.Serializable;
import java.util.HashMap;

import lombok.Data;

@Data
public class JiraData implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = 737323009082403872L;

	public String product;

	public String errorCode;

	public String type;

	public ISSUETYPE issueTypeName;

	public String errorMessage;

	public PRIORITYID priorityId;

	public HashMap<String, String> content = new HashMap<>();

	public enum PRIORITYID {

		SHOWSTOPPER("1"), CRITICAL("2"), MAJOR("3"), MINOR("4"), TRIVIAL("5");

		private final String key;

		PRIORITYID(String key) {
			this.key = key;
		}

		public String getKey() {
			return key;
		}

	}

	public enum ISSUETYPE {

		TASK("Task"), BUG("Bug");

		private final String key;

		ISSUETYPE(String key) {
			this.key = key;
		}

		public String getKey() {
			return key;
		}

	}

}
