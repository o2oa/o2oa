package com.x.correlation.assemble.surface;

import java.util.List;

import com.x.base.core.project.Applications;
import com.x.base.core.project.x_processplatform_service_processing;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.processplatform.core.express.service.processing.jaxrs.taskcompleted.WrapUpdateNextTaskIdentity;

public class TaskCompletedBuilder {

	private TaskCompletedBuilder() {
		// nothing
	}

	/**
	 * 更新已经存在已办的下一处理人
	 * 
	 * @param taskCompletedId
	 * @param identities
	 * @param job
	 * @throws Exception
	 */
	public static void updateNextTaskIdentity(String taskCompletedId, List<String> identities, String job)
			throws Exception {
		// 记录下一处理人信息
		WrapUpdateNextTaskIdentity req = new WrapUpdateNextTaskIdentity();
		req.getTaskCompletedList().add(taskCompletedId);
		req.setNextTaskIdentityList(identities);
		ThisApplication.context().applications()
				.putQuery(false, x_processplatform_service_processing.class,
						Applications.joinQueryUri("taskcompleted", "next", "task", "identity"), req, job)
				.getData(WrapBoolean.class);
	}

}
