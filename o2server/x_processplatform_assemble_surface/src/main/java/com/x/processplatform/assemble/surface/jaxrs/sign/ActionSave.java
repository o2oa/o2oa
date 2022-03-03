package com.x.processplatform.assemble.surface.jaxrs.sign;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.exception.ExceptionFieldEmpty;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.DocSign;
import com.x.processplatform.core.entity.content.Task;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

class ActionSave extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionSave.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String taskId, JsonElement jsonElement) throws Exception {
		String job = null;
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		if(wi.getStatus() == null){
			throw new ExceptionFieldEmpty(DocSign.status_FIELDNAME);
		}
		if(wi.getStatus().equals(DocSign.STATUS_3) && StringUtils.isBlank(wi.getContent())){
			throw new ExceptionFieldEmpty("content");
		}
		Task task = null;
		Wo wo = new Wo();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			task = emc.fetch(taskId, Task.class);
			if(task == null){
				throw new ExceptionEntityNotExist(taskId, Task.class);
			}
			if(!task.getPerson().equals(effectivePerson.getDistinguishedName())){
				throw new ExceptionAccessDenied(effectivePerson);
			}
		}
		this.saveDocSign(wi, task);
		ActionResult<Wo> result = new ActionResult<>();
		result.setData(wo);
		return result;
	}

	private String saveDocSign(Wi wi, Task task) throws Exception{
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			boolean flag = false;
			DocSign docSign = emc.firstEqual(DocSign.class, DocSign.taskId_FIELDNAME, task.getId());
			if(docSign == null) {
				flag = true;
				docSign = new DocSign(task);
				docSign.setStatus(wi.getStatus());
			}
			docSign.getProperties().setInputList(wi.getInputList());
			docSign.getProperties().setScrawlList(wi.getScrawlList());
			if(!wi.getStatus().equals(DocSign.STATUS_1)){
				docSign.setCommitTime(new Date());
			}
		}
		return "";
	}

	public static class Wi extends GsonPropertyObject {
		@FieldDescribe("状态：1(暂存)|2(签批正文不可以修改)|3(签批正文可以修改,正文保存为图片).")
		private Integer status;
		@FieldDescribe("包含签批的html正文后内容，状态为3时必传.")
		private String content;
		@FieldDescribe("输入框列表.")
		private List<String> inputList = new ArrayList<>();
		@FieldDescribe("涂鸦列表.")
		private List<String> scrawlList = new ArrayList<>();

		public Integer getStatus() {
			return status;
		}

		public void setStatus(Integer status) {
			this.status = status;
		}

		public String getContent() {
			return content;
		}

		public void setContent(String content) {
			this.content = content;
		}

		public List<String> getInputList() {
			return inputList;
		}

		public void setInputList(List<String> inputList) {
			this.inputList = inputList;
		}

		public List<String> getScrawlList() {
			return scrawlList;
		}

		public void setScrawlList(List<String> scrawlList) {
			this.scrawlList = scrawlList;
		}
	}

	public static class Wo extends WoId {

		private static final long serialVersionUID = -2577413577740827608L;

	}

}
