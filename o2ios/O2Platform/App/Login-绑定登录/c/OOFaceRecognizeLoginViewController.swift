//
//  OOFaceRecognizeLoginViewController.swift
//  O2Platform
//
//  Created by FancyLou on 2018/10/15.
//  Copyright © 2018 zoneland. All rights reserved.
//

import UIKit
import CoreMotion
import CocoaLumberjack
import AVFoundation
import Promises
import O2OA_Auth_SDK


class OOFaceRecognizeLoginViewController: UIViewController {
    
    var viewModel:OOLoginViewModel = {
        return OOLoginViewModel()
    }()
    
    var _detectImageQueue:DispatchQueue!
    
    var previewView:MGOpenGLView!
    
    var motionManager:CMMotionManager!
    var videoManager:MGVideoManager!
    var renderer:MGOpenGLRenderer!
    var markManager:MGFacepp!
    var hasVideoFormatDescription:Bool = false
    var remoteRecognizingUserFace: Bool = false
    
    var detectMode:MGFppDetectionMode = MGFppDetectionMode.trackingFast
    var pointsNum:Int = 81
    var detectRect:CGRect!
    var videoSize:CGSize!
    var currentFaceCount:Int = 0
    var orientation:Int = 90
    
    //MARK: - 播放音效
    var beepPlayer :AVAudioPlayer!
    

    //MARK: - system override
    deinit {
        self.previewView = nil
        self.renderer = nil
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view.
        self.createView()
        // 后台线程队列，用于解析解析人脸
        self._detectImageQueue = DispatchQueue(label: "com.megvii.image.detect")
        
        //属性初始化处理
        self.detectRect = CGRect.null
        self.videoSize = CGSize(width: 480, height: 640) //TODO 如何计算最优分辨率
        self.videoManager = MGVideoManager.videoPreset(AVCaptureSession.Preset.vga640x480.rawValue, devicePosition: AVCaptureDevice.Position.front, videoRecord: false, videoSound: false)
        self.videoManager.videoDelegate = self
        let path = Bundle.main.path(forResource: "megviifacepp_0_5_2_model", ofType: "")
        do {
            let data = try Data.init(contentsOf: URL(fileURLWithPath: path!))
            let mark = MGFacepp(model: data, maxFaceCount: 1) { (config) in
                config?.minFaceSize = 100 //最小人脸
                config?.interval = 40 //检测间隔
                config?.orientation = 90
                config?.detectionMode = MGFppDetectionMode.trackingFast
                config?.detectROI = MGDetectROI(left: 0, top: 0, right: 0, bottom: 0)
                config?.pixelFormatType = MGPixelFormatType.PixelFormatTypeRGBA
            }
            self.markManager = mark
        }catch {
            DDLogError("face++模型文件无法获取！！！")
        }
        self.pointsNum = 81
        self.detectMode = MGFppDetectionMode.trackingFast
        
        self.renderer = MGOpenGLRenderer()
        self.renderer.show3DView = false
        self.motionManager = CMMotionManager()
        self.motionManager.accelerometerUpdateInterval = 0.3
        let devicePosition = self.videoManager.devicePosition
        let motionQueue = OperationQueue()
        motionQueue.name = "com.megvii.gryo"
        self.motionManager.startAccelerometerUpdates(to: motionQueue) { (accelerometerData, error) in
            if accelerometerData != nil {
                if fabs(accelerometerData!.acceleration.z) > 0.7 {
                    self.orientation = 90
                }else {
                    if AVCaptureDevice.Position.back == devicePosition {
                        if fabs(accelerometerData!.acceleration.x) < 0.4 {
                            self.orientation = 90;
                        }else if accelerometerData!.acceleration.x > 0.4 {
                            self.orientation = 180;
                        }else if accelerometerData!.acceleration.x < -0.4 {
                            self.orientation = 0;
                        }
                    }else {
                        if fabs(accelerometerData!.acceleration.x) < 0.4 {
                            self.orientation = 90;
                        }else if accelerometerData!.acceleration.x > 0.4 {
                            self.orientation = 0;
                        }else if accelerometerData!.acceleration.x < -0.4 {
                            self.orientation = 180;
                        }
                    }
                    
                    if accelerometerData!.acceleration.y > 0.6 {
                        self.orientation = 270;
                    }
                }
            }
        }
        beepPlayerInit()
    }
    
