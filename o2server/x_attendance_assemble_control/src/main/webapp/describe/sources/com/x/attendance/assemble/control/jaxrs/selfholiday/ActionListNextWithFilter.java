package com.x.attendance.assemble.control.jaxrs.selfholiday;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.ExceptionWrapInConvert;
import com.x.attendance.assemble.control.jaxrs.selfholiday.exception.ExceptionSelfHolidayProcess;
import com.x.attendance.entity.AttendanceSelfHoliday;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class ActionListNextWithFilter extends BaseAction {
	
	private static  Logger logger = LoggerFactory.getLogger( ActionListNextWithFilter.class );
	
	protected ActionResult<List<Wo>> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id, Integer count, JsonElement jsonElement ) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wraps = new ArrayList<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		Long total = 0L;
		List<AttendanceSelfHoliday> detailList = null;
		List<String> topUnitNames = new ArrayList<String>();
		List<String> unitNames = new ArrayList<String>();
		List<String> unitNameList = null;
		WrapInFilter wrapIn = null;
		Boolean check = true;
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, WrapInFilter.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new ExceptionWrapInConvert( e, jsonElement );
			result.error( exception );
			logger.error( e, currentPerson, request, null);
		}
		if(check ){
			try {		
				EntityManagerContainer emc = EntityManagerContainerFactory.instance().create();
				Business business = new Business(emc);
				
				//查询出ID对应的记录的sequence
				Object sequence = null;
				if( id == null || "(0)".equals(id) || id.isEmpty() ){
					//logger.info( "第一页查询，没有id传入" );
				}else{
					if (!StringUtils.equalsIgnoreCase(id,StandardJaxrsAction.EMPTY_SYMBOL)) {
						sequence = PropertyUtils.getProperty(emc.find( id, AttendanceSelfHoliday.class ),  JpaObject.sequence_FIELDNAME);
					}
				}
				
				//处理一下顶层组织，查询下级顶层组织
				if( StringUtils.isNotEmpty( wrapIn.getQ_topUnitName() ) ){
					topUnitNames.add( wrapIn.getQ_topUnitName() );
					try{
						unitNameList = userManagerService.listSubUnitNameWithParent( wrapIn.getQ_topUnitName() );
					}catch(Exception e){
						Exception exception = new ExceptionSelfHolidayProcess( e, 
								"系统根据顶层组织名称查询所有下级组织列表时发生异常.Name:" + wrapIn.getQ_topUnitName() );
						result.error( exception );
						logger.error( e, currentPerson, request, null);
					}
					if( unitNameList != null && unitNameList.size() > 0 ){
						for( String unitName : unitNameList){
							topUnitNames.add( unitName );
						}
					}
					wrapIn.setTopUnitNames( topUnitNames );
				}
				
				//处理一下组织,查询下级组织
				if( StringUtils.isNotEmpty( wrapIn.getQ_unitName() ) ){
					unitNames.add(wrapIn.getQ_unitName());
					try{
						unitNameList = userManagerService.listSubUnitNameWithParent( wrapIn.getQ_unitName() );
					}catch(Exception e){
						Exception exception = new ExceptionSelfHolidayProcess( e, "系统根据组织名称查询所有下级组织列表时发生异常.Name:" + wrapIn.getQ_unitName() );
						result.error( exception );
						logger.error( e, currentPerson, request, null);
					}
					if( unitNameList != null && unitNameList.size() > 0 ){
						for( String unitName : unitNameList){
							unitNames.add( unitName );
						}
					}
					wrapIn.setUnitNames(unitNames);
				}
				
				//从数据库中查询符合条件的一页数据对象
				detailList = business.getAttendanceSelfHolidayFactory().listIdsNextWithFilter( id, count, sequence, wrapIn );
				
				//从数据库中查询符合条件的对象总数
				total = business.getAttendanceSelfHolidayFactory().getCountWithFilter( wrapIn );

				//将所有查询出来的有状态的对象转换为可以输出的过滤过属性的对象
				wraps = Wo.copier.copy( detailList );
			} catch (Throwable th) {
				th.printStackTrace();
				result.error(th);
			}
		}
		result.setCount( total );
		result.setData(wraps);
		return result;
	}

	public static class Wo extends AttendanceSelfHoliday  {
		
		private static final long serialVersionUID = -5076990764713538973L;
		
		public static WrapCopier<AttendanceSelfHoliday, Wo> copier = 
				WrapCopierFactory.wo(AttendanceSelfHoliday.class, Wo.class, null,JpaObject.FieldsInvisible);
	}
}