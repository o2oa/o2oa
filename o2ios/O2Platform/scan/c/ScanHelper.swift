//
//  ScanHelper.swift
//  O2Platform
//
//  Created by FancyLou on 2018/9/30.
//  Copyright © 2018 zoneland. All rights reserved.
//
import AVFoundation
import swiftScan
import ProgressHUDSwift

class ScanHelper {
    
    
    /// 生成扫码的ViewController 如果没有权限就返回nil
    static func initScanViewController() -> NewScanViewController? {
        // 权限判断
        let status = AVCaptureDevice.authorizationStatus(for: .video)
        if status == .denied || status == .restricted {
            ProgressSHD.showError("没有摄像头权限，请先开启！")
            return nil
        }else {
            let scanVC = NewScanViewController()
            var scanStyle = LBXScanViewStyle()
            scanStyle.centerUpOffset = 44;
            scanStyle.photoframeAngleStyle = LBXScanViewPhotoframeAngleStyle.Inner;
            scanStyle.photoframeLineW = 2;
            scanStyle.photoframeAngleW = 18;
            scanStyle.photoframeAngleH = 18;
            scanStyle.isNeedShowRetangle = false;
            scanStyle.anmiationStyle = LBXScanViewAnimationStyle.LineMove;
            //scanStyle.colorAngle = UIColor(red: 0.0/255, green: 200.0/255.0, blue: 20.0/255.0, alpha: 1.0)
            scanStyle.colorAngle = base_color
            scanStyle.animationImage = UIImage(named: "qrcode_scan_part_net.png")
            scanVC.scanStyle = scanStyle
        
            return scanVC
        }
    }
}
