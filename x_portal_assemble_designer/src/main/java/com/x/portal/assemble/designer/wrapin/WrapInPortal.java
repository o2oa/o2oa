package com.x.portal.assemble.designer.wrapin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.http.annotation.Wrap;
import com.x.portal.core.entity.Portal;

@Wrap(Portal.class)
public class WrapInPortal extends Portal {

	private static final long serialVersionUID = 6624639107781167248L;

	public static List<String> Excludes = new ArrayList<>(Arrays.asList(new String[] { JpaObject.DISTRIBUTEFACTOR,
			JpaObject.CREATETIME, JpaObject.UPDATETIME, JpaObject.SEQUENCE }));

}
