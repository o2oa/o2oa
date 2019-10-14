//
//  AZPopMenu.swift
//
//  Created by Aaron Zhu on 15/6/4.
//  Copyright (c) 2015年 Aaron Zhu All rights reserved.

/******************************************
    *作用：
        创建一个pop菜单。
    *使用方法： 
        AZPopMenu.show
    *方法声明:
        class func show(superView:UIView, startPoint: CGPoint, items: [String], colors: [UIColor], selected: (itemSelected: Int) -> Void)
    *方法参数:
        superView:  父View，请使用ViewController.view,方便计算坐标
        startPoint: pop菜单上方的箭头位置，使用superView的坐标
        items:      要显示的菜单项
        colors:     菜单项前显示的色块
        selected:   选中菜单项后调用的闭包
******************************************/


import UIKit
import CoreGraphics

let AZ_SELL_WIDTH : CGFloat = 120        //Cell宽度
let AZ_SELL_HEIGHT: CGFloat = 40         //Cell高度
let AZ_ARROW_WIDTH: CGFloat = 10         //Table上方箭头的宽度
let AZ_ARROW_HEIGHT: CGFloat = 10        //Table上方箭头的高度
let AZ_ARROW_FROM_EDGE: CGFloat = 35     //Table上方箭头距离Table边界的最小距离
let AZ_TABLE_WIDTH = AZ_SELL_WIDTH    //Table的宽度（同Cell宽度）
let AZ_TABLE_FROM_EDGE: CGFloat = 10     //Table距离手机边界的最小距离

let AZ_SCREEN_WIDTH = UIScreen.main.bounds.size.width
let AZ_SCREEN_HEIGHT = UIScreen.main.bounds.size.height


class SelectCell : UITableViewCell{
    
    var colorView :UIView!
    var nameLabel :UILabel!
    
    func setUpItem(_ item: String, withColor: UIColor){
        //构造cell上的内容
        colorView = UIView(frame: CGRect(x: 18,y: 13,width: 14,height: 14))
        colorView.layer.cornerRadius = 2
        colorView.layer.masksToBounds = true
        self.addSubview(colorView)
        nameLabel = UILabel(frame: CGRect(x: 42, y: 15, width: 100, height: 10))
        nameLabel.backgroundColor = UIColor.clear
        nameLabel.textColor = UIColor(red:192/255, green: 193/255, blue: 195/255, alpha: 1.0)
        self.addSubview(nameLabel)
        self.backgroundColor = UIColor(red: 57/255, green: 60/255, blue: 66/255, alpha: 1.0)
        
        nameLabel.text = item
        colorView.backgroundColor = withColor
        
    }
}

class ArrowView: UIView{
    var arrowPoint: CGPoint
    init(frame: CGRect, arrowPoint: CGPoint) {
        //转换箭头的坐标 相对super坐标 -> 本view坐标
        self.arrowPoint = arrowPoint
        self.arrowPoint.y = 0
        self.arrowPoint.x = arrowPoint.x - frame.origin.x
        super.init(frame: frame)
    }

    required init(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    //画顶部的三角形
    override func draw(_ rect: CGRect) {
        super.draw(rect)
        let  context = UIGraphicsGetCurrentContext()
        context!.setFillColor(red: 57/255, green: 60/255, blue: 66/255, alpha: 1.0) //设置填充颜色
        let sPoints: [CGPoint] = [arrowPoint,
            CGPoint(x: arrowPoint.x-AZ_ARROW_WIDTH/2, y: arrowPoint.y+AZ_ARROW_HEIGHT),
            CGPoint(x: arrowPoint.x+AZ_ARROW_WIDTH/2, y: arrowPoint.y+AZ_ARROW_HEIGHT)]
        context?.addLines(between: sPoints)
        //CGContextAddLines(context!, sPoints, 3)
        context!.closePath()
        context!.drawPath(using: .fillStroke)
    }
    
}

class AZPopMenu: UIView,UITableViewDelegate,UITableViewDataSource,UIGestureRecognizerDelegate{

    var superView: UIView!  //父View，使用ViewController.view，方便计算坐标。
    var startPoint: CGPoint = CGPoint(x: 0,y: 0) //箭头位置
    var items: [String] = []                     //cell中使用字符串
    var colors: [UIColor] = []                   //cell中使用颜色框
    var selected: ((_ itemSelected: Int) -> Void)? //点击选项后调用的闭包
    
                                    //第一层，self, 全屏透明view，用于接收tabGuesture手势来关闭自己。
    var popViewArrow: ArrowView!    //第二层，小View，用来画三角箭头
    var popTable: UITableView!      //第三层，TableView，显示内容和接收点击事件
    var tabGuesture: UITapGestureRecognizer!
    
    class func show(_ superView:UIView, startPoint: CGPoint, items: [String], colors: [UIColor], selected: @escaping (_ itemSelected: Int) -> Void){
        let p = AZPopMenu()
        p.showPopMenu(superView, startPoint: startPoint, items: items, colors: colors, selected: selected)
    }
    
    init(){
        super.init(frame: CGRect(x: 0, y: 0, width: AZ_SCREEN_WIDTH, height: AZ_SCREEN_HEIGHT))
        self.backgroundColor = UIColor(white: 0.0, alpha: 0.2)
    }

