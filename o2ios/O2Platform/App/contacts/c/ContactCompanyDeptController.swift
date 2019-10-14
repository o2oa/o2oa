//
//  ContactCompanyDeptController.swift
//  O2Platform
//
//  Created by 刘振兴 on 16/7/14.
//  Copyright © 2016年 zoneland. All rights reserved.
//

import UIKit
import Alamofire
import AlamofireImage
import AlamofireObjectMapper
import SwiftyJSON
import ObjectMapper

import CocoaLumberjack

class ContactCompanyDeptController: UITableViewController {
    
    
    var superCompany:Company? {
        didSet{
            let url = AppDelegate.o2Collect.generateURLWithAppContextKey(ContactContext.contactsContextKey, query: ContactContext.subCompanyByNameQuery, parameter: ["##name##":(superCompany?.name)! as AnyObject])
            myCompanyURL = url
        }
    }
    
    var compContacts:[CellViewModel] = []
    
    var myCompanyURL:String?
    

    override func viewDidLoad() {
        super.viewDidLoad()
        self.title = superCompany?.name
        self.tableView.mj_header = MJRefreshNormalHeader(refreshingTarget: self, refreshingAction: #selector(loadCompData(_:)))
        self.loadCompData(nil)
        
    }


    // MARK: - Table view data source

    override func numberOfSections(in tableView: UITableView) -> Int {
        // #warning Incomplete implementation, return the number of sections
        return 1
    }

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return self.compContacts.count
    }

    
    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "compContactCell", for: indexPath)  as! ContactItemCell
        cell.cellViewModel = self.compContacts[(indexPath as NSIndexPath).row]
        return cell
    }
    
    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let viewModel = self.compContacts[(indexPath as NSIndexPath).row]
        switch viewModel.dataType {
            case .company(let c):
                self.superCompany = c as? Company
                self.loadCompData(nil)
            case .depart(let d):
                self.performSegue(withIdentifier: "sDeptPersonSegue", sender:d)
            default:
                DDLogDebug(viewModel.name!)
        }
    }
    

    
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == "sDeptPersonSegue" {
            let destVC = segue.destination as! ContactDeptPersonController
            destVC.superOrgUnit = sender as? OrgUnit
        }
    }
    
    
    @objc func loadCompData(_ obj:AnyObject?){
        self.showLoading(title: "加载中...")
        Alamofire.request(self.myCompanyURL!).responseJSON {
            response in
            //debugPrint(response)
            switch response.result {
            case .success(let val):
                self.compContacts.removeAll()
                let compnayList = JSON(val)["data"]["companyList"]
                let companys = Mapper<Company>().mapArray(JSONString:compnayList.description)
                for comp in companys!{
                    let vm = CellViewModel(name: comp.name,sourceObject: comp)
                    self.compContacts.append(vm)
                }
                let departmentList = JSON(val)["data"]["departmentList"]
                let departmets = Mapper<Department>().mapArray(JSONString:departmentList.description)
                for dept in departmets!{
                    let vm = CellViewModel(name: dept.name,sourceObject: dept)
                    self.compContacts.append(vm)
                }
                self.showSuccess(title: "加载完成")
            case .failure(let err):
                DDLogError(err.localizedDescription)
                self.showError(title: "加载失败")
            }
            if self.tableView.mj_header.isRefreshing() {
                self.tableView.mj_header.endRefreshing()
            }
            self.tableView.reloadData()
        }}

}
