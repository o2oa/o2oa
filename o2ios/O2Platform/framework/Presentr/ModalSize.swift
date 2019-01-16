//
//  ModalSize.swift
//  Presentr
//
//  Created by Daniel Lozano on 7/6/16.
//  Copyright © 2016 danielozano. All rights reserved.
//

import UIKit

/**
 Descibes a presented modal's size dimension (width or height). It is meant to be non-specific, but the exact position can be calculated by calling the 'calculate' methods, passing in the 'parentSize' which only the Presentation Controller should be aware of.

 - Default:     Default size. Will use Presentr's default margins to calculate size of presented controller. This is the size the .Popup presentation type uses.
 - Half:        Half of the screen.
 - Full:        Full screen.
 - Custom:      Custom fixed size.
 - Fluid:       Custom percentage-based fluid size.
 - SideMargin:  Uses side margins to calculate size.
 */
public enum ModalSize {

    case `default`
    case half
    case full
    case custom(size: Float)
    case fluid(percentage: Float)
    case sideMargin(value: Float)

    /**
     Calculates the exact width value for the presented view controller.

     - parameter parentSize: The presenting view controller's size. Provided by the presentation controller.

     - returns: Exact float width value.
     */
    func calculateWidth(_ parentSize: CGSize) -> Float {
        switch self {
        case .default:
            return floorf(Float(parentSize.width) - (PresentrConstants.Values.defaultSideMargin * 2.0))
        case .half:
            return floorf(Float(parentSize.width) / 2.0)
        case .full:
            return Float(parentSize.width)
        case .custom(let size):
            return size
        case .fluid(let percentage):
            return floorf(Float(parentSize.width) * percentage)
        case .sideMargin(let value):
            return floorf(Float(parentSize.width) - value * 2.0)
        }
    }

    /**
     Calculates the exact height value for the presented view controller.

     - parameter parentSize: The presenting view controller's size. Provided by the presentation controller.

     - returns: Exact float height value.
     */
    func calculateHeight(_ parentSize: CGSize) -> Float {
        switch self {
        case .default:
            return floorf(Float(parentSize.height) * PresentrConstants.Values.defaultHeightPercentage)
        case .half:
            return floorf(Float(parentSize.height) / 2.0)
        case .full:
            return Float(parentSize.height)
        case .custom(let size):
            return size
        case .fluid(let percentage):
            return floorf(Float(parentSize.height) * percentage)
        case .sideMargin(let value):
            return floorf(Float(parentSize.height) - value * 2)
        }
    }

}
