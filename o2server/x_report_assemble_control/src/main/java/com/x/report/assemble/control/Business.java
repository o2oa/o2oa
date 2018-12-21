package com.x.report.assemble.control;

import com.x.base.core.container.EntityManagerContainer;
import com.x.organization.core.express.Organization;
import com.x.report.assemble.control.factory.Report_C_WorkPlanFactory;
import com.x.report.assemble.control.factory.Report_C_WorkPlanNextDetailFactory;
import com.x.report.assemble.control.factory.Report_C_WorkPlanNextFactory;
import com.x.report.assemble.control.factory.Report_C_WorkProgDetailFactory;
import com.x.report.assemble.control.factory.Report_C_WorkProgFactory;
import com.x.report.assemble.control.factory.Report_I_BaseFactory;
import com.x.report.assemble.control.factory.Report_I_DetailFactory;
import com.x.report.assemble.control.factory.Report_I_Ext_ContentDetailFactory;
import com.x.report.assemble.control.factory.Report_I_Ext_ContentFactory;
import com.x.report.assemble.control.factory.Report_I_WorkInfoFactory;
import com.x.report.assemble.control.factory.Report_I_WorkTagFactory;
import com.x.report.assemble.control.factory.Report_I_WorkTagUnitFactory;
import com.x.report.assemble.control.factory.Report_P_MeasureInfoFactory;
import com.x.report.assemble.control.factory.Report_P_PermissionFactory;
import com.x.report.assemble.control.factory.Report_P_ProfileDetailFactory;
import com.x.report.assemble.control.factory.Report_P_ProfileFactory;
import com.x.report.assemble.control.factory.Report_R_CreateTimeFactory;
import com.x.report.assemble.control.factory.Report_S_SettingFactory;

public class Business {

	private EntityManagerContainer emc;

	public Business(EntityManagerContainer emc) throws Exception {
		this.emc = emc;
	}

	public EntityManagerContainer entityManagerContainer() {
		return this.emc;
	}

	private Organization organization;
	
	private Report_S_SettingFactory report_S_SettingFactory;
	
	private Report_I_BaseFactory report_I_BaseFactory;
	
	private Report_I_DetailFactory report_I_DetailFactory;
	
	private Report_P_PermissionFactory report_P_PermissionFactory;
	
	private Report_P_ProfileFactory report_P_ProfileFactory;

    private Report_P_MeasureInfoFactory report_P_MeasureInfoFactory;

    private Report_I_WorkInfoFactory report_I_WorkInfoFactory;
    
    private Report_I_WorkTagFactory report_I_WorkTagFactory;
    
    private Report_I_WorkTagUnitFactory report_I_WorkTagUnitFactory;
	
	private Report_R_CreateTimeFactory report_R_CreateTimeFactory;
	
	private Report_P_ProfileDetailFactory report_P_ProfileDetailFactory;
	
	private Report_C_WorkPlanFactory report_C_WorkPlanFactory;
	
	private Report_C_WorkPlanNextFactory report_C_WorkPlanNextFactory;
	
	private Report_C_WorkPlanNextDetailFactory report_C_WorkPlanNextDetailFactory;
	
	private Report_C_WorkProgFactory report_C_WorkProgFactory;
	
	private Report_C_WorkProgDetailFactory report_C_WorkProgDetailFactory;
	
	private Report_I_Ext_ContentFactory report_I_Ext_ContentFactory;
	
	private Report_I_Ext_ContentDetailFactory report_I_Ext_ContentDetailFactory;
	
	public Report_I_Ext_ContentFactory report_I_Ext_ContentFactory() throws Exception {
		if (null == this.report_I_Ext_ContentFactory) {
			this.report_I_Ext_ContentFactory = new Report_I_Ext_ContentFactory( this );
		}
		return report_I_Ext_ContentFactory;
	}
	
	public Report_I_Ext_ContentDetailFactory report_I_Ext_ContentDetailFactory() throws Exception {
		if (null == this.report_I_Ext_ContentDetailFactory) {
			this.report_I_Ext_ContentDetailFactory = new Report_I_Ext_ContentDetailFactory( this );
		}
		return report_I_Ext_ContentDetailFactory;
	}
	
	public Report_I_WorkTagFactory report_I_WorkTagFactory() throws Exception {
        if (null == this.report_I_WorkTagFactory) {
            this.report_I_WorkTagFactory = new Report_I_WorkTagFactory( this );
        }
        return report_I_WorkTagFactory;
    }
	
