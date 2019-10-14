//
//  OOContactSearchResultController.swift
//  o2app
//
//  Created by 刘振兴 on 2017/11/28.
//  Copyright © 2017年 zone. All rights reserved.
//

import UIKit

class OOContactSearchResultController: UITableViewController,UISearchResultsUpdating {
    
    lazy var viewModel = {
        return OOContactSearchViewModel()
    }()

    override func viewDidLoad() {
        super.viewDidLoad()
        viewModel.updateBlock = { msg in
            self.tableView.reloadData()
            print(msg ?? "")
        }
        
        self.tableView.translatesAutoresizingMaskIntoConstraints = false
        
    }
    
    func updateSearchResults(for searchController: UISearchController) {
        let searchText = searchController.searchBar.text ?? ""
        viewModel.searchRefreshData(searchText)
    }
    
    // MARK: - Table view data source
    override func numberOfSections(in tableView: UITableView) -> Int {
        return viewModel.numberOfSectionsForSearch()
    }

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return viewModel.numberOfRowsInSectionForSearch(section)
    }

    
    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "OOContactSearchCell", for: indexPath)
        let pCell = cell as! (OOContactSearchCell & Configurable)
        let item = viewModel.nodeForIndexPathForSearch(indexPath)
        pCell.viewModel = viewModel
        pCell.config(withItem: item)
        return cell
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
    
    override func tableView(_ tableView: UITableView, viewForHeaderInSection section: Int) -> UIView? {
        let headerView = Bundle.main.loadNibNamed("OOContactSearchSectionHeaderView", owner: self, options: nil)![0] as! OOContactSearchSectionHeaderView
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
        if indexPath.section == 0  {
            var model:Any?
            model = viewModel.nodeForIndexPathForSearch(indexPath)
            self.performSegue(withIdentifier: "searchShowUnitSegue", sender: model)
        }else if indexPath.section == 1 {
            let model = viewModel.nodeForIndexPathForSearch(indexPath)
            self.performSegue(withIdentifier: "searchShowPersonSegue", sender: model)
        }
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
//        print(self.parent)
//        let searchController = self.parent as! OOUISearchController
//        searchController.isActive = false
        
        if segue.identifier == "searchShowUnitSegue" {
            let navVC = segue.destination as! ZLNavigationController
            let destVC = navVC.topViewController as! OOListUnitViewController
            destVC.isShowSearchControl = false
            let model = sender as! OOUnitModel
            destVC.unit = model
            //let navVC = OOBaseNavigationController(rootViewController: destVC)
            
        }else if segue.identifier == "searchShowPersonSegue" {
             let navVC = segue.destination as! ZLNavigationController
            let destVC = navVC.topViewController as! OOLinkeManViewController
            let model = sender as! OOPersonModel
            destVC.currentPerson = model
        }
    }
    
    


}
