package com.x.program.center.core.entity;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Created by fancyLou on 3/12/21.
 * Copyright © 2021 O2. All rights reserved.
 */
@Schema(name = "MPWeixinMenu", description = "服务管理微信菜单.")
@Entity
@ContainerEntity(dumpSize = 1000, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Table(name = PersistenceProperties.MPWeixinMenu.TABLE, uniqueConstraints = {
        @UniqueConstraint(name = PersistenceProperties.MPWeixinMenu.TABLE + JpaObject.IndexNameMiddle
                + JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
                JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class MPWeixinMenu extends SliceJpaObject {


    private static final String TABLE = PersistenceProperties.MPWeixinMenu.TABLE;
    private static final long serialVersionUID = 1188998090619699016L;


    @Override
    public void onPersist() throws Exception {
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @FieldDescribe("数据库主键,自动生成.")
    @Id
    @Column(length = length_id, name = ColumnNamePrefix + id_FIELDNAME)
    private String id = createId();


    public static final String name_FIELDNAME = "name";
    @FieldDescribe("菜单显示名称.")
    @Column(length = length_255B, name = ColumnNamePrefix + name_FIELDNAME)
    @CheckPersist(allowEmpty = false)
    private String name;


    public static final String type_FIELDNAME = "type";
    @FieldDescribe("微信菜单类型，目前支持click、view、miniprogram ")
    @Column(length = length_255B, name = ColumnNamePrefix + type_FIELDNAME)
    private String type;


    public static final String key_FIELDNAME = "key";
    @FieldDescribe("点击事件的key，如果type等于click ，这个key必填")
    @Column(length = length_255B, name = ColumnNamePrefix + key_FIELDNAME)
    private String key;


    public static final String content_FIELDNAME = "content";
    @FieldDescribe("如果type等于click ，发送的消息内容，目前支持文本消息，里面可以添加a链接")
    @Lob
    @Basic(fetch = FetchType.EAGER)
    @Column(length = length_1M, name = ColumnNamePrefix + content_FIELDNAME)
    private String content;



    public static final String url_FIELDNAME = "url";
    @FieldDescribe("打开网页的地址，如果type等于view或者miniprogram ，这个url必填")
    @Column(length = length_2K, name = ColumnNamePrefix + url_FIELDNAME)
    private String url;



    public static final String appid_FIELDNAME = "appid";
    @FieldDescribe("打开小程序的id，如果type等于miniprogram ，这个appid必填")
    @Column(length = length_255B, name = ColumnNamePrefix + appid_FIELDNAME)
    private String appid;



    public static final String pagepath_FIELDNAME = "pagepath";
    @FieldDescribe("打开小程序的页面地址，如果type等于miniprogram ，这个pagepath必填")
    @Column(length = length_255B, name = ColumnNamePrefix + pagepath_FIELDNAME)
    private String pagepath;


    public static final String parentId_FIELDNAME = "parentId";
    @FieldDescribe("父菜单Id")
    @Column(length = length_64B, name = ColumnNamePrefix + parentId_FIELDNAME)
    private String parentId;



    public static final String order_FIELDNAME = "order";
    @FieldDescribe("排序号，字符串排序")
    @Column(length = length_16B, name = ColumnNamePrefix + order_FIELDNAME)
    private String order;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getPagepath() {
        return pagepath;
    }

    public void setPagepath(String pagepath) {
        this.pagepath = pagepath;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }
}
