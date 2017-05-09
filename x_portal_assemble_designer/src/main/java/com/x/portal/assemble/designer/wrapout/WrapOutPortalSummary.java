package com.x.portal.assemble.designer.wrapout;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.http.annotation.Wrap;
import com.x.portal.core.entity.Portal;

@Wrap(Portal.class)
public class WrapOutPortalSummary extends WrapOutPortal {

	private static final long serialVersionUID = 8321907320614072125L;
	public static List<String> Excludes = new ArrayList<>(JpaObject.FieldsInvisible);

	private  List<WrapOutPage> pageList = new ArrayList<>();

	public List<WrapOutPage> getPageList() {
		return pageList;
	}

	public void setPageList(List<WrapOutPage> pageList) {
		this.pageList = pageList;
	}

}
