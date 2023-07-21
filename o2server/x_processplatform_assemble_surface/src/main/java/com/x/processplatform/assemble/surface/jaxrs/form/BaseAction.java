package com.x.processplatform.assemble.surface.jaxrs.form;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.cache.Cache.CacheCategory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.jaxrs.WoMaxAgeFastETag;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.WorkCompletedProperties;
import com.x.processplatform.core.entity.content.WorkCompletedProperties.RelatedForm;
import com.x.processplatform.core.entity.content.WorkCompletedProperties.RelatedScript;
import com.x.processplatform.core.entity.element.Form;
import com.x.processplatform.core.entity.element.Script;

abstract class BaseAction extends StandardJaxrsAction {

    CacheCategory cacheCategory = new CacheCategory(Form.class, Script.class, com.x.portal.core.entity.Script.class,
            com.x.cms.core.entity.element.Script.class, com.x.program.center.core.entity.Script.class);

    public static class AbstractWo extends WoMaxAgeFastETag {

        private static final long serialVersionUID = 9043017746047829883L;

        private RelatedForm form;

        private Map<String, RelatedForm> relatedFormMap = new HashMap<>();

        private Map<String, RelatedScript> relatedScriptMap = new HashMap<>();

        public RelatedForm getForm() {
            return form;
        }

        public void setForm(RelatedForm form) {
            this.form = form;
        }

        public Map<String, RelatedForm> getRelatedFormMap() {
            return relatedFormMap;
        }

        public void setRelatedFormMap(Map<String, RelatedForm> relatedFormMap) {
            this.relatedFormMap = relatedFormMap;
        }

        public Map<String, RelatedScript> getRelatedScriptMap() {
            return relatedScriptMap;
        }

        public void setRelatedScriptMap(Map<String, RelatedScript> relatedScriptMap) {
            this.relatedScriptMap = relatedScriptMap;
        }

    }

    protected List<String> convertScriptToCacheTag(Business business, Map<String, String> map) throws Exception {
        List<String> list = new ArrayList<>();
        for (Entry<String, String> entry : map.entrySet()) {
            switch (entry.getValue()) {
                case WorkCompletedProperties.RelatedScript.TYPE_PROCESSPLATFORM:
                    Script pp = business.script().pick(entry.getKey());
                    if (null != pp) {
                        list.add(pp.getId() + pp.getUpdateTime().getTime());
                    }
                    break;
                case WorkCompletedProperties.RelatedScript.TYPE_CMS:
                    com.x.cms.core.entity.element.Script cms = business.cms().script().pick(entry.getKey());
                    if (null != cms) {
                        list.add(cms.getId() + cms.getUpdateTime().getTime());
                    }
                    break;
                case WorkCompletedProperties.RelatedScript.TYPE_PORTAL:
                    com.x.portal.core.entity.Script p = business.portal().script().pick(entry.getKey());
                    if (null != p) {
                        list.add(p.getId() + p.getUpdateTime().getTime());
                    }
                    break;
                case WorkCompletedProperties.RelatedScript.TYPE_SERVICE:
                    com.x.program.center.core.entity.Script cs = business.centerService().script().pick(entry.getKey());
                    if (null != cs) {
                        list.add(cs.getId() + cs.getUpdateTime().getTime());
                    }
                    break;
                default:
                    break;
            }
        }
        return list;
    }

    /**
     * 读入流程脚本,嵌套获取合并成文本
     * 
     * @param business
     * @param script
     * @return
     * @throws Exception
     */
    protected String processPlatformScriptLoad(Business business,
            com.x.processplatform.core.entity.element.Script script)
            throws Exception {
        List<com.x.processplatform.core.entity.element.Script> list = new ArrayList<>();
        for (com.x.processplatform.core.entity.element.Script o : business.script()
                .listScriptNestedWithApplicationWithUniqueName(script.getApplication(),
                        script.getId())) {
            list.add(o);
        }
        StringBuilder sb = new StringBuilder("");
        List<String> imported = new ArrayList<>();
        for (com.x.processplatform.core.entity.element.Script o : list) {
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
        return sb.toString();
    }

    /**
     * 读入内容脚本,嵌套获取合并成文本
     * 
     * @param business
     * @param script
     * @return
     * @throws Exception
     */
    protected String cmsScriptLoad(Business business, com.x.cms.core.entity.element.Script script)
            throws Exception {
        List<com.x.cms.core.entity.element.Script> list = new ArrayList<>();
        for (com.x.cms.core.entity.element.Script o : business.cms().script().listScriptNestedWithAppInfoWithUniqueName(
                script.getAppId(),
                script.getId())) {
            list.add(o);
        }
        StringBuilder sb = new StringBuilder("");
        List<String> imported = new ArrayList<>();
        for (com.x.cms.core.entity.element.Script o : list) {
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
        return sb.toString();
    }

    /**
     * 读入门户脚本,嵌套获取合并成文本
     * 
     * @param business
     * @param script
     * @return
     * @throws Exception
     */
    protected String portalScriptLoad(Business business, com.x.portal.core.entity.Script script)
            throws Exception {
        List<com.x.portal.core.entity.Script> list = new ArrayList<>();
        for (com.x.portal.core.entity.Script o : business.portal().script().listScriptNestedWithPortalWithFlag(
                script.getPortal(),
                script.getId())) {
            list.add(o);
        }
        StringBuilder sb = new StringBuilder("");
        List<String> imported = new ArrayList<>();
        for (com.x.portal.core.entity.Script o : list) {
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
        return sb.toString();
    }

    /**
     * 读入服务脚本,嵌套获取合并成文本
     * 
     * @param business
     * @param script
     * @return
     * @throws Exception
     */
    protected String serviceScriptLoad(Business business, com.x.program.center.core.entity.Script script)
            throws Exception {
        List<com.x.program.center.core.entity.Script> list = new ArrayList<>();
        for (com.x.program.center.core.entity.Script o : business.centerService().script()
                .listScriptNestedWithFlag(script.getId())) {
            list.add(o);
        }
        StringBuilder sb = new StringBuilder("");
        List<String> imported = new ArrayList<>();
        for (com.x.program.center.core.entity.Script o : list) {
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
        return sb.toString();
    }

}
