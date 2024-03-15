package com.x.processplatform.assemble.surface.jaxrs.work;

import java.util.List;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.element.Process;

import io.swagger.v3.oas.annotations.media.Schema;

@Deprecated(forRemoval = true)
class ActionCreateForce extends BaseCreateAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionCreateForce.class);

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

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.work.ActionCreateForce$Wi")
	public class Wi extends GsonPropertyObject {

		private static final long serialVersionUID = -7304888738391800411L;

		@FieldDescribe("直接打开指定人员已经有的草稿,草稿判断:工作没有已办,只有一条此人的待办.")
		@Schema(description = "直接打开指定人员已经有的草稿,草稿判断:工作没有已办,只有一条此人的待办.")
		private Boolean latest;

		@FieldDescribe("标题.")
		@Schema(description = "标题.")
		private String title;

		@FieldDescribe("启动人员身份.")
		@Schema(description = "启动人员身份.")
		private String identity;

		@FieldDescribe("工作数据.")
		@Schema(description = "工作数据.")
		private JsonElement data;

		@FieldDescribe("父工作标识.")
		@Schema(description = "父工作标识.")
		private String parentWork;

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getIdentity() {
			return identity;
		}

		public void setIdentity(String identity) {
			this.identity = identity;
		}

		public JsonElement getData() {
			return data;
		}

		public void setData(JsonElement data) {
			this.data = data;
		}

		public Boolean getLatest() {
			return latest;
		}

		public void setLatest(Boolean latest) {
			this.latest = latest;
		}

		public String getParentWork() {
			return parentWork;
		}

		public void setParentWork(String parentWork) {
			this.parentWork = parentWork;
		}

	}

}
