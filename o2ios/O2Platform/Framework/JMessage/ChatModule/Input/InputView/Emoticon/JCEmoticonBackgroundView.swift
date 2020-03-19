//
//  JCEmoticonBackgroundView.swift
//  JChat
//
//  Created by deng on 2017/3/9.
//  Copyright © 2017年 HXHG. All rights reserved.
//

import UIKit

internal class JCEmoticonBackgroundView: UIView {

    var popoverFrame: CGRect {
        set { return frame = newValue }
        get { return frame }
    }
    var presenterFrame: CGRect = .zero
    
    func boundsOfContent(for type: JCEmoticonType) -> CGRect {
        let rect = bounds(for: type)
        
        switch type {
        case .small: return CGRect(x: 4, y: 4, width: rect.width - 4 * 2, height: rect.width - 4 * 2)
        case .large: return rect.inset(by: UIEdgeInsets(top: 8, left: 8, bottom: 8 + 12, right: 8))
        }
    }
    
    func bounds(for type: JCEmoticonType) -> CGRect {
        var frame = CGRect.zero
        switch type {
        case .small:
            frame.origin.x = 0
            frame.origin.y = 39
            frame.size = _SAIEmoticonPreviewBackgroundImageForSmall?.size ?? .zero
            
        case .large:
            frame.origin.x = 0
            frame.origin.y = 0
            frame.size.width = 170
            frame.size.height = 170
        }
        return frame
    }
    
    func updateBackgroundImages(with type: JCEmoticonType) {
        guard _type != type else {
            return
        }
        
        switch type {
        case .small:
            _leftView.image = _SAIEmoticonPreviewBackgroundImageForSmall
            _rightView.image = _SAIEmoticonPreviewBackgroundImageForSmall
            _middleView.image = _SAIEmoticonPreviewBackgroundImageForSmall
            
        case .large:
            _leftView.image = _SAIEmoticonPreviewBackgroundImageForLargeOfLeft
            _rightView.image = _SAIEmoticonPreviewBackgroundImageForLargeOfRight
            _middleView.image = _SAIEmoticonPreviewBackgroundImageForLargeOfMiddle
        }
        _type = type
    }
    
    func updateBackgroundLayouts() {
        
        var ty = CGFloat(0)
        if _type.isSmall {
            ty = (presenterFrame.height - popoverFrame.height - 34) / 2
        }
        
        _middleViewBottom?.constant = ty
        _middleViewCenterX?.constant = presenterFrame.midX
    }
    
    private func _init() {
        
        isUserInteractionEnabled = false
        
        _leftView.translatesAutoresizingMaskIntoConstraints = false
        _rightView.translatesAutoresizingMaskIntoConstraints = false
        _middleView.translatesAutoresizingMaskIntoConstraints = false
        
        _middleView.image = _SAIEmoticonPreviewBackgroundImageForSmall
        _middleView.setContentHuggingPriority(UILayoutPriority.required, for: .horizontal)
        _middleView.setContentCompressionResistancePriority(UILayoutPriority.required, for: .horizontal)
        
        addSubview(_leftView)
        addSubview(_rightView)
        addSubview(_middleView)
        
        addConstraint(_JCEmoticonLayoutConstraintMake(_leftView, .top, .equal, self, .top))
        addConstraint(_JCEmoticonLayoutConstraintMake(_leftView, .left, .equal, self, .left, priority: UILayoutPriority(rawValue: 751)))
        addConstraint(_JCEmoticonLayoutConstraintMake(_leftView, .right, .equal, _middleView, .left))
        addConstraint(_JCEmoticonLayoutConstraintMake(_leftView, .bottom, .equal, self, .bottom))
        
        addConstraint(_JCEmoticonLayoutConstraintMake(_middleView, .top, .equal, self, .top))
        addConstraint(_JCEmoticonLayoutConstraintMake(_middleView, .centerX, .equal, self, .left, output: &_middleViewCenterX))
        addConstraint(_JCEmoticonLayoutConstraintMake(_middleView, .bottom, .equal, self, .bottom, output: &_middleViewBottom))
        
        addConstraint(_JCEmoticonLayoutConstraintMake(_rightView, .top, .equal, self, .top))
        addConstraint(_JCEmoticonLayoutConstraintMake(_rightView, .left, .equal, _middleView, .right))
        addConstraint(_JCEmoticonLayoutConstraintMake(_rightView, .right, .equal, self, .right, priority: UILayoutPriority(rawValue: 751)))
        addConstraint(_JCEmoticonLayoutConstraintMake(_rightView, .bottom, .equal, self, .bottom))
    }
    
    private var _type: JCEmoticonType = .small
    private var _middleViewBottom: NSLayoutConstraint?
    private var _middleViewCenterX: NSLayoutConstraint?
    
    private lazy var _leftView: UIImageView = UIImageView()
    private lazy var _middleView: UIImageView = UIImageView()
    private lazy var _rightView: UIImageView = UIImageView()
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        _init()
    }
    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        _init()
    }

}

