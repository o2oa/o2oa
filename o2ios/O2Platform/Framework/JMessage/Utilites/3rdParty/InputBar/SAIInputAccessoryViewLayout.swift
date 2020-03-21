//
//  SAIInputAccessoryViewLayout.swift
//  SAIInputBar
//
//  Created by SAGESSE on 8/3/16.
//  Copyright © 2016-2017 SAGESSE. All rights reserved.
//

import UIKit

internal class SAIInputAccessoryViewLayout: UICollectionViewLayout {
    class Line {
        var frame: CGRect
        var inset: UIEdgeInsets
        var section: Int
        var attributes: [Attributes]
        
        var cacheMaxWidth: CGFloat?
        var cacheMaxHeight: CGFloat?
        
        init(_ firstItem: Attributes, _ inset: UIEdgeInsets = .zero) {
            self.frame = firstItem.frame
            self.section = firstItem.indexPath.section
            self.inset = inset
            self.attributes = [firstItem]
            
        }
        
        func addItem(_ item: Attributes, _ spacing: CGFloat) {
            
            let x = min(frame.minX, item.frame.minX)
            let y = min(frame.minY, item.frame.minY)
            let width = frame.width + spacing + item.size.width
            let height = max(frame.height, item.size.height)
            
            frame = CGRect(x: x, y: y, width: width, height: height)
            attributes.append(item)
        }
        func canAddItem(_ item: Attributes, _ width: CGFloat, _ spacing: CGFloat) -> Bool {
            let nWidth = frame.width + spacing + item.size.width
            return nWidth <= width - inset.left - inset.right
        }
        
        func move(toPoint point: CGPoint) {
            let dx = point.x - frame.minX
            let dy = point.y - frame.minY
            
            attributes.forEach {
                $0.frame = $0.frame.offsetBy(dx: dx, dy: dy)
            }
            
            frame.origin = point
        }
        func layout(atPoint point: CGPoint, maxWidth: CGFloat, maxHeight: CGFloat, _ spacing: CGFloat) {
            // 如果布局没有改变直接移动就好了
            if cacheMaxWidth == maxWidth && cacheMaxHeight == maxHeight {
                move(toPoint: point)
                return
            }
            frame.origin = point
            
            var left: CGFloat = 0
            var right: CGFloat = 0
            
            var sp: CGFloat = -1
            var lsp: CGFloat = inset.left
            var rsp: CGFloat = inset.right
            
            var centerCount = 0
            var centerWidth = CGFloat(0)
            
            // vertical alignment
            let alignY = { (item: Attributes) -> CGFloat in
                if item.alignemt.contains(.Top) {
                    // aligned to the top
                    return 0
                }
                if item.alignemt.contains(.VResize) {
                    // resize
                    return 0
                }
                if item.alignemt.contains(.Bottom) {
                    // aligned to the bottom
                    return maxHeight - item.size.height
                }
                // aligned to the center
                return (maxHeight - item.size.height) / 2
            }
            // 从右边开始计算一直到第一个非Right的元素
            _ = attributes.reversed().index {
                if $0.alignemt.contains(.Right)  {
                    // aligned to the right
                    let nx = point.x + (maxWidth - right - rsp - $0.size.width)
                    let ny = point.y + (alignY($0))
                    
                    $0.frame = CGRect(x: nx, y: ny, width: $0.size.width, height: $0.size.height)
                    
                    right = $0.size.width + rsp + right
                    rsp = spacing
                
                    return false
                }
                if $0.alignemt.contains(.HCenter) {
                    centerCount += 1
                    centerWidth += $0.size.width
                    return false
                }
                return true
            }
            // 然后从左边开始计算到右边第一个非right
            _ = attributes.index {
                if $0.alignemt.contains(.Right) {
                    return true
                }
                if $0.alignemt.contains(.Left) {
                    // aligned to the left
                    let nx = point.x + (left + lsp)
                    let ny = point.y + (alignY($0))
                    
                    $0.frame = CGRect(x: nx, y: ny, width: $0.size.width, height: $0.size.height)
                    
                    left = left + lsp + $0.size.width
                    lsp = spacing
                    
                } else if $0.alignemt.contains(.HResize) {
                    // resize
                    let nx = point.x + (left + lsp)
                    let ny = point.y + (alignY($0))
                    let nwidth = maxWidth - left - lsp - right - rsp
                    let nheight = max(maxHeight, $0.size.height)
                    
                    $0.frame = CGRect(x: nx, y: ny, width: nwidth, height: nheight)
                    
                    left = left + lsp + nwidth
                    lsp = spacing
                    
                } else {
                    // NOTE: center must be calculated finally
                    if sp < 0 {
                        sp = (maxWidth - right - left - centerWidth) / CGFloat(centerCount + 1)
                    }
                    // aligned to the center
                    let nx = point.x + (left + sp)
                    let ny = point.y + (alignY($0))
                    
                    $0.frame = CGRect(x: nx, y: ny, width: $0.size.width, height: $0.size.height)
                    
                    left = left + sp + $0.size.width
                }
                return false
            }
            
            // 缓存
            cacheMaxWidth = maxWidth
            cacheMaxHeight = maxHeight
        }
    }
    struct Alignment: OptionSet {
        var rawValue: Int
        
