package com.x.organization.core.express;

import java.net.URLEncoder;
import java.util.List;

import com.x.base.core.project.Context;
import com.x.base.core.project.x_organization_assemble_express;
import com.x.organization.core.express.wrap.WrapGroup;

public class GroupFactory {

	GroupFactory(Context context) {
		this.context = context;
	}

	private Context context;

	public WrapGroup getWithName(String name) throws Exception {
		try {
			return context.applications()
					.getQuery(x_organization_assemble_express.class, "group/" + URLEncoder.encode(name, "UTF-8"))
					.getData(WrapGroup.class);
		} catch (Exception e) {
			throw new Exception("getWithName identity{name:" + name + "} error.", e);
		}
	}

	public List<WrapGroup> listSupDirect(String name) throws Exception {
		try {
			return context.applications()
					.getQuery(x_organization_assemble_express.class,
							"group/list/" + URLEncoder.encode(name, "UTF-8") + "/sup/direct")
					.getDataAsList(WrapGroup.class);
		} catch (Exception e) {
			throw new Exception("listSupDirect name:" + name + " error.", e);
		}
	}

	public List<WrapGroup> listSupNested(String name) throws Exception {
		try {
			return context.applications()
					.getQuery(x_organization_assemble_express.class,
							"group/list/" + URLEncoder.encode(name, "UTF-8") + "/sup/nested")
					.getDataAsList(WrapGroup.class);
		} catch (Exception e) {
			throw new Exception("listSupNested name:" + name + " error.", e);
		}
	}

	public List<WrapGroup> listWithPersonSupDirect(String name) throws Exception {
		try {
			return context.applications()
					.getQuery(x_organization_assemble_express.class,
							"group/list/person/" + URLEncoder.encode(name, "UTF-8") + "/sup/direct")
					.getDataAsList(WrapGroup.class);
		} catch (Exception e) {
			throw new Exception("listWithPersonSupDirect person{name:" + name + "} error.", e);
		}
	}

	public List<WrapGroup> listWithPersonSupNested(String name) throws Exception {
		try {
			return context.applications()
					.getQuery(x_organization_assemble_express.class,
							"group/list/person/" + URLEncoder.encode(name, "UTF-8") + "/sup/nested")
					.getDataAsList(WrapGroup.class);
		} catch (Exception e) {
			throw new Exception("listWithPersonSupNested person{name:" + name + "} error.", e);
		}
	}

	public List<WrapGroup> listSubDirect(String name) throws Exception {
		try {
			return context.applications()
					.getQuery(x_organization_assemble_express.class,
							"group/list/" + URLEncoder.encode(name, "UTF-8") + "/sub/direct")
					.getDataAsList(WrapGroup.class);
		} catch (Exception e) {
			throw new Exception("listSubDirect name:" + name + " error.", e);
		}
	}

	public List<WrapGroup> listSubNested(String name) throws Exception {
		try {
			return context.applications()
					.getQuery(x_organization_assemble_express.class,
							"group/list/" + URLEncoder.encode(name, "UTF-8") + "/sub/nested")
					.getDataAsList(WrapGroup.class);
		} catch (Exception e) {
			throw new Exception("listSubNested name:" + name + " error.", e);
		}
	}

	public List<WrapGroup> listPinyinInitial(String key) throws Exception {
		try {
			return context.applications()
					.getQuery(x_organization_assemble_express.class,
							"group/list/pinyininitial/" + URLEncoder.encode(key, "UTF-8"))
					.getDataAsList(WrapGroup.class);
		} catch (Exception e) {
			throw new Exception("listPinyinInitial key:" + key + " error.", e);
		}
	}

	public List<WrapGroup> listLikePinyin(String key) throws Exception {
		try {
			return context.applications().getQuery(x_organization_assemble_express.class,
					"group/list/like/pinyin/" + URLEncoder.encode(key, "UTF-8")).getDataAsList(WrapGroup.class);
		} catch (Exception e) {
			throw new Exception("listLikePinyin key:" + key + " error.", e);
		}
	}

	public List<WrapGroup> listLike(String key) throws Exception {
		try {
			return context.applications().getQuery(x_organization_assemble_express.class,
					"group/list/like/" + URLEncoder.encode(key, "UTF-8")).getDataAsList(WrapGroup.class);
		} catch (Exception e) {
			throw new Exception("listLike key:" + key + " error.", e);
		}
	}

}
