package com.x.okr.assemble.control;

import com.x.base.core.container.EntityManagerContainer;
import com.x.okr.assemble.control.factory.OkrAttachmentFileInfoFactory;
import com.x.okr.assemble.control.factory.OkrCenterWorkInfoFactory;
import com.x.okr.assemble.control.factory.OkrConfigSecretaryFactory;
import com.x.okr.assemble.control.factory.OkrConfigSystemFactory;
import com.x.okr.assemble.control.factory.OkrConfigWorkLevelFactory;
import com.x.okr.assemble.control.factory.OkrConfigWorkTypeFactory;
import com.x.okr.assemble.control.factory.OkrErrorSystemIdentityInfoFactory;
import com.x.okr.assemble.control.factory.OkrStatisticReportContentFactory;
import com.x.okr.assemble.control.factory.OkrStatisticReportStatusFactory;
import com.x.okr.assemble.control.factory.OkrTaskFactory;
import com.x.okr.assemble.control.factory.OkrTaskHandledFactory;
import com.x.okr.assemble.control.factory.OkrUserInfoFactory;
import com.x.okr.assemble.control.factory.OkrWorkAppraiseInfoFactory;
import com.x.okr.assemble.control.factory.OkrWorkAuthorizeRecordFactory;
import com.x.okr.assemble.control.factory.OkrWorkBaseInfoFactory;
import com.x.okr.assemble.control.factory.OkrWorkChatFactory;
import com.x.okr.assemble.control.factory.OkrWorkDetailInfoFactory;
import com.x.okr.assemble.control.factory.OkrWorkDynamicsFactory;
import com.x.okr.assemble.control.factory.OkrWorkPersonFactory;
import com.x.okr.assemble.control.factory.OkrWorkPersonSearchFactory;
import com.x.okr.assemble.control.factory.OkrWorkReportBaseInfoFactory;
import com.x.okr.assemble.control.factory.OkrWorkReportDetailInfoFactory;
import com.x.okr.assemble.control.factory.OkrWorkReportPersonLinkFactory;
import com.x.okr.assemble.control.factory.OkrWorkReportProcessLogFactory;
import com.x.organization.core.express.Organization;

public class Business {

	private EntityManagerContainer emc;
	
	public Business(EntityManagerContainer emc) throws Exception {
		this.emc = emc;
	}
	public EntityManagerContainer entityManagerContainer() {
		return this.emc;
	}
	//人员组织业务处理类
	private Organization organization;
	
	private OkrStatisticReportContentFactory okrStatisticReportContentFactory;
	private OkrStatisticReportStatusFactory okrStatisticReportStatusFactory;
	
	private OkrAttachmentFileInfoFactory okrAttachmentFileInfoFactory;
	private OkrCenterWorkInfoFactory okrCenterWorkInfoFactory;
	private OkrConfigSecretaryFactory okrConfigSecretaryFactory;
	private OkrConfigSystemFactory okrConfigSystemFactory;
	private OkrConfigWorkLevelFactory okrConfigWorkLevelFactory;
	private OkrConfigWorkTypeFactory okrConfigWorkTypeFactory;
	private OkrTaskFactory okrTaskFactory;
	private OkrTaskHandledFactory okrTaskHandledFactory;
	private OkrWorkAuthorizeRecordFactory okrWorkAuthorizeRecordFactory;
	private OkrWorkBaseInfoFactory okrWorkBaseInfoFactory;
	private OkrWorkAppraiseInfoFactory okrWorkAppraiseInfoFactory;
	private OkrWorkDetailInfoFactory okrWorkDetailInfoFactory;
	private OkrWorkDynamicsFactory okrWorkDynamicsFactory;
	private OkrWorkPersonFactory okrWorkPersonFactory;
	private OkrWorkPersonSearchFactory okrWorkPersonSearchFactory;
	private OkrWorkReportBaseInfoFactory okrWorkReportBaseInfoFactory;
	private OkrWorkReportDetailInfoFactory okrWorkReportDetailInfoFactory;
	private OkrWorkReportPersonLinkFactory okrWorkReportPersonLinkFactory;
	private OkrWorkReportProcessLogFactory okrWorkReportProcessLogFactory;
	private OkrErrorSystemIdentityInfoFactory okrErrorSystemIdentityInfoFactory;
	private OkrUserInfoFactory okrUserInfoFactory;
	
	
	
