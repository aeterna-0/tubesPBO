package GUI;
import javax.swing.*;
import java.awt.*;

public class MenuAdmin extends JFrame {

    public MenuAdmin(String namaAdmin) {
        super("Dashboard Admin");

        setPreferredSize(new Dimension(1000, 650));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // ========== LEFT GRADIENT PANEL ==========
        JPanel leftPanel = new GradientPanel();
        leftPanel.setPreferredSize(new Dimension(340, 0));
        leftPanel.setLayout(new BorderLayout());

        // TOP: Judul + Welcome
        JPanel leftTop = new JPanel();
        leftTop.setOpaque(false);
        leftTop.setLayout(new BoxLayout(leftTop, BoxLayout.Y_AXIS));
        leftTop.setBorder(BorderFactory.createEmptyBorder(40, 30, 0, 20));

        JLabel lblTitle = new JLabel("<html><span style='font-size:36pt; font-weight:600;'>Dashboard<br/>Admin</span></html>");
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblWelcome = new JLabel(
                "<html><span style='font-size:14pt;'>Selamat datang,<br/><strong>"
                        + escapeHtml(namaAdmin)
                        + "</strong></span></html>"
        );
        lblWelcome.setForeground(new Color(230, 245, 255));
        lblWelcome.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        lblWelcome.setAlignmentX(Component.LEFT_ALIGNMENT);

        leftTop.add(lblTitle);
        leftTop.add(lblWelcome);
        leftPanel.add(leftTop, BorderLayout.NORTH);

        // BOTTOM: Tombol Logout
        JButton btnLogout = new JButton("Logout");
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 20));
        btnLogout.setBackground(Color.WHITE);
        btnLogout.setFocusPainted(false);
        btnLogout.setPreferredSize(new Dimension(260, 55));
        btnLogout.setMaximumSize(new Dimension(260, 55));
        btnLogout.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel leftBottomWrapper = new JPanel();
        leftBottomWrapper.setOpaque(false);
        leftBottomWrapper.setLayout(new BoxLayout(leftBottomWrapper, BoxLayout.Y_AXIS));
        leftBottomWrapper.setBorder(BorderFactory.createEmptyBorder(0, 30, 40, 30));

        leftBottomWrapper.add(Box.createVerticalGlue());
        leftBottomWrapper.add(btnLogout);

        leftPanel.add(leftBottomWrapper, BorderLayout.CENTER);
        add(leftPanel, BorderLayout.WEST);

        // ========== RIGHT PANEL (tombol admin) ==========
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.CENTER;

        JButton btnUser = createStyledButton("Manajemen User");
        JButton btnMK = createStyledButton("Setup Mata Kuliah");
        JButton btnReport = createStyledButton("Laporan Sistem");

        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 25, 0);
        rightPanel.add(btnUser, gbc);

        gbc.gridy = 1;
        rightPanel.add(btnMK, gbc);

        gbc.gridy = 2;
        rightPanel.add(btnReport, gbc);

        add(rightPanel, BorderLayout.CENTER);

        // ACTIONS
        btnLogout.addActionListener(e -> dispose());

        btnUser.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "(Placeholder) Manajemen User");
        });
        btnMK.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "(Placeholder) Setup Mata Kuliah");
        });
        btnReport.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "(Placeholder) Laporan Sistem");
        });

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // ========== Button Styling (Sama seperti MenuMahasiswa) ==========
    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);

        btn.setPreferredSize(new Dimension(330, 60));
        btn.setMaximumSize(new Dimension(330, 60));

        btn.setFont(new Font("Segoe UI", Font.BOLD, 20));
        btn.setFocusPainted(false);
        btn.setBackground(Color.WHITE);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(60, 60, 60), 2, true),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));

        return btn;
    }

    // Gradient panel reuse
    static class GradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            int h = getHeight();

            Color top = new Color(0, 122, 255);
            Color bottom = new Color(153, 204, 255);

            g2.setPaint(new GradientPaint(0, 0, top, 0, h, bottom));
            g2.fillRect(0, 0, getWidth(), h);
            g2.dispose();
        }
    }

    private String escapeHtml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    // Testing
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MenuAdmin("Administrator Sistem"));
    }
}
