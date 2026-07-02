# Missing CRUD Implementation Guide

Tai lieu nay dung de chia task va implement cac CRUD con thieu cua du an E-Vehicle Rental.

## Cach Doc File Nay

File nay **khong phai** la mot file code co the copy toan bo vao project de chay ngay.

Trong file co 2 loai doan code:

```text
COPY-READY:
  Doan code co the copy vao project, nhung van can dat dung file/package/import.

SKELETON:
  Doan code minh hoa pattern. Can sua ten entity field, route, JSP attribute,
  enum, query, hoac bo sung method con thieu truoc khi chay.
```

Quy tac khi teammate dung file nay:

```text
1. Doc Business Rules cua module.
2. Tao file DAO/Service/JSP theo pattern.
3. Copy skeleton phu hop.
4. Sua field theo entity that trong src/java/models.
5. Them route vao MainController/AdminController/AdminCrudActionController.
6. Build project.
7. Test ADMIN truy cap duoc, STAFF/CUSTOMER bi chan.
```

Nhung doan code co `...` chac chan **chua chay duoc** neu chua implement tiep.

Cap nhat hien tai:

```text
Muc 3 - Station CRUD da co code copy-ready day du theo project hien tai.
Nhung muc sau Station van la plan/skeleton va can implement tiep neu muon copy chay ngay.
```

Kien truc hien tai:

```text
JSP
MainController / AdminController / AdminCrudActionController
Service
DAO
JPA Entity
SQL Server
```

Nguyen tac chung:

- Chi `ADMIN` duoc truy cap cac CRUD admin.
- JSP chi hien thi UI, khong dat business logic trong JSP.
- Service chiu trach nhiem validation va business rule.
- DAO chi query database.
- Bang transaction/audit khong hard delete: `Rental`, `Payment`, `Wallet_Transaction`, `Rental_Status_History`.
- Form create/update/delete phai dung `POST`.
- GET chi dung de hien thi list/form/detail.
- Tat ca password account moi phai hash bang BCrypt.

## 1. Trang Thai Hien Tai

Da co CRUD:

```text
Account
Vehicle_Model
Vehicle_Model_Image
```

Con thieu CRUD/view management:

```text
Station
Category
Vehicle
Discount
Rental
Payment
Extra_Charge
Incident_Report
Vehicle_Maintenance
Wallet
Wallet_Transaction
Rental_Discount
Rental_Status_History
Review
```

## 2. Pattern Chung Cho Moi CRUD

### 2.1. File Can Tao

Vi du voi Station:

```text
src/java/daos/AdminStationDAO.java
src/java/services/AdminStationService.java

Them route GET vao:
src/java/controllers/AdminController.java

Them POST action vao:
src/java/controllers/AdminCrudActionController.java

Them JSP:
web/WEB-INF/views/admin/stations/list.jsp
web/WEB-INF/views/admin/stations/form.jsp
web/WEB-INF/views/admin/stations/detail.jsp
```

Neu module chi view-only thi khong can form/save/delete.

### 2.2. GET Flow

```text
MainController action=admin-stations
  -> AdminController
    -> requireAdmin()
    -> Service.search(...)
    -> request.setAttribute(...)
    -> forward JSP list
```

### 2.3. POST Flow

```text
POST /admin/stations/save
  -> AdminCrudActionController
    -> requireAdmin()
    -> Service.create/update(...)
    -> flash success/error
    -> redirect action=admin-stations
```

### 2.4. Flash Message Pattern

```java
private void flash(HttpServletRequest request, String key, String value) {
    request.getSession().setAttribute(key, value);
}
```

Trong `AdminController` dung lai `consumeFlash(request)`.

### 2.5. Pagination

Dung lai ham `paginate(request, items)` trong `AdminController`.

Moi list JSP nen include:

```jsp
<%@ include file="/WEB-INF/jspf/admin-pagination.jspf" %>
```

## 3. Station CRUD - Copy-Ready Module

Phan nay la module mau **co the copy vao project de chay** theo cau truc hien tai. No khop voi entity `models.Station` dang co cac field:

```text
stationId
name
address
contactNumber
```

### 3.1. Business Rules

- `name` bat buoc, toi da 100 ky tu.
- `address` bat buoc.
- `contactNumber` optional, neu nhap thi chi nhan so, dau `+`, dau `-`, khoang trang, do dai 8-20 ky tu.
- Khong cho tao/sua trung ten tram.
- Khong xoa station neu da co `Vehicle.stationId` hoac `Rental.pickupStationId` lien ket.
- Tat ca action CRUD chi danh cho `ADMIN`.

### 3.2. Tao File `src/java/daos/AdminStationDAO.java`

Loai code: `COPY-READY`

```java
package daos;

import java.util.List;
import javax.persistence.EntityManager;
import models.Station;

public class AdminStationDAO {

    public List<Station> search(EntityManager em, String keyword) {
        String key = keyword == null ? "" : keyword.trim().toLowerCase();
        return em.createQuery(
                "SELECT s FROM Station s "
                + "WHERE :key = '' "
                + "OR LOWER(s.name) LIKE :likeKey "
                + "OR LOWER(s.address) LIKE :likeKey "
                + "OR LOWER(COALESCE(s.contactNumber, '')) LIKE :likeKey "
                + "ORDER BY s.name",
                Station.class)
                .setParameter("key", key)
                .setParameter("likeKey", "%" + key + "%")
                .getResultList();
    }

    public Station findById(EntityManager em, String stationId) {
        String id = trim(stationId);
        return id.isEmpty() ? null : em.find(Station.class, id);
    }

    public boolean nameExists(EntityManager em, String name, String excludeId) {
        String id = trim(excludeId);
        Long count = em.createQuery(
                "SELECT COUNT(s) FROM Station s "
                + "WHERE LOWER(s.name) = LOWER(:name) "
                + "AND (:excludeId = '' OR s.stationId <> :excludeId)",
                Long.class)
                .setParameter("name", trim(name))
                .setParameter("excludeId", id)
                .getSingleResult();
        return count != null && count > 0;
    }

    public boolean hasLinkedData(EntityManager em, String stationId) {
        Long vehicles = em.createQuery(
                "SELECT COUNT(v) FROM Vehicle v WHERE v.stationId = :stationId",
                Long.class)
                .setParameter("stationId", stationId)
                .getSingleResult();
        Long rentals = em.createQuery(
                "SELECT COUNT(r) FROM Rental r WHERE r.pickupStationId = :stationId",
                Long.class)
                .setParameter("stationId", stationId)
                .getSingleResult();
        return vehicles > 0 || rentals > 0;
    }

    public void create(EntityManager em, Station station) {
        em.persist(station);
    }

    public void delete(EntityManager em, Station station) {
        em.remove(station);
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }
}
```

