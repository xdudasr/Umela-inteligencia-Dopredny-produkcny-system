import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class InstanceOfRule {
    private HashMap<String,String> premenne = new HashMap<String,String>();
    private ArrayList<String> akcie=new ArrayList<String>();

    public ArrayList<String> getakcia() {
        return akcie;
    }
    public void setakcia(ArrayList<String> akcia) {
        this.akcie = akcia;
    }

    public HashMap<String,String> getpremenne() {
        return premenne;
    }
    public void setpremenne(HashMap<String,String> premenna) {
        this.premenne = premenna;
    }

    //prejde zoznamom zisti ci z danym klucom exituje dana premenna ak ano vrati zhodu
    public boolean zistenieobsahu(String podmienka,String fakt){
        boolean zhoda=false;
        //prejdem cez podmienky a fakty a pozeram sa ci kluc s hodnotou existuju co je nutna podmienka
        //pre spravne vyhodnotenie podmienok
        String[] faktyslova = fakt.split(" ");
        String[] podmienkanaslovo = podmienka.split(" ");
        for(int i=0;i<podmienkanaslovo.length;i++){
            if((podmienkanaslovo[i].startsWith("?"))){
                if((premenne.containsKey(podmienkanaslovo[i])==true)&&
                        premenne.get(podmienkanaslovo[i]).equals(faktyslova[i])){zhoda=true;}
            }
        }

        return zhoda;
    }

    //vyhodnocovanie specialnych podmienok
    public boolean specialnepodmienky(ArrayList<String> speciepodm, InstanceOfRule ins){
        for(String sp: speciepodm){
            String[] podmienka = sp.split(" ");
            if(premenne.get(podmienka[1]).equals(premenne.get(podmienka[2]))){
                return false;
            }
        }
        return true;
    }

    public void vypis(){
        for(Map.Entry<String, String> entry: premenne.entrySet()) {
            System.out.println("Key "+entry.getKey()+" value "+entry.getValue());
        }

    }

    public boolean vytvorenieinstancie(Rules pravidlo,String fakt,String podmienka){
        boolean vlozenie=false;
        boolean vyhovuje =false;
        //nastavenie akcie ...to je potom ak bude spravne vyhodnotene pravidlo vykona sa
        setakcia(pravidlo.getpotom());
        //rozdelen fakty a podmienky podla medzery
        String[] faktyslova = fakt.split(" ");
        String[] podmienkanaslovo = podmienka.split(" ");
        for(int l=0;l<faktyslova.length;l++){
            //ak podmienka zacina ? viem ze ide o premennu
            if ((podmienkanaslovo[l].startsWith("?"))){
                //ak je premenna prazdna vloz tato podmienka prebehne iba pre prvu podmienku
                if(premenne.isEmpty()==true){
                    premenne.put(podmienkanaslovo[l], faktyslova[l])	;
                    vlozenie=true;
                }
                //pre ostatne podmienky cize pre druhu atd
                else{
                    //tu je chyba ked idem pre druhu podmienku
                    if(zistenieobsahu(podmienka,fakt)==true){
                        //pytam sa ci hash mapa obsahuje taky kluc ak nie zapisem ak ano nespravim nic
                        if(premenne.containsKey(podmienkanaslovo[l])==false){

                            premenne.put(podmienkanaslovo[l], faktyslova[l])	;


                        }
                        vlozenie=true;
                    }
                }
            }
        }
        return vlozenie;
    }


}
