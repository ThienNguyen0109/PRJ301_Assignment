# Hướng Dẫn Chuyển Dự Án Sang JPA

Tài liệu này mô tả hướng chuyển dự án `E-Vehicle Rental System` từ JDBC DAO sang JPA theo cách an toàn, dễ kiểm soát và phù hợp với database MSSQL hiện tại.

## 1. Mục Tiêu

- Chuyển các model JavaBean hiện tại thành JPA Entity.
- Giữ mapping đúng với script database hiện tại.
- Thay dần JDBC DAO bằng JPA Repository/DAO.
- Có cơ chế seed data khi tạo database mới.
- Không tự drop/create database mỗi lần chạy nếu schema không thay đổi.
- Có hướng quản lý schema version để chỉ reset/seed lại khi cần.

## 2. Cấu Hình `persistence.xml`

Vì project chạy bằng Servlet/JSP trên Tomcat, nên dùng `RESOURCE_LOCAL`.

Khuyến nghị cấu hình:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<persistence version="2.1"
             xmlns="http://xmlns.jcp.org/xml/ns/persistence"
             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/persistence http://xmlns.jcp.org/xml/ns/persistence/persistence_2_1.xsd">

  <persistence-unit name="PRJ301-EVehvicleRentalPU" transaction-type="RESOURCE_LOCAL">
    <provider>org.eclipse.persistence.jpa.PersistenceProvider</provider>
    <exclude-unlisted-classes>false</exclude-unlisted-classes>

    <properties>
      <property name="javax.persistence.jdbc.driver" value="com.microsoft.sqlserver.jdbc.SQLServerDriver"/>
      <property name="javax.persistence.jdbc.url" value="jdbc:sqlserver://localhost:1433;databaseName=EVehicleRental_DB;encrypt=false;trustServerCertificate=true"/>
      <property name="javax.persistence.jdbc.user" value="sa"/>
      <property name="javax.persistence.jdbc.password" value="12345"/>

      <property name="javax.persistence.schema-generation.database.action" value="none"/>
    </properties>
  </persistence-unit>
</persistence>
```

Không nên để:

```xml
<property name="javax.persistence.schema-generation.database.action" value="create"/>
```

vì có thể làm mất hoặc tạo lại schema ngoài ý muốn.

## 3. Mapping Kiểu Dữ Liệu

| SQL Server | Java/JPA nên dùng |
|---|---|
| `UNIQUEIDENTIFIER` | `UUID` |
| `VARCHAR`, `NVARCHAR` | `String` |
| `NVARCHAR(MAX)` | `String` + `@Lob` |
| `BIT` | `Boolean` |
| `INT` | `Integer` |
| `DECIMAL(10,2)` | `BigDecimal` |
| `DATE` | `LocalDate` |
| `DATETIME2` | `LocalDateTime` |

Các field tiền như `balance`, `amount`, `price_per_day`, `total_amount` nên dùng `BigDecimal`, không nên dùng `Double`.

## 4. Enum Mapping

Các cột `VARCHAR` có `CHECK` constraint nên map bằng enum.

Ví dụ:

```java
@Enumerated(EnumType.STRING)
@Column(name = "status", length = 20)
private RentalStatus status;
```

Không dùng `EnumType.ORDINAL` vì dễ lệch dữ liệu khi thêm enum mới.

Các enum nên có:

- `Role`: `CUSTOMER`, `STAFF`, `ADMIN`
- `AccountStatus`: `ACTIVE`, `INACTIVE`
- `WalletTransactionType`: `TOPUP`, `PAYMENT`, `REFUND`
- `VehicleStatus`: `AVAILABLE`, `RENTED`, `MAINTENANCE`
- `RentalStatus`: `BOOKED`, `RENTED`, `COMPLETED`, `CANCELLED`, `NO_SHOW`
- `PaymentMethod`: `WALLET`, `VNPAY`
- `PaymentStatus`: `PENDING`, `SUCCESS`, `FAILED`
- `VehicleModelImageType`: `FRONT`, `BACK`, `INTERIOR`
- `MaintenanceStatus`: `PENDING`, `COMPLETED`
- `IncidentSeverity`: `LOW`, `MEDIUM`, `HIGH`

## 5. Entity Cần Có

Theo script database hiện tại, cần các entity:

- `Account`
- `Wallet`
- `WalletTransaction`
- `Station`
- `Category`
- `VehicleModel`
- `VehicleModelImage`
- `Vehicle`
- `Rental`
- `RentalStatusHistory`
- `Payment`
- `Discount`
- `RentalDiscount`
- `Review`
- `VehicleMaintenance`
- `IncidentReport`

Tên class nên dùng camel-case Java, còn `@Table` giữ đúng tên bảng SQL.

Ví dụ:

```java
@Entity
@Table(name = "Vehicle_Model")
public class VehicleModel {
}
```

## 6. Mapping Quan Hệ Chính

Không nên chỉ giữ foreign key dạng `String`. Nên map thành object relationship.

### `Wallet`

```java
@OneToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "account_id", nullable = false)
private Account account;
```

### `WalletTransaction`

```java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "wallet_id", nullable = false)
private Wallet wallet;
```

### `VehicleModel`

```java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "category_id", nullable = false)
private Category category;
```

### `Vehicle`

```java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "model_id", nullable = false)
private VehicleModel model;

