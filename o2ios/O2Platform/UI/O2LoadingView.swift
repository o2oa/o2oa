//
//  O2LoadingView.swift
//  O2Platform
//
//  Created by 程剑 on 2017/7/6.
//  Copyright © 2017年 zoneland. All rights reserved.
//

import UIKit

class O2LoadingView: UIView {

    fileprivate var displayLink: CADisplayLink?
    fileprivate var shapeLayer: CAShapeLayer?
    fileprivate var treeImageView: UIImageView?
    fileprivate var viewRect: CGRect!
    
    var offsetY: CGFloat = 15
    var moveSpeed: CGFloat = 0.5
    var statusF: CGFloat!
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        self.viewRect = frame
        self.backgroundColor = UIColor(red: 251/255, green: 71/255, blue: 71/255, alpha: 1)
        configUI(frame)
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    func configUI(_ rect: CGRect) {
        self.treeImageView = UIImageView(image: UIImage(named: "oloading0"))
        self.treeImageView?.frame = CGRect(x: 0, y: 0, width: 100, height: 100)
        self.addSubview(self.treeImageView!)
        
        self.shapeLayer = CAShapeLayer()
        let shapeColor = UIColor(red: 251/255, green: 71/255, blue: 71/255, alpha: 0.5)
        
        self.shapeLayer?.fillColor = shapeColor.cgColor
        self.layer.addSublayer(self.shapeLayer!)
        
        self.displayLink = CADisplayLink(target: self, selector: #selector(self.getCurrentShape))
        self.displayLink?.add(to: .current, forMode: RunLoop.Mode.common)
        
        
    }
    
    @objc func getCurrentShape() {
        if (self.offsetY >= self.viewRect.size.height - 15) {
            self.statusF = 1;
        }else if (self.offsetY <= 15){
            self.statusF = 0;
        }
        if (self.statusF == 1) {
            self.offsetY -= self.moveSpeed;
        }else{
            self.offsetY += self.moveSpeed;
        }
        self.setCurrentStatusShapePath()
    }
    
    func setCurrentStatusShapePath() {
        let path = UIBezierPath()
        path.move(to: CGPoint(x: 0, y: self.offsetY))
        for i in 0..<Int(self.viewRect.size.width) {
            path.addLine(to: CGPoint(x: CGFloat(i), y: self.offsetY))
        }
        path.addLine(to: CGPoint(x: self.viewRect.size.width, y: 0))
        path.addLine(to: CGPoint(x: 0, y: 0))
        path.close()
        self.shapeLayer?.path = path.cgPath
        
    }
    
    func finishWave() {
        if let displayLink = self.displayLink {
            displayLink.invalidate()
            self.displayLink = nil
        }
        
    }

}
