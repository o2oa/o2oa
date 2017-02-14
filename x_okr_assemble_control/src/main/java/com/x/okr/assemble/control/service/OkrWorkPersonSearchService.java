package com.x.okr.assemble.control.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.http.HttpAttribute;
import com.x.okr.assemble.control.Business;
import com.x.okr.entity.OkrCenterWorkInfo;
import com.x.okr.entity.OkrWorkPerson;

public class OkrWorkPersonSearchService {

	private Logger logger = LoggerFactory.getLogger( OkrWorkPersonSearchService.class );
	
	public List<OkrCenterWorkInfo> listNextCenterIdsWithFilter( String id, Integer count, com.x.okr.assemble.control.jaxrs.okrcenterworkinfo.WrapInFilter wrapIn ) throws Exception {
		List<OkrCenterWorkInfo> centerWorkInfos = new ArrayList<OkrCenterWorkInfo>();
		List<OkrWorkPerson> okrWorkPersonList = null;
		OkrCenterWorkInfo okrCenterWorkInfo  = null;
		Business business = null;
		Object sequence = null;
		if( count == null ){
			count = 20;
		}
		if( wrapIn == null ){
			throw new Exception( "wrapIn is null!" );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			if( id != null && !"(0)".equals(id) && id.trim().length() > 20 ){
				if ( !StringUtils.equalsIgnoreCase( id, HttpAttribute.x_empty_symbol )) {
					sequence = PropertyUtils.getProperty( emc.find( id, OkrCenterWorkInfo.class, ExceptionWhen.not_found ), "sequence" );
				}
			}
			okrWorkPersonList = business.okrWorkPersonSearchFactory().listNextForCenterInfoWithFilter( id, count, sequence, wrapIn );
			if( okrWorkPersonList != null && !okrWorkPersonList.isEmpty() ){
				for( OkrWorkPerson person : okrWorkPersonList ){
					okrCenterWorkInfo = emc.find( person.getCenterId(), OkrCenterWorkInfo.class );
					if( okrCenterWorkInfo != null ){
						//查询中心工作下级工作总数
						//查询中心工作下所有工作总数
						centerWorkInfos.add( okrCenterWorkInfo );
					}
				}
			}
		} catch ( Exception e ) {
			throw e;
		}
		return centerWorkInfos;
	}
	
	public Long getCenterCountWithFilter( com.x.okr.assemble.control.jaxrs.okrcenterworkinfo.WrapInFilter wrapIn ) throws Exception {
		Business business = null;
		if( wrapIn == null ){
			throw new Exception( "wrapIn is null!" );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.okrWorkPersonSearchFactory().getCountForCenterInfoWithFilter(wrapIn);
		} catch ( Exception e ) {
			throw e;
		}
	}
	
}