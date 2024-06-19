package com.x.general.assemble.control.jaxrs;

import java.util.Set;

import javax.ws.rs.ApplicationPath;

import com.x.base.core.project.jaxrs.AbstractActionApplication;
import com.x.general.assemble.control.jaxrs.area.AreaAction;
import com.x.general.assemble.control.jaxrs.ecnet.EcnetAction;
import com.x.general.assemble.control.jaxrs.excel.ExcelAction;
import com.x.general.assemble.control.jaxrs.generalfile.GeneralFileAction;
import com.x.general.assemble.control.jaxrs.office.OfficeAction;
import com.x.general.assemble.control.jaxrs.qrcode.QrCodeAction;
import com.x.general.assemble.control.jaxrs.securityclearance.SecurityClearanceAction;
import com.x.general.assemble.control.jaxrs.upgrade.UpgradeAction;
import com.x.general.assemble.control.jaxrs.worktime.WorkTimeAction;

@ApplicationPath("jaxrs")
public class ActionApplication extends AbstractActionApplication {

	@Override
	public Set<Class<?>> getClasses() {
		classes.add(AreaAction.class);
		classes.add(EcnetAction.class);
		classes.add(OfficeAction.class);
		classes.add(WorkTimeAction.class);
		classes.add(GeneralFileAction.class);
		classes.add(UpgradeAction.class);
		classes.add(QrCodeAction.class);
		classes.add(SecurityClearanceAction.class);
		classes.add(ExcelAction.class);
		return classes;
	}

}
