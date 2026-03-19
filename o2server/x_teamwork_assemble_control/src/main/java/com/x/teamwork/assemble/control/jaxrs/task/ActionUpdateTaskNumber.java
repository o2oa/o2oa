package com.x.teamwork.assemble.control.jaxrs.task;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.teamwork.core.entity.Task;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author sword
 */
public class ActionUpdateTaskNumber extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionUpdateTaskNumber.class);

	protected ActionResult<Wo> execute(EffectivePerson effectivePerson, String projectId ) throws Exception {
		logger.info("{}操作更新项目：{}中所有任务的序列号。", effectivePerson.getDistinguishedName(), projectId);
		ActionResult<Wo> result = new ActionResult<>();
		if(!this.isManager(projectId, effectivePerson)){
			throw new TaskPersistException("权限不足!");
		}

		List<String> taskList = taskQueryService.listAllTaskIdsWithProject(projectId);
		for(String taskId : taskList){
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Task task = emc.find(taskId, Task.class);
				emc.beginTransaction(Task.class);
				task.setSerialNumber(this.updateSerialNumber(task.getSerialNumber()));
				emc.commit();
			}
		}
		CacheManager.notify(Task.class);
		Wo wo = new Wo();
		wo.setValue(true);
		result.setData( wo );
		return result;
	}

	private String updateSerialNumber(String serialNumber) {
		if(StringUtils.isBlank(serialNumber)){
			return serialNumber;
		}
		String[] numbers = StringUtils.split(serialNumber, Task.SERIAL_NUMBER_SEPARATOR);
		List<String> numberList = new ArrayList<>();
		for (String number : numbers){
			numberList.add(StringUtils.leftPad(number, 4, "0"));
		}
		return StringUtils.join(numberList, Task.SERIAL_NUMBER_SEPARATOR);
	}

	public static class Wo extends WrapBoolean {

		private static final long serialVersionUID = -2073617008016618651L;
	}


}
