//
//  JCAreaPickerView.swift
//  JChat
//
//  Created by deng on 2017/5/3.
//  Copyright © 2017年 dengyonghao. All rights reserved.
//

import UIKit

let stateKey = "state"
let citiesKey = "cities"
let cityKey = "city"
let areasKey = "areas"

enum JCAreaPickerType: Int {
    case province
    case city
    case area
}

@objc public protocol JCAreaPickerViewDelegate: NSObjectProtocol {
    @objc optional func areaPickerView(_ areaPickerView: JCAreaPickerView, didSelect button: UIButton, selectLocate locate: JCLocation)
    @objc optional func areaPickerView(_ areaPickerView: JCAreaPickerView, cancleSelect button: UIButton)
}

@objc(JCAreaPickerView)
public class JCAreaPickerView: UIView {
    
    weak var delegate: JCAreaPickerViewDelegate?

    fileprivate var cities = [[String: AnyObject]]()
    fileprivate var areas = [String]()
    fileprivate var pickerView: UIPickerView!
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        
        let toolView = UIView(frame: CGRect(x: 0, y: 0, width: frame.size.width, height: 40))
        toolView.backgroundColor = .white
        let line = UILabel(frame: CGRect(x: 0, y: toolView.frame.size.height - 0.5 , width: toolView.frame.size.width, height: toolView.frame.size.height - 0.5))
        line.backgroundColor = UIColor(netHex: 0x999999)
        toolView.addSubview(line)
        
        let cancelButton = UIButton(frame: CGRect(x: 15, y: 0, width: 50, height: 39.5))
        cancelButton.setTitle("取消", for: .normal)
        cancelButton.setTitleColor(.black, for: .normal)
        cancelButton.addTarget(self, action: #selector(_areaPickerCancel), for: .touchUpInside)
        
        let sureButton = UIButton(frame: CGRect(x: toolView.frame.size.width - 50 - 15, y: 0, width: 50, height: 39.5))
        sureButton.setTitle("确定", for: .normal)
        sureButton.setTitleColor(.black, for: .normal)
        sureButton.addTarget(self, action: #selector(_areaPickerSure), for: .touchUpInside)
        
        toolView.addSubview(cancelButton)
        toolView.addSubview(sureButton)
        
        addSubview(toolView)
        
        pickerView = UIPickerView(frame: CGRect(x: 0, y: 40, width: frame.size.width, height: frame.size.height - 40))
        pickerView.backgroundColor = .white
        pickerView.delegate = self
        pickerView.dataSource = self
        addSubview(pickerView)
        
        cities = provinces[0][citiesKey] as! [[String : AnyObject]]!
        if let province = provinces[0][stateKey] as? String {
            locate.province = province
        }
        
        if let city = cities[0][cityKey] as? String {
            locate.city = city
        }
        
        areas = cities[0][areasKey] as! [String]!
        if areas.count > 0 {
            locate.area = areas[0]
        } else {
            locate.area = ""
        }
    }
    
    required public init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    func _areaPickerCancel(_ sender: UIButton) {
        delegate?.areaPickerView?(self, cancleSelect: sender)
    }
    
    func _areaPickerSure(_ sender: UIButton) {
        delegate?.areaPickerView?(self, didSelect: sender, selectLocate: locate)
    }

    
    func shouldSelected(proName: String, cityName: String, areaName: String?) {
        
        for index in 0..<provinces.count {
            let pro = provinces[index]
            if pro[stateKey] as! String == proName {
                cities = provinces[index][citiesKey] as! [[String : AnyObject]]!
                if let province = provinces[index][stateKey] as? String {
                    locate.province = province
                }
                pickerView.selectRow(index, inComponent: JCAreaPickerType.province.rawValue, animated: false)
                break
            }
        }
        
        for index in 0..<cities.count {
            let city = cities[index]
            if city[cityKey] as! String == cityName {
                if let city = cities[index][cityKey] as? String {
                    locate.city = city
                }
                
                areas = cities[index][areasKey] as! [String]!
                pickerView.selectRow(index, inComponent: JCAreaPickerType.city.rawValue, animated: false)
                break
            }
        }
        
        if areaName != nil {
            for (index, name) in areas.enumerated() {
                if name == areaName! {
                    locate.area = areas[index]
                    pickerView.reloadAllComponents()
                    pickerView.selectRow(index, inComponent: JCAreaPickerType.area.rawValue, animated: false)
                    break
                }
            }
        }
    }
    
    
    func setCode(provinceName: String, cityName: String, areaName: String?){
        
        let url = Bundle.main.url(forResource: "addressCode", withExtension: nil)
        let data = try! Data(contentsOf: url!)
        let dict = try! JSONSerialization.jsonObject(with: data, options: JSONSerialization.ReadingOptions.mutableContainers) as! [String: AnyObject]
        let provinces = dict["p"] as! [[String: AnyObject]]
        
        for pro in provinces {
            if pro["n"] as! String == provinceName {
                if let proCode = pro["v"] as? String {
                    locate.provinceCode = proCode //找到省编号
                }
                
                var foundCity = false
                for city in pro["c"] as! [[String: AnyObject]] {
                    if city["n"] as! String == cityName {
                        if let cityCode = city["v"] as? String {
                            locate.cityCode = cityCode  //找到城市编码
                        }
                        for area in city["d"] as! [[String: String]] {
                            if area["n"] == areaName {
                                locate.areaCode = area["v"]!
                            }
                        }
                        foundCity = true
                    }
                }
                
                //如果第二层没有找到相应的城市.那就是直辖市了,要重新找
                if !foundCity {
                    for city in pro["c"] as! [[String: AnyObject]] {
                        let areas = city["d"] as! [[String: String]] //直接查找三级区域
                        for area in areas {
                            if area["n"] == cityName {
                                locate.areaCode = area["v"]!
                                if let cityCode = city["v"] as? String {
                                    locate.cityCode = cityCode
                                }
                                break
                            }
                        }
                    }
                }
                
            }
        }
    }
    
    // MARK: - lazy
    lazy var provinces: [[String: AnyObject]] = {
        let path = Bundle.main.path(forResource: "area", ofType: "plist")
        return NSArray(contentsOfFile: path!) as! [[String: AnyObject]]
    }()
    
    lazy var locate: JCLocation = {
        return JCLocation()
    }()
    
    
}

extension JCAreaPickerView: UIPickerViewDelegate, UIPickerViewDataSource {
    
