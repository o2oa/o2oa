package com.x.server.console.action;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.openjpa.jdbc.conf.JDBCConfiguration;
import org.apache.openjpa.jdbc.meta.MappingTool;
import org.apache.openjpa.lib.util.Options;
import org.apache.openjpa.persistence.EntityManagerImpl;
import org.apache.openjpa.persistence.OpenJPAEntityManagerFactory;
import org.apache.openjpa.persistence.OpenJPAPersistence;

import com.x.base.core.container.factory.PersistenceXmlHelper;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ClassLoaderTools;

public class Ddl {

	private static Logger logger = LoggerFactory.getLogger(Ddl.class);

	/* 初始化完成 */
	@SuppressWarnings("unchecked")
	public boolean execute(String type) throws Exception {

		new Thread(() -> {
			try {
				ClassLoader cl = ClassLoaderTools.urlClassLoader(ClassLoader.getSystemClassLoader(), true, true, true,
						true, Config.dir_local_temp_classes().toPath());
				Thread.currentThread().setContextClassLoader(cl);
				String flag = "build";
				if (StringUtils.equalsIgnoreCase(type, "createDB")) {
					flag = "createDB";
				} else if (StringUtils.equalsIgnoreCase(type, "dropDB")) {
					flag = "dropDB";
				} else if (StringUtils.equalsIgnoreCase(type, "drop")) {
					flag = "drop";
				}
				List<String> containerEntityNames = new ArrayList<>();
				containerEntityNames.addAll((List<String>) Config.resource(Config.RESOURCE_CONTAINERENTITYNAMES));
				File persistence = new File(Config.dir_local_temp_classes(), "META-INF/persistence_sql.xml");
				PersistenceXmlHelper.writeForDdl(persistence.getAbsolutePath());
				OpenJPAEntityManagerFactory emf = OpenJPAPersistence.createEntityManagerFactory("enhance",
						persistence.getName());
				EntityManagerImpl em = (EntityManagerImpl) emf.createEntityManager();
				String[] arguments = null;
				String[] args = null;
				Options opts = new Options();
				if (StringUtils.equals(flag, "build") || StringUtils.equals(flag, "drop")
						|| StringUtils.equals(flag, "createDB") || StringUtils.equals(flag, "dropDB")) {
					arguments = new String[4];
					File file = new File(Config.dir_local_temp_sql(true), flag + ".sql");
					arguments[0] = "-schemaAction";
					arguments[1] = flag;
					arguments[2] = "-sql";
					arguments[3] = file.getAbsolutePath();
					args = opts.setFromCmdLine(arguments);
					MappingTool.run((JDBCConfiguration) em.getConfiguration(), args, opts, null);
					logger.print("file : {}.", file.getAbsolutePath());
				} else if (StringUtils.equals(flag, "deleteTableContents")) {
				}
				em.close();
				emf.close();

			} catch (Exception e) {
				logger.error(e);
			}
		}).start();
		return true;
	}

}