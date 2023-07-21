package com.x.query.core.express.assemble.surface.jaxrs.search;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.query.core.express.index.WoFacet;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "com.x.query.core.express.jaxrs.search.ActionPostWo")
public class ActionPostWo extends GsonPropertyObject {

    private static final long serialVersionUID = 6427227486701877864L;

    private Long queryElapsed;

    private Long count;

    private List<WoFacet> facetList = new ArrayList<>();

    private List<Map<String, Object>> documentList = new ArrayList<>();

    public Long getQueryElapsed() {
        return queryElapsed;
    }

    public void setQueryElapsed(Long queryElapsed) {
        this.queryElapsed = queryElapsed;
    }

    public List<Map<String, Object>> getDocumentList() {
        return documentList;
    }

    public void setDocumentList(List<Map<String, Object>> documentList) {
        this.documentList = documentList;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public List<WoFacet> getFacetList() {
        return facetList;
    }

    public void setFacetList(List<WoFacet> facetList) {
        this.facetList = facetList;
    }

    

}
