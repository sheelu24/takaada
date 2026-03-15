# Takaada Integration Service

Accounting integration service with analysis. This service integrates with an external accounting system (customers, invoices, payments), stores data locally in two forms—**raw** (source format for audit) and **normalized** (common schema for analysis)—and exposes APIs for financial insights such as outstanding balances and overdue invoices.

- **On-demand sync**: Trigger sync via `POST /api/sync` (uses dummy data in code; comments indicate where the real API would be called).
- **Insights**: Outstanding balance per customer, list of overdue invoices.
- **Stack**: Java 17, Spring Boot 3.x, H2, Maven.

---

## What it is

A small integration service that:

1. **Fetches** customer, invoice, and payment data from an external accounting API (in this assignment, dummy data in code).
2. **Stores** each response in **raw** form (exact payload per source) and in a **normalized** form (common `Customer`, `Invoice`, `Payment` schema) so different sources (e.g. HDFC, Axis) can have different API shapes while the app exposes one consistent model.
3. **Exposes** insight APIs: per-customer outstanding balance and overdue invoices.

The design supports multiple external sources with different request/response formats; each adapter writes raw + normalized, and all analysis reads only from normalized tables.

---

## Setup and run

**Prerequisites**

- JDK 17. Check: `java -version`

**Steps**

1. Clone or unzip the project and open a terminal in the project root (where `pom.xml` is).
2. Run the application:
   ```bash
   mvn spring-boot:run
   ```
   First run will download dependencies. H2 starts automatically; data is stored in `./data/takaada`.
3. Trigger sync (on-demand; uses dummy data):
   ```bash
   curl -X POST http://localhost:8080/external/sync
   ```
4. Call insight APIs:
   ```bash
   curl http://localhost:8080/insights/invoices/overdue
   curl http://localhost:8080/insights/customers/1/outstanding-balance
   ```
   Use customer IDs returned after sync (e.g. 1, 2).

**Build**

- Compile: `mvn compile`
- Package: `mvn package`
- Run JAR: `java -jar target/takaada-integration-service-0.0.1-SNAPSHOT.jar`

---

## API endpoints

| Method | Path | Description |
|--------|------|-------------|
| POST | `/api/sync` | Run on-demand sync (fetch from source, store raw + normalized). This can be extended to scheduled jobs |
| GET | `/api/customers/{customerId}/outstanding-balance` | Outstanding balance for a customer. |
| GET | `/api/invoices/overdue` | List of overdue invoices with balance and days overdue. |

---

## Class diagram

High-level structure of the main classes and their relationships:

```mermaid
classDiagram
  direction TB
  subgraph api: API layer
    SyncController
    InsightController
  end
  subgraph service: Service layer
    SyncService
    InsightService
  end
  subgraph integration: Integration layer
    AccountingApiAdapter
  end
  subgraph domain: Domain
    Customer
    Invoice
    Payment
    RawIntegrationData
  end
  subgraph repository: Repositories
    CustomerRepository
    InvoiceRepository
    PaymentRepository
    RawIntegrationDataRepository
  end
  subgraph dto: DTOs
    AccountingApiCustomerDto
    AccountingApiInvoiceDto
    AccountingApiPaymentDto
  end
  SyncController --> SyncService
  InsightController --> InsightService
  SyncService --> AccountingApiAdapter
  InsightService --> CustomerRepository
  InsightService --> InvoiceRepository
  InsightService --> PaymentRepository
  AccountingApiAdapter --> RawIntegrationDataRepository
  AccountingApiAdapter --> CustomerRepository
  AccountingApiAdapter --> InvoiceRepository
  AccountingApiAdapter --> PaymentRepository
  AccountingApiAdapter ..> AccountingApiCustomerDto
  AccountingApiAdapter ..> AccountingApiInvoiceDto
  AccountingApiAdapter ..> AccountingApiPaymentDto
  CustomerRepository --> Customer
  InvoiceRepository --> Invoice
  PaymentRepository --> Payment
  RawIntegrationDataRepository --> RawIntegrationData
  Customer "1" --> "*" Invoice
  Invoice "1" --> "*" Payment
```

---

## System design

### Basic design (this assignment)

Single service, on-demand sync, dummy data in code, H2. Sync and insight APIs in one process.

```mermaid
flowchart TB
  subgraph client [Client]
    User[User or API caller]
  end
  subgraph app [This service]
    SyncAPI["POST /api/sync"]
    InsightAPI["GET insights"]
    SyncSvc[Sync service]
    InsightSvc[Insight service]
    Adapter[Accounting adapter]
    RawRepo[Raw repository]
    NormRepo[Normalized repos]
  end
  subgraph storage [Storage]
    RawTable[raw_integration_data]
    NormTables[customer invoice payment]
  end
  subgraph external [External]
    Dummy[Dummy data in code]
  end
  User --> SyncAPI
  User --> InsightAPI
  SyncAPI --> SyncSvc
  SyncSvc --> Adapter
  Adapter --> Dummy
  Adapter --> RawRepo
  Adapter --> NormRepo
  InsightAPI --> InsightSvc
  InsightSvc --> NormRepo
  RawRepo --> RawTable
  NormRepo --> NormTables
```

### Ideal design (production-style)

Multiple sources (e.g. HDFC, Axis), scheduled sync, real HTTP APIs, auth, and observability. Same patterns: per-source adapters, raw + normalized storage, insight layer on normalized data only.

```mermaid
flowchart TB
  subgraph clients [Clients]
    UI[Web or Mobile UI]
    Reports[Reports or BI]
  end
  subgraph gateway [API layer]
    REST[REST API]
    Auth[Auth and rate limiting]
  end
  subgraph domain [Domain]
    SyncOrch[Sync orchestrator]
    InsightSvc[Insight service]
  end
  subgraph adapters [Integration adapters]
    HDFCAdapter[HDFC adapter]
    AxisAdapter[Axis adapter]
  end
  subgraph persistence [Persistence]
    RawStore[Raw store]
    NormStore[Normalized store]
  end
  subgraph external [External systems]
    HDFCAPI[HDFC API]
    AxisAPI[Axis API]
  end
  subgraph ops [Operations]
    Scheduler[Scheduler]
    Metrics[Metrics and alerts]
  end
  UI --> REST
  Reports --> REST
  REST --> Auth
  Auth --> SyncOrch
  Auth --> InsightSvc
  SyncOrch --> HDFCAdapter
  SyncOrch --> AxisAdapter
  HDFCAdapter --> HDFCAPI
  AxisAdapter --> AxisAPI
  HDFCAdapter --> RawStore
  HDFCAdapter --> NormStore
  AxisAdapter --> RawStore
  AxisAdapter --> NormStore
  InsightSvc --> NormStore
  Scheduler --> SyncOrch
  SyncOrch --> Metrics
```

The basic design reflects this assignment (one adapter, on-demand sync, dummy data, single process, H2). The ideal design extends the same ideas to multiple bank adapters, scheduled sync, real APIs, and clearer API/ops boundaries.
