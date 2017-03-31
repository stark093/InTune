package capstone.splash;

/**
 * Created by Nick on 2016-11-21.
 */

final public class Complex_Number {
    private double pRe;
    private double pIm;

    public Complex_Number(double re, double im){
        pRe = re;
        pIm = im;
    }

    public double re(){
        return pRe;
    }

    public double im(){
        return pIm;
    }

    public double magnitude(){
        return Math.sqrt(Math.abs(pRe*pRe) + Math.abs(pIm*pIm));
    }

    public double phase() {
        return Math.atan2(pIm, pRe);
    }

    public Complex_Number add(Complex_Number input){
        //Simply (a+bi)+(c+di) = (a+c)+i(b+d)
        return new Complex_Number((pRe+input.re()),(pIm+input.im()));
    }

    public Complex_Number sub(Complex_Number input){
        //same as add but subtract.
        return new Complex_Number((pRe-input.re()),(pIm-input.im()));
    }

    public Complex_Number mult(Complex_Number input){
        /*
            Multiply complex numbers using FOIL.
            eg.  (a+bi)*(c+di)
                =ac + adi + bci + bdii
                =(ac-bd)+i(bc+ad)
         */
        double retRe = 0;
        double retIm = 0;

        retRe = pRe*input.re() - pIm*input.im();
        retIm = pIm*input.re() + pRe*input.im();

        return new Complex_Number(retRe,retIm);
    }

    public Complex_Number scale(int k){
        return new Complex_Number(pRe/k,pIm/k);
    }


}
