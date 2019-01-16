//
//  OOPersonsViewController.swift
//  O2Platform
//
//  Created by 刘振兴 on 2018/4/24.
//  Copyright © 2018年 zoneland. All rights reserved.
//

import UIKit
import RxSwift
import RxCocoa
import JMessage

private let ooPersonCellIdentifier = "OOContactPersonCell"

class OOPersonsViewController: UIViewController {
    
    @IBOutlet weak var tableView: UITableView!
    
    private let viewModel = OOPersonListViewModel()
    
    private var parameter = CommonPageModel()
    
    private var selectedPersons:[OOPersonModel] = []
    
    var isSingleSelected:Bool = true
    
    private var searchController:UISearchController!
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        navigationItem.rightBarButtonItem = UIBarButtonItem(title: "请选择", style: .plain, target: self, action: #selector(startChart(_:)))
        setRightItem()
        //===添加搜索=====
        searchController = UISearchController.init(searchResultsController: nil)
        searchController.searchResultsUpdater = self
        searchController.dimsBackgroundDuringPresentation = false
        searchController.hidesNavigationBarDuringPresentation = true
        tableView.tableHeaderView = searchController.searchBar
        tableView.register(UINib.init(nibName: "OOSelectPersonTableViewCell", bundle: nil), forCellReuseIdentifier: "OOSelectPersonTableViewCell")
        definesPresentationContext = true
        searchController.searchBar.delegate = self
        //单选发起单聊
        tableView.allowsSelection = true
        if !isSingleSelected  {
            tableView.allowsMultipleSelection = true
        }
      
        tableView.register(UINib.init(nibName: "OOContactPersonCell", bundle: nil), forCellReuseIdentifier: ooPersonCellIdentifier)
        tableView.delegate = self
        tableView.dataSource = self
        tableView.mj_header = MJRefreshNormalHeader(refreshingBlock: {
            self.parameter = CommonPageModel()
            self.viewModel.getAllPerson(false,self.parameter, callbackCompleted: { (parameter, errorMessage) in
                self.parameter = parameter
                self.reloadData()
                if self.tableView.mj_header.isRefreshing() {
                    self.tableView.mj_header.endRefreshing()
                }
            })
        })
        
        tableView.mj_footer = MJRefreshAutoNormalFooter(refreshingBlock: {
            if self.parameter.isLast() {
                self.tableView.mj_footer.endRefreshingWithNoMoreData()
            }else{
                self.viewModel.getAllPerson(true,self.parameter, callbackCompleted: { (parameter, errorMessage) in
                    self.parameter = parameter
                    self.reloadData()
                    if self.tableView.mj_footer.isRefreshing() {
                        self.tableView.mj_footer.endRefreshing()
                    }
                })
            }
        })
        
        MBProgressHUD_JChat.showMessage(message: "loading...", toView: view)
        viewModel.getAllPerson(false, parameter) { (parameter, errorMessage) in
            MBProgressHUD_JChat.hide(forView: self.view, animated: true)
            self.parameter = parameter
            self.reloadData()
        }
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    @objc func startChart(_ sender:UIButton){
        if isSingleSelected {
            let username = selectedPersons.first?.id
            MBProgressHUD_JChat.showMessage(message: "创建中...", toView: view)
            JMSGConversation.createSingleConversation(withUsername: username!) { (result, error) in
                MBProgressHUD_JChat.hide(forView: self.view, animated: true)
                if error == nil {
                    let conv = result as! JMSGConversation
                    let vc = JCChatViewController(conversation: conv)
                    NotificationCenter.default.post(name: NSNotification.Name(rawValue: kUpdateConversation), object: nil, userInfo: nil)
                    self.navigationController?.pushViewController(vc, animated: true)
                }else{
                    O2Logger.error(error.debugDescription)
                    MBProgressHUD_JChat.show(text: "创建会话失败，请重试", view: self.view)
                }
            }
        }else{
            MBProgressHUD_JChat.showMessage(message: "创建中...", toView: view)
            let userNames = selectedPersons.map { (p) -> String in
                return p.id!
            }
            JMSGGroup.createGroup(withName: nil, desc: nil, memberArray: userNames, completionHandler: { (result, error) in
                MBProgressHUD_JChat.hide(forView: self.view, animated: true)
                if error == nil {
                    for vc in (self.navigationController?.viewControllers)! {
                        if vc is JCConversationListViewController {
                            self.navigationController?.popToViewController(vc, animated: true)
                            let group = result as! JMSGGroup
                            JMSGConversation.createGroupConversation(withGroupId: group.gid, completionHandler: { (result, error) in
                                let conv = JMSGConversation.groupConversation(withGroupId: group.gid)
                                let chatVC = JCChatViewController(conversation: conv!)
                                vc.navigationController?.pushViewController(chatVC, animated: true)
                            })
                        }
                    }
                    
                } else {
                    O2Logger.error(error.debugDescription)
                    MBProgressHUD_JChat.show(text: "创建会话失败，请确保添加的群聊成员都使用过O2移动端应用", view: self.view)
                }
            })
        }
    }
    
    private func reloadData(){
        //selectedPersons.removeAll()
        tableView.reloadSections(IndexSet(integer: 1), with: .automatic)
    }
    
    private func setRightItem(){
        navigationItem.rightBarButtonItem?.isEnabled  = !selectedPersons.isEmpty
        if !selectedPersons.isEmpty {
            navigationItem.rightBarButtonItem?.title = "确定(\(selectedPersons.count))"
        }else{
            navigationItem.rightBarButtonItem?.title = "请选择"
        }
    }

}

extension OOPersonsViewController:UITableViewDataSource,UITableViewDelegate {
    func numberOfSections(in tableView: UITableView) -> Int {
        return viewModel.numberOfSections()
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return viewModel.numberOfRowsInSection(section)
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        if indexPath.section == 0 {
            let personCell = tableView.dequeueReusableCell(withIdentifier: "OOSelectPersonTableViewCell", for: indexPath) as! (OOSelectPersonTableViewCell & Configurable)
            personCell.config(withItem: selectedPersons)
            return personCell
        } else if indexPath.section == 1 {
            let cell = tableView.dequeueReusableCell(withIdentifier: ooPersonCellIdentifier, for: indexPath)
            let uCell = cell as! (OOContactPersonCell & Configurable)
            uCell.viewModel = viewModel
            let item = viewModel.nodeForIndexPath(indexPath)
            uCell.config(withItem: item)
            return cell
        }
        return UITableViewCell()
            
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let item = viewModel.nodeForIndexPath(indexPath)
        if isSingleSelected {
            //单选直接删除换新
            selectedPersons.removeAll()
            selectedPersons.append(item!)
        }else{
            if !selectedPersons.contains(item!) {
                selectedPersons.append(item!)
            }
        }
        tableView.beginUpdates()
        tableView.reloadSections(IndexSet(integer: 0) , with: .automatic)
        tableView.endUpdates()
        tableView.deselectRow(at: indexPath, animated: true)
        //searchController.isActive = false
        setRightItem()
    }
    
    
//    func tableView(_ tableView: UITableView, didDeselectRowAt indexPath: IndexPath) {
//        let cell = tableView.cellForRow(at: indexPath)
//        tableView.beginUpdates()
//        cell?.accessoryView = nil
//        tableView.endUpdates()
//        let item = viewModel.nodeForIndexPath(indexPath)
//        selectedPersons.removeFirst(item!)
//        setRightItem()
//    }
    
