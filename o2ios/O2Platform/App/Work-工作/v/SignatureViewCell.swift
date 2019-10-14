//
//  SignatureViewCell.swift
//  O2Platform
//
//  Created by FancyLou on 2018/9/18.
//  Copyright © 2018 zoneland. All rights reserved.
//

import UIKit
import Eureka

public class SignatureViewCell: Cell<String>, CellType {
    @IBOutlet weak var clearBtn: UIImageView!
    @IBOutlet weak var signView: O2UISignatureView!

    @IBOutlet weak var signViewHeightConstraint: NSLayoutConstraint!
    
    public override func setup() {
        super.setup()
        let width = UIScreen.main.bounds.width
        print("screen width:\(width)")
        let height = width * 2 / 3
        print(" cal height:\(height)")
        self.signViewHeightConstraint.constant = height
        self.height = {return height}
        clearBtn.addTapGesture { (ges) in
            self.signView.clearSignature()
        }
    }
    
}

public final class SignatureViewRow: Row<SignatureViewCell>, RowType {
    public required init(tag: String?) {
        super.init(tag: tag)
        // 我们把对应SignatureViewCell的 .xib 加载到cellProvidor
        cellProvider = CellProvider<SignatureViewCell>(nibName: "SignatureViewCell")
    }
}
