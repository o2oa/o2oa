//
//  ContactPickerViewController.swift
//  O2Platform
//
//  Created by FancyLou on 2019/8/12.
//  Copyright © 2019 zoneland. All rights reserved.
//

import UIKit
import CocoaLumberjack

enum ContactPickerType {
    case unit
    case identity
    case group
    case person
}


typealias DidPickedContact = (_ result: O2BizContactPickerResult) -> Void ///< 定义确认回调

class ContactPickerViewController: UIViewController {
    
    
    static func providePickerVC(
        pickerModes:[ContactPickerType],
        topUnitList: [String] = [],
        unitType: String = "",
        maxNumber: Int = 0,
        multiple: Bool = true,
        dutyList:[String] = [],
        initDeptPickedArray:[String] = [],
        initIdPickedArray:[String] = [],
        initGroupPickedArray:[String] = [],
        initUserPickedArray:[String] = [],
        pickedDelegate: @escaping DidPickedContact
    ) -> ContactPickerViewController? {
        
        if pickerModes.count < 1 {
            DDLogError("没有选择器类型")
            return nil
        }
        let storyBoard = UIStoryboard(name: "Contacts_new", bundle: nil)
        let destVC = storyBoard.instantiateViewController(withIdentifier: "contactPicker") as? ContactPickerViewController
        destVC?.selectorList = pickerModes
        if topUnitList.count > 0 {
            destVC?.topUnitList = topUnitList
        }
        if !unitType.isEmpty {
            destVC?.unitType = unitType
        }
        if maxNumber > 0 {
            destVC?.maxNumber = maxNumber
        }
        destVC?.multiple = multiple
        if dutyList.count > 0 {
            destVC?.dutyList = dutyList
        }
        if initDeptPickedArray.count > 0 {
            destVC?.initDeptPickedArray = initDeptPickedArray
        }
        if initIdPickedArray.count > 0 {
            destVC?.initIdPickedArray = initIdPickedArray
        }
        if initGroupPickedArray.count > 0 {
            destVC?.initGroupPickedArray = initGroupPickedArray
        }
        if initUserPickedArray.count > 0 {
            destVC?.initUserPickedArray = initUserPickedArray
        }
        destVC?.pickedDelegate = pickedDelegate
        
        return destVC
    }


    @IBOutlet weak var topBarStackView: UIStackView!
    @IBOutlet weak var pickerContainerView: UIView!
    @IBOutlet weak var topBarStackViewHeightConstraint: NSLayoutConstraint!
    
    
    
    //各个初始化参数
    var selectorList:[ContactPickerType] = [] //选择器 多值
    var topUnitList: [String] = [] //顶级组织
    var unitType: String = "" //组织类型 查询组织用的
    var maxNumber = 0 //可选择的最大数量
    var multiple = true //是否多选
    var dutyList:[String] = [] //身份查询的时候的限制的职务列表
    var initDeptPickedArray:[String] = [] //初始 已选择的数据
    var initIdPickedArray:[String] = [] //初始 已选择的数据
    var initGroupPickedArray:[String] = [] //初始 已选择的数据
    var initUserPickedArray:[String] = [] //初始 已选择的数据
    var pickedDelegate: DidPickedContact?
    
    //已经选中的值
    private var selectedDeptSet:[O2UnitPickerItem] = []
    private var selectedIdSet:[O2IdentityPickerItem] = []
    private var selectedGroupSet:[O2GroupPickerItem] = []
    private var selectedUserSet:[O2PersonPickerItem] = []
    //选择按钮文字
    private var pickBtnTitle = ""
    
