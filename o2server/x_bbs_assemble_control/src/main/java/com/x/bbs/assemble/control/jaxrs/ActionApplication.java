package com.x.bbs.assemble.control.jaxrs;

import java.util.Set;

import javax.ws.rs.ApplicationPath;

import com.x.base.core.project.jaxrs.AbstractActionApplication;
import com.x.bbs.assemble.control.jaxrs.attachment.AttachmentAction;
import com.x.bbs.assemble.control.jaxrs.attachment.PictureAction;
import com.x.bbs.assemble.control.jaxrs.configsetting.BBSConfigSettingAction;
import com.x.bbs.assemble.control.jaxrs.configsetting.BBSConfigSettingAnonymousAction;
import com.x.bbs.assemble.control.jaxrs.foruminfo.ForumInfoAction;
import com.x.bbs.assemble.control.jaxrs.foruminfo.ForumInfoManagerUserAction;
import com.x.bbs.assemble.control.jaxrs.login.LoginAction;
import com.x.bbs.assemble.control.jaxrs.login.LogoutAction;
import com.x.bbs.assemble.control.jaxrs.login.MobileIndexAction;
import com.x.bbs.assemble.control.jaxrs.permissioninfo.PermissionInfoAction;
import com.x.bbs.assemble.control.jaxrs.permissioninfo.PermissionInfoAdminAction;
import com.x.bbs.assemble.control.jaxrs.replyinfo.ReplyInfoAction;
import com.x.bbs.assemble.control.jaxrs.replyinfo.ReplyInfoManagerUserAction;
import com.x.bbs.assemble.control.jaxrs.roleinfo.RoleInfoAction;
import com.x.bbs.assemble.control.jaxrs.sectioninfo.SectionInfoAction;
import com.x.bbs.assemble.control.jaxrs.sectioninfo.SectionInfoManagerUserAction;
import com.x.bbs.assemble.control.jaxrs.shutup.ShutupAction;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.SubjectAttachmentAction;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.SubjectInfoAction;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.SubjectInfoManagerUserAction;
import com.x.bbs.assemble.control.jaxrs.userinfo.UserInfoAction;
import com.x.bbs.assemble.control.jaxrs.uuid.UUIDAction;

@ApplicationPath("jaxrs")
public class ActionApplication extends AbstractActionApplication {

	public Set<Class<?>> getClasses() {
		this.classes.add(UUIDAction.class);
		this.classes.add(LoginAction.class);
		this.classes.add(LogoutAction.class);
		this.classes.add(ForumInfoAction.class);
		this.classes.add(ForumInfoManagerUserAction.class);
		this.classes.add(SectionInfoAction.class);
		this.classes.add(SectionInfoManagerUserAction.class);
		this.classes.add(SubjectInfoAction.class);
		this.classes.add(SubjectInfoManagerUserAction.class);
		this.classes.add(SubjectAttachmentAction.class);
		this.classes.add(ReplyInfoAction.class);
		this.classes.add(ReplyInfoManagerUserAction.class);
		this.classes.add(PermissionInfoAction.class);
		this.classes.add(PermissionInfoAdminAction.class);
		this.classes.add(RoleInfoAction.class);
		this.classes.add(UserInfoAction.class);
		this.classes.add(MobileIndexAction.class);
		this.classes.add(BBSConfigSettingAction.class);
		this.classes.add(BBSConfigSettingAnonymousAction.class);
		this.classes.add(AttachmentAction.class);
		this.classes.add(PictureAction.class);
		this.classes.add(ShutupAction.class);
		return this.classes;
	}

}
