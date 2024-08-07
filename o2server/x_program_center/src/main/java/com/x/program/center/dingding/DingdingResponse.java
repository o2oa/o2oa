package com.x.program.center.dingding;

import com.x.base.core.project.gson.GsonPropertyObject;

public class DingdingResponse<T> extends GsonPropertyObject {

    private static final long serialVersionUID = 3897958184570973499L;

    private T result;

    private Integer errcode;

    private Integer sub_code;

    private String sub_msg;

    private String errmsg;

    private String request_id;

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public Integer getErrcode() {
        return errcode;
    }

    public void setErrcode(Integer errcode) {
        this.errcode = errcode;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

    public String getRequest_id() {
        return request_id;
    }

    public void setRequest_id(String request_id) {
        this.request_id = request_id;
    }

    public Integer getSub_code() {
        return sub_code;
    }

    public void setSub_code(Integer sub_code) {
        this.sub_code = sub_code;
    }

    public String getSub_msg() {
        return sub_msg;
    }

    public void setSub_msg(String sub_msg) {
        this.sub_msg = sub_msg;
    }
}
