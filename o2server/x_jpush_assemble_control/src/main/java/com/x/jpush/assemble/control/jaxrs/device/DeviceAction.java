package com.x.jpush.assemble.control.jaxrs.device;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.JaxrsDescribe;
import com.x.base.core.project.annotation.JaxrsMethodDescribe;
import com.x.base.core.project.annotation.JaxrsParameterDescribe;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.HttpMediaType;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.jpush.assemble.control.jaxrs.sample.ExceptionSampleEntityClassFind;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;


@Path("device")
@JaxrsDescribe("极光推送设备服务模块")
public class DeviceAction extends StandardJaxrsAction {
    private static Logger logger = LoggerFactory.getLogger( DeviceAction.class );


    @JaxrsMethodDescribe( value = "获取当前用户所有绑定设备", action = ActionListAll.class )
    @GET
    @Path("all")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response listAll(@Context HttpServletRequest request) {
        ActionResult<List<ActionListAll.Wo>> result = new ActionResult<>();
        try {
            result = new ActionListAll().execute( request, this.effectivePerson(request) );
        } catch (Exception e) {
            result = new ActionResult<>();
            Exception exception = new ExceptionSampleEntityClassFind( e, "获取当前用户所有绑定设备时发生异常！" );
            result.error( exception );
            logger.error( e, this.effectivePerson(request), request, null);
        }
        return ResponseFactory.getDefaultActionResultResponse(result);
    }

    @JaxrsMethodDescribe( value = "检查设备是否已经绑定", action = ActionCheck.class )
    @GET
    @Path("check/{deviceName}/{deviceType}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response checkBind(@Context HttpServletRequest request, @JaxrsParameterDescribe("设备号") @PathParam("deviceName") String deviceName,
                              @JaxrsParameterDescribe("设备类型：android|ios") @PathParam("deviceType") String deviceType) {

        ActionResult<ActionCheck.Wo> result = new ActionResult<>();
        try{
            result = new ActionCheck().execute(request, this.effectivePerson(request), deviceName, deviceType);
        }catch (Exception e) {
            result = new ActionResult<>();
            Exception exception = new ExceptionSampleEntityClassFind( e, "检查设备是否已经绑定时发生异常！" );
            result.error( exception );
            logger.error( e, this.effectivePerson(request), request, null);
        }
        return ResponseFactory.getDefaultActionResultResponse(result);
    }

    @JaxrsMethodDescribe( value = "绑定设备", action = ActionBind.class )
    @POST
    @Path("bind")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response bind(@Context HttpServletRequest request, @JaxrsParameterDescribe("绑定设备的对象") JsonElement jsonElement) {

        ActionResult<ActionBind.Wo> result = new ActionResult<>();
        try{
            result = new ActionBind().execute(request, this.effectivePerson(request), jsonElement);
        }catch (Exception e) {
            result = new ActionResult<>();
            Exception exception = new ExceptionSampleEntityClassFind( e, "绑定设备时发生异常！" );
            result.error( exception );
            logger.error( e, this.effectivePerson(request), request, null);
        }
        return ResponseFactory.getDefaultActionResultResponse(result);
    }

    @JaxrsMethodDescribe( value = "设备解除绑定", action = ActionRemoveBind.class )
    @DELETE
    @Path("unbind/{deviceName}/{deviceType}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response removeBind(@Context HttpServletRequest request, @JaxrsParameterDescribe("设备号") @PathParam("deviceName") String deviceName,
                              @JaxrsParameterDescribe("设备类型：android|ios") @PathParam("deviceType") String deviceType) {

        ActionResult<ActionRemoveBind.Wo> result = new ActionResult<>();
        try{
            result = new ActionRemoveBind().execute(request, this.effectivePerson(request), deviceName, deviceType);
        }catch (Exception e) {
            result = new ActionResult<>();
            Exception exception = new ExceptionSampleEntityClassFind( e, "设备解除绑定时发生异常！" );
            result.error( exception );
            logger.error( e, this.effectivePerson(request), request, null);
        }
        return ResponseFactory.getDefaultActionResultResponse(result);
    }

}