@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "station_id", nullable = false)
private Station station;
```

### `Rental`

```java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "customer_id", nullable = false)
private Account customer;

@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "vehicle_id", nullable = false)
private Vehicle vehicle;

@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "pickup_station_id", nullable = false)
private Station pickupStation;
```

### `Payment`

```java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "rental_id", nullable = false)
private Rental rental;
```

### `RentalDiscount`

Nên giữ `RentalDiscount` là entity riêng vì bảng có primary key riêng `rental_discount_id`.

Không nên map `Rental` và `Discount` bằng `@ManyToMany` trực tiếp.

## 7. ID Với MSSQL `UNIQUEIDENTIFIER`

Khuyến nghị dùng `UUID`:

```java
@Id
@Column(name = "account_id", columnDefinition = "uniqueidentifier")
private UUID accountId;
```

Để tự generate UUID trong Java:

```java
@PrePersist
public void prePersist() {
    if (accountId == null) {
        accountId = UUID.randomUUID();
    }
}
```

Cách này dễ seed data bằng UUID cố định hơn so với `@GeneratedValue`.

## 8. Ví Dụ Entity

### `Account`

```java
@Entity
@Table(name = "Account")
public class Account implements Serializable {
    @Id
    @Column(name = "account_id", columnDefinition = "uniqueidentifier")
    private UUID accountId;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "full_name", nullable = false, length = 100, columnDefinition = "NVARCHAR(100)")
    private String fullName;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "is_verified")
    private Boolean verified;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", length = 20)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private AccountStatus status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (accountId == null) {
            accountId = UUID.randomUUID();
        }
        if (verified == null) {
            verified = true;
        }
        if (role == null) {
            role = Role.CUSTOMER;
        }
        if (status == null) {
            status = AccountStatus.ACTIVE;
        }
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
```

### `Rental`

```java
@Entity
@Table(name = "Rental")
public class Rental implements Serializable {
    @Id
    @Column(name = "rental_id", columnDefinition = "uniqueidentifier")
    private UUID rentalId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Account customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pickup_station_id", nullable = false)
    private Station pickupStation;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "total_days")
    private Integer totalDays;

    @Column(name = "total_amount", precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private RentalStatus status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (rentalId == null) {
            rentalId = UUID.randomUUID();
        }
        if (status == null) {
            status = RentalStatus.BOOKED;
        }
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
```

## 9. JPAUtil

Tạo utility quản lý `EntityManagerFactory`.

```java
public final class JPAUtil {
    private static final EntityManagerFactory EMF =
            Persistence.createEntityManagerFactory("PRJ301-EVehvicleRentalPU");

    private JPAUtil() {
    }

    public static EntityManager getEntityManager() {
        return EMF.createEntityManager();
    }

    public static void close() {
        if (EMF.isOpen()) {
            EMF.close();
        }
    }
}
```

Nên đóng `EntityManagerFactory` khi web app shutdown bằng `ServletContextListener`.

## 10. Repository/DAO Với JPA

Nên tạo package mới:

```text
src/java/repositories
```

Ví dụ:

```java
public class AccountRepository {
    public Account findByEmail(String email) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            List<Account> result = em.createQuery(
                    "SELECT a FROM Account a WHERE a.email = :email", Account.class)
                    .setParameter("email", email)
                    .getResultList();
            return result.isEmpty() ? null : result.get(0);
        } finally {
            em.close();
        }
    }
}
```

## 11. Transaction Management

Với `RESOURCE_LOCAL`, transaction nên đặt ở service layer.

```java
EntityManager em = JPAUtil.getEntityManager();
EntityTransaction tx = em.getTransaction();

