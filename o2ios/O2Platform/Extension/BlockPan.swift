//
//  BlockPan.swift
//
//
//  Created by Cem Olcay on 12/08/15.
//
//

#if os(iOS) || os(tvOS)

import UIKit

///Make sure you use  "[weak self] (gesture) in" if you are using the keyword self inside the closure or there might be a memory leak
open class BlockPan: UIPanGestureRecognizer {
    private var panAction: ((UIPanGestureRecognizer) -> Void)?

    public override init(target: Any?, action: Selector?) {
        super.init(target: target, action: action)
    }

    public convenience init (action: ((UIPanGestureRecognizer) -> Void)?) {
        self.init()
        self.panAction = action
        self.addTarget(self, action: #selector(BlockPan.didPan(_:)))
    }

    @objc open func didPan (_ pan: UIPanGestureRecognizer) {
        panAction? (pan)
    }
}

#endif
