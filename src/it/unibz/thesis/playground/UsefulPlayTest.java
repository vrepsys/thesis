package it.unibz.thesis.playground;

import java.util.HashMap;
import java.util.Map;

public class UsefulPlayTest {

	public static void main(String[] args) {

		double d =  21314.1/1233432.3;
		
		System.out.println(d);
		
		Map<Object, Double> map = new HashMap<Object, Double>();
		map.put("aa", d);
		
		d = map.get("aa");
		
		System.out.println(d);
	}

}
