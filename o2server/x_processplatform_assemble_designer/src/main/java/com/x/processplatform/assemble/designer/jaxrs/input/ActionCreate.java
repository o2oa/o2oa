package com.x.processplatform.assemble.designer.jaxrs.input;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.dataitem.DataItemConverter;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.core.entity.element.Agent;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.ApplicationDict;
import com.x.processplatform.core.entity.element.ApplicationDictItem;
import com.x.processplatform.core.entity.element.Begin;
import com.x.processplatform.core.entity.element.Cancel;
import com.x.processplatform.core.entity.element.Choice;
import com.x.processplatform.core.entity.element.Delay;
import com.x.processplatform.core.entity.element.Embed;
import com.x.processplatform.core.entity.element.End;
import com.x.processplatform.core.entity.element.File;
import com.x.processplatform.core.entity.element.Form;
import com.x.processplatform.core.entity.element.FormField;
import com.x.processplatform.core.entity.element.Invoke;
import com.x.processplatform.core.entity.element.Manual;
import com.x.processplatform.core.entity.element.Merge;
import com.x.processplatform.core.entity.element.Parallel;
import com.x.processplatform.core.entity.element.Process;
import com.x.processplatform.core.entity.element.Publish;
import com.x.processplatform.core.entity.element.Route;
import com.x.processplatform.core.entity.element.Script;
import com.x.processplatform.core.entity.element.Service;
import com.x.processplatform.core.entity.element.Split;
import com.x.processplatform.core.entity.element.wrap.WrapAgent;
import com.x.processplatform.core.entity.element.wrap.WrapApplicationDict;
import com.x.processplatform.core.entity.element.wrap.WrapBegin;
import com.x.processplatform.core.entity.element.wrap.WrapCancel;
import com.x.processplatform.core.entity.element.wrap.WrapChoice;
import com.x.processplatform.core.entity.element.wrap.WrapDelay;
import com.x.processplatform.core.entity.element.wrap.WrapEmbed;
import com.x.processplatform.core.entity.element.wrap.WrapEnd;
import com.x.processplatform.core.entity.element.wrap.WrapFile;
import com.x.processplatform.core.entity.element.wrap.WrapForm;
import com.x.processplatform.core.entity.element.wrap.WrapFormField;
import com.x.processplatform.core.entity.element.wrap.WrapInvoke;
import com.x.processplatform.core.entity.element.wrap.WrapManual;
import com.x.processplatform.core.entity.element.wrap.WrapMerge;
import com.x.processplatform.core.entity.element.wrap.WrapParallel;
import com.x.processplatform.core.entity.element.wrap.WrapProcess;
import com.x.processplatform.core.entity.element.wrap.WrapProcessPlatform;
import com.x.processplatform.core.entity.element.wrap.WrapPublish;
import com.x.processplatform.core.entity.element.wrap.WrapRoute;
import com.x.processplatform.core.entity.element.wrap.WrapScript;
import com.x.processplatform.core.entity.element.wrap.WrapService;
import com.x.processplatform.core.entity.element.wrap.WrapSplit;

class ActionCreate extends BaseAction {