### 3.3. Tao File `src/java/services/AdminStationService.java`

Loai code: `COPY-READY`

```java
package services;

import daos.AdminStationDAO;
import java.util.List;
import java.util.UUID;
import models.Station;
import utils.JPAUtil;

public class AdminStationService {
    private final AdminStationDAO stationDAO = new AdminStationDAO();

    public List<Station> search(String keyword) {
        return JPAUtil.execute(em -> stationDAO.search(em, keyword));
    }

    public Station findById(String stationId) {
        return JPAUtil.execute(em -> stationDAO.findById(em, stationId));
    }

    public void create(String name, String address, String contactNumber) {
        validateStation(name, address, contactNumber);
        JPAUtil.executeInTransaction(em -> {
            if (stationDAO.nameExists(em, name, null)) {
                throw new IllegalArgumentException("Station name already exists.");
            }
            Station station = new Station();
            station.setStationId(UUID.randomUUID().toString());
            station.setName(trim(name));
            station.setAddress(trim(address));
            station.setContactNumber(blankToNull(contactNumber));
            stationDAO.create(em, station);
            return null;
        });
    }

    public void update(String stationId, String name, String address, String contactNumber) {
        validateRequired(stationId, "Station ID");
        validateStation(name, address, contactNumber);
        JPAUtil.executeInTransaction(em -> {
            Station station = stationDAO.findById(em, stationId);
            if (station == null) {
                throw new IllegalArgumentException("Station not found.");
            }
            if (stationDAO.nameExists(em, name, stationId)) {
                throw new IllegalArgumentException("Station name already exists.");
            }
            station.setName(trim(name));
            station.setAddress(trim(address));
            station.setContactNumber(blankToNull(contactNumber));
            return null;
        });
    }

    public void delete(String stationId) {
        validateRequired(stationId, "Station ID");
        JPAUtil.executeInTransaction(em -> {
            Station station = stationDAO.findById(em, stationId);
            if (station == null) {
                throw new IllegalArgumentException("Station not found.");
            }
            if (stationDAO.hasLinkedData(em, stationId)) {
                throw new IllegalStateException("Cannot delete station because vehicles or rentals are linked.");
            }
            stationDAO.delete(em, station);
            return null;
        });
    }

    private void validateStation(String name, String address, String contactNumber) {
        validateRequired(name, "Station name");
        validateRequired(address, "Address");
        if (trim(name).length() > 100) {
            throw new IllegalArgumentException("Station name must be at most 100 characters.");
        }
        String phone = trim(contactNumber);
        if (!phone.isEmpty() && !phone.matches("[0-9+\\- ]{8,20}")) {
            throw new IllegalArgumentException("Invalid contact number.");
        }
    }

    private void validateRequired(String value, String fieldName) {
        if (trim(value).isEmpty()) {
            throw new IllegalArgumentException(fieldName + " is required.");
        }
    }

    private String blankToNull(String value) {
        String trimmed = trim(value);
        return trimmed.isEmpty() ? null : trimmed;
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }
}
```

### 3.4. Sua `src/java/controllers/AdminController.java`

Loai code: `COPY-READY SNIPPET`

Them import:

```java
import services.AdminStationService;
```

Them field trong class:

```java
private final AdminStationService stationService = new AdminStationService();
```

Them URL pattern vao `@WebServlet`:

```java
"/admin/stations/form",
"/admin/stations/detail",
```

Them vao method `handleCrudGet(...)`, dat sau block accounts hoac truoc vehicle-models:

```java
if ("/admin/stations".equals(path)) {
    configureAdminShell(request, admin, "stations", "Stations", "CRUD", "Search station, address, phone");
    List<models.Station> stations = stationService.search(request.getParameter("keyword"));
    request.setAttribute("stations", paginate(request, stations));
    request.setAttribute("keyword", paramOrDefault(request, "keyword", ""));
    consumeFlash(request);
    request.getRequestDispatcher("/WEB-INF/views/admin/stations/list.jsp").forward(request, response);
    return true;
}
if ("/admin/stations/form".equals(path)) {
    configureAdminShell(request, admin, "stations",
            isBlank(request.getParameter("id")) ? "Create Station" : "Edit Station",
            "CRUD", "Search stations");
    request.setAttribute("station", stationService.findById(request.getParameter("id")));
    consumeFlash(request);
    request.getRequestDispatcher("/WEB-INF/views/admin/stations/form.jsp").forward(request, response);
    return true;
}
if ("/admin/stations/detail".equals(path)) {
    configureAdminShell(request, admin, "stations", "Station Detail", "CRUD", "Search stations");
    request.setAttribute("station", stationService.findById(request.getParameter("id")));
    consumeFlash(request);
    request.getRequestDispatcher("/WEB-INF/views/admin/stations/detail.jsp").forward(request, response);
    return true;
}
```

### 3.5. Sua `src/java/controllers/AdminCrudActionController.java`

Loai code: `COPY-READY SNIPPET`

Them import:

```java
import services.AdminStationService;
```

Them field:

```java
private final AdminStationService stationService = new AdminStationService();
```

Them URL pattern vao `@WebServlet`:

```java
"/admin/stations/save",
"/admin/stations/delete",
```

Them vao `doPost(...)`, dat sau block account hoac truoc vehicle-models:

