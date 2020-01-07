//
//  FileTableViewCell.swift
//  O2Platform
//
//  Created by 刘振兴 on 16/9/13.
//  Copyright © 2016年 zoneland. All rights reserved.
//

import UIKit
import CocoaLumberjack

protocol FileTableViewCellDelegate {
    func cellDidClicked(_ cell:FileTableViewCell,file:OOFile)
}



class FileTableViewCell: UITableViewCell {
    
    @IBOutlet weak var fileIconImageView: UIImageView!
    
    @IBOutlet weak var fileNameLabel: UILabel!
    
    @IBOutlet weak var btnActionImageView: CellTouchImageView!
    
    var delegate:FileTableViewCellDelegate?
    
    
    var file:OOFile? {
        didSet {
            self.fileNameLabel.text = file?.name
            switch  (file?.fileType)! {
            case .file:
                self.fileIconImageView.image = UIImage(named: calcFileIcon((file?.extend)!))
                break
            case .folder:
                self.fileIconImageView.image = UIImage(named: "file_folder_icon")
            }
            
            self.btnActionImageView.addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(btnActionClick(_:))))
            
//            let tap = UITapGestureRecognizer(target: self, action:  #selector(btnActionClick(_:)))
//            tap.delegate = self
//            self.btnActionImageView.addGestureRecognizer(tap)
        }
    }
    
    @objc func btnActionClick(_ sender:AnyObject?){
        //let imageView = sender as? UIImageView
        DDLogDebug("btn action Clicked")
        DDLogDebug("cell frame = \(self.frame),cell center = \(self.center)")
        delegate?.cellDidClicked(self, file: self.file!)
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


    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }
//    override func touchesBegan(touches: Set<UITouch>, withEvent event: UIEvent?) {
//        DDLogDebug(event.debugDescription)
//    }
//    
//    override func gestureRecognizer(gestureRecognizer: UIGestureRecognizer, shouldReceiveTouch touch: UITouch) -> Bool {
//        let point = touch.locationInView(self.btnActionImageView)
//        DDLogDebug("point  = \(point)")
//        return false
//    }

}



