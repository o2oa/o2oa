package com.x.cms.assemble.search.service;  
  
import java.util.List;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import com.x.cms.assemble.search.Elasticsearch_Index_DB;
import com.x.cms.assemble.search.bean.WrapDocument;
import com.x.cms.assemble.search.es.Criteria;
import com.x.cms.assemble.search.service.inf.IDocumentDao;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;
import io.searchbox.core.Delete;
import io.searchbox.core.DeleteByQuery;
import io.searchbox.core.DocumentResult;
import io.searchbox.core.Get;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import io.searchbox.core.Update;
  
public class DocumentJestDaoImpl implements IDocumentDao {  
  
    private JestClient client;  
  
    public DocumentJestDaoImpl(JestClient client) {  
        this.client = client;  
    }
//  
//    public static void main(String[] args) {  
//        JestClient client = EsJestClient.getClient();
//        DocumentJestDaoImpl documentDao = new DocumentJestDaoImpl(client);  
//  
//        WrapDocument document = new WrapDocument();  
//        document.setId(2);  
//        document.setTitle("foo1");  
//        document.setAuthor("bar1");  
//        document.setPublishTime(new Date());  
//        document.setTags(new String[]{"tag5", "tag2"});  
//  
//        //保存  
//        documentDao.insert(document);  
//        System.out.println(documentDao.searchById(2));  
//    }  
  
    /** 
     * 插入 
     * 
     * @param doc 文档 
     * @return 是佛插入成功 
     */  
    public boolean insert(WrapDocument doc) {  
        try {  
            DocumentResult result = client.execute(new Index.Builder(doc)  
                    .index(Elasticsearch_Index_DB.CMS_INDICES)  
                    .type(Elasticsearch_Index_DB.CMS_TYPE)  
                    .refresh(true)  
                    .build());  
            return result.isSucceeded();  
        } catch (Exception e) {  
            throw new RuntimeException("insert exception", e);  
        }  
    }  
  
    /** 
     * 替换 
     * 
     * @param doc 文档 
     * @return 是否执行成功 
     */  
   
    public boolean replace(WrapDocument doc) {  
        return update(doc);  
    }  
  
    /** 
     * 更新 
     * 
     * @param doc 文档 
     * @return 是否更新成功 
     */  
   
    public boolean update(WrapDocument doc) {  
        try {  
            DocumentResult result = client.execute(new Update.Builder(doc)  
                    .index(Elasticsearch_Index_DB.CMS_INDICES)  
                    .type(Elasticsearch_Index_DB.CMS_TYPE)  
                    .refresh(true)  
                    .build());  
            return result.isSucceeded();  
        } catch (Exception e) {  
            throw new RuntimeException("update exception", e);  
        }  
    }  
  
    /** 
     * 删除 
     * 
     * @param id 文档id 
     * @return 是否执行成功 
     */  
   
    public boolean delete(long id) {  
        try {  
            DocumentResult result = client.execute(new Delete.Builder(String.valueOf(id))  
                    .index(Elasticsearch_Index_DB.CMS_INDICES)  
                    .type(Elasticsearch_Index_DB.CMS_TYPE)  
                    .build());  
            return result.isSucceeded();  
        } catch (Exception e) {  
            throw new RuntimeException("delete exception", e);  
        }  
    }  
  
    /** 
     * 根据ID查询 
     * 
     * @param id id 
     * @return 文档 
     */  
   
    public WrapDocument searchById(long id) {  
        try {  
            DocumentResult result = client.execute(new Get.Builder(Elasticsearch_Index_DB.CMS_INDICES, String.valueOf(id))  
                    .type(Elasticsearch_Index_DB.CMS_TYPE)  
                    .build());  
            return result.getSourceAsObject(WrapDocument.class);  
        } catch (Exception e) {  
            throw new RuntimeException("searchById exception", e);  
        }  
    }  
  
    /** 
     * 条件查询 
     * 
     * @param criterias 条件列表 
     * @return 结果集 
     */  
   
    public List<WrapDocument> search(List<Criteria> criterias) {  
        try {  
            SearchResult result = client.execute(new Search.Builder(buildSearch(criterias).toString())  
                    // multiple index or types can be added.  
                    .addIndex(Elasticsearch_Index_DB.CMS_INDICES)  
                    .addType(Elasticsearch_Index_DB.CMS_TYPE)  
                    .build());  
            return result.getSourceAsObjectList(WrapDocument.class, false);  
  
        } catch (Exception e) {  
            throw new RuntimeException("search exception", e);  
        }  
    }  
  
    private SearchSourceBuilder buildSearch(List<Criteria> criterias) {  
        //指定查询的库表  
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();  
  
        if (criterias != null && !criterias.isEmpty()) {  
            //构建查询条件必须嵌入filter中！  
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();  
            for (Criteria c : criterias) {  
                boolQueryBuilder.filter(QueryBuilders.termQuery(c.getFieldName(), c.getFieldValue()));  
            }  

            searchSourceBuilder.query(boolQueryBuilder);  
        } 
        return searchSourceBuilder;  
    }  
    
    /** 
     * 条件删除 ，ElasticSearch V5.1 以上可用 
     * 
     * @param criterias 条件 
     * @return 删除的document数量 
     */ 
    public int deleteByQuery(List<Criteria> criterias) {  
        try {  
            JestResult result = client.execute(new DeleteByQuery.Builder(buildSearch(criterias).toString())  
                    .addIndex(Elasticsearch_Index_DB.CMS_INDICES)  
                    .addType(Elasticsearch_Index_DB.CMS_TYPE)  
                    .build());  
  
            return result.getJsonObject().get("deleted").getAsInt();  
        } catch (Exception e) {  
            throw new RuntimeException("deleteByQuery exception", e);  
        }  
    }  
}  