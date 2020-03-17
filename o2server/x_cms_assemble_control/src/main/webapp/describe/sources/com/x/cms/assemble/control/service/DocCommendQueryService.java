package com.x.cms.assemble.control.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;

/**
 * 文档点赞信息查询管理的服务类
 * 
 * @author O2LEE
 */
public class DocCommendQueryService {
	
	private DocCommendService docCommendService = new DocCommendService();
	
	public List<String> listByDocAndPerson( String docId, String personName, Integer maxCount ) throws Exception {
		if( StringUtils.isEmpty( docId ) ){
			return null;
		}
		if( StringUtils.isEmpty( personName ) ){
			return null;
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return docCommendService.listByDocAndPerson(emc, docId, personName, maxCount);
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	public List<String> listByDocument( String docId, Integer maxCount ) throws Exception {
		if( StringUtils.isEmpty( docId ) ){
			return null;
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return docCommendService.listByDocument( emc, docId, maxCount );
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	public List<String> listWithPerson( String personName, Integer maxCount ) throws Exception {
		if( StringUtils.isEmpty( personName ) ){
			return null;
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return docCommendService.listWithPerson(emc, personName, maxCount);
		} catch ( Exception e ) {
			throw e;
		}
	}
}
