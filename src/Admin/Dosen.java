package Admin;

public class Dosen {
    private String nidn;
    private String nama;
    private String keahlian;

    public Dosen() {
    }

    public Dosen(String nidn, String nama, String keahlian) {
        this.nidn = nidn;
        this.nama = nama;
        this.keahlian = keahlian;
    }

    public String getNidn() {
        return nidn;
    }

    public void setNidn(String nidn) {
        this.nidn = nidn;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getKeahlian() {
        return keahlian;
    }

    public void setKeahlian(String keahlian) {
        this.keahlian = keahlian;
    }

    @Override
    public String toString() {
        return nidn + " - " + nama + " [" + keahlian + "]";
    }
}
