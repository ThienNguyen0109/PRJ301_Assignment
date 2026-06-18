package seeders;

import enums.IncidentSeverity;
import enums.MaintenanceStatus;
import enums.PaymentMethod;
import enums.PaymentStatus;
import enums.RentalStatus;
import enums.Role;
import enums.TransactionType;
import enums.VehicleModelImageType;
import enums.VehicleStatus;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import models.Account;
import models.Category;
import models.Discount;
import models.IncidentReport;
import models.Payment;
import models.Rental;
import models.RentalDiscount;
import models.RentalStatusHistory;
import models.Review;
import models.Station;
import models.Vehicle;
import models.VehicleMaintenance;
import models.VehicleModel;
import models.VehicleModelImage;
import models.Wallet;
import models.WalletTransaction;
import utils.JPAUtil;

/**
 * Seeds baseline demo data when the application starts.
 */
public final class DataSeeder {
    private static final Logger LOGGER = Logger.getLogger(DataSeeder.class.getName());
    private static volatile boolean seeded;

    private DataSeeder() {
    }

    public static synchronized void seed() {
        if (seeded) {
            return;
        }

        try {
            JPAUtil.executeInTransaction(em -> {
                seedAccounts(em);
                seedWallets(em);
                seedWalletTransactions(em);
                seedStations(em);
                seedCategories(em);
                seedVehicleModels(em);
                seedAdditionalVehicleModels(em);
                seedVehicleModelImages(em);
                seedAdditionalVehicleModelImages(em);
                seedVehicles(em);
                seedAdditionalVehicles(em);
                seedRentals(em);
                seedRentalStatusHistories(em);
                seedPayments(em);
                seedDiscounts(em);
                seedRentalDiscounts(em);
                seedReviews(em);
                seedVehicleMaintenances(em);
                seedIncidentReports(em);
                return null;
            });
            seeded = true;
            LOGGER.info("Seed data initialized successfully.");
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Could not initialize seed data", ex);
        }
    }

    private static void seedAccounts(EntityManager em) {
        persistAccountIfMissing(em, account("00000000-0000-0000-0000-000000000001",
                "nguyenvana@gmail.com", "123456", "Nguyen Van A", "0901234567", true, Role.CUSTOMER, "ACTIVE"));
        persistAccountIfMissing(em, account("00000000-0000-0000-0000-000000000002",
                "tranthib@gmail.com", "123456", "Tran Thi B", "0912345678", true, Role.CUSTOMER, "ACTIVE"));
        persistAccountIfMissing(em, account("00000000-0000-0000-0000-000000000003",
                "lephuocc@company.com", "123456", "Le Phuoc C", "0923456789", true, Role.STAFF, "ACTIVE"));
        persistAccountIfMissing(em, account("00000000-0000-0000-0000-000000000004",
                "admin_system@domain.com", "123456", "Pham Hoang Admin", "0934567890", true, Role.ADMIN, "ACTIVE"));
        persistAccountIfMissing(em, account("00000000-0000-0000-0000-000000000005",
                "giahuy_badluck@gmail.com", "123456", "Vo Gia Huy", "0945678901", false, Role.CUSTOMER, "INACTIVE"));
    }

    private static void seedWallets(EntityManager em) {
        persistIfMissing(em, Wallet.class, "00000000-0000-0000-0000-000000000101",
                new Wallet("00000000-0000-0000-0000-000000000101", accountId(em, "nguyenvana@gmail.com"), 500000.0, now()));
        persistIfMissing(em, Wallet.class, "00000000-0000-0000-0000-000000000102",
                new Wallet("00000000-0000-0000-0000-000000000102", accountId(em, "tranthib@gmail.com"), 750000.0, now()));
        persistIfMissing(em, Wallet.class, "00000000-0000-0000-0000-000000000103",
                new Wallet("00000000-0000-0000-0000-000000000103", accountId(em, "lephuocc@company.com"), 1000000.0, now()));
        persistIfMissing(em, Wallet.class, "00000000-0000-0000-0000-000000000104",
                new Wallet("00000000-0000-0000-0000-000000000104", accountId(em, "admin_system@domain.com"), 2000000.0, now()));
        persistIfMissing(em, Wallet.class, "00000000-0000-0000-0000-000000000105",
                new Wallet("00000000-0000-0000-0000-000000000105", accountId(em, "giahuy_badluck@gmail.com"), 100000.0, now()));
    }

