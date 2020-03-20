//
//  JCUserInfoViewController.swift
//  JChat
//
//  Created by deng on 2017/3/16.
//  Copyright © 2017年 HXHG. All rights reserved.
//

import UIKit
import YHPopupView

class JCMyInfoViewController: UIViewController {

    override func viewDidLoad() {
        super.viewDidLoad()
        _init()
    }

    deinit {
        NotificationCenter.default.removeObserver(self)
    }
    
    fileprivate lazy var tableview: UITableView = {
        var tableview = UITableView(frame: CGRect(x: 0, y: 64, width: self.view.width, height: self.view.height - 64), style: .grouped)
        tableview.delegate = self
        tableview.dataSource = self
        tableview.register(JCMyAvatorCell.self, forCellReuseIdentifier: "JCMyAvatorCell")
        tableview.register(JCMyInfoCell.self, forCellReuseIdentifier: "JCMyInfoCell")
        tableview.separatorStyle = .none
        tableview.backgroundColor = UIColor(netHex: 0xe8edf3)
        return tableview
    }()
    fileprivate lazy var imagePicker: UIImagePickerController = {
        var picker = UIImagePickerController()
        picker.sourceType = .camera
        picker.cameraCaptureMode = .photo
        picker.allowsEditing = true
        picker.delegate = self
        return picker
    }()
    
    fileprivate lazy var user: JMSGUser = JMSGUser.myInfo()
   
    fileprivate lazy var datePickerPopupView: YHPopupView = {
        let popupView = YHPopupView(frame: CGRect(x: 0, y: self.view.height - 256, width: self.view.width, height: 256))
        var datePickerView = JCDatePickerViwe(frame: CGRect(x: 0, y: 0, width: self.view.width, height: 256))
        datePickerView.delegate = self
        popupView?.clickBlankSpaceDismiss = true
        popupView?.addSubview(datePickerView)
        return popupView!
    }()
    
    
    fileprivate lazy var areaPickerPopupView: YHPopupView = {
        let popupView = YHPopupView(frame: CGRect(x: 0, y: self.view.height - 256, width: self.view.width, height: 256))
        var areaPickerView = JCAreaPickerView(frame: CGRect(x: 0, y: 0, width: (popupView?.width)!, height: 256))
        areaPickerView.delegate = self
        popupView?.clickBlankSpaceDismiss = true
        popupView?.addSubview(areaPickerView)
        return popupView!
    }()

    //MARK: - private func
    private func _init() {
        self.title = "个人信息"
        automaticallyAdjustsScrollViewInsets = false
        view.addSubview(tableview)
        NotificationCenter.default.addObserver(self, selector: #selector(_updateUserInfo), name: NSNotification.Name(rawValue: kUpdateUserInfo), object: nil)
    }
    
    @objc func _updateUserInfo() {
        user = JMSGUser.myInfo()
        tableview.reloadData()
    }
}

//MARK: - UITableViewDataSource & UITableViewDelegate
extension JCMyInfoViewController: UITableViewDataSource, UITableViewDelegate {
    func numberOfSections(in tableView: UITableView) -> Int {
        return 2
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if section == 0 || section == 2 {
            return 1
        }
        return 6
    }
    
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        if indexPath.section == 0 {
            return 153
        }
        return 45
    }
    
    func tableView(_ tableView: UITableView, heightForFooterInSection section: Int) -> CGFloat {
        if section == 1 {
            return 15
        }
        return 5
    }
    
    func tableView(_ tableView: UITableView, heightForHeaderInSection section: Int) -> CGFloat {
        return 0.0001
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        if indexPath.section == 0 {
            return tableView.dequeueReusableCell(withIdentifier: "JCMyAvatorCell", for: indexPath)
        }
        return tableView.dequeueReusableCell(withIdentifier: "JCMyInfoCell", for: indexPath)
    }
    
    func tableView(_ tableView: UITableView, willDisplay cell: UITableViewCell, forRowAt indexPath: IndexPath) {
        
        cell.selectionStyle = .none
        
        if indexPath.section == 0 {
            guard let cell = cell as? JCMyAvatorCell else {
                return
            }
            cell.bindData(user: user)
        }
        
        if indexPath.section == 1 {
            guard let cell = cell as? JCMyInfoCell else {
                return
            }
            cell.accessoryType = .disclosureIndicator
            switch indexPath.row {
            case 0:
                cell.title = "昵称"
                cell.detail = user.nickname ?? ""
                cell.icon = UIImage.loadImage("com_icon_nickname")
            case 1:
                cell.title = "二维码"
                cell.detail = "我的二维码"
                cell.icon = UIImage.loadImage("com_icon_qrcode")
            case 2:
                cell.title = "性别"
                cell.icon = UIImage.loadImage("com_icon_gender")
                switch user.gender {
                case .male:
                    cell.detail = "男"
                case .female:
                    cell.detail = "女"
                case .unknown:
                    cell.detail = "保密"
                }
            case 3:
                cell.title = "生日"
                cell.icon = UIImage.loadImage("com_icon_birthday")
                cell.detail = user.birthday
            case 4:
                cell.title = "地区"
                cell.icon = UIImage.loadImage("com_icon_region")
                cell.detail = user.region
            case 5:
                cell.title = "个性签名"
                cell.icon = UIImage.loadImage("com_icon_signature")
                cell.detail = user.signature
            default:
                break
            }
        }
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        if indexPath.section == 0 {
            let actionSheet = UIActionSheet(title: nil, delegate: self, cancelButtonTitle: "取消", destructiveButtonTitle: nil, otherButtonTitles: "  从相册中选择", "拍照")
            actionSheet.tag = 1001
            actionSheet.show(in: view)
        }
        
        if indexPath.section == 1 {
            switch indexPath.row {
            case 0:
                let vc = JCNicknameViewController()
                vc.nickName = user.nickname ?? ""
                navigationController?.pushViewController(vc, animated: true)
            case 1:
                navigationController?.pushViewController(MyQRCodeViewController(), animated: true)
            case 2:
                let actionSheet = UIActionSheet(title: nil, delegate: self, cancelButtonTitle: "取消", destructiveButtonTitle: nil, otherButtonTitles: "男", "女", "保密")
                actionSheet.tag = 1002
                actionSheet.show(in: view)
            case 3:
                presentPopupView(datePickerPopupView)
            case 4:
                presentPopupView(areaPickerPopupView)
            case 5:
                let vc = JCSignatureViewController()
                vc.signature = user.signature ?? ""
                navigationController?.pushViewController(vc, animated: true)
            default:
                break
            }
        }
    }
   
}

