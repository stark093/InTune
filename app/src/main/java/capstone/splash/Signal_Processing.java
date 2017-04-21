package capstone.splash;

/**
 * Created by Nick on 2016-11-14.
 * /* To get frequency we are using a method called cepstral analysis. The steps are:
 1-FFT(sound signal) - Spectral density of the sound signal
 -Find spectral density of sound signal.
 -Guitar strings create sounds at its fundimental frequency. f0, and its harmonics, 2f0 3f0 etc.
 -highest peak isn't always f0, so another technique must be used.
 -The harmonics occur in the FFT waveform periodically.. at a frequency of f0
 2-take log(FFT) - log of spectral density of sound signal
 -To convert amplitudes to a log scale, so that all values are on the same scale,
 so that the periodic peaks are relevant when taking the spectral density.
 3-FFT(log(FFT)) - spectral density of log of spectral density of sound signal - turn it to "cepstral" domain
 - If the peaks are occuring periodically... The cepstral peaks will correspond to the spacing between
 periodic components of the log(FFT) waveform. These peaks correspond to the fundimental frequencies of components
 in the original sound signal. The sound signal is assumed to be dominantly guitar signals, so the largest peak would
 be the guitar stings fundimental frequency

 For example, if bin 100(point 100 in the cepstral dataset) is  a peak, then that means peaks occur at
 100 bin(point) intervals in the log(fft). Since all we did was take the log of the fft, the peaks also occur at
 100 bin(point) intervals in the fft of the sound waveform. This means that there are peaks spaced by fs/100 Hz
 in the fft(sound signal) curve, which means the fundimental frequency is fs/100! this is the frequency
 of the string!

 -Result is in quefrency domain. Peaks in quefrency domain correspond to the frequency of harmonic signal components.



 REFERENCES:
 http://iitg.vlab.co.in/?sub=59&brch=164&sim=615&cnt=1
 https://www.johndcook.com/blog/2016/05/18/cepstrum-quefrency-and-pitch/
 https://en.wikipedia.org/wiki/Cepstrum

 *
 *
 */

public class Signal_Processing {
    static final public double[] FFT(double dataset[]) {
        int k = dataset.length;

        Complex_Number[] complex_dataset = new Complex_Number[k];
        for (int i = 0; i < k; i++) {
            complex_dataset[i] = new Complex_Number(dataset[i], 0.0);
        }


        Complex_Number[] FFTValues = fft_recursive(complex_dataset);
        //System.out.println(FFTValues[11].magnitude() + "  " + FFTValues[32757].magnitude());


        //Populate a list of just values. location does not matter here.
        //We need to scale by 1/k and want magnitude.
        double[] valuesFreqPoints = new double[k];
        for (int i = 0; i < k; i++) {
            valuesFreqPoints[i] =  ((FFTValues[i].magnitude()/k)*(FFTValues[i].magnitude()/k));
        }

        return valuesFreqPoints;
    }

    // NUMBER OF POINTS MUST BE A POWER OF 2!
    // see https://en.wikipedia.org/wiki/Cooley%E2%80%93Tukey_FFT_algorithm
    static final private Complex_Number[] fft_recursive(Complex_Number x[]) {
        //Recursive Cooley-Tukey FFT
        int n = x.length;
        if (n == 1) {
            return new Complex_Number[]{x[0]};
        }

        if (n % 2 != 0) {
            throw new RuntimeException("n must be a power of 2");
        }

        Complex_Number[] evens = new Complex_Number[n / 2];
        for (int i = 0; i < n / 2; i++) {
            evens[i] = x[2 * i];
        }

        Complex_Number[] odds = new Complex_Number[n / 2];
        for (int i = 0; i < n / 2; i++) {
            odds[i] = x[2 * i + 1];
        }

        Complex_Number[] E = fft_recursive(evens);
        Complex_Number[] O = fft_recursive(odds);

        Complex_Number[] y = new Complex_Number[n];
        for (int k = 0; k < n / 2; k++) {
            double kthTerm = -2 * Math.PI * k / n;
            Complex_Number expTerm = new Complex_Number(Math.cos(kthTerm), Math.sin(kthTerm));
            y[k] = E[k].add(expTerm.mult(O[k]));
            y[k + n / 2] = E[k].sub(expTerm.mult(O[k]));
        }

        return y;
    }


