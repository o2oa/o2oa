package com.x.cms.assemble.control.jaxrs.document;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.JaxrsDescribe;
import com.x.base.core.project.annotation.JaxrsMethodDescribe;
import com.x.base.core.project.annotation.JaxrsParameterDescribe;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.HttpMediaType;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.cms.assemble.control.queue.DataImportStatus;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("document")
@JaxrsDescribe("信息发布信息文档管理")
public class DocumentAction extends StandardJaxrsAction {

    private static Logger logger = LoggerFactory.getLogger(DocumentAction.class);

    @JaxrsMethodDescribe(value = "变更指定文档的分类信息.", action = ActionPersistChangeCategory.class)
    @PUT
    @Path("category/change")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void persist_changeCategory(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request, JsonElement jsonElement) {
        EffectivePerson effectivePerson = this.effectivePerson(request);
        ActionResult<ActionPersistChangeCategory.Wo> result = new ActionResult<>();
        Boolean check = true;

        if (check) {
            try {
                result = new ActionPersistChangeCategory().execute(request, jsonElement, effectivePerson);
            } catch (Exception e) {
                result = new ActionResult<>();
                result.error(e);
                logger.error(e, effectivePerson, request, null);
            }
        }

        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
    }

    @JaxrsMethodDescribe(value = "变更指定文档的分类信息.", action = ActionPersistChangeCategory.class)
    @POST
    @Path("category/change/mockputtopost")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void persist_changeCategoryMockPutToPost(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request, JsonElement jsonElement) {
        EffectivePerson effectivePerson = this.effectivePerson(request);
        ActionResult<ActionPersistChangeCategory.Wo> result = new ActionResult<>();
        Boolean check = true;

        if (check) {
            try {
                result = new ActionPersistChangeCategory().execute(request, jsonElement, effectivePerson);
            } catch (Exception e) {
                result = new ActionResult<>();
                result.error(e);
                logger.error(e, effectivePerson, request, null);
            }
        }

        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
    }

