package com.x.cms.assemble.control.jaxrs.document;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.CategoryInfo;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.FileInfo;
import com.x.cms.core.entity.Log;
import com.x.cms.core.entity.content.Data;
import com.x.cms.core.entity.element.Form;

import net.sf.ehcache.Element;

public class ActionQueryGetDocument extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionQueryGetDocument.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, String id, EffectivePerson effectivePerson)
			throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		WoDocument wrapOutDocument = null;
		CategoryInfo categoryInfo = null;
		Document document = null;
		List<FileInfo> attachmentList = null;
		Boolean isAppAdmin = false;
		Boolean isCategoryAdmin = false;
		Boolean isManager = false;
		Boolean isEditor = false;
		Boolean isCreator = false;
		Boolean check = true;
		String personName = effectivePerson.getDistinguishedName();
		
		if ( StringUtils.isEmpty(id)) {
			check = false;
			Exception exception = new ExceptionDocumentIdEmpty();
			result.error(exception);
		}

		if (check) {
			try {
				if (effectivePerson.isManager()) {
					isManager = true;
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionDocumentInfoProcess(e,
						"判断用户是否是系统管理员时发生异常！user:" + personName);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}

		String cacheKey = ApplicationCache.concreteCacheKey( id, "get", isManager );
		Element element = cache.get(cacheKey);
		
		if ((null != element) && (null != element.getObjectValue())) {
			wo = (Wo) element.getObjectValue();
			wrapOutDocument = wo.getDocument();
			result.setData(wo);
		} else {
			if (check) {
				try {
					document = documentQueryService.get(id);
					if (document == null) {
						check = false;
						Exception exception = new ExceptionDocumentNotExists(id);
						result.error(exception);
					} else {
						try {
							wrapOutDocument = WoDocument.copier.copy(document);
						} catch (Exception e) {
							check = false;
							Exception exception = new ExceptionDocumentInfoProcess(e, "将查询出来的文档信息对象转换为可输出的数据信息时发生异常。");
							result.error(exception);
							logger.error(e, effectivePerson, request, null);
						}
					}
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionDocumentInfoProcess(e,
							"文档信息获取操作时发生异常。Id:" + id + ", Name:" + personName);
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}
			if (check) {
				if (wrapOutDocument != null) {
					try {
						categoryInfo = categoryInfoServiceAdv.get(document.getCategoryId());
						wrapOutDocument.setForm(categoryInfo.getFormId());
						wrapOutDocument.setFormName(categoryInfo.getFormName());
						wrapOutDocument.setReadFormId(categoryInfo.getReadFormId());
						wrapOutDocument.setReadFormName(categoryInfo.getReadFormName());
						wrapOutDocument.setCategoryName(categoryInfo.getCategoryName());
						wrapOutDocument.setCategoryAlias(categoryInfo.getCategoryAlias());

						if (StringUtils.isNotEmpty( wrapOutDocument.getCreatorPerson() )) {
							wrapOutDocument.setCreatorPersonShort(wrapOutDocument.getCreatorPerson().split("@")[0]);
						}
						if (StringUtils.isNotEmpty( wrapOutDocument.getCreatorUnitName() )) {
							wrapOutDocument.setCreatorUnitNameShort(wrapOutDocument.getCreatorUnitName().split("@")[0]);
						}
						if (StringUtils.isNotEmpty( wrapOutDocument.getCreatorTopUnitName() )) {
							wrapOutDocument.setCreatorTopUnitNameShort(wrapOutDocument.getCreatorTopUnitName().split("@")[0]);
						}
						wo.setDocument(wrapOutDocument);
					} catch (Exception e) {
						check = false;
						Exception exception = new ExceptionDocumentInfoProcess(e,
								"根据ID查询分类信息对象时发生异常。ID:" + document.getCategoryId());
						result.error(exception);
						logger.error(e, effectivePerson, request, null);
					}
				}
			}
			if (check) {
				if (wrapOutDocument != null) {
					try {
						wo.setData(documentQueryService.getDocumentData(document));
					} catch (Exception e) {
						check = false;
						Exception exception = new ExceptionDocumentInfoProcess(e,
								"系统获取文档数据内容信息时发生异常。Id:" + document.getCategoryId());
						result.error(exception);
						logger.error(e, effectivePerson, request, null);
					}
				}
			}
			if (check) {
				try {
					attachmentList = fileInfoServiceAdv.getAttachmentList(document.getId());
					if (attachmentList != null && !attachmentList.isEmpty()) {
						wo.setAttachmentList(WoFileInfo.copier.copy(attachmentList));
					}
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionDocumentInfoProcess(e,
							"系统获取文档附件内容列表时发生异常。Id:" + document.getCategoryId());
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}
			if (check) {
				if (wo.getDocument() != null && wo.getDocument().getCreatorPerson() != null
						&& wo.getDocument().getCreatorPerson().equals(personName)) {
					isCreator = true;
					wo.setIsCreator(isCreator);
				}
			}
			
			if (check) {
				wo.setDocumentLogList(new ArrayList<WoLog>());
				cache.put(new Element(cacheKey, wo));
			}
		}
		
		//不管是从缓存还是数据库查出来，都要重新进行权限判断
		if (check) {
			try {
				if ( categoryInfoServiceAdv.isCategoryInfoManager( wrapOutDocument.getCategoryId(), personName ) ) {
					isCategoryAdmin = true;
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionDocumentInfoProcess(e,
						"判断用户是否是分类管理员时发生异常！user:" + personName);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}

		if (check) {
			try {
				if ( appInfoServiceAdv.isAppInfoManager(wrapOutDocument.getAppId(), personName)) {
					isAppAdmin = true;
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionDocumentInfoProcess(e, "判断用户是否是栏目管理员时发生异常！user:" + personName);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}

		if (check) {
			if (isManager || isAppAdmin || isCategoryAdmin || isCreator) {
				isEditor = true;
			} else {
				AppInfo appInfo =  appInfoServiceAdv.get( wrapOutDocument.getAppId() );
				CategoryInfo category = categoryInfoServiceAdv.get( wrapOutDocument.getCategoryId() );
				// 判断当前登录者是不是该文档的可编辑者
				try {
					List<String> unitNames = userManagerService.listUnitNamesWithPerson( personName );
					List<String> groupNames = userManagerService.listGroupNamesByPerson( personName );
					
					if( ListTools.isEmpty(wrapOutDocument.getAuthorPersonList()) ) {
						wrapOutDocument.setAuthorPersonList( composeAuthorPersonsWithAppAndCagetory( appInfo, category ) );
					}
					if( ListTools.isEmpty(wrapOutDocument.getAuthorUnitList()) ) {
						wrapOutDocument.setAuthorUnitList(composeAuthorUnitsWithAppAndCagetory( appInfo, category ));
					}
					if( ListTools.isEmpty(wrapOutDocument.getAuthorGroupList()) ) {
						wrapOutDocument.setAuthorGroupList(composeAuthorGroupsWithAppAndCagetory( appInfo, category ));
					}
					
					if ( wrapOutDocument.getAuthorPersonList().contains( personName )) {
						isEditor = true;
					}
					
					unitNames.retainAll( wrapOutDocument.getAuthorUnitList() );
					if( ListTools.isNotEmpty( unitNames )) {
						isEditor = true;
					}
					
					groupNames.retainAll( wrapOutDocument.getAuthorGroupList() );
					if( ListTools.isNotEmpty( groupNames )) {
						isEditor = true;
					}
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionDocumentInfoProcess(e, "判断用户是否可编辑文档时发生异常！user:" + personName);
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}
		}

		wo.setIsManager(isManager);
		wo.setIsAppAdmin(isAppAdmin);
		wo.setIsCategoryAdmin(isCategoryAdmin);
		wo.setIsEditor(isEditor);

		result.setData(wo);
		return result;
	}

	private List<String> composeAuthorUnitsWithAppAndCagetory(AppInfo appInfo, CategoryInfo category) {
		List<String> authorUnits = new ArrayList<>();
		if( ListTools.isNotEmpty( appInfo.getManageableUnitList() )) {
			for( String name : appInfo.getManageableUnitList() ) {
				if( !authorUnits.contains( name )) {
					authorUnits.add( name );
				}
			}
		}
		if( ListTools.isNotEmpty( appInfo.getPublishableUnitList() )) {
			for( String name : appInfo.getPublishableUnitList() ) {
				if( !authorUnits.contains( name )) {
					authorUnits.add( name );
				}
			}
		}
		if( ListTools.isNotEmpty( category.getManageableUnitList() )) {
			for( String name : category.getManageableUnitList() ) {
				if( !authorUnits.contains( name )) {
					authorUnits.add( name );
				}
			}
		}
		if( ListTools.isNotEmpty( category.getPublishableUnitList() )) {
			for( String name : category.getPublishableUnitList() ) {
				if( !authorUnits.contains( name )) {
					authorUnits.add( name );
				}
			}
		}
		return authorUnits;
	}

	private List<String> composeAuthorGroupsWithAppAndCagetory(AppInfo appInfo, CategoryInfo category) {
		List<String> authorGroups = new ArrayList<>();
		if( ListTools.isNotEmpty( appInfo.getManageableGroupList() )) {
			for( String name : appInfo.getManageableGroupList() ) {
				if( !authorGroups.contains( name )) {
					authorGroups.add( name );
				}
			}
		}
		if( ListTools.isNotEmpty( appInfo.getPublishableGroupList() )) {
			for( String name : appInfo.getPublishableGroupList() ) {
				if( !authorGroups.contains( name )) {
					authorGroups.add( name );
				}
			}
		}
		if( ListTools.isNotEmpty( category.getManageableGroupList() )) {
			for( String name : category.getManageableGroupList() ) {
				if( !authorGroups.contains( name )) {
					authorGroups.add( name );
				}
			}
		}
		if( ListTools.isNotEmpty( category.getPublishableGroupList() )) {
			for( String name : category.getPublishableGroupList() ) {
				if( !authorGroups.contains( name )) {
					authorGroups.add( name );
				}
			}
		}
		return authorGroups;
	}

	private List<String> composeAuthorPersonsWithAppAndCagetory(AppInfo appInfo, CategoryInfo category) {
		List<String> authorPersons = new ArrayList<>();
		if( ListTools.isNotEmpty( appInfo.getManageablePersonList() )) {
			for( String name : appInfo.getManageablePersonList() ) {
				if( !authorPersons.contains( name )) {
					authorPersons.add( name );
				}
			}
		}
		if( ListTools.isNotEmpty( appInfo.getPublishablePersonList() )) {
			for( String name : appInfo.getPublishablePersonList() ) {
				if( !authorPersons.contains( name )) {
					authorPersons.add( name );
				}
			}
		}
		if( ListTools.isNotEmpty( category.getManageablePersonList() )) {
			for( String name : category.getManageablePersonList() ) {
				if( !authorPersons.contains( name )) {
					authorPersons.add( name );
				}
			}
		}
		if( ListTools.isNotEmpty( category.getPublishablePersonList() )) {
			for( String name : category.getPublishablePersonList() ) {
				if( !authorPersons.contains( name )) {
					authorPersons.add( name );
				}
			}
		}
		return authorPersons;
	}

	public static class Wo extends GsonPropertyObject {

		@FieldDescribe("作为输出的CMS文档数据对象.")
		private WoDocument document;

		@FieldDescribe("作为输出的CMS文档附件文件信息数据对象.")
		private List<WoFileInfo> attachmentList;

		@FieldDescribe("作为输出的CMS文档操作日志.")
		private List<WoLog> documentLogList;

		@FieldDescribe("文档所有数据信息.")
		private Data data;

		@FieldDescribe("作为编辑的CMS文档表单.")
		private WoForm form;

		@FieldDescribe("作为查看的CMS文档表单.")
		private WoForm readForm;

		private Boolean isAppAdmin = false;
		private Boolean isCategoryAdmin = false;
		private Boolean isManager = false;
		private Boolean isCreator = false;
		private Boolean isEditor = false;

		public WoDocument getDocument() {
			return document;
		}

		public void setDocument(WoDocument document) {
			this.document = document;
		}

		public List<WoFileInfo> getAttachmentList() {
			return attachmentList;
		}

		public void setAttachmentList(List<WoFileInfo> attachmentList) {
			this.attachmentList = attachmentList;
		}

		public List<WoLog> getDocumentLogList() {
			return documentLogList;
		}

		public void setDocumentLogList(List<WoLog> documentLogList) {
			this.documentLogList = documentLogList;
		}

		public Data getData() {
			return data;
		}

		public void setData(Data data) {
			this.data = data;
		}

		public WoForm getForm() {
			return form;
		}

		public void setForm(WoForm form) {
			this.form = form;
		}

		public WoForm getReadForm() {
			return readForm;
		}

		public void setReadForm(WoForm readForm) {
			this.readForm = readForm;
		}

		public Boolean getIsAppAdmin() {
			return isAppAdmin;
		}

		public Boolean getIsCategoryAdmin() {
			return isCategoryAdmin;
		}

		public Boolean getIsManager() {
			return isManager;
		}

		public void setIsAppAdmin(Boolean isAppAdmin) {
			this.isAppAdmin = isAppAdmin;
		}

		public void setIsCategoryAdmin(Boolean isCategoryAdmin) {
			this.isCategoryAdmin = isCategoryAdmin;
		}

		public void setIsManager(Boolean isManager) {
			this.isManager = isManager;
		}

		public Boolean getIsCreator() {
			return isCreator;
		}

		public void setIsCreator(Boolean isCreator) {
			this.isCreator = isCreator;
		}

		public Boolean getIsEditor() {
			return isEditor;
		}

		public void setIsEditor(Boolean isEditor) {
			this.isEditor = isEditor;
		}
	}

	public static class WoDocument extends Document {

		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> Excludes = new ArrayList<String>();

		public static WrapCopier<Document, WoDocument> copier = WrapCopierFactory.wo(Document.class, WoDocument.class,
				null, JpaObject.FieldsInvisible);

		/**
		 * 只作显示用
		 */
		private String creatorPersonShort = "";

		private String creatorUnitNameShort = "";

		private String creatorTopUnitNameShort = "";

		public String getCreatorPersonShort() {
			return creatorPersonShort;
		}

		public String getCreatorUnitNameShort() {
			return creatorUnitNameShort;
		}

		public String getCreatorTopUnitNameShort() {
			return creatorTopUnitNameShort;
		}

		public void setCreatorPersonShort(String creatorPersonShort) {
			this.creatorPersonShort = creatorPersonShort;
		}

		public void setCreatorUnitNameShort(String creatorUnitNameShort) {
			this.creatorUnitNameShort = creatorUnitNameShort;
		}

		public void setCreatorTopUnitNameShort(String creatorTopUnitNameShort) {
			this.creatorTopUnitNameShort = creatorTopUnitNameShort;
		}

	}

	public static class WoFileInfo extends FileInfo {

		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> Excludes = new ArrayList<String>();

		public static WrapCopier<FileInfo, WoFileInfo> copier = WrapCopierFactory.wo(FileInfo.class, WoFileInfo.class,
				null, JpaObject.FieldsInvisible);

		private Long referencedCount;

		public Long getReferencedCount() {
			return referencedCount;
		}

		public void setReferencedCount(Long referencedCount) {
			this.referencedCount = referencedCount;
		}
	}

	public static class WoLog extends Log {

		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> Excludes = new ArrayList<String>();
	}

	public static class WoForm extends Form {

		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> Excludes = new ArrayList<String>();
	}
}