    private static Logger logger = LoggerFactory.getLogger(ActionCreate.class);

    ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            ActionResult<Wo> result = new ActionResult<>();
            Wo wo = new Wo();
            Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
            Business business = new Business(emc);
            if (!business.editable(effectivePerson, null)) {
                throw new ExceptionApplicationAccessDenied(effectivePerson.getName(), wi.getName(), wi.getId());
            }
            Application application = this.create(business, wi, effectivePerson);
            wo.setId(application.getId());
            result.setData(wo);
            return result;
        }
    }

    private Application create(Business business, Wi wi, EffectivePerson effectivePerson) throws Exception {
        List<JpaObject> persistObjects = new ArrayList<>();
        Application application = business.entityManagerContainer().find(wi.getId(), Application.class);
        if (null != application) {
            throw new ExceptionApplicationExist(wi.getId());
        }
        application = WrapProcessPlatform.inCopier.copy(wi);
        application.setName(this.idleApplicationName(business, application.getName(), application.getId()));
        application.setAlias(this.idleApplicationAlias(business, application.getAlias(), application.getId()));
        persistObjects.add(application);
        for (WrapForm _o : wi.getFormList()) {
            Form obj = business.entityManagerContainer().find(_o.getId(), Form.class);
            if (null != obj) {
                throw new ExceptionEntityExistForCreate(_o.getId(), Form.class);
            }
            obj = WrapForm.inCopier.copy(_o);
            obj.setApplication(application.getId());
            persistObjects.add(obj);
            for (WrapFormField _ff : _o.getFormFieldList()) {
                FormField formField = business.entityManagerContainer().find(_ff.getId(), FormField.class);
                if (null != formField) {
                    throw new ExceptionEntityExistForCreate(_ff.getId(), FormField.class);
                }
                formField = WrapFormField.inCopier.copy(_ff);
                formField.setForm(obj.getId());
                persistObjects.add(formField);
            }
        }
        for (WrapScript _o : wi.getScriptList()) {
            Script obj = business.entityManagerContainer().find(_o.getId(), Script.class);
            if (null != obj) {
                throw new ExceptionEntityExistForCreate(_o.getId(), Script.class);
            }
            obj = WrapScript.inCopier.copy(_o);
            obj.setApplication(application.getId());
            persistObjects.add(obj);
        }
        for (WrapFile _o : wi.getFileList()) {
            File obj = business.entityManagerContainer().find(_o.getId(), File.class);
            if (null != obj) {
                throw new ExceptionEntityExistForCreate(_o.getId(), File.class);
            }
            obj = WrapFile.inCopier.copy(_o);
            obj.setApplication(application.getId());
            persistObjects.add(obj);
        }
        for (WrapApplicationDict _o : wi.getApplicationDictList()) {
            ApplicationDict obj = business.entityManagerContainer().find(_o.getId(), ApplicationDict.class);
            if (null != obj) {
                throw new ExceptionEntityExistForCreate(_o.getId(), ApplicationDict.class);
            }
            obj = WrapApplicationDict.inCopier.copy(_o);
            obj.setApplication(application.getId());
            persistObjects.add(obj);
            DataItemConverter<ApplicationDictItem> converter = new DataItemConverter<>(ApplicationDictItem.class);
            List<ApplicationDictItem> list = converter.disassemble(_o.getData());
            for (ApplicationDictItem o : list) {
                o.setBundle(obj.getId());
                /** 将数据字典和数据存放在同一个分区 */
                o.setDistributeFactor(obj.getDistributeFactor());
                o.setApplication(obj.getApplication());
                persistObjects.add(o);
            }
        }
        for (WrapProcess wrapProcess : wi.getProcessList()) {
            Process process = business.entityManagerContainer().find(wrapProcess.getId(), Process.class);
            if (null != process) {
                throw new ExceptionEntityExistForCreate(wrapProcess.getId(), Process.class);
            }
            process = WrapProcess.inCopier.copy(wrapProcess);
            process.setLastUpdatePerson(effectivePerson.getDistinguishedName());
            process.setLastUpdateTime(new Date());
            process.setApplication(application.getId());
            persistObjects.add(process);
            if (StringUtils.isNotEmpty(process.getEdition())) {
                if (BooleanUtils.isTrue(process.getEditionEnable())) {
                    for (Process p : business.entityManagerContainer().listEqualAndEqual(Process.class,
                            Process.application_FIELDNAME, process.getApplication(), Process.edition_FIELDNAME,
                            process.getEdition())) {
                        if (!process.getId().equals(p.getId()) && BooleanUtils.isTrue(p.getEditionEnable())) {
                            p.setEditionEnable(false);
                        }
                    }
                }
            } else {
                process.setEdition(process.getId());
                process.setEditionEnable(true);
                process.setEditionNumber(1.0);
                process.setEditionName(process.getName() + "_V" + process.getEditionNumber());
            }
            for (WrapAgent _o : wrapProcess.getAgentList()) {
                Agent obj = business.entityManagerContainer().find(_o.getId(), Agent.class);
                if (null != obj) {
                    throw new ExceptionEntityExistForCreate(_o.getId(), Agent.class);
                }
                obj = WrapAgent.inCopier.copy(_o);
                obj.setProcess(process.getId());
                persistObjects.add(obj);
            }
            if (null != wrapProcess.getBegin()) {
                Begin obj = business.entityManagerContainer().find(wrapProcess.getBegin().getId(), Begin.class);
                if (null != obj) {
                    throw new ExceptionEntityExistForCreate(wrapProcess.getBegin().getId(), Begin.class);
                }
                obj = WrapBegin.inCopier.copy(wrapProcess.getBegin());
                obj.setProcess(process.getId());
                persistObjects.add(obj);
            }
            for (WrapCancel _o : wrapProcess.getCancelList()) {
                Cancel obj = business.entityManagerContainer().find(_o.getId(), Cancel.class);
                if (null != obj) {
                    throw new ExceptionEntityExistForCreate(_o.getId(), Cancel.class);
                }
                obj = WrapCancel.inCopier.copy(_o);
                obj.setProcess(process.getId());
                persistObjects.add(obj);
            }
            for (WrapChoice _o : wrapProcess.getChoiceList()) {
                Choice obj = business.entityManagerContainer().find(_o.getId(), Choice.class);
                if (null != obj) {
                    throw new ExceptionEntityExistForCreate(_o.getId(), Choice.class);
                }
                obj = WrapChoice.inCopier.copy(_o);
                obj.setProcess(process.getId());
                persistObjects.add(obj);
            }
            for (WrapDelay _o : wrapProcess.getDelayList()) {
                Delay obj = business.entityManagerContainer().find(_o.getId(), Delay.class);
                if (null != obj) {
                    throw new ExceptionEntityExistForCreate(_o.getId(), Delay.class);
                }
                obj = WrapDelay.inCopier.copy(_o);
                obj.setProcess(process.getId());
                persistObjects.add(obj);
            }
            for (WrapEmbed _o : wrapProcess.getEmbedList()) {
                Embed obj = business.entityManagerContainer().find(_o.getId(), Embed.class);
                if (null != obj) {
                    throw new ExceptionEntityExistForCreate(_o.getId(), Embed.class);
                }
                obj = WrapEmbed.inCopier.copy(_o);
                obj.setProcess(process.getId());
                persistObjects.add(obj);
            }
            for (WrapEnd _o : wrapProcess.getEndList()) {
                End obj = business.entityManagerContainer().find(_o.getId(), End.class);
                if (null != obj) {
                    throw new ExceptionEntityExistForCreate(_o.getId(), End.class);
                }
                obj = WrapEnd.inCopier.copy(_o);
                obj.setProcess(process.getId());
                persistObjects.add(obj);
            }
            for (WrapInvoke _o : wrapProcess.getInvokeList()) {
                Invoke obj = business.entityManagerContainer().find(_o.getId(), Invoke.class);
                if (null != obj) {
                    throw new ExceptionEntityExistForCreate(_o.getId(), Invoke.class);
                }
                obj = WrapInvoke.inCopier.copy(_o);
                obj.setProcess(process.getId());
                persistObjects.add(obj);
            }
            for (WrapManual _o : wrapProcess.getManualList()) {
                Manual obj = business.entityManagerContainer().find(_o.getId(), Manual.class);
                if (null != obj) {
                    throw new ExceptionEntityExistForCreate(_o.getId(), Manual.class);
                }
                obj = WrapManual.inCopier.copy(_o);
                obj.setProcess(process.getId());
                persistObjects.add(obj);
            }
            for (WrapMerge _o : wrapProcess.getMergeList()) {
                Merge obj = business.entityManagerContainer().find(_o.getId(), Merge.class);
                if (null != obj) {
                    throw new ExceptionEntityExistForCreate(_o.getId(), Merge.class);
                }
                obj = WrapMerge.inCopier.copy(_o);
                obj.setProcess(process.getId());
                persistObjects.add(obj);
            }
            for (WrapParallel _o : wrapProcess.getParallelList()) {
                Parallel obj = business.entityManagerContainer().find(_o.getId(), Parallel.class);
                if (null != obj) {
                    throw new ExceptionEntityExistForCreate(_o.getId(), Parallel.class);
                }
                obj = WrapParallel.inCopier.copy(_o);
                obj.setProcess(process.getId());
                persistObjects.add(obj);
            }
            for (WrapPublish _o : wrapProcess.getPublishList()) {
                Publish obj = business.entityManagerContainer().find(_o.getId(), Publish.class);
                if (null != obj) {
                    throw new ExceptionEntityExistForCreate(_o.getId(), Publish.class);
                }
                obj = WrapPublish.inCopier.copy(_o);
                obj.setProcess(process.getId());
                persistObjects.add(obj);
            }
            for (WrapService _o : wrapProcess.getServiceList()) {
                Service obj = business.entityManagerContainer().find(_o.getId(), Service.class);
                if (null != obj) {
                    throw new ExceptionEntityExistForCreate(_o.getId(), Service.class);
                }
                obj = WrapService.inCopier.copy(_o);
                obj.setProcess(process.getId());
                persistObjects.add(obj);
            }
            for (WrapSplit _o : wrapProcess.getSplitList()) {
                Split obj = business.entityManagerContainer().find(_o.getId(), Split.class);
                if (null != obj) {
                    throw new ExceptionEntityExistForCreate(_o.getId(), Split.class);
                }
                obj = WrapSplit.inCopier.copy(_o);
                obj.setProcess(process.getId());
                persistObjects.add(obj);
            }
            for (WrapRoute _o : wrapProcess.getRouteList()) {
                Route obj = business.entityManagerContainer().find(_o.getId(), Route.class);
                if (null != obj) {
                    throw new ExceptionEntityExistForCreate(_o.getId(), Route.class);
                }
                obj = WrapRoute.inCopier.copy(_o);
                obj.setProcess(process.getId());
                persistObjects.add(obj);
            }
        }

        for (JpaObject o : persistObjects) {
            business.entityManagerContainer().persist(o);
        }
        business.entityManagerContainer().beginTransaction(Application.class);
        business.entityManagerContainer().beginTransaction(File.class);
        business.entityManagerContainer().beginTransaction(Script.class);
        business.entityManagerContainer().beginTransaction(Form.class);
        business.entityManagerContainer().beginTransaction(FormField.class);
        business.entityManagerContainer().beginTransaction(ApplicationDict.class);
        business.entityManagerContainer().beginTransaction(ApplicationDictItem.class);
        business.entityManagerContainer().beginTransaction(Process.class);
        business.entityManagerContainer().beginTransaction(Agent.class);
        business.entityManagerContainer().beginTransaction(Begin.class);
        business.entityManagerContainer().beginTransaction(Cancel.class);
        business.entityManagerContainer().beginTransaction(Choice.class);
        business.entityManagerContainer().beginTransaction(Delay.class);
        business.entityManagerContainer().beginTransaction(Embed.class);
        business.entityManagerContainer().beginTransaction(End.class);
        business.entityManagerContainer().beginTransaction(Invoke.class);
        business.entityManagerContainer().beginTransaction(Manual.class);
        business.entityManagerContainer().beginTransaction(Merge.class);
        business.entityManagerContainer().beginTransaction(Parallel.class);
        business.entityManagerContainer().beginTransaction(Publish.class);
        business.entityManagerContainer().beginTransaction(Service.class);
        business.entityManagerContainer().beginTransaction(Split.class);
        business.entityManagerContainer().beginTransaction(Route.class);
        business.entityManagerContainer().commit();
        CacheManager.notify(ApplicationDictItem.class);
        CacheManager.notify(ApplicationDict.class);
        CacheManager.notify(FormField.class);
        CacheManager.notify(Form.class);
        CacheManager.notify(Script.class);
        CacheManager.notify(Process.class);
        CacheManager.notify(Application.class);
        return application;
    }

    public static class Wi extends WrapProcessPlatform {

        private static final long serialVersionUID = -4612391443319365035L;

    }

    public static class Wo extends WoId {

    }

}
