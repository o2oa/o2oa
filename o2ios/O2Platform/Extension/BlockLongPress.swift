//
//  BlockLongPress.swift
//
//
//  Created by Cem Olcay on 12/08/15.
//
//

#if os(iOS) || os(tvOS)

import UIKit

///Make sure you use  "[weak self] (gesture) in" if you are using the keyword self inside the closure or there might be a memory leak
open class BlockLongPress: UILongPressGestureRecognizer {
    private var longPressAction: ((UILongPressGestureRecognizer) -> Void)?

    public override init(target: Any?, action: Selector?) {
        super.init(target: target, action: action)
    }

    public convenience init (action: ((UILongPressGestureRecognizer) -> Void)?) {
        self.init()
        longPressAction = action
        addTarget(self, action: #selector(BlockLongPress.didLongPressed(_:)))
    }

    @objc open func didLongPressed(_ longPress: UILongPressGestureRecognizer) {
        if longPress.state == UIGestureRecognizer.State.began {
            longPressAction?(longPress)
        }
    }
}

#endif
