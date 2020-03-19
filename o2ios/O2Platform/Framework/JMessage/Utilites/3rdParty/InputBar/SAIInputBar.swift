//
//  SAIbar.swift
//  SAIInputBar
//
//  Created by SAGESSE on 7/23/16.
//  Copyright © 2016-2017 SAGESSE. All rights reserved.
//

import UIKit



public enum SAIInputMode: CustomStringConvertible {
    
    case none
    case editing
    case audio
    case selecting(UIView)
    
    public var isNone: Bool {
        switch self {
        case .none: return true
        default: return false
        }
    }
    public var isEditing: Bool {
        switch self {
        case .editing: return true
        default: return false
        }
    }
    public var isAudio: Bool {
        switch self {
        case .audio: return true
        default: return false
        }
    }
    public var isSelecting: Bool {
        switch self {
        case .selecting: return true
        default: return false
        }
    }
    public var description: String {
        switch self {
        case .none: return "None"
        case .editing: return "Editing"
        case .audio: return "Audio"
        case .selecting(_): return "Selecting"
        }
    }
}


@objc public protocol SAIInputBarDelegate: NSObjectProtocol {
    
    // MARK: Text Edit
    
    @objc optional func inputBar(shouldBeginEditing inputBar: SAIInputBar) -> Bool
    @objc optional func inputBar(shouldEndEditing inputBar: SAIInputBar) -> Bool
    
    @objc optional func inputBar(didBeginEditing inputBar: SAIInputBar)
    @objc optional func inputBar(didEndEditing inputBar: SAIInputBar)
    
    @objc optional func inputBar(shouldReturn inputBar: SAIInputBar) -> Bool
    @objc optional func inputBar(shouldClear inputBar: SAIInputBar) -> Bool
    
    @objc optional func inputBar(didChangeSelection inputBar: SAIInputBar)
    @objc optional func inputBar(didChangeText inputBar: SAIInputBar)
    
    @objc optional func inputBar(_ inputBar: SAIInputBar, shouldInteractWithTextAttachment textAttachment: NSTextAttachment, inRange characterRange: NSRange) -> Bool
    @objc optional func inputBar(_ inputBar: SAIInputBar, shouldChangeCharactersInRange range: NSRange, replacementString string: String) -> Bool
    @objc optional func inputBar(_ inputBar: SAIInputBar, shouldInteractWithURL URL: URL, inRange characterRange: NSRange) -> Bool
    
    // MARK: Accessory Item Selection
    
    @objc optional func inputBar(_ inputBar: SAIInputBar, shouldHighlightFor item: SAIInputItem) -> Bool
    @objc optional func inputBar(_ inputBar: SAIInputBar, shouldDeselectFor item: SAIInputItem) -> Bool
    @objc optional func inputBar(_ inputBar: SAIInputBar, shouldSelectFor item: SAIInputItem) -> Bool
    
    @objc optional func inputBar(_ inputBar: SAIInputBar, didHighlightFor item: SAIInputItem)
    @objc optional func inputBar(_ inputBar: SAIInputBar, didDeselectFor item: SAIInputItem)
    @objc optional func inputBar(_ inputBar: SAIInputBar, didSelectFor item: SAIInputItem)
    
    // MARK: record tap
    
    @objc optional func inputBar(touchDown recordButton: UIButton, inputBar: SAIInputBar)
    @objc optional func inputBar(touchUpInside recordButton: UIButton, inputBar: SAIInputBar)
    @objc optional func inputBar(touchUpOutside recordButton: UIButton, inputBar: SAIInputBar)
    @objc optional func inputBar(dragOutside recordButton: UIButton, inputBar: SAIInputBar)
    @objc optional func inputBar(dragInside recordButton: UIButton, inputBar: SAIInputBar)
    
    // MARK: Input Mode
    
    @objc optional func inputBar(willChangeMode inputBar: SAIInputBar)
    @objc optional func inputBar(didChangeMode inputBar: SAIInputBar)
    
    
    // MARK: Keyboard
    
    @objc optional func inputBar(_ inputBar: SAIInputBar, willShowKeyboard keyboard: UIView)
    @objc optional func inputBar(_ inputBar: SAIInputBar, didShowKeyboard keyboard: UIView)
    
    @objc optional func inputBar(_ inputBar: SAIInputBar, willHideKeyboard keyboard: UIView)
    @objc optional func inputBar(_ inputBar: SAIInputBar, didHideKeyboard keyboard: UIView)
    
    @objc optional func inputBar(_ inputBar: SAIInputBar, sizeForKeyboard keyboard: UIView) -> CGSize
}

// MARK: -

///
/// multifunction input bar
///
/// If the delegate follow SAIDisplayable agreement, 
//  will automatically pop-up keyboard events management
///
/// Sample:
///    ```swift
///    lazy var toolbar: SAIInputBar = SAIInputBar()
/// 
///    override var inputAccessoryView: UIView? {
///        return toolbar
///    }
///    override var canBecomeFirstResponder: Bool {
///        return true
///    }
///    ```
///
/// 当页面切换后键盘自动隐藏
///    ```swift
///    override func viewDidDisappear(animated: Bool) {
///        super.viewDidDisappear(animated)
///        toolbar.inputMode = .None
///    }
///    ```
///
/// 切换到自定义输入栏, 并隐藏键盘, 效果参考: 微信的语音输入
///    ```swift
///    inputBar.setBarItem(_customCenterBarItem, atPosition: .Center)
///    inputBar.setInputMode(.None, animated: true)
///    ```
///
open class SAIInputBar: UIView {
    
