package com.x.crm.assemble.control;

import com.x.base.core.container.EntityManagerContainer;
import com.x.crm.assemble.control.factory.ClueFactory;
import com.x.crm.assemble.control.factory.CrmBaseConfigFactory;
import com.x.crm.assemble.control.factory.CustomerBaseInfoFactory;
import com.x.crm.assemble.control.factory.OpportunityFactory;
import com.x.crm.assemble.control.factory.RegionFactory;
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
	//private HotPictureInfoFactory hotPictureInfoFactory;

	private CustomerBaseInfoFactory customerBaseInfoFactory;
	private OpportunityFactory opportunityFactory;
	private ClueFactory clueFactory;
	private CrmBaseConfigFactory crmBaseConfigFactory;
	private RegionFactory regionFactory;

	public Organization organization() throws Exception {
		if (null == this.organization) {
			this.organization = new Organization(ThisApplication.context());
		}
		return organization;
	}

	/*	
	public HotPictureInfoFactory hotPictureInfoFactory() throws Exception {
			if (null == this.hotPictureInfoFactory) {
				this.hotPictureInfoFactory = new HotPictureInfoFactory( this );
			}
			return hotPictureInfoFactory;
		}
	*/

	public CustomerBaseInfoFactory customerBaseInfoFactory() throws Exception {
		if (null == this.customerBaseInfoFactory) {
			this.customerBaseInfoFactory = new CustomerBaseInfoFactory(this);
		}
		return customerBaseInfoFactory;
	}

	public OpportunityFactory opportunityFactory() throws Exception {
		if (null == this.opportunityFactory) {
			this.opportunityFactory = new OpportunityFactory(this);
		}
		return opportunityFactory;
	}

	public ClueFactory clueFactory() throws Exception {
		if (null == this.clueFactory) {
			this.clueFactory = new ClueFactory(this);
		}
		return clueFactory;
	}

	public CrmBaseConfigFactory crmBaseConfigFactory() throws Exception {
		if (null == this.crmBaseConfigFactory) {
			this.crmBaseConfigFactory = new CrmBaseConfigFactory(this);
		}
		return crmBaseConfigFactory;
	}
	
	public RegionFactory regionFactory() throws Exception {
		if (null == this.regionFactory) {
			this.regionFactory = new RegionFactory(this);
		}
		return regionFactory;
	}

}
