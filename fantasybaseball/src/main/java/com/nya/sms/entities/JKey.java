package com.nya.sms.entities;

import java.io.Serializable;

import org.jose4j.jwk.RsaJsonWebKey;

import com.googlecode.objectify.annotation.Embed;
import com.googlecode.objectify.annotation.EntitySubclass;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Serialize;

@EntitySubclass(index = true)
public class JKey extends BaseEntity implements Serializable {

	private static final long serialVersionUID = 1;

	@Serialize
	RsaJsonWebKey webkey;

	private JKey() {
	}

	public JKey(RsaJsonWebKey webkey) {

		this.webkey = webkey;

	}

	public RsaJsonWebKey getWebkey() {
		return webkey;
	}

	public void setWebkey(RsaJsonWebKey webkey) {
		this.webkey = webkey;
	}



}
