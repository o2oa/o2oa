package com.x.attendance.assemble.control.jaxrs.fileimport;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.x.attendance.assemble.common.excel.reader.ExcelReaderUtil;
import com.x.attendance.assemble.common.excel.reader.IRowReader;
import com.x.attendance.assemble.control.ApplicationGobal;
import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.jaxrs.DateRecord;
import com.x.attendance.assemble.control.jaxrs.WrapOutMessage;
import com.x.attendance.entity.AttendanceDetail;
import com.x.attendance.entity.AttendanceImportFileInfo;
import com.x.base.core.application.jaxrs.StandardJaxrsAction;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.ResponseFactory;
import com.x.base.core.http.annotation.HttpMethodDescribe;


@Path("fileimport")
public class AttendanceDetailFileImportAction extends StandardJaxrsAction{
	
	private Logger logger = LoggerFactory.getLogger( AttendanceDetailFileImportAction.class );

	@HttpMethodDescribe(value = "检查需要导入的数据文件", response = CacheImportFileStatus.class)
	@GET
	@Path("check/{file_id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response checkDataFile(@Context HttpServletRequest request, @PathParam("file_id") String file_id ) {
		ActionResult<CacheImportFileStatus> result = new ActionResult<>();
		CacheImportFileStatus cacheImportFileStatus = new CacheImportFileStatus();
		EffectivePerson currentPerson = this.effectivePerson(request);
		logger.info("[checkDataFile]user[" + currentPerson.getName() + "] try to import detail from file{'id':'"+file_id+"'}......" );
		
		//先查询文件是否存在
		AttendanceImportFileInfo attendanceImportFileInfo = null;
		if( file_id == null || file_id.trim().length() == 0){
			//说明参数文件ID未获取到
			logger.info( "[checkDataFile]需要导入的文件ID为空，无法查询需要导入的文件信息。" );
			cacheImportFileStatus.setCheckStatus( "ERROR" );
			cacheImportFileStatus.setMessage( "需要导入的文件ID为空，无法查询需要导入的文件信息。" );
		}else{
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				attendanceImportFileInfo = emc.find( file_id, AttendanceImportFileInfo.class );
			}catch(Exception e){
				logger.error( "[checkDataFile]系统查询导入文件信息发生异常！", e );
			}
			if( attendanceImportFileInfo == null ){
				logger.info( "[checkDataFile]需要导入的文件信息不存在，无法进行数据导入。" );
				cacheImportFileStatus.setCheckStatus( "ERROR" );
				cacheImportFileStatus.setMessage( "需要导入的文件信息不存在，无法进行数据导入。" );
			}else{
				//将文件到应用服务器形成本地文件
				logger.info( "[checkDataFile]准备将文件保存为本地文件......"  );
				String importFilePath = "./" ;
				String importFileName = "import_" + (new Date()).getTime() + "_" + attendanceImportFileInfo.getFileName();				
				OutputStream output = null;
				try {
					File file = new File( importFilePath + importFileName );
					if( file.exists() ){
						file.delete();
					}
					
					logger.info( "[checkDataFile]删除旧文件，创建新文件......"  );
					file.createNewFile();
					
					try {
						logger.info( "[checkDataFile]准备开始保存文件信息到本地："+importFilePath + importFileName  );
						output = new FileOutputStream( importFilePath + importFileName );
						output.write( attendanceImportFileInfo.getFileBody() );
						output.flush();
						logger.info( "[checkDataFile]保存文件信息到本地成功完成！"  );
					} catch (Exception e) {
						logger.error( "[checkDataFile]get fileInfo{id:" + attendanceImportFileInfo.getId() + ", name:" + attendanceImportFileInfo.getFileName() + "} error.", e);
					} finally{
						logger.info( "[checkDataFile]关闭输出流......"  );
						output.close();
					}
				} catch (Exception e) {
					logger.error( "[checkDataFile]系统进行文件保存到应用服务器时发生异常！", e);
				}
				
				//然后进行数据检查
				logger.info("[checkDataFile]实例化数据检查处理类......");
				IRowReader reader = new AttendancePersonExcelReader();
				logger.info("[checkDataFile]初始化全局检查数据管理缓存对象......");
				
				if ( ApplicationGobal.importFileCheckResultMap ==  null ) {
					 ApplicationGobal.importFileCheckResultMap = new HashMap<String, CacheImportFileStatus>();
				}
				
				if( ApplicationGobal.importFileCheckResultMap.get( file_id ) == null ){
					ApplicationGobal.importFileCheckResultMap.put( file_id, new CacheImportFileStatus() );
				}
				logger.info("[checkDataFile]把导入数据缓存对象置空，准备读取新的数据......");
				ApplicationGobal.importFileCheckResultMap.put( file_id, null );
				logger.info("[checkDataFile]准备开始数据检查过程......");
				try {
					ExcelReaderUtil.readExcel( reader, importFilePath + importFileName, file_id, 1);
				} catch (Exception e) {
					logger.error( "[checkDataFile]系统导入数据文件发生异常，路径：" + importFilePath + importFileName, e);
				}
				logger.debug("[checkDataFile]数据检查完成，准备向客户端返回需要导入的数据检查情况......");
				
				if( ApplicationGobal.importFileCheckResultMap.get( file_id ) != null ){
					logger.info("[checkDataFile]导入数据分析完成......");
					cacheImportFileStatus = ApplicationGobal.importFileCheckResultMap.get( file_id );
					
					CacheImportFileStatus _cacheImportFileStatus = new CacheImportFileStatus();
					List<CacheImportRowDetail> _cacheImportRowDetailList = new ArrayList<CacheImportRowDetail>();
					_cacheImportFileStatus.setCheckStatus( cacheImportFileStatus.getCheckStatus() );
					_cacheImportFileStatus.setErrorCount( cacheImportFileStatus.getErrorCount() );
					_cacheImportFileStatus.setFileId( cacheImportFileStatus.getFileId() );
					_cacheImportFileStatus.setMessage( cacheImportFileStatus.getMessage() );
					_cacheImportFileStatus.setRowCount( cacheImportFileStatus.getRowCount() );
					if( !"success".equalsIgnoreCase( cacheImportFileStatus.getCheckStatus() ) || cacheImportFileStatus.getErrorCount() > 0 ){
						logger.debug("[checkDataFile]导入数据存在错误， 返回20条有错误的数据信息......");
						//如果检查结果有错误，那么响应到浏览器端只返回20条有错误的数据信息
						if( cacheImportFileStatus.getDetailList() != null && cacheImportFileStatus.getDetailList().size() > 0 ){
							for( CacheImportRowDetail cacheImportRowDetail : cacheImportFileStatus.getDetailList() ){
								if( !"success".equalsIgnoreCase(cacheImportRowDetail.getCheckStatus()) ){
									_cacheImportRowDetailList.add( cacheImportRowDetail );
								}
								if( _cacheImportRowDetailList.size() >= 20 ){
									break;
								}
							}
						}
					}else{
						logger.debug("[checkDataFile]导入数据不存在错误， 返回20条数据......");
						//返回20条数据
						if( cacheImportFileStatus.getDetailList() != null && cacheImportFileStatus.getDetailList().size() > 0 ){
							for( CacheImportRowDetail cacheImportRowDetail : cacheImportFileStatus.getDetailList() ){
								_cacheImportRowDetailList.add( cacheImportRowDetail );
								if( _cacheImportRowDetailList.size() >= 20 ){
									break;
								}
							}
						}
					}
					_cacheImportFileStatus.setDetailList( _cacheImportRowDetailList );
					result.setCount( _cacheImportFileStatus.getRowCount() );
					result.setData( _cacheImportFileStatus );
				}else{
					result.setCount( 0L );
					result.setData( cacheImportFileStatus );
				}
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "导入数据文件", response = WrapOutMessage.class)
	@GET
	@Path("import/{file_id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response importDataFile(@Context HttpServletRequest request, @PathParam("file_id") String file_id ) {
		ActionResult<WrapOutMessage> result = new ActionResult<>();
		WrapOutMessage wrapOutMessage = new WrapOutMessage();
		EffectivePerson currentPerson = this.effectivePerson(request);
		logger.info("[importDataFile]user[" + currentPerson.getName() + "] try to import detail from file{'id':'"+file_id+"'}......" );
		
		if ( ApplicationGobal.importFileCheckResultMap ==  null ) {
			ApplicationGobal.importFileCheckResultMap = new HashMap<String, CacheImportFileStatus>();
		}
		
		if( ApplicationGobal.importFileCheckResultMap.get( file_id ) == null ){
			ApplicationGobal.importFileCheckResultMap.put( file_id, new CacheImportFileStatus() );
		}
		logger.info("[importDataFile]从全局数据检查缓存中获取文件[ID="+file_id+"]的检查数据结果......");
		CacheImportFileStatus cacheImportFileStatus = ApplicationGobal.importFileCheckResultMap.get( file_id );
		AttendanceDetail attendanceDetail = null;
		AttendanceDetail _attendanceDetail = null;
		Business business = null;
		List<String> ids = null;
		if( cacheImportFileStatus != null 
				&& "success".equalsIgnoreCase( cacheImportFileStatus.getCheckStatus() )
				&& cacheImportFileStatus.getDetailList() != null 
				&& cacheImportFileStatus.getDetailList().size() > 0 ){			
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				business = new Business(emc);
				//先删除这一个文件ID下的所有记录
				logger.info("[importDataFile]查询batchName = "+file_id+"的所有数据记录......");
				ids = business.getAttendanceDetailFactory().listByBatchName( file_id );
				logger.info("[importDataFile]查询结果"+ids.size()+"条！");
				logger.info("[importDataFile]开始事务，删除batchName = "+file_id+"的"+ids.size()+"条数据记录......");
				emc.beginTransaction( AttendanceDetail.class );
				if( ids != null && ids.size() > 0){
					for( String id : ids ){
						_attendanceDetail = emc.find(id, AttendanceDetail.class);
						emc.remove( _attendanceDetail );
					}
				}
				emc.commit();
				
				logger.info("[importDataFile]开始保存需要导入的数据，新增batchName = "+file_id+"的"+cacheImportFileStatus.getDetailList().size()+"条数据记录......");

				List<DateRecord> dateRecordList = new ArrayList<DateRecord>();
				boolean dateRecordExsist = false;
				
				//对每一条进行检查，如果在不需要考勤的人员名单里，那么就不进行存储。				
				for( CacheImportRowDetail cacheImportRowDetail : cacheImportFileStatus.getDetailList() ){
					logger.info("[importDataFile]查询数据库里是否有重复记录：姓名：" + cacheImportRowDetail.getEmployeeName() + "， 日期：" + cacheImportRowDetail.getRecordDateStringFormated() );
					ids = business.getAttendanceDetailFactory().getByUserAndRecordDate( cacheImportRowDetail.getEmployeeName(), cacheImportRowDetail.getRecordDateStringFormated() );
					if( ids != null && ids.size() > 0 ){
						for( String id : ids){
							logger.info("[importDataFile]查询数据库里是否有重复记录：id=" + id );
							attendanceDetail = emc.find(id, AttendanceDetail.class );
							if( attendanceDetail != null ){
								logger.info("[importDataFile]删除["+cacheImportRowDetail.getEmployeeName()+"]["+cacheImportRowDetail.getRecordDateStringFormated()+"]已存在的打卡数据......");
								emc.beginTransaction( AttendanceDetail.class );
								emc.remove(attendanceDetail);
								emc.commit();
							}
						}
					}
					
					attendanceDetail = new AttendanceDetail();
					attendanceDetail.setEmpNo( cacheImportRowDetail.getEmployeeNo() );
					attendanceDetail.setEmpName( cacheImportRowDetail.getEmployeeName() );
					attendanceDetail.setYearString( cacheImportRowDetail.getRecordYearString());
					attendanceDetail.setMonthString(cacheImportRowDetail.getRecordMonthString());
					attendanceDetail.setRecordDateString( cacheImportRowDetail.getRecordDateStringFormated() );
					attendanceDetail.setOnDutyTime( cacheImportRowDetail.getOnDutyTimeFormated() );
					attendanceDetail.setOffDutyTime( cacheImportRowDetail.getOffDutyTimeFormated() );
					attendanceDetail.setRecordStatus( 0 );
					attendanceDetail.setBatchName( file_id );
					emc.beginTransaction( AttendanceDetail.class );
					emc.persist( attendanceDetail, CheckPersistType.all );
					emc.commit();
					
					dateRecordExsist = false;
					for( DateRecord dateRecord : dateRecordList ){
						if( dateRecord.getYear().equals( cacheImportRowDetail.getRecordYearString() ) && dateRecord.getMonth().equals( cacheImportRowDetail.getRecordMonthString() )
						){
							//说明已经存在了
							dateRecordExsist = true;
						}
					}
					if( !dateRecordExsist ){
						dateRecordList.add( new DateRecord( cacheImportRowDetail.getRecordYearString(), cacheImportRowDetail.getRecordMonthString() ));
					}
					logger.info("[importDataFile]保存["+cacheImportRowDetail.getEmployeeName()+"]["+cacheImportRowDetail.getRecordDateStringFormated()+"]新的打卡数据......");
				}
				
				if( dateRecordList != null ){
					for( DateRecord dateRecord : dateRecordList ){
						logger.info("需要进行统计的年月：" + dateRecord.getYear() + "-" + dateRecord.getMonth());
					}
				}
				wrapOutMessage.setDateRecordList(dateRecordList);				
				logger.info("[importDataFile]数据处理事务提交完成！");
			} catch (Throwable th) {
				wrapOutMessage.setStatus( "ERROR" );
				wrapOutMessage.setMessage( "数据导入出错！" );
			}
			wrapOutMessage.setStatus( "SUCCESS" );
			wrapOutMessage.setMessage( "数据导入完成！" );
			ApplicationGobal.importFileCheckResultMap.remove( file_id );
		}else{
			logger.info("[importDataFile]全局数据检查缓存中文件[ID="+file_id+"]的检查数据结果为空，无法继续导入数据，需要重新进行数据检查......");
			//导入数据为空，需要再检查一次数据文件
			wrapOutMessage.setStatus( "ERROR" );
			wrapOutMessage.setMessage( "导入数据之前请先检查数据文件！" );
		}
		result.setData( wrapOutMessage );
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
}