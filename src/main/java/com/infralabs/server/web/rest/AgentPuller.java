package com.infralabs.server.web.rest;

import java.io.IOException;
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
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.infralabs.server.AlphaServerConfigs;
import com.infralabs.server.domain.Alert;
import com.infralabs.server.scheduledjobs.AlertPoller;
import com.infralabs.server.service.EmailService;

@RestController
@RequestMapping("/")
public class AgentPuller {
	@Autowired	public  AlphaServerConfigs alphaServerConfig;
	RestTemplate restTemplate= new RestTemplate();
	ParameterizedTypeReference<String> ptr= new ParameterizedTypeReference<String>() {};
	
	@Autowired 
	public AlertPoller alertPoller;
	@Autowired
	public EmailService emailService;

@RequestMapping("/names")
	public Map<String,String> callAgents() {
		String retVal="";
		Map<String,String> data = new HashMap<String,String>();
		HttpHeaders headers = new HttpHeaders();
//		headers.setContentType(MediaType.APPLICATION_JSON);
//		headers.set("Authorization", "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImF1dGgiOiJST0xFX0FETUlOLFJPTEVfVVNFUiIsImV4cCI6MTQ4NDY3MTY3Nn0.ny5dPaOfJYsdkBURhm0qYJTP-gYyR8sd2mU-RL481YUo8VnwnFLuZpuxxaKgDIhSpsgXWPB5QXB6zBJSe4fDxQ");

		HttpEntity<String> entity = new HttpEntity<String>(headers);
//		retVal= restTemplate.exchange(String.format("http://%s:8761/api/eureka/applications", alphaServerConfig.getEurekaservice()) ,
//			    HttpMethod.GET, entity, ptr).getBody();
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> map ;
		HashMap<String,String> targets = alphaServerConfig.getTarget();
		for(String each : alphaServerConfig.getTarget().keySet()) {
			String publicip = alphaServerConfig.getTarget().get(each);
			data.put(publicip, restTemplate.exchange( String.format("http://%s:8089/monitor", publicip),
				    HttpMethod.GET, entity, ptr).getBody().replaceAll("\\\\", "").replaceAll("[\n\r]+", "").replace("\"[", "[").replace("]\"", "]"));
		}
		// convert JSON string to Map
		/*try {
			map = mapper.readValue(retVal, new TypeReference<Map<String, Object>>(){});
			ArrayList list= (ArrayList) map.get("applications");
			((LinkedHashMap)((ArrayList) ((LinkedHashMap)list.get(0)).get("instances")).get(0)).get("homePageUrl");
			if(((String) ((LinkedHashMap)list.get(0)).get("name")).equals("ALPHAAGENT")) {
				ArrayList<LinkedHashMap> instances=((ArrayList<LinkedHashMap>) ((LinkedHashMap)list.get(0)).get("instances"));
				if(alphaServerConfig.getTarget().keySet().size()>instances.size()) {
					data.put("error", "One of the agent is down");
				}
				for(LinkedHashMap each : instances) {
					String url = ((String) each.get("homePageUrl")).split("//")[1].split(":")[0];
					String publicip = alphaServerConfig.getTarget().get(url.replace(".", "-"));
					data.put(publicip, restTemplate.exchange( String.format("http://%s:8089/monitor", publicip),
						    HttpMethod.GET, entity, ptr).getBody().replaceAll("\\\\", "").replaceAll("[\n\r]+", "").replace("\"[", "[").replace("]\"", "]"));
				}
			}
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/

		return data;
		
	}

@RequestMapping(value="/startandstopservice",method = RequestMethod.GET)
public Map<String, String> serviceAction(@RequestParam(value="hostname") String hostname,@RequestParam(value="service") String service,@RequestParam(value="action") String action) {
	HttpHeaders headers = new HttpHeaders();
//	headers.setContentType(MediaType.APPLICATION_JSON);
//	headers.set("Authorization", "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImF1dGgiOiJST0xFX0FETUlOLFJPTEVfVVNFUiIsImV4cCI6MTQ4NDY3MTY3Nn0.ny5dPaOfJYsdkBURhm0qYJTP-gYyR8sd2mU-RL481YUo8VnwnFLuZpuxxaKgDIhSpsgXWPB5QXB6zBJSe4fDxQ");

	HttpEntity<String> entity = new HttpEntity<String>(headers);
	Map<String, String> params = new HashMap<String, String>();
	params.put("hostname", hostname);
	params.put("service", service);
	params.put("action", action);
	Map<String, String> map = new HashMap<String, String>();

	String outcome = restTemplate.exchange("http://"+hostname+":8089/performAction/{hostname}/{service}/{action}",  HttpMethod.GET, entity, String.class, params).getBody();
//	restTemplate.postForEntity("http://"+hostname+":8089/performAction?hostname="+hostname+"&service="+service+"&action="+action, HttpMethod.GET , String.class );
	if(outcome.equals("executed"))
		outcome="pass" ;
	 else
		outcome="fail";
		
	map.put("status", outcome);
	return map;
	}

//@RequestMapping(value = "/sendEmail", method = RequestMethod.GET)
//public Map<String, String> alertAction() {
//	Map<String, String> map = new HashMap<String, String>();
//	
//	String alerts = alertPoller.alertPusher();
//	List<String> allMails = new ArrayList<String>();
//	allMails.add("karthik@infralabs.io");
//	allMails.add("dinesh@infralabs.io");
//	if(alerts != null){
//	Alert myAlert = new Alert();
//	myAlert.setBody(alerts);
//	myAlert.setSubject("Alert from InfraLabs");
//	myAlert.setId(UUID.randomUUID().toString());
//	myAlert.setSendTo(allMails);
//	emailService.sendAlert(myAlert);
//	}
//	return map;
//	}

@RequestMapping(value = "/myalerts", method = RequestMethod.GET)
public Map<String, List<String>> alertAction() {
//	Map<String,String> data = new HashMap<String,String>();
//	HttpHeaders headers = new HttpHeaders();
//	HttpEntity<String> entity = new HttpEntity<String>(headers);
//	ObjectMapper mapper = new ObjectMapper();
//	String publicip = "localhost";
//	data.put(publicip, restTemplate.exchange( String.format("http://%s:8089/alerts", publicip),
//		    HttpMethod.GET, entity, ptr).getBody().replaceAll("\\\\", "").replaceAll("[\n\r]+", "").replace("\"[", "[").replace("]\"", "]"));
//
//	 String alerts = data.get(publicip);
	 Map<String, List<String>> map = new HashMap<String, List<String>>();
	List<String> alertsMap = new ArrayList<String>();
	alertsMap.addAll(AlertPoller.alertsMap);
	 map.put("something", alertsMap);
	 AlertPoller.alertsMap.clear();
	return map;
	}

}
