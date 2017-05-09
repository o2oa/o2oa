package com.x.file.assemble.control.servlet.file;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.DefaultCharset;
import com.x.base.core.application.servlet.AbstractServletAction;
import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.project.server.StorageMapping;
import com.x.file.assemble.control.ThisApplication;
import com.x.file.core.entity.open.File;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

@WebServlet(urlPatterns = "/servlet/file/download/*")
@MultipartConfig
public class ActionFileDownload extends AbstractServletAction {

	private static final long serialVersionUID = 6880758614186306946L;

	private static final String PART_DOWNLOAD = "download";

	private Ehcache cache = ApplicationCache.instance().getCache(File.class);

	@HttpMethodDescribe(value = "获取File对象,url格式为:/servlet/file/download/{id}/stream.")
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			request.setCharacterEncoding(DefaultCharset.name);
			String part = this.getURIPart(request.getRequestURI(), PART_DOWNLOAD);
			String id = StringUtils.substringBefore(part, "/");
			/** 确定是否要用application/octet-stream输出 */
			boolean streamContentType = StringUtils.endsWith(part, "/stream");
			if (StringUtils.isEmpty(id)) {
				throw new IdEmptyException();
			}
			File file = emc.find(id, File.class);
			if (null == file) {
				throw new FileNotExistedException(id);
			}
			this.setResponseHeader(response, file, streamContentType);
			String cacheKey = ApplicationCache.concreteCacheKey(id);
			Element element = cache.get(cacheKey);
			byte[] bs = null;
			if ((null != element) && (null != element.getObjectValue())) {
				bs = (byte[]) element.getObjectValue();
			} else {
				StorageMapping mapping = ThisApplication.context().storageMappings().get(File.class, file.getStorage());
				if (null == mapping) {
					throw new StorageMappingNotExistedException(file.getStorage());
				}
				try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
					file.readContent(mapping, os);
					bs = os.toByteArray();
					/**
					 * 对2M以下的文件进行缓存
					 */
					if (bs.length < (1024 * 1024 * 2)) {
						cache.put(new Element(cacheKey, bs));
					}
				}
			}
			response.getOutputStream().write(bs);
		} catch (Exception e) {
			e.printStackTrace();
			ActionResult<Object> result = new ActionResult<>();
			result.error(e);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().print(result.toJson());
		}
	}

}