    private let viewModel: ContactPickerViewModel = {
        return ContactPickerViewModel()
    }()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        if initDeptPickedArray.count > 0 {
            initDeptPickedArray.forEach { (s) in
                var name = ""
                if s.contains("@") {
                    name  = s.split("@")[0]
                }else {
                    name = s
                }
                let unit = O2UnitPickerItem()
                unit.distinguishedName = s
                unit.name = name
                selectedDeptSet.append(unit)
            }
        }
        if initIdPickedArray.count > 0 {
            initIdPickedArray.forEach { (s) in
                var name = ""
                if s.contains("@") {
                    name  = s.split("@")[0]
                }else {
                    name = s
                }
                let id = O2IdentityPickerItem()
                id.distinguishedName = s
                id.name = name
                selectedIdSet.append(id)
            }
        }
        if initGroupPickedArray.count > 0 {
            initGroupPickedArray.forEach { (s) in
                var name = ""
                if s.contains("@") {
                    name  = s.split("@")[0]
                }else {
                    name = s
                }
                let group = O2GroupPickerItem()
                group.distinguishedName = s
                group.name = name
                selectedGroupSet.append(group)
            }
        }
        if initUserPickedArray.count > 0 {
            initUserPickedArray.forEach { (s) in
                var name = ""
                if s.contains("@") {
                    name  = s.split("@")[0]
                }else {
                    name = s
                }
                let person = O2PersonPickerItem()
                person.distinguishedName = s
                person.name = name
                selectedUserSet.append(person)
            }
        }
        let c = selectedDeptSet.count + selectedIdSet.count + selectedGroupSet.count + selectedUserSet.count
        pickBtnTitle = "选择(\(c))"
        if maxNumber > 0 {
            pickBtnTitle = "选择(\(c)/\(maxNumber))"
        }
        navigationItem.rightBarButtonItem = UIBarButtonItem(title: pickBtnTitle, style: .plain, target: self, action: #selector(selected))
        if selectorList.count == 1 {
            self.topBarStackView.isHidden = true
            self.topBarStackViewHeightConstraint.constant = 0.0
            showPicker(tag:  selectorList[0].hashValue)
        }else {
            self.topBarStackView.isHidden = false
            self.topBarStackViewHeightConstraint.constant = 48.0
            topBarStackView.axis = .horizontal
            topBarStackView.alignment = .fill
            topBarStackView.spacing = 5
            topBarStackView.distribution = .fillEqually
            topBarStackView.removeSubviews()
            selectorList.forEach { (s) in
                switch(s) {
                case .unit:
                    let unitBtn = generatePickerTypeBtn(title: "组织选择", type: .unit)
                    topBarStackView.addArrangedSubview(unitBtn)
                case .identity:
                    let identityBtn = generatePickerTypeBtn(title: "身份选择", type: .identity)
                    topBarStackView.addArrangedSubview(identityBtn)
                case .group:
                    let groupBtn = generatePickerTypeBtn(title: "群组选择", type: .group)
                    topBarStackView.addArrangedSubview(groupBtn)
                case .person:
                    let personBtn = generatePickerTypeBtn(title: "人员选择", type: .person)
                    topBarStackView.addArrangedSubview(personBtn)
                }
            }
            if topBarStackView.subviews.count > 0 {
                if let button = (topBarStackView.subviews[0] as? UIButton){
                    button.isSelected = true
                    showPicker(tag:  button.tag)
                }
            }
        }
    }
    
    
    // MARK: - public method 提供给外部是一哦那个
    