    required init(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    func showPopMenu (_ superView:UIView, startPoint: CGPoint, items: [String], colors: [UIColor], selected: @escaping (_ itemSelected: Int) -> Void) {
        if items.isEmpty {
            print("error: items为空。函数结束。")
            return
        }
        if items.count != colors.count {
            print("error: items和colors项目数量不同。函数结束。")
            return
        }

        self.superView = superView
        self.startPoint = startPoint
        self.items = items
        self.colors = colors
        self.selected = selected
        
        //调整箭头位置， 距离屏幕两边35个点以上
        if self.startPoint.x < AZ_ARROW_FROM_EDGE {
            self.startPoint.x = AZ_ARROW_FROM_EDGE
        }else if self.startPoint.x > (AZ_SCREEN_WIDTH-AZ_ARROW_FROM_EDGE){
            self.startPoint.x = AZ_SCREEN_WIDTH-AZ_ARROW_FROM_EDGE
        }
        //调整popmenu的frame  默认箭头置中， table距离屏幕两边10个点以上。
        let width = AZ_TABLE_WIDTH
        let height = AZ_ARROW_HEIGHT + AZ_SELL_HEIGHT * CGFloat(items.count) - 1.0  //隐藏掉最后一个像素的白线
        var tableX = self.startPoint.x - AZ_SELL_WIDTH/2
        if tableX < AZ_TABLE_FROM_EDGE {
            tableX = AZ_TABLE_FROM_EDGE
        }else if tableX > (AZ_SCREEN_WIDTH-AZ_SELL_WIDTH-AZ_TABLE_FROM_EDGE){
            tableX = AZ_SCREEN_WIDTH-AZ_SELL_WIDTH-AZ_TABLE_FROM_EDGE
        }

        popViewArrow = ArrowView(frame: CGRect(x: tableX, y: startPoint.y, width: width, height: height), arrowPoint: self.startPoint)
        popViewArrow.backgroundColor = UIColor.clear
        //popViewArrow.backgroundColor = UIColor(red: 0.2, green: 0.2, blue: 0.2, alpha: 0.3)
        
        //放置table
        popTable = UITableView(frame: CGRect(x: 0, y: AZ_ARROW_HEIGHT, width: width, height: height-AZ_ARROW_HEIGHT), style: UITableView.Style.plain)
        popTable.delegate = self
        popTable.dataSource = self
        
        popTable.layer.cornerRadius = 5
        popTable.layer.masksToBounds = true
        popTable.isScrollEnabled = false //禁止滚动
        popTable.separatorStyle = UITableViewCell.SeparatorStyle.none //去掉cell分割线
        
        popViewArrow.addSubview(popTable)
        self.addSubview(popViewArrow)
        superView.addSubview(self)
        
        //UITapGestureRecognizer! 用于关闭popmenu
        tabGuesture = UITapGestureRecognizer(target: self, action: #selector(AZPopMenu.tabAction(_:)))
        tabGuesture.numberOfTapsRequired = 1
        tabGuesture.delegate = self
        self.addGestureRecognizer(tabGuesture)
        
        //动画打开效果
        animationShowPopMenu()
    
    }
    
    func animationShowPopMenu(){
        //改变popTable
        var done = false
        //popTable.alpha = 0.0
        let toRect = popTable.frame
        popTable.frame =  CGRect(x: startPoint.x-popViewArrow.frame.origin.x, y: 0, width: 1, height: 1)
        UIView.animate(withDuration: 0.2, animations: { () -> Void in
            self.popTable.frame = toRect
            //self.popTable.alpha = 1.0
            }, completion: { (b: Bool) -> Void in
            done = true
        }) 
        while !done{
            RunLoop.current.run(until: Date(timeIntervalSinceNow: 0.01))
        }
    }
    
    
    @objc func tabAction(_ sender: UITapGestureRecognizer){
        //关闭本身
        self.removeGestureRecognizer(tabGuesture)
        self.removeFromSuperview()
        self.selected?(-1)
    }
   

    //UITableViewDelegate/UITableViewDataSource
    func tableView(_ tableView: UITableView, heightForHeaderInSection section: Int) -> CGFloat {
        return 0.0
    }
    func tableView(_ tableView: UITableView, heightForFooterInSection section: Int) -> CGFloat {
        return 0.0
    }
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return AZ_SELL_HEIGHT
    }
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return items.count
    }
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) ->UITableViewCell{
        let cell = SelectCell(style: UITableViewCell.CellStyle.default, reuseIdentifier: "Cell")
        //cell.setUpView(indexPath.row)
        cell.setUpItem(items[(indexPath as NSIndexPath).row], withColor: colors[(indexPath as NSIndexPath).row])
        return cell;
    }
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath){
        tableView.deselectRow(at: indexPath, animated: true)
        //关闭本身
        self.removeGestureRecognizer(tabGuesture)
        self.removeFromSuperview()
        self.selected?((indexPath as NSIndexPath).row)
    }
    //UIGestureRecognizerDelegate 用于过滤tableview
    func gestureRecognizer(_ gestureRecognizer: UIGestureRecognizer, shouldReceive touch: UITouch) -> Bool{
        if touch.view is AZPopMenu {
            return true
        }else{
            return false
        }
    }


}