    static final public double[] zeroPad(double dataSet[], int num){
        int L = dataSet.length;
        double[] padded = new double[L*num];

        //populate first half
        for(int i=0;i<L/2;i++){
            padded[i] = dataSet[i];
        }
        //add zeros
        for(int i=L/2;i<L*num-L/2;i++){
            padded[i] = 0;
        }
        //add second half to end
        for(int i=L/2;i<L;i++){
            padded[i+L*(num-1)] = dataSet[i];
        }
        return padded;
    }

    static public double meanOfAbsoluteVals(double[] input){
        double sum=0;
        for(int i=0;i<input.length;i++){
            sum+=Math.abs(input[i]);
        }
        return sum/input.length;
    }

    static public int[] getMultStartEnd(double f){
        if(f<200){
            return new int[]{1,(int) (4*44100/(f+25)), (int)(4*44100/(f-25))};
        }else{
            return new int[]{2,(int) (4*44100/(f+25)), (int)(4*44100/(f-25))};
        }
    }

    static public boolean soundsGood(double[] input){
        boolean result = true;
        double threshold = 500;
        int subIntervals = 4;
        int L = input.length;
        int subL = L/subIntervals;
        double[] currentSub = new double[subL];
        for(int i= 1;i<=subIntervals;i++){
            for(int j=0;j<subL;j++){
                currentSub[j] = input[(i-1)*subL+j];
            }
            if(meanOfAbsoluteVals(currentSub)<threshold){
                result = false;
                break;
            }
        }
        return result;
    }

    static public double[] conv(double[] signal, double f){
        int peak;
        if(f<200){
            peak = 21;
        }else{
            peak = 31;
        }
        double[] filt = new double[2*peak-1];
        for(int i=0;i<peak-1;i++){
            filt[i] = i+1;
        }
        for(int i=peak-1;i>=0;i--){
            filt[filt.length-1-i] = i+1;
        }
        double[] result = new double[signal.length+filt.length-1];
        //Prepend 0s to B so we dont get Index Out of bounds exception
        double[] paddedSignal = new double[signal.length+2*(filt.length-1)];
        for(int i=0;i<filt.length-1;i++){
            paddedSignal[i] = 0;
        }
        for(int i=0;i<signal.length;i++){
            paddedSignal[i+filt.length-1] = signal[i];
        }
        for(int i=0;i<result.length;i++){
            for(int j=0;j<filt.length;j++){
                result[i] = result[i] + paddedSignal[i+filt.length-1-j]*filt[filt.length-1-j];
            }
        }

        double[] ret = new double[signal.length];
        for(int i=0;i<ret.length;i++){
            ret[i] = result[i+(filt.length-1)/2];
        }
        return ret;
    }

    static final public double[] getMaxFreqMagnitude(double dataSet[], double fDesired){
        double[] FFTOfSignal;
        double[] paddedFFT;
        double[] logFFT;
        double[] cepstral;
        double maxVal = 0;
        int maxi = 0;


        if(!soundsGood(dataSet)){

            return new double[]{0,0};


        }

        System.out.println("going...");
        int[] multStartEnd = getMultStartEnd(fDesired);
        int mult = multStartEnd[0];
        int start = mult*multStartEnd[1];
        int end = mult*multStartEnd[2];

        int dataSetLength = dataSet.length;

        if(!((dataSetLength > 0) && ((dataSetLength & (dataSetLength - 1)) == 0))){
            throw new RuntimeException("n must be a power of 2");
        }

        //We want the FFT of the sound signal, done with windowing applied. Windowing time domain input helps reduce noise.
        FFTOfSignal = FFT(dataSet);
        paddedFFT = zeroPad(FFTOfSignal,4);
        //Then we want the log of this waveform. make it 2x the length to zero-pad for resolution.
        logFFT = new double[paddedFFT.length];


        for(int i=0;i<paddedFFT.length;i++){
            logFFT[i]=Math.log1p(paddedFFT[i]);
        }
        cepstral = FFT(logFFT);
        cepstral = conv(cepstral,fDesired);
        for(int i=start;i<end;i++){
            if(cepstral[i]>maxVal){
                maxVal = cepstral[i];
                maxi = i;
            }
        }


        return new double[]{mult*4.*44100/maxi, maxVal*100};


    }
}