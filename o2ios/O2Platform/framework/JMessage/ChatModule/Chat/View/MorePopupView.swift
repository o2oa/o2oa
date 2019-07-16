//
//  MorePopupView.swift
//  JChat
//
//  Created by deng on 2017/8/15.
//  Copyright © 2017年 HXHG. All rights reserved.
//

import UIKit
import YHPopupView

@objc protocol MorePopupViewDelegate: NSObjectProtocol {
    @objc optional func popupView(view: MorePopupView, addFriend addButton: UIButton)
    @objc optional func popupView(view: MorePopupView, addGroup addButton: UIButton)
    @objc optional func popupView(view: MorePopupView, addSingle addButton: UIButton)
    @objc optional func popupView(view: MorePopupView, scanQRCode addButton: UIButton)
}

class MorePopupView: YHPopupView {
    
    weak var delegate: MorePopupViewDelegate?
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        _init()
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    private lazy var addFriend: UIButton = {
        var addFriend = UIButton()
        addFriend.titleLabel?.font = UIFont.systemFont(ofSize: 16)
        addFriend.addTarget(self, action: #selector(_addFriend), for: .touchUpInside)
        addFriend.setImage(UIImage.loadImage("com_icon_friend_add"), for: .normal)
        addFriend.setImage(UIImage.loadImage("com_icon_friend_add"), for: .highlighted)
        addFriend.setTitle("  添加朋友", for: .normal)
        return addFriend
    }()
    
    private lazy var addGroup: UIButton = {
        var addGroup = UIButton()
        addGroup.titleLabel?.font = UIFont.systemFont(ofSize: 16)
        addGroup.addTarget(self, action: #selector(_addGroup), for: .touchUpInside)
        addGroup.setImage(UIImage.loadImage("com_icon_conv_group"), for: .normal)
        addGroup.setImage(UIImage.loadImage("com_icon_conv_group"), for: .highlighted)
        addGroup.setTitle("  发起群聊", for: .normal)
        return addGroup
    }()
    
    private lazy var addSingle: UIButton = {
        var addSingle = UIButton()
        addSingle.titleLabel?.font = UIFont.systemFont(ofSize: 16)
        addSingle.addTarget(self, action: #selector(_addSingle), for: .touchUpInside)
        addSingle.setImage(UIImage.loadImage("com_icon_conv_single"), for: .normal)
        addSingle.setImage(UIImage.loadImage("com_icon_conv_single"), for: .highlighted)
        addSingle.setTitle("  发起单聊", for: .normal)
        return addSingle
    }()
    
    private lazy var scanQRCode: UIButton = {
        var scanQRCode = UIButton()
        scanQRCode.titleLabel?.font = UIFont.systemFont(ofSize: 16)
        scanQRCode.addTarget(self, action: #selector(_scanQRCode), for: .touchUpInside)
        scanQRCode.setImage(UIImage.loadImage("com_icon_scan"), for: .normal)
        scanQRCode.setImage(UIImage.loadImage("com_icon_scan"), for: .highlighted)
        scanQRCode.setTitle("  扫一扫    ", for: .normal)
        return scanQRCode
    }()
    
    private func _init() {
        let imageView = UIImageView(frame: CGRect(x: 0, y: 0, width: 145, height: 100))
        imageView.image = UIImage.loadImage("com_icon_selectList")
        addSubview(imageView)
        backgroundViewColor = .clear
        clickBlankSpaceDismiss = true
        self.theme_backgroundColor = ThemeColorPicker(keyPath: "Base.base_color")
        
        let height = Double((self.height - 5) / 2)
        let width = Double(self.width)
        let image = UIImage.createImage(color: UIColor(netHex: 0x999999), size: CGSize(width: 140, height: height))
        
        scanQRCode.frame = CGRect(x: 0.0, y: 5 + height * 3, width: width, height: height)
        scanQRCode.setBackgroundImage(image, for: .highlighted)
        
        addFriend.frame = CGRect(x: 0.0, y: 5 + height * 2, width: width, height: height)
        addFriend.setBackgroundImage(image, for: .highlighted)
        
        addGroup.frame = CGRect(x: 0.0, y: 5 + height, width: width, height: height)
        addGroup.setBackgroundImage(image, for: .highlighted)
        
        addSingle.frame = CGRect(x: 0.0, y: 5, width: width, height: height)
        addSingle.setBackgroundImage(image, for: .highlighted)
    
        addSubview(addSingle)
        addSubview(addGroup)
        // TODO:- 只有群聊和单聊，其它功能暂时不开放
//        addSubview(addFriend)
//        addSubview(scanQRCode)
    }
    
    @objc func _addFriend() {
        delegate?.popupView?(view: self, addFriend: addFriend)
    }
    
    @objc func _addGroup() {
        delegate?.popupView?(view: self, addGroup: addGroup)
    }
    
    @objc func _addSingle() {
        delegate?.popupView?(view: self, addSingle: addSingle)
    }
    
    @objc func _scanQRCode() {
        delegate?.popupView?(view: self, scanQRCode: scanQRCode)
    }
    
}
