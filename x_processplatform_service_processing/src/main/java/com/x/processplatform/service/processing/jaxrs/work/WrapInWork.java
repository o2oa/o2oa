package com.x.processplatform.service.processing.jaxrs.work;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.http.annotation.Wrap;
import com.x.processplatform.core.entity.content.Work;

@Wrap(Work.class)
public class WrapInWork extends Work {

	private static final long serialVersionUID = -7004477605535850758L;
	public static List<String> CreateIncludes = new ArrayList<>();

	static {
		CreateIncludes.add("creatorIdentity");
		CreateIncludes.add("process");
	}
}
