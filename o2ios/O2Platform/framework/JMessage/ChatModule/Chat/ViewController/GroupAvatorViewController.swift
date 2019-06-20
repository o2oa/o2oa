//
//  GroupAvatorViewController.swift
//  JChat
//
//  Created by 邓永豪 on 2017/9/19.
//  Copyright © 2017年 HXHG. All rights reserved.
//

import UIKit

class GroupAvatorViewController: UIViewController {

    var group: JMSGGroup!

    override func viewDidLoad() {
        super.viewDidLoad()
        _init()
    }

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        navigationController?.navigationBar.setBackgroundImage(UIImage(), for: .default)
        navigationController?.navigationBar.shadowImage = UIImage()
    }

    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        navigationController?.navigationBar.setBackgroundImage(nil, for: .default)
        navigationController?.navigationBar.shadowImage = nil
        navigationController?.navigationBar.barTintColor = O2ThemeManager.color(for: "Base.base_color")!
    }

    fileprivate lazy var imagePicker: UIImagePickerController = {
        var picker = UIImagePickerController()
        picker.sourceType = .camera
        picker.cameraCaptureMode = .photo
        picker.allowsEditing = true
        picker.delegate = self
        return picker
    }()

    fileprivate lazy var imageView: UIImageView = {
        var imageView = UIImageView()
        imageView.frame = UIScreen.main.bounds
        imageView.contentMode = .scaleAspectFit
        imageView.contentScaleFactor = UIScreen.main.scale
        imageView.image = UIImage.loadImage("com_icon_group_50")
        return imageView
    }()

    private func _init() {
        self.title = "群头像"
        view.backgroundColor = .black
        _setupNavigation()

        view.addSubview(imageView)

        group.largeAvatarData { (data, id, error) in
            if let data = data {
                let image = UIImage(data: data)
                self.imageView.image = image
            }
        }
    }

    private func _setupNavigation() {
        let navButton = UIButton(frame: CGRect(x: 0, y: 0, width: 18, height: 18))
        navButton.setImage(UIImage.loadImage("com_icon_file_more"), for: .normal)
        navButton.addTarget(self, action: #selector(_more), for: .touchUpInside)
        let item = UIBarButtonItem(customView: navButton)
        navigationItem.rightBarButtonItems =  [item]
    }

    func _more() {
        let actionSheet = UIActionSheet(title: nil, delegate: self, cancelButtonTitle: "取消", destructiveButtonTitle: nil, otherButtonTitles: "从相册中选择", "拍照")
        actionSheet.show(in: self.view)
    }
}

extension GroupAvatorViewController: UIActionSheetDelegate {
    func actionSheet(_ actionSheet: UIActionSheet, clickedButtonAt buttonIndex: Int) {
        switch buttonIndex {
        case 1:
            let picker = UIImagePickerController()
            picker.delegate = self
            picker.sourceType = .photoLibrary
            let temp_mediaType = UIImagePickerController.availableMediaTypes(for: picker.sourceType)
            picker.mediaTypes = temp_mediaType!
            picker.allowsEditing = true
            picker.modalTransitionStyle = .coverVertical
            present(picker, animated: true, completion: nil)
        case 2:
            present(imagePicker, animated: true, completion: nil)
        default:
            break
        }
    }
}

extension GroupAvatorViewController: UINavigationControllerDelegate, UIImagePickerControllerDelegate {

    // MARK: - UIImagePickerControllerDelegate
    func imagePickerControllerDidCancel(_ picker: UIImagePickerController) {
        picker.dismiss(animated: true, completion: nil)
    }

    
    func imagePickerController(_ picker: UIImagePickerController, didFinishPickingMediaWithInfo info: [UIImagePickerController.InfoKey : Any]) {

        var image = info[UIImagePickerController.InfoKey.editedImage] as! UIImage?
        image = image?.fixOrientation()
        if image != nil {
            MBProgressHUD_JChat.showMessage(message: "正在上传", toView: view)

            guard let imageData = image?.jpegData(compressionQuality: 0.8) else {
                return
            }
            let info = JMSGGroupInfo()
            info.avatarData = imageData
            JMSGGroup.updateInfo(withGid: group.gid, groupInfo: info, completionHandler: { (result, error) in
                DispatchQueue.main.async(execute: { () -> Void in
                    MBProgressHUD_JChat.hide(forView: self.view, animated: true)
                    if error == nil {
                        MBProgressHUD_JChat.show(text: "上传成功", view: self.view)
                        self.imageView.image = image
                        NotificationCenter.default.post(name: NSNotification.Name(rawValue: kUpdateGroupInfo), object: nil)
                    } else {
                        MBProgressHUD_JChat.show(text: "上传失败", view: self.view)
                    }
                })
            })
        }

        picker.dismiss(animated: true, completion: nil)
    }
}
