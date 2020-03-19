//
//  SAIToolboxInputView.swift
//  SAC
//
//  Created by SAGESSE on 9/6/16.
//  Copyright © 2016-2017 SAGESSE. All rights reserved.
//

import UIKit

// ## TODO
// [x] SAIToolboxInputView - 数据源
// [x] SAIToolboxInputView - 代理
// [x] SAIToolboxInputView - 竖屏
// [x] SAIToolboxInputView - 横屏
// [x] SAIToolboxInputView - 自定义行/列数量
// [x] SAIToolboxItemView - 选中高亮
// [x] SAIToolboxItemView - 限制最大大小(80x80)
// [x] SAIToolboxInputViewLayout - 快速滑动时性能问题

@objc 
public protocol SAIToolboxInputViewDataSource: NSObjectProtocol {
    
    func numberOfToolboxItems(in toolbox: SAIToolboxInputView) -> Int
    func toolbox(_ toolbox: SAIToolboxInputView, toolboxItemForItemAt index: Int) -> SAIToolboxItem
    
    @objc optional func toolbox(_ toolbox: SAIToolboxInputView, numberOfRowsForSectionAt index: Int) -> Int
    @objc optional func toolbox(_ toolbox: SAIToolboxInputView, numberOfColumnsForSectionAt index: Int) -> Int
}
@objc
public protocol SAIToolboxInputViewDelegate: NSObjectProtocol {
    
    @objc optional func inputViewContentSize(_ inputView: UIView) -> CGSize
    
    @objc optional func toolbox(_ toolbox: SAIToolboxInputView, insetForSectionAt index: Int) -> UIEdgeInsets
    
    @objc optional func toolbox(_ toolbox: SAIToolboxInputView, shouldSelectFor item: SAIToolboxItem) -> Bool
    @objc optional func toolbox(_ toolbox: SAIToolboxInputView, didSelectFor item: SAIToolboxItem) 
    
}

open class SAIToolboxInputView: UIView {
    
    open func reloadData() {
        _contentView.reloadData()
        _updatePageControl()
    }
    
    open override func layoutSubviews() {
        super.layoutSubviews()
        
        if _cacheBounds?.width != bounds.width {
            _cacheBounds = bounds
            _updatePageControl()
        }
    }
    open override var intrinsicContentSize: CGSize {
        return delegate?.inputViewContentSize?(self) ?? CGSize(width: frame.width, height: 253)
    }
    
    open weak var delegate: SAIToolboxInputViewDelegate?
    open weak var dataSource: SAIToolboxInputViewDataSource?
    
    
    @objc func onPageChanged(_ sender: UIPageControl) {
        _contentView.setContentOffset(CGPoint(x: _contentView.bounds.width * CGFloat(sender.currentPage), y: 0), animated: true)
    }
    
    private func _updatePageControl() {
        
        
        let maxCount = _contentViewLayout.numberOfRows(in: 0) * _contentViewLayout.numberOfColumns(in: 0)
        let count = _contentView.numberOfItems(inSection: 0)
        let page = (count + (maxCount - 1)) / maxCount
        let currentPage = min(Int(_contentView.contentOffset.x / _contentView.frame.width), page - 1)
        
        _pageControl.numberOfPages = page
        _pageControl.currentPage = currentPage
        
        let x = CGFloat(currentPage) * _contentView.frame.width
        if _contentView.contentOffset.x != x {
            _contentView.contentOffset = CGPoint(x: x, y: 0)
        }
    }

