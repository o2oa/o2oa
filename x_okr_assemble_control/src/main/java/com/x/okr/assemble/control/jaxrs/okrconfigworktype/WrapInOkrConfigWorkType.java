package com.x.okr.assemble.control.jaxrs.okrconfigworktype;
import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.http.annotation.Wrap;
import com.x.okr.entity.OkrConfigWorkType;
@Wrap( OkrConfigWorkType.class)
public class WrapInOkrConfigWorkType extends OkrConfigWorkType {

	private static final long serialVersionUID = -5076990764713538973L;

	public static List<String> Excludes = new ArrayList<String>(JpaObject.FieldsUnmodifies);

}
