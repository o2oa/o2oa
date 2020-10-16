package com.x.processplatform.service.processing.jaxrs.test;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.thread.ParameterRunnable;
import com.x.processplatform.service.processing.ThisApplication;

class ActionTest1 extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		ThisApplication.context().threadFactory().start(new ParameterRunnable() {
			public void run() {
				try {
					for (int i = 0; i < 180; i++) {
						System.out.println("aaaaaaaaaaaaaaaaaaaaa" + i);
						this.parameter.put("asdfasdf", i);
						Thread.sleep(1000);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}, "112233");
		return result;
	}

	public static class Wo extends WrapBoolean {

	}

}