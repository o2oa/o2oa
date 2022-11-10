package com.x.query.service.processing.schedule;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.quartz.JobExecutionContext;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.schedule.AbstractJob;
import com.x.query.core.express.index.Indexs;
import com.x.query.service.processing.IndexWriteQueue;
import com.x.query.service.processing.ThisApplication;

public class OptimizeIndex extends AbstractJob {

    private static final Logger LOGGER = LoggerFactory.getLogger(OptimizeIndex.class);

    @Override
    public void schedule(JobExecutionContext jobExecutionContext) throws Exception {
        if (BooleanUtils.isNotTrue(Config.query().index().getOptimizeIndexEnable())) {
            return;
        }
        List<String> directories = Indexs.directories();
        final Date dateThreshold = DateUtils.addDays(new Date(), -Config.query().index().getCleanupThresholdDays());
        directories.stream().forEach(o -> {
            try {
                ThisApplication.indexWriteQueue.send(new IndexWriteQueue.CleanMessage(o, dateThreshold));
            } catch (Exception e) {
                LOGGER.error(e);
            }
        });
        final Integer maxSegments = Config.query().index().getMaxSegments();
        directories.stream().forEach(o -> {
            try {
                ThisApplication.indexWriteQueue.send(new IndexWriteQueue.MergeMessage(o, maxSegments));
            } catch (Exception e) {
                LOGGER.error(e);
            }
        });
    }
}