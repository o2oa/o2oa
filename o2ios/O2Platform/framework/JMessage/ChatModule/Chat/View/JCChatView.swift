//
//  JCChatView.swift
//  JChat
//
//  Created by deng on 2017/2/28.
//  Copyright © 2017年 HXHG. All rights reserved.
//

import UIKit

public protocol JCChatViewDataSource: class {
    
    func numberOfItems(in chatView: JCChatView)
    
    func chatView(_ chatView: JCChatView, itemAtIndexPath: IndexPath)
    
}

var isWait = false

@objc public protocol JCChatViewDelegate: NSObjectProtocol {
    
    @objc optional func chatView(_ chatView: JCChatView, shouldShowMenuForItemAt indexPath: IndexPath) -> Bool
    @objc optional func chatView(_ chatView: JCChatView, canPerformAction action: Selector, forItemAt indexPath: IndexPath, withSender sender: Any?) -> Bool
    @objc optional func chatView(_ chatView: JCChatView, performAction action: Selector, forItemAt indexPath: IndexPath, withSender sender: Any?)
    @objc optional func refershChatView(chatView: JCChatView)
    @objc optional func tapImageMessage(image: UIImage?, indexPath: IndexPath)
    
    @objc optional func deleteMessage(message: JCMessageType)
    @objc optional func copyMessage(message: JCMessageType)
    @objc optional func forwardMessage(message: JCMessageType)
    @objc optional func withdrawMessage(message: JCMessageType)

    @objc optional func indexPathsForVisibleItems(chatView: JCChatView, items: [IndexPath])
}


@objc open class JCChatView: UIView {
    
    public init(frame: CGRect, chatViewLayout: JCChatViewLayout) {
        _chatViewData = JCChatViewData()
        _chatViewLayout = chatViewLayout
        let containerViewFrame = CGRect(x: 0, y: 0, width: frame.size.width, height: frame.size.height)
        _chatContainerView = JCChatContainerView(frame: containerViewFrame, collectionViewLayout: chatViewLayout)
        super.init(frame: frame)
        _commonInit()
    }
    
    public required init?(coder aDecoder: NSCoder) {
        // decode layout
        guard let chatViewLayout = JCChatViewLayout(coder: aDecoder) else {
            return nil
        }
        // decode container view
        guard let chatContainerView = JCChatContainerView(coder: aDecoder) else {
            return nil
        }
        // init data
        _chatViewData = JCChatViewData()
        // init to layout & container view
        _chatViewLayout = chatViewLayout
        _chatContainerView = chatContainerView
        // init super
        super.init(coder: aDecoder)
        // init other data
        _commonInit()
    }
    
    open weak var delegate: JCChatViewDelegate?
    open weak var dataSource: JCChatViewDataSource?
    open weak var messageDelegate: JCMessageDelegate?
    
    func insert(_ newMessage: JCMessageType, at index: Int) {
        _batchBegin()
        _batchItems.append(.insert(newMessage, at: index))
        _batchCommit()
    }
    func insert(contentsOf newMessages: Array<JCMessageType>, at index: Int) {
        _batchBegin()
        _batchItems.append(contentsOf: newMessages.map({ .insert($0, at: index) }))
        _batchCommit(true)
    }
    
    func update(_ newMessage: JCMessageType, at index: Int) {
        _batchBegin()
        _batchItems.append(.update(newMessage, at: index))
        _batchCommit()
    }
    
    func removeAll() {
        _batchBegin()
        for index in 0..<_chatViewData.count {
            _batchItems.append(.remove(at: index))
        }
        _batchCommit()
    }
    
    func remove(at index: Int) {
        _batchBegin()
        _batchItems.append(.remove(at: index))
        _batchCommit()
    }
    func remove(contentOf indexs: Array<Int>) {
        _batchBegin()
        _batchItems.append(contentsOf: indexs.map({ .remove(at: $0) }))
        _batchCommit()
    }
    
    func move(at index1: Int, to index2: Int) {
        _batchBegin()
        _batchItems.append(.move(at: index1, to: index2))
        _batchCommit()
    }
    
    func append(_ newMessage: JCMessageType) {
        insert(newMessage, at: _chatViewData.count)
    }
    func append(contentsOf newMessages: Array<JCMessageType>) {
        insert(contentsOf: newMessages, at: _chatViewData.count)
    }
    
