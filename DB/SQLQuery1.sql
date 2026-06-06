/* =====================================================
   EVehicleRental_DB - Full MSSQL Script
   Có thể Ctrl + A và Execute toàn bộ.
   - Tự tạo database nếu chưa có
   - Tạo bảng nếu chưa có
   - Insert data mẫu nếu chưa tồn tại
   ===================================================== */

IF DB_ID(N'EVehicleRental_DB') IS NULL
BEGIN
    CREATE DATABASE EVehicleRental_DB;
END;
GO

USE EVehicleRental_DB;
GO

/* =========================
   ACCOUNT
   ========================= */
IF OBJECT_ID(N'Account', N'U') IS NULL
BEGIN
    CREATE TABLE Account (
        account_id UNIQUEIDENTIFIER DEFAULT NEWID() PRIMARY KEY,
        email VARCHAR(100) NOT NULL UNIQUE,
        password VARCHAR(255) NOT NULL,
        full_name NVARCHAR(100) NOT NULL,
        phone VARCHAR(20),
        is_verified BIT DEFAULT 1,
        role VARCHAR(20) DEFAULT 'CUSTOMER' CHECK (role IN ('CUSTOMER', 'STAFF', 'ADMIN')),
        status VARCHAR(20) DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'INACTIVE')),
        created_at DATETIME2 DEFAULT GETDATE()
    );
END;
GO

/* =========================
   WALLET
   ========================= */
IF OBJECT_ID(N'Wallet', N'U') IS NULL
BEGIN
    CREATE TABLE Wallet (
        wallet_id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
        account_id UNIQUEIDENTIFIER NOT NULL,
        balance DECIMAL(10,2) NOT NULL DEFAULT 0,
        updated_at DATETIME2,

        CONSTRAINT FK_Wallet_Account
            FOREIGN KEY (account_id)
            REFERENCES Account(account_id)
    );
END;
GO

/* =========================
   WALLET TRANSACTION
   ========================= */
IF OBJECT_ID(N'Wallet_Transaction', N'U') IS NULL
BEGIN
    CREATE TABLE Wallet_Transaction (
        transaction_id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
        wallet_id UNIQUEIDENTIFIER NOT NULL,
        amount DECIMAL(10,2) NOT NULL,
        type VARCHAR(20) NOT NULL,
        description NVARCHAR(MAX),
        created_at DATETIME2 DEFAULT GETDATE(),

        CONSTRAINT FK_WalletTransaction_Wallet
            FOREIGN KEY (wallet_id)
            REFERENCES Wallet(wallet_id),

        CONSTRAINT CK_WalletTransaction_Type
            CHECK (type IN ('TOPUP', 'PAYMENT', 'REFUND'))
    );
END;
GO

/* =========================
   STATION
   ========================= */
IF OBJECT_ID(N'Station', N'U') IS NULL
BEGIN
    CREATE TABLE Station (
        station_id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
        name NVARCHAR(100) NOT NULL,
        address NVARCHAR(MAX),
        contact_number VARCHAR(20)
    );
END;
GO

/* =========================
   CATEGORY
   ========================= */
IF OBJECT_ID(N'Category', N'U') IS NULL
BEGIN
    CREATE TABLE Category (
        category_id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
        name NVARCHAR(100) NOT NULL
    );
END;
GO

/* =========================
   VEHICLE MODEL
   ========================= */
IF OBJECT_ID(N'Vehicle_Model', N'U') IS NULL
BEGIN
    CREATE TABLE Vehicle_Model (
        model_id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
        category_id UNIQUEIDENTIFIER NOT NULL,
        name NVARCHAR(100) NOT NULL,
        brand NVARCHAR(100),
        seat_count INT,
        price_per_day DECIMAL(10,2),
        description NVARCHAR(MAX),

        CONSTRAINT FK_VehicleModel_Category
            FOREIGN KEY (category_id)
            REFERENCES Category(category_id)
    );
END;
GO

/* =========================
   VEHICLE MODEL IMAGE
   ========================= */
IF OBJECT_ID(N'Vehicle_Model_Image', N'U') IS NULL
BEGIN
    CREATE TABLE Vehicle_Model_Image (
        image_id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
        model_id UNIQUEIDENTIFIER NOT NULL,
        image_url NVARCHAR(MAX),
        image_type VARCHAR(20) NOT NULL,

        CONSTRAINT FK_VehicleModelImage_VehicleModel
            FOREIGN KEY (model_id)
            REFERENCES Vehicle_Model(model_id),

        CONSTRAINT CK_VehicleModelImage_Type
            CHECK (image_type IN ('FRONT', 'BACK', 'INTERIOR'))
    );
END;
GO

/* =========================
   VEHICLE
   ========================= */
