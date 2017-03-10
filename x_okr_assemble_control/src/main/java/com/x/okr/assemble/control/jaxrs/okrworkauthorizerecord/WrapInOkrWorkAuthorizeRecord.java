package com.x.okr.assemble.control.jaxrs.okrworkauthorizerecord;
import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.http.annotation.Wrap;
import com.x.okr.entity.OkrWorkAuthorizeRecord;

@Wrap( OkrWorkAuthorizeRecord.class)
public class WrapInOkrWorkAuthorizeRecord extends OkrWorkAuthorizeRecord {

	private static final long serialVersionUID = -5076990764713538973L;

	public static List<String> Excludes = new ArrayList<String>(JpaObject.FieldsUnmodifies);

}
