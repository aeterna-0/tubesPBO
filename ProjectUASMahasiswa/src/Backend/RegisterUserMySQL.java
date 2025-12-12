package Backend;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class RegisterUserMySQL {

    public static void main(String[] args) {
        // Jalankan file ini setiap kali Anda ingin menambah/memperbaiki User
        // Jika user sudah ada, kode ini akan memperbarui (refresh) izin aksesnya.

        createAdmin("admin1", "admin123");
        // 1. Ika Nurlaili
        createDosen("19830809021211001", "12345", "Ika Nurlaili Isnainiyah, S.Kom., M.Sc.");
        // 2. Dr. Widya
        createDosen("19720520021211002", "12345", "Dr. Widya Cholil, M.I.T");
        // 3. Nurhuda Maulana
        createDosen("19900315021211003", "12345", "Nurhuda Maulana, S.T., M.T.");
        // 4. Prof. Supriyanto
        createDosen("19651110021211004", "12345", "Prof. Dr. Ir. Supriyanto, S.T., M.Sc., IPM.");
        // 5. Henki Bayu Seta
        createDosen("19850725021211005", "12345", "Henki Bayu Seta, S.Kom., MTI.");
        // 6. Hamonangan Kinantan
        createDosen("19880912021211006", "12345", "Hamonangan Kinantan P., M.T.");
        // 7. Ichsan Mardani
        createDosen("19890130021211007", "12345", "Ichsan Mardani, S.Kom., MSc.");
        // 8. Assoc. Prof. Choo Yun Huoy
        createDosen("19701205021211008", "12345", "Assoc. Prof. Dr. Choo Yun Huoy");
        // 9. Sanggi Bayu Ardika
        createDosen("19860618021211009", "12345", "Sanggi Bayu Ardika, S.Kom., M.Kom");
        // 10. Darius Antoni
        createDosen("19780222021211010", "12345", "Darius Antoni, Ph.D.");
        // 11. Dr. Indra Permana Solihin
        createDosen("19760814021211011", "12345", "Dr. Indra Permana Solihin, M.Kom");
        // 12. Musthofa Galih Pradana
        createDosen("19910417021211012", "12345", "Musthofa Galih Pradana, S.Kom., M.Kom");
        // 13. Dr. Mufid Nilmada
        createDosen("19741010021211013", "12345", "Dr. Mufid Nilmada, SSi., MMSI");
        // 14. Novi Trisman Hadi
        createDosen("19920505021211014", "12345", "Novi Trisman Hadi, S.Pd., M.Kom");
        // 15. Ferdiansyah
        createDosen("19811220021211015", "12345", "Ferdiansyah, M.Kom., Ph.D.");
        // 1. Ahmad Billal
        createMahasiswa("2410511116", "billal123", "Ahmad Billal", "Informatika");
        // 2. Hazel Maulan
        createMahasiswa("2410511125", "hazel456", "Hazell Maulan Al Khafi", "Informatika");
        // 3. rafka Nadimsyah
        createMahasiswa("2410511117", "rafka123", "Rafka Nadimsyah Radisi", "Informatika");
        // 4. Raffi Anggi
        createMahasiswa("2410511104", "condetsejahtera123", "Raffi Anggi Rachman Budianto", "Informatika");
        // 5. Naqila Syaniwa
        createMahasiswa("2410511099", "naqilaGUI123", "Naqila Syaniwa", "Informatika");
        // 6. Muhammad Ega
        createMahasiswa("2410511133", "ega123", "Muhammad Ega Pratama", "Informatika");
    }

    // 1. Membuat ADMIN (All Privileges)
    public static void createAdmin(String username, String password) {
        String sqlCreate = "CREATE USER IF NOT EXISTS '" + username + "'@'localhost' IDENTIFIED BY '" + password + "'";
        String sqlGrant = "GRANT ALL PRIVILEGES ON SBAU.* TO '" + username + "'@'localhost' WITH GRANT OPTION";

        executeKueriUser(sqlCreate, sqlGrant, "Admin " + username);
    }

    // 2. Membuat DOSEN (Diperbarui: Ditambah izin tabel tugas)
    public static void createDosen(String nidn, String password, String nama) {
        // Masukkan data profil ke tabel dosen dulu
        insertDataDosen(nidn, nama);

        // Buat User MySQL
        String sqlCreate = "CREATE USER IF NOT EXISTS '" + nidn + "'@'localhost' IDENTIFIED BY '" + password + "'";

        // PRIVILEGES DOSEN:
        // - SELECT, INSERT, UPDATE tabel KRS (Input Nilai)
        // - SELECT tabel MAHASISWA
        // - SELECT tabel MATAKULIAH
        // - SELECT tabel DOSEN
        // - SELECT, INSERT, UPDATE, DELETE tabel TUGAS
        // - SELECT, INSERT, UPDATE tabel ABSENSI

        String sqlGrant1 = "GRANT SELECT, INSERT, UPDATE ON SBAU.krs TO '" + nidn + "'@'localhost'";
        String sqlGrant2 = "GRANT SELECT ON SBAU.mahasiswa TO '" + nidn + "'@'localhost'";
        String sqlGrant3 = "GRANT SELECT ON SBAU.matakuliah TO '" + nidn + "'@'localhost'";
        String sqlGrant4 = "GRANT SELECT ON SBAU.dosen TO '" + nidn + "'@'localhost'";
        String sqlGrant5 = "GRANT SELECT, INSERT, UPDATE, DELETE ON SBAU.tugas TO '" + nidn + "'@'localhost'";
        String sqlGrant6 = "GRANT SELECT, INSERT, UPDATE ON SBAU.absensi TO '" + nidn + "'@'localhost'";

        String allGrants = sqlGrant1 + "; " + sqlGrant2 + "; " + sqlGrant3 + "; " + sqlGrant4 + "; " + sqlGrant5 + "; " + sqlGrant6;

        executeKueriUser(sqlCreate, allGrants, "Dosen " + nidn);
    }

    // 3. Membuat MAHASISWA
    public static void createMahasiswa(String nim, String password, String nama, String jurusan) {
        // Masukkan data profil ke tabel mahasiswa dulu
        insertDataMahasiswa(nim, nama, jurusan);

        // Buat User MySQL
        String sqlCreate = "CREATE USER IF NOT EXISTS '" + nim + "'@'localhost' IDENTIFIED BY '" + password + "'";

        // PRIVILEGES MAHASISWA:
        // - SELECT matakuliah (Lihat MK tersedia)
        // - SELECT, INSERT krs (Ambil MK) -> Tapi TIDAK BOLEH Update/Delete
        // - SELECT mahasiswa (Lihat Profil sendiri)

        String sqlGrant1 = "GRANT SELECT ON SBAU.matakuliah TO '" + nim + "'@'localhost'";
        String sqlGrant2 = "GRANT SELECT, INSERT ON SBAU.krs TO '" + nim + "'@'localhost'";
        String sqlGrant3 = "GRANT SELECT ON SBAU.mahasiswa TO '" + nim + "'@'localhost'";

        String allGrants = sqlGrant1 + "; " + sqlGrant2 + "; " + sqlGrant3;

        executeKueriUser(sqlCreate, allGrants, "Mahasiswa " + nim);
    }

    // --- Method Bantuan Eksekusi ---
    private static void executeKueriUser(String create, String grant, String info) {
        try (Connection conn = KoneksiAdmin.getConnection();
             Statement stmt = conn.createStatement()) {

            // 1. Create User
            stmt.execute(create);

            // 2. Grant Privileges (Split karena execute biasanya per satu perintah)
            String[] grants = grant.split(";");
            for(String g : grants) {
                if(!g.trim().isEmpty()) {
                    try {
                        stmt.execute(g.trim());
                    } catch (SQLException ex) {
                        // Abaikan error jika grant sudah ada, tapi tampilkan log biar tau
                        System.err.println("Info Grant: " + ex.getMessage());
                    }
                }
            }
            // 3. Refresh
            stmt.execute("FLUSH PRIVILEGES");

            System.out.println("Sukses setup user MySQL: " + info);
        } catch (SQLException e) {
            System.err.println("Gagal membuat user " + info + ": " + e.getMessage());
        }
    }

    private static void insertDataMahasiswa(String nim, String nama, String jurusan) {
        try (Connection conn = KoneksiAdmin.getConnection();
             Statement stmt = conn.createStatement()) {
            String sql = "INSERT IGNORE INTO mahasiswa VALUES ('"+nim+"', '"+nama+"', '"+jurusan+"')";
            stmt.execute(sql);
        } catch (Exception e) {}
    }

    private static void insertDataDosen(String nidn, String nama) {
        try (Connection conn = KoneksiAdmin.getConnection();
             Statement stmt = conn.createStatement()) {
            String sql = "INSERT IGNORE INTO dosen VALUES ('"+nidn+"', '"+nama+"')";
            stmt.execute(sql);
        } catch (Exception e) {}
    }
}