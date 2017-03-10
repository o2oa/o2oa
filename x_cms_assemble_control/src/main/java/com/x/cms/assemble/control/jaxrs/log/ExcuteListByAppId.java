package com.x.cms.assemble.control.jaxrs.log;

import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.cms.assemble.control.Business;
import com.x.cms.assemble.control.factory.LogFactory;
import com.x.cms.core.entity.Log;

public class ExcuteListByAppId extends ExcuteBase {
	
	protected ActionResult<List<WrapOutLog>> execute( HttpServletRequest request, EffectivePerson effectivePerson, String appId ) throws Exception {
		ActionResult<List<WrapOutLog>> result = new ActionResult<>();
		List<WrapOutLog> wraps = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			LogFactory logFactory = business.getLogFactory();
			List<String> ids = logFactory.listByObject( appId, null, null, null);// 获取指定应用的操作日志列表
			List<Log> logList = logFactory.list(ids);// 查询ID IN ids 的所有应用日志信息列表
			wraps = copier.copy(logList);// 将所有查询出来的有状态的对象转换为可以输出的过滤过属性的对象
			Collections.sort(wraps);// 对查询的列表进行排序
			result.setData(wraps);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return result;
	}
	
}