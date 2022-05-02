import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.orekit.frames.Frame;
import org.orekit.frames.FramesFactory;
import org.orekit.utils.Constants;
import org.orekit.utils.TimeStampedPVCoordinates;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class OrbitBucketer {

    private String path;
    private final Frame eme2000 = FramesFactory.getEME2000();


    public OrbitBucketer(String path){
        this.path = path;

    }

    void createGroups(double[] altitudes) throws FileNotFoundException {

        PrintWriter output = new PrintWriter(new File("output.csv"));
        // need N (length(alt) + 1) groups
        int N = altitudes.length + 1;

        List<PrintWriter> groups = new ArrayList<>();
        for(int i = 0; i < N; i++){
            groups.add(new PrintWriter(new File("group" + i + ".txt")));
        }

        //write header line
        output.write("SatNo,alt_p,alt_a,Beg Group,End Group\n");

        //you need to specify the absolute file path
        File folderPath = new File(path);
        for (File filePath : folderPath.listFiles()) {

            if (filePath.toString().compareTo(path + "/.DS_Store") == 0) {
                continue;
            }

            for (File file : filePath.listFiles()) {

                SPVecReader spReader = new SPVecReader();
                spReader.readSPVec(file);
                int satNo = spReader.getSatNo();
                TimeStampedPVCoordinates tspvEME = spReader.getInitialState(eme2000);
                Vector3D pos = tspvEME.getPosition(); // in m
                Vector3D vel = tspvEME.getVelocity(); //in m/s

                // calculate some orbital elements to find the min and max altitude of the orbit
                double mu = Constants.EGM96_EARTH_MU; //in m^3/s^2 (i think)
                double eRad = Constants.EGM96_EARTH_EQUATORIAL_RADIUS;
                double r = pos.getNorm();
                double v = vel.getNorm();

                double denom = v * v / mu - 2 / r;
                double a = -1 / denom;

                Vector3D hVec = pos.crossProduct(vel);
                Vector3D eVec = (vel.crossProduct(hVec).scalarMultiply(1 / mu)).subtract(pos.normalize());
                double e = eVec.getNorm();

                double altp = (a * (1 - e) - eRad) / 1000;
                double alta = (a * (1 + e) - eRad) / 1000;

                if(a < 0 && e > 1){// its hyperbolic
                    alta = 100e6; //some arbitrarily large number
                }

                // determine which groups this sat is in
                int groupNoBeg = 0;
                int groupNoEnd = 0;

                //determine groups

                //this:
                for (int i = 0; i < N; i++) {
                    if (i == 0){
                        //check 0 to altitudes[0]
                        if (altp >= 0 && altp < altitudes[i]){
                            groupNoBeg = 0;
                        }
                    }else if (i == (N-1)){
                        //check > altitudes[N-1]
                        if(altp > altitudes[i-1]){
                            groupNoBeg = i;
                            groupNoBeg = groupNoBeg - 1; // buffer (wrote like this for clarity)
                        }
                    }else {
                        //all the middle ones (i = 1 to N-2)
                        if (altp >= altitudes[i - 1] && altp < altitudes[i]){
                            groupNoBeg = i;
                            groupNoBeg = groupNoBeg - 1; // buffer (wrote like this for clarity)
                        }

                    }

                }


                for (int i = 0; i < N; i++) {
                    if (i == 0){
                        //check 0 to altitudes[0]
                        if (alta >= 0 && alta < altitudes[i]){
                            groupNoEnd = 0;
                            groupNoEnd = groupNoEnd + 1; // buffer (wrote like this for clarity)
                        }
                    }else if (i == (N-1)){
                        //check > altitudes[N-2]
                        if(alta > altitudes[i-1]){
                            groupNoEnd = i;
                        }
                    }else {
                        //all the middle ones (i = 1 to N-2)
                        if (alta >= altitudes[i - 1] && alta < altitudes[i]){
                            groupNoEnd = i;
                            groupNoEnd = groupNoEnd + 1; // buffer (wrote like this for clarity)
                        }

                    }

                }

                //create line to write in csv
                StringBuffer buffer = new StringBuffer("");
                buffer.append(String.valueOf(satNo));
                buffer.append(",");
                buffer.append(String.valueOf(altp));
                buffer.append(",");
                buffer.append(String.valueOf(alta));
                buffer.append(",");
                buffer.append(String.valueOf(groupNoBeg));
                buffer.append(",");
                buffer.append(String.valueOf(groupNoEnd));
                buffer.append("\n");

                //write in csv
                output.write(buffer.toString());
                buffer.delete(0, buffer.length());

                //write in group
                for(int i = groupNoBeg; i <= groupNoEnd; i++){
                    groups.get(i).write(String.valueOf(satNo) + "\n");
                }

            }
        }
        //close csv
        output.close();
        for(int i = 0; i < N; i++){
            groups.get(i).close();
        }

    }

    void screenGroup(int screenSatNo) throws FileNotFoundException {

        PrintWriter group = new PrintWriter(new File("group.txt"));

        //determine which groups screenSatNo is in
        Scanner readOut = new Scanner(new File("output.csv"));
        readOut.nextLine(); // skip header

        int outputBeg = 0;
        int outputEnd = 0;
        while (readOut.hasNextLine()) {
            String[] parts = readOut.nextLine().split(",");
            int outputSatNo = Integer.parseInt(parts[0]);

            if (screenSatNo == outputSatNo) {
                outputBeg = Integer.parseInt(parts[3]);
                outputEnd = Integer.parseInt(parts[4]);
                break;
            }
        }

        //pull all unique satNos and write to group.txt
        List<Integer> included = new ArrayList<>();
        for (int i = outputBeg; i <= outputEnd; i++){
            Scanner g = new Scanner(new File("group" + i + ".txt"));
            while (g.hasNext()) {
                Integer satNo = g.nextInt();
                if (!included.contains(satNo)) {
                    included.add(satNo);
                    group.write(String.valueOf(satNo) + "\n");
                }
            }
            g.close();
        }

        //System.out.println("Group created that contains groups " + outputBeg + " to " + outputEnd);
        group.close();

    }


}

