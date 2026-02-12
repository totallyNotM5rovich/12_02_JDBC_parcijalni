import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class ListaPO {
    private final ArrayList<PO> programi;

    public ListaPO() {
        this.programi = new ArrayList<>();

        ucitajPrograme();
    }

    private void ucitajPrograme() {
        DataSource source = DBconnect.createDataSource();
        try(Connection connection = source.getConnection()) {
            Statement statement = connection.createStatement();

            ResultSet rs = statement.executeQuery("SELECT * FROM ProgramObrazovanja");
            while(rs.next()) {
                programi.add(new PO(rs.getInt("ProgramObrazovanjaID"), rs.getString("Naziv"), rs.getInt("CSVET")));
            }

            rs.close();
            statement.close();

        } catch (SQLException e) {
            System.out.println("Greska prilikom spajanja!");
            e.printStackTrace();
        }
    }

    public void dodajPO(String naziv, int csvet) {
        DataSource source = DBconnect.createDataSource();
        try(Connection connection = source.getConnection()) {
            Statement statement = connection.createStatement();

            String sqlUpit = String.format("INSERT INTO ProgramObrazovanja VALUES ('%s', '%d')", naziv, csvet);

            int red = statement.executeUpdate(sqlUpit);

            if(red == 1) {
                System.out.printf("Uspjesno evidentiran novi PO: %s\r\n", naziv);
            } else {
                System.out.println("Greska prilikom evidentiranja novog PO!");
            }

            statement.close();
        } catch (SQLException e) {
            System.out.println("Greska prilikom spajanja!");
            e.printStackTrace();
        }

        programi.clear();
        ucitajPrograme();
    }

    public void ispisPO() {
        for (PO po : programi) {
            po.ispisPO();
        }
    }

    public boolean validanID(int id) {
        for (PO po : programi) {
            if (po.getId() == id) { return true;}
        }
        return false;
    }
}
