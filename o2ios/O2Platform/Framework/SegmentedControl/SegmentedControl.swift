//
//  SegmentedControl.swift
//  SegmentedControl
//
//  Created by Xin Hong on 15/12/29.
//  Copyright © 2015年 Teambition. All rights reserved.
//

import UIKit

public protocol SegmentedControlDelegate: class {
    func segmentedControl(_ segmentedControl: SegmentedControl, didSelectIndex selectedIndex: Int)
    func segmentedControl(_ segmentedControl: SegmentedControl, didLongPressIndex longPressIndex: Int)
}

public extension SegmentedControlDelegate {
    func segmentedControl(_ segmentedControl: SegmentedControl, didSelectIndex selectedIndex: Int) {

    }

    func segmentedControl(_ segmentedControl: SegmentedControl, didLongPressIndex longPressIndex: Int) {

    }
}

open class SegmentedControl: UIControl {
    open weak var delegate: SegmentedControlDelegate?
    open fileprivate(set) var selectedIndex = 0 {
        didSet {
            setNeedsDisplay()
        }
    }
    open var segmentWidth: CGFloat?
    open var minimumSegmentWidth: CGFloat?
    open var maximumSegmentWidth: CGFloat?
    open var isAnimationEnabled = true
    open var isUserDragEnabled = true
    open fileprivate(set) var style: SegmentedControlStyle = .text

    open var selectionBoxStyle: SegmentedControlSelectionBoxStyle = .none
    open var selectionBoxColor = UIColor.blue
    open var selectionBoxCornerRadius: CGFloat = 0
    open var selectionBoxEdgeInsets = UIEdgeInsets.zero

    open var selectionIndicatorStyle: SegmentedControlSelectionIndicatorStyle = .none
    open var selectionIndicatorColor = UIColor.black
    open var selectionIndicatorHeight = SelectionIndicator.defaultHeight
    open var selectionIndicatorEdgeInsets = UIEdgeInsets.zero
    open var titleAttachedIconPositionOffset: (x: CGFloat, y: CGFloat ) = (0, 0)

    open fileprivate(set) var titles = [NSAttributedString]()
    open fileprivate(set) var selectedTitles: [NSAttributedString]?
    open fileprivate(set) var images = [UIImage]()
    open fileprivate(set) var selectedImages: [UIImage]?
    open fileprivate(set) var titleAttachedIcons: [UIImage]?
    open fileprivate(set) var selectedTitleAttachedIcons: [UIImage]?

