import org.orekit.time.AbsoluteDate;

public class CatchStaleSPVectors {

    public static void main(String[] arg){

        // load orekit data
        DataLoader dataLoader = new DataLoader();
        dataLoader.loadData();
        // run this for every CDM issued from ephemeris screening
        // take kratos_pc.csv as input
        // determine if TCA is > # days from the SP Epoch
        // output a boolean (true = stale, false = not stale)

        //input the screen sat no and the SP vec file path
        int screenSatNo = 42013;
        String path = "SP_VEC";

        // take kratos_pc.csv as input
        KratosPCReader kratosPCReader = new KratosPCReader("kratosPCtestOutput.csv");
        kratosPCReader.screenSat(screenSatNo);

        // get tca
        AbsoluteDate tca = kratosPCReader.getTca();

        // determine the other sat No
        int satNo = kratosPCReader.getOtherSatNo();

        // read the sp vector
        SPVecReader spReader = new SPVecReader();
        spReader.readSPVec(path, satNo);

        // get epoch date
        AbsoluteDate epoch = spReader.getDate();

        // find the numbers of days it was propagated to get to tca
        double age = tca.durationFrom(epoch)/86400;

        // check if greater than some number (7 days?)
        boolean state;
        if(age > 7){
            state = true;
        }else{
            state = false;
        }

    }


}
