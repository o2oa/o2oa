package com.x.program.center.jaxrs.jest;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.CategoryInfo;
import com.x.cms.core.entity.element.AppDict;
import com.x.cms.core.entity.element.AppDictItem;
import com.x.cms.core.entity.element.Form;
import com.x.cms.core.entity.element.Script;
import com.x.organization.core.entity.Group;
import com.x.organization.core.entity.Identity;
import com.x.organization.core.entity.Person;
import com.x.organization.core.entity.PersonAttribute;
import com.x.organization.core.entity.Role;
import com.x.organization.core.entity.Unit;
import com.x.organization.core.entity.UnitAttribute;
import com.x.organization.core.entity.UnitDuty;
import com.x.organization.core.entity.accredit.Empower;
import com.x.portal.core.entity.Page;
import com.x.portal.core.entity.Portal;
import com.x.portal.core.entity.Widget;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.ApplicationDict;
import com.x.processplatform.core.entity.element.ApplicationDictItem;
import com.x.processplatform.core.entity.element.FormField;
import com.x.processplatform.core.entity.element.Invoke;
import com.x.processplatform.core.entity.element.Process;
import com.x.program.center.core.entity.Agent;
import com.x.query.core.entity.Stat;
import com.x.query.core.entity.View;
import com.x.query.core.entity.schema.Statement;
import com.x.query.core.entity.schema.Table;

class ActionClearCache extends BaseAction {

    ActionResult<Wo> execute(HttpServletRequest request, String source) throws Exception {
        ActionResult<Wo> result = new ActionResult<>();
        Wo wo = new Wo();
        wo.setValue(false);
        if (StringUtils.isEmpty(source)) {
            wo.setValue(false);
        } else if ("all".equalsIgnoreCase(source)) {
            // cms
            CacheManager.notify(CategoryInfo.class);
            CacheManager.notify(AppDictItem.class);
            CacheManager.notify(AppDict.class);
            CacheManager.notify(Form.class);
            CacheManager.notify(Script.class);
            CacheManager.notify(AppInfo.class);
            // portal
            CacheManager.notify(com.x.portal.core.entity.Script.class);
            CacheManager.notify(Page.class);
            CacheManager.notify(Widget.class);
            CacheManager.notify(Portal.class);
            // query
            CacheManager.notify(Stat.class);
            CacheManager.notify(View.class);
            CacheManager.notify(Table.class);
            CacheManager.notify(Statement.class);
            // process
            CacheManager.notify(ApplicationDictItem.class);
            CacheManager.notify(ApplicationDict.class);
            CacheManager.notify(FormField.class);
            CacheManager.notify(com.x.processplatform.core.entity.element.Form.class);
            CacheManager.notify(com.x.processplatform.core.entity.element.Script.class);
            CacheManager.notify(Process.class);
            CacheManager.notify(Application.class);
            // agent
            CacheManager.notify(Agent.class);
            // invoke
            CacheManager.notify(Invoke.class);
            // org
            CacheManager.notify(Identity.class);
            CacheManager.notify(Unit.class);
            CacheManager.notify(UnitAttribute.class);
            CacheManager.notify(UnitDuty.class);
            CacheManager.notify(Role.class);
            CacheManager.notify(Person.class);
            CacheManager.notify(PersonAttribute.class);
            CacheManager.notify(Group.class);
            CacheManager.notify(Empower.class);
            wo.setValue(true);
        } else if ("cms".equalsIgnoreCase(source)) {
            CacheManager.notify(CategoryInfo.class);
            CacheManager.notify(AppDictItem.class);
            CacheManager.notify(AppDict.class);
            CacheManager.notify(Form.class);
            CacheManager.notify(Script.class);
            CacheManager.notify(AppInfo.class);

            wo.setValue(true);
        } else if ("portal".equalsIgnoreCase(source)) {
            CacheManager.notify(com.x.portal.core.entity.Script.class);
            CacheManager.notify(Page.class);
            CacheManager.notify(Widget.class);
            CacheManager.notify(Portal.class);

            wo.setValue(true);
        } else if ("query".equalsIgnoreCase(source)) {
            CacheManager.notify(Stat.class);
            CacheManager.notify(View.class);
            CacheManager.notify(Table.class);
            CacheManager.notify(Statement.class);

            wo.setValue(true);
        } else if ("process".equalsIgnoreCase(source)) {
            CacheManager.notify(ApplicationDictItem.class);
            CacheManager.notify(ApplicationDict.class);
            CacheManager.notify(FormField.class);
            CacheManager.notify(com.x.processplatform.core.entity.element.Form.class);
            CacheManager.notify(com.x.processplatform.core.entity.element.Script.class);
            CacheManager.notify(Process.class);
            CacheManager.notify(Application.class);

            wo.setValue(true);
        } else if ("agent".equalsIgnoreCase(source)) {
            CacheManager.notify(Agent.class);

            wo.setValue(true);
        } else if ("invoke".equalsIgnoreCase(source)) {
            CacheManager.notify(Invoke.class);

            wo.setValue(true);
        } else if ("org".equalsIgnoreCase(source)) {
            CacheManager.notify(Identity.class);
            CacheManager.notify(Unit.class);
            CacheManager.notify(UnitAttribute.class);
            CacheManager.notify(UnitDuty.class);
            CacheManager.notify(Role.class);
            CacheManager.notify(Person.class);
            CacheManager.notify(PersonAttribute.class);
            CacheManager.notify(Group.class);
            CacheManager.notify(Empower.class);

            wo.setValue(true);
        }
        result.setData(wo);
        return result;
    }

    public static class Wo extends WrapBoolean {

    }

}