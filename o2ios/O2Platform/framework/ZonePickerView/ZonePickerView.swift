//
//  ZonePickerView.swift
//  ZoneBarManager
//
//  Created by 刘振兴 on 2017/3/15.
//  Copyright © 2017年 zone. All rights reserved.
//

import UIKit

public func ZoneRect(_ x:Double,_ y:Double,_ w:Double,_ h:Double) -> CGRect{
    return CGRect(x: Double(UIScreen.main.bounds.width) * x, y: Double(UIScreen.main.bounds.height) * y, width: Double(UIScreen.main.bounds.width) * w, height: Double(UIScreen.main.bounds.height) * h)
}

public func ZoneColorAlpha(_ r:CGFloat,_ g:CGFloat,_ b:CGFloat,_ a:CGFloat) -> UIColor {
    return UIColor(red: r / 255.0, green: g/255.0, blue: b/255.0, alpha: a)
}



class ZonePickerView: UIView,UITableViewDataSource,UITableViewDelegate,ZoneFileListCellDelegate{
    
    var title:String = "附件列表"
    
    var pickViewHeight = 350
    
    var topView:UIView!
    
    var doneBtn:UIButton!
    
    var pickerView:UITableView!
    
    var result:String!
    
    var models:[ZonePickerModel] = []
    
    var selectModel:ZonePickerModel!
    
    override init(frame: CGRect) {
        super.init(frame: ZoneRect(0, 0, 1, 917/617))
       
    }
    
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    override func layoutSubviews() {
        super.layoutSubviews()
        self.backgroundColor = ZoneColorAlpha(0, 0, 0, 0.4)
        
        //
        self.topView = UIView(frame: ZoneRect(0, 667/667, 1, Double(pickViewHeight)/667))
        self.topView.backgroundColor = UIColor.white
        self.addSubview(topView)
        
        self.doneBtn = UIButton(type: .custom)
        self.doneBtn.setImage(#imageLiteral(resourceName: "icon_delete2_por"), for: .normal)
        //        self.doneBtn.setTitle("Close", for: .normal)
        //        self.doneBtn.setTitleColor(UIColor.gray, for: .normal)
        self.doneBtn.frame = ZoneRect(330/375, 5/667, 50/375, 40/667)
        self.doneBtn.addTarget(self, action: #selector(quit), for: .touchUpInside)
        self.topView.addSubview(doneBtn)
        
        let titleLB = UILabel(frame: ZoneRect(10/375, 0, 175/375, 50/667))
        titleLB.backgroundColor = UIColor.clear
        titleLB.textAlignment = .left
        //titleLB.text = title
        titleLB.attributedText = NSAttributedString(string: title, attributes: [NSAttributedString.Key.font:UIFont(name: "PingFangSC-Regular", size: 16.0)!,NSAttributedString.Key.foregroundColor:ZoneColorAlpha(102, 102, 102, 1)])
        //titleLB.font = UIFont(name: "PingFangSC-Regular", size: 16.0)!
        self.topView.addSubview(titleLB)
        
        self.pickerView = UITableView(frame: ZoneRect(0,50/667,1,300/667), style: .plain)
        self.pickerView.tableFooterView = UIView()
        
        self.pickerView.register(UINib(nibName: "ZoneFileListCell", bundle: Bundle.main), forCellReuseIdentifier: "ZonePickerCell")
        self.pickerView.dataSource = self
        self.pickerView.delegate  = self
        self.topView.addSubview(self.pickerView)
        
    }
    
    public func showPickerView(){
        self.showInView(UIApplication.shared.keyWindow!)
    }
    
    public func hidePickerView(){
        UIView.animate(withDuration: 0.5, animations: {
            self.alpha = 0
            var point = self.center
            point.y += CGFloat(self.pickViewHeight)
            self.center = point
        }) { (finished) in
            //NotificationCenter.default.post(name: NSNotification.Name("QUIT_ATTACH_OBJ"), object: nil)
            self.removeFromSuperview()
        }
    }
    
    private func showInView(_ view:UIView){
        UIView.animate(withDuration: 0.5, animations: {
            var point = self.center
            point.y -= CGFloat(self.pickViewHeight)
            self.center = point
        }) { (finished) in
            
        }
        view.addSubview(self)
    }
    
    @objc private func quit(){
        UIView.animate(withDuration: 0.5, animations: {
            self.alpha = 0
            var point = self.center
            point.y += CGFloat(self.pickViewHeight)
            self.center = point
        }) { (finished) in
            NotificationCenter.default.post(name: NSNotification.Name("QUIT_ATTACH_OBJ"), object: nil)
            self.removeFromSuperview()
        }
    }
    
    
    
    func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return models.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "ZonePickerCell", for: indexPath) as! ZoneFileListCell
        cell.delegate = self
        cell.pickerModel = self.models[indexPath.row]
        return cell
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        print("the \(indexPath.row) row")
    }
    
    func tableView(_ tableView: UITableView, viewForFooterInSection section: Int) -> UIView? {
        return UIView()
    }
    
    func cellClick(_ model: ZonePickerModel) {
        self.selectModel = model
        NotificationCenter.default.post(name: NSNotification.Name("SHOW_ATTACH_OBJ"), object: model)
    }

}
