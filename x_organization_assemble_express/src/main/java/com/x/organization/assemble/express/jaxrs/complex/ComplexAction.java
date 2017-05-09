package com.x.organization.assemble.express.jaxrs.complex;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.WrapOutOnline;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.project.x_collaboration_assemble_websocket;
import com.x.base.core.project.jaxrs.AbstractJaxrsAction;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.organization.assemble.express.Business;
import com.x.organization.assemble.express.ThisApplication;
import com.x.organization.assemble.express.jaxrs.wrapout.WrapOutCompany;
import com.x.organization.assemble.express.jaxrs.wrapout.WrapOutCompanyDuty;
import com.x.organization.assemble.express.jaxrs.wrapout.WrapOutDepartment;
import com.x.organization.assemble.express.jaxrs.wrapout.WrapOutDepartmentDuty;
import com.x.organization.assemble.express.jaxrs.wrapout.WrapOutIdentity;
import com.x.organization.core.entity.Company;
import com.x.organization.core.entity.CompanyDuty;
import com.x.organization.core.entity.Department;
import com.x.organization.core.entity.DepartmentDuty;
import com.x.organization.core.entity.Identity;
import com.x.organization.core.entity.Person;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

@Path("complex")
public class ComplexAction extends AbstractJaxrsAction {

