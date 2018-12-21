package com.x.report.assemble.control.jaxrs.profile;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.reflect.TypeToken;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.WrapOutMap;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.report.assemble.control.jaxrs.profile.exception.ExceptionProfileNotExists;
import com.x.report.assemble.control.jaxrs.report.exception.ExceptionReportInfoProcess;
import com.x.report.assemble.control.schedule.bean.ReportUnitInfo;
import com.x.report.core.entity.Report_P_Profile;

/**
 * 根据概要信息重新发起汇报
 * @author O2LEE
 *
 */
public class ActionCheckWithProfile extends BaseAction {
	
	private Logger logger = LoggerFactory.getLogger( ActionCheckWithProfile.class );
	
	protected ActionResult<WrapOutMap> execute( HttpServletRequest request, EffectivePerson effectivePerson, String profileId ) throws Exception {
		ActionResult<WrapOutMap> result = new ActionResult<>();
		WrapOutMap wrapOutMap = new WrapOutMap();
		Report_P_Profile profile = null;
		Boolean check = true;
	
		if( check ){
			try {
				profile = report_P_ProfileServiceAdv.get( profileId );
				if( profile == null ) {
					check = false;
					Exception exception = new ExceptionProfileNotExists( profileId );
					result.error(exception);
				}
			}catch( Exception e ) {
				check = false;
				Exception exception = new ExceptionReportInfoProcess(e, "系统在根据ID查询汇报概要文件信息ID列表时发生异常。");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		
		if( check ){
			try {
				Type type = null;
				List<ReportUnitInfo> unitNames = null;
				
				//查询汇报创建所有组织信息, 查询所有举措涉及的组织信息
				String json_units = report_P_ProfileServiceAdv.getDetailValue( profile.getId(), "STRATEGY", "STRATEGY_MEASURE_UNIT" );
				type = new TypeToken<ArrayList<ReportUnitInfo>>() {}.getType();
				if( json_units != null && json_units.length() > 2 ) {
			        unitNames = gson.fromJson( json_units, type );
				}

				if( ListTools.isNotEmpty( unitNames )) {
					for( ReportUnitInfo unit : unitNames ) {//为指定的组织生成汇报信息
						//获取组织相关的所有工作列表
						logger.info( ">>>>>>>>>>系统正在检查组织【"+unit.getName()+"】月度汇报文档是否已经存在.....");
						if( !report_I_ServiceAdv.reportExists( profile.getId(), unit.getName() ) ) {
							wrapOutMap.put( unit.getName(), "待补充");
						}else {
							wrapOutMap.put( unit.getName(), "已发起");
						}
					}
				}		
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		if( check ){
			try {
				result.setData( wrapOutMap );
			}catch( Exception e ) {
				Exception exception = new ExceptionReportInfoProcess(e, "系统在根据汇报概要文件重新生成或者补充汇报信息时发生异常。");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		return result;
	}
}