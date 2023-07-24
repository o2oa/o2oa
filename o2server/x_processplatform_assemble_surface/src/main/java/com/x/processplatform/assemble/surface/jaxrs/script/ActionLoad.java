package com.x.processplatform.assemble.surface.jaxrs.script;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Script;

class ActionLoad extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionLoad.class);

    ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag, String applicationFlag,
            JsonElement jsonElement) throws Exception {
        LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);
        ActionResult<Wo> result = new ActionResult<>();
        Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Business business = new Business(emc);
            Application application = business.application().pick(applicationFlag);
            if (null == application) {
                throw new ExceptionApplicationNotExist(applicationFlag);
            }
            List<Script> list = new ArrayList<>();
            for (Script o : business.script().listScriptNestedWithApplicationWithUniqueName(application.getId(),
                    flag)) {
                if ((null != wi) && (null != wi.getImportedList())
                        && (!this.contains(wi.getImportedList(), o.getAlias()))
                        && (!this.contains(wi.getImportedList(), o.getName()))
                        && (!this.contains(wi.getImportedList(), o.getId()))) {
                    list.add(o);
                } else {
                    list.add(o);
                }
            }
            StringBuilder sb = new StringBuilder("");
            List<String> imported = new ArrayList<>();
            for (Script o : list) {
                sb.append(o.getText());
                sb.append(System.lineSeparator());
                imported.add(o.getId());
                if (StringUtils.isNotEmpty(o.getName())) {
                    imported.add(o.getName());
                }
                if (StringUtils.isNotEmpty(o.getAlias())) {
                    imported.add(o.getAlias());
                }
            }
            Wo wo = new Wo();
            wo.setImportedList(imported);
            wo.setText(sb.toString());
            result.setData(wo);
        }
        return result;
    }

    private boolean contains(List<String> list, String value) {
        if (StringUtils.isEmpty(value)) {
            return false;
        } else {
            return ListTools.contains(list, value);
        }
    }

    public class Wi extends GsonPropertyObject {

        @FieldDescribe("导入标识")
        private List<String> importedList;

        public List<String> getImportedList() {
            return importedList;
        }

        public void setImportedList(List<String> importedList) {
            this.importedList = importedList;
        }

    }

    public class Wo extends GsonPropertyObject {

        @FieldDescribe("脚本内容")
        private String text;

        @FieldDescribe("应用脚本")
        private List<String> importedList;

        public List<String> getImportedList() {
            return importedList;
        }

        public void setImportedList(List<String> importedList) {
            this.importedList = importedList;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

    }

}