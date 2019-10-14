//
//  OOAttanceTotalController.swift
//  O2Platform
//
//  Created by 刘振兴 on 2018/5/14.
//  Copyright © 2018年 zoneland. All rights reserved.
//

import UIKit
import Promises


class OOAttanceTotalController: UITableViewController {
    
    private var viewModel:OOAttandanceViewModel = {
       return OOAttandanceViewModel()
    }()
    
    private lazy var headerView:OOAttandanceTotalHeaderView = {
       let view = Bundle.main.loadNibNamed("OOAttandanceTotalHeaderView", owner: self, options: nil)?.first as! OOAttandanceTotalHeaderView
        return view
    }()
    
    private var models:[OOAttandanceCheckinTotal] = []
        
    override func viewDidLoad() {
        super.viewDidLoad()
        title = "统计"
        NotificationCenter.default.addObserver(self, selector: #selector(showDatePicker(_:)), name: OONotification.staticsTotal.notificationName, object: nil)
        tableView.register(UINib.init(nibName: "OOAttandanceTotalItemCell", bundle: nil), forCellReuseIdentifier: "OOAttandanceTotalItemCell")
        navigationItem.leftBarButtonItem = UIBarButtonItem(title: "关闭", style: .plain, target: self, action: #selector(closeWindow))
        let currentDate = Date()
        let year = String(currentDate.year)
        let month = currentDate.month > 9 ? "\(currentDate.month)" : "0\(currentDate.month)"
        getTotalDetailList(year, month)
        tableView.mj_header = MJRefreshNormalHeader(refreshingBlock: {
            self.getTotalDetailList(year, month)
        })
        
    }
    
    @objc private func showDatePicker(_ notification:Notification) {
        self.datePickerTapped("选择日期", .date, "yyyy-MM") { (result) in
            let year = result.toString("yyyy")
            let month = result.toString("MM")
            DispatchQueue.main.async {
                self.getTotalDetailList(year, month)
            }
        }
    }
    
    func getTotalDetailList(_ year:String,_ month:String){
        MBProgressHUD_JChat.showMessage(message: "loading...", toView: view)
        viewModel.getCheckinCycle(year,month).then { (cycleDetail) -> Promise<(OOAttandanceAnalyze,[OOAttandanceCheckinTotal])> in
             self.headerView.requestBean = cycleDetail
               return all(self.viewModel.getCheckinAnalyze(cycleDetail), self.viewModel.getCheckinTotal(cycleDetail))
            }.then { (result) in
                self.headerView.config(withItem: result.0)
                self.models.removeAll()
                self.models.append(contentsOf: result.1)
            }.always {
                MBProgressHUD_JChat.hide(forView: self.view, animated: true)
                self.tableView.reloadData()
                if self.tableView.mj_header.isRefreshing() {
                    self.tableView.mj_header.endRefreshing()
                }
                
            }.catch { (myError) in
                let customError = myError as? OOAppError
                MBProgressHUD_JChat.show(text:(customError?.failureReason)! , view: self.view)
        }
    }
    
    @objc func closeWindow() {
        self.tabBarController?.navigationController?.dismiss(animated: true, completion: nil)
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }

    // MARK: - Table view data source

    override func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return models.count
    }

    
    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "OOAttandanceTotalItemCell", for: indexPath) as! (OOAttandanceTotalItemCell & Configurable)
        let item = models[indexPath.row]
        cell.config(withItem: item)
        return cell
    }
    
    override func tableView(_ tableView: UITableView, viewForHeaderInSection section: Int) -> UIView? {
        return headerView
    }
    
    override func tableView(_ tableView: UITableView, heightForHeaderInSection section: Int) -> CGFloat {
        return 130.0
    }
    
    override func tableView(_ tableView: UITableView, viewForFooterInSection section: Int) -> UIView? {
        return UIView()
    }
    
    deinit {
        NotificationCenter.default.removeObserver(self)
    }
}
