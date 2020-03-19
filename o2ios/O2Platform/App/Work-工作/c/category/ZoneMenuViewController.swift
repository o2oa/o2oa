//
//  ZoneMenuViewController.swift
//  ZoneBarManager
//
//  Created by 刘振兴 on 2017/3/16.
//  Copyright © 2017年 zone. All rights reserved.
//

import UIKit
import Alamofire
import AlamofireImage
import AlamofireObjectMapper
import SwiftyJSON
import ObjectMapper

import CocoaLumberjack

class ZoneMenuViewController: UIViewController {
    
    
    private var mainVC:ZoneMainCategoryViewController!
    
    private var subVC:ZoneSubCategoryViewController!
    
    
    fileprivate var apps:[Application] = [] {
        didSet {
            self.mainVC.apps = apps
        }
    }
    
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
         notificationInit()
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        NotificationCenter.default.removeObserver(self)
    }

    override func viewDidLoad() {
        super.viewDidLoad()
        commonInit()
        loadAppList()
       
    }
    
    private func commonInit(){
        
        self.automaticallyAdjustsScrollViewInsets = false
        //mainMenu
        if let mainVC = self.storyboard?.instantiateViewController(withIdentifier: "mainMenu") {
            self.mainVC = mainVC as! ZoneMainCategoryViewController
            self.addChild(mainVC)
            mainVC.view.frame = CGRect(x: 0, y: 0, width: view.bounds.width * 0.4, height: view.bounds.height)
            self.view.addSubview(mainVC.view)
        }
        if let subVC = self.storyboard?.instantiateViewController(withIdentifier: "subMenu") {
            self.subVC = subVC as! ZoneSubCategoryViewController
            self.addChild(subVC)
            subVC.view.frame = CGRect(x: view.bounds.width * 0.4, y: 0, width: view.bounds.width * 0.6, height: view.bounds.height)
            //let tView = subVC.view as! UITableView
            //tView.contentInset = UIEdgeInsets(top: 0, left: 0, bottom: 0, right: 0)
            self.view.addSubview(subVC.view)
        }
    }
    
    private func notificationInit(){
        NotificationCenter.default.addObserver(self, selector: #selector(reveiveCategoryNotification(_:)), name: ZoneMainCategoryViewController.SELECT_MSG_NAME, object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(receiveSubNotification(_:)), name: ZoneSubCategoryViewController.SELEC_SUB_ITEM, object: nil)
    }
    
    @objc private func reveiveCategoryNotification(_ notification:NSNotification){
        let obj = notification.object
        self.subVC.app = obj as! Application
    }
    
    @objc private func receiveSubNotification(_ notification:NSNotification){
        let obj = notification.object
        loadDepartAndIdentity(process: obj as? AppProcess)
    }
    
    
    func loadAppList(){
        let url = AppDelegate.o2Collect.generateURLWithAppContextKey(ApplicationContext.applicationContextKey, query: ApplicationContext.applicationListQuery, parameter: nil)
        self.showLoading(title: "应用加载中...")
        self.apps.removeAll()
        Alamofire.request(url!).responseArray(queue: nil, keyPath: "data", context: nil, completionHandler: { (response:DataResponse<[Application]>) in
            switch response.result {
            case .success(let apps):
                self.apps.append(contentsOf: apps)
                self.showSuccess(title: "加载完成")
            case .failure(let err):
                DDLogError(err.localizedDescription)
                self.showError(title: "加载失败")
            }
            
        })
    }
    
    //获取身份列表
    func loadDepartAndIdentity(process: AppProcess?){
        if process == nil {
            self.showError(title: "流程信息获取失败！")
            return
        }
        let url = AppDelegate.o2Collect.generateURLWithAppContextKey(TaskContext.taskContextKey, query: TaskContext.todoCreateAvaiableIdentityByIdQuery, parameter: ["##processId##": process!.id as AnyObject])
        Alamofire.request(url!).responseArray(keyPath:"data") { (response:DataResponse<[IdentityV2]>) in
            switch response.result {
            case .success(let identitys):
                if identitys.count > 1 { // 多身份需要去选择身份
                    let data = TaskCreateData(process: process, identitys: identitys)
                    self.gotoChooseIdentity(data: data)
                }else if identitys.count == 1 {
                    self.createProcess(processId: process!.id!, identity: identitys[0].distinguishedName!)
                }else {
                    DispatchQueue.main.async {
                        self.showError(title: "当前用户没有身份，无法创建工作！")
                    }
                }
            case .failure(let err):
                DDLogError(err.localizedDescription)
                DispatchQueue.main.async {
                    self.showError(title: "读取身份列表失败")
                }
            }
        }
    }
    
    //创建流程
    private func createProcess(processId: String, identity:String){
        let bean = CreateProcessBean()
        bean.title = ""
        bean.identity = identity
        let createURL = AppDelegate.o2Collect.generateURLWithAppContextKey(WorkContext.workContextKey, query: WorkContext.workCreateQuery, parameter: ["##id##":processId as AnyObject])
        self.showLoading(title: "创建中，请稍候...")
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
    //进入身份选择页面 创建流程
    private func gotoChooseIdentity(data: TaskCreateData) {
        self.performSegue(withIdentifier: "showStartFlowSegue", sender: data)
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == "showStartFlowSegue" {
            let destVc = segue.destination as! TaskCreateViewController
            if sender is TaskCreateData {
                let data = sender as? TaskCreateData
                destVc.process = data?.process
                destVc.identitys = data?.identitys
            }
        }
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    

}
