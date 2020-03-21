//
//  JCChatViewCell.swift
//  JChat
//
//  Created by deng on 2017/2/28.
//  Copyright © 2017年 HXHG. All rights reserved.
//

import UIKit
import CocoaLumberjack

open class JCChatViewCell: UICollectionViewCell, UIGestureRecognizerDelegate {
    
    weak var delegate: JCMessageDelegate?
    
    public override init(frame: CGRect) {
        super.init(frame: frame)
        _commonInit()
    }
    
    public required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        _commonInit()
    }
    
    deinit {
        guard let observer = _menuNotifyObserver else{
            return
        }
        NotificationCenter.default.removeObserver(observer)
    }
    
    func updateView() {
        guard let message = _layoutAttributes?.message else {
            return
        }
        _tipsView?.apply(message)
        let tipsView = _tipsView as? JCMessageTipsView
        if tipsView != nil {
            tipsView?.delegate = self.delegate
        }
        
        let avatarView = _avatarView as? JCMessageAvatarView
        if avatarView != nil {
            avatarView?.delegate = self.delegate
        }
        _contentView?.apply(message)
    }
    
    
    open override func apply(_ layoutAttributes: UICollectionViewLayoutAttributes) {
        
        super.apply(layoutAttributes)
        guard let _ = layoutAttributes as? JCChatViewLayoutAttributes else {
            return
        }
        _updateViews()
        _updateViewLayouts()
        _updateViewValues()
    }

    open override func point(inside point: CGPoint, with event: UIEvent?) -> Bool {
        guard let rect = _layoutAttributes?.info?.layoutedBoxRect(with: .all) else {
            return false
        }
        return rect.contains(point)
    }
    
    open class var cardViewClass: JCMessageContentViewType.Type {
        return JCMessageCardView.self
    }
    
    open class var tipsViewClass: JCMessageContentViewType.Type {
        return JCMessageTipsView.self
    }
    
    open class var avatarViewClass: JCMessageContentViewType.Type {
        return JCMessageAvatarView.self
    }
    
    
    private lazy var send_nor = UIImage.loadImage("chat_bubble_send_nor")!.resizableImage(withCapInsets: UIEdgeInsets(top: 25, left: 25, bottom: 25, right: 25))
    private lazy var send_press = UIImage.loadImage("chat_bubble_send_press")!.resizableImage(withCapInsets: UIEdgeInsets(top: 25, left: 25, bottom: 25, right: 25))
    
    private lazy var recive_nor = UIImage.loadImage("chat_bubble_recive_nor")!.resizableImage(withCapInsets: UIEdgeInsets(top: 25, left: 25, bottom: 25, right: 25))
    private lazy var recive_press = UIImage.loadImage("chat_bubble_recive_press")!.resizableImage(withCapInsets: UIEdgeInsets(top: 25, left: 25, bottom: 25, right: 25))
    
    private func _updateViews() {

        guard let message = _layoutAttributes?.message else {
            return
        }
        let options = message.options

        if options.showsBubble {
            if _bubbleView == nil {
                _bubbleView = UIImageView()
            }
            if let view = _bubbleView, view.superview == nil {
                insertSubview(view, belowSubview: contentView)
            }
        } else {
            if let view = _bubbleView {
                view.removeFromSuperview()
            }
            _bubbleView = nil
        }
        
        if options.showsCard {
            if _cardView == nil {
                _cardView = type(of: self).cardViewClass._init()
            }
            if let view = _cardView as? UIView, view.superview == nil {
                contentView.addSubview(view)
            }
        } else {
            if let view = _cardView as? UIView {
                view.removeFromSuperview()
            }
            _cardView = nil
        }
        
        if options.showsTips {
            if _tipsView == nil {
                _tipsView = type(of: self).tipsViewClass._init()
            }
            if let view = _tipsView as? UIView, view.superview == nil {
                contentView.addSubview(view)
            }
        } else {
            if let view = _tipsView as? UIView {
                view.removeFromSuperview()
            }
            _tipsView = nil
        }
        
        
        if options.showsAvatar {
            if _avatarView == nil {
                _avatarView = type(of: self).avatarViewClass._init()
            }
            if let view = _avatarView as? UIView, view.superview == nil {
                contentView.addSubview(view)
            }
        } else {
            if let view = _avatarView as? UIView {
                view.removeFromSuperview()
            }
            _avatarView = nil
        }
        
        if _contentView == nil {
            // create
            _contentView = type(of: message.content).viewType._init()
            // move
            if let view = _contentView as? UIView, view.superview == nil {
                contentView.addSubview(view)
            }
        }
    }
    private func _updateViewLayouts() {
        // prepare
        guard let layoutInfo = _layoutAttributes?.info else {
            return
        }
        // update bubble view layout
        if let view = _bubbleView {
            view.frame = layoutInfo.layoutedRect(with: .bubble)
        }
        // update visit card view layout
        if let view = _cardView as? UIView {
            view.frame = layoutInfo.layoutedRect(with: .card)
        }
        if let view = _tipsView as? UIView {
            let frame = layoutInfo.layoutedRect(with: .tips)
            view.frame = frame
        }
        // update avatar view layout
        if let view = _avatarView as? UIView {
            view.frame = layoutInfo.layoutedRect(with: .avatar)
        }
        
        // update content view layout
        if let view = _contentView as? UIView {
            view.frame = layoutInfo.layoutedRect(with: .content)
        }
    }
    private func _updateViewValues() {
        guard let message = _layoutAttributes?.message else {
            return
        }
        let options = message.options
        
        _cardView?.apply(message)
        _tipsView?.apply(message)
        _avatarView?.apply(message)
        let avatarView = _avatarView as? JCMessageAvatarView
        if avatarView != nil {
            avatarView?.delegate = self.delegate
            let user = message.sender?.username
            DDLogDebug("更新头像，发送者头像：\(user ?? "")")
            let urlstr = AppDelegate.o2Collect.generateURLWithAppContextKey(ContactContext.contactsContextKeyV2, query: ContactContext.personIconByNameQueryV2, parameter: ["##name##":user as AnyObject])
            let url = URL(string: urlstr!)
            avatarView?.hnk_setImageFromURL(url!)
        }
        _contentView?.apply(message)
        
        if let view = _bubbleView {
            switch options.alignment {
            case .left:
                view.image = recive_nor
                view.highlightedImage = recive_press
                
            case .right:
                view.image = send_nor
                view.highlightedImage = send_press
                
            case .center:
                break
            }
        }
    }
    
    open override func gestureRecognizerShouldBegin(_ gestureRecognizer: UIGestureRecognizer) -> Bool {
        guard gestureRecognizer == _menuGesture else {
            return super.gestureRecognizerShouldBegin(gestureRecognizer)
        }
        guard let rect = _layoutAttributes?.info?.layoutedBoxRect(with: .content) else {
            return false
        }
        guard rect.contains(gestureRecognizer.location(in: contentView)) else {
            return false
        }
        return true
    }
    
    func copyMessage(_ sender: Any) {}
    func deleteMessage(_ sender: Any) {}
    func forwardMessage(_ sender: Any) {}
    func withdrawMessage(_ sender: Any) {}

    private dynamic func _handleMenuGesture(_ sender: UILongPressGestureRecognizer) {
        guard sender.state == .began else {
            return
        }
        guard let view = _contentView as? UIView,
            let content = _layoutAttributes?.message?.content,
            let info = _layoutAttributes?.info else {
                return
        }
        
        let rect = info.layoutedRect(with: .content).inset(by: -content.layoutMargins)
        let menuController = UIMenuController.shared
        
        // set responder
        NSClassFromString("UICalloutBar")?.setValue(self, forKeyPath: "sharedCalloutBar.responderTarget")
        
        
        menuController.menuItems = [
            UIMenuItem(title: "复制", action: #selector(copyMessage(_:))),
            UIMenuItem(title: "转发", action: #selector(forwardMessage(_:))),
            UIMenuItem(title: "撤回", action: #selector(withdrawMessage(_:))),
            UIMenuItem(title: "删除", action: #selector(deleteMessage(_:)))
        ]
    
        // set menu display position
        menuController.setTargetRect(convert(rect, to: view), in: view)
        menuController.setMenuVisible(true, animated: true)
        
        // really show?
        guard menuController.isMenuVisible else {
            return
        }
        
        // set selected
        self.isHighlighted = true
        self._menuNotifyObserver = NotificationCenter.default.addObserver(forName: UIMenuController.willHideMenuNotification, object: nil, queue: nil) { [weak self] notification in
            // is release?
            guard let observer = self?._menuNotifyObserver else {
                return
            }
            NotificationCenter.default.removeObserver(observer)
            // cancel select
            self?.isHighlighted = false
            self?._menuNotifyObserver = nil
        }
    }
    
    open override func canPerformAction(_ action: Selector, withSender sender: Any?) -> Bool {
        // menu bar only process
        guard sender is UIMenuController else {
            // other is default process
            return super.canPerformAction(action, withSender: sender)
        }
        // check collectionView and attributes
        guard let view = _collectionView, let indexPath = _layoutAttributes?.indexPath else {
            return false
        }
        // forward to collectionView
        guard let result = view.delegate?.collectionView?(view, canPerformAction: action, forItemAt: indexPath, withSender: sender) else {
            return false
        }
        return result
    }
    
    open override func perform(_ action: Selector!, with sender: Any!) -> Unmanaged<AnyObject>! {
        // menu bar only process
        guard sender is UIMenuController else {
            // other is default process
            return super.perform(action, with: sender)
        }
        // check collectionView and attributes
        guard let view = _collectionView, let indexPath = _layoutAttributes?.indexPath else {
            return nil
        }
        // forward to collectionView
        view.delegate?.collectionView?(view, performAction: action, forItemAt: indexPath, withSender: sender)
        
        return nil
    }
    
    private func _commonInit() {
    }
    
    fileprivate var _bubbleView: UIImageView?
    
    fileprivate var _cardView: JCMessageContentViewType?
    fileprivate var _avatarView: JCMessageContentViewType?
    fileprivate var _contentView: JCMessageContentViewType?
    fileprivate var _tipsView: JCMessageContentViewType?
    
    fileprivate var _menuNotifyObserver: Any?
    fileprivate var _menuGesture: UILongPressGestureRecognizer? {
        return value(forKeyPath: "_menuGesture") as? UILongPressGestureRecognizer
    }
    
    @NSManaged fileprivate var _collectionView: UICollectionView?
    @NSManaged fileprivate var _layoutAttributes: JCChatViewLayoutAttributes?
}


fileprivate extension JCMessageContentViewType {
    // 如果是NSObject对象, 直接使用self.init()会导致无法释放内存
    // 解决方案是转为显式类型再调用cls.init()
    fileprivate static func _init() -> JCMessageContentViewType {
        guard let cls = self as? NSObject.Type else {
            return self.init()
        }
        guard let ob = cls.init() as? JCMessageContentViewType else {
            return self.init()
        }
        return ob
    }
}

fileprivate extension UICollectionReusableView {
    @NSManaged fileprivate func _setLayoutAttributes(_ layoutAttributes: UICollectionViewLayoutAttributes)
}


fileprivate prefix func -(edg: UIEdgeInsets) -> UIEdgeInsets {
    // 取反
    return .init(top: -edg.top, left: -edg.left, bottom: -edg.bottom, right: edg.right)
}
