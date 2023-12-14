package com.x.program.center.jaxrs.test;

import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.program.center.script.GrralScriptingFactory;

class ActionTest2 extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionTest2.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		ActionResult<Wo> result = new ActionResult<>();
		Source source = GrralScriptingFactory.functionalization(wi.getText());
		System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!222222222");
		System.out.println(source.getCharacters());
		System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!2222222222");
		Value value = GrralScriptingFactory.eval(source);
		System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		System.out.println(value);
		System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
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