    private static void seedWalletTransactions(EntityManager em) {
        persistIfMissing(em, WalletTransaction.class, "00000000-0000-0000-0000-000000000201",
                walletTransaction("00000000-0000-0000-0000-000000000201", "00000000-0000-0000-0000-000000000101", 500000.0, TransactionType.TOPUP, "Nap tien vao vi lan dau"));
        persistIfMissing(em, WalletTransaction.class, "00000000-0000-0000-0000-000000000202",
                walletTransaction("00000000-0000-0000-0000-000000000202", "00000000-0000-0000-0000-000000000102", 250000.0, TransactionType.PAYMENT, "Thanh toan don thue xe"));
        persistIfMissing(em, WalletTransaction.class, "00000000-0000-0000-0000-000000000203",
                walletTransaction("00000000-0000-0000-0000-000000000203", "00000000-0000-0000-0000-000000000103", 1000000.0, TransactionType.TOPUP, "Nap tien boi nhan vien kiem thu"));
        persistIfMissing(em, WalletTransaction.class, "00000000-0000-0000-0000-000000000204",
                walletTransaction("00000000-0000-0000-0000-000000000204", "00000000-0000-0000-0000-000000000104", 300000.0, TransactionType.REFUND, "Hoan tien don thue bi huy"));
        persistIfMissing(em, WalletTransaction.class, "00000000-0000-0000-0000-000000000205",
                walletTransaction("00000000-0000-0000-0000-000000000205", "00000000-0000-0000-0000-000000000105", 100000.0, TransactionType.TOPUP, "Nap tien khuyen mai"));
    }

    private static void seedStations(EntityManager em) {
        persistIfMissing(em, Station.class, "00000000-0000-0000-0000-000000000301",
                new Station("00000000-0000-0000-0000-000000000301", "Tram Quan 1", "12 Nguyen Hue, Quan 1, TP.HCM", "02811110001"));
        persistIfMissing(em, Station.class, "00000000-0000-0000-0000-000000000302",
                new Station("00000000-0000-0000-0000-000000000302", "Tram Binh Thanh", "45 Dien Bien Phu, Binh Thanh, TP.HCM", "02811110002"));
        persistIfMissing(em, Station.class, "00000000-0000-0000-0000-000000000303",
                new Station("00000000-0000-0000-0000-000000000303", "Tram Thu Duc", "88 Vo Van Ngan, TP. Thu Duc, TP.HCM", "02811110003"));
        persistIfMissing(em, Station.class, "00000000-0000-0000-0000-000000000304",
                new Station("00000000-0000-0000-0000-000000000304", "Tram Tan Binh", "20 Cong Hoa, Tan Binh, TP.HCM", "02811110004"));
        persistIfMissing(em, Station.class, "00000000-0000-0000-0000-000000000305",
                new Station("00000000-0000-0000-0000-000000000305", "Tram Binh Chanh", "99 Quoc lo 50, Binh Chanh, TP.HCM", "02811110005"));
    }

    private static void seedCategories(EntityManager em) {
        persistIfMissing(em, Category.class, "00000000-0000-0000-0000-000000000401",
                new Category("00000000-0000-0000-0000-000000000401", "Xe may dien"));
        persistIfMissing(em, Category.class, "00000000-0000-0000-0000-000000000402",
                new Category("00000000-0000-0000-0000-000000000402", "Xe dap dien"));
        persistIfMissing(em, Category.class, "00000000-0000-0000-0000-000000000403",
                new Category("00000000-0000-0000-0000-000000000403", "O to dien mini"));
        persistIfMissing(em, Category.class, "00000000-0000-0000-0000-000000000404",
                new Category("00000000-0000-0000-0000-000000000404", "O to dien gia dinh"));
        persistIfMissing(em, Category.class, "00000000-0000-0000-0000-000000000405",
                new Category("00000000-0000-0000-0000-000000000405", "Xe dien cao cap"));
    }

    private static void seedVehicleModels(EntityManager em) {
        persistIfMissing(em, VehicleModel.class, "00000000-0000-0000-0000-000000000501",
                new VehicleModel("00000000-0000-0000-0000-000000000501", "00000000-0000-0000-0000-000000000401", "VinFast Evo200", "VinFast", 2, 120000.0, "Xe may dien phu hop di chuyen trong thanh pho"));
        persistIfMissing(em, VehicleModel.class, "00000000-0000-0000-0000-000000000502",
                new VehicleModel("00000000-0000-0000-0000-000000000502", "00000000-0000-0000-0000-000000000402", "Yadea iGo", "Yadea", 1, 80000.0, "Xe dap dien nho gon cho hoc sinh, sinh vien"));
        persistIfMissing(em, VehicleModel.class, "00000000-0000-0000-0000-000000000503",
                new VehicleModel("00000000-0000-0000-0000-000000000503", "00000000-0000-0000-0000-000000000403", "Wuling Mini EV", "Wuling", 4, 350000.0, "O to dien mini tiet kiem chi phi"));
        persistIfMissing(em, VehicleModel.class, "00000000-0000-0000-0000-000000000504",
                new VehicleModel("00000000-0000-0000-0000-000000000504", "00000000-0000-0000-0000-000000000404", "VinFast VF e34", "VinFast", 5, 650000.0, "O to dien gia dinh, phu hop di xa"));
        persistIfMissing(em, VehicleModel.class, "00000000-0000-0000-0000-000000000505",
                new VehicleModel("00000000-0000-0000-0000-000000000505", "00000000-0000-0000-0000-000000000405", "Tesla Model 3", "Tesla", 5, 1500000.0, "Xe dien cao cap phuc vu khach hang VIP"));
    }

