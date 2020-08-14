//
//  UnitBreadcrumbViewCell.swift
//  O2Platform
//
//  Created by FancyLou on 2019/8/13.
//  Copyright © 2019 zoneland. All rights reserved.
//

import UIKit
import CocoaLumberjack



protocol UnitPickerBreadcrumbClickDelegate {
    func breadcrumbTap(name: String, distinguished: String)
}

class UnitBreadcrumbViewCell: UITableViewCell {

    @IBOutlet weak var breadcrumbScrollView: UIScrollView!
    var delegate: UnitPickerBreadcrumbClickDelegate?
    
    override func awakeFromNib() {
        super.awakeFromNib()
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)
    }
    
    func refreshBreadcrumb(breadcrumbList: [ContactBreadcrumbBean]) {
        if breadcrumbList.count > 0 {
            self.breadcrumbScrollView.removeSubviews()
            var oX = CGFloat(4.0)
            let arrowW = CGFloat(24)
            let arrowH = CGFloat(32)
            breadcrumbList.forEachEnumerated { (index, bar) in
                let name = bar.name
                var textColor:UIColor
                if breadcrumbList.count == (index+1) {
                    textColor = UIColor(hex:"#666666")
                }else {
                    textColor = base_color
                }
                let firstSize = name.getSize(with: 15)
                let oY = (self.breadcrumbScrollView.bounds.height - firstSize.height) / 2
                let firstLabel = UILabel(frame: CGRect(x: CGFloat(oX), y: oY, width: firstSize.width, height: firstSize.height))
                firstLabel.textAlignment = .left
                let textAttributes = [NSAttributedString.Key.foregroundColor: textColor,NSAttributedString.Key.font:UIFont(name:"PingFangSC-Regular",size:15)!]
                firstLabel.attributedText = NSMutableAttributedString(string: name, attributes: textAttributes)
                firstLabel.sizeToFit()
                oX += firstSize.width
                self.breadcrumbScrollView.addSubview(firstLabel)
                if breadcrumbList.count != (index+1) {
                    let arrowY = (self.breadcrumbScrollView.bounds.height - arrowH) / 2
                    let arrowImage = UIImageView(frame: CGRect(x: CGFloat(oX), y: arrowY, width: arrowW, height: arrowH))
                    arrowImage.image = UIImage(named: "arrow_r")
                    arrowImage.contentMode = .scaleAspectFit
                    self.breadcrumbScrollView.addSubview(arrowImage)
                    oX += arrowW
                }
                firstLabel.addTapGesture(action: { (rec) in
                    DDLogDebug("点击了 \(index)")
                    if breadcrumbList.count != (index+1) {
                        self.delegate?.breadcrumbTap(name: bar.name, distinguished: bar.key)
                    }
                })
            }
            var size = self.breadcrumbScrollView.contentSize
            size.width = oX
            self.breadcrumbScrollView.showsHorizontalScrollIndicator = true
            self.breadcrumbScrollView.contentSize = size
            self.breadcrumbScrollView.bounces = true
            //滚动到底部
            if self.breadcrumbScrollView.contentSize.width > self.breadcrumbScrollView.bounds.size.width {
                let point = CGPoint(x: self.breadcrumbScrollView.contentSize.width - self.breadcrumbScrollView.bounds.size.width + self.breadcrumbScrollView.contentInset.right, y: 0)
                self.breadcrumbScrollView.setContentOffset(point, animated: true)
            }
        }
    }

}