    open override func invalidateIntrinsicContentSize() {
        super.invalidateIntrinsicContentSize()
        _cacheContentSize = nil
    }
    open override var intrinsicContentSize: CGSize {
        if let size = _cacheContentSize, size.width == frame.width {
            return size
        }
        let size = _contentSizeWithoutCache
        _cacheContentSize = size
        return size
    }
    @discardableResult
    open override func resignFirstResponder() -> Bool {
        self.setInputMode(.none, animated: true)
        return _inputAccessoryView.resignFirstResponder()
    }
    @discardableResult
    open override func becomeFirstResponder() -> Bool {
        return _inputAccessoryView.becomeFirstResponder()
    }
    open override var next: UIResponder? {
        return ib_nextResponderOverride ?? super.next
    }
    
    open var textItem: SAIInputItem { 
        return _inputAccessoryView.textField.item 
    }
    
    open var inputMode: SAIInputMode {
        set { return _updateInputMode(newValue, animated: true) }
        get { return _inputMode }
    }
    open func setInputMode(_ mode: SAIInputMode, animated: Bool) {
        _updateInputMode(mode, animated: animated)
    }
    
    open var contentSize: CGSize {
        return _inputAccessoryView.intrinsicContentSize
    }
    open var keyboardSize: CGSize {
        return _keyboardSizeWithoutCache
    }
    
    open var allowsSelection: Bool = true // default is YES
    open var allowsMultipleSelection: Bool = false // default is NO
    
    open weak var delegate: SAIInputBarDelegate? {
        didSet {
            _displayable = delegate as? SAIInputBarDisplayable
        }
    }
    
    // MARK: - Private Method
    
    fileprivate func _updateInputMode(_ newMode: SAIInputMode, animated: Bool) {
        let oldMode = _inputMode
        
        delegate?.inputBar?(willChangeMode: self)
        
        _inputMode = newMode
        _inputView.updateInputMode(newMode, oldMode: oldMode, animated: animated)
        // NOTE: must be updated `contentSize` before at `resignFirstResponder` 
        _updateKeyboardKeyboardWithInputMode(newMode, animated: animated) 
        _inputAccessoryView.updateInputMode(newMode, oldMode: oldMode, animated: animated)
        
        delegate?.inputBar?(didChangeMode: self)
    }
    fileprivate func _updateInputModeForResponder(_ newMode: SAIInputMode,  animated: Bool) {
        let oldMode = _inputMode
        if newMode.isAudio {
            if oldMode.isAudio {
                return
            }
        }
        if newMode.isNone {
            if !oldMode.isEditing {
                return
            }
        } else {
            if oldMode.isEditing {
                return
            }
        }
        
        delegate?.inputBar?(willChangeMode: self)
        
        _inputMode = newMode
        _inputView.updateInputMode(newMode, oldMode: oldMode, animated: animated)
        // unfortunately not update, because I don't know keyboardSize
        //_updateKeyboardKeyboardWithInputMode(newMode, animated: animated)
        
        delegate?.inputBar?(didChangeMode: self)
    }
    
    fileprivate func _updateContentSizeIfNeeded(_ animated: Bool) {
        let newContentSize = _contentSizeWithoutCache
        guard _cacheContentSize != newContentSize else {
            let newKeyboardSize = _keyboardSizeWithoutCache
            // 只处理同一次的事件
            if _cacheKeyboardSize?.width == newKeyboardSize.width &&
                _cacheKeyboardSize?.height != newKeyboardSize.height {
                let height = newKeyboardSize.height - (_cacheKeyboardSize?.height ?? 0)
                // 重置移动事件, 主要针对第三方输入法多次触发willShow的处理
                if let ani = _inputAccessoryView.layer.animation(forKey: "position")?.copy() as? CABasicAnimation {
                    // 系统键盘的大小改变了呢
                    let layer = _inputAccessoryView.layer
                    if let fm = layer.presentation()?.frame {
                        ani.fromValue = NSValue(cgPoint: CGPoint(x: 0, y: fm.minY  + height))
                    }
                    ani.duration = ani.duration - (ani.beginTime - CACurrentMediaTime())
                    if ani.duration > 0 {
                        
                        //_backgroundView.layer.add(ani, forKey: "position")
                        //_inputView.layer.add(ani, forKey: "position")
                        //_inputAccessoryView.layer.add(ani, forKey: "position")
                        _backgroundView.layer.removeAllAnimations()
                        _inputView.layer.removeAnimation(forKey: "position")
                        _inputAccessoryView.layer.removeAnimation(forKey: "position")
                    }
                }
            }
            return // no change
        }
        
        if animated {
            UIView.beginAnimations("SAIB-ANI-AC", context: nil)
            UIView.setAnimationDuration(_SAInputDefaultAnimateDuration)
            UIView.setAnimationCurve(_SAInputDefaultAnimateCurve)
        } 
        
        _inputView.setNeedsLayout()
        _inputAccessoryView.setNeedsLayout()
        _backgroundView.setNeedsLayout()
        //_containerView?.layoutIfNeeded()
        superview?.layoutIfNeeded()
        
        if animated {
            UIView.commitAnimations()
        }
        
        invalidateIntrinsicContentSize()
        // 必须立即更新, 否则的话会导致生成多个动画
        // 必须在invalidate之后, 否则无效
        _cacheContentSize = newContentSize 
        
        // 关闭动画的更新, 主要是为了防止contentSize改变之后的动画效果
        UIView.performWithoutAnimation { 
//            _inputView.setNeedsLayout()
//            _inputAccessoryView.setNeedsLayout()
            _containerView?.setNeedsLayout()
            
            superview?.setNeedsLayout()
            superview?.layoutIfNeeded()
            //_containerView?.layoutIfNeeded()
        }
        
        // 如果没有初始化, 那将他初始
        if !_cacheKeyboardIsInitialized {
            _cacheKeyboardIsInitialized = true
            _displayable?.ib_inputBar(self, initWithFrame: _frameInWindow)
        }
    }
    