    private static void seedAdditionalVehicleModels(EntityManager em) {
        Object[][] models = {
            {"00000000-0000-0000-0000-000000000506", "00000000-0000-0000-0000-000000000401", "VinFast Feliz S", "VinFast", 2, 140000.0, "Xe may dien pho thong, tam di chuyen tot"},
            {"00000000-0000-0000-0000-000000000507", "00000000-0000-0000-0000-000000000401", "VinFast Klara S", "VinFast", 2, 150000.0, "Xe may dien co thiet ke thanh lich cho do thi"},
            {"00000000-0000-0000-0000-000000000508", "00000000-0000-0000-0000-000000000401", "Dat Bike Weaver 200", "Dat Bike", 2, 180000.0, "Xe may dien hieu nang cao san xuat tai Viet Nam"},
            {"00000000-0000-0000-0000-000000000509", "00000000-0000-0000-0000-000000000401", "Selex Camel", "Selex Motors", 2, 160000.0, "Xe may dien ben bi, phu hop giao nhan va di chuyen trong thanh pho"},
            {"00000000-0000-0000-0000-000000000510", "00000000-0000-0000-0000-000000000402", "Giant Momentum E+", "Giant", 1, 110000.0, "Xe dap dien tro luc phu hop di ngan ngay"},
            {"00000000-0000-0000-0000-000000000511", "00000000-0000-0000-0000-000000000402", "Himo C26", "Himo", 1, 95000.0, "Xe dap dien gon nhe, de dieu khien"},
            {"00000000-0000-0000-0000-000000000512", "00000000-0000-0000-0000-000000000403", "VinFast VF 3", "VinFast", 4, 420000.0, "O to dien mini linh hoat cho do thi"},
            {"00000000-0000-0000-0000-000000000513", "00000000-0000-0000-0000-000000000404", "VinFast VF 5", "VinFast", 5, 520000.0, "SUV dien nho, tiet kiem va de thue theo ngay"},
            {"00000000-0000-0000-0000-000000000514", "00000000-0000-0000-0000-000000000404", "VinFast VF 6", "VinFast", 5, 700000.0, "SUV dien gia dinh voi khoang noi that rong"},
            {"00000000-0000-0000-0000-000000000515", "00000000-0000-0000-0000-000000000404", "BYD Dolphin", "BYD", 5, 620000.0, "Hatchback dien thiet ke tre trung"},
            {"00000000-0000-0000-0000-000000000516", "00000000-0000-0000-0000-000000000404", "BYD Atto 3", "BYD", 5, 780000.0, "Crossover dien phu hop du lich ngan ngay"},
            {"00000000-0000-0000-0000-000000000517", "00000000-0000-0000-0000-000000000405", "Hyundai Ioniq 5", "Hyundai", 5, 1250000.0, "Xe dien cao cap voi sac nhanh va khoang cabin rong"},
            {"00000000-0000-0000-0000-000000000518", "00000000-0000-0000-0000-000000000405", "Kia EV6", "Kia", 5, 1350000.0, "Crossover dien cao cap, trai nghiem lai manh me"},
            {"00000000-0000-0000-0000-000000000519", "00000000-0000-0000-0000-000000000405", "Tesla Model Y", "Tesla", 5, 1700000.0, "SUV dien cao cap phu hop khach VIP"},
            {"00000000-0000-0000-0000-000000000520", "00000000-0000-0000-0000-000000000405", "VinFast VF 8", "VinFast", 5, 1450000.0, "SUV dien cao cap cho hanh trinh dai"}
        };

        for (Object[] model : models) {
            persistIfMissing(em, VehicleModel.class, (String) model[0],
                    new VehicleModel((String) model[0], (String) model[1], (String) model[2], (String) model[3],
                            (Integer) model[4], (Double) model[5], (String) model[6]));
        }
    }

