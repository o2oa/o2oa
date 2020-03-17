package com.x.query.assemble.designer;

import java.io.File;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.PersistenceXmlHelper;
import com.x.base.core.entity.dynamic.DynamicEntity;
import com.x.base.core.entity.dynamic.DynamicEntityBuilder;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.base.core.project.tools.DefaultCharset;
import com.x.base.core.project.tools.JarTools;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.StringTools;
import com.x.organization.core.express.Organization;
import com.x.query.assemble.designer.factory.QueryFactory;
import com.x.query.assemble.designer.factory.RevealFactory;
import com.x.query.assemble.designer.factory.StatFactory;
import com.x.query.assemble.designer.factory.StatementFactory;
import com.x.query.assemble.designer.factory.TableFactory;
import com.x.query.assemble.designer.factory.ViewFactory;
import com.x.query.assemble.designer.jaxrs.table.ExceptionCompileError;
import com.x.query.core.entity.Query;
import com.x.query.core.entity.schema.Enhance;
import com.x.query.core.entity.schema.Statement;
import com.x.query.core.entity.schema.Table;

public class Business {

	private static Logger logger = LoggerFactory.getLogger(Business.class);

	private static final String DOT_JAR = ".jar";

	private EntityManagerContainer emc;

	public Business(EntityManagerContainer emc) throws Exception {
		this.emc = emc;
	}

	public EntityManagerContainer entityManagerContainer() {
		return this.emc;
	}

	private Organization organization;

	public Organization organization() throws Exception {
		if (null == this.organization) {
			this.organization = new Organization(ThisApplication.context());
		}
		return organization;
	}

	private QueryFactory query;

	public QueryFactory query() throws Exception {
		if (null == this.query) {
			this.query = new QueryFactory(this);
		}
		return query;
	}

	private ViewFactory view;

	public ViewFactory view() throws Exception {
		if (null == this.view) {
			this.view = new ViewFactory(this);
		}
		return view;
	}

	private TableFactory table;

	public TableFactory table() throws Exception {
		if (null == this.table) {
			this.table = new TableFactory(this);
		}
		return table;
	}

	private StatementFactory statement;

	public StatementFactory statement() throws Exception {
		if (null == this.statement) {
			this.statement = new StatementFactory(this);
		}
		return statement;
	}

	private StatFactory stat;

	public StatFactory stat() throws Exception {
		if (null == this.stat) {
			this.stat = new StatFactory(this);
		}
		return stat;
	}

	private RevealFactory reveal;

	public RevealFactory reveal() throws Exception {
		if (null == this.reveal) {
			this.reveal = new RevealFactory(this);
		}
		return reveal;
	}

	public boolean controllable(EffectivePerson effectivePerson) throws Exception {
		boolean result = false;
		if (effectivePerson.isManager() || (this.organization().person().hasRole(effectivePerson,
				OrganizationDefinition.Manager, OrganizationDefinition.QueryManager))) {
			result = true;
		}
		return result;
	}

	public boolean editable(EffectivePerson effectivePerson, Query o) throws Exception {
		boolean result = false;
		if (effectivePerson.isManager() || (this.organization().person().hasRole(effectivePerson,
				OrganizationDefinition.Manager, OrganizationDefinition.QueryManager))) {
			result = true;
		}
		if (!result && (null != o)) {
			if (effectivePerson.isPerson(o.getControllerList()) || effectivePerson.isPerson(o.getCreatorPerson())) {
				result = true;
			}
		}
		return result;
	}

	public boolean editable(EffectivePerson effectivePerson, Table o) throws Exception {
		boolean result = false;
		if (effectivePerson.isManager() || (this.organization().person().hasRole(effectivePerson,
				OrganizationDefinition.Manager, OrganizationDefinition.QueryManager))) {
			result = true;
		}
		if (!result && (null != o)) {
			if (ListTools.isEmpty(o.getEditPersonList()) && ListTools.isEmpty(o.getEditUnitList())) {
				result = true;
				if (!result) {
					if (effectivePerson.isPerson(o.getEditPersonList())) {
						result = true;
					}
					if (!result && ListTools.isNotEmpty(o.getEditUnitList())) {
						List<String> units = this.organization().unit()
								.listWithPerson(effectivePerson.getDistinguishedName());
						if (ListTools.containsAny(units, o.getEditUnitList())) {
							result = true;
						}
					}
				}
			}
		}
		return result;
	}

	public boolean executable(EffectivePerson effectivePerson, Statement o) throws Exception {
		boolean result = false;
		if (null != o) {
			if (ListTools.isEmpty(o.getExecutePersonList()) && ListTools.isEmpty(o.getExecuteUnitList())) {
				result = true;
			}
			if (!result) {
				if (effectivePerson.isManager()
						|| (this.organization().person().hasRole(effectivePerson, OrganizationDefinition.Manager,
								OrganizationDefinition.QueryManager))
						|| effectivePerson.isPerson(o.getExecutePersonList())) {
					result = true;
				}
				if ((!result) && ListTools.isNotEmpty(o.getExecuteUnitList())) {
					List<String> units = this.organization().unit()
							.listWithPerson(effectivePerson.getDistinguishedName());
					if (ListTools.containsAny(units, o.getExecuteUnitList())) {
						result = true;
					}
				}
			}
		}
		return result;
	}