    // 检查值是否已经包含在选中的列表中
    func isSelectedValue(type: ContactPickerType, value: String) -> Bool {
        switch type {
            case .unit:
               var f = false
               self.selectedDeptSet.forEach { (item) in
                    if item.distinguishedName == value {
                        f = true
                    }
                }
                return f
            case .identity:
                var f = false
                self.selectedIdSet.forEach { (item) in
                    if item.distinguishedName == value {
                        f = true
                    }
                }
                return f
            case .group:
                var f = false
                self.selectedGroupSet.forEach { (item) in
                    if item.distinguishedName == value {
                        f = true
                    }
                }
                return f
            case .person:
                var f = false
                self.selectedUserSet.forEach { (item) in
                    if item.distinguishedName == value {
                        f = true
                    }
                }
                return f
        }
        
    }
    // 删除一个选中的值
    func removeSelectedValue(type: ContactPickerType, value: String) {
        switch type {
            case .unit:
                self.selectedDeptSet.removeAll { (item) -> Bool in
                    return item.distinguishedName == value
                }
                break
            case .identity:
                self.selectedIdSet.removeAll { (item) -> Bool in
                    return item.distinguishedName == value
                }
                break
            case .group:
                self.selectedGroupSet.removeAll { (item) -> Bool in
                    return item.distinguishedName == value
                }
                break
            case .person:
                self.selectedUserSet.removeAll { (item) -> Bool in
                    return item.distinguishedName == value
                }
                break
        }
        self.refreshPickButton()
    }
    // 选择一个组织
    func addSelectedDept(dept: OOUnitModel) {
        let c = selectedDeptSet.count + selectedIdSet.count + selectedGroupSet.count + selectedUserSet.count
        if maxNumber > 0 && c >= maxNumber {
            self.showError(title: "不能添加更多了！")
            return
        }
        let item = O2UnitPickerItem()
        item.copyFromUnitModel(dept: dept)
        self.selectedDeptSet.append(item)
        self.refreshPickButton()
    }
    // 选择一个身份
    func addSelectedIdentity(id: OOIdentityModel) {
        let c = selectedDeptSet.count + selectedIdSet.count + selectedGroupSet.count + selectedUserSet.count
        if maxNumber > 0 && c >= maxNumber {
            self.showError(title: "不能添加更多了！")
            return
        }
        let item = O2IdentityPickerItem()
        item.copyFromIdentityModel(identity: id)
        self.selectedIdSet.append(item)
        //异步获取用户信息 然后填充进去
        self.getPersonInfoFor(forType: "0", dn: id.person!)//这里的person是人员的id
        self.refreshPickButton()
    }
    // 选择一个群组
    func addSelectedGroup(group: OOGroupModel) {
        let c = selectedDeptSet.count + selectedIdSet.count + selectedGroupSet.count + selectedUserSet.count
        if maxNumber > 0 && c >= maxNumber {
            self.showError(title: "不能添加更多了！")
            return
        }
        let item = O2GroupPickerItem()
        item.copyFromGroupModel(group: group)
        self.selectedGroupSet.append(item)
        self.refreshPickButton()
    }
    func addSelectedPerson(id: OOIdentityModel) {
        let c = selectedDeptSet.count + selectedIdSet.count + selectedGroupSet.count + selectedUserSet.count
        if maxNumber > 0 && c >= maxNumber {
            self.showError(title: "不能添加更多了！")
            return
        }
        let item = O2PersonPickerItem()
        item.distinguishedName = id.person
        self.selectedUserSet.append(item)
        //异步获取用户信息 然后填充进去
        self.getPersonInfoFor(forType: "1", dn: id.person!)//这里的person是人员的dn
        self.refreshPickButton()
    }
    
    // MARK: - private method 当前类私有方法

    @objc private func selected() {
        let c = selectedDeptSet.count + selectedIdSet.count + selectedGroupSet.count + selectedUserSet.count
        DDLogDebug("选中了：\(c) 个数据")
        if c < 1 {
            self.showError(title: "请至少选择一条数据！")
            return
        }else {
            let result = O2BizContactPickerResult(departments: selectedDeptSet,
                                     identities: selectedIdSet,
                                     groups: selectedGroupSet,
                                     users: selectedUserSet)
            self.pickedDelegate?(result)
            self.popVC()
        }
    }

    //刷新选择按钮文字内容
    private func refreshPickButton() {
        let c = selectedDeptSet.count + selectedIdSet.count + selectedGroupSet.count + selectedUserSet.count
        pickBtnTitle = "选择(\(c))"
        if maxNumber > 0 {
            pickBtnTitle = "选择(\(c) / \(maxNumber))"
        }
        navigationItem.rightBarButtonItem?.title = pickBtnTitle
    }
    
