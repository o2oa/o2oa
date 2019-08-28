//
//  TodoTaskDetailViewController.swift
//  O2Platform
//
//  Created by 刘振兴 on 16/7/31.
//  Copyright © 2016年 zoneland. All rights reserved.
//

import UIKit
import WebKit
import Alamofire
import AlamofireImage
import AlamofireObjectMapper
import SwiftyJSON
import ObjectMapper
import CocoaLumberjack
import Photos
import QuickLook


struct TodoTaskJS {

    static let DATA_TASK = "JSON.encode(layout.appForm.businessData.task);"
    static let DATA_READ = "JSON.encode(layout.appForm.businessData.read);"
    static let DATA_OPINION = "JSON.encode(layout.appForm.getOpinion());"
    static let DATA_CONTROL = "JSON.encode(layout.appForm.businessData.control);"
    static let DATA_WORK_TITLE = "JSON.encode(layout.appForm.businessData.work.title);"
    static let DATA_WORK = "JSON.encode(layout.appForm.businessData.work);"
    static let DATA_BUSINESS = "JSON.encode(layout.appForm.getData());"
    static let CHECK_FORM = "layout.appForm.formValidation(null, null)"

    static func getDataWithJS(_ webView:UIWebView,jscode:String) -> [String:AnyObject] {
        let str = webView.stringByEvaluatingJavaScript(from: jscode)
        //let data = str?.dataUsingEncoding(NSUTF8StringEncoding)
        let json = JSON.init(parseJSON: str!)
        return json.dictionaryObject! as [String : AnyObject]
    }
}


class TodoTaskDetailViewController: BaseWebViewUIViewController {
    
    @IBOutlet weak var progress: UIProgressView!
    
    @IBOutlet weak var webViewContainer: UIView!
    
    
    var qlController = TaskAttachmentPreviewController()
    
    //是否是已办
    open var isWorkCompeleted:Bool = false
    
    open var workId:String?
    
    
    var toolbarView: UIToolbar!
    
    var taskProcess = TaskProcess()
    
    let group = DispatchGroup()
    
    /// backFlag = 1来自MainTask,backFlag = 2来自TodoTask  3是show dis
    var backFlag:Int = 0
    
    var loadUrl:String?
    
    var isJSExecuted:Bool = true
    
    var hasToolbar:Bool = false
    
    var todoTask:TodoTask? {
        didSet {
            var url:String?
            if let workId = todoTask?.work, workId != "" {
                url = AppDelegate.o2Collect.genrateURLWithWebContextKey(DesktopContext.DesktopContextKey, query: DesktopContext.todoDesktopQuery, parameter: ["##workid##":workId as AnyObject])
                self.isWorkCompeleted = false
                self.workId = workId
            }else if let workCompletedId = todoTask?.workCompleted, workCompletedId != "" {
                url = AppDelegate.o2Collect.genrateURLWithWebContextKey(DesktopContext.DesktopContextKey, query: DesktopContext.todoedDestopQuery, parameter: ["##workCompletedId##":workCompletedId as AnyObject])
                self.isWorkCompeleted = true
                self.workId = workCompletedId
            }
            self.loadUrl = url
        }
    }
    
    var myTask: [String : AnyObject]?
    var myRead: [String : AnyObject]?
    var myControl: [String : AnyObject]?
    var myNewControls: [WorkNewActionItem] = []
    var moreActionMenus: O2WorkMoreActionSheet? = nil
    var myTitle: String?
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // 返回按钮重新定义
        self.navigationItem.hidesBackButton = true
        self.navigationItem.leftBarButtonItem = UIBarButtonItem(image: UIImage(named: "icon_fanhui"), style: .plain, target: self, action: #selector(goBack))
        self.navigationItem.leftItemsSupplementBackButton = true
        // 文档查看器
        self.qlController.dataSource = qlController
        self.qlController.delegate = qlController
    
        
        //toolbar
        self.toolbarView = UIToolbar(frame: CGRect(x: 0, y: self.view.height - 44, width: self.view.width, height: 44))
       
        self.automaticallyAdjustsScrollViewInsets = false
        myTitle = todoTask?.title
        if myTitle != nil {
            title = myTitle
        }
        
        //添加工作页面特殊的js处理
        addScriptMessageHandler(key: "appFormLoaded", handler: self)
        addScriptMessageHandler(key: "uploadAttachment", handler: self)
        addScriptMessageHandler(key: "downloadAttachment", handler: self)
        addScriptMessageHandler(key: "replaceAttachment", handler: self)
        addScriptMessageHandler(key: "openDocument", handler: self)
        self.theWebView()
        
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        //监控进度
        webView.addObserver(self, forKeyPath: "estimatedProgress", options: .new, context: nil)
       
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        webView.removeObserver(self, forKeyPath: "estimatedProgress")
    }
    
    
    override func theWebView(){
        super.theWebView()
        self.webViewContainer.addSubview(self.webView)
        self.webView.translatesAutoresizingMaskIntoConstraints = false
        let top = NSLayoutConstraint(item: self.webView, attribute: NSLayoutConstraint.Attribute.top, relatedBy: NSLayoutConstraint.Relation.equal, toItem: self.webViewContainer, attribute: NSLayoutConstraint.Attribute.top, multiplier: 1, constant: 0)
        let bottom = NSLayoutConstraint(item: self.webView, attribute: NSLayoutConstraint.Attribute.bottom, relatedBy: NSLayoutConstraint.Relation.equal, toItem: self.webViewContainer, attribute: NSLayoutConstraint.Attribute.bottom, multiplier: 1, constant: 0)
        let trailing = NSLayoutConstraint(item: self.webView, attribute: NSLayoutConstraint.Attribute.trailing, relatedBy: NSLayoutConstraint.Relation.equal, toItem: self.webViewContainer, attribute: NSLayoutConstraint.Attribute.trailing, multiplier: 1, constant: 0)
        let leading = NSLayoutConstraint(item: self.webView, attribute: NSLayoutConstraint.Attribute.leading, relatedBy: NSLayoutConstraint.Relation.equal, toItem: self.webViewContainer, attribute: NSLayoutConstraint.Attribute.leading, multiplier: 1, constant: 0)
        self.webViewContainer.addConstraints([top, bottom, trailing, leading])
        webView.navigationDelegate = self
        webView.uiDelegate = self
        DDLogDebug("url:\(loadUrl)")
        webView.load(Alamofire.request(loadUrl!).request!)
        webView.allowsBackForwardNavigationGestures = true
    }
    
