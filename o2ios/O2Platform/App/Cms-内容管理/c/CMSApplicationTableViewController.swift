//
//  CMSApplicationTableViewController.swift
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

class CMSApplicationTableViewController: UITableViewController {
    
    var cmsApplication:CMSApplication?
    
    var pageModel:SubjectPageModel = SubjectPageModel()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        tableView.mj_header = MJRefreshNormalHeader(refreshingBlock: {
            self.pageModel = SubjectPageModel()
            self.loadFirstData()
        })
        self.loadFirstData()
        
    }
    
    private func loadFirstData(){
        let url = AppDelegate.o2Collect.generateURLWithAppContextKey(CMSContext.cmsContextKey, query: CMSContext.cmsCategoryQuery, parameter: nil)
        self.cmsApplication = nil
        Alamofire.request(url!, method: .get, parameters: nil, encoding: JSONEncoding.default, headers: nil).responseJSON { (response) in
            switch response.result {
                case .success(let val):
                    self.cmsApplication = Mapper<CMSApplication>().map(JSONObject: val)
                    self.pageModel.setPageTotal((self.cmsApplication?.count!)!)
                case .failure(let err):
                    DDLogError(err.localizedDescription)
            }
            DispatchQueue.main.async {
                self.tableView.reloadData()
                if self.tableView.mj_header.isRefreshing(){
                    self.tableView.mj_header.endRefreshing()
                }
            }
           
        }
        
    }
    
    
    
    
    
    @IBAction func backToSuper(_ sender: UIBarButtonItem) {
        let backType = AppConfigSettings.shared.appBackType
        if backType == 1 {
            self.performSegue(withIdentifier: "backToMain", sender: nil)
        }else if backType == 2 {
            self.performSegue(withIdentifier: "backToApps", sender: nil)
        }
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }

    // MARK: - Table view data source

    override func numberOfSections(in tableView: UITableView) -> Int {
        // #warning Incomplete implementation, return the number of sections
        return 1
    }

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        guard let application = cmsApplication,(cmsApplication?.data?.count)! > 0  else {
            return 0
        }
        return (application.data?.count)!
    }
    
    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "CMSCategoryItemCell", for: indexPath) as! CMSCategoryItemCell
        let cmsData = self.cmsApplication?.data?[indexPath.row]
        cell.cmsData = cmsData
        return cell
    }
    
    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let cmsData = self.cmsApplication?.data?[indexPath.row]
        if cmsData?.wrapOutCategoryList != nil {
             self.performSegue(withIdentifier: "showCategorySegue", sender: cmsData)
        }else {
            self.showError(title: "该栏目为空，没有数据！")
        }
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == "showCategorySegue" {
            let cmsData = sender as? CMSData
            let destVC = segue.destination as! CMSCategoryListViewController
            destVC.title = cmsData?.appName
            destVC.cmsData = cmsData
       }
    }
}
