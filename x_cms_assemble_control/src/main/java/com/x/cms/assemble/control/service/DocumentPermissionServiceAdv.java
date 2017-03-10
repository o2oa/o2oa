package com.x.cms.assemble.control.service;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.cms.assemble.control.Business;
import com.x.cms.assemble.control.jaxrs.documentpermission.WrapInDocumentSearchFilter;
import com.x.cms.assemble.control.jaxrs.documentpermission.element.PermissionInfo;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.DocumentPermission;

public class DocumentPermissionServiceAdv {
	
	private DocumentPermissionService documentPermissionService = new DocumentPermissionService();
	
	public List<DocumentPermission> list( List<String> ids ) throws Exception {
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return documentPermissionService.list( emc, ids );
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 根据文档ID，为文档设置用户访问和管理权限
	 * @param docmentId
	 * @param permissionList
	 * @throws Exception 
	 */
	public void refreshDocumentPermission( Document docment, List<PermissionInfo> permissionList ) throws Exception {
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			documentPermissionService.refreshDocumentPermission( emc, docment, permissionList );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<String> lisViewableDocIdsWithFilter( WrapInDocumentSearchFilter wrapInDocumentSearchFilter, 
			List<String> permissionObjectCodeList, 
			List<String> manageableCategoryIds,
			Integer maxResultCount ) throws Exception {
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business( emc );
			return business.documentPermissionFactory().lisViewableDocIdsWithFilter(
					wrapInDocumentSearchFilter.getAppIdList(),
					wrapInDocumentSearchFilter.getCategoryIdList(),
					wrapInDocumentSearchFilter.getPublisherList(), 
					wrapInDocumentSearchFilter.getTitle(),
					wrapInDocumentSearchFilter.getCreateDateList(),
					wrapInDocumentSearchFilter.getPublishDateList(),
					wrapInDocumentSearchFilter.getStatusList(),
					permissionObjectCodeList,
					manageableCategoryIds,
					maxResultCount
			);
		} catch ( Exception e ) {
			throw e;
		}
	}
	
//	public Long countWithFilter( WrapInDocumentSearchFilter wrapInDocumentSearchFilter, List<String> permissionObjectCodeList ) throws Exception {
//		Business business = null;
//		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
//			business = new Business( emc );
//			return business.documentPermissionFactory().countWithFilter(
//					wrapInDocumentSearchFilter.getAppIdList(),
//					wrapInDocumentSearchFilter.getCategoryIdList(),
//					wrapInDocumentSearchFilter.getPublisherList(), 
//					wrapInDocumentSearchFilter.getTitle(),
//					wrapInDocumentSearchFilter.getCreateDateList(),
//					wrapInDocumentSearchFilter.getPublishDateList(),
//					wrapInDocumentSearchFilter.getStatusList(),
//					permissionObjectCodeList 
//			);
//		} catch ( Exception e ) {
//			throw e;
//		}
//	}
}
