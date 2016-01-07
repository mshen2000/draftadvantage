package com.nya.sms.dataservices;


import java.util.List;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import com.nya.sms.entities.Note;
import com.nya.sms.entities.Points;
import com.nya.sms.entities.User;

public class NoteService extends AbstractDataServiceImpl<Note>{
	
	private static final long serialVersionUID = 1L;

	public NoteService(Class<Note> clazz) {
		super(clazz);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public Long save(Note item, String uname) {
		// TODO Auto-generated method stub
			
		if (item.getId() == null) item.setCreatedby(uname);
		
		item.setModifiedby(uname);
		
		User user = getIdentityService().getUser(uname);

		item.setAuthor(user.getFirstname() + " " + user.getLastname());
		
		Key<Note> key = ObjectifyService.ofy().save().entity(item).now(); 
		
		return key.getId();
		
	}
	
	// Return list of notes entries based on authorization
	//   - i.e. notes access and student scope
	public List<Note> getAuthorizedNotesList (Authorization auth){
		
		return getAuthorizedAbstractList(auth, auth.getAccess_notes());
		 
	}
	
	private IdentityService getIdentityService() {
		 
		 return new IdentityService();
	 
	}


}
