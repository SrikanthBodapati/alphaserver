package com.infralabs.server.domain;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;

public class Alert  implements Serializable {

    private static final long serialVersionUID = 1L;

	private String id;

	private ZonedDateTime createdDate = ZonedDateTime.now();

	
	private List<String> sendTo;

	private String subject;

	private String body;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<String> getSendTo() {
		return sendTo;
	}

	public void setSendTo(List<String> sendTo) {
		this.sendTo = sendTo;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

}