IF OBJECT_ID(N'Vehicle', N'U') IS NULL
BEGIN
    CREATE TABLE Vehicle (
        vehicle_id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
        model_id UNIQUEIDENTIFIER NOT NULL,
        station_id UNIQUEIDENTIFIER NOT NULL,
        license_plate VARCHAR(20) UNIQUE,
        color NVARCHAR(50),
        battery_level INT,
        status VARCHAR(20) DEFAULT 'AVAILABLE',
        created_at DATETIME2 DEFAULT GETDATE(),

        CONSTRAINT FK_Vehicle_VehicleModel
            FOREIGN KEY (model_id)
            REFERENCES Vehicle_Model(model_id),

        CONSTRAINT FK_Vehicle_Station
            FOREIGN KEY (station_id)
            REFERENCES Station(station_id),

        CONSTRAINT CK_Vehicle_Status
            CHECK (status IN ('AVAILABLE', 'RENTED', 'MAINTENANCE')),

        CONSTRAINT CK_Vehicle_BatteryLevel
            CHECK (battery_level BETWEEN 0 AND 100)
    );
END;
GO

/* =========================
   RENTAL
   ========================= */
IF OBJECT_ID(N'Rental', N'U') IS NULL
BEGIN
    CREATE TABLE Rental (
        rental_id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
        customer_id UNIQUEIDENTIFIER NOT NULL,
        vehicle_id UNIQUEIDENTIFIER NOT NULL,
        pickup_station_id UNIQUEIDENTIFIER NOT NULL,
        start_date DATE,
        end_date DATE,
        total_days INT,
        total_amount DECIMAL(10,2),
        status VARCHAR(20) DEFAULT 'BOOKED',
        created_at DATETIME2 DEFAULT GETDATE(),

        CONSTRAINT FK_Rental_Customer
            FOREIGN KEY (customer_id)
            REFERENCES Account(account_id),

        CONSTRAINT FK_Rental_Vehicle
            FOREIGN KEY (vehicle_id)
            REFERENCES Vehicle(vehicle_id),

        CONSTRAINT FK_Rental_PickupStation
            FOREIGN KEY (pickup_station_id)
            REFERENCES Station(station_id),

        CONSTRAINT CK_Rental_Status
            CHECK (status IN ('BOOKED', 'RENTED', 'COMPLETED', 'CANCELLED'))
    );
END;
GO

/* =========================
   RENTAL STATUS HISTORY
   ========================= */
IF OBJECT_ID(N'Rental_Status_History', N'U') IS NULL
BEGIN
    CREATE TABLE Rental_Status_History (
        history_id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
        rental_id UNIQUEIDENTIFIER NOT NULL,
        status VARCHAR(20) NOT NULL,
        changed_at DATETIME2 DEFAULT GETDATE(),

        CONSTRAINT FK_RentalStatusHistory_Rental
            FOREIGN KEY (rental_id)
            REFERENCES Rental(rental_id),

        CONSTRAINT CK_RentalStatusHistory_Status
            CHECK (status IN ('BOOKED', 'RENTED', 'COMPLETED', 'CANCELLED'))
    );
END;
GO

/* =========================
   PAYMENT
   ========================= */
IF OBJECT_ID(N'Payment', N'U') IS NULL
BEGIN
    CREATE TABLE Payment (
        payment_id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
        rental_id UNIQUEIDENTIFIER NOT NULL,
        amount DECIMAL(10,2) NOT NULL,
        payment_method VARCHAR(20) NOT NULL,
        status VARCHAR(20) NOT NULL,
        transaction_code VARCHAR(255),
        payment_date DATETIME2 DEFAULT GETDATE(),

        CONSTRAINT FK_Payment_Rental
            FOREIGN KEY (rental_id)
            REFERENCES Rental(rental_id),

        CONSTRAINT CK_Payment_Method
            CHECK (payment_method IN ('WALLET', 'VNPAY')),

        CONSTRAINT CK_Payment_Status
            CHECK (status IN ('PENDING', 'SUCCESS', 'FAILED'))
    );
END;
GO

/* =========================
   DISCOUNT
   ========================= */
IF OBJECT_ID(N'Discount', N'U') IS NULL
BEGIN
    CREATE TABLE Discount (
        discount_id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
        code VARCHAR(50) NOT NULL UNIQUE,
        discount_percent INT NOT NULL,
        expired_at DATETIME2,
        quantity INT DEFAULT 0,

        CONSTRAINT CK_Discount_Percent
            CHECK (discount_percent BETWEEN 0 AND 100)
    );
END;
GO

/* =========================
   RENTAL DISCOUNT
   ========================= */
