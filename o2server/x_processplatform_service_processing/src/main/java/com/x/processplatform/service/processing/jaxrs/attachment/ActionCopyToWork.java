package com.x.processplatform.service.processing.jaxrs.attachment;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.Applications;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.core.entity.FileInfo;
import com.x.processplatform.core.entity.content.Attachment;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.express.assemble.surface.jaxrs.attachment.WiAttachment;
import com.x.processplatform.service.processing.ProcessPlatformKeyClassifyExecutorFactory;
import com.x.processplatform.service.processing.ThisApplication;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author zhour 复制指定的附件到work,仅拷贝内容,并清除其他附带的信息
 */
class ActionCopyToWork extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionCopyToWork.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String workId, JsonElement jsonElement)
			throws Exception {
		LOGGER.debug("execute:{}, workId:{}.", effectivePerson::getDistinguishedName, () -> workId);
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		String executorSeed = null;

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Work work = emc.fetch(workId, Work.class, ListTools.toList(Work.job_FIELDNAME));
			if (null == work) {
				throw new ExceptionEntityNotExist(workId, Work.class);
			}
			executorSeed = work.getJob();
		}

		CallableImpl impl = new CallableImpl(effectivePerson, wi, workId);

		return ProcessPlatformKeyClassifyExecutorFactory.get(executorSeed).submit(impl).get(300, TimeUnit.SECONDS);

	}

	private class CallableImpl implements Callable<ActionResult<List<Wo>>> {

		private CallableImpl(EffectivePerson effectivePerson, Wi wi, String workId) {
			this.effectivePerson = effectivePerson;
			this.workId = workId;
			this.wi = wi;
		}

		private final String workId;
		private final EffectivePerson effectivePerson;
		private final Wi wi;

		public ActionResult<List<Wo>> call() throws Exception {
			List<Wo> wos = new ArrayList<>();
			ActionResult<List<Wo>> result = new ActionResult<>();
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Work work = emc.find(workId, Work.class);
				if (null == work) {
					throw new ExceptionEntityNotExist(workId, Work.class);
				}
				List<Attachment> adds = new ArrayList<>();
				for (WiAttachment w : ListTools.trim(wi.getAttachmentList(), true, true)) {
					Attachment attachment = this.joinAttachment(effectivePerson.getDistinguishedName(), work, w, emc);
					adds.add(attachment);
				}
				if (!adds.isEmpty()) {
					emc.beginTransaction(Attachment.class);
					for (Attachment o : adds) {
						emc.persist(o, CheckPersistType.all);
						Wo wo = new Wo();
						wo.setId(o.getId());
						wos.add(wo);
					}
					emc.commit();
				}
			}
			result.setData(wos);
			return result;
		}

		private Attachment joinAttachment(String person, Work work, WiAttachment w, EntityManagerContainer emc) throws Exception {
			Attachment o = emc.find(w.getId(), Attachment.class);
			Attachment attachment = new Attachment(work, person, w.getSite());
			StorageMapping mapping = ThisApplication.context().storageMappings().random(Attachment.class);
			if (BooleanUtils.isTrue(w.getSoftCopy()) && o != null) {
				attachment.setName(o.getName());
				attachment.setDeepPath(mapping.getDeepPath());
				attachment
						.setExtension(StringUtils.lowerCase(StringUtils.substringAfterLast(o.getName(), ".")));
				attachment.setLength(o.getLength());
				attachment.setStorage(o.getStorage());
				attachment.setLastUpdateTime(new Date());
				attachment.setFromJob(o.getJob());
				attachment.setFromId(o.getId());
				attachment.setFromPath(o.path());
				if(StringUtils.isBlank(attachment.getSite())){
					attachment.setSite(o.getSite());
				}
			} else {
				byte[] bs;
				if(WiAttachment.COPY_FROM_CMS.equals(w.getCopyFrom())) {
					FileInfo fileInfo = emc.find(w.getId(), FileInfo.class);
					if(fileInfo == null){
						throw new ExceptionEntityNotExist(w.getId(), Attachment.class);
					}
					if(StringUtils.isBlank(attachment.getSite())){
						attachment.setSite(fileInfo.getSite());
					}
					StorageMapping fromStorageMapping = ThisApplication.context()
							.storageMappings()
							.get(FileInfo.class, fileInfo.getStorage());
					bs = fileInfo.readContent(fromStorageMapping);
				}else if(WiAttachment.COPY_FROM_PAN.equals(w.getCopyFrom())){
					String className = ThisApplication.context().applications().findApplicationName(WiAttachment.COPY_FROM_PAN);
					String downLoadUrl = Applications.joinQueryUri("attachment3", w.getId(), "download");
					bs = ThisApplication.context().applications().getQueryBinary(className, downLoadUrl);
					if(StringUtils.isBlank(attachment.getSite())){
						attachment.setSite("attachment");
					}
				}else{
					if(o == null){
						throw new ExceptionEntityNotExist(w.getId(), Attachment.class);
					}
					if(StringUtils.isBlank(attachment.getSite())){
						attachment.setSite(o.getSite());
					}
					StorageMapping fromStorageMapping = ThisApplication.context()
							.storageMappings()
							.get(Attachment.class, o.getStorage());
					bs = o.readContent(fromStorageMapping);
				}
				attachment.saveContent(mapping, bs, w.getName(), Config.general().getStorageEncrypt());
			}
			return attachment;
		}
	}

	public static class Wi extends GsonPropertyObject {

		private static final long serialVersionUID = 2282531964827996333L;

		@FieldDescribe("附件对象")
		private List<WiAttachment> attachmentList = new ArrayList<>();

		public List<WiAttachment> getAttachmentList() {
			return attachmentList;
		}

		public void setAttachmentList(List<WiAttachment> attachmentList) {
			this.attachmentList = attachmentList;
		}

	}

	public static class Wo extends WoId {

		private static final long serialVersionUID = -5625070539020321800L;

	}

}
