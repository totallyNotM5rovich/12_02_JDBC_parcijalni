import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;

public class MenuNavigacija {
    static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

    public static int odabirOpcije(int max) {
        boolean validnaOpcija = false;
        int opcija = 1;
        do {
            try {
                String unos = br.readLine().trim();
                opcija = Integer.parseInt(unos);
                if (opcija < 1 || opcija > max) {
                    throw new IllegalArgumentException(String.format("Neispravan unos! Odaberite jednu od ponudjenih opcija (1-%d)", max));
                }
                validnaOpcija = true;
            } catch (NumberFormatException e) {
                System.out.printf("Neispravan unos! Ocekivani unos je broj (1-%d):\r\n", max);
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            } catch (Exception e) {
                System.out.println("Neispravan unos!");
            }
        } while (!validnaOpcija);
        return opcija;
    }

    public static void unosPolaznika(ListaPolaznika listaPolaznika) {
        String ime = null;
        String prezime = null;

        dodavanjePolaznikaLoop:
        while(true) {

            System.out.printf(" 1. Unesite ime polaznika: %s\r\n", ((ime == null) ? "NEDEFINIRANO" : ime));
            System.out.printf(" 2. Unesite prezime polaznika: %s\r\n", ((prezime == null) ? "NEDEFINIRANO" : prezime));
            System.out.println(" 3. Odustani");
            if(ime != null && prezime != null) {
                System.out.println(" 4. Dodaj polaznika");
            }

            System.out.println("Odaberite jednu od ponudjenih akcija (1-4):");
            int odabranaOpcija = MenuNavigacija.odabirOpcije(4);

            switch (odabranaOpcija) {
                case 1:
                    System.out.println("Unesite ime polaznika:");
                    ime = unosImePrezimeNaziv("Ime");
                    break;
                case 2:
                    System.out.println("Unesite prezime polaznika:");
                    prezime = unosImePrezimeNaziv("Prezime");
                    break;
                case 3:
                    break dodavanjePolaznikaLoop;
                case 4:
                    if(ime != null && prezime != null) {
                        listaPolaznika.dodajPolaznika(ime, prezime);
                    } else {
                        System.out.println("Neuspjesno dodavanje novog polaznika! Niste unesli sve potrebne podatke!");
                        break;
                    }
                    break dodavanjePolaznikaLoop;
            }

        }
    }

    public static void unosPO(ListaPO listaPO) {
        String naziv = null;
        int csvet = -1;

        dodavanjePolaznikaLoop:
        while(true) {

            System.out.printf(" 1. Unesite naziv PO: %s\r\n", ((naziv == null) ? "NEDEFINIRAN" : naziv));
            System.out.printf(" 2. Unesite broj CSVET: %s\r\n", ((csvet <= 0) ? "NEDEFINIRAN" : String.format("%d",csvet)));
            System.out.println(" 3. Odustani");
            if(naziv != null && csvet > 0) {
                System.out.println(" 4. Dodaj PO");
            }

            System.out.println("Odaberite jednu od ponudjenih akcija (1-4):");
            int odabranaOpcija = MenuNavigacija.odabirOpcije(4);

            switch (odabranaOpcija) {
                case 1:
                    System.out.println("Unesite naziv PO:");
                    naziv = unosImePrezimeNaziv("Naziv PO");
                    break;
                case 2:
                    System.out.println("Unesite broj CSVET bodova:");
                    csvet = MenuNavigacija.unosCSVET();
                    break;
                case 3:
                    break dodavanjePolaznikaLoop;
                case 4:
                    if(naziv != null && csvet > 0) {
                        listaPO.dodajPO(naziv, csvet);
                    } else {
                        System.out.println("Neuspjesno dodavanje novog PO! Niste unesli sve potrebne podatke!");
                        break;
                    }
                    break dodavanjePolaznikaLoop;
            }

        }

    }

    public static void upisPolaznikaNaPO(ListaPolaznika listaPolaznika, ListaPO listaPO) {
        System.out.println("Evidentirani polaznici:");
        listaPolaznika.ispisPolaznika();
        System.out.println("Odaberite polaznika upisivanjem pridruzenog ID-a:");
        int idPolaznik = odabirID(listaPolaznika);

        System.out.println("Evidentirani PO:");
        listaPO.ispisPO();
        System.out.println("Odaberite polaznika upisivanjem pridruzenog ID-a:");
        int idPO = odabirID(listaPO);

        upisSQL(idPolaznik, idPO);
    }

