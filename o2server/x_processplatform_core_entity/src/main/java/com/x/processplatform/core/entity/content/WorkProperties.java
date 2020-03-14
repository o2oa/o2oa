package com.x.processplatform.core.entity.content;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import com.x.base.core.entity.JsonProperties;

public class WorkProperties extends JsonProperties {

	private List<String> manualForceTaskIdentityList = new ArrayList<>();

	private LinkedHashMap<String, String> manualEmpowerMap = new LinkedHashMap<>();

	public List<String> getManualForceTaskIdentityList() {
		if (this.manualForceTaskIdentityList == null) {
			this.manualForceTaskIdentityList = new ArrayList<String>();
		}
		return this.manualForceTaskIdentityList;
	}

	public void setManualForceTaskIdentityList(List<String> manualForceTaskIdentityList) {
		this.manualForceTaskIdentityList = manualForceTaskIdentityList;
	}

	public LinkedHashMap<String, String> getManualEmpowerMap() {
		if (this.manualEmpowerMap == null) {
			this.manualEmpowerMap = new LinkedHashMap<String, String>();
		}
		return this.manualEmpowerMap;
	}

	public void setManualEmpowerMap(LinkedHashMap<String, String> manualEmpowerMap) {
		this.manualEmpowerMap = manualEmpowerMap;
	}

//	private String destinationRoute;
//
//	private String destinationRouteName;
//
//	private ActivityType destinationActivityType;
//
//	private String destinationActivity;

//	public String getDestinationRoute() {
//		return destinationRoute;
//	}
//
//	public void setDestinationRoute(String destinationRoute) {
//		this.destinationRoute = destinationRoute;
//	}
//
//	public String getDestinationRouteName() {
//		return destinationRouteName;
//	}
//
//	public void setDestinationRouteName(String destinationRouteName) {
//		this.destinationRouteName = destinationRouteName;
//	}
//
//	public ActivityType getDestinationActivityType() {
//		return destinationActivityType;
//	}
//
//	public void setDestinationActivityType(ActivityType destinationActivityType) {
//		this.destinationActivityType = destinationActivityType;
//	}
//
//	public String getDestinationActivity() {
//		return destinationActivity;
//	}
//
//	public void setDestinationActivity(String destinationActivity) {
//		this.destinationActivity = destinationActivity;
//	}

}
