package com.x.okr.assemble.control.servlet.export;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.x.base.core.application.servlet.FileUploadServletTools;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.project.server.Config;
import com.x.okr.assemble.common.date.DateOperation;
import com.x.okr.assemble.common.excel.writer.WorkReportStatisticExportExcelWriter;
import com.x.okr.assemble.control.service.OkrCenterWorkReportStatisticService;
import com.x.okr.entity.OkrCenterWorkReportStatistic;

@WebServlet(urlPatterns = "/servlet/export/weekreportstatistic/*")
public class WeekReportStatisticExportServlet extends HttpServlet {

	private static final long serialVersionUID = -4314532091497625540L;
	private Logger logger = LoggerFactory.getLogger( WeekReportStatisticExportServlet.class );

	@HttpMethodDescribe(value = "导出统计表 servlet/export/weekreportstatistic/{statId}/stream", response = Object.class)
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		OkrCenterWorkReportStatisticService okrCenterWorkReportStatisticService = new OkrCenterWorkReportStatisticService();
		DateOperation dateOperation = new DateOperation();
		ActionResult<Object> result = new ActionResult<>();
		String part = null;
		String statId = null;
		Integer year = null;
		Integer month = null;
		Integer week = null;
		Date now = new Date();
		List<OkrCenterWorkReportStatistic> okrCenterWorkReportStatisticList = null;
		OkrCenterWorkReportStatistic okrCenterWorkReportStatistic = null;
		boolean streamContentType = false;
		boolean check = true;

		request.setCharacterEncoding("UTF-8");

		// 获取文件ID
		if (check) {
			try {
				part = FileUploadServletTools.getURIPart(request.getRequestURI(), "weekreportstatistic");
				statId = StringUtils.substringBefore( part, "/" );
			} catch (Exception e) {
				check = false;
				result.setUserMessage("系统URL信息获取传入的附件ID时发生异常。");
				result.error(e);
				logger.error("system get id from request url got an exception.", e);
			}
		}
		
		if (check) {
			streamContentType = StringUtils.endsWith( part, "stream" );
			logger.info("streamContentType = " + streamContentType);
		}

		if( check ){
			year = dateOperation.getYearNumber( now );
			month = dateOperation.getMonthNumber( now );
			week = dateOperation.getWeekNumOfYear( now );
		}
		if (check) {			
			if( "all".equals( statId ) ){
				try {
					okrCenterWorkReportStatisticList = okrCenterWorkReportStatisticService.list( null, "每周统计", year, month, week );
					logger.info( "all center work." );
				} catch (Exception e) {
					check = false;
					logger.error( "系统查询所有工作的汇报情况统计信息时发生异常。", e );
					result.setUserMessage("系统查询所有工作的汇报情况统计信息时发生异常。");
					result.error(e);
				}
			}else{
				if( statId != null && !statId.isEmpty() && !"(0)".equals( statId ) ){
					try {
						okrCenterWorkReportStatisticList = new ArrayList<OkrCenterWorkReportStatistic>();
						okrCenterWorkReportStatistic = okrCenterWorkReportStatisticService.get( statId );
						if( okrCenterWorkReportStatistic != null ){
							okrCenterWorkReportStatisticList.add( okrCenterWorkReportStatistic );
						}
					} catch (Exception e) {
						check = false;
						logger.error( "系统查询指定中心工作的汇报情况统计信息时发生异常。", e );
						result.setUserMessage("系统查询指定中心工作的汇报情况统计信息时发生异常。");
						result.error(e);
					}
				}
			}
		}
		
		if (check) {
			try {
				setResponseHeader( response, streamContentType, "工作汇报情况统计表_"+dateOperation.getNowTimeChar()+".xls" );
			} catch (Exception e) {
				check = false;
				result.setUserMessage("系统设置输出格式时发生异常。");
				result.error(e);
				logger.error("system set response header got an exception.", e);
			}
		}
		
		//文件下载		
		if (check) {
			if( okrCenterWorkReportStatisticList != null ){
				logger.info( "okrCenterWorkReportStatisticList list count = " + okrCenterWorkReportStatisticList.size() );
			}else{
				logger.info( "okrCenterWorkReportStatisticList is empty!" );
			}
			WorkReportStatisticExportExcelWriter.writeExcel( okrCenterWorkReportStatisticList, response.getOutputStream() );
		}

		if (!check) {
			response.setStatus( HttpServletResponse.SC_INTERNAL_SERVER_ERROR );
			FileUploadServletTools.result( response, result );
		}
	}

	private void setResponseHeader( HttpServletResponse response, boolean streamContentType, String fileName ) throws Exception {
		if (streamContentType) {
			response.setHeader("Content-Type", "application/octet-stream");
			response.setHeader("Content-Disposition", "fileInfo; filename=" + URLEncoder.encode(fileName, "utf-8"));
		} else {
			response.setHeader("Content-Type", Config.mimeTypes().getContentType(fileName));
			response.setHeader("Content-Disposition", "inline; filename=" + URLEncoder.encode(fileName, "utf-8"));
		}
	}
}