package com.x.processplatform.assemble.designer.wrapin;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.http.annotation.Wrap;
import com.x.processplatform.core.entity.element.End;
@Wrap(End.class)
public class WrapInEnd extends End {
	private static final long serialVersionUID = -3021841003265028407L;
	public static List<String> Excludes = new ArrayList<>();

	static {
		Excludes.add(DISTRIBUTEFACTOR);
		Excludes.add("updateTime");
		Excludes.add("createTime");
		Excludes.add("sequence");
		Excludes.add("lastUpdatePerson");
		Excludes.add("lastUpdateTime");
	}
}
