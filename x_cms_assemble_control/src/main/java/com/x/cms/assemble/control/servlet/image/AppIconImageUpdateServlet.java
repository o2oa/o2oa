package com.x.cms.assemble.control.servlet.image;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang3.StringUtils;
import org.imgscalr.Scalr;

import com.x.base.core.application.servlet.AbstractServletAction;
import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.cms.common.date.image.ImageUtil;
import com.x.cms.core.entity.AppInfo;

@WebServlet(urlPatterns="/servlet/appinfo/*")
@MultipartConfig
public class AppIconImageUpdateServlet extends AbstractServletAction {

	private static final long serialVersionUID = -516827649716075968L;
	
	@HttpMethodDescribe(value = "更新AppInfo中的icon图标: /servlet/appinfo/{id}/icon", response = WrapOutId.class)
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ActionResult<WrapOutId> result = new ActionResult<>();
		ImageUtil imageUtil = new ImageUtil();
		List<String> colorList = null;
		String iconMainColor = null;
		WrapOutId wrap = null;
		try {
			request.setCharacterEncoding("UTF-8");
			if (!ServletFileUpload.isMultipartContent(request)) {
				throw new Exception("not mulit part request.");
			}
			String part = this.getURIPart( request.getRequestURI(), "appinfo" );
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
								
								//先取图片主色调
								colorList = imageUtil.getColorSolution( image, 30, 1);
								if( colorList != null && !colorList.isEmpty() ){
									iconMainColor = colorList.get(0);
								}
								
								ImageIO.write(scalrImage, "png", output);
								String icon = Base64.encodeBase64String(output.toByteArray());
								
								emc.beginTransaction(AppInfo.class);
								
								appInfo.setAppIcon(icon);
								appInfo.setIconColor(iconMainColor);
								emc.check( appInfo, CheckPersistType.all );
								emc.commit();
								
								wrap = new WrapOutId(appInfo.getId());
								result.setData(wrap);

								ApplicationCache.notify( AppInfo.class );
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
		this.result(response, result);
	}
}