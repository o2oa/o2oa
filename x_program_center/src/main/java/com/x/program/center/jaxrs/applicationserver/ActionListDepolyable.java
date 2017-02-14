package com.x.program.center.jaxrs.applicationserver;

import java.util.ArrayList;
import java.util.List;

public class ActionListDepolyable extends ActionBase {

	public List<WrapOutDeployable> execute() throws Exception {
		List<WrapOutDeployable> wraps = new ArrayList<>();
		for (String str : this.getDeployable().keySet()) {
			WrapOutDeployable wrap = new WrapOutDeployable();
			wrap.setName(str);
			wraps.add(wrap);
		}
		return wraps;
	}

}
