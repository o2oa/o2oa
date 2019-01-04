package jiguang.chat.utils.query;

import android.text.TextUtils;

public final class TextSearcher {
	/** T9 */
	private boolean mT9;

	/** string */
	private String mStr;

	//
	// string state
	// 

	/** string index */
	private int mIndex;

	/** eaten state */
	private boolean mEaten;
	
	// 
	// PinYin state
	// 
	
	/** PinYin */
	private String mPinyin;

	/** string index after PinYin */
	private int mIndexP;
	
	/** PinYin sub index */
	private int mIndexSub;

	/** T9 characters */
	private static final char[] T9 = { '2', '2', '2', '3', '3', '3', '4', '4',
			'4', '5', '5', '5', '6', '6', '6', '7', '7', '7', '7', '8', '8',
			'8', '9', '9', '9', '9' };

	/** searcher */
	private static final ThreadLocal<TextSearcher> sSearcher = new ThreadLocal<TextSearcher>() {
	    protected TextSearcher initialValue() {
	        return new TextSearcher();
	    }
	};
	
	/**
	 * 
	 * @param t9
	 * @return TextSearcher
	 */
	public static final TextSearcher obtain(boolean t9) {
		TextSearcher searcher = sSearcher.get();
	
		searcher.mT9 = t9;
		
		return searcher;
	}
	
	/**
	 * 
	 * @param s
	 * @param i
	 */
	public final void initialize(String s, int i) {
		mStr = s;
		
		mIndex = i;
		mEaten = true;

		mPinyin = null;
		mIndexP = -1;
		mIndexSub = -1;
	}
	
	/**
	 * 
	 * @return last index
	 */
	public final int index() {
		return mIndex;
	}
	
	/**
	 * 
	 * @param eat assuming in lower case or [0-9]
	 * @return eaten
	 */
	public final boolean eat(char eat) {
		//
		// PinYin
		// 
		
		boolean pEaten = false;
		boolean pEnd = false;
		
		// on
		if (mPinyin != null) {
			// compare then move
			pEaten = mPinyin.charAt(mIndexSub++) == eat;
			pEnd = mIndexSub == mPinyin.length();
							
			// not eaten or reach the end
			if (!pEaten || pEnd) {
				// close
				mPinyin = null;
			}
		}

		//
		// string
		// 
		
		String pinyin = null;
		boolean eaten = false;
		
		// on && in bound
		if (mEaten && mIndex < mStr.length()) {
			char chr = mStr.charAt(mIndex);
			
			if (mT9) {
		        if ('a' <= chr && chr <= 'z') {
		        	eaten = T9[chr - 'a'] == eat;
		        } else if ('A' <= chr && chr <= 'Z') {
		        	eaten = T9[chr - 'A'] == eat;
		        } else {
			        eaten = chr == eat;
		        }
			} else {
		        if ('A' <= chr && chr <= 'Z') {
			        eaten = chr + 'a' - 'A' == eat;
		        } else {
			        eaten = chr == eat;
		        }
			}
			
	        // PinYin
	        if (!eaten) {
	        	pinyin = mT9 ? PinYin.getPinYinT9(chr) : PinYin.getPinYin(chr);
	        
	        	// has
	        	if (pinyin != null) {
	        		// equals
	        		if (pinyin.charAt(0) == eat) {
	        			eaten = true;
	        		} else {
	        			// clear
	        			pinyin = null;
	        		}
	        	}
	        }
		}
		
		// fail
		if (!pEaten && !eaten) {
			return false;
		}
		
		// string
		if (eaten) {
			// next
			mIndex++;
			
			// PinYin fail
			if (!pEaten) {
				// clear
				mPinyin = null;
				
				// setup
				if (pinyin != null && pinyin.length() > 1) {
					mPinyin = pinyin;
					// first has done
					mIndexSub = 1;
				
					// at next
					mIndexP = mIndex;
				}
			}
		} else {
			//
			// PinYin here
			// 
			
			// reach the end
			if (pEnd) {
				// previous index done
				eaten = true;
				mIndex = mIndexP;			
			}
		}
		
		// save last
		mEaten = eaten;
		
        return true;
	}
	
	/**
	 * 
	 * @param t9
	 * @param str
	 * @param query assuming in lower case or [0-9]
	 * @return range array or NULL
	 */
	public static final int[] indexOf(boolean t9, String str, String query) {
		if (TextUtils.isEmpty(str) || TextUtils.isEmpty(query)) {
			return null;
		}
		
		TextSearcher searcher = TextSearcher.obtain(t9);
		
		// move
EAT:	for (int index = 0; index < str.length(); index++) {
			searcher.initialize(str, index);
	
			for (int subIndex = 0; subIndex < query.length(); subIndex++) {
				if (!searcher.eat(query.charAt(subIndex))) {
					// next
					continue EAT;
				}
			}
		  
			// eaten
			return new int[] {index, searcher.index()};
		}
		
		return null;
	}
	
	/**
	 * 
	 * @param t9
	 * @param str
	 * @param query assuming in lower case or [0-9]
	 * @return contains
	 */
	public static final boolean contains(boolean t9, String str, String query) {
		if (TextUtils.isEmpty(str) || TextUtils.isEmpty(query)) {
			return false;
		}
		
		TextSearcher searcher = TextSearcher.obtain(t9);
		
		// move
EAT:	for (int index = 0; index < str.length(); index++) {
			searcher.initialize(str, index);
	
			for (int subIndex = 0; subIndex < query.length(); subIndex++) {
				if (!searcher.eat(query.charAt(subIndex))) {
					// next
					continue EAT;
				}
			}
		  
			// eaten
			return true;
		}
		
		return false;
	}
	
	/**
	 * 
	 * @param t9
	 * @param str
	 * @param query assuming in lower case or [0-9]
	 * @return last index or -1
	 */
	public static final int startsWith(boolean t9, String str, String query) {
		if (TextUtils.isEmpty(str) || TextUtils.isEmpty(query)) {
			return -1;
		}
		
		TextSearcher searcher = TextSearcher.obtain(t9);
		
		searcher.initialize(str, 0);
		
		for (int subIndex = 0; subIndex < query.length(); subIndex++) {
			if (!searcher.eat(query.charAt(subIndex))) {
				// fail
				return -1;
			}
		}
		
		return searcher.index();
	}
}