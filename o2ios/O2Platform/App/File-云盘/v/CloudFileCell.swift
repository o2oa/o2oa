//
//  CloudFileCell.swift
//  O2Platform
//
//  Created by FancyLou on 2019/10/8.
//  Copyright © 2019 zoneland. All rights reserved.
//

import UIKit

class CloudFileCell: UITableViewCell {
    @IBOutlet weak var fileImage: UIImageView!
    @IBOutlet weak var fileNameLabel: UILabel!
    @IBOutlet weak var fileUpdateTimeLabel: UILabel!
    @IBOutlet weak var fileSizeLabel: UILabel!
    @IBOutlet weak var checkBoxImage: UIImageView!
    
    var checkDelegate: CloudFileCheckDelegate?
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)
    }

    func setData(file: OOAttachment) {
        self.fileNameLabel.text = file.name ?? ""
        self.fileUpdateTimeLabel.text = file.lastUpdateTime ?? ""
        self.fileSizeLabel.text = self.formatSize(len: file.length)
        self.setFileTypeImage(ext: file.extension)
        if self.checkDelegate != nil {
            self.checkBoxImage.addTapGesture { (tap) in
                self.checkDelegate?.checkItem(file)
            }
        }
    }
    
    
    private func formatSize(len: Int?) -> String {
        if let size = len {
            if size < 1024 {
                return size.toString
            } else if size < (1024 * 1024) {
                return (size / 1024).toString + "K"
            } else {
                 return (size / 1024 / 1024).toString + "M"
            }
        }else {
            return "0K"
        }
    }
    
    private func setFileTypeImage(ext: String?) {
        if let type = ext {
            switch type {
            case "jpg", "png", "jepg", "gif":
                self.fileImage.image = UIImage(named: "icon_img")
                break
            case "html":
                self.fileImage.image = UIImage(named: "icon_html")
                break
            case "xls", "xlsx":
                self.fileImage.image = UIImage(named: "icon_excel")
                break
            case "doc", "docx":
                self.fileImage.image = UIImage(named: "icon_word")
                break
            case "ppt", "pptx":
                self.fileImage.image = UIImage(named: "icon_ppt")
                break
            case "pdf":
                self.fileImage.image = UIImage(named: "icon_pdf")
                break
            case "mp4":
                self.fileImage.image = UIImage(named: "icon_mp4")
                break
            case "mp3":
                self.fileImage.image = UIImage(named: "icon_mp3")
                break
            case "zip", "rar", "7z":
                self.fileImage.image = UIImage(named: "icon_zip")
                break
            default :
                self.fileImage.image = UIImage(named: "icon_moren")
                break
            }
        }else {
            self.fileImage.image = UIImage(named: "icon_moren")
        }
    }
    
    
}