    override func viewWillAppear(_ animated: Bool) {
        UIApplication.shared.isIdleTimerDisabled = true //屏幕不锁屏
        self.videoManager.startRunning()
        self.setUpCameraLayer()
    }
    override func viewWillDisappear(_ animated: Bool) {
        UIApplication.shared.isIdleTimerDisabled = false
        self.motionManager.stopAccelerometerUpdates()
        self.videoManager.stopRunning()
    }
    
    
    //MARK: - private
    
    
    /// 音效播放器初始化
    private func beepPlayerInit() {
        // 建立播放器
        let beepPath = Bundle.main.path(forResource: "beep", ofType: "wav")
        do {
            beepPlayer = try AVAudioPlayer(contentsOf: URL(fileURLWithPath: beepPath!))
            // 重複播放次數 設為 0 則是只播放一次 不重複
            beepPlayer.numberOfLoops = 0
        } catch {
            DDLogError("初始化audioPlayer异常，\(error)")
        }
    }
    
    /// 播放音效
    private func playBeepSound() {
        beepPlayer.play()
    }
    
    private func createView() {
        self.view.backgroundColor = UIColor.white
        self.title = "人脸识别登录"
        let left = UIBarButtonItem(image: UIImage(named: "icon_menu_window_close"), style: .done, target: self, action: #selector(close))
        self.navigationItem.leftBarButtonItem = left
    }
    
    
    /// 加载摄像头预览
    private func setUpCameraLayer() {
        if self.previewView == nil {
            self.previewView = MGOpenGLView(frame: CGRect.zero)
            self.previewView.autoresizingMask = UIView.AutoresizingMask(rawValue: UIView.AutoresizingMask.flexibleHeight.rawValue | UIView.AutoresizingMask.flexibleWidth.rawValue)
            let currentInterfaceOrientation = UIApplication.shared.statusBarOrientation
            let or = AVCaptureVideoOrientation.transform(ui: currentInterfaceOrientation)
            switch or {
            case .landscapeLeft:
                DDLogDebug("landscapeLeft.......")
                break
            case .landscapeRight:
                DDLogDebug("landscapeRight.......")
                break
            case .portrait:
                DDLogDebug("portrait.......")
                break
            case .portraitUpsideDown:
                DDLogDebug("portraitUpsideDown.......")
                break
            }
            let transform = self.videoManager.transformFromVideoBufferOrientation(to: or, withAutoMirroring: true)
            self.previewView.transform = transform
            self.view.insertSubview(self.previewView, at: 0)
            DDLogDebug("preview 已经贴上去了！！！！！！")
            var bounds = CGRect.zero
            bounds.size = self.view.convert(self.view.bounds, to: self.previewView).size
            self.previewView.bounds = bounds
            self.previewView.center = CGPoint(x: self.view.bounds.size.width/2, y: self.view.bounds.size.height/2)
        }
    }
    
    @objc private func close() {
        self.motionManager.stopAccelerometerUpdates()
        self.videoManager.stopRunning()
        self.dismiss(animated: true, completion: nil)
    }
}

//MARK: - 扩展

extension OOFaceRecognizeLoginViewController: MGVideoDelegate {
    
    func mgCaptureOutput(_ captureOutput: AVCaptureOutput!, didOutputSampleBuffer sampleBuffer: CMSampleBuffer!, from connection: AVCaptureConnection!) {
        if !self.hasVideoFormatDescription {
            self.hasVideoFormatDescription = true
            let format = self.videoManager.formatDescription()
            self.renderer.prepareForInput(with: format?.takeUnretainedValue(), outputRetainedBufferCountHint: 6)
        }
        self.rotateAndDetectSampleBuffer(sampleBuffer: sampleBuffer)
    }
    
