package com.x.cms.assemble.control;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.project.Applications;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.x_correlation_service_processing;
import com.x.cms.assemble.control.factory.*;
import com.x.cms.assemble.control.factory.portal.PortalFactory;
import com.x.cms.assemble.control.factory.process.ProcessFactory;
import com.x.cms.assemble.control.factory.service.CenterServiceFactory;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.CategoryInfo;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.FileInfo;
import com.x.correlation.core.express.service.processing.jaxrs.correlation.ActionReadableTypeCmsWi;
import com.x.correlation.core.express.service.processing.jaxrs.correlation.ActionReadableTypeProcessPlatformWi;
import com.x.correlation.core.express.service.processing.jaxrs.correlation.ActionReadableTypeProcessPlatformWo;
import com.x.organization.core.express.Organization;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.OutputStream;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 通用业务类
 *
 * @author sword
 */
public class Business {

	public static final String[] FILENAME_SENSITIVES_KEY = new String[] { "/", ":", "*", "?", "<<", ">>", "|", "<", ">", "\\" };
	public static final String[] FILENAME_SENSITIVES_EMPTY = new String[] { "", "", "", "", "", "", "", "", "", "" };

	private EntityManagerContainer emc;

	public Business(EntityManagerContainer emc) throws Exception {
		this.emc = emc;
	}

	public EntityManagerContainer entityManagerContainer() {
		return this.emc;
	}

	private TemplateFormFactory templateFormFactory;
	private AppInfoFactory appInfoFactory;
	private AppInfoConfigFactory appInfoConfigFactory;
	private CategoryInfoFactory categoryInfoFactory;
	private CategoryExtFactory categoryExtFactory;
	private FileInfoFactory fileInfoFactory;
	private LogFactory logFactory;
	private DocumentFactory documentFactory;
	private DocumentViewRecordFactory documentViewRecordFactory;
	private FormFactory formFactory;
	private FileFactory fileFactory;
	private ViewCategoryFactory viewCategoryFactory;
	private ViewFactory viewFactory;
	private ViewFieldConfigFactory viewFieldConfigFactory;
	private AppDictFactory appDictFactory;
	private AppDictItemFactory appDictItemFactory;
	private ScriptFactory scriptFactory;
	private SearchFactory searchFactory;
	private Organization organization;
	private ItemFactory itemFactory;
	private FormFieldFactory formFieldFactory;
	private CmsBatchOperationFactory cmsBatchOperationFactory;
	private DocumentCommendFactory documentCommendFactory;
	private DocumentCommentCommendFactory documentCommentCommendFactory;
	private DocumentCommentInfoFactory documentCommentInfoFactory;
	private ReviewFactory reviewFactory;

	public AppInfoConfigFactory appInfoConfigFactory() throws Exception {
		if (null == this.appInfoConfigFactory) {
			this.appInfoConfigFactory = new AppInfoConfigFactory(this);
		}
		return appInfoConfigFactory;
	}

	public DocumentCommentCommendFactory documentCommentCommendFactory() throws Exception {
		if (null == this.documentCommentCommendFactory) {
			this.documentCommentCommendFactory = new DocumentCommentCommendFactory(this);
		}
		return documentCommentCommendFactory;
	}

	public ReviewFactory reviewFactory() throws Exception {
		if (null == this.reviewFactory) {
			this.reviewFactory = new ReviewFactory(this);
		}
		return reviewFactory;
	}

	public CmsBatchOperationFactory cmsBatchOperationFactory() throws Exception {
		if (null == this.cmsBatchOperationFactory) {
			this.cmsBatchOperationFactory = new CmsBatchOperationFactory(this);
		}
		return cmsBatchOperationFactory;
	}

	public DocumentCommentInfoFactory documentCommentInfoFactory() throws Exception {
		if (null == this.documentCommentInfoFactory) {
			this.documentCommentInfoFactory = new DocumentCommentInfoFactory(this);
		}
		return documentCommentInfoFactory;
	}

