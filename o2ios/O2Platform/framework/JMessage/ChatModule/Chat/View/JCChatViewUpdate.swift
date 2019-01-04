//
//  JCChatViewUpdate.swift
//  JChat
//
//  Created by deng on 10/04/2017.
//  Copyright © 2017 HXHG. All rights reserved.
//

import UIKit

internal enum JCChatViewUpdateChangeItem {
    case insert(JCMessageType, at: Int)
    case update(JCMessageType, at: Int)
    case remove(at: Int)
    case move(at: Int,  to: Int)
    
    var at: Int {
        switch self {
        case .insert(_, let at): return at
        case .update(_, let at): return at
        case .remove(let at): return at
        case .move(let at, _): return at
        }
    }
}

internal enum JCChatViewUpdateChange: CustomStringConvertible {
    
    case move(from: Int, to: Int)
    case update(from: Int, to: Int)
    case insert(from: Int, to: Int)
    case remove(from: Int, to: Int)
    
    var from: Int {
        switch self {
        case .move(let from, _): return from
        case .insert(let from, _): return from
        case .update(let from, _): return from
        case .remove(let from, _): return from
        }
    }
    var to: Int {
        switch self {
        case .move(_, let to): return to
        case .insert(_, let to): return to
        case .update(_, let to): return to
        case .remove(_, let to): return to
        }
    }
    
    var isMove: Bool {
        switch self {
        case .move: return true
        default: return false
        }
    }
    var isUpdate: Bool {
        switch self {
        case .update: return true
        default: return false
        }
    }
    var isRemove: Bool {
        switch self {
        case .remove: return true
        default: return false
        }
    }
    var isInsert: Bool {
        switch self {
        case .insert: return true
        default: return false
        }
    }
    
    
    var description: String {
        let from = self.from >= 0 ? "\(self.from)" : "N"
        let to = self.to >= 0 ? "\(self.to)" : "N"
        
        switch self {
        case .move: return "M\(from)/\(to)"
        case .insert: return "A\(from)/\(to)"
        case .update: return "R\(from)/\(to)"
        case .remove: return "D\(from)/\(to)"
        }
    }
    
    func offset(_ offset: Int) -> JCChatViewUpdateChange {
        let from = self.from + offset + max(min(self.from, 0), -1) * offset
        let to = self.to + offset + max(min(self.to, 0), -1) * offset
        // convert
        switch self {
        case .move: return .move(from: from, to: to)
        case .insert: return .insert(from: from, to: to)
        case .update: return .update(from: from, to: to)
        case .remove: return .remove(from: from, to: to)
        }
    }
}

internal class JCChatViewUpdate: NSObject {
    
    
    internal init(newData: JCChatViewData, oldData: JCChatViewData, updateItems: Array<JCChatViewUpdateChangeItem>) {
        self.newData = newData
        self.oldData = oldData
        self.updateItems = updateItems
        super.init()
        self.updateChanges = _computeItemUpdates(newData, oldData, updateItems)
    }
    
