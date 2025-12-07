package Admin;

public class MataKuliah {
    private String kodeMk;
    private String namaMk;
    private int sks;
    private String jadwal;

    public MataKuliah() {
    }

    public MataKuliah(String kodeMk, String namaMk, int sks, String jadwal) {
        this.kodeMk = kodeMk;
        this.namaMk = namaMk;
        this.sks = sks;
        this.jadwal = jadwal;
    }

    public String getKodeMk() {
        return kodeMk;
    }

    public void setKodeMk(String kodeMk) {
        this.kodeMk = kodeMk;
    }

    public String getNamaMk() {
        return namaMk;
    }

    public void setNamaMk(String namaMk) {
        this.namaMk = namaMk;
    }

    public int getSks() {
        return sks;
    }

    public void setSks(int sks) {
        this.sks = sks;
    }

    public String getJadwal() {
        return jadwal;
    }

    public void setJadwal(String jadwal) {
        this.jadwal = jadwal;
    }

    @Override
    public String toString() {
        return kodeMk + " - " + namaMk + " (" + sks + " SKS) | " + jadwal;
    }
}