    public func numberOfComponents(in pickerView: UIPickerView) -> Int {
        return 3
    }
    
    public func pickerView(_ pickerView: UIPickerView, numberOfRowsInComponent component: Int) -> Int {
        let pickerType = JCAreaPickerType(rawValue: component)!
        switch pickerType {
        case .province:
            return provinces.count
        case .city:
            return cities.count
        case .area:
            return areas.count
        }
    }
    
    public func pickerView(_ pickerView: UIPickerView, titleForRow row: Int, forComponent component: Int) -> String? {
        let pickerType = JCAreaPickerType(rawValue: component)!
        switch pickerType {
        case .province:
            return provinces[row][stateKey] as! String?
        case .city:
            return cities[row][cityKey] as! String?
        case .area:
            if areas.count > 0 {
                return areas[row]
            } else {
                return ""
            }
        }
    }
    
    public func pickerView(_ pickerView: UIPickerView, didSelectRow row: Int, inComponent component: Int) {
        let pickerType = JCAreaPickerType(rawValue: component)!
        switch pickerType {
        case .province:
            cities = provinces[row][citiesKey] as! [[String : AnyObject]]!
            pickerView.reloadComponent(JCAreaPickerType.city.rawValue)
            pickerView.selectRow(0, inComponent: JCAreaPickerType.city.rawValue, animated: true)
            reloadAreaComponent(pickerView: pickerView, row: 0)
            if let province = provinces[row][stateKey] as? String {
                locate.province = province
            }
        case .city:
            reloadAreaComponent(pickerView: pickerView, row: row)
        case .area:
            if areas.count > 0 {
                locate.area = areas[row]
            } else {
                locate.area = ""
            }
        }
        setCode(provinceName: locate.province, cityName: locate.city, areaName: locate.area)
    }
    
    func reloadAreaComponent(pickerView: UIPickerView, row: Int) {
        
        
        guard row <= cities.count - 1 else {
            return
        }
        
        areas = cities[row][areasKey] as! [String]!
        pickerView.reloadComponent(JCAreaPickerType.area.rawValue)
        pickerView.selectRow(0, inComponent: JCAreaPickerType.area.rawValue, animated: true)
        if let city = cities[row][cityKey] as? String {
            locate.city = city
        }
        if areas.count > 0 {
            locate.area = areas[0]
        } else {
            locate.area = ""
        }
    }
}

