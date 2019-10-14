//
//  MyFileItemCell.swift
//  O2Platform
//
//  Created by 刘振兴 on 16/9/20.
//  Copyright © 2016年 zoneland. All rights reserved.
//

import UIKit

class MyFileItemCell: UITableViewCell {
    
    
    @IBOutlet weak var iconImageView: UIImageView!
    
    @IBOutlet weak var nameLabel: UILabel!
    
    @IBOutlet weak var btnActionImageView: CellTouchImageView!
    
    var fileName:String? {
        didSet {
            self.nameLabel.text = fileName!
            let extName = fileName?.components(separatedBy: ".").last!
            self.iconImageView.image = UIImage(named: calcFileIcon(extName!))
            
        }
    }

    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }
    
    func calcFileIcon(_ ext:String) -> String{
        switch ext {
        case "doc","docx":
            return "file_doc_icon"
        case "xls","xlsx":
            return "file_excel_icon"
        case "ppt","pptx":
            return "file_ppt_icon"
        case "pdf":
            return "file_pdf_icon"
        case "rar","zip","war":
            return "file_compressFile_icon"
        case "txt":
            return "file_txt_icon"
        case "jpg","png","gif","jpeg":
            return "file_image_icon"
        default:
            return "file_unknown_icon"
        }
    }

}