    private static void seedVehicleModelImages(EntityManager em) {
        persistIfMissing(em, VehicleModelImage.class, "00000000-0000-0000-0000-000000000601",
                new VehicleModelImage("00000000-0000-0000-0000-000000000601", "00000000-0000-0000-0000-000000000501", "https://placehold.co/900x520/0f172a/f8d77b?text=VinFast+Evo200", VehicleModelImageType.FRONT));
        persistIfMissing(em, VehicleModelImage.class, "00000000-0000-0000-0000-000000000602",
                new VehicleModelImage("00000000-0000-0000-0000-000000000602", "00000000-0000-0000-0000-000000000502", "https://placehold.co/900x520/0f172a/f8d77b?text=Yadea+iGo", VehicleModelImageType.FRONT));
        persistIfMissing(em, VehicleModelImage.class, "00000000-0000-0000-0000-000000000603",
                new VehicleModelImage("00000000-0000-0000-0000-000000000603", "00000000-0000-0000-0000-000000000503", "https://placehold.co/900x520/0f172a/f8d77b?text=Wuling+Mini+EV", VehicleModelImageType.FRONT));
        persistIfMissing(em, VehicleModelImage.class, "00000000-0000-0000-0000-000000000604",
                new VehicleModelImage("00000000-0000-0000-0000-000000000604", "00000000-0000-0000-0000-000000000504", "https://placehold.co/900x520/0f172a/f8d77b?text=VinFast+VF+e34", VehicleModelImageType.FRONT));
        persistIfMissing(em, VehicleModelImage.class, "00000000-0000-0000-0000-000000000605",
                new VehicleModelImage("00000000-0000-0000-0000-000000000605", "00000000-0000-0000-0000-000000000505", "https://placehold.co/900x520/0f172a/f8d77b?text=Tesla+Model+3", VehicleModelImageType.FRONT));
    }

    private static void seedAdditionalVehicleModelImages(EntityManager em) {
        String[][] images = {
            {"00000000-0000-0000-0000-000000000606", "00000000-0000-0000-0000-000000000506", "VinFast+Feliz+S"},
            {"00000000-0000-0000-0000-000000000607", "00000000-0000-0000-0000-000000000507", "VinFast+Klara+S"},
            {"00000000-0000-0000-0000-000000000608", "00000000-0000-0000-0000-000000000508", "Dat+Bike+Weaver+200"},
            {"00000000-0000-0000-0000-000000000609", "00000000-0000-0000-0000-000000000509", "Selex+Camel"},
            {"00000000-0000-0000-0000-000000000610", "00000000-0000-0000-0000-000000000510", "Giant+Momentum+E+"},
            {"00000000-0000-0000-0000-000000000611", "00000000-0000-0000-0000-000000000511", "Himo+C26"},
            {"00000000-0000-0000-0000-000000000612", "00000000-0000-0000-0000-000000000512", "VinFast+VF+3"},
            {"00000000-0000-0000-0000-000000000613", "00000000-0000-0000-0000-000000000513", "VinFast+VF+5"},
            {"00000000-0000-0000-0000-000000000614", "00000000-0000-0000-0000-000000000514", "VinFast+VF+6"},
            {"00000000-0000-0000-0000-000000000615", "00000000-0000-0000-0000-000000000515", "BYD+Dolphin"},
            {"00000000-0000-0000-0000-000000000616", "00000000-0000-0000-0000-000000000516", "BYD+Atto+3"},
            {"00000000-0000-0000-0000-000000000617", "00000000-0000-0000-0000-000000000517", "Hyundai+Ioniq+5"},
            {"00000000-0000-0000-0000-000000000618", "00000000-0000-0000-0000-000000000518", "Kia+EV6"},
            {"00000000-0000-0000-0000-000000000619", "00000000-0000-0000-0000-000000000519", "Tesla+Model+Y"},
            {"00000000-0000-0000-0000-000000000620", "00000000-0000-0000-0000-000000000520", "VinFast+VF+8"}
        };

        for (String[] image : images) {
            persistIfMissing(em, VehicleModelImage.class, image[0],
                    new VehicleModelImage(image[0], image[1],
                            "https://placehold.co/900x520/0f172a/f8d77b?text=" + image[2],
                            VehicleModelImageType.FRONT));
        }
    }

    private static void seedVehicles(EntityManager em) {
        persistIfMissing(em, Vehicle.class, "00000000-0000-0000-0000-000000000701",
                vehicle("00000000-0000-0000-0000-000000000701", "00000000-0000-0000-0000-000000000501", "00000000-0000-0000-0000-000000000301", "59-EV001", "Trang", 95, VehicleStatus.AVAILABLE));
        persistIfMissing(em, Vehicle.class, "00000000-0000-0000-0000-000000000702",
                vehicle("00000000-0000-0000-0000-000000000702", "00000000-0000-0000-0000-000000000502", "00000000-0000-0000-0000-000000000302", "59-EV002", "Den", 80, VehicleStatus.AVAILABLE));
        persistIfMissing(em, Vehicle.class, "00000000-0000-0000-0000-000000000703",
                vehicle("00000000-0000-0000-0000-000000000703", "00000000-0000-0000-0000-000000000503", "00000000-0000-0000-0000-000000000303", "59-EV003", "Xanh", 70, VehicleStatus.RENTED));
        persistIfMissing(em, Vehicle.class, "00000000-0000-0000-0000-000000000704",
                vehicle("00000000-0000-0000-0000-000000000704", "00000000-0000-0000-0000-000000000504", "00000000-0000-0000-0000-000000000304", "59-EV004", "Do", 60, VehicleStatus.MAINTENANCE));
        persistIfMissing(em, Vehicle.class, "00000000-0000-0000-0000-000000000705",
                vehicle("00000000-0000-0000-0000-000000000705", "00000000-0000-0000-0000-000000000505", "00000000-0000-0000-0000-000000000305", "59-EV005", "Xam", 90, VehicleStatus.AVAILABLE));
    }

