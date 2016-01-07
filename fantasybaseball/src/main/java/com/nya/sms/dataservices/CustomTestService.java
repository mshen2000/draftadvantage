package com.nya.sms.dataservices;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import com.nya.sms.entities.BaseFieldAbstract;
import com.nya.sms.entities.CustomObjectTest;
import com.nya.sms.entities.Site;

public class CustomTestService extends AbstractDataServiceImpl<CustomObjectTest> implements Serializable {
	
	private static final long serialVersionUID = 1L;

	public CustomTestService(Class<CustomObjectTest> clazz) {
		super(clazz);
		// TODO Auto-generated constructor stub
	}
	
	public boolean isProgramPresent(String name) {

		if (ObjectifyService.ofy().load().type(CustomObjectTest.class).filter("name", name).count() > 0) return true;

		return false;
	}
	
	
	public CustomObjectTest get(String name){
		
		return ObjectifyService.ofy().load().type(CustomObjectTest.class).filter("name", name).first().get();
		
	}
	
	@Override
	public Long save(CustomObjectTest program, String uname){
		
		if (!isProgramPresent(program.getName())) {
			
			program.setCreatedby(uname);
			
		} 
		
		program.setModifiedby(uname);
		 
		Key<CustomObjectTest> key = ObjectifyService.ofy().save().entity(program).now(); 
		
		int i = 0;
		
		while ((!isProgramPresent(program.getName()))&&(i < 10)){
			
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			i++;
			
		}
		
		return key.getId();
		
	}
	
	public List<BaseFieldAbstract> getSortedFieldsForQuery(CustomObjectTest program){
		
		Map<String, BaseFieldAbstract> fieldmap = program.getFields();
		
		Iterator<Entry<String, BaseFieldAbstract>> i = fieldmap.entrySet().iterator();
		
		List<BaseFieldAbstract> fieldlist = new ArrayList<BaseFieldAbstract>();
		
		while (i.hasNext()) {
			Map.Entry<String, BaseFieldAbstract> field = (Map.Entry<String, BaseFieldAbstract>) i.next();
			
			if ((field.getValue().getPriority() > 0) && (field.getValue().isQueryVisible())) 
				fieldlist.add(field.getValue());

		}
		
		  Collections.sort(fieldlist, new Comparator<BaseFieldAbstract>() {
		        public int compare(BaseFieldAbstract o1, BaseFieldAbstract o2) {
		            //Sorts by 'Priority' property
		            //return o1.getPriority()<o2.getPriority()?-1:o1.getPriority()>o2.getPriority()?1:doSecodaryOrderSort(o1,o2);
		            return o1.getPriority()<o2.getPriority()?-1:o1.getPriority()>o2.getPriority()?1:0;
		        }

		        //If 'Priority' property is equal sorts by 'Name' property
//		        public int doSecodaryOrderSort(BaseFieldAbstract o1,BaseFieldAbstract o2) {
//		            return o1.getName()<o2.getName()?-1:o1.getName()>o2.getName()?1:0;
//		        }
		    });
		  
		  return fieldlist;
		
	}
	
	public List<BaseFieldAbstract> getSortedFieldsForForm(CustomObjectTest program){
		
		Map<String, BaseFieldAbstract> fieldmap = program.getFields();
		
		Iterator<Entry<String, BaseFieldAbstract>> i = fieldmap.entrySet().iterator();
		
		List<BaseFieldAbstract> fieldlist = new ArrayList<BaseFieldAbstract>();
		
		while (i.hasNext()) {
			Map.Entry<String, BaseFieldAbstract> field = (Map.Entry<String, BaseFieldAbstract>) i.next();
			
			if ((field.getValue().getPriority() > 0) && (field.getValue().isFormAccess())) 
				fieldlist.add(field.getValue());

		}
		
		  Collections.sort(fieldlist, new Comparator<BaseFieldAbstract>() {
		        public int compare(BaseFieldAbstract o1, BaseFieldAbstract o2) {
		            //Sorts by 'Priority' property
		            //return o1.getPriority()<o2.getPriority()?-1:o1.getPriority()>o2.getPriority()?1:doSecodaryOrderSort(o1,o2);
		            return o1.getPriority()<o2.getPriority()?-1:o1.getPriority()>o2.getPriority()?1:0;
		        }

		        //If 'Priority' property is equal sorts by 'Name' property
//		        public int doSecodaryOrderSort(BaseFieldAbstract o1,BaseFieldAbstract o2) {
//		            return o1.getName()<o2.getName()?-1:o1.getName()>o2.getName()?1:0;
//		        }
		    });
		  
		  return fieldlist;
		
	}
	


}
