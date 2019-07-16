//
//  SAIInputView.swift
//  SAIInputBar
//
//  Created by SAGESSE on 7/23/16.
//  Copyright © 2016-2017 SAGESSE. All rights reserved.
//

import UIKit


internal class SAIInputView: UIView {
    
    override var intrinsicContentSize: CGSize {
        if let size = _cacheContentSize {
            return size
        }
        _cacheContentSize = _inputView?.intrinsicContentSize
        return _cacheContentSize ?? .zero
    }
    override func invalidateIntrinsicContentSize() {
        _cacheContentSize = nil
        _inputView?.invalidateIntrinsicContentSize()
        super.invalidateIntrinsicContentSize()
    }
    
    override func layoutSubviews() {
        if _cacheBounds?.width != bounds.width {
            _cacheBounds = bounds
            invalidateIntrinsicContentSize()
        }
        super.layoutSubviews()
    }
    
    func updateInputMode(_ newMode: SAIInputMode, oldMode: SAIInputMode, animated: Bool) {
        
        _inputMode = newMode
        
        switch newMode {
        case .selecting(let view):
            guard view != _inputView else {
                break 
            }
            
            view.translatesAutoresizingMaskIntoConstraints = false
            
            addSubview(view)
            
            let viewcs = [
                _SAInputLayoutConstraintMake(view, .top, .equal, self, .top),
                _SAInputLayoutConstraintMake(view, .left, .equal, self, .left),
                _SAInputLayoutConstraintMake(view, .right, .equal, self, .right),
            ]
            
            addConstraints(viewcs)
            
            view.layoutIfNeeded()
            view.frame = CGRect(origin: .zero, size: view.frame.size)
            //setNeedsLayout()
            //layoutIfNeeded()
            
            if let oview = _inputView, let oviewcs = _inputViewConstraints, oldMode.isSelecting {
                
                oview.transform = CGAffineTransform(translationX: 0, y: 0)
                view.transform = CGAffineTransform(translationX: 0, y: view.frame.height)
                
                UIView.animate(withDuration: _SAInputDefaultAnimateDuration, animations: { 
                    UIView.setAnimationCurve(_SAInputDefaultAnimateCurve)
                    
                    oview.transform = CGAffineTransform(translationX: 0, y: oview.frame.height)
                    view.transform = CGAffineTransform(translationX: 0, y: 0)
                }, completion: { b in
                    guard self._inputView !== oview  else {
                        return
                    }
                    self.removeConstraints(oviewcs)
                    oview.removeFromSuperview()
                    oview.transform = CGAffineTransform(translationX: 0, y: 0)
                })
            }
            
            // update input view
            _inputView = view
            _inputViewConstraints = viewcs
        
        default:
            if let oview = _inputView, let oviewcs = _inputViewConstraints {
                UIView.animate(withDuration: _SAInputDefaultAnimateDuration, animations: { 
                    UIView.setAnimationCurve(_SAInputDefaultAnimateCurve)
                    oview.frame = CGRect(x: 0, y: 0, width: self.frame.width, height: 0)
                }, completion: { b in
                    if self._inputMode.isSelecting && self._inputView === oview {
                        return // ignore
                    }
                    self.removeConstraints(oviewcs)
                    oview.removeFromSuperview()
                    self._inputView = nil
                    self._inputViewConstraints = nil
                })
            }
            break
        }
        
        invalidateIntrinsicContentSize()
    }
    
    private var _cacheBounds: CGRect?
    private var _cacheContentSize: CGSize?
    
    private var _inputMode: SAIInputMode = .none
    private var _inputView: UIView?
    private var _inputViewConstraints: [NSLayoutConstraint]?
}
