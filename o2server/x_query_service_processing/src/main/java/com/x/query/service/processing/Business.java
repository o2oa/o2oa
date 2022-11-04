package com.x.query.service.processing;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.fs.CommonConfigurationKeysPublic;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.RemoteIterator;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.solr.store.hdfs.HdfsDirectory;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.dynamic.DynamicEntity;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.Query;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.organization.core.express.Organization;
import com.x.query.service.processing.factory.ProcessFactory;
import com.x.query.service.processing.factory.QueryFactory;

public class Business {

    private static final Logger LOGGER = LoggerFactory.getLogger(Business.class);

    private EntityManagerContainer emc;

    public Business() {
    }

    private static ClassLoader dynamicEntityClassLoader = null;

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
        dynamicEntityClassLoader = URLClassLoader.newInstance(urlList.toArray(urls),
                null != ThisApplication.context() ? ThisApplication.context().servletContext().getClassLoader()
                        : Thread.currentThread().getContextClassLoader());
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

    public boolean isManager(EffectivePerson effectivePerson) throws Exception {
        if (effectivePerson.isManager() || (BooleanUtils
                .isTrue(this.organization().person().hasRole(effectivePerson, OrganizationDefinition.QueryManager,
                        OrganizationDefinition.Manager)))) {
            return true;
        }
        return false;
    }

    public boolean isProcessManager(EffectivePerson effectivePerson) throws Exception {
        if (effectivePerson.isManager() || (BooleanUtils.isTrue(
                this.organization().person().hasRole(effectivePerson, OrganizationDefinition.ProcessPlatformManager,
                        OrganizationDefinition.Manager)))) {
            return true;
        }
        return false;
    }

    public boolean isServiceManager(EffectivePerson effectivePerson) throws Exception {
        if (effectivePerson.isManager() || (BooleanUtils
                .isTrue(this.organization().person().hasRole(effectivePerson, OrganizationDefinition.ServiceManager,
                        OrganizationDefinition.Manager)))) {
            return true;
        }
        return false;
    }

    public boolean isCmsManager(EffectivePerson effectivePerson) throws Exception {
        if (effectivePerson.isManager()) {
            return true;
        }
        if (this.organization().person().hasRole(effectivePerson, OrganizationDefinition.CMSManager,
                OrganizationDefinition.Manager)) {
            return true;
        }
        return false;
    }

    public boolean isPortalManager(EffectivePerson effectivePerson) throws Exception {
        if (effectivePerson.isManager()
                && (this.organization().person().hasRole(effectivePerson, OrganizationDefinition.PortalManager,
                        OrganizationDefinition.Manager))) {
            return true;
        }
        return false;
    }

    private ProcessFactory process;

    public ProcessFactory process() throws Exception {
        if (null == this.process) {
            this.process = new ProcessFactory(this);
        }
        return process;
    }

    public static class Index {

        public static final String CATEGORY_PROCESSPLATFORM = "processPlatform";
        public static final String CATEGORY_CMS = "cms";

        public static final String TYPE_WORKCOMPLETED = "workCompleted";
        public static final String TYPE_DOCUMENT = "document";

        public static final String FIELD_ID = "id";
        public static final String FIELD_CATEGORY = "category";
        public static final String FIELD_INDEXTIME = "indexTime";
        public static final String FIELD_TITLE = "title";
        public static final String FIELD_SUMMARY = "summary";
        public static final String FIELD_BODY = "body";
        public static final String FIELD_ATTACHMENT = "attachment";
        public static final String FIELD_CREATETIME = "createTime";
        public static final String FIELD_UPDATETIME = "updateTime";
        public static final String FIELD_CREATETIMEMONTH = "createTimeMonth";
        public static final String FIELD_UPDATETIMEMONTH = "updateTimeMonth";
        public static final String FIELD_READERS = "readers";
        public static final String FIELD_CREATORPERSON = "creatorPerson";
        public static final String FIELD_CREATORUNIT = "creatorUnit";
        public static final String FIELD_CREATORUNITLEVELNAME = "creatorUnitLevelName";
        public static final String FIELD_APPLICATION = "application";
        public static final String FIELD_APPLICATIONNAME = "applicationName";
        public static final String FIELD_APPLICATIONALIAS = "applicationAlias";
        public static final String FIELD_PROCESS = "processName";
        public static final String FIELD_PROCESSNAME = "processName";
        public static final String FIELD_PROCESSALIAS = "processAlias";
        public static final String FIELD_JOB = "job";
        public static final String FIELD_SERIAL = "serial";
        public static final String FIELD_EXPIRED = "expired";
        public static final String FIELD_EXPIRETIME = "expireTime";

        public static final String FIELD_APPID = "appId";
        public static final String FIELD_APPNAME = "appName";
        public static final String FIELD_APPALIAS = "appAlias";
        public static final String FIELD_CATEGORYID = "categoryId";
        public static final String FIELD_CATEGORYNAME = "categoryName";
        public static final String FIELD_CATEGORYALIAS = "categoryAlias";
        public static final String FIELD_DESCRIPTION = "description";
        public static final String FIELD_PUBLISHTIME = "publishTime";
        public static final String FIELD_MODIFYTIME = "modifyTime";

