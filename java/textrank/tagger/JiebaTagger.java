/**
 * 
 */
package textrank.tagger;

import java.util.List;

import textrank.JiebaSegmenter2;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.huaban.analysis.jieba.SegToken;


public class JiebaTagger implements Tagger {

	private JiebaSegmenter2 segmenter;
	
	public JiebaTagger() {
		segmenter = new JiebaSegmenter2();
	}
	
	@Override
	public List<Term> seg(String text) {

		return Lists.transform(segmenter.process(text, JiebaSegmenter2.SegMode.INDEX), new Function<SegToken, Term>() {
			@Override
			public Term apply(SegToken input) {
				Term t = new Term();
				
				t.setText(input.word.toString());
				t.setPos("Noun");
				
				//System.out.println(input.word.toString()+"//"+input.startOffset);
				return t;
			}
		});
	}
}
