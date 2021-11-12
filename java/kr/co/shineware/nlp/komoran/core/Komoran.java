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
package kr.co.shineware.nlp.komoran.core;

import kr.co.shineware.ds.aho_corasick.FindContext;
import kr.co.shineware.nlp.komoran.constant.DEFAULT_MODEL;
import kr.co.shineware.nlp.komoran.constant.FILENAME;
import kr.co.shineware.nlp.komoran.constant.SCORE;
import kr.co.shineware.nlp.komoran.constant.SYMBOL;
import kr.co.shineware.nlp.komoran.core.model.ContinuousSymbolInfo;
import kr.co.shineware.nlp.komoran.core.model.Lattice;
import kr.co.shineware.nlp.komoran.core.model.LatticeNode;
import kr.co.shineware.nlp.komoran.core.model.Resources;
import kr.co.shineware.nlp.komoran.corpus.parser.CorpusParser;
import kr.co.shineware.nlp.komoran.corpus.parser.model.ProblemAnswerPair;
import kr.co.shineware.nlp.komoran.model.KomoranResult;
import kr.co.shineware.nlp.komoran.model.MorphTag;
import kr.co.shineware.nlp.komoran.model.ScoredTag;
import kr.co.shineware.nlp.komoran.model.Tag;
import kr.co.shineware.nlp.komoran.modeler.model.IrregularNode;
import kr.co.shineware.nlp.komoran.modeler.model.Observation;
import kr.co.shineware.nlp.komoran.parser.KoreanUnitParser;
import kr.co.shineware.nlp.komoran.util.KomoranCallable;
import kr.co.shineware.util.common.file.FileUtil;
import kr.co.shineware.util.common.model.Pair;
import kr.co.shineware.util.common.string.StringUtil;

import java.io.*;
import java.lang.Character.UnicodeBlock;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

public class Komoran implements Cloneable {

    private Resources resources;
    private Observation userDic;
    private KoreanUnitParser unitParser;

    private HashMap<String, List<Pair<String, String>>> fwd;

    public Komoran(String modelPath) {
        this.resources = new Resources();
        this.load(modelPath);
        this.unitParser = new KoreanUnitParser();
    }

    public Komoran(DEFAULT_MODEL modelType) {

        this.resources = new Resources();
        this.resources.init();
        String modelPath;
        if (modelType == DEFAULT_MODEL.FULL) {
            modelPath = FILENAME.FULL_MODEL;
        } else if (modelType == DEFAULT_MODEL.LIGHT) {
            modelPath = FILENAME.LIGHT_MODEL;
        } else {
            modelPath = FILENAME.FULL_MODEL;
        }

        String delimiter = "/";
        InputStream posTableFile =
            this.getResourceStream(String.join(delimiter, modelPath, FILENAME.POS_TABLE));
        InputStream irrModelFile =
            this.getResourceStream(String.join(delimiter, modelPath, FILENAME.IRREGULAR_MODEL));
        InputStream observationFile =
            this.getResourceStream(String.join(delimiter, modelPath, FILENAME.OBSERVATION));
        InputStream transitionFile =
            this.getResourceStream(String.join(delimiter, modelPath, FILENAME.TRANSITION));

        this.resources.loadPosTable(posTableFile);
        this.resources.loadIrregular(irrModelFile);
        this.resources.loadObservation(observationFile);
        this.resources.loadTransition(transitionFile);
        this.unitParser = new KoreanUnitParser();
    }

    private InputStream getResourceStream(String path) {
        return getClass().getClassLoader().getResourceAsStream(path);
    }

