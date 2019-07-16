//
//  OOAlertViewController.swift
//  o2app
//
//  Created by 刘振兴 on 2017/11/24.
//  Copyright © 2017年 zone. All rights reserved.
//

import UIKit


public typealias OOAlertActionHandler = ((OOAlertAction) -> Void)

/// Describes each action that is going to be shown in the 'AlertViewController'
public class OOAlertAction {
    
    public let title: String
    public let style: OOAlertActionStyle
    public let handler: OOAlertActionHandler?
    
    /**
     Initialized an 'AlertAction'
     
     - parameter title:   The title for the action, that will be used as the title for a button in the alert controller
     - parameter style:   The style for the action, that will be used to style a button in the alert controller.
     - parameter handler: The handler for the action, that will be called when the user clicks on a button in the alert controller.
     
     - returns: An inmutable AlertAction object
     */
    public init(title: String, style: OOAlertActionStyle, handler: OOAlertActionHandler?) {
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
public enum OOAlertActionStyle {
    
    case `default`
    case cancel
    case destructive
    case custom(textColor: UIColor,backColor:UIColor)
    
    /**
     Decides which color to use for each style
     
     - returns: UIColor representing the color for the current style
     */
    func color() -> (UIColor,UIColor) {
        switch self {
        case .default:
            return (UIColor.white, O2ThemeManager.color(for: "Base.base_color")!)
        case .cancel:
            return (UIColor.white, UIColor(hex: "#333333"))
        case .destructive:
            return (O2ThemeManager.color(for: "Base.base_color")!, UIColor.white)
        case let .custom(textColor,backColor):
            return (textColor,backColor)
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


public class OOAlertViewController: UIViewController {
    
    /// Text that will be used as the title for the alert
    public var titleText: String?
    
    /// Text that will be used as the body for the alert
    public var bodyText: String?
    
    /// If set to false, alert wont auto-dismiss the controller when an action is clicked. Dismissal will be up to the action's handler. Default is true.
    public var autoDismiss: Bool = true
    
    /// If autoDismiss is set to true, then set this property if you want the dismissal to be animated. Default is true.
    public var dismissAnimated: Bool = true
    
    fileprivate var actions = [OOAlertAction]()
    
    @IBOutlet weak var categoryIconImageView: UIImageView!
    
    @IBOutlet weak var titleLabel: UILabel!
    
    @IBOutlet weak var bodyLabel: UILabel!
    
    @IBOutlet weak var rightButton: UIButton!
    
    @IBOutlet weak var firstButton: UIButton!
    
    @IBOutlet weak var secondButton: UIButton!
    
    @IBOutlet weak var buttonContainerView: UIStackView!
    //替换默认View的方法
    override public func loadView() {
        let name = "OOAlertViewController"
        guard let view = Bundle.main.loadNibNamed(name, owner: self, options: nil)?.first as? UIView else {
            fatalError("Nib not found.")
        }
        view.layer.cornerRadius = 20
        view.layer.masksToBounds = true
        self.view = view
    }


    public override  func viewDidLoad() {
        super.viewDidLoad()
        if actions.isEmpty {
            let okAction = OOAlertAction(title: "确定", style: .default, handler: nil)
            addAction(okAction)
        }
        setupLabels()
        setupButtons()
    }

    public override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    private func setupButtons(){
        guard let firstAction = actions.first else { return }
        apply(firstAction, toButton: firstButton)
        if actions.count == 2 {
            let secondAction = actions.last!
            apply(secondAction, toButton: secondButton)
        } else {
            secondButton.removeFromSuperview()
        }
    }
    
    public func addAction(_ action: OOAlertAction) {
        guard actions.count < 2 else { return }
        actions += [action]
    }
    
    private func apply(_ action: OOAlertAction, toButton: UIButton) {
        let title = action.title
        let style = action.style
        toButton.setTitle(title, for: .normal)
        toButton.setTitleColor(style.color().0, for: .normal)
    }
    
    private func setupLabels() {
        titleLabel.text = titleText ?? "提示框"
        bodyLabel.text = bodyText ?? "这是一个提标框Demo"
    }
    
    
    @IBAction func didSelectFirstAction(_ sender: Any) {
        guard let firstAction = actions.first else { return }
        if let handler = firstAction.handler {
            handler(firstAction)
        }
        dismiss()
    }
    
    @IBAction func didSelectSecondAction(_ sender: Any) {
        guard let secondAction = actions.last, actions.count == 2 else { return }
        if let handler = secondAction.handler {
            handler(secondAction)
        }
        dismiss()
    }
    
    
    @IBAction func didCloseAction(_ sender: Any) {
        self.dismiss(animated: dismissAnimated, completion: nil)
    }
    
    
    
    // MARK: Helper's
    
    func dismiss() {
        guard autoDismiss else { return }
        self.dismiss(animated: dismissAnimated, completion: nil)
    }
}
