package com.automation.jira.pojo;
import java.io.Serializable;

import lombok.Data;

@Data
public class Priority implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = -3726775999352918379L;
	public String id="1";
	
	//priority 1-showstopper 2-critical 3-major 4-minor 5-Trivial
}

