package com.x.strategydeploy.assemble.control.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtilsBean;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.strategydeploy.assemble.control.Business;

public class PersonActionsService {
	List<String> reader_g = new ArrayList<>();
	List<String> writer_g = new ArrayList<>();

	private PropertyUtilsBean propertyUtilsBean;

	public PersonActionsService() {
		reader_g.add("战略读者@strategy_reader_g@G");
		writer_g.add("战略管理者@strategy_writer_g@G");
	}

	public <T> List<T> setActionsCheckManagerGroup(List<T> wos, String distinguishedName) throws Exception {
		List<T> result = new ArrayList<T>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			List<String> reader_persons = business.organization().person().listWithGroup(reader_g);
			List<String> writer_persons = business.organization().person().listWithGroup(writer_g);
			List<String> actions = new ArrayList<>();

			if (reader_persons.indexOf(distinguishedName) >= 0 || writer_persons.indexOf(distinguishedName) >= 0) {
				if (reader_persons.indexOf(distinguishedName) >= 0) {
					actions.add("OPEN");
					for (Object wo : wos) {
						BeanUtils.setProperty(wo, "actions", actions);
					}
				}
				if (writer_persons.indexOf(distinguishedName) >= 0) {
					actions.add("OPEN");
					actions.add("EDIT");
					actions.add("DELETE");
					for (Object wo : wos) {
						BeanUtils.setProperty(wo, "actions", actions);
					}
					result = wos;
				}
			}
		}
		return result;
	}

	public <T> List<T> setActionsCommonUser(List<T> wos, String distinguishedName) throws Exception {
		List<T> result = new ArrayList<T>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			List<String> actions = new ArrayList<>();
			actions.add("OPEN");
			for (Object wo : wos) {
				BeanUtils.setProperty(wo, "actions", actions);
			}
		}
		return result;
	}

}
