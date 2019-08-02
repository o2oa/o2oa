package com.x.cms.assemble.control;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.assemble.control.factory.AppDictFactory;
import com.x.cms.assemble.control.factory.AppDictItemFactory;
import com.x.cms.assemble.control.factory.AppInfoFactory;
import com.x.cms.assemble.control.factory.CategoryExtFactory;
import com.x.cms.assemble.control.factory.CategoryInfoFactory;
import com.x.cms.assemble.control.factory.CmsBatchOperationFactory;
import com.x.cms.assemble.control.factory.DocumentCommendFactory;
import com.x.cms.assemble.control.factory.DocumentCommentCommendFactory;
import com.x.cms.assemble.control.factory.DocumentCommentInfoFactory;
import com.x.cms.assemble.control.factory.DocumentFactory;
import com.x.cms.assemble.control.factory.DocumentViewRecordFactory;
import com.x.cms.assemble.control.factory.FileFactory;
import com.x.cms.assemble.control.factory.FileInfoFactory;
import com.x.cms.assemble.control.factory.FormFactory;
import com.x.cms.assemble.control.factory.FormFieldFactory;
import com.x.cms.assemble.control.factory.ItemFactory;
import com.x.cms.assemble.control.factory.LogFactory;
import com.x.cms.assemble.control.factory.ReviewFactory;
import com.x.cms.assemble.control.factory.ScriptFactory;
import com.x.cms.assemble.control.factory.SearchFactory;
import com.x.cms.assemble.control.factory.TemplateFormFactory;
import com.x.cms.assemble.control.factory.ViewCategoryFactory;
import com.x.cms.assemble.control.factory.ViewFactory;
import com.x.cms.assemble.control.factory.ViewFieldConfigFactory;
import com.x.cms.assemble.control.factory.element.QueryViewFactory;
import com.x.cms.core.entity.AppInfo;
import com.x.organization.core.express.Organization;

public class Business {

	private EntityManagerContainer emc;

	public Business(EntityManagerContainer emc) throws Exception {
		this.emc = emc;
	}

	public EntityManagerContainer entityManagerContainer() {
		return this.emc;
	}

	private TemplateFormFactory templateFormFactory;
	private AppInfoFactory appInfoFactory;
	private CategoryInfoFactory categoryInfoFactory;
	private CategoryExtFactory categoryExtFactory;
	private FileInfoFactory fileInfoFactory;
	private LogFactory logFactory;
	private DocumentFactory documentFactory;
	private DocumentViewRecordFactory documentViewRecordFactory;
	private FormFactory formFactory;
	private FileFactory fileFactory;
	private QueryViewFactory queryViewFactory;
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

	

