//
//  ScanQRViewController.swift
//  ScanQRCodeLikeWeChat
//
//  Created by FancyLou on 2020/8/25.
//  Copyright © 2020 muliba. All rights reserved.
//

import UIKit
import AVFoundation
import Photos

typealias ScanResultBlock = (String) -> Void

///仿微信 扫码功能 全屏版本
class ScanQRViewController: UIViewController {

    // MARK: - 返回结果
    var resultBlock: ScanResultBlock?

    // MARK: - private 参数
    ///摄像头输出
    private var output: AVCaptureMetadataOutput?
    private var session: AVCaptureSession?
    private var videoDataOutput: AVCaptureVideoDataOutput?
    private var videoPreviewLayer: AVCaptureVideoPreviewLayer?
    ///识别结果绘制图集
    private var reconizationViews: [ScanRecoObj] = []
    ///定时器
    private var animationTimeInterval: TimeInterval = 0.02
    private var timer: Timer?

    private var hasEntered = false

    private var scanBorderW: CGFloat = 0
    private var scanBorderX: CGFloat = 0
    private var scanBorderY: CGFloat = 0

    // MARK: - UI
    private var backBtn: UIButton!
    private var scanningline: UIImageView?


    // MARK: - system lifecycle func
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        self.addTimer()
    }

    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        self.removeTimer()
    }

    override func viewDidLoad() {
        super.viewDidLoad()
        self.view.backgroundColor = UIColor.black
        self.scanBorderW = 0.9 * self.view.frame.size.width
        self.scanBorderX = 0.5 * (1 - 0.9) * self.view.frame.size.width
        self.scanBorderY = 0.25 * self.view.frame.size.height
        self.initUI()

        self.prepareScanQRCodeWithResult { (_) in
            self.scanSessionStart()
        }
    }


    // MARK: - private func
    private func initUI() {
        //返回按钮
        self.backBtn = UIButton(type: .custom)
        self.backBtn.frame = CGRect(x: 15, y: 44, width: 44, height: 44)
        self.backBtn.setImage(UIImage(named: "scan_back"), for: .normal)
        self.backBtn.addTarget(self, action: #selector(closeSelf), for: .touchUpInside)
        self.view.addSubview(self.backBtn)
        //选择相册按钮
        let photosBtn = UIButton(type: .custom)
        photosBtn.frame = CGRect(x: (self.view.bounds.size.width - 44) / 2, y: self.view.bounds.size.height - 44 - 34 - 40, width: 44, height: 44)
        photosBtn.setImage(UIImage(named: "photos_icon"), for: .normal)
        photosBtn.addTarget(self, action: #selector(photosAction), for: .touchUpInside)
        self.view.addSubview(photosBtn)

    }

    ///开始扫描
    private func prepareScanQRCodeWithResult(block: @escaping (String) -> Void) {
        //判断设备权限
        guard let _ = AVCaptureDevice.default(for: .video) else {
            print("没有检测到摄像头，请在真机上测试！")
            return
        }
        let authStatus = AVCaptureDevice.authorizationStatus(for: .video)
        if authStatus == .restricted {
            let alertC = UIAlertController(title: "提示", message: "无法访问相机，请检查权限", preferredStyle: .alert)
            let okAction = UIAlertAction(title: "确定", style: .default, handler: { action in

            })
            alertC.addAction(okAction)
            DispatchQueue.main.async {
                self.present(alertC, animated: true, completion: nil)
            }
        } else if authStatus == .denied { //拒绝相机访问权限
            var name: String? = ""
            if let dic = Bundle.main.infoDictionary {
                name = dic["CFBundleDisplayName"] as? String
                if name == nil {
                    name = dic["CFBundleName"] as? String
                }
            }
            let message = "[前往：设置 - 隐私 - 相机 - \(name ?? "")] 允许应用访问"
            let alertC = UIAlertController(title: "提示", message: message, preferredStyle: .alert)
            let okAction = UIAlertAction(title: "确定", style: .default, handler: { action in

            })
            alertC.addAction(okAction)
            DispatchQueue.main.async {
                self.present(alertC, animated: true, completion: nil)
            }
        } else if authStatus == .authorized { //已经允许
            DispatchQueue.main.async { block("") }
        } else if authStatus == .notDetermined { //初始未选择
            AVCaptureDevice.requestAccess(for: .video) { (granted) in
                if granted {
                    DispatchQueue.main.async { block("") }
                } else {
                    //todo 拒绝访问。。。。
                }
            }
        }
    }

    ///准备摄像头，正式开始扫描
    private func scanSessionStart() {
        // 获取摄像头
        guard let device = AVCaptureDevice.default(for: .video) else {
            print("没有检测到摄像头，请在真机上测试！")
            return
        }
        var input: AVCaptureDeviceInput?
        do {
            input = try AVCaptureDeviceInput(device: device)
        } catch {
            print("err: \(error.localizedDescription)")
        }
        guard let deviceInput = input else {
            print("输入流创建失败")
            return
        }
        //输出流
        self.output = AVCaptureMetadataOutput()
        self.output?.setMetadataObjectsDelegate(self, queue: DispatchQueue.main)
        // 注：微信二维码的扫描范围是整个屏幕，这里并没有做处理（可不用设置）;
        // 如需限制扫描框范围，如下
        //    if !cropRect.equalTo(CGRect.zero)
//        {
        //启动相机后，直接修改该参数无效
//            output.rectOfInterest = cropRect
//        }
        //创建session
        self.session = AVCaptureSession()
        self.session?.sessionPreset = .high
        //添加元数据输出流到会话对象
        self.session?.addOutput(self.output!)
        //创建摄像数据输出流并将其添加到会话对象上,  --> 用于识别光线强弱
        self.videoDataOutput = AVCaptureVideoDataOutput()
        self.videoDataOutput?.setSampleBufferDelegate(self, queue: DispatchQueue.main)
        self.session?.addOutput(self.videoDataOutput!)
        //添加摄像设备输入流到会话对象
        self.session?.addInput(deviceInput)
        //识别类型
        self.output?.metadataObjectTypes = [.qr, .ean13, .ean8, .code128]
        self.videoPreviewLayer = AVCaptureVideoPreviewLayer(session: self.session!)
        self.videoPreviewLayer?.videoGravity = .resizeAspectFill
        self.videoPreviewLayer?.frame = self.view.bounds
        self.view.layer.insertSublayer(self.videoPreviewLayer!, at: 0)

        self.session?.startRunning()

    }

    ///添加定时器
    private func addTimer() {
        if self.session != nil && self.session?.isRunning != true && hasEntered {
            self.session?.startRunning()
        }
        hasEntered = true
        //扫描的时候绿色线条
        self.scanningline = UIImageView(image: UIImage(named: "QRCodeScanLine"))
        self.view.addSubview(self.scanningline!)

        self.scanningline?.frame = CGRect(x: self.scanBorderX, y: self.scanBorderY, width: self.scanBorderW, height: 12)
        self.scanningline?.isHidden = true
        self.timer = Timer.scheduledTimer(timeInterval: self.animationTimeInterval, target: self, selector: #selector(beginRefreshUI), userInfo: nil, repeats: true)
        self.timer?.fire()
    }

    ///移除定时器
    private func removeTimer() {
        self.timer?.invalidate()
        self.timer = nil
        self.scanningline?.removeFromSuperview()
        self.scanningline = nil
        if self.session?.isRunning == true {
            self.session?.stopRunning()
        }
    }

    //开始动画
    @objc private func beginRefreshUI() {
        //防止还没开始执行定时器就扫描到码，导致扫描动画一直进行
        if self.session?.isRunning != true {
            self.removeTimer()
            return
        }
        self.scanningline?.isHidden = false
        var frame = self.scanningline?.frame
        if self.scanningline?.frame.origin.y ?? self.scanBorderY >= self.scanBorderY {
            let maxY = self.view.frame.size.height - self.scanBorderY
            if self.scanningline?.frame.origin.y ?? self.scanBorderY >= maxY - 10 {
                frame?.origin.y = self.scanBorderY
                self.scanningline?.frame = frame!
            } else {
                UIView.animate(withDuration: self.animationTimeInterval) {
                    frame?.origin.y += 2
                    self.scanningline?.frame = frame!
                }
            }
        }

    }


    ///返回结果 关闭页面
    private func processWithResult(result: String) {
        self.resultBlock?(result)
        self.dismiss(animated: true, completion: nil)
    }

    ///关闭当前扫描页面
    @objc private func closeSelf() {
        if self.session?.isRunning == true {
            self.session?.stopRunning()
        }
        self.dismiss(animated: true, completion: nil)
    }

    //取消扫描出来的结果 继续扫描
    @objc private func cancelResult() {
        if self.session?.isRunning == true {
            self.session?.stopRunning()
        }
        self.reconizationViews.forEach { (obj) in
            obj.codeView.removeFromSuperview()
        }
        self.reconizationViews.removeAll()
        if self.session?.isRunning == false {
            self.addTimer()
        }
        self.backBtn.isHidden = false
    }

    ///从相册选择照片
    @objc private func photosAction() {
        let authStatus = PHPhotoLibrary.authorizationStatus()
        if authStatus == .restricted {
            let alertC = UIAlertController(title: "提示", message: "无法访问相册，请检查权限", preferredStyle: .alert)
            let okAction = UIAlertAction(title: "确定", style: .default, handler: { action in

            })
            alertC.addAction(okAction)
            DispatchQueue.main.async {
                self.present(alertC, animated: true, completion: nil)
            }
        } else if authStatus == .denied { //拒绝相机访问权限
            var name: String? = ""
            if let dic = Bundle.main.infoDictionary {
                name = dic["CFBundleDisplayName"] as? String
                if name == nil {
                    name = dic["CFBundleName"] as? String
                }
            }
            let message = "[前往：设置 - 隐私 - 照片 - \(name ?? "")] 允许应用访问"
            let alertC = UIAlertController(title: "提示", message: message, preferredStyle: .alert)
            let okAction = UIAlertAction(title: "确定", style: .default, handler: { action in

            })
            alertC.addAction(okAction)
            DispatchQueue.main.async {
                self.present(alertC, animated: true, completion: nil)
            }
        } else if authStatus == .authorized { //已经允许
            DispatchQueue.main.async { self.enterPhotos() }
        } else if authStatus == .notDetermined { //初始未选择
            PHPhotoLibrary.requestAuthorization { (status) in
                if status == .authorized {
                    DispatchQueue.main.async { self.enterPhotos() }
                } else {
                    //todo 拒绝访问。。。。
                }
            }
        }
    }

    @objc private func clickCurrentCode(btn: UIButton) {
        let obj = self.reconizationViews[btn.tag]
        self.processWithResult(result: obj.codeString)
    }


    ///打开相册
    private func enterPhotos() {
        let imagePicker = UIImagePickerController()
        imagePicker.sourceType = .photoLibrary
        imagePicker.delegate = self
        imagePicker.modalPresentationStyle = .fullScreen
        self.present(imagePicker, animated: true, completion: nil)
    }

    ///生成遮障层
    private func getMaskView(showTips: Bool) -> UIView {
        let maskView = UIView(frame: self.view.bounds)
        maskView.backgroundColor = UIColor(displayP3Red: 0, green: 0, blue: 0, alpha: 0.6)
        if showTips {
            let cancel = UIButton(type: .custom)
            cancel.frame = CGRect(x: 15, y: 44, width: 50, height: 44)
            cancel.setTitle("取消", for: .normal)
            cancel.setTitleColor(.white, for: .normal)
            cancel.addTarget(self, action: #selector(cancelResult), for: .touchUpInside)
            maskView.addSubview(cancel)
            let tips = UILabel(frame: CGRect(x: 20, y: self.view.bounds.size.height - 64 - 50, width: self.view.bounds.size.width - 40, height: 50))
            tips.text = "轻触小蓝点，选中识别二维码"
            tips.font = UIFont.boldSystemFont(ofSize: 14)
            tips.textAlignment = .center
            tips.textColor = UIColor(displayP3Red: 1, green: 1, blue: 1, alpha: 0.6)
            maskView.addSubview(tips)
        }
        return maskView
    }

    //动画
    private func getAnimation() -> CAKeyframeAnimation {
        let ani = CAKeyframeAnimation(keyPath: "transform.scale")
        ani.duration = 2.8
        ani.isRemovedOnCompletion = false
        ani.repeatCount = HUGE
        ani.fillMode = .forwards
        ani.timingFunction = CAMediaTimingFunction(name: .easeInEaseOut)
        let v1 = NSNumber(value: 1.0)
        let v2 = NSNumber(value: 0.8)
        ani.values = [v1, v2, v1, v2, v1, v1, v1, v1]
        return ani
    }

    ///生成扫码结果提示按钮
    private func getScanResultCode(bounds: CGRect, icon: Bool) -> UIButton {
        let btn = UIButton(type: .custom)
        btn.frame = bounds
        btn.backgroundColor = UIColor(displayP3Red: 54 / 255.0, green: 85 / 255.0, blue: 230 / 255.0, alpha: 1)
        btn.addTarget(self, action: #selector(clickCurrentCode(btn:)), for: .touchUpInside)
        if icon {
            btn.setImage(UIImage(named: "right_arrow_icon"), for: .normal)
            btn.layer.add(self.getAnimation(), forKey: "scale-layer")
        }
        var rect = btn.frame
        let center = btn.center
        rect.size.width = 40
        rect.size.height = 40
        btn.frame = rect
        btn.center = center
        btn.layer.cornerRadius = 20
        btn.clipsToBounds = true
        btn.layer.borderColor = UIColor.white.cgColor
        btn.layer.borderWidth = 3
        return btn
    }
}

// MARK: - 摄像头识别代理
extension ScanQRViewController: AVCaptureMetadataOutputObjectsDelegate, AVCaptureVideoDataOutputSampleBufferDelegate {
    func metadataOutput(_ output: AVCaptureMetadataOutput, didOutput metadataObjects: [AVMetadataObject], from connection: AVCaptureConnection) {
        if metadataObjects.count > 0 {
            self.removeTimer()
            if #available(iOS 10, *) {
                let impact = UIImpactFeedbackGenerator(style: .light)
                impact.impactOccurred()
            }
            let maskView = self.getMaskView(showTips: metadataObjects.count > 1)
            maskView.alpha = 0
            self.view.addSubview(maskView)
            UIView.animate(withDuration: 0.6) {
                maskView.alpha = 1
            }
            let obj = ScanRecoObj(codeView: maskView, codeString: "")
            self.reconizationViews.append(obj)

            var indx = 0
            for meta in metadataObjects {
                if meta.isKind(of: AVMetadataMachineReadableCodeObject.self) {
                    let code = self.videoPreviewLayer?.transformedMetadataObject(for: meta) as! AVMetadataMachineReadableCodeObject
                    let codeBtn = self.getScanResultCode(bounds: code.bounds, icon: metadataObjects.count > 1)
                    codeBtn.tag = indx + 1
                    self.view.addSubview(codeBtn)
                    let codeObj = ScanRecoObj(codeView: codeBtn, codeString: code.stringValue ?? "")
                    self.reconizationViews.append(codeObj)
                }
                indx += 1
            }
            self.backBtn.isHidden = true
            if metadataObjects.count == 1 { //只有一个直接返回结果 不需要点击
                DispatchQueue.main.asyncAfter(deadline: DispatchTime.now() + 0.8) {
                    self.processWithResult(result: self.reconizationViews[1].codeString)
                }
            }
        }
    }
}

// MARK: - 相册选择器代理
extension ScanQRViewController: UINavigationControllerDelegate, UIImagePickerControllerDelegate {
    func imagePickerControllerDidCancel(_ picker: UIImagePickerController) {
        self.dismiss(animated: true, completion: nil)
    }

    func imagePickerController(_ picker: UIImagePickerController, didFinishPickingMediaWithInfo info: [UIImagePickerController.InfoKey: Any]) {
        guard let image = info[UIImagePickerController.InfoKey.originalImage] as? UIImage else {
            return
        }
        let context = CIContext(options: nil)
        let detector = CIDetector(ofType: CIDetectorTypeQRCode, context: context, options: [CIDetectorAccuracy: CIDetectorAccuracyHigh])
        guard let ciImage = CIImage(image: image) else {
            return
        }
        let features = detector?.features(in: ciImage)
        if features?.count == 0 {
            DispatchQueue.main.async {
                self.dismiss(animated: true) {
                    self.processWithResult(result: "")
                }
            }
        } else {
            if let feature = features?.first as? CIQRCodeFeature, let result = feature.messageString {
                DispatchQueue.main.async {
                    self.dismiss(animated: true) {
                        self.processWithResult(result: result)
                    }
                }
            }
        }
    }
}