```java
if ("/admin/stations/save".equals(path)) {
    saveStation(request);
    flash(request, "adminSuccess", "Station saved successfully.");
    response.sendRedirect(request.getContextPath() + "?action=admin-stations");
    return;
}
if ("/admin/stations/delete".equals(path)) {
    stationService.delete(request.getParameter("stationId"));
    flash(request, "adminSuccess", "Station deleted successfully.");
    response.sendRedirect(request.getContextPath() + "?action=admin-stations");
    return;
}
```

Them method nay vao class:

```java
private void saveStation(HttpServletRequest request) {
    String stationId = trim(request.getParameter("stationId"));
    if (stationId.isEmpty()) {
        stationService.create(
                request.getParameter("name"),
                request.getParameter("address"),
                request.getParameter("contactNumber"));
    } else {
        stationService.update(
                stationId,
                request.getParameter("name"),
                request.getParameter("address"),
                request.getParameter("contactNumber"));
    }
}
```

Them vao `fallbackUrl(...)`, dat truoc fallback dashboard:

```java
if (path.contains("stations")) {
    String id = trim(request.getParameter("stationId"));
    return context + "?action=admin-station-form" + (id.isEmpty() ? "" : "&id=" + encode(id));
}
```

### 3.6. Sua `src/java/controllers/MainController.java`

Loai code: `COPY-READY SNIPPET`

Chen 2 block moi ngay sau block `admin-stations` hien tai:

```java
} else if (action.equals("admin-station-form")) {
    url = "/admin/stations/form";
} else if (action.equals("admin-station-detail")) {
    url = "/admin/stations/detail";
```

Sau khi chen, doan route se co dang:

```java
} else if (action.equals("admin-stations")) {
    url = "/admin/stations";
} else if (action.equals("admin-station-form")) {
    url = "/admin/stations/form";
} else if (action.equals("admin-station-detail")) {
    url = "/admin/stations/detail";
} else if (action.equals("admin-categories")) {
    url = "/admin/categories";
}
```

### 3.7. Tao File `web/WEB-INF/views/admin/stations/list.jsp`

Loai code: `COPY-READY`

```jsp
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Stations - Admin</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin.css">
</head>
<body class="admin-body">
<div class="admin-shell">
    <%@ include file="/WEB-INF/jspf/admin-sidebar.jspf" %>
    <main class="admin-main">
        <%@ include file="/WEB-INF/jspf/admin-topbar.jspf" %>
        <div class="admin-content">
            <c:if test="${not empty adminSuccess}"><div class="admin-message success"><c:out value="${adminSuccess}"/></div></c:if>
            <c:if test="${not empty adminError}"><div class="admin-message error"><c:out value="${adminError}"/></div></c:if>

            <section class="admin-panel">
                <div class="admin-panel-header">
                    <div>
                        <h2>Station Management</h2>
                        <p>Create and update stations used for pickup, return, and operations reports.</p>
                    </div>
                    <a class="admin-button" href="${pageContext.request.contextPath}?action=admin-station-form">Add Station</a>
                </div>

                <form class="form-row" action="${pageContext.request.contextPath}/" method="GET">
                    <input type="hidden" name="action" value="admin-stations">
                    <input type="search" name="keyword" value="${keyword}" placeholder="Search station, address, phone">
                    <button class="admin-button" type="submit">Filter</button>
                </form>

                <div class="admin-table-wrap">
                    <table class="admin-table">
                        <thead>
                        <tr>
                            <th>Station</th>
                            <th>Address</th>
                            <th>Contact</th>
                            <th>Actions</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="station" items="${stations}">
                            <tr>
                                <td><strong><c:out value="${station.name}"/></strong></td>
                                <td><c:out value="${station.address}"/></td>
                                <td><c:out value="${empty station.contactNumber ? '-' : station.contactNumber}"/></td>
                                <td>
                                    <div class="inline-actions">
                                        <a class="admin-button light" href="${pageContext.request.contextPath}?action=admin-station-detail&id=${station.stationId}">View</a>
                                        <a class="admin-button light" href="${pageContext.request.contextPath}?action=admin-station-form&id=${station.stationId}">Edit</a>
                                        <form class="inline-form" method="POST" action="${pageContext.request.contextPath}/admin/stations/delete" onsubmit="return confirm('Delete this station?');">
                                            <input type="hidden" name="stationId" value="${station.stationId}">
                                            <button class="danger-button" type="submit">Delete</button>
                                        </form>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty stations}">
                            <tr>
                                <td colspan="4">No stations found.</td>
                            </tr>
                        </c:if>
                        </tbody>
                    </table>
                </div>

                <%@ include file="/WEB-INF/jspf/admin-pagination.jspf" %>
            </section>
        </div>
    </main>
</div>
</body>
</html>
```

### 3.8. Tao File `web/WEB-INF/views/admin/stations/form.jsp`

Loai code: `COPY-READY`

```jsp
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${empty station ? 'Create Station' : 'Edit Station'} - Admin</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin.css">
</head>
<body class="admin-body">
<div class="admin-shell">
    <%@ include file="/WEB-INF/jspf/admin-sidebar.jspf" %>
    <main class="admin-main">
        <%@ include file="/WEB-INF/jspf/admin-topbar.jspf" %>
        <div class="admin-content">
            <c:if test="${not empty adminError}"><div class="admin-message error"><c:out value="${adminError}"/></div></c:if>

            <section class="admin-panel">
                <div class="admin-panel-header">
                    <div>
                        <h2>${empty station ? 'Create Station' : 'Edit Station'}</h2>
                        <p>Manage pickup station name, address, and contact number.</p>
                    </div>
                    <a class="admin-button light" href="${pageContext.request.contextPath}?action=admin-stations">Back</a>
                </div>

                <form class="admin-form" action="${pageContext.request.contextPath}/admin/stations/save" method="POST">
                    <input type="hidden" name="stationId" value="${station.stationId}">
                    <div class="admin-form-grid">
                        <div class="admin-field">
                            <label>Station Name</label>
                            <input type="text" name="name" value="${station.name}" maxlength="100" required>
                        </div>
                        <div class="admin-field">
                            <label>Contact Number</label>
                            <input type="text" name="contactNumber" value="${station.contactNumber}" maxlength="20">
                        </div>
                        <div class="admin-field full">
                            <label>Address</label>
                            <textarea name="address" rows="4" required>${station.address}</textarea>
                        </div>
                    </div>
                    <div class="admin-form-actions">
                        <button class="admin-button" type="submit">Save Station</button>
                        <a class="admin-button light" href="${pageContext.request.contextPath}?action=admin-stations">Cancel</a>
                    </div>
                </form>
            </section>
        </div>
    </main>
</div>
</body>
</html>
```