IF OBJECT_ID(N'Rental_Discount', N'U') IS NULL
BEGIN
    CREATE TABLE Rental_Discount (
        rental_discount_id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
        rental_id UNIQUEIDENTIFIER NOT NULL,
        discount_id UNIQUEIDENTIFIER NOT NULL,

        CONSTRAINT FK_RentalDiscount_Rental
            FOREIGN KEY (rental_id)
            REFERENCES Rental(rental_id),

        CONSTRAINT FK_RentalDiscount_Discount
            FOREIGN KEY (discount_id)
            REFERENCES Discount(discount_id)
    );
END;
GO

/* =========================
   REVIEW
   ========================= */
IF OBJECT_ID(N'Review', N'U') IS NULL
BEGIN
    CREATE TABLE Review (
        review_id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
        rental_id UNIQUEIDENTIFIER NOT NULL,
        customer_id UNIQUEIDENTIFIER NOT NULL,
        model_id UNIQUEIDENTIFIER NOT NULL,
        rating INT NOT NULL,
        comment NVARCHAR(MAX),
        created_at DATETIME2 DEFAULT GETDATE(),

        CONSTRAINT FK_Review_Rental
            FOREIGN KEY (rental_id)
            REFERENCES Rental(rental_id),

        CONSTRAINT FK_Review_Customer
            FOREIGN KEY (customer_id)
            REFERENCES Account(account_id),

        CONSTRAINT FK_Review_VehicleModel
            FOREIGN KEY (model_id)
            REFERENCES Vehicle_Model(model_id),

        CONSTRAINT CK_Review_Rating
            CHECK (rating BETWEEN 1 AND 5)
    );
END;
GO

/* =========================
   VEHICLE MAINTENANCE
   ========================= */
IF OBJECT_ID(N'Vehicle_Maintenance', N'U') IS NULL
BEGIN
    CREATE TABLE Vehicle_Maintenance (
        maintenance_id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
        vehicle_id UNIQUEIDENTIFIER NOT NULL,
        description NVARCHAR(MAX),
        maintenance_date DATETIME2 DEFAULT GETDATE(),
        status VARCHAR(20) NOT NULL,

        CONSTRAINT FK_VehicleMaintenance_Vehicle
            FOREIGN KEY (vehicle_id)
            REFERENCES Vehicle(vehicle_id),

        CONSTRAINT CK_VehicleMaintenance_Status
            CHECK (status IN ('PENDING', 'COMPLETED'))
    );
END;
GO

/* =========================
   INCIDENT REPORT
   ========================= */
IF OBJECT_ID(N'Incident_Report', N'U') IS NULL
BEGIN
    CREATE TABLE Incident_Report (
        incident_id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
        rental_id UNIQUEIDENTIFIER NOT NULL,
        vehicle_id UNIQUEIDENTIFIER NOT NULL,
        description NVARCHAR(MAX),
        severity VARCHAR(20) NOT NULL,
        created_at DATETIME2 DEFAULT GETDATE(),

        CONSTRAINT FK_IncidentReport_Rental
            FOREIGN KEY (rental_id)
            REFERENCES Rental(rental_id),

        CONSTRAINT FK_IncidentReport_Vehicle
            FOREIGN KEY (vehicle_id)
            REFERENCES Vehicle(vehicle_id),

        CONSTRAINT CK_IncidentReport_Severity
            CHECK (severity IN ('LOW', 'MEDIUM', 'HIGH'))
    );
END;
GO

/* =====================================================
   SEED DATA - INSERT KHÔNG BỊ TRÙNG KHI CHẠY LẠI
   ===================================================== */

/* 5 Account */
IF NOT EXISTS (SELECT 1 FROM Account WHERE email = 'nguyenvana@gmail.com')
BEGIN
    INSERT INTO Account (email, password, full_name, phone, is_verified, role, status)
    VALUES ('nguyenvana@gmail.com', 'hashed_password_123', N'Nguyễn Văn A', '0901234567', 1, 'CUSTOMER', 'ACTIVE');
END;

IF NOT EXISTS (SELECT 1 FROM Account WHERE email = 'tranthib@gmail.com')
BEGIN
    INSERT INTO Account (email, password, full_name, phone, is_verified, role, status)
    VALUES ('tranthib@gmail.com', 'hashed_password_456', N'Trần Thị B', '0912345678', 1, 'CUSTOMER', 'ACTIVE');
END;

IF NOT EXISTS (SELECT 1 FROM Account WHERE email = 'lephuocc@company.com')
BEGIN
    INSERT INTO Account (email, password, full_name, phone, is_verified, role, status)
    VALUES ('lephuocc@company.com', 'staff_secure_pass', N'Lê Phước C', '0923456789', 1, 'STAFF', 'ACTIVE');
END;

