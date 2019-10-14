//
//  OOMeetingAcceptController.swift
//  o2app
//
//  Created by 刘振兴 on 2018/1/22.
//  Copyright © 2018年 zone. All rights reserved.
//

import UIKit
import EmptyDataSet_Swift

private let meetingIdentifier = "OOMeetingAcceptCell"

class OOMeetingAcceptController: UIViewController,EmptyDataSetSource,EmptyDataSetDelegate {
    
    private lazy var headerView:OOMeetingConfirmHeaderView = {
        let view = Bundle.main.loadNibNamed("OOMeetingConfirmHeaderView", owner: self, options: nil)?.first as! OOMeetingConfirmHeaderView
        view.frame = CGRect(x: 0, y: 0, width: kScreenW, height: 40)
        view.delegate = self
        return view
    }()
    
    private let viewModel:OOMeetingAcceptViewModel = {
       return OOMeetingAcceptViewModel()
    }()
    
    private var uIndex: Int = 0
    
    @IBOutlet weak var tableView: UITableView!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.tabBarItem?.selectedImage = O2ThemeManager.image(for: "Icon.icon_zjhy_pro")
        tableView.register(UINib.init(nibName: "OOMeetingAcceptCell", bundle: nil), forCellReuseIdentifier: meetingIdentifier)
        headerView.autoresizingMask = .flexibleWidth
        tableView.tableHeaderView = headerView
        tableView.emptyDataSetSource = self
        tableView.emptyDataSetDelegate = self
        
        
        tableView.tableFooterView = UIView()
        
        if #available(iOS 11.0, *) {
            //tableView.contentInsetAdjustmentBehavior = .never
        }
        
        viewModel.callbackExecutor = {
            msg in
            //self.tableView.reloadEmptyDataSet()
            //self.tableView.reloadEmptyDataSet()
            self.tableView.reloadData()
        }
        viewModel.loadAcceptListByIndex(uIndex)
        
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    func title(forEmptyDataSet scrollView: UIScrollView) -> NSAttributedString? {
        let text = "没有待处理的申请"
        let titleAttributes = [NSAttributedString.Key.foregroundColor:UIColor(hex:"#CCCCCC"),NSAttributedString.Key.font:UIFont.init(name: "PingFangSC-Regular", size: 18)!]
        return  NSMutableAttributedString(string: text, attributes: titleAttributes)
    }

    
    func image(forEmptyDataSet scrollView: UIScrollView) -> UIImage? {
        return #imageLiteral(resourceName: "icon_wuyaoqing")
    }
    
    func backgroundColor(forEmptyDataSet scrollView: UIScrollView) -> UIColor? {
        return UIColor(hex:"#F5F5F5")
    }
    
    
    func emptyDataSetShouldDisplay(_ scrollView: UIScrollView) -> Bool {
        return true
    }
    
}

extension OOMeetingAcceptController:UITableViewDelegate,UITableViewDataSource {
    func numberOfSections(in tableView: UITableView) -> Int {
        return viewModel.numberOfSections()
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return viewModel.numberOfRowsInSection(section)
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        //let cell = t
        let cell = tableView.dequeueReusableCell(withIdentifier: meetingIdentifier, for: indexPath)
        let uCell = cell as! OOMeetingAcceptCell
        uCell.viewModel = viewModel
        let item = viewModel.nodeForIndexPath(indexPath)
        uCell.config(withItem: item)
        if uIndex == 1 {
            uCell.editButton.isHidden = false
        }else{
            uCell.editButton.isHidden = true
        }
        return cell
    }
}


extension OOMeetingAcceptController:OOMeetingConfirmHeaderViewDelegate {
    
    func confirmHeaderView(_ segmentedControlIndex: Int) {
        self.uIndex = segmentedControlIndex
        viewModel.loadAcceptListByIndex(uIndex)
    }
}
