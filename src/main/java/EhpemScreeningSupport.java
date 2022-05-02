import org.orekit.data.DataContext;
import org.orekit.data.DirectoryCrawler;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Locale;

public class EhpemScreeningSupport {

    public static void main(String[] arg){

        // load orekit data
        loadData();

        int screenSatNo = 42013;

        //you need to specify the absolute file path
        String path = "/Users/connergrey/EphemScreeningSupport/SP_VEC";
        OrbitBucketer bucketer = new OrbitBucketer(path);
        VCMGenerator generator = new VCMGenerator(path);

        try {

            double[] altitudes = {100, 200, 300, 400, 500, 600, 700, 800, 900, 1000, 1100, 1200, 1500,
            2000, 2500, 3000, 3500, 4000, 4500, 5000, 7500, 10000, 15000, 20000, 25000, 30000, 35000, 40000}; // km
            bucketer.createGroups(altitudes);
            bucketer.screenGroup(screenSatNo);

            generator.generateVCM();
            //the method is commented out because you need to load orekit-data
            generator.findDroppedVCMs(7.0);


        }catch (FileNotFoundException e){
            e.printStackTrace();
        }


    }

    public static void loadData(){
        //loads constant data file info
        final File home = new File(System.getProperty("user.home"));
        final File orekitData = new File(home, "orekit-data");
        if (!orekitData.exists()) {
            System.err.format(Locale.US, "Failed to find %s folder%n", orekitData.getAbsolutePath());
            System.err.format(Locale.US, "You need to download %s from %s, unzip it in %s and rename it 'orekit-data' for this tutorial to work%n",
                    "orekit-data-master.zip", "https://gitlab.orekit.org/orekit/orekit-data/-/archive/master/orekit-data-master.zip",
                    home.getAbsolutePath());
            System.exit(1);
        }
        DataContext.
                getDefault().
                getDataProvidersManager().
                addProvider(new DirectoryCrawler(orekitData));
        // end configure

    }


}
