//
//  BlockPinch.swift
//
//
//  Created by Cem Olcay on 12/08/15.
//
//

#if os(iOS)

import UIKit

///Make sure you use  "[weak self] (gesture) in" if you are using the keyword self inside the closure or there might be a memory leak
open class BlockPinch: UIPinchGestureRecognizer {
    private var pinchAction: ((UIPinchGestureRecognizer) -> Void)?

    public override init(target: Any?, action: Selector?) {
        super.init(target: target, action: action)
    }

    public convenience init (action: ((UIPinchGestureRecognizer) -> Void)?) {
        self.init()
        self.pinchAction = action
        self.addTarget(self, action: #selector(BlockPinch.didPinch(_:)))
    }

    @objc open func didPinch (_ pinch: UIPinchGestureRecognizer) {
        pinchAction? (pinch)
    }
}

#endif