    override func observeValue(forKeyPath keyPath: String?, of object: Any?, change: [NSKeyValueChangeKey : Any]?, context: UnsafeMutableRawPointer?) {
        if keyPath == "estimatedProgress" {
            progress.isHidden = webView.estimatedProgress == 1
            progress.setProgress(Float(webView.estimatedProgress), animated: true)
        }
    }


    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == "showTodoProcessSegue" {
            let destVC = segue.destination  as! TodoTaskProcessViewController
            //传递到下一步
            destVC.backFlag = backFlag
            destVC.taskProcess = self.taskProcess
        }
    }
    
    /**
     提交后返回此处，在此执行是返回首页还是待办处理页
     
     - parameter segue:
     */
    @IBAction func processBackMe(_ segue:UIStoryboardSegue){
        goBack()
    }
    

//MARK: - private func
    
    @objc func goBack() {
        DDLogError("backFlag = \(backFlag)")
        switch backFlag {
        case 1:
            self.performSegue(withIdentifier: "backMainTask", sender: nil)
            break
        case 2:
            self.performSegue(withIdentifier: "backToTodoTask", sender: nil)
            break
        default: // 3,4都用隐藏 除非删除 删除结束有特殊处理了。
            self.navigationController?.popViewController(animated: true)
            break
        }
    }
    
    @objc func itemBtnDocDeleteAction() {
        DDLogDebug("btnDeleteDoc Click")
        showDefaultConfirm(title: "提示", message: "确认要删除这个文档吗，删除后无法恢复？", okHandler: { (action) in
            self.showMessage(title: "删除中...")
            let url = AppDelegate.o2Collect.generateURLWithAppContextKey(TaskContext.taskDataContextKey, query: TaskContext.taskWorkDeleteQuery, parameter: ["##id##":self.workId! as AnyObject])
            Alamofire.request(url!,method:.delete, parameters: nil, encoding: JSONEncoding.default, headers: nil).responseJSON { response in
                switch response.result {
                case .success(let val):
                    //DDLogDebug(val)
                    let json = JSON(val)
                    if json["type"] == "success" {
                        self.showSuccess(title: "删除成功")
                        DispatchQueue.main.asyncAfter(deadline: DispatchTime.now() + 0.3, execute: {
                            // 删除之后没有这个工作了，所以直接返回列表 防止返回到已办的TodoedTaskViewController
                            if self.backFlag == 4 {
                                self.backFlag = 2
                            }
                            self.goBack()
                        })
                    }else{
                        DDLogError(json.description)
                        self.showError(title: "删除失败")
                    }
                case .failure(let err):
                    DDLogError(err.localizedDescription)
                    self.showError(title: "删除失败")
                }
            }
        })
        
    }
    
    @objc func itemBtnDocSaveAction() {
        DDLogDebug("btnSaveDoc Click")
        self.showMessage(title: "保存中...")
        self.setupData()
        group.notify(queue: DispatchQueue.main) {
            if self.isJSExecuted {
                let url = AppDelegate.o2Collect.generateURLWithAppContextKey(TaskContext.taskDataContextKey, query: TaskContext.taskDataSaveQuery, parameter: ["##id##":self.taskProcess.workId! as AnyObject])
                Alamofire.request(url!,method:.put, parameters: self.taskProcess.businessDataDict!, encoding: JSONEncoding.default, headers: nil).responseJSON { response in
                    switch response.result {
                    case .success(let val):
                        //DDLogDebug(val)
                        let json = JSON(val)
                        if json["type"] == "success" {
                            self.showSuccess(title: "保存成功")
                        }else{
                            DDLogError(json.description)
                            self.showError(title: "保存失败")
                        }
                    case .failure(let err):
                        DDLogError(err.localizedDescription)
                        self.showError(title: "保存失败")
                    }
                }
            }else{
                self.showError(title: "保存失败")
            }

        }

    }
    
    //提供给TodoTaskProcessViewController使用的 提交之前也要验证一次表单，根据传入的路由和意见来判断表单
    @objc func checkFormBeforeProcessSubmit(routeName:String, opinion:String, callback: @escaping (Bool) -> Void) {
        let js = "layout.appForm.formValidation('\(routeName)', '\(opinion)')"
        DDLogDebug("执行验证:\(js)")
        webView.evaluateJavaScript(js) { (data, err) in
            if let str = data  {
                if str is Bool {
                    callback((str as! Bool))
                }else {
                    let isVaild = str as? String
                    if isVaild == "true" {
                        callback(true)
                    }else {
                        callback(false)
                    }
                }
            }else {
                DDLogError("没有返回值。。。。。。。。。")
                callback(false)
            }
        }
    }
    
    @objc func itemBtnNextProcessAction() {
        DDLogDebug("btnNext Process")
        //校验表单
        webView.evaluateJavaScript(TodoTaskJS.CHECK_FORM) { (data, err) in
            if let str = data  {
                let isVaild = str as! Bool
                if isVaild == true {
                    self.setupData()
                    self.group.notify(queue: DispatchQueue.main, execute: {
                        self.performSegue(withIdentifier: "showTodoProcessSegue", sender: nil)
                    })
                }else{
                    DDLogError("表单验证失败。。。。。。。。。。。。")
                    self.showError(title: "表单验证失败，请正确填写表单内容")
                }
            }else {
                DDLogError("没有返回值。。。。。。。。。")
                self.showError(title: "表单验证失败，请正确填写表单内容")
            }
        }
//        let str = self.todoWebView.stringByEvaluatingJavaScript(from: TodoTaskJS.CHECK_FORM)
//        //let str  = "true"
//        if str == "true" {
//            DDLogDebug("next Step")
//            self.setupData()
//            self.performSegue(withIdentifier: "showTodoProcessSegue", sender: nil)
//        }

    }
    
    @objc func itemBtnReadDocAction() {
        DDLogDebug("readButtonAction")
        let url = AppDelegate.o2Collect.generateURLWithAppContextKey(ReadContext.readContextKey, query: ReadContext.readProcessing, parameter: ["##id##":(todoTask?.id)! as AnyObject])
        self.showMessage(title: "提交中...")
        Alamofire.request(url!, method:.post, parameters: myRead, encoding: JSONEncoding.default, headers: nil).responseJSON {  response in
            switch response.result {
            case .success(let val):
                DDLogDebug(JSON(val).description)
                let json = JSON(val)
                if json["type"]=="success"{
                    self.showSuccess(title: "提交成功")
                    DispatchQueue.main.asyncAfter(deadline: DispatchTime.now() + 0.3, execute: {
                        self.goBack()
                    })
                }else {
                    DDLogError(json["message"].description)
                    self.showError(title: "提交失败")
                }
            case .failure(let err):
                DDLogError(err.localizedDescription)
                self.showError(title: "提交失败")
            }
        }
    }
    
    @objc func itemBtnRetractDocAction() {
        DDLogDebug("撤回开始。。。")
        let url = AppDelegate.o2Collect.generateURLWithAppContextKey(TaskedContext.taskedContextKey, query: TaskedContext.taskedRetractQuery, parameter: ["##work##":(self.workId)! as AnyObject])
        self.showMessage(title: "提交中...")
        Alamofire.request(url!, method:.put, parameters: nil, encoding: JSONEncoding.default, headers: nil).responseJSON {  response in
            switch response.result {
            case .success(let val):
                DDLogDebug(JSON(val).description)
                let json = JSON(val)
                if json["type"]=="success"{
                    self.showSuccess(title: "提交成功")
                    DispatchQueue.main.asyncAfter(deadline: DispatchTime.now() + 0.3, execute: {
                        self.goBack()
                    })
                }else {
                    DDLogError(json["message"].description)
                    self.showError(title: "提交失败")
                }
            case .failure(let err):
                DDLogError(err.localizedDescription)
                self.showError(title: "提交失败")
            }
        }
    }
    

    // 网页加载完成后，获取表单数据 判断是什么表单 待办 待阅 已办 已阅
    private func loadDataFromWork() {
        // 加载read 对象 如果是待阅工作 设置已阅时需要用到
        group.enter()
        DispatchQueue.main.async(group: group, execute: DispatchWorkItem(block: {
            DDLogDebug("执行 \(TodoTaskJS.DATA_READ)")
            self.webView.evaluateJavaScript(TodoTaskJS.DATA_READ, completionHandler: { (data, err) in
                if err == nil && data != nil {
                    let json = JSON.init(parseJSON: data as! String)
                    self.myRead = json.dictionaryObject! as [String: AnyObject]
                }else {
                    DDLogError(String(describing: err))
                }
                self.group.leave()
            })
        }))
        // 加载control 是否能撤回
        group.enter()
        DispatchQueue.main.async(group: group, execute: DispatchWorkItem(block: {
            DDLogDebug("执行 \(TodoTaskJS.DATA_CONTROL)")
            self.webView.evaluateJavaScript(TodoTaskJS.DATA_CONTROL, completionHandler: { (data, err) in
                if err == nil && data != nil {
                    let json = JSON.init(parseJSON: (data as! String))
                    DDLogDebug("control: \(data as! String)")
                    self.myControl = json.dictionaryObject! as [String: AnyObject]
                }else {
                    DDLogError(String(describing: err))
                }
                self.group.leave()
            })
        }))
        if myTitle == nil || myTitle!.trim().isEmpty {
            group.enter()
            DispatchQueue.main.async(group: group, execute: DispatchWorkItem(block: {
                 DDLogDebug("执行 \(TodoTaskJS.DATA_WORK_TITLE)")
                self.webView.evaluateJavaScript(TodoTaskJS.DATA_WORK_TITLE, completionHandler: { (data, err) in
                    if err == nil && data != nil {
                        self.myTitle = data as? String
                        self.title = self.myTitle ?? ""
                    }else {
                        DDLogError(String(describing: err))
                    }
                    self.group.leave()
                })
            }))
        }
        
        group.notify(queue: DispatchQueue.main) {
            self.setupToolbarItems()
        }
    }
    
    //20190522 新版底部操作栏
    private func setupToolbarItemsNew() {
        var items: [UIBarButtonItem] = []
        let spaceItem = UIBarButtonItem(barButtonSystemItem: .flexibleSpace, target: self, action: nil)
        if self.myNewControls.count > 0 {
            let action = self.myNewControls[0]
            let firstButton = UIButton(frame: CGRect(x: 0, y: 0, width: 30, height: 30))
            firstButton.setTitle(action.text, for: .normal)
            firstButton.setTitleColor(base_color, for: .normal)
            firstButton.addTapGesture { (tap) in
                self.clickNewActionButton(action: action)
            }
            let firstButtonItem = UIBarButtonItem(customView: firstButton)
            items.append(spaceItem)
            items.append(firstButtonItem)
            items.append(spaceItem)
        }
        if self.myNewControls.count > 1 {
            let action = self.myNewControls[1]
            let secondButton = UIButton(frame: CGRect(x: 0, y: 0, width: 30, height: 30))
            secondButton.setTitle(action.text, for: .normal)
            secondButton.setTitleColor(base_color, for: .normal)
            secondButton.addTapGesture { (tap) in
                self.clickNewActionButton(action: action)
            }
            let secondButtonItem = UIBarButtonItem(customView: secondButton)
            items.append(spaceItem)
            items.append(secondButtonItem)
            items.append(spaceItem)
        }
        
        // 更多按钮
        if self.myNewControls.count > 2 {
            let moreButton = UIButton(frame: CGRect(x: 0, y: 0, width: 30, height: 30))
            moreButton.setImage(UIImage(named: "icon_more_s"), for: .normal)
            moreButton.addTapGesture { (tap) in
                self.moreActionMenus?.show()
            }
            let moreButtonItem = UIBarButtonItem(customView: moreButton)
            items.append(moreButtonItem)
            self.moreActionMenus = O2WorkMoreActionSheet(moreControls: self.myNewControls) { item in
                self.clickNewActionButton(action: item)
            }
        }
        
        if items.count > 0 {
             self.layoutBottomBar(items: items)
        }
    }
    
    //新版操作按钮点击动作
    private func clickNewActionButton(action: WorkNewActionItem) {
        DDLogDebug("click .....\(action.text)")
        let actionScript = action.actionScript
        if actionScript != "" {
            let jsExc = "layout.app.appForm._runCustomAction(\(actionScript))"
            DDLogDebug(jsExc)
            DispatchQueue.main.async {
                self.webView.evaluateJavaScript(jsExc) { (data, err) in
                    DDLogDebug("actionScript excute finish!!!!")
                }
            }
        }else {
            let control = action.control
            switch control {
            case "allowDelete":
                self.itemBtnDocDeleteAction()
                break
            case "allowSave":
                self.itemBtnDocSaveAction()
                break
            case "allowProcessing":
                self.itemBtnNextProcessAction()
                break
            case "allowReadProcessing":
                self.itemBtnReadDocAction()
                break
            case "allowRetract":
                self.itemBtnRetractDocAction()
                break
            default:
                let jsExc = "layout.app.appForm[\"\(action.action)\"]()"
                DDLogDebug(jsExc)
                DispatchQueue.main.async {
                    self.webView.evaluateJavaScript(jsExc) { (data, err) in
                        DDLogDebug("actionScript excute finish!!!!")
                    }
                }
            }
        }
    }
    
    private func setupToolbarItems() {
        DDLogDebug("setupToolbarItems 处理底部按钮， 根据control")
        if self.myNewControls.count > 0 { //新版操作按钮
            self.setupToolbarItemsNew()
        }else {
            var items: [UIBarButtonItem] = []
            if self.myControl != nil {
                let spaceItem = UIBarButtonItem(barButtonSystemItem: .flexibleSpace, target: self, action: nil)
                if let allowDelete = self.myControl!["allowDelete"] as? Bool {
                    if allowDelete { //删除工作
                        DDLogDebug("删除工作。。。。。。。。。。。。。。。。。。。。。。安装按钮")
                        let deleteBtn = UIButton(frame: CGRect(x: 0, y: 0, width: 30, height: 30))
                        deleteBtn.setTitle("删除", for: .normal)
                        deleteBtn.setTitleColor(base_color, for: .normal)
                        deleteBtn.addTapGesture { (tap) in
                            self.itemBtnDocDeleteAction()
                        }
                        let deleteItem = UIBarButtonItem(customView: deleteBtn)
                        items.append(spaceItem)
                        items.append(deleteItem)
                        items.append(spaceItem)
                    }
                }
                if let allowSave = self.myControl!["allowSave"] as? Bool {
                    if allowSave {// 保存工作
                        DDLogDebug("保存工作。。。。。。。。。。。。。。。。。。。。。。安装按钮")
                        let saveBtn = UIButton(frame: CGRect(x: 0, y: 0, width: 30, height: 30))
                        saveBtn.setTitle("保存", for: .normal)
                        saveBtn.setTitleColor(base_color, for: .normal)
                        saveBtn.addTapGesture { (tap) in
                            self.itemBtnDocSaveAction()
                        }
                        let saveItem = UIBarButtonItem(customView: saveBtn)
                        items.append(spaceItem)
                        items.append(saveItem)
                        items.append(spaceItem)
                    }
                }
                if let allowProcessing = self.myControl!["allowProcessing"] as? Bool {
                    if allowProcessing { // 待办工作
                        DDLogDebug("待办工作。。。。。。。。。。。。。。。。。。。。。。安装按钮")
                        let processingBtn = UIButton(frame: CGRect(x: 0, y: 0, width: 30, height: 30))
                        processingBtn.setTitle("继续流转", for: .normal)
                        processingBtn.setTitleColor(base_color, for: .normal)
                        processingBtn.addTapGesture { (tap) in
                           self.itemBtnNextProcessAction()
                        }
                        let processingItem = UIBarButtonItem(customView: processingBtn)
                        items.append(spaceItem)
                        items.append(processingItem)
                        items.append(spaceItem)
                    }
                }
                if let allowReadProcessing = self.myControl!["allowReadProcessing"] as? Bool {
                    if allowReadProcessing { // 待阅 工作
                        DDLogDebug("待阅工作。。。。。。。。。。。。。。。。。。。。。。安装按钮")
                        let readBtn = UIButton(frame: CGRect(x: 0, y: 0, width: 30, height: 30))
                        readBtn.setTitle("已阅", for: .normal)
                        readBtn.setTitleColor(base_color, for: .normal)
                        readBtn.addTapGesture { (tap) in
                            self.itemBtnReadDocAction()
                        }
                        let readItem = UIBarButtonItem(customView: readBtn)
                        items.append(spaceItem)
                        items.append(readItem)
                        items.append(spaceItem)
                    }
                }
                if let allowRetract = self.myControl!["allowRetract"] as? Bool {
                    if allowRetract { // 撤回
                        DDLogDebug("可以撤回。。。。。。。。。。。。。。。。。。。。。。安装按钮")
                        let retractBtn = UIButton(frame: CGRect(x: 0, y: 0, width: 30, height: 30))
                        retractBtn.setTitle("撤回", for: .normal)
                        retractBtn.setTitleColor(base_color, for: .normal)
                        retractBtn.addTapGesture { (tap) in
                            self.itemBtnRetractDocAction()
                        }
                        let retractItem = UIBarButtonItem(customView: retractBtn)
                        items.append(spaceItem)
                        items.append(retractItem)
                        items.append(spaceItem)
                    }
                }
                self.layoutBottomBar(items: items)
                NSLog("\(self.view.subviews)");
            }else {
                DDLogError("没有control 数据异常 按钮无法计算。。。。")
            }
        }
    }
    
    private func layoutBottomBar(items: [UIBarButtonItem]) {
        if items.count > 0 {
            self.toolbarView.items = items
            self.hasToolbar = true
            self.view.addSubview(self.toolbarView)
            self.toolbarView.translatesAutoresizingMaskIntoConstraints = false
            let heightC = NSLayoutConstraint(item: self.toolbarView, attribute: NSLayoutConstraint.Attribute.height, relatedBy: NSLayoutConstraint.Relation.equal, toItem: nil, attribute: NSLayoutConstraint.Attribute.notAnAttribute, multiplier: 0.0, constant: 44)
            self.toolbarView.addConstraint(heightC)
            let bottom = NSLayoutConstraint(item: self.toolbarView, attribute: NSLayoutConstraint.Attribute.bottom, relatedBy: NSLayoutConstraint.Relation.equal, toItem: self.view, attribute: NSLayoutConstraint.Attribute.bottom, multiplier: 1, constant: 0)
            let trailing = NSLayoutConstraint(item: self.toolbarView, attribute: NSLayoutConstraint.Attribute.trailing, relatedBy: NSLayoutConstraint.Relation.equal, toItem: self.view, attribute: NSLayoutConstraint.Attribute.trailing, multiplier: 1, constant: 0)
            let leading = NSLayoutConstraint(item: self.toolbarView, attribute: NSLayoutConstraint.Attribute.leading, relatedBy: NSLayoutConstraint.Relation.equal, toItem: self.view, attribute: NSLayoutConstraint.Attribute.leading, multiplier: 1, constant: 0)
            self.view.addConstraints([bottom, leading, trailing])
            self.view.constraints.forEach { (constraint) in
                if constraint.identifier == "webViewBottomConstraint" {
                    self.view.removeConstraint(constraint)
                }
            }
            let webcTop = NSLayoutConstraint(item: self.webViewContainer, attribute: NSLayoutConstraint.Attribute.bottom, relatedBy: NSLayoutConstraint.Relation.equal, toItem: self.toolbarView, attribute: NSLayoutConstraint.Attribute.top, multiplier: 1, constant: 0)
            self.view.addConstraint(webcTop)
            self.view.layoutIfNeeded()
        }
    }
    
    /**
     *  读取从页面载入的业务及流程数据，建立数据模型
     */
    func setupData(){
        group.enter()
        DispatchQueue.main.async(group: group, execute: DispatchWorkItem(block: {
            DDLogInfo("opinion queue .....")
            self.webView.evaluateJavaScript(TodoTaskJS.DATA_OPINION, completionHandler: { (data, err) in
                if err == nil && data != nil {
                    let opinion = data as! String
                    DDLogInfo("opinion: \(opinion)")
                    if opinion == "\"\"" {
                        self.taskProcess.opinion = ""
                    }else {
                        let json = JSON.init(parseJSON: opinion)
                        let oJson = json.dictionaryObject as [String : AnyObject]?
                        let op = oJson?["opinion"] as? String
                        self.taskProcess.opinion = op
                    }
                }else {
                    DDLogError(String(describing: err))
                }
                self.group.leave()
            })
        }))
        group.enter()
        DispatchQueue.main.async(group: group, execute: DispatchWorkItem(block: {
            DDLogDebug("taskQueue 1")
            self.webView.evaluateJavaScript(TodoTaskJS.DATA_TASK) { (data, err) in
                if err == nil && data != nil {
                    DDLogDebug("taskQueue complete")
                    let json = JSON.init(parseJSON: data as! String)
                    self.taskProcess.taskDict = json.dictionaryObject! as [String : AnyObject]
                    self.taskProcess.taskId = self.taskProcess.taskDict!["id"] as? String
                    self.taskProcess.decisonList = self.taskProcess.taskDict!["routeNameList"] as? [String]
                }else{
                    DDLogError(String(describing: err))
                    self.isJSExecuted = false
                }
                self.group.leave()
            }
            
        }))
        
        group.enter()
        DispatchQueue.main.async(group: group, execute: DispatchWorkItem(block: {
            DDLogDebug("workQueue 1")
            self.webView.evaluateJavaScript(TodoTaskJS.DATA_WORK) { (data, err) in
                if err == nil &&  data != nil {
                    DDLogDebug("workQueue complete")
                    let json = JSON.init(parseJSON: data as! String)
                    self.taskProcess.workDict = json.dictionaryObject! as [String : AnyObject]
                    self.taskProcess.workId = self.taskProcess.workDict!["id"] as? String
                }else{
                    DDLogError(String(describing: err))
                    self.isJSExecuted = false
                }
                self.group.leave()
            }
            
        }))
        
        group.enter()
       DispatchQueue.main.async(group: group, execute: DispatchWorkItem(block: {
            DDLogDebug("businessQueue 1")
            self.webView.evaluateJavaScript(TodoTaskJS.DATA_BUSINESS) { (data, err) in
                if err == nil  && data != nil {
                    DDLogDebug("businessQueue complete")
                    let json = JSON.init(parseJSON: data as! String)
                    self.taskProcess.businessDataDict = json.dictionaryObject! as [String : AnyObject]
                    //do {
                    
                    //}catch{
                        //DDLogError("set routeNameList Error")
                    //}
                }else{
                    DDLogError(String(describing: err))
                    self.isJSExecuted = false
                }
                self.group.leave()
            }
            
        }))
        
        
    }
    
   

}

