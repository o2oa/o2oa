//
//  JCMessageTextContentView.swift
//  JChat
//
//  Created by deng on 2017/3/9.
//  Copyright © 2017年 HXHG. All rights reserved.
//

import UIKit

open class JCMessageTextContentView: KILabel, JCMessageContentViewType {

    public override init(frame: CGRect) {
        super.init(frame: frame)
        _commonInit()
    }
    public required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        _commonInit()
    }

    open override func canPerformAction(_ action: Selector, withSender sender: Any?) -> Bool {
        return super.canPerformAction(action, withSender: sender)
    }
    
    open func apply(_ message: JCMessageType) {
        guard let content = message.content as? JCMessageTextContent else {
            return
        }
        self.attributedText = content.text
        self.linkDetectionTypes = KILinkTypeOption.URL
        self.urlLinkTapHandler = { label, url, range in
            if let Url = URL(string: url) {
                if UIApplication.shared.canOpenURL(Url) {
                    UIApplication.shared.openURL(Url)
                } else {
                    let newUrl = URL(string: "https://" + url)
                    UIApplication.shared.openURL(newUrl!)
                }
            }
        }
    }
    
    private func _commonInit() {
        self.numberOfLines = 0
    }
}


