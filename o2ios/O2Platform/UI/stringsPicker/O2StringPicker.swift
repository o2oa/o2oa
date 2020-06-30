//
//  O2StringPicker.swift
//  O2Platform
//
//  Created by FancyLou on 2020/6/30.
//  Copyright © 2020 zoneland. All rights reserved.
//

import UIKit

class O2StringPicker: UIView, NibLoadable {
    @IBOutlet weak var titleLabel: UILabel!
    @IBOutlet weak var pickerView: UIPickerView!
    @IBOutlet weak var mainViewBottomLayout: NSLayoutConstraint!
    @IBOutlet weak var mainView: UIView!
    
    @IBAction func cancelBtnAction(_ sender: UIButton) {
        self.dismiss()
    }
    
    @IBAction func okBtnAction(_ sender: UIButton) {
        completionHandler(self.selectItemValue ?? "")
        dismiss()
    }
    
    private var completionHandler: (_ result: String) -> Void = {_ in }
    
    private var itemArray: [String] = []
    private var valueArray: [String] = []
    
    private var selectItemValue: String? = nil
    
    class func instance(items: [String],completionHandler: @escaping (_ result: String) -> Void,  values:[String]? = nil, pickerTitle:String? = nil, currentValue: String? = nil) -> O2StringPicker{
        
        let view: O2StringPicker = O2StringPicker.loadViewFromNib()
        
        view.completionHandler = completionHandler
        
        view.itemArray = items
        if let v = values, v.count > 0 {
            view.valueArray = v
        }
        
        view.selectItemValue = currentValue
        
        view.titleLabel.text = pickerTitle ?? ""
        
        view.setupUI()
        
        return view
    }
    
    private func setupUI() {

        self.mainViewBottomLayout.constant = 320
        
        self.backgroundColor  = UIColor.hexRGB(0x000000, 0.0)
        
        let tap = UITapGestureRecognizer(target: self, action: #selector(dismiss))
        tap.delegate = self
        addGestureRecognizer(tap)
        
        pickerView.delegate = self
        pickerView.dataSource = self
    }
    
    func show() {
        UIApplication.shared.keyWindow!.addSubview(self)
        
        self.frame = UIScreen.main.bounds
        
        UIView.animate(withDuration: 0.3, animations: {
            
            self.mainViewBottomLayout.constant = 0
            
            self.backgroundColor = UIColor.hexRGB(0x000000, 0.5)
            
            self.layoutIfNeeded()
        }, completion: { (finish) in
            //选中
            var row = 0
            if let item = self.selectItemValue {
                if self.valueArray.count > 0 {
                    self.valueArray.forEachEnumerated { (index, value) in
                        if item == value {
                            row = index
                        }
                    }
                }else {
                    self.itemArray.forEachEnumerated { (index, value) in
                        if item == value {
                            row = index
                        }
                    }
                }
            }
            self.pickerView.selectRow(row, inComponent: 0, animated: true)
        })
    }
    
    @objc func dismiss() {
        UIView.animate(withDuration: 0.3, animations: {
            
            self.mainViewBottomLayout.constant = 320
            
            self.backgroundColor = UIColor.hexRGB(0x000000, 0.0)
            
            self.layoutIfNeeded()
            
        }, completion: { (finished) in
            
            self.removeFromSuperview()
            
        })
    }
    
    
}

extension O2StringPicker: UIPickerViewDelegate, UIPickerViewDataSource {
    func numberOfComponents(in pickerView: UIPickerView) -> Int {
        //仅支持一组 多组联动未实现
        return 1
    }
    
    func pickerView(_ pickerView: UIPickerView, numberOfRowsInComponent component: Int) -> Int {
        return self.itemArray.count
    }
    
    func pickerView(_ pickerView: UIPickerView, rowHeightForComponent component: Int) -> CGFloat {
        return 40
    }
    
    func pickerView(_ pickerView: UIPickerView, viewForRow row: Int, forComponent component: Int, reusing view: UIView?) -> UIView {
        let title = self.itemArray[row]
        if let label = view as? UILabel {
            label.text = title
            return label
        }
        
        let label = UILabel()
        label.textAlignment = .center
        label.font = UIFont.systemFont(ofSize: 17)
        label.text = title
        label.sizeToFit()
        return label
    }
    
    func pickerView(_ pickerView: UIPickerView, didSelectRow row: Int, inComponent component: Int) {
        self.selectItemValue = self.valueArray[row]
    }
    
}

extension O2StringPicker: UIGestureRecognizerDelegate {
    
    func gestureRecognizer(_ gestureRecognizer: UIGestureRecognizer, shouldReceive touch: UITouch) -> Bool {
        guard let touchView = touch.view else { return false }
        
        if touchView.isDescendant(of: mainView) {
            // 点击的view是否是mainView或者mainView的子视图
            return false
        }
        
        return true
    }
}
