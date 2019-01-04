//
// Copyright (c) 2016 Related Code - http://relatedcode.com
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
// THE SOFTWARE.

#import "ProgressHUD.h"

//-------------------------------------------------------------------------------------------------------------------------------------------------
@interface ProgressHUD()
{
	UIWindow *window;
	UIView *viewBackground;
	UIToolbar *toolbarHUD;
	UIActivityIndicatorView *spinner;
	UIImageView *imageView;
	UILabel *labelStatus;
}
@end
//-------------------------------------------------------------------------------------------------------------------------------------------------

@implementation ProgressHUD

//-------------------------------------------------------------------------------------------------------------------------------------------------
+ (ProgressHUD *)shared
//-------------------------------------------------------------------------------------------------------------------------------------------------
{
	static dispatch_once_t once;
	static ProgressHUD *progressHUD;
	//---------------------------------------------------------------------------------------------------------------------------------------------
	dispatch_once(&once, ^{ progressHUD = [[ProgressHUD alloc] init]; });
	//---------------------------------------------------------------------------------------------------------------------------------------------
	return progressHUD;
}

#pragma mark - Display methods

//-------------------------------------------------------------------------------------------------------------------------------------------------
+ (void)dismiss
//-------------------------------------------------------------------------------------------------------------------------------------------------
{
	dispatch_async(dispatch_get_main_queue(), ^{
		[[self shared] hudHide];
	});
}

//-------------------------------------------------------------------------------------------------------------------------------------------------
+ (void)show
//-------------------------------------------------------------------------------------------------------------------------------------------------
{
	dispatch_async(dispatch_get_main_queue(), ^{
		[[self shared] hudCreate:nil image:nil spin:YES hide:NO interaction:YES];
	});
}

//-------------------------------------------------------------------------------------------------------------------------------------------------
+ (void)show:(NSString *)status
//-------------------------------------------------------------------------------------------------------------------------------------------------
{
	dispatch_async(dispatch_get_main_queue(), ^{
		[[self shared] hudCreate:status image:nil spin:YES hide:NO interaction:YES];
	});
}

//-------------------------------------------------------------------------------------------------------------------------------------------------
+ (void)show:(NSString *)status Interaction:(BOOL)interaction
//-------------------------------------------------------------------------------------------------------------------------------------------------
{
	dispatch_async(dispatch_get_main_queue(), ^{
		[[self shared] hudCreate:status image:nil spin:YES hide:NO interaction:interaction];
	});
}

//-------------------------------------------------------------------------------------------------------------------------------------------------
+ (void)showSuccess
//-------------------------------------------------------------------------------------------------------------------------------------------------
{
	dispatch_async(dispatch_get_main_queue(), ^{
		[[self shared] hudCreate:nil image:[self shared].imageSuccess spin:NO hide:YES interaction:YES];
	});
}

//-------------------------------------------------------------------------------------------------------------------------------------------------
+ (void)showSuccess:(NSString *)status
//-------------------------------------------------------------------------------------------------------------------------------------------------
{
	dispatch_async(dispatch_get_main_queue(), ^{
		[[self shared] hudCreate:status image:[self shared].imageSuccess spin:NO hide:YES interaction:YES];
	});
}

//-------------------------------------------------------------------------------------------------------------------------------------------------
+ (void)showSuccess:(NSString *)status Interaction:(BOOL)interaction
//-------------------------------------------------------------------------------------------------------------------------------------------------
{
	dispatch_async(dispatch_get_main_queue(), ^{
		[[self shared] hudCreate:status image:[self shared].imageSuccess spin:NO hide:YES interaction:interaction];
	});
}

//-------------------------------------------------------------------------------------------------------------------------------------------------
+ (void)showError
//-------------------------------------------------------------------------------------------------------------------------------------------------
{
	dispatch_async(dispatch_get_main_queue(), ^{
		[[self shared] hudCreate:nil image:[self shared].imageError spin:NO hide:YES interaction:YES];
	});
}

//-------------------------------------------------------------------------------------------------------------------------------------------------
+ (void)showError:(NSString *)status
//-------------------------------------------------------------------------------------------------------------------------------------------------
{
	dispatch_async(dispatch_get_main_queue(), ^{
		[[self shared] hudCreate:status image:[self shared].imageError spin:NO hide:YES interaction:YES];
	});
}

//-------------------------------------------------------------------------------------------------------------------------------------------------
+ (void)showError:(NSString *)status Interaction:(BOOL)interaction
//-------------------------------------------------------------------------------------------------------------------------------------------------
{
	dispatch_async(dispatch_get_main_queue(), ^{
		[[self shared] hudCreate:status image:[self shared].imageError spin:NO hide:YES interaction:interaction];
	});
}

