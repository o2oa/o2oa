package com.x.face.assemble.control.jaxrs.compare;

import javax.ws.rs.Path;

import com.x.base.core.project.annotation.JaxrsDescribe;
import com.x.face.assemble.control.jaxrs.search.BaseAction;

@Path("compare")
@JaxrsDescribe("search操作")
public class CompareAction extends BaseAction{

	//现在使用的邬树涛的测试key o2wsttest
	private static String API_KEY = "kyYSn8JOnv8IPYUzeJ6nVEtXkcovPUrk";
	private static String API_SECRET = "AXavTq8oETR_WdrKOwFb9Jg9Klj8Mclm";

	private static String COMPARE_URL = "https://api-cn.faceplusplus.com/facepp/v3/compare";

	
}