    func tableView(_ tableView: UITableView, titleForHeaderInSection section: Int) -> String? {
        if section == 0 {
            return "所选择的人员"
        }else if section == 1 {
            return "人员列表"
        }
        return nil
    }
    
    func tableView(_ tableView: UITableView, heightForHeaderInSection section: Int) -> CGFloat {
        if section == 0 {
            return 30.0
        }else if section == 1 {
            return 30.0
        }
        return 0.0
    }
    
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        if indexPath.section == 0 {
            return 70.0
        }else if indexPath.section == 1 {
            return 60.0
        }
        return 60.0
    }
    
    
}

extension OOPersonsViewController:UISearchResultsUpdating {
    func updateSearchResults(for searchController: UISearchController) {
        if  let searchText = searchController.searchBar.text {
            viewModel.isSearchActive = searchController.isActive
            viewModel.filterPerson(searchText) { (errorMessage) in
                if errorMessage == nil {
                    self.reloadData()
                }else{
                    self.showError(title: errorMessage ?? "")
                }
            }
        }
    }
}

extension OOPersonsViewController:UISearchBarDelegate {
    
    func searchBarTextDidBeginEditing(_ searchBar: UISearchBar) {
        viewModel.isSearchActive = !viewModel.isSearchActive
        self.reloadData()
    }
    
    func searchBarShouldBeginEditing(_ searchBar: UISearchBar) -> Bool {
        return true
    }
    
    func searchBarShouldEndEditing(_ searchBar: UISearchBar) -> Bool {
        return true
    }
    
    func searchBarTextDidEndEditing(_ searchBar: UISearchBar) {
        
    }
    
    func searchBarCancelButtonClicked(_ searchBar: UISearchBar) {
        viewModel.isSearchActive = !viewModel.isSearchActive
        self.reloadData()
    }
    
    
    func searchBar(_ searchBar: UISearchBar, textDidChange searchText: String) {
        
    }
}