#pragma mark - Property methods

//-------------------------------------------------------------------------------------------------------------------------------------------------
+ (void)statusFont:(UIFont *)font			{	[self shared].statusFont		= font;		}
+ (void)statusColor:(UIColor *)color		{	[self shared].statusColor		= color;	}
+ (void)spinnerColor:(UIColor *)color		{	[self shared].spinnerColor		= color;	}
+ (void)hudColor:(UIColor *)color			{	[self shared].hudColor			= color;	}
+ (void)backgroundColor:(UIColor *)color	{	[self shared].backgroundColor	= color;	}
+ (void)imageSuccess:(UIImage *)image		{	[self shared].imageSuccess		= image;	}
+ (void)imageError:(UIImage *)image			{	[self shared].imageError		= image;	}
//-------------------------------------------------------------------------------------------------------------------------------------------------

#pragma mark -

//-------------------------------------------------------------------------------------------------------------------------------------------------
- (id)init
//-------------------------------------------------------------------------------------------------------------------------------------------------
{
	self = [super initWithFrame:[[UIScreen mainScreen] bounds]];
	//---------------------------------------------------------------------------------------------------------------------------------------------
	self.statusFont			= [UIFont boldSystemFontOfSize:16];
	self.statusColor		= [UIColor blackColor];
	self.spinnerColor		= [UIColor colorWithRed:251.0/255.0 green:71.0/255.0 blue:71.0/255.0 alpha:1.0];
	self.hudColor			= [UIColor colorWithWhite:0.0 alpha:0.1];
	self.backgroundColor	= [UIColor colorWithRed:0.0 green:0.0 blue:0.0 alpha:0.2];
	NSBundle *bundle		= [NSBundle bundleForClass:[self class]];
	self.imageSuccess		= [UIImage imageNamed:@"ProgressHUD.bundle/progresshud-success" inBundle:bundle compatibleWithTraitCollection:nil];
	self.imageError			= [UIImage imageNamed:@"ProgressHUD.bundle/progresshud-error" inBundle:bundle compatibleWithTraitCollection:nil];
	//---------------------------------------------------------------------------------------------------------------------------------------------
	id<UIApplicationDelegate> delegate = [[UIApplication sharedApplication] delegate];
	//---------------------------------------------------------------------------------------------------------------------------------------------
	if ([delegate respondsToSelector:@selector(window)])
		window = [delegate performSelector:@selector(window)];
	else window = [[UIApplication sharedApplication] keyWindow];
	//---------------------------------------------------------------------------------------------------------------------------------------------
	viewBackground = nil; toolbarHUD = nil; spinner = nil; imageView = nil; labelStatus = nil;
	//---------------------------------------------------------------------------------------------------------------------------------------------
	self.alpha = 0;
	//---------------------------------------------------------------------------------------------------------------------------------------------
	return self;
}

#pragma mark -

//-------------------------------------------------------------------------------------------------------------------------------------------------
- (void)hudCreate:(NSString *)status image:(UIImage *)image spin:(BOOL)spin hide:(BOOL)hide interaction:(BOOL)interaction
//-------------------------------------------------------------------------------------------------------------------------------------------------
{
	if (toolbarHUD == nil)
	{
		toolbarHUD = [[UIToolbar alloc] initWithFrame:CGRectZero];
		toolbarHUD.translucent = YES;
		toolbarHUD.backgroundColor = self.hudColor;
		toolbarHUD.layer.cornerRadius = 10;
		toolbarHUD.layer.masksToBounds = YES;
		[self registerNotifications];
	}
	//---------------------------------------------------------------------------------------------------------------------------------------------
	if (toolbarHUD.superview == nil)
	{
		if (interaction == NO)
		{
			viewBackground = [[UIView alloc] initWithFrame:window.frame];
			viewBackground.backgroundColor = self.backgroundColor;
			[window addSubview:viewBackground];
			[viewBackground addSubview:toolbarHUD];
		}
		else [window addSubview:toolbarHUD];
	}
	//---------------------------------------------------------------------------------------------------------------------------------------------
	if (spinner == nil)
	{
		spinner = [[UIActivityIndicatorView alloc] initWithActivityIndicatorStyle:UIActivityIndicatorViewStyleWhiteLarge];
		spinner.color = self.spinnerColor;
		spinner.hidesWhenStopped = YES;
	}
	if (spinner.superview == nil) [toolbarHUD addSubview:spinner];
	//---------------------------------------------------------------------------------------------------------------------------------------------
	if (imageView == nil)
	{
		imageView = [[UIImageView alloc] initWithFrame:CGRectMake(0, 0, 28, 28)];
	}
	if (imageView.superview == nil) [toolbarHUD addSubview:imageView];
	//---------------------------------------------------------------------------------------------------------------------------------------------
	if (labelStatus == nil)
	{
		labelStatus = [[UILabel alloc] initWithFrame:CGRectZero];
		labelStatus.font = self.statusFont;
		labelStatus.textColor = self.statusColor;
		labelStatus.backgroundColor = [UIColor clearColor];
		labelStatus.textAlignment = NSTextAlignmentCenter;
		labelStatus.baselineAdjustment = UIBaselineAdjustmentAlignCenters;
		labelStatus.numberOfLines = 0;
	}
	if (labelStatus.superview == nil) [toolbarHUD addSubview:labelStatus];
	//---------------------------------------------------------------------------------------------------------------------------------------------

	//---------------------------------------------------------------------------------------------------------------------------------------------
	labelStatus.text = status;
	labelStatus.hidden = (status == nil) ? YES : NO;
	//---------------------------------------------------------------------------------------------------------------------------------------------
	imageView.image = image;
	imageView.hidden = (image == nil) ? YES : NO;
	//---------------------------------------------------------------------------------------------------------------------------------------------
	if (spin) [spinner startAnimating]; else [spinner stopAnimating];
	//---------------------------------------------------------------------------------------------------------------------------------------------

	//---------------------------------------------------------------------------------------------------------------------------------------------
	[self hudSize];
	[self hudPosition:nil];
	[self hudShow];
	//---------------------------------------------------------------------------------------------------------------------------------------------
	if (hide) [self timedHide];
}

