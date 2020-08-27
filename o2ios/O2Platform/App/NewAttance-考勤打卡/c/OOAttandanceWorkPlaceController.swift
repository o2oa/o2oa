//
//  OOAttandanceWorkPlaceController.swift
//  O2Platform
//
//  Created by 刘振兴 on 2018/5/21.
//  Copyright © 2018年 zoneland. All rights reserved.
//

import UIKit

class OOAttandanceWorkPlaceController: UITableViewController {
    
    private lazy var viewModel:OOAttandanceViewModel = {
       return OOAttandanceViewModel()
    }()
    
    private var models:[OOAttandanceWorkPlace] = []
        
    override func viewDidLoad() {
        super.viewDidLoad()
        title = "地点管理"
        tableView.register(UINib.init(nibName: "OOAttandanceWorkPlaceCell", bundle: nil), forCellReuseIdentifier: "OOAttandanceWorkPlaceCell")
        tableView.mj_header = MJRefreshNormalHeader(refreshingBlock: {
            self.loadWorkPlace()
        })
        loadWorkPlace()
    }
    
    private func loadWorkPlace() {
        self.showLoading()
        models.removeAll()
        viewModel.getLocationWorkPlace { (result) in
            
            switch result {
            case .ok(let item):
                let items = item as? [OOAttandanceWorkPlace]
                DispatchQueue.main.async {
                    self.hideLoading()
                    items?.forEach({ (place) in
                        self.models.append(place)
                    })
                }
                break
            case .fail(let errorMessage):
                DispatchQueue.main.async {
                    self.showError(title: "读取位置列表出错\n,\(errorMessage)" )
                }
                break
            default:
                break
            }
            DispatchQueue.main.async {
                if self.tableView.mj_header.isRefreshing() {
                    self.tableView.mj_header.endRefreshing()
                }
                self.tableView.reloadData()
            }
        }
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }

    // MARK: - Table view data source

    override func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return models.count
    }

    
    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "OOAttandanceWorkPlaceCell", for: indexPath) as! (OOAttandanceWorkPlaceCell & Configurable)
        let item = models[indexPath.row]
        cell.config(withItem: item)
        return cell
    }
    
    override func tableView(_ tableView: UITableView, canEditRowAt indexPath: IndexPath) -> Bool {
        return true
    }
    
    override func tableView(_ tableView: UITableView, editActionsForRowAt indexPath: IndexPath) -> [UITableViewRowAction]? {
        let action1 = UITableViewRowAction(style: .destructive, title: "删除") { (action, indexPath) in
            self._delete(indexPath)
        }
        return [action1]
    }
    
    private func _delete(_ indexPath:IndexPath){
        let item = models[indexPath.row]
        self.showLoading(title: "删除中，请稍候...")
        viewModel.deleteLocationWorkPlace(item) { (resultType) in
             self.hideLoading()
            switch resultType {
            case .reload:
                DispatchQueue.main.async {
                    
                    self.models.remove(at: indexPath.row)
                    self.tableView.reloadData()
                }
                break
            case .fail(let errorMessage):
                DispatchQueue.main.async {
                    self.showError(title: "删除不成功\n\(errorMessage)" )
                    self.tableView.reloadData()
                }
            default:
                break
            }
        }
    }
    
    
}