IF NOT EXISTS (SELECT 1 FROM Account WHERE email = 'admin_system@domain.com')
BEGIN
    INSERT INTO Account (email, password, full_name, phone, is_verified, role, status)
    VALUES ('admin_system@domain.com', 'super_secure_admin_2026', N'Phạm Hoàng Admin', '0934567890', 1, 'ADMIN', 'ACTIVE');
END;

IF NOT EXISTS (SELECT 1 FROM Account WHERE email = 'giahuy_badluck@gmail.com')
BEGIN
    INSERT INTO Account (email, password, full_name, phone, is_verified, role, status)
    VALUES ('giahuy_badluck@gmail.com', 'forgot_password_xyz', N'Võ Gia Huy', '0945678901', 0, 'CUSTOMER', 'INACTIVE');
END;
GO

/* 5 Wallet */
IF NOT EXISTS (SELECT 1 FROM Wallet WHERE wallet_id = '00000000-0000-0000-0000-000000000101')
BEGIN
    INSERT INTO Wallet (wallet_id, account_id, balance, updated_at)
    SELECT '00000000-0000-0000-0000-000000000101', account_id, 500000.00, GETDATE()
    FROM Account WHERE email = 'nguyenvana@gmail.com';
END;

IF NOT EXISTS (SELECT 1 FROM Wallet WHERE wallet_id = '00000000-0000-0000-0000-000000000102')
BEGIN
    INSERT INTO Wallet (wallet_id, account_id, balance, updated_at)
    SELECT '00000000-0000-0000-0000-000000000102', account_id, 750000.00, GETDATE()
    FROM Account WHERE email = 'tranthib@gmail.com';
END;

IF NOT EXISTS (SELECT 1 FROM Wallet WHERE wallet_id = '00000000-0000-0000-0000-000000000103')
BEGIN
    INSERT INTO Wallet (wallet_id, account_id, balance, updated_at)
    SELECT '00000000-0000-0000-0000-000000000103', account_id, 1000000.00, GETDATE()
    FROM Account WHERE email = 'lephuocc@company.com';
END;

IF NOT EXISTS (SELECT 1 FROM Wallet WHERE wallet_id = '00000000-0000-0000-0000-000000000104')
BEGIN
    INSERT INTO Wallet (wallet_id, account_id, balance, updated_at)
    SELECT '00000000-0000-0000-0000-000000000104', account_id, 2000000.00, GETDATE()
    FROM Account WHERE email = 'admin_system@domain.com';
END;

IF NOT EXISTS (SELECT 1 FROM Wallet WHERE wallet_id = '00000000-0000-0000-0000-000000000105')
BEGIN
    INSERT INTO Wallet (wallet_id, account_id, balance, updated_at)
    SELECT '00000000-0000-0000-0000-000000000105', account_id, 100000.00, GETDATE()
    FROM Account WHERE email = 'giahuy_badluck@gmail.com';
END;
GO

/* 5 Wallet_Transaction */
IF NOT EXISTS (SELECT 1 FROM Wallet_Transaction WHERE transaction_id = '00000000-0000-0000-0000-000000000201')
BEGIN
    INSERT INTO Wallet_Transaction (transaction_id, wallet_id, amount, type, description)
    VALUES ('00000000-0000-0000-0000-000000000201', '00000000-0000-0000-0000-000000000101', 500000.00, 'TOPUP', N'Nạp tiền vào ví lần đầu');
END;

IF NOT EXISTS (SELECT 1 FROM Wallet_Transaction WHERE transaction_id = '00000000-0000-0000-0000-000000000202')
BEGIN
    INSERT INTO Wallet_Transaction (transaction_id, wallet_id, amount, type, description)
    VALUES ('00000000-0000-0000-0000-000000000202', '00000000-0000-0000-0000-000000000102', 250000.00, 'PAYMENT', N'Thanh toán đơn thuê xe');
END;

IF NOT EXISTS (SELECT 1 FROM Wallet_Transaction WHERE transaction_id = '00000000-0000-0000-0000-000000000203')
BEGIN
    INSERT INTO Wallet_Transaction (transaction_id, wallet_id, amount, type, description)
    VALUES ('00000000-0000-0000-0000-000000000203', '00000000-0000-0000-0000-000000000103', 1000000.00, 'TOPUP', N'Nạp tiền bởi nhân viên kiểm thử');
END;

IF NOT EXISTS (SELECT 1 FROM Wallet_Transaction WHERE transaction_id = '00000000-0000-0000-0000-000000000204')
BEGIN
    INSERT INTO Wallet_Transaction (transaction_id, wallet_id, amount, type, description)
    VALUES ('00000000-0000-0000-0000-000000000204', '00000000-0000-0000-0000-000000000104', 300000.00, 'REFUND', N'Hoàn tiền đơn thuê bị hủy');
END;