        public static final String FIELD_HIGHLIGHTING = "highlighting";
        public static final String READERS_SYMBOL_ALL = "ALL";

        public static final List<String> FACET_FIELDS = Stream.<String>of(FIELD_CATEGORY,
                FIELD_CREATETIMEMONTH, FIELD_UPDATETIMEMONTH, FIELD_APPLICATIONNAME,
                FIELD_PROCESSNAME, FIELD_APPNAME, FIELD_CATEGORYNAME,
                FIELD_CREATORPERSON, FIELD_CREATORUNIT)
                .collect(Collectors.toUnmodifiableList());

        private static final List<String> FIXED_DATE_FIELDS = Stream
                .<String>of(FIELD_INDEXTIME, FIELD_CREATETIME, FIELD_UPDATETIME)
                .collect(Collectors.toUnmodifiableList());

        public static final String FIELD_TYPE_STRING = "string";
        public static final String FIELD_TYPE_STRINGS = "strings";
        public static final String FIELD_TYPE_BOOLEAN = "boolean";
        public static final String FIELD_TYPE_BOOLEANS = "booleans";
        public static final String FIELD_TYPE_NUMBER = "number";
        public static final String FIELD_TYPE_NUMBERS = "numbers";
        public static final String FIELD_TYPE_DATE = "date";
        public static final String FIELD_TYPE_DATES = "dates";

        public static final String PREFIX_FIELD_DATA = "data_";
        public static final String PREFIX_FIELD_DATA_STRING = PREFIX_FIELD_DATA + FIELD_TYPE_STRING + "_";
        public static final String PREFIX_FIELD_DATA_STRINGS = PREFIX_FIELD_DATA + FIELD_TYPE_STRINGS + "_";
        public static final String PREFIX_FIELD_DATA_BOOLEAN = PREFIX_FIELD_DATA + FIELD_TYPE_BOOLEAN + "_";
        public static final String PREFIX_FIELD_DATA_BOOLEANS = PREFIX_FIELD_DATA + FIELD_TYPE_BOOLEANS + "_";
        public static final String PREFIX_FIELD_DATA_NUMBER = PREFIX_FIELD_DATA + FIELD_TYPE_NUMBER + "_";
        public static final String PREFIX_FIELD_DATA_NUMBERS = PREFIX_FIELD_DATA + FIELD_TYPE_NUMBERS + "_";
        public static final String PREFIX_FIELD_DATA_DATE = PREFIX_FIELD_DATA + FIELD_TYPE_DATE + "_";
        public static final String PREFIX_FIELD_DATA_DATES = PREFIX_FIELD_DATA + FIELD_TYPE_DATES + "_";

        public static final String DIRECTORY_SEARCH = "search";

        public static final Integer DEFAULT_MAX_HITS = 1000000;

        public static boolean deleteDirectory(String category, String type, String key) {
            try {
                if (StringUtils.equals(Config.query().index().getMode(), Query.Index.MODE_HDFSDIRECTORY)) {
                    org.apache.hadoop.conf.Configuration configuration = hdfsConfiguration();
                    try (org.apache.hadoop.fs.FileSystem fileSystem = org.apache.hadoop.fs.FileSystem
                            .get(configuration)) {
                        org.apache.hadoop.fs.Path path = hdfsBase();
                        path = new org.apache.hadoop.fs.Path(path, category);
                        path = new org.apache.hadoop.fs.Path(path, type);
                        path = new org.apache.hadoop.fs.Path(path, key);
                        if (fileSystem.exists(path) && fileSystem.getFileStatus(path).isDirectory()) {
                            return fileSystem.delete(path, true);
                        }
                    }
                } else {
                    java.nio.file.Path path = Config.path_local_repository_index(true).resolve(category).resolve(type)
                            .resolve(key);
                    if (Files.exists(path) && Files.isDirectory(path)) {
                        Files.walkFileTree(path, new SimpleFileVisitor<java.nio.file.Path>() {
                            @Override
                            public FileVisitResult postVisitDirectory(java.nio.file.Path dir, IOException exc)
                                    throws IOException {
                                Files.delete(dir);
                                return FileVisitResult.CONTINUE;
                            }

                            @Override
                            public FileVisitResult visitFile(java.nio.file.Path file, BasicFileAttributes attrs)
                                    throws IOException {
                                Files.delete(file);
                                return FileVisitResult.CONTINUE;
                            }
                        });
                        return true;
                    }
                }
                return false;
            } catch (Exception e) {
                LOGGER.error(e);
            }
            return false;
        }

