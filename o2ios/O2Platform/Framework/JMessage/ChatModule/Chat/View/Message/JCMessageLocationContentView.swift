//
//  JCMessageLocationContentView.swift
//  JChat
//
//  Created by deng on 2017/4/19.
//  Copyright © 2017年 HXHG. All rights reserved.
//

import UIKit

class JCMessageLocationContentView: UIImageView, JCMessageContentViewType {
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
        guard let content = message.content as? JCMessageLocationContent else {
            return
        }
        _message = message
        _delegate = content.delegate
        _address = content.address
        _lon = content.lon
        _lat = content.lat
        _addressLabel.text = content.address
    }
    
    private weak var _delegate: JCMessageDelegate?
    private var _message: JCMessageType!
    private var _address: String?
    private var _lon: Double?
    private var _lat: Double?
    private var _addressLabel = UILabel(frame: CGRect(x: 10, y: 0, width: 113, height: 40))
    private lazy var locationImage: UIImage? = UIImage.loadImage("location_address")
    
    private func _commonInit() {
        _addressLabel.font = UIFont.systemFont(ofSize: 13)
        _addressLabel.numberOfLines = 2
        addSubview(_addressLabel)
        isUserInteractionEnabled = true
        layer.cornerRadius = 2
        layer.masksToBounds = true
        image = locationImage
        _tapGesture()
    }
    
    func _tapGesture() {
        
        let tap = UITapGestureRecognizer(target: self, action: #selector(_clickCell))
        tap.numberOfTapsRequired = 1
        addGestureRecognizer(tap)
    }
    
    func _clickCell() {
        _delegate?.message?(message: _message, location: _address, lat: _lat ?? 0, lon: _lon ?? 0)
    }
}
