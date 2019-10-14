//
//  CMSQLViewController.swift
//  O2Platform
//
//  Created by 刘振兴 on 2017/3/15.
//  Copyright © 2017年 zoneland. All rights reserved.
//

import UIKit
import QuickLook

class CMSQLViewController: QLPreviewController {
    
    override func viewWillAppear(_ animated: Bool) {
        let item = UIBarButtonItem(title: "关闭", style: .plain, target: self, action: #selector(qlCloseWindow))
//        self.navigationItem.setRightBarButton(item, animated: true)
        self.navigationItem.setLeftBarButton(item, animated: true)
    }

    override func viewDidLoad() {
        super.viewDidLoad()
        print("CMSQLView")
//        self.navigationItem.backBarButtonItem = UIBarButtonItem(title: "关闭", style: .plain, target: self, action: #selector(qlCloseWindow))
        // Do any additional setup after loading the view.
    }
    
    @objc func qlCloseWindow() -> Void {
        self.dismissVC(completion: nil)
    }
    
//    override func viewWillLayoutSubviews() {
//        print("viewWillLayoutSubviews")
//        for item in (navBar?.items)! {
//            print(item)
//        }
//    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    
    
}
