import org.hipparchus.geometry.euclidean.threed.Vector3D;

public class EllipsoidCheck {

    public static void main(String[] arg){
        // load orekit data
        DataLoader dataLoader = new DataLoader();
        dataLoader.loadData();

        // ellipsoid dimensions in RIC (RTN)
        double R = 2e3; //meters
        double T = 50e3; //m
        double N = 40e3; //m

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

        // define screen's RIC frame (same as RTN)
        Vector3D rHat = screenPos.normalize();
        Vector3D hVec = screenPos.crossProduct(screenVel);
        Vector3D cHat = hVec.normalize();
        Vector3D iHat = cHat.crossProduct(rHat);

        // project relative position onto RIC (RTN)
        double rMD = rRel.dotProduct(rHat);
        double tMD = rRel.dotProduct(iHat);
        double nMD = rRel.dotProduct(cHat);

        //check if the point is within the ellipse
        double ellipseEq = (rMD/R)*(rMD/R) + (tMD/T)*(tMD/T) + (nMD/N)*(nMD/N);
        boolean isConjunctionInEllipsoid = ellipseEq < 1;


    }

}
