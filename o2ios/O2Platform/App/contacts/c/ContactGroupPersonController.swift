//
//  ContactGroupPersonController.swift
//  O2Platform
//
//  Created by 刘振兴 on 16/7/15.
//  Copyright © 2016年 zoneland. All rights reserved.
//

import UIKit
import Alamofire
import AlamofireImage
import AlamofireObjectMapper
import SwiftyJSON
import ObjectMapper

import CocoaLumberjack

class ContactGroupPersonController: UITableViewController {

    
    var superGroup:Group?{
        didSet {
            let url = AppDelegate.o2Collect.generateURLWithAppContextKey(ContactContext.contactsContextKey, query: ContactContext.subGroupByNameQuery, parameter: ["##name##":(superGroup?.name)! as AnyObject])
            myGroupURL = url
        }
    }
    
    var myGroupURL:String?
    
    var groupContacts:[CellViewModel] = []

    override func viewDidLoad() {
        super.viewDidLoad()
        self.tableView.mj_header = MJRefreshNormalHeader(refreshingTarget: self, refreshingAction: #selector(loadMyGroupData(_:)))
        loadMyGroupData(nil)
        
    }

    // MARK: - Table view data source

    override func numberOfSections(in tableView: UITableView) -> Int {
        // #warning Incomplete implementation, return the number of sections
        return 1
    }

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        // #warning Incomplete implementation, return the number of rows
        return (groupContacts.count)
    }
    
    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "groupItemCell", for: indexPath)  as! ContactItemCell
        cell.cellViewModel = self.groupContacts[(indexPath as NSIndexPath).row]
        return cell
    }
    
    func loadMyGroupData(_ sender:AnyObject?){
        Alamofire.request(myGroupURL!).responseJSON {
            response in
            DDLogDebug(response.description)
        }
    }

    

}
