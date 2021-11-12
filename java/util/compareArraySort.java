package util;


	public class compareArraySort implements Comparable {

		private String in_str;
		private double in_count;
		
		public compareArraySort(String n,double g){
			in_str = n;
			in_count = g;
			
		}
		
		public String getIn_str(){
			return in_str;
		}
		public double getIn_count(){
			return in_count;
		}
		
		@Override
		public int compareTo(Object arg0) {
			// TODO Auto-generated method stub
			compareArraySort other = (compareArraySort)arg0;
			if(in_count < other.in_count){
				return -1;
			}else if(in_count > other.in_count){
				return 1;
			}
			return 0;
		}

	}


