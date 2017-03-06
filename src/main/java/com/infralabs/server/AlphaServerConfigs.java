package com.infralabs.server;

import java.util.HashMap;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "alphaserver")
public class AlphaServerConfigs {
	private String eurekaservice;

	private HashMap<String, String> target;
	private List<String> admins;
	private List<String> mails;

	public HashMap<String, String> getTarget() {
		return target;
	}

	public void setTarget(HashMap<String, String> somedata) {
		this.target = somedata;
	}

	public String getEurekaservice() {
		return eurekaservice;
	}

	public void setEurekaservice(String eurekaservice) {
		this.eurekaservice = eurekaservice;
	}

	public List<String> getAdmins() {
		return admins;
	}

	public void setAdmins(List<String> admins) {
		this.admins = admins;
	}

	public List<String> getMails() {
		return mails;
	}

	public void setMails(List<String> mails) {
		this.mails = mails;
	}
}
