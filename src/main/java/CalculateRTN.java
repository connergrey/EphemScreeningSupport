import org.hipparchus.geometry.euclidean.threed.Vector3D;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class CalculateRTN {

    public static void main(String[] arg){

        // the satNo that the ephemeris screening is being done for
        int screenSatNo = 47479;

        File file = new File("kratos_pc.csv");

        String[] parts = new String[0];
        try {
            Scanner scan = new Scanner(file);
            String header = scan.nextLine();//header
            header = header.replaceAll("\\s", ""); // remove spaces
            //System.out.println(header);

            String line = scan.nextLine();
            line = line.replaceAll("\\s", ""); // remove spaces
            parts = line.split(",");

            scan.close();
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }

        Vector3D priPos = new Vector3D( Double.parseDouble(parts[10]) , Double.parseDouble(parts[11]), Double.parseDouble(parts[12]) ); //meters
        Vector3D secPos = new Vector3D( Double.parseDouble(parts[13]) , Double.parseDouble(parts[14]), Double.parseDouble(parts[15]) ); //meters

        Vector3D priVel = new Vector3D( Double.parseDouble(parts[16]) , Double.parseDouble(parts[17]), Double.parseDouble(parts[18]) ); // m/s
        Vector3D secVel = new Vector3D( Double.parseDouble(parts[19]) , Double.parseDouble(parts[20]), Double.parseDouble(parts[21]) ); // m/s

        // define frame according to the sat we are screening
        int priSatNo = Integer.parseInt(parts[0]);
        int secSatNo = Integer.parseInt(parts[1]);

        Vector3D screenPos = new Vector3D(0,0,0);
        Vector3D screenVel = new Vector3D(0,0,0);
        Vector3D otherPos = new Vector3D(0,0,0);
        Vector3D otherVel = new Vector3D(0,0,0);
        // determine if sat we are screening is primary or secondary
        if(priSatNo == screenSatNo) {
            screenPos = priPos;
            screenVel = priVel;
            otherPos = secPos;
            otherVel = secVel;
        }else if(secSatNo == screenSatNo){
            screenPos = secPos;
            screenVel = secVel;
            otherPos = priPos;
            otherVel = priVel;
        }

        // find relative vector from screen sat to the other sat
        Vector3D rRel = otherPos.subtract(screenPos);
        Vector3D vRel = otherVel.subtract(screenVel);

        // define screen's RIC frame (same as RTN)
        Vector3D rHat = screenPos.normalize();
        Vector3D hVec = screenPos.crossProduct(screenVel);
        Vector3D cHat = hVec.normalize();
        Vector3D iHat = cHat.crossProduct(rHat);

        // project relative position onto RIC (RTN)
        double rMD = rRel.dotProduct(rHat);
        double tMD = rRel.dotProduct(iHat);
        double nMD = rRel.dotProduct(cHat);
        Vector3D MD = new Vector3D(rMD,tMD,nMD);

        // project relative velocity onto RIC (RTN)
        double rMV = vRel.dotProduct(rHat);
        double tMV = vRel.dotProduct(iHat);
        double nMV = vRel.dotProduct(cHat);
        Vector3D MV = new Vector3D(rMV,tMV,nMV);

    }



}