### 3.9. Tao File `web/WEB-INF/views/admin/stations/detail.jsp`

Loai code: `COPY-READY`

```jsp
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Station Detail - Admin</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin.css">
</head>
<body class="admin-body">
<div class="admin-shell">
    <%@ include file="/WEB-INF/jspf/admin-sidebar.jspf" %>
    <main class="admin-main">
        <%@ include file="/WEB-INF/jspf/admin-topbar.jspf" %>
        <div class="admin-content">
            <section class="admin-panel">
                <div class="admin-panel-header">
                    <div>
                        <h2>Station Detail</h2>
                        <p>Review station information before editing linked data.</p>
                    </div>
                    <div class="inline-actions">
                        <a class="admin-button light" href="${pageContext.request.contextPath}?action=admin-stations">Back</a>
                        <c:if test="${not empty station}">
                            <a class="admin-button" href="${pageContext.request.contextPath}?action=admin-station-form&id=${station.stationId}">Edit</a>
                        </c:if>
                    </div>
                </div>

                <c:choose>
                    <c:when test="${empty station}">
                        <p>Station not found.</p>
                    </c:when>
                    <c:otherwise>
                        <div class="admin-detail-grid">
                            <div class="admin-detail-item">
                                <span>Station ID</span>
                                <strong><c:out value="${station.stationId}"/></strong>
                            </div>
                            <div class="admin-detail-item">
                                <span>Name</span>
                                <strong><c:out value="${station.name}"/></strong>
                            </div>
                            <div class="admin-detail-item">
                                <span>Contact</span>
                                <strong><c:out value="${empty station.contactNumber ? '-' : station.contactNumber}"/></strong>
                            </div>
                            <div class="admin-detail-item full">
                                <span>Address</span>
                                <strong><c:out value="${station.address}"/></strong>
                            </div>
                        </div>
                    </c:otherwise>
                </c:choose>
            </section>
        </div>
    </main>
</div>
</body>
</html>
```

### 3.10. Build/Test Sau Khi Copy

```text
1. Clean and Build.
2. Login ADMIN.
3. Mo /?action=admin-stations.
4. Test create station.
5. Test update station.
6. Test search.
7. Test delete station chua co vehicle/rental.
8. Test delete station da co vehicle/rental: phai bi chan va hien flash error.
9. Login CUSTOMER/STAFF roi truy cap /?action=admin-stations: phai bi chan boi requireAdmin.
```

## 4. Category CRUD

### 4.1. Chuc Nang

```text
List categories
Search by name
Create category
Update category
View detail
Delete only if no Vehicle_Model
```

### 4.2. Business Rules

- `name` bat buoc.
- `name` unique.
- Khong delete category neu co `Vehicle_Model` lien quan.

### 4.3. Validation

```java
validateRequired(name, "Category name");
```

### 4.4. DAO Query Can Co

Loai code: `COPY-READY SAU KHI SUA TEN FIELD`

Can sua:

- Neu entity `VehicleModel` dung field `categoryId` thi giu nguyen.
- Neu dung relationship `category` thi doi query thanh `m.category.categoryId`.

```java
public boolean hasVehicleModels(EntityManager em, String categoryId) {
    Long count = em.createQuery(
            "SELECT COUNT(m) FROM VehicleModel m WHERE m.categoryId = :categoryId",
            Long.class)
            .setParameter("categoryId", categoryId)
            .getSingleResult();
    return count != null && count > 0;
}
```

### 4.5. Service Rules

```text
create:
  validate name
  check unique
  persist

update:
  find category
  validate name
  check unique excluding current id
  update name

delete:
  find category
  check no Vehicle_Model
  remove
```

## 5. Vehicle CRUD

### 5.1. Chuc Nang

```text
List vehicles
Search license plate/model/station/status
Create vehicle
Update vehicle
View detail
Delete only if no history
```

### 5.2. Fields

```text
model_id
station_id
license_plate
color
battery_level
status
```

### 5.3. Business Rules

- `model_id` bat buoc va phai ton tai.
- `station_id` bat buoc va phai ton tai.
- `license_plate` bat buoc va unique.
- `battery_level` tu 0 den 100.
- `status` chi nhan:
  - `AVAILABLE`
  - `RENTED`
  - `MAINTENANCE`
- Khong cho set `AVAILABLE` neu vehicle dang co Rental `RENTED`.
- Khong cho set `RENTED` bang tay neu khong co Rental `RENTED`.
- Delete chi cho phep neu vehicle chua co:
  - Rental
  - Incident_Report
  - Vehicle_Maintenance
- Neu da co history, block delete. Sau nay co the them status `RETIRED`.

### 5.4. Validation Code

Loai code: `SKELETON`

Can sua:

- Them helper `validateRequired`, `parseInt`.
- Bọc `VehicleStatus.valueOf(statusValue)` bằng try/catch để báo lỗi đẹp nếu status sai.

```java
private void validateVehicle(String modelId, String stationId, String licensePlate,
        String batteryLevelValue, String statusValue) {
    validateRequired(modelId, "Vehicle model");
    validateRequired(stationId, "Station");
    validateRequired(licensePlate, "License plate");

    int batteryLevel = parseInt(batteryLevelValue, "Battery level");
    if (batteryLevel < 0 || batteryLevel > 100) {
        throw new IllegalArgumentException("Battery level must be from 0 to 100.");
    }

    VehicleStatus status = VehicleStatus.valueOf(statusValue);
}
```