        static var None = Alignment(rawValue: 0x0000)
        
        static var Top = Alignment(rawValue: 0x0100)
        static var Bottom = Alignment(rawValue: 0x0200)
        static var VCenter = Alignment(rawValue: 0x0400)
        static var VResize = Alignment(rawValue: 0x0800)
        
        static var Left = Alignment(rawValue: 0x0001)
        static var Right = Alignment(rawValue: 0x0002)
        static var HCenter = Alignment(rawValue: 0x0004)
        static var HResize = Alignment(rawValue: 0x0008)
    }
    class Attributes: UICollectionViewLayoutAttributes {
        var item: SAIInputItem?
        var alignemt: Alignment = .None
        
        var cacheSize: CGSize?
    }
    
    // MARK: Property
    
    var minimumLineSpacing: CGFloat = 8
    var minimumInteritemSpacing: CGFloat = 8

    var contentInsets: UIEdgeInsets {
        if isIPhoneX {
            return UIEdgeInsets(top: 8, left: 10, bottom: 20, right: 10)
        }
        return UIEdgeInsets(top: 8, left: 10, bottom: 8, right: 10)
    }
    
    // MARK: Invalidate
    
    override func prepare() {
        super.prepare()
    }
    override var collectionViewContentSize: CGSize {
        return collectionView?.frame.size ?? CGSize.zero
    }
    override func shouldInvalidateLayout(forBoundsChange newBounds: CGRect) -> Bool {
        if collectionView?.frame.width != newBounds.width {
            return true
        }
        return false
    }
    override func invalidateLayout(with context: UICollectionViewLayoutInvalidationContext) {
        super.invalidateLayout(with: context)
        _invalidateLayoutCache(context.invalidateEverything)
    }
    
    // MAKR: Layout
    
    override func layoutAttributesForElements(in rect: CGRect) -> [UICollectionViewLayoutAttributes]? {
        return _layoutIfNeed(inRect: rect)
    }
    override func layoutAttributesForItem(at indexPath: IndexPath) -> UICollectionViewLayoutAttributes? {
        return _layoutAttributesForItemAtIndexPath(indexPath)
    }
    
    // MARK: Change Animation
    
    override func prepare(forCollectionViewUpdates updateItems: [UICollectionViewUpdateItem]) {
        super.prepare(forCollectionViewUpdates: updateItems)
        
        
        updateItems.forEach {
            switch $0.updateAction {
            case .insert:
                add.insert($0.indexPathAfterUpdate!)
            case .delete:
                rm.insert($0.indexPathBeforeUpdate!)
            case .reload:
                reload.insert($0.indexPathAfterUpdate!)
                //rm.insert($0.indexPathBeforeUpdate!)
            default:
                break
            }
        }
    }
    override func finalizeCollectionViewUpdates() {
        super.finalizeCollectionViewUpdates()
        reload = []
        add = []
        rm = []
    }
    
