package com.x.hotpic.assemble.control;

import com.x.base.core.container.EntityManagerContainer;
import com.x.hotpic.assemble.control.factory.HotPictureInfoFactory;

public class Business {

	private EntityManagerContainer emc;
	
	public Business(EntityManagerContainer emc) throws Exception {
		this.emc = emc;
	}
	public EntityManagerContainer entityManagerContainer() {
		return this.emc;
	}
	

	private HotPictureInfoFactory hotPictureInfoFactory;
	
	public HotPictureInfoFactory hotPictureInfoFactory() throws Exception {
		if (null == this.hotPictureInfoFactory) {
			this.hotPictureInfoFactory = new HotPictureInfoFactory( this );
		}
		return hotPictureInfoFactory;
	}
}
