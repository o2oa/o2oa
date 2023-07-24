package com.x.query.core.express.assemble.surface.jaxrs.morelikethis;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "com.x.query.core.express.jaxrs.index.ActionPostWo")
public class ActionPostWo extends GsonPropertyObject {

    @FieldDescribe("最大得分.")
    @Schema(description = "最大得分.")
    private Float maxScore;

    @FieldDescribe("最小得分.")
    @Schema(description = "最小得分.")
    private Float minScore;

    @FieldDescribe("相似对象.")
    @Schema(description = "相似对象.")
    private List<WoMoreLikeThis> moreLikeThisList = new ArrayList<>();

    @FieldDescribe("返回数量.")
    @Schema(description = "返回数量.")
    private Long count;

    public Float getMaxScore() {
        return maxScore;
    }

    public void setMaxScore(Float maxScore) {
        this.maxScore = maxScore;
    }

    public Float getMinScore() {
        return minScore;
    }

    public void setMinScore(Float minScore) {
        this.minScore = minScore;
    }

    public List<WoMoreLikeThis> getMoreLikeThisList() {
        return moreLikeThisList;
    }

    public void setMoreLikeThisList(List<WoMoreLikeThis> moreLikeThisList) {
        this.moreLikeThisList = moreLikeThisList;
    }

    public static class WoMoreLikeThis extends GsonPropertyObject {

        @FieldDescribe("分类.")
        @Schema(description = "分类.")
        private String category;

        @FieldDescribe("类型.")
        @Schema(description = "类型.")
        private String type;

        @FieldDescribe("目录标识.")
        @Schema(description = "目录标识.")
        private String key;

        @FieldDescribe("标识.")
        @Schema(description = "标识.")
        private String flag;

        @FieldDescribe("标题.")
        @Schema(description = "标题.")
        private String title;

        @FieldDescribe("创建时间.")
        @Schema(description = "创建时间.")
        private Date createTime;

        @FieldDescribe("更新时间.")
        @Schema(description = "更新时间.")
        private Date updateTime;

        @FieldDescribe("创建人.")
        @Schema(description = "创建人.")
        private String creatorPerson;

        @FieldDescribe("创建人组织.")
        @Schema(description = "创建人组织.")
        private String creatorUnit;

        @FieldDescribe("得分.")
        @Schema(description = "得分.")
        private Float score;

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
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

        public String getFlag() {
            return flag;
        }

        public void setFlag(String flag) {
            this.flag = flag;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public Float getScore() {
            return score;
        }

        public void setScore(Float score) {
            this.score = score;
        }

        public Date getCreateTime() {
            return createTime;
        }

        public void setCreateTime(Date createTime) {
            this.createTime = createTime;
        }

        public Date getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(Date updateTime) {
            this.updateTime = updateTime;
        }

        public String getCreatorPerson() {
            return creatorPerson;
        }

        public void setCreatorPerson(String creatorPerson) {
            this.creatorPerson = creatorPerson;
        }

        public String getCreatorUnit() {
            return creatorUnit;
        }

        public void setCreatorUnit(String creatorUnit) {
            this.creatorUnit = creatorUnit;
        }

    }

}