package com.x.okr.assemble.control.servlet.export;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.application.servlet.AbstractServletAction;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;

@WebServlet(urlPatterns = "/servlet/export/statisticreportcontent/*")
public class WeekReportContentExportServlet extends AbstractServletAction {

	private static final long serialVersionUID = -4314532091497625540L;
	private Logger logger = LoggerFactory.getLogger(WeekReportContentExportServlet.class);

	@HttpMethodDescribe( value = "导出统计表 servlet/export/statisticreportcontent/{flag}/stream", response = Object.class)
	protected void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
		ActionResult<Object> result = new ActionResult<>();
		String part = null;
		String flag = null;
		Boolean check = true;

		request.setCharacterEncoding("UTF-8");

		// 获取文件ID
		if (check) {
			try {
				part = this.getURIPart(request.getRequestURI(), "statisticreportcontent");
				flag = StringUtils.substringBefore(part, "/");
			} catch (Exception e) {
				check = false;
				result.error(e);
				logger.warn("system get id from request url got an exception." ); 
				logger.error(e);
			}
		}
		
		OutputStream out = null;
		if (check) {
			File dir = new File("download/temp");
			if( !dir.exists() ){
				dir.mkdirs();
			}
			File file = new File("download/temp/export_"+flag+".xls");
			if( file.exists() ){
				BufferedInputStream br = new BufferedInputStream(new FileInputStream(file));
				byte[] buf = new byte[1024];
				int len = 0;
				response.reset(); //非常重要
				response.setContentType("application/x-msdownload");
				response.setHeader("Content-Type", "application/octet-stream");
				response.setHeader("Content-Disposition", "fileInfo; filename=" + URLEncoder.encode("汇报内容统计.xls", "utf-8"));
				
				out = response.getOutputStream();
				try{
					while ((len = br.read(buf)) > 0){
						out.write(buf, 0, len);
					}
				}catch(Exception e){
					logger.warn( "system export file got an exception" );
					logger.error(e);
				}finally{
					br.close();
					out.close();
		            out.flush(); 
		            if( file.exists() ){
		            	 file.delete();
		            }
				}
			}
		}
	}
}