### 5.5. DAO Query Can Co

Loai code: `SKELETON`

Can sua:

- Query `JOIN FETCH v.model` chỉ chạy nếu entity `Vehicle` đã map relationship `model`.
- Nếu chưa map relationship, dùng native query hoặc JPQL theo id rồi join thủ công bằng DTO.
- Bien `key` trong vi du can khai bao truoc khi set parameter.

```java
public List<Vehicle> search(EntityManager em, String keyword, String stationId,
        String modelId, VehicleStatus status) {
    String jpql =
            "SELECT v FROM Vehicle v JOIN FETCH v.model m JOIN FETCH v.station s " +
            "WHERE (:keyword = '' OR LOWER(v.licensePlate) LIKE :likeKey OR LOWER(m.name) LIKE :likeKey) " +
            "AND (:stationId IS NULL OR v.stationId = :stationId) " +
            "AND (:modelId IS NULL OR v.modelId = :modelId) " +
            "AND (:status IS NULL OR v.status = :status) " +
            "ORDER BY v.licensePlate";
    return em.createQuery(jpql, Vehicle.class)
            .setParameter("keyword", key)
            .setParameter("likeKey", "%" + key + "%")
            .setParameter("stationId", stationId)
            .setParameter("modelId", modelId)
            .setParameter("status", status)
            .getResultList();
}

public boolean licensePlateExists(EntityManager em, String plate, String excludeId) { ... }

public boolean hasActiveRentedRental(EntityManager em, String vehicleId) {
    Long count = em.createQuery(
            "SELECT COUNT(r) FROM Rental r WHERE r.vehicleId = :vehicleId AND r.status = :status",
            Long.class)
            .setParameter("vehicleId", vehicleId)
            .setParameter("status", RentalStatus.RENTED)
            .getSingleResult();
    return count != null && count > 0;
}
```

### 5.6. Service Status Rule

Loai code: `COPY-READY SAU KHI CO vehicleDAO.hasActiveRentedRental`

```java
private void validateStatusTransition(EntityManager em, Vehicle vehicle, VehicleStatus newStatus) {
    if (newStatus == VehicleStatus.AVAILABLE && vehicleDAO.hasActiveRentedRental(em, vehicle.getVehicleId())) {
        throw new IllegalStateException("Cannot set AVAILABLE while vehicle has an active RENTED rental.");
    }
    if (newStatus == VehicleStatus.RENTED && !vehicleDAO.hasActiveRentedRental(em, vehicle.getVehicleId())) {
        throw new IllegalStateException("Cannot set RENTED manually without active rental.");
    }
}
```

## 6. Discount CRUD

### 6.1. Chuc Nang

```text
List discounts
Search by code/status/date
Create discount
Update discount
Disable/delete discount
View usage
```

### 6.2. Fields

Theo entity hien tai:

```text
discount_id
code
discount_percent
expired_at
quantity
```

Neu script DB cua team co them status/start_date/type thi service can map them sau.

### 6.3. Business Rules

- `code` bat buoc.
- `code` unique, nen uppercase khi save.
- `discount_percent` tu 1 den 100.
- `quantity >= 0`.
- `expired_at` phai sau ngay hien tai khi create.
- Khong delete discount neu da co `Rental_Discount`.
- Neu da duoc dung, chi cho update `quantity` va `expired_at`, khong doi `code`.

### 6.4. Validation

```java
private void validateDiscount(String code, String percentValue, String quantityValue, Timestamp expiredAt) {
    validateRequired(code, "Discount code");
    int percent = parseInt(percentValue, "Discount percent");
    if (percent < 1 || percent > 100) {
        throw new IllegalArgumentException("Discount percent must be from 1 to 100.");
    }
    int quantity = parseInt(quantityValue, "Quantity");
    if (quantity < 0) {
        throw new IllegalArgumentException("Quantity must not be negative.");
    }
    if (expiredAt == null || expiredAt.before(new Timestamp(System.currentTimeMillis()))) {
        throw new IllegalArgumentException("Expired date must be in the future.");
    }
}
```

## 7. Rental Management

Rental la transaction data. Khong lam CRUD day du. Lam view/detail va action co rule.

### 7.1. Chuc Nang

```text
List rentals
Search rental/customer/email/phone/license plate
Filter status/station/date range
View detail
View payment
View status history
Cancel BOOKED rental
```

### 7.2. Business Rules

- Khong update truc tiep `total_amount`, `start_date`, `end_date`.
- Chi cho cancel neu `Rental.status = BOOKED`.
- Khi cancel:
  - Update Rental.status = `CANCELLED`.
  - Insert Rental_Status_History `CANCELLED`.
  - Neu Vehicle.status = `RENTED` do booking da thanh toan nhung chua pickup, set Vehicle.status = `AVAILABLE`.
  - Khong xoa Payment.
- Khong cancel rental da `RENTED`, `COMPLETED`, `NO_SHOW`.

### 7.3. Cancel Service Skeleton

Loai code: `SKELETON`

Can sua:

- Can co `rentalDAO.findForUpdate`.
- Can import `LockModeType`, `UUID`, `RentalStatusHistory`, `VehicleStatus`.
- Kiem tra entity `Rental` co field `vehicleId`.

```java
public void cancelBookedRental(String rentalId) {
    validateRequired(rentalId, "Rental ID");
    JPAUtil.executeInTransaction(em -> {
        Rental rental = rentalDAO.findForUpdate(em, rentalId);
        if (rental == null) throw new IllegalArgumentException("Rental not found.");
        if (rental.getStatus() != RentalStatus.BOOKED) {
            throw new IllegalStateException("Only BOOKED rental can be cancelled.");
        }

        Vehicle vehicle = em.find(Vehicle.class, rental.getVehicleId(), LockModeType.PESSIMISTIC_WRITE);
        rental.setStatus(RentalStatus.CANCELLED);
        if (vehicle != null && vehicle.getStatus() == VehicleStatus.RENTED) {
            vehicle.setStatus(VehicleStatus.AVAILABLE);
        }
        em.persist(new RentalStatusHistory(UUID.randomUUID().toString(),
                rentalId, RentalStatus.CANCELLED, now()));
        return null;
    });
}
```

