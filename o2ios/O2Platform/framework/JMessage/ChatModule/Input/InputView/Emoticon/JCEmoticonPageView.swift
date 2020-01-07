//
//  JCEmoticonPageView.swift
//  JChat
//
//  Created by deng on 2017/3/9.
//  Copyright © 2017年 HXHG. All rights reserved.
//

import UIKit

internal class JCEmoticonPageView: UICollectionViewCell, UIGestureRecognizerDelegate {
    
    open weak var delegate: JCEmoticonDelegate?
    weak var previewer: JCEmoticonPreviewer?
    
    private var _activedIndexPath: IndexPath?
    
    private lazy var _backgroundLayer: CALayer = CALayer()
    private lazy var _backspaceButton: UIButton = UIButton(type: .system)
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        _init()
    }
    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        _init()
    }
    
    func setupBackspace() {
        _backspaceButton.isHidden = !(page?.itemType.isSmall ?? true)
        guard let page = self.page else {
            return
        }
        var nframe = CGRect(origin: .zero, size: page.itemSize)
        
        nframe.origin.x = page.vaildRect.maxX - nframe.width
        nframe.origin.y = page.vaildRect.maxY - nframe.height
        
        _backspaceButton.frame = nframe
        _backspaceButton.autoresizingMask = [.flexibleRightMargin, .flexibleBottomMargin]
        
        if _backspaceButton.superview == nil {
            addSubview(_backspaceButton)
        }
    }
    
    var page: JCEmoticonPage? {
        didSet {
            let newValue = self.page
            guard newValue !== oldValue else {
                return
            }
            newValue?.contents { contents in
                guard self.page === newValue else {
                    return
                }
                let block = { () -> Void in
                    self.contentView.layer.contents = contents
                    self.setupBackspace()
                }
                
                guard !Thread.current.isMainThread else {
                    block()
                    return
                }
                DispatchQueue.main.async(execute: block)
            }
        }
    }
    
    @objc func onPress(_ sender: UITapGestureRecognizer) {
        guard let idx = _index(at: sender.location(in: self)) else {
            
            return // no index
        }
        guard let emoticon = page?.emoticon(at: idx) else {
            return // outside
        }
        
        if delegate?.emoticon(shouldSelectFor: emoticon) ?? true {
            delegate?.emoticon(didSelectFor: emoticon)
        }
    }
    @objc func onLongPress(_ sender: UITapGestureRecognizer) {
        guard let page = page else {
            return
        }
        
        var idx: IndexPath?
        var rect: CGRect?
        var emoticon: JCEmoticon?
        
        let isbegin = sender.state == .began || sender.state == .possible
        let isend = sender.state == .cancelled || sender.state == .failed || sender.state == .ended
        
        if isend {
            if let idx = _activedIndexPath, let emoticon = page.emoticon(at: idx) {
                if delegate?.emoticon(shouldSelectFor: emoticon) ?? true {
                    delegate?.emoticon(didSelectFor: emoticon)
                }
            }
            idx = nil
        } else {
            idx = _index(at: sender.location(in: self))
        }
        
        if let idx = idx {
            rect = page.rect(at: idx)
            emoticon = page.emoticon(at: idx)
        }
        // 并没有找到任何可用的表情
        if emoticon == nil {
            idx = nil
        }
        // 检查没有改变
        guard _activedIndexPath != idx else {
            return
        }
        
        var canpreview = !isbegin && !isend
        
        if canpreview && !(delegate?.emoticon(shouldPreviewFor: emoticon) ?? true) {
            canpreview = false
            emoticon = nil
            idx = nil
        }
        
        _activedIndexPath = idx
        
        if let nframe = rect, page.itemType.isLarge {
            _backgroundLayer.frame = nframe
            _backgroundLayer.isHidden = false
            _backgroundLayer.removeAllAnimations()
        } else {
            _backgroundLayer.isHidden = true
        }
        
        previewer?.preview(emoticon, page.itemType, in: rect ?? .zero)
        
        if isbegin || canpreview {
            delegate?.emoticon(didPreviewFor: emoticon)
        }
    }
    @objc func onBackspace(_ sender: UIButton) {
        
        if delegate?.emoticon(shouldSelectFor: JCEmoticon.backspace) ?? true {
            delegate?.emoticon(didSelectFor: JCEmoticon.backspace)
        }
    }
    
    // MARK: UIGestureRecognizerDelegate
    override func gestureRecognizerShouldBegin(_ gestureRecognizer: UIGestureRecognizer) -> Bool {
        if let idx = _index(at: gestureRecognizer.location(in: self)), let emoticon = page?.emoticon(at: idx) {
            return delegate?.emoticon(shouldPreviewFor: emoticon) ?? true
        }
        return false
    }
    
    private func _index(at point: CGPoint) -> IndexPath? {
        guard let page = page else {
            return nil
        }
        let rect = page.visableRect
        guard rect.contains(point) else {
            return nil
        }
        let x = point.x - rect.minX
        let y = point.y - rect.minY
        
        let col = Int(x / (page.itemSize.width + page.minimumInteritemSpacing))
        let row = Int(y / (page.itemSize.height + page.minimumLineSpacing))
        
        return IndexPath(item: col, section: row)
    }
    
    private func _init() {
        
        _backgroundLayer.backgroundColor = UIColor(white: 0, alpha: 0.2).cgColor
        _backgroundLayer.masksToBounds = true
        _backgroundLayer.cornerRadius = 4
        
        _backspaceButton.tintColor = .gray
        _backspaceButton.setTitle("⌫", for: .normal)
        _backspaceButton.addTarget(self, action: #selector(onBackspace(_:)), for: .touchUpInside)
        
        let tapgr = UITapGestureRecognizer(target: self, action: #selector(onPress(_:)))
        let longtapgr = UILongPressGestureRecognizer(target: self, action: #selector(onLongPress(_:)))
        
        longtapgr.delegate = self
        longtapgr.minimumPressDuration = 0.25
        
        layer.addSublayer(_backgroundLayer)
        
        contentView.addGestureRecognizer(tapgr)
        contentView.addGestureRecognizer(longtapgr)
    }
}

