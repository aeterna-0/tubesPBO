package Backend;

import java.sql.Connection;
import java.sql.Statement;

public class SetupStrukturSBAU {
    public static void main(String[] args) {
        try (Connection conn = KoneksiSetup.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("CREATE DATABASE IF NOT EXISTS sbau");
            stmt.execute("USE sbau");

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS mahasiswa (
                    nim VARCHAR(20) PRIMARY KEY,
                    nama VARCHAR(100),
                    jurusan VARCHAR(50)
                )
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS matakuliah (
                    kode_mk VARCHAR(10) PRIMARY KEY,
                    nama_mk VARCHAR(100),
                    sks INT,
                    jadwal VARCHAR(100)
                )
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS dosen (
                    nidn VARCHAR(20) PRIMARY KEY,
                    nama VARCHAR(100),
                    kode_mk_ampu VARCHAR(10),
                    FOREIGN KEY (kode_mk_ampu) REFERENCES matakuliah(kode_mk)
                )
            """);

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

            System.out.println("Database dan tabel berhasil dibuat!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
