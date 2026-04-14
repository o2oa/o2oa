package com.x.pan.assemble.control.jaxrs.config;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.pan.assemble.control.Business;
import com.x.pan.assemble.control.util.OfficeManagerInstance;
import com.x.pan.core.entity.FileConfig3;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author sword
 */
public class ActionSaveSystemConfig extends BaseAction {
	private static Logger logger = LoggerFactory.getLogger( ActionSaveSystemConfig.class );

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		boolean flag = false;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);

			/* 判断当前用户是否有权限访问 */
			if(!business.controlAble(effectivePerson)) {
				throw new ExceptionAccessDenied(effectivePerson.getDistinguishedName());
			}

			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			FileConfig3 fileConfig = emc.firstEqual(FileConfig3.class, FileConfig3.person_FIELDNAME, Business.SYSTEM_CONFIG);
			emc.beginTransaction(FileConfig3.class);
			if(fileConfig!=null){
				if(StringUtils.isNotBlank(wi.getOfficeHome()) && !wi.getOfficeHome().equals(fileConfig.getProperties().getOfficeHome())){
					flag = true;
				}
				this.setProperties(wi, fileConfig);
				emc.check(fileConfig, CheckPersistType.all);
			}else{
				fileConfig = new FileConfig3();
				if(StringUtils.isNotBlank(wi.getOfficeHome())){
					flag = true;
				}
				this.setProperties(wi, fileConfig);
				emc.persist(fileConfig, CheckPersistType.all);
			}
			emc.commit();
			CacheManager.notify(FileConfig3.class);
			Wo wo = new Wo();
			wo.setId(fileConfig.getId());
			result.setData(wo);
		}
		if(flag) {
			OfficeManagerInstance.startInit();
		}
		return result;
	}

	private void setProperties(Wi wi, FileConfig3 fileConfig){
		if(wi.getCapacity()==null || wi.getCapacity() < 1){
			fileConfig.setCapacity(0);
		}else{
			fileConfig.setCapacity(wi.getCapacity());
		}
		if(wi.getRecycleDays()==null || wi.getRecycleDays() < 1){
			fileConfig.getProperties().setRecycleDays(FileConfig3.DEFAULT_RECYCLE_DAYS);
		}else{
			fileConfig.getProperties().setRecycleDays(wi.getRecycleDays());
		}
		fileConfig.setPerson(Business.SYSTEM_CONFIG);
		fileConfig.getProperties().setFileTypeIncludes(wi.getFileTypeIncludes());
		fileConfig.getProperties().setFileTypeExcludes(wi.getFileTypeExcludes());
		fileConfig.getProperties().setZoneAdminList(wi.getZoneAdminList());
		fileConfig.getProperties().setOfficeHome(wi.getOfficeHome());
		if(wi.getZoneReadPermissionDown()!=null){
			fileConfig.getProperties().setZoneReadPermissionDown(wi.getZoneReadPermissionDown());
		}else{
			fileConfig.getProperties().setZoneReadPermissionDown(false);
		}
		if(StringUtils.isNotBlank(wi.getPortNumbers())) {
			fileConfig.getProperties().setPortNumbers(wi.getPortNumbers());
		}
		fileConfig.getProperties().setPanMenuList(wi.getPanMenuList());
		fileConfig.getProperties().setPreviewTools(wi.getPreviewTools());
		fileConfig.getProperties().setViewDownLoadUrl(wi.getViewDownLoadUrl());
		fileConfig.getProperties().setOfficeOnlineUrl(wi.getOfficeOnlineUrl());
		fileConfig.getProperties().setPeronFileEnable(wi.getPeronFileEnable());
		if(wi.getOpenOfficeEdit()!=null){
			fileConfig.getProperties().setOpenOfficeEdit(wi.getOpenOfficeEdit());
		}else{
			fileConfig.getProperties().setOpenOfficeEdit(false);
		}
	}

	public static class Wi extends GsonPropertyObject {

		@FieldDescribe("容量(单位M)，0表示无限大.")
		private Integer capacity;

		@FieldDescribe("回收站数据保留天数")
		private Integer recycleDays;

		@FieldDescribe("共享区目录查看权限是否下层(true表示共享区子目录可设置查看权限|false表示共享区所有目录文件查看权限根据共享区的权限来，默认为false)")
		private Boolean zoneReadPermissionDown;

		@FieldDescribe("共享区可创建权限列表(人员、组织或群组)")
		private List<String> zoneAdminList;

		@FieldDescribe("只允许上传的文件后缀")
		private List<String> fileTypeIncludes;

		@FieldDescribe("不允许上传的文件后缀")
		private List<String> fileTypeExcludes;

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
		private Boolean peronFileEnable;

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
			return fileTypeIncludes == null ? new ArrayList<>() : fileTypeIncludes;
		}

		public void setFileTypeIncludes(List<String> fileTypeIncludes) {
			this.fileTypeIncludes = fileTypeIncludes;
		}

		public List<String> getFileTypeExcludes() {
			return fileTypeExcludes == null ? new ArrayList<>() : fileTypeExcludes;
		}

		public void setFileTypeExcludes(List<String> fileTypeExcludes) {
			this.fileTypeExcludes = fileTypeExcludes;
		}

		public Integer getCapacity() {
			return capacity;
		}

		public void setCapacity(Integer capacity) {
			this.capacity = capacity;
		}

		public Integer getRecycleDays() {
			return recycleDays;
		}

		public void setRecycleDays(Integer recycleDays) {
			this.recycleDays = recycleDays;
		}

		public List<String> getZoneAdminList() {
			return zoneAdminList == null ? new ArrayList<>() : zoneAdminList;
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
			return ListTools.isEmpty(panMenuList) ? FileConfig3.DEFAULT_MENU_LIST : panMenuList;
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

	public static class Wo extends WoId {

	}

}
