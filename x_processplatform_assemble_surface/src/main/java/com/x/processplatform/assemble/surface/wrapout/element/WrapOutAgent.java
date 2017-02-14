package com.x.processplatform.assemble.surface.wrapout.element;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.http.annotation.Wrap;
import com.x.processplatform.core.entity.element.Agent;

@Wrap(Agent.class)
public class WrapOutAgent extends Agent {


	private static final long serialVersionUID = 797206511536423164L;
	public static List<String> Excludes = new ArrayList<>(JpaObject.FieldsInvisible);

}
