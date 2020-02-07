package com.x.query.core.express.program;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.query.core.entity.program.Argument;

public class Arguments {

	private static final String CRAWLUPDATEWORKCOMPLETED = "crawlUpdateWorkCompleted";
	private static final String CRAWLUPDATEWORK = "crawlUpdateWork";

	public static String getCrawlUpdateWorkCompleted() throws Exception {
		return get(CRAWLUPDATEWORKCOMPLETED, String.class);
	}

	public static void setCrawlUpdateWorkCompleted(String value) throws Exception {
		set(CRAWLUPDATEWORKCOMPLETED, value, String.class);
	}

	public static String getCrawlUpdateWork() throws Exception {
		return get(CRAWLUPDATEWORK, String.class);
	}

	public static void setCrawlUpdateWork(String value) throws Exception {
		set(CRAWLUPDATEWORK, value, String.class);
	}

	private static <T> T get(String name, Class<T> cls) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Argument argument = emc.firstEqual(Argument.class, Argument.name_FIELDNAME, name);
			if (null == argument) {
				return null;
			}
			if (String.class.isAssignableFrom(cls)) {
				return (T) argument.getStringValue();
			} else {

			}
		}
		return null;
	}

	private synchronized static <T> T set(String name, Object value, Class<T> cls) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			emc.beginTransaction(Argument.class);
			Argument argument = emc.firstEqual(Argument.class, Argument.name_FIELDNAME, name);
			if (null == argument) {
				argument = new Argument();
				setValue(argument, value, cls);
				emc.persist(argument, CheckPersistType.all);
			} else {
				setValue(argument, value, cls);
			}
			emc.commit();
		}
		return null;
	}

	private static <T> void setValue(Argument argument, Object value, Class<T> cls) throws Exception {
		if (String.class.isAssignableFrom(cls)) {
			argument.setStringValue(value.toString());
		} else {
		}
	}

}
