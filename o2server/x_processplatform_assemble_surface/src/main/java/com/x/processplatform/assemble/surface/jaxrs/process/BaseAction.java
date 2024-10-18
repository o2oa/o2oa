package com.x.processplatform.assemble.surface.jaxrs.process;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.core.entity.content.*;
import com.x.processplatform.core.entity.element.Process;
import com.x.query.core.entity.Item;
import org.apache.commons.collections4.ListUtils;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

abstract class BaseAction extends StandardJaxrsAction {

    void deleteBatch(EntityManagerContainer emc, Class<? extends JpaObject> clz, List<String> ids) throws Exception {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < ids.size(); i++) {
            list.add(ids.get(i));
            if ((list.size() == 1000) || (i == (ids.size() - 1))) {
                EntityManager em = emc.beginTransaction(clz);
                for (String str : list) {
                    em.remove(em.find(clz, str));
                }
                em.getTransaction().commit();
                list.clear();
            }
        }
    }

    void deleteDraft(Business business, Process process) throws Exception {
        List<String> ids = business.draft().listWithProcess(process.getId());
        this.deleteBatch(business.entityManagerContainer(), Draft.class, ids);
    }

    void deleteTask(Business business, Process process) throws Exception {
        List<String> ids = business.task().listWithProcess(process.getId());
        this.deleteBatch(business.entityManagerContainer(), Task.class, ids);
    }

    void deleteTaskCompleted(Business business, Process process, boolean onlyRemoveNotCompleted) throws Exception {
        List<String> ids = onlyRemoveNotCompleted
                ? business.taskCompleted().listWithProcessWithCompleted(process.getId(), false)
                : business.taskCompleted().listWithProcess(process.getId());
        this.deleteBatch(business.entityManagerContainer(), TaskCompleted.class, ids);
    }

    void deleteRead(Business business, Process process) throws Exception {
        List<String> ids = business.read().listWithProcess(process.getId());
        this.deleteBatch(business.entityManagerContainer(), Read.class, ids);
    }

    void deleteReadCompleted(Business business, Process process, boolean onlyRemoveNotCompleted) throws Exception {
        List<String> ids = onlyRemoveNotCompleted
                ? business.readCompleted().listWithProcessWithCompleted(process.getId(), false)
                : business.readCompleted().listWithProcess(process.getId());
        this.deleteBatch(business.entityManagerContainer(), ReadCompleted.class, ids);
    }

    void deleteReview(Business business, Process process, boolean onlyRemoveNotCompleted) throws Exception {
        List<String> ids = onlyRemoveNotCompleted
                ? business.review().listWithProcessWithCompleted(process.getId(), false)
                : business.review().listWithProcess(process.getId());
        this.deleteBatch(business.entityManagerContainer(), Review.class, ids);
    }

    void deleteAttachment(Business business, Process process, boolean onlyRemoveNotCompleted) throws Exception {
        List<String> ids = onlyRemoveNotCompleted
                ? business.attachment().listWithProcessWithCompleted(process.getId(), false)
                : business.attachment().listWithProcess(process.getId());
        /** 附件需要单独处理删除 */
        EntityManagerContainer emc = business.entityManagerContainer();
        for (List<String> list : ListTools.batch(ids, 1000)) {
            emc.beginTransaction(Attachment.class);
            for (Attachment o : business.entityManagerContainer().list(Attachment.class, list)) {
                StorageMapping mapping = ThisApplication.context().storageMappings().get(Attachment.class,
                        o.getStorage());
                /** 如果找不到存储器就算了 */
                if (null != mapping) {
                    o.deleteContent(mapping);
                }
                emc.remove(o);
            }
            emc.commit();
        }
    }

    void deleteItem(Business business, Process process, boolean onlyRemoveNotCompleted) throws Exception {
        List<String> jobs = business.work().listJobWithProcess(process.getId());
        if (!onlyRemoveNotCompleted) {
            jobs = ListUtils.union(jobs, business.workCompleted().listJobWithProcess(process.getId()));
        }
        EntityManagerContainer emc = business.entityManagerContainer();
        for (String job : jobs) {
            emc.beginTransaction(Item.class);
            for (Item o : business.item().listObjectWithJob(job)) {
                emc.remove(o);
            }
            emc.commit();
        }
    }

    void deleteSerialNumber(Business business, Process process) throws Exception {
        List<String> ids = business.serialNumber().listWithProcess(process.getId());
        this.deleteBatch(business.entityManagerContainer(), SerialNumber.class, ids);
    }

    void deleteWork(Business business, Process process) throws Exception {
        List<String> ids = business.work().listWithProcess(process.getId());
        this.deleteBatch(business.entityManagerContainer(), Work.class, ids);
    }

    void deleteRecord(Business business, Process process, boolean onlyRemoveNotCompleted) throws Exception {
        List<String> ids = onlyRemoveNotCompleted
                ? business.record().listWithProcessWithCompleted(process.getId(), false)
                : business.record().listWithProcess(process.getId());
        this.deleteBatch(business.entityManagerContainer(), Record.class, ids);
    }

    void deleteDocumentVersion(Business business, Process process) throws Exception {
        List<String> ids = business.entityManagerContainer().idsEqual(DocumentVersion.class,
                DocumentVersion.process_FIELDNAME, process.getId());
        this.deleteBatch(business.entityManagerContainer(), DocumentVersion.class, ids);
    }

    void deleteWorkCompleted(Business business, Process process) throws Exception {
        List<String> ids = business.workCompleted().listWithProcess(process.getId());
        this.deleteBatch(business.entityManagerContainer(), WorkCompleted.class, ids);
    }

    void deleteWorkLog(Business business, Process process, boolean onlyRemoveNotCompleted) throws Exception {
        List<String> ids = onlyRemoveNotCompleted
                ? business.workLog().listWithProcessWithCompleted(process.getId(), false)
                : business.workLog().listWithProcess(process.getId());
        this.deleteBatch(business.entityManagerContainer(), WorkLog.class, ids);
    }
}
