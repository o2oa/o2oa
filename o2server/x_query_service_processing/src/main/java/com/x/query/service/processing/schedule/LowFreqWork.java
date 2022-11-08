package com.x.query.service.processing.schedule;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.collections4.list.TreeList;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreMode;
import org.apache.lucene.search.SimpleCollector;
import org.apache.lucene.store.Directory;
import org.quartz.JobExecutionContext;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.bean.tuple.Pair;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DateTools;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.element.Application;
import com.x.query.core.entity.index.State;
import com.x.query.core.express.index.Indexs;
import com.x.query.service.processing.Business;
import com.x.query.service.processing.IndexWriteQueue;
import com.x.query.service.processing.ThisApplication;
import com.x.query.service.processing.index.FreqWork;

public class LowFreqWork extends FreqWork {

    private static final Logger LOGGER = LoggerFactory.getLogger(LowFreqWork.class);

    @Override
    public void schedule(JobExecutionContext jobExecutionContext) throws Exception {
        if (BooleanUtils.isNotTrue(Config.query().index().getLowFreqWorkEnable())) {
            return;
        }
        Date startAt = new Date();
        cleanup();
        State state = getState(State.FREQ_LOW);
        List<Pair<String, Date>> list;
        int count = 0;
        do {
            list = this.list(state, Config.query().index().getLowFreqWorkBatchSize());
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
                        .getLowFreqWorkMaxMinutes() * 1000 * 60)
                && count < Config.query().index().getLowFreqWorkMaxCount());
        LOGGER.info("low frequency index Work start at:{}, elapsed:{} minutes, count:{}.",
                DateTools.format(startAt), ((new Date()).getTime() - startAt.getTime()) / (1000 * 60), count);
    }

    private void cleanup() throws Exception {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Business business = new Business(emc);
            List<String> applicationIds = emc.ids(Application.class);
            applicationIds.stream().forEach(o -> {
                cleanup(business, o);
            });
        }
    }

    private void cleanup(Business business, String applicationId) {
        try {
            List<String> removes = new TreeList<>();
            List<String> jobs = new TreeList<>(business.entityManagerContainer().fetchEqual(Work.class,
                    Arrays.asList(Work.job_FIELDNAME),
                    Work.application_FIELDNAME,
                    applicationId).stream().map(Work::getJob).distinct().collect(Collectors.toList()));
            Optional<Directory> optional = Indexs.directory(Indexs.CATEGORY_PROCESSPLATFORM, applicationId, true);
            if (optional.isPresent()) {
                try (IndexReader reader = DirectoryReader.open(optional.get())) {
                    IndexSearcher searcher = new IndexSearcher(reader);
                    searcher.search(LongPoint.newExactQuery(Indexs.FIELD_COMPLETED, 0), new SimpleCollector() {
                        @Override
                        public ScoreMode scoreMode() {
                            return ScoreMode.COMPLETE_NO_SCORES;
                        }

                        @Override
                        public void collect(int doc) throws IOException {
                            String id = reader.document(doc).get(Indexs.FIELD_ID);
                            if (!jobs.contains(id)) {
                                removes.add(id);
                            }
                        }
                    });
                }
                if (!removes.isEmpty()) {
                    ThisApplication.indexWriteQueue
                            .send(new IndexWriteQueue.DeleteMessage(removes, Indexs.CATEGORY_PROCESSPLATFORM,
                                    applicationId,
                                    true));
                }
            }
        } catch (Exception e) {
            LOGGER.error(e);
        }
    }

}