//
//  TodoTaskProcessViewController.swift
//  O2Platform
//
//  Created by 刘振兴 on 16/8/5.
//  Copyright © 2016年 zoneland. All rights reserved.
//

import UIKit
import Alamofire
import AlamofireImage
import AlamofireObjectMapper
import SwiftyJSON
import ObjectMapper

import Eureka
import CocoaLumberjack
import Promises


enum TaskError: Error {
    case apiError(String)
}

class TodoTaskProcessViewController:FormViewController {
    
    
    @IBOutlet weak var submitButton: TaskBarButtonItem!
    
    @IBOutlet weak var taskToolbar: TaskUIToolbar!
    
    
    var backFlag:Int = 0
    
    var taskProcess:TaskProcess?
    
    var option:String?
    
    var ideaText:String?
    
    var mediaOpinion: String = ""
    
    
    

    override func viewDidLoad() {
        super.viewDidLoad()
        //self.tableView?.backgroundColor = UIColor.blue
        let continents = taskProcess?.decisonList
        var oneFlag = false
        if continents!.count == 1 {
            oneFlag = true
        }
        
        form +++ SelectableSection<ImageCheckRow<String>>() { section in
            section.header = HeaderFooterView(title: "选择决策")
        }
        
        self.option = continents?[0]
        for option in continents! {
            form.last! <<< ImageCheckRow<String>(option){ lrow in
                lrow.title = option
                lrow.selectableValue = option
                lrow.value = (oneFlag == true ? option : (option == self.option ? option : nil))
                //lrow.cell.isSelected = option == self.option
            }
        }
        
       
        form +++ Section("流程意见")
            <<< SegmentedRow<String>("segments"){
                $0.options = ["输入意见", "手写意见"]
                $0.value = "输入意见"
            }
            <<< TextAreaRow("idea") { row in
                row.hidden = "$segments != '输入意见'"
                row.placeholder = "请输入意见"
                row.value = self.taskProcess?.opinion ?? ""
                }
            <<< SignatureViewRow("sign") { row in
                row.hidden = "$segments != '手写意见'"
            }
        
        
        self.view.bringSubviewToFront(taskToolbar)
        
       
    }
    
    
    private func finishSubmit() {
        if self.backFlag != 1 && self.backFlag != 2 {
            // 门户页面跳过来的情况
            self.navigationController?.popToRootViewController(animated: true)
        }else {
            //原路返回
            self.performSegue(withIdentifier: "backTodoMe", sender: self.backFlag)
        }
    }
    private func validateFailPopBack() {
        DDLogError("返回表单页面")
        self.navigationController?.popViewController(animated: true)
    }

    
    @IBAction func submitFlowButton(_ sender: UIBarButtonItem) {
        DDLogDebug("submit Button Tap")
        guard let _ = self.option else {
            self.showError(title: "请选择决策")
            return
        }
        if let vies = (parent as? ZLNavigationController)?.viewControllers {
            for vc in vies {
                if vc is TodoTaskDetailViewController {
                    DDLogDebug("开始验证表单。。")
                    (vc as! TodoTaskDetailViewController).checkFormBeforeProcessSubmit(routeName: self.option ?? "", opinion: self.ideaText ?? "", callback: { (result) in
                        DDLogDebug("验证返回了：\(result)")
                        if result {
                            self.letSubmitBegin()
                        }else {
                            self.showError(title: "表单验证不通过，无法提交！")
                            self.validateFailPopBack()
                        }
                    })
                    break
                }
            }
        }else {
            self.showError(title: "异常，无法提交！")
        }

    }
    private func letSubmitBegin() {
        self.showLoading(title: "提交中...")
        //保存、提交
        self.saveTaskData().then { (result) -> Promise<String> in
                DDLogDebug("save task is success ....\(result)")
                let signRow = self.form.rowBy(tag: "sign")
                let signImage = (signRow as? SignatureViewRow)?.cell.signView.getSignatureImage()
                return self.uploadSignaturePNG(image: signImage)
            }.then({ (result) -> Promise<Bool> in
                if result != "" { // 签名上传后的id
                    self.mediaOpinion = result
                }
                return self.submitWork()
            }).then { (result) in
                DDLogDebug("submit work is success....\(result)")
                DispatchQueue.main.async {
                    self.showSuccess(title: "提交成功")
                    self.finishSubmit()
                }
            }.catch { (err) in
                DDLogError("提交异常。。。。\(err.localizedDescription)")
                DispatchQueue.main.async {
                    self.showError(title: "提交失败")
                }
            }
    }
    
