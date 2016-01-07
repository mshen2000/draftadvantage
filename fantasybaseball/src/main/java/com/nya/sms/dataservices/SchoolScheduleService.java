package com.nya.sms.dataservices;


import java.util.List;

import com.nya.sms.entities.Note;
import com.nya.sms.entities.SchoolSchedule;

public class SchoolScheduleService extends AbstractDataServiceImpl<SchoolSchedule>{
	
	private static final long serialVersionUID = 1L;

	public SchoolScheduleService(Class<SchoolSchedule> clazz) {
		super(clazz);
		// TODO Auto-generated constructor stub
	}
	
	// Return list of notes entries based on authorization
	//   - i.e. notes access and student scope
	public List<SchoolSchedule> getAuthorizedScheduleList (Authorization auth){
		
		return getAuthorizedAbstractList(auth, auth.getAccess_schedule());
		 
	}

}