private var _SAIEmoticonPreviewBackgroundImageForSmall: UIImage? = {
    let png = "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"
    return _SAEmoticonLoadImage(base64Encoded: png, scale: 2)
}()
private var _SAIEmoticonPreviewBackgroundImageForLargeOfLeft: UIImage? = {
    let png = "iVBORw0KGgoAAAANSUhEUgAAADwAAAC0CAMAAAD/wb/1AAAAY1BMVEUAAADBwcHExMTNzc3Hx8fDw8Pj4+PGxsbIyMjCwsLLy8vIyMjJycnIyMjHx8e/v7/s7Ozf39/Y2NjJycnIyMjIyMjGxsbHx8fIyMjHx8fFxcX////Gxsb39/f6+vrx8fHr6+sc50oiAAAAG3RSTlMABycCHA38I/kW+O7YqlYS/vr468G9VUc8m5v7bzgfAAABEElEQVRo3u3aya7CMAyF4Zs6U0culNkUeP+npLtIBJXKlQxI5+w/ZZ/ff9ivj4jM272WI3SltSEUk3tJRxm607bp42VyuR2p3bd1tb7fBp5e/qyzh13lk5uPyayKNnqet8wem82VJZhM2dWeZ+/p3S7+sxCvjvVoRZhc0XiWYTK23bAUu0O8CjEZu/MsxW5fsRSbsvVSTCbUgxi7rmIxLk9rMTZ2e1+Am5sch35YgCPLcXEBBgYGBgYGBgYGBgYGBgYGBgYGBgYG/ggOcQnuB+0//VQT1DtGKijq7SZVI/1elUqZfqNLdVC/S6Yiqt9iUwXW7s95+VZq7vLan98ZnAV3BoILB+FtBYZhGIZhX7cHYMfPwx97p7cAAAAASUVORK5CYII="
    return _SAEmoticonLoadImage(base64Encoded: png, scale: 2)?.resizableImage(withCapInsets: UIEdgeInsets(top: 16, left: 16, bottom: 32, right: 0))
}()
private var _SAIEmoticonPreviewBackgroundImageForLargeOfMiddle: UIImage? = {
    let png = "iVBORw0KGgoAAAANSUhEUgAAADwAAAC0CAMAAAD/wb/1AAAAulBMVEUAAAC/v7/ExMTBwcG/v7/ExMTJycnGxsbCwsLa2trFxcXHx8fGxsbJycnm5ubq6urh4eHZ2dnPz8/V1dXMzMzKysrJycnIyMjIyMjIyMjIyMjGxsbFxcXCwsLc3NzIyMjIyMjIyMjHx8fHx8fHx8fJycnHx8fS0tLR0dHJycnHx8fIyMjJycnIyMjHx8fGxsbIyMjIyMjFxcXBwcHHx8fGxsbGxsb////Gxsb9/f35+fn19fXy8vLu7u7Y6pwTAAAAN3RSTlMACCcCBA32GBIGFTEiEP39+/n5+Pj47+i5UjcqHhv689nRhXVqSD/4+ODJwqSclX5iWyUdrauG/KKu2AAAAZpJREFUaN7t2seuglAUhWG86rEAVgQUsfdebpHi+7/WNUICuBFOwshk7fk3+bM4IwTcx1wuwwlfGU64ZTjhL8MBAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDfxy+edey+I3V8pH/h4Oot/lxWxc95f9bIRvjBq9tjAzZU4J3Bbmn1vlsXe3JBSF8+UK5yxwe67ButZAXojpXmnbsdGt3pqWcZyP6ul9ZqaFX+yu1D12Uts003NxKRc8SLa5ryba2Fqn1k5cXw3uSvQ/n5XCsaPLqiTlJoU9BaKor5kSx3sZSfs1KyNLk/Z/221V+96OhabTLePBmleMLiUV0T6vHrlILVkkv2OnQpdZlXRI6Llpp1rHjVklixUYzDy3r9fM/mF6sdC3pzZdV6qFVpkUzRo3IKkcGl/WfhoV6D61SXZQTQtOdnpkTrPJMVpmS/KjYfmjlmBaa7nS3tJ6hlzuyyvRo0ua508GGhObRolZ7hNZEHkt3Omeuy8jnz5t8xtiMOzR5jScT8s5y60qxWCGWWz9OwEXuHy5DclLKt/gsAAAAAElFTkSuQmCC"
    return _SAEmoticonLoadImage(base64Encoded: png, scale: 2)?.resizableImage(withCapInsets: UIEdgeInsets(top: 16, left: 0, bottom: 32, right: 0))
}()
private var _SAIEmoticonPreviewBackgroundImageForLargeOfRight: UIImage? = {
    let png = "iVBORw0KGgoAAAANSUhEUgAAADwAAAC0CAMAAAD/wb/1AAAAXVBMVEUAAADBwcHExMTHx8fFxcXGxsbj4+PJycnHx8fGxsbIyMjCwsLLy8vJycnIyMjt7e3f39/Y2NjIyMjHx8fHx8fGxsbBwcHJycnKysr////Gxsb39/f6+vrx8fHr6+urOgctAAAAGXRSTlMABycDDhr87lYi+RT42b/++vitqJtHKT0+uuTYmQAAAQdJREFUaN7t2skKwjAUhWHTTE06OA/Xqu//mLq7IFrtKUSE8+8/CFnmZMH+JvMxa9/iaryUfOOMeePPo+XY9bs6efeay3jD9bZq46Z+zeWLhtD2e+/sNKyFvD04YzEsl3V3bJ61fF2IteqpWJb5oadi1fHoLIoldAdnUSzrrTcwvuS9syiW0HsDY2lrZ2EcNt7AeIgJwnpuGK92DY5vvTcwvnYJx0OcgSVXM/CZmJiYmJiYmJiYmJiYmJiYmJiYmJiY+Dc4V8Vf1vVNv/SaoDtG4QVF76v4aqSnLr+U6UaHr4Pld8llPKkttMXqClx8f9blG9/c8bV/1j8D/IcD/reCMcYYY3/THaKymm1e6hXLAAAAAElFTkSuQmCC"
    return _SAEmoticonLoadImage(base64Encoded: png, scale: 2)?.resizableImage(withCapInsets: UIEdgeInsets(top: 16, left: 0, bottom: 32, right: 16))
}()
