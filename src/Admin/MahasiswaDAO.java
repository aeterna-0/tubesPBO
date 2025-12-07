package Admin;

import Backend.KoneksiAdmin;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MahasiswaDAO {

    public List<Mahasiswa> getAllMahasiswa() throws SQLException {
        List<Mahasiswa> list = new ArrayList<>();

        String sql = "SELECT nim, nama, jurusan FROM mahasiswa ORDER BY nim";

        try (Connection conn = KoneksiAdmin.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Mahasiswa m = new Mahasiswa(
                        rs.getString("nim"),
                        rs.getString("nama"),
                        rs.getString("jurusan")
                );
                list.add(m);
            }
        }

        return list;
    }

    public void insertMahasiswa(Mahasiswa m) throws SQLException {
        String sql = "INSERT INTO mahasiswa (nim, nama, jurusan) VALUES (?, ?, ?)";

        try (Connection conn = KoneksiAdmin.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, m.getNim());
            ps.setString(2, m.getNama());
            ps.setString(3, m.getJurusan());
            ps.executeUpdate();
        }
    }

    public void updateMahasiswa(Mahasiswa m) throws SQLException {
        String sql = "UPDATE mahasiswa SET nama = ?, jurusan = ? WHERE nim = ?";

        try (Connection conn = KoneksiAdmin.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, m.getNama());
            ps.setString(2, m.getJurusan());
            ps.setString(3, m.getNim());
            ps.executeUpdate();
        }
    }

    public void deleteMahasiswa(String nim) throws SQLException {
        String sql = "DELETE FROM mahasiswa WHERE nim = ?";

        try (Connection conn = KoneksiAdmin.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nim);
            ps.executeUpdate();
        }
    }
}
