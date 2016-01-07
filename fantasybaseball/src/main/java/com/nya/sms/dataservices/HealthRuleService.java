package com.nya.sms.dataservices;


import java.util.List;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import com.nya.sms.entities.HealthRule;
import com.nya.sms.entities.Note;
import com.nya.sms.entities.Points;
import com.nya.sms.entities.User;

public class HealthRuleService extends AbstractDataServiceImpl<HealthRule>{
	
	private static final long serialVersionUID = 1L;

	public HealthRuleService(Class<HealthRule> clazz) {
		super(clazz);
		// TODO Auto-generated constructor stub
	}
	
	public boolean isRulePresent(String rulename) {

		if (ObjectifyService.ofy().load().type(HealthRule.class).filter("rulename", rulename).count() > 0) return true;

		return false;
	}

}
