package GUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

import Admin.*;

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
        leftPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // TOP: Judul + sapaan
        JPanel leftTop = new JPanel();
        leftTop.setOpaque(false);
        leftTop.setLayout(new BoxLayout(leftTop, BoxLayout.Y_AXIS));

        JLabel lblTitle = new JLabel(
                "<html><span style='font-size:36pt; font-weight:600;'>Dashboard<br/>Admin</span></html>");
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

        // ========== RIGHT PANEL (3 tombol) ==========
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(10, 10, 10, 10);

        JButton btnKelolaMahasiswa = createStyledButton("Kelola Data Mahasiswa");
        JButton btnKelolaDosen = createStyledButton("Kelola Data Dosen");
        JButton btnKelolaMatakuliah = createStyledButton("Kelola Data Mata Kuliah");

        gbc.gridx = 0;
        gbc.gridy = 0;
        rightPanel.add(btnKelolaMahasiswa, gbc);

        gbc.gridy = 1;
        rightPanel.add(btnKelolaDosen, gbc);

        gbc.gridy = 2;
        rightPanel.add(btnKelolaMatakuliah, gbc);

        add(rightPanel, BorderLayout.CENTER);

        // ========== ACTION LISTENERS ==========
        btnLogout.addActionListener(e -> dispose());

        btnKelolaMahasiswa.addActionListener(e -> new KelolaMahasiswaFrame());
        btnKelolaDosen.addActionListener(e -> new KelolaDosenFrame());
        btnKelolaMatakuliah.addActionListener(e -> new KelolaMatakuliahFrame());

        // finalize
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // ========== Button Styling (sama dengan MenuMahasiswa/MenuDosen) ==========
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

    // ========== Gradient Panel ==========
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

    // =====================================================================
    // ===============   FRAME KELOLA MAHASISWA   ==========================
    // =====================================================================
    static class KelolaMahasiswaFrame extends JFrame {

        private DefaultTableModel model;
        private JTable table;
        private MahasiswaDAO dao = new MahasiswaDAO();

        KelolaMahasiswaFrame() {
            super("Kelola Data Mahasiswa");
            setSize(750, 500);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            setLocationRelativeTo(null);
            setLayout(new BorderLayout());

            model = new DefaultTableModel(new Object[]{"NIM", "Nama", "Jurusan"}, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            table = new JTable(model);
            add(new JScrollPane(table), BorderLayout.CENTER);

            JPanel buttonPanel = new JPanel();
            JButton btnTambah = new JButton("Tambah");
            JButton btnEdit = new JButton("Edit");
            JButton btnHapus = new JButton("Hapus");
            JButton btnRefresh = new JButton("Refresh");
            buttonPanel.add(btnTambah);
            buttonPanel.add(btnEdit);
            buttonPanel.add(btnHapus);
            buttonPanel.add(btnRefresh);
            add(buttonPanel, BorderLayout.SOUTH);

            btnTambah.addActionListener(e -> tambahMahasiswa());
            btnEdit.addActionListener(e -> editMahasiswa());
            btnHapus.addActionListener(e -> hapusMahasiswa());
            btnRefresh.addActionListener(e -> muatData());

            muatData();
            setVisible(true);
        }

        private void muatData() {
            model.setRowCount(0);
            try {
                List<Mahasiswa> list = dao.getAllMahasiswa();
                for (Mahasiswa m : list) {
                    model.addRow(new Object[]{m.getNim(), m.getNama(), m.getJurusan()});
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this,
                        "Gagal memuat data: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        private void tambahMahasiswa() {
            JTextField tfNim = new JTextField();
            JTextField tfNama = new JTextField();
            JTextField tfJurusan = new JTextField();

            Object[] message = {
                    "NIM:", tfNim,
                    "Nama:", tfNama,
                    "Jurusan:", tfJurusan
            };

            int option = JOptionPane.showConfirmDialog(this, message,
                    "Tambah Mahasiswa", JOptionPane.OK_CANCEL_OPTION);

            if (option == JOptionPane.OK_OPTION) {
                Mahasiswa m = new Mahasiswa(
                        tfNim.getText().trim(),
                        tfNama.getText().trim(),
                        tfJurusan.getText().trim()
                );
                try {
                    dao.insertMahasiswa(m);
                    muatData();
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(this,
                            "Gagal menambah data: " + e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }

        private void editMahasiswa() {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this,
                        "Pilih data yang akan diedit.",
                        "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String nim = (String) model.getValueAt(row, 0);
            String namaLama = (String) model.getValueAt(row, 1);
            String jurusanLama = (String) model.getValueAt(row, 2);

            JTextField tfNama = new JTextField(namaLama);
            JTextField tfJurusan = new JTextField(jurusanLama);

            Object[] message = {
                    "NIM: " + nim,
                    "Nama baru:", tfNama,
                    "Jurusan baru:", tfJurusan
            };

            int option = JOptionPane.showConfirmDialog(this, message,
                    "Edit Mahasiswa", JOptionPane.OK_CANCEL_OPTION);

            if (option == JOptionPane.OK_OPTION) {
                Mahasiswa m = new Mahasiswa(
                        nim,
                        tfNama.getText().trim(),
                        tfJurusan.getText().trim()
                );
                try {
                    dao.updateMahasiswa(m);
                    muatData();
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(this,
                            "Gagal mengubah data: " + e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }

        private void hapusMahasiswa() {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this,
                        "Pilih data yang akan dihapus.",
                        "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String nim = (String) model.getValueAt(row, 0);
            int konfirmasi = JOptionPane.showConfirmDialog(this,
                    "Yakin ingin menghapus data dengan NIM " + nim + "?",
                    "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);

            if (konfirmasi == JOptionPane.YES_OPTION) {
                try {
                    dao.deleteMahasiswa(nim);
                    muatData();
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(this,
                            "Gagal menghapus data: " + e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    // =====================================================================
    // ===============   FRAME KELOLA DOSEN   ===============================
    // =====================================================================
    static class KelolaDosenFrame extends JFrame {

        private DefaultTableModel model;
        private JTable table;
        private DosenDAO dao = new DosenDAO();

        KelolaDosenFrame() {
            super("Kelola Data Dosen");
            setSize(750, 500);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            setLocationRelativeTo(null);
            setLayout(new BorderLayout());

            model = new DefaultTableModel(new Object[]{"NIDN", "Nama", "Keahlian"}, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            table = new JTable(model);
            add(new JScrollPane(table), BorderLayout.CENTER);

            JPanel buttonPanel = new JPanel();
            JButton btnTambah = new JButton("Tambah");
            JButton btnEdit = new JButton("Edit");
            JButton btnHapus = new JButton("Hapus");
            JButton btnRefresh = new JButton("Refresh");
            buttonPanel.add(btnTambah);
            buttonPanel.add(btnEdit);
            buttonPanel.add(btnHapus);
            buttonPanel.add(btnRefresh);
            add(buttonPanel, BorderLayout.SOUTH);

            btnTambah.addActionListener(e -> tambahDosen());
            btnEdit.addActionListener(e -> editDosen());
            btnHapus.addActionListener(e -> hapusDosen());
            btnRefresh.addActionListener(e -> muatData());

            muatData();
            setVisible(true);
        }

        private void muatData() {
            model.setRowCount(0);
            try {
                List<Dosen> list = dao.getAllDosen();
                for (Dosen d : list) {
                    model.addRow(new Object[]{d.getNidn(), d.getNama(), d.getKeahlian()});
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this,
                        "Gagal memuat data: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        private void tambahDosen() {
            JTextField tfNidn = new JTextField();
            JTextField tfNama = new JTextField();
            JTextField tfKeahlian = new JTextField();

            Object[] message = {
                    "NIDN:", tfNidn,
                    "Nama:", tfNama,
                    "Keahlian:", tfKeahlian
            };

            int option = JOptionPane.showConfirmDialog(this, message,
                    "Tambah Dosen", JOptionPane.OK_CANCEL_OPTION);

            if (option == JOptionPane.OK_OPTION) {
                Dosen d = new Dosen(
                        tfNidn.getText().trim(),
                        tfNama.getText().trim(),
                        tfKeahlian.getText().trim()
                );
                try {
                    dao.insertDosen(d);
                    muatData();
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(this,
                            "Gagal menambah data: " + e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }

        private void editDosen() {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this,
                        "Pilih data yang akan diedit.",
                        "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String nidn = (String) model.getValueAt(row, 0);
            String namaLama = (String) model.getValueAt(row, 1);
            String keahlianLama = (String) model.getValueAt(row, 2);

            JTextField tfNama = new JTextField(namaLama);
            JTextField tfKeahlian = new JTextField(keahlianLama);

            Object[] message = {
                    "NIDN: " + nidn,
                    "Nama baru:", tfNama,
                    "Keahlian baru:", tfKeahlian
            };

            int option = JOptionPane.showConfirmDialog(this, message,
                    "Edit Dosen", JOptionPane.OK_CANCEL_OPTION);

            if (option == JOptionPane.OK_OPTION) {
                Dosen d = new Dosen(
                        nidn,
                        tfNama.getText().trim(),
                        tfKeahlian.getText().trim()
                );
                try {
                    dao.updateDosen(d);
                    muatData();
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(this,
                            "Gagal mengubah data: " + e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }

        private void hapusDosen() {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this,
                        "Pilih data yang akan dihapus.",
                        "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String nidn = (String) model.getValueAt(row, 0);
            int konfirmasi = JOptionPane.showConfirmDialog(this,
                    "Yakin ingin menghapus data dengan NIDN " + nidn + "?",
                    "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);

            if (konfirmasi == JOptionPane.YES_OPTION) {
                try {
                    dao.deleteDosen(nidn);
                    muatData();
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(this,
                            "Gagal menghapus data: " + e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    // =====================================================================
    // ===============   FRAME KELOLA MATA KULIAH   ========================
    // =====================================================================
    static class KelolaMatakuliahFrame extends JFrame {

        private DefaultTableModel model;
        private JTable table;
        private MataKuliahDAO dao = new MataKuliahDAO();

        KelolaMatakuliahFrame() {
            super("Kelola Data Mata Kuliah");
            setSize(800, 500);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            setLocationRelativeTo(null);
            setLayout(new BorderLayout());

            model = new DefaultTableModel(new Object[]{"Kode MK", "Nama MK", "SKS", "Jadwal"}, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            table = new JTable(model);
            add(new JScrollPane(table), BorderLayout.CENTER);

            JPanel buttonPanel = new JPanel();
            JButton btnTambah = new JButton("Tambah");
            JButton btnEdit = new JButton("Edit");
            JButton btnHapus = new JButton("Hapus");
            JButton btnRefresh = new JButton("Refresh");
            buttonPanel.add(btnTambah);
            buttonPanel.add(btnEdit);
            buttonPanel.add(btnHapus);
            buttonPanel.add(btnRefresh);
            add(buttonPanel, BorderLayout.SOUTH);

            btnTambah.addActionListener(e -> tambahMatakuliah());
            btnEdit.addActionListener(e -> editMatakuliah());
            btnHapus.addActionListener(e -> hapusMatakuliah());
            btnRefresh.addActionListener(e -> muatData());

            muatData();
            setVisible(true);
        }

        private void muatData() {
            model.setRowCount(0);
            try {
                List<MataKuliah> list = dao.getAllMataKuliah();
                for (MataKuliah mk : list) {
                    model.addRow(new Object[]{mk.getKodeMk(), mk.getNamaMk(), mk.getSks(), mk.getJadwal()});
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this,
                        "Gagal memuat data: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        private void tambahMatakuliah() {
            JTextField tfKode = new JTextField();
            JTextField tfNama = new JTextField();
            JTextField tfSks = new JTextField();
            JTextField tfJadwal = new JTextField();

            Object[] message = {
                    "Kode MK:", tfKode,
                    "Nama MK:", tfNama,
                    "SKS:", tfSks,
                    "Jadwal:", tfJadwal
            };

            int option = JOptionPane.showConfirmDialog(this, message,
                    "Tambah Mata Kuliah", JOptionPane.OK_CANCEL_OPTION);

            if (option == JOptionPane.OK_OPTION) {
                int sks;
                try {
                    sks = Integer.parseInt(tfSks.getText().trim());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this,
                            "SKS harus berupa angka.",
                            "Input tidak valid", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                MataKuliah mk = new MataKuliah(
                        tfKode.getText().trim(),
                        tfNama.getText().trim(),
                        sks,
                        tfJadwal.getText().trim()
                );
                try {
                    dao.insertMataKuliah(mk);
                    muatData();
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(this,
                            "Gagal menambah data: " + e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }

        private void editMatakuliah() {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this,
                        "Pilih data yang akan diedit.",
                        "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String kode = (String) model.getValueAt(row, 0);
            String namaLama = (String) model.getValueAt(row, 1);
            int sksLama = (Integer) model.getValueAt(row, 2);
            String jadwalLama = (String) model.getValueAt(row, 3);

            JTextField tfNama = new JTextField(namaLama);
            JTextField tfSks = new JTextField(String.valueOf(sksLama));
            JTextField tfJadwal = new JTextField(jadwalLama);

            Object[] message = {
                    "Kode MK: " + kode,
                    "Nama baru:", tfNama,
                    "SKS baru:", tfSks,
                    "Jadwal baru:", tfJadwal
            };

            int option = JOptionPane.showConfirmDialog(this, message,
                    "Edit Mata Kuliah", JOptionPane.OK_CANCEL_OPTION);

            if (option == JOptionPane.OK_OPTION) {
                int sksBaru;
                try {
                    sksBaru = Integer.parseInt(tfSks.getText().trim());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this,
                            "SKS harus berupa angka.",
                            "Input tidak valid", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                MataKuliah mk = new MataKuliah(
                        kode,
                        tfNama.getText().trim(),
                        sksBaru,
                        tfJadwal.getText().trim()
                );
                try {
                    dao.updateMataKuliah(mk);
                    muatData();
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(this,
                            "Gagal mengubah data: " + e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }

        private void hapusMatakuliah() {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this,
                        "Pilih data yang akan dihapus.",
                        "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String kode = (String) model.getValueAt(row, 0);
            int konfirmasi = JOptionPane.showConfirmDialog(this,
                    "Yakin ingin menghapus data dengan Kode MK " + kode + "?",
                    "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);

            if (konfirmasi == JOptionPane.YES_OPTION) {
                try {
                    dao.deleteMataKuliah(kode);
                    muatData();
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(this,
                            "Gagal menghapus data: " + e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    // Testing manual
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MenuAdmin("Administrator"));
    }
}
