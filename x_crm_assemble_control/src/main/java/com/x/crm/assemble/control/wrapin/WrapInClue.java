package com.x.crm.assemble.control.wrapin;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.http.annotation.Wrap;
import com.x.crm.core.entity.Clue;

@Wrap(WrapInClue.class)
public class WrapInClue extends Clue {
	private static final long serialVersionUID = -5952848343644633010L;
	public static List<String> Excludes = new ArrayList<>(JpaObject.FieldsUnmodifies);
}
