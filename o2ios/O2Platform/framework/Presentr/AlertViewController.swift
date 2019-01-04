//
//  AlertViewController.swift
//  OneUP
//
//  Created by Daniel Lozano on 5/10/16.
//  Copyright © 2016 Icalia Labs. All rights reserved.
//

import UIKit

public typealias AlertActionHandler = ((AlertAction) -> Void)

/// Describes each action that is going to be shown in the 'AlertViewController'
public class AlertAction {

    public let title: String
    public let style: AlertActionStyle
    public let handler: AlertActionHandler?

    /**
     Initialized an 'AlertAction'

     - parameter title:   The title for the action, that will be used as the title for a button in the alert controller
     - parameter style:   The style for the action, that will be used to style a button in the alert controller.
     - parameter handler: The handler for the action, that will be called when the user clicks on a button in the alert controller.

     - returns: An inmutable AlertAction object
     */
    public init(title: String, style: AlertActionStyle, handler: AlertActionHandler?) {
        self.title = title
        self.style = style
        self.handler = handler
    }

}

/**
 Describes the style for an action, that will be used to style a button in the alert controller.

 - Default:     Green text label. Meant to draw attention to the action.
 - Cancel:      Gray text label. Meant to be neutral.
 - Destructive: Red text label. Meant to warn the user about the action.
 */
public enum AlertActionStyle {

    case `default`
    case cancel
    case destructive
    case custom(textColor: UIColor)

    /**
     Decides which color to use for each style

     - returns: UIColor representing the color for the current style
     */
    func color() -> UIColor {
        switch self {
        case .default:
            return ColorPalette.greenColor
        case .cancel:
            return ColorPalette.grayColor
        case .destructive:
            return ColorPalette.redColor
        case let .custom(color):
            return color
        }
    }

}



private enum Font: String {

    case Montserrat = "Montserrat-Regular"
    case SourceSansPro = "SourceSansPro-Regular"

    func font(_ size: CGFloat = 15.0) -> UIFont {
        return UIFont(name: self.rawValue, size: size)!
    }

}


/// UIViewController subclass that displays the alert
public class AlertViewController: UIViewController {

    /// Text that will be used as the title for the alert
    public var titleText: String?

    /// Text that will be used as the body for the alert
    public var bodyText: String?

    /// If set to false, alert wont auto-dismiss the controller when an action is clicked. Dismissal will be up to the action's handler. Default is true.
    public var autoDismiss: Bool = true

    /// If autoDismiss is set to true, then set this property if you want the dismissal to be animated. Default is true.
    public var dismissAnimated: Bool = true

    fileprivate var actions = [AlertAction]()

    @IBOutlet weak var titleLabel: UILabel!
    @IBOutlet weak var bodyLabel: UILabel!
    @IBOutlet weak var firstButton: UIButton!
    @IBOutlet weak var secondButton: UIButton!
    @IBOutlet weak var firstButtonWidthConstraint: NSLayoutConstraint!

    override public func loadView() {
        let name = "AlertViewController"
        let bundle = Bundle(for: type(of: self))
        guard let view = bundle.loadNibNamed(name, owner: self, options: nil)?.first as? UIView else {
            fatalError("Nib not found.")
        }
        self.view = view
    }

    override public func viewDidLoad() {
        super.viewDidLoad()

        if actions.isEmpty {
            let okAction = AlertAction(title: "ok 🕶", style: .default, handler: nil)
            addAction(okAction)
        }

        loadFonts

        setupFonts()
        setupLabels()
        setupButtons()
    }

    override public func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }

    override public func updateViewConstraints() {
        if actions.count == 1 {
            // If only one action, second button will have been removed from superview
            // So, need to add constraint for first button trailing to superview
            if let constraint = firstButtonWidthConstraint {
                view.removeConstraint(constraint)
            }
            let views: [String: UIView] = ["button" : firstButton]
            let constraints = NSLayoutConstraint.constraints(withVisualFormat: "H:|-0-[button]-0-|",
                                                             options: NSLayoutConstraint.FormatOptions(rawValue: 0),
                                                                             metrics: nil,
                                                                             views: views)
            view.addConstraints(constraints)
        }
        super.updateViewConstraints()
    }

    // MARK: AlertAction's

    /**
     Adds an 'AlertAction' to the alert controller. There can be maximum 2 actions. Any more will be ignored. The order is important.

     - parameter action: The 'AlertAction' to be added
     */
    public func addAction(_ action: AlertAction) {
        guard actions.count < 2 else { return }
        actions += [action]
    }

    // MARK: Setup

    fileprivate func setupFonts() {
        titleLabel.font = Font.Montserrat.font()
        bodyLabel.font = Font.SourceSansPro.font()
        firstButton.titleLabel?.font = Font.Montserrat.font(11.0)
        secondButton.titleLabel?.font = Font.Montserrat.font(11.0)
    }

    fileprivate func setupLabels() {
        titleLabel.text = titleText ?? "Alert"
        bodyLabel.text = bodyText ?? "This is an alert."
    }

    fileprivate func setupButtons() {
        guard let firstAction = actions.first else { return }
        apply(firstAction, toButton: firstButton)
        if actions.count == 2 {
            let secondAction = actions.last!
            apply(secondAction, toButton: secondButton)
        } else {
            secondButton.removeFromSuperview()
        }
    }

    fileprivate func apply(_ action: AlertAction, toButton: UIButton) {
        let title = action.title.uppercased()
        let style = action.style
        toButton.setTitle(title, for: UIControl.State())
        toButton.setTitleColor(style.color(), for: UIControl.State())
    }

    // MARK: IBAction's

    @IBAction func didSelectFirstAction(_ sender: AnyObject) {
        guard let firstAction = actions.first else { return }
        if let handler = firstAction.handler {
            handler(firstAction)
        }
        dismiss()
    }

    @IBAction func didSelectSecondAction(_ sender: AnyObject) {
        guard let secondAction = actions.last, actions.count == 2 else { return }
        if let handler = secondAction.handler {
            handler(secondAction)
        }
        dismiss()
    }

    // MARK: Helper's

    func dismiss() {
        guard autoDismiss else { return }
        self.dismiss(animated: dismissAnimated, completion: nil)
    }

}

// MARK: - Font Loading

let loadFonts: () = {
    let loadedFontMontserrat = AlertViewController.loadFont(Font.Montserrat.rawValue)
    let loadedFontSourceSansPro = AlertViewController.loadFont(Font.SourceSansPro.rawValue)
    if loadedFontMontserrat && loadedFontSourceSansPro {
        print("LOADED FONTS")
    }
}()

extension AlertViewController {

    static func loadFont(_ name: String) -> Bool {
        let bundle = Bundle(for: self)
        guard let fontPath = bundle.path(forResource: name, ofType: "ttf"),
            let data = try? Data(contentsOf: URL(fileURLWithPath: fontPath)),
            let provider = CGDataProvider(data: data as CFData),
            let font = CGFont(provider)
        else {
            return false
        }

        var error: Unmanaged<CFError>?

        let success = CTFontManagerRegisterGraphicsFont(font, &error)
        if !success {
            print("Error loading font. Font is possibly already registered.")
            return false
        }

        return true
    }

}
