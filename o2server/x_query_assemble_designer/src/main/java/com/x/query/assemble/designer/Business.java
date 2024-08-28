package com.x.query.assemble.designer;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
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
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
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
import com.x.query.assemble.designer.factory.ImportModelFactory;
import com.x.query.assemble.designer.factory.ProcessFactory;
import com.x.query.assemble.designer.factory.QueryFactory;
import com.x.query.assemble.designer.factory.StatFactory;
import com.x.query.assemble.designer.factory.StatementFactory;
import com.x.query.assemble.designer.factory.TableFactory;
import com.x.query.assemble.designer.factory.ViewFactory;
import com.x.query.assemble.designer.jaxrs.table.ExceptionCompileError;
import com.x.query.core.entity.Query;
import com.x.query.core.entity.schema.Enhance;
import com.x.query.core.entity.schema.Statement;
import com.x.query.core.entity.schema.Table;

/**
 * @author sword
 */
public class Business {

	private static final Logger LOGGER = LoggerFactory.getLogger(Business.class);

	private static URLClassLoader dynamicEntityClassLoader = null;

	public static ClassLoader getDynamicEntityClassLoader() throws IOException, URISyntaxException {
		if (null == dynamicEntityClassLoader) {
			refreshDynamicEntityClassLoader();
		}
		return dynamicEntityClassLoader;
	}

	public static synchronized void refreshDynamicEntityClassLoader() throws IOException, URISyntaxException {
		List<URL> urlList = new ArrayList<>();
		IOFileFilter filter = new WildcardFileFilter(DynamicEntity.JAR_PREFIX + "*.jar");
		for (File o : FileUtils.listFiles(Config.dir_dynamic_jars(true), filter, null)) {
			urlList.add(o.toURI().toURL());
		}
		URL[] urls = new URL[urlList.size()];
		if (null != dynamicEntityClassLoader) {
			dynamicEntityClassLoader.close();
		}
		dynamicEntityClassLoader = URLClassLoader.newInstance(urlList.toArray(urls),
				null != ThisApplication.context() ? ThisApplication.context().servletContext().getClassLoader()
						: Thread.currentThread().getContextClassLoader());
	}

	public static final String DOT_JAR = ".jar";

	private EntityManagerContainer emc;

	public Business(EntityManagerContainer emc) {
		this.emc = emc;
	}

	public EntityManagerContainer entityManagerContainer() {
		return this.emc;
	}

	/**
	 * 获取包含自定义jar包的ClassLoader
	 * 
	 * @param refresh
	 * @return
	 * @throws Exception
	 */

	private Organization organization;

