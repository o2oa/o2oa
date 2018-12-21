package com.x.organization.assemble.express;

import java.util.regex.Pattern;

import com.x.base.core.container.EntityManagerContainer;

public abstract class AbstractFactory {

	private Business business;

	protected static Pattern person_distinguishedName_pattern = Pattern.compile("^(\\S+)\\@(\\S+)\\@P$");

	protected static Pattern personAttribute_distinguishedName_pattern = Pattern.compile("^(\\S+)\\@(\\S+)\\@PA$");

	protected static Pattern group_distinguishedName_pattern = Pattern.compile("^(\\S+)\\@(\\S+)\\@G$");

	protected static Pattern role_distinguishedName_pattern = Pattern.compile("^(\\S+)\\@(\\S+)\\@R$");

	protected static Pattern identity_distinguishedName_pattern = Pattern.compile("^(\\S+)\\@(\\S+)\\@I$");

	protected static Pattern unit_distinguishedName_pattern = Pattern.compile("^(\\S+)\\@(\\S+)\\@U$");

	protected static Pattern unitAttribute_distinguishedName_pattern = Pattern.compile("^(\\S+)\\@(\\S+)\\@UA$");

	protected static Pattern unitDuty_distinguishedName_pattern = Pattern.compile("^(\\S+)\\@(\\S+)\\@UD$");

	public AbstractFactory(Business business) throws Exception {
		try {
			if (null == business) {
				throw new Exception("business can not be null.");
			}
			this.business = business;
		} catch (Exception e) {
			throw new Exception("can not instantiating factory.");
		}
	}

	public EntityManagerContainer entityManagerContainer() throws Exception {
		return this.business.entityManagerContainer();
	}

}