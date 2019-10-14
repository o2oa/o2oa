//
//  MainTaskSecondViewController.swift
//  O2Platform
//
//  Created by 刘振兴 on 2017/3/12.
//  Copyright © 2017年 zoneland. All rights reserved.
//

import UIKit
import Alamofire
import AlamofireImage
import AlamofireObjectMapper
import SwiftyJSON
import ObjectMapper
import CocoaLumberjack
import MBProgressHUD
import WebKit

class MainTaskSecondViewController: UIViewController {
    
    fileprivate static let PAGE_SIZE = 20
    
    
    @IBOutlet weak var tableViewTopConstraint: NSLayoutConstraint!
    @IBOutlet weak var tableView: UITableView!
    //相关变量
    //1段分类
    fileprivate var seguementControl:SegmentedControl!
    //存储热点图片新闻数组
    private var taskImageshowEntitys:[TaskImageshowEntity]  = [] {
        didSet {
            if taskImageshowEntitys.count > 0 {
                self.tableView.tableHeaderView = self.initTableHeaderView()
                let imageShowView = self.tableView.tableHeaderView as! ImageSlidesShowView
                imageShowView.imageshowEntitys = taskImageshowEntitys
//                self.tableView.reloadData()
            }else {
                self.tableView.tableHeaderView = self.initTableHeaderImageView()
            }
        }
    }
    
    fileprivate var newPublishPageModel = CommonPageModel(MainTaskSecondViewController.PAGE_SIZE)
    
    fileprivate var newTaskPageModel = CommonPageModel(MainTaskSecondViewController.PAGE_SIZE)
    
    //所有首页应用
    private var homeApps:[O2App] = []
    
    //所有待办数据
    fileprivate var todoTasks:[TodoTask] = []
    
    //所有最新公告数据
    fileprivate var newPublishInfos:[CMS_PublishInfo] = []
    
    //顶部导航
    private lazy var navView: MyView = {
        let nav = MyView(frame: CGRect.init(x: 0, y: 0, width: SCREEN_WIDTH, height: safeAreaTopHeight))
        nav.backgroundColor = UIColor.clear
        return nav
    }()
    
    //分段视图
    lazy var segmentView: UIView = {
        let view = UIView(frame: CGRect(x: 0, y: 0, width: SCREEN_WIDTH, height: 40))
        view.backgroundColor = UIColor.white
        view.addSubview(self.seguementControl)
        return view
    }()
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        loadHomeApps()
        loadPlayerList()
        loadNewPublish(newPublishPageModel)
        //self.initBarManager()
        
        
        
