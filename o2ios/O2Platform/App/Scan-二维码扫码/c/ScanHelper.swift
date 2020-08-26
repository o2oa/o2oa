//
//  ScanHelper.swift
//  O2Platform
//
//  Created by FancyLou on 2018/9/30.
//  Copyright © 2018 zoneland. All rights reserved.
//
import AVFoundation


class ScanHelper {
    
    
    static func openScan(vc: UIViewController, callbackResult: ((String)->Void)? = nil) {
        //新版本 仿新版微信 全屏扫码
        let scanVC = ScanQRViewController()
        scanVC.resultBlock = { result in
            if let callback = callbackResult  {
                callback(result)
            }else {
                //打开O2业务vc 目前两个功能 扫码登录O2和会议签到
                QRCodeResultViewController.openQRResult(result: result, vc: vc)
            }
        }
        scanVC.modalPresentationStyle = .fullScreen
        vc.present(scanVC, animated: true, completion: nil)
        
        //老版本
//        LBXPermissions.authorizeCameraWith { (result) in
//            if result {
//                if let scanVC = self.initScanViewController(callbackResult: callbackResult) {
//                    vc.navigationController?.pushViewController(scanVC, animated: false)
//                }else {
//                    vc.gotoApplicationSettings(alertMessage: "是否跳转到手机设置页面开启相机权限？")
//                }
//            }else {
//                vc.showError(title: "没有摄像头权限，请先开启！")
//            }
//        }
    }
    
    /// 生成扫码的ViewController 如果没有权限就返回nil
    static private func initScanViewController(callbackResult: ((String)->Void)? = nil) -> NewScanViewController? {
        let scanVC = NewScanViewController()
        var scanStyle = LBXScanViewStyle()
        scanStyle.centerUpOffset = 44
        scanStyle.photoframeAngleStyle = LBXScanViewPhotoframeAngleStyle.Inner
        scanStyle.photoframeLineW = 2
        scanStyle.photoframeAngleW = 18
        scanStyle.photoframeAngleH = 18
        scanStyle.isNeedShowRetangle = false
        scanStyle.anmiationStyle = LBXScanViewAnimationStyle.LineMove
        //scanStyle.colorAngle = UIColor(red: 0.0/255, green: 200.0/255.0, blue: 20.0/255.0, alpha: 1.0)
        scanStyle.colorAngle = O2ThemeManager.color(for: "Base.base_color") ?? UIColor.init(hex: "#fb47474")
        scanStyle.animationImage = UIImage(named: "qrcode_scan_part_net.png")
        scanVC.scanStyle = scanStyle
        scanVC.callbackResult = callbackResult
        return scanVC
    }
    
}
