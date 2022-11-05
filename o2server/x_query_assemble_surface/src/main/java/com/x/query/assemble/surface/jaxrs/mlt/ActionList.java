package com.x.query.assemble.surface.jaxrs.mlt;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queries.mlt.MoreLikeThis;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;

import com.google.gson.JsonElement;
import com.hankcs.lucene.HanLPAnalyzer;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.bean.tuple.Pair;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.query.assemble.surface.Business;
import com.x.query.core.express.index.Indexs;
import com.x.query.core.express.jaxrs.mlt.ActionPostWi;
import com.x.query.core.express.jaxrs.mlt.ActionPostWo;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionList extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionList.class);

    ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {

        LOGGER.info("execute:{}.", effectivePerson::getDistinguishedName);

        ActionResult<List<Wo>> result = new ActionResult<>();
        List<Wo> wos = new ArrayList<>();
        result.setData(wos);

        Wi wi = this.convertToWrapIn(jsonElement, Wi.class);

        String category = wi.getCategory();

        List<String> readers = new ArrayList<>();
        Optional<Pair<String, String>> optionalSummary = Optional.empty();
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Business business = new Business(emc);
            optionalSummary = this.summary(business, category, wi.getFlag());
            if (optionalSummary.isEmpty()) {
                return result;
            }
            String person = business.index().who(effectivePerson, wi.getPerson());
            readers = business.index().determineReaders(person, "", "", "");
        }
        Optional<Query> readersQuery = Indexs.readersQuery(readers);
        Optional<Query> idQuery = this.idQuery(optionalSummary.get().first());
        BooleanQuery.Builder builder = new BooleanQuery.Builder();
        if (readersQuery.isPresent()) {
            builder.add(readersQuery.get(), BooleanClause.Occur.MUST);
        }
        if (idQuery.isPresent()) {
            builder.add(idQuery.get(), BooleanClause.Occur.MUST_NOT);
        }
        Optional<Directory> optional = Indexs.searchDirectory(true);
        if (optional.isEmpty()) {
            return result;
        }
        try (DirectoryReader reader = DirectoryReader.open(optional.get())) {
            IndexSearcher searcher = new IndexSearcher(reader);
            MoreLikeThis mlt = new MoreLikeThis(reader);
            mlt.setAnalyzer(new HanLPAnalyzer());
            try (StringReader sr = new StringReader(optionalSummary.get().second())) {
                builder.add(mlt.like(Indexs.FIELD_SUMMARY, sr), BooleanClause.Occur.MUST);
            }
            Query query = builder.build();
            TopDocs topDocs = searcher.search(query, Config.query().index().getMltSize());
            ScoreDoc[] scoreDocs = topDocs.scoreDocs;
            if (null != scoreDocs) {
                for (ScoreDoc scoreDoc : scoreDocs) {
                    org.apache.lucene.document.Document document = reader.document(scoreDoc.doc);
                    Wo wo = new Wo();
                    wo.setTitle(document.get(Indexs.FIELD_TITLE));
                    wo.setFlag(document.get(Indexs.FIELD_ID));
                    wo.setCategory(document.get(Indexs.FIELD_CATEGORY));
                    wo.setType(document.get(Indexs.FIELD_TYPE));
                    wo.setKey(document.get(Indexs.FIELD_KEY));
                    wo.setScore(scoreDoc.score);
                    wos.add(wo);
                }
            }
        }
        return result;
    }

    @Schema(name = "com.x.query.assemble.surface.jaxrs.mlt.ActionPost$Wi")
    public class Wi extends ActionPostWi {

        private static final long serialVersionUID = -4646809016933808952L;

    }

    @Schema(name = "com.x.query.assemble.surface.jaxrs.mlt.ActionPost$Wo")
    public class Wo extends ActionPostWo {

    }

}