    fileprivate func _batchBegin() {
        _chatContainerView.messageDelegate = self.messageDelegate
        objc_sync_enter(_batchItems)
        _batchRequiredCount = max(_batchRequiredCount + 1, 1)
        objc_sync_exit(_batchItems)
    }
    fileprivate func _batchCommit(_ isInsert: Bool = false) {
        objc_sync_enter(_batchItems)
        _batchRequiredCount = max(_batchRequiredCount - 1, 0)
        guard _batchRequiredCount == 0 else {
            objc_sync_exit(_batchItems)
            return
        }
        let oldData = _chatViewData
        let newData = JCChatViewData()
        let updateItems = _batchItems
        _batchItems.removeAll()
        objc_sync_exit(_batchItems)
        
        _ = _chatContainerView.numberOfItems(inSection: 0)
        let update = JCChatViewUpdate(newData: newData, oldData: oldData, updateItems: updateItems)
        // exec
        _chatViewData = newData
        _chatContainerView.performBatchUpdates(with: update, isInsert, completion: nil)
    }
    
    fileprivate lazy var _batchItems: Array<JCChatViewUpdateChangeItem> = []
    fileprivate lazy var _batchRequiredCount: Int = 0
    
    private func _commonInit() {
        
        backgroundColor = UIColor(netHex: 0xe8edf3)
        let header = MJRefreshNormalHeader(refreshingTarget: self, refreshingAction: #selector(_onPullToFresh))
        header?.stateLabel.isHidden = true
        _chatContainerView.mj_header = header
        _chatContainerView.allowsSelection = false
        _chatContainerView.allowsMultipleSelection = false
        _chatContainerView.autoresizingMask = [.flexibleWidth, .flexibleHeight]
        _chatContainerView.keyboardDismissMode = .onDrag
        _chatContainerView.backgroundColor = UIColor(netHex: 0xE8EDF3)
        _chatContainerView.dataSource = self
        _chatContainerView.delegate = self
        
        addSubview(_chatContainerView)
        #if READ_VERSION
        _chatContainerView.addObserver(self, forKeyPath: "contentOffset", options: .new, context: nil)
        #endif
    }
    
    fileprivate var _chatViewData: JCChatViewData
    
    fileprivate var _chatViewLayout: JCChatViewLayout
    fileprivate var _chatContainerView: JCChatContainerView
    
    fileprivate lazy var _chatContainerRegistedTypes: Set<String> = []
    
    @objc func _onPullToFresh() {
        delegate?.refershChatView?(chatView: self)
    }
    func stopRefresh() {
        _chatContainerView.mj_header.endRefreshing()
    }
    
    func scrollToLast(animated: Bool) {
        let count = _chatContainerView.numberOfItems(inSection: 0)
        if count > 0 {
            _chatContainerView.scrollToItem(at: IndexPath(row: count - 1, section: 0), at: .bottom, animated: animated)
        }
    }

    deinit {
        #if READ_VERSION
        _chatContainerView.removeObserver(self, forKeyPath: "contentOffset")
        #endif
    }
}

internal class JCChatContainerView: UICollectionView {
    
    weak var messageDelegate: JCMessageDelegate?
    
    var currentUpdate: JCChatViewUpdate? {
        return _currentUpdate
    }
    
    func performBatchUpdates(with update: JCChatViewUpdate, _ isInsert: Bool = false, completion:((Bool) -> Void)?) {
        
        // read changes
        guard let changes = update.updateChanges else {
            return
        }
        _currentUpdate = update
        
        // TODO: 不是最优
        if update.updateItems.count > 0 {
            for item in update.updateItems {
                switch item {
                case .update:
                    self.performBatchUpdates({ 
                        self.reloadItems(at: [IndexPath(row: item.at, section: 0)])
                    }, completion: nil)
                    return
                default:
                    break
                }
            }
        }
        
        
        var oldContent = self.contentSize
//        self.contentSize = CGSize(width: self.contentSize.width, height: 3725)
//        self.setContentOffset(CGPoint(x: 0, y: 3725 - oldContent.height), animated: false)
//        self.layoutIfNeeded()
//        self.setContentOffset(CGPoint(x: 0, y: 250232320), animated: false)
//        self.layoutIfNeeded()
        // commit changes
        
        UIView.animate(withDuration: 0) {
            if isInsert {
                self.isHidden = true
            }
            self.performBatchUpdates({
                // apply move
                changes.filter({ $0.isMove }).forEach({
                    self.moveItem(at: .init(item: max($0.from, 0), section: 0),
                                  to: .init(item: max($0.to, 0), section: 0))
                })
//                print(oldContent)
                // apply insert/remove/update
                self.insertItems(at: changes.filter({ $0.isInsert }).map({ .init(item: max($0.to, 0), section: 0) }))
                self.reloadItems(at: changes.filter({ $0.isUpdate }).map({ .init(item: max($0.from, 0), section: 0) }))
                self.deleteItems(at: changes.filter({ $0.isRemove }).map({ .init(item: max($0.from, 0), section: 0) }))
                
            }, completion: { finished in
                if isInsert {
                    UIView.animate(withDuration: 0, animations: {
                        if self.contentSize.height > oldContent.height && oldContent.height != 0 {
                            self.setContentOffset(CGPoint(x: 0, y: self.contentSize.height - oldContent.height), animated: false)
                            self.layoutIfNeeded()
                            oldContent = self.contentSize
                        }
                    })
                    self.isHidden = false
                }
                
                completion?(finished)
            })
        }
        
        _currentUpdate = nil
    }
    
