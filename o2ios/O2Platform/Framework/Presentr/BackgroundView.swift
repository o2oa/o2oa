//
//  BackgroundView.swift
//  Pods
//
//  Created by Daniel Lozano Valdés on 3/20/17.
//
//

import UIKit

class PassthroughBackgroundView: UIView {

    var passthroughViews: [UIView] = []

    var shouldPassthrough = true

    override func hitTest(_ point: CGPoint, with event: UIEvent?) -> UIView? {
        var view = super.hitTest(point, with: event)

        if !shouldPassthrough {
            return view
        }

        if view == self {
            for passthroughView in passthroughViews {
                view = passthroughView.hitTest(convert(point, to: passthroughView), with: event)
                if view != nil {
                    break
                }
            }
        }
        
        return view
    }

}