    open var isLongPressEnabled = false {
        didSet {
            if isLongPressEnabled {
                longPressGesture = UILongPressGestureRecognizer()
                longPressGesture!.addTarget(self, action: #selector(segmentedControlLongPressed(_:)))
                longPressGesture!.minimumPressDuration = longPressMinimumPressDuration
                scrollView.addGestureRecognizer(longPressGesture!)
                longPressGesture!.delegate = self
            } else if let _ = longPressGesture {
                scrollView.removeGestureRecognizer(longPressGesture!)
                longPressGesture!.delegate = nil
                longPressGesture = nil
            }
        }
    }
    open var isUnselectedSegmentsLongPressEnabled = false
    open var longPressMinimumPressDuration: CFTimeInterval = 0.5 {
        didSet {
            assert(longPressMinimumPressDuration >= 0.5, "MinimumPressDuration of LongPressGestureRecognizer must be no less than 0.5")
            if let longPressGesture = longPressGesture {
                longPressGesture.minimumPressDuration = longPressMinimumPressDuration
            }
        }
    }
    open fileprivate(set) var isLongPressActivated = false

    fileprivate lazy var scrollView: SCScrollView = {
        let scrollView = SCScrollView()
        scrollView.scrollsToTop = false
        scrollView.isScrollEnabled = true
        scrollView.showsHorizontalScrollIndicator = false
        scrollView.showsVerticalScrollIndicator = false
        return scrollView
    }()
    fileprivate lazy var selectionBoxLayer = CALayer()
    fileprivate lazy var selectionIndicatorLayer = CALayer()
    fileprivate var longPressGesture: UILongPressGestureRecognizer?

    // MARK: - Public functions
    open class func initWithTitles(_ titles: [NSAttributedString], selectedTitles: [NSAttributedString]?) -> SegmentedControl {
        let segmentedControl = SegmentedControl(frame: CGRect.zero)
        segmentedControl.style = .text
        segmentedControl.titles = titles
        segmentedControl.selectedTitles = selectedTitles
        return segmentedControl
    }

    open class func initWithImages(_ images: [UIImage], selectedImages: [UIImage]?) -> SegmentedControl {
        let segmentedControl = SegmentedControl(frame: CGRect.zero)
        segmentedControl.style = .image
        segmentedControl.images = images
        segmentedControl.selectedImages = selectedImages
        return segmentedControl
    }

    open func setTitles(_ titles: [NSAttributedString], selectedTitles: [NSAttributedString]?) {
        style = .text
        self.titles = titles
        self.selectedTitles = selectedTitles
    }

    open func setImages(_ images: [UIImage], selectedImages: [UIImage]?) {
        style = .image
        self.images = images
        self.selectedImages = selectedImages
    }

    open func setTitleAttachedIcons(_ titleAttachedIcons: [UIImage]?, selectedTitleAttachedIcons: [UIImage]?) {
        self.titleAttachedIcons = titleAttachedIcons
        self.selectedTitleAttachedIcons = selectedTitleAttachedIcons
    }

    open func setSelected(at index: Int, animated: Bool) {
        if !(0..<segmentsCount() ~= selectedIndex) {
            return
        }
        selectedIndex = index
        scrollToSelectedIndex(animated: animated)
        if !animated {
            selectionBoxLayer.actions = ["position": NSNull(), "bounds": NSNull()]
            selectionIndicatorLayer.actions = ["position": NSNull(), "bounds": NSNull()]
            selectionBoxLayer.frame = frameForSelectionBox()
            selectionIndicatorLayer.frame = frameForSelectionIndicator()
        } else {
            selectionBoxLayer.actions = nil
            selectionIndicatorLayer.actions = nil
        }
    }

    // MARK: - Initialization
    public override init(frame: CGRect) {
        super.init(frame: frame)
        commonInit()
    }

    public required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        commonInit()
    }

    open override func awakeFromNib() {
        super.awakeFromNib()
        commonInit()
    }

    fileprivate func commonInit() {
        addSubview(scrollView)
        contentMode = .redraw
        if let parentViewController = scrollView.parentViewController {
            parentViewController.automaticallyAdjustsScrollViewInsets = false
        }
    }

    // MARK: - Overriding
    open override func layoutSubviews() {
        super.layoutSubviews()
        update()
    }

    open override var frame: CGRect {
        didSet {
            update()
        }
    }

    open override func willMove(toSuperview newSuperview: UIView?) {
        super.willMove(toSuperview: newSuperview)
        if newSuperview == nil {
            return
        }
        update()
    }

    open override func draw(_ rect: CGRect) {
        backgroundColor?.setFill()
        UIRectFill(bounds)

        scrollView.layer.sublayers?.removeAll(keepingCapacity: true)
        selectionBoxLayer.backgroundColor = selectionBoxColor.cgColor
        selectionIndicatorLayer.backgroundColor = selectionIndicatorColor.cgColor

        switch style {
        case .text:
            drawTitles()
        case .image:
            drawImages()
        }

        if selectionIndicatorStyle != .none {
            drawSelectionIndicator()
        }
        if selectionBoxStyle != .none {
            drawSelectionBox()
        }
    }

    open override func touchesEnded(_ touches: Set<UITouch>, with event: UIEvent?) {
        if isLongPressActivated {
            return
        }

        if let touch = touches.first {
            let touchLocation = touch.location(in: self)
            if !bounds.contains(touchLocation) {
                return
            }
            if singleSegmentWidth() == 0 {
                return
            }
            let touchIndex = Int((touchLocation.x + scrollView.contentOffset.x) / singleSegmentWidth())
            if 0..<segmentsCount() ~= touchIndex {
                if let delegate = delegate {
                    delegate.segmentedControl(self, didSelectIndex: touchIndex)
                }
                if touchIndex != selectedIndex {
                    setSelected(at: touchIndex, animated: isAnimationEnabled)
                }
            }
        }
    }
}

