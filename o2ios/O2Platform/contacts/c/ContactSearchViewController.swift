//
//  ContactSearchViewController.swift
//  O2Platform
//
//  Created by 刘振兴 on 16/7/18.
//  Copyright © 2016年 zoneland. All rights reserved.
//

import UIKit
import Alamofire
import AlamofireImage
import AlamofireObjectMapper
import SwiftyJSON
import ObjectMapper

import CocoaLumberjack

class ContactSearchViewController: UITableViewController {
    
    
    var searchContacts:[Int:[CellViewModel]] = [:]
    
    var sections:[Int:String] = [:]
    
    let searchController = UISearchController(searchResultsController: nil)
    
    
    override func viewWillAppear(_ animated: Bool) {
        //self.navigationController?.navigationBarHidden = true
        //searchController.searchBar.showsCancelButton = false
    }
    
    override func viewDidDisappear(_ animated: Bool) {
        //self.navigationController?.navigationBarHidden = false
    }

    override func viewDidLoad() {
        super.viewDidLoad()
        self.title  = "搜索"
//        if (self.navigationController != nil) {
//            self.navigationController?.navigationItem.rightBarButtonItem = UIBarButtonItem(barButtonSystemItem: .Cancel, target: self, action: #selector(closeSearch))
//        }
        
        
        searchController.delegate = self
        searchController.searchResultsUpdater = self
        searchController.searchBar.delegate = self
        searchBarInit(searchController.searchBar)
        searchController.searchBar.scopeButtonTitles = ["所有", "公司", "部门", "个人"]
        //searchController.searchBar.showsScopeBar = true
        self.definesPresentationContext = true
        searchController.dimsBackgroundDuringPresentation = false
        searchController.hidesNavigationBarDuringPresentation = false
        // Setup the Scope Bar
        tableView.tableHeaderView  = searchController.searchBar
        tableView.tableHeaderView?.sizeToFit()
        //searchController.active = true
        
        //self.navigationController?.navigationBar.addSubview(searchController.searchBar)
    }
    
