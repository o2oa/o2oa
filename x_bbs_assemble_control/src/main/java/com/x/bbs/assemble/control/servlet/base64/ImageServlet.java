package com.x.bbs.assemble.control.servlet.base64;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

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
import org.apache.commons.lang3.math.NumberUtils;
import org.imgscalr.Scalr;

import com.x.base.core.application.servlet.AbstractServletAction;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.annotation.HttpMethodDescribe;

@WebServlet(urlPatterns = "/servlet/image/encode/*")
@MultipartConfig
public class ImageServlet extends AbstractServletAction {

	private static final long serialVersionUID = -516827649716075968L;

	@HttpMethodDescribe(value = "将上传的图片转换为base64String, x_bbs_assemble_control/servlet/image/encode/base64/size/{size}", response = String.class)
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		ActionResult<String> result = new ActionResult<>();
		String str = null;
		String wrap = null;
		try {
			str = this.getURIPart(request.getRequestURI(), "size");
			Integer size = 0;
			if (NumberUtils.isNumber(str)) {
				size = Integer.parseInt(str);
			}
			request.setCharacterEncoding("UTF-8");
			if (!ServletFileUpload.isMultipartContent(request)) {
				throw new Exception("[ImageServlet]not multi part request.");
			}
			ServletFileUpload upload = new ServletFileUpload();
			FileItemIterator fileItemIterator = upload.getItemIterator(request);
			while (fileItemIterator.hasNext()) {
				FileItemStream item = fileItemIterator.next();
				try (InputStream input = item.openStream()) {
					if (item.isFormField()) {
					} else {
						try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
							BufferedImage image = ImageIO.read(input);
							int width = image.getWidth();
							int height = image.getHeight();
							if (size > 0) {
								if (width * height > size * size) {
									image = Scalr.resize(image, size);
								}
							}
							ImageIO.write(image, "png", baos);
							byte[] byteArray = baos.toByteArray();
							wrap = Base64.encodeBase64String(byteArray);
						}
					}
				}
			}
			result.setData(wrap);
		} catch (Exception e) {
			e.printStackTrace();
			result.error(e);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
		this.result(response, result);
	}
}