    private static void seedAdditionalVehicles(EntityManager em) {
        String[] modelIds = {
            "00000000-0000-0000-0000-000000000501", "00000000-0000-0000-0000-000000000502",
            "00000000-0000-0000-0000-000000000503", "00000000-0000-0000-0000-000000000504",
            "00000000-0000-0000-0000-000000000505", "00000000-0000-0000-0000-000000000506",
            "00000000-0000-0000-0000-000000000507", "00000000-0000-0000-0000-000000000508",
            "00000000-0000-0000-0000-000000000509", "00000000-0000-0000-0000-000000000510",
            "00000000-0000-0000-0000-000000000511", "00000000-0000-0000-0000-000000000512",
            "00000000-0000-0000-0000-000000000513", "00000000-0000-0000-0000-000000000514",
            "00000000-0000-0000-0000-000000000515", "00000000-0000-0000-0000-000000000516",
            "00000000-0000-0000-0000-000000000517", "00000000-0000-0000-0000-000000000518",
            "00000000-0000-0000-0000-000000000519", "00000000-0000-0000-0000-000000000520"
        };
        String[] stationIds = {
            "00000000-0000-0000-0000-000000000301", "00000000-0000-0000-0000-000000000302",
            "00000000-0000-0000-0000-000000000303", "00000000-0000-0000-0000-000000000304",
            "00000000-0000-0000-0000-000000000305"
        };
        String[] colors = {"Trang", "Den", "Xanh", "Do", "Xam", "Bac", "Vang", "Xanh navy", "Nau"};

        for (int i = 0; i < 45; i++) {
            int number = 706 + i;
            String id = String.format("00000000-0000-0000-0000-000000000%03d", number);
            String modelId = modelIds[i % modelIds.length];
            String stationId = stationIds[i % stationIds.length];
            String plate = String.format("59-EV%03d", number - 700);
            String color = colors[i % colors.length];
            int battery = 55 + ((i * 7) % 46);
            persistIfMissing(em, Vehicle.class, id,
                    vehicle(id, modelId, stationId, plate, color, battery, VehicleStatus.AVAILABLE));
        }
    }

    private static void seedRentals(EntityManager em) {
        persistIfMissing(em, Rental.class, "00000000-0000-0000-0000-000000000801",
                rental("00000000-0000-0000-0000-000000000801", accountId(em, "nguyenvana@gmail.com"), "00000000-0000-0000-0000-000000000701", "00000000-0000-0000-0000-000000000301", "2026-06-01", "2026-06-03", 3, 360000.0, RentalStatus.COMPLETED));
        persistIfMissing(em, Rental.class, "00000000-0000-0000-0000-000000000802",
                rental("00000000-0000-0000-0000-000000000802", accountId(em, "tranthib@gmail.com"), "00000000-0000-0000-0000-000000000702", "00000000-0000-0000-0000-000000000302", "2026-06-04", "2026-06-04", 1, 80000.0, RentalStatus.BOOKED));
        persistIfMissing(em, Rental.class, "00000000-0000-0000-0000-000000000803",
                rental("00000000-0000-0000-0000-000000000803", accountId(em, "nguyenvana@gmail.com"), "00000000-0000-0000-0000-000000000703", "00000000-0000-0000-0000-000000000303", "2026-06-05", "2026-06-07", 3, 1050000.0, RentalStatus.RENTED));
        persistIfMissing(em, Rental.class, "00000000-0000-0000-0000-000000000804",
                rental("00000000-0000-0000-0000-000000000804", accountId(em, "tranthib@gmail.com"), "00000000-0000-0000-0000-000000000704", "00000000-0000-0000-0000-000000000304", "2026-06-08", "2026-06-09", 2, 1300000.0, RentalStatus.CANCELLED));
        persistIfMissing(em, Rental.class, "00000000-0000-0000-0000-000000000805",
                rental("00000000-0000-0000-0000-000000000805", accountId(em, "giahuy_badluck@gmail.com"), "00000000-0000-0000-0000-000000000705", "00000000-0000-0000-0000-000000000305", "2026-06-10", "2026-06-11", 2, 3000000.0, RentalStatus.BOOKED));
    }

