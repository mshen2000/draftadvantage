package com.nya.sms.entities;

import java.io.Serializable;

import org.jose4j.jwk.RsaJsonWebKey;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Serialize;

// @Subclass(index = true)
@Entity
public class JKey extends BaseEntity implements Serializable {

	private static final long serialVersionUID = 1;

	@Serialize
	RsaJsonWebKey webkey;

	@SuppressWarnings("unused")
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
