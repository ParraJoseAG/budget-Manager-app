package com.alkemy.java.budgetManager.models;

public class Authority {

	private Long id;
	private AuthorityEnum authority;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public AuthorityEnum getAuthority() {
		return authority;
	}

	public void setAuthority(AuthorityEnum authority) {
		this.authority = authority;
	}

}
