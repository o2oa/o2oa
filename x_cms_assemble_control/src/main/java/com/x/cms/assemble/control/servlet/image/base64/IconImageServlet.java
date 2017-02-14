package com.x.cms.assemble.control.servlet.image.base64;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang3.math.NumberUtils;
import org.imgscalr.Scalr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.x.base.core.application.servlet.FileUploadServletTools;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.Document;

@WebServlet(urlPatterns="/servlet/appIcon/upload/*")
@MultipartConfig
public class IconImageServlet extends HttpServlet {

	private static final long serialVersionUID = -516827649716075968L;
	private Logger logger = LoggerFactory.getLogger( IconImageServlet.class );
	
	@HttpMethodDescribe(value = "将上传的附件转换为base64String", response = String.class)
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ActionResult<String> result = new ActionResult<>();
		String wrap = null;
		try {
			String str = FileUploadServletTools.getURIPart( request.getRequestURI(), "size");
			EffectivePerson effectivePerson = FileUploadServletTools.effectivePerson(request);
			String appId = FileUploadServletTools.getURIPart( request.getRequestURI(), "appId" );
			AppInfo appInfo = null;
			Integer size = 0;			
			if (NumberUtils.isNumber(str)) {
				size = Integer.parseInt(str);
			}
			request.setCharacterEncoding("UTF-8");
			if ( !ServletFileUpload.isMultipartContent(request) ) {
				throw new Exception("[IconImageServlet]not multi part request.");
			}
			logger.debug( "系统正在保存并且转换图标......" );
			ServletFileUpload upload = new ServletFileUpload();
			FileItemIterator fileItemIterator = upload.getItemIterator(request);
			while ( fileItemIterator.hasNext() ) {
				FileItemStream item = fileItemIterator.next();
				try (InputStream input = item.openStream()) {
					if (item.isFormField()) {
						/* ignore */
					} else {
						try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
							BufferedImage image = ImageIO.read(input);
							if (size > 0) {
								image = Scalr.resize(image, size);
							}
							ImageIO.write(image, "png", baos);
							wrap = Base64.encodeBase64String(baos.toByteArray());
						}
					}
				}
			}
			logger.debug( "[IconImageServlet]图标转换完成，准备更新到数据库应用信息表里......" );
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				//从数据库里根据documentId查询出文档的信息，只查询出几个属性
				appInfo = emc.fetchAttribute( appId, AppInfo.class, "id");				
				if ( null == appInfo ) {
					throw new Exception("[IconImageServlet]appInfo{id:" + appId + "} not existed.");
				}
				/* 判断用户是否可以上传附件 */
				if (!business.documentAllowSave( request, effectivePerson, appId )) {
					throw new Exception("[IconImageServlet]person{name:" + effectivePerson.getName() + "} access document{id:" + appId + "} was denied.");
				}
				//保存appInfo的图标信息
				emc.beginTransaction( Document.class);
				appInfo = emc.find( appId, AppInfo.class);
				appInfo.setAppIcon(wrap);
				emc.commit();
			}
			result.setData(wrap);
		} catch (Exception e) {
			e.printStackTrace();
			result.error(e);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
		FileUploadServletTools.result(response, result);
	}
}