    override func initialLayoutAttributesForAppearingItem(at itemIndexPath: IndexPath) -> UICollectionViewLayoutAttributes? {
        if add.contains(itemIndexPath) {
            // 新增, 使用默认动画 
            add.remove(itemIndexPath)
            return nil
        }
        let attr = _layoutAttributesForItemAtIndexPath(itemIndexPath)
        if reload.contains(itemIndexPath) {
            let attro = _layoutAttributesForItemAtOldIndexPath(itemIndexPath)?.copy() as? Attributes
            
            attro?.alpha = 0
            
            reload.remove(itemIndexPath)
            return attro
        }
        return attr
    }
    override func finalLayoutAttributesForDisappearingItem(at itemIndexPath: IndexPath) -> UICollectionViewLayoutAttributes? {
        if rm.contains(itemIndexPath) {
            // 删除, 使用默认动画 
            rm.remove(itemIndexPath)
            return nil
        }
        let attr = _layoutAttributesForItemAtOldIndexPath(itemIndexPath)
        if reload.contains(itemIndexPath) {
            let attrn = _layoutAttributesForItemAtIndexPath(itemIndexPath)?.copy() as? Attributes
            attrn?.alpha = 0
            return attrn
        }
        //attr?.alpha = 1
        return attr
    }
    
    func sizeThatFits(_ maxSize: CGSize, atPosition position: SAIInputItemPosition) -> CGSize {
        if let size = _cacheLayoutSizes[position] {
            return size
        }
        let maxRect = CGRect(x: 0, y: 0, width: maxSize.width, height: maxSize.height)
        let mls = minimumLineSpacing
        let newSize = _lines(atPosition: position, inRect: maxRect).reduce(CGSize(width: 0, height: -mls)) {
            CGSize(width: max($0.width, $1.frame.width),
                       height: $0.height + mls + $1.frame.height)
        }
        let size = CGSize(width: newSize.width, height: max(newSize.height, 0))
        _cacheLayoutSizes[position] = size
        return size
    }
    
    func invalidateLayout(atPosition position: SAIInputItemPosition) {
        _invalidateLayoutAllCache(atPosition: position)
    }
    func invalidateLayoutIfNeeded(atPosition position: SAIInputItemPosition) {
        guard let attributes = _cacheLayoutAllAttributes[position] else {
            return
        }
        var itemIsChanged = false
        var sizeIsChanged = false
        var boundsIsChanged = false
        
        let barItems = _barItems(atPosition: position)
        
        // 数量不同. 重置
        if attributes.count != barItems.count {
            itemIsChanged = true
        } else {
            for index in 0 ..< attributes.count {
                let attr = attributes[index]
                let item = barItems[index]
                
                if attr.item != item {
                    itemIsChanged = true
                }
                if attr.cacheSize != item.size {
                    sizeIsChanged = true
                    attr.cacheSize = nil
                }
            }
        }
        if let lines = _cacheLayoutAllLines[position] {
            lines.forEach {
                if $0.cacheMaxWidth != nil && $0.cacheMaxWidth != collectionView?.frame.width {
                    boundsIsChanged = true
                }
            }
        }
        
        if itemIsChanged {
            _invalidateLayoutAllCache(atPosition: position)
        } else if sizeIsChanged {
            _invalidateLayoutLineCache(atPosition: position)
        } else if boundsIsChanged {
            _invalidateLayoutLineCache(atPosition: position)
        } else {
            // no changed
        }
    }
    
    // MARK: private
    
