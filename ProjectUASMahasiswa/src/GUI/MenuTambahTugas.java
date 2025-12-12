package GUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.sql.*;

public class MenuTambahTugas extends JFrame {

    private Connection conn;
    private String nidn;

    private JComboBox<String> cbKodeMK;
    private JTable tableTugas;
    private DefaultTableModel tableModel;
    private JButton btnTambahTugas, btnEditTugas, btnHapusTugas, btnLihatPengumpulan; // Update variable

    public MenuTambahTugas(Connection conn, String nidn) {
        super("Daftar Tugas Mata Kuliah");
        this.conn = conn;
        this.nidn = nidn;

        // Pastikan file "logo_upn.png" ada di folder project
        ImageIcon icon = new ImageIcon("logo_upn.png");
        this.setIconImage(icon.getImage());
        setSize(950, 600); // Sedikit diperlebar
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // ========================= LEFT GRADIENT PANEL =========================
        JPanel leftPanel = new GradientPanel();
        leftPanel.setPreferredSize(new Dimension(300, 0));
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JLabel lblTitle = new JLabel("<html><div style='font-size:30pt; font-weight:600;color:white;'>Daftar<br>Tugas</div></html>");
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSub = new JLabel("<html><div style='font-size:13pt; color:white;'>Pilih MK dan kelola tugas</div></html>");
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

        JButton btnLoad = styledSmallButton("Tampilkan Tugas");
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
        // Kolom id_tugas disembunyikan nanti untuk referensi
        tableModel = new DefaultTableModel(new Object[]{"ID", "Judul", "Deskripsi", "Deadline"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        tableTugas = new JTable(tableModel);
        tableTugas.setRowHeight(28);
        tableTugas.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tableTugas.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));

        // Sembunyikan kolom ID (index 0) tapi datanya tetap ada
        tableTugas.getColumnModel().getColumn(0).setMinWidth(0);
        tableTugas.getColumnModel().getColumn(0).setMaxWidth(0);
        tableTugas.getColumnModel().getColumn(0).setWidth(0);

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(JLabel.CENTER);
        tableTugas.getColumnModel().getColumn(3).setCellRenderer(center);

        JScrollPane scroll = new JScrollPane(tableTugas);
        scroll.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        add(scroll, BorderLayout.CENTER);

        // ========================= BUTTONS =========================
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JButton btnKembali = new JButton("Kembali");
        btnKembali.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnKembali.setBackground(new Color(108, 117, 125));
        btnKembali.setForeground(Color.WHITE);
        btnKembali.setFocusPainted(false);
        btnKembali.addActionListener(e -> dispose());

        btnTambahTugas = new JButton("Tambah");
        btnTambahTugas.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnTambahTugas.setBackground(new Color(40, 167, 69)); // Hijau
        btnTambahTugas.setForeground(Color.WHITE);
        btnTambahTugas.setFocusPainted(false);

        btnEditTugas = new JButton("Edit");
        btnEditTugas.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnEditTugas.setBackground(new Color(0, 153, 255));
        btnEditTugas.setForeground(Color.WHITE);
        btnEditTugas.setFocusPainted(false);

        btnHapusTugas = new JButton("Hapus");
        btnHapusTugas.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnHapusTugas.setBackground(new Color(220, 53, 69));
        btnHapusTugas.setForeground(Color.WHITE);
        btnHapusTugas.setFocusPainted(false);

        // --- TOMBOL BARU ---
        btnLihatPengumpulan = new JButton("Lihat Pengumpulan");
        btnLihatPengumpulan.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLihatPengumpulan.setBackground(new Color(102, 51, 153)); // Ungu
        btnLihatPengumpulan.setForeground(Color.WHITE);
        btnLihatPengumpulan.setFocusPainted(false);

        bottomPanel.add(btnTambahTugas);
        bottomPanel.add(btnEditTugas);
        bottomPanel.add(btnHapusTugas);
        bottomPanel.add(btnLihatPengumpulan); // Add button
        bottomPanel.add(btnKembali);

        add(bottomPanel, BorderLayout.SOUTH);

        // ========================= EVENTS =========================
        btnLoad.addActionListener(e -> loadTugas());
        btnTambahTugas.addActionListener(e -> new PopupTambahTugas());
        btnEditTugas.addActionListener(e -> openEditPopup());
        btnHapusTugas.addActionListener(e -> hapusTugas());

        // Event Lihat Pengumpulan
        btnLihatPengumpulan.addActionListener(e -> {
            int row = tableTugas.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Pilih tugas terlebih dahulu!");
                return;
            }
            // Ambil ID Tugas dari kolom tersembunyi (index 0)
            String idTugas = tableModel.getValueAt(row, 0).toString();
            String judul = tableModel.getValueAt(row, 1).toString();

            new PopupLihatPengumpulan(idTugas, judul);
        });