    // MARK: compute
    internal func _computeItemUpdates(_ newData: JCChatViewData, _ oldData: JCChatViewData, _ updateItems: Array<JCChatViewUpdateChangeItem>) -> Array<JCChatViewUpdateChange> {
        guard !updateItems.isEmpty else {
            return []
        }
        var allInserts: Array<(Int, JCMessageType)> = []
        var allUpdates: Array<(Int, JCMessageType)> = []
        var allRemoves: Array<(Int)> = []
        var allMoves: Array<(Int, Int)> = []
        
        // get max & min
        let (first, last) = updateItems.reduce((.max, .min)) { result, item -> (Int, Int) in
            
            switch item {
            case .move(let from, let to):
                // ignore for source equ dest
                guard abs(from - to) >= 1 else {
                    return result
                }
                // move message
                allMoves.append((from, to))
                // splite to insert & remove
                if let message = _element(at: from) {
                    allRemoves.append((from))
                    allInserts.append((to + 1, message))
                }
                // from + 1: the selected row will change
                return (min(min(from, to + 1), result.0), max(max(from + 1, to + 1), result.1))
                
            case .remove(let index):
                // remove message
                allRemoves.append((index))
                return (min(index, result.0), max(index + 1, result.1))
                
            case .update(let message, let index):
                // update message
                allUpdates.append((index, message))
                return (min(index, result.0), max(index + 1, result.1))
                
            case .insert(let message, let index):
                // insert message
                allInserts.append((index, message))
                return (min(index, result.0), max(index, result.1))
            }
        }
        // is empty
        guard first != .max && last != .min else {
            return []
        }

        // sort
//        allInserts.sort { $0.0 < $1.0 }
//        allUpdates.sort { $0.0 < $1.0 }
//        allRemoves.sort { $0 < $1 }
//        allMoves.sort { $0.0 < $1.0 }

        let count = oldData.count
        let begin = first - 1 // prev
        let end = last + 1 // next
        
        var ii = allInserts.startIndex
        var iu = allUpdates.startIndex
        var ir = allRemoves.startIndex
//        var im = allMoves.startIndex
        
        // priority: insert > remove > update > move
        
        var items: Array<JCMessageType> = []
        
        // processing
        (first ... last).forEach { index in
            // do you need to insert the operation?
            while ii < allInserts.endIndex && allInserts[ii].0 == index {
                items.append(allInserts[ii].1)
                ii += 1
            }
            // do you need to do this?
            guard index < last && index < count else {
                return
            }
            // do you need to remove the operation?
            while ir < allRemoves.endIndex && allRemoves[ir] == index {
                // adjust previous tl-message & next tl-message, if needed
                if let content = _element(at: index - 1)?.content as? JCMessageTimeLineContent {
                    content.after = nil
                }
                if let content = _element(at: index + 1)?.content as? JCMessageTimeLineContent {
                    content.before = nil
                }
                // move to next operator(prevent repeat operation)
                while ir < allRemoves.endIndex && allRemoves[ir] == index {
                    ir += 1
                }
                // can't update or copy
                return
            }
            // do you need to update the operation?
            while iu < allUpdates.endIndex && allUpdates[iu].0 == index {
                let message = allUpdates[iu].1
                // updating
                items.append(message)
                // adjust previous tl-message & next tl-message, if needed
                if let content = _element(at: index - 1)?.content as? JCMessageTimeLineContent {
                    content.after = message
                }
                if let content = _element(at: index + 1)?.content as? JCMessageTimeLineContent {
                    content.before = message
                }
                // move to next operator(prevent repeat operation)
                while iu < allUpdates.endIndex && allUpdates[iu].0 == index {
                    iu += 1
                }
                // can't copy
                return
            }
            // copy
            items.append(oldData[index])
        }
        // convert messages and replace specify message
        let newItems = items as [JCMessageType]
        let convertedItems = _convert(messages: newItems, first: _element(at: begin), last: _element(at: end - 1))
        let selectedRange = Range<Int>(max(begin, 0) ..< min(end, count))
        let selectedItems = oldData.subarray(with: selectedRange)
        
        // compute index paths
        let start = selectedRange.lowerBound
        // lcs
        let diff = _diff(selectedItems, convertedItems).map { $0.offset(start) }
        // ::
        // replace
        newData.elements = oldData.elements
        newData.replaceSubrange(selectedRange, with: convertedItems)
        
        return diff
    }
    
    // MARK: convert message
    
    private func _convert(messages elements: [JCMessageType], first: JCMessageType?, last: JCMessageType?) -> [JCMessageType] {
        // merge
        let elements = [first].flatMap({ $0 }) + elements + [last].flatMap({ $0 })
        // processing
        return (0 ..< elements.count).reduce(NSMutableArray(capacity: elements.count * 2)) { result, index in
            let current = elements[index]
            result.add(current)
            // continue
            return result
        } as! [JCMessageType]
    }