        @SuppressWarnings("deprecation")
        public static Optional<Directory> directory(String category, String type, String key, boolean checkExists) {
            try {
                if (StringUtils.equals(Config.query().index().getMode(), Query.Index.MODE_HDFSDIRECTORY)) {
                    org.apache.hadoop.conf.Configuration configuration = hdfsConfiguration();
                    org.apache.hadoop.fs.Path path = hdfsBase();
                    path = new org.apache.hadoop.fs.Path(path, category);
                    path = new org.apache.hadoop.fs.Path(path, type);
                    path = new org.apache.hadoop.fs.Path(path, key);
                    if (checkExists) {
                        try (org.apache.hadoop.fs.FileSystem fileSystem = org.apache.hadoop.fs.FileSystem
                                .get(configuration)) {
                            if (!fileSystem.exists(path)) {
                                return Optional.empty();
                            }
                        }
                    }
                    return Optional.of(new HdfsDirectory(path, configuration));
                } else {
                    java.nio.file.Path path = Config.path_local_repository_index(true).resolve(category).resolve(type)
                            .resolve(key);
                    if (checkExists && (!Files.exists(path))) {
                        return Optional.empty();
                    }
                    return Optional.of(FSDirectory.open(path));
                }
            } catch (Exception e) {
                LOGGER.error(e);
            }
            return Optional.empty();
        }

        @SuppressWarnings("deprecation")
        public static Optional<Directory> searchDirectory(boolean checkExists) {
            try {
                if (StringUtils.equals(Config.query().index().getMode(), Query.Index.MODE_HDFSDIRECTORY)) {
                    org.apache.hadoop.conf.Configuration configuration = hdfsConfiguration();
                    org.apache.hadoop.fs.Path path = hdfsBase();
                    path = new org.apache.hadoop.fs.Path(path, DIRECTORY_SEARCH);
                    if (checkExists) {
                        try (org.apache.hadoop.fs.FileSystem fileSystem = org.apache.hadoop.fs.FileSystem
                                .get(configuration)) {
                            if (!fileSystem.exists(path)) {
                                return Optional.empty();
                            }
                        }
                    }
                    return Optional.of(new HdfsDirectory(path, configuration));
                } else {
                    java.nio.file.Path path = Config.path_local_repository_index(true).resolve(DIRECTORY_SEARCH);
                    if (checkExists && (!Files.exists(path))) {
                        return Optional.empty();
                    }
                    return Optional.of(FSDirectory.open(path));
                }
            } catch (Exception e) {
                LOGGER.error(e);
            }
            return Optional.empty();
        }

        private static org.apache.hadoop.conf.Configuration hdfsConfiguration() throws Exception {
            org.apache.hadoop.conf.Configuration configuration = new org.apache.hadoop.conf.Configuration();
            configuration.set(CommonConfigurationKeysPublic.FS_DEFAULT_NAME_KEY,
                    Config.query().index().getHdfsDirectoryDefaultFS());
            return configuration;
        }

        private static org.apache.hadoop.fs.Path hdfsBase() throws IllegalArgumentException, Exception {
            return new org.apache.hadoop.fs.Path(Config.query().index().getDirectoryPath());
        }

        public static List<String> subDirectoryPathOfCategoryType(String category, String type) {
            try {
                if (StringUtils.equals(Config.query().index().getMode(), Query.Index.MODE_HDFSDIRECTORY)) {
                    return subDirectoryPathOfCategoryTypeHdfs(category, type);
                } else {
                    java.nio.file.Path path = Config.path_local_repository_index(true).resolve(category).resolve(type);
                    if (Files.exists(path)) {
                        List<String> list = new ArrayList<>();
                        try (Stream<java.nio.file.Path> stream = Files.walk(path, 1)) {
                            stream.filter(o -> {
                                try {
                                    return !Files.isSameFile(o, path);
                                } catch (IOException e) {
                                    LOGGER.error(e);
                                }
                                return false;
                            }).filter(Files::isDirectory).map(path::relativize).map(java.nio.file.Path::toString)
                                    .forEach(list::add);
                        }
                        return list;
                    }
                }
            } catch (Exception e) {
                LOGGER.error(e);
            }
            return new ArrayList<>();
        }

        private static List<String> subDirectoryPathOfCategoryTypeHdfs(String category, String type)
                throws Exception {
            List<String> list = new ArrayList<>();
            org.apache.hadoop.conf.Configuration configuration = hdfsConfiguration();
            try (org.apache.hadoop.fs.FileSystem fileSystem = org.apache.hadoop.fs.FileSystem.get(configuration)) {
                org.apache.hadoop.fs.Path path = hdfsBase();
                path = new org.apache.hadoop.fs.Path(path, category);
                path = new org.apache.hadoop.fs.Path(path, type);
                if (fileSystem.exists(path)) {
                    RemoteIterator<LocatedFileStatus> fileStatusListIterator = fileSystem.listLocatedStatus(path);
                    while (fileStatusListIterator.hasNext()) {
                        LocatedFileStatus locatedFileStatus = fileStatusListIterator.next();
                        if (locatedFileStatus.isDirectory()) {
                            list.add(locatedFileStatus.getPath().getName());
                        }
                    }
                }
            }
            return list;
        }
    }

}
