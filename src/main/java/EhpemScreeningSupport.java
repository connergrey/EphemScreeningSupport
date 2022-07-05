import java.io.FileNotFoundException;

public class EhpemScreeningSupport {

    public static void main(String[] arg){

        // load orekit data
        DataLoader dataLoader = new DataLoader();
        dataLoader.loadData();

        //int screenSatNo = 42013;
        //int screenSatNo = 45100;
        //passing in zero returns ALL sats vcms
        int screenSatNo = 0;

        //you need to specify the file path
        //String path = "SP_VEC";
        String path = "/Users/connergrey/Documents/SP VECTORS/vectors_22153/scratch/SP_VEC";
        OrbitBucketer bucketer = new OrbitBucketer(path);
        VCMGenerator generator = new VCMGenerator(path);

        try {

            double[] altitudes = {100, 150, 200, 250, 300, 350, 400, 450, 500, 550, 600, 650, 700, 750, 800, 850, 900,
                    950, 1000, 1050, 1100, 1150, 1200, 1500, 2000, 2500, 3000, 3500, 4000, 4500, 5000, 7500, 10000,
                    15000, 20000, 25000, 30000, 35000, 40000, 50000}; // km
            bucketer.createGroups(altitudes);
            bucketer.screenGroup(screenSatNo);

            generator.generateVCM();

            //the method is commented out for now.
            //generator.findDroppedVCMs(7.0);


        }catch (FileNotFoundException e){
            e.printStackTrace();
        }


    }


}