    private static void seedRentalStatusHistories(EntityManager em) {
        persistIfMissing(em, RentalStatusHistory.class, "00000000-0000-0000-0000-000000000901",
                history("00000000-0000-0000-0000-000000000901", "00000000-0000-0000-0000-000000000801", RentalStatus.BOOKED, "2026-05-30 08:00:00"));
        persistIfMissing(em, RentalStatusHistory.class, "00000000-0000-0000-0000-000000000902",
                history("00000000-0000-0000-0000-000000000902", "00000000-0000-0000-0000-000000000801", RentalStatus.COMPLETED, "2026-06-03 18:00:00"));
        persistIfMissing(em, RentalStatusHistory.class, "00000000-0000-0000-0000-000000000903",
                history("00000000-0000-0000-0000-000000000903", "00000000-0000-0000-0000-000000000802", RentalStatus.BOOKED, "2026-06-04 09:00:00"));
        persistIfMissing(em, RentalStatusHistory.class, "00000000-0000-0000-0000-000000000904",
                history("00000000-0000-0000-0000-000000000904", "00000000-0000-0000-0000-000000000803", RentalStatus.RENTED, "2026-06-05 10:00:00"));
        persistIfMissing(em, RentalStatusHistory.class, "00000000-0000-0000-0000-000000000905",
                history("00000000-0000-0000-0000-000000000905", "00000000-0000-0000-0000-000000000804", RentalStatus.CANCELLED, "2026-06-08 11:00:00"));
    }

    private static void seedPayments(EntityManager em) {
        persistIfMissing(em, Payment.class, "00000000-0000-0000-0000-000000001001",
                new Payment("00000000-0000-0000-0000-000000001001", "00000000-0000-0000-0000-000000000801", 360000.0, PaymentMethod.WALLET, PaymentStatus.SUCCESS, "WALLET_TXN_001", timestamp("2026-06-01 08:15:00")));
        persistIfMissing(em, Payment.class, "00000000-0000-0000-0000-000000001002",
                new Payment("00000000-0000-0000-0000-000000001002", "00000000-0000-0000-0000-000000000802", 80000.0, PaymentMethod.VNPAY, PaymentStatus.PENDING, "VNPAY_TXN_002", timestamp("2026-06-04 09:05:00")));
        persistIfMissing(em, Payment.class, "00000000-0000-0000-0000-000000001003",
                new Payment("00000000-0000-0000-0000-000000001003", "00000000-0000-0000-0000-000000000803", 1050000.0, PaymentMethod.WALLET, PaymentStatus.SUCCESS, "WALLET_TXN_003", timestamp("2026-06-05 10:20:00")));
        persistIfMissing(em, Payment.class, "00000000-0000-0000-0000-000000001004",
                new Payment("00000000-0000-0000-0000-000000001004", "00000000-0000-0000-0000-000000000804", 1300000.0, PaymentMethod.VNPAY, PaymentStatus.FAILED, "VNPAY_TXN_004", timestamp("2026-06-08 11:10:00")));
        persistIfMissing(em, Payment.class, "00000000-0000-0000-0000-000000001005",
                new Payment("00000000-0000-0000-0000-000000001005", "00000000-0000-0000-0000-000000000805", 3000000.0, PaymentMethod.WALLET, PaymentStatus.PENDING, "WALLET_TXN_005", timestamp("2026-06-10 12:00:00")));
    }

    private static void seedDiscounts(EntityManager em) {
        persistIfMissing(em, Discount.class, "00000000-0000-0000-0000-000000001101",
                new Discount("00000000-0000-0000-0000-000000001101", "WELCOME10", 10, timestamp("2026-12-31 23:59:59"), 100));
        persistIfMissing(em, Discount.class, "00000000-0000-0000-0000-000000001102",
                new Discount("00000000-0000-0000-0000-000000001102", "SUMMER15", 15, timestamp("2026-08-31 23:59:59"), 50));
        persistIfMissing(em, Discount.class, "00000000-0000-0000-0000-000000001103",
                new Discount("00000000-0000-0000-0000-000000001103", "VIP20", 20, timestamp("2026-10-31 23:59:59"), 20));
        persistIfMissing(em, Discount.class, "00000000-0000-0000-0000-000000001104",
                new Discount("00000000-0000-0000-0000-000000001104", "STUDENT5", 5, timestamp("2026-09-30 23:59:59"), 200));
        persistIfMissing(em, Discount.class, "00000000-0000-0000-0000-000000001105",
                new Discount("00000000-0000-0000-0000-000000001105", "GREEN25", 25, timestamp("2026-07-31 23:59:59"), 30));
    }

