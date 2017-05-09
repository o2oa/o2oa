package com.x.okr.assemble.control.jaxrs.okrworkbaseinfo;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.base.core.utils.SortTools;
import com.x.okr.assemble.control.jaxrs.okrcenterworkinfo.WrapOutOkrCenterWorkInfo;
import com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.exception.CenterWorkNotExistsException;
import com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.exception.GetOkrUserCacheException;
import com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.exception.WorkBaseInfoProcessException;
import com.x.okr.entity.OkrCenterWorkInfo;
import com.x.okr.entity.OkrWorkBaseInfo;

public class ExcuteListAllWorkByCenterId extends ExcuteBase {

	private Logger logger = LoggerFactory.getLogger( ExcuteListAllWorkByCenterId.class );
	
	protected ActionResult<WrapOutOkrCenterWorkInfo> execute( HttpServletRequest request,EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<WrapOutOkrCenterWorkInfo> result = new ActionResult<WrapOutOkrCenterWorkInfo>();
		List<WrapOutOkrWorkBaseInfo> all_wrapWorkBaseInfoList = null;
		List<OkrWorkBaseInfo> all_workBaseInfoList = null;
		WrapOutOkrCenterWorkInfo wrapOutOkrCenterWorkInfo = null;
		OkrCenterWorkInfo okrCenterWorkInfo  = null;
		Boolean check = true;
		if( check ){
			try{
				okrCenterWorkInfo = okrCenterWorkInfoService.get( id );//查询中心工作信息是否存在
			} catch ( Exception e ) {
				check = false;
				Exception exception = new GetOkrUserCacheException( e, effectivePerson.getName()  );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}	
		}
		if( check ){
			if( okrCenterWorkInfo == null ){
				check = false;
				Exception exception = new CenterWorkNotExistsException( id  );
				result.error( exception );
			}
		}
		if( check ){
			try {
				wrapOutOkrCenterWorkInfo = okrCenterWorkInfo_wrapout_copier.copy( okrCenterWorkInfo );
			} catch (Exception e) {
				check = false;
				Exception exception = new WorkBaseInfoProcessException( e, "将中心工作查询结果转换为可以输出的数据信息时发生异常。"  );
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
				Exception exception = new WorkBaseInfoProcessException( e, "根据中心工作ID查询中心工作下所有具体工作信息时发生异常。ID：" + id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if( check ){
			if( all_workBaseInfoList != null ){
				try {
					all_wrapWorkBaseInfoList = wrapout_copier.copy( all_workBaseInfoList );
				} catch (Exception e) {
					check = false;
					Exception exception = new WorkBaseInfoProcessException( e, "将查询结果转换为可以输出的数据信息时发生异常。" );
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