	public DocumentCommendFactory documentCommendFactory() throws Exception {
		if (null == this.documentCommendFactory) {
			this.documentCommendFactory = new DocumentCommendFactory(this);
		}
		return documentCommendFactory;
	}

	public FileFactory fileFactory() throws Exception {
		if (null == this.fileFactory) {
			this.fileFactory = new FileFactory(this);
		}
		return fileFactory;
	}

	public FormFieldFactory formFieldFactory() throws Exception {
		if (null == this.formFieldFactory) {
			this.formFieldFactory = new FormFieldFactory(this);
		}
		return formFieldFactory;
	}

	public ItemFactory itemFactory() throws Exception {
		if (null == this.itemFactory) {
			this.itemFactory = new ItemFactory(this);
		}
		return itemFactory;
	}

	public CategoryExtFactory categoryExtFactory() throws Exception {
		if (null == this.categoryExtFactory) {
			this.categoryExtFactory = new CategoryExtFactory(this);
		}
		return categoryExtFactory;
	}

	public Organization organization() throws Exception {
		if (null == this.organization) {
			this.organization = new Organization(ThisApplication.context());
		}
		return organization;
	}

	public TemplateFormFactory templateFormFactory() throws Exception {
		if (null == this.templateFormFactory) {
			this.templateFormFactory = new TemplateFormFactory(this);
		}
		return templateFormFactory;
	}

	public DocumentViewRecordFactory documentViewRecordFactory() throws Exception {
		if (null == this.documentViewRecordFactory) {
			this.documentViewRecordFactory = new DocumentViewRecordFactory(this);
		}
		return documentViewRecordFactory;
	}

	public ViewCategoryFactory getViewCategoryFactory() throws Exception {
		if (null == this.viewCategoryFactory) {
			this.viewCategoryFactory = new ViewCategoryFactory(this);
		}
		return viewCategoryFactory;
	}

	public ViewFactory getViewFactory() throws Exception {
		if (null == this.viewFactory) {
			this.viewFactory = new ViewFactory(this);
		}
		return viewFactory;
	}

	public ViewFieldConfigFactory getViewFieldConfigFactory() throws Exception {
		if (null == this.viewFieldConfigFactory) {
			this.viewFieldConfigFactory = new ViewFieldConfigFactory(this);
		}
		return viewFieldConfigFactory;
	}

	public SearchFactory getSearchFactory() throws Exception {
		if (null == this.searchFactory) {
			this.searchFactory = new SearchFactory(this);
		}
		return searchFactory;
	}

	public ScriptFactory getScriptFactory() throws Exception {
		if (null == this.scriptFactory) {
			this.scriptFactory = new ScriptFactory(this);
		}
		return scriptFactory;
	}

	public FormFactory getFormFactory() throws Exception {
		if (null == this.formFactory) {
			this.formFactory = new FormFactory(this);
		}
		return formFactory;
	}

	public AppDictFactory getAppDictFactory() throws Exception {
		if (null == this.appDictFactory) {
			this.appDictFactory = new AppDictFactory(this);
		}
		return appDictFactory;
	}

	public AppDictItemFactory getAppDictItemFactory() throws Exception {
		if (null == this.appDictItemFactory) {
			this.appDictItemFactory = new AppDictItemFactory(this);
		}
		return appDictItemFactory;
	}

	public DocumentFactory getDocumentFactory() throws Exception {
		if (null == this.documentFactory) {
			this.documentFactory = new DocumentFactory(this);
		}
		return documentFactory;
	}

	public AppInfoFactory getAppInfoFactory() throws Exception {
		if (null == this.appInfoFactory) {
			this.appInfoFactory = new AppInfoFactory(this);
		}
		return appInfoFactory;
	}

	public CategoryInfoFactory getCategoryInfoFactory() throws Exception {
		if (null == this.categoryInfoFactory) {
			this.categoryInfoFactory = new CategoryInfoFactory(this);
		}
		return categoryInfoFactory;
	}