        self.navigationController?.navigationBar.isHidden = true
        self.tableView.delegate = self
    }
    
    
    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        self.tableView.delegate = nil
        self.navigationController?.navigationBar.isHidden = false
        //ZoneNavigationBarManager.reStoreToSystemNavigationBar()
        
    }

    override func viewDidLoad() {
        super.viewDidLoad()
        //self.title = "首页"
        self.automaticallyAdjustsScrollViewInsets = false
        self.navView.tableViews.append(self.tableView)
        //添加扫描按钮事件
        self.navView.scanBtn?.addTarget(self, action: #selector(startScanAction(_:)), for: .touchUpInside)
        //添加发起按钮事件
        self.navView.addBtn?.addTarget(self, action: #selector(startFlowAction(_:)), for: .touchUpInside)
        self.view.addSubview(self.navView)

        self.seguementControl = initSegumentControl()
        self.tableView.dataSource = self
        self.tableView.tableHeaderView = self.initTableHeaderImageView()
        if #available(iOS 11.0, *) {
            let topConstant = CGFloat(0 - IOS11_TOP_STATUSBAR_HEIGHT)
            self.tableViewTopConstraint.constant = topConstant
        }
       
    }
    
    //初始化热点新闻显示
    private func initTableHeaderView() -> UIView {
        let height = SCREEN_WIDTH / 2
        let frame = CGRect(x: 0, y: 0, width: SCREEN_WIDTH, height: height)
        let imageShowView = ImageSlidesShowView(frame: frame)
        imageShowView.delegate = self
        return imageShowView
    }
    //默认新闻热点使用图片 如果服务器有数据 就用ImageSlidesShowView
    private func initTableHeaderImageView() -> UIView {
        let height = SCREEN_WIDTH / 2
        let frame = CGRect(x: 0, y: 0, width: SCREEN_WIDTH, height: height)
        let imageShowView = UIImageView(image: UIImage(named: "pic_lunbo_1"))
        imageShowView.frame = frame
        imageShowView.contentMode = .scaleAspectFill
        return imageShowView
    }

    //初始化
    private func initBarManager(){
        ZoneNavigationBarManager.managerWithController(self)
        ZoneNavigationBarManager.setBarColor(UIColor.clear)
        ZoneNavigationBarManager.setTintColor(UIColor.white)
//        ZoneNavigationBarManager.setBarColor(UIColor(colorLiteralRed: 1, green: 1, blue: 1, alpha: 0))
//        ZoneNavigationBarManager.setTintColor(UIColor.white)
//        ZoneNavigationBarManager.setStatusBarStyle(.lightContent)
//        ZoneNavigationBarManager.setZeroAlphaOffset(-64)
//        ZoneNavigationBarManager.setFullAlphaOffset(200)
//        ZoneNavigationBarManager.setFullAlphaTintColor(UIColor.red)
        //ZoneNavigationBarManager.setContinus(false)
    }
    
    //初始化分类显示
    private func initSegumentControl() -> SegmentedControl{
        
        //返回一个分类头部
        let titleStrings = ["信息中心", "办公中心"]
        let titles: [NSAttributedString] = {
            let attributes = [NSAttributedString.Key.font: UIFont.systemFont(ofSize: 16), NSAttributedString.Key.foregroundColor: UIColor.black]
            var titles = [NSAttributedString]()
            for titleString in titleStrings {
                let title = NSAttributedString(string: titleString, attributes: attributes)
                titles.append(title)
            }
            return titles
        }()
        let selectedTitles: [NSAttributedString] = {
            let attributes = [NSAttributedString.Key.font: UIFont.systemFont(ofSize: 16), NSAttributedString.Key.foregroundColor: base_color]
            var selectedTitles = [NSAttributedString]()
            for titleString in titleStrings {
                let selectedTitle = NSAttributedString(string: titleString, attributes: attributes)
                selectedTitles.append(selectedTitle)
            }
            return selectedTitles
        }()
        let segmentedControl = SegmentedControl.initWithTitles(titles, selectedTitles: selectedTitles)
        segmentedControl.delegate = self
        segmentedControl.backgroundColor = UIColor.white
        segmentedControl.autoresizingMask = [.flexibleRightMargin, .flexibleWidth]
        segmentedControl.selectionIndicatorStyle = .bottom
        segmentedControl.selectionIndicatorColor = base_color
        segmentedControl.selectionIndicatorHeight = 0.5
        segmentedControl.segmentWidth = (UIScreen.main.bounds.width - 100) / 2
        //segmentedControl.frame.origin.y = 64
        segmentedControl.frame = CGRect(x:50,y:0,width: UIScreen.main.bounds.width, height: 40)
        //segmentedControl.frame.size = CGSize(width: UIScreen.main.bounds.width, height: 40)
        return segmentedControl
    }
    
    //开始扫描
    @objc private func startScanAction(_ sender:AnyObject?)  {
        ScanHelper.openScan(vc: self)
    }
    
    //开始显示新建页面
    @objc private func startFlowAction(_ sender:AnyObject?){
        self.performSegue(withIdentifier: "showAppCategorySegue", sender: nil)
    }
    
    //读取数据待办数据
    fileprivate func loadMainTodo(_ pageModel:CommonPageModel,_ isFirst:Bool = true){
        //pageModel.pageSize = MainTaskSecondViewController.PAGE_SIZE
        self.showLoading(title: "加载中...")
        //ZoneHUD.showNormalHUD((self.navigationController?.view!)!)
        let url = AppDelegate.o2Collect.generateURLWithAppContextKey(TaskContext.taskContextKey, query: TaskContext.todoTaskListQuery, parameter: pageModel.toDictionary() as [String : AnyObject]?)
        if isFirst {
            self.todoTasks.removeAll(keepingCapacity: true)
        }
        Alamofire.request(url!).responseArray(queue: nil, keyPath: "data", context: nil, completionHandler: { (response:DataResponse<[TodoTask]>) in
            DDLogDebug(response.debugDescription)
            switch response.result {
            case .success(let tTasks):
                DispatchQueue.main.async {
                    self.todoTasks.append(contentsOf: tTasks)
                    //let count:Int = JSON(val)["count"].int ?? 0
                    self.newTaskPageModel.setPageTotal(tTasks.count)
                    
                    //ZoneHUD.dismissNormalHUD()
                    self.hideLoading()
                    DispatchQueue.main.async {
                        self.tableView.beginUpdates()
                        self.tableView.reloadSections(IndexSet.init(integer: 1), with: .automatic)
                        self.tableView.endUpdates()
                    }
                }
                //ProgressHUD.showSuccess("读取待办完成")
            case .failure(let err):
                DispatchQueue.main.async {
                    self.showSuccess(title: "加载待办失败")
                    //ZoneHUD.showErrorHUD(errorText: "待办列表出错", 0.5)
                    DDLogError(err.localizedDescription)
                }
            }
        })
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == "showMailSegue" {
            MailViewController.app = sender as? O2App
        }
    }
    
    //读取首页应用
    private func loadHomeApps() {
        let apps = OOAppsInfoDB.shareInstance.queryMainData()
        homeApps.removeAll()
        if apps.isEmpty {
            homeApps.append(contentsOf: O2AppUtil.defaultMainApps)
        }else {
            homeApps.append(contentsOf: apps)
        }
        tableView.beginUpdates()
        tableView.reloadSections(IndexSet.init(integer: 0), with: .automatic)
        tableView.endUpdates()
        
    }
    
    //读取最新公告
    fileprivate func loadNewPublish(_ pageModel:CommonPageModel,_ isFirst:Bool = true){
        //ZoneHUD.showNormalHUD((self.navigationController?.view!)!)
        self.showLoading(title: "加载中...")
        let npURL = AppDelegate.o2Collect.generateURLWithAppContextKey(CMSContext.cmsContextKey, query: CMSContext.cmsCategoryDetailQuery, parameter: pageModel.toDictionary() as[String:AnyObject]?)
        if isFirst {
            self.newPublishInfos.removeAll()
        }
        Alamofire.request(npURL!, method: .put, parameters:[String:Any](), encoding: JSONEncoding.default, headers: nil).responseJSON { (response) in
            switch response.result {
            case .success(let val):
                let json = JSON(val)["data"]
                let type = JSON(val)["type"]
                if type == "success" {
                    let pInfos = Mapper<CMS_PublishInfo>().mapArray(JSONString: json.description)
                    if let uPInfos = pInfos {
                        self.newPublishInfos.append(contentsOf: uPInfos)
                        let count:Int = JSON(val)["count"].int ?? 0
                        self.newPublishPageModel.setPageTotal(count)
                        
                    }
                    DispatchQueue.main.async {
                        //ZoneHUD.dismissNormalHUD()
                        self.hideLoading()
                        self.tableView.beginUpdates()
                        self.tableView.reloadSections(IndexSet.init(integer: 1), with: .automatic)
                        self.tableView.endUpdates()
                    }
                }else{
                    DispatchQueue.main.async {
                        //ZoneHUD.showErrorHUD(errorText: "新闻列表出错", 0.5)
                        self.showError(title: "新闻列表出错")
                        DDLogError(json.description)
                    }
                   
                }
            //print(json)
            case .failure(let err):
                DispatchQueue.main.async {
                    self.showError(title: "新闻列表出错")
                    //ZoneHUD.showErrorHUD(errorText: "新闻列表出错", 0.5)
                    DDLogError(err.localizedDescription)
                }

            }
        }
    }
    
    
    //热点图片新闻
    private func loadPlayerList(){
        self.taskImageshowEntitys.removeAll(keepingCapacity: true)
        let url = AppDelegate.o2Collect.generateURLWithAppContextKey(HotpicContext.hotpicContextKey, query: HotpicContext.hotpicAllListQuery, parameter: ["##page##":"0" as AnyObject,"##count##":"8" as AnyObject])
        Alamofire.request(url!, method: .put, parameters: nil, encoding: JSONEncoding.default, headers: nil).responseJSON { (response) in
            switch response.result {
            case .success(let val):
                let type = JSON(val)["type"]
                if type == "success" {
                    let data = JSON(val)["data"]
                    let entrys = Mapper<TaskImageshowEntity>().mapArray(JSONString: data.description)
                    DispatchQueue.main.async {
                        self.taskImageshowEntitys.append(contentsOf: entrys!)
                    }
                }else{
                    
                }
            case .failure(let err):
                DDLogError(err.localizedDescription)
            }
        }
        
    }
    
    
    // MARK: - 回调刷新
    @IBAction func unWindRefreshSegueForMainTask(_ segue:UIStoryboardSegue){
        DDLogDebug("backRefreshMainTask")
//        newTaskPageModel = CommonPageModel(MainTaskSecondViewController.PAGE_SIZE)
//        self.loadMainTodo(newTaskPageModel)
    }
    
    @IBAction func unBackAppsForMain(_ segue:UIStoryboardSegue){
        DDLogDebug("返回应用列表")
        if segue.source.isKind(of: TaskCreateViewController.self) {
            newTaskPageModel = CommonPageModel(MainTaskSecondViewController.PAGE_SIZE)
            self.loadMainTodo(newTaskPageModel)
//            let sourceVC = segue.source as! TaskCreateViewController
//            if let task = sourceVC.task {
//                self.forwardTodoTaskDetail(task)
//            }
        }
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
}

extension MainTaskSecondViewController:UITableViewDataSource,UITableViewDelegate{
    //分两个Section
    func numberOfSections(in tableView: UITableView) -> Int {
        return 2
    }
    //Section 0 返回1，Section 1返回读了的数据列表
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        switch section {
        case 0:
            return 1
        case 1:
            if self.seguementControl.selectedIndex == 0 {
                return self.newPublishInfos.count
            }else if self.seguementControl.selectedIndex == 1 {
                return self.todoTasks.count
            }else{
                return 0
            }
        default:
            return 0
        }
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        if indexPath.section == 0 {
            //第0段返回应用列表
            let cell = tableView.dequeueReusableCell(withIdentifier: "NewMainAppTableViewCell", for: indexPath) as! (NewMainAppTableViewCell & Configurable)
            cell.apps.removeAll()
            cell.apps.append(contentsOf: homeApps)
            //设置代理
            cell.delegate = self
            return cell
        }else if indexPath.section == 1{
            //第1段返回数据列表
            let cell = tableView.dequeueReusableCell(withIdentifier: "NewMainItemTableViewCell", for: indexPath) as! NewMainItemTableViewCell
            if self.seguementControl.selectedIndex == 0 {
                if !self.newPublishInfos.isEmpty {
                    let obj = self.newPublishInfos[indexPath.row]
                    cell.model = obj
                }
                
            }else if self.seguementControl.selectedIndex == 1 {
                if !self.todoTasks.isEmpty {
                    let obj = self.todoTasks[indexPath.row]
                    cell.model = obj
                }
            }
            
            return cell
        }else{
            return UITableViewCell()
        }
    }
    
    func tableView(_ tableView: UITableView, heightForHeaderInSection section: Int) -> CGFloat {
        if section == 1 {
        return 40.0
        }else{
            return 0
        }
    }
    
    func tableView(_ tableView: UITableView, heightForFooterInSection section: Int) -> CGFloat {
        if section == 0 {
            return 10.0
        }else{
            return 0.0
        }
    }
    
    func tableView(_ tableView: UITableView, viewForFooterInSection section: Int) -> UIView? {
        if section == 0 {
            let view = UIView(frame: CGRect(x: 0, y: 0, width: SCREEN_WIDTH, height: 10.0))
            view.backgroundColor  = UIColor(red: 246.0/255.0, green: 246.0/255.0, blue: 246.0/255.0, alpha: 1.0)
            return view
        }else{
            return nil
        }
    }
    
    func tableView(_ tableView: UITableView, viewForHeaderInSection section: Int) -> UIView? {
        if section == 1{
            return segmentView
        }else{
            return nil
        }
    }
    
    //行高
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        switch indexPath.section {
        case 0:
            return 100
        case 1:
            return 60
        default:
            return 50
        }
    }
    
    //信息点击和新闻点击执行
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        if (indexPath as NSIndexPath).section == 1 {
            //self.navigationController?.navigationBar.isHidden = false
            if seguementControl.selectedIndex == 0 {
                let publishInfo = self.newPublishInfos[indexPath.row]
                let taskInfo = TaskImageshowEntity(JSON: (publishInfo.toJSON()))
                taskInfo?.infoId = publishInfo.id
                self.processApplicationForCMS(entity: taskInfo!)
            }else if seguementControl.selectedIndex == 1{
                let todoTask = self.todoTasks[(indexPath as NSIndexPath).row]
                //DDLogDebug("\(todoTask.title!)")
                self.forwardTodoTaskDetail(todoTask)
            }
        }
    }
    
    private func forwardTodoTaskDetail(_ todoTask:TodoTask){
        let taskStoryboard = UIStoryboard(name: "task", bundle: Bundle.main)
        let todoTaskDetailVC = taskStoryboard.instantiateViewController(withIdentifier: "todoTaskDetailVC") as! TodoTaskDetailViewController
        todoTaskDetailVC.todoTask = todoTask
        todoTaskDetailVC.backFlag = 1
        self.navigationController?.pushViewController(todoTaskDetailVC, animated: true)
    }
}
//分类显示点击代理
extension MainTaskSecondViewController: SegmentedControlDelegate {
    func segmentedControl(_ segmentedControl: SegmentedControl, didSelectIndex selectedIndex: Int) {
        print("Did select index \(selectedIndex)")
        switch segmentedControl.style {
        case .text:
            print("The title is “\(segmentedControl.titles[selectedIndex].string)”\n")
        case .image:
            print("The image is “\(segmentedControl.images[selectedIndex])”\n")
        }
        switch selectedIndex {
        case 0:
            newPublishPageModel = CommonPageModel(MainTaskSecondViewController.PAGE_SIZE)
            loadNewPublish(newPublishPageModel)
            break
        case 1:
            newTaskPageModel = CommonPageModel(MainTaskSecondViewController.PAGE_SIZE)
            loadMainTodo(newTaskPageModel)
            break
        default:
            break
        }
    }
    
}


