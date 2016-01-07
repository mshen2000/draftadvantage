package com.nya.sms.dataservices;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.cmd.Query;
import com.nya.sms.entities.BaseEntity;
import com.nya.sms.entities.Points;
import com.nya.sms.entities.Student;

public class AbstractDataServiceImpl<T extends BaseEntity> implements Serializable, AbstractDataServiceInt<T> {

	
	private Class<T> valueType;
	private static final long serialVersionUID = 1L;
	
	public AbstractDataServiceImpl(Class<T> clazz) {
		
		this.valueType = clazz;
		
	}
	

	public Long save(T item, String uname) {
		// TODO Auto-generated method stub
			
		if (item.getId() == null) item.setCreatedby(uname);
		
		item.setModifiedby(uname);
		
		Key<T> key = ObjectifyService.ofy().save().entity(item).now(); 
		
		return key.getId();
		
	}


	public List<T> getAll() {
		
		return ObjectifyService.ofy().load().type(valueType).list();

	}


	public void delete(Long id) {

		Key<T> key = ObjectifyService.ofy().load().type(valueType).id(id).key();
		
		if (key != null)
			ObjectifyService.ofy().delete().key(key).now();
		
//		T item = get(id);
//		
//		if (item != null)
//			ObjectifyService.ofy().delete().entity(item).now(); 
		
	}


	public T get(Long id) {
		
		return ObjectifyService.ofy().load().type(valueType).id(id).get();
	}
	
	
	// Return list of objects based on authorization
	//   - i.e. object access and student scope
	public List<T> getAuthorizedAbstractList (Authorization auth, String object_access){
		
		List<T> objects = new ArrayList<T>();
		
		// If authorization can access all student data and points data, then return all points.
		// 		- This is to protect against the situation when a student is deleted
		//		  and the points studentid field doesn't refer to a student anymore
		if ((getStudentService().isStudentQueryAccessAllData(auth))
				&&(!object_access.equals(getIdentityService().NO_ACCESS)))
			return getAll();
		
		
		List<Long> sids = getStudentService().getAuthorizedStudentIDList(auth);
		
		if (sids.size() > 0)
			objects = ObjectifyService.ofy().load().type(valueType).filter("studentid in", sids).list();
		
		return objects;
		
	}
	
	 private StudentService getStudentService() {
		 
		 return new StudentService();
	 
	 }
	 
		private IdentityService getIdentityService() {
			 
			 return new IdentityService();
		 
		}

}
