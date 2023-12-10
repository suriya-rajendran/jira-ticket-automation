package com.automation.jira.datamodel;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "jira_master")
@Data
public class JiraMaster implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = -7784833404068483884L;

	@Id
	@GeneratedValue(generator = "ID_GENERATOR")
	@Column(name = "id")
	private Long id;

	@Column(name = "summary_content")
	private String summaryContent;

	@Column(name = "description_content")
	private String descriptionContent;

	@Column(name = "product")
	private String product;

	@Column(name = "code")
	private String code;

	@Column(name = "type")
	private String type;

}
