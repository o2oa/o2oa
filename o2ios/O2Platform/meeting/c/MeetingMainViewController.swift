//
//  MeetingMainViewController.swift
//  O2Platform
//
//  Created by 刘振兴 on 16/8/19.
//  Copyright © 2016年 zoneland. All rights reserved.
//

import UIKit
import CVCalendar
import Alamofire
import AlamofireObjectMapper
import SwiftyUserDefaults
import CocoaLumberjack


class MeetingMainViewController: UIViewController {
    
    struct Color {
        static let selectedText = UIColor.white
        static let text = UIColor.black
        static let textDisabled = UIColor.gray
        static let selectionBackground = UIColor(red: 0.2, green: 0.2, blue: 1.0, alpha: 1.0)
        static let sundayText = UIColor(red: 1.0, green: 0.2, blue: 0.2, alpha: 1.0)
        static let sundayTextDisabled = UIColor(red: 1.0, green: 0.6, blue: 0.6, alpha: 1.0)
        static let sundaySelectionBackground = sundayText
    }
    
    @IBOutlet weak var calendarMenuView: CVCalendarMenuView!
    
    @IBOutlet weak var calendarView: CVCalendarView!
    
    @IBOutlet weak var meetingTableView: ZLBaseTableView!
    
    var button:UIButton?
    
    var window:UIWindow?
    
    var headerTitle:String = ""
    
    
        /// 指定日期的会议列表
    var meetingsForDay:[Meeting] = []
    
        /// 指定月份的会议列表
    var meetingsForMonth:[Foundation.Date] = []
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.perform(#selector(MeetingMainViewController.createButton), with: nil, afterDelay: 1)
        self.calendarMenuView.delegate = self
        self.meetingTableView.delegate = self
        self.meetingTableView.dataSource = self
        
        self.loadData()
    }
    
    func loadData(){
        let cvDate = CVDate(date: Foundation.Date(),calendar: Calendar.current)
        self.loadMonthMeeting(cvDate.year, month: cvDate.month)
        self.loadTheDayMeeting(cvDate.year,month: cvDate.month,day: cvDate.day)
    }
    
    override func viewWillAppear(_ animated: Bool) {
        self.window?.isHidden = false
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        self.window?.isHidden = true
    }
    
    override var preferredStatusBarStyle : UIStatusBarStyle {
        return .lightContent
    }
    
    
    
