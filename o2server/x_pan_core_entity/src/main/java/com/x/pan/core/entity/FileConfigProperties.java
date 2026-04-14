package com.x.pan.core.entity;

import com.x.base.core.entity.JsonProperties;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.tools.ListTools;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 云文件系统扩展配置
 * @author sword
 */
public class FileConfigProperties extends JsonProperties {

	private static final long serialVersionUID = 7505391633543595721L;

	private static final String DEFAULT_LIBRE_OFFICE_PORT = "20014,20015,20016";

	@FieldDescribe("只允许上传的文件后缀")
	private List<String> fileTypeIncludes = new ArrayList<>();

	@FieldDescribe("不允许上传的文件后缀")
	private List<String> fileTypeExcludes = new ArrayList<>();

	@FieldDescribe("回收站数据保留天数")
	private Integer recycleDays;

	@FieldDescribe("共享区可创建权限列表(人员、组织或群组)")
	private List<String> zoneAdminList = new ArrayList<>();

	@FieldDescribe("附件预览工具")
	private String previewTools;

	@FieldDescribe("onlyoffice附件查看下载地址")
	private String viewDownLoadUrl;

	@FieldDescribe("libreoffice安装目录，如：/opt/libreoffice7.0")
	private String officeHome;

	@FieldDescribe("与libreoffice连接端口，多值逗号隔开，默认端口：20011,20012,20013")
	private String portNumbers = DEFAULT_LIBRE_OFFICE_PORT;

	@FieldDescribe("共享区查看权限是否下层(true表示共享区子目录可设置查看权限|false表示共享区所有目录文件查看权限根据共享区的权限来，默认为true)")
	private Boolean zoneReadPermissionDown = true;

	@FieldDescribe("网盘顶部菜单菜单，默认：个人文件、企业文件")
	private List<String> panMenuList;

	@FieldDescribe("是否展现个人文件")
	private Boolean peronFileEnable = true;

	@FieldDescribe("officeOnline服务器web访问地址")
	private String officeOnlineUrl;

	@FieldDescribe("打开Office时是否直接进入编辑状态(true表示进入编辑状态|false表示进入只读状态，默认为false)")
	private Boolean isOpenOfficeEdit = false;

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
		return recycleDays == null ? FileConfig3.DEFAULT_RECYCLE_DAYS : recycleDays;
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
		return StringUtils.isBlank(portNumbers) ? DEFAULT_LIBRE_OFFICE_PORT : portNumbers;
	}

	public void setPortNumbers(String portNumbers) {
		this.portNumbers = portNumbers;
	}

	public Boolean getZoneReadPermissionDown() {
		return zoneReadPermissionDown == null ? true : zoneReadPermissionDown;
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
		return peronFileEnable == null || peronFileEnable;
	}

	public void setPeronFileEnable(Boolean peronFileEnable) {
		this.peronFileEnable = peronFileEnable;
	}
}
