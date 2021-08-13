package com.x.program.center.jaxrs.collect;

import com.x.base.core.project.config.Collect;

import java.util.ArrayList;
import java.util.List;

public class WrapInUrlMapping extends Collect {

	public static List<String> Excludes = new ArrayList<>();

	private String urlMapping;

	public String getUrlMapping() {
		return urlMapping;
	}

	public void setUrlMapping(String urlMapping) {
		this.urlMapping = urlMapping;
	}

}
