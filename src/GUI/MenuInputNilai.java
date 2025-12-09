package GUI;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class MenuInputNilai extends JFrame {

    private Connection conn;
    private String nidn;

    private JComboBox<String> cbKodeMK;
    private JTextField tfNIM;
    private JComboBox<String> cbNilai;
    private JButton btnSimpan;

    public MenuInputNilai(Connection conn, String nidn) {
        super("Input Nilai Mahasiswa");
        this.conn = conn;
        this.nidn = nidn;

        setSize(500, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel lblTitle = new JLabel("Input Nilai Mahasiswa");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));

        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 0, 20, 0);
        panel.add(lblTitle, gbc);

        // ================== KODE MK ==================
        gbc.gridwidth = 1;
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Kode Mata Kuliah:"), gbc);

        cbKodeMK = new JComboBox<>();
        cbKodeMK.setPreferredSize(new Dimension(200, 28));

        gbc.gridx = 1;
        panel.add(cbKodeMK, gbc);

        loadKodeMK();  // LOAD MK sesuai dosen pengampu

        // ================== NIM ==================
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("NIM Mahasiswa:"), gbc);

        tfNIM = new JTextField();
        tfNIM.setPreferredSize(new Dimension(200, 28));

        gbc.gridx = 1;
        panel.add(tfNIM, gbc);

        // ================== NILAI ==================
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Nilai:"), gbc);

        cbNilai = new JComboBox<>(new String[]{"A", "B", "C", "D", "E"});
        cbNilai.setPreferredSize(new Dimension(200, 28));

        gbc.gridx = 1;
        panel.add(cbNilai, gbc);

        // ================== BUTTON SIMPAN ==================
        btnSimpan = new JButton("Simpan Nilai");
        btnSimpan.setPreferredSize(new Dimension(180, 35));
        btnSimpan.setFont(new Font("Segoe UI", Font.BOLD, 16));

        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 0, 10, 0);
        panel.add(btnSimpan, gbc);

        add(panel);

        // ACTION SAVE
        btnSimpan.addActionListener(e -> saveNilai());

        setVisible(true);
    }

    // ================== LOAD KODE MK SESUAI DOSEN ==================
    private void loadKodeMK() {
        try {
            String sql = "SELECT kode_mk_ampu FROM dosen WHERE nidn = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, nidn);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                cbKodeMK.addItem(rs.getString("kode_mk_ampu"));
            }

            rs.close();
            stmt.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error Load MK: " + e.getMessage());
        }
    }

    // ================== SIMPAN NILAI DENGAN VALIDASI DOSEN AMPU ==================
    private void saveNilai() {
        String kodeMk = (String) cbKodeMK.getSelectedItem();
        String nim = tfNIM.getText().trim();
        String nilai = (String) cbNilai.getSelectedItem();

        if (nim.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "NIM tidak boleh kosong!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // Hanya update KRS jika dosen ini memang yang mengajar
            String sql = """
                    UPDATE krs 
                    SET nilai = ? 
                    WHERE nim = ? AND kode_mk = ? AND nidn_dosen = ?
                    """;

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, nilai);
            pstmt.setString(2, nim);
            pstmt.setString(3, kodeMk);
            pstmt.setString(4, nidn);

            int affected = pstmt.executeUpdate();

            if (affected > 0) {
                JOptionPane.showMessageDialog(this, "Nilai berhasil disimpan!");
            } else {
                JOptionPane.showMessageDialog(this,
                        "GAGAL! Dosen tidak mengajar MK ini atau data KRS tidak ditemukan.",
                        "Tidak Diizinkan",
                        JOptionPane.WARNING_MESSAGE);
            }

            pstmt.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "SQL Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        JOptionPane.showMessageDialog(null, "Jalankan dari program utama dengan Connection MySQL");
    }
}
