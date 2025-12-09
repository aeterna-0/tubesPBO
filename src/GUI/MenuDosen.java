package GUI;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;

public class MenuDosen extends JFrame {

    private Connection conn;
    private String nidn;

    public MenuDosen(String username, Connection conn, String nidn) {
        super("Dashboard Dosen");
        this.conn = conn;
        this.nidn = nidn;

        setPreferredSize(new Dimension(1000, 650));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // ===== LEFT PANEL (GRADIENT) =====
        JPanel leftPanel = new GradientPanel();
        leftPanel.setPreferredSize(new Dimension(340, 0)); // tetap lebar kiri
        leftPanel.setLayout(new BorderLayout());

        // judul + welcome di panel atas kiri
        JPanel leftTop = new JPanel();
        leftTop.setOpaque(false);
        leftTop.setLayout(new BoxLayout(leftTop, BoxLayout.Y_AXIS));
        leftTop.setBorder(BorderFactory.createEmptyBorder(40, 30, 0, 20));

        JLabel lblTitle = new JLabel("<html><span style='font-size:36pt; font-weight:600;'>Dashboard<br/>Dosen</span></html>");
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblWelcome = new JLabel("<html><span style='font-size:14pt;'>Selamat datang,<br/><strong>(" + escapeHtml(username) + ")</strong></span></html>");
        lblWelcome.setForeground(new Color(230, 245, 255));
        lblWelcome.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        lblWelcome.setAlignmentX(Component.LEFT_ALIGNMENT);

        leftTop.add(lblTitle);
        leftTop.add(lblWelcome);

        leftPanel.add(leftTop, BorderLayout.NORTH);

        // logout di bagian bawah kiri
        JButton btnLogout = new JButton("Logout");
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnLogout.setFocusPainted(false);
        btnLogout.setBackground(Color.WHITE);
        btnLogout.setPreferredSize(new Dimension(260, 55));
        btnLogout.setMaximumSize(new Dimension(260, 55));
        btnLogout.setAlignmentX(Component.CENTER_ALIGNMENT);

        // panel pembungkus untuk footer agar logout selalu di bawah dengan padding
        JPanel leftBottomWrapper = new JPanel();
        leftBottomWrapper.setOpaque(false);
        BoxLayout bl = new BoxLayout(leftBottomWrapper, BoxLayout.Y_AXIS);
        leftBottomWrapper.setLayout(bl);
        leftBottomWrapper.setBorder(BorderFactory.createEmptyBorder(0, 30, 40, 30));

        leftBottomWrapper.add(Box.createVerticalGlue());
        leftBottomWrapper.add(btnLogout);

        leftPanel.add(leftBottomWrapper, BorderLayout.CENTER);

        add(leftPanel, BorderLayout.WEST);


        // ===== RIGHT PANEL =====
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();

        JButton btnInputNilai = createStyledButton("Input Nilai Mahasiswa");
        JButton btnLihatData = createStyledButton("Lihat Data Mahasiswa");
        JButton btnTambahTugas = createStyledButton("Tambah Tugas");
        JButton btnAbsensi = createStyledButton("Kelola Absensi");

        // letakkan dua tombol dengan jarak yang cukup, center
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 30, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        rightPanel.add(btnInputNilai, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 0, 0);
        rightPanel.add(btnLihatData, gbc);

        gbc.gridy = 2;
        gbc.insets = new Insets(30, 0, 0, 0);
        rightPanel.add(btnTambahTugas, gbc);

        gbc.gridy = 3; // <<< posisinya di bawah Tambah Tugas
        gbc.insets = new Insets(30, 0, 0, 0);
        rightPanel.add(btnAbsensi, gbc);

        add(rightPanel, BorderLayout.CENTER);

        // ===== ACTIONS =====
        btnLogout.addActionListener(e -> {
            dispose();
        });

        btnInputNilai.addActionListener(e -> {
            new MenuInputNilai(conn, nidn);
        });

        btnLihatData.addActionListener(e -> {
            new MenuLihatDataMahasiswa(conn, nidn);
        });

        btnTambahTugas.addActionListener(e -> {
            new MenuTambahTugas(conn, nidn);
        });

        btnAbsensi.addActionListener(e -> {
            new MenuAbsensiDosen(conn, nidn);
        });

        // finalize
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // helper: buat tombol bergaya sama persis (ukuran, border, font)
    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);

        // ukuran tombol lebih kecil
        btn.setPreferredSize(new Dimension(330, 60));
        btn.setMaximumSize(new Dimension(330, 60));

        // font lebih kecil biar proporsional
        btn.setFont(new Font("Segoe UI", Font.BOLD, 20));

        btn.setFocusPainted(false);
        btn.setBackground(Color.WHITE);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // border + padding kecil
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(60, 60, 60), 2, true),
                BorderFactory.createEmptyBorder(8, 16, 8, 16)
        ));

        btn.setOpaque(true);
        return btn;
    }


    // panel gradient sederhana
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

    // very small helper to avoid raw HTML injection if username contains markup
    private String escapeHtml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    public static void main(String[] args) {
    }
}
