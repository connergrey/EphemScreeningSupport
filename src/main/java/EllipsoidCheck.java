import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.hipparchus.util.FastMath;
import org.orekit.utils.Constants;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class EllipsoidCheck {

    public static void main(String[] arg) throws FileNotFoundException {
        // load orekit data
        DataLoader dataLoader = new DataLoader();
        dataLoader.loadData();

        //need to use --csv_type 2 in Kratos CA to get this output format
        Scanner scan = new Scanner(new File("many_many_ex.csv"));

        //read the kcsm file
        KCSM kcsm = new KCSM(scan.nextLine());
        Vector3D priPos = new Vector3D(kcsm.getX_PRI(), kcsm.getY_PRI(), kcsm.getZ_PRI());
        Vector3D priVel = new Vector3D(kcsm.getVX_PRI(), kcsm.getVY_PRI(), kcsm.getVZ_PRI());
        Vector3D secPos = new Vector3D(kcsm.getX_SEC(), kcsm.getY_SEC(), kcsm.getZ_SEC());
        Vector3D secVel = new Vector3D(kcsm.getVX_SEC(), kcsm.getVY_SEC(), kcsm.getVZ_SEC());

        //convert miss distance to RIC frame
        Vector3D relPos = secPos.subtract(priPos); //in XYZ,meters
        double[] ricMDkm = getRICMD(priPos, priVel, relPos); //in RIC, km

        //calculate perigee altitude and eccentricity
        double[] hpANDe = getMinAltAndEcc(priPos, priVel);

        //determine the ellipsoid size (for primary)
        double[] ellipSize = getEllipsoidSize(hpANDe);

        //System.out.println(ricMDkm[0] + " of " + ellipSize[0]);

        //check if its within the ellipsoid
        double ellipseEq = FastMath.pow(ricMDkm[0] / ellipSize[0], 2) + FastMath.pow(ricMDkm[1] / ellipSize[1], 2)
                + FastMath.pow(ricMDkm[2] / ellipSize[2], 2);// if this is less than 1, then the point is within the ellipse
        boolean isConjunctionInEllipsoid = ellipseEq < 1; //true if it is, false if it is not


    }

    public static double[] getRICMD(Vector3D pos,Vector3D vel,Vector3D rRel){

        // define screen's RIC frame (same as RTN)
        Vector3D rHat = pos.normalize();
        Vector3D hVec = pos.crossProduct(vel);
        Vector3D cHat = hVec.normalize();
        Vector3D iHat = cHat.crossProduct(rHat);

        // project relative position onto RIC (RTN)
        double rMD = rRel.dotProduct(rHat)/1000;//in km
        double tMD = rRel.dotProduct(iHat)/1000;
        double nMD = rRel.dotProduct(cHat)/1000;

        return new double[]{rMD,tMD,nMD};

    }

    public static double[] getEllipsoidSize(double[] hpANDe){
        double hp = hpANDe[0];
        double e = hpANDe[1];

        double R = 0;
        double I = 0;
        double C = 0;

        double Rmin = 0.4; //NEEDS TO BE 0.4 IN PRODUCTION

        if(hp <= 500 && e < 0.25 ){
            R = Rmin;
            I = 44;
            C = 51;
        }else if(hp <= 750 && e < 0.25 ){
            R = Rmin;
            I = 25;
            C = 25;
        }else if(hp <= 1200 && e < 0.25 ){
            R = Rmin;
            I = 12;
            C = 12;
        }else if(hp <= 2000 && e < 0.25 ){
            R = Rmin;
            I = 2;
            C = 2;
        }else {
            R = 10;
            I = 10;
            C = 10;
        }

        return new double[]{R,I,C};

    }

    public static double[] getMinAltAndEcc(Vector3D pos, Vector3D vel){ //in m and m/s

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

        return new double[]{altp,e};

    }

}
