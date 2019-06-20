//
//  ContactPersonInfoV2ViewController.swift
//  O2Platform
//
//  Created by 程剑 on 2017/7/11.
//  Copyright © 2017年 zoneland. All rights reserved.
//

import UIKit
import Alamofire
import AlamofireImage
import AlamofireObjectMapper
import SwiftyJSON
import ObjectMapper

import Eureka
import CocoaLumberjack
import O2OA_Auth_SDK

class ContactPersonInfoV2ViewController: UITableViewController {
    @IBOutlet weak var beijingImg: UIImageView!
    @IBOutlet weak var personImg: UIImageView!
    @IBOutlet weak var personName: UILabel!
    @IBOutlet weak var personQQ: UILabel!
    @IBOutlet weak var personCollect: UIButton!
    
    @IBOutlet weak var personGirl: UIButton!
    @IBOutlet weak var personMan: UIButton!
    @IBOutlet weak var personCollectLab: UILabel!
    
    var isCollect = false
    @IBAction func collectPerson(_ sender: UIButton) {
        
        let me = O2AuthSDK.shared.myInfo()
        if personCollect.isSelected == true {
            //删除
            OOContactsInfoDB.shareInstance.deleteData(contact!, (me?.id)!)
        }else{
            //增加
            OOContactsInfoDB.shareInstance.insertData(contact!, (me?.id)!)
        }
        personCollect.isSelected = !personCollect.isSelected
        
    }
    
    let nameLabs = ["企业信息","姓名","员工号","唯一编码","联系电话","电子邮件","部门"]
    
    var myPersonURL:String?
    
    
    var identity:IdentityV2? {
        didSet {
            self.myPersonURL = AppDelegate.o2Collect.generateURLWithAppContextKey(ContactContext.contactsContextKeyV2, query: ContactContext.personInfoByNameQuery, parameter: ["##name##":identity!.person! as AnyObject])
        }
    }
    
    var person:PersonV2?{
        didSet {
            self.myPersonURL = AppDelegate.o2Collect.generateURLWithAppContextKey(ContactContext.contactsContextKeyV2, query: ContactContext.personInfoByNameQuery, parameter: ["##name##":person!.id! as AnyObject])
        }
    }
    
    var isLoadPerson:Bool = true
    
    var contact:PersonV2? {
        didSet {
            isLoadPerson = false
        }
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.beijingImg.theme_image = ThemeImagePicker(keyPath: "Icon.pic_beijing1")
        self.personImg.layer.cornerRadius = self.personImg.frame.size.width / 2
        self.personImg.clipsToBounds = true
        loadPersonInfo(nil)
        let startChatButton = OOBaseUIButton(x: (kScreenW - 260)/2, y: 5, w: 260, h: 30, target: self, action: #selector(_startChat))
        startChatButton.setTitle("发起聊天", for: .normal)
        let btnContainerView = UIView(x: 0, y: 0, w: kScreenW, h: 40)
        btnContainerView.addSubview(startChatButton)
        tableView.tableFooterView = btnContainerView
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        //去掉nav底部分割线
        self.navigationController?.navigationBar.setBackgroundImage(UIImage(), for: .default)
        self.navigationController?.navigationBar.shadowImage = UIImage()
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    @objc private func _startChat() {
        MBProgressHUD_JChat.showMessage(message: "创建中...", toView: view)
        var username = ""
        if self.person != nil {
            username = self.person?.id ?? ""
        }else if self.identity != nil {
            username = self.identity?.person ?? ""
        }
        if username == "" {
            return
        }
        JMSGConversation.createSingleConversation(withUsername: username) { (result, error) in
            MBProgressHUD_JChat.hide(forView: self.view, animated: true)
            if error == nil {
                let conv = result as! JMSGConversation
                let vc = JCChatViewController(conversation: conv)
                NotificationCenter.default.post(name: NSNotification.Name(rawValue: kUpdateConversation), object: nil, userInfo: nil)
                self.navigationController?.pushViewController(vc, animated: true)
            }else{
                O2Logger.error(error.debugDescription)
                MBProgressHUD_JChat.show(text: "创建会话失败，请重试", view: self.view)
            }
        }
    }

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        // #warning Incomplete implementation, return the number of rows
        return nameLabs.count
    }


    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "personInfoCell", for: indexPath) as! ContactPersonInfoCell