	public QueryViewFactory queryViewFactory() throws Exception {
		if (null == this.queryViewFactory) {
			this.queryViewFactory = new QueryViewFactory(this);
		}
		return queryViewFactory;
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

	public boolean isHasPlatformRole( String personName, String roleName) throws Exception {
		if ( StringUtils.isEmpty( personName ) ) {
			throw new Exception("personName is null!");
		}
		if ( StringUtils.isEmpty( roleName )) {
			throw new Exception("roleName is null!");
		}
		List<String> roleList = null;
		roleList = organization().role().listWithPerson( personName );
		if ( roleList != null && !roleList.isEmpty()) {
			if( roleList.stream().filter( r -> roleName.equalsIgnoreCase( r )).count() > 0 ){
				return true;
			}
		} else {
			return false;
		}
		return false;
	}
	
	/**
	 * TODO 判断用户是否管理员权限
	 * 1、person.isManager()
	 * 2、xadmin
	 * 3、CMSManager
	 * 
	 * @param request
	 * @param personName
	 * @return
	 * @throws Exception
	 */
	public boolean isManager(HttpServletRequest request, EffectivePerson person) throws Exception {
		// 如果用户的身份是平台的超级管理员，那么就是超级管理员权限
		if ( person.isManager() ) {
			return true;
		}
		if ( "xadmin".equalsIgnoreCase( person.getDistinguishedName()) ) {
			return true;
		}
		if( isHasPlatformRole(person.getDistinguishedName(), ThisApplication.ROLE_CMSManager ) ) {
			return true;
		}
		return false;
	}

	/**
	 * TODO 判断应用信息是否可以被删除，查询与其他数据之间的关联信息
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public boolean appInfoDeleteAvailable(String appId) throws Exception {
		// 查询是否有下级应用分类信息
		long count = this.getCategoryInfoFactory().countByAppId(appId, "全部");
		if (count > 0) {
			return false;
		}
		return true;
	}

	/**
	 * TODO 判断分类信息是否可以被删除，查询与其他数据之间的关联信息
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public boolean categoryInfoDeleteAvailable(String categoryId) throws Exception {
		// 查询是否有下级应用分类信息
		long count = this.getDocumentFactory().countByCategoryId(categoryId);
		if (count > 0) {
			return false;
		}
		return true;
	}

	/**
	 * TODO TODO (uncomplete)判断用户是否有权限进行：[内容管理应用信息管理]的操作
	 * 
	 * @param request
	 * @param personName
	 * @return
	 * @throws Exception
	 */
	public boolean appInfoEditAvailable(HttpServletRequest request, EffectivePerson person, String id)
			throws Exception {
		if ( isManager(request, person)) {
			return true;
		}
		return true;
	}

	/**
	 * TODO (uncomplete)判断用户是否有权限进行:[内容管理分类信息管理]的操作
	 * 
	 * @param request
	 * @param personName
	 * @return
	 * @throws Exception
	 */
	public boolean categoryInfoEditAvailable(EffectivePerson person, String appId) throws Exception {
		if (person.isManager()) {
			return true;
		}
		return true;
	}

	/**
	 * TODO (uncomplete)判断用户是否有权限进行：[内容管理应用及分类权限配置]的操作
	 * 
	 * @param request
	 * @param personName
	 * @return
	 * @throws Exception
	 */
	public boolean appCategoryPermissionEditAvailable(HttpServletRequest request, EffectivePerson person)
			throws Exception {
		if ( isManager(request, person)) {
			return true;
		}
		// 其他情况暂时全部不允许操作
		return true;
	}

	/**
	 * TODO (uncomplete)判断用户是否有权限进行：[文件或者附件管理]的操作
	 * 
	 * @param request
	 * @param personName
	 * @return
	 * @throws Exception
	 */
	public boolean fileInfoEditAvailable(HttpServletRequest request, EffectivePerson person) throws Exception {
		if ( isManager(request, person)) {
			return true;
		}
		// 其他情况暂时全部不允许操作
		return true;
	}

	/**
	 * TODO (uncomplete)判断用户是否有权限进行：[文件或者附件管理]的操作
	 * 
	 * @param request
	 * @param personName
	 * @return
	 * @throws Exception
	 */
	public boolean logEditAvailable(HttpServletRequest request, EffectivePerson person) throws Exception {
		if ( isManager(request, person)) {
			return true;
		}
		// 其他情况暂时全部不允许操作
		return true;
	}

	/**
	 * TODO (uncomplete)判断用户是否有权限进行：[设置应用或者分类管理员权限]的操作 平台的管理员有权限进行设置
	 * 
	 * @param request
	 * @param person
	 * @return
	 * @throws Exception
	 */
	public boolean appCategoryAdminEditAvailable(HttpServletRequest request, EffectivePerson person) throws Exception {
		if ( isManager(request, person)) {
			return true;
		}
		// 其他情况暂时全部不允许操作
		return true;
	}

	/**
	 * TODO (uncomplete)判断用户是否有权限进行：[表单模板管理]操作
	 * 
	 * @param request
	 * @param person
	 * @return
	 * @throws Exception
	 */
	public boolean formEditAvailable(HttpServletRequest request, EffectivePerson person) throws Exception {
		if ( isManager(request, person)) {
			return true;
		}
		// 其他情况暂时全部不允许操作
		return true;
	}

	/**
	 * TODO (uncomplete)判断用户是否有权限进行：[视图配置管理]操作
	 * 
	 * @param request
	 * @param person
	 * @return
	 * @throws Exception
	 */
	public boolean viewEditAvailable(HttpServletRequest request, EffectivePerson person) throws Exception {
		if ( isManager(request, person)) {
			return true;
		}
		// 其他情况暂时全部不允许操作
		return true;
	}

	/**
	 * TODO (uncomplete)用户是否有权限进行：[访问文档信息]的操作
	 * 
	 * @param person
	 * @param documentId
	 * @return
	 * @throws Exception
	 */
	public boolean documentAllowRead(HttpServletRequest request, EffectivePerson person, String id) throws Exception {
		if ( isManager(request, person)) {
			return true;
		}
		// 其他情况暂时全部不允许操作
		return true;
	}

	/**
	 * TODO (uncomplete)用户是否有权限进行：[保存文档信息]的操作
	 * 
	 * @param person
	 * @param documentId
	 * @return
	 * @throws Exception
	 */
	public boolean documentAllowSave(HttpServletRequest request, EffectivePerson person, String documentId)
			throws Exception {
		if ( isManager(request, person)) {
			return true;
		}
		// 其他情况暂时全部不允许操作
		return true;
	}

	/**
	 * 获取文档列表的查看权限
	 * 
	 * @param request
	 * @param person
	 * @return
	 * @throws Exception
	 */
	public String getDocumentListViewPermission1(HttpServletRequest request, EffectivePerson person) throws Exception {
		if ( isManager( request, person )) {
			return "XAdmin";
		}
		return "Person";
	}

	public boolean editable( EffectivePerson effectivePerson, AppInfo appInfo ) throws Exception {
		boolean result = false;
		if ((StringUtils.equals(appInfo.getCreatorPerson(), effectivePerson.getDistinguishedName()))
				|| effectivePerson.isManager() || organization().person().hasRole(effectivePerson,
						OrganizationDefinition.CMSManager)) {
			return true;
		}
		
		//判断effectivePerson是不是该栏目的管理者：涉及 个人，组织和群组
		List<String> unitNameList= this.organization().unit().listWithPersonSupNested( effectivePerson.getDistinguishedName() );
		List<String> groupNameList = new ArrayList<String>();
		List<String> groupList = this.organization().group().listWithPerson( effectivePerson.getDistinguishedName() );
		if (groupList != null && groupList.size() > 0) {
			groupList.stream().filter( g -> !groupNameList.contains( g )).distinct().forEach( g -> groupNameList.add( g ));
		}

		if( ListTools.isNotEmpty( appInfo.getManageablePersonList() )) {
			if (appInfo.getManageablePersonList().contains( effectivePerson.getDistinguishedName() )) {
				return true;
			}
		}
		
		if( ListTools.isNotEmpty( appInfo.getManageableGroupList() ) && ListTools.isNotEmpty( groupNameList )) {
			groupNameList.retainAll( appInfo.getManageableGroupList()  );
			if( ListTools.isNotEmpty( groupNameList )) {
				return true;
			}
		}
		
		if( ListTools.isNotEmpty( appInfo.getManageableUnitList() )&& ListTools.isNotEmpty( unitNameList )) {
			unitNameList.retainAll( appInfo.getManageableUnitList()  );
			if( ListTools.isNotEmpty( unitNameList )) {
				return true;
			}
		}
		return result;
	}
}
