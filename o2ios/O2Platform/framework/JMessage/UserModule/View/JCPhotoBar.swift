//
//  JCPhotoBar.swift
//  JChat
//
//  Created by deng on 2017/3/31.
//  Copyright © 2017年 HXHG. All rights reserved.
//

import UIKit

@objc public protocol JCPhotoBarDelegate: NSObjectProtocol {
    @objc optional func photoBarDeleteImage(index: Int)
    @objc optional func photoBarClickImage(index: Int)
    @objc optional func photoBarAddImage()
}

class JCPhotoBar: UIView {
    
    weak var delegate: JCPhotoBarDelegate? {
        get {
            return item1.delegate
        }
        set {
            item1.delegate = newValue
            item2.delegate = newValue
            item3.delegate = newValue
            item4.delegate = newValue
        }
    }

    override init(frame: CGRect) {
        super.init(frame: frame)
        _init()
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    private let offset = (UIScreen.main.bounds.size.width - 30 - 65 * 4) / 3

    private lazy var item1: JCPhotoBarItem = JCPhotoBarItem(frame: CGRect(x: 15, y: 0, width: 65, height: 65))
    private lazy var item2: JCPhotoBarItem = JCPhotoBarItem(frame: CGRect(x: 15 + 65 + self.offset, y: 0, width: 65, height: 65))
    private lazy var item3: JCPhotoBarItem = JCPhotoBarItem(frame: CGRect(x: 16 + (65 + self.offset) * 2, y: 0, width: 65, height: 65))
    private lazy var item4: JCPhotoBarItem = JCPhotoBarItem(frame: CGRect(x: 15 + (65 + self.offset) * 3, y: 0, width: 65, height: 65))
    
    func bindData(_ images: [UIImage]) {
        switch images.count + 1 {
        case 1:
            item1.setAddImage()
            item2.isHidden = true
            item3.isHidden = true
            item4.isHidden = true
        case 2:
            item1.setImage(images[0])
            item2.setAddImage()
            item3.isHidden = true
            item4.isHidden = true
        case 3:
            item1.setImage(images[0])
            item2.setImage(images[1])
            item3.setAddImage()
            item4.isHidden = true
        case 4:
            item1.setImage(images[0])
            item2.setImage(images[1])
            item3.setImage(images[2])
            item4.setAddImage()
        default:
            item1.setImage(images[0])
            item2.setImage(images[1])
            item3.setImage(images[2])
            item4.setImage(images[3])
        }
    }

    private func _init() {
        
        item1.index = 0
        item2.index = 1
        item3.index = 2
        item4.index = 3
        addSubview(item1)
        addSubview(item2)
        addSubview(item3)
        addSubview(item4)
    }
}

class JCPhotoBarItem: UIView {
    
    weak var delegate: JCPhotoBarDelegate?
    var index = 0
    
    var isHiddenDelButton: Bool {
        get {
            return delButton.isHidden
        }
        set {
            delButton.isHidden = newValue
        }
    }

    override init(frame: CGRect) {
        super.init(frame: frame)
        _init()
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    private lazy var iconView: UIImageView = UIImageView()
    private lazy var delButton: UIButton = UIButton()
    
    func setAddImage() {
        self.isHidden = false
        iconView.image = UIImage.loadImage("com_icon_add_65")
        delButton.isHidden = true
    }
    
    func setImage(_ image: UIImage) {
        self.isHidden = false
        delButton.isHidden = false
        iconView.image = image
    }
    
    private func _init() {
        iconView.frame = CGRect(x: 0, y: 0, width: self.width, height: self.height)
        let tapGR = UITapGestureRecognizer(target: self, action: #selector(_tapHandler))
        iconView.addGestureRecognizer(tapGR)
        iconView.isUserInteractionEnabled = true
        addSubview(iconView)
        
        let bgImage = UIImage.loadImage("icon_icon_close")
        delButton.frame = CGRect(x: self.width - 20, y: 0, width: 20, height: 20)
        delButton.setBackgroundImage(bgImage, for: .normal)
        delButton.addTarget(self, action: #selector(_delImage(_ :)), for: .touchUpInside)
        addSubview(delButton)
    }
    
    func _tapHandler() {
        if isHiddenDelButton {
            delegate?.photoBarAddImage?()
            return
        }
        delegate?.photoBarClickImage?(index: index)
    }
    
    func _delImage(_ sender: UIButton) {
        delegate?.photoBarDeleteImage?(index: index)
    }
}
