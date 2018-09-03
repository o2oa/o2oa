package com.x.cms.assemble.search.service.inf;  
  
import java.util.List;

import com.x.cms.assemble.search.bean.WrapDocument;
import com.x.cms.assemble.search.es.Criteria;
  
public interface IDocumentDao {  
    /** 
     * 插入 
     * 
     * @param doc 
     * @return 
     */  
    boolean insert(WrapDocument doc);  
  
    /** 
     * 替换 
     * 
     * @param doc 
     * @return 
     */  
    boolean replace(WrapDocument doc);  
  
    /** 
     * 更新 
     * 
     * @param doc 
     * @return 
     */  
    boolean update(WrapDocument doc);  
  
  
    /** 
     * 删除 
     * 
     * @param id 
     * @return 
     */  
    boolean delete(long id);  
  
    /** 
     * 根据ID查询 
     * 
     * @param id 
     * @return 
     */  
    WrapDocument searchById(long id);  
  
    /** 
     * 条件查询 
     * 
     * @param criterias 
     * @return 
     */  
    List<WrapDocument> search(List<Criteria> criterias);  
  
    /** 
     * 条件删除 
     * 
     * @param criterias 
     * @return 删除的document数量 
     */  
    int deleteByQuery(List<Criteria> criterias);  
}  