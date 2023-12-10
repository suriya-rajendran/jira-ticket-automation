package com.automation.jira.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.automation.jira.datamodel.JiraMaster;

public interface JiraMasterRepo extends JpaRepository<JiraMaster, Long> {

	JiraMaster findByProductAndCodeAndType(String product, String code, String type);

}
