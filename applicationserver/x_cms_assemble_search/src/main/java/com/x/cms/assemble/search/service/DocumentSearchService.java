package com.x.cms.assemble.search.service;  
  
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import com.x.base.core.project.tools.ListTools;
import com.x.cms.assemble.search.Elasticsearch_Index_DB;
import com.x.cms.assemble.search.bean.WrapDocument;
import com.x.cms.assemble.search.es.Criteria;
import com.x.cms.core.entity.tools.LogUtil;

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
  
public class DocumentSearchService {  
  
    private JestClient client;  
  
    public DocumentSearchService(JestClient client) {  
        this.client = client;  
    }
  
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
   
    public boolean delete(String id) {  
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
   
    public WrapDocument searchById(String id) {  
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
     * @param key 
     * 
     * @param criterias 条件列表 
     * @return 结果集 
     */  
   
    public List<WrapDocument> search( String personName, List<String> unitNames, List<String> groupNames, String key, List<Criteria> terms_criterias, List<Criteria> match_criterias ) {  
        try {  
            SearchResult result = client.execute(new Search.Builder(
            		buildSearch( personName, unitNames, groupNames, key, terms_criterias, match_criterias ).toString()
            )  
           // multiple index or types can be added.  
           .addIndex(Elasticsearch_Index_DB.CMS_INDICES)  
           .addType(Elasticsearch_Index_DB.CMS_TYPE)  
           .build());  
           return result.getSourceAsObjectList(WrapDocument.class, false);  
        } catch (Exception e) {  
            throw new RuntimeException("search exception", e);  
        }  
    }  
  
    private SearchSourceBuilder buildSearch( String personName, List<String> unitNames, List<String> groupNames, String key, List<Criteria> terms_criterias, List<Criteria> match_criterias) {  
        //指定查询的库表  
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();  
  
      //构建查询条件
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        BoolQueryBuilder appViewPermissionQB = QueryBuilders.boolQuery();
        BoolQueryBuilder appAllViewPermissionQB = QueryBuilders.boolQuery();
        BoolQueryBuilder appUserPermissionQB = QueryBuilders.boolQuery();
        BoolQueryBuilder categoryViewPermissionQB = QueryBuilders.boolQuery();
        BoolQueryBuilder categoryAllViewPermissionQB = QueryBuilders.boolQuery();
        BoolQueryBuilder categoryUserPermissionQB = QueryBuilders.boolQuery();
        BoolQueryBuilder docPermissionQueryBuilder = QueryBuilders.boolQuery();           
        
        
        //栏目所有人可见
        appAllViewPermissionQB.should().add(
        		QueryBuilders.boolQuery().filter(QueryBuilders.termQuery("appInfo.allPeopleView", true ))
        );
        //栏目所有人可发布
        appAllViewPermissionQB.should().add(
        		QueryBuilders.boolQuery().filter(QueryBuilders.termQuery("appInfo.allPeoplePublish", true ))
        );
        //用户权限：可管理、可发布、可见 。其中之一
        //用户唯一标识
        appUserPermissionQB.should().add(
        		QueryBuilders.boolQuery().filter(QueryBuilders.termQuery("appInfo.manageablePersonList.keyword", personName ))
        );
        appUserPermissionQB.should().add(
        		QueryBuilders.boolQuery().filter(QueryBuilders.termQuery("appInfo.publishablePersonList.keyword", personName ))
        );
        appUserPermissionQB.should().add(
        		QueryBuilders.boolQuery().filter(QueryBuilders.termQuery("appInfo.viewablePersonList.keyword", personName ))
        );
        //用户所属组织
        if( ListTools.isNotEmpty( unitNames )) {
        	for( String unitName : unitNames ) {
        		 appUserPermissionQB.should().add(
                 		QueryBuilders.boolQuery().filter(QueryBuilders.termQuery("appInfo.manageableUnitList.keyword", unitName ))
                 );
                 //用户可发布
                 appUserPermissionQB.should().add(
                 		QueryBuilders.boolQuery().filter(QueryBuilders.termQuery("appInfo.publishableUnitList.keyword", unitName ))
                 );
                 //用户可见
                 appUserPermissionQB.should().add(
                 		QueryBuilders.boolQuery().filter(QueryBuilders.termQuery("appInfo.viewableUnitList.keyword", unitName ))
                 );
        	}
        }
        //用户所属群组
        if( ListTools.isNotEmpty( groupNames )) {
        	for( String groupName : groupNames ) {
        		 appUserPermissionQB.should().add(
                 		QueryBuilders.boolQuery().filter(QueryBuilders.termQuery("appInfo.manageableGroupList.keyword", groupName ))
                 );
                 //用户可发布
                 appUserPermissionQB.should().add(
                 		QueryBuilders.boolQuery().filter(QueryBuilders.termQuery("appInfo.publishableGroupList.keyword", groupName ))
                 );
                 //用户可见
                 appUserPermissionQB.should().add(
                 		QueryBuilders.boolQuery().filter(QueryBuilders.termQuery("appInfo.viewableGroupList.keyword", groupName ))
                 );
        	}
        }
        
        //分类所有人可见
        categoryAllViewPermissionQB.should().add(
        		QueryBuilders.boolQuery().filter(QueryBuilders.termQuery("categoryInfo.allPeopleView", true ))
        );
        //分类所有人可发布
        categoryAllViewPermissionQB.should().add(
        		QueryBuilders.boolQuery().filter(QueryBuilders.termQuery("categoryInfo.allPeoplePublish", true ))
        );
        //用户权限：可管理、可发布、可见 。其中之一
        //用户唯一标识
        appUserPermissionQB.should().add(
        		QueryBuilders.boolQuery().filter(QueryBuilders.termQuery("categoryInfo.manageablePersonList.keyword", personName ))
        );
        appUserPermissionQB.should().add(
        		QueryBuilders.boolQuery().filter(QueryBuilders.termQuery("categoryInfo.publishablePersonList.keyword", personName ))
        );
        appUserPermissionQB.should().add(
        		QueryBuilders.boolQuery().filter(QueryBuilders.termQuery("categoryInfo.viewablePersonList.keyword", personName ))
        );
        //用户所属组织
        if( ListTools.isNotEmpty( unitNames )) {
        	for( String unitName : unitNames ) {
        		 appUserPermissionQB.should().add(
                 		QueryBuilders.boolQuery().filter(QueryBuilders.termQuery("categoryInfo.manageableUnitList.keyword", unitName ))
                 );
                 //用户可发布
                 appUserPermissionQB.should().add(
                 		QueryBuilders.boolQuery().filter(QueryBuilders.termQuery("categoryInfo.publishableUnitList.keyword", unitName ))
                 );
                 //用户可见
                 appUserPermissionQB.should().add(
                 		QueryBuilders.boolQuery().filter(QueryBuilders.termQuery("categoryInfo.viewableUnitList.keyword", unitName ))
                 );
        	}
        }
        //用户所属群组
        if( ListTools.isNotEmpty( groupNames )) {
        	for( String groupName : groupNames ) {
        		 appUserPermissionQB.should().add(
                 		QueryBuilders.boolQuery().filter(QueryBuilders.termQuery("categoryInfo.manageableGroupList.keyword", groupName ))
                 );
                 //用户可发布
                 appUserPermissionQB.should().add(
                 		QueryBuilders.boolQuery().filter(QueryBuilders.termQuery("categoryInfo.publishableGroupList.keyword", groupName ))
                 );
                 //用户可见
                 appUserPermissionQB.should().add(
                 		QueryBuilders.boolQuery().filter(QueryBuilders.termQuery("categoryInfo.viewableGroupList.keyword", groupName ))
                 );
        	}
        }
        
        //文档权限也要满足要求，所有人可见或者用户权限可见
        docPermissionQueryBuilder.should().add(
        		QueryBuilders.boolQuery().filter(QueryBuilders.termQuery("managerList.keyword", personName ))
        );
        docPermissionQueryBuilder.should().add(
        		QueryBuilders.boolQuery().filter(QueryBuilders.termQuery("managerList.keyword", "所有人" ))
        );
        docPermissionQueryBuilder.should().add(
        		QueryBuilders.boolQuery().filter(QueryBuilders.termQuery("readPersonList.keyword", personName ))
        );
        docPermissionQueryBuilder.should().add(
        		QueryBuilders.boolQuery().filter(QueryBuilders.termQuery("readPersonList.keyword", "所有人" ))
        );
        docPermissionQueryBuilder.should().add(
        		QueryBuilders.boolQuery().filter(QueryBuilders.termQuery("authorPersonList.keyword", personName ))
        );
        docPermissionQueryBuilder.should().add(
        		QueryBuilders.boolQuery().filter(QueryBuilders.termQuery("authorPersonList.keyword", "所有人" ))
        );
        //用户所属组织
        if( ListTools.isNotEmpty( unitNames )) {
        	for( String unitName : unitNames ) {
        		 //读者组织
        		 appUserPermissionQB.should().add(
                 		QueryBuilders.boolQuery().filter(QueryBuilders.termQuery("readUnitList.keyword", unitName ))
                 );
                 //作者组织
                 appUserPermissionQB.should().add(
                 		QueryBuilders.boolQuery().filter(QueryBuilders.termQuery("authorUnitList.keyword", unitName ))
                 );
        	}
        }
        //用户所属群组
        if( ListTools.isNotEmpty( groupNames )) {
        	for( String groupName : groupNames ) {
        		//读者群组
        		 appUserPermissionQB.should().add(
                 		QueryBuilders.boolQuery().filter(QueryBuilders.termQuery("readUnitList.keyword", groupName ))
                 );
        		//作者组织
                 appUserPermissionQB.should().add(
                 		QueryBuilders.boolQuery().filter(QueryBuilders.termQuery("authorUnitList.keyword", groupName ))
                 );
        	}
        }
        
        appViewPermissionQB.should().add( appAllViewPermissionQB );
        appViewPermissionQB.should().add( appUserPermissionQB );
        categoryViewPermissionQB.should().add( categoryAllViewPermissionQB );
        categoryViewPermissionQB.should().add( categoryUserPermissionQB );
        
        //组织全文检索查询条件
        if( ListTools.isNotEmpty( match_criterias )) {
        	for (Criteria c : match_criterias) {
            	boolQueryBuilder.must().add(
            		QueryBuilders.boolQuery().filter(QueryBuilders.matchQuery(c.getFieldName(), c.getFieldValue()).operator(Operator.AND))
            	);
            }
        }
        //组织过滤查询条件
        if( ListTools.isNotEmpty( terms_criterias )) {
        	for (Criteria c : match_criterias) {
            	boolQueryBuilder.must().add(
            		QueryBuilders.boolQuery().filter(QueryBuilders.termQuery(c.getFieldName(), c.getFieldValue()))
            	);
            }
        }
        
        if( StringUtils.isNotEmpty( key )) {
        	boolQueryBuilder.must().add(
            		QueryBuilders.boolQuery().filter(QueryBuilders.matchQuery("title", key).operator(Operator.AND))
            	);
        }
        boolQueryBuilder.must().add( appViewPermissionQB );
        boolQueryBuilder.must().add( categoryViewPermissionQB );
        boolQueryBuilder.must().add( docPermissionQueryBuilder );
        
        LogUtil.INFO( ">>>>>>>QueryDSL", boolQueryBuilder.toString() );
        searchSourceBuilder.query(boolQueryBuilder); 
        
        return searchSourceBuilder;  
    }  
    
    /** 
     * 条件删除 ，ElasticSearch V5.1 以上可用 
     * 
     * @param criterias 条件 
     * @return 删除的document数量 
     */ 
    public int deleteByQuery(String personName, List<String> unitNames, List<String> groupNames, String key, List<Criteria> terms_criterias, List<Criteria> match_criterias) {  
        try {  
            JestResult result = client.execute(new DeleteByQuery.Builder(
            		buildSearch(personName, unitNames, groupNames, key, terms_criterias, match_criterias).toString())  
                    .addIndex(Elasticsearch_Index_DB.CMS_INDICES)  
                    .addType(Elasticsearch_Index_DB.CMS_TYPE)  
                    .build());  
  
            return result.getJsonObject().get("deleted").getAsInt();  
        } catch (Exception e) {  
            throw new RuntimeException("deleteByQuery exception", e);  
        }  
    }  
}  