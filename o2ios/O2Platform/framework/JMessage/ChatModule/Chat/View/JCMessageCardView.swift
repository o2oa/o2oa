//
//  JCMessageCardView.swift
//  JChat
//
//  Created by deng on 10/04/2017.
//  Copyright © 2017 HXHG. All rights reserved.
//

import UIKit

open class JCMessageCardView: UILabel, JCMessageContentViewType {
    
    public override init(frame: CGRect) {
        super.init(frame: frame)
        _commonInit()
    }
    public required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        _commonInit()
    }
    
    open func apply(_ message: JCMessageType) {
        let isRight = message.options.alignment == .right
        text = message.name
        textAlignment = isRight ? .right : .left
    }
    
    private func _commonInit() {
        font = UIFont.systemFont(ofSize: 14)
        textColor = UIColor(netHex: 0xB3B3B3)
    }
}
