//
//  ZSSTextView.m
//  ZSSRichTextEditor
//
//  Created by Nicholas Hubbard on 1/29/14.
//  Copyright (c) 2014 Zed Said Studio. All rights reserved.
//

#import "ZSSTextView.h"
#import <CoreText/CoreText.h>

#define RGB(r,g,b) [UIColor colorWithRed:r/255.0f green:g/255.0f blue:b/255.0f alpha:1.0f]

@implementation ZSSTextView

- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        [self commonSetup];
    }
    return self;
}

- (void)commonSetup
{
    _defaultFont = [UIFont systemFontOfSize:14.0f];
    _boldFont = [UIFont boldSystemFontOfSize:14.0f];
    _italicFont = [UIFont fontWithName:@"HelveticaNeue-Oblique" size:14.0f];
    
    self.font = _defaultFont;
    
    [self addObserver:self forKeyPath:NSStringFromSelector(@selector(defaultFont)) options:NSKeyValueObservingOptionNew context:0];
    [self addObserver:self forKeyPath:NSStringFromSelector(@selector(boldFont)) options:NSKeyValueObservingOptionNew context:0];
    [self addObserver:self forKeyPath:NSStringFromSelector(@selector(italicFont)) options:NSKeyValueObservingOptionNew context:0];
    
    if (_italicFont == nil && ([UIFontDescriptor class] != nil))
    {
        // This works around a bug in 7.0.3 where HelveticaNeue-Italic is not present as a UIFont option
        _italicFont = (__bridge_transfer UIFont*)CTFontCreateWithName(CFSTR("HelveticaNeue-Italic"), 14.0f, NULL);
    }
    
    self.tokens = [self solverTokens];
}

