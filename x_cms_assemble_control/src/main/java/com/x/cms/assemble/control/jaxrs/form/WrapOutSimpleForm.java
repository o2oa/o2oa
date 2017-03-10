package com.x.cms.assemble.control.jaxrs.form;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.http.annotation.Wrap;
import com.x.cms.core.entity.element.Form;

@Wrap( Form.class )
public class WrapOutSimpleForm extends Form {
	
	private static final long serialVersionUID = -5076990764713538973L;
	
	public static List<String> Excludes = new ArrayList<String>();

	static {
		Excludes.add("data");
		Excludes.add("mobileData");
	}
	
}