    func mgCaptureOutput(_ captureOutput: AVCaptureOutput!, error: Error!) {
        if error != nil {
            DDLogError("摄像头数据获取异常，\(error!)")
        }
        self.showSystemAlert(title: "提示", message: "摄像头不支持！") { (action) in
            self.close()
        }
    }
    
    
    /// 旋转并且显示
    ///
    /// - Parameter sampleBuffer: 输出流
    private func rotateAndDetectSampleBuffer(sampleBuffer: CMSampleBuffer?) {
        if self.markManager.status != .markWorking && sampleBuffer != nil {
            var bufferCopy: CMSampleBuffer?
            let copy = CMSampleBufferCreateCopy(allocator: kCFAllocatorDefault, sampleBuffer: sampleBuffer!, sampleBufferOut: &bufferCopy)
            if  copy == noErr {
                let item = DispatchWorkItem {
                    if self.markManager.getConfig()?.orientation != self.orientation.toInt32 {
                        self.markManager.updateSetting({ (config) in
                            config?.orientation = self.orientation.toInt32
                        })
                    }
                    if self.detectMode == MGFppDetectionMode.trackingFast {
                        self.trackSampleBuffer(detectSampleBufferRef: bufferCopy)
                    }
                }
                self._detectImageQueue.async(execute: item)
            }else {
                DDLogError("copy 视频流出错！！！")
            }
        }
    }
    
    private func trackSampleBuffer(detectSampleBufferRef: CMSampleBuffer?) {
        guard let sample = detectSampleBufferRef else {
            return
        }
        
        let imageData = MGImageData.init(sampleBuffer: sample)
        self.markManager.beginDetectionFrame()
        let array = self.markManager.detect(with: imageData)
//        let faceModelArray = MGFaceModelArray()
//        faceModelArray.getFaceInfo = false
//        faceModelArray.get3DInfo = false
//        faceModelArray.detectRect = self.detectRect
        if array != nil && array!.count > 0 {
//            faceModelArray.faceArray = NSMutableArray.init(array: array!)
            let faceInfo = array![0]
            //self.markManager.getGetLandmark(faceInfo, isSmooth: true, pointsNumber: self.pointsNum.toInt32)
            if !self.remoteRecognizingUserFace {
                self.remoteRecognizingUserFace = true
                // 生成图片发送到服务器验证身份
                let image = MGImage.image(from: sample, orientation: UIImage.Orientation.rightMirrored)
                if image != nil {
                    // 将检测出的人脸框放大
                    let x = faceInfo.rect.origin.x;
                    let y = faceInfo.rect.origin.y;
                    let width = faceInfo.rect.size.width;
                    let height = faceInfo.rect.size.height;
                    let rect = CGRect(x: x-width/2, y: y-height/5, w: width*1.8, h: height*1.4);
                    // 截取人脸部分的图片
                    let faceImage = MGImage.croppedImage(image, rect: rect)
                    viewModel.faceRecognize(image: faceImage!)
                        .then { (userId)  in
                             DDLogInfo("userId:\(userId)")
                            
                            O2AuthSDK.shared.faceRecognizeLogin(userId: userId, callback: { (result, msg) in
                                if result {
                                    DispatchQueue.main.async {
                                        self.playBeepSound()
                                        //登录成功，跳转到主页
                                        let destVC = O2MainController.genernateVC()
                                        destVC.selectedIndex = 2 // 首页选中 TODO 图标不亮。。。。。
                                        UIApplication.shared.keyWindow?.rootViewController = destVC
                                        UIApplication.shared.keyWindow?.makeKeyAndVisible()
                                    }
                                }else {
                                    DDLogError("识别错误。。。。。。。\(msg ?? "")")
                                    self.remoteRecognizingUserFace = false
                                }
                            })
                        }.catch { (error) in
                            DDLogError("识别错误。。。。。。。\(error)")
                            self.remoteRecognizingUserFace = false
                        }
                }
            }
        }
        self.markManager.endDetectionFrame()
        self.displayWithfaceModel(faceModelArray: nil, detectSampleBufferRef: sample)
    }
    
    private func displayWithfaceModel(faceModelArray: MGFaceModelArray?, detectSampleBufferRef: CMSampleBuffer) {
        let item = DispatchWorkItem {
            let renderedPixelBuffer = self.renderer.drawPixelBuffer(detectSampleBufferRef, custumDrawing: {
//                self.renderer.drawFaceLandMark(faceModelArray)
            })
            if  let pixel = renderedPixelBuffer?.takeRetainedValue()  {
                self.previewView.display(pixel)
            }
        }
        DispatchQueue.main.async(execute: item)
    }
    
}