try {
    tx.begin();

    // persist / merge / query

    tx.commit();
} catch (Exception ex) {
    if (tx.isActive()) {
        tx.rollback();
    }
    throw ex;
} finally {
    em.close();
}
```

Phần `BookingService` nên chuyển sau cùng vì có transaction nhiều bảng:

- `Rental`
- `Payment`
- `Wallet`
- `WalletTransaction`
- `Discount`
- `RentalDiscount`
- `RentalStatusHistory`
- `Vehicle`

## 12. Seeder Data

Nên viết seeder bằng Java thay vì để SQL chạy tự do mỗi lần app start.

Tạo:

```text
src/java/seed/DatabaseSeeder.java
```

Ý tưởng:

```java
public class DatabaseSeeder {
    public static void seed(EntityManager em) {
        if (count(em, Account.class) > 0) {
            return;
        }

        seedAccounts(em);
        seedWallets(em);
        seedStations(em);
        seedCategories(em);
        seedVehicleModels(em);
        seedVehicleModelImages(em);
        seedVehicles(em);
        seedDiscounts(em);
        seedRentals(em);
        seedPayments(em);
        seedRentalStatusHistory(em);
        seedRentalDiscounts(em);
        seedReviews(em);
        seedVehicleMaintenances(em);
        seedIncidentReports(em);
    }
}
```

Seeder nên dùng UUID cố định như script hiện tại để dễ test:

```java
Station station = new Station();
station.setStationId(UUID.fromString("00000000-0000-0000-0000-000000000301"));
station.setName("Trạm Quận 1");
em.persist(station);
```

## 13. Không Seed Lại Nếu DB Không Đổi

Nếu muốn chỉ tạo lại DB/data khi schema version thay đổi, nên tạo bảng quản lý version.

```sql
CREATE TABLE Schema_Version (
    version_key VARCHAR(50) PRIMARY KEY,
    version_value VARCHAR(50) NOT NULL
);
```

Trong Java:

```java
private static final String CURRENT_SCHEMA_VERSION = "2026_06_18_01";
```

Flow:

```java
if (isDevMode() && isSchemaVersionChanged()) {
    dropAllTables();
    createSchema();
    seedData();
    saveSchemaVersion(CURRENT_SCHEMA_VERSION);
} else {
    // chạy app bình thường
}
```

Không nên auto drop database ở môi trường thật.

Nên có flag:

```properties
app.db.reset-on-version-change=true
```

Chỉ bật khi dev/demo.

## 14. Flyway/Liquibase

Cách tốt hơn về lâu dài là dùng Flyway hoặc Liquibase.

Ví dụ Flyway:

```text
db/migration
  V1__init_schema.sql
  V2__seed_initial_data.sql
  V3__add_no_show_status.sql
```

Flyway sẽ tạo bảng:

```text
flyway_schema_history
```

và chỉ chạy migration chưa chạy.

Với project PRJ301, nếu muốn đơn giản, dùng custom `Schema_Version` là đủ. Nếu muốn chuẩn hơn, dùng Flyway.

## 15. Lộ Trình Chuyển Đổi An Toàn

Không nên đổi toàn bộ project trong một lần. Nên đi theo thứ tự:

1. Sửa `persistence.xml`, để schema generation là `none`.
2. Tạo `JPAUtil`.
3. Tạo `ServletContextListener` để đóng `EntityManagerFactory`.
4. Chuyển models sang JPA Entity.
5. Tạo seeder Java.
6. Thêm cơ chế `Schema_Version` hoặc Flyway.
7. Chuyển `AccountDAO`, `WalletDAO` trước.
8. Chuyển `StationDAO`, `CategoryDAO`, `VehicleSearchDAO`.
9. Chuyển `BookingService` sau cùng.
10. Sau khi ổn định, bỏ JDBC DAO cũ.

## 16. Các Query Quan Trọng Khi Sang JPA

### Check customer có rental active

```java
Long count = em.createQuery(
        "SELECT COUNT(r) FROM Rental r " +
        "WHERE r.customer = :customer " +
        "AND r.status IN :statuses", Long.class)
        .setParameter("customer", customer)
        .setParameter("statuses", Arrays.asList(RentalStatus.BOOKED, RentalStatus.RENTED))
        .getSingleResult();
```

### Check vehicle overlap

```java
Long count = em.createQuery(
        "SELECT COUNT(r) FROM Rental r " +
        "WHERE r.vehicle = :vehicle " +
        "AND r.status IN :statuses " +
        "AND r.startDate < :endDate " +
        "AND r.endDate > :startDate", Long.class)
        .setParameter("vehicle", vehicle)
        .setParameter("statuses", Arrays.asList(RentalStatus.BOOKED, RentalStatus.RENTED))
        .setParameter("endDate", endDate)
        .setParameter("startDate", startDate)
        .getSingleResult();
```

### Lấy xe trống theo model/trạm/ngày

```java
List<Vehicle> vehicles = em.createQuery(
        "SELECT v FROM Vehicle v " +
        "WHERE v.station = :station " +
        "AND v.model = :model " +
        "AND v.status = :status " +
        "AND NOT EXISTS (" +
        "   SELECT r FROM Rental r " +
        "   WHERE r.vehicle = v " +
        "   AND r.status IN :activeStatuses " +
        "   AND r.startDate < :endDate " +
        "   AND r.endDate > :startDate" +
        ") " +
        "ORDER BY v.licensePlate", Vehicle.class)
        .setParameter("station", station)
        .setParameter("model", model)
        .setParameter("status", VehicleStatus.AVAILABLE)
        .setParameter("activeStatuses", Arrays.asList(RentalStatus.BOOKED, RentalStatus.RENTED))
        .setParameter("endDate", endDate)
        .setParameter("startDate", startDate)
        .getResultList();
```

## 17. Kết Luận

Hướng tốt nhất cho dự án này:

- Entity dùng `UUID`, `BigDecimal`, `LocalDate`, `LocalDateTime`.
- Quan hệ dùng `@ManyToOne`, `@OneToMany`, `@OneToOne`.
- Enum dùng `@Enumerated(EnumType.STRING)`.
- Không để JPA tự `create` DB mỗi lần chạy.
- Seeder nên viết bằng Java.
- Dùng `Schema_Version` hoặc Flyway để biết khi nào cần reset/seed lại.
- Chuyển `BookingService` cuối cùng vì đây là phần có transaction phức tạp nhất.
