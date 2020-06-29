//
//  OOBBSModels.swift
//  O2Platform
//
//  Created by 刘振兴 on 2018/4/5.
//  Copyright © 2018年 zoneland. All rights reserved.
//

import Foundation
import HandyJSON
import ObjectMapper


// MARK:- 读取Section列表参数
class SubjectsParameter {
    var sectionId: String?
    var withTopSubject: Bool?
    var pageParameter: CommonPageParameter?
}


class CommonPageParameter {
    //当前页
    var currentPageNo: Int = 1
    //每页行数
    let countByPage: Int = 20
    //总页数
    private var totalPageCount = 1

    //总行数
    var totalLineCount: Int = -1 {
        didSet {
            if totalLineCount > 0 && totalLineCount > countByPage * currentPageNo {
                //总页数
                totalPageCount = Int(ceil(Double(totalLineCount) / Double(countByPage)))
            }
        }
    }

    init() {

    }

    func calcNextPageNo() -> Bool {
        if currentPageNo < totalPageCount {
            currentPageNo += 1
            return true
        } else {
            return false
        }
    }
}

// MARK:- O2ForumInfo
class O2ForumInfo: NSObject, DataModel {
    var createTime: String?
    var creatorName: String?
    var forumColor: String?
    var forumIndexStyle: String?
    var forumManagerName: String?
    var forumName: String?
    var forumNotice: String?
    var forumStatus: String?
    var forumVisible: String?
    var id: String?
    var indexListStyle: String?
    var indexRecommendable: Bool?
    var orderNumber: Int?
    var replyNeedAudit: Bool?
    var replyPublishAble: String?
    var replyTotal: Int?
    var replyTotalToday: Int?
    var sectionCreateAble: Bool?
    var sectionInfoList: [O2SectionInfo]?
    var sectionTotal: Int?
    var subjectNeedAudit: Bool?
    var subjectPublishAble: String?
    var subjectTotal: Int?
    var subjectTotalToday: Int?
    var subjectType: String?
    var typeCategory: String?
    var updateTime: String?

    required override init() {

    }
}


// MARK:- O2SectionInfo
class O2SectionInfo: NSObject, DataModel {
    var createTime: String?
    var creatorName: String?
    var forumId: String?
    var forumName: String?
    var icon: String?
    var id: String?
    var indexRecommendable: Bool?
    var mainSectionId: String?
    var mainSectionName: String?
    var moderatorNames: String?
    var orderNumber: Int?
    var replyNeedAudit: Bool?
    var replyPublishAble: String?
    var replyTotal: Int?
    var replyTotalToday: Int?
    var sectionDescription: String?
    var sectionLevel: String?
    var sectionName: String?
    var sectionNotice: String?
    var sectionStatus: String?
    var sectionType: String?
    var sectionVisible: String?
    var subSectionCreateAble: Bool?
    var subjectNeedAudit: Bool?
    var subjectPublishAble: String?
    var subjectTotal: Int?
    var subjectTotalToday: Int?
    var subjectType: String?
    var typeCategory: String?
    var updateTime: String?
    required override init() {

    }
}


// MARK:- O2BBSubjectDetailInfo
class O2BBSubjectDetailInfo: NSObject, DataModel {
    var lastSubject: O2BBSSubjectInfo?
    var currentSubject: O2BBSSubjectInfo?
    var nextSubject: O2BBSSubjectInfo?
    required override init() {

    }
}

// MARK:- O2BBSSubjectInfo
class O2BBSSubjectInfo: NSObject, DataModel {
    var acceptReplyId: String?
    var attachmentList: [AnyObject]?
    var auditorName: String?
    var auditorNameShort: String?
    var bBSIndexSetterName: String?
    var bBSIndexSetterNameShort: String?
    var createTime: String?
    var creatorName: String?
    var creatorNameShort: String?
    var forumId: String?
    var forumIndexSetterName: String?
    var forumName: String?
    var hostIp: String?
    var hot: Int?
    var id: String?
    var isCompleted: Bool?
    var isCreamSubject: Bool?
    var isOriginalSubject: Bool?
    var isRecommendSubject: Bool?
    var isTopSubject: Bool?
    var latestReplyId: String?
    var latestReplyTime: String?
    var latestReplyUser: String?
    var latestReplyUserShort: String?
    var machineName: String?
    var mainSectionId: String?
    var mainSectionName: String?
    var orderNumber: Int?
    var originalSetterName: String?
    var originalSetterNameShort: String?
    var picId: String?
    var recommendTime: String?
    var recommendToBBSIndex: Bool?
    var recommendToForumIndex: Bool?
    var recommendorName: String?
    var replyTotal: Int?
    var screamSetterName: String?
    var screamSetterNameShort: String?
    var screamSetterTime: String?
    var sectionId: String?
    var sectionName: String?
    var stopReply: Bool?
    var subjectAuditStatus: String?
    var subjectStatus: String?
    var summary: String?
    var systemType: String?
    var title: String?
    var topToBBS: Bool?
    var topToForum: Bool?
    var topToMainSection: Bool?
    var topToSection: Bool?
    var type: String?
    var typeCategory: String?
    var updateTime: String?
    var viewTotal: Int?
    var voteCount: Int?
    var votePersonVisible: Bool?
    var voteResultVisible: Bool?
    var voted: Bool?
    
    required override init() {}
}


//附件对象列表
class O2BBSSubjectAttachmentInfo: NSObject, DataModel {
    @objc var id: String?
    @objc var createTime: String?
    @objc var updateTime: String?
    @objc var lastUpdateTime: String?
    @objc var storage: String?
    @objc var forumId: String?
    @objc var forumName: String?
    @objc var sectionId: String?
    @objc var sectionName: String?
    @objc var mainSectionId: String?
    @objc var mainSectionName: String?
    @objc var subjectId: String?
    @objc var title: String?
    @objc var name: String?
    @objc var fileName: String?
    @objc var fileHost: String?
    @objc var filePath: String?
    @objc var storageName: String?
    @objc var desc: String?
    @objc var creatorUid: String?
    @objc var ext: String?
    var length: Int?

    required override init() {}

    func mapping(mapper: HelpingMapper) {
        mapper <<< self.ext <-- "extension"
        mapper <<< self.desc <-- "description"
    }
}
