//
//  ZoneMenuViewController.swift
//  ZoneBarManager
//
//  Created by 刘振兴 on 2017/3/16.
//  Copyright © 2017年 zone. All rights reserved.
//

import UIKit
import Alamofire
import AlamofireImage
import AlamofireObjectMapper
import SwiftyJSON
import ObjectMapper

import CocoaLumberjack

class ZoneMenuViewController: UIViewController {
    
    
    private var mainVC:ZoneMainCategoryViewController!
    
    private var subVC:ZoneSubCategoryViewController!
    
    
    fileprivate var apps:[Application] = [] {
        didSet {
            self.mainVC.apps = apps
        }
    }
    
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
         notificationInit()
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        NotificationCenter.default.removeObserver(self)
    }

    override func viewDidLoad() {
        super.viewDidLoad()
        commonInit()
        loadAppList()
       
    }
    
    private func commonInit(){
        
        self.automaticallyAdjustsScrollViewInsets = false
        //mainMenu
        if let mainVC = self.storyboard?.instantiateViewController(withIdentifier: "mainMenu") {
            self.mainVC = mainVC as! ZoneMainCategoryViewController
            self.addChild(mainVC)
            mainVC.view.frame = CGRect(x: 0, y: 0, width: view.bounds.width * 0.4, height: view.bounds.height)
            self.view.addSubview(mainVC.view)
        }
        if let subVC = self.storyboard?.instantiateViewController(withIdentifier: "subMenu") {
            self.subVC = subVC as! ZoneSubCategoryViewController
            self.addChild(subVC)
            subVC.view.frame = CGRect(x: view.bounds.width * 0.4, y: 0, width: view.bounds.width * 0.6, height: view.bounds.height)
            //let tView = subVC.view as! UITableView
            //tView.contentInset = UIEdgeInsets(top: 0, left: 0, bottom: 0, right: 0)
            self.view.addSubview(subVC.view)
        }
    }
    
    private func notificationInit(){
        NotificationCenter.default.addObserver(self, selector: #selector(reveiveCategoryNotification(_:)), name: ZoneMainCategoryViewController.SELECT_MSG_NAME, object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(receiveSubNotification(_:)), name: ZoneSubCategoryViewController.SELEC_SUB_ITEM, object: nil)
    }
    
    @objc private func reveiveCategoryNotification(_ notification:NSNotification){
        let obj = notification.object
        self.subVC.app = obj as! Application
    }
    
    @objc private func receiveSubNotification(_ notification:NSNotification){
        let obj = notification.object
        self.performSegue(withIdentifier: "showStartFlowSegue", sender: obj)
    }
    
    
    func loadAppList(){
        let url = AppDelegate.o2Collect.generateURLWithAppContextKey(ApplicationContext.applicationContextKey, query: ApplicationContext.applicationListQuery, parameter: nil)
        self.showLoading(title: "应用加载中...")
        self.apps.removeAll()
        Alamofire.request(url!).responseArray(queue: nil, keyPath: "data", context: nil, completionHandler: { (response:DataResponse<[Application]>) in
            switch response.result {
            case .success(let apps):
                self.apps.append(contentsOf: apps)
                self.showSuccess(title: "加载完成")
            case .failure(let err):
                DDLogError(err.localizedDescription)
                self.showError(title: "加载失败")
            }
            
        })
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == "showStartFlowSegue" {
            let destVc = segue.destination as! TaskCreateViewController
            destVc.process = sender as? AppProcess
        }
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    

}