extension JCMyInfoViewController: UIActionSheetDelegate {
    func actionSheet(_ actionSheet: UIActionSheet, clickedButtonAt buttonIndex: Int) {
        let user = JMSGUser.myInfo()
        if actionSheet.tag == 1001 {
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
        
        if actionSheet.tag == 1002 {
            var index = buttonIndex
            if index == 0 {
                return
            }
            if index == 3 {
                index = 0
            }
            if index == Int((user.gender.rawValue)) {
                return
            }
            MBProgressHUD_JChat.showMessage(message: "修改中", toView: view)
            JMSGUser.updateMyInfo(withParameter: NSNumber(value: index), userFieldType: .fieldsGender, completionHandler: { (resultObject, error) -> Void in
                DispatchQueue.main.async {
                    MBProgressHUD_JChat.hide(forView: self.view, animated: true)
                    if error == nil {
                        MBProgressHUD_JChat.show(text: "修改成功", view: self.view)
                        self.tableview.reloadData()
                    } else {
                        MBProgressHUD_JChat.show(text: "\(String.errorAlert(error! as NSError))", view: self.view)
                    }
                }
            })
        }
    }
        
}

extension JCMyInfoViewController: UINavigationControllerDelegate, UIImagePickerControllerDelegate {
    
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
            
            JMSGUser.updateMyInfo(withParameter: imageData, userFieldType: .fieldsAvatar) { (resultObject, error) -> Void in
                DispatchQueue.main.async(execute: { () -> Void in
                     MBProgressHUD_JChat.hide(forView: self.view, animated: true)
                    if error == nil {
                        MBProgressHUD_JChat.show(text: "上传成功", view: self.view)
                        NotificationCenter.default.post(name: Notification.Name(rawValue: kUpdateUserInfo), object: nil)
                        self.tableview.reloadData()
                        let avatorData = NSKeyedArchiver.archivedData(withRootObject: imageData)
                        UserDefaults.standard.set(avatorData, forKey: kLastUserAvator)
                        
                    } else {
                        MBProgressHUD_JChat.show(text: "上传失败", view: self.view)
                    }
                })
            }
        }
        
        picker.dismiss(animated: true, completion: nil)
    }
}

extension JCMyInfoViewController: JCDatePickerViweDelegate {
    func datePicker(cancel cancelButton: UIButton, date: Date) {
        dismissPopupView()
    }
    func datePicker(finish finishButton: UIButton, date: Date) {
        dismissPopupView()
        MBProgressHUD_JChat.showMessage(message: "修改中", toView: view)
        JMSGUser.updateMyInfo(withParameter: NSNumber(value: date.timeIntervalSince1970), userFieldType: .fieldsBirthday, completionHandler: { (resultObject, error) -> Void in
            DispatchQueue.main.async {
                MBProgressHUD_JChat.hide(forView: self.view, animated: true)
                if error == nil {
                    MBProgressHUD_JChat.show(text: "修改成功", view: self.view)
                    self._updateUserInfo()
                } else {
                    MBProgressHUD_JChat.show(text: "修改失败", view: self.view)
                }
            }
        })
    }
}

extension JCMyInfoViewController: JCAreaPickerViewDelegate {
    func areaPickerView(_ areaPickerView: JCAreaPickerView, didSelect button: UIButton, selectLocate locate: JCLocation) {
        dismissPopupView()
        let region = locate.province + locate.city + locate.area
        
        JMSGUser.updateMyInfo(withParameter: region, userFieldType: .fieldsRegion, completionHandler: { (resultObject, error) -> Void in
            DispatchQueue.main.async {
                MBProgressHUD_JChat.hide(forView: self.view, animated: true)
                if error == nil {
                    MBProgressHUD_JChat.show(text: "修改成功", view: self.view)
                    self._updateUserInfo()
                } else {
                    MBProgressHUD_JChat.show(text: "修改失败", view: self.view)
                }
            }
        })
    }
    
    func areaPickerView(_ areaPickerView: JCAreaPickerView, cancleSelect button: UIButton) {
        dismissPopupView()
    }
}