public extension SegmentedControl {
    // MARK: - Events
    fileprivate func update() {
        scrollView.contentInset = UIEdgeInsets.zero
        scrollView.frame = CGRect(origin: CGPoint.zero, size: frame.size)
        scrollView.isScrollEnabled = isUserDragEnabled
        scrollView.contentSize = CGSize(width: totalSegmentsWidth(), height: frame.height)
        scrollToSelectedIndex(animated: false)
    }

    fileprivate func scrollToSelectedIndex(animated: Bool) {
        let rectToScroll: CGRect = {
            var rectToScroll = self.rectForSelectedIndex()
            let scrollOffset = self.frame.width / 2 - self.singleSegmentWidth() / 2
            rectToScroll.origin.x -= scrollOffset
            rectToScroll.size.width += scrollOffset * 2
            return rectToScroll
        }()
        scrollView.scrollRectToVisible(rectToScroll, animated: animated)
    }
}

extension SegmentedControl: UIGestureRecognizerDelegate {
    // MARK: - UIGestureRecognizerDelegate
    open override func gestureRecognizerShouldBegin(_ gestureRecognizer: UIGestureRecognizer) -> Bool {
        if gestureRecognizer == longPressGesture {
            if let longPressIndex = locationIndex(for: gestureRecognizer) {
                return isUnselectedSegmentsLongPressEnabled ? true : longPressIndex == selectedIndex
            }
        }
        return false
    }

    @objc func segmentedControlLongPressed(_ gesture: UIGestureRecognizer) {
        switch gesture.state {
        case .possible:
            print("LongPressGesture Possible!")
            break
        case .began:
            print("LongPressGesture Began!")
            isLongPressActivated = true
            longPressDidBegin(gesture)
            break
        case .changed:
            print("LongPressGesture Changed!")
            break
        case .ended:
            print("LongPressGesture Ended!")
            isLongPressActivated = false
            break
        case .cancelled:
            print("LongPressGesture Cancelled!")
            isLongPressActivated = false
            break
        case .failed:
            print("LongPressGesture Failed!")
            isLongPressActivated = false
            break
        }
    }

    fileprivate func locationIndex(for gesture: UIGestureRecognizer) -> Int? {
        let longPressLocation = gesture.location(in: self)
        if !bounds.contains(longPressLocation) {
            return nil
        }
        if singleSegmentWidth() == 0 {
            return nil
        }
        let longPressIndex = Int((longPressLocation.x + scrollView.contentOffset.x) / singleSegmentWidth())
        return longPressIndex
    }

    fileprivate func longPressDidBegin(_ gesture: UIGestureRecognizer) {
        if let longPressIndex = locationIndex(for: gesture) {
            if longPressIndex != selectedIndex && !isUnselectedSegmentsLongPressEnabled {
                return
            }
            if 0..<segmentsCount() ~= longPressIndex {
                if let delegate = delegate {
                    delegate.segmentedControl(self, didLongPressIndex: longPressIndex)
                }
            }
        }
    }
}

