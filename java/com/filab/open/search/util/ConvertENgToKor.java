package com.filab.open.search.util;

import java.io.UnsupportedEncodingException;

public class ConvertENgToKor {
	
	static enum CodeType { chosung, jungsung, jongsung }
	 static String ignoreChars = "`1234567890-=[]\\;',./~!@#$%^&*()_+{}|:\"<>? "; 
	 /** * 영어를 한글로... */ 
	 public static String engToKor(String eng) { 
	 StringBuffer sb = new StringBuffer(); 
	 int initialCode = 0, medialCode = 0, finalCode = 0; 
	 int tempMedialCode, tempFinalCode; 
	 for (int i = 0; i < eng.length(); i++) { 
		 // 숫자특수문자 처리
			 if(ignoreChars.indexOf(eng.substring(i, i + 1)) > -1){
				System.out.println(eng.substring(i, i + 1));
			 sb.append(eng.substring(i, i + 1)); 
			 continue;
		 } // 초성코드 추출 
			 
		 initialCode = getCode(CodeType.chosung, eng.substring(i, i + 1));
		 i++; 
		 // 다음문자로 // 중성코드 추출 
		 tempMedialCode = getDoubleMedial(i, eng);
		 
		 // 두 자로 이루어진 중성코드 추출 
		 if (tempMedialCode != -1) { 
			 medialCode = tempMedialCode; i += 2;
		 } else { // 없다면,
			 medialCode = getSingleMedial(i, eng); 
			 // 한 자로 이루어진 중성코드 추출
			 i++; 
		 } // 종성코드 추출 
		 
			 tempFinalCode = getDoubleFinal(i, eng);
			 // 두 자로 이루어진 종성코드 추출
			 if (tempFinalCode != -1) { 
				 finalCode = tempFinalCode; // 그 다음의 중성 문자에 대한 코드를 추출한다.
				 tempMedialCode = getSingleMedial(i + 2, eng);
				 if (tempMedialCode != -1) { // 코드 값이 있을 경우 
					 finalCode = getSingleFinal(i, eng); // 종성 코드 값을 저장한다.
				 } else { 
					 i++;
				 } 
			 } else { // 코드 값이 없을 경우 , 
				 tempMedialCode = getSingleMedial(i + 1, eng); // 그 다음의 중성 문자에 대한 코드 추출. 
				 if (tempMedialCode != -1) { // 그 다음에 중성 문자가 존재할 경우, 
					 finalCode = 0; // 종성 문자는 없음.
					 i--;
				 } else { 
					 finalCode = getSingleFinal(i, eng); // 종성 문자 추출 
					 if (finalCode == -1){ 
						 finalCode = 0; 
						 i--; // 초성,중성 + 숫자,특수문자, //기호가 나오는 경우 index를 줄임.
					 } 
				 } 
			 } // 추출한 초성 문자 코드, //중성 문자 코드, 종성 문자 코드를 합한 후 변환하여 스트링버퍼에 넘김 
			 
			 sb.append((char) (0xAC00 + initialCode + medialCode + finalCode));
		 } 
	 	return sb.toString();
	 } 
	 /** * 해당 문자에 따른 코드를 추출한다. * * @param type * 초성 : chosung, 중성 : jungsung, 종성 : jongsung 구분 * @param char 해당 문자 */ 
	 static private int getCode(CodeType type, String c) { 
	 // 초성  
		 String init = "rRseEfaqQtTdwWczxvg"; 
	 // 중성 
	 String[] mid = { "k", "o", "i", "O", "j", "p", "u", "P", "h", "hk", "ho", "hl", "y", "n", "nj", "np", "nl", "b", "m", "ml", "l" };
	 // 종성
	 String[] fin = { "r", "R", "rt", "s", "sw", "sg", "e", "f", "fr", "fa", "fq", "ft", "fx", "fv", "fg", "a", "q", "qt", "t", "T", "d", "w", "c", "z", "x", "v", "g" };
	 switch (type) { 
	 	case chosung: int index = init.indexOf(c); 
		 if (index != -1) { 
			 return index * 21 * 28;
		 } 
	 	break; 
	 	case jungsung: for (int i = 0; i < mid.length; i++) {
			 if (mid[i].equals(c)) {
				 return i * 28;
			 } 
	 	} 
	 	break; 
		 case jongsung: for (int i = 0; i < fin.length; i++) { 
			 if (fin[i].equals(c)) { 
				 return i + 1;
			 } 
		 } 
	 	break; 
	 	default: System.out.println("잘못된 타입 입니다");
	 } 
	 	return -1; 
	 } // 한 자로 된 중성값을 리턴한다 // 인덱스를 벗어낫다면 -1을 리턴 
	 static private int getSingleMedial(int i, String eng) { 
		 if ((i + 1) <= eng.length()) { 
			 return getCode(CodeType.jungsung, eng.substring(i, i + 1));
		 } else { 
			 return -1;
		 } 
	 } 
	 // 두 자로 된 중성을 체크하고, 있다면 값을 리턴한다. // 없으면 리턴값은 -1 
	 static private int getDoubleMedial(int i, String eng) {
		 int result; 
		 if ((i + 2) > eng.length()) { 
			 return -1;
		 } else {
			 result = getCode(CodeType.jungsung, eng.substring(i, i + 2)); 
			 if (result != -1) {
				 return result;
			 } else { 
				 return -1;
			 }
		 } 
	 } 
	 // 한 자로된 종성값을 리턴한다 // 인덱스를 벗어낫다면 -1을 리턴 
	 static private int getSingleFinal(int i, String eng) { 
		 if ((i + 1) <= eng.length()) { 
			 return getCode(CodeType.jongsung, eng.substring(i, i + 1)); 
		 } else { 
			 return -1;
		 } 
	 } 
	 // 두 자로된 종성을 체크하고, 있다면 값을 리턴한다. // 없으면 리턴값은 -1 
	 	static private int getDoubleFinal(int i, String eng) { 
		 if ((i + 2) > eng.length()) { 
			 return -1; 
		 } else {
			 return getCode(CodeType.jongsung, eng.substring(i, i + 2));
		 } 
	 } 
	 public static void main(String[] args) { 
		 String ins = "rudskatlsan의 문제점dmf vkdkrgodigksek.";
		 String[] in_str = ins.split("[ ]");
		 String engtokor_str = "";
		 
		 boolean eng_include_flag = false;
		 
		 for(int i=0;i<in_str.length;i++) {
			 if(in_str[i] != null) {
				 
				 String word[] = in_str[i].toString().split(""); 
				 
				 String hangul_str = "";
				 String eng_str = "";
				 String merge_str = "";
				 boolean han_eng_flag = true;
				 for(int j=0; j<word.length; j++){
					 
					
						//System.out.println(word[i]+" : "+getWordType(word[i]));
						
						if(j==0) {
							if(getWordType(word[j]).equals("K")) {
								han_eng_flag = true;
							}
							if(getWordType(word[j]).equals("E")) {
								han_eng_flag = false;
							}
						}
						if(getWordType(word[j]).equals("K")) {
							hangul_str = hangul_str + word[j];
						}
						if(getWordType(word[j]).equals("E")) {
							eng_str = eng_str + word[j];
							
							eng_include_flag = true;
						}
						
				  }
				 
				 
				 
				 String in_word = "";
				 //영문만 입력 처리 한다.
				 String re_str = "";
				 if(eng_str != null && eng_str.trim().length() > 0) {
					 
				 
					  re_str = StringReplaceRe(ConvertENgToKor.engToKor(eng_str.toString()));
					 if(re_str != null && re_str.trim().length() > 0) {
						 re_str = re_str;
					 }else {
						 re_str = in_str[i].toString();
					 }
					 
					 
				 }
				 
				 if(han_eng_flag) { //처음 시작이 한글일 경우
					 merge_str = hangul_str+""+re_str;
				 }else {
					 merge_str = re_str+""+hangul_str;
				 }
			 
			
			 
				 engtokor_str = engtokor_str + merge_str + " ";
				
			 }
		 }
		 
		 if(!eng_include_flag) {
			 engtokor_str = ins;
			 System.out.println("영문 없음 : "+engtokor_str); 
		 }else {
			 System.out.println("영문 있음 : "+engtokor_str); 
		 }
		
	 }
	 
