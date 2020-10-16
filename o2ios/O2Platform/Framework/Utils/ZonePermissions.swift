//
//  ZonePermissions.swift
//  ZoneBarManager
//
//  Created by 刘振兴 on 2017/3/14.
//  Copyright © 2017年 zone. All rights reserved.
//

import UIKit
import AVFoundation
import Photos
import AssetsLibrary

public enum ZoneAuthorizationStatus:NSInteger {
    case zAuthorizationStatusAuthorized = 0, //正常
    zAuthorizationStatusDenied, //没有权限
    zAuthorizationStatusRestricted, //受限制
    zAuthorizationStatusNotSupport //不支持
}


public class ZonePermissions: NSObject {

    
    public class func requestImagePickerAuthorization(callback:@escaping (_ authorizationStatus:ZoneAuthorizationStatus) -> Void){
        if(UIImagePickerController.isSourceTypeAvailable(.camera) || UIImagePickerController.isSourceTypeAvailable(.photoLibrary)){
            let authStatus = PHPhotoLibrary.authorizationStatus()
            switch authStatus {
            case .notDetermined:
                PHPhotoLibrary.requestAuthorization({ (status) in
                    if(status == PHAuthorizationStatus.authorized){
                        DispatchQueue.main.async {
                            callback(ZoneAuthorizationStatus.zAuthorizationStatusAuthorized)
                        }
                        
                    }else if(status ==  PHAuthorizationStatus.denied){
                        DispatchQueue.main.async {
                            callback(ZoneAuthorizationStatus.zAuthorizationStatusDenied)
                        }
                        
                    }else if(authStatus == PHAuthorizationStatus.restricted){
                        DispatchQueue.main.async {
                            callback(ZoneAuthorizationStatus.zAuthorizationStatusRestricted)
                        }
                    }
                })
            case .authorized:
                DispatchQueue.main.async {
                    callback(ZoneAuthorizationStatus.zAuthorizationStatusAuthorized)
                    
                }
            case .denied:
                DispatchQueue.main.async {
                    callback(ZoneAuthorizationStatus.zAuthorizationStatusDenied)
                }
                
            case .restricted:
                DispatchQueue.main.async {
                    callback(ZoneAuthorizationStatus.zAuthorizationStatusRestricted)
                }
                
            case .limited:
                DispatchQueue.main.async {
                    callback(ZoneAuthorizationStatus.zAuthorizationStatusAuthorized)
                    
                }
            }
        }else {
            DispatchQueue.main.async {
                callback(ZoneAuthorizationStatus.zAuthorizationStatusNotSupport)
            }
            
        }
    }
    
    public class func requestCameraAuthorization(callback:@escaping (_ authorizationStatus:ZoneAuthorizationStatus) -> Void){
        if(UIImagePickerController.isSourceTypeAvailable(.camera)){
            let authStatus = AVCaptureDevice.authorizationStatus(for: AVMediaType.video)
            switch authStatus {
            case .notDetermined:
                AVCaptureDevice.requestAccess(for: AVMediaType.video, completionHandler: { (granted) in
                    if granted == true {
                        DispatchQueue.main.async {
                            callback(ZoneAuthorizationStatus.zAuthorizationStatusAuthorized)
                        }
                        
                    }else{
                        DispatchQueue.main.async {
                            callback(ZoneAuthorizationStatus.zAuthorizationStatusDenied)
                            
                        }
                    }
                })
            case .authorized:
                DispatchQueue.main.async {
                    callback(ZoneAuthorizationStatus.zAuthorizationStatusAuthorized)
                }
                
            case .denied:
                DispatchQueue.main.async {
                    callback(ZoneAuthorizationStatus.zAuthorizationStatusDenied)
                }
                
            case .restricted:
                DispatchQueue.main.async {
                    callback(ZoneAuthorizationStatus.zAuthorizationStatusRestricted)
                }
                
            }
        }else{
            DispatchQueue.main.async {
                callback(ZoneAuthorizationStatus.zAuthorizationStatusNotSupport)
            }
            
        }
    }
    
    public class func requestAddressBookAuthorization(callback:(_ authorizationStatus:ZoneAuthorizationStatus) -> Void){
        
    }
    
}
