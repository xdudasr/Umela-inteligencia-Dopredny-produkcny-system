import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ProductionSystemMethods {
    public ArrayList<String> fakty = new ArrayList<String>();
    public ArrayList<String> spravy = new ArrayList<String>();
    public ArrayList<Rules> pravidla =new ArrayList<Rules> ();
    ArrayList<InstanceOfRule> apinstan = new ArrayList<InstanceOfRule>() ;
    ArrayList<InstanceOfRule> apkilovatelneinstancie = new ArrayList<InstanceOfRule>() ;
    ArrayList<String> apfakty=new ArrayList<String>();


    public void rieseniepravidiel() throws IOException{
        citanie();


        boolean koniec=false;
        boolean vystup=false;
        boolean zhoda=true;
        int poc=0;
        ArrayList<InstanceOfRule> docaplik=new ArrayList<InstanceOfRule>() ;
        while(true){
            for(int i =0;i<pravidla.size();i++){
                ArrayList<String> rozdelenepodmienky=(pravidla.get(i).getak());
                for(int z=0;z<rozdelenepodmienky.size();z++){
                    if(false==porovnanie(pravidla.get(i),rozdelenepodmienky.get(z))){
                        apfakty.clear();
                        break;}
                    docaplik.clear();
                    //prepis
                    for(InstanceOfRule ins:apinstan){
                        docaplik.add(ins);
                    }

                    apinstan.clear();
                    //cyklus cez odpovedajuce fakty podmienke

                    if(z>0){
                        //dalsie podmienky
                        //cyklus cez instancie

                        for(int l=0;l<docaplik.size();l++){
                            for(String fakt:apfakty){
                                //System.out.println("pridal som");
                                InstanceOfRule inpravidlo=docaplik.get(l);
                                if((inpravidlo.vytvorenieinstancie(pravidla.get(i), fakt,rozdelenepodmienky.get(z)))==true){
                                    apinstan.add(inpravidlo);

                                }

                            }}
                    }
                    //tu sa vyhodnocuje prva podmienka
                    else{
                        for(String fakt:apfakty){
                            InstanceOfRule inpravidlo=new InstanceOfRule();
                            if((inpravidlo.vytvorenieinstancie(pravidla.get(i), fakt,rozdelenepodmienky.get(z)))==true){
                                apinstan.add(inpravidlo);

                            }
                        }
                    }
                    apfakty.clear();
                }


                //vyhodnotenie specialnej podmienky
                for(int l=0;l<apinstan.size();l++){
                    InstanceOfRule inpravidlo=apinstan.get(l);
                    if(pravidla.get(i).getobsahujespecialnapodmienka()==true){
                        if(inpravidlo.specialnepodmienky(pravidla.get(i).getsp(),inpravidlo)==false){
                            apinstan.remove(l);
                            l--;
                        }
                    }
                }
                if(apinstan.size()>0){
                    for(InstanceOfRule ins: apinstan){
                        apkilovatelneinstancie.add(ins);
                    }
                    apinstan.clear();
                }

            }
            if(apkilovatelneinstancie.size()>0){
                for(int t=0;t<apkilovatelneinstancie.size();t++){
                    InstanceOfRule inpravidlo=apkilovatelneinstancie.get(t);
                    koniec=akcie(inpravidlo);
                    if(koniec==true)break;
                }

            }
            apkilovatelneinstancie.clear();
            vypisfaktov();
            if(koniec==false)break;
        }
    }
    public void vypisfaktov(){
        System.out.println("Pracovna pamat:");
        for(String fakt:fakty){
            System.out.println(fakt);
        }
        System.out.println("Spravy:");
        for(String sprava:spravy){
            System.out.println(sprava);
        }
    }



    //metoda hlada zhodu zo znamimi faktami
    public boolean porovnanie(Rules pravidlo,String podmienka){
        //rozdelena podmienka na medzery
        String[] regpodmienka = podmienka.split(" ");
        String[] podmienkanaslova = podmienka.split(" ");
        String regexpodmienka="";
        boolean zhoda=false;

        //nahradenie premennych regexom
        for(int i=0;i<regpodmienka.length;i++){
            if(regpodmienka[i].startsWith("?")){regpodmienka[i]="[\\w]+";}

        }
        //spojenie do jedneho stringu
        for(int i=0;i<regpodmienka.length;i++){

            if(i<regpodmienka.length-1){
                regexpodmienka=regexpodmienka+regpodmienka[i]+" ";

            }
            else regexpodmienka=regexpodmienka+regpodmienka[i];

        }

        //vytvorenei patternu
        Pattern pattern = Pattern.compile(regexpodmienka);

        //cyklus cez fakty
        for(String fakt:fakty) {
            Matcher matcher = pattern.matcher(fakt);
            if(matcher.find()){
                apfakty.add(fakt);
                zhoda=true;

            }
        }
        return zhoda;

    }


    public boolean akcie(InstanceOfRule ins){
        //boolean koniec=false;


        String hotovaakcia="";
        ArrayList<String> pom=new ArrayList<String>();
        for(int l=0;l<ins.getakcia().size();l++){
            hotovaakcia="";
            String[] akcianaslova = ins.getakcia().get(l).split(" ");


            for(int i=0;i<akcianaslova.length;i++){

                if(akcianaslova[i].startsWith("?")){
                    akcianaslova[i]=ins.getpremenne().get(akcianaslova[i]);
                }
            }
            for(int i=0;i<akcianaslova.length;i++){
                if(i<akcianaslova.length-1){
                    hotovaakcia=hotovaakcia+akcianaslova[i]+" ";

                }
                else hotovaakcia=hotovaakcia+akcianaslova[i];
            }
            pom.add(hotovaakcia);

        }


        ins.setakcia(pom);
        return(vykonajakcie(ins));
    }



    public boolean vykonajakcie(InstanceOfRule ins){
        boolean vykonatelny=false;

        for(String akcia:ins.getakcia()){

            if(akcia.contains("pridaj")){

                if(!fakty.contains(akcia.substring(akcia.indexOf(' ')+1))){vykonatelny=true;}
            }
            if(akcia.contains("vymaz")){
                if(fakty.contains(akcia.substring(akcia.indexOf(' ')+1))){vykonatelny=true;}
            }

        }
        if(vykonatelny==true){

            for(String akcia:ins.getakcia()){
                if(akcia.contains("pridaj")){

                    if(!fakty.contains(akcia.substring(akcia.indexOf(' ')+1))){
                        fakty.add(akcia.substring(akcia.indexOf(' ')+1));
                    }
                }
                if(akcia.contains("vymaz")){
                    if(fakty.contains(akcia.substring(akcia.indexOf(' ')+1))){
                        fakty.remove(akcia.substring(akcia.indexOf(' ')+1));
                    }
                }
                if(akcia.contains("sprava")){
                    spravy.add(akcia.substring(akcia.indexOf(' ')+1));

                }
            }

        }

        return vykonatelny;
    }


    public void citanie() throws IOException{

        String csvFile = "C:\\Users\\Robo\\IdeaProjects\\Dopredny produkcny system\\baza.txt";
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
                    fakty.add(line);
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
