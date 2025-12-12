package GUI;
import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class SplashScreen extends JWindow {

    private JLabel lblStatus;
    private LoadingSpinner spinner;

    public SplashScreen() {
        setSize(400, 350);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(new Color(0, 102, 204));
        mainPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 0;

        // --- LOGO UPN ---
        gbc.gridy = 0;
        URL imgUrl = getClass().getResource("/logo_upn.png");
        if (imgUrl != null) {
            ImageIcon originalIcon = new ImageIcon(imgUrl);
            Image img = originalIcon.getImage();
            Image resizedImg = img.getScaledInstance(120, 120, Image.SCALE_SMOOTH);
            JLabel lblLogo = new JLabel(new ImageIcon(resizedImg));
            mainPanel.add(lblLogo, gbc);
        } else {
            JLabel lblNoImg = new JLabel("[LOGO UPN]");
            lblNoImg.setForeground(Color.WHITE);
            lblNoImg.setFont(new Font("Segoe UI", Font.BOLD, 22));
            mainPanel.add(lblNoImg, gbc);
        }

        // --- TITLE ---
        gbc.gridy = 1;
        JLabel lblTitle = new JLabel("System Base Academic University");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(Color.WHITE);
        mainPanel.add(lblTitle, gbc);

        gbc.gridy = 2;
        JLabel lblWelcome = new JLabel("Universitas Pembangunan Nasional 'Veteran' Jakarta");
        lblWelcome.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblWelcome.setForeground(Color.WHITE);
        mainPanel.add(lblWelcome, gbc);

        // --- SPINNER ---
        gbc.gridy = 3;
        spinner = new LoadingSpinner();
        spinner.setPreferredSize(new Dimension(40, 40));
        mainPanel.add(spinner, gbc);

        // --- STATUS TEXT ---
        gbc.gridy = 4;
        lblStatus = new JLabel("Memulai...", SwingConstants.CENTER);
        lblStatus.setForeground(Color.WHITE);
        lblStatus.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        mainPanel.add(lblStatus, gbc);

        add(mainPanel, BorderLayout.CENTER);
    }

    public void setProgress(String msg) {
        SwingUtilities.invokeLater(() -> lblStatus.setText(msg));
    }

    public void showSplash() {
        setVisible(true);

        new Thread(() -> {
            try {
                setProgress("Memuat modul...");
                Thread.sleep(800);

                setProgress("Menghubungkan database...");
                Thread.sleep(1000);

                setProgress("Menyiapkan tampilan...");
                Thread.sleep(1200);

            } catch (Exception e) {
                e.printStackTrace();
            }

            dispose(); // Tutup splash
            new LoginForm().setVisible(true); // Buka Login
        }).start();
    }

    // ----- SPINNER ANIMATION -----
    private class LoadingSpinner extends JPanel {
        private int angle = 0;
        private Timer timer;

        public LoadingSpinner() {
            setOpaque(false);
            timer = new Timer(40, e -> {
                angle += 15;
                if (angle >= 360) angle = 0;
                repaint();
            });
            timer.start();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight();
            g2.setStroke(new BasicStroke(4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            g2.setColor(new Color(255, 255, 255, 80));
            g2.drawOval(5, 5, w - 10, h - 10);

            g2.setColor(Color.WHITE);
            g2.drawArc(5, 5, w - 10, h - 10, -angle, 100);
        }
    }

    public static void main(String[] args) {
        new SplashScreen().showSplash();
    }
}
