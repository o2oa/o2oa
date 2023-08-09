package com.x.file.assemble.control.jaxrs.share;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.set.ListOrderedSet;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionFieldEmpty;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.tools.ListTools;
import com.x.file.assemble.control.Business;
import com.x.file.assemble.control.ThisApplication;
import com.x.file.core.entity.personal.Attachment2;
import com.x.file.core.entity.personal.Folder2;
import com.x.file.core.entity.personal.Share;

/**
 * 分享文件
 * 
 * @author sword
 */
public class ActionCreate extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionCreate.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		List<String> oldUserList = new ArrayList<>();
		List<String> oldOrgList = new ArrayList<>();
		List<String> oldGroupList = new ArrayList<>();
		final Share tempShare = new Share();
		Business business;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			if (StringUtils.isEmpty(wi.getFileId())) {
				throw new ExceptionShareNameEmpty();
			}
			if (StringUtils.isEmpty(wi.getShareType())) {
				throw new ExceptionFieldEmpty(Share.fileType_FIELDNAME);
			}
			Share share = business.share().getShareByFileId(wi.getFileId(), effectivePerson.getDistinguishedName());
			boolean isExist = true;
			if (share == null) {
				share = Wi.copier.copy(wi);
				isExist = false;
			} else {
				oldUserList.addAll(share.getShareUserList());
				oldOrgList.addAll(share.getShareOrgList());
				oldGroupList.addAll(share.getShareGroupList());
				share.setPassword(wi.getPassword());
				share.setShareUserList(wi.getShareUserList());
				share.setShareOrgList(wi.getShareOrgList());
				share.setShareGroupList(wi.getShareGroupList());
				share.setShareType(wi.getShareType());
			}
			if (Share.SHARE_TYPE_PASSWORD.equals(wi.getShareType())) {
				if (StringUtils.isBlank(share.getPassword())) {
					throw new ExceptionFieldEmpty(Share.password_FIELDNAME);
				}
			} else {
				boolean flag = ListTools.isEmpty(wi.getShareUserList()) && ListTools.isEmpty(wi.getShareOrgList())
						&& ListTools.isEmpty(wi.getShareGroupList());
				if (flag) {
					throw new Exception("shareUserList or shareOrgList or getShareGroupList can not be empty.");
				}
			}
			this.assembleShare(share, business, wi, effectivePerson);
			emc.beginTransaction(Share.class);
			if (isExist) {
				emc.check(share, CheckPersistType.all);
			} else {
				emc.persist(share, CheckPersistType.all);
			}
			emc.commit();
			logger.info("{}分享文件:{}", effectivePerson.getDistinguishedName(), share.getName());
			wo.setId(share.getId());
			share.copyTo(tempShare);
		}
		if (!Share.SHARE_TYPE_PASSWORD.equals(wi.getShareType())) {
			CompletableFuture.runAsync(() -> sendSms(business, tempShare, oldUserList, oldOrgList, oldGroupList),
					ThisApplication.forkJoinPool());
		}

		result.setData(wo);
		return result;
	}

	private void assembleShare(Share share, Business business, Wi wi, EffectivePerson effectivePerson)
			throws Exception {
		Attachment2 attachment = business.entityManagerContainer().find(wi.getFileId(), Attachment2.class);
		if (attachment == null) {
			Folder2 folder = business.entityManagerContainer().find(wi.getFileId(), Folder2.class);
			if (folder == null) {
				throw new ExceptionShareNotExist(wi.getFileId());
			} else {
				if (!business.controlAble(effectivePerson)
						&& !StringUtils.equals(folder.getPerson(), effectivePerson.getDistinguishedName())) {
					throw new ExceptionAccessDenied(effectivePerson, folder);
				}
				share.setFileType(Share.FILE_TYPE_FOLDER);
				share.setName(folder.getName());
				share.setPerson(folder.getPerson());
			}
		} else {
			if (!business.controlAble(effectivePerson)
					&& !StringUtils.equals(attachment.getPerson(), effectivePerson.getDistinguishedName())) {
				throw new ExceptionAccessDenied(effectivePerson, attachment);
			}
			share.setFileType(Share.FILE_TYPE_ATTACHMENT);
			share.setName(attachment.getName());
			share.setLength(attachment.getLength());
			share.setExtension(attachment.getExtension());
			share.setPerson(attachment.getPerson());
		}
		share.setLastUpdateTime(new Date());
		if (share.getValidTime() == null) {
			share.setValidTime(DateTools.getDateAfterYearAdjust(new Date(), 100, null, null));
		}
	}

	private void sendSms(Business business, Share share, List<String> oldUserList, List<String> oldOrgList,
			List<String> oldGroupList) {
		try {
			if (ListTools.isNotEmpty(oldOrgList)) {
				oldUserList.addAll(business.organization().person().listWithUnitSubNested(oldOrgList));
			}
			if (ListTools.isNotEmpty(oldGroupList)) {
				oldUserList.addAll(business.organization().person().listWithGroup(oldGroupList));
			}
			List<String> newUserList = new ArrayList<>(share.getShareUserList());
			if (ListTools.isNotEmpty(share.getShareOrgList())) {
				newUserList.addAll(business.organization().person().listWithUnitSubNested(share.getShareOrgList()));
			}
			if (ListTools.isNotEmpty(share.getShareGroupList())) {
				newUserList.addAll(business.organization().person().listWithGroup(share.getShareGroupList()));
			}
			List<String> shareAdds = ListUtils.subtract(newUserList, oldOrgList);
			oldUserList.clear();
			newUserList.clear();
			ListOrderedSet<String> set = new ListOrderedSet<>();
			set.addAll(shareAdds);
			shareAdds = set.asList();
			/* 发送共享通知 */
			for (String str : shareAdds) {
				this.message_send_attachment_share(share, str);
			}
			shareAdds.clear();
		} catch (Exception e) {
			logger.warn("文件分享消息发送异常：{}", e.getMessage());
		}
	}

	public static class Wi extends Share {

		private static final long serialVersionUID = 3965042303681243568L;

		static WrapCopier<Wi, Share> copier = WrapCopierFactory.wi(Wi.class, Share.class, null,
				JpaObject.FieldsUnmodify);
	}

	public static class Wo extends WoId {

		private static final long serialVersionUID = -8225324228431176092L;

	}

}
