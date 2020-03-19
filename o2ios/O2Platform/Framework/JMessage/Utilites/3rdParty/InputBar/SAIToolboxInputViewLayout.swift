//
//  SAIToolboxInputViewLayout.swift
//  SAC
//
//  Created by SAGESSE on 9/15/16.
//  Copyright © 2016-2017 SAGESSE. All rights reserved.
//

import UIKit

@objc
internal protocol SAIToolboxInputViewLayoutDelegate: UICollectionViewDelegate {
    
    @objc optional func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: SAIToolboxInputViewLayout, insetForSectionAt index: Int) -> UIEdgeInsets
    @objc optional func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: SAIToolboxInputViewLayout, numberOfRowsForSectionAt index: Int) -> Int
    @objc optional func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: SAIToolboxInputViewLayout, numberOfColumnsForSectionAt index: Int) -> Int
    
}

internal class SAIToolboxInputViewLayout: UICollectionViewLayout {
    
    func numberOfRows(in section: Int) -> Int {
        guard let collectionView = collectionView else {
            return 2
        }
        guard let delegate = collectionView.delegate as? SAIToolboxInputViewLayoutDelegate else {
            return 2
        }
        return delegate.collectionView?(collectionView, layout: self, numberOfRowsForSectionAt: section) ?? 2
    }
    func numberOfColumns(in section: Int) -> Int {
        guard let collectionView = collectionView else {
            return 4
        }
        guard let delegate = collectionView.delegate as? SAIToolboxInputViewLayoutDelegate else {
            return 4
        }
        return delegate.collectionView?(collectionView, layout: self, numberOfColumnsForSectionAt: section) ?? 4
    }
    func contentInset(in section: Int) -> UIEdgeInsets {
        guard let collectionView = collectionView else {
            return .zero
        }
        guard let delegate = collectionView.delegate as? SAIToolboxInputViewLayoutDelegate else {
            return .zero
        }
        return delegate.collectionView?(collectionView, layout: self, insetForSectionAt: section) ?? .zero
    }
    
    weak var delegate: SAIToolboxInputViewLayoutDelegate? {
        return collectionView?.delegate as? SAIToolboxInputViewLayoutDelegate
    }
    
    override var collectionViewContentSize: CGSize {
        
        let count = collectionView?.numberOfItems(inSection: 0) ?? 0
        let maxCount = numberOfRows(in: 0) * numberOfColumns(in: 0)
        let page = (count + (maxCount - 1)) / maxCount
        let frame = collectionView?.frame ?? CGRect.zero
        
        return CGSize(width: frame.width * CGFloat(page) - 1, height: 0)
    }
    override func shouldInvalidateLayout(forBoundsChange newBounds: CGRect) -> Bool {
        if collectionView?.frame.width != newBounds.width {
            return true
        }
        return false
    }
    
    override func prepare() {
        super.prepare()
        _attributesCache = nil
    }
    override func layoutAttributesForElements(in rect: CGRect) -> [UICollectionViewLayoutAttributes]? {
        if let attributes = _attributesCache {
            return attributes
        }
        
        var ats = [UICollectionViewLayoutAttributes]()
        // 生成
        let edg = contentInset(in: 0)
        let frame = collectionView?.bounds ?? .zero
        let count = collectionView?.numberOfItems(inSection: 0) ?? 0
        
        let width = frame.width - edg.left - edg.right
        let height = frame.height - edg.top - edg.bottom
        let irows = numberOfRows(in: 0)
        let icolumns = numberOfColumns(in: 0)
        let rows = CGFloat(irows)
        let columns = CGFloat(icolumns)
        
        let w: CGFloat = min(trunc((width - 8 * columns) / columns), 80)
        let h: CGFloat = min(trunc((height - 4 * rows) / rows), 80)
        let yg: CGFloat = (height / rows) - h
        let xg: CGFloat = (width / columns) - w
        // fill
        for i in 0 ..< count {
            // 计算。
            let r = CGFloat((i / icolumns) % irows)
            let c = CGFloat((i % icolumns))
            let idx = IndexPath(item: i, section: 0)
            let page = CGFloat(i / (irows * icolumns))
            
            let a = self.layoutAttributesForItem(at: idx) ?? UICollectionViewLayoutAttributes(forCellWith: idx)
            let x = edg.left + xg / 2 + c * (w + xg) + page * frame.width
            let y = edg.top + yg / 2 + r * (h + yg)
            a.frame = CGRect(x: x, y: y, width: w, height: h)
            
            ats.append(a)
        }
        _attributesCache = ats
        return ats
    }
    
    private var _defaultRows: Int = 2
    private var _defaultColumns: Int = 4
    
    private var _attributesCache: [UICollectionViewLayoutAttributes]?
}
