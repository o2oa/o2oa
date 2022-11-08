package com.x.query.service.processing.schedule;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.quartz.JobExecutionContext;

import com.x.base.core.project.bean.tuple.Pair;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DateTools;
import com.x.query.core.entity.index.State;
import com.x.query.core.express.index.Indexs;
import com.x.query.service.processing.IndexWriteQueue;
import com.x.query.service.processing.ThisApplication;
import com.x.query.service.processing.index.FreqWorkCompleted;

public class LowFreqWorkCompleted extends FreqWorkCompleted {

    private static final Logger LOGGER = LoggerFactory.getLogger(LowFreqWorkCompleted.class);

    @Override
    public void schedule(JobExecutionContext jobExecutionContext) throws Exception {
        if (BooleanUtils.isNotTrue(Config.query().index().getLowFreqWorkCompletedEnable())) {
            return;
        }
        Date startAt = new Date();
        cleanup();
        State state = getState(State.FREQ_LOW);
        List<Pair<String, Date>> list;
        int count = 0;
        do {
            list = this.list(state, Config.query().index().getLowFreqWorkCompletedBatchSize());
            if (!list.isEmpty()) {
                index(list.stream().map(Pair::first).collect(Collectors.toList()));
                count += list.size();
                state.setLatestId(list.get(list.size() - 1).first());
                state.setLatestUpdateTime(list.get(list.size() - 1).second());
            } else {
                state.setLatestId(null);
                state.setLatestUpdateTime(null);
            }
            updateState(state);
        } while ((!list.isEmpty())
                && ((new Date().getTime() - startAt.getTime()) < Config.query().index()
                        .getLowFreqWorkCompletedMaxMinutes() * 1000 * 60)
                && count < Config.query().index().getLowFreqWorkCompletedMaxCount());
        LOGGER.info("low frequency index WorkCompleted start at:{}, elapsed:{} minutes, count:{}.",
                DateTools.format(startAt), ((new Date()).getTime() - startAt.getTime()) / (1000 * 60), count);
    }

    private void cleanup() throws Exception {
        Date threshold = DateUtils.addDays(new Date(),
                -Config.query().index().getWorkCompletedCleanupThresholdDays());
        Indexs
                .subDirectoryPathOfCategoryType(Indexs.CATEGORY_PROCESSPLATFORM,
                        Indexs.TYPE_WORKCOMPLETED)
                .stream().forEach(o -> {
                    try {
                        ThisApplication.indexWriteQueue
                                .send(new IndexWriteQueue.CleanMessage(Indexs.CATEGORY_PROCESSPLATFORM,
                                        Indexs.TYPE_WORKCOMPLETED, o, true, threshold));
                        ThisApplication.indexWriteQueue.send(new IndexWriteQueue.CheckMessage(
                                Indexs.CATEGORY_PROCESSPLATFORM, Indexs.TYPE_WORKCOMPLETED, o));
                    } catch (Exception e) {
                        LOGGER.error(e);
                    }
                });
    }

}