    func createButton(){
        let width = SCREEN_WIDTH
        let height = SCREEN_HEIGHT
        self.button  = UIButton(frame: CGRect(x: 0,y: 0,width: 40,height: 40))
        self.button?.setImage(UIImage(named: "icon_add"), for: UIControlState())
        self.button?.addTarget(self, action: #selector(createAction), for: .touchUpInside)
        self.window = UIWindow(frame: CGRect(x: width - 60, y: height - 60, width: 40, height: 40))
        self.window?.windowLevel = UIWindowLevelAlert + 1
        self.window?.backgroundColor = UIColor.red
        self.window?.layer.cornerRadius = 20
        self.window?.layer.masksToBounds = true
        self.window?.addSubview(self.button!)
        self.window?.makeKeyAndVisible()
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    override func viewDidLayoutSubviews() {
        super.viewDidLayoutSubviews()
        calendarView.commitCalendarViewUpdate()
        calendarMenuView.commitMenuViewUpdate()
    }
    
    func createAction(){
        self.performSegue(withIdentifier: "showCreateMeetingSegue", sender: nil)
    }
    
    @IBAction func unCreateMeetingBackMain(_ sender:UIStoryboardSegue){
        DDLogDebug("create back")
        self.loadData()
        
    }
    
    @IBAction func openAcceptMeetingList(_ sender: UIBarButtonItem) {
        self.performSegue(withIdentifier: "showWaitAcceptMeetingSegue", sender: nil)
        
    }
    
    
    
    func loadMonthMeeting(_ year:Int,month:Int){
        self.title = "\(year)年\(month)月"
        self.meetingsForMonth.removeAll(keepingCapacity: true)
        let year = StringPrefix(year)
        let month = StringPrefix(month)
        let url = AppDelegate.o2Collect.generateURLWithAppContextKey(MeetingContext.meetingContextKey, query: MeetingContext.meetingListYearMonthQuery, parameter: ["##year##":year as AnyObject,"##month##":month as AnyObject])
        DDLogDebug("monthURL = \(String(describing: url))")
        Alamofire.request(url!,method:.get, parameters: nil, encoding: JSONEncoding.default, headers: nil).responseArray(keyPath: "data") { (response:DataResponse<[Meeting]>) in
            switch response.result {
            case .success(let meetings):
                self.meetingsForMonth.removeAll(keepingCapacity: true)
                meetings.forEachEnumerated({(index,meeting) in
                    let meetingDate = SharedDateUtil.dateFromString(string: meeting.startTime!, withFormat: SharedDateUtil.kNSDateHelperFormatSQLDateWithTime)
                    self.meetingsForMonth.append(meetingDate as Date)
                    
                })
                DispatchQueue.main.async {
                    self.calendarView.contentController.refreshPresentedMonth()
                }
                
               // self.calendarView.commitCalendarViewUpdate()
            case .failure(let err):
                DDLogError(err.localizedDescription)
            }
        }
    }
    
    func loadTheDayMeeting(_ year:Int,month:Int,day:Int){
        let year = StringPrefix(year)
        let month = StringPrefix(month)
        let day = StringPrefix(day)
        self.meetingTableView.emptyTitle = "\(month)月\(day)日没有需要参加的会议"
        self.headerTitle = "\(year)年\(month)月\(day)日 会议列表"
        let url = AppDelegate.o2Collect.generateURLWithAppContextKey(MeetingContext.meetingContextKey, query: MeetingContext.meetingListYearMonthDayQuery, parameter: ["##year##":year as AnyObject,"##month##":month as AnyObject,"##day##":day as AnyObject])
        Alamofire.request(url!,method:.get, parameters: nil, encoding:JSONEncoding.default, headers: nil).responseArray(keyPath:"data") { (response:DataResponse<[Meeting]>) in
            switch response.result {
            case .success(let meetings):
                self.meetingsForDay.removeAll()
                self.meetingsForDay.append(contentsOf: meetings)
                self.meetingTableView.reloadData()
                ProgressHUD.showSuccess("加载完成")
            case .failure( let err):
                DDLogError(err.localizedDescription)
                ProgressHUD.showError("加载失败")
            }
            
        }
    }
    
    func StringPrefix(_ theNumber:Int) -> String{
        if theNumber >= 10 {
            return String(theNumber)
        }else{
            return "0"+String(theNumber)
        }
    }
    
    
    @IBAction func backToSuper(_ sender: UIBarButtonItem) {
        let backType = Defaults[.appBackType]
        if backType == 1 {
            self.performSegue(withIdentifier: "backToMain", sender: nil)
        }else if backType == 2 {
            self.performSegue(withIdentifier: "backToApps", sender: nil)
        }
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == "showUpdateMeetingSegue" {
            let destVC = segue.destination as! MeetingUpdateViewController
            destVC.meeting = sender as? Meeting
        }
    }
    
    

}

// MARK: - 表格数据源及操作代理实现 UITableViewDelegate,UITableViewDataSource
extension MeetingMainViewController:UITableViewDelegate,UITableViewDataSource{
    func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return self.meetingsForDay.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "MeetingListItemCell", for: indexPath) as! MeetingListItemCell
        let meeting = self.meetingsForDay[(indexPath as NSIndexPath).row]
        cell.meeting  = meeting
        return cell
    }
    
    func tableView(_ tableView: UITableView, titleForHeaderInSection section: Int) -> String? {
        return self.headerTitle
    }
    
    func tableView(_ tableView: UITableView, viewForHeaderInSection section: Int) -> UIView? {
        let headerView = UIView(frame: CGRect(x: 0,y: 0,width: SCREEN_WIDTH,height: 20))
        let titleLabel = UILabel(frame: headerView.frame)
        titleLabel.text = self.headerTitle
        titleLabel.textColor = UIColor.white
        titleLabel.font = UIFont.boldSystemFont(ofSize: 14)
        titleLabel.backgroundColor = RGB(271, g: 71, b: 71)
        headerView.addSubview(titleLabel)
        return headerView
    }
    
