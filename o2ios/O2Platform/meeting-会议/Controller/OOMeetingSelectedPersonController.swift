//
//  OOMeetingSelectedPersonController.swift
//  o2app
//
//  Created by 刘振兴 on 2018/2/2.
//  Copyright © 2018年 zone. All rights reserved.
//

import UIKit

private let personTableCell = "OOMeetingPersonTableViewCell"

class OOMeetingSelectedPersonController: UITableViewController {
    
    var viewModel:OOMeetingCreateViewModel?
    
    //当前模式 0--正常 1--单选择 2--多选
    var currentMode:Int = 0 {
        didSet {
            
        }
    }
    
    var delegate:OOCommonBackResultDelegate?
    
    private var selectedPersons:[OOPersonModel] = []
    
    private var selectedCellIndexPaths:[IndexPath] = []
    
    var searchController:OOUISearchController!
    

    override func viewDidLoad() {
        super.viewDidLoad()
        
        //增加searchbar
//        searchController = OOUISearchController(searchResultsController: self)
//        searchController.searchResultsUpdater  = self
//        tableView.tableHeaderView = searchController.searchBar
        
        tableView.register(UINib.init(nibName: "OOMeetingPersonTableViewCell", bundle: nil), forCellReuseIdentifier: personTableCell)
        if currentMode > 0  {
            self.navigationItem.rightBarButtonItem = UIBarButtonItem(title: "确定", style: .plain, target: self, action: #selector(selectSubmit(_:)))
            if currentMode == 1{
                tableView.allowsSelection = true
            }else if currentMode == 2 {
                tableView.allowsMultipleSelection = true
            }
        }
        tableView.mj_header = MJRefreshNormalHeader(refreshingBlock: {
            self.viewModel?.getAllPerson(nil)
        })
        tableView.mj_footer = MJRefreshAutoNormalFooter(refreshingBlock: {
            if let person = self.viewModel?.getLastPerson() {
                self.viewModel?.getAllPerson(person.id)
            }else{
                self.tableView.mj_header.endRefreshing()
            }
        })
        viewModel?.contactCallBlock  = {
            msg in
            DispatchQueue.main.async {
                self.tableView.reloadData()
                if self.tableView.mj_header.isRefreshing(){
                    self.tableView.mj_header.endRefreshing()
                }
                if self.tableView.mj_footer.isRefreshing() {
                    self.tableView.mj_footer.endRefreshing()
                }
            }
            
        }
        viewModel?.getAllPerson(nil)
        
    }
    
    @objc func selectSubmit(_ sender:Any) {
        guard let block = delegate else {
            return
        }
        block.backResult("showPersonSelectedSegue", selectedPersons)
        self.dismiss(animated: true, completion: nil)
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }

    // MARK: - Table view data source

    override func numberOfSections(in tableView: UITableView) -> Int {
        // #warning Incomplete implementation, return the number of sections
        return (viewModel?.tableViewNumberOfSections())!
    }

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return (viewModel?.tableViewNumberOfRowsInSection(section))!
    }
    
    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: personTableCell, for: indexPath)
        let item = viewModel?.tableViewNodeForIndexPath(indexPath)
        let uCell = cell as! (OOMeetingPersonTableViewCell & Configurable)
        uCell.viewModel = self.viewModel
        uCell.config(withItem: item)
        return cell
    }
    
    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        if currentMode > 0 {
            let item = viewModel?.tableViewNodeForIndexPath(indexPath)
            self.selectedCellIndexPaths.append(indexPath)
            self.selectedPersons.append(item!)
        }
    }
    
    override func tableView(_ tableView: UITableView, didDeselectRowAt indexPath: IndexPath) {
        if currentMode > 0 {
            let item = viewModel?.tableViewNodeForIndexPath(indexPath)
            self.selectedCellIndexPaths.remove(at: self.selectedCellIndexPaths.index(of: indexPath)!)
            self.selectedPersons.remove(at: self.selectedPersons.index(of: item!)!)
        }
    }
    
    
    
}

extension OOMeetingSelectedPersonController:UISearchResultsUpdating {
    func updateSearchResults(for searchController: UISearchController) {
        
    }
}
