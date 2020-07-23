//
//  ZoneSubCategoryViewController.swift
//  ZoneBarManager
//
//  Created by 刘振兴 on 2017/3/16.
//  Copyright © 2017年 zone. All rights reserved.
//

import UIKit

class ZoneSubCategoryViewController: UITableViewController {

    public static let SELEC_SUB_ITEM = NSNotification.Name("SELECT_ITEM_OBJ")

//    public var app:Application! {
//        didSet {
//            self.tableView.reloadData()
//        }
//    }

    var processList: [AppProcess] = [] {
        didSet {
            self.tableView.reloadData()
        }
    }

    override func viewDidLoad() {
        super.viewDidLoad()

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
        return self.processList.count
    }


    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "ZoneSubCategoryTableViewCell", for: indexPath) as! ZoneSubCategoryTableViewCell
        cell.itemLabel.text = self.processList[indexPath.row].name
        return cell
    }

    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let item = self.processList[indexPath.row]
        NotificationCenter.default.post(name: ZoneSubCategoryViewController.SELEC_SUB_ITEM, object: item)
    }

}
