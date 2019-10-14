//
//  CloudFileViewController.swift
//  O2Platform
//
//  Created by FancyLou on 2019/10/8.
//  Copyright © 2019 zoneland. All rights reserved.
//

import UIKit

class CloudFileViewController: UITableViewController {

    @IBAction func clickCloseAction(_ sender: UIBarButtonItem) {
        print("点击了关闭按钮。。。。。。。。。。。")
    }
    @IBOutlet weak var imageBtn: UIStackView!
    @IBOutlet weak var documentBtn: UIStackView!
    @IBOutlet weak var musicBtn: UIStackView!
    @IBOutlet weak var videoBtn: UIStackView!
    @IBOutlet weak var otherBtn: UIStackView!
    @IBOutlet weak var shareBtn: UIStackView!
    
    
    override func viewDidLoad() {
        super.viewDidLoad()

        // Uncomment the following line to preserve selection between presentations
        // self.clearsSelectionOnViewWillAppear = false

        // Uncomment the following line to display an Edit button in the navigation bar for this view controller.
        // self.navigationItem.rightBarButtonItem = self.editButtonItem
        self.imageBtn.addTapGesture { (tap) in
            print("图片按钮。。。。。。。")
        }
        self.documentBtn.addTapGesture { (tap) in
            print("文档按钮。。。。。。。")
        }
        self.musicBtn.addTapGesture { (tap) in
            print("音频按钮。。。。。。。")
        }
        self.videoBtn.addTapGesture { (tap) in
            print("视频按钮。。。。。。。")
        }
        self.otherBtn.addTapGesture { (tap) in
            print("其他按钮。。。。。。。")
        }
        self.shareBtn.addTapGesture { (tap) in
            print("分享按钮。。。。。。。")
        }
    }

    // MARK: - Table view data source

    override func numberOfSections(in tableView: UITableView) -> Int {
        // #warning Incomplete implementation, return the number of sections
        return 0
    }

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        // #warning Incomplete implementation, return the number of rows
        return 0
    }

    /*
    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "reuseIdentifier", for: indexPath)

        // Configure the cell...

        return cell
    }
    */

    /*
    // Override to support conditional editing of the table view.
    override func tableView(_ tableView: UITableView, canEditRowAt indexPath: IndexPath) -> Bool {
        // Return false if you do not want the specified item to be editable.
        return true
    }
    */

    /*
    // Override to support editing the table view.
    override func tableView(_ tableView: UITableView, commit editingStyle: UITableViewCell.EditingStyle, forRowAt indexPath: IndexPath) {
        if editingStyle == .delete {
            // Delete the row from the data source
            tableView.deleteRows(at: [indexPath], with: .fade)
        } else if editingStyle == .insert {
            // Create a new instance of the appropriate class, insert it into the array, and add a new row to the table view
        }    
    }
    */

    /*
    // Override to support rearranging the table view.
    override func tableView(_ tableView: UITableView, moveRowAt fromIndexPath: IndexPath, to: IndexPath) {

    }
    */

    /*
    // Override to support conditional rearranging of the table view.
    override func tableView(_ tableView: UITableView, canMoveRowAt indexPath: IndexPath) -> Bool {
        // Return false if you do not want the item to be re-orderable.
        return true
    }
    */

    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destination.
        // Pass the selected object to the new view controller.
    }
    */

}