    func tableView(_ tableView: UITableView, heightForHeaderInSection section: Int) -> CGFloat {
        return 20.0
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let meeting = self.meetingsForDay[(indexPath as NSIndexPath).row]
        self.performSegue(withIdentifier: "showUpdateMeetingSegue", sender: meeting)
        
    }
}

extension MeetingMainViewController:CVCalendarViewDelegate {
    func presentationMode() -> CalendarMode {
        return .monthView
    }
    
    func didShowNextMonthView(_ date: Foundation.Date) {
        DDLogDebug("didShowNextMonthView\(date.description)")
        let cal = Calendar.current
        let theDate = (cal as NSCalendar).date(byAdding: .month, value: 0, to: date, options: [])
        DDLogDebug("didShowNextMonthView theDate\(theDate!.description)")
        //let cvDate = CVDate(date:theDate!)
        let cvDate = CVDate(date: theDate!, calendar: Calendar.current)
        self.loadMonthMeeting(cvDate.year,month: cvDate.month)
        
    }
    
    func didShowPreviousMonthView(_ date: Foundation.Date) {
        DDLogDebug("didShowPreviousMonthView\(date.description)")
        let cal = Calendar.current
        let theDate = (cal as NSCalendar).date(byAdding: .month, value: 0, to: date, options: [])
        DDLogDebug("didShowPreviousMonthView theDate\(theDate!.description)")
        let cvDate = CVDate(date:theDate!,calendar: Calendar.current)
        self.loadMonthMeeting(cvDate.year,month: cvDate.month)
    }
    

    
    func shouldShowWeekdaysOut() -> Bool {
        return true
    }
    
    func shouldAnimateResizing() -> Bool {
        return true // Default value is true
    }
    
    func shouldSelectDayView(_ dayView: DayView) -> Bool {
        //return arc4random_uniform(3) == 0 ? true : false
        return true
    }
    


    
    func didSelectDayView(_ dayView: CVCalendarDayView, animationDidFinish: Bool) {
        print("\(dayView.date.commonDescription) is selected!")
        self.loadTheDayMeeting(dayView.date.year,month: dayView.date.month,day: dayView.date.day)
    }
    
    func topMarker(shouldDisplayOnDayView dayView: CVCalendarDayView) -> Bool {
        return true
    }
    
    func dotMarker(shouldShowOnDayView dayView: CVCalendarDayView) -> Bool {
        let dayDate = dayView.date
        let year = dayDate?.year
        let month  = dayDate?.month
        let day = dayDate?.day
        return self.meetingsForMonth.contains { (tDate:Foundation.Date) -> Bool in
            let cvDate = CVDate(date: tDate,calendar: Calendar.current)
            if year == cvDate.year && month == cvDate.month && day == cvDate.day {
                return true
            }else {
                return false
            }
        }
    }
    
    func dotMarker(colorOnDayView dayView: CVCalendarDayView) -> [UIColor] {
        
        let color = RGB(251, g: 71, b: 71)
        return [color]
//        
//        let red = CGFloat(arc4random_uniform(600) / 255)
//        let green = CGFloat(arc4random_uniform(600) / 255)
//        let blue = CGFloat(arc4random_uniform(600) / 255)
//        
//        let color = UIColor(red: red, green: green, blue: blue, alpha: 1)
//        
//        let numberOfDots = Int(arc4random_uniform(3) + 1)
//        switch(numberOfDots) {
//        case 2:
//            return [color, color]
//        case 3:
//            return [color, color, color]
//        default:
//            return [color] // return 1 dot
//        }
    }
    
    func dotMarker(shouldMoveOnHighlightingOnDayView dayView: CVCalendarDayView) -> Bool {
        return true
    }
    
