import org.orekit.time.TimeScale;
import org.orekit.time.TimeScalesFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

public class VCMGenerator {

    private String path;

    public VCMGenerator(String path){
        this.path = path;
    }


    void generateVCM() throws FileNotFoundException {

        //write to vcm file
        PrintWriter vcm = new PrintWriter("output.vcm");

        //read norad IDs from group.txt
        Scanner input = new Scanner(new File("group.txt"));

        while(input.hasNext()){
            int satNo = input.nextInt();


            //for each norad id, convert to the format in text file
            String satNoString;
            if(Integer.toString(satNo).length()==1){
                satNoString = "0000"+Integer.toString(satNo);
            }else if(Integer.toString(satNo).length()==2){
                satNoString = "000"+Integer.toString(satNo);
            }else if(Integer.toString(satNo).length()==3){
                satNoString = "00"+Integer.toString(satNo);
            }else if(Integer.toString(satNo).length()==4){
                satNoString = "0"+Integer.toString(satNo);
            }else{
                satNoString = Integer.toString(satNo);
            }

            //pull the sp vector
            String filePath = path + "/" + satNoString.substring(0,2) + "/" + satNoString + ".txt";

            Scanner spvec = new Scanner(new File(filePath));

            //format into vcm
            vcm.write("<> SP VECTOR/COVARIANCE MESSAGE\n");
            vcm.write("<>\n");
            vcm.write("<> MESSAGE TIME (UTC): 2017 334 (30 NOV) 00:17:42.752  CENTER:\n");
            vcm.write("<> " + spvec.nextLine() + "\n");//sat no, int des
            vcm.write("<> COMMON NAME:\n");
            vcm.write("<> " + spvec.nextLine() + "\n");//epoch time, epoch rev

            String ecipos = spvec.nextLine();
            String ecivel = spvec.nextLine();

            //                                                                       ////
            // NEED TO CONVERT FROM TEME TO EMEJ2K, JUST DOING THIS RN CAUSE IM LAZY ////
            //                                                                       ////

            vcm.write("<> J2K POS " + ecipos.substring(8,ecipos.length()) + "\n");//j2k pos
            vcm.write("<> J2K VEL " + ecivel.substring(8,ecivel.length()) + "\n");//j2k vel
            vcm.write("<> " + ecipos + "\n");
            vcm.write("<> " + ecivel + "\n");
            vcm.write("<> EFG POS " + ecipos.substring(8,ecipos.length()) + "\n");
            vcm.write("<> EFG VEL " + ecivel.substring(8,ecivel.length()) + "\n");

            vcm.write("<> " + spvec.nextLine() + "\n");//geopotential
            vcm.write("<> " + spvec.nextLine() + "\n");//models
            vcm.write("<> " + spvec.nextLine() + "\n");//ballistic
            vcm.write("<> " + spvec.nextLine() + "\n");//srp
            vcm.write("<> " + spvec.nextLine() + "\n");//thrust
            vcm.write("<> " + spvec.nextLine() + "\n");//weather
            vcm.write("<> " + spvec.nextLine() + "\n");//tai utc
            vcm.write("<> " + spvec.nextLine() + "\n");//polar mot
            vcm.write("<> " + spvec.nextLine() + "\n");//leap sec
            vcm.write("<> " + spvec.nextLine() + "\n");//integrator
            vcm.write("<> " + spvec.nextLine() + "\n");//settings
            vcm.write("<> " + spvec.nextLine() + "\n");//step size

            vcm.write("<> VECTOR U,V,W SIGMAS (KM):           0.1049     0.1238     0.0216\n");
            vcm.write("<> VECTOR UD,VD,WD SIGMAS (KM/S):      0.0000     0.0000     0.0000\n");
            vcm.write("<> COVARIANCE MATRIX (EQUINOCTIAL ELS): ( 9x 9) WTD RMS:  1.00000E+00\n");

            vcm.write("<>  1.48882E-11 -1.37549E-11  2.68345E-11 -3.74201E-11  5.98460E-11\n");
            vcm.write("<>  1.46817E-10  1.41432E-14  6.31584E-14  7.26607E-13  1.30469E-13\n");
            vcm.write("<> -6.88136E-13  6.27056E-13  1.67902E-12 -3.38392E-15  2.23359E-13\n");
            vcm.write("<> -6.09893E-13  1.20022E-12  2.67220E-12  1.52213E-15  2.17965E-13\n");
            vcm.write("<>  3.65867E-13  0.00000E+00  0.00000E+00  0.00000E+00  0.00000E+00\n");
            vcm.write("<>  0.00000E+00  0.00000E+00  0.00000E+00  0.00000E+00  0.00000E+00\n");
            vcm.write("<>  0.00000E+00  0.00000E+00  0.00000E+00  0.00000E+00  0.00000E+00\n");
            vcm.write("<>  0.00000E+00  0.00000E+00  0.00000E+00  0.00000E+00  0.00000E+00\n");
            vcm.write("<>  0.00000E+00  0.00000E+00  0.00000E+00  0.00000E+00  0.00000E+00\n");

            //close the sp vec
            spvec.close();

        } // end while loop

        //close the vcm file when all sp vec info is written
        vcm.close();
    }

    void findDroppedVCMs(double days) throws FileNotFoundException {

        //TimeScale utc = TimeScalesFactory.getUTC();

        /*Scanner quick = new Scanner(new File(filePath));
            // GET SAT NO
            quick.next();
            quick.next();
            int satNoTest = quick.nextInt();
            quick.nextLine();

            quick.next();
            quick.next();
            quick.next();
            int year = quick.nextInt();
            int DOY = quick.nextInt();

            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.YEAR,year);
            cal.set(Calendar.DAY_OF_YEAR,DOY);
            AbsoluteDate spdate = new AbsoluteDate(year,cal.get(Calendar.MONTH)+1,cal.get(Calendar.DAY_OF_MONTH), utc);
            if(spdate.durationFrom(new AbsoluteDate(2022, 04, 16, utc)) / 86400 < -1*days){

                //write satNos that need to be removed from screening

                //System.out.println(satNoTest);
                continue;
            }

            quick.close();*/

    }

}
