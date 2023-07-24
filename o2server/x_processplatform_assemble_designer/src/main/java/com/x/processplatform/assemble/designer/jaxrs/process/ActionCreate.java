package com.x.processplatform.assemble.designer.jaxrs.process;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.exception.ExceptionEntityExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.assemble.designer.MessageFactory;
import com.x.processplatform.assemble.designer.ThisApplication;
import com.x.processplatform.core.entity.element.Agent;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Begin;
import com.x.processplatform.core.entity.element.Cancel;
import com.x.processplatform.core.entity.element.Choice;
import com.x.processplatform.core.entity.element.Delay;
import com.x.processplatform.core.entity.element.Embed;
import com.x.processplatform.core.entity.element.End;
import com.x.processplatform.core.entity.element.Invoke;
import com.x.processplatform.core.entity.element.Manual;
import com.x.processplatform.core.entity.element.Merge;
import com.x.processplatform.core.entity.element.Parallel;
import com.x.processplatform.core.entity.element.Process;
import com.x.processplatform.core.entity.element.ProcessVersion;
import com.x.processplatform.core.entity.element.Publish;
import com.x.processplatform.core.entity.element.Route;
import com.x.processplatform.core.entity.element.Service;
import com.x.processplatform.core.entity.element.Split;
import com.x.processplatform.core.entity.element.wrap.WrapProcess;

class ActionCreate extends BaseAction {

    ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
        ActionResult<Wo> result = new ActionResult<>();
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Business business = new Business(emc);
            WrapProcess wrapIn = this.convertToWrapIn(jsonElement, WrapProcess.class);
            Application application = emc.find(wrapIn.getApplication(), Application.class);
            if (null == application) {
                throw new ExceptionApplicationNotExist(wrapIn.getApplication());
            }
            if (!business.editable(effectivePerson, application)) {
                throw new ExceptionApplicationAccessDenied(effectivePerson.getDistinguishedName(),
                        application.getName(), application.getId());
            }
            if (emc.duplicateWithFlags(Process.class, wrapIn.getId())) {
                throw new ExceptionEntityExist(wrapIn.getId());
            }
            List<JpaObject> jpaObjects = new ArrayList<>();
            Process process = new Process();
            WrapProcess.inCopier.copy(wrapIn, process);
            process.setCreatorPerson(effectivePerson.getDistinguishedName());
            process.setLastUpdatePerson(effectivePerson.getDistinguishedName());
            process.setLastUpdateTime(new Date());
            this.updateEdition(process);
            jpaObjects.add(process);
            jpaObjects.addAll(createAgent(wrapIn.getAgentList(), process));
            jpaObjects.add(createBegin(wrapIn.getBegin(), process));
            jpaObjects.addAll(createCancel(wrapIn.getCancelList(), process));
            jpaObjects.addAll(createChoice(wrapIn.getChoiceList(), process));
            jpaObjects.addAll(createDelay(wrapIn.getDelayList(), process));
            jpaObjects.addAll(createEmbed(wrapIn.getEmbedList(), process));
            jpaObjects.addAll(createEnd(wrapIn.getEndList(), process));
            jpaObjects.addAll(createInvoke(wrapIn.getInvokeList(), process));
            jpaObjects.addAll(createManual(wrapIn.getManualList(), process));
            jpaObjects.addAll(createMerge(wrapIn.getMergeList(), process));
            jpaObjects.addAll(createParallel(wrapIn.getParallelList(), process));
            jpaObjects.addAll(createPublish(wrapIn.getPublishList(), process));
            jpaObjects.addAll(createService(wrapIn.getServiceList(), process));
            jpaObjects.addAll(createSplit(wrapIn.getSplitList(), process));
            jpaObjects.addAll(createRoute(wrapIn.getRouteList(), process));
            emc.beginTransaction(Process.class);
            emc.beginTransaction(Agent.class);
            emc.beginTransaction(Begin.class);
            emc.beginTransaction(Cancel.class);
            emc.beginTransaction(Choice.class);
            emc.beginTransaction(Delay.class);
            emc.beginTransaction(Embed.class);
            emc.beginTransaction(End.class);
            emc.beginTransaction(Invoke.class);
            emc.beginTransaction(Manual.class);
            emc.beginTransaction(Merge.class);
            emc.beginTransaction(Parallel.class);
            emc.beginTransaction(Publish.class);
            emc.beginTransaction(Service.class);
            emc.beginTransaction(Split.class);
            emc.beginTransaction(Route.class);
            for (JpaObject o : jpaObjects) {
                emc.persist(o, CheckPersistType.all);
            }
            emc.commit();
            cacheNotify();
            Wo wo = new Wo();
            wo.setId(process.getId());
            result.setData(wo);
            MessageFactory.process_create(process);
            // 保存历史版本
            ThisApplication.processVersionQueue.send(new ProcessVersion(process.getId(), jsonElement, effectivePerson.getDistinguishedName()));
            return result;
        }
    }

    public static class Wo extends WoId {

        private static final long serialVersionUID = 6978048199369939702L;

    }

    private void updateEdition(Process process) {
        process.setEdition(process.getId());
        process.setEditionEnable(true);
        process.setEditionNumber(1.0);
        process.setEditionName(process.getName() + "_V" + process.getEditionNumber());
    }

}
