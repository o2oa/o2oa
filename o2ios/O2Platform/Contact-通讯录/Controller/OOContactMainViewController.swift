//
//  ContactMainViewController.swift
//  o2app
//
//  Created by 刘振兴 on 2017/11/20.
//  Copyright © 2017年 zone. All rights reserved.
//

import UIKit
import EmptyDataSet_Swift

class OOContactMainViewController: UITableViewController {
    
   private lazy var viewModel = {
        return OOContactViewModel()
    }()
    
    var searchController:OOUISearchController!
    
    private lazy var searchResultController = { () -> UIViewController? in
        let searchVC = self.storyboard?.instantiateViewController(withIdentifier: "OOContactSearchController")
        return searchVC
    }()

    override func viewDidLoad() {
        super.viewDidLoad()
        searchController = OOUISearchController(searchResultsController: searchResultController)
        //searchController.dimsBackgroundDuringPresentation = false
        searchController.searchResultsUpdater = searchResultController as! UISearchResultsUpdating
        self.tableView.emptyDataSetSource = self
        self.tableView.emptyDataSetDelegate = self
        self.tableView.tableHeaderView = searchController.searchBar
        viewModel.updateBlock = { msg in
            self.tableView.reloadData()
        }
//        self.tableView.mj_header.refreshingBlock = {
//            self.viewModel.refreshData()
//        }
        viewModel.refreshData()
    }
    


    // MARK: - Table view data source

    override func numberOfSections(in tableView: UITableView) -> Int {
        return viewModel.numberOfSections()
    }

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return viewModel.numberOfRowsInSection(section)
    }

    
    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let type = OOContactGroupHeaderType(rawValue: indexPath.section)!
        var cell:UITableViewCell!
        switch type {
        case .department,.company,.group:
            cell = tableView.dequeueReusableCell(withIdentifier: "CDLCell", for: indexPath)
            var item:DataModel?
            item = viewModel.nodeForIndexPath(indexPath)
            let uCell = cell as! Configurable
            uCell.config(withItem: item ?? nil)
            break
            
        case .linkman:
            cell = tableView.dequeueReusableCell(withIdentifier: "LinkManCell", for: indexPath) as! OOLinkManCell
            var item:DataModel?
            item = viewModel.nodeForIndexPath(indexPath)
            let pCell = cell as! Configurable
            pCell.config(withItem: item ?? nil)
            break
        }
        return cell
    }
    
    override func tableView(_ tableView: UITableView, viewForHeaderInSection section: Int) -> UIView? {
        let headerView = Bundle.main.loadNibNamed("OOContactGroupHeaderView", owner: self, options: nil)![0] as! OOContactGroupHeaderView
        let type = viewModel.headerTypeOfSection(section)
        headerView.setHeaderType(type)
        return headerView
    }
    
    override func tableView(_ tableView: UITableView, heightForHeaderInSection section: Int) -> CGFloat {
        return viewModel.headerHeightOfSection(section)
    }
    
    override func tableView(_ tableView: UITableView, heightForFooterInSection section: Int) -> CGFloat {
        return viewModel.footerHeightOfSection(section)
    }
    
    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        if indexPath.section == 0  || indexPath.section == 1 {
            var model:Any?
            model = viewModel.nodeForIndexPath(indexPath)
            self.performSegue(withIdentifier: "listUnitSegue", sender: model)
        }
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == "listUnitSegue" {
            let destVC = segue.destination as! OOListUnitViewController
            let model = sender as! OOUnitModel
            destVC.unit = model
        }
    }
    


}

// MARK: - DZN DataSource
extension OOContactMainViewController:EmptyDataSetSource,EmptyDataSetDelegate {
    
    func image(forEmptyDataSet scrollView: UIScrollView!) -> UIImage? {
        return #imageLiteral(resourceName: "pic_o2_moren1")
    }

    func title(forEmptyDataSet scrollView: UIScrollView!) -> NSAttributedString? {
        let text = "没有组织及人员"
        let titleAttributes = [NSAttributedString.Key.foregroundColor:UIColor.lightText,NSAttributedString.Key.font:UIFont.init(name: "PingFangSC-Regular", size: 18)!]
        return  NSMutableAttributedString(string: text, attributes: titleAttributes)
    }
    
    func backgroundColor(forEmptyDataSet scrollView: UIScrollView!) -> UIColor? {
        return UIColor(hex: "#F5F5F5")
    }
}