	public FileInfoFactory getFileInfoFactory() throws Exception {
		if (null == this.fileInfoFactory) {
			this.fileInfoFactory = new FileInfoFactory(this);
		}
		return fileInfoFactory;
	}

	public LogFactory getLogFactory() throws Exception {
		if (null == this.logFactory) {
			this.logFactory = new LogFactory(this);
		}
		return logFactory;
	}

	private ProcessFactory process;

	public ProcessFactory process() throws Exception {
		if (null == this.process) {
			this.process = new ProcessFactory(this);
		}
		return process;
	}

	private PortalFactory portal;

	public PortalFactory portal() throws Exception {
		if (null == this.portal) {
			this.portal = new PortalFactory(this);
		}
		return portal;
	}

	private CenterServiceFactory centerService;

	public CenterServiceFactory centerService() throws Exception {
		if (null == this.centerService) {
			this.centerService = new CenterServiceFactory(this);
		}
		return centerService;
	}

	public boolean isHasPlatformRole(String personName, String roleName) throws Exception {
		if (StringUtils.isEmpty(personName)) {
			throw new Exception("personName is null!");
		}
		if (StringUtils.isEmpty(roleName)) {
			throw new Exception("roleName is null!");
		}
		List<String> roleList = null;
		roleList = organization().role().listWithPerson(personName);
		if (roleList != null && !roleList.isEmpty()) {
			if (roleList.stream().filter(r -> roleName.equalsIgnoreCase(r)).count() > 0) {
				return true;
			}
		} else {
			return false;
		}
		return false;
	}

