package com.x.strategydeploy.assemble.control;

import com.x.base.core.container.EntityManagerContainer;
import com.x.organization.core.express.Organization;
import com.x.strategydeploy.assemble.control.factory.AttachmentFactory;
import com.x.strategydeploy.assemble.control.factory.ConfigFactory;
import com.x.strategydeploy.assemble.control.factory.KeyWorkInfoFactory;
import com.x.strategydeploy.assemble.control.factory.MeasuresInfoFactory;
import com.x.strategydeploy.assemble.control.factory.StrategyDeployFactory;

public class Business {

	private EntityManagerContainer emc;

	public Business(EntityManagerContainer emc) throws Exception {
		this.emc = emc;
	}

	public EntityManagerContainer entityManagerContainer() {
		return this.emc;
	}

	private Organization organization;
	private StrategyDeployFactory strategyDeployFactory;
	private MeasuresInfoFactory measuresInfoFactory;
	private KeyWorkInfoFactory keyworkInfoFactory;
	private AttachmentFactory attachmentFactory;
	private ConfigFactory configFactory;

	public Organization organization() throws Exception {
		if (null == this.organization) {
			this.organization = new Organization(ThisApplication.context());
		}
		return organization;
	}

	public StrategyDeployFactory strategyDeployFactory() throws Exception {
		if (null == this.strategyDeployFactory) {
			this.strategyDeployFactory = new StrategyDeployFactory(this);
		}
		return strategyDeployFactory;
	}

	public MeasuresInfoFactory measuresInfoFactory() throws Exception {
		if (null == this.measuresInfoFactory) {
			this.measuresInfoFactory = new MeasuresInfoFactory(this);
		}
		return measuresInfoFactory;
	}

	public KeyWorkInfoFactory keyworkInfoFactory() throws Exception {
		if (null == this.keyworkInfoFactory) {
			this.keyworkInfoFactory = new KeyWorkInfoFactory(this);
		}
		return keyworkInfoFactory;
	}

	public AttachmentFactory attachmentFactory() throws Exception {
		if (null == this.attachmentFactory) {
			this.attachmentFactory = new AttachmentFactory(this);
		}
		return attachmentFactory;
	}
	
	public ConfigFactory configFactory() throws Exception {
		if (null == this.configFactory) {
			this.configFactory = new ConfigFactory(this);
		}
		return configFactory;
	}
}