    //生成选择器Tab按钮
    private func generatePickerTypeBtn(title: String, type: ContactPickerType) -> UIButton {
        let button = UIButton(type: .system)
        button.setTitle(title, for: .normal)
        button.tag = type.hashValue
        button.setTitleColor(toolbar_text_color, for: .normal)
        button.theme_setTitleColor(ThemeColorPicker(keyPath: "Base.base_color"), forState: .selected)
        button.tintColor = UIColor.clear
        button.addTarget(self, action: #selector(clickBtn(btn:)), for: .touchUpInside)
        return button
    }
    
    //点击选择器Tab按钮
    @objc private func clickBtn(btn: UIButton) {
        topBarStackView.subviews.forEach { (v) in
            if let b = v as? UIButton {
                b.isSelected = false
                if b.tag == btn.tag {
                    b.isSelected = true
                    showPicker(tag: b.tag)
                }
            }
        }
    }
    
    //显示对应的选择器内容页面
    private func showPicker(tag: Int) {
        self.pickerContainerView.removeSubviews()
        switch(tag) {
        case ContactPickerType.unit.hashValue:
            self.title = "组织选择"
            if let pickerViewController = self.storyboard?.instantiateViewController(withIdentifier: "unitPicker") as? ContactUnitPickerViewController {
                pickerViewController.topUnitList = self.topUnitList
                pickerViewController.unitType = self.unitType
                if self.children.contains(pickerViewController) {
                    self.pickerContainerView.addSubview(pickerViewController.view)
                }else {
                    pickerViewController.view.frame = CGRect(x: CGFloat.zero, y: CGFloat.zero, width: self.pickerContainerView.frame.width, height: self.pickerContainerView.frame.height)
                    self.addChild(pickerViewController)
                    self.pickerContainerView.addSubview(pickerViewController.view)
                }
            }
            break
        case ContactPickerType.identity.hashValue:
            self.title = "身份选择"
            if let pickerViewController = self.storyboard?.instantiateViewController(withIdentifier: "identityPicker") as? ContactIdentityPickerViewController {
                pickerViewController.dutyList = self.dutyList
                pickerViewController.topUnitList = self.topUnitList
                pickerViewController.backResultIsUser = false
                if self.children.contains(pickerViewController) {
                    self.pickerContainerView.addSubview(pickerViewController.view)
                }else {
                    pickerViewController.view.frame = CGRect(x: CGFloat.zero, y: CGFloat.zero, width: self.pickerContainerView.frame.width, height: self.pickerContainerView.frame.height)
                    self.addChild(pickerViewController)
                    self.pickerContainerView.addSubview(pickerViewController.view)
                }
            }
            break
        case ContactPickerType.group.hashValue:
            self.title = "群组选择"
            if let pickerViewController = self.storyboard?.instantiateViewController(withIdentifier: "groupPicker") as? ContactGroupPickerViewController {
                if self.children.contains(pickerViewController) {
                    self.pickerContainerView.addSubview(pickerViewController.view)
                }else {
                    pickerViewController.view.frame = CGRect(x: CGFloat.zero, y: CGFloat.zero, width: self.pickerContainerView.frame.width, height: self.pickerContainerView.frame.height)
                    self.addChild(pickerViewController)
                    self.pickerContainerView.addSubview(pickerViewController.view)
                }
            }
            break
        case ContactPickerType.person.hashValue:
            self.title = "人员选择"
            
            if let pickerViewController = self.storyboard?.instantiateViewController(withIdentifier: "identityPicker") as? ContactIdentityPickerViewController {
                pickerViewController.dutyList = self.dutyList
                pickerViewController.topUnitList = self.topUnitList
                pickerViewController.backResultIsUser = true
                if self.children.contains(pickerViewController) {
                    self.pickerContainerView.addSubview(pickerViewController.view)
                }else {
                    pickerViewController.view.frame = CGRect(x: CGFloat.zero, y: CGFloat.zero, width: self.pickerContainerView.frame.width, height: self.pickerContainerView.frame.height)
                    self.addChild(pickerViewController)
                    self.pickerContainerView.addSubview(pickerViewController.view)
                }
            }
//
//
//            if let pickerViewController = self.storyboard?.instantiateViewController(withIdentifier: "personPicker") as? ContactPersonPickerViewController {
//                if self.children.contains(pickerViewController) {
//                    self.pickerContainerView.addSubview(pickerViewController.view)
//                }else {
//                    pickerViewController.view.frame = CGRect(x: 0, y: 0, w: self.pickerContainerView.frame.width, h: self.pickerContainerView.frame.height)
//                    self.addChild(pickerViewController)
//                    self.pickerContainerView.addSubview(pickerViewController.view)
//                }
//            }
            break
        default:
            DDLogDebug("click unkown")
        }
    }
    
    // forType 0:身份填充 1:用户填充
    private func getPersonInfoFor(forType: String, dn: String) {
        viewModel.getPersonInfo(dn: dn).then { (person) in
            if (forType == "0") {
                self.selectedIdSet.first(where: { $0.person == person.distinguishedName })?.updatePersonInfo(person: person)
            }else {
                self.selectedUserSet.first(where: { $0.distinguishedName == person.distinguishedName })?.copyFromPersonModel(person: person)
            }
        }
    }
    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destination.
        // Pass the selected object to the new view controller.
    }
    */

}