## 8. Payment Management

Payment khong nen CRUD day du.

### 8.1. Chuc Nang

```text
List payments
Search payment/rental/customer/transaction code
Filter method/status/type/date
View payment detail
Mark pending payment failed
Confirm CASH pending payment neu co
```

### 8.2. Business Rules

- Khong update amount truc tiep.
- Khong delete payment.
- Chi `PENDING` moi duoc mark `FAILED`.
- Chi `CASH + PENDING` moi duoc mark `SUCCESS` thu cong.
- Moi update status phai ghi ro ly do trong message/flash. Neu can audit sau thi them bang history.

### 8.3. Validation

```java
if (payment.getStatus() != PaymentStatus.PENDING) {
    throw new IllegalStateException("Only pending payment can be updated.");
}
```

## 9. Extra Charge Management

### 9.1. Chuc Nang

```text
List extra charges
Search rental/customer/type/status/date
View detail
Create manual charge
Update unpaid charge
Mark paid by CASH
Cancel unpaid charge
```

### 9.2. Business Rules

- Charge type hop le:
  - `LATE_FEE`
  - `DAMAGE_FEE`
  - `CLEANING_FEE`
  - `LOST_ACCESSORY`
  - `OTHER`
- Manual create nen chi cho:
  - `CLEANING_FEE`
  - `LOST_ACCESSORY`
  - `OTHER`
- `amount > 0`.
- Khong sua amount neu charge status `PAID`.
- Khong cancel charge status `PAID`.
- Neu mark paid CASH:
  - Extra_Charge.status = `PAID`.
  - Extra_Charge.paid_at = now.
  - Tao Payment method `CASH`, status `SUCCESS`, type tu charge type.

### 9.3. Service Skeleton

Loai code: `SKELETON`

Can sua:

- Can co `extraChargeDAO.findForUpdate`.
- Can co enum `ExtraChargeStatus`, `PaymentMethod`, `PaymentStatus`.
- Can implement `mapPaymentType(charge.getChargeType())`.
- Kiem tra entity `ExtraCharge` co getter/setter `chargeId`, `rentalId`, `amount`, `status`, `paidAt`.

```java
public void markPaidByCash(String chargeId) {
    JPAUtil.executeInTransaction(em -> {
        ExtraCharge charge = extraChargeDAO.findForUpdate(em, chargeId);
        if (charge == null) throw new IllegalArgumentException("Charge not found.");
        if (charge.getStatus() == ExtraChargeStatus.PAID) {
            throw new IllegalStateException("Charge already paid.");
        }
        charge.setStatus(ExtraChargeStatus.PAID);
        charge.setPaidAt(now());

        Payment payment = new Payment();
        payment.setPaymentId(UUID.randomUUID().toString());
        payment.setRentalId(charge.getRentalId());
        payment.setChargeId(charge.getChargeId());
        payment.setAmount(charge.getAmount().doubleValue());
        payment.setPaymentMethod(PaymentMethod.CASH);
        payment.setPaymentType(mapPaymentType(charge.getChargeType()));
        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setPaymentDate(now());
        payment.setTransactionCode("CASH_CHARGE_" + System.currentTimeMillis());
        em.persist(payment);
        return null;
    });
}
```

## 10. Incident Report Management

### 10.1. Chuc Nang

```text
List incidents
Search rental/vehicle/license plate
Filter severity/date
View detail
Update description/severity
```

### 10.2. Business Rules

- Incident duoc tao chinh tu Return flow.
- Admin/Staff chi nen update description/severity neu can danh gia lai.
- Khong hard delete incident.
- Severity hop le:
  - `LOW`
  - `MEDIUM`
  - `HIGH`

### 10.3. Validation

```java
validateRequired(description, "Incident description");
IncidentSeverity severity = IncidentSeverity.valueOf(severityValue);
```

## 11. Vehicle Maintenance Management

Da co staff maintenance flow, admin CRUD con thieu create/detail/update note.

### 11.1. Chuc Nang

```text
List maintenance
Search vehicle/status/date
View detail
Create maintenance manually
Update description while PENDING
Mark completed
```

### 11.2. Business Rules

- Create maintenance:
  - Vehicle phai ton tai.
  - Description bat buoc.
  - Status default `PENDING`.
  - Update Vehicle.status = `MAINTENANCE`.
- Update description chi khi status `PENDING`.
- Mark completed:
  - Maintenance.status phai `PENDING`.
  - Vehicle.status phai `MAINTENANCE`.
  - Update maintenance.status = `COMPLETED`.
  - Update vehicle.status = `AVAILABLE`.

### 11.3. Service Skeleton

Loai code: `SKELETON`

Can sua:

- Kiem tra constructor `VehicleMaintenance(...)` co dung tham so trong model hien tai.
- Can import `LockModeType`, `UUID`, `VehicleStatus`, `MaintenanceStatus`.

```java
public void createMaintenance(String vehicleId, String description) {
    validateRequired(vehicleId, "Vehicle");
    validateRequired(description, "Description");
    JPAUtil.executeInTransaction(em -> {
        Vehicle vehicle = em.find(Vehicle.class, vehicleId, LockModeType.PESSIMISTIC_WRITE);
        if (vehicle == null) throw new IllegalArgumentException("Vehicle not found.");
        if (vehicle.getStatus() == VehicleStatus.RENTED) {
            throw new IllegalStateException("Cannot move rented vehicle to maintenance manually.");
        }
        VehicleMaintenance maintenance = new VehicleMaintenance(
                UUID.randomUUID().toString(),
                vehicleId,
                description.trim(),
                now(),
                MaintenanceStatus.PENDING);
        em.persist(maintenance);
        vehicle.setStatus(VehicleStatus.MAINTENANCE);
        return null;
    });
}
```

## 12. Wallet And Wallet Transaction View

Khong lam CRUD day du vi lien quan tien.

### 12.1. Wallet Chuc Nang

