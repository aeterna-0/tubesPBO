package GUI;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class AplikasiLoginSBAU {

    public static Connection loginKeDatabase(String username, String password) throws SQLException {
        String url = "jdbc:mysql://localhost:3306/SBAU";
        return DriverManager.getConnection(url, username, password);
    }

    public static String cariNamaLengkap(Connection conn, String username) {
        if (username.startsWith("admin")) {
            return "Administrator";
        }

        String sql = "";
        String kolom = "nama";

        if (username.length() == 10) {
            sql = "SELECT nama FROM mahasiswa WHERE nim = ?";
        } else {
            sql = "SELECT nama FROM dosen WHERE nidn = ?";
        }

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString(kolom);
            }
        } catch (SQLException e) {
            System.err.println("(Gagal mengambil nama profil: " + e.getMessage() + ")");
        }
        return username;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("=== LOGIN SISTEM AKADEMIK (SBAU) ===");
        System.out.print("Username (NIM/NIDN/Admin): ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        try (Connection conn = loginKeDatabase(username, password)) {
            String namaAsli = cariNamaLengkap(conn, username);

            System.out.println("\n[SUKSES] Login Berhasil!");
            System.out.println("Selamat Datang, " + namaAsli);
            System.out.println("============================================");

            if (username.startsWith("admin")) {
                menuAdmin(conn);
            } else if (username.length() == 10) {
                menuMahasiswa(conn, username);
            } else {
                menuDosen(conn, username);
            }

        } catch (SQLException e) {
            System.out.println("\n[GAGAL] Login Ditolak!");
            System.out.println("Pesan Error: " + e.getMessage());
        }
    }

    // ================= MENU ADMIN =================
    static void menuAdmin(Connection conn) {
        System.out.println(">> MENU ADMIN");
        System.out.println("1. Manajemen User (Tambah Dosen/Mahasiswa)");
        System.out.println("2. Setup Mata Kuliah");
        System.out.println("0. Keluar");
    }

    // ================= MENU DOSEN =================
    static void menuDosen(Connection conn, String nidn) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\n=== DASHBOARD DOSEN ===");
            System.out.println("1. Input Nilai Mahasiswa");
            System.out.println("2. Lihat Data Mahasiswa");
            System.out.println("0. Keluar / Logout");
            System.out.print("Pilih Menu: ");
            String pilihan = scanner.nextLine();

            if (pilihan.equals("1")) {
                inputNilaiMahasiswa(conn, scanner, nidn);
            } else if (pilihan.equals("2")) {
                lihatDataMahasiswa(conn);
            } else if (pilihan.equals("0")) {
                break;
            } else {
                System.out.println("Pilihan tidak valid.");
            }
        }
    }

    static void lihatDataMahasiswa(Connection conn) {
        System.out.println("\n--- DATA MAHASISWA TERDAFTAR ---");
        String sql = "SELECT * FROM mahasiswa";

        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            System.out.printf("| %-12s | %-30s | %-20s |\n", "NIM", "NAMA LENGKAP", "JURUSAN");
            System.out.println("----------------------------------------------------------------------");

            boolean adaData = false;
            while(rs.next()) {
                adaData = true;
                System.out.printf("| %-12s | %-30s | %-20s |\n",
                        rs.getString("nim"),
                        rs.getString("nama"),
                        rs.getString("jurusan"));
            }
            System.out.println("----------------------------------------------------------------------");
            if (!adaData) System.out.println("Belum ada data mahasiswa.");

        } catch (SQLException e) {
            System.err.println("[ERROR] Gagal mengambil data: " + e.getMessage());
        }
    }

    static void inputNilaiMahasiswa(Connection conn, Scanner scanner, String nidn) {
        System.out.println("\n--- INPUT NILAI MAHASISWA ---");
        try {
            System.out.println("Daftar Mata Kuliah aktif di KRS:");
            String sqlShowMK = "SELECT DISTINCT kode_mk FROM krs";
            try (PreparedStatement pstmt = conn.prepareStatement(sqlShowMK);
                 ResultSet rs = pstmt.executeQuery()) {
                while(rs.next()) System.out.print("[" + rs.getString("kode_mk") + "] ");
                System.out.println();
            }

            System.out.print("Masukkan Kode Mata Kuliah: ");
            String kodeMk = scanner.nextLine();
            System.out.print("Masukkan NIM Mahasiswa: ");
            String nimMhs = scanner.nextLine();
            System.out.print("Masukkan Nilai (A/B/C/D/E): ");
            String nilai = scanner.nextLine().toUpperCase();

            String sqlUpdate = "UPDATE krs SET nilai = ? WHERE nim = ? AND kode_mk = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sqlUpdate)) {
                pstmt.setString(1, nilai);
                pstmt.setString(2, nimMhs);
                pstmt.setString(3, kodeMk);
                int affected = pstmt.executeUpdate();
                if (affected > 0) System.out.println("[SUKSES] Nilai berhasil diupdate!");
                else System.out.println("[GAGAL] Data tidak ditemukan.");
            }
        } catch (SQLException e) {
            System.err.println("[ERROR SQL] " + e.getMessage());
        }
    }

    // ================= MENU MAHASISWA =================
    static void menuMahasiswa(Connection conn, String nim) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\n=== DASHBOARD MAHASISWA (" + nim + ") ===");
            System.out.println("1. Isi KRS (Ambil Mata Kuliah)");
            System.out.println("2. Lihat KHS (Nilai & SKS)");
            System.out.println("3. Lihat Daftar Mata Kuliah Tersedia"); // OPSI BARU
            System.out.println("0. Keluar / Logout");
            System.out.print("Pilih Menu: ");
            String pilihan = scanner.nextLine();

            if (pilihan.equals("1")) {
                ambilKrsMahasiswa(conn, scanner, nim);
            } else if (pilihan.equals("2")) {
                lihatKHSMahasiswa(conn, nim);
            } else if (pilihan.equals("3")) {
                lihatDaftarMataKuliah(conn); // PANGGIL FUNGSI BARU DI BAWAH
            } else if (pilihan.equals("0")) {
                System.out.println("Logout berhasil.");
                break;
            } else {
                System.out.println("Pilihan tidak valid.");
            }
        }
    }

    static void ambilKrsMahasiswa(Connection conn, Scanner scanner, String nim) {
        System.out.println("\n--- PENGISIAN KRS ---");

        // Memanggil fungsi lihat daftar MK agar mahasiswa bisa memilih
        lihatDaftarMataKuliah(conn);

        System.out.print("\nMasukkan KODE MK yang ingin diambil: ");
        String kodeMk = scanner.nextLine();

        if (kodeMk.trim().isEmpty()) {
            return;
        }

        String sqlInsert = "INSERT INTO krs (nim, kode_mk) VALUES (?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sqlInsert)) {
            pstmt.setString(1, nim);
            pstmt.setString(2, kodeMk);
            int affected = pstmt.executeUpdate();
            if (affected > 0) System.out.println("[SUKSES] Mata Kuliah " + kodeMk + " berhasil diambil!");
        } catch (java.sql.SQLIntegrityConstraintViolationException e) {
            System.out.println("[GAGAL] Anda sudah mengambil mata kuliah ini sebelumnya.");
        } catch (SQLException e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    static void lihatKHSMahasiswa(Connection conn, String nim) {
        System.out.println("\n--- KARTU HASIL STUDI (KHS) ---");
        String sql = """
            SELECT mk.kode_mk, mk.nama_mk, mk.sks, k.nilai 
            FROM krs k
            JOIN matakuliah mk ON k.kode_mk = mk.kode_mk
            WHERE k.nim = ?
        """;
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nim);
            ResultSet rs = pstmt.executeQuery();
            System.out.printf("| %-10s | %-30s | %-3s | %-5s |\n", "KODE", "NAMA MK", "SKS", "NILAI");
            System.out.println("------------------------------------------------------------");
            int totalSKS = 0;
            while (rs.next()) {
                String nilai = rs.getString("nilai");
                if (nilai == null) nilai = "-";
                System.out.printf("| %-10s | %-30s | %-3d | %-5s |\n",
                        rs.getString("kode_mk"), rs.getString("nama_mk"), rs.getInt("sks"), nilai);
                totalSKS += rs.getInt("sks");
            }
            System.out.println("------------------------------------------------------------");
            System.out.println("Total SKS Diambil: " + totalSKS);
        } catch (SQLException e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    // ================= FUNGSI TAMBAHAN =================
    // Method ini sekarang ada dan siap dipanggil
    static void lihatDaftarMataKuliah(Connection conn) {
        System.out.println("\n--- DAFTAR MATA KULIAH TERSEDIA ---");
        String sql = "SELECT * FROM matakuliah";

        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            // Format tabel diperlebar untuk jadwal
            System.out.printf("| %-10s | %-32s | %-3s | %-30s |\n", "KODE", "NAMA MK", "SKS", "JADWAL");
            System.out.println("-------------------------------------------------------------------------------------");

            boolean adaData = false;
            while (rs.next()) {
                adaData = true;
                System.out.printf("| %-10s | %-32s | %-3d | %-30s |\n",
                        rs.getString("kode_mk"),
                        rs.getString("nama_mk"), // Batasi panjang string jika perlu
                        rs.getInt("sks"),
                        rs.getString("jadwal")); // Menampilkan kolom baru
            }
            System.out.println("-------------------------------------------------------------------------------------");

            if (!adaData) System.out.println("Belum ada data mata kuliah.");

        } catch (SQLException e) {
            System.out.println("[ERROR] Gagal mengambil data MK: " + e.getMessage());
        }
    }
}