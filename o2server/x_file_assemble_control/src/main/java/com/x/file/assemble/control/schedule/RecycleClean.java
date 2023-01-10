package com.x.file.assemble.control.schedule;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject_;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.schedule.AbstractJob;
import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.tools.ListTools;
import com.x.file.assemble.control.Business;
import com.x.file.assemble.control.ThisApplication;
import com.x.file.core.entity.open.FileConfig;
import com.x.file.core.entity.open.OriginFile;
import com.x.file.core.entity.personal.Attachment2;
import com.x.file.core.entity.personal.Folder2;
import com.x.file.core.entity.personal.Recycle;
import com.x.file.core.entity.personal.Recycle_;
import com.x.file.core.entity.personal.Share;

/**
 * 定时清理回收站数据
 * 
 * @author sword
 */
public class RecycleClean extends AbstractJob {

    private static final Logger LOGGER = LoggerFactory.getLogger(RecycleClean.class);

    @Override
    public void schedule(JobExecutionContext jobExecutionContext) throws Exception {
        try {
            Date start = new Date();
            this.cleanRecycle();
            LOGGER.info("结束定时清理网盘回收站数据,耗时:{}ms.", (new Date()).getTime() - start.getTime());
        } catch (Exception e) {
            throw new JobExecutionException(e);
        }
    }

    private void cleanRecycle() throws Exception {
        List<String> list = this.listRecycle();
        if (ListTools.isEmpty(list)) {
            return;
        }
        for (String id : list) {
            try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
                Business business = new Business(emc);
                Recycle recycle = emc.find(id, Recycle.class);
                if (Share.FILE_TYPE_ATTACHMENT.equals(recycle.getFileType())) {
                    Attachment2 att = emc.find(recycle.getFileId(), Attachment2.class);
                    this.deleteFile(business, att);
                } else {
                    Folder2 folder = emc.find(recycle.getFileId(), Folder2.class);
                    if (folder != null) {
                        List<String> ids = new ArrayList<>();
                        ids.add(folder.getId());
                        ids.addAll(business.folder2().listSubNested(folder.getId(), null));
                        for (int i = ids.size() - 1; i >= 0; i--) {
                            List<Attachment2> attachments = business.attachment2().listWithFolder2(ids.get(i), null);
                            for (Attachment2 att : attachments) {
                                this.deleteFile(business, att);
                            }
                            emc.beginTransaction(Folder2.class);
                            emc.delete(Folder2.class, ids.get(i));
                            emc.commit();
                        }
                    }
                }
                emc.beginTransaction(Recycle.class);
                emc.delete(Recycle.class, recycle.getId());
                emc.commit();
            } catch (Exception e) {
                LOGGER.warn("清理网盘回收站文件{}异常：{}.", id, e.getMessage());
            }
        }
    }

    private void deleteFile(Business business, Attachment2 att) throws Exception {
        if (att == null) {
            return;
        }
        EntityManagerContainer emc = business.entityManagerContainer();
        Long count = emc.countEqual(Attachment2.class, Attachment2.originFile_FIELDNAME, att.getOriginFile());
        if (count.equals(1L)) {
            OriginFile originFile = emc.find(att.getOriginFile(), OriginFile.class);
            if (originFile != null) {
                StorageMapping mapping = ThisApplication.context().storageMappings().get(OriginFile.class,
                        originFile.getStorage());
                if (mapping != null) {
                    originFile.deleteContent(mapping);
                }
                emc.beginTransaction(Attachment2.class);
                emc.beginTransaction(OriginFile.class);
                emc.remove(att);
                emc.remove(originFile);
                emc.commit();
            }
        } else {
            emc.beginTransaction(Attachment2.class);
            emc.remove(att);
            emc.commit();
        }
    }

    private List<String> listRecycle() throws Exception {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Integer days = FileConfig.DEFAULT_RECYCLE_DAYS;
            FileConfig config = emc.firstEqual(FileConfig.class, FileConfig.person_FIELDNAME, Business.SYSTEM_CONFIG);
            if (config != null && config.getRecycleDays() != null) {
                days = config.getRecycleDays();
            }
            EntityManager em = emc.get(Recycle.class);
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<String> cq = cb.createQuery(String.class);
            Root<Recycle> root = cq.from(Recycle.class);
            Predicate p = cb.lessThan(root.get(JpaObject_.createTime), DateTools.addDay(new Date(), -days));
            cq.select(root.get(Recycle_.id)).where(p);
            return em.createQuery(cq).getResultList();
        }
    }
}
