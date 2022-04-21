import org.hipparchus.geometry.euclidean.threed.Vector3D;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class OrbitBucketer {

    private String path;


    public OrbitBucketer(String path){
        this.path = path;

    }

    void createGroups() throws FileNotFoundException {

        // define group separation altitudes
        double alt1 = 500;
        double alt2 = 1000;

        PrintWriter output = new PrintWriter(new File("output.csv"));
        PrintWriter group1 = new PrintWriter(new File("group1.txt"));
        PrintWriter group2 = new PrintWriter(new File("group2.txt"));
        PrintWriter group3 = new PrintWriter(new File("group3.txt"));

        //write header line
        output.write("SatNo,alt_p,alt_a,Beg Group,End Group\n");

        //you need to specify the absolute file path
        File folderPath = new File(path);
        for (File filePath : folderPath.listFiles()) {

            if (filePath.toString().compareTo(path + "/.DS_Store") == 0) {
                continue;
            }

            for (File file : filePath.listFiles()) {

                Scanner scan = new Scanner(file);

                // GET SAT NO
                scan.next();
                scan.next();
                int satNo = scan.nextInt();

                // GET DATE
                scan.nextLine();
                scan.next();
                scan.next();
                scan.next();
                int year = scan.nextInt();
                int dayOfYear = scan.nextInt();

                scan.next();
                scan.next();
                String time = scan.next();

                scan.nextLine();
                scan.next();
                scan.next();
                scan.next();
                Vector3D pos = new Vector3D(scan.nextDouble(), scan.nextDouble(), scan.nextDouble()); //KM

                scan.nextLine();
                scan.next();
                scan.next();
                scan.next();
                Vector3D vel = new Vector3D(scan.nextDouble(), scan.nextDouble(), scan.nextDouble()); //KM/S

                //
                // ADD CONVERSION FROM TEME TO EMEJ2000 (IM SKIPPING CAUSE IM LAZY AND IT REQUIRES I READ THE TIME)
                //

                double mu = 398600;
                double r = pos.getNorm();
                double v = vel.getNorm();

                double denom = v * v / mu - 2 / r;
                double a = -1 / denom;

                Vector3D hVec = pos.crossProduct(vel);
                Vector3D eVec = (vel.crossProduct(hVec).scalarMultiply(1 / mu)).subtract(pos.normalize());
                double e = eVec.getNorm();

                double altp = a * (1 - e) - 6378;
                double alta = a * (1 + e) - 6378;

                // determine which groups this sat is in
                int groupNoBeg = 0;
                int groupNoEnd = 0;

                if (altp >= 0 && altp < alt1) {
                    groupNoBeg = 1;
                } else if (altp >= alt1 && altp < alt2) {
                    groupNoBeg = 2;
                } else if (altp >= alt2) {
                    groupNoBeg = 3;
                }

                if (alta >= 0 && alta < alt1) {
                    groupNoEnd = 1;
                } else if (alta >= alt1 && alta < alt2) {
                    groupNoEnd = 2;
                } else if (alta >= alt2) {
                    groupNoEnd = 3;
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
                if (groupNoBeg <= 1 && 1 <= groupNoEnd) {
                    group1.write(String.valueOf(satNo) + "\n");
                }
                if (groupNoBeg <= 2 && 2 <= groupNoEnd) {
                    group2.write(String.valueOf(satNo) + "\n");
                }
                if (groupNoBeg <= 3 && 3 <= groupNoEnd) {
                    group3.write(String.valueOf(satNo) + "\n");
                }

            }
        }
        //close csv
        output.close();
        group1.close();
        group2.close();
        group3.close();

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
        if (outputBeg == 0 || outputEnd == 0) {
            System.out.println("Error: Screen Sat No not found");
        }

        //pull all unique satNos and write to group.txt
        List<Integer> included = new ArrayList<>();

        if (outputBeg <= 1 && 1 <= outputEnd) {
            Scanner g = new Scanner(new File("group1.txt"));

            while (g.hasNext()) {
                Integer satNo = g.nextInt();

                if (!included.contains(satNo)) {
                    included.add(satNo);
                    group.write(String.valueOf(satNo) + "\n");
                }

            }
            g.close();
        }
        if (outputBeg <= 2 && 2 <= outputEnd) {
            Scanner g = new Scanner(new File("group2.txt"));

            while (g.hasNext()) {
                Integer satNo = g.nextInt();

                if (!included.contains(satNo)) {
                    included.add(satNo);
                    group.write(String.valueOf(satNo) + "\n");
                }

            }
            g.close();
        }
        if (outputBeg <= 3 && 3 <= outputEnd) {
            Scanner g = new Scanner(new File("group3.txt"));

            while (g.hasNext()) {
                Integer satNo = g.nextInt();

                if (!included.contains(satNo)) {
                    included.add(satNo);
                    group.write(String.valueOf(satNo) + "\n");
                }

            }
            g.close();
        }

        System.out.println("Group created that contains groups " + outputBeg + " to " + outputEnd);
        group.close();

    }


}

