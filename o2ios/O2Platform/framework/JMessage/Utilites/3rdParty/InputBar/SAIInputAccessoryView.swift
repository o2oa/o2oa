//
//  SAIInputAccessoryView.swift
//  SAIInputBar
//
//  Created by SAGESSE on 7/23/16.
//  Copyright © 2016-2017 SAGESSE. All rights reserved.
//

import UIKit

internal protocol SAIInputAccessoryViewDelegate: class {
    func inputAccessoryView(touchDown recordButton: UIButton)
    func inputAccessoryView(touchUpInside recordButton: UIButton)
    func inputAccessoryView(touchUpOutside recordButton: UIButton)
    func inputAccessoryView(dragOutside recordButton: UIButton)
    func inputAccessoryView(dragInside recordButton: UIButton)
}

internal class SAIInputAccessoryView: UIView {
    
    var isShowRecordButton: Bool {
        get {
            return _recordButton.isHidden
        }
        set {
            _recordButton.isHidden = !newValue
            _textField.isHidden = newValue
        }
    }
    
    var textField: SAIInputTextField {
        return _textField
    }
    
    weak var delegate: (UITextViewDelegate & SAIInputItemViewDelegate & SAIInputAccessoryViewDelegate)?
    
    override func layoutSubviews() {
        super.layoutSubviews()
        
        if _cacheBounds?.width != bounds.width {
            _cacheBounds = bounds
            _boundsDidChanged()
        }
    }
    
    override func becomeFirstResponder() -> Bool {
        return _textField.becomeFirstResponder()
    }
    override func resignFirstResponder() -> Bool {
        return _textField.resignFirstResponder()
    }
    
    override func invalidateIntrinsicContentSize() {
        _cacheContentSize = nil
        super.invalidateIntrinsicContentSize()
    }
    override var intrinsicContentSize: CGSize {
        if let size = _cacheContentSize, size.width == frame.width {
            return size
        }
        // Calculate intrinsicContentSize that will fit all the text
        let size = _contentSizeWithoutCache
        
        _cacheContentSize = size
        return size
    }
    
    func updateInputMode(_ newMode: SAIInputMode, oldMode: SAIInputMode, animated: Bool) {
        
        if !newMode.isEditing && textField.isFirstResponder {
            _ = resignFirstResponder()
        }
        if newMode.isEditing && !textField.isFirstResponder {
            _ = becomeFirstResponder()
        }
    }
    
    // MARK: Selection
    
    func barItems(atPosition position: SAIInputItemPosition) -> [SAIInputItem] {
        return _barItems(atPosition: position)
    }
    func setBarItems(_ barItems: [SAIInputItem], atPosition position: SAIInputItemPosition, animated: Bool) {
        
        _setBarItems(barItems, atPosition: position)
        
        // if _cacheBoundsSize is nil, the layout is not initialize
        if _cacheBounds != nil {
            _collectionViewLayout.invalidateLayoutIfNeeded(atPosition: position)
            _updateBarItemsLayout(animated)
        }
    }
    
    func canSelectBarItem(_ barItem: SAIInputItem) -> Bool {
        return !_selectedBarItems.contains(barItem)
    }
    func canDeselectBarItem(_ barItem: SAIInputItem) -> Bool {
        return _selectedBarItems.contains(barItem)
    }
    
    func selectBarItem(_ barItem: SAIInputItem, animated: Bool) {
        
        _selectedBarItems.insert(barItem)
        // need to be updated in the visible part of it
        _collectionView.visibleCells.forEach {
            guard let cell = ($0 as? SAIInputItemView), cell.item === barItem else {
                return
            }
            cell.setSelected(true, animated: animated)
        }
    }
    func deselectBarItem(_ barItem: SAIInputItem, animated: Bool) {
        
        _selectedBarItems.remove(barItem)
        // need to be updated in the visible part of it
        _collectionView.visibleCells.forEach {
            guard let cell = $0 as? SAIInputItemView, cell.item === barItem else {
                return
            }
            cell.setSelected(false, animated: animated)
        }
    }
    
    // MARK: Private Method
    
