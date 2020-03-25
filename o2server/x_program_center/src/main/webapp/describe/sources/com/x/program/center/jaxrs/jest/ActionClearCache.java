package com.x.program.center.jaxrs.jest;

import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.CategoryInfo;
import com.x.cms.core.entity.element.AppDict;
import com.x.cms.core.entity.element.AppDictItem;
import com.x.cms.core.entity.element.Form;
import com.x.cms.core.entity.element.Script;
import com.x.portal.core.entity.Page;
import com.x.portal.core.entity.Portal;
import com.x.portal.core.entity.Widget;
import com.x.processplatform.core.entity.element.*;
import com.x.processplatform.core.entity.element.Process;
import com.x.program.center.core.entity.Agent;
import com.x.query.core.entity.Reveal;
import com.x.query.core.entity.Stat;
import com.x.query.core.entity.View;
import com.x.query.core.entity.schema.Statement;
import com.x.query.core.entity.schema.Table;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;

class ActionClearCache extends BaseAction {

	ActionResult<Wo> execute(HttpServletRequest request, String source) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		wo.setValue(false);
		if(StringUtils.isEmpty(source)){
			wo.setValue(false);
		}else if("all".equalsIgnoreCase(source)){
			//cms
			ApplicationCache.notify(CategoryInfo.class);
			ApplicationCache.notify(AppDictItem.class);
			ApplicationCache.notify(AppDict.class);
			ApplicationCache.notify(Form.class);
			ApplicationCache.notify(Script.class);
			ApplicationCache.notify(AppInfo.class);
			//portal
			ApplicationCache.notify(com.x.portal.core.entity.Script.class);
			ApplicationCache.notify(Page.class);
			ApplicationCache.notify(Widget.class);
			ApplicationCache.notify(Portal.class);
			//query
			ApplicationCache.notify(Reveal.class);
			ApplicationCache.notify(Stat.class);
			ApplicationCache.notify(View.class);
			ApplicationCache.notify(Table.class);
			ApplicationCache.notify(Statement.class);
			//process
			ApplicationCache.notify(ApplicationDictItem.class);
			ApplicationCache.notify(ApplicationDict.class);
			ApplicationCache.notify(FormField.class);
			ApplicationCache.notify(com.x.processplatform.core.entity.element.Form.class);
			ApplicationCache.notify(com.x.processplatform.core.entity.element.Script.class);
			ApplicationCache.notify(Process.class);
			ApplicationCache.notify(Application.class);
			//agent
			ApplicationCache.notify(Agent.class);
			//invoke
			ApplicationCache.notify(Invoke.class);
			wo.setValue(true);
		}else if("cms".equalsIgnoreCase(source)){
			ApplicationCache.notify(CategoryInfo.class);
			ApplicationCache.notify(AppDictItem.class);
			ApplicationCache.notify(AppDict.class);
			ApplicationCache.notify(Form.class);
			ApplicationCache.notify(Script.class);
			ApplicationCache.notify(AppInfo.class);

			wo.setValue(true);
		}else if("portal".equalsIgnoreCase(source)){
			ApplicationCache.notify(com.x.portal.core.entity.Script.class);
			ApplicationCache.notify(Page.class);
			ApplicationCache.notify(Widget.class);
			ApplicationCache.notify(Portal.class);

			wo.setValue(true);
		}else if("query".equalsIgnoreCase(source)){
			ApplicationCache.notify(Reveal.class);
			ApplicationCache.notify(Stat.class);
			ApplicationCache.notify(View.class);
			ApplicationCache.notify(Table.class);
			ApplicationCache.notify(Statement.class);

			wo.setValue(true);
		}else if("process".equalsIgnoreCase(source)){
			ApplicationCache.notify(ApplicationDictItem.class);
			ApplicationCache.notify(ApplicationDict.class);
			ApplicationCache.notify(FormField.class);
			ApplicationCache.notify(com.x.processplatform.core.entity.element.Form.class);
			ApplicationCache.notify(com.x.processplatform.core.entity.element.Script.class);
			ApplicationCache.notify(Process.class);
			ApplicationCache.notify(Application.class);

			wo.setValue(true);
		}else if("agent".equalsIgnoreCase(source)){
			ApplicationCache.notify(Agent.class);

			wo.setValue(true);
		}else if("invoke".equalsIgnoreCase(source)){
			ApplicationCache.notify(Invoke.class);

			wo.setValue(true);
		}
		result.setData(wo);
		return result;
	}

	public static class Wo extends WrapBoolean {

	}

}