//MARK: - extension



extension TodoTaskDetailViewController:WKNavigationDelegate,WKUIDelegate {
    
    func webView(_ webView: WKWebView, didStartProvisionalNavigation navigation: WKNavigation!) {
        DDLogDebug("didStartProvisionalNavigation")
    }
    
    func webView(_ webView: WKWebView, didCommit navigation: WKNavigation!) {
        DDLogDebug("didCommit")
    }
    
    func webView(_ webView: WKWebView, didFinish navigation: WKNavigation!) {
        DDLogDebug("didFinish")
        
    }
    
    
    func webView(_ webView: WKWebView, didFail navigation: WKNavigation!, withError error: Error) {
        DDLogDebug("didFail")
        DDLogError(error.localizedDescription)
        self.showError(title: "工作加载异常！")
    }
    
}

extension TodoTaskDetailViewController: O2WKScriptMessageHandlerImplement {
    func userController(_ userContentController: WKUserContentController, didReceive message: WKScriptMessage) {
        let name = message.name
        switch name {
        case "appFormLoaded":
             DDLogDebug("appFormLoaded start。。。。")
            if let newControls = (message.body as? NSString) {
                let str = newControls as String
                DDLogDebug("appFormLoaded , controls :\(str)")
                if str != "true" {
                    myNewControls.removeAll()
                    if let controls = [WorkNewActionItem].deserialize(from: str) {
                        controls.forEach { (item) in
                            if item != nil {
                                myNewControls.append(item!)
                            }
                        }
                    }
                }
            }
            self.loadDataFromWork()
            break
        case "uploadAttachment":
            ZonePermissions.requestImagePickerAuthorization(callback: { (zoneStatus) in
                if zoneStatus == ZoneAuthorizationStatus.zAuthorizationStatusAuthorized {
                    let site = (message.body as! NSDictionary)["site"]
                    self.uploadAttachment(site as! String)
                }else {
                    //显示
                    self.gotoApplicationSettings(alertMessage: "需要照片允许访问权限，是否跳转到手机设置页面开启相机权限？")
                }
            })
            break
        case "downloadAttachment":
            let attachmentId = (message.body as! NSDictionary)["id"]
            self.downloadAttachment(attachmentId as! String)
            break
        case "replaceAttachment":
            let attachmentId = (message.body as! NSDictionary)["id"] as! String
            let site = (message.body as! NSDictionary)["site"] as? String
            self.replaceAttachment(attachmentId, site ?? "")
            break
        case "openDocument":
            let url = (message.body as! NSString)
            self.downloadDocumentAndPreview(String(url))
            break
         
        default:
            DDLogError("未知方法名：\(name)！")
            break
            
        }
    }
    
    
    
