package GUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.text.NumberFormat;
import java.util.Locale;

public class MenuMahasiswa extends JFrame {

    private String namaMahasiswa;
    private String nim;
    private Connection conn;
    private JPanel statsPanel;

    private JLabel lblValMK, lblValSKS, lblValIPK, lblValUKT; // Label untuk angka/nilai
    private JPanel cardUKT; // Panel UKT (untuk ubah warna background)

    // Variabel untuk menyimpan status terkini (untuk logic klik UKT)
    private boolean currentStatusLunas = false;
    private double currentNominalUkt = 0;

    public MenuMahasiswa(String nama, String nim, Connection conn) {
        this.namaMahasiswa = nama;
        this.nim = nim;
        this.conn = conn;

        setTitle("Dashboard Mahasiswa - SBAU");
        // Pastikan file "logo_upn.png" ada di folder project
        ImageIcon icon = new ImageIcon("logo_upn.png");
        this.setIconImage(icon.getImage());
        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // ==========================================================
        // 1. SIDEBAR KIRI (Gradient Pink + Profil)
        // ==========================================================
        JPanel leftPanel = new GradientPanel();
        leftPanel.setPreferredSize(new Dimension(300, 0));
        leftPanel.setLayout(new BorderLayout());

        // --- Info Atas ---
        JPanel leftTop = new JPanel();
        leftTop.setOpaque(false);
        leftTop.setLayout(new BoxLayout(leftTop, BoxLayout.Y_AXIS));
        leftTop.setBorder(BorderFactory.createEmptyBorder(40, 30, 0, 20));

        // Logo Logic
        ImageIcon originalIcon = new ImageIcon("logo_upn.png");
        if (originalIcon.getIconWidth() > 0) {
            Image img = originalIcon.getImage();
            Image newImg = img.getScaledInstance(100, -1, java.awt.Image.SCALE_SMOOTH);
            ImageIcon iconColor = new ImageIcon(newImg);
            JLabel lblLogo = new JLabel(iconColor);
            lblLogo.setAlignmentX(Component.LEFT_ALIGNMENT);
            lblLogo.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
            leftTop.add(lblLogo);
        }

        JLabel lblTitle = new JLabel("<html><span style='font-size:28pt; font-weight:600;'>Dashboard<br/>Mahasiswa</span></html>");
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblWelcome = new JLabel(
                "<html><br/><span style='font-size:12pt;'>Selamat datang,<br/><strong>"
                        + nama + "</strong><br/>(" + nim + ")</span></html>"
        );
        lblWelcome.setForeground(new Color(255, 245, 250));
        lblWelcome.setAlignmentX(Component.LEFT_ALIGNMENT);

        leftTop.add(lblTitle);
        leftTop.add(lblWelcome);
        leftPanel.add(leftTop, BorderLayout.NORTH);

        // --- Tombol Logout ---
        JButton btnLogout = new JButton("Logout");
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnLogout.setForeground(new Color(180, 40, 90));
        btnLogout.setBackground(Color.WHITE);
        btnLogout.setFocusPainted(false);
        btnLogout.setMaximumSize(new Dimension(220, 45));
        btnLogout.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogout.addActionListener(e -> {
            try { if (conn != null) conn.close(); } catch (SQLException ex) {}
            dispose();
            new LoginForm().setVisible(true);
        });

        JPanel leftBottomWrapper = new JPanel();
        leftBottomWrapper.setOpaque(false);
        leftBottomWrapper.setLayout(new BoxLayout(leftBottomWrapper, BoxLayout.Y_AXIS));
        leftBottomWrapper.setBorder(BorderFactory.createEmptyBorder(0, 30, 40, 30));
        leftBottomWrapper.add(Box.createVerticalGlue());
        leftBottomWrapper.add(btnLogout);
        leftPanel.add(leftBottomWrapper, BorderLayout.CENTER);

        add(leftPanel, BorderLayout.WEST);

        // ==========================================================
        // 2. PANEL KANAN (Updated: Running Text + DASHBOARD STATS + Tombol)
        // ==========================================================
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BorderLayout());
        rightPanel.setBackground(Color.WHITE);

        // A. RUNNING TEXT
        RunningTextPanel runningText = new RunningTextPanel(
                "PENGUMUMAN RESMI: Pelaksanaan Evaluasi Akhir Tahun BEM UPN \"Veteran\" Jakarta "
                        + "akan diselenggarakan di Auditorium Bhineka Tunggal Ika pada tanggal 04 Desember 2025. "
                        + "Seluruh anggota dan perwakilan organisasi mahasiswa diwajibkan hadir tepat waktu. "
                        + "| PENGUMUMAN AKADEMIK: Pelaksanaan Yudisium Semester Ganjil Tahun Akademik 2025/2026 "
                        + "akan dilaksanakan pada 10 Januari 2026 di Aula Rektorat Lantai 4. "
                        + "Mahasiswa peserta Yudisium wajib melakukan registrasi kehadiran sebelum acara dimulai. "
                        + "| PENGUMUMAN KEUANGAN: Batas pembayaran UKT Semester Genap Tahun Akademik 2025/2026 "
                        + "ditetapkan sampai dengan tanggal 15 Januari 2026. "
                        + "Mahasiswa yang belum melakukan pembayaran tidak dapat melakukan proses KRS. "
                        + "| PENGUMUMAN: Pengisian KRS Semester Genap dimulai tanggal 20 Januari 2026 "
                        + "hingga 28 Januari 2026 melalui Sistem Informasi Akademik UPNVJ. "
                        + "| PENGUMUMAN RESMI: Pelaksanaan Wisuda Periode I Tahun 2026 "
                        + "akan dilaksanakan pada 17 Februari 2026 di Balai Sudirman, Jakarta. "
                        + "| PENGUMUMAN: Pendaftaran Beasiswa Prestasi Tahap I Tahun 2026 "
                        + "dibuka mulai tanggal 5 Januari 2026 sampai 25 Januari 2026 melalui laman kemahasiswaan. "
                        + "| PENGUMUMAN: Workshop Penulisan Skripsi dan Publikasi Ilmiah "
                        + "akan dilaksanakan pada 8 Januari 2026 di Ruang Rapat Fakultas Ilmu Komputer. "
                        + "| PENGUMUMAN: Pelaksanaan Ujian Akhir Semester Ganjil Tahun Akademik 2025/2026 "
                        + "akan dimulai pada tanggal 14 Desember 2025. Mahasiswa wajib mengecek jadwal ujian masing-" +
                        "masing pada portal akademik."
        );

