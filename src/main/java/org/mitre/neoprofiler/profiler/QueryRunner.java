package org.mitre.neoprofiler.profiler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.mitre.neoprofiler.NeoProfiler;
import org.mitre.neoprofiler.profile.NeoProperty;
import org.neo4j.graphdb.PropertyContainer;
import org.neo4j.graphdb.ResourceIterator;

import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Value;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Transaction;
import org.neo4j.driver.v1.TransactionWork;
import org.neo4j.driver.v1.types.Entity;

/**
 * Abstract class that contains various query running utilities to make implementation of downstream profilers easier.
 * @author moxious
 */
public abstract class QueryRunner {
	protected static final Logger log = Logger.getLogger(QueryRunner.class.getName());
				
	public List<NeoProperty> getSampleProperties(NeoProfiler parent, Entity n) {
		List<NeoProperty> props = new ArrayList<NeoProperty>();
		
		Session s = parent.getDriver().session();
		try ( Transaction tx = s.beginTransaction() ) {			 
			Iterator<String> propKeys = n.keys().iterator();
			
			while(propKeys.hasNext()) {
				String key = propKeys.next();
				Object val = n.get(key);
				
				props.add(new NeoProperty(key, val.getClass().getSimpleName()));				
			}
			
		} finally {
			s.close();
		}

		return props;
	}
	
	public Map<String,List<Object>> runQueryComplexResult(NeoProfiler parent, String query, String...columns) {
		HashMap<String,List<Object>> all = new HashMap<String,List<Object>>();
		
		List<Object> retvals = new ArrayList<Object>();
		
		Session s = parent.getDriver().session();
		try ( Transaction tx = s.beginTransaction()) {
			// log.info(query);
			StatementResult result = tx.run(query);
			
			while(result.hasNext()) {
				Map<String,Object> row = result.next().asMap();
				
				for(String col : columns) { 
					if(!all.containsKey(col)) all.put(col, new ArrayList<Object>());
					all.get(col).add(row.get(col));
				}
			}	
			
			tx.close();
		} finally {
			s.close();
		}
		
		return all;
	}
	
	public Value runQuerySingleResult(NeoProfiler parent, String query, String columnReturn) {		
		Session s = parent.getDriver().session();

		try ( Transaction tx = s.beginTransaction()) {
			// log.info(query);
			StatementResult result = tx.run(query);
						
			if(result.hasNext()) {
				Value val = result.next().get(columnReturn);
				return val;
			}
			
			return null;
		} finally {
			s.close();
		}
	}
	
	public List<Object> runQueryMultipleResult(NeoProfiler parent, String query, String columnReturn) {
		Session s = parent.getDriver().session();
		List<Object> retvals = new ArrayList<Object>();
		
		try ( Transaction tx = s.beginTransaction()) {
			// log.info(query);
			StatementResult result = tx.run(query);
					
			while(result.hasNext()) {
				retvals.add(result.next().get(columnReturn));
			}					
		}
		
		return retvals;
	}

	/*
	public String stringList(Iterable<?> objs) { 
		StringBuffer b = new StringBuffer("");		
		Iterator<?> it = objs.iterator();		
		while(it.hasNext()) b.append(""+it.next() + " ");		
		return b.toString();		
	}
	
	public String stringList(List<Object> objs) { 
		StringBuffer b = new StringBuffer("");
		
		for(int x=0; x<objs.size(); x++) { 
			b.append(""+objs.get(x));
			if(x < (objs.size() - 1)) b.append(", ");
		}
		
		return b.toString();
	}
	*/
}