```text
List wallets
Search customer/email/phone
View wallet detail
View transactions
Manual adjustment neu can demo
```

### 12.2. Business Rules

- Khong delete wallet.
- Khong update balance truc tiep.
- Neu admin dieu chinh balance:
  - Tao Wallet_Transaction type moi, vi enum hien tai chua co `ADJUSTMENT`.
  - Neu chua muon sua enum/DB thi chua implement adjustment.
- `Wallet_Transaction` la audit log, khong delete/update.

### 12.3. View Query

Loai code: `COPY-READY SAU KHI KIEM TRA FIELD`

Can sua:

- Neu `WalletTransaction` dung relationship `wallet` thay vi `walletId`, sua query theo relationship.

```java
public List<WalletTransaction> findTransactions(EntityManager em, String walletId) {
    return em.createQuery(
            "SELECT t FROM WalletTransaction t WHERE t.walletId = :walletId ORDER BY t.createdAt DESC",
            WalletTransaction.class)
            .setParameter("walletId", walletId)
            .getResultList();
}
```

## 13. Rental Discount View

### 13.1. Chuc Nang

```text
List rental discounts
Search rental/customer/discount code
View detail
```

### 13.2. Business Rules

- Khong create/update/delete trong admin.
- `Rental_Discount` chi duoc tao tu BookingService khi customer apply discount hop le.
- Neu can sua sai du lieu, lam qua SQL/admin internal, khong dua len UI demo.

## 14. Rental Status History View

### 14.1. Chuc Nang

```text
List rental status history
Search rental ID
Filter status/date
View rental detail
```

### 14.2. Business Rules

- Audit log, khong update/delete.
- Moi thay doi Rental.status trong service phai insert history.

## 15. Review Management

### 15.1. Chuc Nang

```text
List reviews
Search customer/model/rating/date
View review detail
Delete review neu bat buoc
```

### 15.2. Business Rules

- DB hien tai chua co `status` cho Review, nen khuyen nghi view-only truoc.
- Neu can moderation dep hon, them column:

```sql
status VARCHAR(20) DEFAULT 'VISIBLE'
CHECK (status IN ('VISIBLE', 'HIDDEN'))
```

- Sau khi co status:
  - Hide review thay vi delete.
  - Customer khong thay review `HIDDEN`.

## 16. Code Can Them Vao AdminController

Vi du route cho Station:

Loai code: `SKELETON`

Can sua:

- Them field service o dau class.
- Them block vao `handleCrudGet(...)`.
- Kiem tra action name trong `MainController`.
- Kiem tra duong dan JSP da tao dung chua.

```java
private final AdminStationService stationService = new AdminStationService();

if ("/admin/stations".equals(path)) {
    configureAdminShell(request, admin, "stations", "Stations", "CRUD", "Search station");
    request.setAttribute("stations", paginate(request,
            stationService.search(request.getParameter("keyword"))));
    request.setAttribute("keyword", paramOrDefault(request, "keyword", ""));
    consumeFlash(request);
    request.getRequestDispatcher("/WEB-INF/views/admin/stations/list.jsp").forward(request, response);
    return true;
}

if ("/admin/stations/form".equals(path)) {
    configureAdminShell(request, admin, "stations",
            isBlank(request.getParameter("id")) ? "Create Station" : "Edit Station",
            "CRUD", "Search station");
    request.setAttribute("station", stationService.findById(request.getParameter("id")));
    consumeFlash(request);
    request.getRequestDispatcher("/WEB-INF/views/admin/stations/form.jsp").forward(request, response);
    return true;
}
```

## 17. Code Can Them Vao AdminCrudActionController

Vi du Station:

Loai code: `SKELETON`

Can sua:

- Them URL patterns vao annotation hien co, khong tao duplicate servlet.
- Them `AdminStationService stationService`.
- Them `fallbackUrl` cho path stations.

```java
@WebServlet(name = "AdminCrudActionController", urlPatterns = {
    "/admin/stations/save",
    "/admin/stations/delete"
})
```

```java
if ("/admin/stations/save".equals(path)) {
    saveStation(request);
    flash(request, "adminSuccess", "Station saved successfully.");
    response.sendRedirect(request.getContextPath() + "?action=admin-stations");
    return;
}

if ("/admin/stations/delete".equals(path)) {
    stationService.delete(request.getParameter("stationId"));
    flash(request, "adminSuccess", "Station deleted successfully.");
    response.sendRedirect(request.getContextPath() + "?action=admin-stations");
    return;
}
```

```java
private void saveStation(HttpServletRequest request) {
    String stationId = trim(request.getParameter("stationId"));
    if (stationId.isEmpty()) {
        stationService.create(
                request.getParameter("name"),
                request.getParameter("address"),
                request.getParameter("phone"));
    } else {
        stationService.update(
                stationId,
                request.getParameter("name"),
                request.getParameter("address"),
                request.getParameter("phone"));
    }
}
```

## 18. JSP List Pattern

Loai code: `SKELETON JSP`

Can sua:

- Dam bao `stations`, `keyword`, `adminSuccess`, `adminError`, `adminPagination` duoc set tu controller.
- Kiem tra class CSS `admin-button danger` co ton tai, neu chua co thi them CSS hoac doi class.
- Nut delete nen co confirm JS neu muon tranh bam nham.