public extension SegmentedControl {
    // MARK: - Drawing
    fileprivate func drawTitles() {
        for (index, title) in titles.enumerated() {
            let titleSize = sizeForAttributedString(title)
            let xPosition: CGFloat = {
                return singleSegmentWidth() * CGFloat(index) + (singleSegmentWidth() - titleSize.width) / 2
            }()
            let yPosition: CGFloat = {
                let yPosition = (frame.height - titleSize.height) / 2
                var yPositionOffset: CGFloat = 0
                switch selectionIndicatorStyle {
                case .top:
                    yPositionOffset = selectionIndicatorHeight / 2
                case .bottom:
                    yPositionOffset = -selectionIndicatorHeight / 2
                default:
                    break
                }
                return round(yPosition + yPositionOffset)
            }()
            let attachedIcon = index == selectedIndex ? selectedTitleAttachedIcon(at: index) : titleAttachedIcon(at: index)
            var attachedIconRect = CGRect.zero

            let titleRect: CGRect = {
                var titleRect = CGRect(origin: CGPoint(x: xPosition, y: yPosition), size: titleSize)

                if let attachedIcon = attachedIcon {
                    let addedWidth = attachedIcon.size.width + titleAttachedIconPositionOffset.x
                    titleRect.origin.x -= addedWidth / 2

                    let xPositionOfAttachedIcon = titleRect.origin.x + titleRect.width + titleAttachedIconPositionOffset.x
                    let yPositionOfAttachedIcon: CGFloat = {
                        let yPositionOfAttachedIcon = (frame.height - attachedIcon.size.height) / 2
                        var yPositionOffset = titleAttachedIconPositionOffset.y
                        switch selectionIndicatorStyle {
                        case .top:
                            yPositionOffset += selectionIndicatorHeight / 2
                        case .bottom:
                            yPositionOffset += -selectionIndicatorHeight / 2
                        default:
                            break
                        }
                        return round(yPositionOfAttachedIcon + yPositionOffset)
                    }()
                    attachedIconRect = CGRect(x: round(xPositionOfAttachedIcon), y: round(yPositionOfAttachedIcon), width: round(attachedIcon.size.width), height: round(attachedIcon.size.height))
                }

                return CGRect(x: round(titleRect.origin.x), y: round(titleRect.origin.y), width: round(titleRect.width), height: round(titleRect.height))
            }()

            let titleString: NSAttributedString = {
                if index == selectedIndex {
                    if let selectedTitle = selectedTitle(at: index) {
                        return selectedTitle
                    }
                }
                return title
            }()
            let titleLayer: CATextLayer = {
                let titleLayer = CATextLayer()
                titleLayer.frame = titleRect
                titleLayer.alignmentMode = CATextLayerAlignmentMode.center
                if #available(iOS 10.0, *) {
                    titleLayer.truncationMode = CATextLayerTruncationMode.none
                } else {
                    titleLayer.truncationMode = CATextLayerTruncationMode.end
                }
                titleLayer.string = titleString
                titleLayer.contentsScale = UIScreen.main.scale
                return titleLayer
            }()

            if let attachedIcon = attachedIcon {
                let attachedIconLayer = CALayer()
                attachedIconLayer.frame = attachedIconRect
                attachedIconLayer.contents = attachedIcon.cgImage
                scrollView.layer.addSublayer(attachedIconLayer)
            }

            scrollView.layer.addSublayer(titleLayer)
        }
    }

    fileprivate func drawImages() {
        for (index, image) in images.enumerated() {
            let xPosition: CGFloat = {
                return singleSegmentWidth() * CGFloat(index) + (singleSegmentWidth() - image.size.width) / 2
            }()
            let yPosition: CGFloat = {
                let yPosition = (frame.height - image.size.height) / 2
                var yPositionOffset: CGFloat = 0
                switch selectionIndicatorStyle {
                case .top:
                    yPositionOffset = selectionIndicatorHeight / 2
                case .bottom:
                    yPositionOffset = -selectionIndicatorHeight / 2
                default:
                    break
                }
                return round(yPosition + yPositionOffset)
            }()
            let imageRect: CGRect = {
                let imageRect = CGRect(origin: CGPoint(x: xPosition, y: yPosition), size: image.size)
                return CGRect(x: round(imageRect.origin.x), y: round(imageRect.origin.y), width: round(imageRect.width), height: round(imageRect.height))
            }()

            let contents: CGImage? = {
                if index == selectedIndex {
                    if let selectedImage = selectedImage(at: index) {
                        return selectedImage.cgImage
                    }
                }
                return image.cgImage
            }()
            let imageLayer: CALayer = {
                let imageLayer = CALayer()
                imageLayer.frame = imageRect
                imageLayer.contents = contents
                return imageLayer
            }()
            scrollView.layer.addSublayer(imageLayer)
        }
    }

    fileprivate func drawSelectionBox() {
        selectionBoxLayer.frame = frameForSelectionBox()
        selectionBoxLayer.cornerRadius = selectionBoxCornerRadius
        if selectionBoxLayer.superlayer == nil {
            scrollView.layer.insertSublayer(selectionBoxLayer, at: 0)
        }
    }

    fileprivate func drawSelectionIndicator() {
        selectionIndicatorLayer.frame = frameForSelectionIndicator()
        if selectionBoxLayer.superlayer == nil {
            if let _ = selectionIndicatorLayer.superlayer {
                scrollView.layer.insertSublayer(selectionIndicatorLayer, above: selectionBoxLayer)
            } else {
                scrollView.layer.insertSublayer(selectionIndicatorLayer, at: 0)
            }
        }
    }
}

public extension SegmentedControl {
    // MARK: - Helper
    fileprivate func sizeForAttributedString(_ attributedString: NSAttributedString) -> CGSize {
        let size = attributedString.size()
        return CGRect(origin: CGPoint.zero, size: size).integral.size
    }

