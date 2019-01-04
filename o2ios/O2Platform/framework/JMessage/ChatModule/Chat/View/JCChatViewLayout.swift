//
//  JCChatViewLayout.swift
//  JChat
//
//  Created by deng on 2017/2/28.
//  Copyright © 2017年 HXHG. All rights reserved.
//

import UIKit

@objc open class JCChatViewLayout: UICollectionViewFlowLayout {
    
    public override init() {
        super.init()
        _commonInit()
    }
    public required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        _commonInit()
    }
    
    internal weak var _chatView: JCChatView?
    
    open override class var layoutAttributesClass: AnyClass {
        return JCChatViewLayoutAttributes.self
    }
    
    open override func layoutAttributesForItem(at indexPath: IndexPath) -> UICollectionViewLayoutAttributes? {
        let attributes = super.layoutAttributesForItem(at: indexPath)
        if let attributes = attributes as? JCChatViewLayoutAttributes, attributes.info == nil {
            attributes.info = layoutAttributesInfoForItem(at: indexPath)
        }
        return attributes
    }
    open override func layoutAttributesForElements(in rect: CGRect) -> [UICollectionViewLayoutAttributes]? {
        let arr = super.layoutAttributesForElements(in: rect)
        arr?.forEach({
            guard let attributes = $0 as? JCChatViewLayoutAttributes, attributes.info == nil else {
                return
            }
            attributes.info = layoutAttributesInfoForItem(at: attributes.indexPath)
        })
        return arr
    }
    
    open func layoutAttributesInfoForItem(at indexPath: IndexPath) -> JCChatViewLayoutAttributesInfo? {
        guard let collectionView = collectionView, let _ = collectionView.delegate as? JCChatViewLayoutDelegate, let message = _message(at: indexPath) else {
            return nil
        }
        let size = CGSize(width: collectionView.frame.width, height: .greatestFiniteMagnitude)
        if let info = _allLayoutAttributesInfo[message.identifier] {
            // 这不是合理的做法
            if !message.updateSizeIfNeeded {
                return info
            }
        }
        let options = message.options
        
        var allRect: CGRect = .zero
        var allBoxRect: CGRect = .zero
        
        var cardRect: CGRect = .zero
        var cardBoxRect: CGRect = .zero
        
        var avatarRect: CGRect = .zero
        var avatarBoxRect: CGRect = .zero
        
        var bubbleRect: CGRect = .zero
        var bubbleBoxRect: CGRect = .zero
        
        var contentRect: CGRect = .zero
        var contentBoxRect: CGRect = .zero
        
        var tipsRect: CGRect = .zero
        var tipsBoxRect: CGRect = .zero
        
        // 计算的时候以左对齐为基准
        
        // +---------------------------------------+ r0
        // |+---------------------------------+ r1 |
        // ||+---+ <NAME>                     |    |
        // ||| A | +---------------------\ r4 |    |
        // ||+---+ |+---------------+ r5 |    |    |
        // ||      ||    CONTENT    |    |    |    |
        // ||      |+---------------+    |    |    |
        // ||      \---------------------/    |    |  +---+ r6
        // |+---------------------------------+  <-|- | ! |
        // +---------------------------------------+  +---+
        
        let edg0 = _inset(with: options.style, for: .all)
        var r0 = CGRect(x: 0, y: 0, width: size.width, height: .greatestFiniteMagnitude)
        var r1 = r0.inset(by: edg0)
        
        var x1 = r1.minX
        var y1 = r1.minY
        var x2 = r1.maxX
        var y2 = r1.maxY
        
        if options.showsAvatar {
            let edg = _inset(with: options.style, for: .avatar)
            let size = _size(with: options.style, for: .avatar)
            
            let box = CGRect(x: x1, y: y1, width: edg.left + size.width + edg.right, height: edg.top + size.height + edg.bottom)
            let rect = box.inset(by: edg)
            
            avatarRect = rect
            avatarBoxRect = box
            
            x1 = box.maxX
        }
        
        if options.showsCard {
            let edg = _inset(with: options.style, for: .card)
            let size = _size(with: options.style, for: .card)
            
            let box = CGRect(x: x1, y: y1, width: x2 - x1, height: edg.top + size.height + edg.bottom)
            let rect = box.inset(by: edg)
            
            cardRect = rect
            cardBoxRect = box
            
            y1 = box.maxY
        }

        if options.showsBubble {
            let edg = _inset(with: options.style, for: .bubble)
            
            let box = CGRect(x: x1, y: y1, width: x2 - x1, height: y2 - y1)
            let rect = box.inset(by: edg)
            
            bubbleRect = rect
            bubbleBoxRect = box
            
            x1 = rect.minX
            x2 = rect.maxX
            y1 = rect.minY
            y2 = rect.maxY
        }

        if true {
            let edg0 = _inset(with: options.style, for: .content)
            let edg1 = message.content.layoutMargins
            //
            let edg = UIEdgeInsets(top: edg0.top + edg1.top, left: edg0.left + edg1.left, bottom: edg0.bottom + edg1.bottom, right: edg0.right + edg1.right)
            
            var box = CGRect(x: x1, y: y1, width: x2 - x1, height: y2 - y1)
            var rect = box.inset(by: edg)
            
            // calc content size
            let size = message.content.sizeThatFits(rect.size)
            
            // restore offset
            box.size.width = edg.left + size.width + edg.right
            box.size.height = edg.top + size.height + edg.bottom
            rect.size.width = size.width
            rect.size.height = size.height
            
            contentRect = rect
            contentBoxRect = box
            
            x1 = box.maxX
            y1 = box.maxY
        }

        if options.showsBubble {
            let edg = _inset(with: options.style, for: .bubble)
            
            bubbleRect.size.width = contentBoxRect.width
            bubbleRect.size.height = contentBoxRect.height
            
            bubbleBoxRect.size.width = edg.left + contentBoxRect.width + edg.right
            bubbleBoxRect.size.height = edg.top + contentBoxRect.height + edg.bottom
        }
        
        if options.showsTips {
            let edg = _inset(with: options.style, for: .tips)
            let size = _size(with: options.style, for: .tips)
            
            let box = CGRect(x: x1 + 3, y: y1 - size.height - edg0.bottom, width: edg.left + size.width + edg.right, height: edg.top + size.height + edg.bottom)
            let rect = box.inset(by: edg)
            
            tipsRect = rect
            tipsBoxRect = box
            
            x1 = box.maxX
        }
        
        // adjust
        r1.size.width = x1 - r1.minX
        r1.size.height = y1 - r1.minY
        r0.size.width = x1
        r0.size.height = y1 + edg0.bottom
        
        allRect = r1
        allBoxRect = r0
        
        // algin
        switch options.alignment {
        case .right:
            // to right
            allRect.origin.x = size.width - allRect.maxX
            allBoxRect.origin.x = size.width - allBoxRect.maxX
            
            cardRect.origin.x = size.width - cardRect.maxX
            cardBoxRect.origin.x = size.width - cardBoxRect.maxX
            
            avatarRect.origin.x = size.width - avatarRect.maxX
            avatarBoxRect.origin.x = size.width - avatarBoxRect.maxX
            
            bubbleRect.origin.x = size.width - bubbleRect.maxX
            bubbleBoxRect.origin.x = size.width - bubbleBoxRect.maxX
            
            contentRect.origin.x = size.width - contentRect.maxX
            contentBoxRect.origin.x = size.width - contentBoxRect.maxX
            
            tipsRect.origin.x = size.width - tipsRect.maxX
            tipsBoxRect.origin.x = size.width - tipsBoxRect.maxX
            
        case .center:
            allRect.origin.x = (size.width - allRect.width) / 2
            allBoxRect.origin.x = (size.width - allBoxRect.width) / 2
            
            bubbleRect.origin.x = (size.width - bubbleRect.width) / 2
            bubbleBoxRect.origin.x = (size.width - bubbleBoxRect.width) / 2
            
            contentRect.origin.x = (size.width - contentRect.width) / 2
            contentBoxRect.origin.x = (size.width - contentBoxRect.width) / 2
            
        case .left:
            break
        }
        // save
        let rects: [JCChatViewLayoutItem: CGRect] = [
            .all: allRect,
            .card: cardRect,
            .avatar: avatarRect,
            .bubble: bubbleRect,
            .content: contentRect,
            .tips: tipsRect
        ]
        let boxRects: [JCChatViewLayoutItem: CGRect] = [
            .all: allBoxRect,
            .card: cardBoxRect,
            .avatar: avatarBoxRect,
            .bubble: bubbleBoxRect,
            .content: contentBoxRect,
            .tips: tipsBoxRect
        ]
        let info = JCChatViewLayoutAttributesInfo(message: message, size: size, rects: rects, boxRects: boxRects)
        _allLayoutAttributesInfo[message.identifier] = info
        return info
    }
    
    private func _size(with style: JCMessageStyle, for item: JCChatViewLayoutItem) -> CGSize {
        let key = "\(style.rawValue)-\(item.rawValue)"
        if let size = _cachedAllLayoutSize[key] {
            return size // hit cache
        }
        var size: CGSize?
        if let collectionView = collectionView, let delegate = collectionView.delegate as? JCChatViewLayoutDelegate {
            switch item {
            case .all: size = .zero
            case .card: size = delegate.collectionView?(collectionView, layout: self, sizeForItemCardOf: style)
            case .avatar: size = delegate.collectionView?(collectionView, layout: self, sizeForItemAvatarOf: style)
            case .bubble: size = .zero
            case .content: size = .zero
            case .tips: size = delegate.collectionView?(collectionView, layout: self, sizeForItemTipsOf: style)
            }
        }
        _cachedAllLayoutSize[key] = size ?? .zero
        return size ?? .zero
    }
    private func _inset(with style: JCMessageStyle, for item: JCChatViewLayoutItem) -> UIEdgeInsets {
        let key = "\(style.rawValue)-\(item.rawValue)"
        if let edg = _cachedAllLayoutInset[key] {
            return edg // hit cache
        }
        var edg: UIEdgeInsets?
        if let collectionView = collectionView, let delegate = collectionView.delegate as? JCChatViewLayoutDelegate {
            switch item {
            case .all: edg = delegate.collectionView?(collectionView, layout: self, insetForItemOf: style)
            case .card: edg = delegate.collectionView?(collectionView, layout: self, insetForItemCardOf: style)
            case .tips: edg = delegate.collectionView?(collectionView, layout: self, insetForItemTipsOf: style)
            case .avatar: edg = delegate.collectionView?(collectionView, layout: self, insetForItemAvatarOf: style)
            case .bubble: edg = delegate.collectionView?(collectionView, layout: self, insetForItemBubbleOf: style)
            case .content: edg = delegate.collectionView?(collectionView, layout: self, insetForItemContentOf: style)
            }
        }
        _cachedAllLayoutInset[key] = edg ?? .zero
        return edg ?? .zero
    }
    private func _message(at indexPath: IndexPath) -> JCMessageType? {
        guard let collectionView = collectionView, let delegate = collectionView.delegate as? JCChatViewLayoutDelegate else {
            return nil
        }
        return delegate.collectionView(collectionView, layout: self, itemAt: indexPath)
    }
    
    private func _commonInit() {
        minimumLineSpacing = 0
        minimumInteritemSpacing = 0
    }
    
    private lazy var _cachedAllLayoutSize: [String: CGSize] = [:]
    private lazy var _cachedAllLayoutInset: [String: UIEdgeInsets] = [:]
    
    private lazy var _allLayoutAttributesInfo: [UUID: JCChatViewLayoutAttributesInfo] = [:]
}

@objc public protocol JCChatViewLayoutDelegate: UICollectionViewDelegateFlowLayout {
    
    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, itemAt indexPath: IndexPath) -> JCMessageType
    
    @objc optional func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, sizeForItemCardOf style: JCMessageStyle) -> CGSize
    @objc optional func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, sizeForItemTipsOf style: JCMessageStyle) -> CGSize
    @objc optional func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, sizeForItemAvatarOf style: JCMessageStyle) -> CGSize
    
    @objc optional func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, insetForItemOf style: JCMessageStyle) -> UIEdgeInsets
    @objc optional func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, insetForItemCardOf style: JCMessageStyle) -> UIEdgeInsets
    @objc optional func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, insetForItemTipsOf style: JCMessageStyle) -> UIEdgeInsets
    @objc optional func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, insetForItemAvatarOf style: JCMessageStyle) -> UIEdgeInsets
    @objc optional func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, insetForItemBubbleOf style: JCMessageStyle) -> UIEdgeInsets
    @objc optional func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, insetForItemContentOf style: JCMessageStyle) -> UIEdgeInsets
}