//-------------------------------------------------------------------------------------------------------------------------------------------------
- (void)registerNotifications
//-------------------------------------------------------------------------------------------------------------------------------------------------
{
	[[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(hudPosition:)
												 name:UIApplicationDidChangeStatusBarOrientationNotification object:nil];

	[[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(hudPosition:) name:UIKeyboardWillHideNotification object:nil];
	[[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(hudPosition:) name:UIKeyboardDidHideNotification object:nil];
	[[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(hudPosition:) name:UIKeyboardWillShowNotification object:nil];
	[[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(hudPosition:) name:UIKeyboardDidShowNotification object:nil];
}

//-------------------------------------------------------------------------------------------------------------------------------------------------
- (void)hudDestroy
//-------------------------------------------------------------------------------------------------------------------------------------------------
{
	[[NSNotificationCenter defaultCenter] removeObserver:self];
	//---------------------------------------------------------------------------------------------------------------------------------------------
	[labelStatus removeFromSuperview];		labelStatus = nil;
	[imageView removeFromSuperview];		imageView = nil;
	[spinner removeFromSuperview];			spinner = nil;
	[toolbarHUD removeFromSuperview];		toolbarHUD = nil;
	[viewBackground removeFromSuperview];	viewBackground = nil;
}

#pragma mark -

//-------------------------------------------------------------------------------------------------------------------------------------------------
- (void)hudSize
//-------------------------------------------------------------------------------------------------------------------------------------------------
{
	CGRect rectLabel = CGRectZero;
	CGFloat widthHUD = 100, heightHUD = 100;
	//---------------------------------------------------------------------------------------------------------------------------------------------
	if (labelStatus.text != nil)
	{
		NSDictionary *attributes = @{NSFontAttributeName:labelStatus.font};
		NSInteger options = NSStringDrawingUsesFontLeading | NSStringDrawingTruncatesLastVisibleLine | NSStringDrawingUsesLineFragmentOrigin;
		rectLabel = [labelStatus.text boundingRectWithSize:CGSizeMake(200, 300) options:options attributes:attributes context:NULL];

		widthHUD = rectLabel.size.width + 50;
		heightHUD = rectLabel.size.height + 75;

		if (widthHUD < 100) widthHUD = 100;
		if (heightHUD < 100) heightHUD = 100;

		rectLabel.origin.x = (widthHUD - rectLabel.size.width) / 2;
		rectLabel.origin.y = (heightHUD - rectLabel.size.height) / 2 + 25;
	}
	//---------------------------------------------------------------------------------------------------------------------------------------------
	toolbarHUD.bounds = CGRectMake(0, 0, widthHUD, heightHUD);
	//---------------------------------------------------------------------------------------------------------------------------------------------
	CGFloat imageX = widthHUD/2;
	CGFloat imageY = (labelStatus.text == nil) ? heightHUD/2 : 36;
	imageView.center = spinner.center = CGPointMake(imageX, imageY);
	//---------------------------------------------------------------------------------------------------------------------------------------------
	labelStatus.frame = rectLabel;
}

//-------------------------------------------------------------------------------------------------------------------------------------------------
- (void)hudPosition:(NSNotification *)notification
//-------------------------------------------------------------------------------------------------------------------------------------------------
{
	CGFloat heightKeyboard = 0;
	NSTimeInterval duration = 0;
	//---------------------------------------------------------------------------------------------------------------------------------------------
	if (notification != nil)
	{
		NSDictionary *info = [notification userInfo];
		CGRect keyboard = [[info valueForKey:UIKeyboardFrameEndUserInfoKey] CGRectValue];
		duration = [[info valueForKey:UIKeyboardAnimationDurationUserInfoKey] doubleValue];
		if ((notification.name == UIKeyboardWillShowNotification) || (notification.name == UIKeyboardDidShowNotification))
		{
			heightKeyboard = keyboard.size.height;
		}
	}
	else heightKeyboard = [self keyboardHeight];
	//---------------------------------------------------------------------------------------------------------------------------------------------
	CGRect screen = [UIScreen mainScreen].bounds;
	CGPoint center = CGPointMake(screen.size.width/2, (screen.size.height-heightKeyboard)/2);
	//---------------------------------------------------------------------------------------------------------------------------------------------
	[UIView animateWithDuration:duration delay:0 options:UIViewAnimationOptionAllowUserInteraction animations:^{
		toolbarHUD.center = CGPointMake(center.x, center.y);
	} completion:nil];
	//---------------------------------------------------------------------------------------------------------------------------------------------
	if (viewBackground != nil) viewBackground.frame = window.frame;
}

//-------------------------------------------------------------------------------------------------------------------------------------------------
- (CGFloat)keyboardHeight
//-------------------------------------------------------------------------------------------------------------------------------------------------
{
	for (UIWindow *testWindow in [[UIApplication sharedApplication] windows])
	{
		if ([[testWindow class] isEqual:[UIWindow class]] == NO)
		{
			for (UIView *possibleKeyboard in [testWindow subviews])
			{
				if ([[possibleKeyboard description] hasPrefix:@"<UIPeripheralHostView"])
				{
					return possibleKeyboard.bounds.size.height;
				}
				else if ([[possibleKeyboard description] hasPrefix:@"<UIInputSetContainerView"])
				{
					for (UIView *hostKeyboard in [possibleKeyboard subviews])
					{
						if ([[hostKeyboard description] hasPrefix:@"<UIInputSetHost"])
						{
							return hostKeyboard.frame.size.height;
						}
					}
				}
			}
		}
	}
	return 0;
}

#pragma mark -

//-------------------------------------------------------------------------------------------------------------------------------------------------
- (void)hudShow
//-------------------------------------------------------------------------------------------------------------------------------------------------
{
	if (self.alpha == 0)
	{
		self.alpha = 1;
		toolbarHUD.alpha = 0;
		toolbarHUD.transform = CGAffineTransformScale(toolbarHUD.transform, 1.4, 1.4);

		UIViewAnimationOptions options = UIViewAnimationOptionAllowUserInteraction | UIViewAnimationCurveEaseOut;
		[UIView animateWithDuration:0.15 delay:0 options:options animations:^{
			toolbarHUD.transform = CGAffineTransformScale(toolbarHUD.transform, 1/1.4, 1/1.4);
			toolbarHUD.alpha = 1;
		} completion:nil];
	}
}

//-------------------------------------------------------------------------------------------------------------------------------------------------
- (void)hudHide
//-------------------------------------------------------------------------------------------------------------------------------------------------
{
	if (self.alpha == 1)
	{
		UIViewAnimationOptions options = UIViewAnimationOptionAllowUserInteraction | UIViewAnimationCurveEaseIn;
		[UIView animateWithDuration:0.15 delay:0 options:options animations:^{
			toolbarHUD.transform = CGAffineTransformScale(toolbarHUD.transform, 0.7, 0.7);
			toolbarHUD.alpha = 0;
		}
		completion:^(BOOL finished) {
			[self hudDestroy];
			self.alpha = 0;
		}];
	}
}

//-------------------------------------------------------------------------------------------------------------------------------------------------
- (void)timedHide
//-------------------------------------------------------------------------------------------------------------------------------------------------
{
	NSTimeInterval delay = labelStatus.text.length * 0.04 + 0.5;
	dispatch_time_t time = dispatch_time(DISPATCH_TIME_NOW, delay * NSEC_PER_SEC);
	dispatch_after(time, dispatch_get_main_queue(), ^(void){ [self hudHide]; });
}

@end