        cell.nameLab.text = self.nameLabs[indexPath.row]
        switch self.nameLabs[indexPath.row] {
        case "企业信息":
            cell.nameLab.font = UIFont.systemFont(ofSize: 17)
            cell.nameLab.textColor = UIColor.black
            cell.valueLab.isHidden = true
            cell.eventBut.isHidden = true
        case "姓名":
            cell.valueLab.text = self.contact?.name
            cell.eventBut.isHidden = true
        case "员工号":
            cell.valueLab.text = self.contact?.employee
            cell.eventBut.isHidden = true
        case "唯一编码":
            cell.valueLab.text = self.contact?.unique
            cell.eventBut.isHidden = true
        case "联系电话":
            cell.valueLab.text = self.contact?.mobile
            cell.eventBut.addTarget(self, action: #selector(self.call), for: UIControl.Event.touchUpInside)
        case "电子邮件":
            cell.valueLab.text = self.contact?.mail
            cell.eventBut.theme_setImage(ThemeImagePicker(keyPath:"Icon.icon_email"), forState: .normal)
            cell.eventBut.addTarget(self, action: #selector(self.sendMail), for: UIControl.Event.touchUpInside)
        case "部门":
            var unitName = ""
            if let idenList = self.contact?.woIdentityList {
                for iden in idenList {
                    if let unit = iden.woUnit {
                        if unitName != "" {
                            unitName.append(";")
                        }
                        unitName.append(unit.name ?? "")
                    }
                }
            }
            cell.valueLab.text = unitName
            cell.eventBut.isHidden = true
        default:
            break
        }
        

        return cell
    }
    
    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        switch self.nameLabs[indexPath.row] {
        case "联系电话":
            self.call()
        case "电子邮件":
            self.sendMail()
        default:
            break
        }
        self.tableView.deselectRow(at: indexPath, animated: true)
    }
    
    

    @objc func sendMail() {
        if let mail = self.contact?.mail, mail != "" {
            let alertController = UIAlertController(title: "", message: nil,preferredStyle: .actionSheet)
            let mailAction = UIAlertAction(title: "发邮件", style: .default, handler: { _ in
                let mailURL = URL(string: "mailto://\(mail)")
                
                if UIApplication.shared.canOpenURL(mailURL!) {
                    UIApplication.shared.openURL(mailURL!)
                }else{
                    self.showError(title: "发邮件失败")
                }
            })
            let copyAction = UIAlertAction(title: "复制", style: .default, handler: { _ in
                UIPasteboard.general.string = mail
                self.showSuccess(title: "复制成功")
            })
            
            let cancelAction = UIAlertAction(title: "取消", style: .cancel, handler: nil)
            alertController.addAction(mailAction)
            alertController.addAction(copyAction)
            alertController.addAction(cancelAction)
            self.present(alertController, animated: true, completion: nil)
        }
    }
    
    @objc func call(){
        if let phone = self.contact?.mobile, phone != "" {
            let alertController = UIAlertController(title: "", message: nil,preferredStyle: .actionSheet)
            let smsAction = UIAlertAction(title: "发短信", style: .default, handler: { _ in
                let smsURL = URL(string: "sms://\(phone)")
                if UIApplication.shared.canOpenURL(smsURL!) {
                    UIApplication.shared.openURL(smsURL!)
                }else{
                    self.showError(title: "发短信失败")
                }
            })
            let phoneAction = UIAlertAction(title: "打电话", style: .default, handler: { _ in
                let phoneURL = URL(string: "tel://\(phone)")
                if UIApplication.shared.canOpenURL(phoneURL!) {
                    UIApplication.shared.openURL(phoneURL!)
                }else{
                    self.showError(title: "打电话失败")
                }
            })
            let copyAction = UIAlertAction(title: "复制", style: .default, handler: { _ in
                UIPasteboard.general.string = phone
                self.showSuccess(title: "复制成功")
            })
            
            let cancelAction = UIAlertAction(title: "取消", style: .cancel, handler: nil)
            alertController.addAction(phoneAction)
            alertController.addAction(smsAction)
            alertController.addAction(copyAction)
            alertController.addAction(cancelAction)
            self.present(alertController, animated: true, completion: nil)
        }
    }
    
    func loadPersonInfo(_ sender: AnyObject?){
        self.showMessage(title: "加载中...")
        Alamofire.request(myPersonURL!).responseJSON {
            response in
            switch response.result {
            case .success( let val):
                let json = JSON(val)["data"]
                self.contact = Mapper<PersonV2>().map(JSONString:json.description)!
                //OOCon
                let me = O2AuthSDK.shared.myInfo()
                self.isCollect = OOContactsInfoDB.shareInstance.isCollect(self.contact!, (me?.id)!)
                if self.isCollect == true {
                    self.personCollect.isSelected = true
                }else{
                    self.personCollect.isSelected = false
                }
                self.personName.text = self.contact?.name
                if let qq = self.contact?.qq, qq != "" {
                    self.personQQ.text = "QQ \(qq)"
                }else{
                    self.personQQ.text = ""
                }
                if let gt = self.contact?.genderType, gt == "f" {
                    self.personGirl.setImage(UIImage(named: "icon_girl_2"), for: .normal)
                }else{
                    self.personMan.setImage(UIImage(named: "icon_boy_2"), for: .normal)
                }
                let urlstr = AppDelegate.o2Collect.generateURLWithAppContextKey(ContactContext.contactsContextKeyV2, query: ContactContext.personIconByNameQueryV2, parameter: ["##name##":self.contact?.unique as AnyObject], generateTime: false)
                let url = URL(string: urlstr!)
                self.personImg.hnk_setImageFromURL(url!)
                DispatchQueue.main.async {
                    self.dismissProgressHUD()
                    self.tableView.reloadData()
                }
            case .failure(let err):
                DDLogError(err.localizedDescription)
                DispatchQueue.main.async {
                    self.showError(title: "加载失败")
                }
            }
            
        }
    }
}
