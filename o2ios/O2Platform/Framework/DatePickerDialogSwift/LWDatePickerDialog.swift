//
//  LWDatePickerDialog.swift
//  DatePickerDialogSwift
//
//  Created by 刘振兴 on 2018/1/17.
//

import UIKit

private extension Selector {
    //按钮点击
    static let buttonTapped = #selector(LWDatePickerDialog.buttonTapped)
    //设备方向转换
    static let deviceOrientationDidChange = #selector(LWDatePickerDialog.deviceOrientationDidChange)
}


struct LWDialogStyle {
    //title
    static let titleColor = UIColor(hex: "#FFFFFF")
    static let titleTextFont = UIFont(name: "PingFangSC-Regular", size: 18)
    static var titleViewBackColor: UIColor {
        get {
            return O2ThemeManager.color(for: "Base.base_color")!
        }
    }
    
    //DatePicker unSelected TextColor Font
    static let dpUnSelTextColor = UIColor(hex: "#999999")
    static let dpUnSelTextFont = UIFont(name: "PingFangSC-Regular", size: 18)
    
    static var dpSelTextColor: UIColor {
        get {
            return O2ThemeManager.color(for: "Base.base_color")!
        }
    }
    static let dpSelTextFont = UIFont(name: "PingFangSC-Regular", size: 23)
    
    //button
    static let okButtonTextColor = UIColor(hex: "#FFFFFF")
    static let okButtonFont = UIFont(name: "PingFangSC-Regular", size: 16)
    static var okButtonBackColor: UIColor {
        get {
            return O2ThemeManager.color(for: "Base.base_color")!
        }
    }
    
    static let cancelButtonTextColor = UIColor(hex: "#FFFFFF")
    static let cancelButtonFont = UIFont(name: "PingFangSC-Regular", size: 16)
    static let cancelButtonBackColor = UIColor(hex: "#CCCCCC")
    
    // MARK: - Constants
    static let defaultWidth:CGFloat = 300
    
    static let defaultTitleContainerHeight:CGFloat = 50
    static let defaultTitleHeight:CGFloat = 35
    
    static let defaultDatePickerHeight:CGFloat = 230
    
    static let defaultButtonContainerHeight:CGFloat = 50
    static let defaultButtonHeight: CGFloat = 35
    
    static let defaultButtonSpacerHeight: CGFloat = 1
    
    static let cornerRadius: CGFloat = 15
    static let doneButtonTag: Int     = 1
    
}


open class LWDatePickerDialog: UIView {
    //回调类型定义
    public typealias DatePickerCallback = ( Date? ) -> Void

    // MARK: - Views
    private var dialogView: UIView!
    //title view
    private var titleContainerView:UIView!
    private var titleLabel: UILabel!
    
    //picker view
    open var datePicker: UIDatePicker!
    
    //button view
    private var buttonContainerView:UIView!
    private var cancelButton: UIButton!
    private var doneButton: UIButton!
    
    // MARK: - Variables
    private var defaultDate: Date?
    private var datePickerMode: UIDatePicker.Mode?
    private var callback: DatePickerCallback?
    
    var showCancelButton: Bool = false
    var locale: Locale?

    private var textColor: UIColor!
    private var buttonColor: UIColor!
    private var font: UIFont!
    
    // MARK: - Dialog initialization
    public init(textColor: UIColor = UIColor.black,
                buttonColor: UIColor = UIColor.blue,
                font: UIFont = .boldSystemFont(ofSize: 15),
                locale: Locale? = nil,
                showCancelButton: Bool = true) {
        let size = UIScreen.main.bounds.size
        super.init(frame: CGRect(x: 0, y: 0, width: size.width, height: size.height))
        self.textColor = textColor
        self.buttonColor = buttonColor
        self.font = font
        self.showCancelButton = showCancelButton
        self.locale = locale
        setupView()
    }
    
