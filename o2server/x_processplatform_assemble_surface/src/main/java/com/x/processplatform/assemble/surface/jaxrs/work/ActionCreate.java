package com.x.processplatform.assemble.surface.jaxrs.work;

import java.util.List;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.element.Process;
import com.x.processplatform.core.express.assemble.surface.jaxrs.work.ActionCreateWi;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionCreate extends BaseCreateAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionCreate.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String processFlag, JsonElement jsonElement)
			throws Exception {
		LOGGER.debug("execute:{}, processFlag:{}.", effectivePerson::getDistinguishedName, () -> processFlag);
		// 新建工作id
		String workId = "";
		// 已存在草稿id
		String lastestWorkId = "";
		String identity = "";
		Process process = null;
		ActionResult<List<Wo>> result = new ActionResult<>();
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			identity = this.decideCreatorIdentity(business, effectivePerson, wi.getIdentity());
			process = business.process().pick(processFlag);
			if (null == process) {
				throw new ExceptionProcessNotExist(processFlag);
			}
			if (BooleanUtils.isNotTrue(wi.getAllowEdition()) && StringUtils.isNotEmpty(process.getEdition())
					&& BooleanUtils.isFalse(process.getEditionEnable())) {
				process = business.process().pickEnabled(process.getApplication(), process.getEdition());
			}

			List<String> identities = List.of(identity);
			List<String> units = business.organization().unit().listWithIdentitySupNested(identity);
			List<String> groups = business.organization().group().listWithIdentity(identities);
			if (!business.process().startable(effectivePerson, identities, units, groups, process)) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
			if (BooleanUtils.isTrue(wi.getLatest())) {
				// 判断是否是要直接打开之前创建的草稿,草稿的判断标准:有待办无任何已办
				workId = lastestWorkId = this.latest(business, process, identity);
			}
		}
		if (StringUtils.isEmpty(workId)) {
			workId = this.createWork(process.getId(), wi.getData());
		}
		// 设置Work信息
		if(BooleanUtils.isTrue(wi.getSkipDraftCheck())){
			this.updateWorkDraftCheck(workId);
		}
		if (BooleanUtils.isFalse(wi.getLatest()) || (StringUtils.isEmpty(lastestWorkId))) {
			updateWork(identity, workId, wi.getTitle(), wi.getParentWork());
			// 驱动工作,使用非队列方式
			this.processingCreateWork(workId);
		} else {
			// 如果是草稿,准备后面的直接打开
			workId = lastestWorkId;
		}
		List<Wo> wos = assemble(effectivePerson, workId);
		result.setData(wos);
		return result;
	}

	@Schema(description = "com.x.processplatform.assemble.surface.jaxrs.work.ActionCreate$Wi")
	public static class Wi extends ActionCreateWi {

		private static final long serialVersionUID = -9206989162313092355L;

	}

}
