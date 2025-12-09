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
    private JButton btnTambahTugas, btnEditTugas, btnHapusTugas;

    public MenuTambahTugas(Connection conn, String nidn) {
        super("Daftar Tugas Mata Kuliah");
        this.conn = conn;
        this.nidn = nidn;

        setSize(900, 550);
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

        JLabel lblSub = new JLabel("<html><div style='font-size:13pt; color:white;'>Pilih MK dan lihat tugas</div></html>");
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
        tableModel = new DefaultTableModel(new Object[]{"Judul", "Deskripsi", "Deadline"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        tableTugas = new JTable(tableModel);
        tableTugas.setRowHeight(28);
        tableTugas.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tableTugas.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(JLabel.CENTER);
        tableTugas.getColumnModel().getColumn(2).setCellRenderer(center);

        JScrollPane scroll = new JScrollPane(tableTugas);
        scroll.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        add(scroll, BorderLayout.CENTER);

        // ========================= BUTTONS (Tambah & Edit) =========================
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(Color.WHITE);

        btnTambahTugas = new JButton("Tambah Tugas");
        btnTambahTugas.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnTambahTugas.setBackground(new Color(255, 140, 0));
        btnTambahTugas.setForeground(Color.WHITE);
        btnTambahTugas.setFocusPainted(false);

        btnEditTugas = new JButton("Edit Tugas");
        btnEditTugas.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnEditTugas.setBackground(new Color(0, 153, 255));
        btnEditTugas.setForeground(Color.WHITE);
        btnEditTugas.setFocusPainted(false);

        btnHapusTugas = new JButton("Hapus Tugas");
        btnHapusTugas.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnHapusTugas.setBackground(new Color(220, 53, 69));
        btnHapusTugas.setForeground(Color.WHITE);
        btnHapusTugas.setFocusPainted(false);

        bottomPanel.add(btnTambahTugas);
        bottomPanel.add(btnEditTugas);
        bottomPanel.add(btnHapusTugas);

        bottomPanel.add(btnTambahTugas);
        bottomPanel.add(btnEditTugas);

        add(bottomPanel, BorderLayout.SOUTH);

        // ========================= EVENTS =========================
        btnLoad.addActionListener(e -> loadTugas());
        btnTambahTugas.addActionListener(e -> new PopupTambahTugas());
        btnEditTugas.addActionListener(e -> openEditPopup());
        btnHapusTugas.addActionListener(e -> hapusTugas());

        setVisible(true);
    }

    private void hapusTugas() {
        int row = tableTugas.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Pilih tugas yang ingin dihapus!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Yakin ingin menghapus tugas ini?",
                "Konfirmasi Hapus",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) return;

        String judul = tableModel.getValueAt(row, 0).toString();
        String kodeMk = cbKodeMK.getSelectedItem().toString();
        kodeMk = kodeMk.substring(1, kodeMk.indexOf(")"));

        try {
            PreparedStatement stmt = conn.prepareStatement(
                    "DELETE FROM tugas WHERE kode_mk=? AND judul=?"
            );
            stmt.setString(1, kodeMk);
            stmt.setString(2, judul);
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Tugas berhasil dihapus!");
            loadTugas();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    // ========================= LOAD MK =========================
    private void loadKodeMK() {
        try {
            PreparedStatement stmt = conn.prepareStatement("SELECT kode_mk, nama_mk FROM matakuliah WHERE nidn_dosen = ?");
            stmt.setString(1, nidn);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                cbKodeMK.addItem("(" + rs.getString("kode_mk") + ") " + rs.getString("nama_mk"));
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error Load MK: " + e.getMessage());
        }
    }

    // ========================= LOAD TUGAS =========================
    private void loadTugas() {
        tableModel.setRowCount(0);

        if (cbKodeMK.getSelectedItem() == null) return;

        String kodeMk = cbKodeMK.getSelectedItem().toString();
        kodeMk = kodeMk.substring(1, kodeMk.indexOf(")"));

        try {
            PreparedStatement stmt = conn.prepareStatement(
                    "SELECT judul, deskripsi, deadline FROM tugas WHERE kode_mk = ?"
            );
            stmt.setString(1, kodeMk);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getString("judul"),
                        rs.getString("deskripsi"),
                        rs.getString("deadline")
                });
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error Load Tugas: " + e.getMessage());
        }
    }

    // ========================= OPEN EDIT POPUP =========================
    private void openEditPopup() {
        int row = tableTugas.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Pilih tugas yang ingin diedit!");
            return;
        }

        String judul = tableModel.getValueAt(row, 0).toString();
        String desk = tableModel.getValueAt(row, 1).toString();
        String deadline = tableModel.getValueAt(row, 2).toString();

        new PopupEditTugas(judul, desk, deadline);
    }

    // ========================================================================
    // ========================= POPUP TAMBAH =========================
    // ========================================================================
    class PopupTambahTugas extends JDialog {

        public PopupTambahTugas() {
            setTitle("Tambah Tugas");
            setSize(750, 550);
            setLocationRelativeTo(MenuTambahTugas.this);
            setModal(true);
            setLayout(new BorderLayout());

            // ===================== TITLE =====================
            JLabel lblTitle = new JLabel("Tambah Tugas", JLabel.CENTER);
            lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 32));
            lblTitle.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
            add(lblTitle, BorderLayout.NORTH);

            // ===================== MAIN PANEL =====================
            JPanel main = new JPanel();
            main.setBackground(new Color(242, 242, 242));
            main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
            main.setBorder(BorderFactory.createEmptyBorder(20, 60, 20, 60));

            // Font Setup
            Font labelFont = new Font("Segoe UI", Font.BOLD, 18);
            Font fieldFont = new Font("Segoe UI", Font.PLAIN, 16);

            // ===================== JUDUL =====================
            JLabel lblJudul = new JLabel("Judul");
            lblJudul.setFont(labelFont);
            lblJudul.setAlignmentX(Component.LEFT_ALIGNMENT);   // << penting

            JTextField tfJudul = new JTextField();
            tfJudul.setFont(fieldFont);
            tfJudul.setPreferredSize(new Dimension(400, 40));
            tfJudul.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            tfJudul.setAlignmentX(Component.LEFT_ALIGNMENT);    // << sejajarkan input

            // ===================== DESKRIPSI =====================
            JLabel lblDesk = new JLabel("Deskripsi");
            lblDesk.setFont(labelFont);
            lblDesk.setAlignmentX(Component.LEFT_ALIGNMENT);

            JTextArea tfDesk = new JTextArea();
            tfDesk.setFont(fieldFont);
            tfDesk.setLineWrap(true);
            tfDesk.setWrapStyleWord(true);

            JScrollPane spDesk = new JScrollPane(tfDesk);
            spDesk.setPreferredSize(new Dimension(400, 150));
            spDesk.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
            spDesk.setAlignmentX(Component.LEFT_ALIGNMENT);

            // ===================== DEADLINE =====================
            JLabel lblDeadline = new JLabel("Deadline");
            lblDeadline.setFont(labelFont);
            lblDeadline.setAlignmentX(Component.LEFT_ALIGNMENT);

            JTextField tfDeadline = new JTextField();
            tfDeadline.setFont(fieldFont);
            tfDeadline.setPreferredSize(new Dimension(400, 40));
            tfDeadline.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            tfDeadline.setAlignmentX(Component.LEFT_ALIGNMENT);

            // ===================== ADD TO PANEL =====================
            main.add(lblJudul);
            main.add(Box.createVerticalStrut(8));
            main.add(tfJudul);
            main.add(Box.createVerticalStrut(18));

            main.add(lblDesk);
            main.add(Box.createVerticalStrut(8));
            main.add(spDesk);
            main.add(Box.createVerticalStrut(18));

            main.add(lblDeadline);
            main.add(Box.createVerticalStrut(8));
            main.add(tfDeadline);
            main.add(Box.createVerticalStrut(30));

            add(main, BorderLayout.CENTER);

            // ===================== SAVE BUTTON =====================
            JButton btnSave = new JButton("Simpan");
            btnSave.setFont(new Font("Segoe UI", Font.BOLD, 20));
            btnSave.setForeground(Color.WHITE);
            btnSave.setBackground(new Color(255, 140, 0));
            btnSave.setFocusPainted(false);
            btnSave.setPreferredSize(new Dimension(250, 55));

            JPanel bottom = new JPanel();
            bottom.setBackground(new Color(242, 242, 242));
            bottom.add(btnSave);

            add(bottom, BorderLayout.SOUTH);

            // ===================== ACTION =====================
            btnSave.addActionListener(e -> {
                try {
                    String kodeMk = cbKodeMK.getSelectedItem().toString();
                    kodeMk = kodeMk.substring(1, kodeMk.indexOf(")"));

                    PreparedStatement stmt = conn.prepareStatement(
                            "INSERT INTO tugas VALUES (NULL, ?, ?, ?, ?)"
                    );
                    stmt.setString(1, kodeMk);
                    stmt.setString(2, tfJudul.getText());
                    stmt.setString(3, tfDesk.getText());
                    stmt.setString(4, tfDeadline.getText());
                    stmt.executeUpdate();

                    JOptionPane.showMessageDialog(this, "Tugas berhasil ditambahkan!");
                    loadTugas();
                    dispose();

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
                }
            });

            setVisible(true);
        }
    }

    // ========================================================================
    // ========================= POPUP EDIT =========================
    // ========================================================================
    class PopupEditTugas extends JDialog {

        public PopupEditTugas(String j, String d, String dl) {
            setTitle("Edit Tugas");
            setSize(750, 550);
            setLocationRelativeTo(MenuTambahTugas.this);
            setModal(true);
            setLayout(new BorderLayout());

            // ===================== TITLE =====================
            JLabel lblTitle = new JLabel("Edit Tugas", JLabel.CENTER);
            lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 32));
            lblTitle.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
            add(lblTitle, BorderLayout.NORTH);

            // ===================== MAIN PANEL =====================
            JPanel main = new JPanel();
            main.setBackground(new Color(242, 242, 242));
            main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
            main.setBorder(BorderFactory.createEmptyBorder(20, 60, 20, 60));

            Font labelFont = new Font("Segoe UI", Font.BOLD, 18);
            Font fieldFont = new Font("Segoe UI", Font.PLAIN, 16);

            // ===================== FIELD: JUDUL =====================
            JLabel lblJudul = new JLabel("Judul");
            lblJudul.setFont(labelFont);
            lblJudul.setAlignmentX(Component.LEFT_ALIGNMENT);

            JTextField tfJudul = new JTextField(j);
            tfJudul.setFont(fieldFont);
            tfJudul.setPreferredSize(new Dimension(400, 40));
            tfJudul.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            tfJudul.setAlignmentX(Component.LEFT_ALIGNMENT);

            // ===================== DESKRIPSI =====================
            JLabel lblDesk = new JLabel("Deskripsi");
            lblDesk.setFont(labelFont);
            lblDesk.setAlignmentX(Component.LEFT_ALIGNMENT);

            JTextArea tfDesk = new JTextArea(d);
            tfDesk.setFont(fieldFont);
            tfDesk.setLineWrap(true);
            tfDesk.setWrapStyleWord(true);

            JScrollPane spDesk = new JScrollPane(tfDesk);
            spDesk.setPreferredSize(new Dimension(400, 150));
            spDesk.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
            spDesk.setAlignmentX(Component.LEFT_ALIGNMENT);

            // ===================== DEADLINE =====================
            JLabel lblDeadline = new JLabel("Deadline");
            lblDeadline.setFont(labelFont);
            lblDeadline.setAlignmentX(Component.LEFT_ALIGNMENT);

            JTextField tfDeadline = new JTextField(dl);
            tfDeadline.setFont(fieldFont);
            tfDeadline.setPreferredSize(new Dimension(400, 40));
            tfDeadline.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            tfDeadline.setAlignmentX(Component.LEFT_ALIGNMENT);

            // Masukkan ke panel
            main.add(lblJudul);
            main.add(Box.createVerticalStrut(8));
            main.add(tfJudul);
            main.add(Box.createVerticalStrut(18));

            main.add(lblDesk);
            main.add(Box.createVerticalStrut(8));
            main.add(spDesk);
            main.add(Box.createVerticalStrut(18));

            main.add(lblDeadline);
            main.add(Box.createVerticalStrut(8));
            main.add(tfDeadline);
            main.add(Box.createVerticalStrut(30));

            add(main, BorderLayout.CENTER);

            // ===================== BUTTON =====================
            JButton btnUpdate = new JButton("Update");
            btnUpdate.setFont(new Font("Segoe UI", Font.BOLD, 20));
            btnUpdate.setForeground(Color.WHITE);
            btnUpdate.setBackground(new Color(255, 140, 0));
            btnUpdate.setFocusPainted(false);
            btnUpdate.setPreferredSize(new Dimension(250, 55));

            JPanel bottom = new JPanel();
            bottom.setBackground(new Color(242, 242, 242));
            bottom.add(btnUpdate);

            add(bottom, BorderLayout.SOUTH);

            // ===================== ACTION =====================
            btnUpdate.addActionListener(e -> {
                try {
                    String kodeMk = cbKodeMK.getSelectedItem().toString();
                    kodeMk = kodeMk.substring(1, kodeMk.indexOf(")"));

                    PreparedStatement stmt = conn.prepareStatement(
                            "UPDATE tugas SET judul=?, deskripsi=?, deadline=? WHERE kode_mk=? AND judul=?"
                    );
                    stmt.setString(1, tfJudul.getText());
                    stmt.setString(2, tfDesk.getText());
                    stmt.setString(3, tfDeadline.getText());
                    stmt.setString(4, kodeMk);
                    stmt.setString(5, j);

                    stmt.executeUpdate();

                    JOptionPane.showMessageDialog(this, "Tugas berhasil diedit!");
                    loadTugas();
                    dispose();

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
                }
            });

            setVisible(true);
        }
    }

    // ========================= STYLED BUTTON =========================
    private JButton styledSmallButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(80, 80, 80), 2, true),
                BorderFactory.createEmptyBorder(8, 14, 8, 14)
        ));
        return btn;
    }

    // ========================= GRADIENT BACKGROUND =========================
    static class GradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            int h = getHeight();

            GradientPaint gp = new GradientPaint(
                    0, 0, new Color(255, 140, 0),
                    0, h, new Color(255, 199, 153)
            );

            g2.setPaint(gp);
            g2.fillRect(0, 0, getWidth(), h);
            g2.dispose();
        }
    }
}
