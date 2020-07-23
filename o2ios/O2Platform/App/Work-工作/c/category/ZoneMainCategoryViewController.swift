//
//  ZoneMainCategoryViewController.swift
//  ZoneBarManager
//
//  Created by 刘振兴 on 2017/3/16.
//  Copyright © 2017年 zone. All rights reserved.
//

import UIKit

class ZoneMainCategoryViewController: UITableViewController {
    
    public static let SELECT_MSG_NAME = Notification.Name("CATEGORY_SELECT_OBJ")
    
    public var apps:[O2Application] = [] {
        didSet {
            self.tableView.reloadData()
            if apps.count > 0 {
                let indexPath = IndexPath(row: 0, section: 0)
                self.tableView.selectRow(at: indexPath, animated: true, scrollPosition: .top)
                let sAPP = apps[0]
                self.postMessage(sAPP)
            }
        }
    }

    override func viewDidLoad() {
        super.viewDidLoad()
        self.tableView.tableFooterView = UIView()

        // Uncomment the following line to preserve selection between presentations
        // self.clearsSelectionOnViewWillAppear = false

        // Uncomment the following line to display an Edit button in the navigation bar for this view controller.
        // self.navigationItem.rightBarButtonItem = self.editButtonItem()
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }

    // MARK: - Table view data source

    override func numberOfSections(in tableView: UITableView) -> Int {
        // #warning Incomplete implementation, return the number of sections
        return 1
    }

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        // #warning Incomplete implementation, return the number of rows
        return apps.count
    }
    
    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "ZoneMainCategoryTableViewCell", for: indexPath) as! ZoneMainCategoryTableViewCell
        let currentAPP = apps[indexPath.row]
        cell.appNameLabel.text = currentAPP.name
        if let icon = currentAPP.icon {
            let imageData = Data(base64Encoded: icon, options: .ignoreUnknownCharacters)
            cell.appIconImageView.image = UIImage(data: imageData!)
            //cell.appIconImageView.image = UIImage(data: Data(base64Encoded: icon, options:.D))
        }
        return cell
    }
    
    
    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let sAPP = self.apps[indexPath.row]
        self.postMessage(sAPP)
    }
    
    private func postMessage(_ currenApp: O2Application){
        NotificationCenter.default.post(name: ZoneMainCategoryViewController.SELECT_MSG_NAME, object: currenApp)
    }
    
    
    
    

}