IF NOT EXISTS (SELECT 1 FROM Wallet_Transaction WHERE transaction_id = '00000000-0000-0000-0000-000000000205')
BEGIN
    INSERT INTO Wallet_Transaction (transaction_id, wallet_id, amount, type, description)
    VALUES ('00000000-0000-0000-0000-000000000205', '00000000-0000-0000-0000-000000000105', 100000.00, 'TOPUP', N'Nạp tiền khuyến mãi');
END;
GO

/* 5 Station */
IF NOT EXISTS (SELECT 1 FROM Station WHERE station_id = '00000000-0000-0000-0000-000000000301')
BEGIN
    INSERT INTO Station (station_id, name, address, contact_number) VALUES
    ('00000000-0000-0000-0000-000000000301', N'Trạm Quận 1', N'12 Nguyễn Huệ, Quận 1, TP.HCM', '02811110001'),
    ('00000000-0000-0000-0000-000000000302', N'Trạm Bình Thạnh', N'45 Điện Biên Phủ, Bình Thạnh, TP.HCM', '02811110002'),
    ('00000000-0000-0000-0000-000000000303', N'Trạm Thủ Đức', N'88 Võ Văn Ngân, TP. Thủ Đức, TP.HCM', '02811110003'),
    ('00000000-0000-0000-0000-000000000304', N'Trạm Tân Bình', N'20 Cộng Hòa, Tân Bình, TP.HCM', '02811110004'),
    ('00000000-0000-0000-0000-000000000305', N'Trạm Bình Chánh', N'99 Quốc lộ 50, Bình Chánh, TP.HCM', '02811110005');
END;
GO

/* 5 Category */
IF NOT EXISTS (SELECT 1 FROM Category WHERE category_id = '00000000-0000-0000-0000-000000000401')
BEGIN
    INSERT INTO Category (category_id, name) VALUES
    ('00000000-0000-0000-0000-000000000401', N'Xe máy điện'),
    ('00000000-0000-0000-0000-000000000402', N'Xe đạp điện'),
    ('00000000-0000-0000-0000-000000000403', N'Ô tô điện mini'),
    ('00000000-0000-0000-0000-000000000404', N'Ô tô điện gia đình'),
    ('00000000-0000-0000-0000-000000000405', N'Xe điện cao cấp');
END;
GO

/* 5 Vehicle_Model */
IF NOT EXISTS (SELECT 1 FROM Vehicle_Model WHERE model_id = '00000000-0000-0000-0000-000000000501')
BEGIN
    INSERT INTO Vehicle_Model (model_id, category_id, name, brand, seat_count, price_per_day, description) VALUES
    ('00000000-0000-0000-0000-000000000501', '00000000-0000-0000-0000-000000000401', N'VinFast Evo200', N'VinFast', 2, 120000.00, N'Xe máy điện phù hợp di chuyển trong thành phố'),
    ('00000000-0000-0000-0000-000000000502', '00000000-0000-0000-0000-000000000402', N'Yadea iGo', N'Yadea', 1, 80000.00, N'Xe đạp điện nhỏ gọn cho học sinh, sinh viên'),
    ('00000000-0000-0000-0000-000000000503', '00000000-0000-0000-0000-000000000403', N'Wuling Mini EV', N'Wuling', 4, 350000.00, N'Ô tô điện mini tiết kiệm chi phí'),
    ('00000000-0000-0000-0000-000000000504', '00000000-0000-0000-0000-000000000404', N'VinFast VF e34', N'VinFast', 5, 650000.00, N'Ô tô điện gia đình, phù hợp đi xa'),
    ('00000000-0000-0000-0000-000000000505', '00000000-0000-0000-0000-000000000405', N'Tesla Model 3', N'Tesla', 5, 1500000.00, N'Xe điện cao cấp phục vụ khách hàng VIP');
END;
GO

/* 5 Vehicle */
IF NOT EXISTS (SELECT 1 FROM Vehicle WHERE vehicle_id = '00000000-0000-0000-0000-000000000701')
BEGIN
    INSERT INTO Vehicle (vehicle_id, model_id, station_id, license_plate, color, battery_level, status) VALUES
    ('00000000-0000-0000-0000-000000000701', '00000000-0000-0000-0000-000000000501', '00000000-0000-0000-0000-000000000301', '59-EV001', N'Trắng', 95, 'AVAILABLE'),
    ('00000000-0000-0000-0000-000000000702', '00000000-0000-0000-0000-000000000502', '00000000-0000-0000-0000-000000000302', '59-EV002', N'Đen', 80, 'AVAILABLE'),
    ('00000000-0000-0000-0000-000000000703', '00000000-0000-0000-0000-000000000503', '00000000-0000-0000-0000-000000000303', '59-EV003', N'Xanh', 70, 'RENTED'),
    ('00000000-0000-0000-0000-000000000704', '00000000-0000-0000-0000-000000000504', '00000000-0000-0000-0000-000000000304', '59-EV004', N'Đỏ', 60, 'MAINTENANCE'),
    ('00000000-0000-0000-0000-000000000705', '00000000-0000-0000-0000-000000000505', '00000000-0000-0000-0000-000000000305', '59-EV005', N'Xám', 90, 'AVAILABLE');