    fileprivate func selectedImage(at index: Int) -> UIImage? {
        if let selectedImages = selectedImages {
            if 0..<selectedImages.count ~= index {
                return selectedImages[index]
            }
        }
        return nil
    }

    fileprivate func selectedTitle(at index: Int) -> NSAttributedString? {
        if let selectedTitles = selectedTitles {
            if 0..<selectedTitles.count ~= index {
                return selectedTitles[index]
            }
        }
        return nil
    }

    fileprivate func titleAttachedIcon(at index: Int) -> UIImage? {
        if let titleAttachedIcons = titleAttachedIcons {
            if 0..<titleAttachedIcons.count ~= index {
                return titleAttachedIcons[index]
            }
        }
        return nil
    }

    fileprivate func selectedTitleAttachedIcon(at index: Int) -> UIImage? {
        if let selectedTitleAttachedIcons = selectedTitleAttachedIcons {
            if 0..<selectedTitleAttachedIcons.count ~= index {
                return selectedTitleAttachedIcons[index]
            }
        }
        return nil
    }

    fileprivate func segmentsCount() -> Int {
        switch style {
        case .text:
            return titles.count
        case .image:
            return images.count
        }
    }

    fileprivate func frameForSelectionBox() -> CGRect {
        if selectionBoxStyle == .none {
            return CGRect.zero
        }

        let xPosition: CGFloat = {
            return singleSegmentWidth() * CGFloat(selectedIndex)
        }()
        let fullRect = CGRect(x: xPosition, y: 0, width: singleSegmentWidth(), height: frame.height)
        let boxRect = CGRect(x: fullRect.origin.x + selectionBoxEdgeInsets.left,
            y: fullRect.origin.y + selectionBoxEdgeInsets.top,
            width: fullRect.width - (selectionBoxEdgeInsets.left + selectionBoxEdgeInsets.right),
            height: fullRect.height - (selectionBoxEdgeInsets.top + selectionBoxEdgeInsets.bottom))
        return boxRect
    }

    fileprivate func frameForSelectionIndicator() -> CGRect {
        if selectionIndicatorStyle == .none {
            return CGRect.zero
        }

        let xPosition: CGFloat = {
            return singleSegmentWidth() * CGFloat(selectedIndex)
        }()
        let yPosition: CGFloat = {
            switch selectionIndicatorStyle {
            case .bottom:
                return frame.height - selectionIndicatorHeight
            case .top:
                return 0
            default:
                return 0
            }
        }()
        let fullRect = CGRect(x: xPosition, y: yPosition, width: singleSegmentWidth(), height: selectionIndicatorHeight)
        let indicatorRect = CGRect(x: fullRect.origin.x + selectionIndicatorEdgeInsets.left,
            y: fullRect.origin.y + selectionIndicatorEdgeInsets.top,
            width: fullRect.width - (selectionIndicatorEdgeInsets.left + selectionIndicatorEdgeInsets.right),
            height: fullRect.height - (selectionIndicatorEdgeInsets.top + selectionIndicatorEdgeInsets.bottom))
        return indicatorRect
    }

    fileprivate func rectForSelectedIndex() -> CGRect {
        return CGRect(x: singleSegmentWidth() * CGFloat(selectedIndex), y: 0, width: singleSegmentWidth(), height: frame.height)
    }

    fileprivate func singleSegmentWidth() -> CGFloat {
        func defaultSegmentWidth() -> CGFloat {
            if segmentsCount() == 0 {
                return 0
            }
            var segmentWidth = frame.width / CGFloat(segmentsCount())
            if let minimumSegmentWidth = minimumSegmentWidth {
                if segmentWidth < minimumSegmentWidth {
                    segmentWidth = minimumSegmentWidth
                }
            }
            if let maximumSegmentWidth = maximumSegmentWidth {
                if segmentWidth > maximumSegmentWidth {
                    segmentWidth = maximumSegmentWidth
                }
            }
            return segmentWidth
        }

        if let segmentWidth = segmentWidth {
            return segmentWidth
        }
        return defaultSegmentWidth()
    }

    fileprivate func totalSegmentsWidth() -> CGFloat {
        return CGFloat(segmentsCount()) * singleSegmentWidth()
    }
}