	private OkrWorkChatFactory okrWorkChatFactory;
	
	public Organization organization() throws Exception {
		if (null == this.organization) {
			this.organization = new Organization(ThisApplication.context());
		}
		return organization;
	}

	public OkrWorkAppraiseInfoFactory okrWorkAppraiseInfoFactory() throws Exception {
		if (null == this.okrWorkAppraiseInfoFactory) {
			this.okrWorkAppraiseInfoFactory = new OkrWorkAppraiseInfoFactory( this );
		}
		return okrWorkAppraiseInfoFactory;
	}

	public OkrErrorSystemIdentityInfoFactory okrErrorSystemIdentityInfoFactory() throws Exception {
		if (null == this.okrErrorSystemIdentityInfoFactory) {
			this.okrErrorSystemIdentityInfoFactory = new OkrErrorSystemIdentityInfoFactory( this );
		}
		return okrErrorSystemIdentityInfoFactory;
	}
	public OkrStatisticReportStatusFactory okrStatisticReportStatusFactory() throws Exception {
		if (null == this.okrStatisticReportStatusFactory) {
			this.okrStatisticReportStatusFactory = new OkrStatisticReportStatusFactory( this );
		}
		return okrStatisticReportStatusFactory;
	}
	public OkrUserInfoFactory okrUserInfoFactory() throws Exception {
		if (null == this.okrUserInfoFactory) {
			this.okrUserInfoFactory = new OkrUserInfoFactory( this );
		}
		return okrUserInfoFactory;
	} 
	public OkrStatisticReportContentFactory okrStatisticReportContentFactory() throws Exception {
		if (null == this.okrStatisticReportContentFactory) {
			this.okrStatisticReportContentFactory = new OkrStatisticReportContentFactory( this );
		}
		return okrStatisticReportContentFactory;
	} 
	public OkrWorkChatFactory okrWorkChatFactory() throws Exception {
		if (null == this.okrWorkChatFactory) {
			this.okrWorkChatFactory = new OkrWorkChatFactory( this );
		}
		return okrWorkChatFactory;
	} 
	public OkrAttachmentFileInfoFactory okrAttachmentFileInfoFactory() throws Exception {
		if (null == this.okrAttachmentFileInfoFactory) {
			this.okrAttachmentFileInfoFactory = new OkrAttachmentFileInfoFactory( this );
		}
		return okrAttachmentFileInfoFactory;
	} 
	public OkrCenterWorkInfoFactory okrCenterWorkInfoFactory() throws Exception {
		if (null == this.okrCenterWorkInfoFactory) {
			this.okrCenterWorkInfoFactory = new OkrCenterWorkInfoFactory( this );
		}
		return okrCenterWorkInfoFactory;
	}
	public OkrConfigWorkLevelFactory okrConfigWorkLevelFactory() throws Exception {
		if (null == this.okrConfigWorkLevelFactory) {
			this.okrConfigWorkLevelFactory = new OkrConfigWorkLevelFactory( this );
		}
		return okrConfigWorkLevelFactory;
	}
	public OkrConfigSecretaryFactory okrConfigSecretaryFactory() throws Exception {
		if (null == this.okrConfigSecretaryFactory) {
			this.okrConfigSecretaryFactory = new OkrConfigSecretaryFactory( this );
		}
		return okrConfigSecretaryFactory;
	}
	public OkrConfigSystemFactory okrConfigSystemFactory() throws Exception {
		if (null == this.okrConfigSystemFactory) {
			this.okrConfigSystemFactory = new OkrConfigSystemFactory( this );
		}
		return okrConfigSystemFactory;
	}
	public OkrConfigWorkTypeFactory okrConfigWorkTypeFactory() throws Exception {
		if (null == this.okrConfigWorkTypeFactory) {
			this.okrConfigWorkTypeFactory = new OkrConfigWorkTypeFactory( this );
		}
		return okrConfigWorkTypeFactory;
	}
	public OkrTaskFactory okrTaskFactory() throws Exception {
		if (null == this.okrTaskFactory) {
			this.okrTaskFactory = new OkrTaskFactory( this );
		}
		return okrTaskFactory;
	}
	public OkrTaskHandledFactory okrTaskHandledFactory() throws Exception {
		if (null == this.okrTaskHandledFactory) {
			this.okrTaskHandledFactory = new OkrTaskHandledFactory( this );
		}
		return okrTaskHandledFactory;
	}
	public OkrWorkAuthorizeRecordFactory okrWorkAuthorizeRecordFactory() throws Exception {
		if (null == this.okrWorkAuthorizeRecordFactory) {
			this.okrWorkAuthorizeRecordFactory = new OkrWorkAuthorizeRecordFactory( this );
		}
		return okrWorkAuthorizeRecordFactory;
	}
	public OkrWorkBaseInfoFactory okrWorkBaseInfoFactory() throws Exception {
		if (null == this.okrWorkBaseInfoFactory) {
			this.okrWorkBaseInfoFactory = new OkrWorkBaseInfoFactory( this );
		}
		return okrWorkBaseInfoFactory;
	}
	public OkrWorkDetailInfoFactory okrWorkDetailInfoFactory() throws Exception {
		if (null == this.okrWorkDetailInfoFactory) {
			this.okrWorkDetailInfoFactory = new OkrWorkDetailInfoFactory( this );
		}
		return okrWorkDetailInfoFactory;
	}
	public OkrWorkDynamicsFactory okrWorkDynamicsFactory() throws Exception {
		if (null == this.okrWorkDynamicsFactory) {
			this.okrWorkDynamicsFactory = new OkrWorkDynamicsFactory( this );
		}
		return okrWorkDynamicsFactory;
	}
	public OkrWorkPersonFactory okrWorkPersonFactory() throws Exception {
		if (null == this.okrWorkPersonFactory) {
			this.okrWorkPersonFactory = new OkrWorkPersonFactory( this );
		}
		return okrWorkPersonFactory;
	}
	public OkrWorkPersonSearchFactory okrWorkPersonSearchFactory() throws Exception {
		if (null == this.okrWorkPersonSearchFactory) {
			this.okrWorkPersonSearchFactory = new OkrWorkPersonSearchFactory( this );
		}
		return okrWorkPersonSearchFactory;
	}
	public OkrWorkReportBaseInfoFactory okrWorkReportBaseInfoFactory() throws Exception {
		if (null == this.okrWorkReportBaseInfoFactory) {
			this.okrWorkReportBaseInfoFactory = new OkrWorkReportBaseInfoFactory( this );
		}
		return okrWorkReportBaseInfoFactory;
	}
	public OkrWorkReportDetailInfoFactory okrWorkReportDetailInfoFactory() throws Exception {
		if (null == this.okrWorkReportDetailInfoFactory) {
			this.okrWorkReportDetailInfoFactory = new OkrWorkReportDetailInfoFactory( this );
		}
		return okrWorkReportDetailInfoFactory;
	} 
	public OkrWorkReportPersonLinkFactory okrWorkReportPersonLinkFactory() throws Exception {
		if (null == this.okrWorkReportPersonLinkFactory) {
			this.okrWorkReportPersonLinkFactory = new OkrWorkReportPersonLinkFactory( this );
		}
		return okrWorkReportPersonLinkFactory;
	} 
	public OkrWorkReportProcessLogFactory okrWorkReportProcessLogFactory() throws Exception {
		if (null == this.okrWorkReportProcessLogFactory) {
			this.okrWorkReportProcessLogFactory = new OkrWorkReportProcessLogFactory( this );
		}
		return okrWorkReportProcessLogFactory;
	}
}
