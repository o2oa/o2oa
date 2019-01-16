//
//  JCMessageTipsView.swift
//  JChat
//
//  Created by deng on 2017/4/26.
//  Copyright © 2017年 HXHG. All rights reserved.
//

import UIKit

open class JCMessageTipsView: UIView, JCMessageContentViewType {
    
    weak var delegate: JCMessageDelegate?
    
    public override init(frame: CGRect) {
        super.init(frame: frame)
        _commonInit()
    }
    public required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        _commonInit()
    }
    
    open func apply(_ message: JCMessageType) {
        self.message = message
        switch message.options.state {
        case .sending:
            errorInfoView.isHidden = true
            activityView.startAnimating()
        case .sendSucceed:
            errorInfoView.isHidden = true
            activityView.stopAnimating()
        case .sendError:
            activityView.stopAnimating()
            errorInfoView.isHidden = false
        default:
            activityView.stopAnimating()
        }
        if message.content is JCMessageImageContent {
            activityView.stopAnimating()
            activityView.isHidden = true
        }

        #if READ_VERSION
        if activityView.isHidden && errorInfoView.isHidden && message.options.alignment == .right {
            unreadCountTips.isHidden = false
            if message.unreadCount > 0 {
                unreadCountTips.isEnabled = true
                unreadCountTips.setTitleColor(UIColor(netHex: 0x2DD0CF), for: .normal)
                if message.targetType == .single {
                    unreadCountTips.isEnabled = false
                    unreadCountTips.setTitle("未读", for: .normal)
                } else {
                    unreadCountTips.setTitle("\(message.unreadCount)人未读", for: .normal)
                }
            } else {
                unreadCountTips.isEnabled = false
                unreadCountTips.setTitleColor(UIColor(netHex: 0x999999), for: .normal)
                if message.targetType == .single {
                    unreadCountTips.setTitle("已读", for: .normal)
                } else {
                    unreadCountTips.setTitle("全部已读", for: .normal)
                }
            }
        } else {
            unreadCountTips.isHidden = true
        }
        #endif
    }
    
    private lazy var activityView: UIActivityIndicatorView = {
        let activityView = UIActivityIndicatorView(frame: CGRect(x: 100 - 15, y: 5, width: 10, height: 10))
        activityView.style = .gray
        activityView.isUserInteractionEnabled = false
        return activityView
    }()
    private lazy var errorInfoView: UIImageView = {
        let image = UIImage.loadImage("com_icon_send_error")
        let errorInfoView = UIImageView(frame: CGRect(x: 100 - 21, y: 0, width: 21, height: 21))
        errorInfoView.isUserInteractionEnabled = true
        errorInfoView.image = image
        errorInfoView.isHidden = true
        return errorInfoView
    }()
    private lazy var unreadCountTips: UIButton = {
        let unreadCountTips = UIButton(frame: CGRect(x: 0, y: 0, width: 95, height: 21))
        unreadCountTips.addTarget(self, action: #selector(_clickUnreadCount), for: .touchUpInside)
        unreadCountTips.setTitle("未读", for: .normal)
        unreadCountTips.titleLabel?.font = UIFont.systemFont(ofSize: 12)
        unreadCountTips.setTitleColor(UIColor(netHex: 0x2DD0CF), for: .normal)
        unreadCountTips.isHidden = true
        unreadCountTips.contentHorizontalAlignment = .right
        return unreadCountTips
    }()
    private var message: JCMessageType!
    
    private func _commonInit() {
        addSubview(activityView)
        let tapGR = UITapGestureRecognizer(target: self, action: #selector(_tapHandler))
        errorInfoView.addGestureRecognizer(tapGR)
        addSubview(errorInfoView)
        #if READ_VERSION
        addSubview(unreadCountTips)
        #endif
    }
    
    func _clickUnreadCount() {
        delegate?.tapUnreadTips?(message: message)
    }
    
    func _tapHandler(sender: UITapGestureRecognizer) {
        delegate?.clickTips?(message: message)
    }
}
