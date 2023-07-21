package com.x.cms.assemble.control.jaxrs.log;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.SortTools;
import com.x.cms.assemble.control.Business;
import com.x.cms.assemble.control.factory.LogFactory;
import com.x.cms.core.entity.Log;

public class ActionListByLevel extends BaseAction {

	protected ActionResult<List<Wo>> execute( HttpServletRequest request, EffectivePerson effectivePerson, String operationLevel ) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wraps = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			LogFactory logFactory = business.getLogFactory();
			List<String> ids = logFactory.listByOperationLevel(operationLevel);// 获取指定文档的操作日志列表
			List<Log> logList = emc.list( Log.class , ids );// 查询ID IN ids 的所有应用日志信息列表
			if( logList != null && !logList.isEmpty() ){
				wraps = Wo.copier.copy( logList );
				SortTools.desc( wraps, "createTime" );// 对查询的列表进行排序
				result.setData(wraps);
			}
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return result;
	}

	public static class Wo extends Log{

		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> Excludes = new ArrayList<String>();

		public static final WrapCopier<Log, Wo> copier = WrapCopierFactory.wo( Log.class, Wo.class, null, JpaObject.FieldsInvisible);
	}
}