        setVisible(true);
    }

    // ========================= LOGIC HELPERS =========================

    private void loadKodeMK() {
        try {
            PreparedStatement stmt = conn.prepareStatement("SELECT kode_mk, nama_mk FROM matakuliah WHERE nidn_dosen = ?");
            stmt.setString(1, nidn);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                cbKodeMK.addItem("(" + rs.getString("kode_mk") + ") " + rs.getString("nama_mk"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void loadTugas() {
        tableModel.setRowCount(0);
        if (cbKodeMK.getSelectedItem() == null) return;
        String kodeMk = cbKodeMK.getSelectedItem().toString();
        kodeMk = kodeMk.substring(1, kodeMk.indexOf(")"));

        try {
            // Ambil juga ID_TUGAS
            PreparedStatement stmt = conn.prepareStatement(
                    "SELECT id_tugas, judul, deskripsi, deadline FROM tugas WHERE kode_mk = ?"
            );
            stmt.setString(1, kodeMk);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("id_tugas"), // Column 0 (Hidden)
                        rs.getString("judul"),
                        rs.getString("deskripsi"),
                        rs.getString("deadline")
                });
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void hapusTugas() {
        int row = tableTugas.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Pilih tugas!"); return; }

        int confirm = JOptionPane.showConfirmDialog(this, "Hapus tugas ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        String idTugas = tableModel.getValueAt(row, 0).toString();
        try {
            PreparedStatement stmt = conn.prepareStatement("DELETE FROM tugas WHERE id_tugas=?");
            stmt.setString(1, idTugas);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Tugas dihapus!");
            loadTugas();
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    private void openEditPopup() {
        int row = tableTugas.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Pilih tugas!"); return; }

        String id = tableModel.getValueAt(row, 0).toString();
        String j = tableModel.getValueAt(row, 1).toString();
        String d = tableModel.getValueAt(row, 2).toString();
        String dl = tableModel.getValueAt(row, 3).toString();

        new PopupEditTugas(id, j, d, dl);
    }

    // ========================= CLASS POPUP LIHAT PENGUMPULAN (BARU) =========================
    class PopupLihatPengumpulan extends JDialog {
        private JTable tableP;
        private DefaultTableModel modelP;
        private String idTugasRef; // Simpan ID Tugas untuk refresh data

        public PopupLihatPengumpulan(String idTugas, String judulTugas) {
            this.idTugasRef = idTugas; // Simpan ID

            setTitle("Pengumpulan: " + judulTugas);
            setSize(900, 550);
            setLocationRelativeTo(MenuTambahTugas.this);
            setModal(true);
            setLayout(new BorderLayout());

            // Header
            JLabel lblH = new JLabel("Daftar Pengumpulan Mahasiswa", JLabel.CENTER);
            lblH.setFont(new Font("Segoe UI", Font.BOLD, 18));
            lblH.setBorder(BorderFactory.createEmptyBorder(15,0,10,0));
            add(lblH, BorderLayout.NORTH);

            // Table
            String[] cols = {"NIM", "Nama Mahasiswa", "Tanggal Kumpul", "File/Catatan", "Nilai"};
            modelP = new DefaultTableModel(cols, 0) {
                @Override public boolean isCellEditable(int row, int col) { return false; }
            };

            tableP = new JTable(modelP);
            tableP.setRowHeight(30);
            tableP.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            tableP.getTableHeader().setBackground(new Color(102, 51, 153)); // Ungu
            tableP.getTableHeader().setForeground(Color.WHITE);
            tableP.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));

            // Atur lebar kolom
            tableP.getColumnModel().getColumn(1).setPreferredWidth(200); // Nama
            tableP.getColumnModel().getColumn(3).setPreferredWidth(250); // File

            add(new JScrollPane(tableP), BorderLayout.CENTER);

            // Load Data Awal
            loadDataPengumpulan();

            // --- PANEL TOMBOL BAWAH ---
            JPanel pBawah = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            pBawah.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            JButton btnNilai = new JButton("Beri Nilai");
            btnNilai.setFont(new Font("Segoe UI", Font.BOLD, 14));
            btnNilai.setBackground(new Color(40, 167, 69)); // Hijau
            btnNilai.setForeground(Color.WHITE);

            JButton btnClose = new JButton("Tutup");
            btnClose.setFont(new Font("Segoe UI", Font.BOLD, 14));
            btnClose.setBackground(new Color(220, 53, 69)); // Merah
            btnClose.setForeground(Color.WHITE);

            pBawah.add(btnNilai);
            pBawah.add(btnClose);
            add(pBawah, BorderLayout.SOUTH);

            // --- ACTIONS ---
            btnClose.addActionListener(e -> dispose());

            btnNilai.addActionListener(e -> {
                int row = tableP.getSelectedRow();
                if (row == -1) {
                    JOptionPane.showMessageDialog(this, "Pilih mahasiswa yang ingin dinilai!");
                    return;
                }

                String nimMhs = tableP.getValueAt(row, 0).toString();
                String namaMhs = tableP.getValueAt(row, 1).toString();
                String nilaiLama = tableP.getValueAt(row, 4) == null ? "" : tableP.getValueAt(row, 4).toString();

                // Munculkan Input Dialog
                String nilaiBaru = JOptionPane.showInputDialog(this,
                        "Masukkan Nilai untuk " + namaMhs + " (" + nimMhs + "):",
                        nilaiLama);

                if (nilaiBaru != null) {
                    simpanNilaiTugas(nimMhs, nilaiBaru);
                }
            });

            setVisible(true);
        }

        // Method Load Data
        private void loadDataPengumpulan() {
            modelP.setRowCount(0);
            try {
                String sql = """
                        SELECT m.nim, m.nama, p.tanggal_kumpul, p.file_path, p.nilai
                        FROM pengumpulan_tugas p
                        JOIN mahasiswa m ON p.nim = m.nim
                        WHERE p.id_tugas = ?
                        ORDER BY p.tanggal_kumpul DESC
                        """;
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, idTugasRef);
                ResultSet rs = ps.executeQuery();

                while(rs.next()){
                    modelP.addRow(new Object[]{
                            rs.getString("nim"),
                            rs.getString("nama"),
                            rs.getString("tanggal_kumpul"),
                            rs.getString("file_path"),
                            rs.getString("nilai") == null ? "-" : rs.getString("nilai")
                    });
                }

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error load pengumpulan: " + ex.getMessage());
            }
        }

        // Method Simpan Nilai ke Database
        private void simpanNilaiTugas(String nim, String nilai) {
            try {
                String sql = "UPDATE pengumpulan_tugas SET nilai = ? WHERE id_tugas = ? AND nim = ?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, nilai);
                ps.setString(2, idTugasRef);
                ps.setString(3, nim);

                int affected = ps.executeUpdate();
                if (affected > 0) {
                    JOptionPane.showMessageDialog(this, "Nilai berhasil disimpan!");
                    loadDataPengumpulan(); // Refresh tabel biar nilai muncul
                } else {
                    JOptionPane.showMessageDialog(this, "Gagal menyimpan. Pastikan mahasiswa sudah mengumpulkan tugas.");
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error simpan nilai: " + ex.getMessage());
            }
        }
    }

    // ========================= CLASS POPUP TAMBAH =========================
    class PopupTambahTugas extends JDialog {
        public PopupTambahTugas() {
            setTitle("Tambah Tugas Baru");
            setSize(500, 500); // Ukuran lebih proporsional
            setLocationRelativeTo(MenuTambahTugas.this);
            setModal(true);
            setLayout(new BorderLayout());

            // --- HEADER PANEL ---
            JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
            headerPanel.setBackground(new Color(255, 140, 0)); // Orange
            JLabel lblTitle = new JLabel("Form Tambah Tugas");
            lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
            lblTitle.setForeground(Color.WHITE);
            headerPanel.add(lblTitle);
            add(headerPanel, BorderLayout.NORTH);

            // --- FORM PANEL ---
            JPanel formPanel = new JPanel(new GridBagLayout());
            formPanel.setBackground(Color.WHITE);
            formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weightx = 1.0;

            // Input Fields
            JTextField tfJudul = new JTextField();
            tfJudul.setPreferredSize(new Dimension(0, 30));

            JTextArea tfDesk = new JTextArea(5, 20); // 5 baris
            tfDesk.setLineWrap(true);
            tfDesk.setWrapStyleWord(true);
            JScrollPane scrollDesk = new JScrollPane(tfDesk);

            JTextField tfDeadline = new JTextField();
            tfDeadline.setPreferredSize(new Dimension(0, 30));
            tfDeadline.setToolTipText("Format: YYYY-MM-DD (Contoh: 2025-12-31)");

            // Add Components
            addFormItem(formPanel, gbc, 0, "Judul Tugas:", tfJudul);
            addFormItem(formPanel, gbc, 1, "Deskripsi:", scrollDesk);
            addFormItem(formPanel, gbc, 2, "Deadline (YYYY-MM-DD):", tfDeadline);

            add(formPanel, BorderLayout.CENTER);

            // --- BUTTON PANEL ---
            JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            btnPanel.setBackground(new Color(245, 245, 245));
            btnPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));

            JButton btnBatal = new JButton("Batal");
            styleButton(btnBatal, new Color(220, 53, 69));
            btnBatal.addActionListener(e -> dispose());

            JButton btnSimpan = new JButton("Simpan Tugas");
            styleButton(btnSimpan, new Color(40, 167, 69)); // Hijau

            btnPanel.add(btnBatal);
            btnPanel.add(btnSimpan);
            add(btnPanel, BorderLayout.SOUTH);

            // --- LOGIC SIMPAN ---
            btnSimpan.addActionListener(e -> {
                if (tfJudul.getText().isEmpty() || tfDeadline.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Judul dan Deadline wajib diisi!");
                    return;
                }
                try {
                    String kodeMk = cbKodeMK.getSelectedItem().toString();
                    kodeMk = kodeMk.substring(1, kodeMk.indexOf(")"));

                    PreparedStatement ps = conn.prepareStatement("INSERT INTO tugas (kode_mk, judul, deskripsi, deadline) VALUES (?,?,?,?)");
                    ps.setString(1, kodeMk);
                    ps.setString(2, tfJudul.getText());
                    ps.setString(3, tfDesk.getText());
                    ps.setString(4, tfDeadline.getText());
                    ps.executeUpdate();

                    JOptionPane.showMessageDialog(this, "Berhasil menambahkan tugas!");
                    loadTugas();
                    dispose();
                } catch(Exception ex) {
                    JOptionPane.showMessageDialog(this, "Gagal: " + ex.getMessage());
                }
            });
            setVisible(true);
        }
    }

    // ========================= CLASS POPUP EDIT =========================
    class PopupEditTugas extends JDialog {
        public PopupEditTugas(String id, String j, String d, String dl) {
            setTitle("Edit Tugas");
            setSize(500, 500);
            setLocationRelativeTo(MenuTambahTugas.this);
            setModal(true);
            setLayout(new BorderLayout());

            // --- HEADER PANEL ---
            JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
            headerPanel.setBackground(new Color(0, 123, 255)); // Biru
            JLabel lblTitle = new JLabel("Edit Data Tugas");
            lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
            lblTitle.setForeground(Color.WHITE);
            headerPanel.add(lblTitle);
            add(headerPanel, BorderLayout.NORTH);

            // --- FORM PANEL ---
            JPanel formPanel = new JPanel(new GridBagLayout());
            formPanel.setBackground(Color.WHITE);
            formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weightx = 1.0;

            // Input Fields (Pre-filled)
            JTextField tfJudul = new JTextField(j);
            tfJudul.setPreferredSize(new Dimension(0, 30));

            JTextArea tfDesk = new JTextArea(d, 5, 20);
            tfDesk.setLineWrap(true);
            tfDesk.setWrapStyleWord(true);
            JScrollPane scrollDesk = new JScrollPane(tfDesk);

            JTextField tfDeadline = new JTextField(dl);
            tfDeadline.setPreferredSize(new Dimension(0, 30));

            // Add Components
            addFormItem(formPanel, gbc, 0, "Judul Tugas:", tfJudul);
            addFormItem(formPanel, gbc, 1, "Deskripsi:", scrollDesk);
            addFormItem(formPanel, gbc, 2, "Deadline (YYYY-MM-DD):", tfDeadline);

            add(formPanel, BorderLayout.CENTER);

            // --- BUTTON PANEL ---
            JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            btnPanel.setBackground(new Color(245, 245, 245));
            btnPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));

            JButton btnBatal = new JButton("Batal");
            styleButton(btnBatal, new Color(108, 117, 125)); // Abu-abu
            btnBatal.addActionListener(e -> dispose());

            JButton btnUpdate = new JButton("Update Tugas");
            styleButton(btnUpdate, new Color(0, 123, 255)); // Biru

            btnPanel.add(btnBatal);
            btnPanel.add(btnUpdate);
            add(btnPanel, BorderLayout.SOUTH);

            // --- LOGIC UPDATE ---
            btnUpdate.addActionListener(e -> {
                try {
                    PreparedStatement ps = conn.prepareStatement("UPDATE tugas SET judul=?, deskripsi=?, deadline=? WHERE id_tugas=?");
                    ps.setString(1, tfJudul.getText());
                    ps.setString(2, tfDesk.getText());
                    ps.setString(3, tfDeadline.getText());
                    ps.setString(4, id);
                    ps.executeUpdate();

                    JOptionPane.showMessageDialog(this, "Data berhasil diperbarui!");
                    loadTugas();
                    dispose();
                } catch(Exception ex) {
                    JOptionPane.showMessageDialog(this, "Gagal: " + ex.getMessage());
                }
            });
            setVisible(true);
        }
    }

    // --- Helper untuk Form Layout ---
    private void addFormItem(JPanel p, GridBagConstraints gbc, int row, String labelText, JComponent field) {
        gbc.gridx = 0;
        gbc.gridy = row * 2;
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        p.add(lbl, gbc);

        gbc.gridy = row * 2 + 1;
        p.add(field, gbc);

        // Tambah jarak antar elemen
        gbc.gridy = row * 2 + 2;
        p.add(Box.createVerticalStrut(5), gbc);
    }

    // --- Helper untuk Style Tombol Popup ---
    private void styleButton(JButton btn, Color bgColor) {
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(130, 35));
    }
    private JButton styledSmallButton(String text) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setBackground(Color.WHITE);
        return btn;
    }

    static class GradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setPaint(new GradientPaint(0, 0, new Color(255, 140, 0), 0, getHeight(), new Color(255, 199, 153)));
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.dispose();
        }
    }
}