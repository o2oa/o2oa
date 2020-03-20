//
//  JCEmoticonInputViewLayout.swift
//  JChat
//
//  Created by deng on 2017/3/9.
//  Copyright © 2017年 HXHG. All rights reserved.
//

import UIKit

@objc internal protocol JCEmoticonInputViewDelegateLayout: UICollectionViewDelegate {
    
    @objc optional func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: JCEmoticonInputViewLayout, groupAt index: Int) -> JCEmoticonGroup?
    @objc optional func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: JCEmoticonInputViewLayout, insetForGroupAt index: Int) -> UIEdgeInsets
    @objc optional func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: JCEmoticonInputViewLayout, numberOfRowsForGroupAt index: Int) -> Int
    @objc optional func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: JCEmoticonInputViewLayout, numberOfColumnsForGroupAt index: Int) -> Int
}

internal class JCEmoticonInputViewLayout: UICollectionViewLayout {
    override var collectionViewContentSize: CGSize {
        if let size = _cacheContentSize {
            return size
        }
        guard let collectionView = collectionView else {
            return .zero
        }
        let width = collectionView.frame.width
        let count = (0 ..< collectionView.numberOfSections).reduce(0) {
            return $0 + numberOfPages(in: $1)
        }
        let size = CGSize(width: CGFloat(count) * width, height: 0)
        _cacheContentSize = size
        return size
    }
    override func shouldInvalidateLayout(forBoundsChange newBounds: CGRect) -> Bool {
        if collectionView?.frame.width != newBounds.width {
            return true
        }
        return false
    }
    
    override func prepare() {
        super.prepare()
        _cacheContentSize = nil
        _cacheLayoutAttributes = nil
    }
    override func invalidateLayout() {
        super.invalidateLayout()
        
        _cacheContentSize = nil
        _cacheLayoutAttributes = nil
    }
    
    override func layoutAttributesForElements(in rect: CGRect) -> [UICollectionViewLayoutAttributes]? {
        guard let collectionView = collectionView else {
            return nil
        }
        if let attributes = _cacheLayoutAttributes {
            return attributes
        }
        var allAttributes = [UICollectionViewLayoutAttributes]()
        
        var x = CGFloat(0)
        let width = collectionView.frame.width
        let height = collectionView.frame.height
        
        (0 ..< collectionView.numberOfSections).forEach { section in
            (0 ..< collectionView.numberOfItems(inSection: section)).forEach { item in
                let idx = IndexPath(item: item, section: section)
                let attributes = layoutAttributesForItem(at: idx) ?? UICollectionViewLayoutAttributes(forCellWith: idx)
                
                attributes.frame = CGRect(x: x, y: 0, width: width, height: height)
                x += width
                
                allAttributes.append(attributes)
            }
        }
        _cacheLayoutAttributes = allAttributes
        return allAttributes
    }
    
    func page(at indexPath: IndexPath) -> JCEmoticonPage {
        return _pages(at: indexPath.section, with: Int(collectionView?.frame.width ?? 0))[indexPath.item]
    }
    func pages(in section: Int) -> [JCEmoticonPage] {
        return _pages(at: section, with: Int(collectionView?.frame.width ?? 0))
    }
    
    func numberOfPages(in section: Int) -> Int {
        return _numberOfPages(in: section, with: Int(collectionView?.frame.width ?? 0))
    }
    
    func numberOfRows(in section: Int) -> Int {
        guard let collectionView = collectionView else {
            return 3
        }
        guard let delegate = collectionView.delegate as? JCEmoticonInputViewDelegateLayout else {
            return 3
        }
        return delegate.collectionView?(collectionView, layout: self, numberOfRowsForGroupAt: section) ?? 3
    }
    func numberOfColumns(in section: Int) -> Int {
        guard let collectionView = collectionView else {
            return 7
        }
        guard let delegate = collectionView.delegate as? JCEmoticonInputViewDelegateLayout else {
            return 7
        }
        return delegate.collectionView?(collectionView, layout: self, numberOfColumnsForGroupAt: section) ?? 7
    }
    func contentInset(in section: Int) -> UIEdgeInsets {
        guard let collectionView = collectionView else {
            return .zero
        }
        guard let delegate = collectionView.delegate as? JCEmoticonInputViewDelegateLayout else {
            return .zero
        }
        return delegate.collectionView?(collectionView, layout: self, insetForGroupAt: section) ?? .zero
    }
    
    private func _group(at index: Int) -> JCEmoticonGroup? {
        guard let collectionView = collectionView else {
            return nil
        }
        guard let delegate = collectionView.delegate as? JCEmoticonInputViewDelegateLayout else {
            return nil
        }
        return delegate.collectionView?(collectionView, layout: self, groupAt: index)
    }
    private func _pagesWithoutCache(at index: Int, with width: Int) -> [JCEmoticonPage]  {
        guard let group = _group(at: index) else {
            return []
        }
        
        let inset = contentInset(in: index)
        let rows = CGFloat(numberOfRows(in: index))
        let columns = CGFloat(numberOfColumns(in: index))
        
        let bounds = CGRect(origin: .zero, size: collectionView?.frame.size ?? .zero)
        let rect = bounds.inset(by: inset)
        
        let type = group.type
        let size = CGSize(width: min(trunc((rect.width - 8 * columns) / columns), 80),
                          height: min(trunc((rect.height - 8 * rows) / rows), 80))
        
        let nlsp = (rect.height / rows) - size.height
        let nisp = (rect.width / columns) - size.width
        
        return group.emoticons.reduce([]) {
            if let page = $0.last, page.addEmoticon($1) {
                return $0
            }
            return $0 + [JCEmoticonPage($1, size, rect, bounds, nlsp, nisp, type)]
        }
    }
    private func _pages(at index: Int, with width: Int) -> [JCEmoticonPage]  {
        if let pages = _allPages[width]?[index] {
            return pages
        }
        let pages = _pagesWithoutCache(at: index, with: width)
        if _allPages[width] == nil {
            _allPages[width] = [:]
        }
        _allPages[width]?[index] = pages
        return pages
    }
    private func _numberOfPages(in index: Int, with width: Int) -> Int {
        return _allPages[width]?[index]?.count ?? _pages(at: index, with: width).count
    }
    
    private var _cacheContentSize: CGSize?
    private var _cacheLayoutAttributes: [UICollectionViewLayoutAttributes]?
    
    // width + section + pages
    private lazy var _allPages: [Int: [Int: [JCEmoticonPage]]] = [:]
}
