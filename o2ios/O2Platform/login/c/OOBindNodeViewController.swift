//
//  OOBindNodeViewController.swift
//  O2Platform
//
//  Created by 刘振兴 on 2018/4/8.
//  Copyright © 2018年 zoneland. All rights reserved.
//

import UIKit
import Promises
import O2OA_Auth_SDK

class OOBindNodeViewController:OOBaseViewController,UITableViewDataSource,UITableViewDelegate {
    
    private var viewModel:OOLoginViewModel = {
       return OOLoginViewModel()
    }()
    
    private let cellIdentitifer = "OONodeUnitTableViewCell"
    
    private let headerFrame = CGRect(x: 0, y: 0, width: kScreenW, height: 164)
    
    private let footerFrame = CGRect(x: 0, y: 0, width: kScreenW, height: 100)
    
    lazy var headerView:UIImageView = {
        return UIImageView(image: O2ThemeManager.image(for: "Icon.pic_xzzz_bj"))
    }()
    
    lazy var footerView:UIView = {
        
        let containerView = UIView(frame: self.footerFrame)
        let buttonFrame = CGRect(x: 25, y: (self.footerFrame.height - 44) / 2, width: self.footerFrame.width - 25 * 2, height: 44)
        let nextButton = OOBaseUIButton(frame: buttonFrame)
        nextButton.theme_backgroundColor = ThemeColorPicker(keyPath: "Base.base_color")
        nextButton.configUI()
        //下一步
        let attrits = NSAttributedString(string: "下一步", attributes: [NSAttributedString.Key.foregroundColor:UIColor.white,NSAttributedString.Key.font:UIFont(name:"PingFangSC-Regular",size:17)!])
        nextButton.setAttributedTitle(attrits, for: .normal)
        nextButton.addTarget(self, action: #selector(nextButtonClick(_:)), for: .touchUpInside)
        containerView.addSubview(nextButton)
        return containerView
    }()
    
    public var nodes:[O2BindUnitModel] = []
    
    public var mobile:String!
    
    public var value:String!
    
    private var selectedNode:O2BindUnitModel?
    
    @IBOutlet weak var tableView: UITableView!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        let headerView1 = Bundle.main.loadNibNamed("OORegisterTableView", owner: self, options: nil)?.first as! OORegisterTableView
        headerView1.configTitle(title: "选择服务节点", actionTitle: nil)
        headerView1.frame = CGRect(x: 0, y: 0, width: kScreenW, height: 66)
        headerView1.theme_backgroundColor = ThemeColorPicker(keyPath: "Base.base_color")
        if #available(iOS 11, *) {
            self.tableView.contentInsetAdjustmentBehavior = .never
            self.view.addSubview(headerView1)
        }else{
            self.view.addSubview(headerView1)
        }
        self.tableView.tableHeaderView = headerView
        self.tableView.tableFooterView = footerView
        self.tableView.dataSource = self
        self.tableView.delegate = self
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return nodes.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: cellIdentitifer, for: indexPath) as! OONodeUnitTableViewCell
        cell.config(withItem: nodes[indexPath.row])
        if cell.isSelected {
            cell.selectImageView.isHighlighted = true
        }else {
            cell.selectImageView.isHighlighted = false
        }
        return cell
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
       selectedNode = nodes[indexPath.row]
    }
    
    private func nextAction() {
        if let node = selectedNode {
            MBProgressHUD_JChat.showMessage(message: "绑定中...", toView: self.view)
            O2AuthSDK.shared.bindMobileToServer(unit: node, mobile: mobile, code: value) { (state, msg) in
                switch state {
                case .goToChooseBindServer(_):
                    //多于一个节点到节点列表
                    //self.performSegue(withIdentifier: "nextSelectNodeSegue", sender: unitList)
                    self.showError(title: "错误！")
                    break
                case .goToLogin:
//                    self.showError(title: "错误！\(msg ?? "")")
                    self.forwardDestVC("login", "loginVC")
                    break
                case .noUnitCanBindError:
                    self.showError(title: "没有获取到服务器列表，请确认服务器是否已经注册！")
                    break
                case .unknownError:
                    self.showError(title: "错误！\(msg ?? "")")
                    break
                case .success:
                    //处理移动端应用
                    self.viewModel._saveAppConfigToDb()
                    //成功，跳转
                    DispatchQueue.main.async {
                        if self.presentedViewController == nil {
                            self.dismissVC(completion:nil)
                        }
                        let destVC = O2MainController.genernateVC()
                        destVC.selectedIndex = 2
                        UIApplication.shared.keyWindow?.rootViewController = destVC
                        UIApplication.shared.keyWindow?.makeKeyAndVisible()
                    }
                    break
                }
                MBProgressHUD_JChat.hide(forView: self.view, animated: true)
            }
        }else{
            //请选择指定的目标服务
            self.showError(title: "请选择服务节点")
        }
    }
    
    @objc func nextButtonClick(_ sender:Any) {
        nextAction()
    }
    
    
}
