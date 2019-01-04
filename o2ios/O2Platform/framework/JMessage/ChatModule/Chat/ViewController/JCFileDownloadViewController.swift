//
//  JCFileDownloadViewController.swift
//  JChat
//
//  Created by deng on 2017/7/24.
//  Copyright © 2017年 HXHG. All rights reserved.
//

import UIKit

class JCFileDownloadViewController: UIViewController {
    
    var message: JMSGMessage!
    var fileSize: String?

    override func viewDidLoad() {
        super.viewDidLoad()
        _init()
    }

    private lazy var _imageView: UIImageView = {
        var _imageView = UIImageView()
        _imageView.image = UIImage.loadImage("com_icon_file_file_75")
        return _imageView
    }()
    
    private lazy var _tipsLabel: UILabel = {
        var _tipsLabel = UILabel()
        _tipsLabel.font = UIFont.systemFont(ofSize: 12)
        _tipsLabel.textAlignment = .center
        _tipsLabel.textColor = UIColor(netHex: 0x999999)
        _tipsLabel.text = "该文件不支持预览，请下载原文件查看"
        return _tipsLabel
    }()
    
    private lazy var _downloadButton: UIButton = {
        var _downloadButton = UIButton()
        _downloadButton.setBackgroundImage(UIImage.createImage(color: UIColor(netHex: 0x2ECFCF), size: CGSize(width: 225, height: 40)), for: .normal)
        _downloadButton.addTarget(self, action: #selector(_downloadFile), for: .touchUpInside)
        _downloadButton.setTitle("下载(\(self.fileSize ?? "未知大小"))", for: .normal)
        return _downloadButton
    }()
    
    private lazy var _openButton: UIButton = {
        var _openButton = UIButton()
        _openButton.setBackgroundImage(UIImage.createImage(color: UIColor(netHex: 0x2ECFCF), size: CGSize(width: 225, height: 40)), for: .normal)
        _openButton.addTarget(self, action: #selector(_openFile), for: .touchUpInside)
        _openButton.setTitle("打开文件", for: .normal)
        _openButton.isHidden = true
        return _openButton
    }()
    
    private lazy var _fileNameLabel: UILabel = {
        var _fileNameLabel = UILabel()
        _fileNameLabel.font = UIFont.systemFont(ofSize: 16)
        _fileNameLabel.numberOfLines = 0
        _fileNameLabel.textAlignment = .center
        _fileNameLabel.text = self.title
        return _fileNameLabel
    }()
    
    private lazy var _progressView: UIProgressView = {
        var _progressView = UIProgressView(frame: .zero)
        _progressView.backgroundColor = UIColor(netHex: 0x72D635)
        _progressView.isHidden = true
        return _progressView
    }()
    
    private var _fileData: Data!
    private lazy var documentInteractionController = UIDocumentInteractionController()
    
    private func _init() {
        view.backgroundColor = UIColor(netHex: 0xE8EDF3)
        
        documentInteractionController.delegate = self
        
        view.addSubview(_imageView)
        view.addSubview(_fileNameLabel)
        view.addSubview(_tipsLabel)
        view.addSubview(_downloadButton)
        view.addSubview(_openButton)
        
        view.addConstraint(_JCLayoutConstraintMake(_imageView, .top, .equal, view, .top, 98 + 64))
        view.addConstraint(_JCLayoutConstraintMake(_imageView, .centerX, .equal, view, .centerX))
        view.addConstraint(_JCLayoutConstraintMake(_imageView, .width, .equal, nil, .notAnAttribute, 75))
        view.addConstraint(_JCLayoutConstraintMake(_imageView, .height, .equal, nil, .notAnAttribute, 75))
        
        view.addConstraint(_JCLayoutConstraintMake(_fileNameLabel, .top, .equal, _imageView, .bottom, 14))
        view.addConstraint(_JCLayoutConstraintMake(_fileNameLabel, .centerX, .equal, view, .centerX))
        view.addConstraint(_JCLayoutConstraintMake(_fileNameLabel, .width, .equal, nil, .notAnAttribute, 225))
        view.addConstraint(_JCLayoutConstraintMake(_fileNameLabel, .height, .equal, nil, .notAnAttribute, 45))
        
        view.addConstraint(_JCLayoutConstraintMake(_tipsLabel, .top, .equal, _fileNameLabel, .bottom, 14))
        view.addConstraint(_JCLayoutConstraintMake(_tipsLabel, .centerX, .equal, view, .centerX))
        view.addConstraint(_JCLayoutConstraintMake(_tipsLabel, .width, .equal, nil, .notAnAttribute, 225))
        view.addConstraint(_JCLayoutConstraintMake(_tipsLabel, .height, .equal, nil, .notAnAttribute, 7))

        view.addConstraint(_JCLayoutConstraintMake(_downloadButton, .top, .equal, _tipsLabel, .bottom, 30))
        view.addConstraint(_JCLayoutConstraintMake(_downloadButton, .centerX, .equal, view, .centerX))
        view.addConstraint(_JCLayoutConstraintMake(_downloadButton, .width, .equal, nil, .notAnAttribute, 225))
        view.addConstraint(_JCLayoutConstraintMake(_downloadButton, .height, .equal, nil, .notAnAttribute, 40))
        
        view.addConstraint(_JCLayoutConstraintMake(_openButton, .top, .equal, _tipsLabel, .bottom, 30))
        view.addConstraint(_JCLayoutConstraintMake(_openButton, .centerX, .equal, view, .centerX))
        view.addConstraint(_JCLayoutConstraintMake(_openButton, .width, .equal, nil, .notAnAttribute, 225))
        view.addConstraint(_JCLayoutConstraintMake(_openButton, .height, .equal, nil, .notAnAttribute, 40))
    }
    
    @objc func _openFile() {
        if let fileType = message.ex.fileType {
            let content = message.content as! JMSGFileContent
            switch fileType.fileFormat() {
            case .document:
                let vc = JCDocumentViewController()
                vc.title = self.title
                vc.fileData = _fileData
                vc.filePath = content.originMediaLocalPath
                vc.fileType = fileType
                navigationController?.pushViewController(vc, animated: true)
            case .video, .voice:
                let url = URL(fileURLWithPath: content.originMediaLocalPath ?? "")
                try! JCVideoManager.playVideo(data: Data(contentsOf: url), fileType, currentViewController: self)
            case .photo:
                let browserImageVC = JCImageBrowserViewController()
                let image = UIImage(contentsOfFile: content.originMediaLocalPath ?? "")
                browserImageVC.imageArr = [image!]
                browserImageVC.imgCurrentIndex = 0
                present(browserImageVC, animated: true) {}
            default:
                let url = URL(fileURLWithPath: content.originMediaLocalPath ?? "")
                documentInteractionController.url = url
                documentInteractionController.presentOptionsMenu(from: .zero, in: view, animated: true)
            }
        }
    }
    
    @objc func _downloadFile() {
        let content = message.content as! JMSGFileContent
        MBProgressHUD_JChat.showMessage(message: "下载中", toView: view)
        content.fileData { (data, id, error) in
            MBProgressHUD_JChat.hide(forView: self.view, animated: true)
            if error == nil {
                self._openButton.isHidden = false
                self._downloadButton.isHidden = true
                self._fileData = data
                NotificationCenter.default.post(name: NSNotification.Name(rawValue: kUpdateFileMessage), object: nil, userInfo: [kUpdateFileMessage : self.message.msgId])
            } else {
                MBProgressHUD_JChat.show(text: "下载失败", view: self.view)
            }
        }
    }

}

extension JCFileDownloadViewController: UIDocumentInteractionControllerDelegate {
    func documentInteractionControllerViewControllerForPreview(_ controller: UIDocumentInteractionController) -> UIViewController {
        return self
    }
    func documentInteractionControllerViewForPreview(_ controller: UIDocumentInteractionController) -> UIView? {
        return view
    }
    
    func documentInteractionControllerRectForPreview(_ controller: UIDocumentInteractionController) -> CGRect {
        return view.frame
    }
}
