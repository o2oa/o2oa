package com.x.pan.assemble.control.jaxrs.config;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.pan.assemble.control.Business;
import com.x.pan.core.entity.FileConfig3;

import java.util.ArrayList;
import java.util.List;

class ActionGetSystemConfig extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);

			/* 判断当前用户是否有权限访问 */
			if(!business.controlAble(effectivePerson)) {
				throw new ExceptionAccessDenied(effectivePerson.getDistinguishedName());
			}

			FileConfig3 config = emc.firstEqual(FileConfig3.class, FileConfig3.person_FIELDNAME, Business.SYSTEM_CONFIG);
			if(config==null){
				config = new FileConfig3();
				config.setPerson(Business.SYSTEM_CONFIG);
				config.setCapacity(0);
			}

			Wo wo = Wo.copier.copy(config);
			wo.setFileTypeExcludes(config.getProperties().getFileTypeExcludes());
			wo.setFileTypeIncludes(config.getProperties().getFileTypeIncludes());
			wo.setZoneAdminList(config.getProperties().getZoneAdminList());
			wo.setRecycleDays(config.getProperties().getRecycleDays());
			wo.setOfficeHome(config.getProperties().getOfficeHome());
			wo.setPortNumbers(config.getProperties().getPortNumbers());
			wo.setZoneReadPermissionDown(config.getProperties().getZoneReadPermissionDown());
			wo.setPanMenuList(config.getProperties().getPanMenuList());
			wo.setPreviewTools(config.getProperties().getPreviewTools());
			wo.setViewDownLoadUrl(config.getProperties().getViewDownLoadUrl());
			wo.setOfficeOnlineUrl(config.getOfficeOnlineUrlFromProperties());
			wo.setOpenOfficeEdit(config.getProperties().getOpenOfficeEdit());
			wo.setPeronFileEnable(business.getSystemConfig().getProperties().getPeronFileEnable());
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends FileConfig3 {

		private static final long serialVersionUID = -2207172562907788382L;
		static WrapCopier<FileConfig3, Wo> copier = WrapCopierFactory.wo(FileConfig3.class, Wo.class,
				JpaObject.singularAttributeField(FileConfig3.class, true, true), null);

		@FieldDescribe("只允许上传的文件后缀")
		private List<String> fileTypeIncludes = new ArrayList<>();

		@FieldDescribe("不允许上传的文件后缀")
		private List<String> fileTypeExcludes = new ArrayList<>();

		@FieldDescribe("回收站数据保留天数")
		private Integer recycleDays;

		@FieldDescribe("共享区目录查看权限是否下层(true表示共享区子目录可设置查看权限|false表示共享区所有目录文件查看权限根据共享区的权限来，默认为true)")
		private Boolean zoneReadPermissionDown;

		@FieldDescribe("共享区可创建权限列表(人员、组织或群组)")
		private List<String> zoneAdminList = new ArrayList<>();

		@FieldDescribe("附件预览工具")
		private String previewTools;

		@FieldDescribe("onlyoffice附件查看下载地址")
		private String viewDownLoadUrl;

		@FieldDescribe("libreoffice安装目录，如：/opt/libreoffice7.0")
		private String officeHome;

		@FieldDescribe("与libreoffice连接端口，多值逗号隔开，默认端口：20014,20015,20016")
		private String portNumbers;

		@FieldDescribe("网盘顶部菜单菜单，默认：个人文件、企业文件")
		private List<String> panMenuList;

		@FieldDescribe("是否展现个人文件")
		private Boolean peronFileEnable = true;

		@FieldDescribe("officeOnline服务器web访问地址")
		private String officeOnlineUrl;

		@FieldDescribe("打开Office时是否直接进入编辑状态(true表示进入编辑状态|false表示进入只读状态，默认为false)")
		private Boolean isOpenOfficeEdit;

		public Boolean getOpenOfficeEdit() {
			return isOpenOfficeEdit;
		}
		public void setOpenOfficeEdit(Boolean openOfficeEdit) {
			isOpenOfficeEdit = openOfficeEdit;
		}
		public List<String> getFileTypeIncludes() {
			return fileTypeIncludes;
		}

		public void setFileTypeIncludes(List<String> fileTypeIncludes) {
			this.fileTypeIncludes = fileTypeIncludes;
		}

		public List<String> getFileTypeExcludes() {
			return fileTypeExcludes;
		}

		public void setFileTypeExcludes(List<String> fileTypeExcludes) {
			this.fileTypeExcludes = fileTypeExcludes;
		}

		public Integer getRecycleDays() {
			return recycleDays;
		}

		public void setRecycleDays(Integer recycleDays) {
			this.recycleDays = recycleDays;
		}

		public List<String> getZoneAdminList() {
			return zoneAdminList;
		}

		public void setZoneAdminList(List<String> zoneAdminList) {
			this.zoneAdminList = zoneAdminList;
		}

		public String getOfficeHome() {
			return officeHome;
		}

		public void setOfficeHome(String officeHome) {
			this.officeHome = officeHome;
		}

		public String getPortNumbers() {
			return portNumbers;
		}

		public void setPortNumbers(String portNumbers) {
			this.portNumbers = portNumbers;
		}

		public Boolean getZoneReadPermissionDown() {
			return zoneReadPermissionDown;
		}

		public void setZoneReadPermissionDown(Boolean zoneReadPermissionDown) {
			this.zoneReadPermissionDown = zoneReadPermissionDown;
		}

		public List<String> getPanMenuList() {
			return panMenuList;
		}

		public void setPanMenuList(List<String> panMenuList) {
			this.panMenuList = panMenuList;
		}

		public String getPreviewTools() {
			return previewTools;
		}

		public void setPreviewTools(String previewTools) {
			this.previewTools = previewTools;
		}

		public String getViewDownLoadUrl() {
			return viewDownLoadUrl;
		}

		public void setViewDownLoadUrl(String viewDownLoadUrl) {
			this.viewDownLoadUrl = viewDownLoadUrl;
		}

		public String getOfficeOnlineUrl() {
			return officeOnlineUrl;
		}

		public void setOfficeOnlineUrl(String officeOnlineUrl) {
			this.officeOnlineUrl = officeOnlineUrl;
		}

		public Boolean getPeronFileEnable() {
			return peronFileEnable;
		}

		public void setPeronFileEnable(Boolean peronFileEnable) {
			this.peronFileEnable = peronFileEnable;
		}
	}
}