    func dotMarker(sizeOnDayView dayView: DayView) -> CGFloat {
        return 13
    }

    
    func selectionViewPath() -> ((CGRect) -> (UIBezierPath)) {
        return { UIBezierPath(rect: CGRect(x: 0, y: 0, width: $0.width, height: $0.height)) }
    }
    
    func shouldShowCustomSingleSelection() -> Bool {
        return false
    }
    
    func preliminaryView(viewOnDayView dayView: DayView) -> UIView {
        let circleView = CVAuxiliaryView(dayView: dayView, rect: dayView.bounds, shape: CVShape.circle)
        circleView.fillColor = .colorFromCode(0xCCCCCC)
        return circleView
    }
    
    func preliminaryView(shouldDisplayOnDayView dayView: DayView) -> Bool {
        if (dayView.isCurrentDay) {
            return true
        }
        return false
    }
    
    func supplementaryView(viewOnDayView dayView: DayView) -> UIView {
        let π = Double.pi
        
        let ringSpacing: CGFloat = 3.0
        let ringInsetWidth: CGFloat = 1.0
        let ringVerticalOffset: CGFloat = 1.0
        var ringLayer: CAShapeLayer!
        let ringLineWidth: CGFloat = 4.0
        let ringLineColour: UIColor = UIColor.blue
        
        let newView = UIView(frame: dayView.bounds)
        
        let diameter: CGFloat = (newView.bounds.width) - ringSpacing
        let radius: CGFloat = diameter / 2.0
        
        let rect = CGRect(x: newView.frame.midX-radius, y: newView.frame.midY-radius-ringVerticalOffset, width: diameter, height: diameter)
        
        ringLayer = CAShapeLayer()
        newView.layer.addSublayer(ringLayer)
        
        ringLayer.fillColor = nil
        ringLayer.lineWidth = ringLineWidth
        ringLayer.strokeColor = ringLineColour.cgColor
        
        let ringLineWidthInset: CGFloat = CGFloat(ringLineWidth/2.0) + ringInsetWidth
        let ringRect: CGRect = rect.insetBy(dx: ringLineWidthInset, dy: ringLineWidthInset)
        let centrePoint: CGPoint = CGPoint(x: ringRect.midX, y: ringRect.midY)
        let startAngle: CGFloat = CGFloat(-π/2.0)
        let endAngle: CGFloat = CGFloat(π * 2.0) + startAngle
        let ringPath: UIBezierPath = UIBezierPath(arcCenter: centrePoint, radius: ringRect.width/2.0, startAngle: startAngle, endAngle: endAngle, clockwise: true)
        
        ringLayer.path = ringPath.cgPath
        ringLayer.frame = newView.layer.bounds
        
        return newView
    }
    
    func supplementaryView(shouldDisplayOnDayView dayView: DayView) -> Bool {
        if (Int(arc4random_uniform(3)) == 1) {
            return false
        }
        
        return false
    }
    
    
    func dayOfWeekTextColor(by weekday: Weekday) -> UIColor {
        return weekday == .sunday ? UIColor(red: 1.0, green: 0.5, blue: 0.5, alpha: 1.0) : UIColor.white
    }
    
    func dayOfWeekBackGroundColor() -> UIColor {
        return UIColor.orange
    }
    
    
}

extension MeetingMainViewController:CVCalendarMenuViewDelegate{
    func dayOfWeekTextColor() -> UIColor {
        return RGB(12, g: 12, b: 12)
    }
    

    func weekdaySymbolType() -> WeekdaySymbolType {
        return .veryShort
    }
    
    func dayOfWeekFont() -> UIFont {
        return UIFont.systemFont(ofSize: 14)
    }
    
    func dayOfWeekTextUppercase() -> Bool {
        return true
    }
    
    func firstWeekday() -> Weekday {
        return .sunday
    }
 
}

extension MeetingMainViewController: CVCalendarViewAppearanceDelegate {
    func dayLabelPresentWeekdayInitallyBold() -> Bool {
        return false
    }
    
    func spaceBetweenDayViews() -> CGFloat {
        return 2
    }
    
    func dayLabelWeekdayFont() -> UIFont {
        return UIFont.systemFont(ofSize: 14)
    }
    

}

