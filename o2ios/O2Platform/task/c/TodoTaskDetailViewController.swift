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


class TodoTaskDetailViewController: BaseTaskWebViewController {
    
    @IBOutlet weak var progress: UIProgressView!
    
    @IBOutlet weak var webViewContainer: UIView!
    
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
    var myTitle: String?
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // 返回按钮重新定义
        self.navigationItem.hidesBackButton = true
        self.navigationItem.leftBarButtonItem = UIBarButtonItem(image: UIImage(named: "icon_fanhui"), style: .plain, target: self, action: #selector(goBack))
        self.navigationItem.leftItemsSupplementBackButton = true
        
        // 表单加载完成的代理
        self.loadedDelegate = self
        
        //toolbar
        self.toolbarView = UIToolbar(frame: CGRect(x: 0, y: self.view.height - 44, width: self.view.width, height: 44))
       
        self.automaticallyAdjustsScrollViewInsets = false
        myTitle = todoTask?.title
        if myTitle != nil {
            title = myTitle
        }
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
                        self.myTitle = data as! String
                        self.title = self.myTitle
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
    
    private func setupToolbarItems() {
        DDLogDebug("setupToolbarItems 处理底部按钮， 根据control")
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
            
            NSLog("\(self.view.subviews)");
        }else {
            DDLogError("没有control 数据异常 按钮无法计算。。。。")
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
                        let oJson = json.dictionaryObject as? [String : AnyObject]
                        let op = oJson!["opinion"] as? String
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


extension TodoTaskDetailViewController: O2WorkFormLoadedDelegate {
    func workFormLoaded() {
        DDLogInfo("表单页面加载完成， 开始判断工作，生成操作按钮！")
        self.loadDataFromWork()
    }
}

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
    
    
//    func webView(_ webView: WKWebView, decidePolicyFor navigationAction: WKNavigationAction, decisionHandler: @escaping (WKNavigationActionPolicy) -> Void) {
//        print("navigationAction \(navigationAction)")
//        decisionHandler(WKNavigationActionPolicy.allow)
//    }
//    
//    func webView(_ webView: WKWebView, commitPreviewingViewController previewingViewController: UIViewController) {
//         print("previewingViewController \(previewingViewController)")
//    }
//    
//    func webView(_ webView: WKWebView, didFailProvisionalNavigation navigation: WKNavigation!, withError error: Error) {
//        print("didFailProvisionalNavigation navigation \(navigation)")
//    }
//    
//    func webView(_ webView: WKWebView, didReceive challenge: URLAuthenticationChallenge, completionHandler: @escaping (URLSession.AuthChallengeDisposition, URLCredential?) -> Void) {
//        
//    }
//    
//    func webView(_ webView: WKWebView, runJavaScriptAlertPanelWithMessage message: String, initiatedByFrame frame: WKFrameInfo, completionHandler: @escaping () -> Void) {
//        print("runJavaScriptAlertPanelWithMessage message \(message)")
//        completionHandler()
//    }
//    
//    func webView(_ webView: WKWebView, runJavaScriptConfirmPanelWithMessage message: String, initiatedByFrame frame: WKFrameInfo, completionHandler: @escaping (Bool) -> Void) {
//        print("runJavaScriptConfirmPanelWithMessage message \(message)")
//        completionHandler(true)
//    }
//    func webView(_ webView: WKWebView, runJavaScriptTextInputPanelWithPrompt prompt: String, defaultText: String?, initiatedByFrame frame: WKFrameInfo, completionHandler: @escaping (String?) -> Void) {
//        print("runJavaScriptTextInputPanelWithPrompt message \(prompt)")
//        completionHandler("")
//    }
//    
//    
//    func webView(_ webView: WKWebView, didReceiveServerRedirectForProvisionalNavigation navigation: WKNavigation!) {
//        print(navigation)
//    }
//    
//    func webView(_ webView: WKWebView, decidePolicyFor navigationResponse: WKNavigationResponse, decisionHandler: @escaping (WKNavigationResponsePolicy) -> Void) {
//        print("navigationResponse navigationResponse \(navigationResponse)")
//        decisionHandler(.allow)
//    }
    

}

