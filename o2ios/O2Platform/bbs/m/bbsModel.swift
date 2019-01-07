//
//  bbsModel.swift
//  O2Platform
//
//  Created by 刘振兴 on 2016/11/3.
//  Copyright © 2016年 zoneland. All rights reserved.
//

import Foundation
import ObjectMapper
///论坛分区
class BBSForumListData:Mappable {
    var id:String?
    var createTime:String?
    var updateTime:String?
    var sequence:String?
    var forumName:String?
    var forumManagerName:String?
    var forumNotice:String?
    var forumVisiable:String?
    var subjectPublishAble:String?
    var replyPublishAble:String?
    var indexListStyle:String?
    var forumIndexStyle:String?
    var indexRecommendable:String?
    var subjectNeedAudit:Bool?
    var replyNeedAudit:Bool?
    var sectionCreateAble:Bool?
    var sectionTotal:Int?
    var subjectTotal:Int?
    var replyTotal:Int?
    var subjectTotalToday:Int?
    var replyTotalToday:Int?
    var creatorName:String?
    var forumStatus:String?
    var orderNumber:Int?
    var sectionInfoList:[BBSectionListData]?
    
    required init?(map: Map) {
        
    }
    
    func mapping(map: Map) {
        id<-map["id"]
        createTime<-map["createTime"]
        updateTime<-map["updateTime"]
        sequence<-map["sequence"]
        forumName<-map["forumName"]
        forumManagerName<-map["forumManagerName"]
        forumNotice<-map["forumNotice"]
        forumVisiable<-map["forumVisiable"]
        subjectPublishAble<-map["subjectPublishAble"]
        replyPublishAble<-map["replyPublishAble"]
        indexListStyle<-map["indexListStyle"]
        forumIndexStyle<-map["forumIndexStyle"]
        indexRecommendable<-map["indexRecommendable"]
        subjectNeedAudit<-map["subjectNeedAudit"]
        replyNeedAudit<-map["replyNeedAudit"]
        sectionCreateAble<-map["sectionCreateAble"]
        sectionTotal<-map["sectionTotal"]
        subjectTotal<-map["subjectTotal"]
        replyTotal<-map["replyTotal"]
        subjectTotalToday<-map["subjectTotalToday"]
        replyTotalToday<-map["replyTotalToday"]
        creatorName<-map["creatorName"]
        forumStatus<-map["forumStatus"]
        orderNumber<-map["orderNumber"]
        sectionInfoList<-map["sectionInfoList"]
    }
}

///论坛板块
class BBSectionListData:Mappable{
    var id:String?
    var createTime:String?
    var updateTime:String?
    var sequence:String?
    var sectionName:String?
    var forumId:String?
    var forumName:String?
    var mainSectionId:String?
    var mainSectionName:String?
    var sectionLevel:String?
    var sectionDescription:String?
    var sectionNotice:String?
    var icon:String?
    var sectionVisiable:String?
    var subjectPublishAble:String?
    var replyPublishAble:String?
    var moderatorNames:String?
    var sectionType:String?
    var indexRecommendable:Bool?
    var subjectNeedAudit:Bool?
    var replyNeedAudit:Bool?
    var sectionCreateAble:Bool?
    var subjectTotal:Int?
    var replyTotal:Int?
    var subjectTotalToday:Int?
    var replyTotalToday:Int?
    var creatorName:String?
    var sectionStatus:String?
    var orderNumber:Int?
    
    required init?(map: Map) {
        
    }
    
    func mapping(map: Map) {
        id<-map["id"]
        createTime<-map["createTime"]
        updateTime<-map["updateTime"]
        sequence<-map["sequence"]
        sectionName<-map["sectionName"]
        forumId<-map["forumId"]
        forumName<-map["forumName"]
        mainSectionId<-map["mainSectionId"]
        mainSectionName<-map["mainSectionName"]
        sectionLevel<-map["sectionLevel"]
        sectionDescription<-map["sectionDescription"]
        sectionNotice<-map["sectionNotice"]
        icon<-map["icon"]
        sectionVisiable<-map["sectionVisiable"]
        subjectPublishAble<-map["subjectPublishAble"]
        replyPublishAble<-map["replyPublishAble"]
        moderatorNames<-map["moderatorNames"]
        sectionType<-map["sectionType"]
        indexRecommendable<-map["indexRecommendable"]
        subjectNeedAudit<-map["subjectNeedAudit"]
        replyNeedAudit<-map["replyNeedAudit"]
        sectionCreateAble<-map["sectionCreateAble"]
        subjectTotal<-map["subjectTotal"]
        replyTotal<-map["replyTotal"]
        subjectTotalToday<-map["subjectTotalToday"]
        replyTotalToday<-map["replyTotalToday"]
        creatorName<-map["creatorName"]
        sectionStatus<-map["sectionStatus"]
        orderNumber<-map["orderNumber"]

    }
}

//论坛帖子
class BBSSubjectData: Mappable {
    var id:String?
    var createTime:String?
    var updateTime:String?
    var title:String?
    var type:String?
    var summary:String?
    var content:String?
    var latestReplyTime:String?
    var latestReplyUser:String?
    var latestReplyId:String?
    var replyTotal:Int?
    var viewTotal:Int?
    var hot:Int?
    var stopReply:Bool?
    var recommendToBBSIndex:Bool?
    var bBSIndexSetterName:String?
    var recommendToForumIndex:Bool?
    var forumIndexSetterName:String?
    var topToSection:Bool?
    var topToMainSection:Bool?
    var topToForum:Bool?
    var topToBBS:Bool?
    var isTopSubject:Bool?
    var isCreamSubject:Bool?
    var screamSetterName:String?
    var screamSetterTime:String?
    var isOriginalSubject:Bool?
    var originalSetterName:String?
    var isRecommendSubject:Bool?
    var recommendorName:String?
    var recommendTime:String?
    var creatorName:String?
    var subjectAuditStatus:String?
    var auditorName:String?
    var subjectStatus:String?
    var orderNumber:Int?
    var attachmentList:[String]?
    var machineName:String?
    var systemType:String?
    var hostIp:String?
    var subjectAttachmentList:[BBSSubjectAttachmentData]?//附件对象列表
    
