package com.x.cms.assemble.control.jaxrs.document;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.DocumentPictureInfo;

public class ExcutePictureDelete extends ExcuteBase {
	
	protected ActionResult<WrapOutId> execute( HttpServletRequest request, String id, EffectivePerson effectivePerson ) throws Exception {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapOutId wrap = null;
		DocumentPictureInfo picture = null;
		
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business( emc );
			//先判断需要操作的文档信息是否存在，根据ID进行一次查询，如果不存在不允许继续操作
			picture = business.documentPictureInfoFactory().get( id );
			
			if ( null != picture ) {
				//进行数据库持久化操作
				emc.beginTransaction( DocumentPictureInfo.class );
				//删除文档信息
				emc.remove( picture, CheckRemoveType.all );
				emc.commit();
				wrap = new WrapOutId( picture.getId() );
				result.setData(wrap);
				
				ApplicationCache.notify( DocumentPictureInfo.class );
			}
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return result;
	}

}