    private func _layoutIfNeed(inRect rect: CGRect) -> [Attributes] {
        if let attributes = _cacheLayoutedAttributes {
            return attributes
        }
        
        let mis = minimumInteritemSpacing // 列间隔
        let mls = minimumLineSpacing // 行间隔
        
        var y = contentInsets.top
        var attributes = [Attributes]()
        
        [.top, .center, .bottom].forEach {
            _lines(atPosition: $0, inRect: rect).forEach {
                $0.layout(atPoint: CGPoint(x: 0, y: y), maxWidth: rect.width, maxHeight: $0.frame.height, mis)
                y = y + mls + $0.frame.height
                attributes.append(contentsOf: $0.attributes)
            }
        }
        
        // cache => save
        _cacheLayoutAllAttributesOfPrevious = _cacheLayoutAllAttributesOfCurrent
        _cacheLayoutAllAttributesOfCurrent = _cacheLayoutAllAttributes
        _cacheLayoutedAttributes = attributes
        
        return attributes
    }
    
    private func _layoutAttributesForItemAtIndexPath(_ indexPath: IndexPath) -> Attributes? {
        if let position = SAIInputItemPosition(rawValue: (indexPath as NSIndexPath).section) {
            if let attributes = _cacheLayoutAllAttributesOfCurrent[position], (indexPath as NSIndexPath).item < attributes.count {
                return attributes[(indexPath as NSIndexPath).item]
            }
        }
        return nil
    }
    private func _layoutAttributesForItemAtOldIndexPath(_ indexPath: IndexPath) -> Attributes? {
        if let position = SAIInputItemPosition(rawValue: (indexPath as NSIndexPath).section) {
            if let attributes = _cacheLayoutAllAttributesOfPrevious[position], (indexPath as NSIndexPath).item < attributes.count {
                return attributes[(indexPath as NSIndexPath).item]
            }
        }
        return nil
    }
    
    private func _linesWithAttributes(_ attributes: [Attributes], inRect rect: CGRect) -> [Line] {
        return attributes.reduce([Line]()) {
            // update cache
            if $1.cacheSize == nil || $1.cacheSize != $1.item?.size {
                $1.cacheSize = $1.item?.size
                $1.size = $1.cacheSize ?? CGSize.zero
            }
            // check the width, if you can't hold, then create a new line
            guard let line = $0.last, line.canAddItem($1, rect.width, minimumInteritemSpacing) else {
                var lines = $0
                lines.append(Line($1, contentInsets))
                return lines
            }
            line.addItem($1, minimumInteritemSpacing)
            return $0
        }
    }
    private func _attributesWithBarItems(_ barItems: [SAIInputItem], atPosition position: SAIInputItemPosition) -> [Attributes] {
        // 查找左对齐和右对齐的item
        let ax = barItems.enumerated().reduce((-1, barItems.count)) {
            if $1.element.alignment.rawValue & 0x00FF == 1 {
                return (max($0.0, $1.offset), barItems.count)
            }
            if $1.element.alignment.rawValue & 0x00FF == 2 {
                return ($0.0, min($0.1, $1.offset))
            }
            return $0
        }
        return barItems.enumerated().map {
            let idx = IndexPath(item: $0, section: position.rawValue)
            let attr = Attributes(forCellWith: idx)
            
            attr.item = $1
            attr.size = $1.size
            attr.cacheSize = $1.size
            attr.alignemt = Alignment(rawValue: $1.alignment.rawValue)
            
            // 额外处理水平对齐
            if $0 <= ax.0 {
                attr.alignemt.formUnion(.Left)
            } else if $0 >= ax.1 {
                attr.alignemt.formUnion(.Right)
            } else {
                attr.alignemt.formUnion(.HCenter)
            }
            // 额外处理垂直对齐
            if attr.alignemt.rawValue & 0xFF00 == 0 {
                attr.alignemt.formUnion(.Bottom)
            }
            
            // 特殊处理
            if position == .center {
                // 强制resize
                attr.alignemt = [.HResize, .VResize]
            } else if position == .left {
                // 强制左对齐
                attr.alignemt.formUnion(.Left)
            } else if position == .right {
                // 强制右对齐
                attr.alignemt.formUnion(.Right)
            }
            
            return attr
        }
    }
    private func _lines(atPosition position: SAIInputItemPosition, inRect rect: CGRect) -> [Line] {
        if let lines = _cacheLayoutAllLines[position] {
            return lines
        }
        let attributes = { () -> [Attributes] in
            if position == .center {
                let a1 = self._attributes(atPosition: .left)
                let a2 = self._attributes(atPosition: .center)
                let a3 = self._attributes(atPosition: .right)
                return a1 + a2 + a3
            }
            return self._attributes(atPosition: position)
        }()
        let lines = _linesWithAttributes(attributes, inRect: rect)
        _cacheLayoutAllLines[position] = lines
        return lines
    }
    private func _attributes(atPosition position: SAIInputItemPosition) -> [Attributes] {
        if let attributes = _cacheLayoutAllAttributes[position] {
            return attributes
        }
        let barItems = _barItems(atPosition: position)
        let attributes = _attributesWithBarItems(barItems, atPosition: position)
        _cacheLayoutAllAttributes[position] = attributes
        return attributes
    }
    private func _barItems(atPosition position: SAIInputItemPosition) -> [SAIInputItem] {
        // 如果不是这样的话... 直接报错(高耦合, 反正不重用)
        let ds = collectionView?.dataSource as! SAIInputAccessoryView
        return ds.barItems(atPosition: position)
    }
    
