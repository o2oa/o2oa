package com.x.okr.assemble.control.jaxrs.okrworkperson;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.okr.entity.OkrWorkPerson;

public class WrapInOkrWorkPerson extends OkrWorkPerson {
	
	private static final long serialVersionUID = -5076990764713538973L;
	
	public static List<String> Excludes = new ArrayList<String>(JpaObject.FieldsUnmodify);

}