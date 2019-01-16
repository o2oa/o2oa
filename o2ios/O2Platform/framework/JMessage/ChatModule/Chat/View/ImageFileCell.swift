//
//  ImageFileCell.swift
//  JChat
//
//  Created by 邓永豪 on 2017/8/28.
//  Copyright © 2017年 HXHG. All rights reserved.
//

import UIKit

class ImageFileCell: UICollectionViewCell {
    
    var isEditMode: Bool {
        get {
            return !selectView.isHidden
        }
        set {
            selectView.isHidden = !newValue
        }
    }
    
    var isSelectImage: Bool {
        get {
            return isSelect
        }
        set {
            if newValue {
                selectView.image = UIImage.loadImage("com_icon_file_select")
            } else {
                selectView.image = UIImage.loadImage("com_icon_file_unselect")
            }
            isSelect = newValue
        }
    }
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        _init()
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    func bindDate(_ message: JMSGMessage) {
        if message.contentType == .image {
            let content = message.content as! JMSGImageContent
            content.largeImageData(progress: nil, completionHandler: { (data, msgId, error) in
                if msgId == message.msgId {
                    if let data = data {
                        let image = UIImage(data: data)
                        self.imageView.image = image
                    }
                }
            })
        }
        guard let content = message.content as? JMSGFileContent else {
            return
        }
        content.fileData { (data, msgId, error) in
            if msgId == message.msgId {
                if let data = data {
                    let image = UIImage(data: data)
                    self.imageView.image = image
                }
            }
        }
    }
    
    lazy var imageView: UIImageView = UIImageView()
    private var isSelect = false
    private lazy var selectView: UIImageView = UIImageView()

    private func _init(){
        imageView.frame = CGRect(x: 0, y: 0, width: self.width, height: self.height)
        imageView.contentMode = .scaleAspectFit
        imageView.clipsToBounds = true
        addSubview(imageView)
        
        selectView.isHidden = true
        selectView.frame = CGRect(x: self.width - 31, y: 5, width: 21, height: 21)
        selectView.image = UIImage.loadImage("com_icon_file_unselect")
        addSubview(selectView)
    }
}
