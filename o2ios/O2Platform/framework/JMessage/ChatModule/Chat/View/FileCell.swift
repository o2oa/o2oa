//
//  FileCell.swift
//  JChat
//
//  Created by 邓永豪 on 2017/8/28.
//  Copyright © 2017年 HXHG. All rights reserved.
//

import UIKit

class File {
    var fileIcon: UIImage
    var fileName: String
    var fileSize: String
    var summary: String
    
    init(_ fileIcon: UIImage, _ fileName: String, _ fileSize: String, _ summary: String) {
        self.fileName = fileName
        self.fileIcon = fileIcon
        self.fileSize = fileSize
        self.summary = summary
    }
}

class FileCell: JCTableViewCell {
    
    var isEditMode: Bool {
        get {
            return !selectView.isHidden
        }
        set {
            selectView.isHidden = !newValue
            contentView.removeConstraint(selectImageWidthConstraint)
            if newValue {
                selectImageWidthConstraint = _JCLayoutConstraintMake(selectView, .width, .equal, nil, .notAnAttribute, 21)
            } else {
                selectImageWidthConstraint = _JCLayoutConstraintMake(selectView, .width, .equal, nil, .notAnAttribute, 0)
            }
            contentView.addConstraint(selectImageWidthConstraint)
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

    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        _init()
    }
    
    override func awakeFromNib() {
        super.awakeFromNib()
        _init()
    }
    
    func bindData(_ file: File) {
        fileIcon.image = file.fileIcon
        fileName.text = file.fileName
        fileSize.text = file.fileSize
        summary.text = file.summary
    }
    
    private var selectImageWidthConstraint: NSLayoutConstraint!
    private lazy var selectView: UIImageView = UIImageView()
    private var isSelect = false
    private lazy var fileIcon: UIImageView = UIImageView()
    private lazy var fileName: UILabel = {
        let fileName = UILabel()
        fileName.textColor = UIColor(netHex: 0x2C2C2C)
        fileName.font = UIFont.systemFont(ofSize: 15)
        return fileName
    }()
    private lazy var fileSize: UILabel = {
        let fileSize = UILabel()
        fileSize.textColor = UIColor(netHex: 0x2C2C2C)
        fileSize.font = UIFont.systemFont(ofSize: 12)
        return fileSize
    }()
    private lazy var summary: UILabel = {
        let summary = UILabel()
        summary.textColor = UIColor(netHex: 0x999999)
        summary.font = UIFont.systemFont(ofSize: 12)
        return summary
    }()
    
    private func _init() {
        selectView.image = UIImage.loadImage("com_icon_file_unselect")
        
        contentView.addSubview(fileIcon)
        contentView.addSubview(fileName)
        contentView.addSubview(fileSize)
        contentView.addSubview(summary)
        contentView.addSubview(selectView)
        
        selectImageWidthConstraint = _JCLayoutConstraintMake(selectView, .width, .equal, nil, .notAnAttribute, 0)
        contentView.addConstraint(_JCLayoutConstraintMake(selectView, .left, .equal, contentView, .left, 17.5))
        contentView.addConstraint(_JCLayoutConstraintMake(selectView, .centerY, .equal, contentView, .centerY))
        contentView.addConstraint(selectImageWidthConstraint)
        contentView.addConstraint(_JCLayoutConstraintMake(selectView, .height, .equal, nil, .notAnAttribute, 21))
        
        contentView.addConstraint(_JCLayoutConstraintMake(fileIcon, .left, .equal, selectView, .right, 17.5))
        contentView.addConstraint(_JCLayoutConstraintMake(fileIcon, .centerY, .equal, contentView, .centerY))
        contentView.addConstraint(_JCLayoutConstraintMake(fileIcon, .width, .equal, nil, .notAnAttribute, 50))
        contentView.addConstraint(_JCLayoutConstraintMake(fileIcon, .height, .equal, nil, .notAnAttribute, 50))
        
        contentView.addConstraint(_JCLayoutConstraintMake(fileName, .left, .equal, fileIcon, .right, 12.5))
        contentView.addConstraint(_JCLayoutConstraintMake(fileName, .top, .equal, contentView, .top, 14.5))
        contentView.addConstraint(_JCLayoutConstraintMake(fileName, .right, .equal, contentView, .right, -17.5))
        contentView.addConstraint(_JCLayoutConstraintMake(fileName, .height, .equal, nil, .notAnAttribute, 21))
        
        contentView.addConstraint(_JCLayoutConstraintMake(fileSize, .right, .equal, fileName, .right))
        contentView.addConstraint(_JCLayoutConstraintMake(fileSize, .left, .equal, fileName, .left))
        contentView.addConstraint(_JCLayoutConstraintMake(fileSize, .top, .equal, fileName, .bottom))
        contentView.addConstraint(_JCLayoutConstraintMake(fileSize, .height, .equal, nil, .notAnAttribute, 16.5))
        
        contentView.addConstraint(_JCLayoutConstraintMake(summary, .left, .equal, fileSize, .left))
        contentView.addConstraint(_JCLayoutConstraintMake(summary, .right, .equal, fileSize, .right))
        contentView.addConstraint(_JCLayoutConstraintMake(summary, .top, .equal, fileSize, .bottom))
        contentView.addConstraint(_JCLayoutConstraintMake(summary, .height, .equal, fileSize, .height))
    }
}
