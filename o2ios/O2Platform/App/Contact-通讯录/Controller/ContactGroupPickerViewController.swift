//
//  ContactGroupPickerViewController.swift
//  O2Platform
//
//  Created by FancyLou on 2019/8/12.
//  Copyright © 2019 zoneland. All rights reserved.
//

import UIKit
import CocoaLumberjack

class ContactGroupPickerViewController: UITableViewController {

    
    private var groupDataList:[OOGroupModel] = []
    private let viewModel: ContactPickerViewModel = {
        return ContactPickerViewModel()
    }()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        DDLogDebug("viewdidload ............group")
        //分页刷新功能
        self.tableView.mj_header = MJRefreshNormalHeader(refreshingBlock: {
            self.loadFirstPageData()
        })
        
        self.tableView.mj_footer = MJRefreshAutoFooter(refreshingBlock: {
            self.loadNextPageData()
        })
        self.loadFirstPageData()
    }
    
    // MARK: - Table view data source
    
    override func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }
    
    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
       
        return self.groupDataList.count
    }
    
    override func tableView(_ tableView: UITableView, heightForHeaderInSection section: Int) -> CGFloat {
        return 10
    }
    
    override func tableView(_ tableView: UITableView, heightForFooterInSection section: Int) -> CGFloat {
        return 3
    }
    
    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "groupPickerViewCell", for: indexPath) as! GroupPickerTableViewCell
        let group = self.groupDataList[indexPath.row]
        cell.loadGroupInfo(info: group, checked: self.isSelected(value: group.distinguishedName!))
        return cell
    }
    
    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let value = self.groupDataList[indexPath.row].distinguishedName!
        if self.isSelected(value: value) {
            self.removeSelected(value: value)
        }else {
            self.addSelected(group: self.groupDataList[indexPath.row])
        }
        self.tableView.reloadRows(at: [indexPath], with: .automatic)
        
        self.tableView.deselectRow(at: indexPath, animated: false)
    }
    
    

    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destination.
        // Pass the selected object to the new view controller.
    }
    */
    
    //MARK: - private method
    
    private func loadFirstPageData() {
         DDLogDebug("loadFirstPageData ............group")
        self.showLoading()
        viewModel.loadGroupList(lastId: "(0)").then { (list)  in
            self.groupDataList.removeAll()
            self.groupDataList = list
            self.tableView.reloadData()
            self.hideLoading()
            if self.tableView.mj_header.isRefreshing(){
                self.tableView.mj_header.endRefreshing()
            }
            DDLogDebug("loadFirstPageData ............finish")
        }.catch { (error) in
                DDLogError(error.localizedDescription)
            self.hideLoading()
            if self.tableView.mj_header.isRefreshing(){
                self.tableView.mj_header.endRefreshing()
            }
        }
    }
    private func loadNextPageData() {
        DDLogDebug("loadNextPageData ............group")
        if let last = self.groupDataList.last?.id {
            viewModel.loadGroupList(lastId: last).then { (list)  in
                list.forEach({ (model) in
                    self.groupDataList.append(model)
                })
                self.tableView.reloadData()
                self.hideLoading()
                if self.tableView.mj_footer.isRefreshing(){
                    self.tableView.mj_footer.endRefreshing()
                }
                }.catch { (error) in
                    DDLogError(error.localizedDescription)
                    self.hideLoading()
                    if self.tableView.mj_footer.isRefreshing(){
                        self.tableView.mj_footer.endRefreshing()
                    }
            }
        }else {
            self.loadFirstPageData()
        }
    }
    
    private func isSelected(value: String) -> Bool {
        if let vc = self.parent as? ContactPickerViewController {
            return vc.isSelectedValue(type: .group, value: value)
        }
        return false
    }
    
    private func removeSelected(value: String) {
        if let vc = self.parent as? ContactPickerViewController {
            vc.removeSelectedValue(type: .group, value: value)
        }
    }
    
    private func addSelected(group: OOGroupModel) {
        if let vc = self.parent as? ContactPickerViewController {
            vc.addSelectedGroup(group: group)
        }
    }

}
