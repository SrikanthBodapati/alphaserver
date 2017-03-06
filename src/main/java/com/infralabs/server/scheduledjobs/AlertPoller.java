package com.infralabs.server.scheduledjobs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.infralabs.server.AlphaServerConfigs;
import com.infralabs.server.domain.Alert;
import com.infralabs.server.service.EmailService;

@Component
public class AlertPoller {
	@Autowired	public  AlphaServerConfigs alphaServerConfig;
	public static List<String> alertsMap = new ArrayList<String>();
	RestTemplate restTemplate= new RestTemplate();
	ParameterizedTypeReference<String> ptr= new ParameterizedTypeReference<String>() {};
	@Autowired
	public EmailService emailService;
	@Scheduled(fixedRateString = "${alert.run.frequency:5000}")
	public void alertPusher() {
//		List<String> allMails = new ArrayList<String>();
//		allMails.add("karthik@infralabs.io");
//		allMails.add("dinesh@infralabs.io");		
		Map<String,String> data = new HashMap<String,String>();
		HttpHeaders headers = new HttpHeaders();
//		headers.setContentType(MediaType.APPLICATION_JSON);
//		headers.set("Authorization", "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImF1dGgiOiJST0xFX0FETUlOLFJPTEVfVVNFUiIsImV4cCI6MTQ4NDY3MTY3Nn0.ny5dPaOfJYsdkBURhm0qYJTP-gYyR8sd2mU-RL481YUo8VnwnFLuZpuxxaKgDIhSpsgXWPB5QXB6zBJSe4fDxQ");

		HttpEntity<String> entity = new HttpEntity<String>(headers);
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> map ;
		HashMap<String,String> targets = alphaServerConfig.getTarget();
//		for(String each : alphaServerConfig.getTarget().keySet()) {
//			String publicip = alphaServerConfig.getTarget().get(each);
//			data.put(publicip, restTemplate.exchange( String.format("http://%s:8089/monitor", publicip),
//				    HttpMethod.GET, entity, ptr).getBody().replaceAll("\\\\", "").replaceAll("[\n\r]+", "").replace("\"[", "[").replace("]\"", "]"));
//		}
		String publicip = "localhost";
		data.put(publicip, restTemplate.exchange( String.format("http://%s:8089/alerts", publicip),
			    HttpMethod.GET, entity, ptr).getBody().replaceAll("\\\\", "").replaceAll("[\n\r]+", "").replace("\"[", "[").replace("]\"", "]"));

		 String alerts = data.get(publicip);
		 alertsMap.add(alerts);
		 System.out.println(alerts);
		if (alerts != null) {
			Alert myAlert = new Alert();
			myAlert.setBody(alerts);
			myAlert.setSubject("Alert from InfraLabs");
			myAlert.setId(UUID.randomUUID().toString());
			myAlert.setSendTo(alphaServerConfig.getMails());
			emailService.sendAlert(myAlert);
		}
	}


}
