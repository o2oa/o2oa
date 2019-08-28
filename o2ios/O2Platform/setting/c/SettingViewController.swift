//
//  SettingViewController.swift
//  O2Platform
//
//  Created by 刘振兴 on 16/7/6.
//  Copyright © 2016年 zoneland. All rights reserved.
//

import UIKit
import Alamofire
import AlamofireImage
import AlamofireObjectMapper
import SwiftyJSON

import SDWebImage
import CocoaLumberjack
import O2OA_Auth_SDK
import Flutter

class SettingViewController: UIViewController,UITableViewDelegate,UITableViewDataSource {
    
    @IBOutlet weak var settingHeaderView: SettingHeaderView!
    
    @IBOutlet weak var iconImageView: UIImageView!
    
    @IBOutlet weak var nameLabel: UILabel!
    
    @IBOutlet weak var settingItemTableView: UITableView!
    
    @IBOutlet weak var SettingHeaderViewTopConstraint: NSLayoutConstraint!
    
    
    var itemModels:[Int:[SettingHomeCellModel]] {
        let item1 = SettingHomeCellModel(iconName: "setting_accout", title: "账号与安全", status: nil,segueIdentifier:"showInfoAndSecuritySegue")
        let itemSkin = SettingHomeCellModel(iconName: "icon_skin", title: "个性换肤", status: nil,segueIdentifier:"showSkinViewSegue")
        let item2 = SettingHomeCellModel(iconName: "setting_newMessage", title: "新消息通知", status: nil,segueIdentifier:"showMessageNotiSegue")
        let item3 = SettingHomeCellModel(iconName: "setting_common", title: "通用", status: nil,segueIdentifier:"showCommonSegue")
//        let item4 = SettingHomeCellModel(iconName: "setting_myCRM", title: "我的客服", status: nil,segueIdentifier:"showServiceSegue")
        let item5 = SettingHomeCellModel(iconName: "setting_ideaback", title: "意见反馈", status: nil,segueIdentifier:"showIdeaBackSegue")
        let item6 = SettingHomeCellModel(iconName: "setting_about", title: "关于", status: nil,segueIdentifier:"showAboutSegue")
        return [0:[item1],1:[itemSkin, item2,item3,item5],2:[item6]]
    }
    
    override func viewWillAppear(_ animated: Bool) {
        self.navigationController?.navigationBar.isHidden = true
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        self.navigationController?.navigationBar.isHidden = false
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        //更新头像之后刷新
        NotificationCenter.default.addObserver(self, selector: #selector(loadAvatar), name: Notification.Name("reloadMyIcon"), object: nil)
        
        
        if #available(iOS 11.0, *) {
            let topConstant = CGFloat(0 - IOS11_TOP_STATUSBAR_HEIGHT)
            self.SettingHeaderViewTopConstraint.constant = topConstant
        }
        self.settingHeaderView.theme_backgroundColor = ThemeColorPicker(keyPath: "Base.base_color")
        
        self.iconImageView.layer.masksToBounds = true
        self.iconImageView.layer.cornerRadius =  75 / 2.0
        self.iconImageView.layer.borderColor = UIColor.white.cgColor
        self.iconImageView.layer.borderWidth = 1
        
        self.settingItemTableView.delegate = self
        self.settingItemTableView.dataSource = self
        
        self.loadAvatar()

    }
    
    override var preferredStatusBarStyle : UIStatusBarStyle {
        return .lightContent
    }
    
    func numberOfSections(in tableView: UITableView) -> Int {
        return itemModels.keys.count
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return (itemModels[section]?.count)!
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "SettingHomeCellIdentifier", for: indexPath) as! SettingHomeCell
        cell.cellModel = itemModels[(indexPath as NSIndexPath).section]![(indexPath as NSIndexPath).row]
        return cell
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let cellModel = self.itemModels[indexPath.section]?[indexPath.row]
        if let segue = cellModel?.segueIdentifier {
            if segue == "showIdeaBackSegue" {
                PgyManager.shared().showFeedbackView()
//                self.testShowPicker()
            }else{
                self.performSegue(withIdentifier: segue, sender: nil)
            }
        }
    
        
    }
    
    @IBAction func showPersonDetail(_ sender: UITapGestureRecognizer) {
        self.performSegue(withIdentifier: "showPersonSegue", sender: nil)
    }
    

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        
    }
    
    
    @objc private func loadAvatar() {
        DDLogInfo("刷新头像和名字。。。。。。。。。。。。。。。。。。。。。")
        let me = O2AuthSDK.shared.myInfo()
        self.nameLabel.text = me?.name
        let avatarUrlString = AppDelegate.o2Collect.generateURLWithAppContextKey(ContactContext.contactsContextKeyV2, query: ContactContext.personIconByNameQueryV2, parameter: ["##name##":me?.id as AnyObject])
        let avatarUrl = URL(string: avatarUrlString!)
        self.iconImageView.hnk_setImageFromURL(avatarUrl!)
    }
    
    

}