    fileprivate func _updateKeyboardSizeIfNeeded(_ animated: Bool) {
        let newVisableSize = _visableKeybaordSize
        if _inputAccessoryViewBottom?.constant != -newVisableSize.height {
            _inputAccessoryViewBottom?.constant = -newVisableSize.height
        }
        _updateContentSizeIfNeeded(animated)
        _cacheKeyboardOffset = CGPoint.zero
        _cacheKeyboardSize = _keyboardSizeWithoutCache
    }
    fileprivate func _updateKeyboardOffsetIfNeeded(_ newPoint: CGPoint, animated: Bool) {
        let ny = _visableKeybaordSize.height - newPoint.y
        guard _inputAccessoryViewBottom?.constant != -ny else {
            return // no change
        }
        
        if animated {
            UIView.beginAnimations("SAIB-ANI-AC", context: nil)
            UIView.setAnimationDuration(_SAInputDefaultAnimateDuration)
            UIView.setAnimationCurve(_SAInputDefaultAnimateCurve)
        }
        
        _inputAccessoryViewBottom?.constant = -ny
        _containerView?.layoutIfNeeded()
        
        if animated {
            UIView.commitAnimations()
        }
    }
    
    fileprivate func _updateCustomKeyboard(_ newSize: CGSize, animated: Bool) {
        
        _cacheCustomKeyboardSize = newSize
        _updateKeyboardSizeIfNeeded(animated)
    }
    fileprivate func _updateSystemKeyboard(_ newSize: CGSize, animated: Bool) {
        
        _cacheSystemKeyboardSize = newSize
        _updateKeyboardSizeIfNeeded(animated)
    }
    fileprivate func _updateKeyboardKeyboardWithInputMode(_ mode: SAIInputMode, animated: Bool) {
        
        _updateCustomKeyboard(_inputView.intrinsicContentSize, animated: animated)
    }
    
