//
//  JCFPSLabel.swift
//  JChat
//
//  Created by deng on 2017/3/8.
//  Copyright © 2017年 HXHG. All rights reserved.
//
//  Reference: ibireme/YYKit/YYFPSLabel
//

import UIKit

class JCFPSLabel: UILabel {

    public override init(frame: CGRect) {
        super.init(frame: frame)
        build()
    }
    public required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        build()
    }
    
    public override var intrinsicContentSize: CGSize {
        return CGSize(width: 55, height: 20)
    }
    public override func willMove(toWindow newWindow: UIWindow?) {
        super.willMove(toWindow: newWindow)
        if newWindow == nil {
            _link.invalidate()
        } else {
            _link.add(to: RunLoop.main, forMode: RunLoop.Mode.common)
        }
    }
    
    @inline(__always) private func build() {
        
        text = "calc..."
        font = UIFont.systemFont(ofSize: 14)
        textColor = UIColor.white
        textAlignment = .center
        backgroundColor = UIColor(white: 0, alpha: 0.7)
        
        layer.cornerRadius = 5
        layer.masksToBounds = true
    }
    
    private dynamic func tack(_ link: CADisplayLink) {
        guard let lastTime = _lastTime else {
            _lastTime = link.timestamp
            return
        }
        _count += 1
        let delta = link.timestamp - lastTime
        guard delta >= 1 else {
            return
        }
        let fps = Double(_count) / delta + 0.03
        let progress = CGFloat(fps / 60)
        let color = UIColor(hue: 0.27 * (progress - 0.2), saturation: 1, brightness: 0.9, alpha: 1)
        
        let text = NSMutableAttributedString(string: "\(Int(fps)) FPS")
        text.addAttribute(NSAttributedString.Key.foregroundColor, value: color, range: NSMakeRange(0, text.length - 3))
        attributedText = text
        
        _count = 0
        _lastTime = link.timestamp
    }
    
    private var _count: Int = 0
    private var _lastTime: TimeInterval?
    
    private lazy var _link: CADisplayLink = {
        return CADisplayLink(target: self, selector: #selector(type(of: self).tack(_:)))
    }()

}
