//
//  OOAppEditController.swift
//  O2Platform
//
//  Created by 刘振兴 on 2018/5/10.
//  Copyright © 2018年 zoneland. All rights reserved.
//

import UIKit

protocol AppEditControllerUpdater:class {
    func appEditControllerUpdater()
}

class OOAppEditController: UITableViewController {
    
    private var mainApps:[O2App] = []
    
    private var noMainApps:[O2App] = []
    
    var delegate:AppEditControllerUpdater?

    override func viewDidLoad() {
        super.viewDidLoad()
        title = "应用管理"
        navigationItem.rightBarButtonItem = UIBarButtonItem(title: "完成", style: .plain, target: self, action: #selector(_saveCustomApps))
        tableView.isEditing = true
        loadData()
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    @objc private func _saveCustomApps() {
        mainApps.forEachEnumerated { (i, app) in
            app.mainOrder = i
            DBManager.shared.updateData(app, 1)
        }
        
        noMainApps.forEachEnumerated { (i, app) in
            app.order = i
            DBManager.shared.updateData(app, 0)
        }
        
        delegate?.appEditControllerUpdater()
        self.showMessage(msg: "更新成功")
    }
    
    func loadData() {
        mainApps =  DBManager.shared.queryMainData()
        noMainApps = DBManager.shared.queryNoMainData()
        tableView.reloadData()
    }

    // MARK: - Table view data source

    override func numberOfSections(in tableView: UITableView) -> Int {
        return 2
    }

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if section == 0 {
            return mainApps.count
        }else if section == 1 {
            return noMainApps.count
        }
        return 0
    }

    
    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "OOAppEditCell", for: indexPath) as! (OOAppEditCell & Configurable)
        let section = indexPath.section
        if section == 0 {
            let item = mainApps[indexPath.row]
            cell.config(withItem: item)
        }else if section == 1 {
            let item = noMainApps[indexPath.row]
            cell.config(withItem: item)
        }
        return cell
    }
    
    
    override func tableView(_ tableView: UITableView, titleForHeaderInSection section: Int) -> String? {
        if section == 0 {
            return "主页应用"
        }else if section == 1 {
            return "所有应用"
        }
        return nil
    }
    
    override func tableView(_ tableView: UITableView, canEditRowAt indexPath: IndexPath) -> Bool {
        return true
    }
    
    override func tableView(_ tableView: UITableView, editingStyleForRowAt indexPath: IndexPath) -> UITableViewCell.EditingStyle {
        return .none
    }

    override func tableView(_ tableView: UITableView, canMoveRowAt indexPath: IndexPath) -> Bool {
        return true
    }
    
    override func tableView(_ tableView: UITableView, moveRowAt sourceIndexPath: IndexPath, to destinationIndexPath: IndexPath) {
        //section相同
        if sourceIndexPath.section == destinationIndexPath.section {
            if sourceIndexPath.section == 0 {
                if sourceIndexPath.row != destinationIndexPath.row {
                    swap(&mainApps[sourceIndexPath.row], &mainApps[destinationIndexPath.row])
                }
            }else if sourceIndexPath.section == 1 {
                if sourceIndexPath.row != destinationIndexPath.row {
                    swap(&noMainApps[sourceIndexPath.row], &noMainApps[destinationIndexPath.row])
                }
            }
        }else{
             //section不同
            if sourceIndexPath.section == 0  && destinationIndexPath.section == 1 {
                noMainApps.insert(mainApps.remove(at: sourceIndexPath.row), at: destinationIndexPath.row)
            }else if sourceIndexPath.section == 1 && destinationIndexPath.section == 0 {
                mainApps.insert(noMainApps.remove(at: sourceIndexPath.row), at: destinationIndexPath.row)
            }
        }
       
    }

}