END;
GO

/* 5 Rental */
IF NOT EXISTS (SELECT 1 FROM Rental WHERE rental_id = '00000000-0000-0000-0000-000000000801')
BEGIN
    INSERT INTO Rental (rental_id, customer_id, vehicle_id, pickup_station_id, start_date, end_date, total_days, total_amount, status)
    SELECT '00000000-0000-0000-0000-000000000801', account_id, '00000000-0000-0000-0000-000000000701', '00000000-0000-0000-0000-000000000301', '2026-06-01', '2026-06-03', 3, 360000.00, 'COMPLETED'
    FROM Account WHERE email = 'nguyenvana@gmail.com';

    INSERT INTO Rental (rental_id, customer_id, vehicle_id, pickup_station_id, start_date, end_date, total_days, total_amount, status)
    SELECT '00000000-0000-0000-0000-000000000802', account_id, '00000000-0000-0000-0000-000000000702', '00000000-0000-0000-0000-000000000302', '2026-06-04', '2026-06-04', 1, 80000.00, 'BOOKED'
    FROM Account WHERE email = 'tranthib@gmail.com';

    INSERT INTO Rental (rental_id, customer_id, vehicle_id, pickup_station_id, start_date, end_date, total_days, total_amount, status)
    SELECT '00000000-0000-0000-0000-000000000803', account_id, '00000000-0000-0000-0000-000000000703', '00000000-0000-0000-0000-000000000303', '2026-06-05', '2026-06-07', 3, 1050000.00, 'RENTED'
    FROM Account WHERE email = 'nguyenvana@gmail.com';

    INSERT INTO Rental (rental_id, customer_id, vehicle_id, pickup_station_id, start_date, end_date, total_days, total_amount, status)
    SELECT '00000000-0000-0000-0000-000000000804', account_id, '00000000-0000-0000-0000-000000000704', '00000000-0000-0000-0000-000000000304', '2026-06-08', '2026-06-09', 2, 1300000.00, 'CANCELLED'
    FROM Account WHERE email = 'tranthib@gmail.com';

    INSERT INTO Rental (rental_id, customer_id, vehicle_id, pickup_station_id, start_date, end_date, total_days, total_amount, status)
    SELECT '00000000-0000-0000-0000-000000000805', account_id, '00000000-0000-0000-0000-000000000705', '00000000-0000-0000-0000-000000000305', '2026-06-10', '2026-06-11', 2, 3000000.00, 'BOOKED'
    FROM Account WHERE email = 'giahuy_badluck@gmail.com';
END;
GO

/* 5 Rental_Status_History */
IF NOT EXISTS (SELECT 1 FROM Rental_Status_History WHERE history_id = '00000000-0000-0000-0000-000000000901')
BEGIN
    INSERT INTO Rental_Status_History (history_id, rental_id, status, changed_at) VALUES
    ('00000000-0000-0000-0000-000000000901', '00000000-0000-0000-0000-000000000801', 'BOOKED', '2026-05-30 08:00:00'),
    ('00000000-0000-0000-0000-000000000902', '00000000-0000-0000-0000-000000000801', 'COMPLETED', '2026-06-03 18:00:00'),
    ('00000000-0000-0000-0000-000000000903', '00000000-0000-0000-0000-000000000802', 'BOOKED', '2026-06-04 09:00:00'),
    ('00000000-0000-0000-0000-000000000904', '00000000-0000-0000-0000-000000000803', 'RENTED', '2026-06-05 10:00:00'),
    ('00000000-0000-0000-0000-000000000905', '00000000-0000-0000-0000-000000000804', 'CANCELLED', '2026-06-08 11:00:00');
END;
GO

/* 5 Payment */
IF NOT EXISTS (SELECT 1 FROM Payment WHERE payment_id = '00000000-0000-0000-0000-000000001001')
BEGIN
    INSERT INTO Payment (payment_id, rental_id, amount, payment_method, status, transaction_code, payment_date) VALUES
    ('00000000-0000-0000-0000-000000001001', '00000000-0000-0000-0000-000000000801', 360000.00, 'WALLET', 'SUCCESS', 'WALLET_TXN_001', '2026-06-01 08:15:00'),
    ('00000000-0000-0000-0000-000000001002', '00000000-0000-0000-0000-000000000802', 80000.00, 'VNPAY', 'PENDING', 'VNPAY_TXN_002', '2026-06-04 09:05:00'),
    ('00000000-0000-0000-0000-000000001003', '00000000-0000-0000-0000-000000000803', 1050000.00, 'WALLET', 'SUCCESS', 'WALLET_TXN_003', '2026-06-05 10:20:00'),
    ('00000000-0000-0000-0000-000000001004', '00000000-0000-0000-0000-000000000804', 1300000.00, 'VNPAY', 'FAILED', 'VNPAY_TXN_004', '2026-06-08 11:10:00'),
    ('00000000-0000-0000-0000-000000001005', '00000000-0000-0000-0000-000000000805', 3000000.00, 'WALLET', 'PENDING', 'WALLET_TXN_005', '2026-06-10 12:00:00');
