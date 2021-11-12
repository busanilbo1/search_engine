/**
 * 
 */
package textrank;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import textrank.pagerank.Node;
import textrank.pagerank.UndirectWeightedGraph;
import textrank.tagger.JiebaTagger;
//import textrank.tagger.JiebaTagger;
import textrank.tagger.Term;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * @author zhangcheng
 * 
 */
public class KeywordsExtractor {


    public static void main(String[] args) throws Exception {

        Set<String> filt = Sets.newHashSet();
        filt.add("n");
       // filt.add("v");
      //  filt.add("nt");
       // filt.add("nz");

        String text = "민주 평화 당 장병완 의원 금감원 제출 받 자료 따르 생명 손해보험 사가 의뢰 의료 자문 건수 2014  장병완 년대 대비 지난해 2 배 넘 증가 의료 자문 의뢰 사례 절반 넘 수준 보험금 지급 거부 것 나타나  민주 평화 당 장병완";
        // 1.build text graph

        // 1.1 get tokens
      //  List<Term> terms = new JiebaTagger().seg(text);
        
        List<Term> terms = new JiebaTagger().seg(text);
        /*
         * 
         Term t = new Term();
				t.setText(input.word.getToken());
				t.setPos(input.word.getTokenType());
         */
        
        // 1.2 filt pos
        Term[] filtTerms = Lists.newArrayList(terms.stream().filter(t -> {
            return filt.contains(t.getPos());
        }).iterator()).toArray(new Term[0]);
        
        for (int i = 0; i < filtTerms.length; ++i ) {
        	//System.out.println(filtTerms[i].getText());
        }
        // 1.3 build graph
        CounterMap cm = new CounterMap();

        int span = 5;

        for (int i = 0; i < filtTerms.length; ++i ) {
            for (int j = i + 1; j < i + span && j < filtTerms.length; ++j) {
            	
            //	System.out.println(filtTerms[i].getText() + ":" + filtTerms[j].getText());
                cm.incr(filtTerms[i].getText() + ":" + filtTerms[j].getText());
            }
        }
        
        // TODO we can do more optimization as follow: 1) pos 2) stopwords 3) rational span
        UndirectWeightedGraph uwg = new UndirectWeightedGraph();
        for (String pair : cm.countAll().keySet()) {
			//System.out.println(pair + "\t" + cm.get(pair));
            String[] segs = Lists.newArrayList(Splitter.on(":").split(pair)).toArray(new String[0]);
            
            //System.out.println(segs[0]+"---"+segs[1] + "-----" + pair);
            
            uwg.addNode(segs[0], segs[1], cm.get(pair));
        }
        // 2. rank
        uwg.rank();
        List<Node> list = uwg.topk(10);
        list.forEach(t -> {
            System.out.println(t.label + "----->" + t.rank);
        });
    }
}
