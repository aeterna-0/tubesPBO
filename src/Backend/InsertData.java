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

            // MATIKAN FOREIGN KEY
            stmt.execute("SET FOREIGN_KEY_CHECKS = 0");

            // TRUNCATE (reset tabel sebelum insert)
            stmt.execute("TRUNCATE TABLE krs");
            stmt.execute("TRUNCATE TABLE dosen");
            stmt.execute("TRUNCATE TABLE mahasiswa");
            stmt.execute("TRUNCATE TABLE matakuliah");

            // HIDUPKAN FOREIGN KEY
            stmt.execute("SET FOREIGN_KEY_CHECKS = 1");

        } catch (Exception e) {
            System.err.println("[ERROR RESET] " + e.getMessage());
        }

        // ====================================================
        // =============== INSERT MAHASISWA ===================
        // ====================================================
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
            System.out.println("✓ Insert data mahasiswa sukses");
        } catch (Exception e) {
            System.err.println("[ERROR MAHASISWA] " + e.getMessage());
        }

        // ====================================================
        // =============== INSERT MATA KULIAH ===================
        // ====================================================
        String sqlMK =
                "INSERT INTO matakuliah (kode_mk, nama_mk, sks, jadwal) VALUES (?, ?, ?, ?)";

        Object[][] dataMK = {
                {"INF124301", "Metode Penelitian", 3, "Selasa, 13:00-15:30 (R.301)"},
                {"INF124302", "Jaringan Komputer", 2, "Jumat, 09:40-11:20 (R.303)"},
                {"INF124303", "Praktikum Jaringan Komputer", 1, "Selasa, 10:30-12:10 (Lab 301)"},
                {"INF124304", "Manajemen Proyek Perangkat Lunak", 3, "Selasa, 15:30-18:00 (R.302)"},
                {"INF124305", "Sistem Operasi", 2, "Kamis, 08:00-09:40 (R.403)"},
                {"INF124306", "Praktikum Sistem Operasi", 1, "Kamis, 10:30-12:10 (Lab 402)"},
                {"INF124307", "Kompleksitas Algoritma", 3, "Rabu, 08:50-10:30 (R.401)"},
                {"INF124308", "Pemrograman Berorientasi Objek", 2, "Rabu, 07:10-08:50 (R.301)"},
                {"INF124309", "Praktikum PBO", 1, "Kamis, 14:40-16:20 (Lab 403)"},
                {"INF124310", "Interaksi Manusia dan Komputer", 3, "Jumat, 13:30-16:00 (R.202)"},
                {"INF124311", "Keamanan Siber (CE.1)", 3, "Rabu, 13:00-15:30 (Lab 304)"}
        };

        try (PreparedStatement ps = conn.prepareStatement(sqlMK)) {
            for (Object[] mk : dataMK) {
                ps.setString(1, (String) mk[0]);
                ps.setString(2, (String) mk[1]);
                ps.setInt(3, (int) mk[2]);
                ps.setString(4, (String) mk[3]);
                ps.addBatch();
            }
            ps.executeBatch();
            System.out.println("✓ Insert data matakuliah sukses");
        } catch (Exception e) {
            System.err.println("[ERROR MATA KULIAH] " + e.getMessage());
        }

        // ====================================================
        // =============== INSERT DOSEN ===================
        // ====================================================
        String sqlDosen =
                "INSERT INTO dosen (nidn, nama, kode_mk_ampu) VALUES (?, ?, ?)";

        String[][] dataDosen = {
                {"19651110021211004", "Prof. Dr. Ir. Supriyanto, S.T., M.Sc., IPM.", "INF124302"},
                {"19701205021211008", "Assoc. Prof. Dr. Choo Yun Huoy", "INF124307"},
                {"19720520021211002", "Dr. Widya Cholil, M.I.T", "INF124310"},
                {"19741010021211013", "Dr. Mufid Nilmada, SSi., MMSI", "INF124308"},
                {"19760814021211011", "Dr. Indra Permana Solihin, M.Kom", "INF124301"},
                {"19780222021211010", "Darius Antoni, Ph.D.", "INF124304"},
                {"19811220021211015", "Ferdiansyah, M.Kom., Ph.D.", "INF124305"},
                {"19830809021211001", "Ika Nurlaili Isnainiyah, S.Kom., M.Sc.", "INF124310"},
                {"19850725021211005", "Henki Bayu Seta, S.Kom., MTI.", "INF124311"},
                {"19860618021211009", "Sanggi Bayu Ardika, S.Kom., M.Kom", "INF124304"},
                {"19880912021211006", "Hamonangan Kinantan P., M.T.", "INF124311"},
                {"19890130021211007", "Ichsan Mardani, S.Kom., MSc.", "INF124307"},
                {"19900315021211003", "Nurhuda Maulana, S.T., M.T.", "INF124302"},
                {"19910417021211012", "Musthofa Galih Pradana, S.Kom., M.Kom", "INF124308"},
                {"19920505021211014", "Novi Trisman Hadi, S.Pd., M.Kom", "INF124305"}
        };

        try (PreparedStatement ps = conn.prepareStatement(sqlDosen)) {
            for (String[] d : dataDosen) {
                ps.setString(1, d[0]);
                ps.setString(2, d[1]);
                ps.setString(3, d[2]);
                ps.addBatch();
            }
            ps.executeBatch();
            System.out.println("✓ Insert data dosen sukses");
        } catch (Exception e) {
            System.err.println("[ERROR DOSEN] " + e.getMessage());
        }

        System.out.println("==================================");
        System.out.println("   SEMUA DATA BERHASIL DIINSERT   ");
        System.out.println("==================================");
    }
}