    required public init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
    }
    
    func setupView() {
        self.dialogView = createContainerView()
        
        self.dialogView!.layer.shouldRasterize = true
        self.dialogView!.layer.rasterizationScale = UIScreen.main.scale
        
        self.layer.shouldRasterize = true
        self.layer.rasterizationScale = UIScreen.main.scale
        
        self.dialogView!.layer.opacity = 0.5
        self.dialogView!.layer.transform = CATransform3DMakeScale(1.3, 1.3, 1)
        
        self.backgroundColor = UIColor(red: 0, green: 0, blue: 0, alpha: 0)
        
        self.addSubview(self.dialogView!)
    }
    
    /// Handle device orientation changes
    @objc func deviceOrientationDidChange(_ notification: Notification) {
        self.frame = UIScreen.main.bounds
        let dialogSize = CGSize(width: LWDialogStyle.defaultWidth,height: LWDialogStyle.defaultTitleContainerHeight + LWDialogStyle.defaultDatePickerHeight + LWDialogStyle.defaultButtonContainerHeight)
        dialogView.frame = CGRect(x: (UIScreen.main.bounds.size.width - dialogSize.width) / 2,
                                  y: (UIScreen.main.bounds.size.height - dialogSize.height) / 2,
                                  width: dialogSize.width,
                                  height: dialogSize.height)
    }
    
    /// Create the dialog view, and animate opening the dialog
    open func show(_ title: String,
                   doneButtonTitle: String = "Done",
                   cancelButtonTitle: String = "Cancel",
                   defaultDate: Date = Date(),
                   minimumDate: Date? = nil, maximumDate: Date? = nil,
                   datePickerMode: UIDatePicker.Mode = .dateAndTime,
                   callback: @escaping DatePickerCallback) {
        self.titleLabel.text = title
        self.doneButton.setTitle(doneButtonTitle, for: .normal)
        if showCancelButton {
            self.cancelButton.setTitle(cancelButtonTitle, for: .normal)
        }
        self.datePickerMode = datePickerMode
        self.callback = callback
        self.defaultDate = defaultDate
        self.datePicker.datePickerMode = self.datePickerMode ?? UIDatePicker.Mode.date
        self.datePicker.date = self.defaultDate ?? Date()
        self.datePicker.maximumDate = maximumDate
        self.datePicker.minimumDate = minimumDate
        if let locale = self.locale {
            self.datePicker.locale = locale
        }
        /* Add dialog to main window */
        guard let appDelegate = UIApplication.shared.delegate else { fatalError() }
        guard let window = appDelegate.window else { fatalError() }
        window?.addSubview(self)
        window?.bringSubviewToFront(self)
        window?.endEditing(true)
        
        NotificationCenter.default.addObserver(self,
                                               selector: .deviceOrientationDidChange,
                                               name: UIDevice.orientationDidChangeNotification,
                                               object: nil)
        
        /* Anim */
        UIView.animate(
            withDuration: 0.2,
            delay: 0,
            options: .curveEaseInOut,
            animations: {
                self.backgroundColor = UIColor(red: 0, green: 0, blue: 0, alpha: 0.4)
                self.dialogView!.layer.opacity = 1
                self.dialogView!.layer.transform = CATransform3DMakeScale(1, 1, 1)
        }
        )
    }
    
    /// Dialog close animation then cleaning and removing the view from the parent
    private func close() {
        let currentTransform = self.dialogView.layer.transform
        
        let startRotation = (self.value(forKeyPath: "layer.transform.rotation.z") as? NSNumber) as? Double ?? 0.0
        let rotation = CATransform3DMakeRotation((CGFloat)(-startRotation + .pi * 270 / 180), 0, 0, 0)
        
        self.dialogView.layer.transform = CATransform3DConcat(rotation, CATransform3DMakeScale(1, 1, 1))
        self.dialogView.layer.opacity = 1
        
        UIView.animate(
            withDuration: 0.2,
            delay: 0,
            options: [],
            animations: {
                self.backgroundColor = UIColor(red: 0, green: 0, blue: 0, alpha: 0)
                let transform = CATransform3DConcat(currentTransform, CATransform3DMakeScale(0.6, 0.6, 1))
                self.dialogView.layer.transform = transform
                self.dialogView.layer.opacity = 0
        }) { (_) in
            for v in self.subviews {
                v.removeFromSuperview()
            }
            
            self.removeFromSuperview()
            self.setupView()
        }
    }
    
    /// Creates the container view here: create the dialog, then add the custom content and buttons
    private func createContainerView() -> UIView {
        let screenSize = UIScreen.main.bounds.size
        //title + datePicker + button height
        let dialogSize = CGSize(width: LWDialogStyle.defaultWidth, height: LWDialogStyle.defaultTitleContainerHeight + LWDialogStyle.defaultDatePickerHeight + LWDialogStyle.defaultButtonContainerHeight)
        
        // For the black background
        self.frame = CGRect(x: 0, y: 0, width: screenSize.width, height: screenSize.height)
        
        // This is the dialog's container; we attach the custom content and the buttons to this one
        let container = UIView(frame: CGRect(x: (screenSize.width - dialogSize.width) / 2,
                                             y: (screenSize.height - dialogSize.height) / 2,
                                             width: dialogSize.width,
                                             height: dialogSize.height))
        
        // First, we style the dialog to match the iOS8 UIAlertView >>>
        let gradient: CAGradientLayer = CAGradientLayer(layer: self.layer)
        gradient.frame = container.bounds
        gradient.colors = [UIColor(red: 218/255, green: 218/255, blue: 218/255, alpha: 1).cgColor,
                           UIColor(red: 233/255, green: 233/255, blue: 233/255, alpha: 1).cgColor,
                           UIColor(red: 218/255, green: 218/255, blue: 218/255, alpha: 1).cgColor]
        
        let cornerRadius = LWDialogStyle.cornerRadius
        gradient.cornerRadius = cornerRadius
        
        container.layer.insertSublayer(gradient, at: 0)
        container.layer.cornerRadius = cornerRadius
        container.layer.masksToBounds = true
        container.layer.borderColor = UIColor(red: 198/255, green: 198/255, blue: 198/255, alpha: 1).cgColor
        container.layer.borderWidth = 1
        container.layer.shadowRadius = cornerRadius + 5
        container.layer.shadowOpacity = 0.1
        container.layer.shadowOffset = CGSize(width: 0 - (cornerRadius + 5) / 2, height: 0 - (cornerRadius + 5) / 2)
        container.layer.shadowColor = UIColor.black.cgColor
        container.layer.shadowPath = UIBezierPath(roundedRect: container.bounds,
                                                  cornerRadius: container.layer.cornerRadius).cgPath
        
        // There is a line above the button
//        let yPosition = container.bounds.size.height - kDefaultButtonHeight - kDefaultButtonSpacerHeight
//        let lineView = UIView(frame: CGRect(x: 0,
//                                            y: yPosition,
//                                            width: container.bounds.size.width,
//                                            height: kDefaultButtonSpacerHeight))
//        lineView.backgroundColor = UIColor(red: 198/255, green: 198/255, blue: 198/255, alpha: 1)
//        container.addSubview(lineView)
        
        //Title
        self.titleContainerView = UIView(frame: CGRect(x: 0, y: 0, width: LWDialogStyle.defaultWidth, height: LWDialogStyle.defaultTitleContainerHeight))
        self.titleContainerView.backgroundColor = LWDialogStyle.titleViewBackColor
        self.titleLabel = UILabel(frame: CGRect(x: 20, y: (LWDialogStyle.defaultTitleContainerHeight-LWDialogStyle.defaultTitleHeight)/2, width: LWDialogStyle.defaultWidth - 50, height: LWDialogStyle.defaultTitleHeight))
        self.titleLabel.textAlignment = .left
        self.titleLabel.textColor = LWDialogStyle.titleColor
        self.titleLabel.font = LWDialogStyle.titleTextFont
        self.titleContainerView.addSubview(self.titleLabel)
        container.addSubview(self.titleContainerView)
        //DatePicker
        self.datePicker = configuredDatePicker()
        container.addSubview(self.datePicker)
        
        // Add the buttons
        self.buttonContainerView = UIView(frame: CGRect(x: 0, y: LWDialogStyle.defaultTitleContainerHeight + LWDialogStyle.defaultDatePickerHeight, width: LWDialogStyle.defaultWidth, height: LWDialogStyle.defaultButtonContainerHeight))
        self.backgroundColor = UIColor.white
        addButtonsToView(container: buttonContainerView)
        container.addSubview(self.buttonContainerView)
        
        return container
    }
    
    fileprivate func configuredDatePicker() -> UIDatePicker {
        let datePicker = UIDatePicker(frame: CGRect(x: 0, y: LWDialogStyle.defaultTitleContainerHeight, width: 0, height: 0))
        datePicker.setValue(LWDialogStyle.dpSelTextColor, forKeyPath: "textColor")
        datePicker.autoresizingMask = .flexibleRightMargin
        datePicker.frame.size.width = LWDialogStyle.defaultWidth
        datePicker.frame.size.height = LWDialogStyle.defaultDatePickerHeight
        return datePicker
    }
    
    /// Add buttons to container
    private func addButtonsToView(container: UIView) {
        var buttonWidth = (container.bounds.size.width - 20*2) / 2
        
        var leftButtonFrame = CGRect(
            x: 10,
            y: (container.bounds.size.height - LWDialogStyle.defaultButtonHeight)/2,
            width: buttonWidth,
            height: LWDialogStyle.defaultButtonHeight
        )
        var rightButtonFrame = CGRect(
            x: 10 + buttonWidth + 10 * 2,
            y: (container.bounds.size.height - LWDialogStyle.defaultButtonHeight)/2,
            width: buttonWidth,
            height: LWDialogStyle.defaultButtonHeight
        )
        if showCancelButton == false {
            buttonWidth = container.bounds.size.width
            leftButtonFrame = CGRect()
            rightButtonFrame = CGRect(
                x: (LWDialogStyle.defaultWidth - buttonWidth) / 2,
                y: (container.bounds.size.height - LWDialogStyle.defaultButtonHeight)/2,
                width: buttonWidth,
                height: LWDialogStyle.defaultButtonHeight
            )
        }
        let interfaceLayoutDirection = UIApplication.shared.userInterfaceLayoutDirection
        let isLeftToRightDirection = interfaceLayoutDirection == .leftToRight
        
        if showCancelButton {
            self.cancelButton = UIButton(type: .custom) as UIButton
            self.cancelButton.frame = isLeftToRightDirection ? leftButtonFrame : rightButtonFrame
            self.cancelButton.setTitleColor(LWDialogStyle.cancelButtonTextColor, for: .normal)
            self.cancelButton.setTitleColor(LWDialogStyle.cancelButtonTextColor, for: .highlighted)
            self.cancelButton.backgroundColor = LWDialogStyle.cancelButtonBackColor
            self.cancelButton.titleLabel!.font = LWDialogStyle.cancelButtonFont
            self.cancelButton.layer.cornerRadius = LWDialogStyle.cornerRadius
            self.cancelButton.addTarget(self, action: .buttonTapped, for: .touchUpInside)
            container.addSubview(self.cancelButton)
        }
        self.doneButton = UIButton(type: .custom) as UIButton
        self.doneButton.frame = isLeftToRightDirection ? rightButtonFrame : leftButtonFrame
        self.doneButton.tag = LWDialogStyle.doneButtonTag
        self.doneButton.backgroundColor = LWDialogStyle.okButtonBackColor
        self.doneButton.setTitleColor(LWDialogStyle.okButtonTextColor, for: .normal)
        self.doneButton.setTitleColor(LWDialogStyle.okButtonTextColor, for: .highlighted)
        self.doneButton.titleLabel!.font = LWDialogStyle.okButtonFont
        self.doneButton.layer.cornerRadius = LWDialogStyle.cornerRadius
        self.doneButton.addTarget(self, action: .buttonTapped, for: .touchUpInside)
        container.addSubview(self.doneButton)
    }
    
    @objc func buttonTapped(sender: UIButton!) {
        if sender.tag == LWDialogStyle.doneButtonTag {
            self.callback?(self.datePicker.date)
        } else {
            self.callback?(nil)
        }
        close()
    }
    
    deinit {
        NotificationCenter.default.removeObserver(self)
    }

}