    private func _invalidateLayoutCache(_ force: Bool) {
        
        guard !force else {
            _invalidateLayoutAllCache()
            return
        }
        // 计算出变更的点
        [.top, .left, .center, .right, .bottom].forEach {
            invalidateLayoutIfNeeded(atPosition: $0)
        }
    }
    
    private func _invalidateLayoutAllCache() {
        
        _cacheLayoutSizes.removeAll(keepingCapacity: true)
        _cacheLayoutAllLines.removeAll(keepingCapacity: true)
        _cacheLayoutAllAttributes.removeAll(keepingCapacity: true)
        _cacheLayoutedAttributes = nil
    }
    private func _invalidateLayoutAllCache(atPosition position: SAIInputItemPosition) {
        
        _cacheLayoutSizes.removeValue(forKey: position)
        _cacheLayoutAllLines.removeValue(forKey: position)
        _cacheLayoutAllAttributes.removeValue(forKey: position)
        
        // center总是要清的
        if position == .left || position == .right {
            _cacheLayoutSizes.removeValue(forKey: .center)
            _cacheLayoutAllLines.removeValue(forKey: .center)
            _cacheLayoutAllAttributes.removeValue(forKey: .center)
        }
        
        _cacheLayoutedAttributes = nil
    }
    private func _invalidateLayoutLineCache(atPosition position: SAIInputItemPosition) {
        
        _cacheLayoutSizes.removeValue(forKey: position)
        _cacheLayoutAllLines.removeValue(forKey: position)
        
        // center总是要清的
        if position == .left || position == .right {
            _cacheLayoutSizes.removeValue(forKey: .center)
            _cacheLayoutAllLines.removeValue(forKey: .center)
            _cacheLayoutAllAttributes[.center]?.forEach {
                $0.cacheSize = nil
            }
        }
        
        _cacheLayoutedAttributes = nil
    }
    
    // MARK: Cache
    
    var rm: Set<IndexPath> = []
    var add: Set<IndexPath> = []
    var reload: Set<IndexPath> = []
    
    var _cacheLayoutAllAttributesOfCurrent: [SAIInputItemPosition: [Attributes]] = [:]
    var _cacheLayoutAllAttributesOfPrevious: [SAIInputItemPosition: [Attributes]] = [:]
    
    var _cacheLayoutSizes: [SAIInputItemPosition: CGSize] = [:]
    var _cacheLayoutAllLines: [SAIInputItemPosition: [Line]] = [:]
    var _cacheLayoutAllAttributes: [SAIInputItemPosition: [Attributes]] = [:]
    
    var _cacheLayoutedAttributes: [Attributes]?
    
    // MARK: Init
    
    override init() {
        super.init()
    }
    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
    }
    deinit {
    }
}
