//
//  JCRecordingView.swift
//  JChatSwift
//
//  Created by oshumini on 16/2/19.
//  Copyright © 2016年 HXHG. All rights reserved.
//

import UIKit

internal let voiceRecordResaueString = "松开手指，取消发送"
internal let voiceRecordPauseString = "手指上滑，取消发送"

class JCRecordingView: UIView {
    
    var remiadeLable: UILabel!
    var cancelRecordImageView: UIImageView!
    var recordingHUDImageView: UIImageView!
    var errorTipsView: UIImageView!
    var timeLable: UILabel!
    var tipsLable: UILabel!
    var peakPower:Float!
    
    var isRecording = false
    
    func dismissCompled(_ completed: (_ finish:Bool) -> Void) {
        
    }
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        _init()
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    func _init() {
        backgroundColor = UIColor.black.withAlphaComponent(0.5)
        layer.masksToBounds = true
        layer.cornerRadius = 5
        
        remiadeLable = UILabel()
        remiadeLable.textColor = UIColor.white
        remiadeLable.layer.cornerRadius = 2.5
        remiadeLable.layer.masksToBounds = true
        remiadeLable.font = UIFont.systemFont(ofSize: 12)
        remiadeLable.text = voiceRecordPauseString
        remiadeLable.textAlignment = .center
        addSubview(remiadeLable)
        
        timeLable = UILabel()
        timeLable.textColor = UIColor(netHex: 0x2DD0CF)
        timeLable.font = UIFont.systemFont(ofSize: 12)
        timeLable.text = "······ 0.00 ······"
        timeLable.textAlignment = .center
        addSubview(timeLable)
        
        tipsLable = UILabel()
        tipsLable.textColor = UIColor(netHex: 0x2DD0CF)
        tipsLable.font = UIFont.systemFont(ofSize: 62)
        tipsLable.textAlignment = .center
        tipsLable.isHidden = true
        addSubview(tipsLable)
        
        recordingHUDImageView = UIImageView()
        recordingHUDImageView.image = UIImage.loadImage("com_icon_record")
        addSubview(recordingHUDImageView)
        
        errorTipsView = UIImageView()
        errorTipsView.image = UIImage.loadImage("com_icon_record_error")
        errorTipsView.isHidden = true
        addSubview(errorTipsView)
        
        cancelRecordImageView = UIImageView()
        cancelRecordImageView.image = UIImage.loadImage("com_icon_record_cancel")
        cancelRecordImageView.contentMode = .scaleToFill
        addSubview(cancelRecordImageView)
        
        addConstraint(_JCLayoutConstraintMake(recordingHUDImageView, .centerX, .equal, self, .centerX))
        addConstraint(_JCLayoutConstraintMake(recordingHUDImageView, .top, .equal, self, .top, 21.5))
        addConstraint(_JCLayoutConstraintMake(recordingHUDImageView, .width, .equal, nil, .notAnAttribute, 43))
        addConstraint(_JCLayoutConstraintMake(recordingHUDImageView, .height, .equal, nil, .notAnAttribute, 60))
        
        addConstraint(_JCLayoutConstraintMake(errorTipsView, .centerX, .equal, self, .centerX))
        addConstraint(_JCLayoutConstraintMake(errorTipsView, .top, .equal, self, .top, 27.5))
        addConstraint(_JCLayoutConstraintMake(errorTipsView, .width, .equal, nil, .notAnAttribute, 5))
        addConstraint(_JCLayoutConstraintMake(errorTipsView, .height, .equal, nil, .notAnAttribute, 60))
        
        addConstraint(_JCLayoutConstraintMake(cancelRecordImageView, .centerX, .equal, self, .centerX))
        addConstraint(_JCLayoutConstraintMake(cancelRecordImageView, .top, .equal, self, .top, 21.5))
        addConstraint(_JCLayoutConstraintMake(cancelRecordImageView, .width, .equal, nil, .notAnAttribute, 43))
        addConstraint(_JCLayoutConstraintMake(cancelRecordImageView, .height, .equal, nil, .notAnAttribute, 60))
        
        addConstraint(_JCLayoutConstraintMake(remiadeLable, .height, .equal, nil, .notAnAttribute, 19))
        addConstraint(_JCLayoutConstraintMake(remiadeLable, .top, .equal, recordingHUDImageView, .bottom, 25.5))
        addConstraint(_JCLayoutConstraintMake(remiadeLable, .right, .equal, self, .right, -10))
        addConstraint(_JCLayoutConstraintMake(remiadeLable, .left, .equal, self, .left, 10))
        
        addConstraint(_JCLayoutConstraintMake(timeLable, .height, .equal, nil, .notAnAttribute, 16.5))
        addConstraint(_JCLayoutConstraintMake(timeLable, .top, .equal, recordingHUDImageView, .bottom, 5))
        addConstraint(_JCLayoutConstraintMake(timeLable, .right, .equal, self, .right))
        addConstraint(_JCLayoutConstraintMake(timeLable, .left, .equal, self, .left))
        
        addConstraint(_JCLayoutConstraintMake(tipsLable, .height, .equal, nil, .notAnAttribute, 86.5))
        addConstraint(_JCLayoutConstraintMake(tipsLable, .top, .equal, self, .top, 9.5))
        addConstraint(_JCLayoutConstraintMake(tipsLable, .right, .equal, self, .right))
        addConstraint(_JCLayoutConstraintMake(tipsLable, .left, .equal, self, .left))
    }
    