- (NSArray *)solverTokens
{
    NSArray *solverTokens =  @[
                               [CYRToken tokenWithName:@"html_tags"
                                            expression:@"(<\\/?[a-z][^<>]*>)"
                                            attributes:@{
                                                         NSForegroundColorAttributeName : RGB(25, 0, 151)}],
                               [CYRToken tokenWithName:@"a_tag"
                                            expression:@"(<\\/?[a][^<>]*>)"
                                            attributes:@{
                                                         NSForegroundColorAttributeName : RGB(24, 104, 10)}],
                               [CYRToken tokenWithName:@"img_tag"
                                            expression:@"(<\\/?[img][^<>]*>)"
                                            attributes:@{
                                                         NSForegroundColorAttributeName : RGB(148, 0, 151)}],
                               [CYRToken tokenWithName:@"html_entities"
                                            expression:@"(&#34;|&#38;|&#39;|&#60;|&#62;|&#160;|&#161;|&#162;|&#163;|&#164;|&#165;|&#166;|&#167;|&#168;|&#169;|&#170;|&#171;|&#172;|&#173;|&#174;|&#175;|&#176;|&#177;|&#178;|&#179;|&#180;|&#181;|&#182;|&#183;|&#184;|&#185;|&#186;|&#187;|&#188;|&#189;|&#190;|&#191;|&#192;|&#193;|&#194;|&#195;|&#196;|&#197;|&#198;|&#199;|&#200;|&#201;|&#202;|&#203;|&#204;|&#205;|&#206;|&#207;|&#208;|&#209;|&#210;|&#211;|&#212;|&#213;|&#214;|&#215;|&#216;|&#217;|&#218;|&#219;|&#220;|&#221;|&#222;|&#223;|&#224;|&#225;|&#226;|&#227;|&#228;|&#229;|&#230;|&#231;|&#232;|&#233;|&#234;|&#235;|&#236;|&#237;|&#238;|&#239;|&#240;|&#241;|&#242;|&#243;|&#244;|&#245;|&#246;|&#247;|&#248;|&#249;|&#250;|&#251;|&#252;|&#253;|&#254;|&#255;|&#338;|&#339;|&#352;|&#353;|&#376;|&#402;|&#710;|&#732;|&#913;|&#914;|&#915;|&#916;|&#917;|&#918;|&#919;|&#920;|&#921;|&#922;|&#923;|&#924;|&#925;|&#926;|&#927;|&#928;|&#929;|&#931;|&#932;|&#933;|&#934;|&#935;|&#936;|&#937;|&#945;|&#946;|&#947;|&#948;|&#949;|&#950;|&#951;|&#952;|&#953;|&#954;|&#955;|&#956;|&#957;|&#958;|&#959;|&#960;|&#961;|&#962;|&#963;|&#964;|&#965;|&#966;|&#967;|&#968;|&#969;|&#977;|&#978;|&#982;|&#8194;|&#8195;|&#8201;|&#8204;|&#8205;|&#8206;|&#8207;|&#8211;|&#8212;|&#8216;|&#8217;|&#8218;|&#8220;|&#8221;|&#8222;|&#8224;|&#8225;|&#8226;|&#8230;|&#8240;|&#8242;|&#8243;|&#8249;|&#8250;|&#8254;|&#8260;|&#8364;|&#8465;|&#8472;|&#8476;|&#8482;|&#8501;|&#8592;|&#8593;|&#8594;|&#8595;|&#8596;|&#8629;|&#8656;|&#8657;|&#8658;|&#8659;|&#8660;|&#8704;|&#8706;|&#8707;|&#8709;|&#8711;|&#8712;|&#8713;|&#8715;|&#8719;|&#8721;|&#8722;|&#8727;|&#8730;|&#8733;|&#8734;|&#8736;|&#8743;|&#8744;|&#8745;|&#8746;|&#8747;|&#8756;|&#8764;|&#8773;|&#8776;|&#8800;|&#8801;|&#8804;|&#8805;|&#8834;|&#8835;|&#8836;|&#8838;|&#8839;|&#8853;|&#8855;|&#8869;|&#8901;|&#8968;|&#8969;|&#8970;|&#8971;|&#9001;|&#9002;|&#9674;|&#9824;|&#9827;|&#9829;|&#9830;|&quot;|&amp;|&apos;|&lt;|&gt;|&nbsp;|&iexcl;|&cent;|&pound;|&curren;|&yen;|&brvbar;|&sect;|&uml;|&copy;|&ordf;|&laquo;|&not;|&shy;|&reg;|&macr;|&deg;|&plusmn;|&sup2;|&sup3;|&acute;|&micro;|&para;|&middot;|&cedil;|&sup1;|&ordm;|&raquo;|&frac14;|&frac12;|&frac34;|&iquest;|&Agrave;|&Aacute;|&Acirc;|&Atilde;|&Auml;|&Aring;|&AElig;|&Ccedil;|&Egrave;|&Eacute;|&Ecirc;|&Euml;|&Igrave;|&Iacute;|&Icirc;|&Iuml;|&ETH;|&Ntilde;|&Ograve;|&Oacute;|&Ocirc;|&Otilde;|&Ouml;|&times;|&Oslash;|&Ugrave;|&Uacute;|&Ucirc;|&Uuml;|&Yacute;|&THORN;|&szlig;|&agrave;|&aacute;|&acirc;|&atilde;|&auml;|&aring;|&aelig;|&ccedil;|&egrave;|&eacute;|&ecirc;|&euml;|&igrave;|&iacute;|&icirc;|&iuml;|&eth;|&ntilde;|&ograve;|&oacute;|&ocirc;|&otilde;|&ouml;|&divide;|&oslash;|&Ugrave;|&Uacute;|&Ucirc;|&Uuml;|&yacute;|&thorn;|&yuml;|&OElig;|&oelig;|&Scaron;|&scaron;|&Yuml;|&fnof;|&circ;|&tilde;|&Alpha;|&Beta;|&Gamma;|&Delta;|&Epsilon;|&Zeta;|&Eta;|&Theta;|&Iota;|&Kappa;|&Lambda;|&Mu;|&Nu;|&Xi;|&Omicron;|&Pi;|&Rho;|&Sigma;|&Tau;|&Upsilon;|&Phi;|&Chi;|&Psi;|&Omega;|&alpha;|&beta;|&gamma;|&delta;|&epsilon;|&zeta;|&eta;|&theta;|&iota;|&kappa;|&lambda;|&mu;|&nu;|&xi;|&omicron;|&pi;|&rho;|&sigmaf;|&sigma;|&tau;|&upsilon;|&phi;|&chi;|&psi;|&omega;|&thetasym;|&Upsih;|&piv;|&ensp;|&emsp;|&thinsp;|&zwnj;|&zwj;|&lrm;|&rlm;|&ndash;|&mdash;|&lsquo;|&rsquo;|&sbquo;|&ldquo;|&rdquo;|&bdquo;|&dagger;|&Dagger;|&bull;|&hellip;|&permil;|&prime;|&Prime;|&lsaquo;|&rsaquo;|&oline;|&frasl;|&euro;|&image;|&weierp;|&real;|&trade;|&alefsym;|&larr;|&uarr;|&rarr;|&darr;|&harr;|&crarr;|&lArr;|&UArr;|&rArr;|&dArr;|&hArr;|&forall;|&part;|&exist;|&empty;|&nabla;|&isin;|&notin;|&ni;|&prod;|&sum;|&minus;|&lowast;|&radic;|&prop;|&infin;|&ang;|&and;|&or;|&cap;|&cup;|&int;|&there4;|&sim;|&cong;|&asymp;|&ne;|&equiv;|&le;|&ge;|&sub;|&sup;|&nsub;|&sube;|&supe;|&oplus;|&otimes;|&perp;|&sdot;|&lceil;|&rceil;|&lfloor;|&rfloor;|&lang;|&rang;|&loz;|&spades;|&clubs;|&hearts;|&diams;)"
                                            attributes:@{
                                                         NSForegroundColorAttributeName : [UIColor blackColor],
                                                         NSFontAttributeName: self.boldFont,
                                                         NSDocumentTypeDocumentAttribute: NSPlainTextDocumentType}],
                               [CYRToken tokenWithName:@"comment"
                                            expression:@"<!--(.*?)-->"
                                            attributes:@{
                                                         NSForegroundColorAttributeName : RGB(31, 131, 0),
                                                         NSFontAttributeName : self.italicFont
                                                         }],
                               [CYRToken tokenWithName:@"attributes"
                                            expression:@"(?<==)('|\").*?\\1(?=.*?>)"
                                            attributes:@{
                                                         NSForegroundColorAttributeName : RGB(0, 12, 255)
                                                         }]
                               ];
    
    return solverTokens;
}


#pragma mark - Cleanup

- (void)dealloc {
    [self removeObserver:self forKeyPath:NSStringFromSelector(@selector(defaultFont))];
    [self removeObserver:self forKeyPath:NSStringFromSelector(@selector(boldFont))];
    [self removeObserver:self forKeyPath:NSStringFromSelector(@selector(italicFont))];
}


#pragma mark - KVO

- (void)observeValueForKeyPath:(NSString *)keyPath ofObject:(id)object change:(NSDictionary *)change context:(void *)context
{

    if ([keyPath isEqualToString:NSStringFromSelector(@selector(defaultFont))] ||
        [keyPath isEqualToString:NSStringFromSelector(@selector(boldFont))] ||
        [keyPath isEqualToString:NSStringFromSelector(@selector(italicFont))])
    {
        // Reset the tokens, this will clear any existing formatting
        self.tokens = [self solverTokens];
    }
    else
    {
        [super observeValueForKeyPath:keyPath ofObject:object change:change context:context];
    }
}


#pragma mark - Overrides

- (void)setDefaultFont:(UIFont *)defaultFont
{
    _defaultFont = defaultFont;
    self.font = defaultFont;
}


@end
