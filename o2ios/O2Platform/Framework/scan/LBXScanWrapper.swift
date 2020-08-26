//
//  LBXScanWrapper.swift
//  swiftScan
//
//  Created by lbxia on 15/12/10.
//  Copyright © 2015年 xialibing. All rights reserved.
//

import UIKit
import AVFoundation

public struct LBXScanResult {

    //码内容
    public var strScanned: String? = ""
    //扫描图像
    public var imgScanned: UIImage?
    //码的类型
    public var strBarCodeType: String? = ""

    //码在图像中的位置
    public var arrayCorner: [AnyObject]?

    public init(str: String?, img: UIImage?, barCodeType: String?, corner: [AnyObject]?)
    {
        self.strScanned = str
        self.imgScanned = img
        self.strBarCodeType = barCodeType
        self.arrayCorner = corner
    }
}



open class LBXScanWrapper: NSObject, AVCaptureMetadataOutputObjectsDelegate {

    let device = AVCaptureDevice.default(for: AVMediaType.video)
    var input: AVCaptureDeviceInput?
    var output: AVCaptureMetadataOutput

    let session = AVCaptureSession()
    var previewLayer: AVCaptureVideoPreviewLayer?
    var stillImageOutput: AVCaptureStillImageOutput?

    var videoPreView: UIView?
    
    var scaleSize:CGFloat = 1
    
    //存储返回结果
    var arrayResult: [LBXScanResult] = [];

    //扫码结果返回block
    var successBlock: ([LBXScanResult]) -> Void

    //是否需要拍照
    var isNeedCaptureImage: Bool

    //当前扫码结果是否处理
    var isNeedScanResult: Bool = true
    
    

    /**
     初始化设备
     - parameter videoPreView: 视频显示UIView
     - parameter objType:      识别码的类型,缺省值 QR二维码
     - parameter isCaptureImg: 识别后是否采集当前照片
     - parameter cropRect:     识别区域
     - parameter success:      返回识别信息
     - returns:
     */
    init(videoPreView: UIView, objType: [AVMetadataObject.ObjectType] = [(AVMetadataObject.ObjectType.qr as NSString) as AVMetadataObject.ObjectType], isCaptureImg: Bool, cropRect: CGRect = CGRect.zero, success: @escaping (([LBXScanResult]) -> Void))
    {
        self.videoPreView = videoPreView
        
        do {
            input = try AVCaptureDeviceInput(device: device!)
        }
        catch let error as NSError {
            print("AVCaptureDeviceInput(): \(error)")
        }

        successBlock = success

        // Output
        output = AVCaptureMetadataOutput()

        isNeedCaptureImage = isCaptureImg

        stillImageOutput = AVCaptureStillImageOutput();

        super.init()

        if device == nil || input == nil {
            return
        }

        if session.canAddInput(input!) {
            session.addInput(input!)
        }

        if session.canAddOutput(output) {
            session.addOutput(output)
        }

        if session.canAddOutput(stillImageOutput!) {
            session.addOutput(stillImageOutput!)
        }

        let outputSettings: Dictionary = [AVVideoCodecJPEG: AVVideoCodecKey]
        stillImageOutput?.outputSettings = outputSettings

        session.sessionPreset = AVCaptureSession.Preset.high

        //参数设置
        output.setMetadataObjectsDelegate(self, queue: DispatchQueue.main)

        output.metadataObjectTypes = objType

        if !cropRect.equalTo(CGRect.zero)
        {
            //启动相机后，直接修改该参数无效
            output.rectOfInterest = cropRect
        }

        previewLayer = AVCaptureVideoPreviewLayer(session: session)
        previewLayer?.videoGravity = AVLayerVideoGravity.resizeAspectFill

        var frame: CGRect = videoPreView.frame
        frame.origin = CGPoint.zero
        previewLayer?.frame = frame
     
        videoPreView.layer.insertSublayer(previewLayer!, at: 0)
        
        do {
            try input?.device.lockForConfiguration()

            if device?.isWhiteBalanceModeSupported(.autoWhiteBalance) == true {
                input?.device.whiteBalanceMode = .autoWhiteBalance
            }

            if (device!.isFocusPointOfInterestSupported && device!.isFocusModeSupported(AVCaptureDevice.FocusMode.continuousAutoFocus))
            {
                input?.device.focusMode = AVCaptureDevice.FocusMode.continuousAutoFocus
            }
            
            input?.device.unlockForConfiguration()
        } catch let error as NSError {
            print("device.lockForConfiguration(): \(error)")
        }
         
    }