    //上传附件
    private func uploadAttachment(_ site:String){
        //选择附件上传
        let updloadURL = AppDelegate.o2Collect.generateURLWithAppContextKey(TaskContext.taskContextKey, query: TaskContext.todoTaskUploadAttachmentQuery, parameter: ["##workId##":workId as AnyObject])
        self.uploadAttachment(site, uploadURL: updloadURL!)
    }
    private func uploadAttachment(_ site:String,uploadURL url:String){
        let vc = FileBSImagePickerViewController()
        bs_presentImagePickerController(vc, animated: true,
                                        select: { (asset: PHAsset) -> Void in
                                            // User selected an asset.
                                            // Do something with it, start upload perhaps?
        }, deselect: { (asset: PHAsset) -> Void in
            // User deselected an assets.
            // Do something, cancel upload?
        }, cancel: { (assets: [PHAsset]) -> Void in
            // User cancelled. And this where the assets currently selected.
        }, finish: { (assets: [PHAsset]) -> Void in
            for asset in assets {
                switch asset.mediaType {
                case .audio:
                    DDLogDebug("Audio")
                case .image:
                    let options = PHImageRequestOptions()
                    options.isSynchronous = true
                    options.deliveryMode = .fastFormat
                    options.resizeMode = .none
                    PHImageManager.default().requestImageData(for: asset, options: options, resultHandler: { (imageData, result, imageOrientation, dict) in
                        //DDLogDebug("result = \(result) imageOrientation = \(imageOrientation) \(dict)")
                        let fileURL = dict?["PHImageFileURLKey"] as! URL
                        DispatchQueue.main.async {
                            self.showMessage(title: "上传中...")
                        }
                        DispatchQueue.global(qos: .userInitiated).async {
                            Alamofire.upload(multipartFormData: { (mData) in
                                //mData.append(fileURL, withName: "file")
                                mData.append(imageData!, withName: "file", fileName: fileURL.lastPathComponent, mimeType: "application/octet-stream")
                                let siteData = site.data(using: String.Encoding.utf8, allowLossyConversion: false)
                                mData.append(siteData!, withName: "site")
                            }, to: url, encodingCompletion: { (encodingResult) in
                                switch encodingResult {
                                case .success(let upload, _, _):
                                    debugPrint(upload)
                                    upload.responseJSON {
                                        respJSON in
                                        switch respJSON.result {
                                        case .success(let val):
                                            let attachId = JSON(val)["data"]["id"].string!
                                            DispatchQueue.main.async {
                                                //ProgressHUD.showSuccess("上传成功")
                                                let callJS = "layout.appForm.uploadedAttachment(\"\(site)\", \"\(attachId)\")"
                                                self.webView.evaluateJavaScript(callJS, completionHandler: { (result, err) in
                                                    self.showSuccess(title: "上传成功")
                                                })
                                            }
                                        case .failure(let err):
                                            DispatchQueue.main.async {
                                                DDLogError(err.localizedDescription)
                                                self.showError(title: "上传失败")
                                            }
                                            break
                                        }
                                        
                                    }
                                case .failure(let errType):
                                    DispatchQueue.main.async {
                                        DDLogError(errType.localizedDescription)
                                        self.showError(title: "上传失败")
                                    }
                                }
                                
                            })
                        }
                    })
                case .video:
                    DDLogDebug("Unknown")
                case .unknown:
                    DDLogDebug("Unknown")
                    
                }
            }
        }, completion: nil)
    }
    