	public Organization organization() {
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

	private ImportModelFactory importModel;

	public ImportModelFactory importModel() throws Exception {
		if (null == this.importModel) {
			this.importModel = new ImportModelFactory(this);
		}
		return importModel;
	}

	private StatFactory stat;

	public StatFactory stat() throws Exception {
		if (null == this.stat) {
			this.stat = new StatFactory(this);
		}
		return stat;
	}

	private ProcessFactory process;

	public ProcessFactory process() throws Exception {
		if (null == this.process) {
			this.process = new ProcessFactory(this);
		}
		return process;
	}

	public boolean controllable(EffectivePerson effectivePerson) throws Exception {
		boolean result = false;
		if (effectivePerson.isManager() || BooleanUtils.isTrue(this.organization().person().hasRole(effectivePerson,
				OrganizationDefinition.Manager, OrganizationDefinition.QueryManager))) {
			result = true;
		}
		return result;
	}

	public boolean editable(EffectivePerson effectivePerson, Query o) throws Exception {
		boolean result = false;
		if (effectivePerson.isManager() || BooleanUtils.isTrue(this.organization().person().hasRole(effectivePerson,
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
		if (effectivePerson.isManager() || BooleanUtils.isTrue(this.organization().person().hasRole(effectivePerson,
				OrganizationDefinition.Manager, OrganizationDefinition.QueryManager))) {
			return true;
		}
		if (null != o) {
			if (ListTools.isEmpty(o.getEditPersonList()) && ListTools.isEmpty(o.getEditUnitList())) {
				return true;
			}
			if (ListTools.isNotEmpty(o.getEditPersonList()) && effectivePerson.isPerson(o.getEditPersonList())) {
				return true;
			}
			if (ListTools.isNotEmpty(o.getEditUnitList())) {
				List<String> units = this.organization().unit().listWithPerson(effectivePerson.getDistinguishedName());
				if (ListTools.containsAny(units, o.getEditUnitList())) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean executable(EffectivePerson effectivePerson, Statement o) throws Exception {
		if (null == o) {
			return false;
		}
		if (ListTools.isEmpty(o.getExecutePersonList()) && ListTools.isEmpty(o.getExecuteUnitList())) {
			return true;
		}
		if (effectivePerson.isManager()
				|| BooleanUtils.isTrue(this.organization().person().hasRole(effectivePerson,
						OrganizationDefinition.Manager, OrganizationDefinition.QueryManager))
				|| effectivePerson.isPerson(o.getExecutePersonList())) {
			return true;
		}
		if (ListTools.isNotEmpty(o.getExecuteUnitList())) {
			List<String> units = this.organization().unit().listWithPerson(effectivePerson.getDistinguishedName());
			if (ListTools.containsAny(units, o.getExecuteUnitList())) {
				return true;
			}
		}
		return false;
	}

	public boolean buildQuery(String query) throws Exception {
		boolean result = false;
		List<Table> tables = emc.listEqualAndEqual(Table.class, Table.STATUS_FIELDNAME, Table.STATUS_BUILD,
				Table.QUERY_FIELDNAME, query);
		if (ListTools.isEmpty(tables)) {
			return true;
		}
		File dir = new File(Config.dir_local_temp_dynamic(true), StringTools.uniqueToken());
		FileUtils.forceMkdir(dir);
		File src = new File(dir, "src");
		FileUtils.forceMkdir(src);
		File target = new File(dir, "target");
		FileUtils.forceMkdir(target);
		File resources = new File(dir, "resources");
		FileUtils.forceMkdir(resources);
		List<String> classNames = new ArrayList<>();
		for (Table t : tables) {
			try {
				emc.beginTransaction(Table.class);
				if (StringUtils.isNotEmpty(t.getData())) {
					DynamicEntity dynamicEntity = XGsonBuilder.instance().fromJson(t.getData(), DynamicEntity.class);
					dynamicEntity.setName(t.getName());
					DynamicEntityBuilder builder = new DynamicEntityBuilder(dynamicEntity, src);
					builder.build();
					classNames.add(dynamicEntity.className());
				}
				t.setBuildSuccess(true);
				emc.commit();
			} catch (Exception e) {
				LOGGER.error(e);
			}
		}
		if (!classNames.isEmpty()) {
			PersistenceXmlHelper.directWriteDynamicEnhance(
					new File(resources, "META-INF/persistence.xml").getAbsolutePath(), classNames);
			List<File> classPath = new ArrayList<>();
			classPath.addAll(FileUtils.listFiles(Config.pathCommonsExt(true).toFile(),
					FileFilterUtils.suffixFileFilter(DOT_JAR), DirectoryFileFilter.INSTANCE));
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

			DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
			StringWriter out = new StringWriter();

			if (BooleanUtils.isFalse(compiler.getTask(out, fileManager, diagnostics, null, null, res).call())) {
				for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
					out.append("Error on line " + diagnostic.getLineNumber() + " in " + diagnostic).append('\n');
				}
				throw new ExceptionCompileError(out.toString());
			}

			result = true;
			fileManager.close();
			this.enhance(target, resources);

			File jar = new File(Config.dir_dynamic_jars(true), DynamicEntity.JAR_PREFIX + query + DOT_JAR);
			JarTools.jar(target, jar);
			LOGGER.info("build table reload jar:{}", jar.getName());
		}
		FileUtils.cleanDirectory(dir);
		return result;
	}

	private void enhance(File target, File resources) throws Exception {

		List<String> paths = new ArrayList<>();

		paths.add(Config.dir_store_jars().getAbsolutePath() + File.separator + "*");
		paths.add(Config.pathCommonsExt(true).toString() + File.separator + "*");
		paths.add(target.getAbsolutePath());
		paths.add(resources.getAbsolutePath());

		String command = Config.command_java_path().toString() + " -classpath \""
				+ StringUtils.join(paths, File.pathSeparator) + "\" " + Enhance.class.getName() + " \""
				+ target.getAbsolutePath() + "\"";

		LOGGER.debug("enhance command:{}.", () -> command);

		java.lang.ProcessBuilder processBuilder = new java.lang.ProcessBuilder();

		if (SystemUtils.IS_OS_WINDOWS) {
			processBuilder.command("cmd", "/c", command);
		} else {
			processBuilder.command("sh", "-c", command);
		}

		Process p = processBuilder.start();

		String resp = IOUtils.toString(p.getErrorStream(), DefaultCharset.charset_utf_8);

		p.destroy();

		LOGGER.info("enhance result:{}.", () -> resp);

	}

	public static void reloadClassLoader() {
		try {
			EntityManagerContainerFactory.close();
			Business.refreshDynamicEntityClassLoader();
			ThisApplication.context().initDatas(true, Business.getDynamicEntityClassLoader());
		} catch (Exception e) {
			LOGGER.error(e);
		}
	}

}