    public static void prebacivanjePolaznika(ListaPolaznika listaPolaznika, ListaPO listaPO) throws IOException {
        System.out.println("Evidentirani polaznici:");
        listaPolaznika.ispisPolaznika();
        System.out.println("Odaberite polaznika upisivanjem pridruzenog ID-a:");
        int idPolaznik = odabirID(listaPolaznika);

        DataSource source = DBconnect.createDataSource();
        try(Connection connection = source.getConnection()) {
            CallableStatement stmt = connection.prepareCall("{CALL DohvatiUpisanePO(?)}");
            stmt.setInt(1, idPolaznik);
            ResultSet rs = stmt.executeQuery();
            if (!rs.next() ) {
                System.out.println("Odabrani polaznik nije upisan ni na jedan PO!");
                rs.close();
                stmt.close();
                return;
            } else {
                do {
                    System.out.printf("ID: %d, %s\r\n", rs.getInt("ID"), rs.getString("Naziv"));
                } while (rs.next());
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.err.println("Greška pri spajanju na bazu!");
            e.printStackTrace();
        }

        System.out.println("Odaberite tecaj koji zelite zamjeniti upisivanjem pridruzenog ID-a:");
        int upisID = Integer.parseInt(br.readLine().trim());

        listaPO.ispisPO();
        System.out.println("Odaberite tecaj sa kojim zelite zamjeniti upisivanjem pridruzenog ID-a:");
        int noviTecajID = odabirID(listaPO);


        try(Connection connection = source.getConnection()) {

            try (Statement stmt = connection.createStatement()) {

                connection.setAutoCommit(false);

                stmt.executeUpdate(String.format("UPDATE Upis SET IDProgramObrazovanja = %d WHERE UpisID = %d", noviTecajID, upisID));

                connection.commit();

                System.out.println("PO uspjesno promjenjen!");
            } catch (SQLException e) {
                connection.rollback();
                System.err.println("Transakcija ponistena!");
            }

        } catch (SQLException e) {
            System.out.println("Greska prilikom spajanja!");
            e.printStackTrace();
        }
    }

    public static void ispisUpisanihPolaznika(ListaPO listaPO) {
        listaPO.ispisPO();
        System.out.println("Odaberite PO cije polaznike zelite ispisati upisivanjem pridruzenog ID-a:");
        int tecajID = odabirID(listaPO);

        dohvatPolaznika(tecajID);

    }

    private static String unosImePrezimeNaziv(String podatak) {
        String uneseniPodatak = null;
        boolean validanPodatak = false;
        do {
            try {
                String unos = br.readLine().trim();
                if (unos.isEmpty()) {
                    throw new Exception("Neispravan unos! " + podatak + " je obavezan podatak:");
                }
                if (!unos.matches("^[A-ZĆČĐŠŽ][a-zA-ZćčđšžĆČĐŠŽ\\s]*$")) {
                    String poruka = podatak.equals("Naziv") ? "Neispravan unos! Unesite ispravan naziv PO: " : "Neispravan unos! Unesite ispravno " + podatak.toLowerCase() + " polaznika: ";
                    throw new Exception(poruka);
                }
                uneseniPodatak = unos;
                validanPodatak = true;
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } while (!validanPodatak);
        return uneseniPodatak;
    }

    private static int unosCSVET() {
        boolean validanCSVET = false;
        int csvet = -1;
        do {
            try {
                String unos = br.readLine().trim();
                csvet = Integer.parseInt(unos);
                if (csvet < 0) {
                    throw new IllegalArgumentException("Neispravan unos! Broj CSVET bodova ne moze biti negativan broj!");
                }
                validanCSVET = true;
            } catch (NumberFormatException e) {
                System.out.println("Neispravan unos! Ocekivani unos je broj CSVET bodova:");
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            } catch (Exception e) {
                System.out.println("Neispravan unos!");
            }
        } while (!validanCSVET);
        return csvet;
    }

    private static int odabirID(ListaPolaznika listaPolaznika) {
        boolean validan = false;
        int ID = -1;
        do {
            try {
                String unos = br.readLine().trim();
                ID = Integer.parseInt(unos);
                if(!listaPolaznika.validanID(ID)) {
                    throw new IllegalArgumentException("Neispravan unos! Odaberite polaznika upisivanjem pridruzenog ID-a:");
                }
                validan = true;
            } catch (NumberFormatException e) {
                System.out.println("Neispravan unos! Ocekivani unos je broj (vrijednost ID-a):");
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            } catch (Exception e) {
                System.out.println("Neispravan unos!");
            }
        } while (!validan);
        return ID;
    }

    private static int odabirID(ListaPO listaPO) {
        boolean validan = false;
        int ID = -1;
        do {
            try {
                String unos = br.readLine().trim();
                ID = Integer.parseInt(unos);
                if(!listaPO.validanID(ID)) {
                    throw new IllegalArgumentException("Neispravan unos! Odaberite PO upisivanjem pridruzenog ID-a:");
                }
                validan = true;
            } catch (NumberFormatException e) {
                System.out.println("Neispravan unos! Ocekivani unos je broj (vrijednost ID-a):");
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            } catch (Exception e) {
                System.out.println("Neispravan unos!");
            }
        } while (!validan);
        return ID;
    }

    private static void upisSQL(int polaznik, int po) {
        DataSource source = DBconnect.createDataSource();
        try(Connection connection = source.getConnection()) {
            CallableStatement stmt = connection.prepareCall("{CALL NoviUpis(?,?)}");
            stmt.setInt(1, polaznik);
            stmt.setInt(2, po);
            stmt.executeUpdate();

            stmt.close();
        } catch (SQLException e) {
            System.err.println("Greška pri spajanju na bazu!");
            e.printStackTrace();
        }
    }

    private static void dohvatPolaznika(int id) {
        DataSource source = DBconnect.createDataSource();
        try(Connection connection = source.getConnection()) {
            CallableStatement stmt = connection.prepareCall("{CALL DohvatiPolaznike(?)}");
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            while(rs.next()) {
                System.out.printf("%s %s (%s, %d CSVET)\r\n", rs.getString("Ime"), rs.getString("Prezime"), rs.getString("Program"), rs.getInt("Bodovi"));
            }

            stmt.close();
        } catch (SQLException e) {
            System.err.println("Greška pri spajanju na bazu!");
            e.printStackTrace();
        }
    }
}
