package com.x.bbs.assemble.control;

import com.x.base.core.container.EntityManagerContainer;
import com.x.bbs.assemble.control.factory.BBSConfigSettingFactory;
import com.x.bbs.assemble.control.factory.BBSForumInfoFactory;
import com.x.bbs.assemble.control.factory.BBSOperationRecordFactory;
import com.x.bbs.assemble.control.factory.BBSPermissionInfoFactory;
import com.x.bbs.assemble.control.factory.BBSPermissionRoleFactory;
import com.x.bbs.assemble.control.factory.BBSReplyInfoFactory;
import com.x.bbs.assemble.control.factory.BBSRoleInfoFactory;
import com.x.bbs.assemble.control.factory.BBSSectionInfoFactory;
import com.x.bbs.assemble.control.factory.BBSSubjectAttachmentFactory;
import com.x.bbs.assemble.control.factory.BBSSubjectInfoFactory;
import com.x.bbs.assemble.control.factory.BBSSubjectVoteResultFactory;
import com.x.bbs.assemble.control.factory.BBSUserInfoFactory;
import com.x.bbs.assemble.control.factory.BBSUserRoleFactory;
import com.x.bbs.assemble.control.factory.BBSVoteOptionFactory;
import com.x.bbs.assemble.control.factory.BBSVoteRecordFactory;
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
	private BBSForumInfoFactory forumInfoFactory;
	private BBSSectionInfoFactory sectionInfoFactory;
	private BBSSubjectInfoFactory subjectInfoFactory;
	private BBSVoteOptionFactory voteOptionFactory;
	private BBSSubjectVoteResultFactory subjectVoteResultFactory;
	private BBSVoteRecordFactory voteRecordFactory;
	private BBSReplyInfoFactory replyInfoFactory;
	private BBSSubjectAttachmentFactory subjectAttachmentFactory;
	private BBSOperationRecordFactory operationRecordFactory;

	private BBSUserInfoFactory userInfoFactory;
	private BBSRoleInfoFactory roleInfoFactory;
	private BBSPermissionInfoFactory permissionInfoFactory;
	private BBSPermissionRoleFactory permissionRoleFactory;
	private BBSUserRoleFactory userRoleFactory;

	private BBSConfigSettingFactory configSettingFactory;

	public Organization organization() throws Exception {
		if (null == this.organization) {
			this.organization = new Organization(ThisApplication.context());
		}
		return organization;
	}

	public BBSVoteRecordFactory voteRecordFactory() throws Exception {
		if (null == this.voteRecordFactory) {
			this.voteRecordFactory = new BBSVoteRecordFactory(this);
		}
		return voteRecordFactory;
	}

	public BBSSubjectVoteResultFactory subjectVoteResultFactory() throws Exception {
		if (null == this.subjectVoteResultFactory) {
			this.subjectVoteResultFactory = new BBSSubjectVoteResultFactory(this);
		}
		return subjectVoteResultFactory;
	}

	public BBSVoteOptionFactory voteOptionFactory() throws Exception {
		if (null == this.voteOptionFactory) {
			this.voteOptionFactory = new BBSVoteOptionFactory(this);
		}
		return voteOptionFactory;
	}

	public BBSConfigSettingFactory configSettingFactory() throws Exception {
		if (null == this.configSettingFactory) {
			this.configSettingFactory = new BBSConfigSettingFactory(this);
		}
		return configSettingFactory;
	}

	public BBSSubjectAttachmentFactory subjectAttachmentFactory() throws Exception {
		if (null == this.subjectAttachmentFactory) {
			this.subjectAttachmentFactory = new BBSSubjectAttachmentFactory(this);
		}
		return subjectAttachmentFactory;
	}

	public BBSUserInfoFactory userInfoFactory() throws Exception {
		if (null == this.userInfoFactory) {
			this.userInfoFactory = new BBSUserInfoFactory(this);
		}
		return userInfoFactory;
	}

	public BBSRoleInfoFactory roleInfoFactory() throws Exception {
		if (null == this.roleInfoFactory) {
			this.roleInfoFactory = new BBSRoleInfoFactory(this);
		}
		return roleInfoFactory;
	}

	public BBSPermissionInfoFactory permissionInfoFactory() throws Exception {
		if (null == this.permissionInfoFactory) {
			this.permissionInfoFactory = new BBSPermissionInfoFactory(this);
		}
		return permissionInfoFactory;
	}

	public BBSPermissionRoleFactory permissionRoleFactory() throws Exception {
		if (null == this.permissionRoleFactory) {
			this.permissionRoleFactory = new BBSPermissionRoleFactory(this);
		}
		return permissionRoleFactory;
	}

	public BBSUserRoleFactory userRoleFactory() throws Exception {
		if (null == this.userRoleFactory) {
			this.userRoleFactory = new BBSUserRoleFactory(this);
		}
		return userRoleFactory;
	}

	public BBSForumInfoFactory forumInfoFactory() throws Exception {
		if (null == this.forumInfoFactory) {
			this.forumInfoFactory = new BBSForumInfoFactory(this);
		}
		return forumInfoFactory;
	}

	public BBSSectionInfoFactory sectionInfoFactory() throws Exception {
		if (null == this.sectionInfoFactory) {
			this.sectionInfoFactory = new BBSSectionInfoFactory(this);
		}
		return sectionInfoFactory;
	}

	public BBSSubjectInfoFactory subjectInfoFactory() throws Exception {
		if (null == this.subjectInfoFactory) {
			this.subjectInfoFactory = new BBSSubjectInfoFactory(this);
		}
		return subjectInfoFactory;
	}

	public BBSReplyInfoFactory replyInfoFactory() throws Exception {
		if (null == this.replyInfoFactory) {
			this.replyInfoFactory = new BBSReplyInfoFactory(this);
		}
		return replyInfoFactory;
	}

	public BBSOperationRecordFactory operationRecordFactory() throws Exception {
		if (null == this.operationRecordFactory) {
			this.operationRecordFactory = new BBSOperationRecordFactory(this);
		}
		return operationRecordFactory;
	}
}
