package org.mitre.neoprofiler.profile;

import java.util.HashMap;
import java.util.Map;

public class ParameterizedNeoProfile extends NeoProfile {
	protected Map<String,Object> params = new HashMap<String,Object>();
	
	public Object getParameter(String name) { return params.get(name); } 
	public void setParameter(String name, Object param) { params.put(name,  param); }  
	
	public Map<String,Object> getParameters() { return params; } 
}
