package com.takaada.integration.integration.dto;

import java.math.BigDecimal;
import java.time.Instant;

public class AccountingApiPaymentDto {
    private String id;
    private String invoiceId;
    private BigDecimal amount;
    private Instant paidAt;

    public AccountingApiPaymentDto() {}

    public AccountingApiPaymentDto(String id, String invoiceId, BigDecimal amount, Instant paidAt) {
        this.id = id;
        this.invoiceId = invoiceId;
        this.amount = amount;
        this.paidAt = paidAt;
    }

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getInvoiceId() {
		return invoiceId;
	}

	public void setInvoiceId(String invoiceId) {
		this.invoiceId = invoiceId;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public Instant getPaidAt() {
		return paidAt;
	}

	public void setPaidAt(Instant paidAt) {
		this.paidAt = paidAt;
	}
}
