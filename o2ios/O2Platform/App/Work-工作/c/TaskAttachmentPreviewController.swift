//
//  TaskAttachmentPreviewController.swift
//  O2Platform
//
//  Created by 刘振兴 on 2017/5/9.
//  Copyright © 2017年 zoneland. All rights reserved.
//

import UIKit
import QuickLook

class TaskAttachmentPreviewController: QLPreviewController {
    
    var currentFileURLS:[NSURL] = []

    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    @objc public func qlCloseWindow() -> Void {
        self.dismissVC(completion: nil)
    }

}

extension TaskAttachmentPreviewController:QLPreviewControllerDelegate,QLPreviewControllerDataSource{
    
    func numberOfPreviewItems(in controller: QLPreviewController) -> Int {
        return self.currentFileURLS.count
    }
    
    func previewController(_ controller: QLPreviewController, previewItemAt index: Int) -> QLPreviewItem {
        return self.currentFileURLS[index]
    }
    
    func previewControllerWillDismiss(_ controller: QLPreviewController) {
//        guard #available(iOS 10,*) else{
//            self.showAttachmentList(UIButton(type: .custom))
//            return
//        }
    }
    
}


