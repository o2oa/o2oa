//
//  OOLinkeManViewController.swift
//  o2app
//
//  Created by 刘振兴 on 2017/11/23.
//  Copyright © 2017年 zone. All rights reserved.
//

import UIKit

class OOLinkeManViewController: UITableViewController {
    
    var currentPerson:OOPersonModel?
    
    private lazy var viewModel = {
       return OOLinkManViewModel()
    }()
    
    let presenter: Presentr = {
        let presenter = Presentr(presentationType: .alert)
        presenter.transitionType = TransitionType.coverHorizontalFromRight
        presenter.dismissOnSwipe = true
        return presenter
    }()
    
    private var OldBackColor:UIColor?
    
    override func viewWillAppear(_ animated: Bool) {
//        self.OldBackColor = self.navigationController?.navigationBar.overlay?.backgroundColor
//        self.navigationController?.navigationBar.lt_setBackgroundColor(backgroundColor: UIColor.clear)
    }
    
    override func viewWillDisappear(_ animated: Bool) {
//        self.navigationController?.navigationBar.lt_setBackgroundColor(backgroundColor: self.OldBackColor!)
    }

    override func viewDidLoad() {
        super.viewDidLoad()
        viewModel.currentPerson = currentPerson
        title = currentPerson?.name
        tableView.tableHeaderView = viewModel.tableHeaderView()
        tableView.contentInset = UIEdgeInsets(top: -64, left: 0, bottom: 0, right: 0)
        let rightSwipeGestureRecognizer = UISwipeGestureRecognizer(target: self, action: #selector(toggleRightAction))
        rightSwipeGestureRecognizer.direction = .right
        tableView.addGestureRecognizer(rightSwipeGestureRecognizer)
        tableView.reloadData()
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    @objc func toggleRightAction(){
        guard let  returnVC = self.navigationController?.popViewController(animated: true) else {
            self.dismiss(animated: true, completion: nil)
            return
        }
    }
    
    @IBAction func backPreVC(_ sender: UIBarButtonItem) {
        toggleRightAction()
    }
    
    

    // MARK: - Table view data source

    override func numberOfSections(in tableView: UITableView) -> Int {
        return viewModel.numberOfSections()
    }

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return viewModel.numberOfRowsInSection(section)
    }
    
    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "linkManInfoCell", for: indexPath) as! (OOLinkManInfoCell & Configurable)
        let item = viewModel.nodeForIndexPath(indexPath)
        cell.config(withItem: item)
        return cell
    }
    
    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let item = viewModel.nodeForIndexPath(indexPath)
        guard  let actionURL = item?.actionURL else {
            return
        }
        let openURL = URL(string:actionURL)!
        if UIApplication.shared.canOpenURL(openURL) {
//            let cancelAction = self.noticeAlertAction("取消", handler: { (myAction) in
//                print("取消")
//            })
//            let okAction = self.noticeAlertAction("确定", handler: { (okAction) in
//                if #available(iOS 10.0, *) {
//                    UIApplication.shared.open(openURL, options: [:], completionHandler: { (result) in
//                        print(result)
//                    })
//                } else {
//                    UIApplication.shared.openURL(openURL)
//                }
//            })
//            self.showInfoNotice("拨打电话","确认要电话联系吗?", [cancelAction,okAction])
        }
    }
    
    
    
}
