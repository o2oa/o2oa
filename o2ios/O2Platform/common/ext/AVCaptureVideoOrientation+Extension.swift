//
//  AVCaptureVideoOrientation+Extension.swift
//  O2Platform
//
//  Created by FancyLou on 2018/10/16.
//  Copyright © 2018 zoneland. All rights reserved.
//

import Foundation


extension AVCaptureVideoOrientation {
    
    
    
    
    
    static func transform(ui: UIInterfaceOrientation) -> AVCaptureVideoOrientation {
        switch ui {
        case .landscapeLeft:
            return .landscapeLeft
        case .landscapeRight:
            return .landscapeRight
        case .portrait:
            return .portrait
        case .portraitUpsideDown:
            return .portraitUpsideDown
        default:
            return .portrait
        }
    }
}
