//
//  ContactPersonPickerViewController.swift
//  O2Platform
//
//  Created by FancyLou on 2019/8/12.
//  Copyright © 2019 zoneland. All rights reserved.
//

import UIKit
import CocoaLumberjack

class ContactPersonPickerViewController: UITableViewController {

    
    private var personDataList:[OOPersonModel] = []
    private let viewModel: ContactPickerViewModel = {
        return ContactPickerViewModel()
    }()
    private let searchController = UISearchController(searchResultsController: nil)
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.searchController.delegate = self
        self.searchController.searchResultsUpdater = self
        self.searchController.searchBar.delegate = self
        self.searchBarInit(searchController.searchBar)
        self.definesPresentationContext = true
        self.searchController.dimsBackgroundDuringPresentation = false
        self.searchController.hidesNavigationBarDuringPresentation = false
        // Setup the Scope Bar
        self.tableView.tableHeaderView  = searchController.searchBar
        self.tableView.tableHeaderView?.sizeToFit()
        
    }
    
    // MARK: - Table view data source
    
    override func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }
    
    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        
        return self.personDataList.count
    }
    
    override func tableView(_ tableView: UITableView, heightForHeaderInSection section: Int) -> CGFloat {
        return 10
    }
    
    override func tableView(_ tableView: UITableView, heightForFooterInSection section: Int) -> CGFloat {
        return 3
    }
    
    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "personPickerViewCell", for: indexPath) as! PersonPickerTableViewCell
        let person = self.personDataList[indexPath.row]
        cell.loadPersonInfo(info: person, checked: self.isSelected(value: person.distinguishedName!))
        return cell
    }
    
    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let value = self.personDataList[indexPath.row].distinguishedName!
        let name = self.personDataList[indexPath.row].name!
        if self.isSelected(value: value) {
            self.removeSelected(value: value)
        }else {
            self.addSelected(value: value, name: name)
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
    
    func loadSearchData(_ searchText:String?,scopeIndex:Int){
        if (searchText == nil || searchText?.isEmpty == true) {
            self.personDataList.removeAll()
            self.tableView.reloadData()
        }else{
            self.showLoading()
            viewModel.searchPersonList(searchText: searchText!).then { (list)  in
                self.personDataList.removeAll()
                self.personDataList = list
                self.tableView.reloadData()
                self.hideLoading()
                }.catch { (error) in
                    DDLogError(error.localizedDescription)
                   self.hideLoading()
                     
            }
        }
    }
    
    private func isSelected(value: String) -> Bool {
        if let vc = self.parent as? ContactPickerViewController {
            return vc.isSelectedValue(type: .person, value: value)
        }
        return false
    }
    
    private func removeSelected(value: String) {
        if let vc = self.parent as? ContactPickerViewController {
            vc.removeSelectedValue(type: .person, value: value)
        }
    }
    
    private func addSelected(value: String, name: String) {
        
    }
    
    private func searchBarInit(_ searchBar:UISearchBar){
        if let searchField = searchBar.value(forKey: "searchField") as? UITextField {
            searchField.placeholder = "请输入搜索关键字"
        }
        UIBarButtonItem.appearance(whenContainedInInstancesOf: [UISearchBar.self]).title = "取消"
    }

}



extension ContactPersonPickerViewController:UISearchControllerDelegate{
    func willPresentSearchController(_ searchController: UISearchController) {
        NSLog("willPresentSearchController")
        
    }
    
    func didPresentSearchController(_ searchController: UISearchController) {
        NSLog("didPresentSearchController")
        searchController.searchBar.setShowsCancelButton(false, animated: true)
    }
    
    func willDismissSearchController(_ searchController: UISearchController) {
        NSLog("willDismissSearchController")
    }
    
    func didDismissSearchController(_ searchController: UISearchController) {
        NSLog("didDismissSearchController")
    }
    
    func presentSearchController(_ searchController: UISearchController) {
        NSLog("presentSearchController")
    }
    
    
}

extension ContactPersonPickerViewController:UISearchBarDelegate{
    func searchBar(_ searchBar: UISearchBar, selectedScopeButtonIndexDidChange selectedScope: Int) {
        self.loadSearchData(searchBar.text, scopeIndex: selectedScope)
    }
    
    
}

extension ContactPersonPickerViewController:UISearchResultsUpdating{
    func updateSearchResults(for searchController: UISearchController) {
        let searchBar = searchController.searchBar
        self.loadSearchData(searchBar.text, scopeIndex: searchBar.selectedScopeButtonIndex)
    }
}
