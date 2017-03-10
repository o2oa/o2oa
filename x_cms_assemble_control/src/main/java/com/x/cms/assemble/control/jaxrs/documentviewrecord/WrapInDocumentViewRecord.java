package com.x.cms.assemble.control.jaxrs.documentviewrecord;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.http.annotation.Wrap;
import com.x.cms.core.entity.DocumentViewRecord;

@Wrap( DocumentViewRecord.class )
public class WrapInDocumentViewRecord {
	
	public static List<String> Excludes = new ArrayList<String>(JpaObject.FieldsUnmodifies);
	
}