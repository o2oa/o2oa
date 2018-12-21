package com.x.report.assemble.control.jaxrs.profile;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.WrapOutBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.report.assemble.control.creator.document.month.ReportDocumentCreator;
import com.x.report.assemble.control.creator.workflow.MonthReportWorkFlowStarter;
import com.x.report.assemble.control.jaxrs.profile.exception.ExceptionProfileNotExists;
import com.x.report.assemble.control.jaxrs.report.exception.ExceptionReportInfoProcess;
import com.x.report.core.entity.Report_P_Profile;

/**
 * 根据概要信息重新发起汇报
 * @author O2LEE
 *
 */
public class ActionSupplementalWithProfile extends BaseAction {
	
	private Logger logger = LoggerFactory.getLogger( ActionSupplementalWithProfile.class );
	
	protected ActionResult<WrapOutBoolean> execute( HttpServletRequest request, EffectivePerson effectivePerson, String profileId ) throws Exception {
		ActionResult<WrapOutBoolean> result = new ActionResult<>();
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
				logger.info( ">>>>>>>>>>根据获取到的概要信息为依据，生成所有个人以及部门的【汇报文档】，ProfileID=" + profileId);
				profile = new ReportDocumentCreator().create(effectivePerson, profile );
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		if( check ){
			try {
				logger.info( ">>>>>>>>>>【汇报文档】成功生成，开始【启动汇报流程】，ProfileID=" + profileId);
				profile = new MonthReportWorkFlowStarter().startWorkFlow( effectivePerson, profile );
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		if( check ){
			try {
				profile.setCreateSuccess( true );
				logger.info( ">>>>>>>>>>更新汇报启动概要文件！，ProfileID=" + profileId);
				report_P_ProfileServiceAdv.updateWithId( profile ) ;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		if( check ){
			try {
				WrapOutBoolean data = new WrapOutBoolean();
				data.setValue(check);
				result.setData( data );
			}catch( Exception e ) {
				Exception exception = new ExceptionReportInfoProcess(e, "系统在根据汇报概要文件重新生成或者补充汇报信息时发生异常。");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		return result;
	}
}