//应用点击代理
extension MainTaskSecondViewController:NewMainAppTableViewCellDelegate{
    
    func emptyTapClick() {
        // TODO 
//        tabBarController?.cyl_tabBarController.cyl_popSelectTabBarChildViewController(at: 3)
        //tabBarController?.selectedIndex = 3
    }
    
    func NewMainAppTableViewCellWithApp(_ app: O2App) {
        AppConfigSettings.shared.appBackType = 1
        if let segueIdentifier = app.segueIdentifier,segueIdentifier != "" {
            if app.storyBoard! == "webview" {
                DDLogDebug("open webview for : "+app.title!+" url: "+app.vcName!)
                self.performSegue(withIdentifier: segueIdentifier, sender: app)
            }else {
                self.performSegue(withIdentifier: segueIdentifier, sender: nil)
            }
            
        } else {
            if app.storyBoard! == "webview" {
                DDLogError("open webview for : "+app.title!+" url: "+app.vcName!)
            } else {
                // 语音助手还没做
                if app.appId == "o2ai" {
                    app.storyBoard = "ai"
                }
                let story = O2AppUtil.apps.first { (appInfo) -> Bool in
                    return app.appId == appInfo.appId
                }
                var storyBoardName = app.storyBoard
                if story != nil {
                    storyBoardName = story?.storyBoard
                }
                DDLogDebug("storyboard: \(storyBoardName!) , app:\(app.appId!)")
                let storyBoard = UIStoryboard(name: storyBoardName!, bundle: nil)
                //let storyBoard = UIStoryboard(name: app.storyBoard!, bundle: nil)
                var destVC:UIViewController!
                if let vcname = app.vcName,vcname.isEmpty == false {
                    destVC = storyBoard.instantiateViewController(withIdentifier: app.vcName!)
                }else{
                    destVC = storyBoard.instantiateInitialViewController()
                }
                
                if app.vcName == "todoTask" {
                    if "taskcompleted" == app.appId {
                        AppConfigSettings.shared.taskIndex = 2
                    }else if "read" == app.appId {
                        AppConfigSettings.shared.taskIndex = 1
                    }else if "readcompleted" == app.appId {
                        AppConfigSettings.shared.taskIndex = 3
                    }else {
                        AppConfigSettings.shared.taskIndex = 0
                    }
                }
                if destVC.isKind(of: ZLNavigationController.self) {
                    self.show(destVC, sender: nil)
                }else{
                    self.navigationController?.pushViewController(destVC, animated: true)
                }
                
            }
        }

    }
}

