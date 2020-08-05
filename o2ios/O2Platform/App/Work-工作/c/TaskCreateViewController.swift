//
//  TaskCreateViewController.swift
//  O2Platform
//
//  Created by 刘振兴 on 16/7/29.
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

class TaskCreateViewController: UIViewController {

    var process: AppProcess? {
        didSet {

        }
    }

    var identitys: [IdentityV2]?

    var task: TodoTask?

    var identityViews: [IdentitySelectView] = []

    override func viewDidLoad() {
        super.viewDidLoad()
        self.navigationItem.rightBarButtonItem = UIBarButtonItem(title: "创建", style: .plain, target: self, action: #selector(create))
        title = process?.name
//        showInputUI()

        self.setupUI()

    }

    private func setupUI() {
        let title = self.initTitleAndConstraint()
        let height = title.frame.height + 20
        if let ids = self.identitys {
            var i = 0
            ids.forEach { (identity) in
                self.initIdentityView(id: identity, top: (height + i.toCGFloat * 94 + 20))
                i += 1
            }
            self.identityViews.first?.selectedIdentity()
        }
    }

    private func initIdentityView(id: IdentityV2, top: CGFloat) {
        let view = Bundle.main.loadNibNamed("IdentitySelectView", owner: self, options: nil)?.first as! IdentitySelectView
        view.frame = CGRect(x: 0, y: 0, width: SCREEN_WIDTH, height: 94)
        view.setUp(identity: id)
        self.identityViews.append(view)
        view.translatesAutoresizingMaskIntoConstraints = false
        self.view.addSubview(view)

        let top = NSLayoutConstraint(item: view, attribute: .top, relatedBy: .equal, toItem: view.superview!, attribute: .top, multiplier: 1, constant: top)
        let left = NSLayoutConstraint(item: view, attribute: .leading, relatedBy: .equal, toItem: view.superview!, attribute: .leading, multiplier: 1, constant: 0)
        let right = NSLayoutConstraint(item: view.superview!, attribute: .trailing, relatedBy: .equal, toItem: view, attribute: .trailing, multiplier: 1, constant: 0)
        NSLayoutConstraint.activate([top, left, right])
        
        view.addTapGesture { (tap) in
            let id = view.id?.distinguishedName
            self.identityViews.forEach { (i) in
                if i.id?.distinguishedName == id {
                    i.selectedIdentity()
                } else {
                    i.deSelectedIdentity()
                }
            }
        }
    }


    private func initTitleAndConstraint() -> UILabel {
        //标题
        let title = UILabel()
        title.text = "请选择身份："
        title.font = setting_content_textFont
        title.translatesAutoresizingMaskIntoConstraints = false
        self.view.addSubview(title)
        let top = NSLayoutConstraint(item: title, attribute: .top, relatedBy: .equal, toItem: title.superview!, attribute: .top, multiplier: 1, constant: 20)
        let left = NSLayoutConstraint(item: title, attribute: .leading, relatedBy: .equal, toItem: title.superview!, attribute: .leading, multiplier: 1, constant: 10)
        let right = NSLayoutConstraint(item: title.superview!, attribute: .trailing, relatedBy: .equal, toItem: title, attribute: .trailing, multiplier: 1, constant: 10)
        NSLayoutConstraint.activate([top, left, right])

        return title
    }
//
//    func showInputUI() {
//        form +++ Section("创建流程")
//        //不需要标题
////        <<< TextRow("title") {row in
////            row.title = "标题"
////            row.placeholder = "请输入标题"
////        }.cellSetup({ (cell, row) in
////            //cell.height = 50
////        })
//
//        <<< ActionSheetRow<IdentityV2>("selectedIdentity") {
//            $0.title = "用户身份"
//            $0.selectorTitle = "请选择身份?"
//            $0.options = self.identitys
//            if(self.identitys != nil && self.identitys!.count > 0) {
//                $0.value = self.identitys![0]
//            }
//        }.cellSetup({ (cell, row) in
//            //cell.height = 50
//        })
//
//
//        <<< DateTimeRow("createTime") { row in
//            row.title = "创建时间"
//            row.value = Date()
//            let formatter = DateFormatter()
//            formatter.locale = Locale.current
//            formatter.dateStyle = .long
//            row.dateFormatter = formatter
//        }.cellSetup({ (cell, row) in
//            //cell.height = 50
//        })
//
//            +++ Section()
//        <<< ButtonRow("createButton") { (row: ButtonRow) in
//            row.title = "创建"
//        }.onCellSelection({ (cell, row) in
////                let titleRow:TextRow = self.form.rowBy(tag:"title")!
//            let identityRow: ActionSheetRow<IdentityV2> = self.form.rowBy(tag: "selectedIdentity")!
////                guard let title = titleRow.value else{
////                    self.showError(title: "请输入标题")
////                    return
////                }
//            guard let id = identityRow.value else {
//                self.showError(title: "请选择身份")
//                return
//            }
//
//            if let mode = self.process?.defaultStartMode, mode == O2.O2_Word_draft_mode {
//                self.createDraft(processId: self.process!.id!, identity: id.distinguishedName!)
//            } else {
//                self.createProcess(processId: self.process!.id!, identity: id.distinguishedName!)
//            }
//        })
//    }

    @objc func create() {
        guard let id = self.identityViews.first(where: { (view) -> Bool in
            view.selected == true
        }) else {
            self.showError(title: "请选择身份")
            return
        }
        if let mode = self.process?.defaultStartMode, mode == O2.O2_Word_draft_mode {
            self.createDraft(processId: self.process?.id ?? "", identity: id.id?.distinguishedName ?? "")
        } else {
            self.createProcess(processId: self.process?.id ?? "", identity: id.id?.distinguishedName ?? "")
        }
    }

    //开启流程 创建工作
    func createProcess(processId: String, identity: String) {
        let bean = CreateProcessBean()
        bean.title = ""//不需要标题
        bean.identity = identity
        let createURL = AppDelegate.o2Collect.generateURLWithAppContextKey(WorkContext.workContextKey, query: WorkContext.workCreateQuery, parameter: ["##id##": processId as AnyObject])
        self.showLoading(title: "创建中，请稍候...")
        Alamofire.request(createURL!, method: .post, parameters: bean.toJSON(), encoding: JSONEncoding.default, headers: nil).responseJSON { response in
            debugPrint(response.result)
            switch response.result {
            case .success(let val):
                let taskList = JSON(val)["data"][0]
                DDLogDebug(taskList.description)
                if let tasks = Mapper<TodoTask>().mapArray(JSONString: taskList["taskList"].debugDescription), tasks.count > 0 {
                    let taskStoryboard = UIStoryboard(name: "task", bundle: Bundle.main)
                    let todoTaskDetailVC = taskStoryboard.instantiateViewController(withIdentifier: "todoTaskDetailVC") as! TodoTaskDetailViewController
                    todoTaskDetailVC.todoTask = tasks[0]
                    todoTaskDetailVC.backFlag = 1
                    self.navigationController?.pushViewController(todoTaskDetailVC, animated: true)
                    DispatchQueue.main.async {
                        self.hideLoading()
                    }
                } else {
                    self.showError(title: "创建失败")
                }
            case .failure(let err):
                DDLogError(err.localizedDescription)
                self.showError(title: "创建失败")
            }

        }
    }

    //创建草稿
    private func createDraft(processId: String, identity: String) {
        let bean = CreateProcessBean()
        bean.title = ""
        bean.identity = identity
        let draftCreateUrl = AppDelegate.o2Collect.generateURLWithAppContextKey(WorkContext.workContextKey, query: WorkContext.draftWorkCreateQuery, parameter: ["##processId##": processId as AnyObject])
        self.showLoading(title: "创建中，请稍候...")
        Alamofire.request(draftCreateUrl!, method: .post, parameters: bean.toJSON(), encoding: JSONEncoding.default, headers: nil).responseJSON { response in

            switch response.result {
            case .success(let val):
                let draftData = JSON(val)["data"]
                DDLogDebug(draftData.description)
                if let draft = Mapper<ProcessDraftBean>().map(JSONString: draftData["work"].debugDescription) {
                    let taskStoryboard = UIStoryboard(name: "task", bundle: Bundle.main)
                    let todoTaskDetailVC = taskStoryboard.instantiateViewController(withIdentifier: "todoTaskDetailVC") as! TodoTaskDetailViewController
                    todoTaskDetailVC.draft = draft
                    todoTaskDetailVC.backFlag = 1
                    self.navigationController?.pushViewController(todoTaskDetailVC, animated: true)
                    DispatchQueue.main.async {
                        self.hideLoading()
                    }
                } else {
                    self.showError(title: "创建失败")
                }
            case .failure(let err):
                DDLogError(err.localizedDescription)
                self.showError(title: "创建失败")
            }
        }
    }


}