```jsp
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Stations - Admin</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin.css">
</head>
<body class="admin-body">
<div class="admin-shell">
    <%@ include file="/WEB-INF/jspf/admin-sidebar.jspf" %>
    <main class="admin-main">
        <%@ include file="/WEB-INF/jspf/admin-topbar.jspf" %>
        <div class="admin-content">
            <section class="admin-section admin-panel">
                <div class="admin-panel-header">
                    <div>
                        <h2>Stations</h2>
                        <p>Manage pickup and return stations.</p>
                    </div>
                    <a class="admin-button" href="${pageContext.request.contextPath}?action=admin-station-form">Create Station</a>
                </div>

                <form class="form-row" method="GET" action="${pageContext.request.contextPath}/">
                    <input type="hidden" name="action" value="admin-stations">
                    <input type="search" name="keyword" value="${keyword}" placeholder="Search station">
                    <button class="admin-button" type="submit">Filter</button>
                </form>

                <c:if test="${not empty adminSuccess}">
                    <div class="success-message">${adminSuccess}</div>
                </c:if>
                <c:if test="${not empty adminError}">
                    <div class="error-message">${adminError}</div>
                </c:if>

                <div class="admin-table-wrap">
                    <table class="admin-table">
                        <thead>
                            <tr>
                                <th>Name</th>
                                <th>Address</th>
                                <th>Phone</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="station" items="${stations}">
                                <tr>
                                    <td>${station.name}</td>
                                    <td>${station.address}</td>
                                    <td>${station.phone}</td>
                                    <td>
                                        <a class="admin-button light" href="${pageContext.request.contextPath}?action=admin-station-form&id=${station.stationId}">Edit</a>
                                        <form method="POST" action="${pageContext.request.contextPath}/admin/stations/delete" style="display:inline">
                                            <input type="hidden" name="stationId" value="${station.stationId}">
                                            <button class="admin-button danger" type="submit">Delete</button>
                                        </form>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
                <%@ include file="/WEB-INF/jspf/admin-pagination.jspf" %>
            </section>
        </div>
    </main>
</div>
</body>
</html>
```

## 19. JSP Form Pattern

Loai code: `SKELETON JSP`

Can sua:

- Dam bao `station` co the null khi create. Neu null, EL van render duoc rong.
- Neu JSP bi loi khi station null, tao object rong trong controller hoac dung `<c:if>`.

```jsp
<form method="POST" action="${pageContext.request.contextPath}/admin/stations/save">
    <input type="hidden" name="stationId" value="${station.stationId}">

    <label>Name</label>
    <input name="name" value="${station.name}" required>

    <label>Address</label>
    <input name="address" value="${station.address}" required>

    <label>Phone</label>
    <input name="phone" value="${station.phone}">

    <button class="admin-button" type="submit">Save</button>
</form>
```

## 20. Common Validation Helper

Loai code: `COPY-READY`

Co the copy vao tung service, hoac tot hon la tao:

```text
src/java/utils/ValidationUtil.java
```

Sau do doi method thanh `public static`.

Nen tao base helper hoac copy nhe vao tung service:

```java
protected void validateRequired(String value, String label) {
    if (value == null || value.trim().isEmpty()) {
        throw new IllegalArgumentException(label + " is required.");
    }
}

protected boolean isBlank(String value) {
    return value == null || value.trim().isEmpty();
}

protected String blankToNull(String value) {
    return isBlank(value) ? null : value.trim();
}

protected int parseInt(String value, String label) {
    try {
        return Integer.parseInt(value);
    } catch (Exception ex) {
        throw new IllegalArgumentException(label + " must be a number.");
    }
}

protected double parseDouble(String value, String label) {
    try {
        return Double.parseDouble(value);
    } catch (Exception ex) {
        throw new IllegalArgumentException(label + " must be a number.");
    }
}
```

## 21. Route Mapping Can Them Vao MainController

Neu dung action routing:

Loai code: `SKELETON`

Can sua:

- Them vao dung vi tri trong `processRequest`.
- Action name phai khop link trong sidebar/JSP.
- URL phai khop `@WebServlet` cua `AdminController`.

```java
} else if (action.equals("admin-stations")) {
    url = "/admin/stations";
} else if (action.equals("admin-station-form")) {
    url = "/admin/stations/form";
} else if (action.equals("admin-categories")) {
    url = "/admin/categories";
} else if (action.equals("admin-category-form")) {
    url = "/admin/categories/form";
} else if (action.equals("admin-vehicles")) {
    url = "/admin/vehicles";
} else if (action.equals("admin-vehicle-form")) {
    url = "/admin/vehicles/form";
} else if (action.equals("admin-discounts")) {
    url = "/admin/discounts";
} else if (action.equals("admin-discount-form")) {
    url = "/admin/discounts/form";
}
```

## 22. Priority Implement

Lam theo thu tu nay de it bi chan dependency:

```text
P1:
  Station CRUD
  Category CRUD
  Vehicle CRUD

P2:
  Discount CRUD
  Rental list/detail/cancel
  Payment list/detail
  Extra Charge list/detail/manual charge
  Vehicle Maintenance admin create/detail

P3:
  Incident management
  Wallet/Wallet Transaction view
  Rental Status History view

P4:
  Rental Discount view
  Review moderation
```

## 23. Checklist Cho Teammate

Moi module khi xong phai co:

```text
[ ] Admin role guard
[ ] GET list route
[ ] GET form/detail route neu can
[ ] POST save route neu la CRUD
[ ] POST delete/status route neu can
[ ] DAO search/find/create/update/delete/check relationship
[ ] Service validation
[ ] Business rule check
[ ] JSP list
[ ] JSP form/detail
[ ] Flash success/error
[ ] Pagination
[ ] UTF-8 form
[ ] Build successful
[ ] Customer/Staff bi chan khi truy cap URL admin
```

## 24. Nhung Bang Khong Nen Hard Delete

```text
Account
Vehicle da co rental
Rental
Payment
Extra_Charge da paid
Incident_Report
Wallet
Wallet_Transaction
Rental_Status_History
Rental_Discount
```

Neu can xoa tren UI, uu tien:

```text
Lock / Disable / Hide / Cancel
```

thay vi hard delete.

## 25. Ket Luan Ve Viec Copy Code

Khong nen copy nguyen file nay va mong project chay ngay.

Nen copy theo tung block:

```text
Business Rules:
  Copy vao issue/task de teammate nam nghiep vu.

Validation:
  Copy vao Service va sua label/message.

DAO Query:
  Copy va sua field theo entity hien tai.

Controller Route:
  Copy vao AdminController/AdminCrudActionController dung vi tri.

JSP:
  Copy thanh file JSP moi, sua attribute va action URL.
```

Doan code gan nhan `COPY-READY` van can build va test lai, vi project co the da doi ten field/entity sau khi file docs nay duoc viet.