END;
GO

/* 5 Discount */
IF NOT EXISTS (SELECT 1 FROM Discount WHERE discount_id = '00000000-0000-0000-0000-000000001101')
BEGIN
    INSERT INTO Discount (discount_id, code, discount_percent, expired_at, quantity) VALUES
    ('00000000-0000-0000-0000-000000001101', 'WELCOME10', 10, '2026-12-31 23:59:59', 100),
    ('00000000-0000-0000-0000-000000001102', 'SUMMER15', 15, '2026-08-31 23:59:59', 50),
    ('00000000-0000-0000-0000-000000001103', 'VIP20', 20, '2026-10-31 23:59:59', 20),
    ('00000000-0000-0000-0000-000000001104', 'STUDENT5', 5, '2026-09-30 23:59:59', 200),
    ('00000000-0000-0000-0000-000000001105', 'GREEN25', 25, '2026-07-31 23:59:59', 30);
END;
GO

/* 5 Rental_Discount */
IF NOT EXISTS (SELECT 1 FROM Rental_Discount WHERE rental_discount_id = '00000000-0000-0000-0000-000000001201')
BEGIN
    INSERT INTO Rental_Discount (rental_discount_id, rental_id, discount_id) VALUES
    ('00000000-0000-0000-0000-000000001201', '00000000-0000-0000-0000-000000000801', '00000000-0000-0000-0000-000000001101'),
    ('00000000-0000-0000-0000-000000001202', '00000000-0000-0000-0000-000000000802', '00000000-0000-0000-0000-000000001102'),
    ('00000000-0000-0000-0000-000000001203', '00000000-0000-0000-0000-000000000803', '00000000-0000-0000-0000-000000001103'),
    ('00000000-0000-0000-0000-000000001204', '00000000-0000-0000-0000-000000000804', '00000000-0000-0000-0000-000000001104'),
    ('00000000-0000-0000-0000-000000001205', '00000000-0000-0000-0000-000000000805', '00000000-0000-0000-0000-000000001105');
END;
GO

/* 5 Review */
IF NOT EXISTS (SELECT 1 FROM Review WHERE review_id = '00000000-0000-0000-0000-000000001301')
BEGIN
    INSERT INTO Review (review_id, rental_id, customer_id, model_id, rating, comment)
    SELECT '00000000-0000-0000-0000-000000001301', '00000000-0000-0000-0000-000000000801', account_id, '00000000-0000-0000-0000-000000000501', 5, N'Xe sạch, pin tốt, nhân viên hỗ trợ nhanh'
    FROM Account WHERE email = 'nguyenvana@gmail.com';

    INSERT INTO Review (review_id, rental_id, customer_id, model_id, rating, comment)
    SELECT '00000000-0000-0000-0000-000000001302', '00000000-0000-0000-0000-000000000802', account_id, '00000000-0000-0000-0000-000000000502', 4, N'Xe nhỏ gọn, dễ sử dụng'
    FROM Account WHERE email = 'tranthib@gmail.com';

    INSERT INTO Review (review_id, rental_id, customer_id, model_id, rating, comment)
    SELECT '00000000-0000-0000-0000-000000001303', '00000000-0000-0000-0000-000000000803', account_id, '00000000-0000-0000-0000-000000000503', 4, N'Giá hợp lý, phù hợp đi trong nội thành'
    FROM Account WHERE email = 'nguyenvana@gmail.com';

    INSERT INTO Review (review_id, rental_id, customer_id, model_id, rating, comment)
    SELECT '00000000-0000-0000-0000-000000001304', '00000000-0000-0000-0000-000000000804', account_id, '00000000-0000-0000-0000-000000000504', 3, N'Đơn bị hủy do xe bảo trì'
    FROM Account WHERE email = 'tranthib@gmail.com';

    INSERT INTO Review (review_id, rental_id, customer_id, model_id, rating, comment)
    SELECT '00000000-0000-0000-0000-000000001305', '00000000-0000-0000-0000-000000000805', account_id, '00000000-0000-0000-0000-000000000505', 5, N'Xe cao cấp, trải nghiệm tốt'
    FROM Account WHERE email = 'giahuy_badluck@gmail.com';
