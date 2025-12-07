package Admin;

import Backend.KoneksiAdmin;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MataKuliahDAO {

    public List<MataKuliah> getAllMataKuliah() throws SQLException {
        List<MataKuliah> list = new ArrayList<>();

        String sql = "SELECT kode_mk, nama_mk, sks, jadwal FROM matakuliah ORDER BY kode_mk";

        try (Connection conn = KoneksiAdmin.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                MataKuliah mk = new MataKuliah(
                        rs.getString("kode_mk"),
                        rs.getString("nama_mk"),
                        rs.getInt("sks"),
                        rs.getString("jadwal")
                );
                list.add(mk);
            }
        }

        return list;
    }

    public void insertMataKuliah(MataKuliah mk) throws SQLException {
        String sql = "INSERT INTO matakuliah (kode_mk, nama_mk, sks, jadwal) VALUES (?, ?, ?, ?)";

        try (Connection conn = KoneksiAdmin.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, mk.getKodeMk());
            ps.setString(2, mk.getNamaMk());
            ps.setInt(3, mk.getSks());
            ps.setString(4, mk.getJadwal());
            ps.executeUpdate();
        }
    }

    public void updateMataKuliah(MataKuliah mk) throws SQLException {
        String sql = "UPDATE matakuliah SET nama_mk = ?, sks = ?, jadwal = ? WHERE kode_mk = ?";

        try (Connection conn = KoneksiAdmin.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, mk.getNamaMk());
            ps.setInt(2, mk.getSks());
            ps.setString(3, mk.getJadwal());
            ps.setString(4, mk.getKodeMk());
            ps.executeUpdate();
        }
    }

    public void deleteMataKuliah(String kodeMk) throws SQLException {
        String sql = "DELETE FROM matakuliah WHERE kode_mk = ?";

        try (Connection conn = KoneksiAdmin.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, kodeMk);
            ps.executeUpdate();
        }
    }
}
