package com.x.query.assemble.surface.jaxrs.search;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapString;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

class ActionTest extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionTest.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String q) throws Exception {

		ActionResult<Wo> result = new ActionResult<>();

//		Analyzer analyzer = new HanLPAnalyzer();
//
//		QueryParser titleParser = new QueryParser("title", analyzer);
//		QueryParser summaryParser = new QueryParser("summary", analyzer);
//		QueryParser bodyParser = new QueryParser("body", analyzer);
//
//		BooleanQuery.Builder analyzerQuery = new BooleanQuery.Builder();
//
//		analyzerQuery.add(titleParser.parse(q), BooleanClause.Occur.SHOULD);
//		analyzerQuery.add(summaryParser.parse(q), BooleanClause.Occur.SHOULD);
//		analyzerQuery.add(bodyParser.parse(q), BooleanClause.Occur.SHOULD);
//
//		BooleanQuery.Builder query = new BooleanQuery.Builder();
//
//		query.add(analyzerQuery.build(), BooleanClause.Occur.SHOULD);
//
////		SolrRequestHandler solrRequestHandler = new SolrRequestHandler();
////		FacetComponent FacetComponent = new FacetComponent();
//		Optional<Directory> optional = Business.config().searchDirectory();
//		try (DirectoryReader reader = DirectoryReader.open(optional.get())) {
//			IndexSearcher searcher = new IndexSearcher(reader);
//
//			// searcher.search(null, null)
//			// GroupingSearch groupingSearch = new
//			// GroupingSearch(CustomIndex.FIELD_APPLICATIONNAME);
//			TopDocs topDocs = searcher.search(query.build(), 100);
//			if (null != topDocs.scoreDocs) {
//				System.out.println(topDocs.totalHits);
//				for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
//					System.out.println(scoreDoc.doc + "-> " + reader.document(scoreDoc.doc).get("title"));
//				}
//			}
//
//		}

//		List<String> list = Business.config().subDirectoryPathOfCategoryType(CustomIndex.CATEGORY_PROCESSPLATFORM,
//				CustomIndex.TYPE_WORKCOMPLETED);
//		System.out.println("!!!!!!!!!!!!!!!!!!");
//		System.out.println(gson.toJson(list));
//		System.out.println("!!!!!!!!!!!!!!!!!!");
		return result;
	}

	// @Schema(name =
	// "com.x.custom.index.assemble.control.jaxrs.touch.ActionTest$Wo")
	public static class Wo extends WrapString {

		private static final long serialVersionUID = -6815067359344499966L;

	}

}