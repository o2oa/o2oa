package com.x.hotpic.assemble.control;

import com.x.base.core.container.EntityManagerContainer;
import com.x.hotpic.assemble.control.factory.HotPictureInfoFactory;
import com.x.organization.core.express.Organization;

public class Business {

	private EntityManagerContainer emc;
	
	public Business(EntityManagerContainer emc) throws Exception {
		this.emc = emc;
	}
	public EntityManagerContainer entityManagerContainer() {
		return this.emc;
	}
	
	private Organization organization;
	private HotPictureInfoFactory hotPictureInfoFactory;
	
	public Organization organization() throws Exception {
		if (null == this.organization) {
			this.organization = new Organization(ThisApplication.context());
		}
		return organization;
	}
	
	public HotPictureInfoFactory hotPictureInfoFactory() throws Exception {
		if (null == this.hotPictureInfoFactory) {
			this.hotPictureInfoFactory = new HotPictureInfoFactory( this );
		}
		return hotPictureInfoFactory;
	}
}
