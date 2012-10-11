package it.unibz.thesis.data.test;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import it.unibz.thesis.data.DataSplitter;
import it.unibz.thesis.tests.data.Preference;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DataSplitterTest {

	private Preference[] preferenceData = new Preference[] {
		new Preference(0, 0, 1, 0),
		new Preference(0, 0, 5, 0),
		new Preference(0, 0, 2, 0),
		new Preference(0, 0, 3, 0),
		new Preference(0, 0, 4, 0)
	};
	
	private DataSplitter dataSplitter;
	
	@Before
	public void setUp() throws Exception {
		dataSplitter = new DataSplitter(Arrays.asList(preferenceData));
	}

	@After
	public void tearDown() throws Exception {
		dataSplitter = null;
	}

	@Test
	public void testSplitByTime() {
		dataSplitter.splitByTime(50);
		List<Preference> firstPart = dataSplitter.getFirstPart();
		List<Preference> secondPart = dataSplitter.getSecondPart();
		assertEquals(2, firstPart.size());
		assertEquals(3, secondPart.size());
		assertEquals(1, firstPart.get(0).getTime());
		assertEquals(2, firstPart.get(1).getTime());
		assertEquals(3, secondPart.get(0).getTime());
		assertEquals(4, secondPart.get(1).getTime());
		assertEquals(5, secondPart.get(2).getTime());
		
		dataSplitter.splitByTime(20);
		firstPart = dataSplitter.getFirstPart();
		secondPart = dataSplitter.getSecondPart();
		assertEquals(1, firstPart.size());
		assertEquals(4, secondPart.size());
		assertEquals(1, firstPart.get(0).getTime());
		assertEquals(2, secondPart.get(0).getTime());
		assertEquals(3, secondPart.get(1).getTime());
		assertEquals(4, secondPart.get(2).getTime());
		assertEquals(5, secondPart.get(3).getTime());
		
	}

	@Test
	public void testSplitRandomly() {
		dataSplitter.splitRandomly(50);
		List<Preference> firstPart = dataSplitter.getFirstPart();
		List<Preference> secondPart = dataSplitter.getSecondPart();
		assertEquals(2, firstPart.size());
		assertEquals(3, secondPart.size());
		
	}

}
