package com.x.processplatform.assemble.designer.wrapin;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.http.annotation.Wrap;
import com.x.processplatform.core.entity.element.Application;

@Wrap(Application.class)
public class WrapInApplication extends Application {

	private static final long serialVersionUID = 6624639107781167248L;

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
