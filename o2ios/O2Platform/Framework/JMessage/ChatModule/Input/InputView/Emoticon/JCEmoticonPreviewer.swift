//
//  JCEmoticonPreviewer.swift
//  JChat
//
//  Created by deng on 2017/3/9.
//  Copyright © 2017年 HXHG. All rights reserved.
//

import UIKit

internal class JCEmoticonPreviewer: UIView {

    func preview(_ emoticon: JCEmoticon?, _ itemType: JCEmoticonType, in rect: CGRect) {
        guard let emoticon = emoticon else {
            isHidden = true
            return
        }
        _type = itemType
        _popoverFrame = _popoverFrame(in: rect, and: _backgroundView.bounds(for: itemType))
        _presenterFrame = _presenterFrame(in: rect, and: _popoverFrame)
        
        frame = _popoverFrame
        isHidden = false
        
        _contentView.frame = _backgroundView.boundsOfContent(for: itemType)
        
        _backgroundView.popoverFrame = convert(_popoverFrame, from: superview)
        _backgroundView.presenterFrame = convert(_presenterFrame, from: superview)
        _backgroundView.updateBackgroundImages(with: itemType)
        _backgroundView.updateBackgroundLayouts()
        
        // update
        emoticon.show(in: _contentView)
    }
    
    private func _popoverFrame(in frame: CGRect, and bounds: CGRect) -> CGRect {
        var nframe = bounds
        
        nframe.origin.x = frame.midX + bounds.minX - nframe.width / 2
        nframe.origin.y = frame.minY + bounds.minY - nframe.height
        
        if let window = window, _type.isLarge {
            nframe.origin.x = max(nframe.minX, _inset.left)
            nframe.origin.x = min(nframe.minX, window.frame.maxX - bounds.width - _inset.right)
        }
        
        return nframe
    }
    private func _presenterFrame(in frame: CGRect, and bounds: CGRect) -> CGRect {
        return CGRect(x: frame.minX,
                      y: frame.minY - bounds.height,
                      width: frame.width,
                      height: frame.height + bounds.height)
    }
    
    private func _init() {
        
        addSubview(_backgroundView)
        addSubview(_contentView)
    }
    
    private var _type: JCEmoticonType = .small
    private var _inset: UIEdgeInsets = UIEdgeInsets(top: 0, left: 4, bottom: 0, right: 4)
    private var _popoverFrame: CGRect = .zero
    private var _presenterFrame: CGRect = .zero
    
    private lazy var _contentView: UIView = UIView()
    private lazy var _backgroundView: JCEmoticonBackgroundView = JCEmoticonBackgroundView()
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        _init()
    }
    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        _init()
    }

}