        rightPanel.add(runningText, BorderLayout.NORTH);

        // B. CONTAINER TENGAH
        JPanel centerContainer = new JPanel();
        centerContainer.setLayout(new BoxLayout(centerContainer, BoxLayout.Y_AXIS));
        centerContainer.setBackground(Color.WHITE);
        centerContainer.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // --- 1. DASHBOARD STATS (MODIFIKASI DI SINI) ---
        statsPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        statsPanel.setBackground(Color.WHITE);
        statsPanel.setPreferredSize(new Dimension(0, 120));
        statsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));
        // 1. Inisialisasi Tampilan (Hanya Layout Kosong)
        initDashboardUI();

        // 2. Jalankan Timer untuk Update Data Realtime (Setiap 2 detik)
        Timer timerUpdate = new Timer(2000, e -> refreshData());
        timerUpdate.start();

        // Panggil sekali di awal agar langsung muncul tanpa nunggu 2 detik
        refreshData();

        centerContainer.add(statsPanel);

        centerContainer.add(Box.createVerticalStrut(40));


        // --- 2. TOMBOL MENU ---
        JPanel buttonGrid = new JPanel(new GridLayout(2, 2, 15, 15));
        buttonGrid.setBackground(Color.WHITE);
        buttonGrid.setMaximumSize(new Dimension(1000, 200));

        JButton btnProfil = createStyledButton("Profil Saya");
        JButton btnLihatMK = createStyledButton("Lihat Mata Kuliah");
        JButton btnIsiKRS = createStyledButton("Isi KRS");
        JButton btnLihatKHS = createStyledButton("Nilai Semester");
        JButton btnLihatTugas = createStyledButton("Tugas Kuliah");

        buttonGrid.add(btnProfil);
        buttonGrid.add(btnLihatMK);
        buttonGrid.add(btnIsiKRS);
        buttonGrid.add(btnLihatKHS);
        buttonGrid.add(btnLihatTugas);

        centerContainer.add(buttonGrid);

        rightPanel.add(centerContainer, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.CENTER);

        // --- EVENTS ---
        btnProfil.addActionListener(e -> tampilkanProfil());
        btnLihatMK.addActionListener(e -> tampilkanTabelMatakuliah());
        btnIsiKRS.addActionListener(e -> prosesAmbilKRS());
        btnLihatKHS.addActionListener(e -> tampilkanKHS());
        btnLihatTugas.addActionListener(e -> tampilkanTugas());
    }

    // ==========================================================
    // HELPER: DASHBOARD STATISTICS (LAPORAN KOTAK WARNA)
    // ==========================================================

    // Membuat Kerangka UI Dashboard
    private void initDashboardUI() {
        // Inisialisasi Label Global
        lblValMK = new JLabel("Loading...");
        lblValSKS = new JLabel("Loading...");
        lblValIPK = new JLabel("Loading...");
        lblValUKT = new JLabel("Loading...");

        // Buat Panel (Card) dan simpan ke statsPanel
        statsPanel.add(createStatCard("Mata Kuliah", lblValMK, new Color(0, 188, 212)));
        statsPanel.add(createStatCard("Total SKS", lblValSKS, new Color(76, 175, 80)));
        statsPanel.add(createStatCard("IPK Sem", lblValIPK, new Color(255, 152, 0)));

        // Khusus UKT kita simpan panelnya ke variabel global agar bisa ubah warna
        cardUKT = createStatCard("Tagihan UKT", lblValUKT, new Color(244, 67, 54));
        cardUKT.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Event Listener UKT (Sekarang menggunakan variabel global currentStatusLunas)
        cardUKT.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                // Panggil fungsi bayar dengan data terbaru
                prosesPembayaranUKT(currentNominalUkt, currentStatusLunas);
            }
        });

        statsPanel.add(cardUKT);
    }

    // Helper Modifikasi: Menerima JLabel object, bukan String value
    private JPanel createStatCard(String title, JLabel labelComponent, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(color);
        card.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Icon
        JLabel lblIcon = new JLabel("●");
        lblIcon.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblIcon.setForeground(new Color(255, 255, 255, 100));

        // Teks
        JPanel textPanel = new JPanel(new GridLayout(2, 1));
        textPanel.setOpaque(false);
        textPanel.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));

        JLabel lblTitle = new JLabel(title.toUpperCase());
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblTitle.setForeground(Color.WHITE);

        // Styling Label Value (Variabel Global)
        labelComponent.setFont(new Font("Segoe UI", Font.BOLD, 17));
        labelComponent.setForeground(Color.WHITE);

        textPanel.add(lblTitle);
        textPanel.add(labelComponent);

        card.add(lblIcon, BorderLayout.WEST);
        card.add(textPanel, BorderLayout.CENTER);

        return card;
    }
    private void refreshData() {
        // Variabel Data Sementara
        int totalSks = 0;
        int jumlahMK = 0;
        double ipk = 0.0;

        // Logic Hitung Nilai (Sama seperti sebelumnya)
        try {
            String sqlNilai = "SELECT k.nilai, m.sks FROM krs k JOIN matakuliah m ON k.kode_mk = m.kode_mk WHERE k.nim = ?";
            PreparedStatement pst = conn.prepareStatement(sqlNilai);
            pst.setString(1, nim);
            ResultSet rs = pst.executeQuery();

            double totalBobot = 0;
            while(rs.next()) {
                jumlahMK++;
                int sks = rs.getInt("sks");
                String n = rs.getString("nilai");
                double bobot = 0;
                if(n!=null) {
                    if(n.equals("A")) bobot=4.0; else if(n.equals("B")) bobot=3.0;
                    else if(n.equals("C")) bobot=2.0; else if(n.equals("D")) bobot=1.0;
                }
                totalBobot += (bobot * sks);
                totalSks += sks;
            }
            if(totalSks > 0) ipk = totalBobot / totalSks;

            // --- UPDATE LABEL MK, SKS, IPK ---
            lblValMK.setText(jumlahMK + " MK");
            lblValSKS.setText(totalSks + " SKS");
            lblValIPK.setText(String.format("%.2f", ipk));

            // --- CEK UKT ---
            String sqlUkt = "SELECT ukt, status_bayar FROM mahasiswa WHERE nim = ?";
            PreparedStatement pstUkt = conn.prepareStatement(sqlUkt);
            pstUkt.setString(1, nim);
            ResultSet rsUkt = pstUkt.executeQuery();

            if(rsUkt.next()) {
                double nominal = rsUkt.getDouble("ukt");

                // GANTI 'status_ukt' MENJADI 'status_bayar'
                String status = rsUkt.getString("status_bayar");

                // Update Global Variables
                currentNominalUkt = nominal;
                currentStatusLunas = (status != null && status.equalsIgnoreCase("Lunas"));
                // Update UI UKT
                if (currentStatusLunas) {
                    lblValUKT.setText("LUNAS");
                    cardUKT.setBackground(new Color(76, 175, 80)); // Hijau
                    // Tengahkan teks LUNAS
                    lblValUKT.setHorizontalAlignment(SwingConstants.LEFT);
                } else {
                    NumberFormat formatRp = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
                    lblValUKT.setText(formatRp.format(nominal).replace(",00", ""));
                    cardUKT.setBackground(new Color(244, 67, 54)); // Merah
                }
            }

            // Revalidate tidak diperlukan karena kita hanya ubah setText
            // Repaint opsional untuk memastikan warna background berubah mulus
            cardUKT.repaint();

        } catch (Exception e) {
            System.out.println("Gagal refresh data: " + e.getMessage());
        }
    }

    // ==========================================================
    // 4. FITUR LIHAT & EDIT PROFIL (UPDATE)
    // ==========================================================
    private void tampilkanProfil() {
        JDialog dialog = new JDialog(this, "Profil Mahasiswa", true);
        dialog.setSize(700, 550); // Diperlebar agar muat foto
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        // --- Header Pink ---
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(236, 64, 122));
        headerPanel.setPreferredSize(new Dimension(0, 70));
        headerPanel.setLayout(new GridBagLayout());
        JLabel lblTitle = new JLabel("BIODATA LENGKAP");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(Color.WHITE);
        headerPanel.add(lblTitle);

        // --- Container Utama (Split Kiri: Foto, Kanan: Data) ---
        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBackground(Color.WHITE);
        mainContainer.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // ================= BAGIAN FOTO (KIRI) =================
        JPanel photoPanel = new JPanel();
        photoPanel.setLayout(new BoxLayout(photoPanel, BoxLayout.Y_AXIS));
        photoPanel.setBackground(Color.WHITE);
        photoPanel.setPreferredSize(new Dimension(200, 0));
        photoPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20)); // Jarak ke data

        // 1. Label Gambar
        JLabel lblFoto = new JLabel();
        lblFoto.setPreferredSize(new Dimension(150, 200));
        lblFoto.setMaximumSize(new Dimension(150, 200));
        lblFoto.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        lblFoto.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 2. Tombol Ganti Foto
        JButton btnGantiFoto = new JButton("Ganti Foto");
        btnGantiFoto.setBackground(new Color(64, 169, 255)); // Biru
        btnGantiFoto.setForeground(Color.WHITE);
        btnGantiFoto.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnGantiFoto.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnGantiFoto.setFocusPainted(false);

        // Spacer
        photoPanel.add(Box.createVerticalStrut(10));
        photoPanel.add(lblFoto);
        photoPanel.add(Box.createVerticalStrut(10));
        photoPanel.add(btnGantiFoto);

        // ================= BAGIAN DATA (KANAN) =================
        JPanel dataPanel = new JPanel(new GridBagLayout());
        dataPanel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 5, 8, 5);

        // -- Load Data Database --
        String dNim="-", dNama="-", dJurusan="-", dKelas="-", dAngkatan="-", dEmail="-", dHp="-", dFoto=null;

        try {
            String sql = "SELECT * FROM mahasiswa WHERE nim = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, nim);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                dNim = rs.getString("nim");
                dNama = rs.getString("nama");
                dJurusan = rs.getString("jurusan");
                if(rs.getString("kelas") != null) dKelas = rs.getString("kelas");
                if(rs.getString("angkatan") != null) dAngkatan = rs.getString("angkatan");
                if(rs.getString("email") != null) dEmail = rs.getString("email");
                if(rs.getString("no_hp") != null) dHp = rs.getString("no_hp");
                if(rs.getString("foto") != null) dFoto = rs.getString("foto");
            }
        } catch (SQLException e) { e.printStackTrace(); }

        // Set Gambar Awal
        loadFotoKeLabel(lblFoto, dFoto);

        // Render Data Read-Only
        addProfileRow(dataPanel, gbc, 0, "NIM", dNim);
        addProfileRow(dataPanel, gbc, 1, "Nama Lengkap", dNama);
        addProfileRow(dataPanel, gbc, 2, "Program Studi", dJurusan);
        addProfileRow(dataPanel, gbc, 3, "Kelas", dKelas);
        addProfileRow(dataPanel, gbc, 4, "Angkatan", dAngkatan);

        // Data Editable
        JTextField txtEmail = new JTextField(dEmail);
        JTextField txtHp = new JTextField(dHp);
        addEditableRow(dataPanel, gbc, 5, "Email Kampus", txtEmail);
        addEditableRow(dataPanel, gbc, 6, "Nomor HP", txtHp);

        // ================= PANEL TOMBOL BAWAH =================
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 15));
        bottomPanel.setBackground(Color.WHITE);

        JButton btnSimpan = new JButton("Simpan Perubahan");
        btnSimpan.setBackground(new Color(40, 167, 69)); // Hijau
        btnSimpan.setForeground(Color.WHITE);
        btnSimpan.setPreferredSize(new Dimension(170, 40));

        JButton btnClose = new JButton("Tutup");
        btnClose.setBackground(new Color(220, 53, 69)); // Merah
        btnClose.setForeground(Color.WHITE);
        btnClose.setPreferredSize(new Dimension(100, 40));

        // --- LOGIKA GANTI FOTO ---
        btnGantiFoto.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Pilih Foto Profil (JPG/PNG)");
            fileChooser.setFileFilter(new FileNameExtensionFilter("Gambar (JPG, PNG)", "jpg", "png", "jpeg"));

            int result = fileChooser.showOpenDialog(dialog);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();

                try {
                    // 1. Tentukan Nama File Baru (Gunakan NIM agar unik)
                    // Ambil ekstensi file asli (misal .jpg)
                    String fileName = selectedFile.getName();
                    String extension = "";
                    int i = fileName.lastIndexOf('.');
                    if (i > 0) extension = fileName.substring(i);

                    String newFileName = nim + extension; // Contoh: 2410511116.jpg

                    // 2. Copy File ke Folder Project
                    File destFile = new File(newFileName);
                    Files.copy(selectedFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                    // 3. Update Database (Simpan Nama Filenya Saja)
                    String sqlUpdate = "UPDATE mahasiswa SET foto = ? WHERE nim = ?";
                    PreparedStatement pstmt = conn.prepareStatement(sqlUpdate);
                    pstmt.setString(1, newFileName);
                    pstmt.setString(2, nim);
                    pstmt.executeUpdate();


                    // 4. Update Tampilan Langsung
                    loadFotoKeLabel(lblFoto, newFileName);
                    JOptionPane.showMessageDialog(dialog, "Foto berhasil diubah!");

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog, "Gagal upload: " + ex.getMessage());
                }
            }
        });

        // --- LOGIKA SIMPAN DATA ---
        btnSimpan.addActionListener(e -> {
            try {
                String sqlUpdate = "UPDATE mahasiswa SET email = ?, no_hp = ? WHERE nim = ?";
                PreparedStatement pstmt = conn.prepareStatement(sqlUpdate);
                pstmt.setString(1, txtEmail.getText());
                pstmt.setString(2, txtHp.getText());
                pstmt.setString(3, nim);
                if(pstmt.executeUpdate() > 0) {
                    JOptionPane.showMessageDialog(dialog, "Biodata berhasil diperbarui!");
                    dialog.dispose();
                }
            } catch (SQLException ex) { JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage()); }
        });

        btnClose.addActionListener(e -> dialog.dispose());

        bottomPanel.add(btnSimpan);
        bottomPanel.add(btnClose);

        // Gabungkan Layout
        mainContainer.add(photoPanel, BorderLayout.WEST);
        mainContainer.add(dataPanel, BorderLayout.CENTER);

        dialog.add(headerPanel, BorderLayout.NORTH);
        dialog.add(mainContainer, BorderLayout.CENTER);
        dialog.add(bottomPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    // --- HELPER UNTUK LOAD GAMBAR KE LABEL (RESIZE OTOMATIS) ---
    private void loadFotoKeLabel(JLabel label, String namaFile) {
        if (namaFile != null && !namaFile.isEmpty()) {
            File f = new File(namaFile);
            if (f.exists()) {
                ImageIcon icon = new ImageIcon(namaFile);
                Image img = icon.getImage();
                // Resize agar pas di kotak 150x200
                Image newImg = img.getScaledInstance(150, 200, Image.SCALE_SMOOTH);
                label.setIcon(new ImageIcon(newImg));
                label.setText(""); // Hapus teks jika ada gambar
                return;
            }
        }
        // Jika tidak ada foto atau file hilang
        label.setIcon(null);
        label.setText("<html><center>Belum ada<br>Foto</center></html>");
        label.setHorizontalAlignment(SwingConstants.CENTER);
    }

    // Helper 1: Read-only Row
    private void addProfileRow(JPanel panel, GridBagConstraints gbc, int row, String label, String value) {
        gbc.gridy = row;
        gbc.gridx = 0; gbc.weightx = 0.3;
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl.setForeground(Color.GRAY);
        panel.add(lbl, gbc);

        gbc.gridx = 1; gbc.weightx = 0.7;
        JLabel val = new JLabel(value);
        val.setFont(new Font("Segoe UI", Font.BOLD, 15));
        val.setForeground(Color.BLACK);
        panel.add(val, gbc);
    }

    // Helper 2: Editable Row
    private void addEditableRow(JPanel panel, GridBagConstraints gbc, int row, String label, JTextField textField) {
        gbc.gridy = row;
        gbc.gridx = 0; gbc.weightx = 0.3;
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl.setForeground(new Color(236, 64, 122)); // Pink
        panel.add(lbl, gbc);

        gbc.gridx = 1; gbc.weightx = 0.7;
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textField.setPreferredSize(new Dimension(200, 30));
        panel.add(textField, gbc);
    }
    // ==========================================================
    // 5. FITUR LIHAT & UPLOAD TUGAS (UPDATE)
    // ==========================================================
    private void tampilkanTugas() {
        JDialog dialog = new JDialog(this, "Daftar Tugas Kuliah", true);
        dialog.setSize(1100, 600);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        // 1. PANEL KIRI (Profil Singkat)
        JPanel leftProfile = createSideProfilePanel();

        // 2. PANEL KANAN (Tabel Tugas)
        JPanel rightContent = new JPanel(new BorderLayout());

        // --- Header Kanan ---
        JPanel headerKanan = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerKanan.setBackground(new Color(236, 64, 122)); // Pink
        JLabel lblTitle = new JLabel("DAFTAR TUGAS SAYA");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(Color.WHITE);
        headerKanan.add(lblTitle);

        // --- Tabel Setup ---
        // Kolom 0 (ID Tugas) akan disembunyikan
        String[] columnNames = {"ID", "Mata Kuliah", "Judul Tugas", "Deskripsi", "Deadline"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Agar tidak bisa diedit
            }
        };

        // --- Query SQL (UPDATE: Ambil id_tugas) ---
        try {
            String sql = """
                    SELECT t.id_tugas, mk.nama_mk, t.judul, t.deskripsi, t.deadline
                    FROM tugas t
                    JOIN krs k ON t.kode_mk = k.kode_mk
                    JOIN matakuliah mk ON t.kode_mk = mk.kode_mk
                    WHERE k.nim = ?
                    ORDER BY t.deadline ASC
                    """;

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, nim);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getString("id_tugas"), // Index 0 (Hidden)
                        rs.getString("nama_mk"),
                        rs.getString("judul"),
                        rs.getString("deskripsi"),
                        rs.getString("deadline")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat tugas: " + e.getMessage());
        }

        // --- Styling Tabel ---
        JTable table = createTableStyle(model);

        // Sembunyikan Kolom ID Tugas (Index 0)
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setWidth(0);

        table.getColumnModel().getColumn(1).setPreferredWidth(200); // MK
        table.getColumnModel().getColumn(2).setPreferredWidth(200); // Judul
        table.getColumnModel().getColumn(3).setPreferredWidth(300); // Deskripsi
        table.getColumnModel().getColumn(4).setPreferredWidth(100); // Deadline

        table.getTableHeader().setBackground(new Color(236, 64, 122));

        // --- Panel Bawah (Tombol Upload & Kembali) ---
        JPanel panelBawah = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBawah.setBackground(new Color(245, 245, 245));
        panelBawah.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // TOMBOL UPLOAD (BARU)
        JButton btnUpload = new JButton("Upload Tugas");
        btnUpload.setBackground(new Color(40, 167, 69)); // Hijau
        btnUpload.setForeground(Color.WHITE);
        btnUpload.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnUpload.setPreferredSize(new Dimension(150, 35));
        btnUpload.setFocusPainted(false);

        // TOMBOL KEMBALI
        JButton btnKembali = new JButton("Kembali");
        btnKembali.setBackground(new Color(220, 53, 69));
        btnKembali.setForeground(Color.WHITE);
        btnKembali.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnKembali.setPreferredSize(new Dimension(100, 35));
        btnKembali.setFocusPainted(false);
        btnKembali.addActionListener(e -> dialog.dispose());

        panelBawah.add(btnUpload);
        panelBawah.add(btnKembali);

        // --- LOGIC UPLOAD TUGAS ---
        btnUpload.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(dialog, "Pilih tugas yang ingin dikumpulkan!");
                return;
            }

            // Ambil ID Tugas dari kolom tersembunyi
            String idTugas = table.getValueAt(row, 0).toString();

            // Buka File Chooser
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Pilih File Tugas (PDF/DOCX/ZIP/JPG)");

            if (fileChooser.showOpenDialog(dialog) == JFileChooser.APPROVE_OPTION) {
                try {
                    File selectedFile = fileChooser.getSelectedFile();
                    String fileName = selectedFile.getName();

                    // Generate nama file unik: tugas_NIM_IDTUGAS_NamaAsli
                    String newFileName = "tugas_" + nim + "_" + idTugas + "_" + fileName;
                    File destFile = new File(newFileName);

                    // Copy file ke folder project
                    Files.copy(selectedFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                    // Simpan ke Database
                    // Pastikan tabel pengumpulan_tugas sudah dibuat!
                    String sqlInsert = """
                            INSERT INTO pengumpulan_tugas (id_tugas, nim, file_path, tanggal_kumpul) 
                            VALUES (?, ?, ?, CURDATE())
                            """;

                    PreparedStatement pst = conn.prepareStatement(sqlInsert);
                    pst.setString(1, idTugas);
                    pst.setString(2, nim);
                    pst.setString(3, newFileName); // Simpan nama filenya saja

                    pst.executeUpdate();

                    JOptionPane.showMessageDialog(dialog, "Tugas berhasil dikumpulkan!");

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog, "Gagal upload: " + ex.getMessage());
                }
            }
        });

        // --- Susun Layout ---
        rightContent.add(headerKanan, BorderLayout.NORTH);
        rightContent.add(new JScrollPane(table), BorderLayout.CENTER);
        rightContent.add(panelBawah, BorderLayout.SOUTH);

        dialog.add(leftProfile, BorderLayout.WEST);
        dialog.add(rightContent, BorderLayout.CENTER);

        dialog.setVisible(true);
    }

    // ==========================================================
    // 6. FITUR ISI KRS & UBAH UKT
    // ==========================================================
    private void prosesAmbilKRS() {
        JDialog dialog = new JDialog(this, "Isi KRS", true);
        dialog.setSize(1100, 650);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        // 1. PANEL KIRI (Profil)
        JPanel leftProfile = createSideProfilePanel();

        // 2. PANEL KANAN (Konten KRS)
        JPanel rightContent = new JPanel(new BorderLayout());

        // --- Table ---
        String[] columnNames = {"Kode MK", "Nama Mata Kuliah", "SKS", "Jadwal"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        try {
            String sql = "SELECT * FROM matakuliah";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getString("kode_mk"), rs.getString("nama_mk"), rs.getInt("sks"), rs.getString("jadwal")
                });
            }
        } catch (SQLException e) { JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()); }

        JTable table = createTableStyle(model);
        table.getColumnModel().getColumn(0).setPreferredWidth(80);
        table.getColumnModel().getColumn(1).setPreferredWidth(300);
        table.getColumnModel().getColumn(3).setPreferredWidth(200);

        // --- Panel Input Bawah (UPDATE: Ada Tombol Kembali) ---
        JPanel panelInput = new JPanel(new BorderLayout());
        panelInput.setBackground(new Color(245, 245, 245));
        panelInput.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20)); // Padding

        // Container Input (Kiri)
        JPanel inputWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        inputWrapper.setOpaque(false);

        JTextField txtKode = new JTextField(10);
        txtKode.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtKode.setPreferredSize(new Dimension(100, 35));

        JButton btnAmbil = new JButton("AMBIL MATA KULIAH");
        btnAmbil.setBackground(new Color(40, 167, 69)); // Hijau
        btnAmbil.setForeground(Color.WHITE);
        btnAmbil.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnAmbil.setPreferredSize(new Dimension(180, 35));
        btnAmbil.setFocusPainted(false);

        inputWrapper.add(new JLabel("Kode MK:"));
        inputWrapper.add(txtKode);
        inputWrapper.add(btnAmbil);

        // Tombol Kembali (Kanan)
        JButton btnKembali = new JButton("Kembali");
        btnKembali.setBackground(new Color(220, 53, 69)); // Merah
        btnKembali.setForeground(Color.WHITE);
        btnKembali.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnKembali.setPreferredSize(new Dimension(100, 35));
        btnKembali.setFocusPainted(false);
        btnKembali.addActionListener(e -> dialog.dispose()); // Tutup Dialog

        panelInput.add(inputWrapper, BorderLayout.WEST);
        panelInput.add(btnKembali, BorderLayout.EAST);

        // Logic Klik Tabel
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                txtKode.setText(table.getValueAt(table.getSelectedRow(), 0).toString());
            }
        });

        // Logic Tombol Ambil
        btnAmbil.addActionListener(e -> {
            String k = txtKode.getText().trim();
            if (k.isEmpty()) return;
            try {
                PreparedStatement ps = conn.prepareStatement("INSERT INTO krs (nim, kode_mk) VALUES (?, ?)");
                ps.setString(1, nim); ps.setString(2, k);
                if (ps.executeUpdate() > 0) JOptionPane.showMessageDialog(dialog, "Berhasil ambil: " + k);
            } catch (Exception ex) { JOptionPane.showMessageDialog(dialog, "Gagal: " + ex.getMessage()); }
        });

        // Header Kanan
        JPanel headerKanan = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerKanan.setBackground(new Color(236, 64, 122));
        JLabel lblTitle = new JLabel("PILIH MATA KULIAH");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(Color.WHITE);
        headerKanan.add(lblTitle);

        rightContent.add(headerKanan, BorderLayout.NORTH);
        rightContent.add(new JScrollPane(table), BorderLayout.CENTER);
        rightContent.add(panelInput, BorderLayout.SOUTH);

        dialog.add(leftProfile, BorderLayout.WEST);
        dialog.add(rightContent, BorderLayout.CENTER);

        dialog.setVisible(true);
    }

    private void tampilkanTabelMatakuliah() {
        JDialog dialog = new JDialog(this, "Daftar Mata Kuliah Tersedia", true);
        dialog.setSize(950, 550);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout()); // Pastikan Layout di-set

        // --- 1. SETUP TABEL ---
        String[] columnNames = {"Kode MK", "Nama Mata Kuliah", "SKS", "Jadwal"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        try {
            String sql = "SELECT * FROM matakuliah";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getString("kode_mk"),
                        rs.getString("nama_mk"),
                        rs.getInt("sks"),
                        rs.getString("jadwal")
                });
            }
        } catch (SQLException e) { JOptionPane.showMessageDialog(this, e.getMessage()); }

        JTable table = createTableStyle(model);
        table.getColumnModel().getColumn(0).setPreferredWidth(100);
        table.getColumnModel().getColumn(1).setPreferredWidth(350);
        table.getColumnModel().getColumn(3).setPreferredWidth(250);

        // --- 2. SETUP PANEL BAWAH (TOMBOL KEMBALI) ---
        // (Ini kode yang tadi Anda taruh di bawah setVisible, harusnya di sini)
        JPanel panelBawah = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10));
        panelBawah.setBackground(new Color(245, 245, 245));
        panelBawah.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));

        JButton btnKembali = new JButton("Kembali");
        btnKembali.setBackground(new Color(220, 53, 69)); // Merah
        btnKembali.setForeground(Color.WHITE);
        btnKembali.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnKembali.setPreferredSize(new Dimension(100, 35));
        btnKembali.setFocusPainted(false);

        btnKembali.addActionListener(e -> dialog.dispose()); // Aksi Tutup

        panelBawah.add(btnKembali);

        // --- 3. SUSUN LAYOUT & TAMPILKAN ---
        dialog.add(new JScrollPane(table), BorderLayout.CENTER);
        dialog.add(panelBawah, BorderLayout.SOUTH); // Panel bawah harus di-add dulu

        dialog.setVisible(true); // INI WAJIB DI BARIS PALING TERAKHIR
    }

    // ==========================================================
    // FITUR PEMBAYARAN / UPLOAD BUKTI UKT
    // ==========================================================
    private void prosesPembayaranUKT(double nominal, boolean isLunas) {
        JDialog dialog = new JDialog(this, "Status Pembayaran UKT", true);
        dialog.setSize(400, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel content = new JPanel(new GridBagLayout());
        content.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0; gbc.gridy = 0;

        // 1. Tampilkan Info Nominal
        NumberFormat formatRp = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        JLabel lblNominal = new JLabel(formatRp.format(nominal).replace(",00", ""));
        lblNominal.setFont(new Font("Segoe UI", Font.BOLD, 24));
        content.add(lblNominal, gbc);

        gbc.gridy++;
        JLabel lblStatus = new JLabel(isLunas ? "STATUS: LUNAS" : "STATUS: BELUM LUNAS");
        lblStatus.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblStatus.setForeground(isLunas ? new Color(76, 175, 80) : Color.RED);
        content.add(lblStatus, gbc);

        // 2. Logika Tampilan Berdasarkan Status
        if (isLunas) {
            // Tampilan Jika Sudah Lunas
            gbc.gridy++;
            JLabel lblIcon = new JLabel("✔ Pembayaran Terverifikasi");
            lblIcon.setFont(new Font("Segoe UI", Font.BOLD, 16));
            lblIcon.setForeground(new Color(76, 175, 80));
            content.add(lblIcon, gbc);

            gbc.gridy++;
            JButton btnTutup = new JButton("Tutup");
            btnTutup.addActionListener(e -> dialog.dispose());
            content.add(btnTutup, gbc);

        } else {
            // Tampilan Jika Belum Lunas (Form Upload)
            gbc.gridy++;
            JLabel lblInstruksi = new JLabel("<html><center>Silakan transfer ke BNI: 3452361890<br>a.n UPN Veteran Jakarta<br>Lalu upload bukti transfer di bawah:</center></html>");
            lblInstruksi.setHorizontalAlignment(SwingConstants.CENTER);
            content.add(lblInstruksi, gbc);

            gbc.gridy++;
            JButton btnUpload = new JButton("Upload Bukti Transfer");
            btnUpload.setBackground(new Color(33, 150, 243)); // Biru
            btnUpload.setForeground(Color.WHITE);
            btnUpload.setPreferredSize(new Dimension(200, 40));
            btnUpload.setFocusPainted(false);
            content.add(btnUpload, gbc);

            // LOGIKA UPLOAD
            btnUpload.addActionListener(e -> {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Pilih Bukti Transfer (JPG/PNG)");
                fileChooser.setFileFilter(new FileNameExtensionFilter("Gambar", "jpg", "png", "jpeg"));

                if (fileChooser.showOpenDialog(dialog) == JFileChooser.APPROVE_OPTION) {
                    try {
                        File selectedFile = fileChooser.getSelectedFile();

                        // Buat nama file unik (bukti_ukt_NIM.jpg) agar tidak tertimpa
                        String ext = "";
                        int i = selectedFile.getName().lastIndexOf('.');
                        if (i > 0) ext = selectedFile.getName().substring(i);

                        String newName = "bukti_ukt_" + nim + ext;
                        File dest = new File(newName);

                        // Copy file ke folder project
                        Files.copy(selectedFile.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);

                        // UPDATE DATABASE: Ubah status jadi Lunas & Simpan nama file
                        String sqlUpdate = "UPDATE mahasiswa SET status_bayar = 'Lunas', bukti_bayar = ? WHERE nim = ?";
                        PreparedStatement pst = conn.prepareStatement(sqlUpdate);
                        pst.setString(1, newName);
                        pst.setString(2, nim);

                        int affected = pst.executeUpdate();
                        if(affected > 0) {
                            JOptionPane.showMessageDialog(dialog, "Bukti berhasil diupload!\nStatus pembayaran kini LUNAS.");
                            dialog.dispose();

                            // PENTING: Refresh Dashboard Stats agar warna berubah jadi Hijau
                            refreshData();
                        }

                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(dialog, "Gagal upload: " + ex.getMessage());
                    }
                }
            });
        }

        dialog.add(content, BorderLayout.CENTER);
        dialog.setVisible(true);
    }
    // ==========================================================
    // 3. FITUR LIHAT KHS
    // ==========================================================
    private void tampilkanKHS() {
        JDialog dialog = new JDialog(this, "Kartu Hasil Studi", true);
        dialog.setSize(1100, 600);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        // 1. PANEL KIRI (Profil - Reuse Code)
        JPanel leftProfile = createSideProfilePanel();

        // 2. PANEL KANAN (Tabel Nilai)
        JPanel rightContent = new JPanel(new BorderLayout());

        DefaultTableModel model = new DefaultTableModel(new String[]{"Kode", "Mata Kuliah", "SKS", "Nilai", "Absensi"}, 0);

        try {
            String sql = "SELECT mk.kode_mk, mk.nama_mk, mk.sks, k.nilai, k.absensi FROM krs k JOIN matakuliah mk ON k.kode_mk = mk.kode_mk WHERE k.nim = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, nim);
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()) {
                model.addRow(new Object[]{
                        rs.getString(1), rs.getString(2), rs.getInt(3),
                        (rs.getString(4)==null?"-":rs.getString(4)),
                        rs.getInt(5)
                });
            }
        } catch(SQLException e) { JOptionPane.showMessageDialog(this, e.getMessage()); }

        JTable table = createTableStyle(model);

        // Header Kanan
        JPanel headerKanan = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerKanan.setBackground(new Color(236, 64, 122));
        JLabel lblTitle = new JLabel("HASIL STUDI SEMESTER");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(Color.WHITE);
        headerKanan.add(lblTitle);

        // --- Panel Bawah (UPDATE: Tombol Kembali) ---
        JPanel panelBawah = new JPanel(new BorderLayout());
        panelBawah.setBackground(new Color(245, 245, 245));
        panelBawah.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); // Margin

        // Info (Kiri) - Opsional kalau mau nampilin pesan
        JLabel lblInfo = new JLabel("*) Nilai dapat berubah sewaktu-waktu.");
        lblInfo.setForeground(Color.GRAY);
        lblInfo.setFont(new Font("Segoe UI", Font.ITALIC, 12));

        // Tombol Kembali (Kanan)
        JButton btnKembali = new JButton("Kembali");
        btnKembali.setBackground(new Color(220, 53, 69));
        btnKembali.setForeground(Color.WHITE);
        btnKembali.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnKembali.setPreferredSize(new Dimension(100, 35));
        btnKembali.setFocusPainted(false);
        btnKembali.addActionListener(e -> dialog.dispose());

        panelBawah.add(lblInfo, BorderLayout.WEST);
        panelBawah.add(btnKembali, BorderLayout.EAST);

        rightContent.add(headerKanan, BorderLayout.NORTH);
        rightContent.add(new JScrollPane(table), BorderLayout.CENTER);
        rightContent.add(panelBawah, BorderLayout.SOUTH);

        dialog.add(leftProfile, BorderLayout.WEST);
        dialog.add(rightContent, BorderLayout.CENTER);

        dialog.setVisible(true);
    }

    // HELPER UI
    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(Color.WHITE);
        btn.setForeground(new Color(50, 50, 50));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JTable createTableStyle(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setRowHeight(40);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setGridColor(new Color(230, 230, 230));
        table.setShowVerticalLines(true);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(255, 204, 229));
        table.setSelectionForeground(Color.BLACK);
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                return this;
            }
        };
        table.setDefaultRenderer(Object.class, renderer);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(236, 64, 122));
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setPreferredSize(new Dimension(0, 45));
        return table;
    }

    // ==========================================================
    // INNER CLASSES
    // ==========================================================

    class RunningTextPanel extends JPanel implements ActionListener {
        private float xPos;
        private String text;
        private Timer timer;
        private int textWidth = 0;
        private final int GAP = 100;

        public RunningTextPanel(String text) {
            this.text = text;
            this.setPreferredSize(new Dimension(0, 40));
            this.setBackground(new Color(180, 40, 90));
            this.xPos = 1000;
            timer = new Timer(15, this);
            timer.start();
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2d.setFont(new Font("Segoe UI", Font.BOLD, 16));
            g2d.setColor(Color.WHITE);
            if (textWidth == 0) {
                FontMetrics fm = g2d.getFontMetrics();
                textWidth = fm.stringWidth(text);
                if (xPos == 1000) xPos = getWidth();
            }
            int yPos = (getHeight() + g2d.getFontMetrics().getAscent()) / 2 - 3;
            g2d.drawString(text, (int)xPos, yPos);
            g2d.drawString(text, (int)xPos + textWidth + GAP, yPos);
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            xPos -= 1.5f;
            if (xPos <= -(textWidth + GAP)) xPos += (textWidth + GAP);
            repaint();
        }
    }

    class GradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            GradientPaint gp = new GradientPaint(0, 0, new Color(244, 143, 177), 0, getHeight(), new Color(194, 24, 91));
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    private JPanel createSideProfilePanel() {
        JPanel sidePanel = new JPanel();
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
        sidePanel.setBackground(Color.WHITE);
        sidePanel.setPreferredSize(new Dimension(300, 0)); // Lebar panel kiri
        sidePanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.LIGHT_GRAY));

        // -- VARIABEL DATA --
        String dNama="-", dNim="-", dAngkatan="-", dKelas="-", dJurusan="-", dFoto=null;
        double ipk = 0.0;
        int totalSks = 0;

        // 1. AMBIL DATA MAHASISWA & HITUNG IPK/SKS
        try {
            // Data Diri
            String sqlMhs = "SELECT * FROM mahasiswa WHERE nim = ?";
            PreparedStatement pstMhs = conn.prepareStatement(sqlMhs);
            pstMhs.setString(1, nim);
            ResultSet rsMhs = pstMhs.executeQuery();
            if (rsMhs.next()) {
                dNim = rsMhs.getString("nim");
                dNama = rsMhs.getString("nama");
                dJurusan = rsMhs.getString("jurusan");
                dKelas = rsMhs.getString("kelas") == null ? "-" : rsMhs.getString("kelas");
                dAngkatan = rsMhs.getString("angkatan") == null ? "-" : rsMhs.getString("angkatan");
                dFoto = rsMhs.getString("foto");
            }

            // Hitung IPK & Total SKS
            String sqlNilai = "SELECT k.nilai, m.sks FROM krs k JOIN matakuliah m ON k.kode_mk = m.kode_mk WHERE k.nim = ?";
            PreparedStatement pstNilai = conn.prepareStatement(sqlNilai);
            pstNilai.setString(1, nim);
            ResultSet rsNilai = pstNilai.executeQuery();

            double totalBobot = 0;
            while(rsNilai.next()) {
                String n = rsNilai.getString("nilai");
                int sks = rsNilai.getInt("sks");

                // Konversi Nilai Huruf ke Angka
                double bobot = 0;
                if(n != null) {
                    switch(n) {
                        case "A": bobot = 4.0; break;
                        case "B": bobot = 3.0; break;
                        case "C": bobot = 2.0; break;
                        case "D": bobot = 1.0; break;
                        default: bobot = 0.0;
                    }
                }
                totalBobot += (bobot * sks);
                totalSks += sks;
            }
            if(totalSks > 0) ipk = totalBobot / totalSks;

        } catch (SQLException e) { e.printStackTrace(); }

        // -- 2. KOMPONEN UI --

        // A. FOTO PROFIL
        JLabel lblFoto = new JLabel();
        lblFoto.setPreferredSize(new Dimension(140, 180));
        lblFoto.setMaximumSize(new Dimension(140, 180));
        lblFoto.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        lblFoto.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Load Foto
        if (dFoto != null && !dFoto.isEmpty()) {
            ImageIcon icon = new ImageIcon(dFoto);
            Image img = icon.getImage().getScaledInstance(140, 180, Image.SCALE_SMOOTH);
            lblFoto.setIcon(new ImageIcon(img));
        } else {
            lblFoto.setText("No Photo");
            lblFoto.setHorizontalAlignment(SwingConstants.CENTER);
        }

        // B. TABEL INFO KECIL
        JPanel infoPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 0, 20)); // Margin kiri kanan

        // Tambah baris data
        addSmallInfo(infoPanel, "Nama", dNama);
        addSmallInfo(infoPanel, "NIM", dNim);
        addSmallInfo(infoPanel, "Angkatan", dAngkatan);
        addSmallInfo(infoPanel, "Kelas", dKelas);
        addSmallInfo(infoPanel, "Prodi", dJurusan);

        // C. PANEL IPK & SKS (Biru)
        JPanel statsPanel = new JPanel(new GridLayout(2, 1));
        statsPanel.setBackground(new Color(240, 248, 255)); // Alice Blue
        statsPanel.setBorder(BorderFactory.createTitledBorder("Statistik Akademik"));
        statsPanel.setMaximumSize(new Dimension(260, 100));

        JLabel lblSks = new JLabel("Total SKS: " + totalSks);
        lblSks.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblSks.setBorder(BorderFactory.createEmptyBorder(5, 10, 0, 0));

        JLabel lblIpk = new JLabel(String.format("IPK: %.2f", ipk));
        lblIpk.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblIpk.setForeground(new Color(0, 100, 0)); // Hijau
        lblIpk.setBorder(BorderFactory.createEmptyBorder(0, 10, 5, 0));

        statsPanel.add(lblSks);
        statsPanel.add(lblIpk);

        // -- SUSUN KE PANEL UTAMA --
        sidePanel.add(Box.createVerticalStrut(20)); // Spasi Atas
        sidePanel.add(lblFoto);
        sidePanel.add(Box.createVerticalStrut(10));
        sidePanel.add(infoPanel);
        sidePanel.add(Box.createVerticalGlue()); // Pendorong ke bawah
        sidePanel.add(statsPanel);
        sidePanel.add(Box.createVerticalStrut(20)); // Spasi Bawah

        return sidePanel;
    }

    // Helper kecil untuk teks label di sidebar
    private void addSmallInfo(JPanel p, String label, String val) {
        JLabel lblK = new JLabel(label);
        lblK.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblK.setForeground(Color.GRAY);

        JLabel lblV = new JLabel("<html><b>" + val + "</b></html>"); // HTML agar bold dan wrap
        lblV.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        p.add(lblK);
        p.add(lblV);
    }

}