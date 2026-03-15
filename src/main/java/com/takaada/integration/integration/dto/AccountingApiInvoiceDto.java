package com.takaada.integration.integration.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class AccountingApiInvoiceDto {
    private String id;
    private String customerId;
    private BigDecimal amount;
    private String currency;
    private LocalDate dueDate;
    private String status;

    public AccountingApiInvoiceDto() {}

    public AccountingApiInvoiceDto(String id, String customerId, BigDecimal amount, String currency, LocalDate dueDate, String status) {
        this.id = id;
        this.customerId = customerId;
        this.amount = amount;
        this.currency = currency;
        this.dueDate = dueDate;
        this.status = status;
    }

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public LocalDate getDueDate() {
		return dueDate;
	}

	public void setDueDate(LocalDate dueDate) {
		this.dueDate = dueDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
