//
//  OOVoiceView.swift
//  O2Platform
//
//  Created by FancyLou on 2018/9/10.
//  Copyright © 2018 zoneland. All rights reserved.
//

import UIKit
import CocoaLumberjack

class OOVoiceView: UIView {
    
    private var timer: Timer?
    private var time: TimeInterval?
    
    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        DDLogDebug("OOVoiceView    init coder  .... ")
    }
    
    private func startTimer() {
        self.time = 0.3
        self.timer = Timer.scheduledTimer(timeInterval: self.time!, target: self, selector: #selector(createCircleView), userInfo: nil, repeats: true)
        self.timer?.fire()
    }
    
    @objc private func createCircleView() {
        let rect = CGRect(x: 0, y: 0, width: self.width, height: self.height)
        let circleView = OOCircleRippleView(frame: rect)
        circleView.initSize = 0.3
        circleView.drawCircle()
        self.addSubview(circleView)
        UIView.animate(withDuration: 2, delay: 0, options: .allowUserInteraction, animations: {
            circleView.layer.transform = CATransform3DMakeScale(3, 3, 3)
            circleView.layer.opacity = 0
        }) { (result) in
            circleView.removeFromSuperview()
        }
    }
    
    private func stopTimer() {
        self.timer?.invalidate()
        self.timer = nil
        self.subviews.forEach { (view) in
            view.removeFromSuperview()
        }
    }
    
    func startAnimation() {
        DDLogDebug("start timer........")
        stopTimer()
        startTimer()
    }
    
    func stopAnimation() {
        DDLogDebug("stop timer......")
        stopTimer()
    }
}
