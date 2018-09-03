package com.x.cms.assemble.search;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.project.http.EffectivePerson;
import com.x.cms.assemble.search.factory.AppInfoFactory;
import com.x.cms.assemble.search.factory.CategoryExtFactory;
import com.x.cms.assemble.search.factory.CategoryInfoFactory;
import com.x.cms.assemble.search.factory.DocumentFactory;
import com.x.cms.assemble.search.factory.FileInfoFactory;
import com.x.cms.assemble.search.factory.ItemFactory;
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
	public Organization organization() throws Exception {
		if (null == this.organization) {
			this.organization = new Organization(ThisApplication.context());
		}
		return organization;
	}
	
	private AppInfoFactory appInfoFactory;
	public AppInfoFactory appInfoFactory() throws Exception {
		if (null == this.appInfoFactory) {
			this.appInfoFactory = new AppInfoFactory(this);
		}
		return appInfoFactory;
	}
	
	private CategoryInfoFactory categoryInfoFactory;
	public CategoryInfoFactory categoryInfoFactory() throws Exception {
		if (null == this.categoryInfoFactory) {
			this.categoryInfoFactory = new CategoryInfoFactory(this);
		}
		return categoryInfoFactory;
	}
	
	private CategoryExtFactory categoryExtFactory;
	public CategoryExtFactory categoryExtFactory() throws Exception {
		if (null == this.categoryExtFactory) {
			this.categoryExtFactory = new CategoryExtFactory(this);
		}
		return categoryExtFactory;
	}
	
	private FileInfoFactory fileInfoFactory;
	public FileInfoFactory fileInfoFactory() throws Exception {
		if (null == this.fileInfoFactory) {
			this.fileInfoFactory = new FileInfoFactory(this);
		}
		return fileInfoFactory;
	}
	
	private DocumentFactory documentFactory;
	public DocumentFactory documentFactory() throws Exception {
		if (null == this.documentFactory) {
			this.documentFactory = new DocumentFactory(this);
		}
		return documentFactory;
	}
	
	private ItemFactory itemFactory;
	public ItemFactory itemFactory() throws Exception {
		if (null == this.itemFactory) {
			this.itemFactory = new ItemFactory(this);
		}
		return itemFactory;
	}
	
	public boolean isHasPlatformRole( String personName, String roleName) throws Exception {
		if ( personName == null || personName.isEmpty()) {
			throw new Exception("personName is null!");
		}
		if (roleName == null || roleName.isEmpty()) {
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
}
