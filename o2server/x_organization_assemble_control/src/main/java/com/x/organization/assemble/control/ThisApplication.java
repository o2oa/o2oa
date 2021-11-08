package com.x.organization.assemble.control;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.Context;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.organization.core.entity.Identity;
import com.x.organization.core.entity.Person;
import org.apache.commons.lang3.BooleanUtils;

import java.util.List;

/**
 * @author sword
 */
public class ThisApplication {

	private static Logger logger = LoggerFactory.getLogger(ThisApplication.class);

	private static final String SYSTEM_MANAGER = "系统管理员";

	private ThisApplication() {
	}

	protected static Context context;

	public static Context context() {
		return context;
	}

	public static void init() {
		try {
			CacheManager.init(context.clazz().getSimpleName());
			initMaintainer();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void initMaintainer(){
		try {
			if(Config.person().getMaintainer()!=null
					&& BooleanUtils.isTrue(Config.person().getMaintainer().getEnable())){
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business business = new Business(emc);
					String name = Config.person().getMaintainer().getName().trim();
					String mobile = Config.person().getMaintainer().getMobile().trim();
					Long count = business.person().countWithNameOrMobile(name, mobile);
					if(count==null || count.longValue() == 0){
						List<Person> personList = emc.listEqual(Person.class, Person.name_FIELDNAME, SYSTEM_MANAGER);
						if(ListTools.isNotEmpty(personList)){
							Person person = personList.get(0);
							List<Identity> identityList = business.entityManagerContainer().listEqual(Identity.class,
									Identity.person_FIELDNAME, person.getId());
							emc.beginTransaction(Person.class);
							emc.beginTransaction(Identity.class);
							person.setName(name);
							person.setMobile(mobile);
							emc.check(person, CheckPersistType.all);
							if (ListTools.isNotEmpty(identityList)) {
								for (Identity identity : identityList) {
									identity.setName(person.getName());
									emc.check(identity, CheckPersistType.all);
								}
							}
							emc.commit();
						}
					}
				}
			}
		} catch (Exception e) {
			logger.warn("init maintainer error：{}", e.getMessage());
		}
	}

	public static void destroy() {
		try {
			CacheManager.shutdown();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