    fileprivate func _barItems(atPosition position: SAIInputItemPosition) -> [SAIInputItem] {
        return _barItems[position.rawValue] ?? []
    }
    fileprivate func _setBarItems(_ barItems: [SAIInputItem], atPosition position: SAIInputItemPosition) {
        if position == .center {
            _centerBarItem = barItems.first ?? _textField.item
            _barItems[position.rawValue] = [_centerBarItem]
        } else {
            _barItems[position.rawValue] = barItems
        }
    }
    
    fileprivate func _barItem(atIndexPath indexPath: IndexPath) -> SAIInputItem {
        if let items = _barItems[(indexPath as NSIndexPath).section], (indexPath as NSIndexPath).item < items.count {
            return items[(indexPath as NSIndexPath).item]
        }
        fatalError("barItem not found at \(indexPath)")
    }
    fileprivate func _barItemAlginment(at indexPath: IndexPath) -> SAIInputItemAlignment {
        let item = _barItem(atIndexPath: indexPath)
        if item.alignment == .automatic {
            // in automatic mode, the section will have different performance
            switch (indexPath as NSIndexPath).section {
            case 0:  return .bottom
            case 1:  return .bottom
            case 2:  return .bottom
            default: return .center
            }
        }
        return item.alignment
    }
    
    fileprivate func _boundsDidChanged() {
        
        _textField.item.invalidateCache()
        _updateBarItemsLayout(false)
    }
    fileprivate func _contentDidChange() {
        
        let center = NotificationCenter.default
        center.post(name: Notification.Name(rawValue: SAIAccessoryDidChangeFrameNotification), object: nil)
    }
    
    fileprivate func _updateContentInsetsIfNeeded() {
        let contentInsets = _contentInsetsWithoutCache
        guard contentInsets != _cacheContentInsets else {
            return
        }
        
        // update the constraints
        _textFieldTop.constant = contentInsets.top
        _textFieldLeft.constant = contentInsets.left
        _textFieldRight.constant = contentInsets.right
        _textFieldBottom.constant = contentInsets.bottom
        
        _cacheContentInsets = contentInsets
    }
    fileprivate func _updateContentSizeIfNeeded() {
        let contentSize = _contentSizeWithoutCache
        guard _cacheContentSize != contentSize else {
            return
        }
        
        invalidateIntrinsicContentSize()
        _cacheContentSize = contentSize
        _contentDidChange()
    }
    fileprivate func _updateContentSizeForTextChanged(_ animated: Bool) {
        guard _textField.item.needsUpdateContent else {
            return
        }
        
        if animated {
            UIView.beginAnimations("SAIB-ANI-AC", context: nil)
            UIView.setAnimationDuration(_SAInputDefaultAnimateDuration)
            UIView.setAnimationCurve(_SAInputDefaultAnimateCurve)
        }
        
        _updateContentSizeInCollectionView()
        _updateContentSizeIfNeeded()
        
        // reset the offset, because offset in the text before the update has been made changes
        _textField.setContentOffset(CGPoint.zero, animated: true)
        
        if animated {
            UIView.commitAnimations()
        }
    }
    fileprivate func _updateContentSizeInCollectionView() {
        _collectionView.reloadSections(IndexSet(integer: _SAInputAccessoryViewCenterSection))
    }
    
