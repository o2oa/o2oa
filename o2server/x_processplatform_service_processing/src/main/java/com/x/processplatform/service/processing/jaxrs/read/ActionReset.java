package com.x.processplatform.service.processing.jaxrs.read;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.core.entity.content.Read;
import com.x.processplatform.core.entity.content.ReadCompleted;
import com.x.processplatform.service.processing.Business;
import com.x.processplatform.service.processing.MessageFactory;
import com.x.processplatform.service.processing.ProcessPlatformKeyClassifyExecutorFactory;

class ActionReset extends BaseAction {

    private static Logger logger = LoggerFactory.getLogger(ActionReset.class);

    ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {
        logger.debug(effectivePerson.getDistinguishedName());
        ActionResult<Wo> result = new ActionResult<>();
        Wo wo = new Wo();
        final Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
        String executorSeed;
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Read read = emc.fetch(id, Read.class, ListTools.toList(Read.job_FIELDNAME));
            if (null == read) {
                throw new ExceptionEntityNotExist(id, Read.class);
            }
            executorSeed = read.getJob();
        }
        Callable<String> callable = () -> {
            try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
                Business business = new Business(emc);
                Read read = emc.find(id, Read.class);
                if (null == read) {
                    throw new ExceptionEntityNotExist(id, Read.class);
                }
                List<String> identities = ListTools.trim(business.organization().identity().list(wi.getIdentityList()),
                        true, true);
                boolean flag = false;
                if (identities.contains(read.getIdentity())) {
                    flag = true;
                    identities.remove(read.getIdentity());
                }
                if (identities.isEmpty()) {
                    throw new ExceptionResetEmpty();
                }
                Date now = new Date();
                List<Read> readList = new ArrayList<>();
                assembleRead(identities, readList, read, business, now);
                emc.beginTransaction(Read.class);
                emc.beginTransaction(ReadCompleted.class);
                for (Read resetRead : readList) {
                    emc.persist(resetRead, CheckPersistType.all);
                }
                if (flag) {
                    if (StringUtils.isNotEmpty(wi.getOpinion())) {
                        read.setOpinion(wi.getOpinion());
                    }
                    emc.commit();
                } else {
                    Long duration = Config.workTime().betweenMinutes(read.getStartTime(), now);
                    ReadCompleted readCompleted = new ReadCompleted(read, now, duration);
                    if (StringUtils.isNotEmpty(wi.getOpinion())) {
                        readCompleted.setOpinion(wi.getOpinion());
                    }
                    emc.persist(readCompleted, CheckPersistType.all);
                    emc.remove(read, CheckRemoveType.all);
                    emc.commit();
                    MessageFactory.readCompleted_create(readCompleted);
                    MessageFactory.read_to_readCompleted(readCompleted);
                }
                for (Read resetRead : readList) {
                    MessageFactory.read_create(resetRead);
                }
                wo.setId(read.getId());
                result.setData(wo);
            }
            return "";
        };
        ProcessPlatformKeyClassifyExecutorFactory.get(executorSeed).submit(callable).get(300, TimeUnit.SECONDS);
        return result;
    }

    private void assembleRead(List<String> identities, List<Read> readList, Read read, Business business, Date date)
            throws Exception {
        for (String identity : identities) {
            String person = business.organization().person().getWithIdentity(identity);
            String unit = business.organization().unit().getWithIdentity(identity);
            if (StringUtils.isNotBlank(person)) {
                Read resetRead = new Read();
                read.copyTo(resetRead, Read.FieldsUnmodify);
                resetRead.setIdentity(identity);
                resetRead.setPerson(person);
                resetRead.setUnit(unit);
                resetRead.setCreatorPerson(read.getPerson());
                resetRead.setCreatorIdentity(read.getIdentity());
                resetRead.setCreatorUnit(read.getUnit());
                resetRead.setStartTime(date);
                resetRead.setOpinion(null);
                readList.add(resetRead);
            }
        }
    }

    public static class CallWrap {
        String job;
    }

    public static class Wi extends GsonPropertyObject {

        private static final long serialVersionUID = 3966536547959075002L;

        @FieldDescribe("身份")
        private List<String> identityList;

        @FieldDescribe("待阅意见")
        private String opinion;

        public List<String> getIdentityList() {
            return identityList;
        }

        public void setIdentityList(List<String> identityList) {
            this.identityList = identityList;
        }

        public String getOpinion() {
            return opinion;
        }

        public void setOpinion(String opinion) {
            this.opinion = opinion;
        }
    }

    public static class Wo extends WoId {

        private static final long serialVersionUID = 2035801489227371292L;

    }

}
