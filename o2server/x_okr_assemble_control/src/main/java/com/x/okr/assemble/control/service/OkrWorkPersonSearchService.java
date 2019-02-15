package com.x.okr.assemble.control.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.okr.assemble.control.Business;
import com.x.okr.entity.OkrCenterWorkInfo;
import com.x.okr.entity.OkrWorkPerson;

public class OkrWorkPersonSearchService {
	
	public List<OkrCenterWorkInfo> listCenterInfoNextWithFilter( String id, Integer count, com.x.okr.assemble.control.jaxrs.WorkCommonQueryFilter wrapIn ) throws Exception {
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
				if ( !StringUtils.equalsIgnoreCase( id, StandardJaxrsAction.EMPTY_SYMBOL)) {
					sequence = PropertyUtils.getProperty( emc.find( id, OkrCenterWorkInfo.class  ),  JpaObject.sequence_FIELDNAME );
				}
			}
			okrWorkPersonList = business.okrWorkPersonSearchFactory().listCenterWorkPersonNextWithFilter( id, count, sequence, wrapIn );
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
	
	public List<OkrCenterWorkInfo> listCenterInfoPrevWithFilter( String id, Integer count, com.x.okr.assemble.control.jaxrs.WorkCommonQueryFilter wrapIn ) throws Exception {
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
				if ( !StringUtils.equalsIgnoreCase( id, StandardJaxrsAction.EMPTY_SYMBOL)) {
					sequence = PropertyUtils.getProperty( emc.find( id, OkrCenterWorkInfo.class  ),  JpaObject.sequence_FIELDNAME );
				}
			}
			okrWorkPersonList = business.okrWorkPersonSearchFactory().listCenterWorkPersonPrevWithFilter( id, count, sequence, wrapIn );
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
	
	public Long getCenterCountWithFilter( com.x.okr.assemble.control.jaxrs.WorkCommonQueryFilter wrapIn ) throws Exception {
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