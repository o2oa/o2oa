package com.x.base.core.project.script;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.project.AbstractContext;
import com.x.base.core.project.Applications;
import com.x.base.core.project.webservices.WebservicesClient;

public abstract class AbstractResources {

	private EntityManagerContainer entityManagerContainer;
	private AbstractContext context;
	private Applications applications;
	private WebservicesClient webservicesClient;

	public WebservicesClient getWebservicesClient() {
		return webservicesClient;
	}

	public void setWebservicesClient(WebservicesClient webservicesClient) {
		this.webservicesClient = webservicesClient;
	}

	public EntityManagerContainer getEntityManagerContainer() {
		return entityManagerContainer;
	}

	public void setEntityManagerContainer(EntityManagerContainer entityManagerContainer) {
		this.entityManagerContainer = entityManagerContainer;
	}

	public Applications getApplications() {
		return applications;
	}

	public void setApplications(Applications applications) {
		this.applications = applications;
	}

	public AbstractContext getContext() {
		return context;
	}

	public void setContext(AbstractContext context) {
		this.context = context;
	}

}