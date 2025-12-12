package GUI;
import java.sql.Connection;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginForm extends JFrame {

    private JTextField tfUsername;
    private JPasswordField pfPassword;
    private JButton btnLogin;

    public LoginForm() {

        setTitle("Dashboard Login - SBAU");
        // Pastikan file "logo_upn.png" ada di folder project
        ImageIcon icon = new ImageIcon("logo_upn.png");
        this.setIconImage(icon.getImage());
        setSize(900, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // ===== PANEL KIRI DENGAN GRADIENT =====
        JPanel leftPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(0, 102, 204),
                        0, getHeight(), new Color(102, 178, 255)
                );
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        leftPanel.setPreferredSize(new Dimension(350, 500));
        leftPanel.setLayout(new GridBagLayout());

        // ===== LOGO UPN =====
        ImageIcon iconLogo = new ImageIcon("src/images/logo-upn.png");
        // Resize biar proporsional
        Image img = iconLogo.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
        JLabel logo = new JLabel(new ImageIcon(img));

        // ===== TITLE & SUBTITLE =====
        JLabel lblTitle = new JLabel("<html><div style='text-align:center;'>System Base<br>Academic University</div></html>");
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));

        JLabel lblWelcome = new JLabel("Silakan Login untuk melanjutkan");
        lblWelcome.setForeground(Color.WHITE);
        lblWelcome.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        GridBagConstraints gbcLeft = new GridBagConstraints();
        gbcLeft.gridx = 0;
        gbcLeft.insets = new Insets(10, 10, 10, 10);
        gbcLeft.anchor = GridBagConstraints.CENTER;

        gbcLeft.gridy = 0;
        leftPanel.add(logo, gbcLeft);

        gbcLeft.gridy = 1;
        leftPanel.add(lblTitle, gbcLeft);

        gbcLeft.gridy = 2;
        leftPanel.add(lblWelcome, gbcLeft);

        add(leftPanel, BorderLayout.WEST);

        // ===== PANEL KANAN (FORM LOGIN) =====
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 10, 12, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblUsername = new JLabel("Username (NIM/NIDN/Admin)");
        lblUsername.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        tfUsername = new JTextField();
        tfUsername.setPreferredSize(new Dimension(250, 35));

        JLabel lblPassword = new JLabel("Password");
        lblPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        pfPassword = new JPasswordField();
        pfPassword.setPreferredSize(new Dimension(250, 35));

        btnLogin = new JButton("LOGIN");
        btnLogin.setBackground(new Color(41, 128, 185));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnLogin.setPreferredSize(new Dimension(250, 40));
        btnLogin.setFocusPainted(false);

        gbc.gridx = 0;
        gbc.gridy = 0;
        rightPanel.add(lblUsername, gbc);
        gbc.gridy++;
        rightPanel.add(tfUsername, gbc);
        gbc.gridy++;
        rightPanel.add(lblPassword, gbc);
        gbc.gridy++;
        rightPanel.add(pfPassword, gbc);
        gbc.gridy++;
        gbc.insets = new Insets(25, 10, 10, 10);
        rightPanel.add(btnLogin, gbc);

        add(rightPanel, BorderLayout.CENTER);

        // ===== ACTION LOGIN =====
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = tfUsername.getText();
                String password = String.valueOf(pfPassword.getPassword());

                try {
                    Connection conn = AplikasiLoginSBAU.loginKeDatabase(username, password);
                    String namaAsli = AplikasiLoginSBAU.cariNamaLengkap(conn, username);

                    JOptionPane.showMessageDialog(null,
                            "Login Berhasil!\nSelamat datang, " + namaAsli,
                            "Sukses",
                            JOptionPane.INFORMATION_MESSAGE);

                    dispose();

                    if (username.startsWith("admin")) {
                        new MenuAdmin(namaAsli).setVisible(true);
                    } else if (username.length() == 10) {
                        new MenuMahasiswa(namaAsli, username, conn).setVisible(true);
                    } else {
                        new MenuDosen(namaAsli, conn, username).setVisible(true);
                    }

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null,
                            "Login gagal!\n" + ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    public static void main(String[] args) {
        SplashScreen splash = new SplashScreen();
        splash.setVisible(true);

        // Thread agar splash tampil beberapa detik
        new Thread(() -> {
            try {
                splash.setProgress("Menghubungkan ke server...");
                Thread.sleep(2000);

                splash.setProgress("Memuat database...");
                Thread.sleep(2000);

                splash.setProgress("Menyiapkan aplikasi...");
                Thread.sleep(1500);

            } catch (Exception e) {
                e.printStackTrace();
            }

            splash.dispose();  // Tutup splash
            new LoginForm().setVisible(true); // Buka Login
        }).start();
    }

}
