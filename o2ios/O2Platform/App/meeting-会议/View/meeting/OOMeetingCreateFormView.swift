//
//  OOMeetingCreateFormView.swift
//  o2app
//
//  Created by 刘振兴 on 2018/1/25.
//  Copyright © 2018年 zone. All rights reserved.
//

import UIKit

protocol OOMeetingCreateFormViewDelegate {
    func performPersonSelected()
    func performRoomSelected()
}

class OOMeetingCreateFormView: UIView {
    
    private lazy var textItemView:OOFormTextItemView = {
        let view = Bundle.main.loadNibNamed("OOFormTextItemView", owner: self, options: nil)![0] as! OOFormTextItemView
        return view
    }()
    
    private lazy var dateItemView:OOFormDateItemView = {
        let view = Bundle.main.loadNibNamed("OOFormTextItemView", owner: self, options: nil)![1] as! OOFormDateItemView
        return view
    }()
    
    private lazy var dateIntervalItemView:OOFormDateIntervalItemView = {
        let view = Bundle.main.loadNibNamed("OOFormTextItemView", owner: self, options: nil)![2] as! OOFormDateIntervalItemView
        return view
    }()
    
    private lazy var segueItemView:OOFormSegueItemView = {
        let view = Bundle.main.loadNibNamed("OOFormTextItemView", owner: self, options: nil)![3] as! OOFormSegueItemView
        return view
    }()
    
    var delegate:OOMeetingCreateFormViewDelegate?
    
    //model Array
    private var _ooFormsModels:[OOFormBaseModel]?
    var ooFormsModels:[OOFormBaseModel]? {
        get {
            return _ooFormsModels
        }
        set(models){
            _ooFormsModels = models
            _ooFormsModels?.forEach({ (model) in
                switch model.componentType! {
                case .textItem:
                    let view = textItemView
                    view.configItem(model)
                    self.addSubview(view)
                    break
                case .dateItem:
                    let view = dateItemView
                    view.configItem(model)
                    self.addSubview(view)
                    break
                case .dateIntervalItem:
                    let view = dateIntervalItemView
                    view.configItem(model)
                    self.addSubview(view)
                    break
                case .segueItem:
                    let view = segueItemView
                    view.backCallAction = {
                        sender in
                        self.delegate?.performRoomSelected()
                    }
                    view.configItem(model)
                    self.addSubview(view)
                    break
                }
            })
        }
    }
    
    override init(frame: CGRect) {
        super.init(frame: frame)
    }
    
    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
    }
    
    override func awakeFromNib() {
        
    }
    
    //重新布局所有子控件
    override func layoutSubviews() {
        super.layoutSubviews()
        let x:CGFloat = 0
        var y:CGFloat  = 0
        let width:CGFloat = kScreenW
        let height:CGFloat = 44
        self.subviews.forEach { (view) in
            view.frame = CGRect(x: x, y: y, width: width, height: height)
            y+=height
        }
    }
    
    func setSelectedRoom(_ room:OOMeetingRoomInfo){
        segueItemView.setBackValueUpdate(room)
    }
    
    
    func getFormDataFormBean() -> (OOMeetingForm?, err: String?) {
        let meetingForm = OOMeetingForm()
        //标题
        guard let title = textItemView.model?.callbackValue  as? String else {
            return (nil, "请输入会议主题")
        }
        meetingForm.subject = title
        //日期
        guard let date = dateItemView.model?.callbackValue as? Date else {
            return (nil, "请选择日期")
        }
        meetingForm.meetingDate = date
        
        //开始时间
        let model = dateIntervalItemView.model as! OOFormDateIntervalModel
        guard let startTime = model.value1 as? Date else {
            return (nil, "请选择开始时间")
        }
        meetingForm.startTime = startTime
        //结束时间
        guard let endTime = model.value2 as? Date else {
            return (nil, "请选择结束时间")
        }
        meetingForm.completedTime = endTime
        //会议室
        
        guard let room = segueItemView.model?.callbackValue as? OOMeetingRoomInfo else {
            return (nil, "请选择会议室")
        }
        meetingForm.room = room.id
        meetingForm.roomName = room.name
        return (meetingForm, nil)
    }
    
     

}