    private func _init() {
        //_logger.trace()
        
        backgroundColor = .white
        
        _pageControl.numberOfPages = 0
        _pageControl.hidesForSinglePage = true
        _pageControl.pageIndicatorTintColor = UIColor.gray
        _pageControl.currentPageIndicatorTintColor = UIColor.darkGray
        _pageControl.translatesAutoresizingMaskIntoConstraints = false
        _pageControl.backgroundColor = .clear
        _pageControl.addTarget(self, action: #selector(onPageChanged(_:)), for: .valueChanged)
        
        _contentView.delegate = self
        _contentView.dataSource = self
        _contentView.scrollsToTop = false
        _contentView.isPagingEnabled = true
        _contentView.delaysContentTouches = false
        _contentView.showsVerticalScrollIndicator = false
        _contentView.showsHorizontalScrollIndicator = false
        _contentView.register(SAIToolboxItemView.self, forCellWithReuseIdentifier: "Item")
        _contentView.translatesAutoresizingMaskIntoConstraints = false
        _contentView.backgroundColor = .clear
        
        _line.translatesAutoresizingMaskIntoConstraints = false
        _line.layer.backgroundColor = UIColor(netHex: 0xE8E8E8).cgColor
        
        addSubview(_contentView)
        addSubview(_pageControl)
        addSubview(_line)
        
        addConstraint(_SAToolboxLayoutConstraintMake(_contentView, .top, .equal, self, .top))
        addConstraint(_SAToolboxLayoutConstraintMake(_contentView, .left, .equal, self, .left))
        addConstraint(_SAToolboxLayoutConstraintMake(_contentView, .right, .equal, self, .right))
        addConstraint(_SAToolboxLayoutConstraintMake(_contentView, .bottom, .equal, _pageControl, .top))
        
        addConstraint(_SAToolboxLayoutConstraintMake(_pageControl, .left, .equal, self, .left))
        addConstraint(_SAToolboxLayoutConstraintMake(_pageControl, .right, .equal, self, .right))
        addConstraint(_SAToolboxLayoutConstraintMake(_pageControl, .bottom, .equal, self, .bottom))
        addConstraint(_SAToolboxLayoutConstraintMake(_pageControl, .height, .equal, nil, .notAnAttribute, 32))
        
        addConstraint(_SAToolboxLayoutConstraintMake(_line, .top, .equal, self, .top))
        addConstraint(_SAToolboxLayoutConstraintMake(_line, .left, .equal, self, .left))
        addConstraint(_SAToolboxLayoutConstraintMake(_line, .right, .equal, self, .right))
        addConstraint(_SAToolboxLayoutConstraintMake(_line, .height, .equal, nil, .notAnAttribute, 0.5))
    }
    
    private var _cacheBounds: CGRect?
    
    fileprivate lazy var _pageControl: UIPageControl = UIPageControl()
    
    fileprivate lazy var _contentViewLayout: SAIToolboxInputViewLayout = SAIToolboxInputViewLayout()
    fileprivate lazy var _contentView: UICollectionView = UICollectionView(frame: .zero, collectionViewLayout: self._contentViewLayout)
    private lazy var _line: UILabel = UILabel()
    
    public override init(frame: CGRect) {
        super.init(frame: frame)
        _init()
    }
    public required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        _init()
    }
}

// MARK: - UICollectionViewDataSource & SAIToolboxInputViewLayoutDelegate

extension SAIToolboxInputView: UICollectionViewDataSource, SAIToolboxInputViewLayoutDelegate {
    
    public func scrollViewDidScroll(_ scrollView: UIScrollView) {
        _pageControl.currentPage = Int(round(scrollView.contentOffset.x / scrollView.frame.width))
    }
    
    public func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return dataSource?.numberOfToolboxItems(in: self) ?? 0
    }
    
    public func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        return collectionView.dequeueReusableCell(withReuseIdentifier: "Item", for: indexPath)
    }
    public func collectionView(_ collectionView: UICollectionView, willDisplay cell: UICollectionViewCell, forItemAt indexPath: IndexPath) {
        guard let cell = cell as? SAIToolboxItemView else {
            return
        }
        
        cell.item = dataSource?.toolbox(self, toolboxItemForItemAt: indexPath.item)
        cell.handler = self
    }
    
    internal func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: SAIToolboxInputViewLayout, insetForSectionAt index: Int) -> UIEdgeInsets {
        return delegate?.toolbox?(self, insetForSectionAt: index) ?? UIEdgeInsets(top: 12, left: 10, bottom: 12, right: 10)
    }
    internal func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: SAIToolboxInputViewLayout, numberOfRowsForSectionAt index: Int) -> Int {
        return dataSource?.toolbox?(self, numberOfRowsForSectionAt: index) ?? 2
    }
    internal func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: SAIToolboxInputViewLayout, numberOfColumnsForSectionAt index: Int) -> Int {
        return dataSource?.toolbox?(self, numberOfColumnsForSectionAt: index) ?? 4
    }
    
    public func collectionView(_ collectionView: UICollectionView, didSelectItemAt indexPath: IndexPath) {
        collectionView.deselectItem(at: indexPath, animated: true)
        
        guard let item = dataSource?.toolbox(self, toolboxItemForItemAt: indexPath.row) else {
            return
        }
        
        if delegate?.toolbox?(self, shouldSelectFor: item) ?? true {
            delegate?.toolbox?(self, didSelectFor: item)
        }
    }
}

@inline(__always)
internal func _SAToolboxLayoutConstraintMake(_ item: AnyObject, _ attr1: NSLayoutConstraint.Attribute, _ related: NSLayoutConstraint.Relation, _ toItem: AnyObject? = nil, _ attr2: NSLayoutConstraint.Attribute = .notAnAttribute, _ constant: CGFloat = 0, priority: UILayoutPriority = UILayoutPriority.init(1000.0), multiplier: CGFloat = 1, output: UnsafeMutablePointer<NSLayoutConstraint?>? = nil) -> NSLayoutConstraint {
    
    let c = NSLayoutConstraint(item:item, attribute:attr1, relatedBy:related, toItem:toItem, attribute:attr2, multiplier:multiplier, constant:constant)
    c.priority = priority
    if output != nil {
        output?.pointee = c
    }
    
    return c
}