	@SuppressWarnings("unchecked")
	@HttpMethodDescribe(value = "获取顶层公司，并获取顶层公司下属的子公司数量和顶层部门数量。", response = WrapOutCompany.class)
	@GET
	@Path("list/company/top")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listCompanyTop(@PathParam("name") String name) {
		ActionResult<List<WrapOutCompany>> result = new ActionResult<>();
		List<WrapOutCompany> wraps = new ArrayList<>();
		try {
			Ehcache cache = ApplicationCache.instance().getCache(Company.class);
			String cacheKey = "complex#listCompanyTop";
			Element element = cache.get(cacheKey);
			if (null != element) {
				wraps = (List<WrapOutCompany>) element.getObjectValue();
			} else {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business business = new Business(emc);
					List<String> ids = business.company().listTop();
					for (Company o : emc.fetchAttribute(ids, Company.class, "name", "display")) {
						WrapOutCompany wrap = new WrapOutCompany();
						wrap.setName(o.getName());
						wrap.setCompanyCount(business.company().countSubDirect(o.getId()));
						wrap.setDepartmentCount(business.department().countTopWithCompany(o.getId()));
						wraps.add(wrap);
					}
					cache.put(new Element(cacheKey, wraps));
				}
			}
			result.setData(wraps);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "获公司下属的直接分公司和直接部门，并计算下级的下级数量，用于级联显示。", response = WrapOutCompany.class)
	@GET
	@Path("company/{name}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getCompanyWithSubDirect(@PathParam("name") String name) {
		ActionResult<WrapOutCompany> result = new ActionResult<>();
		WrapOutCompany wrap = null;
		try {
			Ehcache cache = ApplicationCache.instance().getCache(Company.class);
			String cacheKey = "complex#getCompanyWithSubDirect#" + name;
			Element element = cache.get(cacheKey);
			if (null != element) {
				wrap = (WrapOutCompany) element.getObjectValue();
			} else {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business business = new Business(emc);
					String company = business.company().getWithName(name);
					if (StringUtils.isNotEmpty(company)) {
						Company o = emc.fetchAttribute(company, Company.class, "name", "display", "superior");
						wrap = this.wrapOutCompany(o, emc);
						wrap.setCompanyList(this.wrapOutSubDirectCompany(o, business));
						wrap.setDepartmentList(this.wrapOutSubDirectDepartment(o, business));
						cache.put(new Element(cacheKey, wrap));
					}
				}
			}
			result.setData(wrap);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "获取部门的下属部门和下属Identity.并计算下级的下级数量，用于级联显示。", response = WrapOutDepartment.class)
	@GET
	@Path("department/{name}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getDepartmentWithSubDirect(@PathParam("name") String name) {
		ActionResult<WrapOutDepartment> result = new ActionResult<>();
		WrapOutDepartment wrap = null;
		try {
			Ehcache cache = ApplicationCache.instance().getCache(Department.class);
			String cacheKey = "complex#getDepartmentWithSubDirect#" + name;
			Element element = cache.get(cacheKey);
			if (null != element) {
				wrap = (WrapOutDepartment) element.getObjectValue();
			} else {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business business = new Business(emc);
					String department = business.department().getWithName(name);
					if (StringUtils.isNotEmpty(department)) {
						Department o = emc.fetchAttribute(department, Department.class, "name", "display", "superior");
						wrap = this.wrapOutDepartment(o, emc);
						wrap.setDepartmentList(this.wrapOutSubDirectDepartment(o, business));
						wrap.setIdentityList(this.wrapOutSubDirectIdentity(o, business));
						cache.put(new Element(cacheKey, wrap));
					}
				}
			}
			result.setData(wrap);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据Person取得所有的身份和对应的部门.以及所有的CompanyDuty和DepartmentDuty", response = WrapOutComplexPerson.class)
	@GET
	@Path("person/{name}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getPerson(@PathParam("name") String name) {
		ActionResult<WrapOutComplexPerson> result = new ActionResult<>();
		WrapOutComplexPerson wrap = null;
		try {
			Ehcache cache = ApplicationCache.instance().getCache(Person.class);
			String cacheKey = "complex#getPerson#" + name;
			Element element = cache.get(cacheKey);
			if (null != element) {
				wrap = (WrapOutComplexPerson) element.getObjectValue();
			} else {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business business = new Business(emc);
					String person = business.person().getWithName(name);
					if (StringUtils.isNotEmpty(person)) {
						wrap = wrapOutPerson(business, person);
						cache.put(new Element(cacheKey, wrap));
					}
				}
			}
			if (null != wrap) {
				WrapOutOnline online = ThisApplication.context().applications()
						.getQuery(x_collaboration_assemble_websocket.class,
								"online/person/" + URLEncoder.encode(wrap.getName(), "UTF-8"))
						.getData(WrapOutOnline.class);
				wrap.setOnlineStatus(online.getOnlineStatus());
			}
			result.setData(wrap);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	private WrapOutComplexPerson wrapOutPerson(Business business, String id) throws Exception {
		WrapOutComplexPerson wrap;
		Person p = business.entityManagerContainer().find(id, Person.class);
		wrap = new WrapOutComplexPerson();
		p.copyTo(wrap);
		List<WrapOutIdentity> wis = new ArrayList<>();
		List<WrapOutCompanyDuty> wcds = new ArrayList<>();
		List<WrapOutDepartmentDuty> wdds = new ArrayList<>();
		for (Identity o : business.entityManagerContainer().fetchAttribute(business.identity().listWithPerson(id),
				Identity.class, "name", "display", "department")) {
			WrapOutIdentity w = new WrapOutIdentity();
			w.setName(o.getName());
			w.setDisplay(o.getDisplay());
			Department d = business.entityManagerContainer().fetchAttribute(o.getDepartment(), Department.class,
					"name");
			if (null != d) {
				w.setDepartment(d.getName());
			}
			wis.add(w);
			for (CompanyDuty cd : business.entityManagerContainer().fetchAttribute(
					business.companyDuty().listWithIdentity(o.getId()), CompanyDuty.class, "name", "company")) {
				WrapOutCompanyDuty wcd = new WrapOutCompanyDuty();
				wcd.setCompany(business.entityManagerContainer().fetchAttribute(cd.getCompany(), Company.class, "name")
						.getName());
				wcd.setName(cd.getName());
				wcds.add(wcd);
			}
			for (DepartmentDuty dd : business.entityManagerContainer().fetchAttribute(
					business.departmentDuty().listWithIdentity(o.getId()), DepartmentDuty.class, "name",
					"department")) {
				WrapOutDepartmentDuty wdd = new WrapOutDepartmentDuty();
				wdd.setDepartment(business.entityManagerContainer()
						.fetchAttribute(dd.getDepartment(), Department.class, "name").getName());
				wdd.setName(dd.getName());
				wdds.add(wdd);
			}
		}
		Collections.sort(wis, new Comparator<WrapOutIdentity>() {
			public int compare(WrapOutIdentity o1, WrapOutIdentity o2) {
				return ObjectUtils.compare(o1.getName(), o2.getName(), true);
			}
		});
		wrap.setIdentityList(wis);
		wrap.setCompanyDutyList(wcds);
		wrap.setDepartmentDutyList(wdds);
		return wrap;
	}

	private WrapOutDepartment wrapOutDepartment(Department o, EntityManagerContainer emc) throws Exception {
		WrapOutDepartment wrap = new WrapOutDepartment();
		wrap.setName(o.getName());
		wrap.setDisplay(o.getDisplay());
		if (StringUtils.isNotEmpty(o.getSuperior())) {
			Department superior = emc.fetchAttribute(o.getSuperior(), Department.class, "name");
			if (null != superior) {
				wrap.setSuperior(superior.getName());
			}
		}
		return wrap;
	}

	private WrapOutCompany wrapOutCompany(Company o, EntityManagerContainer emc) throws Exception {
		WrapOutCompany wrap = new WrapOutCompany();
		wrap.setName(o.getName());
		wrap.setDisplay(o.getDisplay());
		if (StringUtils.isNotEmpty(o.getSuperior())) {
			Company superior = emc.fetchAttribute(o.getSuperior(), Company.class, "name");
			if (null != superior) {
				wrap.setSuperior(superior.getName());
			}
		}
		return wrap;
	}

	private List<WrapOutCompany> wrapOutSubDirectCompany(Company o, Business business) throws Exception {
		List<WrapOutCompany> list = new ArrayList<>();
		List<String> ids = business.company().listSubDirect(o.getId());
		for (Company c : business.entityManagerContainer().fetchAttribute(ids, Company.class, "name")) {
			WrapOutCompany w = new WrapOutCompany();
			w.setName(c.getName());
			w.setCompanyCount(business.company().countSubDirect(c.getId()));
			w.setDepartmentCount(business.department().countTopWithCompany(c.getId()));
			list.add(w);
		}
		Collections.sort(list, new Comparator<WrapOutCompany>() {
			public int compare(WrapOutCompany o1, WrapOutCompany o2) {
				return ObjectUtils.compare(o1.getName(), o2.getName(), true);
			}
		});
		return list;
	}

	private List<WrapOutDepartment> wrapOutSubDirectDepartment(Company o, Business business) throws Exception {
		List<WrapOutDepartment> list = new ArrayList<>();
		List<String> ids = business.department().listTopWithCompany(o.getId());
		for (Department d : business.entityManagerContainer().fetchAttribute(ids, Department.class, "name")) {
			WrapOutDepartment w = new WrapOutDepartment();
			w.setName(d.getName());
			w.setDepartmentCount(business.department().countSubDirect(d.getId()));
			w.setIdentityCount(business.identity().countSubDirectWithDepartment(d.getId()));
			list.add(w);
		}
		Collections.sort(list, new Comparator<WrapOutDepartment>() {
			public int compare(WrapOutDepartment o1, WrapOutDepartment o2) {
				return ObjectUtils.compare(o1.getName(), o2.getName(), true);
			}
		});
		return list;
	}

	private List<WrapOutDepartment> wrapOutSubDirectDepartment(Department o, Business business) throws Exception {
		List<WrapOutDepartment> list = new ArrayList<>();
		List<String> ids = business.department().listSubDirect(o.getId());
		for (Department d : business.entityManagerContainer().fetchAttribute(ids, Department.class, "name")) {
			WrapOutDepartment w = new WrapOutDepartment();
			w.setName(d.getName());
			w.setDepartmentCount(business.department().countSubDirect(d.getId()));
			w.setIdentityCount(business.identity().countSubDirectWithDepartment(d.getId()));
			list.add(w);
		}
		Collections.sort(list, new Comparator<WrapOutDepartment>() {
			public int compare(WrapOutDepartment o1, WrapOutDepartment o2) {
				return ObjectUtils.compare(o1.getName(), o2.getName(), true);
			}
		});
		return list;
	}

	private List<WrapOutIdentity> wrapOutSubDirectIdentity(Department o, Business business) throws Exception {
		List<WrapOutIdentity> list = new ArrayList<>();
		List<String> ids = business.identity().listWithDepartment(o.getId());
		for (Identity i : business.entityManagerContainer().fetchAttribute(ids, Identity.class, "name", "person")) {
			WrapOutIdentity w = new WrapOutIdentity();
			w.setName(i.getName());
			if (StringUtils.isNotEmpty(i.getPerson())) {
				Person person = business.entityManagerContainer().fetchAttribute(i.getPerson(), Person.class, "name");
				if (null != person) {
					w.setPerson(person.getName());
				}
			}
			list.add(w);
		}
		Collections.sort(list, new Comparator<WrapOutIdentity>() {
			public int compare(WrapOutIdentity o1, WrapOutIdentity o2) {
				return ObjectUtils.compare(o1.getName(), o2.getName(), true);
			}
		});
		return list;
	}
}