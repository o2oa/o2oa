package com.x.processplatform.service.processing.jaxrs.job;

import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.core.entity.content.*;
import com.x.processplatform.service.processing.Business;
import com.x.processplatform.service.processing.MessageFactory;
import com.x.processplatform.service.processing.ThisApplication;
import com.x.query.core.entity.Item;

import java.util.List;

abstract class BaseAction extends StandardJaxrsAction {

    protected void deleteTask(Business business, String job) throws Exception {
        List<String> ids = business.entityManagerContainer().idsEqual(Task.class, Task.job_FIELDNAME, job);
        if (ListTools.isNotEmpty(ids)) {
            business.entityManagerContainer().beginTransaction(Task.class);
            for (Task o : business.entityManagerContainer().list(Task.class, ids)) {
                business.entityManagerContainer().remove(o);
                MessageFactory.task_delete(o);
            }
        }
    }

    protected void deleteTaskCompleted(Business business, String job) throws Exception {
        List<String> ids = business.entityManagerContainer().idsEqual(TaskCompleted.class, TaskCompleted.job_FIELDNAME,
                job);
        if (ListTools.isNotEmpty(ids)) {
            business.entityManagerContainer().beginTransaction(TaskCompleted.class);
            for (TaskCompleted o : business.entityManagerContainer().list(TaskCompleted.class, ids)) {
                business.entityManagerContainer().remove(o);
                MessageFactory.taskCompleted_delete(o);
            }
        }
    }

    protected void deleteRead(Business business, String job) throws Exception {
        List<String> ids = business.entityManagerContainer().idsEqual(Read.class, Read.job_FIELDNAME, job);
        if (ListTools.isNotEmpty(ids)) {
            business.entityManagerContainer().beginTransaction(Read.class);
            for (Read o : business.entityManagerContainer().list(Read.class, ids)) {
                business.entityManagerContainer().remove(o);
                MessageFactory.read_delete(o);
            }
        }
    }

    protected void deleteReadCompleted(Business business, String job) throws Exception {
        List<String> ids = business.entityManagerContainer().idsEqual(ReadCompleted.class, ReadCompleted.job_FIELDNAME,
                job);
        if (ListTools.isNotEmpty(ids)) {
            business.entityManagerContainer().beginTransaction(ReadCompleted.class);
            for (ReadCompleted o : business.entityManagerContainer().list(ReadCompleted.class, ids)) {
                business.entityManagerContainer().remove(o);
                MessageFactory.readCompleted_delete(o);
            }
        }
    }

    protected void deleteReview(Business business, String job) throws Exception {
        List<String> ids = business.entityManagerContainer().idsEqual(Review.class, Review.job_FIELDNAME, job);
        if (ListTools.isNotEmpty(ids)) {
            business.entityManagerContainer().beginTransaction(Review.class);
            business.entityManagerContainer().delete(Review.class, ids);
        }
    }

    protected void deleteAttachment(Business business, String job) throws Exception {
        List<String> ids = business.entityManagerContainer().idsEqual(Attachment.class, Attachment.job_FIELDNAME, job);
        if (ListTools.isNotEmpty(ids)) {
            business.entityManagerContainer().beginTransaction(Attachment.class);
            Attachment obj;
            for (String id : ids) {
                obj = business.entityManagerContainer().find(id, Attachment.class);
                if (null != obj) {
                    StorageMapping mapping = ThisApplication.context().storageMappings().get(Attachment.class,
                            obj.getStorage());
                    if (null != mapping) {
                        obj.deleteContent(mapping);
                    }
                    business.entityManagerContainer().remove(obj, CheckRemoveType.all);
                }
            }
        }
    }

    protected void deleteWorkLog(Business business, String job) throws Exception {
        List<String> ids = business.entityManagerContainer().idsEqual(WorkLog.class, WorkLog.job_FIELDNAME, job);
        if (ListTools.isNotEmpty(ids)) {
            business.entityManagerContainer().beginTransaction(WorkLog.class);
            business.entityManagerContainer().delete(WorkLog.class, ids);
        }
    }

    protected void deleteItem(Business business, String job) throws Exception {
        List<String> ids = business.entityManagerContainer().idsEqual(Item.class, Item.bundle_FIELDNAME, job);
        if (ListTools.isNotEmpty(ids)) {
            business.entityManagerContainer().beginTransaction(Item.class);
            business.entityManagerContainer().delete(Item.class, ids);
        }
    }

    protected void deleteDocumentVersion(Business business, String job) throws Exception {
        List<String> ids = business.entityManagerContainer().idsEqual(DocumentVersion.class,
                DocumentVersion.job_FIELDNAME, job);
        if (ListTools.isNotEmpty(ids)) {
            business.entityManagerContainer().beginTransaction(DocumentVersion.class);
            business.entityManagerContainer().delete(DocumentVersion.class, ids);
        }
    }

    protected void deleteRecord(Business business, String job) throws Exception {
        List<String> ids = business.entityManagerContainer().idsEqual(Record.class, Record.job_FIELDNAME, job);
        if (ListTools.isNotEmpty(ids)) {
            business.entityManagerContainer().beginTransaction(Record.class);
            business.entityManagerContainer().delete(Record.class, ids);
        }
    }
}