    public func metadataOutput(_ output: AVCaptureMetadataOutput, didOutput metadataObjects: [AVMetadataObject], from connection: AVCaptureConnection) {
        captureOutput(output, didOutputMetadataObjects: metadataObjects, from: connection)
    }

    func start()
    {
        if !session.isRunning
        {
            isNeedScanResult = true
            session.startRunning()
        }
    }
    func stop()
    {
        if session.isRunning
        {
            isNeedScanResult = false
            session.stopRunning()
        }
    }
    
//    func setVideoScale(scale: CGFloat) {
//        print("调整镜头................")
//        do {
//            try input?.device.lockForConfiguration()
//            let videoConnection = self.connectionWithMediaType(mediaType: .video, connections: self.stillImageOutput?.connections ?? [])
//            let maxScaleAndCropFactor = self.stillImageOutput?.connection(with: .video)?.videoMaxScaleAndCropFactor ?? 0 / 16
//            var newScale = scale
//            if newScale > maxScaleAndCropFactor {
//                newScale = maxScaleAndCropFactor
//            }
//            print("scale.....\(newScale)")
//            let videoFactor = videoConnection?.videoScaleAndCropFactor ?? 1
//            let zoom = newScale / videoFactor
//            videoConnection?.videoScaleAndCropFactor = newScale
//            input?.device.unlockForConfiguration()
//            print("zoom.......\(zoom)")
//            if let transform = self.videoPreView?.transform {
//                self.videoPreView?.transform = transform.scaledBy(x: zoom, y: zoom)
//            }
//
//        } catch let error as NSError {
//           print("device.lockForConfiguration(): \(error)")
//       }
//    }
//
//    func changeVideoScale(obj: AVMetadataMachineReadableCodeObject?) {
//        if let o = obj {
//            let arr = o.corners
//            if arr.count >= 3 {
//                let point = arr[1]
//                let point2 = arr[2]
//                let scale = 150 / (point2.x -  point.x) //当二维码图片宽小于150，进行放大
//                print("scale.......\(scale)")
//                if scale > 1 {
//                    for item in stride(from: 1, to: scale, by: 0.01) {
//                        self.setVideoScale(scale: item)
//                    }
//                }
//            }
//        }
//    }

    open func captureOutput(_ captureOutput: AVCaptureOutput, didOutputMetadataObjects metadataObjects: [AVMetadataObject], from connection: AVCaptureConnection) {

        if !isNeedScanResult
        {
            //上一帧处理中
            return
        }

        isNeedScanResult = false

        arrayResult.removeAll()

//        print(metadataObjects)
        
//        if metadataObjects.count < 1 {
//            if self.scaleSize == 1 {
//                self.scaleSize = 2
//            }else {
//                self.scaleSize = 1
//            }
//            self.setVideoScale(scale: self.scaleSize)
//        }
        
        //识别扫码类型
        for current  in metadataObjects
        {
            if (current).isKind(of: AVMetadataMachineReadableCodeObject.self)
            {
                let code = current as! AVMetadataMachineReadableCodeObject

                //码类型
                let codeType = code.type
                //                print("code type:%@",codeType)
                //码内容
                let codeContent = code.stringValue
                //                print("code string:%@",codeContent)

                //4个字典，分别 左上角-右上角-右下角-左下角的 坐标百分百，可以使用这个比例抠出码的图像
                // let arrayRatio = code.corners

                arrayResult.append(LBXScanResult(str: codeContent, img: UIImage(), barCodeType: codeType.rawValue, corner: code.corners as [AnyObject]?))
            }
        }

        if arrayResult.count > 0
        {
            if isNeedCaptureImage
            {
                captureImage()
            }
            else
            {
                stop()
                successBlock(arrayResult)
            }

        }
        else
        {
            isNeedScanResult = true
        }

    }

