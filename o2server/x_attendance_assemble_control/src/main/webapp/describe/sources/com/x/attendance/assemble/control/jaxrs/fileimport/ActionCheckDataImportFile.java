package com.x.attendance.assemble.control.jaxrs.fileimport;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import com.x.attendance.assemble.common.excel.reader.ExcelReaderUtil;
import com.x.attendance.assemble.common.excel.reader.IRowReader;
import com.x.attendance.assemble.common.excel.reader.ImportExcelReader;
import com.x.attendance.assemble.control.processor.monitor.StatusImportFileDetail;
import com.x.attendance.assemble.control.processor.monitor.StatusSystemImportOpt;
import com.x.attendance.entity.AttendanceImportFileInfo;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class ActionCheckDataImportFile extends BaseAction {
	
	private static  Logger logger = LoggerFactory.getLogger(ActionCheckDataImportFile.class);
	
	protected ActionResult<StatusImportFileDetail> execute( HttpServletRequest request, EffectivePerson effectivePerson, String file_id ) throws Exception {
		ActionResult<StatusImportFileDetail> result = new ActionResult<>();
		StatusImportFileDetail cacheImportFileStatus = new StatusImportFileDetail();
		EffectivePerson currentPerson = this.effectivePerson(request);

		// 先查询文件是否存在
		AttendanceImportFileInfo attendanceImportFileInfo = null;
		if ( file_id == null || file_id.trim().length() == 0 ) {
			// 说明参数文件ID未获取到
			logger.info("需要导入的文件ID为空，无法查询需要导入的文件信息。");
			cacheImportFileStatus.setCheckStatus("ERROR");
			cacheImportFileStatus.setMessage("需要导入的文件ID为空，无法查询需要导入的文件信息。");
		} else {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				attendanceImportFileInfo = emc.find(file_id, AttendanceImportFileInfo.class);
			} catch (Exception e) {
				Exception exception = new ExceptionFileImportProcess(e, "根据指定ID查询导入文件信息时发生异常.ID:" + file_id);
				result.error(exception);
				logger.error(e, currentPerson, request, null);
			}
			if (attendanceImportFileInfo == null) {
				logger.info("需要导入的文件信息不存在，无法进行数据导入。");
				cacheImportFileStatus.setCheckStatus("ERROR");
				cacheImportFileStatus.setMessage("需要导入的文件信息不存在，无法进行数据导入。");
			} else {
				// 将文件到应用服务器形成本地文件
				logger.info("准备将文件保存为本地文件......");
				String importFilePath = "./";
				String importFileName = "import_" + (new Date()).getTime() + "_" + attendanceImportFileInfo.getFileName();
				OutputStream output = null;
				try {
					File file = new File(importFilePath + importFileName);
					if (file.exists()) {
						file.delete();
					}
					logger.info("删除旧文件，创建新文件......");
					file.createNewFile();
					try {
						logger.info("准备开始保存文件信息到本地：" + importFilePath + importFileName);
						output = new FileOutputStream(importFilePath + importFileName);
						output.write(attendanceImportFileInfo.getFileBody());
						output.flush();
						logger.info("保存文件信息到本地成功完成！");
					} catch (Exception e) {
						Exception exception = new ExceptionFileImportProcess(e, "将文件写入到本地文件时发生异常.ID:" + attendanceImportFileInfo.getId() + ", FileName:" + attendanceImportFileInfo.getFileName() );
						result.error(exception);
						logger.error(e, currentPerson, request, null);
					} finally {
						logger.info("关闭输出流......");
						output.close();
					}
				} catch (Exception e) {
					Exception exception = new ExceptionFileImportProcess(e, "将文件写入到本地文件时发生异常.");
					result.error(exception);
					logger.error(e, currentPerson, request, null);
				}

				// 然后进行数据检查
				logger.info("实例化数据检查处理类......");
				IRowReader reader = new ImportExcelReader();
				logger.info("初始化全局检查数据管理缓存对象......");
				
				logger.info("把导入数据缓存对象置空，准备读取新的数据......");
				StatusSystemImportOpt.getInstance().cleanCacheImportFileStatus( file_id );
				
				logger.info("准备开始数据检查过程......");
				try {
					ExcelReaderUtil.readExcel( reader, importFilePath + importFileName, file_id, 1);
				} catch (Exception e) {
					Exception exception = new ExceptionFileImportProcess(e,  "解析本地Excle文件时发生异常.ID:" + file_id + ", FileName:" + importFilePath + importFileName );
					result.error(exception);
					logger.error(e, currentPerson, request, null);
				}
			}
		}
		return result;
	}
}