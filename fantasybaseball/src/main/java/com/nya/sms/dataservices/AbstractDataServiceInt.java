package com.nya.sms.dataservices;

import java.util.List;


public interface AbstractDataServiceInt<T> {
	
	Long save(T item, String uname);
	
	List<T> getAll();
	
	void delete(Long id);
	
	T get(Long id);

}
