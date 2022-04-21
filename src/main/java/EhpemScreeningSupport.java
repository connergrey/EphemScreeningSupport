import java.io.FileNotFoundException;

public class EhpemScreeningSupport {

    public static void main(String[] arg){

        int screenSatNo = 42013;

        //you need to specify the absolute file path
        String path = "/Users/connergrey/EphemScreeningSupport/SP_VEC";
        OrbitBucketer bucketer = new OrbitBucketer(path);
        VCMGenerator generator = new VCMGenerator(path);

        try {

            bucketer.createGroups();
            bucketer.screenGroup(screenSatNo);

            generator.generateVCM();
            //the method is commented out because you need to load orekit-data

            generator.findDroppedVCMs(7.0);


        }catch (FileNotFoundException e){
            e.printStackTrace();
        }


    }


}