    fileprivate func _updateBarItemsInCollectionView() {
        
        // add, remove, update
        (0 ..< numberOfSections(in: _collectionView)).forEach { section in
            
            let newItems = _barItems[section] ?? []
            let oldItems = _cacheBarItems[section] ?? []
            
            var addIdxs: Set<Int> = []
            var removeIdxs: Set<Int> = []
            var reloadIdxs: Set<Int> = []
            
            _diff(oldItems, newItems).forEach { 
                if $0.0 < 0 {
                    
                    let idx = max($0.1, 0)
                    removeIdxs.insert(idx)
                }
                if $0.0 > 0 {
                    let idx = max($0.1, 0)
                    if removeIdxs.contains(idx) {
                        removeIdxs.remove(idx)
                        reloadIdxs.insert(idx)
                    } else {
                        addIdxs.insert($0.1 + addIdxs.count - removeIdxs.count + 1)
                    }
                }
            }
            
            _collectionView.reloadItems(at: reloadIdxs.map({ 
                IndexPath(item: $0, section: section)
            }))
            _collectionView.insertItems(at: addIdxs.map({ 
                IndexPath(item: $0, section: section)
            }))
            _collectionView.deleteItems(at: removeIdxs.map({ 
                IndexPath(item: $0, section: section)
            }))
            
            _cacheBarItems[section] = newItems
        }
    }
    fileprivate func _updateBarItemsLayout(_ animated: Bool) {
        
        if animated {
            UIView.beginAnimations("SAIB-ANI-AC", context: nil)
            UIView.setAnimationDuration(_SAInputDefaultAnimateDuration)
            UIView.setAnimationCurve(_SAInputDefaultAnimateCurve)
        }
        
        // step 0: update the boundary
        _updateContentInsetsIfNeeded() 
        // step 1: update textField the size
        _textField.layoutIfNeeded() 
        // step 2: update cell
        _updateBarItemsIfNeeded(animated) 
        // step 3: update contentSize
        _updateContentSizeIfNeeded() 
        // step 4: update other
        if _centerBarItem != _textField.item {
            _textField.alpha = 0
            _textField.backgroundView.alpha = 0
        } else {
            _textField.alpha = 1
            _textField.backgroundView.alpha = 1
        }
        
        if animated {
            UIView.commitAnimations()
        }
    }
    fileprivate func _updateBarItemsIfNeeded(_ animated: Bool) {
        guard !_collectionView.indexPathsForVisibleItems.isEmpty else {
            // initialization is not complete
            _cacheBarItems = _barItems
            return
        }
        _collectionView.performBatchUpdates(_updateBarItemsInCollectionView, completion: nil)
    }
    
