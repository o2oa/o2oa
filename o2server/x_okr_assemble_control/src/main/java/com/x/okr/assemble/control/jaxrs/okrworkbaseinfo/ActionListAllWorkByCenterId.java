package com.x.okr.assemble.control.jaxrs.okrworkbaseinfo;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.SortTools;
import com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.exception.ExceptionCenterWorkNotExists;
import com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.exception.ExceptionGetOkrUserCache;
import com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.exception.ExceptionWorkBaseInfoProcess;
import com.x.okr.entity.OkrCenterWorkInfo;
import com.x.okr.entity.OkrWorkBaseInfo;

public class ActionListAllWorkByCenterId extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ActionListAllWorkByCenterId.class );
	
	protected ActionResult<WoOkrCenterWorkInfo> execute( HttpServletRequest request,EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<WoOkrCenterWorkInfo> result = new ActionResult<>();
		List<WoOkrWorkBaseInfo> all_wrapWorkBaseInfoList = null;
		List<OkrWorkBaseInfo> all_workBaseInfoList = null;
		WoOkrCenterWorkInfo wrapOutOkrCenterWorkInfo = null;
		OkrCenterWorkInfo okrCenterWorkInfo  = null;
		Boolean check = true;
		if( check ){
			try{
				okrCenterWorkInfo = okrCenterWorkInfoService.get( id );//查询中心工作信息是否存在
			} catch ( Exception e ) {
				check = false;
				Exception exception = new ExceptionGetOkrUserCache( e, effectivePerson.getDistinguishedName()  );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}	
		}
		if( check ){
			if( okrCenterWorkInfo == null ){
				check = false;
				Exception exception = new ExceptionCenterWorkNotExists( id  );
				result.error( exception );
			}
		}
		if( check ){
			try {
				wrapOutOkrCenterWorkInfo = WoOkrCenterWorkInfo.copier.copy( okrCenterWorkInfo );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionWorkBaseInfoProcess( e, "将中心工作查询结果转换为可以输出的数据信息时发生异常。"  );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if( check ){
			//获取到该中心工作下所有的工作信息
			try {
				all_workBaseInfoList = okrWorkBaseInfoService.listWorkInCenter( id, null );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionWorkBaseInfoProcess( e, "根据中心工作ID查询中心工作下所有具体工作信息时发生异常。ID：" + id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if( check ){
			if( all_workBaseInfoList != null ){
				try {
					all_wrapWorkBaseInfoList = WoOkrWorkBaseInfo.copier.copy( all_workBaseInfoList );
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionWorkBaseInfoProcess( e, "将查询结果转换为可以输出的数据信息时发生异常。" );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
		}
		if( check ){
			if( all_wrapWorkBaseInfoList != null && !all_wrapWorkBaseInfoList.isEmpty() ){
				try {
					SortTools.asc( all_wrapWorkBaseInfoList, "completeDateLimit" );
				} catch (Exception e) {
					logger.warn( "system sort work list got an exception." );
					logger.error(e);
					result.error( e );
				}
			}
			wrapOutOkrCenterWorkInfo.setWorks( all_wrapWorkBaseInfoList );
			result.setData( wrapOutOkrCenterWorkInfo );
		}
		return result;
	}
}