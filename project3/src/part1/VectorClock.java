package part1;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Set;

public class VectorClock implements Comparable<VectorClock>{
	private HashMap<String, Integer> mMap;
	
	public VectorClock(){
		mMap = new HashMap<String, Integer>();
	}
	
	public void increment(String key){
		int val = mMap.get(key);
		val++;
		mMap.put(key, val);
	}
	
	public void init(String key){
		mMap.put(key, 0);
	}
	
	public HashMap<String, Integer> getMap(){
		return mMap;
	}
	
	@Override
	public int compareTo(VectorClock o){
		HashMap<String, Integer> oMap = o.getMap();
		Set<String> mSet = mMap.keySet();
		Set<String> oSet = oMap.keySet();
		
		boolean mCo = mSet.containsAll(oSet);
		boolean oCm = oSet.containsAll(mSet);
		
		if(!mCo || !oCm){
			//don't contain the same keys => not equal
			return mCo ? 1 : -1;
		}else{
			for(String k : mSet){
				int lval = mMap.get(k);
				int rval = oMap.get(k);
				if(lval!=rval){
					//don't have the same value => not equal
					return lval > rval ? 1 : -1;
				}
			}
			//no differences in keys => equal
			return 0;
		}
	}
	
	
	public int order(VectorClock o) {
		// o clock is....
		//-1: before
		// 0: concurrent
		// 1: after
		HashMap<String, Integer> oMap = o.getMap();
		Set<String> mSet = mMap.keySet();
		Set<String> oSet = oMap.keySet();
		
		boolean mCo = mSet.containsAll(oSet);
		boolean oCm = oSet.containsAll(mSet);
		
		if(mCo && !oCm){
			//I am after, I contain a key not present in the other
			return 1;
		}else if(!mCo && oCm){
			//I am before, other contains key I do not have
			return -1;
		}
		ArrayList<Boolean> lte = new ArrayList<Boolean>();
		ArrayList<Boolean> lt = new ArrayList<Boolean>();
		ArrayList<Boolean> gt = new ArrayList<Boolean>();
		for(String key : mSet){
			int lval = mMap.get(key);
			int rval = oMap.get(key);
			lte.add(lval <= rval);
			lt.add(lval < rval);
			gt.add(lval > rval);
		}
		if(!lte.contains(false) && lt.contains(true)){
			//all values are <= and there is atleast one that is <
			//definition of precedence
			return -1;
		}else if(lt.contains(true) && gt.contains(true)){
			//some values are before and some after
			// => clocks are concurrent
			return 0;
		}else{
			//all vals are either equal or after the others,
			// => I am after
			return 1;
		}
		
	}
}
