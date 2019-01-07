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

#import <UIKit/UIKit.h>

//-------------------------------------------------------------------------------------------------------------------------------------------------
@interface ProgressHUD : UIView
//-------------------------------------------------------------------------------------------------------------------------------------------------

#pragma mark - Display methodsProgressHUD

+ (void)dismiss;

+ (void)show;
+ (void)show:(NSString *)status;
+ (void)show:(NSString *)status Interaction:(BOOL)interaction;

+ (void)showSuccess;
+ (void)showSuccess:(NSString *)status;
+ (void)showSuccess:(NSString *)status Interaction:(BOOL)interaction;

+ (void)showError;
+ (void)showError:(NSString *)status;
+ (void)showError:(NSString *)status Interaction:(BOOL)interaction;

#pragma mark - Property methods

+ (void)statusFont:(UIFont *)font;
+ (void)statusColor:(UIColor *)color;
+ (void)spinnerColor:(UIColor *)color;
+ (void)hudColor:(UIColor *)color;
+ (void)backgroundColor:(UIColor *)color;
+ (void)imageSuccess:(UIImage *)image;
+ (void)imageError:(UIImage *)image;

#pragma mark - Properties

@property (strong, nonatomic) UIFont *statusFont;
@property (strong, nonatomic) UIColor *statusColor;
@property (strong, nonatomic) UIColor *spinnerColor;
@property (strong, nonatomic) UIColor *hudColor;
@property (strong, nonatomic) UIColor *backgroundColor;
@property (strong, nonatomic) UIImage *imageSuccess;
@property (strong, nonatomic) UIImage *imageError;

@end
