package com.x.program.center.jaxrs.collect.wrapout;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.http.annotation.Wrap;
import com.x.base.core.project.server.Collect;

@Wrap(Collect.class)
public class WrapOutCollect extends Collect {
	public static List<String> Excludes = new ArrayList<>();
}
