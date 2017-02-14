package com.x.organization.core.express.factory;

import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.reflect.TypeToken;
import com.x.base.core.project.AbstractThisApplication;
import com.x.base.core.project.x_organization_assemble_express;
import com.x.organization.core.express.wrap.WrapGroup;

public class GroupFactory {

	private Type collectionType = new TypeToken<ArrayList<WrapGroup>>() {
	}.getType();

	public WrapGroup getWithName(String name) throws Exception {
		try {
			return AbstractThisApplication.applications.getQuery(x_organization_assemble_express.class,
					"group/" + URLEncoder.encode(name, "UTF-8"), WrapGroup.class);
		} catch (Exception e) {
			throw new Exception("getWithName identity{name:" + name + "} error.", e);
		}
	}

	public List<WrapGroup> listSupDirect(String name) throws Exception {
		try {
			return AbstractThisApplication.applications.getQuery(x_organization_assemble_express.class,
					"group/list/" + URLEncoder.encode(name, "UTF-8") + "/sup/direct", collectionType);
		} catch (Exception e) {
			throw new Exception("listSupDirect name:" + name + " error.", e);
		}
	}

	public List<WrapGroup> listSupNested(String name) throws Exception {
		try {
			return AbstractThisApplication.applications.getQuery(x_organization_assemble_express.class,
					"group/list/" + URLEncoder.encode(name, "UTF-8") + "/sup/nested", collectionType);
		} catch (Exception e) {
			throw new Exception("listSupNested name:" + name + " error.", e);
		}
	}

	public List<WrapGroup> listWithPersonSupDirect(String name) throws Exception {
		try {
			return AbstractThisApplication.applications.getQuery(x_organization_assemble_express.class,
					"group/list/person/" + URLEncoder.encode(name, "UTF-8") + "/sup/direct", collectionType);
		} catch (Exception e) {
			throw new Exception("listWithPersonSupDirect person{name:" + name + "} error.", e);
		}
	}

	public List<WrapGroup> listWithPersonSupNested(String name) throws Exception {
		try {
			return AbstractThisApplication.applications.getQuery(x_organization_assemble_express.class,
					"group/list/person/" + URLEncoder.encode(name, "UTF-8") + "/sup/nested", collectionType);
		} catch (Exception e) {
			throw new Exception("listWithPersonSupNested person{name:" + name + "} error.", e);
		}
	}

	public List<WrapGroup> listSubDirect(String name) throws Exception {
		try {
			return AbstractThisApplication.applications.getQuery(x_organization_assemble_express.class,
					"group/list/" + URLEncoder.encode(name, "UTF-8") + "/sub/direct", collectionType);
		} catch (Exception e) {
			throw new Exception("listSubDirect name:" + name + " error.", e);
		}
	}

	public List<WrapGroup> listSubNested(String name) throws Exception {
		try {
			return AbstractThisApplication.applications.getQuery(x_organization_assemble_express.class,
					"group/list/" + URLEncoder.encode(name, "UTF-8") + "/sub/nested", collectionType);
		} catch (Exception e) {
			throw new Exception("listSubNested name:" + name + " error.", e);
		}
	}

	public List<WrapGroup> listPinyinInitial(String key) throws Exception {
		try {
			return AbstractThisApplication.applications.getQuery(x_organization_assemble_express.class,
					"group/list/pinyininitial/" + URLEncoder.encode(key, "UTF-8"), collectionType);
		} catch (Exception e) {
			throw new Exception("listPinyinInitial key:" + key + " error.", e);
		}
	}

	public List<WrapGroup> listLikePinyin(String key) throws Exception {
		try {
			return AbstractThisApplication.applications.getQuery(x_organization_assemble_express.class,
					"group/list/like/pinyin/" + URLEncoder.encode(key, "UTF-8"), collectionType);
		} catch (Exception e) {
			throw new Exception("listLikePinyin key:" + key + " error.", e);
		}
	}

	public List<WrapGroup> listLike(String key) throws Exception {
		try {
			return AbstractThisApplication.applications.getQuery(x_organization_assemble_express.class,
					"group/list/like/" + URLEncoder.encode(key, "UTF-8"), collectionType);
		} catch (Exception e) {
			throw new Exception("listLike key:" + key + " error.", e);
		}
	}

}
