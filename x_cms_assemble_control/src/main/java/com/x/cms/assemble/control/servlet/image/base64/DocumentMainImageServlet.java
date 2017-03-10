package com.x.cms.assemble.control.servlet.image.base64;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.application.servlet.AbstractServletAction;
import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.cms.assemble.control.service.DocumentInfoServiceAdv;
import com.x.cms.core.entity.DocumentPictureInfo;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

@WebServlet(urlPatterns = "/servlet/document/*")
@MultipartConfig
public class DocumentMainImageServlet extends AbstractServletAction {

	private static final long serialVersionUID = -516827649716075968L;
	private Logger logger = LoggerFactory.getLogger(DocumentMainImageServlet.class);
	private DocumentInfoServiceAdv documentInfoServiceAdv = new DocumentInfoServiceAdv();
	
	
	@HttpMethodDescribe(value = "直接获取指定文档的第一个大图base64编码内容, x_cms_assemble_control/servlet/document/{id}/mainpic", response = String.class)
	protected void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
		
		List<DocumentPictureInfo> pictures = null;
		String part = null;
		String docId = null;
		Boolean check = true;
		String base64 = null;
		EffectivePerson effectivePerson = null;
		Ehcache cache = ApplicationCache.instance().getCache( DocumentPictureInfo.class);
		
		String cacheKey = ApplicationCache.concreteCacheKey( "document", docId, "base64" );
		Element element = cache.get(cacheKey);
		
		if ((null != element) && ( null != element.getObjectValue()) ) {
			base64 = ( String ) element.getObjectValue();
		} else {
			if (check) {
				try {
					effectivePerson = this.effectivePerson(request);
				} catch (Exception e) {
					check = false;
					logger.error( e, effectivePerson, request, null);
				}
			}
			if (check) {
				try {
					part = this.getURIPart(request.getRequestURI(), "document");
					docId = StringUtils.substringBefore(part, "/"); // 附件的ID
				} catch (Exception e) {
					check = false;
					logger.error( e, effectivePerson, request, null);
				}
			}
			
			if (check) {
				try {
					pictures = documentInfoServiceAdv.listMainPictureByDocId( docId );
					if( pictures != null && !pictures.isEmpty() ){
						base64 = pictures.get(0).getBase64();
						
					}else{
						base64 = "0";
					}
				} catch (Exception e) {
					logger.error( e, effectivePerson, request, null);
				}
			}
			response.getWriter().write( base64 ); 
		}
	}
}