    private var _currentUpdate: JCChatViewUpdate?
}



extension JCChatView: UICollectionViewDataSource, JCChatViewLayoutDelegate {
    
    open var isRoll: Bool {
        return _chatContainerView.isDragging || _chatContainerView.isDecelerating
    }
    
    open dynamic var indexPathsForVisibleItems: [IndexPath] {
        return _chatContainerView.indexPathsForVisibleItems
    }
    
    open dynamic var contentSize: CGSize {
        set { return _chatContainerView.contentSize = newValue }
        get { return _chatContainerView.contentSize }
    }
    open dynamic var contentOffset: CGPoint {
        set { return _chatContainerView.contentOffset = newValue }
        get { return _chatContainerView.contentOffset }
    }
    open dynamic var contentInset: UIEdgeInsets {
        set { return _chatContainerView.contentInset = newValue }
        get { return _chatContainerView.contentInset }
    }
    open dynamic var scrollIndicatorInsets: UIEdgeInsets {
        set { return _chatContainerView.scrollIndicatorInsets = newValue }
        get { return _chatContainerView.scrollIndicatorInsets }
    }
    
    open func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return _chatViewData.count
    }
    
    open func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {

        let message = _chatViewData[indexPath.item]
        
//        let options = (message.options.showsCard.hashValue << 0) | (message.options.showsAvatar.hashValue << 1)
        let alignment = message.options.alignment.rawValue
        let identifier = NSStringFromClass(type(of: message.content)) + ".\(alignment)"
        
        if !_chatContainerRegistedTypes.contains(identifier) {
            _chatContainerRegistedTypes.insert(identifier)
            _chatContainerView.register(JCChatViewCell.self, forCellWithReuseIdentifier: identifier)
        }
        let cell = _chatContainerView.dequeueReusableCell(withReuseIdentifier: identifier, for: indexPath) as! JCChatViewCell
        cell.delegate = messageDelegate
        cell.updateView()
        return cell
    }
    
