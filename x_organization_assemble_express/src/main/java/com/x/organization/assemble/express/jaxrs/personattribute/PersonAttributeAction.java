package com.x.organization.assemble.express.jaxrs.personattribute;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.application.jaxrs.AbstractJaxrsAction;
import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.ResponseFactory;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.organization.assemble.express.Business;
import com.x.organization.assemble.express.jaxrs.wrapout.WrapOutPersonAttribute;
import com.x.organization.core.entity.Person;
import com.x.organization.core.entity.PersonAttribute;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

@Path("personattribute")
public class PersonAttributeAction extends AbstractJaxrsAction {

	private Ehcache cache = ApplicationCache.instance().getCache(PersonAttribute.class, Person.class);

	@HttpMethodDescribe(value = "按名称和人员名称查找部门属性.", response = WrapOutPersonAttribute.class)
	@GET
	@Path("{name}/person/{personName}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getWithPerson(@PathParam("name") String name, @PathParam("personName") String personName) {
		ActionResult<WrapOutPersonAttribute> result = new ActionResult<>();
		WrapOutPersonAttribute wrap = null;
		try {
			String cacheKey = "getWithPerson#" + name + "#" + personName;
			Element element = cache.get(cacheKey);
			if (null != element) {
				wrap = (WrapOutPersonAttribute) element.getObjectValue();
			} else {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business business = new Business(emc);
					/* 按名称查找Person */
					String person = business.person().getWithName(personName);
					if (StringUtils.isNotEmpty(person)) {
						/* 查找PersonAttribute */
						String personAttributeId = business.personAttribute().getWithName(name, person);
						if (StringUtils.isNotEmpty(personAttributeId)) {
							PersonAttribute o = emc.find(personAttributeId, PersonAttribute.class);
							wrap = business.personAttribute().wrap(o);
							cache.put(new Element(cacheKey, wrap));
						}
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

	@SuppressWarnings("unchecked")
	@HttpMethodDescribe(value = "查找人员所有属性.", response = WrapOutPersonAttribute.class)
	@GET
	@Path("list/person/{name}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listWithPerson(@PathParam("name") String name) {
		ActionResult<List<WrapOutPersonAttribute>> result = new ActionResult<>();
		List<WrapOutPersonAttribute> wraps = new ArrayList<>();
		try {
			String cacheKey = "listWithPerson#" + name;
			Element element = cache.get(cacheKey);
			if (null != element) {
				wraps = (List<WrapOutPersonAttribute>) element.getObjectValue();
			} else {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business business = new Business(emc);
					/* 按名称查找Person */
					String person = business.person().getWithName(name);
					if (StringUtils.isNotEmpty(person)) {
						/* 查找PersonAttribute */
						List<String> ids = business.personAttribute().listWithPerson(person);
						for (PersonAttribute o : emc.list(PersonAttribute.class, ids)) {
							WrapOutPersonAttribute wrap = business.personAttribute().wrap(o);
							wraps.add(wrap);
						}
						business.personAttribute().sort(wraps);
						cache.put(new Element(cacheKey, wraps));
					}
				}
			}
			result.setData(wraps);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
}