import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.TimeScalesFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class KratosPCReader {

    private Vector3D screenPos;
    private Vector3D screenVel;
    private Vector3D otherPos;
    private Vector3D otherVel;
    private int priSatNo;
    private int secSatNo;
    private Vector3D priPos;
    private Vector3D priVel;
    private Vector3D secPos;
    private Vector3D secVel;
    private int screenSatNo;
    private int otherSatNo;
    private AbsoluteDate tca;

    public KratosPCReader(String kratosPCFileName){

        File file = new File(kratosPCFileName);

        String[] parts = new String[0];
        try {
            Scanner scan = new Scanner(file);
            String header = scan.nextLine();//header
            header = header.replaceAll("\\s", ""); // remove spaces

            String line = scan.nextLine();
            line = line.replaceAll("\\s", ""); // remove spaces
            parts = line.split(",");

            scan.close();
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }

        // set all the parts I need to variables
        priPos = new Vector3D( Double.parseDouble(parts[10]) , Double.parseDouble(parts[11]), Double.parseDouble(parts[12]) ); //meters
        secPos = new Vector3D( Double.parseDouble(parts[13]) , Double.parseDouble(parts[14]), Double.parseDouble(parts[15]) ); //meters

        priVel = new Vector3D( Double.parseDouble(parts[16]) , Double.parseDouble(parts[17]), Double.parseDouble(parts[18]) ); // m/s
        secVel = new Vector3D( Double.parseDouble(parts[19]) , Double.parseDouble(parts[20]), Double.parseDouble(parts[21]) ); // m/s

        priSatNo = Integer.parseInt(parts[0]);
        secSatNo = Integer.parseInt(parts[1]);

        tca = new AbsoluteDate(parts[4], TimeScalesFactory.getUTC());

    }

    public void screenSat(int screenSatNo){

        // determine if sat we are screening is primary or secondary
        if(priSatNo == screenSatNo) {
            screenPos = priPos;
            screenVel = priVel;
            otherPos = secPos;
            otherVel = secVel;
            this.screenSatNo = priSatNo;
            otherSatNo = secSatNo;

        }else if(secSatNo == screenSatNo){
            screenPos = secPos;
            screenVel = secVel;
            otherPos = priPos;
            otherVel = priVel;
            this.screenSatNo = secSatNo;
            otherSatNo = priSatNo;

        }

    }

    public Vector3D getScreenPos() {
        return screenPos;
    }

    public Vector3D getScreenVel() {
        return screenVel;
    }

    public Vector3D getOtherPos() {
        return otherPos;
    }

    public Vector3D getOtherVel() {
        return otherVel;
    }

    public int getOtherSatNo() {
        return otherSatNo;
    }

    public AbsoluteDate getTca() {
        return tca;
    }
}
