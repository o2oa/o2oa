package com.x.strategydeploy.assemble.control.keywork;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.connection.ActionResponse;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.report.core.entity.Report_S_Setting;
import com.x.strategydeploy.assemble.control.Business;
import com.x.strategydeploy.assemble.control.ThisApplication;

public class ActionIsAdd extends BaseAction {
	private static Logger logger = LoggerFactory.getLogger(ActionIsAdd.class);

	private static String configCode = "UNITREPORT_DUTY";

	public static class Wo extends WrapBoolean {
	}

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson) throws Exception {
		ActionResult<Wo> result = new ActionResult<Wo>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			// 战略作者（公司级别）
			List<String> writer_g = keyWorkOperationService.getWriter_groups();
			List<String> writer_persons = business.organization().person().listWithGroup(writer_g);

			// 部门战略作者（部门级别）
			List<String> dept_writer_g = keyWorkOperationService.getDept_writer_groups();
			List<String> dept_writer_persons = business.organization().person().listWithGroup(dept_writer_g);
			String distinguishedName = effectivePerson.getDistinguishedName();

			// ---------------------部门战略管理员--------------------
			List<String> identities = null;
			List<String> dutyNames = null;
			List<String> dutyNameResultList = new ArrayList<>();
			identities = business.organization().identity().listWithPerson(distinguishedName);
			if (identities != null && !identities.isEmpty()) {
				for (String identity : identities) {
					dutyNames = business.organization().unitDuty().listNameWithIdentity(identity);
					if (dutyNames != null && !identities.isEmpty()) {
						for (String dutyName : dutyNames) {
							if (!dutyNameResultList.contains(dutyName)) {
								dutyNameResultList.add(dutyName);
							}
						}
					}
				}
			}

			// 部门战略管理员
			String deptDuty = "";
			deptDuty = this.getDutyNameFromReportSettingByConfigode();
			logger.info("部门战略管理员 deptDuty：" + deptDuty);
			// ---------------------部门战略管理员---------------------

			Wo wo = new Wo();
			if (writer_persons.indexOf(distinguishedName) >= 0 || dept_writer_persons.indexOf(distinguishedName) >= 0
					|| dutyNameResultList.indexOf(deptDuty) >= 0) {
				wo.setValue(true);
				result.setData(wo);
			} else {
				wo.setValue(false);
				result.setData(wo);
			}
		} catch (Exception e) {
			result.error(e);
		}

		return result;
	}

 
	String getDutyNameFromReportSettingByConfigode() throws Exception {
		String key = this.configCode;
		String serviceUri = "setting/code/" + key;
//		ActionResponse resp = ThisApplication.context().applications().getQuery(x_report_assemble_control.class,
//				serviceUri);
		ActionResponse resp = ThisApplication.context().applications().getQuery("x_report_assemble_control",
				serviceUri);
		Report_S_Setting setting = resp.getData(Report_S_Setting.class);
		return setting.getConfigValue();
	}
}
