//
//  FileViewController.swift
//  JChat
//
//  Created by 邓永豪 on 2017/8/28.
//  Copyright © 2017年 HXHG. All rights reserved.
//

import UIKit

enum FileType {
    case doc
    case music
    case video
    case other
}

class FileViewController: UIViewController {
    
    var isEditModel: Bool {
        get {
            return isEdit
        } set {
            isEdit = newValue
            selectMessages = []
            tableView.reloadData()
        }
    }

    
    var fileType = FileType.doc // default
    var messages: [JMSGMessage] = []
    var selectMessages: [JMSGMessage] = []

    override func viewDidLoad() {
        super.viewDidLoad()
        _init()
    }
    
    func reloadDate() {
        sortMessage()
        tableView.reloadData()
    }
    
    fileprivate var isEdit = false
    fileprivate var tableView: UITableView = UITableView(frame: .zero, style: .grouped)
    fileprivate lazy var data: Dictionary<String, [JMSGMessage]> = Dictionary()
    fileprivate lazy var keys: [String] = []
    
    fileprivate lazy var documentInteractionController = UIDocumentInteractionController()
    
    private func _init() {
        view.backgroundColor = UIColor(netHex: 0xe8edf3)
        tableView.backgroundColor = UIColor(netHex: 0xe8edf3)
        tableView.delegate = self
        tableView.dataSource = self
        tableView.separatorStyle = .none
        tableView.register(FileCell.self, forCellReuseIdentifier: "FileCell")
        view.addSubview(tableView)
        view.addConstraint(_JCLayoutConstraintMake(tableView, .left, .equal, view, .left))
        view.addConstraint(_JCLayoutConstraintMake(tableView, .top, .equal, view, .top))
        view.addConstraint(_JCLayoutConstraintMake(tableView, .right, .equal, view, .right))
        view.addConstraint(_JCLayoutConstraintMake(tableView, .bottom, .equal, view, .bottom))
    }
    
    func sortMessage() {
        keys.removeAll()
        data.removeAll()
        for message in messages {
            let formatter = DateFormatter()
            formatter.dateFormat = "yyyy-MM";
            let date = Date(timeIntervalSince1970: TimeInterval(message.timestamp.intValue / 1000))
            let key = formatter.string(from: date)
            var array = data[key]
            if array == nil {
                array = [message]
            } else {
                array?.append(message)
            }
            if !keys.contains(key) {
                keys.append(key)
            }
            data[key] = array
        }
        keys = keys.sorted(by: { (str1, str2) -> Bool in
            str1 > str2
        })
    }
}

//Mark: -
extension FileViewController: UITableViewDelegate, UITableViewDataSource {
    
    func numberOfSections(in tableView: UITableView) -> Int {
        return keys.count
    }
    
    public func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return data[keys[section]]!.count
    }
    
    func tableView(_ tableView: UITableView, titleForHeaderInSection section: Int) -> String? {
        return keys[section]
    }
    
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return 84
    }
    
    func tableView(_ tableView: UITableView, heightForFooterInSection section: Int) -> CGFloat {
        return 26
    }
    
    func tableView(_ tableView: UITableView, heightForHeaderInSection section: Int) -> CGFloat {
        return 0.001
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        return tableView.dequeueReusableCell(withIdentifier: "FileCell", for: indexPath)
    }
    
    func tableView(_ tableView: UITableView, willDisplay cell: UITableViewCell, forRowAt indexPath: IndexPath) {
        guard let cell = cell as? FileCell else {
            return
        }
        let message = data[keys[indexPath.section]]![indexPath.row]
        let content = message.content as! JMSGFileContent
        var image = UIImage()
        switch fileType {
        case .doc:
            image = UIImage.loadImage("com_icon_file_file")!
        case .music:
            image = UIImage.loadImage("com_icon_file_music")!
        case .video:
            image = UIImage.loadImage("com_icon_file_video")!
        default:
            image = UIImage.loadImage("com_icon_file_other")!
        }
        let file = File(image, content.fileName, message.ex.fileSize ?? "", "\(message.fromName) \(keys[indexPath.section])")
        cell.bindData(file)
        cell.isEditMode = isEdit
        if !isEditModel {
            cell.isSelectImage = false
        }
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: true)
        guard let cell = tableView.cellForRow(at: indexPath) as? FileCell else {
            return
        }
        let message = data[keys[indexPath.section]]![indexPath.row]
        if cell.isEditMode {
            if cell.isSelectImage {
                selectMessages = selectMessages.filter({ (m) -> Bool in
                    message.msgId != m.msgId
                })
            } else {
                selectMessages.append(message)
            }
            cell.isSelectImage = !cell.isSelectImage
            NotificationCenter.default.post(name: NSNotification.Name(rawValue: "kDidSelectFileMessage"), object: nil)
        } else {
            let content = message.content as! JMSGFileContent
            let type = message.ex.fileType
            let fileName = content.fileName
            switch fileType {
            case .doc:
                content.fileData({ (data, msgId, error) in
                    let vc = JCDocumentViewController()
                    vc.title = fileName
                    vc.fileData = data
                    vc.filePath = content.originMediaLocalPath
                    vc.fileType = type
                    DispatchQueue.main.async {
                        self.navigationController?.pushViewController(vc, animated: true)
                    }
                })
                
            case .video, .music:
                let url = URL(fileURLWithPath: content.originMediaLocalPath ?? "")
                try! JCVideoManager.playVideo(data: Data(contentsOf: url), type ?? "", currentViewController: self)
            default:
                let url = URL(fileURLWithPath: content.originMediaLocalPath ?? "")
                documentInteractionController.url = url
                documentInteractionController.presentOptionsMenu(from: .zero, in: self.view, animated: true)
            }
        }
    }
}

extension FileViewController: UIDocumentInteractionControllerDelegate {
    func documentInteractionControllerViewControllerForPreview(_ controller: UIDocumentInteractionController) -> UIViewController {
        return self
    }
    func documentInteractionControllerViewForPreview(_ controller: UIDocumentInteractionController) -> UIView? {
        return view
    }
    func documentInteractionControllerRectForPreview(_ controller: UIDocumentInteractionController) -> CGRect {
        return view.frame
    }
}

