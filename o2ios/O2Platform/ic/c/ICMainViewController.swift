//
//  ICMainViewController.swift
//  O2Platform
//
//  Created by 刘振兴 on 16/9/8.
//  Copyright © 2016年 zoneland. All rights reserved.
//

import UIKit
import Alamofire
import AlamofireImage
import AlamofireObjectMapper
import ObjectMapper
import SwiftyJSON

import Charts
import CocoaLumberjack
import O2OA_Auth_SDK

class ICMainViewController: UIViewController {
    
    
    var pieEntrys:[AttendanceStatusType:AttendanceTotalEntry] = [:]
    
    @IBOutlet weak var chartView: PieChartView!
    
    var button:UIButton?
    
    var window:UIWindow?
    
    
    override func viewWillAppear(_ animated: Bool) {
        self.window?.isHidden = false
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        self.window?.isHidden = true
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.createButton()
        self.initChartView()
        self.loadICTotalData()
    }
    
    func loadICTotalData(){
        let filter = AttendanceDetailWrapInFilter()
        let date = Date()
        let result = calcTimeComponetFromDate(date: date)
        filter.cycleYear = result.year
        filter.cycleMonth = result.month
        filter.order = "asc"
        filter.q_empName = O2AuthSDK.shared.myInfo()?.distinguishedName
        self.loadDataFromFilter(filter: filter)
    }
    
    func loadDataFromFilter(filter:AttendanceDetailWrapInFilter){
        let url = AppDelegate.o2Collect.generateURLWithAppContextKey(icContext.icContextKey, query: icContext.detailMonthPieChartQuery, parameter: nil)
        DDLogDebug(url!)
        DDLogDebug(filter.toJSONString()!)
        Alamofire.request(url!, method: .put, parameters: filter.toJSON(), encoding:JSONEncoding.default, headers: nil).responseJSON { (response) in
            switch response.result {
            case .success(let val):
                let type = JSON(val)["type"]
                if type == "success"{
                    let dataJSON = JSON(val)["data"]
                    self.setChartViewData(json: dataJSON)
                }else{
                    DDLogError(JSON(val).description)
                }
            case .failure(let err):
                DDLogError(err.localizedDescription)
            }
        }
        //self.setChartViewData(json: nil)
    }
    
    func setChartViewData(json:JSON?){
        //AttendanceDetailDataArray
        if let detailJSON = json {
            let detailArray = Mapper<AttendanceDetailData>().mapArray(JSONString: detailJSON.description)
            //生成Dictonary
            if detailArray != nil {
                for d in detailArray! {
                    let t = calcAttendanceStatus(attendance: d)
                    if self.pieEntrys.has(t.statusType) {
                        let entry:AttendanceTotalEntry = self.pieEntrys[t.statusType]!
                        entry.incCount()
                        self.pieEntrys[t.statusType] = entry
                    }else{
                        let entry = AttendanceTotalEntry(label: t.statusType.rawValue, type: t.statusType)
                        entry.incCount()
                        self.pieEntrys[t.statusType] = entry
                    }
                }
         
            
            var entrys:[PieChartDataEntry] = []
            for d in self.pieEntrys.enumerated() {
                let entry = d.element.value
                let cEntry = PieChartDataEntry(value: Double(entry.count), label: entry.label, data: entry)
                entrys.append(cEntry)
            }
            
            let pieDataSet = PieChartDataSet(values: entrys, label: "")
            //pieDataSet颜色
            var colors:[NSUIColor] = []
            //colors.append(contentsOf: ChartColorTemplates.vordiplom())
            //       colors.append(contentsOf: ChartColorTemplates.joyful())
            colors.append(contentsOf: ChartColorTemplates.colorful())
            //        colors.append(contentsOf: ChartColorTemplates.liberty())
            //        colors.append(contentsOf: ChartColorTemplates.pastel())
            //pieDataSet.entryLabelFont =  UIFont(name: "PingFangSC-Regular", size: 15.0)!
            //pieDataSet.entryLabelColor =  RGB(18, g: 18, b: 18)
            pieDataSet.colors = colors
            
            let pieData = PieChartData(dataSets: [pieDataSet])
            //pieData数据格式
            let pFormatter = NumberFormatter()
            pFormatter.numberStyle = NumberFormatter.Style.percent
            pFormatter.maximumFractionDigits = 1
            pFormatter.multiplier = 1.0
            pFormatter.percentSymbol = " %"
            pieData.setValueFormatter(DefaultValueFormatter.init(formatter: pFormatter))
            //
            pieData.setValueFont(UIFont(name: "PingFangSC-Regular", size: 12.0)!)
            pieData.setValueTextColor(UIColor.white)
            //设置数据
            self.chartView.data = pieData
            //通知数据表更新
            self.chartView.notifyDataSetChanged()
                
            }

            
        }
    }
    

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    func initChartView(){
        chartView.delegate = self
        chartView.usePercentValuesEnabled = true
        chartView.drawSlicesUnderHoleEnabled = true
        chartView.holeRadiusPercent = 0.58
        chartView.transparentCircleRadiusPercent = 0.61
        chartView.chartDescription?.enabled = false
        chartView.setExtraOffsets(left: 5.0, top: 10.0, right: 5.0, bottom: 5.0)
        
        //无数据文本样式
        chartView.noDataText = "您这个月还没有考勤记录"
        chartView.noDataFont = UIFont(name: "PingFangSC-Regular", size: 20.0)!
        chartView.noDataTextColor = RGB(108, g: 108, b: 108)
        //中心文本样式
        chartView.drawCenterTextEnabled = true
        let paragraphStyle:NSMutableParagraphStyle = NSParagraphStyle.default.mutableCopy() as! NSMutableParagraphStyle
        paragraphStyle.lineBreakMode = NSLineBreakMode.byTruncatingTail
        paragraphStyle.alignment = NSTextAlignment.center
        let centerText:NSMutableAttributedString = NSMutableAttributedString(string: "考勤月报")
        centerText.addAttribute(NSAttributedString.Key.font, value: UIFont(name: "PingFangSC-Regular", size: 15.0)!, range: NSMakeRange(0, centerText.length))
        centerText.addAttribute(NSAttributedString.Key.foregroundColor, value: RGB(18,g:18,b:18), range: NSMakeRange(0, centerText.length))
        chartView.centerAttributedText = centerText
        
        
        chartView.drawHoleEnabled = true
        chartView.rotationAngle = 0.0
        chartView.rotationEnabled = true
        chartView.highlightPerTapEnabled = true
        //图例样式
        let l:Legend = chartView.legend
        l.horizontalAlignment = Legend.HorizontalAlignment.left
        l.verticalAlignment = Legend.VerticalAlignment.bottom
        l.orientation = Legend.Orientation.horizontal
//        l.horizontalAlignment = 
//        l.verticalAlignment = ChartLegendVerticalAlignmentTop
//        l.orientation = ChartLegendOrientationVertical
        l.drawInside = false
        l.xEntrySpace = 7.0
        l.yEntrySpace = 0.0
        l.yOffset = 20
        l.font = UIFont(name: "PingFangSC-Regular", size: 12.0)!
        l.textColor = RGB(18, g: 18, b: 18)
        
        //entry文本样式

    }
    
