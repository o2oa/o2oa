//
//  JCMessageImageCollectionViewCell.swift
//  JChatSwift
//
//  Created by oshumini on 16/6/7.
//  Copyright © 2016年 HXHG. All rights reserved.
//

import UIKit
import JMessage

@objc public protocol JCImageBrowserCellDelegate: NSObjectProtocol {
    @objc optional func singleTap()
    @objc optional func longTap(tableviewCell cell: JCMessageImageCollectionViewCell)
}

@objc(JCMessageImageCollectionViewCell)
public class JCMessageImageCollectionViewCell: UICollectionViewCell {
    
    weak var delegate: JCImageBrowserCellDelegate?
    
    @IBOutlet weak var messageImageContent: UIScrollView!
    var messageImage: UIImageView!
    
    override public func awakeFromNib() {
        super.awakeFromNib()
        messageImage = UIImageView()
        messageImage.contentMode = .scaleAspectFit
        messageImage.backgroundColor = UIColor.black
        messageImage.frame = UIScreen.main.bounds
        
        messageImageContent.addSubview(messageImage)
        messageImageContent.delegate = self
        messageImageContent.maximumZoomScale = 2.0
        messageImageContent.minimumZoomScale = 1.0
        messageImageContent.contentSize = messageImageContent.frame.size
        
        let singleTapGesture = UITapGestureRecognizer(target: self, action: #selector(singleTapImage(_:)))
        singleTapGesture.numberOfTapsRequired = 1
        addGestureRecognizer(singleTapGesture)
        
        let doubleTapGesture = UITapGestureRecognizer(target: self, action: #selector(doubleTapImage(_:)))
        doubleTapGesture.numberOfTapsRequired = 2
        addGestureRecognizer(doubleTapGesture)
        singleTapGesture.require(toFail: doubleTapGesture)
        
        let longTapGesture = UILongPressGestureRecognizer(target: self, action: #selector(longTapImage(_:)))
        addGestureRecognizer(longTapGesture)
    }
    
    @objc func singleTapImage(_ gestureRecognizer: UITapGestureRecognizer)  {
        delegate?.singleTap?()
    }
    
    @objc func doubleTapImage(_ gestureRecognizer: UITapGestureRecognizer) {
        adjustImageScale()
    }
    
    @objc func longTapImage(_ gestureRecognizer: UILongPressGestureRecognizer)  {
        if gestureRecognizer.state == .began {
            delegate?.longTap?(tableviewCell: self)
        }
    }
    
    func adjustImageScale() {
        if messageImageContent.zoomScale > 1.5 {
            messageImageContent.setZoomScale(1.0, animated: true)
        } else {
            messageImageContent.setZoomScale(2.0, animated: true)
        }
    }
    
    func setImage(image: UIImage) {
        messageImage.image = image
    }
    
    func setMessage(_ message: JMSGMessage) {
        guard let content = message.content as? JMSGImageContent else {
            return
        }
        content.thumbImageData { (data, msgId, error) in
            if msgId == message.msgId {
                if let data = data {
                    self.messageImage.image = UIImage(data: data)
                }
            }
            
            content.largeImageData(progress: nil, completionHandler: { (data, msgId, error) in
                if error == nil {
                    if msgId != message.msgId {
                        return
                    }
                    if let data = data {
                        self.messageImage.image = UIImage(data: data)
                    }
                }
            })
        }
    }
}

extension JCMessageImageCollectionViewCell:UIScrollViewDelegate {
    public func viewForZooming(in scrollView: UIScrollView) -> UIView? {
        return messageImage
    }
}