	 public static String StringReplaceRe(String str){       
	      String match = "[^\\uAC00-\\uD7A30-9a-zA-Z\\u3131-\\u3163\\\\s]";
	      str =str.replaceAll(match, "");
	      return str;
	   }
	 
	 public static String getWordType(String word){				
			//문자열 판별을 위한 변수 선언 실시
			String data = "";
			boolean number = false;
			boolean english = false;
			boolean korean = false;
			boolean special = false;

			//for 반목문을 수행해 문자열을 한글자씩 분리해 decimal로 변환 후 문자열 판별 실시		
			for(int i=0; i<word.length(); i++){
				int value = word.charAt(i);
				if(value >= 48 && value <= 57){ //숫자
					number = true;
				}
				else if(value >= 65 && value <= 90 //대문자 
						|| value >= 97 && value <= 122){ //소문자
					english = true;
				}
				else if(value >= 0 && value <= 47
						|| value >= 58 && value <= 64
						|| value >= 91 && value <= 96
						|| value >= 123 && value <= 127) { //특수문자
					special = true;
				}
				else{ //한글
					korean = true;
				}
			}
		      
			if(number){
				data = "N";		        
			}
			if(english){
				data = "E";			
			}
			if(korean){
				data = "K";			
			}
			if(special) {
				data = "S";
			}
			return data;
		}
	 
	 
}
