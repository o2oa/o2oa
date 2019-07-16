//
//  CMSCategoryListViewController.swift
//  O2Platform
//
//  Created by 刘振兴 on 2016/12/8.
//  Copyright © 2016年 zoneland. All rights reserved.
//

import UIKit
import Alamofire
import AlamofireImage
import AlamofireObjectMapper
import SwiftyJSON
import ObjectMapper
import CocoaLumberjack
import O2OA_Auth_SDK


class CMSCategoryListViewController: UIViewController {
    
    
    @IBOutlet weak var tableview: ZLBaseTableView!
    
    
    var pageModel:CommonPageModel = CommonPageModel()
    
//    var segmentedControl:SegmentedControl?{
//        didSet {
//            tabIndex = (segmentedControl?.selectedIndex)!
//        }
//    }
    
    var cmsCategory:CMSCategory?
    
    var tabIndex:Int = 0
    
    var cmsData:CMSData? {
        didSet {
            //生成titles
            if  let list = cmsData?.wrapOutCategoryList {
                for wrap in list {
                    //print("\(wrap.id!),\(wrap.categoryName)")
                    self.itemsKeys.append(wrap.id!)
                    self.itemsTitles.append(wrap.categoryName!)
                }
            }
        }
    }
    
    var currentCategoryId:String = ""
    
    var itemsKeys:[String] = []
    
    var itemsTitles:[String] = []
    
    var itemTotalWidth:CGFloat = 0
    
    var segmentedControl:SegmentedControl?{
        didSet {
            tabIndex = (segmentedControl?.selectedIndex)!
        }
    }
    // 当前用户能够发布category列表
    var canPublishCategories: [CMSWrapOutCategoryList] = []
    var selectedCategory: CMSWrapOutCategoryList?

