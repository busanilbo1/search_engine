package textrank;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;




class DictSegment2 implements Comparable<DictSegment2> {

   
    private static final Map<Character, Character> charMap = new HashMap<Character, Character>(16, 0.95f);
   
    private static final int ARRAY_LENGTH_LIMIT = 3;

   
    private Map<Character, DictSegment2> childrenMap;
 
    private DictSegment2[] childrenArray;

    private Character nodeChar;
    
    private int storeSize = 0;
   
    private int nodeState = 0;

    DictSegment2(Character nodeChar) {
        if (nodeChar == null) {
            throw new IllegalArgumentException("eeeee");
        }
        this.nodeChar = nodeChar;
    }


    Character getNodeChar() {
        return nodeChar;
    }


    boolean hasNextNode() {
        return this.storeSize > 0;
    }


    Hit2 match(char[] charArray) {
        return this.match(charArray, 0, charArray.length, null);
    }


    Hit2 match(char[] charArray, int begin, int length) {
        return this.match(charArray, begin, length, null);
    }


    Hit2 match(char[] charArray, int begin, int length, Hit2 searchHit) {

        if (searchHit == null) {
            
            searchHit = new Hit2();
          
            searchHit.setBegin(begin);
        }
        else {
           
            searchHit.setUnmatch();
        }
       
        searchHit.setEnd(begin);

        Character keyChar = new Character(charArray[begin]);
        DictSegment2 ds = null;

        
        DictSegment2[] segmentArray = this.childrenArray;
        Map<Character, DictSegment2> segmentMap = this.childrenMap;

       
        if (segmentArray != null) {
           
        	DictSegment2 keySegment = new DictSegment2(keyChar);
            int position = Arrays.binarySearch(segmentArray, 0, this.storeSize, keySegment);
            if (position >= 0) {
                ds = segmentArray[position];
            }

        }
        else if (segmentMap != null) {
            
            ds = (DictSegment2) segmentMap.get(keyChar);
        }

       
        if (ds != null) {
            if (length > 1) {
               
                return ds.match(charArray, begin + 1, length - 1, searchHit);
            }
            else if (length == 1) {

              
                if (ds.nodeState == 1) {
                   
                    searchHit.setMatch();
                }
                if (ds.hasNextNode()) {
                   
                    searchHit.setPrefix();
                    
                    searchHit.setMatchedDictSegment(ds);
                }
                return searchHit;
            }

        }
       
        return searchHit;
    }


    void fillSegment(char[] charArray) {
        this.fillSegment(charArray, 0, charArray.length, 1);
    }


    void disableSegment(char[] charArray) {
        this.fillSegment(charArray, 0, charArray.length, 0);
    }


    private synchronized void fillSegment(char[] charArray, int begin, int length, int enabled) {
       
        Character beginChar = new Character(charArray[begin]);
        Character keyChar = charMap.get(beginChar);
       
        if (keyChar == null) {
            charMap.put(beginChar, beginChar);
            keyChar = beginChar;
        }

      
        DictSegment2 ds = lookforSegment(keyChar, enabled);
        if (ds != null) {
          
            if (length > 1) {
              
                ds.fillSegment(charArray, begin + 1, length - 1, enabled);
            }
            else if (length == 1) {
                
                ds.nodeState = enabled;
            }
        }

    }


    private DictSegment2 lookforSegment(Character keyChar, int create) {

    	DictSegment2 ds = null;

        if (this.storeSize <= ARRAY_LENGTH_LIMIT) {
            
        	DictSegment2[] segmentArray = getChildrenArray();
            
        	DictSegment2 keySegment = new DictSegment2(keyChar);
            int position = Arrays.binarySearch(segmentArray, 0, this.storeSize, keySegment);
            if (position >= 0) {
                ds = segmentArray[position];
            }

            
            if (ds == null && create == 1) {
                ds = keySegment;
                if (this.storeSize < ARRAY_LENGTH_LIMIT) {
                   
                    segmentArray[this.storeSize] = ds;
                   
                    this.storeSize++;
                    Arrays.sort(segmentArray, 0, this.storeSize);

                }
                else {
                   
                    Map<Character, DictSegment2> segmentMap = getChildrenMap();
                   
                    migrate(segmentArray, segmentMap);
                    
                    segmentMap.put(keyChar, ds);
  
                    this.storeSize++;
                   
                    this.childrenArray = null;
                }

            }

        }
        else {
           
            Map<Character, DictSegment2> segmentMap = getChildrenMap();
         
            ds = (DictSegment2) segmentMap.get(keyChar);
            if (ds == null && create == 1) {
               
                ds = new DictSegment2(keyChar);
                segmentMap.put(keyChar, ds);
               
                this.storeSize++;
            }
        }

        return ds;
    }


    private DictSegment2[] getChildrenArray() {
        if (this.childrenArray == null) {
            synchronized (this) {
                if (this.childrenArray == null) {
                    this.childrenArray = new DictSegment2[ARRAY_LENGTH_LIMIT];
                }
            }
        }
        return this.childrenArray;
    }


  
    private Map<Character, DictSegment2> getChildrenMap() {
        if (this.childrenMap == null) {
            synchronized (this) {
                if (this.childrenMap == null) {
                    this.childrenMap = new HashMap<Character, DictSegment2>(ARRAY_LENGTH_LIMIT * 2, 0.8f);
                }
            }
        }
        return this.childrenMap;
    }


    private void migrate(DictSegment2[] segmentArray, Map<Character, DictSegment2> segmentMap) {
        for (DictSegment2 segment : segmentArray) {
            if (segment != null) {
                segmentMap.put(segment.nodeChar, segment);
            }
        }
    }


    public int compareTo(DictSegment2 o) {
        
        return this.nodeChar.compareTo(o.nodeChar);
    }

}