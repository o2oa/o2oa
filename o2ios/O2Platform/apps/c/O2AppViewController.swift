//
//  O2AppViewController.swift
//  O2Platform
//
//  Created by 刘振兴 on 16/7/25.
//  Copyright © 2016年 zoneland. All rights reserved.
//

import UIKit
import WebKit
import SwiftyJSON
import Alamofire
import ObjectMapper
import AlamofireObjectMapper
import Flutter
import O2OA_Auth_SDK
import CocoaLumberjack


class O2AppViewController: UIViewController{
    
    @IBOutlet weak var collectionView: UICollectionView!
    
    
    private let reuseIdentifier = "myCell"
    

//    let loadUrl1 = AppDelegate.o2Collect.genrateURLWithWebContextKey2(ApplicationContext.applicationListQuery2,parameter: nil)
//
//    let loadUrl2 = AppDelegate.o2Collect.generateURLWithAppContextKey(ApplicationContext.applicationContextKey2, query: ApplicationContext.applicationListQueryForPortal, parameter: nil)
    
    fileprivate let collectionViewDelegate = ZLCollectionView()
    
    var o2apps:[O2App] = []
    var apps2:[[O2App]] = [[],[]]

    override func viewDidLoad() {
        super.viewDidLoad()
        self.title = "应用"
        navigationItem.rightBarButtonItem = UIBarButtonItem(title: "编辑", style: .plain, target: self, action: #selector(_forwardEditSegue))
        //self.collectionView.contentInset = UIEdgeInsetsMake(160, 0, 0, 0)
        self.collectionViewDelegate.delegate = self
        //self.collectionViewDelegate.cellHeight = Float(self.collectionViewDelegate.ItemWithSize) * 2
        self.collectionView.dataSource = self.collectionViewDelegate
        self.collectionView.delegate = self.collectionViewDelegate
        self.o2apps = []
        self.apps2 = []
        //self.loadAppConfigDb()
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        self.loadAppConfigDb()
    }
    
    func loadAppConfigDb() {
        let mainApps = OOAppsInfoDB.shareInstance.queryMainData()
        o2apps = mainApps
        let allApps = OOAppsInfoDB.shareInstance.queryData()
        apps2 = [mainApps,allApps]
        self.collectionViewDelegate.apps = apps2
        DispatchQueue.main.async {
            self.collectionView.reloadData()
        }
    }
    
    @objc private func _forwardEditSegue() {
        self.performSegue(withIdentifier: "showAppEditSegue", sender: nil)
    }
    

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == "showMailSegue" {
            MailViewController.app = sender as? O2App
        }
    }


    @IBAction func unBackAppsForApps(_ segue:UIStoryboardSegue){
        DDLogDebug("返回应用列表")
    }

    
    // MARK: - Flutter
    //打开flutter应用
    /**
     @param routeName flutter的路由 打开不同的页面
    **/
    func openFlutterApp(routeName: String) {
        let flutterViewController = O2FlutterViewController()
        DDLogDebug("init route:\(routeName)")
        flutterViewController.setInitialRoute(routeName)
        self.present(flutterViewController, animated: false, completion: nil)
    }
    

}

extension O2AppViewController:ZLCollectionViewDelegate{
    func clickWithApp(_ app: O2App) {
        if let flutter = app.storyBoard, flutter == "flutter" {
            openFlutterApp(routeName: app.appId!)
        }else {
            //设置返回标志，其它应用根据此返回标志调用返回unwindSegue
            AppConfigSettings.shared.appBackType = 2
            if let segueIdentifier = app.segueIdentifier,segueIdentifier != "" { // portal 门户 走这边
                if app.storyBoard! == "webview" { // 打开MailViewController
                    DDLogDebug("open webview for : "+app.title!+" url: "+app.vcName!)
                    self.performSegue(withIdentifier: segueIdentifier, sender: app)
                }else {
                    self.performSegue(withIdentifier: segueIdentifier, sender: nil)
                }
                
            } else {
                if app.storyBoard! == "webview" {
                    DDLogError("321 open webview for : "+app.title!+" url: "+app.vcName!)
                } else {
                    // 内置应用走这边  根据appkey 打开对应的storyboard
                    if app.appId == "o2ai" {
                        app.storyBoard = "ai"
                    }
                    let story = O2AppUtil.apps.first { (appInfo) -> Bool in
                        return app.appId == appInfo.appId
                    }
                    var storyBoardName = app.storyBoard
                    if story != nil {
                        storyBoardName = story?.storyBoard
                    }
                    DDLogDebug("storyboard: \(storyBoardName!) , app:\(app.appId!)")
                    let storyBoard = UIStoryboard(name: storyBoardName!, bundle: nil)
                    var destVC:UIViewController!
                    if let vcname = app.vcName,vcname.isEmpty == false {
                        destVC = storyBoard.instantiateViewController(withIdentifier: app.vcName!)
                    }else{
                        destVC = storyBoard.instantiateInitialViewController()
                    }
                    
                    if app.vcName == "todoTask" {
                        if "taskcompleted" == app.appId {
                            AppConfigSettings.shared.taskIndex = 2
                        }else if "read" == app.appId {
                           AppConfigSettings.shared.taskIndex = 1
                        }else if "readcompleted" == app.appId {
                            AppConfigSettings.shared.taskIndex = 3
                        }else {
                            AppConfigSettings.shared.taskIndex = 0
                        }
                    }
                    if destVC.isKind(of: ZLNavigationController.self) {
                        self.show(destVC, sender: nil)
                    }else{
                        self.navigationController?.pushViewController(destVC, animated: true)
                    }
                    
                }
            }
        }
    }
}

extension O2AppViewController:AppEditControllerUpdater {
    func appEditControllerUpdater() {
        self.loadAppConfigDb()
    }
}

