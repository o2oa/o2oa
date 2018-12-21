package com.x.strategydeploy.assemble.control.jaxrs;

import java.util.Set;

import javax.ws.rs.ApplicationPath;

import com.x.base.core.project.jaxrs.AbstractActionApplication;
import com.x.strategydeploy.assemble.control.attachment.AttachmentAction;
import com.x.strategydeploy.assemble.control.configsys.ConfigSysAction;
import com.x.strategydeploy.assemble.control.inputmeasures.MeasureImportCheck;
import com.x.strategydeploy.assemble.control.keywork.KeyworkAction;
import com.x.strategydeploy.assemble.control.keywork.extra.KeyworkActionExtra;
import com.x.strategydeploy.assemble.control.measures.MeasuresAction;
import com.x.strategydeploy.assemble.control.measures.MeasuresExportAction;
import com.x.strategydeploy.assemble.control.measures.MeasuresImportAction;
import com.x.strategydeploy.assemble.control.measures.extra.MeasuresActionExtra;
import com.x.strategydeploy.assemble.control.strategy.StrategyAction;
import com.x.strategydeploy.assemble.control.strategy.StrategyExportAction;
import com.x.strategydeploy.assemble.control.strategy.StrategyImportAction;
import com.x.strategydeploy.assemble.control.strategy.extra.StrategyActionExtra;

@ApplicationPath("jaxrs")
public class ActionApplication extends AbstractActionApplication {

	public Set<Class<?>> getClasses() {
		this.classes.add(AttachmentAction.class);
		this.classes.add(StrategyAction.class);
		this.classes.add(StrategyActionExtra.class);
		this.classes.add(MeasuresAction.class);
		this.classes.add(MeasuresImportAction.class);
		this.classes.add(MeasuresExportAction.class);
		this.classes.add(MeasuresActionExtra.class);
		this.classes.add(MeasureImportCheck.class);
		this.classes.add(KeyworkAction.class);
		this.classes.add(KeyworkActionExtra.class);
		this.classes.add(ConfigSysAction.class);
		this.classes.add(StrategyExportAction.class);
		this.classes.add(StrategyImportAction.class);
		return this.classes;
	}
}