    //下载预览附件
    private func downloadAttachment(_ attachmentId:String){
        //生成两个URL，一个获取附件信息，一个链接正式下载
        var infoURL:String?,downURL:String?
        if isWorkCompeleted {
            infoURL = AppDelegate.o2Collect.generateURLWithAppContextKey(TaskedContext.taskedContextKey, query: TaskedContext.taskedGetAttachmentInfoQuery, parameter: ["##attachmentId##":attachmentId as AnyObject,"##workcompletedId##":workId as AnyObject])
            downURL = AppDelegate.o2Collect.generateURLWithAppContextKey(TaskedContext.taskedContextKey, query: TaskedContext.taskedGetAttachmentQuery, parameter:["##attachmentId##":attachmentId as AnyObject,"##workcompletedId##":workId as AnyObject])
        }else{
            infoURL = AppDelegate.o2Collect.generateURLWithAppContextKey(TaskContext.taskContextKey, query: TaskContext.todoTaskGetAttachmentInfoQuery, parameter: ["##attachmentId##":attachmentId as AnyObject,"##workId##":workId as AnyObject])
            downURL = AppDelegate.o2Collect.generateURLWithAppContextKey(TaskContext.taskContextKey, query: TaskContext.todoTaskGetAttachmentQuery, parameter:["##attachmentId##":attachmentId as AnyObject,"##workId##":workId as AnyObject])
        }
        self.showAttachViewInController(infoURL!, downURL!)
    }
    private func showAttachViewInController(_ infoURL:String,_ downURL:String){
        self.showMessage(title: "下载中...")
        Alamofire.request(infoURL).responseJSON { (response) in
            switch response.result {
            case .success(let val):
                //DDLogDebug(JSON(val).description)
                let info = Mapper<O2TaskAttachmentInfo>().map(JSONString: JSON(val).description)
                //执行下载
                let destination: DownloadRequest.DownloadFileDestination = { _, _ in
                    let documentsURL = FileManager.default.urls(for: .cachesDirectory, in: .userDomainMask)[0]
                    
                    let fileURL = documentsURL.appendingPathComponent((info?.data?.name)!)
                    
                    return (fileURL, [.removePreviousFile, .createIntermediateDirectories])
                }
                Alamofire.download(downURL, to: destination).response(completionHandler: { (response) in
                    if response.error == nil , let fileurl = response.destinationURL?.path {
                        //打开文件
                        self.dismissProgressHUD()
                        self.previewAttachment(fileurl)
                    }else{
                        DispatchQueue.main.async {
                            self.showError(title: "预览文件出错")
                        }
                    }
                })
                break
            case .failure(let err):
                DDLogError(err.localizedDescription)
                DispatchQueue.main.async {
                    self.showError(title: "预览文件出错")
                }
                break
            }
        }
    }
    
