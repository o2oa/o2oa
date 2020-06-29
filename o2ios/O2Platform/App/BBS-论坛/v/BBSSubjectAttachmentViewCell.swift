//
//  BBSSubjectAttachmentViewCell.swift
//  O2Platform
//
//  Created by FancyLou on 2020/6/28.
//  Copyright © 2020 zoneland. All rights reserved.
//

import UIKit

class BBSSubjectAttachmentViewCell: UITableViewCell {

    @IBOutlet weak var fileNameLabel: UILabel!
    @IBOutlet weak var typeImage: UIImageView!
    
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }
    
    func setAttachment(file: O2BBSSubjectAttachmentInfo)  {
        self.fileNameLabel.text = file.name
        self.setFileTypeImage(ext: file.ext)
    }

    private func setFileTypeImage(ext: String?) {
        if let type = ext {
            switch type {
            case "jpg", "png", "jepg", "gif":
                self.typeImage.image = UIImage(named: "icon_img")
                break
            case "html":
                self.typeImage.image = UIImage(named: "icon_html")
                break
            case "xls", "xlsx":
                self.typeImage.image = UIImage(named: "icon_excel")
                break
            case "doc", "docx":
                self.typeImage.image = UIImage(named: "icon_word")
                break
            case "ppt", "pptx":
                self.typeImage.image = UIImage(named: "icon_ppt")
                break
            case "pdf":
                self.typeImage.image = UIImage(named: "icon_pdf")
                break
            case "mp4":
                self.typeImage.image = UIImage(named: "icon_mp4")
                break
            case "mp3":
                self.typeImage.image = UIImage(named: "icon_mp3")
                break
            case "zip", "rar", "7z":
                self.typeImage.image = UIImage(named: "icon_zip")
                break
            default :
                self.typeImage.image = UIImage(named: "icon_moren")
                break
            }
        }else {
            self.typeImage.image = UIImage(named: "icon_moren")
        }
    }
    
}