	/**
	 * 判断用户是否管理员权限
	 *
	 * @param person
	 * @return
	 * @throws Exception
	 */
	public boolean isManager(EffectivePerson person) throws Exception {
		// 如果用户的身份是平台的超级管理员，那么就是超级管理员权限
		if (person.isManager()) {
			return true;
		} else {
			if (organization().person().hasRole(person, OrganizationDefinition.Manager,
					OrganizationDefinition.CMSManager)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 判断用户是否管理员权限
	 *
	 * @param person
	 * @return
	 * @throws Exception
	 */
	public boolean isCreatorManager(EffectivePerson person) throws Exception {
		// 如果用户的身份是平台的超级管理员，那么就是超级管理员权限
		if (person.isManager()) {
			return true;
		} else {
			if (organization().person().hasRole(person, OrganizationDefinition.Manager,
					OrganizationDefinition.CMSManager, OrganizationDefinition.CMSCreator)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 是否是栏目管理员
	 *
	 * @param person
	 * @param appInfo
	 * @return
	 * @throws Exception
	 */
	public boolean isAppInfoManager(EffectivePerson person, AppInfo appInfo) throws Exception {
		if (isManager(person)) {
			return true;
		}
		if (appInfo != null) {
			if (ListTools.isNotEmpty(appInfo.getManageablePersonList())) {
				if (appInfo.getManageablePersonList().contains(person.getDistinguishedName())) {
					return true;
				}
			}
			if (ListTools.isNotEmpty(appInfo.getManageableUnitList())) {
				List<String> unitNames = this.organization().unit()
						.listWithPersonSupNested(person.getDistinguishedName());
				if (ListTools.containsAny(unitNames, appInfo.getManageableUnitList())) {
					return true;
				}
			}
			if (ListTools.isNotEmpty(appInfo.getManageableGroupList())) {
				List<String> groupNames = this.organization().group().listWithPerson(person.getDistinguishedName());
				if (ListTools.containsAny(groupNames, appInfo.getManageableGroupList())) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 是否是栏目创建管理员
	 *
	 * @param person
	 * @param appInfo
	 * @return
	 * @throws Exception
	 */
	public boolean isAppCreatorManager(EffectivePerson person, AppInfo appInfo) throws Exception {
		if (appInfo != null) {
			if (isManager(person)) {
				return true;
			}
			if (ListTools.isNotEmpty(appInfo.getManageablePersonList())) {
				if (appInfo.getManageablePersonList().contains(person.getDistinguishedName())) {
					return true;
				}
			}
			if (ListTools.isNotEmpty(appInfo.getManageableUnitList())) {
				List<String> unitNames = this.organization().unit()
						.listWithPersonSupNested(person.getDistinguishedName());
				if (ListTools.containsAny(unitNames, appInfo.getManageableUnitList())) {
					return true;
				}
			}
			if (ListTools.isNotEmpty(appInfo.getManageableGroupList())) {
				List<String> groupNames = this.organization().group().listWithPerson(person.getDistinguishedName());
				if (ListTools.containsAny(groupNames, appInfo.getManageableGroupList())) {
					return true;
				}
			}
		} else if (isCreatorManager(person)) {
			return true;
		}
		return false;
	}

	/**
	 * 是否是文档的编辑者
	 * 文档不存在判断是否是分类或应用的发布者
	 * @param person
	 * @param appInfo
	 * @return
	 * @throws Exception
	 */
	public boolean isDocumentEditor(EffectivePerson person, AppInfo appInfo, CategoryInfo categoryInfo, Document document) throws Exception {
		if (isManager(person)) {
			return true;
		}
		List<String> unitNames = this.organization().unit().listWithPersonSupNested(person.getDistinguishedName());
		List<String> groupNames = this.organization().group().listWithPerson(person.getDistinguishedName());
		if(document!=null){
			if( ListTools.isNotEmpty( document.getManagerList() )) {
				if( document.getManagerList().contains( getShortTargetFlag(person.getDistinguishedName()) ) ) {
					return true;
				}
			}
			if( ListTools.isNotEmpty( document.getAuthorPersonList() )) {
				if( document.getAuthorPersonList().contains( getShortTargetFlag(person.getDistinguishedName()) ) ) {
					return true;
				}
			}
			if( ListTools.isNotEmpty( document.getAuthorUnitList() )) {
				if( ListTools.containsAny( getShortTargetFlag(unitNames), document.getAuthorUnitList())) {
					return true;
				}
			}
			if( ListTools.isNotEmpty( document.getAuthorGroupList() )) {
				if( ListTools.containsAny( getShortTargetFlag(groupNames), document.getAuthorGroupList())) {
					return true;
				}
			}
			if(appInfo == null){
				appInfo = this.emc.find(document.getAppId(), AppInfo.class);
			}
			if(categoryInfo == null){
				categoryInfo = this.emc.find(document.getCategoryId(), CategoryInfo.class);
			}
		}
		boolean publishFlag = document == null ? true : false;

		if (categoryInfo != null) {
			Set<String> catePersonList = new HashSet<>(categoryInfo.getManageablePersonList());
			Set<String> cateUnitList = new HashSet<>(categoryInfo.getManageableUnitList());
			Set<String> cateGroupList = new HashSet<>(categoryInfo.getManageableGroupList());
			if(document == null){
				catePersonList.addAll(categoryInfo.getPublishablePersonList());
				cateUnitList.addAll(categoryInfo.getPublishableUnitList());
				cateGroupList.addAll(categoryInfo.getPublishableGroupList());
				if(!categoryInfo.getPublishablePersonList().isEmpty() || !categoryInfo.getPublishableUnitList().isEmpty()
						|| !categoryInfo.getPublishableGroupList().isEmpty()){
					publishFlag = false;
				}
			}
			if (catePersonList.size() > 0 && catePersonList.contains(person.getDistinguishedName())) {
				return true;
			}
			if (cateUnitList.size() > 0 && ListTools.containsAny(unitNames, new ArrayList<>(cateUnitList))) {
				return true;
			}
			if (cateGroupList.size() > 0 && ListTools.containsAny(groupNames, new ArrayList<>(cateGroupList))) {
				return true;
			}
		}
		if (appInfo != null) {
			Set<String> appPersonList = new HashSet<>(appInfo.getManageablePersonList());
			Set<String> appUnitList = new HashSet<>(appInfo.getManageableUnitList());
			Set<String> appGroupList = new HashSet<>(appInfo.getManageableGroupList());
			if(document == null){
				appPersonList.addAll(appInfo.getPublishablePersonList());
				appUnitList.addAll(appInfo.getPublishableUnitList());
				appGroupList.addAll(appInfo.getPublishableGroupList());
				if(!appInfo.getPublishablePersonList().isEmpty() || !appInfo.getPublishableUnitList().isEmpty()
						|| !appInfo.getPublishableGroupList().isEmpty()){
					publishFlag = false;
				}
			}
			if (appPersonList.size() > 0 && appPersonList.contains(person.getDistinguishedName())) {
				return true;
			}
			if (appUnitList.size() > 0 && ListTools.containsAny(unitNames, new ArrayList<>(appUnitList))) {
				return true;
			}
			if (appGroupList.size() > 0 && ListTools.containsAny(groupNames, new ArrayList<>(appGroupList))) {
				return true;
			}
		}
		return publishFlag;
	}

	/**
	 * 是否是文档的读者
	 * @param person
	 * @return
	 * @throws Exception
	 */
	public boolean isDocumentReader(EffectivePerson person, Document document) throws Exception {
		if (isManager(person)) {
			return true;
		}
		String documentType = "数据";
		if(documentType.equals(document.getDocumentType())){
			return true;
		}
		if(BooleanUtils.isTrue(document.getIsAllRead())){
			return true;
		}
		String allPerson = "所有人";
		if( document.getReadPersonList().contains(getShortTargetFlag(person.getDistinguishedName())) ||
				document.getReadPersonList().contains(allPerson)) {
			return true;
		}
		Long count = this.reviewFactory().countByDocumentAndPerson(document.getId(), person.getDistinguishedName());
		if(count > 0){
			return true;
		}
		count = this.reviewFactory().countByDocumentAndPerson(document.getId(), "*");
		if(count > 0){
			return true;
		}
		return ifDocumentHasBeenCorrelation(person.getDistinguishedName(), document.getId());
	}

	public boolean ifDocumentHasBeenCorrelation(String person, String docId) throws Exception {
		ActionReadableTypeCmsWi req = new ActionReadableTypeCmsWi();
		req.setPerson(person);
		req.setDoucment(docId);
		ActionReadableTypeProcessPlatformWo resp = ThisApplication.context().applications()
				.postQuery(x_correlation_service_processing.class,
						Applications.joinQueryUri("correlation", "readable", "type", "cms"), req, docId)
				.getData(ActionReadableTypeProcessPlatformWo.class);
		return resp.getValue();
	}

	/**
	 * TODO (uncomplete)判断用户是否有权限进行：[表单模板管理]操作
	 *
	 * @param person
	 * @return
	 * @throws Exception
	 */
	public boolean formEditAvailable(EffectivePerson person) throws Exception {
		if (isManager(person)) {
			return true;
		}
		// 其他情况暂时全部不允许操作
		return true;
	}

	/**
	 * TODO (uncomplete)判断用户是否有权限进行：[视图配置管理]操作
	 *
	 * @param person
	 * @return
	 * @throws Exception
	 */
	public boolean viewEditAvailable(EffectivePerson person) throws Exception {
		if (isManager(person)) {
			return true;
		}
		// 其他情况暂时全部不允许操作
		return true;
	}

	public boolean editable(EffectivePerson effectivePerson, AppInfo appInfo) throws Exception {
		if ((StringUtils.equals(appInfo.getCreatorPerson(), effectivePerson.getDistinguishedName()))
				|| effectivePerson.isManager()
				|| organization().person().hasRole(effectivePerson, OrganizationDefinition.CMSManager)) {
			return true;
		}

		// 判断effectivePerson是不是该栏目的管理者：涉及 个人，组织和群组
		List<String> unitNameList = this.organization().unit()
				.listWithPersonSupNested(effectivePerson.getDistinguishedName());
		List<String> groupNameList = new ArrayList<String>();
		List<String> groupList = this.organization().group().listWithPerson(effectivePerson.getDistinguishedName());
		if (groupList != null && groupList.size() > 0) {
			groupList.stream().filter(g -> !groupNameList.contains(g)).distinct().forEach(g -> groupNameList.add(g));
		}

		if (ListTools.isNotEmpty(appInfo.getManageablePersonList())) {
			if (appInfo.getManageablePersonList().contains(effectivePerson.getDistinguishedName())) {
				return true;
			}
		}

		if (ListTools.isNotEmpty(appInfo.getManageableGroupList()) && ListTools.isNotEmpty(groupNameList)) {
			groupNameList.retainAll(appInfo.getManageableGroupList());
			if (ListTools.isNotEmpty(groupNameList)) {
				return true;
			}
		}

		if (ListTools.isNotEmpty(appInfo.getManageableUnitList()) && ListTools.isNotEmpty(unitNameList)) {
			unitNameList.retainAll(appInfo.getManageableUnitList());
			if (ListTools.isNotEmpty(unitNameList)) {
				return true;
			}
		}
		return false;
	}

	public static String getShortTargetFlag(String distinguishedName) {
		String target = distinguishedName;
		if( StringUtils.isNotEmpty( distinguishedName ) ){
			String[] array = distinguishedName.split("@");
			StringBuffer sb = new StringBuffer();
			if( array.length == 3 ){
				target = sb.append(array[1]).append("@").append(array[2]).toString();
			}else if( array.length == 2 ){
				//2段
				target = sb.append(array[0]).append("@").append(array[1]).toString();
			}else{
				target = array[0];
			}
		}
		return target;
	}

	public static List<String> getShortTargetFlag(List<String> nameList) {
		List<String> targetList = new ArrayList<>();
		if( ListTools.isNotEmpty( nameList ) ){
			for(String distinguishedName : nameList) {
				String target = distinguishedName;
				String[] array = target.split("@");
				StringBuffer sb = new StringBuffer();
				if (array.length == 3) {
					target = sb.append(array[1]).append("@").append(array[2]).toString();
				} else if (array.length == 2) {
					target = sb.append(array[0]).append("@").append(array[1]).toString();
				} else {
					target = array[0];
				}
				targetList.add(target);
			}
		}
		return targetList;
	}

	/**
	 * 下载附件并打包为zip
	 *
	 * @param attachmentList
	 * @param os
	 * @throws Exception
	 */
	public void downToZip(List<FileInfo> attachmentList, OutputStream os, Map<String, byte[]> otherAttMap)
			throws Exception {
		Map<String, FileInfo> filePathMap = new HashMap<>();
		List<String> emptyFolderList = new ArrayList<>();
		/* 生成zip压缩文件内的目录结构 */
		if (attachmentList != null) {
			for (FileInfo att : attachmentList) {
				if (filePathMap.containsKey(att.getName())) {
					filePathMap.put(att.getSite() + "-" + att.getName(), att);
				} else {
					filePathMap.put(att.getName(), att);
				}
			}
		}
		try (ZipOutputStream zos = new ZipOutputStream(os)) {
			for (Map.Entry<String, FileInfo> entry : filePathMap.entrySet()) {
				zos.putNextEntry(new ZipEntry(StringUtils.replaceEach(entry.getKey(),
						FILENAME_SENSITIVES_KEY,
						FILENAME_SENSITIVES_EMPTY)));
				StorageMapping mapping = ThisApplication.context().storageMappings().get(FileInfo.class,
						entry.getValue().getStorage());
				entry.getValue().readContent(mapping, zos);
			}

			if (otherAttMap != null) {
				for (Map.Entry<String, byte[]> entry : otherAttMap.entrySet()) {
					zos.putNextEntry(new ZipEntry(StringUtils.replaceEach(entry.getKey(),
							FILENAME_SENSITIVES_KEY,
							FILENAME_SENSITIVES_EMPTY)));
					zos.write(entry.getValue());
				}
			}

			// 往zip里添加空文件夹
			for (String emptyFolder : emptyFolderList) {
				zos.putNextEntry(new ZipEntry(emptyFolder));
			}
		}
	}
}
