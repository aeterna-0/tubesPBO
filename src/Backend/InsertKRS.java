import Backend.KoneksiAdmin;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

public class InsertKRS {
    public static void main(String[] args) {
        // QUERY SQL dimodifikasi untuk menyertakan kolom 'nilai'
        String sql = "INSERT INTO krs (nim, kode_mk, nilai) VALUES (?, ?, ?)";
        int totalInserted = 0;
        try (Connection conn = KoneksiAdmin.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             Scanner scanner = new Scanner(System.in)) {
            if (conn == null) {
                System.err.println("Gagal terhubung ke database. Mohon cek Koneksi.java.");
                return;
            }

            System.out.println("--- Tambah Mata Kuliah ke KRS (Beserta Nilai) ---");
            boolean inputLagi = true;
            while (inputLagi) {
                System.out.print("\nMasukkan NIM Mahasiswa: ");
                String nim = scanner.nextLine();

                System.out.print("Masukkan Kode Mata Kuliah: ");
                String kodeMk = scanner.nextLine();

                // INPUT BARU: Nilai
                System.out.print("Masukkan Nilai (contoh: A, B, C, atau -): ");
                String nilai = scanner.nextLine().toUpperCase();

                try {
                    // 1. Set parameter untuk PreparedStatement
                    pstmt.setString(1, nim);    // nim
                    pstmt.setString(2, kodeMk); // kode_mk
                    pstmt.setString(3, nilai);  // nilai (Parameter ke-3)

                    // 2. Eksekusi insert
                    int rowsAffected = pstmt.executeUpdate();

                    if (rowsAffected > 0) {
                        System.out.println("KRS berhasil ditambahkan: NIM " + nim + " mengambil MK " + kodeMk + " dengan Nilai: " + nilai);
                        totalInserted++;
                    } else {
                        System.out.println("Gagal memasukkan data KRS.");
                    }

                } catch (java.sql.SQLIntegrityConstraintViolationException e) {
                    // Menangani error Foreign Key atau Unique Constraint
                    System.err.println("ERROR: Data tidak valid atau sudah ada.");
                    System.err.println("Detail: Cek apakah NIM dan Kode MK sudah terdaftar, atau mahasiswa sudah mengambil mata kuliah ini.");
                } catch (SQLException e) {
                    System.err.println("Terjadi kesalahan SQL:");
                    e.printStackTrace();
                }

                // Opsi untuk melanjutkan input
                System.out.print("\nIngin menambahkan mata kuliah lain ke KRS? (ya/tidak): ");
                String jawaban = scanner.nextLine().trim().toLowerCase();
                if (!jawaban.equals("ya")) {
                    inputLagi = false;
                }
            }

            System.out.println("\n--- Proses Penambahan KRS Selesai ---");
            System.out.println("Total " + totalInserted + " entri KRS berhasil ditambahkan.");

        } catch (Exception e) {
            System.err.println("Terjadi kesalahan umum:");
            e.printStackTrace();
        }
    }
}