    private static void seedRentalDiscounts(EntityManager em) {
        persistIfMissing(em, RentalDiscount.class, "00000000-0000-0000-0000-000000001201",
                new RentalDiscount("00000000-0000-0000-0000-000000001201", "00000000-0000-0000-0000-000000000801", "00000000-0000-0000-0000-000000001101"));
        persistIfMissing(em, RentalDiscount.class, "00000000-0000-0000-0000-000000001202",
                new RentalDiscount("00000000-0000-0000-0000-000000001202", "00000000-0000-0000-0000-000000000802", "00000000-0000-0000-0000-000000001102"));
        persistIfMissing(em, RentalDiscount.class, "00000000-0000-0000-0000-000000001203",
                new RentalDiscount("00000000-0000-0000-0000-000000001203", "00000000-0000-0000-0000-000000000803", "00000000-0000-0000-0000-000000001103"));
        persistIfMissing(em, RentalDiscount.class, "00000000-0000-0000-0000-000000001204",
                new RentalDiscount("00000000-0000-0000-0000-000000001204", "00000000-0000-0000-0000-000000000804", "00000000-0000-0000-0000-000000001104"));
        persistIfMissing(em, RentalDiscount.class, "00000000-0000-0000-0000-000000001205",
                new RentalDiscount("00000000-0000-0000-0000-000000001205", "00000000-0000-0000-0000-000000000805", "00000000-0000-0000-0000-000000001105"));
    }

    private static void seedReviews(EntityManager em) {
        persistIfMissing(em, Review.class, "00000000-0000-0000-0000-000000001301",
                review("00000000-0000-0000-0000-000000001301", "00000000-0000-0000-0000-000000000801", accountId(em, "nguyenvana@gmail.com"), "00000000-0000-0000-0000-000000000501", 5, "Xe sach, pin tot, nhan vien ho tro nhanh"));
        persistIfMissing(em, Review.class, "00000000-0000-0000-0000-000000001302",
                review("00000000-0000-0000-0000-000000001302", "00000000-0000-0000-0000-000000000802", accountId(em, "tranthib@gmail.com"), "00000000-0000-0000-0000-000000000502", 4, "Xe nho gon, de su dung"));
        persistIfMissing(em, Review.class, "00000000-0000-0000-0000-000000001303",
                review("00000000-0000-0000-0000-000000001303", "00000000-0000-0000-0000-000000000803", accountId(em, "nguyenvana@gmail.com"), "00000000-0000-0000-0000-000000000503", 4, "Gia hop ly, phu hop di trong noi thanh"));
        persistIfMissing(em, Review.class, "00000000-0000-0000-0000-000000001304",
                review("00000000-0000-0000-0000-000000001304", "00000000-0000-0000-0000-000000000804", accountId(em, "tranthib@gmail.com"), "00000000-0000-0000-0000-000000000504", 3, "Don bi huy do xe bao tri"));
        persistIfMissing(em, Review.class, "00000000-0000-0000-0000-000000001305",
                review("00000000-0000-0000-0000-000000001305", "00000000-0000-0000-0000-000000000805", accountId(em, "giahuy_badluck@gmail.com"), "00000000-0000-0000-0000-000000000505", 5, "Xe cao cap, trai nghiem tot"));
    }

    private static void seedVehicleMaintenances(EntityManager em) {
        persistIfMissing(em, VehicleMaintenance.class, "00000000-0000-0000-0000-000000001401",
                maintenance("00000000-0000-0000-0000-000000001401", "00000000-0000-0000-0000-000000000701", "Kiem tra phanh va lop xe", "2026-06-02 08:00:00", MaintenanceStatus.COMPLETED));
        persistIfMissing(em, VehicleMaintenance.class, "00000000-0000-0000-0000-000000001402",
                maintenance("00000000-0000-0000-0000-000000001402", "00000000-0000-0000-0000-000000000702", "Ve sinh xe dinh ky", "2026-06-03 09:00:00", MaintenanceStatus.COMPLETED));
        persistIfMissing(em, VehicleMaintenance.class, "00000000-0000-0000-0000-000000001403",
                maintenance("00000000-0000-0000-0000-000000001403", "00000000-0000-0000-0000-000000000703", "Kiem tra pin sau chuyen thue", "2026-06-07 10:00:00", MaintenanceStatus.PENDING));
        persistIfMissing(em, VehicleMaintenance.class, "00000000-0000-0000-0000-000000001404",
                maintenance("00000000-0000-0000-0000-000000001404", "00000000-0000-0000-0000-000000000704", "Sua loi he thong dieu khien", "2026-06-08 11:00:00", MaintenanceStatus.PENDING));
        persistIfMissing(em, VehicleMaintenance.class, "00000000-0000-0000-0000-000000001405",
                maintenance("00000000-0000-0000-0000-000000001405", "00000000-0000-0000-0000-000000000705", "Kiem tra noi that va sac pin", "2026-06-09 13:00:00", MaintenanceStatus.COMPLETED));
    }

