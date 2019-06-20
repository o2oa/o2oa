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

class TaskCreateViewController: FormViewController {
    
    var process:AppProcess?{
        didSet{
            
        }
    }
    
    var identitys:[IdentityV2]? {
        didSet {
            for identity in identitys! {
                identityString.append("\(identity.name!)(\(identity.unitName!))")
            }
        }
    }
    
    var task:TodoTask?
    
    var  identityString:[String] = []
    
    override func viewDidLoad() {
        super.viewDidLoad()
        TextRow.defaultCellUpdate = {
            cell,row in
            cell.textLabel?.font = setting_content_textFont
            cell.textLabel?.textColor  = setting_content_textColor
            //cell.textField.attributedPlaceholder = NSAttributedString(string: "",attributes: [NSFontAttributeName:setting_content_textFont,NSBackgroundColorAttributeName:setting_content_textColor])
            //cell.accessoryType = .disclosureIndicator
        }
        ActionSheetRow<String>.defaultCellUpdate = {
            cell,row in
            cell.textLabel?.font = setting_content_textFont
            cell.textLabel?.textColor  = setting_content_textColor
            //cell.accessoryType = .disclosureIndicator
        }
        DateTimeRow.defaultCellUpdate = {
            cell,row in
            cell.textLabel?.font = setting_content_textFont
            cell.textLabel?.textColor  = setting_content_textColor
            //cell.accessoryType = .disclosureIndicator
        }
        
        ButtonRow.defaultCellUpdate = {
            cell,row in
            cell.textLabel?.font = setting_item_textFont
            cell.textLabel?.theme_textColor = ThemeColorPicker(keyPath: "Base.base_color")
            
        }
        title = process?.name
        loadDepartAndIdentity()
       // showInputUI()
        
        
    }
    
    func loadDepartAndIdentity(){
        let url = AppDelegate.o2Collect.generateURLWithAppContextKey(TaskContext.taskContextKey, query: TaskContext.todoCreateAvaiableIdentityByIdQuery, parameter: ["##processId##":process?.id as AnyObject])
        Alamofire.request(url!).responseArray(keyPath:"data") { (response:DataResponse<[IdentityV2]>) in
            switch response.result {
            case .success(let identitys):
                self.identitys = identitys
                DispatchQueue.main.async {
                    self.showInputUI()
                }
                
            case .failure(let err):
                DDLogError(err.localizedDescription)
                DispatchQueue.main.async {
                    self.showError(title: "读取身份列表失败")
                }
            }
        }
     
    }
    
    func showInputUI(){
        form +++ Section("创建流程")
        <<< TextRow("title") {row in
            row.title = "标题"
            row.placeholder = "请输入标题"
        }.cellSetup({ (cell, row) in
            //cell.height = 50
        })
            
        <<< ActionSheetRow<IdentityV2>("selectedIdentity") {
                $0.title = "用户身份"
                $0.selectorTitle = "请选择身份?"
                $0.options = self.identitys
                if(self.identitys != nil && self.identitys!.count>0){
                    $0.value = self.identitys![0]
                }
        }.cellSetup({ (cell, row) in
            //cell.height = 50
        })
        
        <<< DateTimeRow("createTime") { row in
            row.title = "创建时间"
            row.value  = Date()
            let formatter = DateFormatter()
            formatter.locale = Locale.current
            formatter.dateStyle = .long
            row.dateFormatter = formatter
        }.cellSetup({ (cell, row) in
            //cell.height = 50
        })
            
        +++ Section()
            <<< ButtonRow("createButton") { (row:ButtonRow) in
                row.title = "创建"
            }.onCellSelection({ (cell, row) in
                let titleRow:TextRow = self.form.rowBy(tag:"title")!
                let identityRow:ActionSheetRow<IdentityV2> = self.form.rowBy(tag:"selectedIdentity")!
                guard let title = titleRow.value else{
                    self.showError(title: "请输入标题")
                    return
                }
                guard let id = identityRow.value  else {
                    self.showError(title: "请选择身份")
                    return
                }
                self.createProcess(title, identity: id.distinguishedName!)
            })
    }
    
    func createProcess(_ title:String,identity:String){
        DDLogDebug("title = \(title),identity = \(identity)")
        let bean = CreateProcessBean()
        bean.title = title
        bean.identity = identity
        let createURL = AppDelegate.o2Collect.generateURLWithAppContextKey(WorkContext.workContextKey, query: WorkContext.workCreateQuery, parameter: ["##id##":(process?.id)! as AnyObject])
        self.showMessage(title: "创建中，请稍候...")
        Alamofire.request(createURL!,method:.post, parameters: bean.toJSON(), encoding: JSONEncoding.default, headers: nil).responseJSON { response in
            debugPrint(response.result)
            switch response.result {
            case .success(let val):
                let taskList = JSON(val)["data"][0]
                DDLogDebug(taskList.description)
                if let tasks = Mapper<TodoTask>().mapArray(JSONString:taskList["taskList"].debugDescription) , tasks.count > 0 {
                    let taskStoryboard = UIStoryboard(name: "task", bundle: Bundle.main)
                    let todoTaskDetailVC = taskStoryboard.instantiateViewController(withIdentifier: "todoTaskDetailVC") as! TodoTaskDetailViewController
                    todoTaskDetailVC.todoTask = tasks[0]
                    todoTaskDetailVC.backFlag = 1
                    self.navigationController?.pushViewController(todoTaskDetailVC, animated: true)
//                    self.task = tasks[0]
                    DispatchQueue.main.async {
                        self.dismissProgressHUD()
                        //self.performSegue(withIdentifier: "newToBackMainSegue", sender: nil)
                    }
                    
                    //ProgressHUD.showSuccess("创建成功")
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
