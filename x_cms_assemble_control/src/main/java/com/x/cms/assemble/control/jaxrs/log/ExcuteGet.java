package com.x.cms.assemble.control.jaxrs.log;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.Log;

public class ExcuteGet extends ExcuteBase {
	
	protected ActionResult<WrapOutLog> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<WrapOutLog> result = new ActionResult<>();
		WrapOutLog wrap = null;
		
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Log log = business.getLogFactory().get(id);
			if ( null == log ) {
				throw new Exception( "log{id:" + id + "} 信息不存在." );
			}
			// 如果信息存在，则需要向客户端返回信息，先将查询出来的JPA对象COPY到一个普通JAVA对象里，再进行返回
			BeanCopyTools<Log, WrapOutLog> copier = BeanCopyToolsBuilder.create( Log.class, WrapOutLog.class, null, WrapOutLog.Excludes);
			wrap = new WrapOutLog();
			copier.copy(log, wrap);
			result.setData(wrap);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return result;
	}
	
}