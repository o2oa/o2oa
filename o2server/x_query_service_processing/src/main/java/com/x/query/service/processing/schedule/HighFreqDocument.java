package com.x.query.service.processing.schedule;

import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.quartz.JobExecutionContext;

import com.google.gson.Gson;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.bean.tuple.Pair;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DateTools;
import com.x.cms.core.entity.message.DocumentEvent;
import com.x.query.core.entity.index.State;
import com.x.query.core.express.index.Indexs;
import com.x.query.service.processing.Business;
import com.x.query.service.processing.IndexWriteQueue;
import com.x.query.service.processing.ThisApplication;
import com.x.query.service.processing.index.Doc;
import com.x.query.service.processing.index.DocFunction;
import com.x.query.service.processing.index.HighFreq;

public class HighFreqDocument extends HighFreq {

    private static final Logger LOGGER = LoggerFactory.getLogger(HighFreqDocument.class);

    @Override
    public void schedule(JobExecutionContext jobExecutionContext) throws Exception {
        Date startAt = new Date();
        State state = null;
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Business business = new Business(emc);
            state = business.state().getState(Config.query().index().getMode(), Indexs.TYPE_DOCUMENT, State.FREQ_HIGH,
                    Config.node());
        }
        List<DocumentEvent> list;
        int count = 0;
        AtomicInteger deleteCount = new AtomicInteger(0);
        AtomicInteger indexCount = new AtomicInteger(0);
        do {
            list = this.list(state, DocumentEvent.class, Config.query().index().getHighFreqDocumentBatchSize());
            if (!list.isEmpty()) {
                update(list, indexCount);
                delete(list, deleteCount);
                Optional<Map.Entry<Date, List<Pair<String, Date>>>> optional = list.stream()
                        .map(o -> Pair.of(o.getId(), DateUtils.truncate(o.getCreateTime(), Calendar.SECOND)))
                        .collect(Collectors.groupingBy(Pair::second))
                        .entrySet().stream()
                        .max(Comparator.comparingLong(o -> o.getKey().getTime()));
                if (optional.isPresent()) {
                    state.logLatestIds(optional.get().getKey(),
                            optional.get().getValue().stream().map(Pair::first).collect(Collectors.toList()));
                }
                try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
                    new Business(emc).state().updateState(state);
                }
                count += list.size();
            }
        } while ((!list.isEmpty())
                && ((new Date().getTime() - startAt.getTime()) < Config.query().index()
                        .getHighFreqDocumentMaxMinutes() * 1000 * 60)
                && count < Config.query().index().getHighFreqDocumentMaxCount());
        LOGGER.info(
                "high freq index document start at:{}, elapsed:{} minutes, total count:{}, write:{}, delete:{}.",
                DateTools.format(startAt), ((new Date()).getTime() - startAt.getTime()) / (1000 * 60), count,
                indexCount.get(), deleteCount.get());
    }

    private void delete(List<DocumentEvent> list, AtomicInteger deleteCount) {
        list.stream().filter(o -> StringUtils.equalsIgnoreCase(o.getType(), DocumentEvent.TYPE_DELETE))
                .map(o -> Pair.of(o.getAppInfo(), o.getDocument())).collect(Collectors.groupingBy(Pair::first))
                .entrySet().forEach(o -> {
                    try {
                        List<String> ids = o.getValue().stream().map(Pair::second).distinct()
                                .collect(Collectors.toList());
                        ThisApplication.indexWriteQueue.send(new IndexWriteQueue.DeleteMessage(ids,
                                Indexs.CATEGORY_CMS, o.getKey(), null));
                        ThisApplication.indexWriteQueue.send(new IndexWriteQueue.DeleteMessage(ids,
                                Indexs.CATEGORY_SEARCH, Indexs.KEY_ENTIRE, null));
                        deleteCount.addAndGet(ids.size());
                    } catch (Exception e) {
                        LOGGER.error(e);
                    }
                });
    }

    private void update(List<DocumentEvent> list, AtomicInteger indexCount) {
        list.stream().filter(o -> StringUtils.equalsAnyIgnoreCase(o.getType(), DocumentEvent.TYPE_CREATE,
                DocumentEvent.TYPE_UPDATE)).collect(Collectors.groupingBy(DocumentEvent::getAppInfo)).entrySet()
                .stream().forEach(o -> {
                    Map<String, String> map = new LinkedHashMap<>();
                    o.getValue().stream().forEach(p -> map.put(p.getAppInfo(), p.getDocument()));
                    List<Doc> docs = index(map.values().stream().collect(Collectors.toList()),
                            DocFunction.wrapDocument);
                    try {
                        ThisApplication.indexWriteQueue.send(new IndexWriteQueue.UpdateMessage(docs,
                                Indexs.CATEGORY_CMS, o.getKey(), true));
                        ThisApplication.indexWriteQueue.send(new IndexWriteQueue.UpdateMessage(docs,
                                Indexs.CATEGORY_SEARCH, Indexs.KEY_ENTIRE, false));
                        indexCount.addAndGet(map.entrySet().size());
                    } catch (Exception e) {
                        LOGGER.error(e);
                    }
                });
    }

}