//
//  CFFileTableViewCell.swift
//  O2Platform
//
//  Created by FancyLou on 2019/10/17.
//  Copyright © 2019 zoneland. All rights reserved.
//

import UIKit
import CocoaLumberjack

class CFFileTableViewCell: UITableViewCell {
   
    
    @IBOutlet weak var checkBoxButton: UIButton!
    @IBOutlet weak var fileTypeImageView: UIImageView!
    @IBOutlet weak var fileSizeLabel: UILabel!
    @IBOutlet weak var fileTimeLabel: UILabel!
    @IBOutlet weak var fileNameLabel: UILabel!
    
    
    
    var file: OOAttachment?
    var clickdelegate: CloudFileCheckClickDelegate?
    var showCheckBox = true
    
    
    @IBAction func clickBoxAction(_ sender: Any) {
        DDLogDebug("点击。。。。。。。。CFFileTableViewCell")
        if self.showCheckBox {
            if self.file != nil {
                DDLogDebug("enter click file")
                self.clickdelegate?.clickFile(self.file!)
            }
        }
    }
    
    override func awakeFromNib() {
        super.awakeFromNib()
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)
    }
    
    func setData(file: OOAttachment, checked: Bool, isShowCheck: Bool = true) {
        self.file = file
        self.fileNameLabel.text = file.name ?? ""
        self.fileTimeLabel.text = file.lastUpdateTime ?? ""
        self.fileSizeLabel.text = self.formatSize(len: file.length)
        self.setFileTypeImage(ext: file.extension)
        self.showCheckBox = isShowCheck
        if self.showCheckBox {
            self.checkBoxButton.isHidden = false
            if checked {
                self.checkBoxButton.setImage(UIImage(named: "icon__ok2_click"), for: .normal)
            }else {
                self.checkBoxButton.setImage(UIImage(named: "icon_circle"), for: .normal)
            }
        }else {
            self.checkBoxButton.isHidden = true
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
                self.fileTypeImageView.image = UIImage(named: "icon_img")
                break
            case "html":
                self.fileTypeImageView.image = UIImage(named: "icon_html")
                break
            case "xls", "xlsx":
                self.fileTypeImageView.image = UIImage(named: "icon_excel")
                break
            case "doc", "docx":
                self.fileTypeImageView.image = UIImage(named: "icon_word")
                break
            case "ppt", "pptx":
                self.fileTypeImageView.image = UIImage(named: "icon_ppt")
                break
            case "pdf":
                self.fileTypeImageView.image = UIImage(named: "icon_pdf")
                break
            case "mp4":
                self.fileTypeImageView.image = UIImage(named: "icon_mp4")
                break
            case "mp3":
                self.fileTypeImageView.image = UIImage(named: "icon_mp3")
                break
            case "zip", "rar", "7z":
                self.fileTypeImageView.image = UIImage(named: "icon_zip")
                break
            default :
                self.fileTypeImageView.image = UIImage(named: "icon_moren")
                break
            }
        }else {
            self.fileTypeImageView.image = UIImage(named: "icon_moren")
        }
    }
    
}