    open func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, itemAt indexPath: IndexPath) -> JCMessageType {
        return _chatViewData[indexPath.item]
    }
    
    open func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, sizeForItemAt indexPath: IndexPath) -> CGSize {
        guard let collectionViewLayout = collectionViewLayout as? JCChatViewLayout else {
            return .zero
        }
        guard let layoutAttributesInfo = collectionViewLayout.layoutAttributesInfoForItem(at: indexPath) else {
            return .zero
        }
        let size = layoutAttributesInfo.layoutedBoxRect(with: .all).size
        return .init(width: collectionView.frame.width, height: size.height)
    }
    open func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, sizeForItemAvatarOf style: JCMessageStyle) -> CGSize {
        // 78 * 78
        return .init(width: 40, height: 40)
    }
    open func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, sizeForItemCardOf style: JCMessageStyle) -> CGSize {
        return .init(width: 0, height: 18)
    }
    
    public func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, sizeForItemTipsOf style: JCMessageStyle) -> CGSize {
        return .init(width: 100, height: 21)
    }
    
    open func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, insetForItemOf style: JCMessageStyle) -> UIEdgeInsets {
        switch style {
        case .bubble:
            // bubble content edg, 2x
            // +----12--+-+---+
            // |        | |   |
            // 16       4 40  16
            // |        | |   |
            // +----12--+-+---+
            return .init(top: 6, left: 8, bottom: 6, right: 2 + 20 + 8)
            
        case .notice:
            // default edg
            // +----10----+
            // 20         20
            // +----10----+
            return .init(top: 10, left: 20, bottom: 10, right: 20)
            
//        default:
//            // default edg
//            // +----10----+
//            // 10         10
//            // +----10----+
//            return .init(top: 10, left: 10, bottom: 10, right: 10)
        }
    }
    open func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, insetForItemCardOf style: JCMessageStyle) -> UIEdgeInsets {
        return .init(top: 0, left: 8, bottom: 2, right: 8)
    }
    open func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, insetForItemAvatarOf style: JCMessageStyle) -> UIEdgeInsets {
        return .init(top: 0, left: 2, bottom: 2, right: 2)
    }
    open func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, insetForItemBubbleOf style: JCMessageStyle) -> UIEdgeInsets {
//        return .init(top: -2, left: 0, bottom: -2, right: 0)
        return .init(top: 0, left: 8, bottom: 0, right: 0)
    }
    open func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, insetForItemContentOf style: JCMessageStyle) -> UIEdgeInsets {
        switch style {
        case .bubble:
            // bubble image edg, scale: 2x, radius: 15
            // /--------16-------\
            // |  +-----04-----+ |
            // 20 04          04 20
            // |  +-----04-----+ |
            // \--------16-------/
//            return .init(top: 8 + 2, left: 10 + 2, bottom: 8 + 2, right: 10 + 2)
            return .init(top: 2, left: 5 + 2, bottom: 2, right: 2)
            
        case .notice:
            // notice edg
            // /------4-------\
            // 10             10
            // \------4-------/
            return .init(top: 4, left: 10, bottom: 4, right: 10)
            
        }
    }
    
    open func collectionView(_ collectionView: UICollectionView, shouldShowMenuForItemAt indexPath: IndexPath) -> Bool {
        return true
    }
    
    open func collectionView(_ collectionView: UICollectionView, canPerformAction action: Selector, forItemAt indexPath: IndexPath, withSender sender: Any?) -> Bool {
        let message = _chatViewData[indexPath.item]
        if message.content is JCMessageNoticeContent || message.content is JCMessageTimeLineContent  {
            return false
        }
        if let _ = message.content as? JCMessageTextContent {
            if action == #selector(copyMessage(_:)) {
                return true
            }
        }
        if action == #selector(deleteMessage(_:)) {
            return true
        }
        
        if action == #selector(forwardMessage(_:)) {
            return true
        }
        
        if action == #selector(withdrawMessage(_:)) {
            if let sender = message.sender {
                if sender.isEqual(to: JMSGUser.myInfo()) {
                    return true
                }
            }
            return false
        }
 
        return false
    }
    
    @objc func copyMessage(_ sender: Any) {}
    @objc func deleteMessage(_ sender: Any) {}
    @objc func forwardMessage(_ sender: Any) {}
    @objc func withdrawMessage(_ sender: Any) {}
    
    open func collectionView(_ collectionView: UICollectionView, performAction action: Selector, forItemAt indexPath: IndexPath, withSender sender: Any?) {
        let message = _chatViewData[indexPath.item]
        if action == #selector(copyMessage(_:)) {
            if let content = message.content as? JCMessageTextContent {
                let pas = UIPasteboard.general
                pas.string = content.text.string
            }
        }
        if action == #selector(deleteMessage(_:)) {
            remove(at: indexPath.item)
            delegate?.deleteMessage?(message: message)
        }
//        if action == #selector(paste(_:)) {
//            move(at: indexPath.item, to: _chatViewData.count - 1)
//        }
        if action == #selector(forwardMessage(_:)) {
            delegate?.forwardMessage?(message: message)
        }
        
        if action == #selector(withdrawMessage(_:)) {
            delegate?.withdrawMessage?(message: message)
        }
    }
}

extension JCChatView {
    override open func observeValue(forKeyPath keyPath: String?, of object: Any?, change: [NSKeyValueChangeKey : Any]?, context: UnsafeMutableRawPointer?) {
        if keyPath == "contentOffset" {
            DispatchQueue.main.asyncAfter(deadline: DispatchTime.now()) {
                if !isWait {
                    isWait = true
                    DispatchQueue.main.asyncAfter(deadline: DispatchTime.now() + 0.05) {
                        self.delegate?.indexPathsForVisibleItems?(chatView: self, items: self._chatContainerView.indexPathsForVisibleItems)
                        isWait = false
                    }
                }
            }
        }
    }

}

extension JCChatView: SAIInputBarScrollViewType {
}
