//
//  OOCircleRippleView.swift
//  O2Platform
//
//  Created by FancyLou on 2018/9/10.
//  Copyright © 2018 zoneland. All rights reserved.
//

import UIKit

class OOCircleRippleView: UIView {
    
    //MARK: - arguments
    var initSize: CGFloat = 0.5
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    override init(frame: CGRect) {
        super.init(frame: frame)
    }
    
    func drawCircle() {
        let centerPoint = CGPoint(x: self.width/2, y: self.height/2)
        let beizerPath: UIBezierPath = UIBezierPath(arcCenter: centerPoint, radius: (self.frame.width * self.initSize), startAngle: 0, endAngle: CGFloat(2 * Double.pi), clockwise: true)
        let circlelayer = CAShapeLayer()
        circlelayer.fillColor  = base_color.cgColor// circleColor.cgColor // 填充颜色
        circlelayer.strokeColor = base_color.cgColor // 边框颜色
        circlelayer.path = beizerPath.cgPath
        self.layer.addSublayer(circlelayer)
    }
}
