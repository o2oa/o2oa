//
//  OOMeetingRoomDetailViewController.swift
//  o2app
//
//  Created by 刘振兴 on 2018/1/22.
//  Copyright © 2018年 zone. All rights reserved.
//

import UIKit

private let roomIdentifier = "OOMeetingRoomMainCell"
private let meetingIdentifier = "OOMeetingInforItemCell"

class OOMeetingRoomDetailViewController: UIViewController {
    
    var ooMeetingRoomInfo:OOMeetingRoomInfo!{
        didSet {
            viewModel.ooMeetingRoomInfo = ooMeetingRoomInfo
        }
    }
    
    @IBOutlet weak var tableView: UITableView!
    
    private lazy var viewModel:OOMeetingRoomDetailViewModel = {
        return OOMeetingRoomDetailViewModel()
    }()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.title = viewModel.ooMeetingRoomInfo?.name
        tableView.register(UINib.init(nibName: "OOMeetingRoomMainCell", bundle: nil), forCellReuseIdentifier: roomIdentifier)
        tableView.register(UINib.init(nibName: "OOMeetingInforItemCell", bundle: nil), forCellReuseIdentifier: meetingIdentifier)
        tableView.delegate = self
        tableView.dataSource = self
        viewModel.callbackExecutor = {
            msg in
            self.tableView.reloadData()
        }
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
}

extension OOMeetingRoomDetailViewController:UITableViewDataSource,UITableViewDelegate {
    
    func numberOfSections(in tableView: UITableView) -> Int {
        return viewModel.numberOfSections()
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return viewModel.numberOfRowsInSection(section)
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        if indexPath.section == 0 {
            let cell = tableView.dequeueReusableCell(withIdentifier: roomIdentifier, for: indexPath)
            let uCell = cell as! OOMeetingRoomMainCell
            let item = viewModel.nodeForIndexPath(indexPath)
            uCell.config(withItem: item)
            return cell
        }else if(indexPath.section == 1){
            let cell = tableView.dequeueReusableCell(withIdentifier: meetingIdentifier, for: indexPath)
            let uCell = cell as! OOMeetingInforItemCell
            uCell.meetingroomLabel.text = ooMeetingRoomInfo.name
            let item = viewModel.nodeForIndexPath(indexPath)
            uCell.config(withItem: item)
            return cell
        }else{
            return UITableViewCell()
        }
    }
}