END;
GO

/* 5 Vehicle_Maintenance */
IF NOT EXISTS (SELECT 1 FROM Vehicle_Maintenance WHERE maintenance_id = '00000000-0000-0000-0000-000000001401')
BEGIN
    INSERT INTO Vehicle_Maintenance (maintenance_id, vehicle_id, description, maintenance_date, status) VALUES
    ('00000000-0000-0000-0000-000000001401', '00000000-0000-0000-0000-000000000701', N'Kiểm tra phanh và lốp xe', '2026-06-02 08:00:00', 'COMPLETED'),
    ('00000000-0000-0000-0000-000000001402', '00000000-0000-0000-0000-000000000702', N'Vệ sinh xe định kỳ', '2026-06-03 09:00:00', 'COMPLETED'),
    ('00000000-0000-0000-0000-000000001403', '00000000-0000-0000-0000-000000000703', N'Kiểm tra pin sau chuyến thuê', '2026-06-07 10:00:00', 'PENDING'),
    ('00000000-0000-0000-0000-000000001404', '00000000-0000-0000-0000-000000000704', N'Sửa lỗi hệ thống điều khiển', '2026-06-08 11:00:00', 'PENDING'),
    ('00000000-0000-0000-0000-000000001405', '00000000-0000-0000-0000-000000000705', N'Kiểm tra nội thất và sạc pin', '2026-06-09 13:00:00', 'COMPLETED');
END;
GO

/* 5 Incident_Report */
IF NOT EXISTS (SELECT 1 FROM Incident_Report WHERE incident_id = '00000000-0000-0000-0000-000000001501')
BEGIN
    INSERT INTO Incident_Report (incident_id, rental_id, vehicle_id, description, severity, created_at) VALUES
    ('00000000-0000-0000-0000-000000001501', '00000000-0000-0000-0000-000000000801', '00000000-0000-0000-0000-000000000701', N'Khách báo xe có vết trầy nhỏ ở thân xe', 'LOW', '2026-06-03 18:30:00'),
    ('00000000-0000-0000-0000-000000001502', '00000000-0000-0000-0000-000000000802', '00000000-0000-0000-0000-000000000702', N'Khách báo chuông xe hoạt động không ổn định', 'LOW', '2026-06-04 10:00:00'),
    ('00000000-0000-0000-0000-000000001503', '00000000-0000-0000-0000-000000000803', '00000000-0000-0000-0000-000000000703', N'Xe bị giảm pin nhanh hơn dự kiến', 'MEDIUM', '2026-06-06 14:00:00'),
    ('00000000-0000-0000-0000-000000001504', '00000000-0000-0000-0000-000000000804', '00000000-0000-0000-0000-000000000704', N'Xe cần kiểm tra hệ thống trước khi giao khách', 'MEDIUM', '2026-06-08 11:30:00'),
    ('00000000-0000-0000-0000-000000001505', '00000000-0000-0000-0000-000000000805', '00000000-0000-0000-0000-000000000705', N'Khách yêu cầu kiểm tra xe trước ngày nhận', 'LOW', '2026-06-10 12:30:00');
END;
GO

/* =====================================================
   KIỂM TRA SỐ LƯỢNG DỮ LIỆU
   ===================================================== */
SELECT 'Account' AS table_name, COUNT(*) AS total_rows FROM Account
UNION ALL SELECT 'Wallet', COUNT(*) FROM Wallet
UNION ALL SELECT 'Wallet_Transaction', COUNT(*) FROM Wallet_Transaction
UNION ALL SELECT 'Station', COUNT(*) FROM Station
UNION ALL SELECT 'Category', COUNT(*) FROM Category
UNION ALL SELECT 'Vehicle_Model', COUNT(*) FROM Vehicle_Model
UNION ALL SELECT 'Vehicle_Model_Image', COUNT(*) FROM Vehicle_Model_Image
UNION ALL SELECT 'Vehicle', COUNT(*) FROM Vehicle
UNION ALL SELECT 'Rental', COUNT(*) FROM Rental
UNION ALL SELECT 'Rental_Status_History', COUNT(*) FROM Rental_Status_History
UNION ALL SELECT 'Payment', COUNT(*) FROM Payment
UNION ALL SELECT 'Discount', COUNT(*) FROM Discount
UNION ALL SELECT 'Rental_Discount', COUNT(*) FROM Rental_Discount
UNION ALL SELECT 'Review', COUNT(*) FROM Review
UNION ALL SELECT 'Vehicle_Maintenance', COUNT(*) FROM Vehicle_Maintenance
UNION ALL SELECT 'Incident_Report', COUNT(*) FROM Incident_Report;
GO
