//
//  Presentr.swift
//  Presentr
//
//  Created by Daniel Lozano on 5/10/16.
//  Copyright © 2016 Icalia Labs. All rights reserved.
//

import Foundation
import UIKit

public enum PresentrConstants {

    public enum Values {
        public static let defaultSideMargin: Float = 30.0
        public static let defaultHeightPercentage: Float = 0.66
    }

    public enum Strings {
        public static let alertTitle = "Alert"
        public static let alertBody = "This is an alert."
    }
    
}

public enum DismissSwipeDirection {

    case `default`
    case bottom
    case top

}

// MARK: - PresentrDelegate

/**
 The 'PresentrDelegate' protocol defines methods that you use to respond to changes from the 'PresentrController'. All of the methods of this protocol are optional.
 */
@objc public protocol PresentrDelegate {
    /**
     Asks the delegate if it should dismiss the presented controller on the tap of the outer chrome view.

     Use this method to validate requirments or finish tasks before the dismissal of the presented controller.

     After things are wrapped up and verified it may be good to dismiss the presented controller automatically so the user does't have to close it again.

     - parameter keyboardShowing: Whether or not the keyboard is currently being shown by the presented view.
     - returns: False if the dismissal should be prevented, otherwise, true if the dimissal should occur.
     */
    @objc optional func presentrShouldDismiss(keyboardShowing: Bool) -> Bool
}

/// Main Presentr class. This is the point of entry for using the framework.
public class Presentr: NSObject {

    /// This must be set during initialization, but can be changed to reuse a Presentr object.
    public var presentationType: PresentationType

    /// The type of transition animation to be used to present the view controller. This is optional, if not provided the default for each presentation type will be used.
    public var transitionType: TransitionType?

    /// The type of transition animation to be used to dismiss the view controller. This is optional, if not provided transitionType or default value will be used.
    public var dismissTransitionType: TransitionType?

    /// Should the presented controller have rounded corners. Each presentation type has its own default if nil.
    public var roundCorners: Bool?

    /// Radius of rounded corners for presented controller if roundCorners is true. Default is 4.
    public var cornerRadius: CGFloat = 20

    /// Shadow settings for presented controller.
    public var dropShadow: PresentrShadow?

    /// Should the presented controller dismiss on background tap. Default is true.
    public var dismissOnTap = true

    /// Should the presented controller dismiss on Swipe inside the presented view controller. Default is false.
    public var dismissOnSwipe = false

    /// If dismissOnSwipe is true, the direction for the swipe. Default depends on presentation type.
    public var dismissOnSwipeDirection: DismissSwipeDirection = .default

    /// Should the presented controller use animation when dismiss on background tap or swipe. Default is true.
    public var dismissAnimated = true

    /// Color of the background. Default is Black.
    public var backgroundColor = UIColor.black

    /// Opacity of the background. Default is 0.7.
    public var backgroundOpacity: Float = 0.7

    /// Should the presented controller blur the background. Default is false.
    public var blurBackground = false

    /// The type of blur to be applied to the background. Ignored if blurBackground is set to false. Default is Dark.
    public var blurStyle: UIBlurEffect.Style = .dark

    /// A custom background view to be added on top of the regular background view.
    public var customBackgroundView: UIView?
    
    /// How the presented view controller should respond to keyboard presentation.
    public var keyboardTranslationType: KeyboardTranslationType = .none

    /// When a ViewController for context is set this handles what happens to a tap when it is outside the context. True will ignore tap and pass the tap to the background controller, false will handle the tap and dismiss the presented controller. Default is false.
    public var shouldIgnoreTapOutsideContext = false

    /// Uses the ViewController's frame as context for the presentation. Imitates UIModalPresentation.currentContext
    public weak var viewControllerForContext: UIViewController? {
        didSet {
            guard let viewController = viewControllerForContext, let view = viewController.view else {
                contextFrameForPresentation = nil
                return
            }
            let correctedOrigin = view.convert(view.frame.origin, to: nil) // Correct origin in relation to UIWindow
            contextFrameForPresentation = CGRect(x: correctedOrigin.x, y: correctedOrigin.y, width: view.bounds.width, height: view.bounds.height)
        }
    }