    //MARK: ----拍照
    open func captureImage()
    {
        let stillImageConnection: AVCaptureConnection? = connectionWithMediaType(mediaType: AVMediaType.video as AVMediaType, connections: (stillImageOutput?.connections)! as [AnyObject])


        stillImageOutput?.captureStillImageAsynchronously(from: stillImageConnection!, completionHandler: { (imageDataSampleBuffer, error) -> Void in

            self.stop()
            if imageDataSampleBuffer != nil
            {
                let imageData: Data = AVCaptureStillImageOutput.jpegStillImageNSDataRepresentation(imageDataSampleBuffer!)!
                let scanImg: UIImage? = UIImage(data: imageData)


                for idx in 0...self.arrayResult.count - 1
                {
                    self.arrayResult[idx].imgScanned = scanImg
                }
            }

            self.successBlock(self.arrayResult)

        })
    }

    open func connectionWithMediaType(mediaType: AVMediaType, connections: [AnyObject]) -> AVCaptureConnection?
    {
        for connection: AnyObject in connections
        {
            let connectionTmp: AVCaptureConnection = connection as! AVCaptureConnection

            for port: Any in connectionTmp.inputPorts
            {
                if (port as AnyObject).isKind(of: AVCaptureInput.Port.self)
                {
                    let portTmp: AVCaptureInput.Port = port as! AVCaptureInput.Port
                    if portTmp.mediaType == mediaType
                    {
                        return connectionTmp
                    }
                }
            }
        }
        return nil
    }


    //MARK:切换识别区域
    open func changeScanRect(cropRect: CGRect)
    {
        //待测试，不知道是否有效
        stop()
        output.rectOfInterest = cropRect
        start()
    }

    //MARK: 切换识别码的类型
    open func changeScanType(objType: [AVMetadataObject.ObjectType])
    {
        //待测试中途修改是否有效
        output.metadataObjectTypes = objType
    }

    open func isGetFlash() -> Bool
    {
        if (device != nil && device!.hasFlash && device!.hasTorch)
        {
            return true
        }
        return false
    }

    /**
     打开或关闭闪关灯
     - parameter torch: true：打开闪关灯 false:关闭闪光灯
     */
    open func setTorch(torch: Bool)
    {
        if isGetFlash()
        {
            do
            {
                try input?.device.lockForConfiguration()

                input?.device.torchMode = torch ? AVCaptureDevice.TorchMode.on : AVCaptureDevice.TorchMode.off

                input?.device.unlockForConfiguration()
            }
            catch let error as NSError {
                print("device.lockForConfiguration(): \(error)")

            }
        }

    }


    /**
     ------闪光灯打开或关闭
     */
    open func changeTorch()
    {
        if isGetFlash()
        {
            do
            {
                try input?.device.lockForConfiguration()

                var torch = false

                if input?.device.torchMode == AVCaptureDevice.TorchMode.on
                {
                    torch = false
                }
                else if input?.device.torchMode == AVCaptureDevice.TorchMode.off
                {
                    torch = true
                }

                input?.device.torchMode = torch ? AVCaptureDevice.TorchMode.on : AVCaptureDevice.TorchMode.off

                input?.device.unlockForConfiguration()
            }
            catch let error as NSError {
                print("device.lockForConfiguration(): \(error)")

            }
        }
    }

