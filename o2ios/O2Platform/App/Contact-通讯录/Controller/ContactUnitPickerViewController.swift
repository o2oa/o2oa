//
//  ContactUnitPickerViewController.swift
//  O2Platform
//
//  Created by FancyLou on 2019/8/12.
//  Copyright © 2019 zoneland. All rights reserved.
//

import CocoaLumberjack
import UIKit

class ContactUnitPickerViewController: UITableViewController {
    
    // MARK: - 需要传入的参数
    var topUnitList: [String] = [] //顶级组织
    var unitType: String = "" //组织类型 查询组织用的

    // MARK: - 私有属性
    private var dataList:[OOUnitModel] = []
    private var breadcrumbList: [ContactBreadcrumbBean] = []
    private var unitParent: String = "-1"
    private var unitParentName: String = "通讯录"
    private let viewModel: ContactPickerViewModel = {
        return ContactPickerViewModel()
    }()
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.loadData()
    }
    
    
    
    // MARK: - Table view data source
    
    override func numberOfSections(in tableView: UITableView) -> Int {
        return 2
    }
    
    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if section == 0 {
            return 1
        }
        return self.dataList.count
    }
    
    override func tableView(_ tableView: UITableView, heightForHeaderInSection section: Int) -> CGFloat {
        return 10
    }
    
    override func tableView(_ tableView: UITableView, heightForFooterInSection section: Int) -> CGFloat {
        return 3
    }
    
    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        if indexPath.section == 0 {
            let cell = tableView.dequeueReusableCell(withIdentifier: "breadcrumbViewCell", for: indexPath) as! UnitBreadcrumbViewCell
            cell.refreshBreadcrumb(breadcrumbList: self.breadcrumbList)
            cell.delegate = self
            return cell
        }else {
            let cell = tableView.dequeueReusableCell(withIdentifier: "unitPickerViewCell", for: indexPath) as! UnitPickerTableViewCell
            let unit = dataList[indexPath.row]
            cell.loadUnitInfo(info: unit, checked: self.isSelected(value: unit.distinguishedName!))
            cell.delegate = self
            return cell
        }
    }
    
    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        if indexPath.section == 1 {
            let value = dataList[indexPath.row].distinguishedName!
            if self.isSelected(value: value) {
                self.removeSelected(value: value)
            }else {
                self.addSelected(dept: dataList[indexPath.row])
            }
            tableView.reloadRows(at: [indexPath], with: .automatic)
        }
        self.tableView.deselectRow(at: indexPath, animated: false)
    }
//
//    // MARK: - UITextViewDelegate
//    func textView(_ textView: UITextView, shouldInteractWith URL: URL, in characterRange: NSRange) -> Bool {
//        DDLogDebug("url: "+URL.description)
//        if let scheme = URL.scheme {
//            switch scheme {
//            case "reloadto" :
//                let id = (URL.description as NSString).substring(from: 9)
//                for unit in self.breadcrumbList {
//                    if id == unit.key {
//                        self.clickBreadcrumb(bean: unit)
//                        break;
//                    }
//                }
//            default:
//                break
//            }
//        }
//        return true
//    }
    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destination.
        // Pass the selected object to the new view controller.
    }
    */

    
    // MARK: - private method
    
    
    
    //获取组织数据 必须先操作面包屑导航数据
    private func loadData() {
        self.showLoading()
        viewModel.loadUnitList(parent: unitParent, topList: topUnitList, unitType: unitType)
            .then { (list)  in
                DDLogDebug("loadUnitList 结果： \(list.count)")
                self.dataList = list
                var bean = ContactBreadcrumbBean()
                bean.key =  self.unitParent
                bean.name = self.unitParentName
                bean.level = self.breadcrumbList.count
                self.breadcrumbList.append(bean)
                self.tableView.reloadData()
                self.hideLoading()
            }.catch { (error) in
                DDLogError(error.localizedDescription)
                self.hideLoading()
        }
    }
    
    private func isSelected(value: String) -> Bool {
        if let vc = self.parent as? ContactPickerViewController {
            return vc.isSelectedValue(type: .unit, value: value)
        }
        return false
    }
    
    private func removeSelected(value: String) {
        if let vc = self.parent as? ContactPickerViewController {
            vc.removeSelectedValue(type: .unit, value: value)
        }
    }
    
    private func addSelected(dept: OOUnitModel) {
        if let vc = self.parent as? ContactPickerViewController {
            vc.addSelectedDept(dept: dept)
        }
    }
    
    //点击面包屑导航上的组织按钮
//    private func clickBreadcrumb(bean: ContactBreadcrumbBean) {
//        //清空后面的导航按钮
//        for (index,unit) in self.breadcrumbList.enumerated() {
//            if unit.key == bean.key {
//                let n = self.breadcrumbList.count - index
//                self.breadcrumbList.removeLast(n)
//                break
//            }
//        }
//        self.unitParentName = bean.name
//        self.unitParent = bean.key
//        self.loadData()
//    }
}

// MARK: - extension delegate

extension ContactUnitPickerViewController : UnitPickerNextBtnDelegate {
    //进入下级组织
    func next(unitName: String?, unitDistinguishedName: String?) {
        DDLogDebug("name: \(String(describing: unitName)) dis:\(String(describing: unitDistinguishedName))")
        if unitName == nil || unitDistinguishedName == nil {
            DDLogError("参数为空。。。。。")
        }else {
            self.unitParentName = unitName!
            self.unitParent = unitDistinguishedName!
            self.loadData()
        }
    }
}

extension ContactUnitPickerViewController: UnitPickerBreadcrumbClickDelegate {
    //点击面包屑导航上的组织按钮
    func breadcrumbTap(name: String, distinguished: String) {
        //清空后面的导航按钮
        for (index,unit) in self.breadcrumbList.enumerated() {
            if unit.key == distinguished {
                let n = self.breadcrumbList.count - index
                self.breadcrumbList.removeLast(n)
                break
            }
        }
        self.unitParentName = name
        self.unitParent = distinguished
        self.loadData()
    }
    
    
}
