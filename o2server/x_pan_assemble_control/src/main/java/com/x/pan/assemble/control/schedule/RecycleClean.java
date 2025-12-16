package com.x.pan.assemble.control.schedule;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject_;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.schedule.AbstractJob;
import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.tools.ListTools;
import com.x.file.core.entity.open.OriginFile;
import com.x.file.core.entity.personal.Attachment2;
import com.x.file.core.entity.personal.Folder2;
import com.x.file.core.entity.personal.Share;
import com.x.pan.assemble.control.Business;
import com.x.pan.assemble.control.ThisApplication;
import com.x.pan.core.entity.*;
import org.apache.commons.lang3.StringUtils;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 定时清理回收站数据
 * @author sword
 */
public class RecycleClean extends AbstractJob {

	private static Logger logger = LoggerFactory.getLogger(RecycleClean.class);

	@Override
	public void schedule(JobExecutionContext jobExecutionContext) throws Exception {
		try {
			logger.info("开始定时清理网盘3回收站数据==========");
			this.cleanRecycle();
			logger.info("结束定时清理网盘3回收站数据==========");
		} catch (Exception e) {
			throw new JobExecutionException(e);
		}
	}

	private void cleanRecycle() throws Exception{
		List<String> list = this.listRecycle();
		if(ListTools.isEmpty(list)){
			return;
		}
		for (String id : list){
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				Recycle3 recycle = emc.find(id, Recycle3.class);
				if(Share.FILE_TYPE_ATTACHMENT.equals(recycle.getFileType())){
					if(StringUtils.isBlank(recycle.getZoneId())) {
						Attachment2 att = emc.find(recycle.getFileId(), Attachment2.class);
						emc.beginTransaction(Attachment2.class);
						this.deleteFile(business, att.getOriginFile());
						emc.remove(att);
					}else{
						Attachment3 att = emc.find(recycle.getFileId(), Attachment3.class);
						emc.beginTransaction(Attachment3.class);
						this.deleteFile(business, att.getOriginFile());
						emc.remove(att);
					}
				}else{
					if(StringUtils.isBlank(recycle.getZoneId())) {
						Folder2 folder = emc.find(recycle.getFileId(), Folder2.class);
						if (folder != null) {
							List<String> ids = new ArrayList<>();
							ids.add(folder.getId());
							ids.addAll(business.folder2().listSubNested(folder.getId(), null));
							for (int i = ids.size() - 1; i >= 0; i--) {
								List<Attachment2> attachments = business.attachment2().listWithFolder2(ids.get(i), null);
								for (Attachment2 att : attachments) {
									emc.beginTransaction(Attachment2.class);
									this.deleteFile(business, att.getOriginFile());
									emc.remove(att);
									emc.commit();
								}
								emc.beginTransaction(Folder2.class);
								emc.delete(Folder2.class, ids.get(i));
								emc.commit();
							}
						}
					}else{
						Folder3 folder = emc.find(recycle.getFileId(), Folder3.class);
						if (folder != null) {
							List<String> ids = new ArrayList<>();
							ids.add(folder.getId());
							ids.addAll(business.folder3().listSubNested(folder.getId(), null));
							for (int i = ids.size() - 1; i >= 0; i--) {
								List<Attachment3> attachments = business.attachment3().listWithFolder2(ids.get(i), null);
								for (Attachment3 att : attachments) {
									emc.beginTransaction(Attachment3.class);
									this.deleteFile(business, att.getOriginFile());
									emc.remove(att);
									emc.commit();
								}
								emc.beginTransaction(Folder3.class);
								if(ids.get(i).equals(folder.getZoneId())){
									emc.beginTransaction(ZonePermission.class);
									emc.deleteEqual(ZonePermission.class, ZonePermission.zoneId_FIELDNAME, folder.getZoneId());
								}
								emc.delete(Folder3.class, ids.get(i));
								emc.commit();
							}
						}
					}
				}
				emc.beginTransaction(Recycle3.class);
				emc.delete(Recycle3.class, recycle.getId());
				emc.commit();
			} catch (Exception e){
				logger.warn("清理网盘回收站文件{}异常：{}",id,e.getMessage());
			}
		}
	}

	private void deleteFile(Business business, String origin) throws Exception{
		EntityManagerContainer emc = business.entityManagerContainer();
		Long count = emc.countEqual(Attachment2.class, Attachment2.originFile_FIELDNAME, origin);
		count = count + emc.countEqual(Attachment3.class, Attachment3.originFile_FIELDNAME, origin);
		if(count.equals(1L)){
			OriginFile originFile = emc.find(origin, OriginFile.class);
			if(originFile!=null){
				StorageMapping mapping = ThisApplication.context().storageMappings().get(OriginFile.class,
						originFile.getStorage());
				if(mapping!=null){
					originFile.deleteContent(mapping);
				}
				emc.beginTransaction(OriginFile.class);
				emc.remove(originFile);
			}
		}
	}

	private List<String> listRecycle() throws Exception{
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Integer days = business.getSystemConfig().getPanRecycleDays();
			EntityManager em = emc.get(Recycle3.class);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<String> cq = cb.createQuery(String.class);
			Root<Recycle3> root = cq.from(Recycle3.class);
			Predicate p = cb.lessThan(root.get(JpaObject_.createTime), DateTools.addDay(new Date(), -days));
			cq.select(root.get(Recycle3_.id)).where(p);
			return em.createQuery(cq).getResultList();
		}
	}
}
