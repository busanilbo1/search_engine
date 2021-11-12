package com.filab.open.search.util;

public class Chosung {
	
	public static void main( String args[] ) throws Exception
    {
		
    }
	public static String getInitial(String text) {
		// 초성 19자 final 
		String[] initialChs = { "ㄱ", "ㄲ", "ㄴ", "ㄷ", "ㄸ", "ㄹ", "ㅁ", "ㅂ", "ㅃ", "ㅅ", "ㅆ", "ㅇ", "ㅈ", "ㅉ", "ㅊ", "ㅋ", "ㅌ", "ㅍ", "ㅎ" }; 
		// 중성 21자
		final String[] medialChs = { "ㅏ", "ㅐ", "ㅑ", "ㅒ", "ㅓ", "ㅔ", "ㅕ", "ㅖ", "ㅗ", "ㅘ", "ㅙ", "ㅚ", "ㅛ", "ㅜ", "ㅝ", "ㅞ", "ㅟ", "ㅠ", "ㅡ", "ㅢ", "ㅣ" }; 
		// 종성 없는 경우 포함하여 28자 
		final String[] finalChs = { " ", "ㄱ", "ㄲ", "ㄳ", "ㄴ", "ㄵ", "ㄶ", "ㄷ", "ㄹ", "ㄺ", "ㄻ", "ㄼ", "ㄽ", "ㄾ", "ㄿ", "ㅀ", "ㅁ", "ㅂ", "ㅄ", "ㅅ", "ㅆ", "ㅇ", "ㅈ", "ㅊ", "ㅋ", "ㅌ", "ㅍ", "ㅎ" }; 
		// 19: 초성 // 21: 중성 // 28: 종성 
		if(text.length() > 0) {
			char chName = text.charAt(0); 
			if(chName >= 0xAC00 && chName <= 0xD7A3) { 
				// 0xAC00(가) ~ 0xD7A3(힣) 
				int uniVal = chName - 0xAC00; 
				int initialCh = ((uniVal) / (21 * 28)); 
				// 초성 index 
				System.out.println(initialChs[initialCh]);
				// 중성 
				int medialCh = ((uniVal % (28 * 21)) / 28); 
				System.out.println(medialChs[medialCh]); 
				// 종성 
				int finalCh = ((uniVal % 28)); 
				System.out.println(finalChs[finalCh]); 
				return initialChs[initialCh]; 
				} else { 
					return ""+chName; 
				} 
			}else { 
				return "";
			}
			
	}
	
}

