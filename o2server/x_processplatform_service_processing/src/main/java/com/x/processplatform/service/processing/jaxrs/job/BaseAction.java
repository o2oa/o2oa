package com.x.processplatform.service.processing.jaxrs.job;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.core.entity.content.Attachment;
import com.x.processplatform.core.entity.content.DocSign;
import com.x.processplatform.core.entity.content.DocSignScrawl;
import com.x.processplatform.core.entity.content.DocumentVersion;
import com.x.processplatform.core.entity.content.Read;
import com.x.processplatform.core.entity.content.ReadCompleted;
import com.x.processplatform.core.entity.content.Record;
import com.x.processplatform.core.entity.content.Review;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.WorkLog;
import com.x.processplatform.service.processing.Business;
import com.x.processplatform.service.processing.MessageFactory;
import com.x.processplatform.service.processing.ThisApplication;
import com.x.query.core.entity.Item;

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
        List<String> ids = business.entityManagerContainer().idsEqual(WorkLog.class, WorkLog.JOB_FIELDNAME, job);
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

    protected void deleteSign(Business business, String job) throws Exception {
        List<String> ids = business.entityManagerContainer().idsEqual(DocSign.class, DocSign.job_FIELDNAME, job);
        if (ListTools.isNotEmpty(ids)) {
            business.entityManagerContainer().beginTransaction(DocSign.class);
            business.entityManagerContainer().delete(DocSign.class, ids);
        }
    }

    protected void deleteSignScrawl(Business business, String job) throws Exception {
        List<String> ids = business.entityManagerContainer().idsEqual(DocSignScrawl.class, DocSignScrawl.job_FIELDNAME, job);
        if (ListTools.isNotEmpty(ids)) {
            business.entityManagerContainer().beginTransaction(DocSignScrawl.class);
            DocSignScrawl obj;
            for (String id : ids) {
                obj = business.entityManagerContainer().find(id, DocSignScrawl.class);
                if (null != obj) {
                    if(StringUtils.isNotBlank(obj.getStorage())) {
                        StorageMapping mapping = ThisApplication.context().storageMappings().get(DocSignScrawl.class,
                                obj.getStorage());
                        if (null != mapping) {
                            obj.deleteContent(mapping);
                        }
                    }
                    business.entityManagerContainer().remove(obj, CheckRemoveType.all);
                }
            }
        }
    }
}
