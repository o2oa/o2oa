//
//  BBSReplySubjectViewController.swift
//  O2Platform
//
//  Created by 刘振兴 on 2016/11/9.
//  Copyright © 2016年 zoneland. All rights reserved.
//

import UIKit
import Alamofire
import AlamofireObjectMapper
import ObjectMapper
import SwiftyJSON
import CocoaLumberjack
import O2OA_Auth_SDK


class BBSReplySubjectViewController: UIViewController {
    
    var subject:BBSSubjectData?{
        didSet {
            
        }
    }
    
    var parentId:String?
    
    var htmlEditorController:ZSSRichTextEditor!

    override func viewDidLoad() {
        super.viewDidLoad()
        htmlEditorController  = ZSSRichTextEditor()
        //htmlEditorController.setSelectedColor(<#T##color: UIColor!##UIColor!#>, tag: <#T##Int32#>)
        htmlEditorController.view.frame = self.view.frame
        self.view.addSubview(htmlEditorController.view)
        htmlEditorController.alwaysShowToolbar = true
        htmlEditorController.placeholder = "请输入回复内容"
        self.addChild(htmlEditorController)

        
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    @IBAction func closeReplyWindow(_ sender: UIBarButtonItem) {
        //print(self.htmlEditorController.getHTML())
        self.dismiss(animated: true, completion: nil)
    }
    
    @IBAction func replySubjectAction(_ sender: UIBarButtonItem) {
        //执行回复提交并返回帖子
        let url = AppDelegate.o2Collect.generateURLWithAppContextKey(BBSContext.bbsContextKey, query: BBSContext.itemReplyQuery, parameter: nil)
        //参数
        var entity = SubjectReplayRequestEntity()
        entity.creatorName = O2AuthSDK.shared.myInfo()?.name
        entity.content = self.htmlEditorController.getHTML()
        entity.subjectId = subject?.id
        if let pid = parentId {
            entity.parentId = pid
        }
        Alamofire.request(url!, method: .post, parameters: entity.toJSON(), encoding: JSONEncoding.default, headers: nil).responseJSON { (response) in
            switch response.result {
            case .success(let val):
                let type = JSON(val)["type"]
                if type == "success" {
                    DispatchQueue.main.async {
                        self.showSuccess(title: "回复成功")
                        DispatchQueue.main.asyncAfter(deadline: .now() + 0.3, execute: {
                            self.performSegue(withIdentifier: "backSubjectSegue", sender: nil)
                        })
                        
                    }
                    
                }else{
                    DispatchQueue.main.async {
                        DDLogError(JSON(val).description)
                        self.showError(title: "回复失败")
                    }
                }
            case .failure(let err):
                DispatchQueue.main.async {
                    DDLogError(err.localizedDescription)
                    self.showError(title: "回复失败")
                }
            }
            
            }
    }
    
    
}