	public boolean buildAllTable() throws Exception {
		boolean result = false;
		File dir = new File(Config.dir_local_temp_dynamic(true), StringTools.uniqueToken());
		FileUtils.forceMkdir(dir);
		File src = new File(dir, "src");
		FileUtils.forceMkdir(src);
		File target = new File(dir, "target");
		FileUtils.forceMkdir(target);
		File resources = new File(dir, "resources");
		FileUtils.forceMkdir(resources);
		List<Table> tables = emc.listEqual(Table.class, Table.status_FIELDNAME, Table.STATUS_build);
		/* 产生用于创建persistence.xml */
		List<String> classNames = new ArrayList<>();
		for (Table table : tables) {
			try {
				emc.beginTransaction(Table.class);
				if (StringUtils.isNotEmpty(table.getData())) {
					DynamicEntity dynamicEntity = XGsonBuilder.instance().fromJson(table.getData(),
							DynamicEntity.class);
					dynamicEntity.setName(table.getName());
					DynamicEntityBuilder builder = new DynamicEntityBuilder(dynamicEntity, src);
					builder.build();
					classNames.add(dynamicEntity.className());
				}
				table.setBuildSuccess(true);
				emc.commit();
			} catch (Exception e) {
				logger.error(e);
			}
		}

		if (!classNames.isEmpty()) {

			PersistenceXmlHelper.directWrite(new File(resources, "META-INF/persistence.xml").getAbsolutePath(),
					classNames);

			List<File> classPath = new ArrayList<>();
			classPath.addAll(FileUtils.listFiles(Config.dir_commons_ext(), FileFilterUtils.suffixFileFilter(DOT_JAR),
					DirectoryFileFilter.INSTANCE));
			classPath.addAll(FileUtils.listFiles(Config.dir_store_jars(), FileFilterUtils.suffixFileFilter(DOT_JAR),
					DirectoryFileFilter.INSTANCE));

			JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
			StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null,
					DefaultCharset.charset_utf_8);

			fileManager.setLocation(StandardLocation.CLASS_OUTPUT, Arrays.asList(target));
			fileManager.setLocation(StandardLocation.SOURCE_PATH, Arrays.asList(src, resources));
			fileManager.setLocation(StandardLocation.CLASS_PATH, classPath);

			Iterable<JavaFileObject> res = fileManager.list(StandardLocation.SOURCE_PATH, DynamicEntity.CLASS_PACKAGE,
					EnumSet.of(JavaFileObject.Kind.SOURCE), true);

			DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();

			StringWriter out = new StringWriter();

			if (!compiler.getTask(out, fileManager, diagnostics, null, null, res).call()) {
				for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
					out.append("Error on line " + diagnostic.getLineNumber() + " in " + diagnostic).append('\n');
				}
				throw new ExceptionCompileError(out.toString());
			}

			result = true;

			fileManager.close();

			this.enhance(target, resources);

			File jar = new File(Config.dir_dynamic_jars(true), DynamicEntity.JAR_NAME + DOT_JAR);

			JarTools.jar(target, jar);
		}
		return result;
	}

	private void enhance(File target, File resources) throws Exception {

		File commandJavaFile = null;
		if (SystemUtils.IS_OS_AIX) {
			commandJavaFile = new File(Config.dir_jvm_aix(), "bin/java");
		} else if (SystemUtils.IS_OS_LINUX) {
			if (Config.dir_jvm_neokylin_loongson().exists()) {
				commandJavaFile = new File(Config.dir_jvm_neokylin_loongson(), "bin/java");
			} else {
				commandJavaFile = new File(Config.dir_jvm_linux(), "bin/java");
			}
		} else if (SystemUtils.IS_OS_MAC) {
			commandJavaFile = new File(Config.dir_jvm_macos(), "bin/java");
		} else {
			commandJavaFile = new File(Config.dir_jvm_windows(), "bin/java.exe");
		}

		List<String> paths = new ArrayList<>();

		paths.add(Config.dir_store_jars().getAbsolutePath() + File.separator + "*");
		paths.add(Config.dir_commons_ext().getAbsolutePath() + File.separator + "*");
		paths.add(target.getAbsolutePath());
		paths.add(resources.getAbsolutePath());

		String command = commandJavaFile.getAbsolutePath() + " -classpath \""
				+ StringUtils.join(paths, File.pathSeparator) + "\" " + Enhance.class.getName() + " \""
				+ target.getAbsolutePath() + "\"";

		logger.debug("enhance command:{}.", command);

		ProcessBuilder processBuilder = new ProcessBuilder();

		if (SystemUtils.IS_OS_AIX) {
			processBuilder.command("sh", "-c", command);
		} else if (SystemUtils.IS_OS_LINUX) {
			processBuilder.command("sh", "-c", command);
		} else if (SystemUtils.IS_OS_MAC) {
			processBuilder.command("sh", "-c", command);
		} else {
			processBuilder.command("cmd", "/c", command);
		}

		Process process = processBuilder.start();

		// process.waitFor();

		String resp = IOUtils.toString(process.getErrorStream(), DefaultCharset.charset_utf_8);

		process.destroy();

		logger.info("enhance result:{}.", resp);

	}
}