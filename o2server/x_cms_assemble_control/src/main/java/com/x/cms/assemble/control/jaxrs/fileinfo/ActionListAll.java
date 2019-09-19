package com.x.cms.assemble.control.jaxrs.fileinfo;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.SortTools;
import com.x.cms.assemble.control.Business;
import com.x.cms.assemble.control.factory.FileInfoFactory;
import com.x.cms.core.entity.FileInfo;

import net.sf.ehcache.Element;

public class ActionListAll extends BaseAction {
	
	@SuppressWarnings("unchecked")
	protected ActionResult<List<Wo>> execute( HttpServletRequest request, EffectivePerson effectivePerson ) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wraps = null;
		String cacheKey = ApplicationCache.concreteCacheKey( "file.all" );
		Element element = cache.get(cacheKey);
		
		if ((null != element) && ( null != element.getObjectValue()) ) {
			wraps = ( List<Wo> ) element.getObjectValue();
			result.setData(wraps);
		} else {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {			
				Business business = new Business(emc);			
				//如判断用户是否有查看所有文件或者附件的权限，如果没权限不允许继续操作
				if (!business.fileInfoEditAvailable( request, effectivePerson )) {
					throw new Exception("person{name:" + effectivePerson.getDistinguishedName() + "} 用户没有查询全部文件或者附件的权限！");
				}			
				//如果有权限，继续操作
				FileInfoFactory fileInfoFactory  = business.getFileInfoFactory();
				List<String> ids = fileInfoFactory.listAll();//获取所有文件或者附件列表
				List<FileInfo> fileInfoList = emc.list( FileInfo.class, ids );//查询ID IN ids 的所有文件或者附件信息列表
				wraps = Wo.copier.copy( fileInfoList );//将所有查询出来的有状态的对象转换为可以输出的过滤过属性的对象
				SortTools.asc( wraps, "sequence" );
				cache.put(new Element( cacheKey, wraps ));
				result.setData(wraps);
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
		
		public static WrapCopier<FileInfo, Wo> copier = WrapCopierFactory.wo( FileInfo.class, Wo.class, null,JpaObject.FieldsInvisible);
	}
}