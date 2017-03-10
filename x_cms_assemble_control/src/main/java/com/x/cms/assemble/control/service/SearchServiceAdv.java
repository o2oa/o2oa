package com.x.cms.assemble.control.service;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.cms.assemble.control.jaxrs.search.AppFilter;
import com.x.cms.assemble.control.jaxrs.search.CategoryFilter;
import com.x.cms.assemble.control.jaxrs.search.CompanyFilter;
import com.x.cms.assemble.control.jaxrs.search.DepartmentFilter;

public class SearchServiceAdv {
	
	private SearchService searchService = new SearchService();
	
	public List<AppFilter> listAppInfoSearchFilter( List<String> app_ids, String docStatus, String categoryId ) throws Exception {
		if( app_ids == null || app_ids.isEmpty() ){
			return null;
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return searchService.listAppInfoSearchFilter( emc, app_ids, docStatus, categoryId );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<CategoryFilter> listCategorySearchFilter( List<String> app_ids, String docStatus, String categoryId ) throws Exception {
		if( app_ids == null || app_ids.isEmpty() ){
			return null;
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return searchService.listCategorySearchFilter( emc, app_ids, docStatus, categoryId );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<CompanyFilter> listCompanySearchFilter(List<String> app_ids, String docStatus, String categoryId) throws Exception {
		if( app_ids == null || app_ids.isEmpty() ){
			return null;
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return searchService.listCompanySearchFilter( emc, app_ids, docStatus, categoryId );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<DepartmentFilter> listDepartmentSearchFilter(List<String> app_ids, String docStatus, String categoryId) throws Exception {
		if( app_ids == null || app_ids.isEmpty() ){
			return null;
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return searchService.listDepartmentSearchFilter( emc, app_ids, docStatus, categoryId );
		} catch ( Exception e ) {
			throw e;
		}
	}

}
