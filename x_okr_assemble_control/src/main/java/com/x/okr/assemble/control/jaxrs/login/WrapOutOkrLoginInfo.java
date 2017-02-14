package com.x.okr.assemble.control.jaxrs.login;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.x.base.core.gson.GsonPropertyObject;

public class WrapOutOkrLoginInfo extends GsonPropertyObject implements Serializable{

	private static final long serialVersionUID = -5076990764713538973L;

	public static List<String> Excludes = new ArrayList<String>();
}
