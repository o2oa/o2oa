package com.x.okr.assemble.control.jaxrs.okrtaskhandled;
import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.http.annotation.Wrap;
import com.x.okr.entity.OkrTaskHandled;
@Wrap( OkrTaskHandled.class)
public class WrapInOkrTaskHandled extends OkrTaskHandled {

	private static final long serialVersionUID = -5076990764713538973L;

	public static List<String> Excludes = new ArrayList<String>(JpaObject.FieldsUnmodifies);

}
