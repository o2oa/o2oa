//
//  DeviceListViewController.swift
//  O2Platform
//
//  Created by FancyLou on 2019/5/7.
//  Copyright © 2019 zoneland. All rights reserved.
//

import UIKit
import O2OA_Auth_SDK


protocol DeviceUnbindBtnClickListener {
    func onClick(device: O2BindDeviceModel)
}

class DeviceListViewController: UITableViewController {

    private let viewModel: DeviceManagerViewModel = {
       return DeviceManagerViewModel()
    }()
    
    private var deviceList: [O2BindDeviceModel] = []
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.title = "常用设备管理"
        self.loadDeviceList()
    }
    
    private func loadDeviceList() {
        viewModel.getDeviceList().then { (list) in
            self.deviceList.removeAll()
            list.forEach({ (device) in
                self.deviceList.append(device)
            })
            self.tableView.reloadData()
        }
    }

    // MARK: - Table view data source
    override func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return self.deviceList.count
    }
    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "deviceTableViewCell", for: indexPath) as! DeviceTableViewCell
        cell.deviceData = self.deviceList[indexPath.row]
        cell.unbindClickDelegate = self
        return cell
    }




}

// MARK: - 解绑点击事件
extension DeviceListViewController: DeviceUnbindBtnClickListener {
    func onClick(device: O2BindDeviceModel) {
        guard let deviceToken = device.name else {
            return
        }
        guard let token = O2AuthSDK.shared.bindDevice()?.name else {
            return
        }
        if  token != deviceToken {
            self.showDefaultConfirm(title: "提示", message: "确定要解绑 \(device.deviceType) 设备") { (action) in
                self.viewModel.unbindDevice(token: deviceToken).then({ (result) in
                    if (result) {
                        self.loadDeviceList()
                    }else {
                        self.showError(title: "解绑失败！")
                    }
                })
            }
        }
    }
    
    
}
