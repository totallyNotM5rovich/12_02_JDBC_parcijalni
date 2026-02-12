public class PO {
    private int id;
    private String naziv;
    private int csvet;

    public PO(int id, String naziv, int csvet) {
        this.id = id;
        this.naziv = naziv;
        this.csvet = csvet;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNaziv() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    public int getCsvet() {
        return csvet;
    }

    public void setCsvet(int csvet) {
        this.csvet = csvet;
    }

    @Override
    public String toString() {
        return String.format(" ID: %d, %s", id, naziv);
    }

    public void ispisPO() {
        System.out.printf(" ID: %d, %s\r\n", id, naziv);
    }
}
