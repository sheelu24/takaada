package com.takaada.integration.service;

import com.takaada.integration.integration.AccountingApiAdapter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SyncService {

    private final AccountingApiAdapter accountingApiAdapter;

    public SyncService(AccountingApiAdapter accountingApiAdapter) {
        this.accountingApiAdapter = accountingApiAdapter;
    }

    @Transactional
    public void runSync() {
        accountingApiAdapter.sync();
    }
}