    //MARK: ------获取系统默认支持的码的类型
    static func defaultMetaDataObjectTypes() -> [AVMetadataObject.ObjectType]
    {
        var types =
            [AVMetadataObject.ObjectType.qr,
                AVMetadataObject.ObjectType.upce,
                AVMetadataObject.ObjectType.code39,
                AVMetadataObject.ObjectType.code39Mod43,
                AVMetadataObject.ObjectType.ean13,
                AVMetadataObject.ObjectType.ean8,
                AVMetadataObject.ObjectType.code93,
                AVMetadataObject.ObjectType.code128,
                AVMetadataObject.ObjectType.pdf417,
                AVMetadataObject.ObjectType.aztec
            ]
        //if #available(iOS 8.0, *)

        types.append(AVMetadataObject.ObjectType.interleaved2of5)
        types.append(AVMetadataObject.ObjectType.itf14)
        types.append(AVMetadataObject.ObjectType.dataMatrix)
        return types as [AVMetadataObject.ObjectType]
    }


    static func isSysIos8Later() -> Bool
    {
        //        return floor(NSFoundationVersionNumber) > NSFoundationVersionNumber_iOS_8_0

        if #available(iOS 8, *) {
            return true;
        }
        return false
    }

    /**
     识别二维码码图像
     
     - parameter image: 二维码图像
     
     - returns: 返回识别结果
     */
    static public func recognizeQRImage(image: UIImage) -> [LBXScanResult]
    {
        var returnResult: [LBXScanResult] = []

        if LBXScanWrapper.isSysIos8Later()
        {
            //if #available(iOS 8.0, *)

            let detector: CIDetector = CIDetector(ofType: CIDetectorTypeQRCode, context: nil, options: [CIDetectorAccuracy: CIDetectorAccuracyHigh])!

            let img = CIImage(cgImage: (image.cgImage)!)

            let features: [CIFeature]? = detector.features(in: img, options: [CIDetectorAccuracy: CIDetectorAccuracyHigh])

            if(features != nil && (features?.count)! > 0)
            {
                let feature = features![0]

                if feature.isKind(of: CIQRCodeFeature.self)
                {
                    let featureTmp: CIQRCodeFeature = feature as! CIQRCodeFeature

                    let scanResult = featureTmp.messageString


                    let result = LBXScanResult(str: scanResult, img: image, barCodeType: AVMetadataObject.ObjectType.qr.rawValue, corner: nil)

                    returnResult.append(result)
                }
            }

        }

        return returnResult
    }


    //MARK: -- - 生成二维码，背景色及二维码颜色设置
    static public func createCode(codeType: String, codeString: String, size: CGSize, qrColor: UIColor, bkColor: UIColor) -> UIImage?
    {
        //if #available(iOS 8.0, *)

        let stringData = codeString.data(using: String.Encoding.utf8)


        //系统自带能生成的码
        //        CIAztecCodeGenerator
        //        CICode128BarcodeGenerator
        //        CIPDF417BarcodeGenerator
        //        CIQRCodeGenerator
        let qrFilter = CIFilter(name: codeType)


        qrFilter?.setValue(stringData, forKey: "inputMessage")

        qrFilter?.setValue("H", forKey: "inputCorrectionLevel")


        //上色
        let colorFilter = CIFilter(name: "CIFalseColor", parameters: ["inputImage": qrFilter!.outputImage!, "inputColor0": CIColor(cgColor: qrColor.cgColor), "inputColor1": CIColor(cgColor: bkColor.cgColor)])


        let qrImage = colorFilter!.outputImage!;

        //绘制
        let cgImage = CIContext().createCGImage(qrImage, from: qrImage.extent)!


        UIGraphicsBeginImageContext(size);
        let context = UIGraphicsGetCurrentContext()!;
        context.interpolationQuality = CGInterpolationQuality.none;
        context.scaleBy(x: 1.0, y: -1.0);
        context.draw(cgImage, in: context.boundingBoxOfClipPath)
        let codeImage = UIGraphicsGetImageFromCurrentImageContext();
        UIGraphicsEndImageContext();

        return codeImage

    }

    static public func createCode128(codeString: String, size: CGSize, qrColor: UIColor, bkColor: UIColor) -> UIImage?
    {
        let stringData = codeString.data(using: String.Encoding.utf8)


        //系统自带能生成的码
        //        CIAztecCodeGenerator 二维码
        //        CICode128BarcodeGenerator 条形码
        //        CIPDF417BarcodeGenerator
        //        CIQRCodeGenerator     二维码
        let qrFilter = CIFilter(name: "CICode128BarcodeGenerator")
        qrFilter?.setDefaults()
        qrFilter?.setValue(stringData, forKey: "inputMessage")



        let outputImage: CIImage? = qrFilter?.outputImage
        let context = CIContext()
        let cgImage = context.createCGImage(outputImage!, from: outputImage!.extent)

        let image = UIImage(cgImage: cgImage!, scale: 1.0, orientation: UIImage.Orientation.up)


        // Resize without interpolating
        let scaleRate: CGFloat = 20.0
        let resized = resizeImage(image: image, quality: CGInterpolationQuality.none, rate: scaleRate)

        return resized;
    }


    //MARK:根据扫描结果，获取图像中得二维码区域图像（如果相机拍摄角度故意很倾斜，获取的图像效果很差）
    static func getConcreteCodeImage(srcCodeImage: UIImage, codeResult: LBXScanResult) -> UIImage?
    {
        let rect: CGRect = getConcreteCodeRectFromImage(srcCodeImage: srcCodeImage, codeResult: codeResult)

        if rect.isEmpty
        {
            return nil
        }

        let img = imageByCroppingWithStyle(srcImg: srcCodeImage, rect: rect)

        if img != nil
        {
            let imgRotation = imageRotation(image: img!, orientation: UIImage.Orientation.right)
            return imgRotation
        }
        return nil
    }
    //根据二维码的区域截取二维码区域图像
    static public func getConcreteCodeImage(srcCodeImage: UIImage, rect: CGRect) -> UIImage?
    {
        if rect.isEmpty
        {
            return nil
        }

        let img = imageByCroppingWithStyle(srcImg: srcCodeImage, rect: rect)

        if img != nil
        {
            let imgRotation = imageRotation(image: img!, orientation: UIImage.Orientation.right)
            return imgRotation
        }
        return nil
    }

    //获取二维码的图像区域
    static public func getConcreteCodeRectFromImage(srcCodeImage: UIImage, codeResult: LBXScanResult) -> CGRect
    {
        if (codeResult.arrayCorner == nil || (codeResult.arrayCorner?.count)! < 4)
        {
            return CGRect.zero
        }

        let corner: [[String: Float]] = codeResult.arrayCorner as! [[String: Float]]

        let dicTopLeft = corner[0]
        let dicTopRight = corner[1]
        let dicBottomRight = corner[2]
        let dicBottomLeft = corner[3]

        let xLeftTopRatio: Float = dicTopLeft["X"]!
        let yLeftTopRatio: Float = dicTopLeft["Y"]!

        let xRightTopRatio: Float = dicTopRight["X"]!
        let yRightTopRatio: Float = dicTopRight["Y"]!

        let xBottomRightRatio: Float = dicBottomRight["X"]!
        let yBottomRightRatio: Float = dicBottomRight["Y"]!

        let xLeftBottomRatio: Float = dicBottomLeft["X"]!
        let yLeftBottomRatio: Float = dicBottomLeft["Y"]!

        //由于截图只能矩形，所以截图不规则四边形的最大外围
        let xMinLeft = CGFloat(min(xLeftTopRatio, xLeftBottomRatio))
        let xMaxRight = CGFloat(max(xRightTopRatio, xBottomRightRatio))

        let yMinTop = CGFloat(min(yLeftTopRatio, yRightTopRatio))
        let yMaxBottom = CGFloat (max(yLeftBottomRatio, yBottomRightRatio))

        let imgW = srcCodeImage.size.width
        let imgH = srcCodeImage.size.height

        //宽高反过来计算
        let rect = CGRect(x: xMinLeft * imgH, y: yMinTop * imgW, width: (xMaxRight - xMinLeft) * imgH, height: (yMaxBottom - yMinTop) * imgW)
        return rect
    }

    //MARK: ----图像处理

    /**

    @brief  图像中间加logo图片
    @param srcImg    原图像
    @param LogoImage logo图像
    @param logoSize  logo图像尺寸
    @return 加Logo的图像
    */
    static public func addImageLogo(srcImg: UIImage, logoImg: UIImage, logoSize: CGSize) -> UIImage

    {
        UIGraphicsBeginImageContext(srcImg.size);
        srcImg.draw(in: CGRect(x: 0, y: 0, width: srcImg.size.width, height: srcImg.size.height))
        let rect = CGRect(x: srcImg.size.width / 2 - logoSize.width / 2, y: srcImg.size.height / 2 - logoSize.height / 2, width: logoSize.width, height: logoSize.height);
        logoImg.draw(in: rect)
        let resultingImage = UIGraphicsGetImageFromCurrentImageContext();
        UIGraphicsEndImageContext();
        return resultingImage!;
    }

    //图像缩放
    static func resizeImage(image: UIImage, quality: CGInterpolationQuality, rate: CGFloat) -> UIImage?
    {
        var resized: UIImage?;
        let width = image.size.width * rate;
        let height = image.size.height * rate;

        UIGraphicsBeginImageContext(CGSize(width: width, height: height));
        let context = UIGraphicsGetCurrentContext();
        context!.interpolationQuality = quality;
        image.draw(in: CGRect(x: 0, y: 0, width: width, height: height))

        resized = UIGraphicsGetImageFromCurrentImageContext();
        UIGraphicsEndImageContext();

        return resized;
    }


    //图像裁剪
    static func imageByCroppingWithStyle(srcImg: UIImage, rect: CGRect) -> UIImage?
    {
        let imageRef = srcImg.cgImage
        let imagePartRef = imageRef!.cropping(to: rect)
        let cropImage = UIImage(cgImage: imagePartRef!)

        return cropImage
    }
    //图像旋转
    static func imageRotation(image: UIImage, orientation: UIImage.Orientation) -> UIImage
    {
        var rotate: Double = 0.0;
        var rect: CGRect;
        var translateX: CGFloat = 0.0;
        var translateY: CGFloat = 0.0;
        var scaleX: CGFloat = 1.0;
        var scaleY: CGFloat = 1.0;

        switch (orientation) {
        case UIImage.Orientation.left:
            rotate = .pi / 2;
            rect = CGRect(x: 0, y: 0, width: image.size.height, height: image.size.width);
            translateX = 0;
            translateY = -rect.size.width;
            scaleY = rect.size.width / rect.size.height;
            scaleX = rect.size.height / rect.size.width;
            break;
        case UIImage.Orientation.right:
            rotate = 3 * .pi / 2;
            rect = CGRect(x: 0, y: 0, width: image.size.height, height: image.size.width);
            translateX = -rect.size.height;
            translateY = 0;
            scaleY = rect.size.width / rect.size.height;
            scaleX = rect.size.height / rect.size.width;
            break;
        case UIImage.Orientation.down:
            rotate = .pi;
            rect = CGRect(x: 0, y: 0, width: image.size.width, height: image.size.height);
            translateX = -rect.size.width;
            translateY = -rect.size.height;
            break;
        default:
            rotate = 0.0;
            rect = CGRect(x: 0, y: 0, width: image.size.width, height: image.size.height);
            translateX = 0;
            translateY = 0;
            break;
        }

        UIGraphicsBeginImageContext(rect.size);
        let context = UIGraphicsGetCurrentContext()!;
        //做CTM变换
        context.translateBy(x: 0.0, y: rect.size.height);
        context.scaleBy(x: 1.0, y: -1.0);
        context.rotate(by: CGFloat(rotate));
        context.translateBy(x: translateX, y: translateY);

        context.scaleBy(x: scaleX, y: scaleY);
        //绘制图片
        context.draw(image.cgImage!, in: CGRect(x: 0, y: 0, width: rect.size.width, height: rect.size.height))
        let newPic = UIGraphicsGetImageFromCurrentImageContext();

        return newPic!;
    }

    deinit
    {
        //        print("LBXScanWrapper deinit")
    }
}