    func saveTaskData() -> Promise<Bool> {
        DDLogDebug("保存表单数据，。。。。。。。。。")
        return Promise { fulfill, reject in
            let url = AppDelegate.o2Collect.generateURLWithAppContextKey(TaskContext.taskDataContextKey, query: TaskContext.taskDataSaveQuery, parameter: ["##id##":self.taskProcess!.workId! as AnyObject])
            Alamofire.request(url!,method:.put, parameters: self.taskProcess!.businessDataDict!, encoding: JSONEncoding.default, headers: nil).responseJSON { response in
                switch response.result {
                case .success(let val):
                    let json = JSON(val)
                    if json["type"] == "success" {
                        fulfill(true)
                    }else{
                        DDLogError(json.description)
                        reject(TaskError.apiError(json.description))
                    }
                case .failure(let err):
                    DDLogError(err.localizedDescription)
                    reject(err)
                }
            }
        }
    }
    
    func uploadSignaturePNG(image: UIImage?) -> Promise<String> {
        DDLogDebug("上传签名。。。。。。。。。。。")
        return Promise { fulfill, reject in
            if image == nil {
                //没有签名图片 不上传
                DDLogDebug("没有签名图片 不上。。。。。。")
                fulfill("")
            }else {
                let updloadURL = AppDelegate.o2Collect.generateURLWithAppContextKey(TaskContext.taskContextKey, query: TaskContext.todoTaskUploadAttachmentQuery, parameter: ["##workId##":self.taskProcess!.workId! as AnyObject])
                let site = "$mediaOpinion"
                Alamofire.upload(multipartFormData: { (mData) in
                    let pngData = image?.pngData()
                    mData.append(pngData!, withName: "file", fileName: "signature.png", mimeType: "application/octet-stream")
                    let siteData = site.data(using: String.Encoding.utf8, allowLossyConversion: false)
                    mData.append(siteData!, withName: "site")
                }, to: updloadURL!, encodingCompletion: { (encodingResult) in
                    switch encodingResult {
                    case .success(let upload, _, _):
                        upload.responseJSON {
                            respJSON in
                            switch respJSON.result {
                            case .success(let val):
                                let attachId = JSON(val)["data"]["id"].string!
                                DDLogDebug("上传签名成功。。。id:\(attachId)")
                                fulfill(attachId)
                            case .failure(let err):
                                reject(err)
                                break
                            }
                            
                        }
                    case .failure(let errType):
                        reject(errType)
                    }
                    
                })
            }
        }
    }
    
    func submitWork() -> Promise<Bool> {
       
        return Promise { fulfill, reject in
            self.taskProcess?.decisionRoute = self.option
            self.taskProcess?.decisionIdea = self.ideaText ?? ""
            var params:[String:String] = [:]
            params["routeName"]=self.option
            params["opinion"]=self.ideaText ?? ""
            params["mediaOpinion"] = self.mediaOpinion
            let url = AppDelegate.o2Collect.generateURLWithAppContextKey(TaskContext.taskContextKey, query: TaskContext.todoTaskSaveAndSubmitQuery, parameter: ["##id##":(self.taskProcess?.taskId)! as AnyObject])
            Alamofire.request(url!,method:.post, parameters: params, encoding:JSONEncoding.default, headers: nil).responseJSON { response in
                switch response.result {
                case .success(let val):
                    let json = JSON(val)
                    if json["type"] == "success" {
                        fulfill(true)
                    }else{
                        DDLogError(json.description)
                        reject(TaskError.apiError(json.description))
                    }
                case .failure(let err):
                    DDLogError(err.localizedDescription)
                    reject(err)
                }
            }
        }
    }
    

    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == "backTodoMe" {
            let destVC = segue.destination as! TodoTaskDetailViewController
            destVC.backFlag = backFlag
        }
    }
    
    
    /**
     自动监测各行变化的值
     
     - parameter row:      row
     - parameter oldValue: 旧值
     - parameter newValue: 新值
     */
    override func valueHasBeenChanged(for row: BaseRow, oldValue: Any?, newValue: Any?) {
        DDLogDebug("oldValue = \(String(describing: oldValue)),newValue=\(String(describing: newValue))")
        if row.section == form[0] {
            //if newValue != nil {
                self.option = newValue as? String
            //}
        }
        if row.section == form[1] {
            if row.tag == "idea" {
                self.ideaText = newValue as? String
            }
            if row.tag == "segments" {
                DDLogDebug("切换。。。。。。。。。")
                let key = newValue as? String
                if key == "输入意见" {
                    let signRow = form.rowBy(tag: "sign") as? SignatureViewRow
                    signRow?.cell.signView.clearSignature()
                    self.tableView.isScrollEnabled = true
                }else if key == "手写意见" {
                    let editRow = form.rowBy(tag: "idea") as? TextAreaRow
                    editRow?.value = ""
                    self.tableView.isScrollEnabled = false
                }
            }
        }

    }

}