	public Report_I_WorkTagUnitFactory report_I_WorkTagUnitFactory() throws Exception {
        if (null == this.report_I_WorkTagUnitFactory) {
            this.report_I_WorkTagUnitFactory = new Report_I_WorkTagUnitFactory( this );
        }
        return report_I_WorkTagUnitFactory;
    }
	
    public Report_I_WorkInfoFactory report_I_WorkInfoFactory() throws Exception {
        if (null == this.report_I_WorkInfoFactory) {
            this.report_I_WorkInfoFactory = new Report_I_WorkInfoFactory( this );
        }
        return report_I_WorkInfoFactory;
    }

    public Report_P_MeasureInfoFactory report_P_MeasureInfoFactory() throws Exception {
        if (null == this.report_P_MeasureInfoFactory) {
            this.report_P_MeasureInfoFactory = new Report_P_MeasureInfoFactory( this );
        }
        return report_P_MeasureInfoFactory;
    }

	public Report_P_PermissionFactory report_P_PermissionFactory() throws Exception {
		if (null == this.report_P_PermissionFactory) {
			this.report_P_PermissionFactory = new Report_P_PermissionFactory( this );
		}
		return report_P_PermissionFactory;
	}
	
	public Report_C_WorkPlanFactory report_C_WorkPlanFactory() throws Exception {
		if (null == this.report_C_WorkPlanFactory) {
			this.report_C_WorkPlanFactory = new Report_C_WorkPlanFactory( this );
		}
		return report_C_WorkPlanFactory;
	}
	
	public Report_C_WorkProgDetailFactory report_C_WorkProgDetailFactory() throws Exception {
		if (null == this.report_C_WorkProgDetailFactory) {
			this.report_C_WorkProgDetailFactory = new Report_C_WorkProgDetailFactory( this );
		}
		return report_C_WorkProgDetailFactory;
	}
	
	public Report_C_WorkPlanNextFactory report_C_WorkPlanNextFactory() throws Exception {
		if (null == this.report_C_WorkPlanNextFactory) {
			this.report_C_WorkPlanNextFactory = new Report_C_WorkPlanNextFactory( this );
		}
		return report_C_WorkPlanNextFactory;
	}
	
	public Report_C_WorkPlanNextDetailFactory report_C_WorkPlanNextDetailFactory() throws Exception {
		if (null == this.report_C_WorkPlanNextDetailFactory) {
			this.report_C_WorkPlanNextDetailFactory = new Report_C_WorkPlanNextDetailFactory( this );
		}
		return report_C_WorkPlanNextDetailFactory;
	}
	
	public Report_C_WorkProgFactory report_C_WorkProgFactory() throws Exception {
		if (null == this.report_C_WorkProgFactory) {
			this.report_C_WorkProgFactory = new Report_C_WorkProgFactory( this );
		}
		return report_C_WorkProgFactory;
	}

	public Organization organization() throws Exception {
		if (null == this.organization) {
			this.organization = new Organization(ThisApplication.context());
		}
		return organization;
	}
	
	public Report_I_BaseFactory report_I_BaseFactory() throws Exception {
		if (null == this.report_I_BaseFactory) {
			this.report_I_BaseFactory = new Report_I_BaseFactory( this );
		}
		return report_I_BaseFactory;
	}
	
	public Report_I_DetailFactory report_I_DetailFactory() throws Exception {
		if (null == this.report_I_DetailFactory) {
			this.report_I_DetailFactory = new Report_I_DetailFactory( this );
		}
		return report_I_DetailFactory;
	}
	
	public Report_S_SettingFactory report_S_SettingFactory() throws Exception {
		if (null == this.report_S_SettingFactory) {
			this.report_S_SettingFactory = new Report_S_SettingFactory( this );
		}
		return report_S_SettingFactory;
	}
	
	public Report_R_CreateTimeFactory report_R_CreateTimeFactory() throws Exception {
		if (null == this.report_R_CreateTimeFactory) {
			this.report_R_CreateTimeFactory = new Report_R_CreateTimeFactory( this );
		}
		return report_R_CreateTimeFactory;
	}
	
	public Report_P_ProfileFactory report_P_ProfileFactory() throws Exception {
		if (null == this.report_P_ProfileFactory) {
			this.report_P_ProfileFactory = new Report_P_ProfileFactory( this );
		}
		return report_P_ProfileFactory;
	}
	
	public Report_P_ProfileDetailFactory report_P_ProfileDetailFactory() throws Exception {
		if (null == this.report_P_ProfileDetailFactory) {
			this.report_P_ProfileDetailFactory = new Report_P_ProfileDetailFactory( this );
		}
		return report_P_ProfileDetailFactory;
	}
}
