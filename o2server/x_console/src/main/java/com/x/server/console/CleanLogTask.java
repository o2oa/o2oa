//package com.x.server.console;
//
//import java.io.File;
//import java.io.FileFilter;
//import java.util.Date;
//
//import org.apache.commons.io.FileUtils;
//import org.apache.commons.io.FilenameUtils;
//import org.apache.commons.io.filefilter.RegexFileFilter;
//import org.apache.commons.lang3.StringUtils;
//import org.quartz.Job;
//import org.quartz.JobExecutionContext;
//import org.quartz.JobExecutionException;
//
//import com.x.base.core.project.config.Config;
//import com.x.base.core.project.logger.Logger;
//import com.x.base.core.project.logger.LoggerFactory;
//import com.x.base.core.project.tools.DateTools;
//
//public class CleanLogTask implements Job {
//
//	private static Logger logger = LoggerFactory.getLogger(CleanLogTask.class);
//
//	@Override
//	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
//		try {
//			Date now = new Date();
//			// File dir = new File("d:/o2server", "logs");
//			File dir = new File(Config.base(), "logs");
//			// 2018_05_24.out.log
//			FileFilter fileFilter = new RegexFileFilter("^20[1-9][0-9]_[0-1][0-9]_[0-3][0-9].out.log(.*)$");
//			File[] files = dir.listFiles(fileFilter);
//			for (File f : files) {
//				String name = FilenameUtils.getBaseName(f.getName());
//				String value = StringUtils.substringBefore(name, ".");
//				Date date = DateTools.parse(value, "yyyy_MM_dd");
//				if (now.getTime() - date.getTime() > (1000 * 60 * 60 * 24 * Config.currentNode().logSize())) {
//					logger.print("删除旧的日志文件:{}.", f.getAbsolutePath());
//					FileUtils.forceDelete(f);
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//}