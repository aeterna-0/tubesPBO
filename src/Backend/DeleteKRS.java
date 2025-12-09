package Backend;

import Backend.KoneksiAdmin;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

public class DeleteKRS {
    public static void main(String[] args) {
        // Query SQL untuk menghapus data dari tabel krs
        String sql = "DELETE FROM krs WHERE nim = ? AND kode_mk = ?";
        int totalDeleted = 0; // Penghitung untuk data yang berhasil dihapus
        try (Connection conn = KoneksiAdmin.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             Scanner scanner = new Scanner(System.in)) {
            if (conn == null) {
                System.err.println("Gagal terhubung ke database. Mohon cek Koneksi.java.");
                return;
            }

            System.out.println("--- Hapus Mata Kuliah dari KRS (Berulang) ---");
            boolean deleteLagi = true;
            while (deleteLagi) {
                System.out.print("\nMasukkan NIM Mahasiswa yang KRS-nya akan dihapus: ");
                String nim = scanner.nextLine();

                System.out.print("Masukkan Kode Mata Kuliah yang akan dihapus dari KRS: ");
                String kodeMk = scanner.nextLine();

                try {
                    // 1. Set parameter untuk PreparedStatement
                    pstmt.setString(1, nim);    // nim (Kondisi WHERE)
                    pstmt.setString(2, kodeMk); // kode_mk (Kondisi WHERE)

                    // 2. Eksekusi delete
                    int rowsAffected = pstmt.executeUpdate();

                    if (rowsAffected > 0) {
                        System.out.println("Berhasil menghapus KRS: NIM " + nim + " tidak lagi mengambil MK " + kodeMk);
                        totalDeleted++;
                    } else {
                        System.out.println("Gagal menghapus. Tidak ada entri KRS ditemukan untuk kombinasi tersebut.");
                    }

                } catch (SQLException e) {
                    System.err.println("Terjadi kesalahan SQL:");
                    e.printStackTrace();
                }

                // Opsi untuk melanjutkan penghapusan
                System.out.print("\nIngin menghapus entri KRS lain? (ya/tidak): ");
                String jawaban = scanner.nextLine().trim().toLowerCase();
                if (!jawaban.equals("ya")) {
                    deleteLagi = false;
                }
            }

            System.out.println("\n--- Proses Penghapusan KRS Selesai ---");
            System.out.println("Total " + totalDeleted + " entri KRS berhasil dihapus.");

        } catch (Exception e) {
            System.err.println("Terjadi kesalahan umum:");
            e.printStackTrace();
        }
    }
}