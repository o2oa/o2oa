package com.x.cms.assemble.control.jaxrs.queryview;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.http.annotation.Wrap;
import com.x.cms.core.entity.element.QueryView;


@Wrap(QueryView.class)
public class WrapOutQueryView extends QueryView {

	private static final long serialVersionUID = 2886873983211744188L;
	public static List<String> Excludes = new ArrayList<>(JpaObject.FieldsInvisible);

}
