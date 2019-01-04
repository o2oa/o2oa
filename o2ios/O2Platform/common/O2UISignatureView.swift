//
//  O2UISignatureView.swift
//  O2Platform
//
//  Created by FancyLou on 2018/8/28.
//  Copyright © 2018 zoneland. All rights reserved.
//

import UIKit
import CocoaLumberjack


class O2UISignatureView: UIView {
    
    //MARK: - arguments
    var control: Int!
    var beizerPath: UIBezierPath!
    var points:[CGPoint] = [CGPoint](repeating: CGPoint(), count: 5)
    
    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        self.initArguments()
    }
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        self.initArguments()
    }
    
    private func initArguments() {
        self.backgroundColor = UIColor.white //背景颜色
        self.isMultipleTouchEnabled = false
        self.beizerPath = UIBezierPath()
        self.beizerPath.lineWidth = 2
    }
    
    override func draw(_ rect: CGRect) {
        let color = UIColor.black
        color.setStroke()
        self.beizerPath.stroke()
    }
    
    override func touchesBegan(_ touches: Set<UITouch>, with event: UIEvent?) {
        self.control = 0
        if let point = touches.first?.location(in: self) {
            points[0] = point
            let startPoint = points[0]
            let endPoint = CGPoint(x: startPoint.x + 1.5, y: startPoint.y + 2)
            self.beizerPath.move(to: startPoint)
            self.beizerPath.addLine(to: endPoint)
        }
    }
    
    override func touchesMoved(_ touches: Set<UITouch>, with event: UIEvent?) {
        if let point = touches.first?.location(in: self) {
            self.control = self.control + 1
            points[self.control] = point
            if self.control == 4 {
                points[3] = CGPoint(x: (points[2].x + points[4].x)/2.0, y: (points[2].y + points[4].y)/2.0)
                //设置画笔起始点
                self.beizerPath.move(to: points[0])
                //endPoint终点 controlPoint1、controlPoint2控制点
                self.beizerPath.addCurve(to: points[3], controlPoint1: points[1], controlPoint2: points[2])
                //setNeedsDisplay会自动调用drawRect方法，这样可以拿到UIGraphicsGetCurrentContext，就可以画画了
                self.setNeedsDisplay()
                points[0] = points[3]
                points[1] = points[4]
                self.control = 1
            }
        }
    }
    
    // 设置笔粗细
    func setLineSize(lineSize: CGFloat) {
        DDLogDebug("line size : \(lineSize)")
        self.beizerPath.lineWidth = lineSize
    }
    
    // 清除内容
    func clearSignature() {
        self.beizerPath.removeAllPoints()
        self.setNeedsDisplay()
        DDLogDebug("is empty: \(self.beizerPath.isEmpty)")
    }
    
    // 生成签名图片
    func getSignatureImage() -> UIImage? {
        if self.beizerPath.isEmpty {
            return nil
        }
        UIGraphicsBeginImageContextWithOptions(self.bounds.size, false, UIScreen.main.scale)
        self.layer.render(in: UIGraphicsGetCurrentContext()!)
        let image = UIGraphicsGetImageFromCurrentImageContext()
        UIGraphicsEndImageContext()
        if image != nil {
            let documentsURL = FileManager.default.urls(for: .cachesDirectory, in: .userDomainMask)[0]
            let fileURL = documentsURL.appendingPathComponent("signature.png")
            do {
                try image?.pngData()?.write(to: fileURL, options: .atomic)
            }catch {
                DDLogError("写入本地异常，\(error.localizedDescription)")
            }
            return image!
        }else {
            return nil
        }
        
    }
    
    
    
}