    public void analyzeTextFile(String inputFilename, String outputFilename, int thread) {

        try {
            List<String> lines = FileUtil.load2List(inputFilename);

            BufferedWriter bw = new BufferedWriter(new FileWriter(outputFilename));
            List<Future<KomoranResult>> komoranResultList = new ArrayList<>();
            ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(thread);

            for (String line : lines) {
                KomoranCallable komoranCallable = new KomoranCallable(this, line);
                komoranResultList.add(executor.submit(komoranCallable));
            }

            for (Future<KomoranResult> komoranResultFuture : komoranResultList) {
                KomoranResult komoranResult = komoranResultFuture.get();
                bw.write(komoranResult.getPlainText());
                bw.newLine();
            }
            bw.close();
            executor.shutdown();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public KomoranResult analyze(String sentence) {

        FindContext<List<ScoredTag>> observationFindContext;
        FindContext<List<IrregularNode>> irregularFindContext;
        FindContext<List<ScoredTag>> userDicFindContext = null;

        observationFindContext = this.resources.getObservation().getTrieDictionary().newFindContext();

        irregularFindContext = this.resources.getIrrTrie().getTrieDictionary().newFindContext();
        if (this.userDic != null) {
            userDicFindContext = this.userDic.getTrieDictionary().newFindContext();
        }

        sentence = sentence.replaceAll("[ ]+", " ").trim();

        List<LatticeNode> resultList = new ArrayList<>();

        Lattice lattice;

        lattice = new Lattice(this.resources);
        lattice.setUnitParser(this.unitParser);

      
        ContinuousSymbolInfo continuousSymbolInfo = new ContinuousSymbolInfo();

       
        String jasoUnits = unitParser.parse(sentence);
        List<Pair<Character, KoreanUnitParser.UnitType>> jasoUnitsWithType = unitParser.parseWithType(sentence);

        int length = jasoUnits.length();
        
        int prevStartIdx = 0;
        boolean inserted;

        for (int i = 0; i < length; i++) {
            
            int skipIdx = this.lookupFwd(lattice, jasoUnits, i);
            if (skipIdx != -1) {
                i = skipIdx - 1;
                continue;
            }

            
            if (jasoUnits.charAt(i) == ' ') {
                this.consumeContiniousSymbolParserBuffer(lattice, i, continuousSymbolInfo);
                this.bridgeToken(lattice, i, jasoUnits, prevStartIdx);
                prevStartIdx = i + 1;
            }
            this.continiousSymbolParsing(lattice, jasoUnits.charAt(i), i, continuousSymbolInfo); 
            this.symbolParsing(lattice, jasoUnits.charAt(i), i); 
            this.userDicParsing(lattice, userDicFindContext, jasoUnits.charAt(i), i); 

            this.regularParsing(lattice, observationFindContext, jasoUnits.charAt(i), i); 
            this.irregularParsing(lattice, irregularFindContext, jasoUnits.charAt(i), i);
            this.irregularExtends(lattice, jasoUnits.charAt(i), i); 

        }

        this.consumeContiniousSymbolParserBuffer(lattice, jasoUnits, continuousSymbolInfo);
        lattice.setLastIdx(jasoUnits.length());
        inserted = lattice.appendEndNode();
       
        if (!inserted) {
            double NAPenaltyScore = SCORE.NA;
            if (prevStartIdx != 0) {
                NAPenaltyScore += lattice.getNodeList(prevStartIdx).get(0).getScore();
            }
            String combinedWord = unitParser.combineWithType(jasoUnitsWithType.subList(prevStartIdx, jasoUnits.length()));
            LatticeNode latticeNode = new LatticeNode(prevStartIdx, jasoUnits.length(), new MorphTag(combinedWord, SYMBOL.NA, this.resources.getTable().getId(SYMBOL.NA)), NAPenaltyScore);
            latticeNode.setPrevNodeIdx(0);
            lattice.appendNode(latticeNode);
            lattice.appendEndNode();
        }

        List<LatticeNode> shortestPathList = lattice.findPath();

       
        if (shortestPathList == null) {
            resultList.add(new LatticeNode(0, jasoUnits.length(), new MorphTag(sentence, "NA", -1), SCORE.NA));
        } else {
            Collections.reverse(shortestPathList);
            resultList.addAll(shortestPathList);
        }

        return new KomoranResult(resultList, jasoUnits);
    }

    private void bridgeToken(Lattice lattice, int curIdx, String jasoUnits, int prevBeginSymbolIdx) {


        if (lattice.put(curIdx, curIdx + 1, SYMBOL.END, SYMBOL.END, this.resources.getTable().getId(SYMBOL.END), 0.0)) {
            return;
        }

        //怨듬갚�씠�씪硫� END 湲고샇瑜� �궫�엯
        LatticeNode naLatticeNode = lattice.makeNode(prevBeginSymbolIdx, curIdx, jasoUnits.substring(prevBeginSymbolIdx, curIdx), SYMBOL.NA, this.resources.getTable().getId(SYMBOL.NA), SCORE.NA, 0);

        int naNodeIndex = lattice.appendNode(naLatticeNode);
        LatticeNode endLatticeNode = lattice.makeNode(curIdx, curIdx + 1, SYMBOL.END, SYMBOL.END, this.resources.getTable().getId(SYMBOL.END), 0.0, naNodeIndex);
        lattice.appendNode(endLatticeNode);
    }

    private boolean symbolParsing(Lattice lattice, char jaso, int idx) {

        Character.UnicodeBlock unicodeBlock = Character.UnicodeBlock.of(jaso);
        //�닽�옄
        if (Character.isDigit(jaso)) {
            return false;
        } else if (unicodeBlock == Character.UnicodeBlock.BASIC_LATIN) {
            //�쁺�뼱
            if (((jaso >= 'A') && (jaso <= 'Z')) || ((jaso >= 'a') && (jaso <= 'z'))) {
                return false;
            } else if (this.resources.getObservation().getTrieDictionary().getValue("" + jaso) != null) {
                return false;
            } else if (jaso == ' ') {
                return false;
            }
            //�븘�뒪�궎 肄붾뱶 踰붿쐞 �궡�뿉 �궗�쟾�뿉 �뾾�뒗 寃쎌슦�뿉�뒗 湲고� 臾몄옄
            else {
                lattice.put(idx, idx + 1, "" + jaso, SYMBOL.SW, this.resources.getTable().getId(SYMBOL.SW), SCORE.SW);
                return true;
            }
        }
        //�븳湲�
        else if (unicodeBlock == UnicodeBlock.HANGUL_COMPATIBILITY_JAMO
                || unicodeBlock == UnicodeBlock.HANGUL_JAMO
                || unicodeBlock == UnicodeBlock.HANGUL_JAMO_EXTENDED_A
                || unicodeBlock == UnicodeBlock.HANGUL_JAMO_EXTENDED_B
                || unicodeBlock == UnicodeBlock.HANGUL_SYLLABLES) {
            return false;
        }
        //�씪蹂몄뼱
        else if (unicodeBlock == UnicodeBlock.KATAKANA
                || unicodeBlock == UnicodeBlock.KATAKANA_PHONETIC_EXTENSIONS) {
            return false;
        }
        //以묎뎅�뼱
        else if (UnicodeBlock.CJK_COMPATIBILITY.equals(unicodeBlock)
                || UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS.equals(unicodeBlock)
                || UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A.equals(unicodeBlock)
                || UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B.equals(unicodeBlock)
                || UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS.equals(unicodeBlock)) {
            return false;
        }
        //洹� �쇅 臾몄옄�씤 寃쎌슦
        else {
            lattice.put(idx, idx + 1, "" + jaso, SYMBOL.SW, this.resources.getTable().getId(SYMBOL.SW), SCORE.SW);
            return true;
        }
    }

    private boolean userDicParsing(Lattice lattice, FindContext<List<ScoredTag>> userDicFindContext, char jaso, int curIndex) {
        //TRIE 湲곕컲�쓽 �궗�쟾 寃��깋�븯�뿬 �삎�깭�냼�� �뭹�궗 諛� �뭹�궗 �젏�닔(observation)瑜� �뼸�뼱�샂
        Map<String, List<ScoredTag>> morphScoredTagsMap = this.getMorphScoredTagMapFromUserDic(userDicFindContext, jaso);

        if (morphScoredTagsMap == null) {
            return false;
        }

        //�삎�깭�냼 �젙蹂대쭔 �뼸�뼱�샂
        Set<String> morphes = this.getMorphes(morphScoredTagsMap);

        //媛� �삎�깭�냼�� �뭹�궗 �젙蹂대�� lattice�뿉 �궫�엯
        for (String morph : morphes) {
            int beginIdx = curIndex - morph.length() + 1;
            int endIdx = curIndex + 1;

            //�삎�깭�냼�뿉 ���븳 �뭹�궗 諛� �젏�닔(observation) �젙蹂대�� List �삎�깭濡� 媛��졇�샂
            List<ScoredTag> scoredTags = morphScoredTagsMap.get(morph);
            for (ScoredTag scoredTag : scoredTags) {
                this.insertLattice(lattice, beginIdx, endIdx, morph, scoredTag, scoredTag.getScore());
            }
        }
        return true;
    }

    //TO DO
    //湲곕텇�꽍 �궗�쟾�쓣 �뼱�뼸寃� �쟻�슜�븷 寃껋씤媛�....
    //Lucene�뿉�꽌 �솢�슜�븷 �븣 �씤�뜳�뒪 �젙蹂대�� �뼱�뼸寃� keep �븷 寃껋씤媛�..
    private int lookupFwd(Lattice lattice, String token, int curIdx) {

        if (this.fwd == null) {
            return -1;
        }

        //�쁽�옱 �씤�뜳�뒪媛� �떆�옉�씠嫄곕굹 �씠�쟾 �씤�뜳�뒪媛� 怨듬갚�씤 寃쎌슦 (word �떒�뼱�씤 寃쎌슦)
        //�쁽�옱 �씤�뜳�뒪媛� �삩�쟾�븳 �떒�뼱�쓽 �떆�옉 遺�遺꾩씤 寃쎌슦
        if (curIdx == 0 || token.charAt(curIdx - 1) == ' ') {
            //怨듬갚�쓣 李얠븘 �떒�뼱(word)�쓽 留덉�留� �씤�뜳�뒪瑜� 媛��졇�샂
            int wordEndIdx = token.indexOf(' ', curIdx);
            wordEndIdx = wordEndIdx == -1 ? token.length() : wordEndIdx;
            String targetWord = token.substring(curIdx, wordEndIdx);
            List<Pair<String, String>> fwdResultList = this.fwd.get(targetWord);

            if (fwdResultList != null) {
                this.insertLatticeForFwd(lattice, curIdx, wordEndIdx, fwdResultList);
                return wordEndIdx;
            }
        }
        return -1;
    }

    private void insertLatticeForFwd(Lattice lattice, int beginIdx, int endIdx,
                                     List<Pair<String, String>> fwdResultList) {
        lattice.put(beginIdx, endIdx, fwdResultList);
    }

    private void continiousSymbolParsing(Lattice lattice, char charAt, int i, ContinuousSymbolInfo continuousSymbolInfo) {
        String curPos = "";
        if (StringUtil.isEnglish(charAt)) {
            curPos = "SL";
        } else if (StringUtil.isNumeric(charAt)) {
            curPos = "SN";
        } else if (StringUtil.isChinese(charAt)) {
            curPos = "SH";
        } else if (StringUtil.isForeign(charAt)) {
            curPos = "SL";
        }

        if (curPos.equals(continuousSymbolInfo.getPrevPos())) {
            continuousSymbolInfo.setPrevMorph(continuousSymbolInfo.getPrevMorph() + charAt);
        } else {
            switch (continuousSymbolInfo.getPrevPos()) {
                case "SL":
//					lattice.put(this.prevBeginIdx, i, this.prevMorph, this.prevPos, this.resources.getTable().getId(this.prevPos), SCORE.SL);
                    lattice.put(continuousSymbolInfo.getPrevBeginIdx(), i,
                            continuousSymbolInfo.getPrevMorph(),
                            continuousSymbolInfo.getPrevPos(),
                            this.resources.getTable().getId(continuousSymbolInfo.getPrevPos()),
                            SCORE.SL
                    );
                    break;
                case "SN":
//					lattice.put(this.prevBeginIdx, i, this.prevMorph, this.prevPos, this.resources.getTable().getId(this.prevPos), SCORE.SN);
                    lattice.put(continuousSymbolInfo.getPrevBeginIdx(), i,
                            continuousSymbolInfo.getPrevMorph(),
                            continuousSymbolInfo.getPrevPos(),
                            this.resources.getTable().getId(continuousSymbolInfo.getPrevPos()),
                            SCORE.SN
                    );
                    break;
                case "SH":
//					lattice.put(this.prevBeginIdx, i, this.prevMorph, this.prevPos, this.resources.getTable().getId(this.prevPos), SCORE.SH);
                    lattice.put(continuousSymbolInfo.getPrevBeginIdx(), i,
                            continuousSymbolInfo.getPrevMorph(),
                            continuousSymbolInfo.getPrevPos(),
                            this.resources.getTable().getId(continuousSymbolInfo.getPrevPos()),
                            SCORE.SH
                    );
                    break;
            }
            continuousSymbolInfo.setPrevBeginIdx(i);
            continuousSymbolInfo.setPrevMorph("" + charAt);
            continuousSymbolInfo.setPrevPos(curPos);
//			this.prevBeginIdx = i;
//			this.prevMorph = ""+charAt;
//			this.prevPos = curPos;
        }
    }

    private void consumeContiniousSymbolParserBuffer(Lattice lattice, String in, ContinuousSymbolInfo continuousSymbolInfo) {
        if (continuousSymbolInfo.getPrevPos().trim().length() != 0) {
            switch (continuousSymbolInfo.getPrevPos()) {
                case "SL":
//					lattice.put(this.prevBeginIdx, in.length(), this.prevMorph, this.prevPos, this.resources.getTable().getId(this.prevPos), SCORE.SL);
                    lattice.put(continuousSymbolInfo.getPrevBeginIdx(),
                            in.length(),
                            continuousSymbolInfo.getPrevMorph(),
                            continuousSymbolInfo.getPrevPos(),
                            this.resources.getTable().getId(continuousSymbolInfo.getPrevPos()),
                            SCORE.SL
                    );
                    break;
                case "SH":
//					lattice.put(this.prevBeginIdx, in.length(), this.prevMorph, this.prevPos, this.resources.getTable().getId(this.prevPos), SCORE.SH);
                    lattice.put(continuousSymbolInfo.getPrevBeginIdx(),
                            in.length(),
                            continuousSymbolInfo.getPrevMorph(),
                            continuousSymbolInfo.getPrevPos(),
                            this.resources.getTable().getId(continuousSymbolInfo.getPrevPos()),
                            SCORE.SH
                    );
                    break;
                case "SN":
//					lattice.put(this.prevBeginIdx, in.length(), this.prevMorph, this.prevPos, this.resources.getTable().getId(this.prevPos), SCORE.SN);
                    lattice.put(continuousSymbolInfo.getPrevBeginIdx(),
                            in.length(),
                            continuousSymbolInfo.getPrevMorph(),
                            continuousSymbolInfo.getPrevPos(),
                            this.resources.getTable().getId(continuousSymbolInfo.getPrevPos()),
                            SCORE.SN
                    );
                    break;
            }
        }
    }

    private void consumeContiniousSymbolParserBuffer(Lattice lattice, int endIdx, ContinuousSymbolInfo continuousSymbolInfo) {
        if (continuousSymbolInfo.getPrevPos().trim().length() != 0) {
            switch (continuousSymbolInfo.getPrevPos()) {
                case "SL":
                    lattice.put(continuousSymbolInfo.getPrevBeginIdx(), endIdx, continuousSymbolInfo.getPrevMorph(),
                            continuousSymbolInfo.getPrevPos(), this.resources.getTable().getId(continuousSymbolInfo.getPrevPos()), SCORE.SL);
                    break;
                case "SH":
                    lattice.put(continuousSymbolInfo.getPrevBeginIdx(), endIdx, continuousSymbolInfo.getPrevMorph(),
                            continuousSymbolInfo.getPrevPos(), this.resources.getTable().getId(continuousSymbolInfo.getPrevPos()), SCORE.SH);
                    break;
                case "SN":
                    lattice.put(continuousSymbolInfo.getPrevBeginIdx(), endIdx, continuousSymbolInfo.getPrevMorph(),
                            continuousSymbolInfo.getPrevPos(), this.resources.getTable().getId(continuousSymbolInfo.getPrevPos()), SCORE.SN);
//					lattice.put(this.prevBeginIdx, endIdx, this.prevMorph, this.prevPos, this.resources.getTable().getId(this.prevPos), SCORE.SN);
                    break;
            }
        }
    }

    private void irregularExtends(Lattice lattice, char jaso, int curIndex) {
        List<LatticeNode> prevLatticeNodes = lattice.getNodeList(curIndex);
        if (prevLatticeNodes != null) {
            Set<LatticeNode> extendedIrrNodeList = new HashSet<>();

            for (LatticeNode prevLatticeNode : prevLatticeNodes) {
                //遺덇퇋移� �깭洹몄씤 寃쎌슦�뿉 ���빐�꽌留�
                if (prevLatticeNode.getMorphTag().getTagId() == SYMBOL.IRREGULAR_ID) {
                    //留덉�留� �삎�깭�냼 �젙蹂대�� �뼸�뼱�샂
                    String lastMorph = prevLatticeNode.getMorphTag().getMorph();

                    //遺덇퇋移숈쓽 留덉�留� �삎�깭�냼�뿉 �쁽�옱 �옄�냼 �떒�쐞瑜� �빀爾ㅼ쓣 �븣 �옄�떇 �끂�뱶媛� �엳�떎硫� 怨꾩냽 �깘�깋 媛��뒫 �썑蹂대줈 泥섎━ �빐�빞�븿
                    if (this.resources.getObservation().getTrieDictionary().hasChild((lastMorph + jaso).toCharArray())) {
                        LatticeNode extendedIrregularNode = new LatticeNode();
                        extendedIrregularNode.setBeginIdx(prevLatticeNode.getBeginIdx());
                        extendedIrregularNode.setEndIdx(curIndex + 1);
                        extendedIrregularNode.setMorphTag(new MorphTag(prevLatticeNode.getMorphTag().getMorph() + jaso, SYMBOL.IRREGULAR, SYMBOL.IRREGULAR_ID));
                        extendedIrregularNode.setPrevNodeIdx(prevLatticeNode.getPrevNodeIdx());
                        extendedIrregularNode.setScore(prevLatticeNode.getScore());
                        extendedIrrNodeList.add(extendedIrregularNode);
                    }
                    //遺덇퇋移숈쓽 留덉�留� �삎�깭�냼�뿉 �쁽�옱 �옄�냼 �떒�쐞瑜� �빀爾� �젏�닔瑜� �뼸�뼱�샂
                    List<ScoredTag> lastScoredTags = this.resources.getObservation().getTrieDictionary().getValue(lastMorph + jaso);
                    if (lastScoredTags == null) {
                        continue;
                    }

                    //�뼸�뼱�삩 �젏�닔瑜� �넗��濡� lattice�뿉 �꽔�쓬
                    for (ScoredTag scoredTag : lastScoredTags) {
                        lattice.put(prevLatticeNode.getBeginIdx(), curIndex + 1, prevLatticeNode.getMorphTag().getMorph() + jaso,
                                scoredTag.getTag(), scoredTag.getTagId(), scoredTag.getScore());
                    }
                }
            }
            for (LatticeNode extendedIrrNode : extendedIrrNodeList) {
                lattice.appendNode(extendedIrrNode);
            }

        }
    }

    private boolean irregularParsing(Lattice lattice, FindContext<List<IrregularNode>> irregularFindContext, char jaso, int curIndex) {
        //遺덇퇋移� �끂�뱶�뱾�쓣 �뼸�뼱�샂
        Map<String, List<IrregularNode>> morphIrrNodesMap = this.getIrregularNodes(irregularFindContext, jaso);
        if (morphIrrNodesMap == null) {
            return false;
        }

        //�삎�깭�냼 �젙蹂대쭔 �뼸�뼱�샂
        Set<String> morphs = morphIrrNodesMap.keySet();
        for (String morph : morphs) {
            List<IrregularNode> irrNodes = morphIrrNodesMap.get(morph);
            int beginIdx = curIndex - morph.length() + 1;
            int endIdx = curIndex + 1;
            for (IrregularNode irregularNode : irrNodes) {
                this.insertLattice(lattice, beginIdx, endIdx, irregularNode);
            }
        }

        return true;
    }

    private void insertLattice(Lattice lattice, int beginIdx, int endIdx,
                               IrregularNode irregularNode) {
        lattice.put(beginIdx, endIdx, irregularNode);
    }

    private void regularParsing(Lattice lattice, FindContext<List<ScoredTag>> observationFindContext, char jaso, int curIndex) {
        //TRIE 湲곕컲�쓽 �궗�쟾 寃��깋�븯�뿬 �삎�깭�냼�� �뭹�궗 諛� �뭹�궗 �젏�닔(observation)瑜� �뼸�뼱�샂
        Map<String, List<ScoredTag>> morphScoredTagsMap = this.getMorphScoredTagsMap(observationFindContext, jaso);

        if (morphScoredTagsMap == null) {
            return;
        }

        //�삎�깭�냼 �젙蹂대쭔 �뼸�뼱�샂
        Set<String> morphes = this.getMorphes(morphScoredTagsMap);

        //媛� �삎�깭�냼�� �뭹�궗 �젙蹂대�� lattice�뿉 �궫�엯
        for (String morph : morphes) {
            int beginIdx = curIndex - morph.length() + 1;
            int endIdx = curIndex + 1;

            //�삎�깭�냼�뿉 ���븳 �뭹�궗 諛� �젏�닔(observation) �젙蹂대�� List �삎�깭濡� 媛��졇�샂
            List<ScoredTag> scoredTags = morphScoredTagsMap.get(morph);
            for (ScoredTag scoredTag : scoredTags) {
                lattice.put(beginIdx, endIdx, morph, scoredTag.getTag(), scoredTag.getTagId(), scoredTag.getScore());
                //�뭹�궗媛� EC�씤 寃쎌슦�뿉 �뭹�궗瑜� EF濡� 蹂��솚�븯�뿬 lattice�뿉 異붽�
                if (scoredTag.getTag().equals(SYMBOL.EC)) {
                    lattice.put(beginIdx, endIdx, morph, SYMBOL.EF, this.resources.getTable().getId(SYMBOL.EF), scoredTag.getScore());
                }
            }
        }
    }

    private Map<String, List<IrregularNode>> getIrregularNodes(FindContext<List<IrregularNode>> irregularFindContext, char jaso) {
        return this.resources.getIrrTrie().getTrieDictionary().get(irregularFindContext, jaso);
    }

    private void insertLattice(Lattice lattice, int beginIdx, int endIdx, String morph,
                               Tag tag, double score) {
        lattice.put(beginIdx, endIdx, morph, tag.getTag(), tag.getTagId(), score);
    }

    private Set<String> getMorphes(
            Map<String, List<ScoredTag>> morphScoredTagMap) {
        return morphScoredTagMap.keySet();
    }

    private Map<String, List<ScoredTag>> getMorphScoredTagsMap(FindContext<List<ScoredTag>> observationFindContext, char jaso) {
        return this.resources.getObservation().getTrieDictionary().get(observationFindContext, jaso);
    }

    private Map<String, List<ScoredTag>> getMorphScoredTagMapFromUserDic(FindContext<List<ScoredTag>> userDicFindContext, char jaso) {
        if (this.userDic == null) {
            return null;
        }
        if (userDicFindContext == null) {
            userDicFindContext = this.userDic.getTrieDictionary().newFindContext();
        }
        return this.userDic.getTrieDictionary().get(userDicFindContext, jaso);
    }

    public void load(String modelPath) {
        this.resources.load(modelPath);
    }

    //湲곕텇�꽍 �궗�쟾
    public void setFWDic(String filename) {
        try {
        	//System.out.println(filename);
            CorpusParser corpusParser = new CorpusParser();
            BufferedReader br = new BufferedReader(new FileReader(filename));
            String line;
            this.fwd = new HashMap<>();
            while ((line = br.readLine()) != null) {
                String[] tmp = line.split("\t");
                
                //System.out.println(line);
                
                //二쇱꽍�씠嫄곕굹 format�뿉 �븞 留욌뒗 寃쎌슦�뒗 skip
                if (tmp.length != 2 || tmp[0].charAt(0) == '#') {
                    tmp = null;
                    continue;
                }
                ProblemAnswerPair problemAnswerPair = corpusParser.parse(line);
                List<Pair<String, String>> convertAnswerList = new ArrayList<>();
                for (Pair<String, String> pair : problemAnswerPair.getAnswerList()) {
                    convertAnswerList.add(
                            new Pair<>(pair.getFirst(), pair.getSecond()));
                }

                this.fwd.put(this.unitParser.parse(problemAnswerPair.getProblem()),
                        convertAnswerList);
                tmp = null;
                problemAnswerPair = null;
                convertAnswerList = null;
            }
            br.close();

            //init
            corpusParser = null;
            br = null;
            line = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setUserDic(String userDic) {
        try {
        	//System.out.println(userDic);
            this.userDic = new Observation();
            BufferedReader br = new BufferedReader(new FileReader(userDic));
            String line = null;
            while ((line = br.readLine()) != null) {
                line = line.trim();
               // System.out.println(line);
                if (line.length() == 0 || line.charAt(0) == '#') continue;
                int lastIdx = line.lastIndexOf("\t");

                String morph;
                String pos;
                
                if (lastIdx == -1) {
                    morph = line.trim();
                    pos = "NNP";
                } else {
                    morph = line.substring(0, lastIdx);
                    pos = line.substring(lastIdx + 1);
                }
                
              //  System.out.println(morph+"//"+pos);
                
                this.userDic.put(morph, pos, this.resources.getTable().getId(pos), 0.0);

                line = null;
                morph = null;
                pos = null;
            }
            br.close();

            //init
            br = null;
            line = null;
            this.userDic.getTrieDictionary().buildFailLink();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
