package com.x.bbs.assemble.control.jaxrs.permissioninfo;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.SortTools;
import com.x.bbs.assemble.control.Business;
import com.x.bbs.assemble.control.service.bean.RoleAndPermission;
import com.x.bbs.entity.BBSForumInfo;
import com.x.bbs.entity.BBSSectionInfo;
import com.x.bbs.entity.enums.BbsRoleEnum;

/**
 * 查询用户的权限
 * @author sword
 */
public class ActionGetUserPermission extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionGetUserPermission.class);

	protected ActionResult<Wo> execute(EffectivePerson effectivePerson) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		result.setData(wo);
		if(effectivePerson.isAnonymous()){
			return result;
		}
		Business business = new Business(null);
		if(business.controlAble(effectivePerson)) {
			wo.setBbsAdmin(true);
			wo.setBbsForumAdmin(true);
			wo.setBbsSectionAdmin(true);
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				List<BBSForumInfo> forumInfoList = emc.fetchAll(BBSForumInfo.class, ListTools.toList(JpaObject.id_FIELDNAME));
				wo.setForumList(ListTools.extractField(forumInfoList, JpaObject.id_FIELDNAME, String.class, true, true));
				List<BBSSectionInfo> sectionInfoList = emc.fetchAll(BBSSectionInfo.class,
						ListTools.toList(JpaObject.id_FIELDNAME, BBSSectionInfo.orderNumber_FIELDNAME, JpaObject.createTime_FIELDNAME));
				SortTools.asc(sectionInfoList, BBSSectionInfo.orderNumber_FIELDNAME, JpaObject.createTime_FIELDNAME);
				wo.setSectionList(ListTools.extractField(sectionInfoList, JpaObject.id_FIELDNAME, String.class, true, true));
			}
		}else{
			RoleAndPermission roleAndPermission = this.UserPermissionService.getUserRoleAndPermission(effectivePerson.getDistinguishedName());
			if(ListTools.isNotEmpty(roleAndPermission.getRoleInfoList())){
				roleAndPermission.getRoleInfoList().forEach(r -> {
					if(r.startsWith(BbsRoleEnum.FORUM_SUPER_MANAGER.getValue())){
						wo.getForumList().add(StringUtils.substringAfter(r, BbsRoleEnum.FORUM_SUPER_MANAGER.getValue()+"_"));
					}
					if(r.startsWith(BbsRoleEnum.SECTION_MANAGER.getValue())){
						wo.getSectionList().add(StringUtils.substringAfter(r, BbsRoleEnum.SECTION_MANAGER.getValue()+"_"));
					}
				});
				if(!wo.getForumList().isEmpty()){
					wo.setBbsForumAdmin(true);
					try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
						business = new Business(emc);
						wo.setSectionList(ListTools.add(business.sectionInfoFactory().listByForumIds(wo.getForumList()),
								true, true, wo.getSectionList()));
					}
				}
				if(!wo.getSectionList().isEmpty()){
					wo.setBbsSectionAdmin(true);
					try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
						List<BBSSectionInfo> sectionInfoList = emc.fetch(wo.getSectionList(), BBSSectionInfo.class,
								ListTools.toList(JpaObject.id_FIELDNAME, BBSSectionInfo.orderNumber_FIELDNAME, JpaObject.createTime_FIELDNAME));
						SortTools.asc(sectionInfoList, BBSSectionInfo.orderNumber_FIELDNAME, JpaObject.createTime_FIELDNAME);
						wo.setSectionList(ListTools.extractField(sectionInfoList, JpaObject.id_FIELDNAME, String.class, true, true));
					}
				}
			}
		}
		result.setData(wo);
		return result;
	}

	public static class Wo{

		@FieldDescribe( "是否是BBS管理员." )
		private Boolean bbsAdmin = false;

		@FieldDescribe( "是否是分区管理员." )
		private Boolean bbsForumAdmin = false;

		@FieldDescribe( "是否是板块管理员." )
		private Boolean bbsSectionAdmin = false;

		@FieldDescribe( "可管理的分区." )
		private List<String> forumList = new ArrayList();

		@FieldDescribe( "可管理的板块." )
		private List<String> sectionList = new ArrayList();

		public Boolean getBbsAdmin() {
			return bbsAdmin;
		}

		public void setBbsAdmin(Boolean bbsAdmin) {
			this.bbsAdmin = bbsAdmin;
		}

		public Boolean getBbsForumAdmin() {
			return bbsForumAdmin;
		}

		public void setBbsForumAdmin(Boolean bbsForumAdmin) {
			this.bbsForumAdmin = bbsForumAdmin;
		}

		public Boolean getBbsSectionAdmin() {
			return bbsSectionAdmin;
		}

		public void setBbsSectionAdmin(Boolean bbsSectionAdmin) {
			this.bbsSectionAdmin = bbsSectionAdmin;
		}

		public List<String> getForumList() {
			return forumList;
		}

		public void setForumList(List<String> forumList) {
			this.forumList = forumList;
		}

		public List<String> getSectionList() {
			return sectionList;
		}

		public void setSectionList(List<String> sectionList) {
			this.sectionList = sectionList;
		}
	}
}
