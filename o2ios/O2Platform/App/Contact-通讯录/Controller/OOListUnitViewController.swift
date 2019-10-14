//
//  OOListUnitViewController.swift
//  o2app
//
//  Created by 刘振兴 on 2017/11/21.
//  Copyright © 2017年 zone. All rights reserved.
//

import UIKit
import EmptyDataSet_Swift

class OOListUnitViewController: UITableViewController {
    
    open var isShowSearchControl = true
    
    open var unit:OOUnitModel?{
        didSet {
            self.currentUnit = unit
            stackOfUnit.push(unit!)
        }
    }
    
    private var currentUnit:OOUnitModel?
    
    private var stackOfUnit = Stack<OOUnitModel>()
    
    private lazy var viewModel = {
        return OOListUnitViewModel()
    }()
    
    var searchController:OOUISearchController!
    
    private lazy var searchResultController = { () -> UIViewController? in
        let searchVC = self.storyboard?.instantiateViewController(withIdentifier: "OOContactSearchController")
        return searchVC
    }()

    override func viewDidLoad() {
        super.viewDidLoad()
        if isShowSearchControl {
            searchController = OOUISearchController(searchResultsController: searchResultController)
            //searchController.dimsBackgroundDuringPresentation = false
            searchController.searchResultsUpdater = searchResultController as! UISearchResultsUpdating
            self.tableView.tableHeaderView = searchController.searchBar
        }
        self.navigationItem.hidesBackButton = false
        self.tableView.emptyDataSetSource = self
        self.tableView.emptyDataSetDelegate = self
        
        
        //加入手势操作
        let rightSwipeGestureRecognizer = UISwipeGestureRecognizer(target: self, action: #selector(toggleRightAction))
        rightSwipeGestureRecognizer.direction = .right
        tableView.addGestureRecognizer(rightSwipeGestureRecognizer)
        let leftSwipeGestureRecognizer = UISwipeGestureRecognizer(target: self, action: #selector(toggleLeftAction))
        leftSwipeGestureRecognizer.direction = .left
        tableView.addGestureRecognizer(leftSwipeGestureRecognizer)
        
        
        viewModel.updateBlock = { msg in
            self.tableView.reloadData()
        }
        title = unit?.name
        refreshData()
    }
    
    private func refreshData(){
        title = self.currentUnit?.shortName
        viewModel.refreshData(self.currentUnit?.id ?? "")
    }
    
    @objc func toggleRightAction(){
        print("rightSwipe")
        let _ = stackOfUnit.pop()
        if stackOfUnit.isEmpty {
            guard let  returnVC = self.navigationController?.popViewController(animated: true) else {
                self.dismiss(animated: true, completion: nil)
                return
            }
                
            
        }else{
            let preUnit = stackOfUnit.pop()
            self.unit = preUnit
            refreshData()
        }
//        if let preUnit = stackOfUnit.pop() {
//            if stackOfUnit.isEmpty {
//
//            }else{
//                self.currentUnit = preUnit
//                refreshData()
//            }
//        }
    }
    
    @objc func toggleLeftAction(){
        print("leftSwipe")
    }
    @IBAction func backPreVC(_ sender: UIBarButtonItem) {
       toggleRightAction()
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }

    // MARK: - Table view data source

    override func numberOfSections(in tableView: UITableView) -> Int {
        return viewModel.numberOfSections()
    }

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return viewModel.numberOfRowsInSection(section)
    }
    

    
    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "CDLCell", for: indexPath)
        let item = viewModel.nodeForIndexPath(indexPath)
        let uCell = cell as! (Configurable & OOCDLCell)
        uCell.viewModel = viewModel
        uCell.config(withItem: item)
        return cell
    }
    
    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let item = viewModel.nodeForIndexPath(indexPath)
        let m = item as! NSObject
        if m.isKind(of: OOUnitModel.self) {
            self.unit = m as? OOUnitModel
            self.refreshData()
        }else if m.isKind(of: OOPersonModel.self) {
            let p = m as! OOPersonModel
            self.performSegue(withIdentifier: "showPersonSegue", sender: p)
        }
    }
    
    override func tableView(_ tableView: UITableView, heightForHeaderInSection section: Int) -> CGFloat {
        return viewModel.headerHeightOfSection(section)
    }
    
    override func tableView(_ tableView: UITableView, viewForHeaderInSection section: Int) -> UIView? {
        let headerView = Bundle.main.loadNibNamed("OOContactUnitHeader", owner: self, options: nil)![0] as! OOContactUnitHeader
        headerView.setNavBar((currentUnit?.level)!,currentUnit?.levelName)
        return headerView
    }
    
   override  func tableView(_ tableView: UITableView, canEditRowAt indexPath: IndexPath) -> Bool {
        return false
    }
    
    override func tableView(_ tableView: UITableView, editingStyleForRowAt indexPath: IndexPath) -> UITableViewCell.EditingStyle {
        return .none
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == "showPersonSegue" {
            let destVC = segue.destination  as! OOLinkeManViewController
            destVC.currentPerson = sender as? OOPersonModel
        }
    }

}
// MARK: - DZN DataSource
extension OOListUnitViewController:EmptyDataSetSource,EmptyDataSetDelegate {
    func image(forEmptyDataSet scrollView: UIScrollView) -> UIImage? {
        return #imageLiteral(resourceName: "icon_moren_2")
    }
    
    func title(forEmptyDataSet scrollView: UIScrollView) -> NSAttributedString? {
        let text = "没有组织和人员"
        let titleAttributes = [NSAttributedString.Key.foregroundColor:UIColor.lightText,NSAttributedString.Key.font:UIFont.init(name: "PingFangSC-Regular", size: 18)!]
        return  NSMutableAttributedString(string: text, attributes: titleAttributes)
    }
    
    func backgroundColor(forEmptyDataSet scrollView: UIScrollView) -> UIColor? {
        return UIColor(hex:"#999999")
        
    }
}
