package com.x.server.console.action;

import com.x.base.core.container.factory.PersistenceXmlHelper;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.Storage;
import com.x.base.core.entity.StorageObject;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.config.StorageMappings;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.apache.openjpa.persistence.OpenJPAPersistence;

public class CleanDiscardFile {

    private static final Logger LOGGER = LoggerFactory.getLogger(CleanDiscardFile.class);

    public boolean execute() {
        try {
            ClassLoader classLoader = EntityClassLoaderTools.concreteClassLoader();
            Thread.currentThread().setContextClassLoader(classLoader);
            Set<String> set = new TreeSet<>();
            try (ScanResult sr = new ClassGraph().addClassLoader(classLoader)
                    .enableAnnotationInfo().scan()) {
                for (ClassInfo info : sr.getClassesWithAnnotation(Storage.class.getName())) {
                    set.add(info.getName());
                }
            }
            LOGGER.info("clean discard file class size:{}.", set.size());
            this.clean(set, classLoader);
            LOGGER.info("clean discard file complete.");
            return true;
        } catch (Exception e) {
            LOGGER.error(e);
        }
        return false;
    }

    private void clean(Set<String> classSet, ClassLoader classLoader) throws Exception{
        Path xml = Paths.get(Config.dir_local_temp_classes().getAbsolutePath(), "cleanDiscardFile.xml");
        PersistenceXmlHelper.write(xml.toString(), new ArrayList<>(classSet), true, classLoader);
        StorageMappings storageMappings = Config.storageMappings();
        for (String className : classSet){
            Class<?> jpaClass = classLoader.loadClass(className);
            if(!StorageObject.class.isAssignableFrom(jpaClass)){
                continue;
            }
            @SuppressWarnings("unchecked")
            Class<? extends StorageObject> cls = (Class<? extends StorageObject>)jpaClass;
            EntityManagerFactory emf = null;
            EntityManager em = null;
            try {
                emf = OpenJPAPersistence.createEntityManagerFactory(cls.getName(), xml.getFileName().toString(),
                        PersistenceXmlHelper.properties(cls.getName(), false));
                em = emf.createEntityManager();
                List<String> ids = listIds(cls, em);
                int count = 0;
                for (String id : ids) {
                    StorageObject so = em.find(cls, id);
                    StorageMapping mapping = storageMappings.get(cls, so.getStorage());
                    if(mapping != null && !so.existContent(mapping)){
                        em.getTransaction().begin();
                        em.remove(so);
                        em.getTransaction().commit();
                        count++;
                    }
                }
                LOGGER.info("clean:{} discard file count:{}.", className, count);
            } catch (Exception e) {
                LOGGER.error(new Exception(String.format("clean:%s error.", className), e));
            } finally {
                if (null != em) {
                    em.close();
                }
                if (null != emf) {
                    emf.close();
                }
            }
        }
    }

    private <T> List<String> listIds(Class<T> cls, EntityManager em) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<String> cq = cb.createQuery(String.class);
        Root<T> root = cq.from(cls);
        cq.select(root.get(JpaObject.id_FIELDNAME));
        return em.createQuery(cq).getResultList();
    }
}
