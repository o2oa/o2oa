package com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.WorkReportDispatchOverException;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.WorkReportIdEmptyException;

/**
 * 将汇报信息调度到结束
 * 1、汇报信息的信息状态修改为“结束”，详细信息里状态修改为“结束”
 * 2、汇报信息的当前处理环节“结束”
 * 3、汇报信息待办信息
 * 4、汇报信息待办汇总信息
 * 5、汇报信息处理记录里添加系统处理记录
 * 6、PERSONLINK记录里的处理状态修改为“结束”
 * @author liyi_
 *
 */
public class ExcuteDispatchToOver extends ExcuteBase {

	private Logger logger = LoggerFactory.getLogger( ExcuteDispatchToOver.class );

	protected ActionResult<WrapOutId> execute( HttpServletRequest request,EffectivePerson effectivePerson, String reportId ) throws Exception {
		ActionResult<WrapOutId> result = new ActionResult<>();
		if( reportId == null || reportId.isEmpty() ){
			Exception exception = new WorkReportIdEmptyException();
			result.error( exception );
			//logger.error( e, effectivePerson, request, null);
		}else{
			try{
				okrWorkReportFlowService.dispatchToOver( reportId );
				result.setData(new WrapOutId( reportId ));
			}catch(Exception e){
				Exception exception = new WorkReportDispatchOverException( e, reportId );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return result;
	}
	
}