    private func _init() {
        
        // configuration
//        _textField.isHidden = true
        _textField.font = UIFont.systemFont(ofSize: 15)
        _textField.layer.cornerRadius = 5
        _textField.layer.borderWidth = 0.5
        _textField.layer.borderColor = UIColor.gray.cgColor
        _textField.layer.masksToBounds = true
        _textField.delegate = self
        _textField.scrollsToTop = false
        _textField.returnKeyType = .send
        _textField.backgroundColor = .clear
        _textField.scrollIndicatorInsets = UIEdgeInsets(top: 2, left: 0, bottom: 2, right: 0)
        _textField.translatesAutoresizingMaskIntoConstraints = false
        _textField.backgroundView.translatesAutoresizingMaskIntoConstraints = false
        _textField.typingAttributes[NSAttributedString.Key.paragraphStyle] = {
            let style = NSMutableParagraphStyle()
            style.lineBreakMode = .byCharWrapping
            return style
        }()
        
        _recordButton.isHidden = true
        _recordButton.layer.cornerRadius = 5
        _recordButton.layer.borderWidth = 0.5
        _recordButton.layer.borderColor = UIColor.gray.cgColor
        _recordButton.layer.masksToBounds = true
        _recordButton.setTitleColor(UIColor(netHex: 0x5A5A5A), for: .normal)
        _recordButton.titleLabel?.font = UIFont.systemFont(ofSize: 15)
        _recordButton.translatesAutoresizingMaskIntoConstraints = false
        _recordButton.setTitle("按住 说话", for: .normal)
        _recordButton.setTitle("松开 发送", for: .highlighted)
        _recordButton.setTitleColor(.black, for: .normal)
        
        _recordButton.addTarget(self, action: #selector(_touchDown), for: .touchDown)
        _recordButton.addTarget(self, action: #selector(_touchUpInside), for: .touchUpInside)
        _recordButton.addTarget(self, action: #selector(_touchUpOutside), for: .touchUpOutside)
        _recordButton.addTarget(self, action: #selector(_dragOutside), for: .touchDragExit)
        _recordButton.addTarget(self, action: #selector(_dragInside), for: .touchDragEnter)
        
        _collectionViewLayout.minimumLineSpacing = 8
        _collectionViewLayout.minimumInteritemSpacing = 8
        
        _collectionView.bounces = false
        _collectionView.scrollsToTop = false
        _collectionView.isScrollEnabled = false
        _collectionView.allowsSelection = false
        _collectionView.isMultipleTouchEnabled = false
        _collectionView.showsHorizontalScrollIndicator = false
        _collectionView.showsVerticalScrollIndicator = false
        _collectionView.delaysContentTouches = false
        _collectionView.canCancelContentTouches = false
        _collectionView.backgroundColor = .clear
        _collectionView.dataSource = self
        _collectionView.delegate = self
        _collectionView.translatesAutoresizingMaskIntoConstraints = false
        _collectionView.setContentHuggingPriority(UILayoutPriority(rawValue: 700), for: .horizontal)
        _collectionView.setContentHuggingPriority(UILayoutPriority(rawValue: 700), for: .vertical)
        _collectionView.setContentCompressionResistancePriority(UILayoutPriority(rawValue: 200), for: .horizontal)
        _collectionView.setContentCompressionResistancePriority(UILayoutPriority(rawValue: 200), for: .vertical)
        
        // update center bar item
        _setBarItems([], atPosition: .center)
        
        // adds a child view
        addSubview(_collectionView)
        addSubview(_textField.backgroundView)
        addSubview(_textField)
        addSubview(_recordButton)
        
        // adding constraints
        addConstraints([
            _SAInputLayoutConstraintMake(_collectionView, .top, .equal, self, .top),
            _SAInputLayoutConstraintMake(_collectionView, .left, .equal, self, .left),
            _SAInputLayoutConstraintMake(_collectionView, .right, .equal, self, .right),
            _SAInputLayoutConstraintMake(_collectionView, .bottom, .equal, self, .bottom),
            
            _SAInputLayoutConstraintMake(_textField.backgroundView, .top, .equal, _textField, .top),
            _SAInputLayoutConstraintMake(_textField.backgroundView, .left, .equal, _textField, .left),
            _SAInputLayoutConstraintMake(_textField.backgroundView, .right, .equal, _textField, .right),
            _SAInputLayoutConstraintMake(_textField.backgroundView, .bottom, .equal, _textField, .bottom),
            
            _textFieldTop,
            _textFieldLeft,
            _textFieldRight,
            _textFieldBottom,
            
            _SAInputLayoutConstraintMake(_recordButton, .top, .equal, _textField, .top),
            _SAInputLayoutConstraintMake(_recordButton, .left, .equal, _textField, .left),
            _SAInputLayoutConstraintMake(_recordButton, .right, .equal, _textField, .right),
            _SAInputLayoutConstraintMake(_recordButton, .bottom, .equal, _textField, .bottom),
        ])
        
        // init collection view
        (0 ..< numberOfSections(in: _collectionView)).forEach {
            _collectionView.register(SAIInputItemView.self, forCellWithReuseIdentifier: "Cell-\($0)")
        }
    }
    
    // click event
    open func _touchDown() {
        delegate?.inputAccessoryView(touchDown: _recordButton)
    }
    
    open func _touchUpInside() {
        delegate?.inputAccessoryView(touchUpInside: _recordButton)
    }
    
    open func _touchUpOutside() {
        delegate?.inputAccessoryView(touchUpOutside: _recordButton)
    }
    
    open func _dragOutside() {
       delegate?.inputAccessoryView(dragOutside: _recordButton)
    }
    
    open func _dragInside() {
        delegate?.inputAccessoryView(dragInside: _recordButton)
    }
    
    private var _contentSizeWithoutCache: CGSize {
        
        let centerBarItemSize = _centerBarItem.size
        let height = _textFieldTop.constant + centerBarItemSize.height + _textFieldBottom.constant
        return CGSize(width: frame.width, height: height)
    }
    private var _contentInsetsWithoutCache: UIEdgeInsets {
        
        var contentInsets = _collectionViewLayout.contentInsets
        // merge the top
        let topSize = _collectionViewLayout.sizeThatFits(bounds.size, atPosition: .top)
        if topSize.height != 0 {
            contentInsets.top += topSize.height + _collectionViewLayout.minimumLineSpacing
        }
        // merge the left
        let leftSize = _collectionViewLayout.sizeThatFits(bounds.size, atPosition: .left)
        if leftSize.width != 0 {
            contentInsets.left += leftSize.width + _collectionViewLayout.minimumInteritemSpacing
        }
        // merge the right
        let rightSize = _collectionViewLayout.sizeThatFits(bounds.size, atPosition: .right)
        if rightSize.width != 0 {
            contentInsets.right += rightSize.width + _collectionViewLayout.minimumInteritemSpacing
        }
        // merge the bottom
        let bottomSize = _collectionViewLayout.sizeThatFits(bounds.size, atPosition: .bottom)
        if bottomSize.height != 0 {
            contentInsets.bottom += bottomSize.height + _collectionViewLayout.minimumLineSpacing
        }
        return contentInsets
    }
    
    //  MARK: Ivar
    
    fileprivate lazy var _centerBarItem: SAIInputItem = self.textField.item
    
    fileprivate lazy var _barItems: [Int: [SAIInputItem]] = [:]
    fileprivate lazy var _cacheBarItems: [Int: [SAIInputItem]] = [:]
    fileprivate lazy var _selectedBarItems: Set<SAIInputItem> = []
    
    fileprivate lazy var _textField: SAIInputTextField = SAIInputTextField()
    fileprivate lazy var _recordButton: UIButton = UIButton()
    
    fileprivate lazy var _collectionViewLayout: SAIInputAccessoryViewLayout = SAIInputAccessoryViewLayout()
    fileprivate lazy var _collectionView: UICollectionView = UICollectionView(frame: CGRect.zero, collectionViewLayout: self._collectionViewLayout)
   
    fileprivate lazy var _textFieldTop: NSLayoutConstraint = {
        return _SAInputLayoutConstraintMake(self._textField, .top, .equal, self, .top)
    }()
    fileprivate lazy var _textFieldLeft: NSLayoutConstraint = {
        return _SAInputLayoutConstraintMake(self._textField, .left, .equal, self, .left)
    }()
    fileprivate lazy var _textFieldRight: NSLayoutConstraint = {
        return _SAInputLayoutConstraintMake(self, .right, .equal, self._textField, .right)
    }()
    fileprivate lazy var _textFieldBottom: NSLayoutConstraint = {
        return _SAInputLayoutConstraintMake(self, .bottom, .equal, self._textField, .bottom)
    }()
    
    
    fileprivate var _cacheBounds: CGRect?
    fileprivate var _cacheContentSize: CGSize?
    fileprivate var _cacheContentInsets: UIEdgeInsets?
    
    fileprivate var _cacheBarItemContainer: UICollectionViewCell?
    
    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        _init()
    }
    override init(frame: CGRect) {
        super.init(frame: frame)
        _init()
    }
}

// MARK: - SAIInputItemDelegate(Forwarding)

extension SAIInputAccessoryView: SAIInputItemViewDelegate {
    
    func barItem(shouldHighlightFor barItem: SAIInputItem) -> Bool {
        return delegate?.barItem(shouldHighlightFor: barItem) ?? true
    }
    func barItem(shouldDeselectFor barItem: SAIInputItem) -> Bool {
        return delegate?.barItem(shouldDeselectFor: barItem) ?? false
    }
    func barItem(shouldSelectFor barItem: SAIInputItem) -> Bool {
        return delegate?.barItem(shouldSelectFor: barItem) ?? false
    }
    
    func barItem(didHighlightFor barItem: SAIInputItem) {
        delegate?.barItem(didHighlightFor: barItem)
    }
    func barItem(didDeselectFor barItem: SAIInputItem) {
        _selectedBarItems.remove(barItem)
        delegate?.barItem(didDeselectFor: barItem)
    }
    func barItem(didSelectFor barItem: SAIInputItem) {
        _selectedBarItems.insert(barItem)
        delegate?.barItem(didSelectFor: barItem)
    }
}

// MARK: - UITextViewDelegate(Forwarding)

extension SAIInputAccessoryView: UITextViewDelegate {
    
    func textViewShouldBeginEditing(_ textView: UITextView) -> Bool {
        return delegate?.textViewShouldBeginEditing?(textView) ?? true
    }
    func textViewShouldEndEditing(_ textView: UITextView) -> Bool {
        return delegate?.textViewShouldEndEditing?(textView) ?? true
    }
    
    func textViewDidBeginEditing(_ textView: UITextView) {
        delegate?.textViewDidBeginEditing?(textView)
    }
    func textViewDidEndEditing(_ textView: UITextView) {
        delegate?.textViewDidEndEditing?(textView)
    }
    
    func textViewDidChangeSelection(_ textView: UITextView) {
        delegate?.textViewDidChangeSelection?(textView)
    }
    func textViewDidChange(_ textView: UITextView) {
        delegate?.textViewDidChange?(textView)
        _updateContentSizeForTextChanged(true)
    }
    
    func textView(_ textView: UITextView, shouldInteractWith textAttachment: NSTextAttachment, in characterRange: NSRange) -> Bool {
        return delegate?.textView?(textView, shouldInteractWith: textAttachment, in: characterRange) ?? true
    }
    func textView(_ textView: UITextView, shouldChangeTextIn range: NSRange, replacementText text: String) -> Bool {
        return delegate?.textView?(textView, shouldChangeTextIn: range, replacementText: text) ?? true
    }
    func textView(_ textView: UITextView, shouldInteractWith URL: URL, in characterRange: NSRange) -> Bool {
        return delegate?.textView?(textView, shouldInteractWith: URL, in: characterRange) ?? true
    }
}
    
// MARK: - UICollectionViewDataSource & UICollectionViewDelegateFlowLayout

extension SAIInputAccessoryView: UICollectionViewDataSource, UICollectionViewDelegateFlowLayout {
    
    func numberOfSections(in collectionView: UICollectionView) -> Int {
        return 5
    }
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        if section == _SAInputAccessoryViewCenterSection {
            return 1
        }
        return _barItems[section]?.count ?? 0
    }
    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        return collectionView.dequeueReusableCell(withReuseIdentifier: "Cell-\((indexPath as NSIndexPath).section)", for: indexPath)
    }
    
    func collectionView(_ collectionView: UICollectionView, willDisplay cell: UICollectionViewCell, forItemAt indexPath: IndexPath) {
        guard let cell = cell as? SAIInputItemView else {
            return
        }
        let item = _barItem(atIndexPath: indexPath)
        
        cell.delegate = self
        cell.item = item
        cell.setSelected(_selectedBarItems.contains(item), animated: false)
        
        cell.isHidden = (item == _textField.item)
    }
}

///
/// 比较数组差异
/// (+1, src.index, dest.index) // add
/// ( 0, src.index, dest.index) // equal
/// (-1, src.index, dest.index) // remove
///
private func _diff<T: Equatable>(_ src: Array<T>, _ dest: Array<T>) -> Array<(Int, Int, Int)> {
    
    let len1 = src.count
    let len2 = dest.count
    
    var c = [[Int]](repeating: [Int](repeating: 0, count: len2 + 1), count: len1 + 1)
    
    // lcs + 动态规划
    for i in 1 ..< len1 + 1 { 
        for j in 1 ..< len2 + 1 {
            if src[i - 1] == dest[j - 1] {
                c[i][j] = c[i - 1][j - 1] + 1
            } else {
                c[i][j] = max(c[i - 1][j], c[i][j - 1])
            }
        }
    }
    
    var r = [(Int, Int, Int)]()
    var i = len1
    var j = len2
    
    // create the optimal path
    repeat {
        guard i != 0 else {
            // the remaining is add
            while j > 0 {
                r.append((+1, i - 1, j - 1))
                j -= 1
            }
            break
        }
        guard j != 0 else {
            // the remaining is remove
            while i > 0 {
                r.append((-1, i - 1, j - 1))
                i -= 1
            }
            break
        }
        guard src[i - 1] != dest[j - 1]  else {
            // no change
            r.append((0, i - 1, j - 1))
            i -= 1
            j -= 1
            continue
        }
        // check the weight
        if c[i - 1][j] > c[i][j - 1] {
            // is remove
            r.append((-1, i - 1, j - 1))
            i -= 1
        } else {
            // is add
            r.append((+1, i - 1, j - 1))
            j -= 1
        }
    } while i > 0 || j > 0
    
    return r.reversed()
}

private let _SAInputAccessoryViewCenterSection = SAIInputItemPosition.center.rawValue

public let SAIAccessoryDidChangeFrameNotification = "SAIAccessoryDidChangeFrameNotification"
