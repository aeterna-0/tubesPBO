package Admin;

import Backend.KoneksiAdmin;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DosenDAO {

    public List<Dosen> getAllDosen() throws SQLException {
        List<Dosen> list = new ArrayList<>();

        String sql = "SELECT nidn, nama, keahlian FROM dosen ORDER BY nidn";

        try (Connection conn = KoneksiAdmin.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Dosen d = new Dosen(
                        rs.getString("nidn"),
                        rs.getString("nama"),
                        rs.getString("keahlian")
                );
                list.add(d);
            }
        }

        return list;
    }

    public void insertDosen(Dosen d) throws SQLException {
        String sql = "INSERT INTO dosen (nidn, nama, keahlian) VALUES (?, ?, ?)";

        try (Connection conn = KoneksiAdmin.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, d.getNidn());
            ps.setString(2, d.getNama());
            ps.setString(3, d.getKeahlian());
            ps.executeUpdate();
        }
    }

    public void updateDosen(Dosen d) throws SQLException {
        String sql = "UPDATE dosen SET nama = ?, keahlian = ? WHERE nidn = ?";

        try (Connection conn = KoneksiAdmin.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, d.getNama());
            ps.setString(2, d.getKeahlian());
            ps.setString(3, d.getNidn());
            ps.executeUpdate();
        }
    }

    public void deleteDosen(String nidn) throws SQLException {
        String sql = "DELETE FROM dosen WHERE nidn = ?";

        try (Connection conn = KoneksiAdmin.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nidn);
            ps.executeUpdate();
        }
    }
}