    private func searchBarInit(_ searchBar:UISearchBar){
//        searchBar.setBackgroundImage(UIImage(), for: .any, barMetrics: .default)
//        searchBar.barTintColor = navbar_barTint_color
//        
        if let searchField = searchBar.value(forKey: "searchField") as? UITextField {
            searchField.placeholder = "请输入搜索关键字"
//            searchField.backgroundColor = navbar_barTint_color
//            searchField.layer.cornerRadius = 14
//            searchField.layer.borderColor = UIColor.gray.cgColor
//            searchField.layer.borderWidth  = 1
//            searchField.layer.masksToBounds = true
            
        }
        
       UIBarButtonItem.appearance(whenContainedInInstancesOf: [UISearchBar.self]).title = "取消"
    }
   
    
    
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }

    // MARK: - Table view data source

    override func numberOfSections(in tableView: UITableView) -> Int {
        return self.searchContacts.count
    }

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return (self.searchContacts[section]?.count)!
    }

    
    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "searchItemCell", for: indexPath) as! ContactItemCell
        cell.cellViewModel = self.searchContacts[(indexPath as NSIndexPath).section]![(indexPath as NSIndexPath).row]
        return cell
    }
    
    override func tableView(_ tableView: UITableView, titleForHeaderInSection section: Int) -> String? {
       return self.sections[section]
    }
    
    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let cellViewModel = self.searchContacts[(indexPath as NSIndexPath).section]![(indexPath as NSIndexPath).row]
        switch cellViewModel.dataType {
        case .company(let c):
            //self.performSegueWithIdentifier("searchCompanyDepartSegue", sender: c)
            let contactStoryboard = UIStoryboard(name: "contacts", bundle: Bundle.main)
            let destVC = contactStoryboard.instantiateViewController(withIdentifier: "companyDept") as! ContactCompanyDeptController
            destVC.superCompany = c as? Company
            self.navigationController?.pushViewController(destVC, animated: true)
        case .depart(let d):
            let contactStoryboard = UIStoryboard(name: "contacts", bundle: Bundle.main)
            let destVC = contactStoryboard.instantiateViewController(withIdentifier: "DeptPerson") as! ContactDeptPersonController
            destVC.superOrgUnit = d as? OrgUnit
            self.navigationController?.pushViewController(destVC, animated: true)
            //self.performSegueWithIdentifier("searchDepartPersonSegue", sender: d)
        case .group(let g):
            self.performSegue(withIdentifier: "searchPersonGroupSegue", sender: g)
        case .identity(let i):
            self.performSegue(withIdentifier: "searchPersonInfoSegue", sender: i)
        case .person(let p):
            self.performSegue(withIdentifier: "searchPersonDetailInfoSegue", sender: p)
        case .title:
            break
        }
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == "searchCompanyDepartSegue" {
            let destVC = segue.destination as! ContactCompanyDeptController
            destVC.superCompany = sender as? Company
        }else if segue.identifier == "searchDepartPersonSegue" {
            let destVC = segue.destination as! ContactDeptPersonController
            destVC.superOrgUnit = sender as? OrgUnit
        }else if segue.identifier == "searchPersonDetailInfoSegue" {
            let destVC = segue.destination  as! ContactPersonInfoController
            destVC.contact = sender as? PersonV2
        }
    }


    
    func loadSearchData(_ searchText:String?,scopeIndex:Int){
        //let sText = (searchText == nil ? "":searchText)
        //DDLogDebug("searchText=\(sText),scopeIndex = \(scopeIndex)")
        self.searchContacts.removeAll()
        if !(searchText?.isEmpty)! {
            let urls = getURLSFromScope(searchText!, scope: scopeIndex)
            if urls.count>1 {
                for (index,url) in urls {
                    Alamofire.request(url).responseJSON {
                        response in
                        switch response.result {
                        case .success(let val):
                            let objects = JSON(val)["data"]
                            self.searchContacts[index]?.removeAll()
                            switch index {
                            case 0:
                                let companys = Mapper<Company>().mapArray(JSONString:objects.description)
                                for comp in companys!{
                                    let vm = CellViewModel(name: comp.name,sourceObject: comp)
                                    self.searchContacts[index]?.append(vm)
                                }
                            case 1:
                                let departmets = Mapper<Department>().mapArray(JSONString:objects.description)
                                for dept in departmets!{
                                    let vm = CellViewModel(name: dept.name,sourceObject: dept)
                                    self.searchContacts[index]?.append(vm)
                                }
                            case 2:
                                let persons = Mapper<Person>().mapArray(JSONString:objects.description)
                                for person in persons!{
                                    let vm = CellViewModel(name: person.name,sourceObject: person)
                                    self.searchContacts[index]?.append(vm)
                                }
                            default:
                                DDLogError("have no match category")
                            }
                        case .failure(let err):
                            DDLogError("request err = \(err)")
                        }
                        self.tableView.reloadData()
                    }
                    
                }
            }else{
                let (index,url) = urls.first!
                Alamofire.request(url).responseJSON {
                    response in
                    switch response.result {
                    case .success(let val):
                        let objects = JSON(val)["data"]
                        self.searchContacts[0]?.removeAll()
                        switch index {
                        case 0:
                            let companys = Mapper<Company>().mapArray(JSONString:objects.description)
                            for comp in companys!{
                                let vm = CellViewModel(name: comp.name,sourceObject: comp)
                                self.searchContacts[0]?.append(vm)
                            }
                        case 1:
                            let departmets = Mapper<Department>().mapArray(JSONString:objects.description)
                            for dept in departmets!{
                                let vm = CellViewModel(name: dept.name,sourceObject: dept)
                                self.searchContacts[0]?.append(vm)
                            }
                        case 2:
                            let persons = Mapper<Person>().mapArray(JSONString:objects.description)
                            for person in persons!{
                                let vm = CellViewModel(name: person.name,sourceObject: person)
                                self.searchContacts[0]?.append(vm)
                            }
                        default:
                            DDLogError("have no match category")
                        }
                    case .failure(let err):
                        DDLogError("request err = \(err)")
                    }
                    self.tableView.reloadData()
                }
            }
        }else{
            self.searchContacts.removeAll()
            self.sections.removeAll()
            self.tableView.reloadData()
        }
        //self.tableView.reloadData()
    }
    
    func getURLSFromScope(_ searchText:String,scope:Int)->[Int:String]{
        var urls:[Int:String]=[:]
        self.searchContacts.removeAll()
        switch scope {
        case 1:
            self.searchContacts = [0:[]]
            self.sections = [0:"公司列表"]
            let url = AppDelegate.o2Collect.generateURLWithAppContextKey(ContactContext.contactsContextKey, query: ContactContext.companySearchByKeyQuery, parameter: ["##key##":searchText as AnyObject])
            urls = [0:url!]
        case 2:
            self.searchContacts = [0:[]]
             self.sections = [0:"部门列表"]
            let url = AppDelegate.o2Collect.generateURLWithAppContextKey(ContactContext.contactsContextKey, query: ContactContext.departmentSearchByKeyQuery, parameter: ["##key##":searchText as AnyObject])
            urls = [1:url!]
        case 3:
            self.searchContacts = [0:[]]
            self.sections = [0:"人员列表"]
            let url = AppDelegate.o2Collect.generateURLWithAppContextKey(ContactContext.contactsContextKey, query: ContactContext.personSearchByKeyQuery, parameter: ["##key##":searchText as AnyObject])
            urls = [2:url!]
        default:
            self.searchContacts = [0:[],1:[],2:[]]
            self.sections = [0:"公司列表",1:"部门列表",2:"人员列表"]
            let url1 = AppDelegate.o2Collect.generateURLWithAppContextKey(ContactContext.contactsContextKey, query: ContactContext.companySearchByKeyQuery, parameter: ["##key##":searchText as AnyObject])
            let url2 = AppDelegate.o2Collect.generateURLWithAppContextKey(ContactContext.contactsContextKey, query: ContactContext.departmentSearchByKeyQuery, parameter: ["##key##":searchText as AnyObject])
            let url3 = AppDelegate.o2Collect.generateURLWithAppContextKey(ContactContext.contactsContextKey, query: ContactContext.personSearchByKeyQuery, parameter: ["##key##":searchText as AnyObject])
            urls[0]=url1!
            urls[1]=url2!
            urls[2]=url3!
        }
        return urls
    }
    
    @IBAction func closeWindow(_ sender: AnyObject) {
        if let preVC = self.presentedViewController {
            preVC.dismiss(animated: true, completion: {
                
            })
        }
        self.dismiss(animated: true, completion: nil)
    }
//    func closeSearch(sender:AnyObject?){
//        self.dismissViewControllerAnimated(true, completion: nil)
//    }
    
    
    
}

extension ContactSearchViewController:UISearchControllerDelegate{
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

extension ContactSearchViewController:UISearchBarDelegate{
//    func searchBarCancelButtonClicked(searchBar: UISearchBar) {
//        self.dismissViewControllerAnimated(true, completion: nil)
//    }
    func searchBar(_ searchBar: UISearchBar, selectedScopeButtonIndexDidChange selectedScope: Int) {
        self.loadSearchData(searchBar.text, scopeIndex: selectedScope)
    }
    
    
}

extension ContactSearchViewController:UISearchResultsUpdating{
    func updateSearchResults(for searchController: UISearchController) {
        let searchBar = searchController.searchBar
        self.loadSearchData(searchBar.text, scopeIndex: searchBar.selectedScopeButtonIndex)
    }
}

