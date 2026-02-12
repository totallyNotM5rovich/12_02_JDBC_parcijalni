import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class ListaPolaznika {
    private final ArrayList<Polaznik> polaznici;

    public ListaPolaznika() {
        this.polaznici = new ArrayList<>();

        ucitajPolaznike();
    }

    private void ucitajPolaznike() {
        DataSource source = DBconnect.createDataSource();
        try(Connection connection = source.getConnection()) {
            Statement statement = connection.createStatement();

            ResultSet rs = statement.executeQuery("SELECT * FROM Polaznik");
            while(rs.next()) {
                polaznici.add(new Polaznik(rs.getInt("PolaznikID"), rs.getString("Ime"), rs.getString("Prezime")));
            }

            rs.close();
            statement.close();

        } catch (SQLException e) {
            System.out.println("Greska prilikom spajanja!");
            e.printStackTrace();
        }
    }

    public void dodajPolaznika(String ime, String prezime) {
        DataSource source = DBconnect.createDataSource();
        try(Connection connection = source.getConnection()) {
            Statement statement = connection.createStatement();

            String sqlUpit = String.format("INSERT INTO Polaznik VALUES ('%s', '%s')", ime, prezime);

            int red = statement.executeUpdate(sqlUpit);

            if(red == 1) {
                System.out.printf("Uspjesno evidentiran novi polaznik: %s\r\n", String.format("%s %s", ime, prezime));
            } else {
                System.out.println("Greska prilikom evidentiranja novog polaznika!");
            }

            statement.close();
        } catch (SQLException e) {
            System.out.println("Greska prilikom spajanja!");
            e.printStackTrace();
        }

        polaznici.clear();
        ucitajPolaznike();
    }

    public void ispisPolaznika() {
        for (Polaznik polaznik : polaznici) {
            polaznik.ispisPolaznika();
        }
    }

    public boolean validanID(int id) {
        for (Polaznik polaznik : polaznici) {
            if (polaznik.getId() == id) { return true;}
        }
        return false;
    }

}
