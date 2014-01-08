package edu.zju.bme.openehr.hibernate.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.openehr.am.parser.ContentObject;
import org.openehr.am.parser.DADLParser;
import org.openehr.rm.binding.DADLBinding;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import edu.zju.bme.snippet.java.FileOperator;

public class RMORMPersistenceImplTest {
	
	RMORMPersistence persistence;

	public RMORMPersistenceImplTest() {
		@SuppressWarnings("resource")
		ApplicationContext context = new ClassPathXmlApplicationContext(
				"applicationContext.xml", RMORMPersistenceImplTest.class);
		
		persistence = (RMORMPersistence) context.getBean("wsclient");
	}

	@Test
	public void testInsert() throws Exception {		
		List<String> dadls = new ArrayList<String>();
		dadls.add(FileOperator.INSTANCE.readLinesFromResource("patient1.dadl"));
		dadls.add(FileOperator.INSTANCE.readLinesFromResource("patient2.dadl"));
		
		List<String> adls = new ArrayList<String>();
		adls.add(FileOperator.INSTANCE.readLinesFromFile("../document/knowledge/ZJU/archetype/openEHR-DEMOGRAPHIC-PERSON.patient.v1.adl"));
		
		assertEquals(persistence.insert(dadls, adls), 0);
		assertEquals(persistence.insert(dadls, adls), 0);
	}
	
	@Test
	public void testSelectPersonByObjectUids() throws Exception {
		testInsert();
		
		List<String> objectUids = new ArrayList<String>();
		objectUids.add("patient1");
		List<String> listPerson = 
				persistence.selectPersonByObjectUids(objectUids);
		assertNotNull(listPerson);
		assertEquals(listPerson.size(), 1);
		String s1 = FileOperator.INSTANCE.readLinesFromResource("patient1.dadl");
		String s2 = listPerson.get(0);
		Object o1 = parseDADL(s1);
		Object o2 = parseDADL(s2);
		assertTrue(o1.equals(o2));
		
		objectUids.add("patient2");
		listPerson = persistence.selectPersonByObjectUids(objectUids);
		assertNotNull(listPerson);
		assertEquals(listPerson.size(), 2);
		Object o3 = parseDADL(FileOperator.INSTANCE.readLinesFromResource("patient2.dadl"));
		Object o4 = parseDADL(listPerson.get(1));
		assertTrue(o3.equals(o4));
	}
	
//	@Test
//	public void selectCoarseNodePathByPathValues() throws Exception {
//		testInsert();
//		
//		List<String> paths = new ArrayList<>();
//		List<String> values = new ArrayList<>();
//		paths.add("/details[at0001]/items[at0004]/value/value");
//		values.add("1984-08-11T19:20:30+08:00");		
//		List<CoarseNodePathEntity> listCoarseNodePath = 
//				nodePathPersistence.selectCoarseNodePathByPathValues(paths, values);
//		assertNotNull(listCoarseNodePath);
//		assertEquals(listCoarseNodePath.size(), 1);
//		paths.add("/details[at0001]/items[at0009]/value/value");
//		values.add("zhangsan");
//		listCoarseNodePath = 
//				nodePathPersistence.selectCoarseNodePathByPathValues(paths, values);
//		assertNotNull(listCoarseNodePath);
//		assertEquals(listCoarseNodePath.size(), 1);
//		paths.add("/details[at0001]/items[at0009]/value/value");
//		values.add("lisi");
//		listCoarseNodePath = 
//				nodePathPersistence.selectCoarseNodePathByPathValues(paths, values);
//		assertNotNull(listCoarseNodePath);
//		assertEquals(listCoarseNodePath.size(), 2);
//	}
	
	protected Object parseDADL(String str) throws Exception {

		DADLBinding binding = new DADLBinding();
		InputStream is = new ByteArrayInputStream(str.getBytes("UTF-8"));
		DADLParser parser = new DADLParser(is);
		ContentObject contentObj = parser.parse();
		Object archetypeInstance = binding.bind(contentObj);
		
		return archetypeInstance;
	}

}
