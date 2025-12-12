package GUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MenuLihatDataMahasiswa extends JFrame {

    private Connection conn;
    private JTable table;
    private DefaultTableModel model;
    private String nidn;

    public MenuLihatDataMahasiswa(Connection conn, String nidn) {
        this.conn = conn;
        this.nidn = nidn;

        setTitle("Lihat Data Mahasiswa");
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // ===========================
        // LEFT PANEL (GRADIENT)
        // ===========================
        JPanel leftPanel = new GradientPanel();
        leftPanel.setPreferredSize(new Dimension(320, 0));
        leftPanel.setLayout(new BorderLayout());

        // TITLE
        JPanel leftTop = new JPanel();
        leftTop.setOpaque(false);
        leftTop.setLayout(new BoxLayout(leftTop, BoxLayout.Y_AXIS));
        leftTop.setBorder(BorderFactory.createEmptyBorder(40, 30, 0, 20));

        JLabel lblTitle = new JLabel("<html><span style='font-size:32pt; font-weight:600;'>Data<br/>Mahasiswa</span></html>");
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblSub = new JLabel("<html><span style='font-size:13pt;'>Mata kuliah yang Anda ampu</span></html>");
        lblSub.setForeground(new Color(240, 240, 255));
        lblSub.setBorder(BorderFactory.createEmptyBorder(16, 0, 0, 0));
        lblSub.setAlignmentX(Component.LEFT_ALIGNMENT);

        leftTop.add(lblTitle);
        leftTop.add(lblSub);
        leftPanel.add(leftTop, BorderLayout.NORTH);

        add(leftPanel, BorderLayout.WEST);

        // ===========================
        // RIGHT PANEL
        // ===========================
        JPanel rightPanel = new JPanel(new BorderLayout(20, 20));
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        // HEADER TEXT
        JLabel lblHeader = new JLabel("DAFTAR MAHASISWA");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblHeader.setHorizontalAlignment(SwingConstants.LEFT);

        rightPanel.add(lblHeader, BorderLayout.NORTH);

        // ===========================
        // TABLE
        // ===========================
        String[] kolom = {"NIM", "Nama Mahasiswa", "Jurusan", "Mata Kuliah"};

        model = new DefaultTableModel(kolom, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // agar tidak bisa diketik
            }
        };

        table = new JTable(model);
        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(250, 250, 250));

        JScrollPane scrollPane = new JScrollPane(table);
        rightPanel.add(scrollPane, BorderLayout.CENTER);

        // BUTTON REFRESH
        JButton btnRefresh = createStyledButton("Refresh");
        btnRefresh.addActionListener(e -> loadData());

        // --- BUTTON KEMBALI (BARU) ---
        JButton btnKembali = new JButton("Kembali");
        btnKembali.setPreferredSize(new Dimension(200, 45)); // Ukuran sama dengan Refresh
        btnKembali.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnKembali.setBackground(new Color(220, 53, 69)); // Warna Merah
        btnKembali.setForeground(Color.WHITE);
        btnKembali.setFocusPainted(false);
        btnKembali.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnKembali.addActionListener(e -> dispose()); // Tutup menu ini

        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(Color.WHITE);
        btnPanel.add(btnRefresh);               // Tombol Refresh (LAMA)
        btnPanel.add(Box.createHorizontalStrut(15)); // Jarak antar tombol
        btnPanel.add(btnKembali);               // Tombol Kembali (BARU)

        rightPanel.add(btnPanel, BorderLayout.SOUTH);

        add(rightPanel, BorderLayout.CENTER);

        loadData();
        setVisible(true);
    }

    // ===========================
    // LOAD DATA
    // ===========================
    private void loadData() {
        model.setRowCount(0);

        String sql = """
                SELECT m.nim, m.nama, m.jurusan, mk.nama_mk AS mata_kuliah
                FROM mahasiswa m
                JOIN krs k ON m.nim = k.nim
                JOIN matakuliah mk ON k.kode_mk = mk.kode_mk
                WHERE mk.nidn_dosen = ?
                ORDER BY m.nim
                """;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nidn);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getString("nim"),
                        rs.getString("nama"),
                        rs.getString("jurusan"),
                        rs.getString("mata_kuliah")
                });
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Gagal mengambil data: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // ===========================
    // Styled Button
    // ===========================
    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(200, 45));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setFocusPainted(false);
        btn.setBackground(Color.WHITE);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(70, 70, 70), 2, true),
                BorderFactory.createEmptyBorder(6, 14, 6, 14)
        ));

        return btn;
    }

    // ===========================
    // Gradient Sidebar Panel
    // ===========================
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