    func startRecordingHUDAtView(_ view:UIView) {
        view.addSubview(self)
        self.center = view.center
        configRecoding(true)
    }
    
    func pauseRecord() {
        configRecoding(true)
        remiadeLable.backgroundColor = UIColor.clear
        remiadeLable.text = voiceRecordPauseString
    }
    
    func resaueRecord() {
        configRecoding(false)
        remiadeLable.backgroundColor = UIColor(netHex: 0x7E1D22)
        remiadeLable.text = voiceRecordResaueString
    }
    
    func stopRecordCompleted(_ completed: (_ finish:Bool) -> Void) {
        dismissCompled(completed)
    }
    
    func cancelRecordCompleted(_ completed: (_ finish:Bool) -> Void) {
        dismissCompled(completed)
    }
    
    func dismissCompleted(_ completed:@escaping (_ finish:Bool) -> Void) {
        UIView.animate(withDuration: 0.3, delay: 0, options: .curveEaseOut, animations: { () -> Void in
            self.alpha = 0.0
        }) { (finished:Bool) -> Void in
            super.removeFromSuperview()
            completed(finished)
        }
    }
    
    func configRecoding(_ recording: Bool) {
        isRecording =  recording
        recordingHUDImageView.isHidden = !recording
        cancelRecordImageView.isHidden = recording
    }
    
    func setTime(_ time: TimeInterval) {
        let t = Int(time)
        
        if t > 49 {
            tipsLable.isHidden = false
            timeLable.isHidden = true
            cancelRecordImageView.isHidden = true
            recordingHUDImageView.isHidden = true
            tipsLable.text = "\(60 - t)"
        } else {
            tipsLable.isHidden = true
            timeLable.isHidden = false
            if isRecording {
                recordingHUDImageView.isHidden = false
            } else {
                cancelRecordImageView.isHidden = false
            }
        }
        if t >= 60 {
            timeLable.text = "······ 1.00 ······"
        } else if t > 9 {
            timeLable.text = "······ 0.\(t) ······"
        } else {
            timeLable.text = "······ 0.0\(t) ······"
        }
    }
    
    func setPeakPower(_ peakPower: Float) {
        self.peakPower = peakPower
    }
    
    func showErrorTips() {
        recordingHUDImageView.isHidden = true
        errorTipsView.isHidden = false
        timeLable.isHidden = true
        remiadeLable.text = "说话时间太短"
    }
}
