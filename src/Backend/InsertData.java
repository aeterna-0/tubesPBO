package Backend;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;

public class InsertData {

    public static void main(String[] args) {
        try (Connection conn = KoneksiAdmin.getConnection()) {
            prosesInsert(conn);
        } catch (Exception e) {
            System.err.println("Koneksi gagal: " + e.getMessage());
        }
    }

    public static void prosesInsert(Connection conn) {
        System.out.println("=====================================");
        System.out.println("       MULAI INSERT SEMUA DATA       ");
        System.out.println("=====================================");

        try (Statement stmt = conn.createStatement()) {
            stmt.execute("SET FOREIGN_KEY_CHECKS = 0");

            stmt.execute("TRUNCATE TABLE krs");
            stmt.execute("TRUNCATE TABLE matakuliah");
            stmt.execute("TRUNCATE TABLE dosen");
            stmt.execute("TRUNCATE TABLE mahasiswa");

            stmt.execute("SET FOREIGN_KEY_CHECKS = 1");

        } catch (Exception e) {
            System.err.println("[ERROR RESET] " + e.getMessage());
        }


        // -----------------------------------------------------
        // 1. INSERT MAHASISWA
        // -----------------------------------------------------
        String sqlMahasiswa =
                "INSERT INTO mahasiswa (nim, nama, jurusan) VALUES (?, ?, ?)";

        String[][] dataMahasiswa = {
                {"2410511099", "Naqila Syaniwa", "Informatika"},
                {"2410511104", "Raffi Anggi Rachman Budianto", "Informatika"},
                {"2410511116", "Ahmad Billal", "Informatika"},
                {"2410511117", "Rafka Nadimsyah Radisi", "Informatika"},
                {"2410511125", "Hazell Maulan Al Khafi", "Informatika"},
                {"2410511133", "Muhammad Ega Pratama", "Informatika"}
        };

        try (PreparedStatement ps = conn.prepareStatement(sqlMahasiswa)) {
            for (String[] m : dataMahasiswa) {
                ps.setString(1, m[0]);
                ps.setString(2, m[1]);
                ps.setString(3, m[2]);
                ps.addBatch();
            }
            ps.executeBatch();
            System.out.println("✓ Insert mahasiswa sukses");
        } catch (Exception e) {
            System.err.println("[ERROR MAHASISWA] " + e.getMessage());
        }


        // -----------------------------------------------------
        // 2. INSERT DOSEN (sesuai tabel baru: NIDN + Nama)
        // -----------------------------------------------------
        String sqlDosen =
                "INSERT INTO dosen (nidn, nama) VALUES (?, ?)";

        String[][] dataDosen = {
                {"19651110021211004", "Prof. Dr. Ir. Supriyanto"},
                {"19701205021211008", "Assoc. Prof. Dr. Choo Yun Huoy"},
                {"19720520021211002", "Dr. Widya Cholil"},
                {"19741010021211013", "Dr. Mufid Nilmada"},
                {"19760814021211011", "Dr. Indra Permana Solihin"},
                {"19780222021211010", "Darius Antoni"},
                {"19811220021211015", "Ferdiansyah"},
                {"19830809021211001", "Ika Nurlaili Isnainiyah"},
                {"19850725021211005", "Henki Bayu Seta"},
                {"19860618021211009", "Sanggi Bayu Ardika"},
                {"19880912021211006", "Hamonangan Kinantan"},
                {"19890130021211007", "Ichsan Mardani"},
                {"19900315021211003", "Nurhuda Maulana"},
                {"19910417021211012", "Musthofa Galih Pradana"},
                {"19920505021211014", "Novi Trisman Hadi"}
        };

        try (PreparedStatement ps = conn.prepareStatement(sqlDosen)) {
            for (String[] d : dataDosen) {
                ps.setString(1, d[0]);
                ps.setString(2, d[1]);
                ps.addBatch();
            }
            ps.executeBatch();
            System.out.println("✓ Insert dosen sukses");
        } catch (Exception e) {
            System.err.println("[ERROR DOSEN] " + e.getMessage());
        }



        // -----------------------------------------------------
        // 3. INSERT MATAKULIAH (dengan nidn_dosen sesuai FK)
        // -----------------------------------------------------
        String sqlMK =
                "INSERT INTO matakuliah (kode_mk, nama_mk, sks, jadwal, nidn_dosen) VALUES (?, ?, ?, ?, ?)";

        Object[][] dataMK = {
                {"INF124301", "Metode Penelitian", 3, "Selasa, 13:00-15:30", "19760814021211011"},
                {"INF124302", "Jaringan Komputer", 2, "Jumat, 09:40-11:20", "19900315021211003"},
                {"INF124303", "Praktikum Jaringan", 1, "Selasa, 10:30-12:10", "19900315021211003"},
                {"INF124304", "Manajemen Projek", 3, "Selasa, 15:30-18:00", "19780222021211010"},
                {"INF124305", "Sistem Operasi", 2, "Kamis, 08:00-09:40", "19811220021211015"},
                {"INF124306", "Praktikum Sistem Operasi", 1, "Kamis, 10:30-12:10", "19811220021211015"},
                {"INF124307", "Kompleksitas Algoritma", 3, "Rabu, 08:50-10:30", "19701205021211008"},
                {"INF124308", "Pemrograman OOP", 2, "Rabu, 07:10-08:50", "19741010021211013"},
                {"INF124309", "Praktikum PBO", 1, "Kamis, 14:40-16:20", "19741010021211013"},
                {"INF124310", "Interaksi Manusia Komputer", 3, "Jumat, 13:30-16:00", "19720520021211002"},
                {"INF124311", "Keamanan Siber", 3, "Rabu, 13:00-15:30", "19850725021211005"}
        };

        try (PreparedStatement ps = conn.prepareStatement(sqlMK)) {
            for (Object[] mk : dataMK) {
                ps.setString(1, (String) mk[0]);
                ps.setString(2, (String) mk[1]);
                ps.setInt(3, (int) mk[2]);
                ps.setString(4, (String) mk[3]);
                ps.setString(5, (String) mk[4]);
                ps.addBatch();
            }
            ps.executeBatch();
            System.out.println("✓ Insert matakuliah sukses");
        } catch (Exception e) {
            System.err.println("[ERROR MATAKULIAH] " + e.getMessage());
        }

        System.out.println("==================================");
        System.out.println("   SEMUA DATA BERHASIL DIINSERT   ");
        System.out.println("==================================");
    }
}
