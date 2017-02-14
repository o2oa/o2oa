package com.x.cms.assemble.control.servlet.image;

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
import org.apache.commons.lang3.StringUtils;
import org.imgscalr.Scalr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.x.base.core.application.servlet.FileUploadServletTools;
import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.cms.core.entity.AppInfo;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

@WebServlet(urlPatterns="/servlet/appinfo/*")
@MultipartConfig
public class AppInfoIconServlet extends HttpServlet {

	private static final long serialVersionUID = -516827649716075968L;
	private Logger logger = LoggerFactory.getLogger( AppInfoIconServlet.class );
	private Ehcache cache = ApplicationCache.instance().getCache( AppInfo.class );
	
	@HttpMethodDescribe(value = "更新AppInfo中的icon图标: /servlet/appinfo/{id}/icon", response = WrapOutId.class)
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapOutId wrap = null;
		try {
			request.setCharacterEncoding("UTF-8");
			if (!ServletFileUpload.isMultipartContent(request)) {
				throw new Exception("not mulit part request.");
			}
			EffectivePerson effectivePerson = FileUploadServletTools.effectivePerson(request);
			String part = FileUploadServletTools.getURIPart( request.getRequestURI(), "appinfo" );
			String appId = StringUtils.substringBefore( part, "/icon" );
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				AppInfo appInfo = emc.find( appId, AppInfo.class );
				if (null == appInfo) {
					throw new Exception("[AppInfoIconServlet]appInfo{id:" + appId + "} not existed.");
				}
				FileItemIterator fileItemIterator = new ServletFileUpload().getItemIterator(request);
				while (fileItemIterator.hasNext()) {
					FileItemStream item = fileItemIterator.next();
					try (InputStream input = item.openStream()) {
						if (item.isFormField()) {
							/* ignore */
						} else {
							try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
								BufferedImage image = ImageIO.read(input);
								BufferedImage scalrImage = Scalr.resize(image, 72, 72);
								ImageIO.write(scalrImage, "png", output);
								String icon = Base64.encodeBase64String(output.toByteArray());
								emc.beginTransaction(AppInfo.class);
								appInfo.setAppIcon(icon);
								emc.commit();
								wrap = new WrapOutId(appInfo.getId());
								result.setData(wrap);
							
								//将图标内容放进缓存里
								String cacheKey = "getIcon." + appInfo.getId() + "#";
								cache.put( new Element( cacheKey, icon ) );
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.error(e);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
		FileUploadServletTools.result(response, result);
	}
}