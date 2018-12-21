package com.x.report.assemble.control.jaxrs.export;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoFile;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

import net.sf.ehcache.Element;

/**
 * 下载导出文件
 * 
 * @author O2LEE
 */
public class ActionDownLoadExportFile extends BaseAction {
	
	private static Logger logger = LoggerFactory.getLogger(ActionDownLoadExportFile.class);
	
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson,  String id  ) {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = null;
		
		String cacheKey = ApplicationCache.concreteCacheKey( "ActionExportForUnitReport-" + id );
		Element element = cache.get( cacheKey );
		if (( null != element ) && ( null != element.getObjectValue()) ) {
			WrapFileInfo wrapFileInfo = ( WrapFileInfo ) element.getObjectValue();
			try {
				wo = new Wo( wrapFileInfo.getBytes(),  
						this.contentType( false, wrapFileInfo.getTitle() ),  
						this.contentDisposition( false, wrapFileInfo.getTitle() )
				);
			} catch (Exception e) {
				logger.warn("system export file got an exception");
				logger.error( e, effectivePerson, request, null);
			}
		}
		result.setData( wo );
		return result;
	}
	
	
	
	public static class Wo extends WoFile {
		public Wo(byte[] bytes, String contentType, String contentDisposition) {
			super(bytes, contentType, contentDisposition);
		}
	}
}
