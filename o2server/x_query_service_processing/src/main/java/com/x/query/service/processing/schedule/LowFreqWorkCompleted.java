package com.x.query.service.processing.schedule;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.time.DateUtils;
import org.quartz.JobExecutionContext;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.bean.tuple.Pair;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DateTools;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.query.core.entity.index.State;
import com.x.query.core.express.index.Indexs;
import com.x.query.service.processing.Business;
import com.x.query.service.processing.IndexWriteQueue;
import com.x.query.service.processing.ThisApplication;
import com.x.query.service.processing.index.Doc;
import com.x.query.service.processing.index.DocFunction;
import com.x.query.service.processing.index.LowFreq;

public class LowFreqWorkCompleted extends LowFreq {

    private static final Logger LOGGER = LoggerFactory.getLogger(LowFreqWorkCompleted.class);

    @Override
    public void schedule(JobExecutionContext jobExecutionContext) throws Exception {
        Date startAt = new Date();
        State state = null;
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Business business = new Business(emc);
            state = business.state().getState(Config.query().index().getMode(), Indexs.TYPE_WORKCOMPLETED,
                    State.FREQ_LOW,
                    Config.node());
        }
        List<Pair<String, Date>> list;
        int count = 0;
        do {
            list = this.list(state, WorkCompleted.class, Config.query().index().getLowFreqWorkCompletedBatchSize());
            if (!list.isEmpty()) {
                List<Pair<String, Doc>> pairs = index(
                        list.stream().map(Pair::first).collect(Collectors.toList()),
                        DocFunction.wrapWorkCompleted);
                pairs.stream().collect(Collectors.groupingBy(Pair::first)).entrySet().stream().forEach(o -> {
                    try {
                        List<Doc> docs = o.getValue().stream().map(Pair::second).collect(Collectors.toList());
                        ThisApplication.indexWriteQueue.send(new IndexWriteQueue.UpdateMessage(
                                docs, Indexs.CATEGORY_PROCESSPLATFORM, o.getKey(), true));
                        ThisApplication.indexWriteQueue.send(new IndexWriteQueue.UpdateMessage(
                                docs,
                                Indexs.CATEGORY_SEARCH, Indexs.KEY_ENTIRE, false));
                    } catch (Exception e) {
                        LOGGER.error(e);
                    }
                });
                count += list.size();
                Optional<Map.Entry<Date, List<Pair<String, Date>>>> optional = list.stream()
                        .map(o -> Pair.of(o.first(), DateUtils.truncate(o.second(), Calendar.SECOND)))
                        .collect(Collectors.groupingBy(Pair::second)).entrySet().stream()
                        .max(Comparator.comparingLong(o -> o.getKey().getTime()));
                if (optional.isPresent()) {
                    state.logLatestIds(optional.get().getKey(),
                            optional.get().getValue().stream().map(Pair::first).collect(Collectors.toList()));
                }
            } else {
                state.setLatestIdList(new ArrayList<>());
                state.setLatestUpdateTime(null);
            }
            try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
                new Business(emc).state().updateState(state);
            }
        } while ((!list.isEmpty())
                && ((new Date().getTime() - startAt.getTime()) < Config.query().index()
                        .getLowFreqWorkCompletedMaxMinutes() * 1000 * 60)
                && count < Config.query().index().getLowFreqWorkCompletedMaxCount());
        LOGGER.info("low freq index workCompleted start at:{}, elapsed:{} minutes, count:{}.",
                DateTools.format(startAt), ((new Date()).getTime() - startAt.getTime()) / (1000 * 60), count);
    }

}