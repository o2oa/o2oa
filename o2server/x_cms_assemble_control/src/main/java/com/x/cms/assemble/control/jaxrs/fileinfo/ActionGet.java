package com.x.cms.assemble.control.jaxrs.fileinfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.cache.Cache;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.cms.assemble.control.jaxrs.comment.ActionListPrevWithFilter;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.FileInfo;

import net.sf.ehcache.Element;

public class ActionGet extends BaseAction {
	
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id, String documentId ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wrap = null;
		List<String> attachmentIds = null;

		Cache.CacheKey cacheKey = new Cache.CacheKey( this.getClass(), id );
		Optional<?> optional = CacheManager.get(cacheCategory, cacheKey );

		if (optional.isPresent()) {
			wrap = ( Wo ) optional.get();
			result.setData(wrap);
		} else {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				//先查询附件是否该文档里的附件，从属关系是否正常
				Document document = emc.find( documentId, Document.class);
				if (null == document) {
					throw new Exception("document{id:" + documentId + "} not existed.");
				}
				attachmentIds = fileInfoServiceAdv.listIdsWithDocId(documentId);				
				if ( attachmentIds == null || !attachmentIds.contains(id)) {
					throw new Exception("document{id" + documentId + "} not contian attachment{id:" + id + "}.");
				}
				FileInfo fileInfo = emc.find(id, FileInfo.class);
				if ( null == fileInfo ) {
					throw new Exception("需要查询的附件或者文件信息不存在，请联系管理员。ID:" + id );
				}
				//如果信息存在，则需要向客户端返回信息，先将查询出来的JPA对象COPY到一个普通JAVA对象里，再进行返回
				wrap = Wo.copier.copy( fileInfo );
				CacheManager.put(cacheCategory, cacheKey, wrap );
				result.setData(wrap);
			} catch (Throwable th) {
				th.printStackTrace();
				result.error(th);
			}
		}		
		return result;
	}

	public static class Wo extends FileInfo {
		
		private static final long serialVersionUID = -5076990764713538973L;
		
		public static List<String> Excludes = new ArrayList<String>();
		
		public static WrapCopier<FileInfo, Wo> copier = WrapCopierFactory.wo( FileInfo.class, Wo.class, null, JpaObject.FieldsInvisible);
	}
}