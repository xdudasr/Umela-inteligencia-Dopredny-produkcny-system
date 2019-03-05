import java.awt.List;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Solution {
    HashMap<Integer, String> fakty = new HashMap<Integer, String>();
    static ArrayList<Rules> pravidla =new ArrayList<Rules> ();
    ArrayList<HashMap> zoznampremennych= new ArrayList<HashMap>();
    private List list;

    public void vypis(){
        for(int i=0;i<pravidla.size();i++){
            System.out.println(pravidla.get(i).getmeno());
            System.out.println(pravidla.get(i).getak());
            System.out.println(pravidla.get(i).getpotom());
        }

    }

    public void vypiszoznamu(){
        System.out.println(zoznampremennych.size());
        for(int i=0;i<zoznampremennych.size();i++){
            HashMap<String,String> nw=zoznampremennych.get(i);
            for(Map.Entry<String, String> entry: nw.entrySet()) {
                System.out.println("Key "+entry.getKey()+" value "+entry.getValue());


            }
        }

    }
    public void vypisfaktov(){
        System.out.println("Pracovna pamat:");
        for(Map.Entry<Integer, String> entry: fakty.entrySet()) {
            System.out.println(entry.getValue());

        }
    }

    public void rieseniepravidiel(){
        boolean vystup=false;
        boolean zhoda=true;
        int poc=0;
        for(int i =0;i<pravidla.size();i++){
            ArrayList<HashMap> zoznamakcii=new ArrayList<HashMap>();
            ArrayList<String> rozdelenepodmienky=(pravidla.get(i).getak());
            for(String podmienka:rozdelenepodmienky){
                if(false==porovnanie(podmienka,poc)){break;}
                poc++;
            }
            for(int l=0;l<zoznampremennych.size();l++){

                if(zoznampremennych.get(l).size()!=pravidla.get(i).getpocet()){
                    //odstranim hashmapy kt. nesplanaju pocet
                    zoznampremennych.remove(l);
                    l--;
                }
                else{

                    if(pravidla.get(i).getobsahujespecialnapodmienka()==true){
                        if(specialnepodmienky(pravidla.get(i).getsp(),zoznampremennych.get(l))==false){
                            zoznampremennych.remove(l);
                            l--;
                        }

                    }
                }
            }
            //mam zoznampremennych kt. su zmysluplne premenne zamenym
            for(int l=0;l<zoznampremennych.size();l++){
                boolean vykonatelny=false;
                ArrayList<String> akcie =(akcie(pravidla.get(i).getpotom(),zoznampremennych.get(l)));
                for(String akcia:akcie){
                    if(akcia.contains("pridaj")){
                        if(!fakty.containsValue(akcia.substring(akcia.indexOf(' ')+1))){vykonatelny=true;}
                    }
                    if(akcia.contains("vymaz")){
                        if(fakty.containsValue(akcia.substring(akcia.indexOf(' ')+1))){vykonatelny=true;}
                    }

                }
                if(vykonatelny==true){
                    System.out.println("Spravy:");
                    for(String akcia:akcie){
                        if(akcia.contains("pridaj")){
                            if(!fakty.containsValue(akcia.substring(akcia.indexOf(' ')+1))){
                                pridaniefaktov(akcia.substring(akcia.indexOf(' ')+1),1);
                            }
                        }
                        if(akcia.contains("vymaz")){
                            if(fakty.containsValue(akcia.substring(akcia.indexOf(' ')+1))){
                                pridaniefaktov(akcia.substring(akcia.indexOf(' ')+1),0);
                            }
                        }
                        if(akcia.contains("sprava")){
                            System.out.println(akcia.substring(akcia.indexOf(' ')+1));
                        }
                    }
                }
            }
            vypisfaktov();
            zoznampremennych.clear();
            poc=0;
        }

    }

    //vyhodnocovanie spec.podmienok
    public ArrayList<String> akcie(ArrayList<String> zoznamakcii,HashMap<String,String> zoznampremennych){
        ArrayList<String> akcie = new ArrayList<String>();
        String hotovaakcia="";
        for(String akcia:zoznamakcii){
            String[] akcianaslova = akcia.split(" ");
            for(int i=0;i<akcianaslova.length;i++){
                if(akcianaslova[i].startsWith("?")){
                    akcianaslova[i]=zoznampremennych.get(akcianaslova[i]);
                }
            }
            for(int i=0;i<akcianaslova.length;i++){
                if(i<akcianaslova.length-1){
                    hotovaakcia=hotovaakcia+akcianaslova[i]+" ";

                }
                else hotovaakcia=hotovaakcia+akcianaslova[i];
            }
            akcie.add(hotovaakcia)	;
        }
        return akcie;
    }



    public boolean specialnepodmienky(ArrayList<String> speciepodm,HashMap<String,String> zoznampremennych){
        for(String sp: speciepodm){
            String[] regpodmienka = sp.split(" ");
            if(zoznampremennych.get(regpodmienka[1]).equals(zoznampremennych.get(regpodmienka[2]))){
                return false;
            }
        }
        return true;
    }


    public boolean porovnanie(String podmienka,int poc){
        String[] regpodmienka = podmienka.split(" ");
        String[] podmienkanaslova = podmienka.split(" ");
        String regexpodmienka="";
        boolean zhoda=false;
        for(int i=0;i<regpodmienka.length;i++){
            if(regpodmienka[i].startsWith("?")){regpodmienka[i]="[\\w]+";}

        }
        for(int i=0;i<regpodmienka.length;i++){

            if(i<regpodmienka.length-1){
                regexpodmienka=regexpodmienka+regpodmienka[i]+" ";

            }
            else regexpodmienka=regexpodmienka+regpodmienka[i];

        }
        Pattern pattern = Pattern.compile(regexpodmienka);
        ArrayList<String> aplikovatelnefakty = new ArrayList<String>() ;

        for(Map.Entry<Integer, String> entry: fakty.entrySet()) {

            Matcher matcher = pattern.matcher(entry.getValue());
            if(matcher.find()){
                //nasiel som zhodu s faktom
                if(poc==0){
                    //System.out.println("empty");
                    zhoda=true;
                    HashMap<String,String> premenne =new HashMap<String,String>();
                    String[] faktyslova = entry.getValue().split(" ");
                    for(int i=0;i<faktyslova.length;i++){
                        if ((podmienkanaslova[i].startsWith("?"))){
                            premenne.put(podmienkanaslova[i], faktyslova[i]);
                        }
                    }
                    zoznampremennych.add(premenne);
                }
                else {
                    for(int i=0;i<zoznampremennych.size();i++){
                        HashMap<String,String> docansnepremenne  =new HashMap<String,String>();
                        boolean vlozenie=false;
                        String[] faktyslova = entry.getValue().split(" ");
                        for(int l=0;l<faktyslova.length;l++){
                            if ((podmienkanaslova[l].startsWith("?"))){

                                //kluc je rovnaky
                                if((zoznampremennych.get(i).containsKey(podmienkanaslova[l])==true)){

                                    if((zoznampremennych.get(i).get(podmienkanaslova[l])).equals(faktyslova[l])){
                                        vlozenie=true;


                                        //doacsna premenna nie je prazdna
                                        if(docansnepremenne.isEmpty()==false){

                                            zoznampremennych.get(i).putAll(docansnepremenne);
                                            docansnepremenne.clear();
                                            zhoda=true;
                                        }
                                    }
                                    //tu je konflikt
                                    else{docansnepremenne.clear();
                                    }
                                }

                                if((zoznampremennych.get(i).containsKey(podmienkanaslova[l])==false)){
                                    docansnepremenne.put(podmienkanaslova[l], faktyslova[l]);
                                    if(vlozenie==true){
                                        zoznampremennych.get(i).putAll(docansnepremenne);
                                        zhoda=true;
                                    }

                                }

                            }
                        }
                    }
                }
            }
        }
        return zhoda;

    }


    public void pridaniefaktov(String line,int l){
        int ASCII=0;
        for(int i=0;i<line.length();i++){
            ASCII +=(((int)line.charAt(i))*(i+1));
        }
        if(l==1)fakty.put(ASCII,line);
        if(l==0)fakty.remove(ASCII);
    }


    public void citanie() throws IOException{
        String csvFile = "c:/Produkcny system/baza.txt";
        BufferedReader br = null;
        String line = "";
        String porpravidla = "Pravidla:";
        String porfakty = "Fakty:";
        String pom="";
        int ASCII=0;
        boolean stav=false;

        try {

            br = new BufferedReader(new FileReader(csvFile));
            Rules rules=new Rules();

            while ((line = br.readLine()) != null) {

                if(stav==false && (!line.equals(porfakty)) && (!line.equals("")) && (!line.equals(porpravidla))){
                    pridaniefaktov(line,1);
                }
                if(stav==true   ) {

                    if((line.matches("^\\s*[Mm]eno.*"))){
                        rules.setmeno(line.replaceAll("^\\s*[Mm][Ee][Nn][Oo]\\s*:\\s*",""));
                    }
                    if((line.matches("^\\s*[Aa][Kk].*"))){
                        String[] podmienky=(line.replaceAll("^\\s*[Aa][Kk]\\s*","")).split(",");
                        for(String podmienka:podmienky){ rules.setak(podmienka);}

                    }
                    if((line.matches("^\\s*[Pp][Oo][Tt][Oo][Mm].*"))){
                        String[] akcie=(line.replaceAll("^\\s*[Pp][Oo][Tt][Oo][Mm]\\s*","")).split(",");
                        for(String akcia:akcie){ rules.setpotom(akcia);}

                        pravidla.add(rules);
                        rules=new Rules();
                    }


                }
                if(line.equals(porpravidla))stav=true;

            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

}