package GUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.sql.*;

public class MenuAbsensiDosen extends JFrame {

    private final Connection conn;
    private final String nidn;

    private JComboBox<String> cbMK;
    private JComboBox<String> cbPertemuan;
    private JTable tableAbsensi;
    private DefaultTableModel model;

    private static final String[] STATUS_OPTIONS = {"Hadir", "Izin", "Sakit", "Alpha"};

    public MenuAbsensiDosen(Connection conn, String nidn) {
        this.conn = conn;
        this.nidn = nidn;

        setTitle("Kelola Absensi Mahasiswa");
        setSize(950, 650);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // TITLE
        JLabel title = new JLabel("Absensi Mahasiswa", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 30));
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(title, BorderLayout.NORTH);

        // TOP PANEL
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        cbMK = new JComboBox<>();
        cbMK.setPreferredSize(new Dimension(380, 36));
        loadMK();

        // Tambah pilihan pertemuan 1–16
        cbPertemuan = new JComboBox<>();
        for (int i = 1; i <= 16; i++) cbPertemuan.addItem("Pertemuan " + i);
        cbPertemuan.setPreferredSize(new Dimension(150, 36));

        JButton btnLoad = new JButton("Tampilkan");
        btnLoad.setPreferredSize(new Dimension(140, 36));
        btnLoad.addActionListener(e -> loadMahasiswa());

        top.add(new JLabel("Mata Kuliah:"));
        top.add(cbMK);
        top.add(Box.createHorizontalStrut(20));
        top.add(new JLabel("Pertemuan:"));
        top.add(cbPertemuan);
        top.add(btnLoad);

        add(top, BorderLayout.PAGE_START);

        // TABLE
        model = new DefaultTableModel(new String[]{"NIM", "Nama", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return col == 2; // hanya status yg bisa diubah
            }
        };

        tableAbsensi = new JTable(model);
        tableAbsensi.setRowHeight(28);
        tableAbsensi.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tableAbsensi.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));

        // combobox editor untuk kolom status
        TableColumn statusCol = tableAbsensi.getColumnModel().getColumn(2);
        statusCol.setCellEditor(new DefaultCellEditor(new JComboBox<>(STATUS_OPTIONS)));

        add(new JScrollPane(tableAbsensi), BorderLayout.CENTER);

        // BOTTOM SAVE BUTTON
        JButton btnSave = new JButton("Simpan Absensi");
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnSave.setBackground(new Color(255, 140, 0));
        btnSave.setForeground(Color.WHITE);
        btnSave.setPreferredSize(new Dimension(220, 46));
        btnSave.addActionListener(e -> simpanAbsensi());

        JPanel bottom = new JPanel();
        bottom.setBorder(BorderFactory.createEmptyBorder(10, 10, 20, 10));
        bottom.add(btnSave);

        add(bottom, BorderLayout.SOUTH);

        setVisible(true);
    }

    // Load MK yang diajar dosen
    private void loadMK() {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT kode_mk, nama_mk FROM matakuliah WHERE nidn_dosen = ? ORDER BY kode_mk"
        )) {
            ps.setString(1, nidn);
            ResultSet rs = ps.executeQuery();
            cbMK.removeAllItems();
            while (rs.next()) {
                cbMK.addItem("(" + rs.getString("kode_mk") + ") " + rs.getString("nama_mk"));
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error load MK: " + ex.getMessage());
        }
    }

    // Load mahasiswa + status berdasarkan MK & Pertemuan
    private void loadMahasiswa() {
        model.setRowCount(0);

        if (cbMK.getSelectedItem() == null) return;

        String kodeMk = cbMK.getSelectedItem().toString();
        kodeMk = kodeMk.substring(1, kodeMk.indexOf(")"));

        int pertemuan = cbPertemuan.getSelectedIndex() + 1;

        String sql = """
                SELECT m.nim, m.nama,
                COALESCE(a.status, 'Hadir') AS status
                FROM krs k
                JOIN mahasiswa m ON k.nim = m.nim
                LEFT JOIN absensi a 
                      ON a.nim = m.nim 
                      AND a.kode_mk = k.kode_mk
                      AND a.pertemuan_ke = ?
                WHERE k.kode_mk = ?
                ORDER BY m.nama
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, pertemuan);
            ps.setString(2, kodeMk);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getString("nim"),
                        rs.getString("nama"),
                        rs.getString("status")
                });
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error load mahasiswa: " + ex.getMessage());
        }
    }

    // Simpan absensi per pertemuan
    private void simpanAbsensi() {

        String kodeMk = cbMK.getSelectedItem().toString();
        kodeMk = kodeMk.substring(1, kodeMk.indexOf(")"));
        int pertemuan = cbPertemuan.getSelectedIndex() + 1;

        String sql = """
                INSERT INTO absensi (nim, kode_mk, pertemuan_ke, status, tanggal)
                VALUES (?, ?, ?, ?, CURDATE())
                ON DUPLICATE KEY UPDATE status = VALUES(status)
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            for (int i = 0; i < model.getRowCount(); i++) {
                ps.setString(1, model.getValueAt(i, 0).toString()); // nim
                ps.setString(2, kodeMk);
                ps.setInt(3, pertemuan);
                ps.setString(4, model.getValueAt(i, 2).toString()); // status
                ps.addBatch();
            }

            ps.executeBatch();
            JOptionPane.showMessageDialog(this, "Absensi berhasil disimpan!");

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error simpan absensi: " + ex.getMessage());
        }
    }
}
