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
    
    
    func getFormDataFormBean() -> OOMeetingForm {
        let meetingForm = OOMeetingForm()
        //标题
        meetingForm.subject = textItemView.model?.callbackValue  as? String
        //日期
        meetingForm.meetingDate = dateItemView.model?.callbackValue as! Date
        //开始时间
        let model = dateIntervalItemView.model as! OOFormDateIntervalModel
        meetingForm.startTime = model.value1 as! Date
        //结束时间
        meetingForm.completedTime = model.value2 as! Date
        //会议室
        let room = segueItemView.model?.callbackValue as? OOMeetingRoomInfo
        meetingForm.room = room?.id
        meetingForm.roomName = room?.name
        return meetingForm
    }

}
