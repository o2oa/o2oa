package com.x.program.center.jaxrs.test;

import javax.naming.InitialContext;
import javax.transaction.TransactionManager;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

class ActionTest3 extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionTest3.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		InitialContext ctx = new InitialContext();
		InitialContext initialContext = new InitialContext();
		// 从 JNDI 中查找 TransactionManager
		TransactionManager transactionManager = (TransactionManager) initialContext.lookup("java:/TransactionManager");
		transactionManager.begin();
		Wo wo = new Wo();
		result.setData(wo);
		return result;
	}

	public static class Wi extends GsonPropertyObject {

		private static final long serialVersionUID = 3854135292216248503L;

		@FieldDescribe("执行脚本.")
		private String text;

		public String getText() {
			return text;
		}

		public void setText(String text) {
			this.text = text;
		}

	}

	public static class Wo extends GsonPropertyObject {

		private static final long serialVersionUID = 3854135292216248503L;

		@FieldDescribe("执行脚本.")
		private String text;

		public String getText() {
			return text;
		}

		public void setText(String text) {
			this.text = text;
		}

	}

}