//
//  ModalCenterPosition.swift
//  Presentr
//
//  Created by Daniel Lozano on 7/6/16.
//  Copyright © 2016 danielozano. All rights reserved.
//

import UIKit

/**
 Describes the presented presented view controller's center position. It is meant to be non-specific, but we can use the 'calculatePoint' method when we want to calculate the exact point by passing in the 'containerBounds' rect that only the presentation controller should be aware of.

 - Center:       Center of the screen.
 - TopCenter:    Center of the top half of the screen.
 - BottomCenter: Center of the bottom half of the screen.
 - Custom: A custom center position using a CGPoint which represents the center point of the presented view controller.
 - Custom: A custom center position to be calculated, using a CGPoint which represents the origin of the presented view controller.
 */
public enum ModalCenterPosition {

    case center
    case topCenter
    case bottomCenter
    case custom(centerPoint: CGPoint)
    case customOrigin(origin: CGPoint)

    /**
     Calculates the exact position for the presented view controller center.

     - parameter containerBounds: The container bounds the controller is being presented in.

     - returns: CGPoint representing the presented view controller's center point.
     */
    func calculateCenterPoint(_ containerFrame: CGRect) -> CGPoint? {
        switch self {
        case .center:
            return CGPoint(x: containerFrame.origin.x + (containerFrame.width / 2),
                           y: containerFrame.origin.y + (containerFrame.height / 2))
        case .topCenter:
            return CGPoint(x: containerFrame.origin.x + (containerFrame.width / 2),
                           y: containerFrame.origin.y + (containerFrame.height * (1 / 4) - 1))
        case .bottomCenter:
            return CGPoint(x: containerFrame.origin.x + (containerFrame.width / 2),
                           y: containerFrame.origin.y + (containerFrame.height * (3 / 4)))
        case .custom(let point):
            return point
        case .customOrigin(_):
            return nil
        }
    }

    func calculateOrigin() -> CGPoint? {
        switch self {
        case .customOrigin(let origin):
            return origin
        default:
            return nil
        }
    }

}
