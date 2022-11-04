package com.x.query.service.processing;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.x.base.core.project.Context;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.config.Config;
import com.x.query.service.processing.schedule.CrawlCms;
import com.x.query.service.processing.schedule.CrawlWork;
import com.x.query.service.processing.schedule.CrawlWorkCompleted;
import com.x.query.service.processing.schedule.HighFrequencyDocument;
import com.x.query.service.processing.schedule.HighFrequencyWorkCompleted;
import com.x.query.service.processing.schedule.LowFrequencyDocument;
import com.x.query.service.processing.schedule.LowFrequencyWorkCompleted;
import com.x.query.service.processing.schedulelocal.HighFrequencyDocumentLocal;
import com.x.query.service.processing.schedulelocal.HighFrequencyWorkCompletedLocal;
import com.x.query.service.processing.schedulelocal.LowFrequencyDocumentLocal;
import com.x.query.service.processing.schedulelocal.LowFrequencyWorkCompletedLocal;

public class ThisApplication {

    private ThisApplication() {
        // nothing
    }

    public static final IndexWriteQueue indexWriteQueue = new IndexWriteQueue();

    private static ExecutorService threadPool;

    public static ExecutorService threadPool() {
        return threadPool;
    }

    private static void initThreadPool() {
        int maximumPoolSize = Runtime.getRuntime().availableProcessors() + 1;
        ThreadFactory threadFactory = new ThreadFactoryBuilder()
                .setNameFormat(ThisApplication.class.getPackageName() + "-threadpool-%d").build();
        threadPool = new ThreadPoolExecutor(0, maximumPoolSize, 120, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1000),
                threadFactory);
    }

    protected static Context context;

    public static Context context() {
        return context;
    }

    protected static void setContext(Context context) {
        ThisApplication.context = context;
    }

    public static void init() {
        try {
            initThreadPool();
            context.startQueue(indexWriteQueue);
            CacheManager.init(context.clazz().getSimpleName());
            scheduleLowFrequencyDocument();
            scheduleLowFrequencyWorkCompleted();
            scheduleHighFrequencyDocument();
            scheduleHighFrequencyWorkCompleted();
            if (BooleanUtils.isTrue(Config.query().getCrawlWork().getEnable())) {
                context.schedule(CrawlWork.class, Config.query().getCrawlWork().getCron());
            }
            if (BooleanUtils.isTrue(Config.query().getCrawlWorkCompleted().getEnable())) {
                context.schedule(CrawlWorkCompleted.class, Config.query().getCrawlWorkCompleted().getCron());
            }
            if (BooleanUtils.isTrue(Config.query().getCrawlCms().getEnable())) {
                context.schedule(CrawlCms.class, Config.query().getCrawlCms().getCron());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void scheduleLowFrequencyDocument() throws Exception {
        if (BooleanUtils.isTrue(Config.query().index().getLowFrequencyDocumentEnable())) {
            if (StringUtils.equals(Config.query().index().getMode(), Config.query().index().MODE_LOCALDIRECTORY)) {
                context.scheduleLocal(LowFrequencyDocumentLocal.class,
                        Config.query().index().getLowFrequencyDocumentCron());
            } else {
                context.scheduleLocal(LowFrequencyDocument.class,
                        Config.query().index().getLowFrequencyDocumentCron());
            }
        }
    }

    private static void scheduleLowFrequencyWorkCompleted() throws Exception {
        if (BooleanUtils.isTrue(Config.query().index().getLowFrequencyWorkCompletedEnable())) {
            if (StringUtils.equals(Config.query().index().getMode(), Config.query().index().MODE_LOCALDIRECTORY)) {
                context.scheduleLocal(LowFrequencyWorkCompletedLocal.class,
                        Config.query().index().getLowFrequencyWorkCompletedCron());
            } else {
                context.scheduleLocal(LowFrequencyWorkCompleted.class,
                        Config.query().index().getLowFrequencyWorkCompletedCron());
            }
        }
    }

    private static void scheduleHighFrequencyDocument() throws Exception {
        if (BooleanUtils.isTrue(Config.query().index().getHighFrequencyDocumentEnable())) {
            if (StringUtils.equals(Config.query().index().getMode(), Config.query().index().MODE_LOCALDIRECTORY)) {
                context.scheduleLocal(HighFrequencyDocumentLocal.class,
                        Config.query().index().getHighFrequencyDocumentCron());
            } else {
                context.scheduleLocal(HighFrequencyDocument.class,
                        Config.query().index().getHighFrequencyDocumentCron());
            }
        }
    }

    private static void scheduleHighFrequencyWorkCompleted() throws Exception {
        if (BooleanUtils.isTrue(Config.query().index().getHighFrequencyWorkCompletedEnable())) {
            if (StringUtils.equals(Config.query().index().getMode(), Config.query().index().MODE_LOCALDIRECTORY)) {
                context.scheduleLocal(HighFrequencyWorkCompletedLocal.class,
                        Config.query().index().getHighFrequencyWorkCompletedCron());
            } else {
                context.scheduleLocal(HighFrequencyWorkCompleted.class,
                        Config.query().index().getHighFrequencyWorkCompletedCron());
            }
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
