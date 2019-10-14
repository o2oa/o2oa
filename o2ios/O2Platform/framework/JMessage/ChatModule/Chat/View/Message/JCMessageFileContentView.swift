//
//  JCMessageFileContentView.swift
//  JChat
//
//  Created by deng on 2017/7/20.
//  Copyright © 2017年 HXHG. All rights reserved.
//

import UIKit

class JCMessageFileContentView: UIView, JCMessageContentViewType {
    
    public override init(frame: CGRect) {
        super.init(frame: frame)
        _commonInit()
    }
    public required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        _commonInit()
    }
    
    open func apply(_ message: JCMessageType) {
        guard let content = message.content as? JCMessageFileContent else {
            return
        }
        _message = message
        _delegate = content.delegate
        _fileData = content.data
        _fileName = content.fileName
        _fileType = content.fileType
        _fileSize = content.fileSize
        
        _updateFileTypeIcon(_fileType)

        fileNameLabel.text = _fileName
        fileSizeLabel.text = _fileSize
        if _fileData != nil {
            fileStatusLabel.text = "己下载"
        } else {
            fileStatusLabel.text = "未下载"
        }
    }
    
    private func _updateFileTypeIcon(_ fileType: String?) {
        if let type = fileType {
            switch type.fileFormat() {
            case .document:
                imageView.image = UIImage.loadImage("com_icon_file_file")
            case .video:
                imageView.image = UIImage.loadImage("com_icon_file_video")
            case .photo:
                imageView.image = UIImage.loadImage("com_icon_file_photo")
            case .voice:
                imageView.image = UIImage.loadImage("com_icon_file_music")
            default:
                imageView.image = UIImage.loadImage("com_icon_file_other")
            }
        } else {
            imageView.image = UIImage.loadImage("com_icon_file_other")
        }
    }
    
    private weak var _delegate: JCMessageDelegate?
    
    private var _fileData: Data?
    private var _fileName: String?
    private var _fileType: String?
    private var _fileSize: String?
    private var _message: JCMessageType!
    
    private lazy var imageView: UIImageView = {
        let imageView = UIImageView(frame: CGRect(x: 12, y: 18, width: 40, height: 40))
        imageView.layer.cornerRadius = 2.5
        imageView.layer.masksToBounds = true
        return imageView
    }()
    private lazy var line: UILabel = {
        let line = UILabel()
        line.frame = CGRect(x: 12, y: 74, width: 176, height: 1)
        line.backgroundColor = UIColor(netHex: 0xE8E8E8)
        line.layer.backgroundColor = UIColor(netHex: 0xE8E8E8).cgColor
        return line
    }()
    private lazy var fileNameLabel: UILabel = {
        let fileNameLabel = UILabel()
        fileNameLabel.frame = CGRect(x: 68, y: 18, width: 120, height: 40)
        fileNameLabel.numberOfLines = 0
        fileNameLabel.font = UIFont.systemFont(ofSize: 16)
        fileNameLabel.textColor = UIColor(netHex: 0x5a5a5a)
        return fileNameLabel
    }()
    private lazy var fileSizeLabel: UILabel = {
        let fileSizeLabel = UILabel()
        fileSizeLabel.frame = CGRect(x: 12, y: 75, width: 85, height: 20)
        fileSizeLabel.font = UIFont.systemFont(ofSize: 12)
        fileSizeLabel.textColor = UIColor(netHex: 0x989898)
        return fileSizeLabel
    }()
    private lazy var fileStatusLabel: UILabel = {
        let fileStatusLabel = UILabel()
        fileStatusLabel.frame = CGRect(x: 103, y: 75, width: 85, height: 20)
        fileStatusLabel.textAlignment = .right
        fileStatusLabel.font = UIFont.systemFont(ofSize: 12)
        fileStatusLabel.textColor = UIColor(netHex: 0x989898)
        return fileStatusLabel
    }()
    
    private func _commonInit() {
        _tapGesture()

        addSubview(imageView)
        addSubview(fileNameLabel)
        addSubview(fileStatusLabel)
        addSubview(fileSizeLabel)
        addSubview(line)
    }
    
    func _tapGesture() {
        let tap = UITapGestureRecognizer(target: self, action: #selector(_clickCell))
        tap.numberOfTapsRequired = 1
        addGestureRecognizer(tap)
    }
    
    @objc func _clickCell() {
        _delegate?.message?(message: _message, fileData: _fileData, fileName: _fileName, fileType: _fileType)
    }
}