    //替换附件
    private func replaceAttachment(_ attachmentId:String, _ site:String){
        //替换结束后回调js名称
        let replaceURL = AppDelegate.o2Collect.generateURLWithAppContextKey(TaskContext.taskContextKey, query: TaskContext.todoTaskUpReplaceAttachmentQuery, parameter: ["##attachmentId##":attachmentId as AnyObject,"##workId##":workId as AnyObject])!
        self.replaceAttachment(site, attachmentId, replaceURL: replaceURL)
    }
    
    
    /**
     * 下载公文 并阅览
     **/
    private func downloadDocumentAndPreview(_ url: String) {
        DDLogDebug("文档下载地址：\(url)")
        self.showMessage(title: "下载中...")
        // 文件地址
        let localFileDestination: DownloadRequest.DownloadFileDestination = { _, response in
            let documentsURL = FileManager.default.urls(for: .cachesDirectory, in: .userDomainMask)[0]
            let fileURL = documentsURL.appendingPathComponent(response.suggestedFilename!)
            // 有重名文件就删除重建
            return (fileURL, [.removePreviousFile, .createIntermediateDirectories])
        }
        Alamofire.download(url, to: localFileDestination).response(completionHandler: { (response) in
            if response.error == nil , let fileurl = response.destinationURL?.path {
                DDLogDebug("文件地址：\(fileurl)")
                let newUrl = self.dealDocFileSaveAsDocx(fileUrl: response.destinationURL!)
                DDLogDebug("处理过的文件地址：\(newUrl.path)")
                //打开文件
                self.dismissProgressHUD()
                self.previewAttachment(newUrl.path)
            }else{
                let msg = response.error?.localizedDescription ?? ""
                DDLogError("下载文件出错，\(msg)")
                DispatchQueue.main.async {
                    self.showError(title: "预览文件出错")
                }
            }
        })
    }
    
