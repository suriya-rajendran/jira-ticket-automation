package com.automation.jira.service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Base64;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.automation.jira.datamodel.JiraMaster;
import com.automation.jira.pojo.Assignee;
import com.automation.jira.pojo.Fields;
import com.automation.jira.pojo.Issuetype;
import com.automation.jira.pojo.JiraData;
import com.automation.jira.pojo.JiraRequest;
import com.automation.jira.pojo.JiraSuccessResponse;
import com.automation.jira.pojo.Priority;
import com.automation.jira.pojo.Project;
import com.automation.jira.pojo.Reporter;
import com.automation.jira.repo.JiraMasterRepo;
import com.automation.jira.util.JiraConfigurationProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode; 

@Service
public class JiraService {

	public Logger logger = LoggerFactory.getLogger(this.getClass().getName());

	@Autowired
	JiraConfigurationProperty jiraConfigurationProperty;

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	JiraMasterRepo jiraMasterRepo;

	@Value("${error.file.path:}")
	private String errorFilePath;

	@Value("${jira.enable:false}")
	private boolean jiraEnable;

	@Value("${jira.attachment:false}")
	private boolean jiraAttachment;

	public ResponseEntity<?> createTicket(@RequestBody JiraData data) {
		JiraRequest request = new JiraRequest();
		String summary = null;

		String description = null;
		if (jiraEnable) {
			try {

				JiraMaster jiraMaster = jiraMasterRepo.findByProductAndCodeAndType(data.getProduct(),
						data.getErrorCode(), data.getType());
				if (jiraMaster != null) {
					summary = jiraMaster.getSummaryContent();

					description = jiraMaster.getDescriptionContent();

					for (Entry<String, String> vo : data.getContent().entrySet()) {

						summary = summary.replace("${" + vo.getKey().toUpperCase() + "}", vo.getValue());
						description = description.replace("${" + vo.getKey().toUpperCase() + "}", vo.getValue());

					}
					logger.info("summary: {} -------- description: {}", summary, description);

					getJiraRequest(request, data, summary, description);

					ObjectMapper mapper = new ObjectMapper();
					String jsonStr = mapper.writeValueAsString(request);
					JsonNode node = mapper.readValue(jsonStr, JsonNode.class);

					updateJsonNode(request, node);
					logger.debug("create request: {}", node.toString());

					HttpEntity<?> httpEntity = new HttpEntity<>(node.toString(), getHeaders(false));

					ResponseEntity<JiraSuccessResponse> result = restTemplate
							.postForEntity(jiraConfigurationProperty.getUrl(), httpEntity, JiraSuccessResponse.class);

					if (jiraAttachment) {
						initiateJiraAttachment(data, result, summary, description);
					}
				} else {
					logger.error("no jira master found for product: {} code: {} type: {}", data.getProduct(),
							data.getErrorCode(), data.getType());
				}
			} catch (RestClientException | IOException e) {
				logger.error("failed for creating jira ticket ", summary); 
			}
		}
		return new ResponseEntity<>("Successful", HttpStatus.OK);
	}

	private void updateJsonNode(JiraRequest request, JsonNode node) {
		if (request.getFields().getAssignee() == null
				|| StringUtils.isEmpty(request.getFields().getAssignee().getId())) {
			((ObjectNode) node.get("fields")).remove("assignee");
		}
		if (request.getFields().getReporter() == null
				|| StringUtils.isEmpty(request.getFields().getReporter().getId())) {
			((ObjectNode) node.get("fields")).remove("reporter");
		}
	}

	private void initiateJiraAttachment(JiraData data, ResponseEntity<JiraSuccessResponse> result, String summary,
			String description) {
		try {
			if (StringUtils.isNotEmpty(data.getErrorMessage())) {

				String fileName = errorFilePath + File.separator + result.getBody().getKey() + ".txt";

				File file = new File(fileName);
				FileUtils.writeStringToFile(file, data.getErrorMessage(), Charset.forName("UTF-8"));

				MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
				body.add("file", new FileSystemResource(fileName));

				HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, getHeaders(true));

				String url = jiraConfigurationProperty.getUrl() + File.separator + result.getBody().getId()
						+ File.separator + "attachments";

				ResponseEntity<String> results = restTemplate.postForEntity(url, requestEntity, String.class);
				logger.info("attachment response: ", results.getStatusCode());
			}
		} catch (RestClientException | IOException e) {
			logger.info("failed to add attachment for ticket: ", result.getBody().getKey()); 
		}
	}

	private void getJiraRequest(JiraRequest request, JiraData data, String summary, String description) {
		Fields field = new Fields();

		field.setDescription(description);
		field.setSummary(summary);

		Project project = new Project();
		project.setKey(jiraConfigurationProperty.getProjectKey().get(data.getProduct()));
		field.setProject(project);

		if (!StringUtils.isEmpty(jiraConfigurationProperty.getAssigneeId())) {
			Assignee assignee = new Assignee();
			assignee.setId(jiraConfigurationProperty.getAssigneeId());
			field.setAssignee(assignee);
		}

		if (!StringUtils.isEmpty(jiraConfigurationProperty.getReporterId())) {
			Reporter reporter = new Reporter();
			reporter.setId(jiraConfigurationProperty.getReporterId());
			field.setReporter(reporter);
		}
		Issuetype issuetype = new Issuetype();
		issuetype.setName(data.getIssueTypeName().getKey());
		field.setIssuetype(issuetype);

		Priority priority = new Priority();
		priority.setId(data.priorityId.getKey());
		field.setPriority(priority);

		request.setFields(field);
	}
 
	private HttpHeaders getHeaders(boolean attachment) {
		HttpHeaders header = new HttpHeaders();

		header.set("Authorization", getToken());
		if (attachment) {
			header.set("X-Atlassian-Token", "nocheck");
			header.setContentType(MediaType.MULTIPART_FORM_DATA);
		} else {
			header.set("Content-Type", "application/json");
			header.set("Accept", "application/json");
		}
		return header;

	}

	private String getToken() {
		String token = "Basic " + Base64.getEncoder().encodeToString(
				(jiraConfigurationProperty.getUserName() + ":" + jiraConfigurationProperty.getToken()).getBytes());
		logger.info(token);
		return token;
	}
}
