//
//  SCommonViewController.swift
//  O2Platform
//
//  Created by 刘振兴 on 2016/10/13.
//  Copyright © 2016年 zoneland. All rights reserved.
//

import UIKit
import WebKit

class SCommonViewController: UITableViewController {
    
    @IBOutlet weak var clearCacheButton: UIButton!
    let group = DispatchGroup()

    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    

    
    
    @IBAction func checkUpdateVersion(_ sender: UIButton) {
        PgyUpdateManager.sharedPgy().checkUpdate(withDelegete: self, selector: #selector(updateVersion(_:)))
        self.showLoading(title: "更新校验中，请稍候...")
    }
    
    @objc private func updateVersion(_ response:AnyObject?){
        print("update be callbacked")
        if let obj = response {
            self.hideLoading()
            //print(obj)
            let appURLString = obj["downloadURL"]
            if  let appURL = URL(string: appURLString as! String) {
                if UIApplication.shared.canOpenURL(appURL) {
                    if UIApplication.shared.openURL(appURL) {
                        PgyUpdateManager.sharedPgy().updateLocalBuildNumber()
                    }
                }
            }
        }else{
            self.showSuccess(title: "已经是最新版本")
        }
    }
    
    
    @IBAction func clearCacheAction(_ sender: UIButton) {
        self.showLoading(title: "正在收集...")
        let fileSize = SZKCleanCache.folderSizeAtPath()
        let msg = "检测到可以清理的缓存大小为\(fileSize)M，是否立即清除？"
        //let msgAttrib = msg.color(RGB(155, g: 155, b: 155))
        let alertController = UIAlertController(title: "", message: msg, preferredStyle: .alert)
        let okAction = UIAlertAction(title: "确定", style: .default) { (action) in
            self.clearCache()
        }
        
        let cancelAction = UIAlertAction(title: "取消", style: .cancel, handler: nil)
        alertController.addAction(okAction)
        alertController.addAction(cancelAction)
        self.hideLoading()
        self.present(alertController, animated: true, completion: nil)
        
    }
    
    private func clearCache() {
        self.group.enter()
        DispatchQueue.main.async(group: self.group, execute: DispatchWorkItem(block: {
            Shared.imageCache.removeAll({
                SZKCleanCache.cleanCache({
                    print("图片缓存清除。。。")
                    self.group.leave()
                })
            })
        }))
        self.group.enter()
        DispatchQueue.main.async(group: self.group, execute: DispatchWorkItem(block: {
            let types = WKWebsiteDataStore.allWebsiteDataTypes()
            WKWebsiteDataStore.default().removeData(ofTypes: types, modifiedSince: Date(timeIntervalSince1970: 0), completionHandler: {
                print("浏览器缓存清除")
                self.group.leave()
            })
        }))
        
        self.group.notify(queue: DispatchQueue.main) {
            self.showSuccess(title: "清理完成")
            self.notify()
        }
    }
    
    private func notify() {
    // 通知门户页面刷新 因为清除了浏览器缓存 没有cookie了 需要重新加载webview
        NotificationCenter.default.post(name: OONotification.reloadPortal.notificationName, object: nil)
    }
    

    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */

}