    fileprivate var contextFrameForPresentation: CGRect?

    // MARK: Init

    public init(presentationType: PresentationType) {
        self.presentationType = presentationType
    }

    // MARK: Class Helper Methods

    /**
     Public helper class method for creating and configuring an instance of the 'AlertViewController'

     - parameter title: Title to be used in the Alert View Controller.
     - parameter body: Body of the message to be displayed in the Alert View Controller.

     - returns: Returns a configured instance of 'AlertViewController'
     */
    static func alertViewController(title: String = PresentrConstants.Strings.alertTitle, body: String = PresentrConstants.Strings.alertBody) -> OOAlertViewController {
        let alertController = OOAlertViewController()
        alertController.titleText = title
        alertController.bodyText = body
        return alertController
    }

    // MARK: Private Methods

    /**
     Private method for presenting a view controller, using the custom presentation. Called from the UIViewController extension.

     - parameter presentingVC: The view controller which is doing the presenting.
     - parameter presentedVC:  The view controller to be presented.
     - parameter animated:     Animation boolean.
     - parameter completion:   Completion block.
     */
    fileprivate func presentViewController(presentingViewController presentingVC: UIViewController, presentedViewController presentedVC: UIViewController, animated: Bool, completion: (() -> Void)?) {
        presentedVC.transitioningDelegate = self
        presentedVC.modalPresentationStyle = .custom
        presentingVC.present(presentedVC, animated: animated, completion: completion)
    }

    fileprivate var transitionForPresent: TransitionType {
        return transitionType ?? presentationType.defaultTransitionType()
    }

    fileprivate var transitionForDismiss: TransitionType {
        return dismissTransitionType ?? transitionType ?? presentationType.defaultTransitionType()
    }

}

// MARK: - UIViewControllerTransitioningDelegate

extension Presentr: UIViewControllerTransitioningDelegate {

    public func presentationController(forPresented presented: UIViewController, presenting: UIViewController?, source: UIViewController) -> UIPresentationController? {
        return presentationController(presented, presenting: presenting)
    }

    public func animationController(forPresented presented: UIViewController, presenting: UIViewController, source: UIViewController) -> UIViewControllerAnimatedTransitioning? {
        return transitionForPresent.animation()
    }

    public func animationController(forDismissed dismissed: UIViewController) -> UIViewControllerAnimatedTransitioning? {
        return transitionForDismiss.animation()
    }

    // MARK: - Private Helper's

    fileprivate func presentationController(_ presented: UIViewController, presenting: UIViewController?) -> PresentrController {
        return PresentrController(presentedViewController: presented,
                                    presentingViewController: presenting,
                                    presentationType: presentationType,
                                    roundCorners: roundCorners,
                                    cornerRadius: cornerRadius,
                                    dropShadow: dropShadow,
                                    dismissOnTap: dismissOnTap,
                                    dismissOnSwipe: dismissOnSwipe,
                                    dismissOnSwipeDirection: dismissOnSwipeDirection,
                                    backgroundColor: backgroundColor,
                                    backgroundOpacity: backgroundOpacity,
                                    blurBackground: blurBackground,
                                    blurStyle: blurStyle,
                                    customBackgroundView: customBackgroundView,
                                    keyboardTranslationType:  keyboardTranslationType,
                                    dismissAnimated: dismissAnimated,
                                    contextFrameForPresentation: contextFrameForPresentation,
                                    shouldIgnoreTapOutsideContext: shouldIgnoreTapOutsideContext)
    }

}

// MARK: - UIViewController extension to provide customPresentViewController(_:viewController:animated:completion:) method

public extension UIViewController {

    /// Present a view controller with a custom presentation provided by the Presentr object.
    ///
    /// - Parameters:
    ///   - presentr: Presentr object used for custom presentation.
    ///   - viewController: The view controller to be presented.
    ///   - animated: Animation setting for the presentation.
    ///   - completion: Completion handler.
    func customPresentViewController(_ presentr: Presentr, viewController: UIViewController, animated: Bool, completion: (() -> Void)?) {
        presentr.presentViewController(presentingViewController: self,
                                       presentedViewController: viewController,
                                       animated: animated,
                                       completion: completion)
    }

}
