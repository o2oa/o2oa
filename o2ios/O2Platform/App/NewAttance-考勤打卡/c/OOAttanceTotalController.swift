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
    
    private var year: String = {
        let currentDate = Date()
        return String(currentDate.year)
    }()
    private var month: String = {
        let currentDate = Date()
        return currentDate.month > 9 ? "\(currentDate.month)" : "0\(currentDate.month)"
    }()
        
    override func viewDidLoad() {
        super.viewDidLoad()
//        title = "统计"
        NotificationCenter.default.addObserver(self, selector: #selector(showDatePicker(_:)), name: OONotification.staticsTotal.notificationName, object: nil)
        tableView.register(UINib.init(nibName: "OOAttandanceTotalItemCell", bundle: nil), forCellReuseIdentifier: "OOAttandanceTotalItemCell")
        tableView.mj_header = MJRefreshNormalHeader(refreshingBlock: {
            self.getTotalDetailList()
        })
        getTotalDetailList()
    }
    
    @objc private func showDatePicker(_ notification:Notification) {
        self.datePickerTapped("选择日期", .date, "yyyy-MM") { (result) in
            self.year = result.toString("yyyy")
            self.month = result.toString("MM")
            DispatchQueue.main.async {
                self.getTotalDetailList()
            }
        }
    }
    
    func getTotalDetailList(){
        self.showLoading()
        viewModel.getCheckinCycle(self.year, self.month).then { (cycleDetail) -> Promise<(OOAttandanceAnalyze,[OOAttandanceCheckinTotal])> in
                self.headerView.requestBean = cycleDetail
               return all(self.viewModel.getCheckinAnalyze(cycleDetail), self.viewModel.getCheckinTotal(cycleDetail))
            }.always {
                self.hideLoading()
            }.then { (result) in
                self.headerView.config(withItem: result.0)
                let list = result.1
                self.models.removeAll()
                if !list.isEmpty {
                    let newList = list.sorted { (f, s) -> Bool in
                        if let fd = f.recordDateString, let sd = s.recordDateString {
                            return fd.toDate(formatter: "yyyy-MM-dd") < sd.toDate(formatter: "yyyy-MM-dd")
                        }else {
                            return false
                        }
                    }
                    self.models.append(contentsOf: newList)
                }
                self.tableView.reloadData()
                if self.tableView.mj_header.isRefreshing() {
                    self.tableView.mj_header.endRefreshing()
                }
            }.catch { (myError) in
                let customError = myError as? OOAppError
                self.showError(title: (customError?.failureReason)!)
                
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
