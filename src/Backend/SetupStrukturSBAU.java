package Backend;

import java.sql.Connection;
import java.sql.Statement;

public class SetupStrukturSBAU {
    public static void main(String[] args) {
        // Kita gunakan koneksi root awal untuk setup struktur
        try (Connection conn = KoneksiAdmin.getConnection();
             Statement stmt = conn.createStatement()) {

            // === Tambahkan pembuatan database di sini ===
            stmt.execute("CREATE DATABASE IF NOT EXISTS sbau");
            stmt.execute("USE sbau");

            // 1. Tabel Mahasiswa
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS mahasiswa (
                    nim VARCHAR(20) PRIMARY KEY,
                    nama VARCHAR(100),
                    jurusan VARCHAR(50)
                )
            """);

            // 2. Tabel Dosen (kolom keahlian diganti dengan kode_mk_ampu)
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS dosen (
                    nidn VARCHAR(20) PRIMARY KEY,
                    nama VARCHAR(100),
                    kode_mk_ampu VARCHAR(10),
                    FOREIGN KEY (kode_mk_ampu) REFERENCES matakuliah(kode_mk)
                )
            """);

            // 3. Tabel Mata Kuliah
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS matakuliah (
                    kode_mk VARCHAR(10) PRIMARY KEY,
                    nama_mk VARCHAR(100),
                    sks INT,
                    jadwal VARCHAR(100)
                )
            """);

            // 4. Tabel KRS & Nilai
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS krs (
                    id_krs INT AUTO_INCREMENT PRIMARY KEY,
                    nim VARCHAR(20),
                    nidn_dosen VARCHAR(20),
                    kode_mk VARCHAR(10),
                    nilai VARCHAR(2),
                    FOREIGN KEY (nim) REFERENCES mahasiswa(nim),
                    FOREIGN KEY (nidn_dosen) REFERENCES dosen(nidn),
                    FOREIGN KEY (kode_mk) REFERENCES matakuliah(kode_mk)
                )
            """);

            System.out.println("Struktur Tabel SBAU Berhasil Dibuat!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
