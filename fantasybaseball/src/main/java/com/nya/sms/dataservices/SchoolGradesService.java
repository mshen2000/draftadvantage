package com.nya.sms.dataservices;


import java.util.List;

import com.nya.sms.entities.Note;
import com.nya.sms.entities.SchoolGrades;

public class SchoolGradesService extends AbstractDataServiceImpl<SchoolGrades>{
	
	private static final long serialVersionUID = 1L;

	public SchoolGradesService(Class<SchoolGrades> clazz) {
		super(clazz);
		// TODO Auto-generated constructor stub
	}
	
	// Return list of notes entries based on authorization
	//   - i.e. notes access and student scope
	public List<SchoolGrades> getAuthorizedNotesList (Authorization auth){
		
		return getAuthorizedAbstractList(auth, auth.getAccess_grades());
		 
	}

}
