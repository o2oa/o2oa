package com.x.query.service.processing.schedule;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.BooleanUtils;
import org.quartz.JobExecutionContext;

import com.x.base.core.project.bean.tuple.Pair;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DateTools;
import com.x.query.core.entity.index.State;
import com.x.query.service.processing.index.FreqDocument;

public class HighFreqDocument extends FreqDocument {

    private static final Logger LOGGER = LoggerFactory.getLogger(HighFreqDocument.class);

    @Override
    public void schedule(JobExecutionContext jobExecutionContext) throws Exception {
        if (BooleanUtils.isNotTrue(Config.query().index().getHighFreqDocumentEnable())) {
            return;
        }
        State state = getState(State.FREQ_HIGH);
        Date startAt = new Date();
        List<Pair<String, Date>> list;
        int count = 0;
        do {
            list = this.list(state, Config.query().index().getHighFreqDocumentMaxCount());
            if (!list.isEmpty()) {
                index(list.stream().map(Pair::first).collect(Collectors.toList()));
                count += list.size();
                state.setLatestId(list.get(list.size() - 1).first());
                state.setLatestUpdateTime(list.get(list.size() - 1).second());
                updateState(state);
            }
        } while ((!list.isEmpty())
                && ((new Date().getTime() - startAt.getTime()) < Config.query().index()
                        .getHighFreqDocumentMaxMinutes() * 1000 * 60)
                && count < Config.query().index().getHighFreqDocumentMaxCount());
        LOGGER.info("high frequency index Document start at:{}, elapsed:{} minutes, count:{}.",
                DateTools.format(startAt), ((new Date()).getTime() - startAt.getTime()) / (1000 * 60), count);
    }

}