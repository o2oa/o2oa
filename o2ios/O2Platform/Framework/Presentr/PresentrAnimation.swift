//
//  PresentrAnimation.swift
//  Presentr
//
//  Created by Daniel Lozano on 5/14/16.
//  Copyright © 2016 danielozano. All rights reserved.
//

import UIKit

/// Simplified wrapper for the UIViewControllerContextTransitioning protocol.
public struct PresentrTransitionContext {

    public let containerView: UIView

    public let initialFrame: CGRect

    public let finalFrame: CGRect

    public let isPresenting: Bool

    public let fromViewController: UIViewController?

    public let toViewController: UIViewController?

    public let fromView: UIView?

    public let toView: UIView?

    public let animatingViewController: UIViewController?
    
    public let animatingView: UIView?
    
}

/// Options for the UIView animation.
public enum AnimationOptions {

    case normal(duration: TimeInterval)

    case spring(duration: TimeInterval, delay: TimeInterval, damping: CGFloat, velocity: CGFloat)

}

/// Class that handles animating the transition. Override this class if you want to create your own transition animation.
open class PresentrAnimation: NSObject {

    public var options: AnimationOptions

    public init(options: AnimationOptions = .normal(duration: 0.4)) {
        self.options = options
    }

    /// For simple transitions, override this method to calculate an initial frame for the animation. For more complex animations override beforeAnimation & performAnimation. Only override this method OR beforeAnimation & performAnimation. This method won't even be called if you override beforeAnimation.
    ///
    /// - Parameters:
    ///   - containerFrame: The container frame for the animation.
    ///   - finalFrame: The final frame for the animation.
    /// - Returns: The initial frame.
    open func transform(containerFrame: CGRect, finalFrame: CGRect) -> CGRect {
        var initialFrame = finalFrame
        initialFrame.origin.y = containerFrame.height + initialFrame.height
        return initialFrame
    }


    /// Actions to be performed in preparation, before an animation.
    ///
    /// - Parameter transitionContext: The context with everything needed for the animiation.
    open func beforeAnimation(using transitionContext: PresentrTransitionContext) {
        let finalFrameForVC = transitionContext.finalFrame
        let initialFrameForVC = transform(containerFrame: transitionContext.containerView.frame, finalFrame: finalFrameForVC)

        let initialFrame = transitionContext.isPresenting ? initialFrameForVC : finalFrameForVC
        transitionContext.animatingView?.frame = initialFrame
    }


    /// Actions to be performed for the animation.
    ///
    /// - Parameter transitionContext: The context with everything needed for the animiation.
    open func performAnimation(using transitionContext: PresentrTransitionContext) {
        let finalFrameForVC = transitionContext.finalFrame
        let initialFrameForVC = transform(containerFrame: transitionContext.containerView.frame, finalFrame: finalFrameForVC)

        let finalFrame = transitionContext.isPresenting ? finalFrameForVC : initialFrameForVC
        transitionContext.animatingView?.frame = finalFrame
    }

    /// Actions to be performed after the animation.
    ///
    /// - Parameter transitionContext: The context with everything needed for the animiation.
    open func afterAnimation(using transitionContext: PresentrTransitionContext) {
        // Any cleanup to be done after the animation is over.
    }

}

// MARK: - UIViewControllerAnimatedTransitioning

extension PresentrAnimation: UIViewControllerAnimatedTransitioning {

    public func transitionDuration(using transitionContext: UIViewControllerContextTransitioning?) -> TimeInterval {
        switch options {
        case let .normal(duration):
            return duration
        case let .spring(duration, _, _, _):
            return duration
        }
    }

    public func animateTransition(using transitionContext: UIViewControllerContextTransitioning) {
        let containerView = transitionContext.containerView
        
        let fromViewController = transitionContext.viewController(forKey: UITransitionContextViewControllerKey.from)
        let toViewController = transitionContext.viewController(forKey: UITransitionContextViewControllerKey.to)
        let fromView = transitionContext.view(forKey: UITransitionContextViewKey.from)
        let toView = transitionContext.view(forKey: UITransitionContextViewKey.to)

        let isPresenting: Bool = (toViewController?.presentingViewController == fromViewController)

        let animatingVC = isPresenting ? toViewController : fromViewController
        let animatingView = isPresenting ? toView : fromView

        let initialFrame = transitionContext.initialFrame(for: animatingVC!)
        let finalFrame = transitionContext.finalFrame(for: animatingVC!)

        let presentrContext = PresentrTransitionContext(containerView: containerView,
                                                        initialFrame: initialFrame,
                                                        finalFrame: finalFrame,
                                                        isPresenting: isPresenting,
                                                        fromViewController: fromViewController,
                                                        toViewController: toViewController,
                                                        fromView: fromView,
                                                        toView: toView,
                                                        animatingViewController: animatingVC,
                                                        animatingView: animatingView)
        if isPresenting {
            containerView.addSubview(toView!)
        }

        switch options {
        case let .normal(duration):
            animate(presentrContext: presentrContext,
                    transitionContext: transitionContext,
                    duration: duration)
        case let .spring(duration, delay, damping, velocity):
            animateWithSpring(presentrContext: presentrContext,
                              transitionContext: transitionContext,
                              duration: duration,
                              delay: delay,
                              damping: damping,
                              velocity: velocity)
        }

    }

    private func animate(presentrContext: PresentrTransitionContext, transitionContext: UIViewControllerContextTransitioning, duration: TimeInterval) {
        beforeAnimation(using: presentrContext)
        UIView.animate(withDuration: duration, animations: {
            self.performAnimation(using: presentrContext)
        }) { (completed) in
            self.afterAnimation(using: presentrContext)
            transitionContext.completeTransition(!transitionContext.transitionWasCancelled)
        }
    }

    private func animateWithSpring(presentrContext: PresentrTransitionContext, transitionContext: UIViewControllerContextTransitioning, duration: TimeInterval, delay: TimeInterval, damping: CGFloat, velocity: CGFloat) {
        beforeAnimation(using: presentrContext)
        UIView.animate(withDuration: duration,
                       delay: delay,
                       usingSpringWithDamping: damping,
                       initialSpringVelocity: velocity,
                       options: [],
                       animations: {
            self.performAnimation(using: presentrContext)
        }) { (completed) in
            self.afterAnimation(using: presentrContext)
            transitionContext.completeTransition(!transitionContext.transitionWasCancelled)
        }
    }

}