    override func viewDidLoad() {
        super.viewDidLoad()
         initSegmentedControl()
        
        tableview.delegate = self
        tableview.dataSource = self
        tableview.contentInset = UIEdgeInsets(top: 40, left: 0, bottom: 0, right: 0)
        tableview.mj_header = MJRefreshNormalHeader(refreshingBlock: {
            self.pageModel = CommonPageModel()
            self.loadFirstData()
        })
        
        tableview.mj_footer = MJRefreshAutoFooter(refreshingBlock: {
            //先生成下一页的页号
            if self.pageModel.isLast() == false {
                self.pageModel.nextPageId = (self.cmsCategory?.data?.last?.id)!
                self.pageModel.nextPage()
                self.loadNextPageData()
            }else{
                self.showSuccess(title: "最后一页了，没有帖子了")
            }
        })
        currentCategoryId = self.itemsKeys.first!
        self.loadFirstData()
        self.loadCanPublishCategories()

        // Do any additional setup after loading the view.
    }
    
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == "showDetailContentSegue" {
            let destVC = segue.destination as! CMSItemDetailViewController
            destVC.itemData = sender as! CMSCategoryItemData?
            destVC.fromCreateDocVC = true
        }else if segue.identifier == "createDocument" {
            let createVC = segue.destination as! CMSCreateDocViewController
            createVC.category = self.selectedCategory
        }
    }
    
    @IBAction func viewBack2DocumentList(_ sender: UIStoryboardSegue) {
        DDLogDebug("backto List")
        self.tableview.mj_header.beginRefreshing()
    }
    
    private func sizeForAttributedString(_ attributedString: NSAttributedString) -> CGSize {
        let size = attributedString.size()
        return CGRect(origin: CGPoint.zero, size: size).integral.size
    }

    
    //MARK: - private func
    private func initSegmentedControl() {
        let titles: [NSAttributedString] = {
            let attributes = [NSAttributedString.Key.font: UIFont.systemFont(ofSize: 16), NSAttributedString.Key.foregroundColor: UIColor.black]
            var titles = [NSAttributedString]()
            for titleString in self.itemsTitles {
                let title = NSAttributedString(string: titleString, attributes: attributes)
                titles.append(title)
            }
            return titles
        }()
        let selectedTitles: [NSAttributedString] = {
            let attributes = [NSAttributedString.Key.font: UIFont.systemFont(ofSize: 16), NSAttributedString.Key.foregroundColor: O2ThemeManager.color(for: "Base.base_color")]
            var selectedTitles = [NSAttributedString]()
            for titleString in self.itemsTitles {
                let selectedTitle = NSAttributedString(string: titleString, attributes: attributes)
                selectedTitles.append(selectedTitle)
            }
            return selectedTitles
        }()
        self.segmentedControl = SegmentedControl.initWithTitles(titles, selectedTitles: selectedTitles)
        self.segmentedControl!.delegate = self
        self.segmentedControl!.backgroundColor = toolbar_background_color
        self.segmentedControl!.autoresizingMask = [.flexibleRightMargin, .flexibleWidth]
        self.segmentedControl!.selectionIndicatorStyle = .bottom
        self.segmentedControl!.selectionIndicatorColor = base_color
        self.segmentedControl!.selectionIndicatorHeight = 3
        self.segmentedControl!.segmentWidth = SCREEN_WIDTH / 3
        self.segmentedControl!.frame.origin.y = 0
        self.segmentedControl!.frame.size = CGSize(width: UIScreen.main.bounds.width, height: 40)
        view.insertSubview(self.segmentedControl!, belowSubview: navigationController!.navigationBar)
    }
    
    fileprivate func selectedSegmentioIndex() -> Int {
        return self.tabIndex
    }
    
    func loadFirstData(){
        let url = AppDelegate.o2Collect.generateURLWithAppContextKey(CMSContext.cmsContextKey, query: CMSContext.cmsCategoryDetailQuery, parameter: self.pageModel.toDictionary() as [String : AnyObject]?)
        var params:[String:Array<String>] = [:]
        params["categoryIdList"] = [currentCategoryId]
        Alamofire.request(url!, method: .put, parameters: params, encoding: JSONEncoding.default, headers: nil).responseJSON { (response) in
            switch response.result {
            case .success(let val):
                DDLogDebug(JSON(val).description)
                self.cmsCategory = Mapper<CMSCategory>().map(JSONObject: val)
                self.pageModel.setPageTotal((self.cmsCategory?.count)!)
            case .failure(let err):
                DDLogError(err.localizedDescription)
            }
            DispatchQueue.main.async {
                self.tableview.reloadData()
                if self.tableview.mj_header.isRefreshing(){
                    self.tableview.mj_header.endRefreshing()
                }
            }
            
        }
        
    }
    
    func loadNextPageData(){
        let url = AppDelegate.o2Collect.generateURLWithAppContextKey(CMSContext.cmsContextKey, query: CMSContext.cmsCategoryDetailQuery, parameter: self.pageModel.toDictionary() as [String : AnyObject]?)
        var params:[String:Array<String>] = [:]
        params["categoryIdList"] = [currentCategoryId]
        Alamofire.request(url!, method: .put, parameters: params, encoding: JSONEncoding.default, headers: nil).responseJSON { (response) in
            switch response.result {
            case .success(let val):
                DDLogDebug(JSON(val).description)
                let cmsObjs = Mapper<CMSCategory>().map(JSONObject: val)
                self.cmsCategory?.data?.append(contentsOf: (cmsObjs?.data)!)
            case .failure(let err):
                DDLogError(err.localizedDescription)
            }
            DispatchQueue.main.async {
                self.tableview.reloadData()
                if self.tableview.mj_footer.isRefreshing(){
                    self.tableview.mj_footer.endRefreshing()
                }
            }
        }
    }
    
    //获取当前用户能新建文档的分类
    private func loadCanPublishCategories()  {
        if let appId = self.cmsData?.wrapOutCategoryList?[0].appId {
            let url = AppDelegate.o2Collect.generateURLWithAppContextKey(CMSContext.cmsContextKey, query: CMSContext.cmsCanPublishCategoryQuery, parameter: ["##appId##": appId as AnyObject])
            Alamofire.request(url!, method: .get, parameters: nil, encoding: JSONEncoding.default, headers: nil).responseJSON { (response) in
                switch response.result {
                case .success(let val):
                    let app = Mapper<CMSSingleApplication>().map(JSONObject: val)
                    self.canPublishCategories = app?.data?.wrapOutCategoryList ?? []
                    self.addPublishBtn()
                case .failure(let err):
                    DDLogError(err.localizedDescription)
                }
            }
        }
    }
    //如果有权限就显示新建按钮
    private func addPublishBtn() {
        if self.canPublishCategories.count > 0 {
            self.navigationItem.rightBarButtonItem = UIBarButtonItem(title: "新建", style: .plain, target: self, action: #selector(tapPublishBtn))
        }
    }
    // 点击新建按钮显示需要发布的分类列表
    @objc private func tapPublishBtn() {
        var actions: [UIAlertAction] = []
        self.canPublishCategories.forEach { (category) in
            let item = UIAlertAction(title: "\(category.categoryName ?? "")", style: .default, handler: { (action) in
                self.selectedCategory = category
                self.checkDraftThenJump(categoryId: category.id)
            })
            actions.append(item)
        }
        self.showSheetAction(title: "分类", message: "请选择发布的分类", actions: actions)
    }
    //检查选择的分类下是否有未完成的草稿， 有草稿就直接跳转到编辑页面，没有就到新建页面
    private func checkDraftThenJump(categoryId: String?) {
        let model = CommonPageModel().toDictionary()
        let url = AppDelegate.o2Collect.generateURLWithAppContextKey(CMSContext.cmsContextKey, query: CMSContext.cmsDocumentDraftQuery, parameter: model as [String : AnyObject]?)
        var params:[String: Any] = [:]
        params["categoryIdList"] = [categoryId]
        if let distinguishedName = O2AuthSDK.shared.myInfo()?.distinguishedName {
            params["creatorList"] = [distinguishedName]
        }
        params["documentType"] = "全部"
        Alamofire.request(url!, method: .put, parameters: params, encoding: JSONEncoding.default, headers: nil).responseJSON { (response) in
            switch response.result {
            case .success(let val):
                DDLogDebug(JSON(val).description)
                let res = Mapper<CMSCategory>().map(JSONObject: val)
                if let docList = res?.data, docList.count > 0 {
                    self.performSegue(withIdentifier: "showDetailContentSegue", sender: docList[0])
                }else {
                    self.gotoNewDocController()
                }
            case .failure(let err):
                DDLogError(err.localizedDescription)
                self.gotoNewDocController()
            }
        }
    }
    
    private func gotoNewDocController() {
        self.performSegue(withIdentifier: "createDocument", sender: nil)
    }
    
}


//MARK: - extension
//
extension CMSCategoryListViewController:SegmentedControlDelegate {
    func segmentedControl(_ segmentedControl: SegmentedControl, didSelectIndex selectedIndex: Int) {
        DDLogDebug("selectedIndex = \(selectedIndex)")
        self.tabIndex = selectedIndex
        self.currentCategoryId = self.itemsKeys[selectedIndex]
        self.loadFirstData()
    }
    
}

extension CMSCategoryListViewController:UITableViewDelegate,UITableViewDataSource{
    func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        guard let _ = cmsCategory,(cmsCategory?.data?.count)! > 0  else {
            return 0
        }
        //return (application.data?.count)!
        return (self.cmsCategory?.data?.count)!
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "CMSItemTableViewCell", for: indexPath) as! CMSItemTableViewCell
        let itemData = self.cmsCategory?.data?[indexPath.row]
        cell.itemData = itemData
        return cell
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let itemData = self.cmsCategory?.data?[indexPath.row]
        self.performSegue(withIdentifier: "showDetailContentSegue", sender: itemData)
    }
}
