package com.x.collect.service.transmit.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.gson.XGsonBuilder;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.CommonActionResult;
import com.x.base.core.http.connection.HttpConnection;
import com.x.base.core.project.server.Collect;
import com.x.base.core.project.server.Config;
import com.x.organization.core.entity.Person;
import com.x.organization.core.entity.Person_;

public class CollectTask implements Runnable {

	public void run() {
		try {
			Collect collect = Config.collect();
			if (BooleanUtils.isTrue(collect.getEnable())) {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					List<String> mobiles = this.listMobile(emc);
					String address = "http://collect.o2server.io:20080/o2_collect_assemble/jaxrs/collect/transmit/receive";
					Map<String, Object> body = new TreeMap<>();
					body.put("name", collect.getName());
					body.put("password", collect.getPassword());
					body.put("mobileList", mobiles);
					body.put("centerProxyHost", Config.centerServer().getProxyHost());
					body.put("centerProxyPort", Config.centerServer().getProxyPort());
					CommonActionResult result = HttpConnection.putAsObject(address, null, XGsonBuilder.toJson(body),
							CommonActionResult.class);
					if (ActionResult.Type.error.equals(result.getType())) {
						throw new Exception("collect update error:" + result);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private List<String> listMobile(EntityManagerContainer emc) throws Exception {
		EntityManager em = emc.get(Person.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Person> root = cq.from(Person.class);
		cq.select(root.get(Person_.mobile));
		List<String> list = em.createQuery(cq).getResultList();
		List<String> mobiles = new ArrayList<>();
		for (String str : list) {
			if (StringUtils.isNoneEmpty(str)) {
				mobiles.add(str);
			}
		}
		return mobiles;
	}

}