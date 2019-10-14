//
//  BBSSubjectListViewController.swift
//  O2Platform
//
//  Created by 刘振兴 on 2016/11/4.
//  Copyright © 2016年 zoneland. All rights reserved.
//

import UIKit
import Alamofire
import AlamofireImage
import AlamofireObjectMapper
import SwiftyJSON
import ObjectMapper
import CocoaLumberjack

class BBSSubjectListViewController: UIViewController {
    
    @IBOutlet weak var subjectTableView: ZLBaseTableView!
    
    var pageModel:SubjectPageModel = SubjectPageModel()
    
    var sectionData:BBSectionListData?
    
    var sectionUrl:String?
    
    var subjects:[BBSSubjectData] = []
    
    var window:UIWindow?
    
    var button:UIButton?
    

    override func viewDidLoad() {
        super.viewDidLoad()
        title = sectionData?.sectionName
        subjectTableView.dataSource = self
        subjectTableView.delegate = self
        subjectTableView.mj_header = MJRefreshNormalHeader(refreshingBlock: {
            self.pageModel = SubjectPageModel()
            self.loadFirstData()
        })
        
        subjectTableView.mj_footer = MJRefreshAutoFooter(refreshingBlock: {
            //先生成下一页的页号
            if self.pageModel.isLast() == false {
                self.pageModel.nextPage()
                self.loadNextPageData()
            }else{
                self.showSuccess(title: "最后一页了，没有帖子了")
            }
        })
        self.loadFirstData()
        self.createButton()
        
    }
    
    override func viewWillAppear(_ animated: Bool) {
        self.window?.isHidden = false
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        self.window?.isHidden = true
    }
    
    override var preferredStatusBarStyle : UIStatusBarStyle {
        return .lightContent
    }
    
    
    
    func createButton(){
        let width = SCREEN_WIDTH
        let height = SCREEN_HEIGHT
        self.button  = UIButton(frame: CGRect(x: 0,y: 0,width: 40,height: 40))
        self.button?.setImage(UIImage(named: "icon_bbs_publish"), for: UIControl.State())
        self.button?.addTarget(self, action: #selector(createAction), for: .touchUpInside)
        self.window = UIWindow(frame: CGRect(x: width - 60, y: height - 60, width: 40, height: 40))
        self.window?.windowLevel = UIWindow.Level.alert + 1
        self.window?.backgroundColor = UIColor.green
        self.window?.layer.cornerRadius = 20
        self.window?.layer.masksToBounds = true
        self.window?.addSubview(self.button!)
        self.window?.makeKeyAndVisible()
    }
    
    func createAction(sender:Any?){
        self.performSegue(withIdentifier: "showCreateSubjectSegue", sender: nil)
    }
    
    func loadFirstData(){
        self.subjects.removeAll()
        let url = AppDelegate.o2Collect.generateURLWithAppContextKey(BBSContext.bbsContextKey, query: BBSContext.subjectFromSectionByPageQuery, parameter: ["##pageNumber##":self.pageModel.pageNumber.toString as AnyObject,"##pageSize##":self.pageModel.pageSize.toString as AnyObject])
        Alamofire.request(url!, method: .put, parameters: ["sectionId":(sectionData?.id)!,"withTopSubject":true], encoding: JSONEncoding.default, headers: nil).responseJSON { (response) in
            debugPrint("sectionId = \(self.sectionData?.id)!")
            debugPrint(response)
            switch response.result {
            case .success(let val):
                let type = JSON(val)["type"]
                let count = JSON(val)["count"]
                //第一次设置总数
                self.pageModel.setPageTotal(count.int!)
                if type == "success" &&  count > 0 {
                    let subjectArray:[BBSSubjectData] = Mapper<BBSSubjectData>().mapArray(JSONString: JSON(val)["data"].description)!
                    self.subjects.append(contentsOf: subjectArray)
                }else{
                    DDLogError(JSON(val).description)
                }
            case .failure(let err):
                DDLogError(err.localizedDescription)
            }
            self.subjectTableView.reloadData()
            if self.subjectTableView.mj_header.isRefreshing(){
                self.subjectTableView.mj_header.endRefreshing()
            }
        }
    }
    
    func loadNextPageData(){
        let url = AppDelegate.o2Collect.generateURLWithAppContextKey(BBSContext.bbsContextKey, query: BBSContext.subjectFromSectionByPageQuery, parameter: ["##pageNumber##":self.pageModel.pageNumber.toString as AnyObject,"##pageSize##":self.pageModel.pageSize.toString as AnyObject])
        Alamofire.request(url!, method: .put, parameters: ["sectionId":(sectionData?.id)!,"withTopSubject":true], encoding: JSONEncoding.default, headers: nil).responseJSON { (response) in
            switch response.result {
            case .success(let val):
                let type = JSON(val)["type"]
                let count = JSON(val)["count"]
                if type == "success" &&  count > 0 {
                    let subjectArray:[BBSSubjectData] = Mapper<BBSSubjectData>().mapArray(JSONString: JSON(val)["data"].description)!
                    self.subjects.append(contentsOf: subjectArray)
                }else{
                    DDLogError(JSON(val).description)
                }
            case .failure(let err):
                DDLogError(err.localizedDescription)
            }
            self.subjectTableView.reloadData()
            if self.subjectTableView.mj_footer.isRefreshing(){
                self.subjectTableView.mj_footer.endRefreshing()
            }
        }

        
    }
    
    @IBAction func UnBackPublishSubjectSuccess(_ segue:UIStoryboardSegue){
        self.loadFirstData()
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == "showSubjectDetailSegue" {
            let destVC = segue.destination as! BBSSubjectDetailViewController
            destVC.subject = sender as? BBSSubjectData
        }else if segue.identifier == "showCreateSubjectSegue" {
            let destVC = segue.destination as! BBSSubjectCreateTableViewController
            destVC.sectionData = self.sectionData
        }
    }
    

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }

}

extension BBSSubjectListViewController:UITableViewDataSource {
    
    func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return subjects.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell:SubjectTableViewCell = tableView.dequeueReusableCell(withIdentifier: "SubjectTableViewCell", for: indexPath) as! SubjectTableViewCell
        let subject = self.subjects[indexPath.row]
        cell.bbsSubjectData = subject
        return cell
    }
    
}

extension BBSSubjectListViewController:UITableViewDelegate {
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let subject = subjects[indexPath.row]
        self.performSegue(withIdentifier: "showSubjectDetailSegue", sender: subject)
    }
}