    internal func _element(at index: Int) -> JCMessageType? {
        guard index >= 0 && index < oldData.count else {
            return nil
        }
        return oldData[index]
    }
    

    // MARK: compare
    private func _equal<T: JCMessageType>(_ lhs: T, _ rhs: T) -> Bool {
        return lhs.identifier == rhs.identifier && lhs.options.state == rhs.options.state
    }
    
    private func _diff<T: JCMessageType>(_ src: Array<T>, _ dest: Array<T>) -> Array<JCChatViewUpdateChange> {
        
        let len1 = src.count
        let len2 = dest.count
        
        var c = [[Int]](repeating: [Int](repeating: 0, count: len2 + 1), count: len1 + 1)
        
        // lcs + 动态规划
        for i in 1 ..< len1 + 1 {
            for j in 1 ..< len2 + 1 {
                if _equal(src[i - 1], (dest[j - 1])) {
                    c[i][j] = c[i - 1][j - 1] + 1
                } else {
                    c[i][j] = max(c[i - 1][j], c[i][j - 1])
                }
            }
        }
        
        var i = len1
        var j = len2
        
        var rms: Array<(from: Int, to: Int)> = []
        var adds: Array<(from: Int, to: Int)> = []
        
        // create the optimal path
        repeat {
            guard i != 0 else {
                // the remaining is add
                while j > 0 {
                    adds.append((from: i - 1, to: j - 1))
                    j -= 1
                }
                break
            }
            guard j != 0 else {
                // the remaining is remove
                while i > 0 {
                    rms.append((from: i - 1, to: j - 1))
                    i -= 1
                }
                break
            }
            guard !_equal(src[i - 1], (dest[j - 1])) else {
                // no change, ignore
                i -= 1
                j -= 1
                continue
            }
            // check the weight
            if c[i - 1][j] > c[i][j - 1] {
                // is remove
                rms.append((from: i - 1, to: j - 1))
                i -= 1
            } else {
                // is add
                adds.append((from: i - 1, to: j - 1))
                j -= 1
            }
        } while i > 0 || j > 0
        
        var results: Array<JCChatViewUpdateChange> = []
        results.reserveCapacity(rms.count + adds.count)
        
        // move(f,t): f = remove(f), t = insert(t), new move(f,t): f = remove(f), t = insert(f)
        // update(f,t): f = remove(f), t = insert(t), new update(f,t): f = remove(f), t = insert(f)
        
        // automatic merge delete and update items
        results.append(contentsOf: rms.map({ item in
            let from = item.from
            let delElement = src[from]
            // can't merge to move item?
            if let addIndex = adds.index(where: { _equal(dest[$0.to], delElement) }) {
                let addItem = adds.remove(at: addIndex)
                return .move(from: from, to: addItem.to)
            }
            // can't merge to update item?
            if let addIndex = adds.index(where: { $0.to == from }) {
                let addItem = adds[addIndex]
                let addElement = dest[addItem.to]
                // the same type is allowed to merge
                if type(of: delElement.content) == type(of: addElement.content) {
                    adds.remove(at: addIndex)
                    return .update(from: from, to: addItem.to)
                }
            }
            return .remove(from: item.from, to: -1)
        }))
        // automatic merge insert items
        results.append(contentsOf: adds.map({ item in
            return .insert(from: -1, to: item.to)
        }))
        
        // sort
        return results.sorted { $0.from < $1.from }
    }
    
    // MARK: property
    internal let newData: JCChatViewData
    internal let oldData: JCChatViewData
    
    internal let updateItems: Array<JCChatViewUpdateChangeItem>
    internal var updateChanges: Array<JCChatViewUpdateChange>?
    
    internal static var minimuxTimeInterval: TimeInterval = 60
}
