import org.hipparchus.geometry.euclidean.threed.Vector3D;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class CalculateRTN {

    public static void main(String[] arg){

        // load orekit data
        DataLoader dataLoader = new DataLoader();
        dataLoader.loadData();

        // the satNo that the ephemeris screening is being done for
        int screenSatNo = 47479;

        KratosPCReader kratosPCReader = new KratosPCReader("kratos_pc.csv");
        kratosPCReader.screenSat(screenSatNo);

        // define frame according to the sat we are screening
        Vector3D screenPos = kratosPCReader.getScreenPos();
        Vector3D screenVel = kratosPCReader.getScreenVel();
        Vector3D otherPos = kratosPCReader.getOtherPos();
        Vector3D otherVel = kratosPCReader.getOtherVel();

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