    @JaxrsMethodDescribe(value = "指修改指定文档的数据。", action = ActionPersistBatchModifyData.class)
    @PUT
    @Path("batch/data/modify")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void persist_batchDataModify(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request, JsonElement jsonElement) {
        EffectivePerson effectivePerson = this.effectivePerson(request);
        ActionResult<ActionPersistBatchModifyData.Wo> result = new ActionResult<>();
        Boolean check = true;
        if (check) {
            try {
                result = new ActionPersistBatchModifyData().execute(request, jsonElement, effectivePerson);
            } catch (Exception e) {
                result = new ActionResult<>();
                result.error(e);
                logger.error(e, effectivePerson, request, null);
            }
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
    }

    @JaxrsMethodDescribe(value = "指修改指定文档的数据。", action = ActionPersistBatchModifyData.class)
    @POST
    @Path("batch/data/modify/mockputtopost")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void persist_batchDataModifyMockPutToPost(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request, JsonElement jsonElement) {
        EffectivePerson effectivePerson = this.effectivePerson(request);
        ActionResult<ActionPersistBatchModifyData.Wo> result = new ActionResult<>();
        Boolean check = true;
        if (check) {
            try {
                result = new ActionPersistBatchModifyData().execute(request, jsonElement, effectivePerson);
            } catch (Exception e) {
                result = new ActionResult<>();
                result.error(e);
                logger.error(e, effectivePerson, request, null);
            }
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
    }

    @JaxrsMethodDescribe(value = "根据导入批次号查询导入状态信息.", action = ActionQueryImportStatusWithName.class)
    @GET
    @Path("batch/{batchName}/status")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void query_checkImportStatus(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request,
            @JaxrsParameterDescribe("导入批次号") @PathParam("batchName") String batchName) {
        EffectivePerson effectivePerson = this.effectivePerson(request);
        ActionResult<DataImportStatus> result = new ActionResult<>();
        try {
            result = new ActionQueryImportStatusWithName().execute(request, effectivePerson, batchName);
        } catch (Exception e) {
            result = new ActionResult<>();
            result.error(e);
            logger.error(e, effectivePerson, request, null);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "查询所有的导入状态信息.", action = ActionQueryAllImportStatus.class)
    @GET
    @Path("batch/status")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void query_checkAllImportStatus(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request) {
        EffectivePerson effectivePerson = this.effectivePerson(request);
        ActionResult<List<DataImportStatus>> result = new ActionResult<>();
        try {
            result = new ActionQueryAllImportStatus().execute(request, effectivePerson);
        } catch (Exception e) {
            result = new ActionResult<>();
            result.error(e);
            logger.error(e, effectivePerson, request, null);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "根据ID获取信息发布文档信息对象详细信息，包括附件列表，数据信息.", action = ActionQueryGetDocument.class)
    @GET
    @Path("{id}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void query_get(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("信息文档ID") @PathParam("id") String id) {
        EffectivePerson effectivePerson = this.effectivePerson(request);
        ActionResult<ActionQueryGetDocument.Wo> result = new ActionResult<>();
        try {
            result = new ActionQueryGetDocument().execute(request, id, effectivePerson);
        } catch (Exception e) {
            result = new ActionResult<>();
            result.error(e);
            logger.error(e, effectivePerson, request, null);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "列示文档对象可供排序和展示使用的列名.", action = ActionQueryListDocumentFields.class)
    @GET
    @Path("document/fields")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void query_listDocumentFields(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request) {
        EffectivePerson effectivePerson = this.effectivePerson(request);
        ActionResult<ActionQueryListDocumentFields.Wo> result = new ActionResult<>();
        try {
            result = new ActionQueryListDocumentFields().execute(request);
        } catch (Exception e) {
            result = new ActionResult<>();
            result.error(e);
            logger.error(e, effectivePerson, request, null);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "根据ID访问信息发布文档信息对象详细信息，包括附件列表，数据信息.", action = ActionQueryViewDocument.class)
    @GET
    @Path("{id}/view")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void query_view(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("信息文档ID") @PathParam("id") String id) {
        EffectivePerson effectivePerson = this.effectivePerson(request);
        ActionResult<ActionQueryViewDocument.Wo> result = new ActionResult<>();
        try {
            result = new ActionQueryViewDocument().execute(request, id, effectivePerson);
        } catch (Exception e) {
            result = new ActionResult<>();
            result.error(e);
            logger.error(e, effectivePerson, request, null);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "根据ID获取信息发布文档信息被访问次数.", action = ActionQueryCountViewTimes.class)
    @GET
    @Path("{id}/view/count")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void query_getViewCount(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("信息文档ID") @PathParam("id") String id) {
        EffectivePerson effectivePerson = this.effectivePerson(request);
        ActionResult<ActionQueryCountViewTimes.Wo> result = new ActionResult<>();
        try {
            result = new ActionQueryCountViewTimes().execute(request, id, effectivePerson);
        } catch (Exception e) {
            result = new ActionResult<>();
            result.error(e);
            logger.error(e, effectivePerson, request, null);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "查询符合过滤条件的已发布的信息数量.", action = ActionQueryCountWithFilter.class)
    @PUT
    @Path("filter/count")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void query_countDocumentWithFilter(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request, JsonElement jsonElement) {
        EffectivePerson effectivePerson = this.effectivePerson(request);
        ActionResult<ActionQueryCountWithFilter.Wo> result = new ActionResult<>();
        Boolean check = true;

        if (check) {
            try {
                result = new ActionQueryCountWithFilter().execute(request, jsonElement, effectivePerson);
            } catch (Exception e) {
                result = new ActionResult<>();
                result.error(e);
                logger.error(e, effectivePerson, request, null);
            }
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
    }

    @JaxrsMethodDescribe(value = "查询符合过滤条件的已发布的信息数量.", action = ActionQueryCountWithFilter.class)
    @POST
    @Path("filter/count/mockputtopost")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void query_countDocumentWithFilterMockPutToPost(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request, JsonElement jsonElement) {
        EffectivePerson effectivePerson = this.effectivePerson(request);
        ActionResult<ActionQueryCountWithFilter.Wo> result = new ActionResult<>();
        Boolean check = true;

        if (check) {
            try {
                result = new ActionQueryCountWithFilter().execute(request, jsonElement, effectivePerson);
            } catch (Exception e) {
                result = new ActionResult<>();
                result.error(e);
                logger.error(e, effectivePerson, request, null);
            }
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
    }

    @JaxrsMethodDescribe(value = "根据ID删除信息发布文档信息.", action = ActionPersistDeleteDocument.class)
    @DELETE
    @Path("{id}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void persist_delete(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("信息文档ID") @PathParam("id") String id) {
        EffectivePerson effectivePerson = this.effectivePerson(request);
        ActionResult<ActionPersistDeleteDocument.Wo> result = new ActionResult<>();
        try {
            result = new ActionPersistDeleteDocument().execute(request, id, effectivePerson);
        } catch (Exception e) {
            result = new ActionResult<>();
            result.error(e);
            logger.error(e, effectivePerson, request, null);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "根据ID删除信息发布文档信息.", action = ActionPersistDeleteDocument.class)
    @GET
    @Path("{id}/mockdeletetoget")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void persist_deleteMockDeleteToGet(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request,
            @JaxrsParameterDescribe("信息文档ID") @PathParam("id") String id) {
        EffectivePerson effectivePerson = this.effectivePerson(request);
        ActionResult<ActionPersistDeleteDocument.Wo> result = new ActionResult<>();
        try {
            result = new ActionPersistDeleteDocument().execute(request, id, effectivePerson);
        } catch (Exception e) {
            result = new ActionResult<>();
            result.error(e);
            logger.error(e, effectivePerson, request, null);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "根据批次号删除信息发布文档信息.", action = ActionPersistDeleteWithBatch.class)
    @DELETE
    @Path("batch/{batchId}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void persist_deleteWithBatchName(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request,
            @JaxrsParameterDescribe("信息文档ID") @PathParam("batchId") String batchId) {
        EffectivePerson effectivePerson = this.effectivePerson(request);
        ActionResult<ActionPersistDeleteWithBatch.Wo> result = new ActionResult<>();
        try {
            result = new ActionPersistDeleteWithBatch().execute(request, batchId, effectivePerson);
        } catch (Exception e) {
            result = new ActionResult<>();
            result.error(e);
            logger.error(e, effectivePerson, request, null);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "根据批次号删除信息发布文档信息.", action = ActionPersistDeleteWithBatch.class)
    @GET
    @Path("batch/{batchId}/mockdeletetoget")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void persist_deleteWithBatchNameMockDeleteToGet(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request,
            @JaxrsParameterDescribe("信息文档ID") @PathParam("batchId") String batchId) {
        EffectivePerson effectivePerson = this.effectivePerson(request);
        ActionResult<ActionPersistDeleteWithBatch.Wo> result = new ActionResult<>();
        try {
            result = new ActionPersistDeleteWithBatch().execute(request, batchId, effectivePerson);
        } catch (Exception e) {
            result = new ActionResult<>();
            result.error(e);
            logger.error(e, effectivePerson, request, null);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "根据ID归档信息发布文档信息.", action = ActionPersistArchive.class)
    @GET
    @Path("achive/{id}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void persist_achive(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("信息文档ID") @PathParam("id") String id) {
        EffectivePerson effectivePerson = this.effectivePerson(request);
        ActionResult<ActionPersistArchive.Wo> result = new ActionResult<>();
        try {
            result = new ActionPersistArchive().execute(request, id, effectivePerson);
        } catch (Exception e) {
            result = new ActionResult<>();
            result.error(e);
            logger.error(e, effectivePerson, request, null);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "根据文档ID正式发布文档，消息通过publishNotify接口发送。", action = ActionPersistPublishAndNotify.class)
    @PUT
    @Path("publish/{id}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void persist_publishAndNotify(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request, @JaxrsParameterDescribe("文档ID") @PathParam("id") String id,
            JsonElement jsonElement) {
        EffectivePerson effectivePerson = this.effectivePerson(request);
        ActionResult<ActionPersistPublishAndNotify.Wo> result = new ActionResult<>();
        Boolean check = true;

        if (check) {
            try {
                result = new ActionPersistPublishAndNotify().execute(request, id, effectivePerson, jsonElement);
            } catch (Exception e) {
                result = new ActionResult<>();
                result.error(e);
                logger.error(e, effectivePerson, request, null);
            }
        }

        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
    }

    @JaxrsMethodDescribe(value = "根据文档ID正式发布文档，消息通过publishNotify接口发送。", action = ActionPersistPublishAndNotify.class)
    @POST
    @Path("publish/{id}/mockputtopost")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void persist_publishAndNotifyMockPutToPost(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request, @JaxrsParameterDescribe("文档ID") @PathParam("id") String id,
            JsonElement jsonElement) {
        EffectivePerson effectivePerson = this.effectivePerson(request);
        ActionResult<ActionPersistPublishAndNotify.Wo> result = new ActionResult<>();
        Boolean check = true;

        if (check) {
            try {
                result = new ActionPersistPublishAndNotify().execute(request, id, effectivePerson, jsonElement);
            } catch (Exception e) {
                result = new ActionResult<>();
                result.error(e);
                logger.error(e, effectivePerson, request, null);
            }
        }

        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
    }

    @JaxrsMethodDescribe(value = "直接发布信息内容，创建新的信息发布文档并且直接发布.", action = ActionPersistPublishContent.class)
    @PUT
    @Path("publish/content")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void persist_publishContent(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request, JsonElement jsonElement) {
        EffectivePerson effectivePerson = this.effectivePerson(request);
        ActionResult<ActionPersistPublishContent.Wo> result = new ActionResult<>();
        Boolean check = true;

        if (check) {
            System.out.println("please wait, system try to publish content......");
            try {
                result = new ActionPersistPublishContent().execute(request, jsonElement, effectivePerson);
                System.out.println("system publish content successful!");
            } catch (Exception e) {
                result = new ActionResult<>();
                result.error(e);
                logger.error(e, effectivePerson, request, null);
            }
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
    }

    @JaxrsMethodDescribe(value = "直接发布信息内容，创建新的信息发布文档并且直接发布.", action = ActionPersistPublishContent.class)
    @POST
    @Path("publish/content/mockputtopost")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void persist_publishContentMockPutToPost(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request, JsonElement jsonElement) {
        EffectivePerson effectivePerson = this.effectivePerson(request);
        ActionResult<ActionPersistPublishContent.Wo> result = new ActionResult<>();
        Boolean check = true;

        if (check) {
            System.out.println("please wait, system try to publish content......");
            try {
                result = new ActionPersistPublishContent().execute(request, jsonElement, effectivePerson);
                System.out.println("system publish content successful!");
            } catch (Exception e) {
                result = new ActionResult<>();
                result.error(e);
                logger.error(e, effectivePerson, request, null);
            }
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
    }

    @JaxrsMethodDescribe(value = "根据ID取消信息内容发布状态，修改为草稿.", action = ActionPersistPublishCancel.class)
    @PUT
    @Path("publish/{id}/cancel")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void persist_publishCancel(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("信息文档ID") @PathParam("id") String id) {
        EffectivePerson effectivePerson = this.effectivePerson(request);
        ActionResult<ActionPersistPublishCancel.Wo> result = new ActionResult<>();
        try {
            result = new ActionPersistPublishCancel().execute(request, id, effectivePerson);
        } catch (Exception e) {
            result = new ActionResult<>();
            result.error(e);
            logger.error(e, effectivePerson, request, null);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "根据ID取消信息内容发布状态，修改为草稿.", action = ActionPersistPublishCancel.class)
    @POST
    @Path("publish/{id}/cancel/mockputtopost")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void persist_publishCancelMockPutToPost(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request,
            @JaxrsParameterDescribe("信息文档ID") @PathParam("id") String id) {
        EffectivePerson effectivePerson = this.effectivePerson(request);
        ActionResult<ActionPersistPublishCancel.Wo> result = new ActionResult<>();
        try {
            result = new ActionPersistPublishCancel().execute(request, id, effectivePerson);
        } catch (Exception e) {
            result = new ActionResult<>();
            result.error(e);
            logger.error(e, effectivePerson, request, null);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "列示符合过滤条件的已发布的信息内容, 下一页.", action = ActionQueryListNextWithFilter.class)
    @PUT
    @Path("filter/list/{id}/next/{count}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void query_listNextWithFilter(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request,
            @JaxrsParameterDescribe("最后一条信息ID，如果是第一页，则可以用(0)代替") @PathParam("id") String id,
            @JaxrsParameterDescribe("每页显示的条目数量") @PathParam("count") Integer count, JsonElement jsonElement) {
        EffectivePerson effectivePerson = this.effectivePerson(request);
        ActionResult<List<ActionQueryListNextWithFilter.Wo>> result = new ActionResult<>();
        Boolean check = true;

        if (check) {
            try {
                result = new ActionQueryListNextWithFilter().execute(request, id, count, jsonElement, effectivePerson);
            } catch (Exception e) {
                result = new ActionResult<>();
                result.error(e);
                logger.error(e, effectivePerson, request, null);
            }
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "列示符合过滤条件的已发布的信息内容, 下一页.", action = ActionQueryListNextWithFilter.class)
    @POST
    @Path("filter/list/{id}/next/{count}/mockputtopost")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void query_listNextWithFilterMockPutToPost(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request,
            @JaxrsParameterDescribe("最后一条信息ID，如果是第一页，则可以用(0)代替") @PathParam("id") String id,
            @JaxrsParameterDescribe("每页显示的条目数量") @PathParam("count") Integer count, JsonElement jsonElement) {
        EffectivePerson effectivePerson = this.effectivePerson(request);
        ActionResult<List<ActionQueryListNextWithFilter.Wo>> result = new ActionResult<>();
        Boolean check = true;

        if (check) {
            try {
                result = new ActionQueryListNextWithFilter().execute(request, id, count, jsonElement, effectivePerson);
            } catch (Exception e) {
                result = new ActionResult<>();
                result.error(e);
                logger.error(e, effectivePerson, request, null);
            }
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "列示符合过滤条件的已发布的信息内容, 上一页.", action = ActionQueryListPrevWithFilter.class)
    @PUT
    @Path("filter/list/{id}/prev/{count}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void query_listPrevWithFilter(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request,
            @JaxrsParameterDescribe("最后一条信息ID，如果是第一页，则可以用(0)代替") @PathParam("id") String id,
            @JaxrsParameterDescribe("每页显示的条目数量") @PathParam("count") Integer count, JsonElement jsonElement) {
        EffectivePerson effectivePerson = this.effectivePerson(request);
        ActionResult<List<ActionQueryListPrevWithFilter.Wo>> result = new ActionResult<>();
        Boolean check = true;

        if (check) {
            try {
                result = new ActionQueryListPrevWithFilter().execute(request, id, count, jsonElement, effectivePerson);
            } catch (Exception e) {
                result = new ActionResult<>();
                result.error(e);
                logger.error(e, effectivePerson, request, null);
            }
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "列示符合过滤条件的已发布的信息内容, 上一页.", action = ActionQueryListPrevWithFilter.class)
    @POST
    @Path("filter/list/{id}/prev/{count}/mockputtopost")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void query_listPrevWithFilterMockPutToPost(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request,
            @JaxrsParameterDescribe("最后一条信息ID，如果是第一页，则可以用(0)代替") @PathParam("id") String id,
            @JaxrsParameterDescribe("每页显示的条目数量") @PathParam("count") Integer count, JsonElement jsonElement) {
        EffectivePerson effectivePerson = this.effectivePerson(request);
        ActionResult<List<ActionQueryListPrevWithFilter.Wo>> result = new ActionResult<>();
        Boolean check = true;

        if (check) {
            try {
                result = new ActionQueryListPrevWithFilter().execute(request, id, count, jsonElement, effectivePerson);
            } catch (Exception e) {
                result = new ActionResult<>();
                result.error(e);
                logger.error(e, effectivePerson, request, null);
            }
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "列示符合过滤条件的草稿信息内容, 下一页.", action = ActionQueryListDraftNextWithFilter.class)
    @PUT
    @Path("draft/list/{id}/next/{count}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void query_listDraftNextWithFilter(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request,
            @JaxrsParameterDescribe("最后一条信息ID，如果是第一页，则可以用(0)代替") @PathParam("id") String id,
            @JaxrsParameterDescribe("每页显示的条目数量") @PathParam("count") Integer count, JsonElement jsonElement) {
        EffectivePerson effectivePerson = this.effectivePerson(request);
        ActionResult<List<ActionQueryListDraftNextWithFilter.Wo>> result = new ActionResult<>();
        Boolean check = true;

        if (check) {
            try {
                result = new ActionQueryListDraftNextWithFilter().execute(request, id, count, jsonElement,
                        effectivePerson);
            } catch (Exception e) {
                result = new ActionResult<>();
                result.error(e);
                logger.error(e, effectivePerson, request, null);
            }
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "列示符合过滤条件的草稿信息内容, 下一页.", action = ActionQueryListDraftNextWithFilter.class)
    @POST
    @Path("draft/list/{id}/next/{count}/mockputtopost")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void query_listDraftNextWithFilterMockPutToPost(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request,
            @JaxrsParameterDescribe("最后一条信息ID，如果是第一页，则可以用(0)代替") @PathParam("id") String id,
            @JaxrsParameterDescribe("每页显示的条目数量") @PathParam("count") Integer count, JsonElement jsonElement) {
        EffectivePerson effectivePerson = this.effectivePerson(request);
        ActionResult<List<ActionQueryListDraftNextWithFilter.Wo>> result = new ActionResult<>();
        Boolean check = true;

        if (check) {
            try {
                result = new ActionQueryListDraftNextWithFilter().execute(request, id, count, jsonElement,
                        effectivePerson);
            } catch (Exception e) {
                result = new ActionResult<>();
                result.error(e);
                logger.error(e, effectivePerson, request, null);
            }
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "保存信息发布文档信息对象.", action = ActionPersistSaveDocument.class)
    @POST
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void persist_save(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            JsonElement jsonElement) {
        EffectivePerson effectivePerson = this.effectivePerson(request);
        ActionResult<ActionPersistSaveDocument.Wo> result = new ActionResult<>();
        Boolean check = true;

        if (check) {
            try {
                result = new ActionPersistSaveDocument().execute(request, jsonElement, effectivePerson);
            } catch (Exception e) {
                result = new ActionResult<>();
                result.error(e);
                logger.error(e, effectivePerson, request, jsonElement);
            }
        }

        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
    }

    @JaxrsMethodDescribe(value = "文档点赞.", action = ActionPersistCommend.class)
    @GET
    @Path("{id}/commend")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void persist_commend(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("信息文档ID") @PathParam("id") String id) {
        EffectivePerson effectivePerson = this.effectivePerson(request);
        ActionResult<ActionPersistCommend.Wo> result = new ActionResult<>();
        Boolean check = true;
        if (check) {
            try {
                result = new ActionPersistCommend().execute(request, id, effectivePerson);
            } catch (Exception e) {
                result = new ActionResult<>();
                result.error(e);
                logger.error(e, effectivePerson, request, null);
            }
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "取消文档点赞.", action = ActionPersistUnCommend.class)
    @GET
    @Path("{id}/uncommend")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void persist_unCommend(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("信息文档ID") @PathParam("id") String id) {
        EffectivePerson effectivePerson = this.effectivePerson(request);
        ActionResult<ActionPersistUnCommend.Wo> result = new ActionResult<>();
        Boolean check = true;
        if (check) {
            try {
                result = new ActionPersistUnCommend().execute(request, id, effectivePerson);
            } catch (Exception e) {
                result = new ActionResult<>();
                result.error(e);
                logger.error(e, effectivePerson, request, null);
            }
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "文档置顶.", action = ActionPersistTopDocument.class)
    @GET
    @Path("{id}/top")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void persist_top(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("信息文档ID") @PathParam("id") String id) {
        EffectivePerson effectivePerson = this.effectivePerson(request);
        ActionResult<ActionPersistTopDocument.Wo> result = new ActionResult<>();
        Boolean check = true;
        if (check) {
            try {
                result = new ActionPersistTopDocument().execute(request, id, effectivePerson);
            } catch (Exception e) {
                result = new ActionResult<>();
                result.error(e);
                logger.error(e, effectivePerson, request, null);
            }
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "取消文档置顶.", action = ActionPersistUnTopDocument.class)
    @GET
    @Path("{id}/unTop")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void persist_unTop(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("信息文档ID") @PathParam("id") String id) {
        EffectivePerson effectivePerson = this.effectivePerson(request);
        ActionResult<ActionPersistUnTopDocument.Wo> result = new ActionResult<>();
        Boolean check = true;
        if (check) {
            try {
                result = new ActionPersistUnTopDocument().execute(request, id, effectivePerson);
            } catch (Exception e) {
                result = new ActionResult<>();
                result.error(e);
                logger.error(e, effectivePerson, request, null);
            }
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "分页查询符合过滤条件的已发布的信息内容.", action = ActionQueryListWithFilterPaging.class)
    @PUT
    @Path("filter/list/{page}/size/{size}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void query_listWithFilterPaging(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request, @JaxrsParameterDescribe("分页") @PathParam("page") Integer page,
            @JaxrsParameterDescribe("数量") @PathParam("size") Integer size, JsonElement jsonElement) {
        EffectivePerson effectivePerson = this.effectivePerson(request);
        ActionResult<List<ActionQueryListWithFilterPaging.Wo>> result = new ActionResult<>();
        Boolean check = true;

        if (check) {
            try {
                result = new ActionQueryListWithFilterPaging().execute(request, page, size, jsonElement,
                        effectivePerson);
            } catch (Exception e) {
                result = new ActionResult<>();
                result.error(e);
                logger.error(e, effectivePerson, request, null);
            }
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
    }

    @JaxrsMethodDescribe(value = "分页查询符合过滤条件的已发布的信息内容.", action = ActionQueryListWithFilterPaging.class)
    @POST
    @Path("filter/list/{page}/size/{size}/mockputtopost")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void query_listWithFilterPagingMockPutToPost(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request, @JaxrsParameterDescribe("分页") @PathParam("page") Integer page,
            @JaxrsParameterDescribe("数量") @PathParam("size") Integer size, JsonElement jsonElement) {
        EffectivePerson effectivePerson = this.effectivePerson(request);
        ActionResult<List<ActionQueryListWithFilterPaging.Wo>> result = new ActionResult<>();
        Boolean check = true;

        if (check) {
            try {
                result = new ActionQueryListWithFilterPaging().execute(request, page, size, jsonElement,
                        effectivePerson);
            } catch (Exception e) {
                result = new ActionResult<>();
                result.error(e);
                logger.error(e, effectivePerson, request, null);
            }
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
    }

    @JaxrsMethodDescribe(value = "获取文件访问控制信息.", action = ActionQueryGetControl.class)
    @GET
    @Path("{id}/control")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void query_getControl(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("信息文档ID") @PathParam("id") String id) {
        EffectivePerson effectivePerson = this.effectivePerson(request);
        ActionResult<ActionQueryGetControl.Wo> result = new ActionResult<>();
        Boolean check = true;
        if (check) {
            try {
                result = new ActionQueryGetControl().execute(request, id, effectivePerson);
            } catch (Exception e) {
                result = new ActionResult<>();
                result.error(e);
                logger.error(e, effectivePerson, request, null);
            }
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "获取文件可见范围内的所有人员.", action = ActionQueryListVisiblePersons.class)
    @GET
    @Path("{id}/persons")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void query_getVisiblePersons(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request, @JaxrsParameterDescribe("信息文档ID") @PathParam("id") String id) {
        EffectivePerson effectivePerson = this.effectivePerson(request);
        ActionResult<ActionQueryListVisiblePersons.Wo> result = new ActionResult<>();
        Boolean check = true;
        if (check) {
            try {
                result = new ActionQueryListVisiblePersons().execute(request, id, effectivePerson);
            } catch (Exception e) {
                result = new ActionResult<>();
                result.error(e);
                logger.error(e, effectivePerson, request, null);
            }
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "查询当前用户是否有阅读文档的权限.", action = ActionQueryPermissionReadDocument.class)
    @GET
    @Path("{id}/permission/read")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void query_PermissionReadDocument(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request, @JaxrsParameterDescribe("信息文档ID") @PathParam("id") String id) {
        EffectivePerson effectivePerson = this.effectivePerson(request);
        ActionResult<ActionQueryPermissionReadDocument.Wo> result = new ActionResult<>();
        Boolean check = true;

        if (check) {
            try {
                result = new ActionQueryPermissionReadDocument().execute(effectivePerson, id, null);
            } catch (Exception e) {
                result = new ActionResult<>();
                result.error(e);
                logger.error(e, effectivePerson, request, null);
            }
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "列示文档数据.", action = ActionQueryListDocumentData.class)
    @POST
    @Path("list/document/data")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void query_listDocumentData(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request, JsonElement jsonElement) {
        EffectivePerson effectivePerson = this.effectivePerson(request);
        ActionResult<List<ActionQueryListDocumentData.Wo>> result = new ActionResult<>();

        try {
            result = new ActionQueryListDocumentData().execute(request, effectivePerson, jsonElement);
        } catch (Exception e) {
            result.error(e);
            logger.error(e, effectivePerson, request, jsonElement);
        }

        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
    }

    @JaxrsMethodDescribe(value = "列示文档.", action = ActionQueryListDocument.class)
    @POST
    @Path("list/document")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void query_listDocument(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request, JsonElement jsonElement) {
        EffectivePerson effectivePerson = this.effectivePerson(request);
        ActionResult<List<ActionQueryListDocument.Wo>> result = new ActionResult<>();

        try {
            result = new ActionQueryListDocument().execute(effectivePerson, jsonElement);
        } catch (Exception e) {
            result.error(e);
            logger.error(e, effectivePerson, request, jsonElement);
        }

        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
    }

    @JaxrsMethodDescribe(value = "查看文档数据.", action = ActionQueryGetDocumentData.class)
    @GET
    @Path("{id}/document/data")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void query_getDocumentData(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("信息文档ID") @PathParam("id") String id) {
        EffectivePerson effectivePerson = this.effectivePerson(request);
        ActionResult<ActionQueryGetDocumentData.Wo> result = new ActionResult<>();
        Boolean check = true;

        if (check) {
            try {
                result = new ActionQueryGetDocumentData().execute(request, effectivePerson, id);
            } catch (Exception e) {
                result = new ActionResult<>();
                result.error(e);
                logger.error(e, effectivePerson, request, null);
            }
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "管理员分页查询符合过滤条件的已发布的信息内容.", action = ActionQueryListWithFilterPagingAdmin.class)
    @POST
    @Path("filter/list/{page}/size/{size}/manager")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void managerQuery_listWithFilterPaging(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request,
            @JaxrsParameterDescribe("分页") @PathParam("page") Integer page,
            @JaxrsParameterDescribe("数量") @PathParam("size") Integer size, JsonElement jsonElement) {
        EffectivePerson effectivePerson = this.effectivePerson(request);
        ActionResult<List<ActionQueryListWithFilterPagingAdmin.Wo>> result = new ActionResult<>();
        Boolean check = true;

        if (check) {
            try {
                result = new ActionQueryListWithFilterPagingAdmin().execute(request, page, size, jsonElement,
                        effectivePerson);
            } catch (Exception e) {
                result = new ActionResult<>();
                result.error(e);
                logger.error(e, effectivePerson, request, null);
            }
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
    }

    @JaxrsMethodDescribe(value = "已发布文档消息通知。", action = ActionNotify.class)
    @POST
    @Path("{id}/notify")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void publishNotify(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("文档ID") @PathParam("id") String id, JsonElement jsonElement) {
        EffectivePerson effectivePerson = this.effectivePerson(request);
        ActionResult<ActionNotify.Wo> result = new ActionResult<>();
        try {
            result = new ActionNotify().execute(effectivePerson, id, jsonElement);
        } catch (Exception e) {
            result.error(e);
            logger.error(e, effectivePerson, request, jsonElement);
        }

        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
    }

    @JaxrsMethodDescribe(value = "已发布文档发送html文本到webServer下。", action = ActionPublishHtml.class)
    @POST
    @Path("{id}/publish/html")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void publishHtml(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("文档ID") @PathParam("id") String id, JsonElement jsonElement) {
        EffectivePerson effectivePerson = this.effectivePerson(request);
        ActionResult<ActionPublishHtml.Wo> result = new ActionResult<>();
        try {
            result = new ActionPublishHtml().execute(effectivePerson, id, jsonElement);
        } catch (Exception e) {
            result.error(e);
            logger.error(e, effectivePerson, request, jsonElement);
        }

        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }
}