    private static void seedIncidentReports(EntityManager em) {
        persistIfMissing(em, IncidentReport.class, "00000000-0000-0000-0000-000000001501",
                incident("00000000-0000-0000-0000-000000001501", "00000000-0000-0000-0000-000000000801", "00000000-0000-0000-0000-000000000701", "Khach bao xe co vet tray nho o than xe", IncidentSeverity.LOW, "2026-06-03 18:30:00"));
        persistIfMissing(em, IncidentReport.class, "00000000-0000-0000-0000-000000001502",
                incident("00000000-0000-0000-0000-000000001502", "00000000-0000-0000-0000-000000000802", "00000000-0000-0000-0000-000000000702", "Khach bao chuong xe hoat dong khong on dinh", IncidentSeverity.LOW, "2026-06-04 10:00:00"));
        persistIfMissing(em, IncidentReport.class, "00000000-0000-0000-0000-000000001503",
                incident("00000000-0000-0000-0000-000000001503", "00000000-0000-0000-0000-000000000803", "00000000-0000-0000-0000-000000000703", "Xe bi giam pin nhanh hon du kien", IncidentSeverity.MEDIUM, "2026-06-06 14:00:00"));
        persistIfMissing(em, IncidentReport.class, "00000000-0000-0000-0000-000000001504",
                incident("00000000-0000-0000-0000-000000001504", "00000000-0000-0000-0000-000000000804", "00000000-0000-0000-0000-000000000704", "Xe can kiem tra he thong truoc khi giao khach", IncidentSeverity.MEDIUM, "2026-06-08 11:30:00"));
        persistIfMissing(em, IncidentReport.class, "00000000-0000-0000-0000-000000001505",
                incident("00000000-0000-0000-0000-000000001505", "00000000-0000-0000-0000-000000000805", "00000000-0000-0000-0000-000000000705", "Khach yeu cau kiem tra xe truoc ngay nhan", IncidentSeverity.LOW, "2026-06-10 12:30:00"));
    }

    private static Account account(String id, String email, String password, String fullName, String phone,
            boolean verified, Role role, String status) {
        Account account = new Account(id, email, password, fullName, phone, verified, role, status, now());
        return account;
    }

    private static Vehicle vehicle(String id, String modelId, String stationId, String plate, String color,
            int battery, VehicleStatus status) {
        return new Vehicle(id, modelId, stationId, plate, color, battery, status, now());
    }

    private static Rental rental(String id, String customerId, String vehicleId, String stationId,
            String startDate, String endDate, int totalDays, double totalAmount, RentalStatus status) {
        return new Rental(id, customerId, vehicleId, stationId, Date.valueOf(startDate), Date.valueOf(endDate),
                totalDays, BigDecimal.valueOf(totalAmount), status, now());
    }

    private static RentalStatusHistory history(String id, String rentalId, RentalStatus status, String changedAt) {
        return new RentalStatusHistory(id, rentalId, status, timestamp(changedAt));
    }

    private static WalletTransaction walletTransaction(String id, String walletId, double amount,
            TransactionType type, String description) {
        return new WalletTransaction(id, walletId, amount, type, description, now());
    }

    private static Review review(String id, String rentalId, String customerId, String modelId, int rating,
            String comment) {
        return new Review(id, rentalId, customerId, modelId, rating, comment, now());
    }

    private static VehicleMaintenance maintenance(String id, String vehicleId, String description,
            String maintenanceDate, MaintenanceStatus status) {
        return new VehicleMaintenance(id, vehicleId, description, timestamp(maintenanceDate), status);
    }

    private static IncidentReport incident(String id, String rentalId, String vehicleId, String description,
            IncidentSeverity severity, String createdAt) {
        return new IncidentReport(id, rentalId, vehicleId, description, severity, timestamp(createdAt));
    }

    private static void persistAccountIfMissing(EntityManager em, Account account) {
        if (findAccountByEmail(em, account.getEmail()) == null) {
            em.persist(account);
        }
    }

    private static <T> void persistIfMissing(EntityManager em, Class<T> type, String id, T entity) {
        if (id != null && em.find(type, id) == null) {
            em.persist(entity);
        }
    }

    private static Account findAccountByEmail(EntityManager em, String email) {
        List<Account> accounts = em.createQuery(
                "SELECT a FROM Account a WHERE a.email = :email",
                Account.class)
                .setParameter("email", email)
                .setMaxResults(1)
                .getResultList();
        return accounts.isEmpty() ? null : accounts.get(0);
    }

    private static String accountId(EntityManager em, String email) {
        Account account = findAccountByEmail(em, email);
        if (account == null) {
            throw new IllegalStateException("Missing seed account: " + email);
        }
        return account.getAccountId();
    }

    private static Timestamp now() {
        return new Timestamp(System.currentTimeMillis());
    }

    private static Timestamp timestamp(String value) {
        return Timestamp.valueOf(value);
    }
}