//热点新闻代理实现
extension MainTaskSecondViewController:ImageSlidesShowViewDelegate{
    func ImageSlidesShowClick(taskImageshowEntity: TaskImageshowEntity) {
        
        if taskImageshowEntity.application == "BBS" {
            processApplicationForBBS(entity: taskImageshowEntity)
        }else if taskImageshowEntity.application == "CMS" {
            processApplicationForCMS(entity: taskImageshowEntity)
        }
        
    }
    
    func processApplicationForBBS(entity:TaskImageshowEntity){
        let subjectURL = AppDelegate.o2Collect.generateURLWithAppContextKey(BBSContext.bbsContextKey, query: BBSContext.subjectByIdQuery, parameter: ["##id##":entity.infoId! as AnyObject])
        Alamofire.request(subjectURL!, method: .get, parameters: nil, encoding: JSONEncoding.default, headers: nil).responseJSON { (response) in
            switch response.result {
            case .success(let val):
                let type = JSON(val)["type"]
                if type == "success" {
                    DDLogDebug(JSON(val).description)
                    let currentSubject = JSON(val)["data"]["currentSubject"]
                    let subjectData = Mapper<BBSSubjectData>().map(JSONString: currentSubject.description)
                    DispatchQueue.main.async {
                        let bbsStoryboard = UIStoryboard(name: "bbs", bundle: Bundle.main)
                        let destVC = bbsStoryboard.instantiateViewController(withIdentifier: "BBSSubjectDetailVC") as! BBSSubjectDetailViewController
                        destVC.subject = subjectData
                        destVC.title = entity.title
                        //self.navigationController?.navigationBar.isHidden = false
                        self.pushVC(destVC)
                    }
                }else{
                    DDLogError(JSON(val).description)
                }
            case .failure(let err):
                DDLogDebug(err as! String)
            }
        }
    }
    
    func processApplicationForCMS(entity:TaskImageshowEntity){
        let bbsStoryboard = UIStoryboard(name: "information", bundle: Bundle.main)
        let destVC = bbsStoryboard.instantiateViewController(withIdentifier: "CMSSubjectDetailVC") as! CMSItemDetailViewController
        destVC.documentId = entity.infoId
        destVC.title = entity.title
        //self.navigationController?.navigationBar.isHidden = false
        self.pushVC(destVC)

    }
}
