package com.x.report.core.entity;

import com.x.base.core.entity.AbstractPersistenceProperties;

public final class PersistenceProperties extends AbstractPersistenceProperties {
	
	public static class Report_S_Setting {
		public static final String table = "RPT_SETTING";
	}
	
	public static class Report_S_SettingLobValue {
		public static final String table = "RPT_SETTING_LOBVALUE";
	}

	public static class Report_I_Base {
		public static final String table = "RPT_INFOBASE";
	}
	
	public static class Report_I_Detail {
		public static final String table = "RPT_INFODETAIL";
	}

    public static class Report_I_WorkInfo {
        public static final String table = "RPT_I_WORKINFO";
    }

	public static class Report_I_WorkInfoDetail {
		public static final String table = "RPT_I_WORKINFO_DETAIL";
	}

	public static class Report_I_WorkTag {
		public static final String table = "RPT_I_WORKTAG";
	}

	public static class Report_I_WorkTagUnit {
		public static final String table = "RPT_I_WORKTAG_UNIT";
	}

    public static class Report_P_MeasureInfo {
        public static final String table = "RPT_P_MEASUREINFO";
    }

	public static class Report_C_WorkPlan {
		public static final String table = "RPT_C_WORKPLAN";
	}
	
	public static class Report_C_WorkPlanDetail {
		public static final String table = "RPT_C_WORKPLANDETAIL";
	}
	
	public static class Report_C_WorkPlanNext {
        public static final String table = "RPT_C_WORKPLANNEXT";
    }
	
	public static class Report_C_WorkPlanNextDetail {
		public static final String table = "RPT_C_WORKPLANNEXTDETAIL";
	}
	
	public static class Report_C_WorkProg {
		public static final String table = "RPT_C_WORKPROG";
	}

	public static class Report_C_WorkProgDetail {
		public static final String table = "RPT_C_WORKPROGDETAIL";
	}
	
	public static class Report_P_Profile {
		public static final String table = "RPT_P_PROFILE";
	}
	
	public static class Report_P_ProfileDetail {
		public static final String table = "RPT_P_PROFILEDETAIL";
	}
	
	public static class Report_R_CreateTime {
		public static final String table = "RPT_R_CREATETIME";
	}
	
	public static class Report_P_Permission {
		public static final String table = "RPT_P_PERMISSION";
	}
	
	public static class Report_R_View {
		public static final String table = "RPT_R_VIEW";
	}
	
	public static class Report_I_Ext_Content {
		public static final String table = "RPT_I_EXT_CONTENT";
	}
	
	public static class Report_I_Ext_ContentDetail {
		public static final String table = "RPT_I_EXT_CONTENTDETAIL";
	}
}