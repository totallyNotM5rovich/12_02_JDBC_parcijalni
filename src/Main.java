import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        ListaPO listaPO = new ListaPO();
        ListaPolaznika listaPolaznika = new ListaPolaznika();

        String[] akcije = {" 1. Dodaj novog polaznika", " 2. Dodaj novi program obrazovanja", " 3. Upisi polaznika na PO", " 4. Prebacivanje polaznika na drugi PO", " 5. Ispis upisanih polaznika na PO", " 6. Izlaz"};

        beskonacnaPetlja:
        while (true) {

            System.out.println("\r\nEvidencija polaznika obrazovnih programa");
            for (String akcija : akcije) {
                System.out.println(akcija);
            }
            System.out.println("Odaberite jednu od ponudjenih akcija (1-6):");
            int akcija = MenuNavigacija.odabirOpcije(6);

            switch (akcija) {
                case 1:
                    MenuNavigacija.unosPolaznika(listaPolaznika);
                    break;
                case 2:
                    MenuNavigacija.unosPO(listaPO);
                    break;
                case 3:
                    MenuNavigacija.upisPolaznikaNaPO(listaPolaznika, listaPO);
                    break;
                case 4:
                    MenuNavigacija.prebacivanjePolaznika(listaPolaznika, listaPO);
                    break;
                case 5:
                    MenuNavigacija.ispisUpisanihPolaznika(listaPO);
                    break;
                case 6:
                    break beskonacnaPetlja;
                default:
            }
        }
    }
}