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
        setTitle("");
        setSize(400, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Panel utama
        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(new Color(245, 248, 255));
        mainPanel.setLayout(new BorderLayout());
        add(mainPanel);

        // Header
        JPanel headerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(0, 102, 204), // biru tua
                        0, getHeight(), new Color(102, 178, 255) // biru muda
                );
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        headerPanel.setPreferredSize(new Dimension(400, 100));

        JLabel lblTitle = new JLabel("System Base Academic University");
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));

        JLabel lblSub = new JLabel("Silakan login untuk melanjutkan");
        lblSub.setForeground(Color.DARK_GRAY);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        headerPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbcHeader = new GridBagConstraints();
        gbcHeader.gridx = 0;
        gbcHeader.gridy = 0;
        headerPanel.add(lblTitle, gbcHeader);
        gbcHeader.gridy = 1;
        headerPanel.add(lblSub, gbcHeader);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Form panel
        JPanel formPanel = new JPanel();
        formPanel.setBackground(new Color(245, 248, 255));
        formPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblUsername = new JLabel("Username (NIM/NIDN/Admin)");
        lblUsername.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        tfUsername = new JTextField();
        tfUsername.setPreferredSize(new Dimension(200, 35));

        JLabel lblPassword = new JLabel("Password");
        lblPassword.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        pfPassword = new JPasswordField();
        pfPassword.setPreferredSize(new Dimension(200, 35));

        btnLogin = new JButton("LOGIN");
        btnLogin.setBackground(new Color(41, 128, 185));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogin.setPreferredSize(new Dimension(200, 40));

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(lblUsername, gbc);

        gbc.gridy++;
        formPanel.add(tfUsername, gbc);

        gbc.gridy++;
        formPanel.add(lblPassword, gbc);

        gbc.gridy++;
        formPanel.add(pfPassword, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(20, 10, 10, 10);
        formPanel.add(btnLogin, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Event Login
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

                    // Tutup GUI Login
                    dispose();

                    // Pilih role
                    if (username.startsWith("admin")) {
                        new MenuAdmin(namaAsli).setVisible(true);
                    } else if (username.length() == 10) {
                        new MenuMahasiswa(namaAsli, username).setVisible(true);
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
        SwingUtilities.invokeLater(() -> {
            new LoginForm().setVisible(true);
        });
    }
}
