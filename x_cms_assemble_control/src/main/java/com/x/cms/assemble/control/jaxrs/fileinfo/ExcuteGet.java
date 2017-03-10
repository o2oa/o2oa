package com.x.cms.assemble.control.jaxrs.fileinfo;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.cms.assemble.control.WrapTools;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.FileInfo;

import net.sf.ehcache.Element;

public class ExcuteGet extends ExcuteBase {
	
	protected ActionResult<WrapOutFileInfo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id, String documentId ) throws Exception {
		ActionResult<WrapOutFileInfo> result = new ActionResult<>();
		WrapOutFileInfo wrap = null;
		String cacheKey = ApplicationCache.concreteCacheKey( id );
		Element element = cache.get(cacheKey);
		
		if ((null != element) && ( null != element.getObjectValue()) ) {
			wrap = ( WrapOutFileInfo ) element.getObjectValue();
			result.setData(wrap);
		} else {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				//先查询附件是否该文档里的附件，从属关系是否正常
				Document document = emc.find( documentId, Document.class);
				if (null == document) {
					throw new Exception("document{id:" + documentId + "} not existed.");
				}
				if (!document.getAttachmentList().contains(id)) {
					throw new Exception("document{id" + documentId + "} not contian attachment{id:" + id + "}.");
				}
				FileInfo fileInfo = emc.find(id, FileInfo.class);
				if ( null == fileInfo ) {
					throw new Exception("[get]fileInfo{id:" + id + "} 信息不存在.");
				}
				//如果信息存在，则需要向客户端返回信息，先将查询出来的JPA对象COPY到一个普通JAVA对象里，再进行返回
				wrap = new WrapOutFileInfo();
				WrapTools.file_wrapout_copier.copy(fileInfo, wrap);
				cache.put(new Element( cacheKey, wrap ));
				result.setData(wrap);
			} catch (Throwable th) {
				th.printStackTrace();
				result.error(th);
			}
		}		
		return result;
	}

}