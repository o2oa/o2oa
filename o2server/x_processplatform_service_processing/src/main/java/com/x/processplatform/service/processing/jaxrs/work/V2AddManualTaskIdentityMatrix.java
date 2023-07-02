package com.x.processplatform.service.processing.jaxrs.work;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.executor.ProcessPlatformExecutorFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.processplatform.ManualTaskIdentityMatrix;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.express.service.processing.jaxrs.work.V2AddManualTaskIdentityMatrixWi;
import com.x.processplatform.core.express.service.processing.jaxrs.work.V2AddManualTaskIdentityMatrixWo;
import com.x.processplatform.service.processing.Business;

class V2AddManualTaskIdentityMatrix extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(V2AddManualTaskIdentityMatrix.class);

    ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {

        final Wi wi = this.convertToWrapIn(jsonElement, Wi.class);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("execute:{}, id:{}, jsonElement:{}.", effectivePerson::getDistinguishedName, () -> id,
                    () -> jsonElement);
        }

        Work work = null;

        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Business business = new Business(emc);
            work = getWork(business, id);
            if (null == work) {
                throw new ExceptionEntityNotExist(id, Work.class);
            }
        }

        return ProcessPlatformExecutorFactory.get(work.getJob())
                .submit(new CallableImpl(work.getId(), wi.getIdentity(), wi.getOptionList(), wi.getRemove()))
                .get(300, TimeUnit.SECONDS);

    }

    private Work getWork(Business business, String id) throws Exception {
        List<String> attributes = new ArrayList<>();
        attributes.add(Work.job_FIELDNAME);
        attributes.add(JpaObject.id_FIELDNAME);
        return business.entityManagerContainer().fetch(id, Work.class, attributes);
    }

    private class CallableImpl implements Callable<ActionResult<Wo>> {

        private String id;
        private String identity;
        private List<V2AddManualTaskIdentityMatrixWi.Option> optionList;
        private Boolean remove;

        CallableImpl(String id, String identity, List<V2AddManualTaskIdentityMatrixWi.Option> optionList,
                Boolean remove) {
            this.id = id;
            this.identity = identity;
            this.optionList = optionList;
            this.remove = remove;
        }

        @Override
        public ActionResult<Wo> call() throws Exception {
            ManualTaskIdentityMatrix matrix = null;
            try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
                Business business = new Business(emc);
                emc.beginTransaction(Work.class);
                Work work = emc.find(this.id, Work.class);
                if (null == work) {
                    throw new ExceptionEntityNotExist(id, Work.class);
                }
                matrix = work.getManualTaskIdentityMatrix();
                List<String> flat = matrix.flat();
                // 需要将ADD_POSITION_AFTER挑选出来进行逆序执行
                List<V2AddManualTaskIdentityMatrixWi.Option> afterOptionList = optionList.stream()
                        .filter(o -> StringUtils.equals(ManualTaskIdentityMatrix.ADD_POSITION_AFTER, o.getPosition()))
                        .collect(Collectors.toList());
                List<V2AddManualTaskIdentityMatrixWi.Option> otherOptionList = optionList.stream()
                        .filter(o -> !StringUtils.equals(ManualTaskIdentityMatrix.ADD_POSITION_AFTER, o.getPosition()))
                        .collect(Collectors.toList());
                Collections.reverse(afterOptionList);
                Set<String> combineIdentities = new HashSet<>();
                for (V2AddManualTaskIdentityMatrixWi.Option option : ListUtils.sum(afterOptionList, otherOptionList)) {
                    List<String> identities = business.organization().identity().list(option.getIdentityList());
                    identities = ListUtils.subtract(identities, flat);
                    if (!ListTools.isEmpty(identities)) {
                        LOGGER.debug("add identites:{}.", identities);
                        matrix.add(identity, option.getPosition(), identities);
                        combineIdentities.addAll(identities);
                    }
                }
                if (BooleanUtils.isTrue(remove)) {
                    matrix.remove(identity);
                }
                // 将当前环节可能已经存在的已办标记为不参与办理
                emc.beginTransaction(TaskCompleted.class);
                emc.listEqualAndEqualAndNotEqual(TaskCompleted.class, TaskCompleted.job_FIELDNAME, work.getJob(),
                        TaskCompleted.activityToken_FIELDNAME,
                        work.getActivityToken(), TaskCompleted.joinInquire_FIELDNAME, false).stream()
                        .filter(o -> combineIdentities.contains(o.getIdentity())).forEach(p -> {
                            try {
                                p.setJoinInquire(false);
                                emc.check(p, CheckPersistType.all);
                            } catch (Exception e) {
                                LOGGER.error(e);
                            }
                        });
                work.setManualTaskIdentityMatrix(matrix);
                emc.check(work, CheckPersistType.all);
                emc.commit();
            } catch (Exception e) {
                LOGGER.error(e);
            }
            ActionResult<Wo> result = new ActionResult<>();
            Wo wo = new Wo();
            wo.setManualTaskIdentityMatrix(matrix);
            result.setData(wo);
            return result;
        }

    }

    public static class Wi extends V2AddManualTaskIdentityMatrixWi {

        private static final long serialVersionUID = 7870902860170655791L;

    }

    public static class Wo extends V2AddManualTaskIdentityMatrixWo {

        private static final long serialVersionUID = -1377290527826280418L;

    }

}