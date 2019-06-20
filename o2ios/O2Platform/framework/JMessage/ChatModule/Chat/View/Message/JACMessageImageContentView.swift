//
//  JCMessageImageContentView.swift
//  JChat
//
//  Created by deng on 2017/3/9.
//  Copyright © 2017年 HXHG. All rights reserved.
//

import UIKit

open class JCMessageImageContentView: UIImageView, JCMessageContentViewType {
    
    public override init(image: UIImage?) {
        super.init(image: image)
        _commonInit()
    }
    public override init(image: UIImage?, highlightedImage: UIImage?) {
        super.init(image: image, highlightedImage: highlightedImage)
        _commonInit()
    }
    public override init(frame: CGRect) {
        super.init(frame: frame)
        _commonInit()
    }
    public required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        _commonInit()
    }

    open func apply(_ message: JCMessageType) {
        _message = message
        weak var weakSelf = self
        guard let content = message.content as? JCMessageImageContent else {
            return
        } 
        image = content.image
        _delegate = content.delegate
        percentLabel.frame = CGRect(x: 0, y: 0, width: self.width, height: self.height)
        if message.options.state == .sending {
            percentLabel.backgroundColor = UIColor.black.withAlphaComponent(0.3)
            percentLabel.isHidden = false
            percentLabel.textColor = .white
            content.upload = {  (percent:Float) -> Void in
                DispatchQueue.main.async {
                    let p = Int(percent * 100)
                    weakSelf?.percentLabel.text = "\(p)%"
                    if percent == 1.0 {
                        weakSelf?.percentLabel.isHidden = true
                        weakSelf?.percentLabel.text = ""
                    }
                }
            }
        } else {
            percentLabel.textColor = .clear
            percentLabel.backgroundColor = .clear
        }
    }
    
    private weak var _delegate: JCMessageDelegate?
    private var _message: JCMessageType!
    
    private lazy var percentLabel: UILabel = {
        var percentLabel = UILabel(frame: CGRect(x: 20, y: 40, width: 50, height: 20))
        percentLabel.isUserInteractionEnabled = false
        percentLabel.textAlignment = .center
        percentLabel.textColor = .white
        percentLabel.font = UIFont.systemFont(ofSize: 17)
        return percentLabel
    }()
    
    private func _commonInit() {
        let tapGR = UITapGestureRecognizer(target: self, action: #selector(_tapHandler))
        addGestureRecognizer(tapGR)
        isUserInteractionEnabled = true
        layer.cornerRadius = 2
        layer.masksToBounds = true
        addSubview(percentLabel)
    }
    
    @objc func _tapHandler(sender:UITapGestureRecognizer) {
        _delegate?.message?(message: _message, image: image)
    }
}
