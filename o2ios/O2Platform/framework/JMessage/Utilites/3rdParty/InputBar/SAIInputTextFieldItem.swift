//
//  SAIInputTextFieldItem.swift
//  SAIInputBar
//
//  Created by SAGESSE on 8/3/16.
//  Copyright © 2016-2017 SAGESSE. All rights reserved.
//

import UIKit

internal class SAIInputTextFieldItem: SAIInputItem {
    
    init(textView: UITextView, backgroundView: UIImageView) {
        super.init()
        
        _textView = textView
        _backgroundView = backgroundView
        
        _backgroundView.image = _SAInputDefaultTextFieldBackgroundImage
    }
    
    override var font: UIFont? {
        set {
            _cacheMinHeight = nil
            _textView.font = newValue
        }
        get {
            return _textView.font
        }
    }
    
    override var tintColor: UIColor? {
        set { return _textView.tintColor = newValue }
        get { return _textView.tintColor }
    }
    
    override var image: UIImage? {
        set { return _backgroundView.image = newValue }
        get { return _backgroundView.image }
    }
    
    var needsUpdateContent: Bool {
        let newValue = _textView.contentSize
        let oldValue = _cacheContentSize ?? _textView.contentSize
        
        if newValue.width != _textView.frame.width {
            return true
        }
        if newValue.height == oldValue.height {
            return false
        }
        if newValue.height <= _maxHeight {
            // 没有超出去
            return true
        }
        if oldValue.height < _maxHeight {
            // 虽然己经超出去了, 但还没到最大值呢
            return true
        }
        return false
    }
    var contentSize: CGSize {
        return size
    }
    
    override var size: CGSize {
        set {
            // don't set
        }
        get {
            if let size = _cacheSize {
                return size
            }
            let size = sizeThatFits()
            _cacheSize = size
            _cacheContentSize = _textView.contentSize
            return size
        }
    }
    
    func contentSizeChanged() {
        if needsUpdateContent {
            _cacheSize = nil
        }
        //self.setNeedsLayout()
    }
    
    func invalidateCache() {
        
        _cacheSize = nil
        _cacheContentSize = nil
    }
    
    func sizeThatFits() -> CGSize {
        
        var size = _textView.sizeThatFits(CGSize(width: _textView.bounds.width, height: CGFloat.greatestFiniteMagnitude))
        //size.height = min(max(size.height, _minHeight), _maxHeight)
        size.height = min(size.height, _maxHeight)
        return size
    }
    
    var _maxHeight: CGFloat = 106
    var _minHeight: CGFloat {
        if let height = _cacheMinHeight {
            return height
        }
        if let font = _textView.font {
            let edg = _textView.textContainerInset
            let mh = font.lineHeight + edg.top + edg.bottom
            _cacheMinHeight = mh
            return mh
        }
        return 0
    }
    
    var _cacheMinHeight: CGFloat?
    var _cacheSize: CGSize?
    var _cacheContentSize: CGSize?
    
    weak var _textView: UITextView!
    weak var _backgroundView: UIImageView!
}
