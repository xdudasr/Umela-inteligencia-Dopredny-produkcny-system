import java.util.ArrayList;


public class Rules {

    String meno="";
    ArrayList<String> ak = new ArrayList<String>();
    ArrayList<String> potom =new ArrayList<String>();
    ArrayList<String> specialnepodmienky= new ArrayList<String>();
    boolean stav=false;


    public int getpocet() {
        int pocetpremennych=0;
        ArrayList<String> podmie =new ArrayList<String>();
        for(String podmienka:ak){
            String[] podmienkanaslova = podmienka.split(" ");
            for(String slovo :podmienkanaslova){
                if(slovo.startsWith("?")){
                    if(podmie.contains(slovo)==false) podmie.add(slovo);
                }
            }
        }
        return podmie.size();
    }

    public String getmeno() {
        return meno;
    }
    public void setmeno(String menoo) {
        this.meno = menoo;
    }
    public ArrayList<String> getak() {
        return ak;
    }
    public void setak(String akk) {

        if((akk.contains("<>"))){
            specialnepodmienky.add(akk);
            stav=true;
        }
        else {
            this.ak.add(akk);
        }
    }

    public ArrayList<String> getsp() {
        return specialnepodmienky;
    }
    public ArrayList<String> getpotom() {
        return potom;
    }
    public void setpotom(String potomm) {
        this.potom.add(potomm);
    }
    public boolean getobsahujespecialnapodmienka() {
        return stav;
    }

}
