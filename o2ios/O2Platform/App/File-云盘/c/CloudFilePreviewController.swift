//
//  CloudFilePreviewController.swift
//  O2Platform
//
//  Created by FancyLou on 2019/11/8.
//  Copyright © 2019 zoneland. All rights reserved.
//

import UIKit
import CocoaLumberjack
import QuickLook


//文件预览
class CloudFilePreviewController: QLPreviewController {

    var currentFileURLS:[NSURL] = []
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.delegate = self
        self.dataSource = self
    }
    

}

extension CloudFilePreviewController: QLPreviewControllerDelegate,QLPreviewControllerDataSource {
    func numberOfPreviewItems(in controller: QLPreviewController) -> Int {
        return self.currentFileURLS.count
    }
    
    func previewController(_ controller: QLPreviewController, previewItemAt index: Int) -> QLPreviewItem {
        return self.currentFileURLS[index]
    }
    
    
}
