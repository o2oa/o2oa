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
            breadcrumbList.forEachEnumerated { (index, bar) in
                var name: String
                var textColor:UIColor
                if breadcrumbList.count == (index+1) {
                    name = bar.name
                    textColor = base_color
                }else {
                    name = bar.name + " > "
                    textColor = UIColor(hex:"#333333")
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
                firstLabel.addTapGesture(action: { (rec) in
                    DDLogDebug("点击了 \(index)")
                    if breadcrumbList.count != (index+1) {
                        self.delegate?.breadcrumbTap(name: bar.name, distinguished: bar.key)
                    }
                })
            }
            var size = self.breadcrumbScrollView.contentSize;
            size.width = oX;
            self.breadcrumbScrollView.showsHorizontalScrollIndicator = true;
            self.breadcrumbScrollView.contentSize = size;
            self.breadcrumbScrollView.bounces = true;
            
        }
    }

}
