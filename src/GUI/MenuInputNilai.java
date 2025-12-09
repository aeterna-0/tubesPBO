package GUI;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class MenuInputNilai extends JFrame {

    private Connection conn;
    private String nidn;

    private JComboBox<String> cbKodeMK;
    private JTable tableMahasiswa;
    private DefaultTableModel tableModel;
    private JButton btnInputNilai;

    public MenuInputNilai(Connection conn, String nidn) {
        super("Input Nilai Mahasiswa");
        this.conn = conn;
        this.nidn = nidn;

        setSize(900, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // ========================= LEFT PANEL (GRADIENT) =========================
        JPanel leftPanel = new GradientPanel();
        leftPanel.setPreferredSize(new Dimension(300, 0));
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));  // LEBIH RAPI

        JLabel lblTitle = new JLabel("<html><div style='font-size:30pt; font-weight:600;color:white;'>Input<br>Nilai</div></html>");
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSub = new JLabel("<html><div style='font-size:13pt; color:white;'>Pilih MK dan input nilai mahasiswa</div></html>");
        lblSub.setBorder(BorderFactory.createEmptyBorder(10, 0, 25, 0));
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);

        leftPanel.add(lblTitle);
        leftPanel.add(lblSub);

        JLabel lblMK = new JLabel("Pilih Kode MK:");
        lblMK.setForeground(Color.WHITE);
        lblMK.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblMK.setAlignmentX(Component.CENTER_ALIGNMENT);

        leftPanel.add(lblMK);
        leftPanel.add(Box.createVerticalStrut(8));

        cbKodeMK = new JComboBox<>();
        cbKodeMK.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cbKodeMK.setPreferredSize(new Dimension(220, 30));
        cbKodeMK.setMaximumSize(new Dimension(220, 30));
        loadKodeMK();

        JPanel cbWrapper = new JPanel();
        cbWrapper.setOpaque(false);
        cbWrapper.setLayout(new BoxLayout(cbWrapper, BoxLayout.X_AXIS));
        cbWrapper.add(Box.createHorizontalGlue());
        cbWrapper.add(cbKodeMK);
        cbWrapper.add(Box.createHorizontalGlue());

        leftPanel.add(cbWrapper);
        leftPanel.add(Box.createVerticalStrut(15));

        JButton btnLoad = styledSmallButton("Tampilkan Mahasiswa");
        btnLoad.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel btnWrapper = new JPanel();
        btnWrapper.setOpaque(false);
        btnWrapper.setLayout(new BoxLayout(btnWrapper, BoxLayout.X_AXIS));
        btnWrapper.add(Box.createHorizontalGlue());
        btnWrapper.add(btnLoad);
        btnWrapper.add(Box.createHorizontalGlue());

        leftPanel.add(btnWrapper);
        leftPanel.add(Box.createVerticalGlue());

        add(leftPanel, BorderLayout.WEST);


        // ========================= TABLE PANEL =========================
        tableModel = new DefaultTableModel(new Object[]{"NIM", "Nama", "Nilai"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableMahasiswa = new JTable(tableModel);
        tableMahasiswa.setRowHeight(28);
        tableMahasiswa.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tableMahasiswa.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));

        // center column "nilai"
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(JLabel.CENTER);
        tableMahasiswa.getColumnModel().getColumn(2).setCellRenderer(center);

        JScrollPane scroll = new JScrollPane(tableMahasiswa);
        scroll.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        add(scroll, BorderLayout.CENTER);

        // ========================= INPUT BUTTON =========================
        btnInputNilai = new JButton("Input Nilai");
        btnInputNilai.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnInputNilai.setBackground(new Color(255, 140, 0));
        btnInputNilai.setForeground(Color.WHITE);
        btnInputNilai.setFocusPainted(false);
        btnInputNilai.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnInputNilai.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.add(btnInputNilai);

        add(bottomPanel, BorderLayout.SOUTH);

        // ========================= EVENT =========================
        btnLoad.addActionListener(e -> loadMahasiswa());
        btnInputNilai.addActionListener(e -> inputNilaiPopup());

        setVisible(true);
    }

    // ========================= LOAD MK =========================
    private void loadKodeMK() {
        try {
            String sql = "SELECT kode_mk, nama_mk FROM matakuliah WHERE nidn_dosen = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, nidn);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String kode = rs.getString("kode_mk");
                String nama = rs.getString("nama_mk");

                cbKodeMK.addItem("(" + kode + ") " + nama);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error Load MK: " + e.getMessage());
        }
    }

    // ========================= LOAD MAHASISWA =========================
    private void loadMahasiswa() {
        tableModel.setRowCount(0);

        String selected = (String) cbKodeMK.getSelectedItem();
        if (selected == null) return;

        String kodeMk = selected.substring(1, selected.indexOf(")")); // ekstrak kode

        try {
            String sql = """
            SELECT k.nim, m.nama, k.nilai
            FROM krs k 
            JOIN mahasiswa m ON k.nim = m.nim
            WHERE k.kode_mk = ?
        """;

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, kodeMk);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getString("nim"),
                        rs.getString("nama"),
                        rs.getString("nilai") == null ? "-" : rs.getString("nilai")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error Load Mahasiswa: " + e.getMessage());
        }
    }


    // ========================= POPUP INPUT =========================
    private void inputNilaiPopup() {

        int row = tableMahasiswa.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this,
                    "Pilih mahasiswa terlebih dahulu!",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String nim = tableModel.getValueAt(row, 0).toString();

        String selected = cbKodeMK.getSelectedItem().toString();
        String kodeMk = selected.substring(1, selected.indexOf(")"));

        JComboBox<String> cbNilai = new JComboBox<>(new String[]{"A", "A-", "B+", "B", "B-", "C", "D", "E"});
        cbNilai.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cbNilai.setPreferredSize(new Dimension(80, 30));

        JPanel panel = new JPanel();
        panel.add(new JLabel("Nilai untuk NIM " + nim + ": "));
        panel.add(cbNilai);

        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Input Nilai",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            simpanNilai(nim, kodeMk, cbNilai.getSelectedItem().toString());
            loadMahasiswa();
        }
    }

    // ========================= SIMPAN NILAI =========================
    private void simpanNilai(String nim, String kodeMk, String nilai) {

        try {
            String sql = """
                    UPDATE krs 
                    SET nilai = ?
                    WHERE nim = ? AND kode_mk = ?
                    """;

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, nilai);
            pstmt.setString(2, nim);
            pstmt.setString(3, kodeMk);

            int updated = pstmt.executeUpdate();

            if (updated > 0) {
                JOptionPane.showMessageDialog(this, "Nilai berhasil disimpan!");
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menyimpan nilai.");
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "SQL Error: " + e.getMessage());
        }
    }

    // ========================= STYLED BUTTON =========================
    private JButton styledSmallButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBackground(Color.WHITE);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(80, 80, 80), 2, true),
                BorderFactory.createEmptyBorder(8, 14, 8, 14)
        ));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        return btn;
    }

    // ========================= GRADIENT PANEL =========================
    static class GradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            int w = getWidth();
            int h = getHeight();

            Color top = new Color(255, 140, 0);
            Color bottom = new Color(255, 199, 153);

            GradientPaint gp = new GradientPaint(0, 0, top, 0, h, bottom);
            g2.setPaint(gp);
            g2.fillRect(0, 0, w, h);
            g2.dispose();
        }
    }
}
