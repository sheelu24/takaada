package com.takaada.integration.integration.dto;

public class AccountingApiCustomerDto {
    private String id;
    private String name;

    public AccountingApiCustomerDto() {}

    public AccountingApiCustomerDto(String id, String name) {
        this.id = id;
        this.name = name;
    }

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
