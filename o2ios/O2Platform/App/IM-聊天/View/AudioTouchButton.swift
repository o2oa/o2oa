//
//  AudioTouchButton.swift
//  O2Platform
//
//  Created by FancyLou on 2020/7/7.
//  Copyright © 2020 zoneland. All rights reserved.
//

import UIKit

class AudioTouchButton: UIView {

    var areaY: CGFloat = 40
    var clickTime = 0.5
    var touchBegan: (()->Void)? = nil
    var upglide: (()->Void)? = nil
    var down: (()->Void)? = nil
    var touchEnd: (()->Void)? = nil
    var voiceButton: UIButton?
    
    private var isBegan: Bool = false
    private var timer: Timer?

    
    override init(frame: CGRect) {
        super.init(frame: frame)
        self.areaY = -40
        self.clickTime = 0.5
        self.isBegan = false
        self.voiceButton = UIButton(type: .custom)
        self.addSubview(self.voiceButton!)
        self.voiceButton?.titleLabel?.font = .systemFont(ofSize: 14)
        self.voiceButton?.isUserInteractionEnabled = false
        self.voiceButton?.snp_makeConstraints({ (make) in
            make.edges.equalTo(self)
        })
        
        
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    override func point(inside point: CGPoint, with event: UIEvent?) -> Bool {
        if self.voiceButton?.isSelected == true {
            return true
        }else {
            return super.point(inside: point, with: event)
        }
    }
    
    override func touchesBegan(_ touches: Set<UITouch>, with event: UIEvent?) {
        let timer = Timer.scheduledTimer(timeInterval: self.clickTime, target: self, selector: #selector(timeAction), userInfo: nil, repeats: false)
        print("++++++++++++++++++开始")
        self.timer = timer
    }
    
    override func touchesMoved(_ touches: Set<UITouch>, with event: UIEvent?) {
        if self.voiceButton?.isSelected == true {
            let anchTouch = touches.first
            let point = anchTouch?.location(in: self)
            if point?.y ?? 0 > self.areaY {
                self.down?()
            }else {
                self.upglide?()
            }
        }
        
    }
    
    override func touchesEnded(_ touches: Set<UITouch>, with event: UIEvent?) {
        if self.voiceButton?.isSelected == true {
            self.touchEnd?()
        }
        self.voiceButton?.isSelected = false
        self.timer?.invalidate()
        self.timer = nil
        print("+++++++++++++++++取消")
    }

    @objc func timeAction() {
        self.touchBegan?()
        print("++++++++++++执行");
        self.voiceButton?.isSelected = true
        self.timer?.invalidate()
    }
    
}