    required init?(map: Map) {
        
    }
    
    func mapping(map: Map) {
        id<-map["id"]
        createTime<-map["createTime"]
        updateTime<-map["updateTime"]
        title<-map["title"]
        type<-map["type"]
        summary<-map["summary"]
        content<-map["content"]
        latestReplyTime<-map["latestReplyTime"]
        latestReplyUser<-map["latestReplyUser"]
        latestReplyId<-map["latestReplyId"]
        replyTotal<-map["replyTotal"]
        viewTotal<-map["viewTotal"]
        hot<-map["hot"]
        stopReply<-map["stopReply"]
        recommendToBBSIndex<-map["recommendToBBSIndex"]
        bBSIndexSetterName<-map["bBSIndexSetterName"]
        recommendToForumIndex<-map["recommendToForumIndex"]
        forumIndexSetterName<-map["forumIndexSetterName"]
        topToSection<-map["topToSection"]
        topToMainSection<-map["topToMainSection"]
        topToForum<-map["topToForum"]
        topToBBS<-map["topToBBS"]
        isTopSubject<-map["isTopSubject"]
        isCreamSubject<-map["isCreamSubject"]
        screamSetterName<-map["screamSetterName"]
        screamSetterTime<-map["screamSetterTime"]
        isOriginalSubject<-map["isOriginalSubject"]
        originalSetterName<-map["originalSetterName"]
        isRecommendSubject<-map["isRecommendSubject"]
        recommendorName<-map["recommendorName"]
        recommendTime<-map["recommendTime"]
        creatorName<-map["creatorName"]
        subjectAuditStatus<-map["subjectAuditStatus"]
        auditorName<-map["auditorName"]
        subjectStatus<-map["subjectStatus"]
        orderNumber<-map["orderNumber"]
        attachmentList<-map["attachmentList"]
        machineName<-map["machineName"]
        systemType<-map["systemType"]
        hostIp<-map["hostIp"]
        subjectAttachmentList<-map["subjectAttachmentList"]
    }
}

//附件对象列表
class BBSSubjectAttachmentData:Mappable{
    var id:String?
    var createTime:String?
    var updateTime:String?
    var sequence:String?
    var lastUpdateTime:String?
    var storage:String?
    var forumId:String?
    var forumName:String?
    var sectionId:String?
    var sectionName:String?
    var mainSectionId:String?
    var mainSectionName:String?
    var subjectId:String?
    var title:String?
    var name:String?
    var fileName:String?
    var fileHost:String?
    var filePath:String?
    var storageName:String?
    var description:String?
    var creatorUid:String?
    var `extension`:String?
    var length:CLongLong?
    var url:String?//计算后的地址 下载地址
    
    required init?(map: Map) {
        
    }
    
    func mapping(map: Map) {
        id<-map["id"]
        createTime<-map["createTime"]
        updateTime<-map["updateTime"]
        sequence<-map["sequence"]
        lastUpdateTime<-map["lastUpdateTime"]
        storage<-map["storage"]
        forumId<-map["forumId"]
        forumName<-map["forumName"]
        sectionId<-map["sectionId"]
        sectionName<-map["sectionName"]
        mainSectionId<-map["mainSectionId"]
        mainSectionName<-map["mainSectionName"]
        subjectId<-map["subjectId"]
        title<-map["title"]
        name<-map["name"]
        fileName<-map["fileName"]
        fileHost<-map["fileHost"]
        filePath<-map["filePath"]
        storageName<-map["storageName"]
        description<-map["description"]
        creatorUid<-map["creatorUid"]
        `extension`<-map["extension"]
        length<-map["length"]
    }
}

struct SubjectReplayRequestEntity:Mappable {
    
    var creatorName:String?
    
    var content:String?

    var subjectId:String?
    
    var parentId:String?
    
    init() {
        
    }
    
    init?(map: Map) {
        
    }
    
    mutating func mapping(map: Map) {
        creatorName<-map["creatorName"]
        content<-map["content"]
        subjectId<-map["subjectId"]
        parentId<-map["parentId"]
    }
    
}

struct PublishSubjectEntity:Mappable{
    var type:String?
    var title:String?
    var summary:String?
    var content:String?
    var sectionId:String?
    
    init() {
        
    }
    
    init?(map: Map) {
        
    }
    
    mutating func mapping(map: Map) {
        type<-map["type"]
        title<-map["title"]
        summary<-map["summary"]
        content<-map["content"]
        sectionId<-map["sectionId"]
    }
    
}

//分页模型
struct SubjectPageModel {
    var pageNumber:Int = 1
    let pageSize:Int = 20
    var pageTotal:Int = 0
    
    mutating func setPageTotal(_ pageTotal:Int){
        self.pageTotal = pageTotal
    }
    
    mutating func nextPage(){
        if pageTotal > 0 && pageNumber * pageSize < pageTotal {
            pageNumber += 1
        }else{
            
        }
    }
    
    func isLast() -> Bool {
       return  pageNumber * pageSize >= pageTotal
    }
}
