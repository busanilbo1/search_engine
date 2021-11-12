/*******************************************************************************
 * KOMORAN 3.0 - Korean Morphology Analyzer
 *
 * Copyright 2015 Shineware http://www.shineware.co.kr
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 * 	http://www.apache.org/licenses/LICENSE-2.0
 * 	
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package kr.co.shineware.nlp.komoran.test;

import kr.co.shineware.nlp.komoran.core.Komoran;
import kr.co.shineware.nlp.komoran.model.KomoranResult;
import kr.co.shineware.nlp.komoran.model.Token;

import java.util.List;

public class KomoranTest {

	public static void main(String[] args) throws Exception {

		//����� ���� ������ euc-kr�� �ۼ��Ǿ����� �Ѵ�.
		Komoran komoran = new Komoran("D:\\dic\\models_full");
		komoran.setFWDic("D:\\dic\\user_data/fwd.user");
		komoran.setUserDic("D:\\dic\\user_data/dic.user");

		String input = "������ȭ�� �庴�� �ǿ��� �ݰ������κ��� ������� �ڷῡ ������, �������غ���簡 �Ƿ��� �Ƿ��ڹ��Ǽ��� 2014�⵵ ��� ������ 2�� �Ѱ� �����߰� �Ƿ��ڹ��� �Ƿ��� ����� ���� �Ѵ� �������� ����� ������ �ź��� ������ ��Ÿ����";
		KomoranResult analyzeResultList = komoran.analyze(input);
		List<Token> tokenList = analyzeResultList.getTokenList();

		//print each tokens by getTokenList()
		//System.out.println("==========print 'getTokenList()'==========");
		for (Token token : tokenList) {
			
			if(token.getPos().toString().equalsIgnoreCase("NNG") 
					|| token.getPos().toString().equalsIgnoreCase("NNP")
					|| token.getPos().toString().equalsIgnoreCase("NNB")
					|| token.getPos().toString().equalsIgnoreCase("NR")
					|| token.getPos().toString().equalsIgnoreCase("NP")
					|| token.getPos().toString().equalsIgnoreCase("VV")
					|| token.getPos().toString().equalsIgnoreCase("SH")
					|| token.getPos().toString().equalsIgnoreCase("SL")
					|| token.getPos().toString().equalsIgnoreCase("SN")){
				System.out.println(token.getMorph() + "//" + token.getPos());
			}
			//System.out.println(token.getMorph()+"/"+token.getPos()+"("+token.getBeginIndex()+","+token.getEndIndex()+")");
			//System.out.println();
		}
		//print nouns
		System.out.println("==========print 'getNouns()'==========");
		System.out.println(analyzeResultList.getNouns());
		System.out.println();
		System.out.println("==========print 'getPlainText()'==========");
		System.out.println(analyzeResultList.getPlainText());
		//System.out.println();
		//System.out.println("==========print 'getList()'==========");
		//System.out.println(analyzeResultList.getList());
	}
}
