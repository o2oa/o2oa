package com.x.base.core.project;

import java.util.ArrayList;
import java.util.List;

public class Dependency {

	public Dependency() {
		this.customJars = new ArrayList<String>();
		this.storeJars = new ArrayList<String>();
		this.containerEntities = new ArrayList<String>();
		this.storageTypes = new ArrayList<String>();
	}

	public List<String> customJars = new ArrayList<>();

	public List<String> storeJars = new ArrayList<>();

	public List<String> containerEntities = new ArrayList<>();

	public List<String> storageTypes = new ArrayList<>();

}