    private func replaceAttachment(_ site:String,_ attachmentId:String,replaceURL url:String){
        let vc = FileBSImagePickerViewController()
        bs_presentImagePickerController(vc, animated: true,
                                        select: { (asset: PHAsset) -> Void in
                                            // User selected an asset.
                                            // Do something with it, start upload perhaps?
        }, deselect: { (asset: PHAsset) -> Void in
            // User deselected an assets.
            // Do something, cancel upload?
        }, cancel: { (assets: [PHAsset]) -> Void in
            // User cancelled. And this where the assets currently selected.
        }, finish: { (assets: [PHAsset]) -> Void in
            for asset in assets {
                switch asset.mediaType {
                case .audio:
                    DDLogDebug("Audio")
                case .image:
                    let options = PHImageRequestOptions()
                    options.isSynchronous = true
                    options.deliveryMode = .fastFormat
                    options.resizeMode = .none
                    PHImageManager.default().requestImageData(for: asset, options: options, resultHandler: { (imageData, result, imageOrientation, dict) in
                        //DDLogDebug("result = \(result) imageOrientation = \(imageOrientation) \(dict)")
                        let fileURL = dict?["PHImageFileURLKey"] as! URL
                        DispatchQueue.main.async {
                            self.showMessage(title: "上传中...")
                        }
                        DispatchQueue.global(qos: .userInitiated).async {
                            Alamofire.upload(multipartFormData: { (mData) in
                                //mData.append(fileURL, withName: "file")
                                mData.append(imageData!, withName: "file", fileName: fileURL.lastPathComponent, mimeType: "application/octet-stream")
                                let siteData = site.data(using: String.Encoding.utf8, allowLossyConversion: false)
                                mData.append(siteData!, withName: "site")
                            }, usingThreshold: SessionManager.multipartFormDataEncodingMemoryThreshold, to: url, method: .put, headers: nil, encodingCompletion: { (encodingResult) in
                                switch encodingResult {
                                case .success(let upload, _, _):
                                    debugPrint(upload)
                                    upload.responseJSON {
                                        respJSON in
                                        switch respJSON.result {
                                        case .success( _):
                                            //let attachId = JSON(val)["data"]["id"].string!
                                            DispatchQueue.main.async {
                                                //ProgressHUD.showSuccess("上传成功")
                                                let callJS = "layout.appForm.replacedAttachment(\"\(site)\", \"\(attachmentId)\")"
                                                self.webView.evaluateJavaScript(callJS, completionHandler: { (result, err) in
                                                    self.showSuccess(title: "替换成功")
                                                })
                                            }
                                        case .failure(let err):
                                            DispatchQueue.main.async {
                                                DDLogError(err.localizedDescription)
                                                self.showError(title: "替换失败")
                                            }
                                            break
                                        }
                                        
                                    }
                                case .failure(let errType):
                                    DispatchQueue.main.async {
                                        DDLogError(errType.localizedDescription)
                                        self.showError(title: "替换失败")
                                    }
                                }
                                
                            })
                        }
                    })
                case .video:
                    let options = PHVideoRequestOptions()
                    options.deliveryMode = .fastFormat
                    options.isNetworkAccessAllowed = true
                    options.progressHandler = { (progress,err, stop,dict) in
                        DDLogDebug("progress = \(progress) dict  = \(String(describing: dict))")
                    }
                    PHImageManager.default().requestAVAsset(forVideo: asset, options: options, resultHandler: { (avAsset, avAudioMx, dict) in
                        
                    })
                case .unknown:
                    DDLogDebug("Unknown")
                    
                }
            }
        }, completion: nil)
    }
    
    
    private func previewAttachment(_ url:String){
        let currentURL = NSURL(fileURLWithPath: url)
        if QLPreviewController.canPreview(currentURL) {
            qlController.currentFileURLS.removeAll(keepingCapacity: true)
            qlController.currentFileURLS.append(currentURL)
            qlController.reloadData()
            if #available(iOS 10, *) {
                let navVC = ZLNormalNavViewController(rootViewController: qlController)
                qlController.navigationItem.leftBarButtonItem = UIBarButtonItem(title: "关闭", style: .plain, target: qlController, action: #selector(qlController.qlCloseWindow))
                self.presentVC(navVC)
            }else{
                //if #available(iOS 9, *){
                self.pushVC(qlController)
                //}
            }
            
            
        }else{
            self.showError(title: "此文件无法预览，请在PC端查看")
        }
        
    }

    
    
    
    //处理特殊情况 docx的文件有可能是doc 需要判断下文件信息头
    private func dealDocFileSaveAsDocx(fileUrl: URL) -> URL {
        if fileUrl.pathExtension == "docx" {
            if let data = try? Data(contentsOf: fileUrl) {
                let mimeType = Swime.mimeType(data: data)
                if mimeType?.type == .msi {
                    let newURL = fileUrl.appendingPathExtension("doc")
                    do {
                        DDLogDebug("copy 了一个 文件。。。。。。")
                        try FileManager.default.copyItem(at: fileUrl, to: newURL)
                        return newURL
                    }catch {
                        DDLogError(error.localizedDescription)
                    }
                }
            }
        }
        
        return fileUrl
    }
}