    func createButton(){
        let width = SCREEN_WIDTH
        let height = SCREEN_HEIGHT
        let frame = CGRect(x: 0, y: 0, w: 60, h: 60)
        button = UIButton(frame: frame)
        button?.setImage(UIImage(named:"icon_fab_list"), for: .normal)
        button?.addTarget(self, action: #selector(self.showDetail(sender:)), for: .touchUpInside)
        window = UIWindow.init(frame: CGRect(x:width - 80 , y: height - 80, w: 60, h: 60))
        window?.windowLevel = UIWindow.Level.alert + 1
        window?.backgroundColor = RGB(46, g: 204, b: 113)
        window?.layer.cornerRadius = 30
        window?.layer.masksToBounds = true
        window?.addSubview(button!)
        window?.makeKeyAndVisible()
        
    }
    
    @objc func showDetail(sender:UIButton){
        self.performSegue(withIdentifier: "showICDetailSegue", sender: nil)
    }
    
    
    @IBAction func showCheckAppeal(_ sender: UIBarButtonItem) {
        self.performSegue(withIdentifier: "showCheckAppealSegue", sender: nil)
    }
    

    
    
    @IBAction func backToSuper(_ sender: UIBarButtonItem) {
        let backType = AppConfigSettings.shared.appBackType
        if backType == 1 {
            self.performSegue(withIdentifier: "backToMain", sender: nil)
        }else if backType == 2 {
            self.performSegue(withIdentifier: "backToApps", sender: nil)
        }
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        let date = Date()
        let result = calcTimeComponetFromDate(date: date)
        if segue.identifier == "showICDetailSegue" {
            let destVC = segue.destination as! ICDetailViewController
            destVC.currentTime = result
        }else if segue.identifier == "" {
            
        }
    }
    
}

extension ICMainViewController:ChartViewDelegate{
    
    func chartValueSelected(_ chartView: ChartViewBase, entry: ChartDataEntry, highlight: Highlight) {
        
    }
    
    func chartValueNothingSelected(_ chartView: ChartViewBase) {
        
    }
    
    func chartTranslated(_ chartView: ChartViewBase, dX: CGFloat, dY: CGFloat) {
        
    }
    
    func chartScaled(_ chartView: ChartViewBase, scaleX: CGFloat, scaleY: CGFloat) {
        
    }
    
}