    private func _addNotifications() {
        
        let center = NotificationCenter.default
        
        // keyboard
        center.addObserver(self, selector:#selector(ntf_keyboard(willShow:)), name:UIResponder.keyboardWillShowNotification, object:nil)
        center.addObserver(self, selector:#selector(ntf_keyboard(willHide:)), name:UIResponder.keyboardWillHideNotification, object:nil)
        
        // accessory
        center.addObserver(self, selector: #selector(ntf_accessory(didChangeFrame:)), name: NSNotification.Name(rawValue: SAIAccessoryDidChangeFrameNotification), object: nil)
    }
    private func _removeNotifications() {
        
        let center = NotificationCenter.default
        center.removeObserver(self)
    }
    
    private func _addComponents(toView view: UIView) {
        
        _inputView.isHidden = false
        _inputAccessoryView.isHidden = false
        
        view.addSubview(_backgroundView)
        view.addSubview(_inputAccessoryView)
        view.addSubview(_inputView)
        
        // add the constraints
        _containerView = view
        _constraints = [
            
            _SAInputLayoutConstraintMake(_inputAccessoryView, .left, .equal, view, .left),
            _SAInputLayoutConstraintMake(_inputAccessoryView, .right, .equal, view, .right),
            //_SAInputLayoutConstraintMake(_inputAccessoryView, .Bottom, .Equal, view, .Bottom),
            _SAInputLayoutConstraintMake(_inputAccessoryView, .bottom, .equal, view, .bottom, output: &_inputAccessoryViewBottom),
            
            _SAInputLayoutConstraintMake(_inputView, .top, .equal, _inputAccessoryView, .bottom),
            _SAInputLayoutConstraintMake(_inputView, .left, .equal, view, .left),
            _SAInputLayoutConstraintMake(_inputView, .right, .equal, view, .right),
            //_SAInputLayoutConstraintMake(_inputView, .bottom, .equal, view, .bottom),
            
            _SAInputLayoutConstraintMake(_backgroundView, .top, .equal, _inputAccessoryView, .top),
            _SAInputLayoutConstraintMake(_backgroundView, .left, .equal, view, .left),
            _SAInputLayoutConstraintMake(_backgroundView, .right, .equal, view, .right),
            _SAInputLayoutConstraintMake(_backgroundView, .bottom, .equal, view, .bottom),
        ]
        view.addConstraints(_constraints)
    }
    private func _removeComponents(formView view: UIView?) {
        
        // remove the constraints
        view?.removeConstraints(_constraints)
        _constraints = []
        
        _inputView.removeFromSuperview()
        _inputAccessoryView.removeFromSuperview()
    }
    
    private func _init() {
        
        autoresizingMask = .flexibleHeight
        backgroundColor = .clear
        
//        let color = UIColor(colorLiteralRed: 0xec / 0xff, green: 0xed / 0xff, blue: 0xf1 / 0xff, alpha: 1)
        
        //_inputView.backgroundColor = color
        //_inputView.clipsToBounds = true
        _inputView.backgroundColor = .clear
        _inputView.translatesAutoresizingMaskIntoConstraints = false
        
        _inputAccessoryView.delegate = self
        _inputAccessoryView.translatesAutoresizingMaskIntoConstraints = false
        _inputAccessoryView.setContentHuggingPriority(UILayoutPriority.required, for: .vertical)
        _inputAccessoryView.setContentCompressionResistancePriority(UILayoutPriority.required, for: .vertical)
        //_inputAccessoryView.backgroundColor = color
        _inputAccessoryView.backgroundColor = .clear
        
        _backgroundView.translatesAutoresizingMaskIntoConstraints = false
        _backgroundView.setContentHuggingPriority(UILayoutPriority(rawValue: 1), for: .vertical)
        _backgroundView.setContentCompressionResistancePriority(UILayoutPriority(rawValue: 1), for: .vertical)
        _backgroundView.barTintColor = .white
        _backgroundView.isTranslucent = false // 毛玻璃效果还有bug
        //_backgroundView.barStyle = .black
        
        _addComponents(toView: self)
        _addNotifications()
    }
    private func _deinit() {
        
        _removeNotifications()
        _removeComponents(formView: self)
    }
    
    fileprivate var _visableKeybaordSize: CGSize {
        if _inputMode.isSelecting {
            return _cacheCustomKeyboardSize
        }
        return CGSize.zero
    }
    fileprivate var _keyboardSizeWithoutCache: CGSize {
        if _inputMode.isSelecting {
            return _cacheCustomKeyboardSize
        }
        return _cacheSystemKeyboardSize
    }
    fileprivate var _contentSizeWithoutCache: CGSize {
        var size = _inputAccessoryView.intrinsicContentSize
        // Append the keyboard size
        if _inputMode.isSelecting {
            size.height += _cacheCustomKeyboardSize.height //_inputView.intrinsicContentSize.height
        }
        return size
    }
    fileprivate var _frameInWindow: CGRect {
        guard let window = window else {
            return CGRect.zero
        }
        let ivheight = _inputAccessoryView.intrinsicContentSize.height
        let height = ivheight + max(_keyboardSizeWithoutCache.height, 0)
        
        return CGRect(x: 0, y: window.frame.height - height, width: window.frame.width, height: height)
    }
    
    // MARK: - 
    
    fileprivate var _inputMode: SAIInputMode = .none
    
    fileprivate var _inputViewBottom: NSLayoutConstraint?
    fileprivate var _inputAccessoryViewBottom: NSLayoutConstraint?
    
    fileprivate lazy var _inputView: SAIInputView = SAIInputView()
    fileprivate lazy var _inputAccessoryView: SAIInputAccessoryView = SAIInputAccessoryView()
    fileprivate lazy var _backgroundView: SAIInputBackgroundView = SAIInputBackgroundView()
    
    fileprivate lazy var _constraints: [NSLayoutConstraint] = []
    fileprivate lazy var _selectedItems: Set<SAIInputItem> = []
    
    fileprivate weak var _containerView: UIView?
    fileprivate weak var _displayable: SAIInputBarDisplayable?
    
    fileprivate var _cacheBounds: CGRect?
    fileprivate var _cacheContentSize: CGSize?
    fileprivate var _cacheKeyboardSize: CGSize?
    fileprivate var _cacheKeyboardOffset: CGPoint = .zero
    
    fileprivate var _cacheSystemKeyboardSize: CGSize = .zero
    fileprivate var _cacheCustomKeyboardSize: CGSize = .zero
    
    fileprivate var _cacheKeyboardIsInitialized: Bool = false
    
    // MARK: - 

    
//    open override class func initialize() {
//        _ = _ib_inputBar_once
//    }
    func initialize() {
        _ = _ib_inputBar_once
    }
    
    public override init(frame: CGRect) {
        super.init(frame: frame)
        _init()
    }
    public required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        _init()
    }
    
    deinit {
        _deinit()
    }
}

// MARK: - System Keyboard Event

extension SAIInputBar {
    
    @objc func ntf_keyboard(willShow sender: Notification) {
        guard let window = window else {
            return
        }
        _ntf_animation(sender) { bf, ef in
            let ef1 = window.frame.inset(by: UIEdgeInsets(top: ef.minY, left: 0, bottom: 0, right: 0))
            _updateSystemKeyboard(ef1.size, animated: false)
            
            _displayable?.ib_inputBar(self, showWithFrame: _frameInWindow)
        }
    }
    @objc func ntf_keyboard(willHide sender: Notification) {
        guard let window = window else {
            return
        }
        _ntf_animation(sender) { bf, ef in
            let ef1 = window.frame.inset(by: UIEdgeInsets(top: ef.minY, left: 0, bottom: 0, right: 0))
            
            _cacheSystemKeyboardSize = ef1.size
//            _updateSystemKeyboard(ef1.size, animated: false)
            _cacheKeyboardSize = _keyboardSizeWithoutCache
            
            _displayable?.ib_inputBar(self, hideWithFrame: _frameInWindow)
        }
    }
    @objc func ntf_keyboard(didScroll sender: UIPanGestureRecognizer) {
        // if inputbar state is `None`, ignore this event
        if _inputMode.isNone {
            return
        }
        // if recgognizer state is end, process custom event
        guard sender.state == .began || sender.state == .changed || sender.state == .possible else {
            // clear keyboard offset
            _cacheKeyboardOffset = CGPoint.zero
            // ignore system keyboard, in system keyboard, the show/dismiss is automatic process
            guard _inputMode.isSelecting else {
                return
            }
            // if nheight > height, this means that it at outside of the keyboard, cancel the event
            if sender.location(in: _inputAccessoryView).y < 0 {
                // cancel touch at outside
                _updateKeyboardOffsetIfNeeded(CGPoint.zero, animated: true)
            } else if sender.velocity(in: _inputAccessoryView).y <= 0 {
                // cancel touch at inside
                _updateKeyboardOffsetIfNeeded(CGPoint.zero, animated: true)
                _displayable?.ib_inputBar(self, showWithFrame: _frameInWindow)
            } else {
                // dismiss
                _updateInputMode(.none, animated: true)
                //_displayable?.ib_inputBar(self, hideWithFrame: _frameInWindow)
            }
            return
        }
        guard let window = self.window, sender.numberOfTouches != 0 else {
            return
        }
        // Must use the first touch to calculate the position
        let nheight = window.frame.height - sender.location(ofTouch: 0, in: window).y
        let kbheight = _keyboardSizeWithoutCache.height
        let iavheight = _inputAccessoryView.intrinsicContentSize.height
        let height = iavheight + kbheight
        let ty = height - min(max(nheight, iavheight), height)
        
        if _cacheKeyboardOffset.y != ty {
            // in editing(system keybaord), system automatic process
            if _inputMode.isSelecting {
                _updateKeyboardOffsetIfNeeded(CGPoint(x: 0, y: ty), animated: false)
            }
            _displayable?.ib_inputBar(self, didChangeOffset: CGPoint(x: 0, y: ty))
        }
        
        _cacheKeyboardOffset.y = ty
    }
    
    @objc func ntf_accessory(didChangeFrame sender: Notification) {
        
        let newCustomKeyboardSize = _inputView.intrinsicContentSize
        if _cacheCustomKeyboardSize.height != newCustomKeyboardSize.height {
            _cacheCustomKeyboardSize = newCustomKeyboardSize
            _updateKeyboardSizeIfNeeded(false)
        } else {
            _updateContentSizeIfNeeded(false)
        }
        // update in advance, don't wait for willShow event, otherwise there will be a delay
        _displayable?.ib_inputBar(self, didChangeFrame: _frameInWindow)
    }
    
    private func _ntf_flatMap(_ ntf: Notification, handler: (CGRect, CGRect, TimeInterval, UIView.AnimationCurve) -> ()) {
        guard let u = (ntf as NSNotification).userInfo,
            let bf = (u[UIResponder.keyboardFrameBeginUserInfoKey] as? NSValue)?.cgRectValue,
            let ef = (u[UIResponder.keyboardFrameEndUserInfoKey] as? NSValue)?.cgRectValue,
            let cv = (u[UIResponder.keyboardAnimationCurveUserInfoKey] as? Int),
            let dr = (u[UIResponder.keyboardAnimationDurationUserInfoKey] as? TimeInterval) else {
                return
        }
        let edg = UIEdgeInsets(top: intrinsicContentSize.height, left: 0, bottom: 0, right: 0)
        
        // rect correction
        let bf1 = bf.inset(by: edg)
        let ef1 = ef.inset(by: edg)
        
        let cv1 = UIView.AnimationCurve(rawValue: cv) ?? _SAInputDefaultAnimateCurve
        
        handler(bf1, ef1, dr, cv1)
    }
    private func _ntf_animation(_ ntf: Notification, handler: (CGRect, CGRect) -> Void) {
        _ntf_flatMap(ntf) { bf, ef, dr, cv in
            guard dr != 0 else {
                handler(bf, ef)
                return
            }
            UIView.beginAnimations("SAIB-ANI-KB", context: nil)
            UIView.setAnimationDuration(dr)
            UIView.setAnimationCurve(cv)
            handler(bf, ef)
            UIView.commitAnimations()
        }
    }
}

// MARK: - UITextView(Forwarding)

extension SAIInputBar: UIKeyInput {
    
    // UITextView
    
    open var text: String! {
        set { return _inputAccessoryView.textField.text = newValue }
        get { return _inputAccessoryView.textField.text }
    }
    open var font: UIFont? {
        set { return _inputAccessoryView.textField.font = newValue }
        get { return _inputAccessoryView.textField.font }
    }
    open var textColor: UIColor? {
        set { return _inputAccessoryView.textField.textColor = newValue }
        get { return _inputAccessoryView.textField.textColor }
    }
    
    open var attributedText: NSAttributedString! {
        set { return _inputAccessoryView.textField.attributedText = newValue }
        get { return _inputAccessoryView.textField.attributedText }
    }
    
    open var textAlignment: NSTextAlignment {
        set { return _inputAccessoryView.textField.textAlignment = newValue }
        get { return _inputAccessoryView.textField.textAlignment }
    }
    open var selectedRange: NSRange {
        set { return _inputAccessoryView.textField.selectedRange = newValue }
        get { return _inputAccessoryView.textField.selectedRange }
    }
    
    open var editable: Bool {
        set { return _inputAccessoryView.textField.isEditable = newValue }
        get { return _inputAccessoryView.textField.isEditable }
    }
    open var selectable: Bool {
        set { return _inputAccessoryView.textField.isSelectable = newValue }
        get { return _inputAccessoryView.textField.isSelectable }
    }
    
    // UIKeyInput(Forwarding)
    
    open var hasText: Bool {
        return _inputAccessoryView.textField.hasText
    }
    open func insertText(_ text: String) {
        _inputAccessoryView.textField.insertText(text)
        _inputAccessoryView.textViewDidChange(_inputAccessoryView.textField)
    }
    open func insertAttributedText(_ attributedText: NSAttributedString) {
        _inputAccessoryView.textField.insertAttributedText(attributedText)
        _inputAccessoryView.textViewDidChange(_inputAccessoryView.textField)
    }
    open func deleteBackward() {
        _inputAccessoryView.textField.deleteBackward()
        _inputAccessoryView.textViewDidChange(_inputAccessoryView.textField)
    }
    
    // UITextInputTraits(Forwarding)
    
    open var autocapitalizationType: UITextAutocapitalizationType {
        set { return _inputAccessoryView.textField.autocapitalizationType = newValue }
        get { return _inputAccessoryView.textField.autocapitalizationType }
    }
    open var autocorrectionType: UITextAutocorrectionType {
        set { return _inputAccessoryView.textField.autocorrectionType = newValue }
        get { return _inputAccessoryView.textField.autocorrectionType }
    }
    open var spellCheckingType: UITextSpellCheckingType {
        set { return _inputAccessoryView.textField.spellCheckingType = newValue }
        get { return _inputAccessoryView.textField.spellCheckingType }
    }
    open var keyboardType: UIKeyboardType {
        set { return _inputAccessoryView.textField.keyboardType = newValue }
        get { return _inputAccessoryView.textField.keyboardType }
    }
    open var keyboardAppearance: UIKeyboardAppearance {
        set { return _inputAccessoryView.textField.keyboardAppearance = newValue }
        get { return _inputAccessoryView.textField.keyboardAppearance }
    }
    open var returnKeyType: UIReturnKeyType {
        set { return _inputAccessoryView.textField.returnKeyType = newValue }
        get { return _inputAccessoryView.textField.returnKeyType }
    }
    open var enablesReturnKeyAutomatically: Bool {
        set { return _inputAccessoryView.textField.enablesReturnKeyAutomatically = newValue }
        get { return _inputAccessoryView.textField.enablesReturnKeyAutomatically }
    }
    open var isSecureTextEntry: Bool {
        @objc(setSecureTextEntry:) 
        set { return _inputAccessoryView.textField.isSecureTextEntry = newValue }
        @objc(isSecureTextEntry) get { return _inputAccessoryView.textField.isSecureTextEntry }
    }
}

// MARK: - UITextViewDelegate(Forwarding)

extension SAIInputBar: UITextViewDelegate {
    
    open func textViewShouldBeginEditing(_ textView: UITextView) -> Bool {
        if let r = delegate?.inputBar?(shouldBeginEditing: self), !r {
            return false
        }
        _updateInputModeForResponder(.editing, animated: true)
        return true
    }
    open func textViewShouldEndEditing(_ textView: UITextView) -> Bool {
        if let r = delegate?.inputBar?(shouldEndEditing: self), !r {
            return false
        }
        return true
    }
    
    open func textViewDidBeginEditing(_ textView: UITextView) {
        delegate?.inputBar?(didBeginEditing: self)
    }
    open func textViewDidEndEditing(_ textView: UITextView) {
        delegate?.inputBar?(didEndEditing: self)
        _updateInputModeForResponder(.none, animated: true)
    }
    
    open func textViewDidChangeSelection(_ textView: UITextView) {
        delegate?.inputBar?(didChangeSelection: self)
    }
    open func textViewDidChange(_ textView: UITextView) {
        delegate?.inputBar?(didChangeText: self)
    }

    open func textView(_ textView: UITextView, shouldInteractWith textAttachment: NSTextAttachment, in characterRange: NSRange) -> Bool {
        if let r = delegate?.inputBar?(self, shouldInteractWithTextAttachment: textAttachment, inRange: characterRange), !r {
            return false
        }
        return true
    }
    open func textView(_ textView: UITextView, shouldChangeTextIn range: NSRange, replacementText text: String) -> Bool {
        if let r = delegate?.inputBar?(self, shouldChangeCharactersInRange: range, replacementString: text), !r {
            return false
        }
        // This is return
        if text == "\n" {
            return delegate?.inputBar?(shouldReturn: self) ?? true
        }
        // This is clear
        if text.isEmpty && range.length - range.location == (textView.text as NSString).length {
            return delegate?.inputBar?(shouldClear: self) ?? true
        }
        return true
    }
    open func textView(_ textView: UITextView, shouldInteractWith URL: URL, in characterRange: NSRange) -> Bool {
        if let r = delegate?.inputBar?(self, shouldInteractWithURL: URL, inRange: characterRange), !r {
            return false
        }
        return true
    }
}

// MARK: - SAIInputAccessoryView(Forwarding)

extension SAIInputBar {
    
    open func barItems(atPosition position: SAIInputItemPosition) -> [SAIInputItem] {
        return _inputAccessoryView.barItems(atPosition: position)
    }
    open func setBarItem(_ barItem: SAIInputItem, atPosition position: SAIInputItemPosition, animated: Bool = true) {
        return _inputAccessoryView.setBarItems([barItem], atPosition: position, animated: animated)
    }
    open func setBarItems(_ barItems: [SAIInputItem], atPosition position: SAIInputItemPosition, animated: Bool = true) {
        return _inputAccessoryView.setBarItems(barItems, atPosition: position, animated: animated)
    }
    
    open func canSelectBarItem(_ barItem: SAIInputItem) -> Bool {
        return _inputAccessoryView.canSelectBarItem(barItem)
    }
    open func canDeselectBarItem(_ barItem: SAIInputItem) -> Bool {
        return _inputAccessoryView.canDeselectBarItem(barItem)
    }
    
    open func selectBarItem(_ barItem: SAIInputItem, animated: Bool) {
        _selectedItems.insert(barItem)
        return _inputAccessoryView.selectBarItem(barItem, animated: animated)
    }
    open func deselectBarItem(_ barItem: SAIInputItem, animated: Bool) {
        _selectedItems.remove(barItem)
        _inputAccessoryView.isShowRecordButton = false
        return _inputAccessoryView.deselectBarItem(barItem, animated: animated)
    }
    open func deselectBarAllItem() {
        _selectedItems.forEach{
            self.deselectBarItem($0, animated: true)
            self.barItem(didDeselectFor: $0)
        }
        _selectedItems = []
        _inputAccessoryView.isShowRecordButton = true
        self.setInputMode(.audio, animated: true)
    }
    
    open func editMode() {
        _selectedItems.forEach{
            self.deselectBarItem($0, animated: true)
            self.barItem(didDeselectFor: $0)
        }
        _selectedItems = []
        _inputAccessoryView.isShowRecordButton = false
        self.setInputMode(.editing, animated: true)
//        self.becomeFirstResponder()
    }
}

// MARK: - SAIInputItemViewDelegate(Forwarding)

extension SAIInputBar: SAIInputItemViewDelegate {
    
    open func barItem(shouldHighlightFor barItem: SAIInputItem) -> Bool {
        return delegate?.inputBar?(self, shouldHighlightFor: barItem) ?? true
    }
    open func barItem(shouldDeselectFor barItem: SAIInputItem) -> Bool {
//        if !allowsMultipleSelection {
//            return false // not allowed to cancel
//        }
        return delegate?.inputBar?(self, shouldDeselectFor: barItem) ?? true 
    }
    open func barItem(shouldSelectFor barItem: SAIInputItem) -> Bool {
        guard allowsSelection else {
            // do not allow the selected
            return false
        }
        if _selectedItems.contains(barItem) {
            // has been selected
            return false
        }
        guard delegate?.inputBar?(self, shouldSelectFor: barItem) ?? true else {
            // users are not allowed to select
            return false
        }
        if !allowsMultipleSelection {
            // don't allow a multiple-select, cancel has been chosen
            for item in _selectedItems  {
                if !(self.delegate?.inputBar?(self, shouldDeselectFor: item) ?? true) {
                    // Not allowed to cancel, so do not allow the selected
                    return false
                }
            }
            // 
            _selectedItems.forEach{ 
                self.deselectBarItem($0, animated: true)
//                self.barItem(didDeselectFor: $0)
                _selectedItems.remove(barItem)
            }
            _selectedItems = []
        }
        return true
    }
    
    open func barItem(didHighlightFor barItem: SAIInputItem) {
        delegate?.inputBar?(self, didHighlightFor: barItem)
    }
    open func barItem(didDeselectFor barItem: SAIInputItem) {
//        delegate?.inputBar?(self, didDeselectFor: barItem)
        // Remove from the selected list
        _selectedItems.remove(barItem)
//        self.editMode()
        _inputAccessoryView.isShowRecordButton = false
        let _ = self.becomeFirstResponder()
    }
    open func barItem(didSelectFor barItem: SAIInputItem) {
        delegate?.inputBar?(self, didSelectFor: barItem)
        // Added to the selected list
        _selectedItems.insert(barItem)
    }
}

extension SAIInputBar: SAIInputAccessoryViewDelegate {
    open func inputAccessoryView(touchDown recordButton: UIButton) {
        delegate?.inputBar?(touchDown: recordButton, inputBar: self)
    }
    open func inputAccessoryView(dragInside recordButton: UIButton) {
        delegate?.inputBar?(dragInside: recordButton, inputBar: self)
    }
    open func inputAccessoryView(dragOutside recordButton: UIButton) {
        delegate?.inputBar?(dragOutside: recordButton, inputBar: self)
    }
    open func inputAccessoryView(touchUpInside recordButton: UIButton) {
        delegate?.inputBar?(touchUpInside: recordButton, inputBar: self)
    }
    open func inputAccessoryView(touchUpOutside recordButton: UIButton) {
        delegate?.inputBar?(touchUpOutside: recordButton, inputBar: self)
    }
}
    

// MARK: -

private extension UIResponder {
    
    @objc func ib_overrideInputAccessoryViewNextResponderWithResponder(_ arg1: UIResponder?) {
        ib_nextResponderOverride = arg1
        return ib_overrideInputAccessoryViewNextResponderWithResponder(arg1)
    }
    
    var ib_nextResponderOverride: UIResponder? {
        set { return objc_setAssociatedObject(self, &_SAInputUIResponderNextResponderOverride, newValue, .OBJC_ASSOCIATION_ASSIGN) }
        get { return objc_getAssociatedObject(self, &_SAInputUIResponderNextResponderOverride) as? UIResponder }
    }
}

// MARK: -

private extension UIScrollView {
    
    // gesture recognizer handler
    @objc private func ib_handlePan(_ sender: UIPanGestureRecognizer) {
        ib_handlePan(sender)
        guard let inputBar = inputAccessoryView as? SAIInputBar else {
            return
        }
        if keyboardDismissMode == .onDrag {
            // is `OnDrag`
            guard inputBar.inputMode.isSelecting else {
                return
            }
            inputBar.setInputMode(.none, animated: true)
        } else if keyboardDismissMode == .interactive {
            // is `Interactive`
            inputBar.ntf_keyboard(didScroll: sender)
        }
    }
}

// MARK: -

private extension UIPresentationController {
    
    @objc func _preserveResponderAcrossWindows() -> Bool {
        // repair the iOS 8.1 bugs, if return true
        // system will invoke `_preserveInputViewsWithId:animated:reset:`
        // to save the input environment
        return true
    }
}

// MARK: -

internal func SAIInputBarLoad() {
    
    // 解释一下为什么采用这个方法, 因为swift没有不能重写load方法, 
    // 如果写在initialize可能会被其他库覆盖掉
    // 采用这个方法安全一点
    _SAInputExchangeSelector(UIScrollView.self, "handlePan:", "ib_handlePan:")
    
    // 计划中止, 复杂度略高
    //_SAInputExchangeSelector(NSClassFromString("UIInputSetContainerView"), "snapshotViewAfterScreenUpdates:", "ib_snapshotViewAfterScreenUpdates:")
    
    // 解决iOS8中的bug
    _SAInputExchangeSelector(UIResponder.self, "_overrideInputAccessoryViewNextResponderWithResponder:", "ib_overrideInputAccessoryViewNextResponderWithResponder:")
}

@inline(__always)
internal func _SAInputLayoutConstraintMake(_ item: AnyObject, _ attr1: NSLayoutConstraint.Attribute, _ related: NSLayoutConstraint.Relation, _ toItem: AnyObject? = nil, _ attr2: NSLayoutConstraint.Attribute = .notAnAttribute, _ constant: CGFloat = 0, _ multiplier: CGFloat = 1, output: UnsafeMutablePointer<NSLayoutConstraint?>? = nil) -> NSLayoutConstraint {
    
    let c = NSLayoutConstraint(item:item, attribute:attr1, relatedBy:related, toItem:toItem, attribute:attr2, multiplier:multiplier, constant:constant)
    if output != nil {
        output?.pointee = c
    }
    
    return c
}

@inline(__always)
internal func _SAInputExchangeSelector(_ cls: AnyClass?, _ sel1: String, _ sel2: String) {
    _SAInputExchangeSelector(cls, Selector(sel1), Selector(sel2))
}
@inline(__always)
internal func _SAInputExchangeSelector(_ cls: AnyClass?, _ sel1: Selector, _ sel2: Selector) {
    guard let cls = cls else {
        return
    }
    method_exchangeImplementations(class_getInstanceMethod(cls, sel1)!, class_getInstanceMethod(cls, sel2)!)
}

private var _ib_inputBar_once: Bool = {
    
    SAIInputBarLoad()
    SAIInputBarDisplayableLoad()
    return true
    
}()


private var SAIInputBarWillSnapshot = "SAIInputBarWillSnapshot"
private var SAIInputBarDidSnapshot = "SAIInputBarDidSnapshot"

private var _SAInputUIResponderNextResponderOverride = "_SAInputUIResponderNextResponderOverride"


internal var _SAInputDefaultAnimateDuration: TimeInterval = 0.25
internal var _SAInputDefaultAnimateCurve: UIView.AnimationCurve = UIView.AnimationCurve(rawValue: 7) ?? .easeInOut

internal var _SAInputDefaultTextFieldBackgroundImage: UIImage? = {
    // 生成默认图片
    
    let radius = CGFloat(8)
    let rect = CGRect(x: 0, y: 0, width: 32, height: 32)
    let path = UIBezierPath(roundedRect: rect, cornerRadius: radius)
    
    UIGraphicsBeginImageContextWithOptions(rect.size, false, UIScreen.main.scale)
    
    UIColor.white.setFill()
    
    path.fill()
    path.addClip()
    
    let image = UIGraphicsGetImageFromCurrentImageContext()
    
    UIGraphicsEndImageContext()
    
    return image?.resizableImage(withCapInsets: UIEdgeInsets(top: radius, left: radius, bottom: radius, right: radius))
}()

