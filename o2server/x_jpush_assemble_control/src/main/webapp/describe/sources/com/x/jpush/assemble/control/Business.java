package com.x.jpush.assemble.control;

import com.x.base.core.container.EntityManagerContainer;
import com.x.organization.core.express.Organization;
import com.x.jpush.assemble.control.factory.SampleEntityClassNameFactory;

public class Business {

	private EntityManagerContainer emc;



	public Business(EntityManagerContainer emc) throws Exception {
		this.emc = emc;
	}

	public EntityManagerContainer entityManagerContainer() {
		return this.emc;
	}

	//组织架构管理相关的工厂服务类
	private Organization organization;
	
	//示例数据表工厂服务类
	private SampleEntityClassNameFactory sampleEntityClassNameFactory;
	
	
	public SampleEntityClassNameFactory sampleEntityClassNameFactory() throws Exception {
		if (null == this.sampleEntityClassNameFactory) {
			this.sampleEntityClassNameFactory = new SampleEntityClassNameFactory( this );
		}
		return sampleEntityClassNameFactory;
	}

	public Organization organization() throws Exception {
		if (null == this.organization) {
			this.organization = new Organization(ThisApplication.context());
		}
		return organization;
	}




}
