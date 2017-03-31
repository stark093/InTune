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
    static private double sampleRate = 44100.0;

    static final public double[] FFT(double dataset[], boolean applyWindow) {
        int k = dataset.length;
        double[] windowedDataset = new double[k];
        if(applyWindow) {
            windowedDataset = applyHanningWindow(dataset);
        }else{
            windowedDataset = dataset;
        }

        Complex_Number[] complex_dataset = new Complex_Number[k];
        for (int i = 0; i < k; i++) {
            complex_dataset[i] = new Complex_Number(windowedDataset[i], 0.0);
        }


        Complex_Number[] FFTValues = fft_recursive(complex_dataset);



        //Populate a list of just values. location does not matter here.
        //We need to scale by 1/k and want magnitude.
        double[] valuesFreqPoints = new double[k / 2];
        for (int i = 0; i < k / 2; i++) {
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

    static final public double[] firFilterLowPass(double dataset[]) {
        //double[] coefficients = {0,3.37453387444751E-05,0.000067178075233962,9.82254382573334E-05,0.000124826044565589,0.000144999144422798,0.000156913565293898,0.000158954454334829,0.000149785929482592,0.000128407804930799,9.42046592670186E-05,0.00004698566210653,-1.29862348204519E-05,-0.000084976907018078,-0.000167776368219842,-0.000259707391417571,-0.000358647732520443,-0.0004620659173014,-0.000567070211545734,-0.000670470052474539,-0.000768848884477696,-0.000858647021886311,-0.000936252863723967,-0.000998100517556234,-0.00104077165866977,-0.00106109926315795,-0.00105627071455977,-0.00102392769803433,-0.000962260267126914,-0.000870092498300057,-0.000746957238626073,-0.000593157602134516,-0.000409813078731815,-0.000198888383499238,3.67965106113659E-05,0.000293576352487993,0.000566971400133211,0.000851739378059725,0.00114194851120255,0.00143107107114407,0.0017120963950792,0.00197766186783413,0.00222019990257192,0.0024320985266925,0.00260587278571567,0.00273434382899102,0.00281082224559087,0.00282929198450718,0.0027845910270231,0.00267258488636626,0.00249032899460739,0.00223621610189002,0.00191010495953877,0.00151342678583658,0.00104926631907112,0.000522414642965886,-6.06085806380408E-05,-0.000691565324996391,-0.0013605431449224,-0.00205603353394643,-0.0027650447264613,-0.00347324831446338,-0.00416515837478229,-0.00482434113288647,-0.00543365253664152,-0.00597550049094572,-0.00643212792485063,-0.00678591233893274,-0.00701967702385443,-0.00711700876174383,-0.00706257652947278,-0.00684244552488952,-0.00644438073968984,-0.00585813431020592,-0.00507571099235884,-0.0040916063297567,-0.00290301241277917,-0.00150998655776856,8.44212365734565E-05,0.0018740855814745,0.0038497705684198,0.00599914428199998,0.00830684419923895,0.010754593857175,0.0133213702816349,0.0159836207704548,0.0187155267301509,0.0214893113926912,0.0242755874049348,0.0270437395033916,0.0297623367764183,0.0323995683889479,0.0349236961142011,0.0373035165938058,0.0395088259418285,0.0415108791268414,0.0432828365145842,0.0448001900350153,0.0460411616521368,0.0469870671610979,0.0476226388104222,0.0469870671610979,0.0460411616521368,0.0448001900350153,0.0432828365145842,0.0415108791268414,0.0395088259418285,0.0373035165938058,0.0349236961142011,0.0323995683889479,0.0297623367764183,0.0270437395033916,0.0242755874049348,0.0214893113926912,0.0187155267301509,0.0159836207704548,0.0133213702816349,0.010754593857175,0.00830684419923895,0.00599914428199998,0.0038497705684198,0.0018740855814745,8.44212365734565E-05,-0.00150998655776856,-0.00290301241277917,-0.0040916063297567,-0.00507571099235884,-0.00585813431020592,-0.00644438073968984,-0.00684244552488952,-0.00706257652947278,-0.00711700876174383,-0.00701967702385443,-0.00678591233893274,-0.00643212792485063,-0.00597550049094572,-0.00543365253664152,-0.00482434113288647,-0.00416515837478229,-0.00347324831446338,-0.0027650447264613,-0.00205603353394643,-0.0013605431449224,-0.000691565324996391,-6.06085806380408E-05,0.000522414642965886,0.00104926631907112,0.00151342678583658,0.00191010495953877,0.00223621610189002,0.00249032899460739,0.00267258488636626,0.0027845910270231,0.00282929198450718,0.00281082224559087,0.00273434382899102,0.00260587278571567,0.0024320985266925,0.00222019990257192,0.00197766186783413,0.0017120963950792,0.00143107107114407,0.00114194851120255,0.000851739378059725,0.000566971400133211,0.000293576352487993,3.67965106113659E-05,-0.000198888383499238,-0.000409813078731815,-0.000593157602134516,-0.000746957238626073,-0.000870092498300057,-0.000962260267126914,-0.00102392769803433,-0.00105627071455977,-0.00106109926315795,-0.00104077165866977,-0.000998100517556234,-0.000936252863723967,-0.000858647021886311,-0.000768848884477696,-0.000670470052474539,-0.000567070211545734,-0.0004620659173014,-0.000358647732520443,-0.000259707391417571,-0.000167776368219842,-0.000084976907018078,-1.29862348204519E-05,0.00004698566210653,9.42046592670186E-05,0.000128407804930799,0.000149785929482592,0.000158954454334829,0.000156913565293898,0.000144999144422798,0.000124826044565589,9.82254382573334E-05,0.000067178075233962,3.37453387444751E-05,0};
        double[] coefficients ={0.000066076126811740601             , 0.000076842919285905327             , 0.000095422919814980634             , 0.000088848685811824282             , 0.000042247253995598687             ,-0.000053997721059687007             ,-0.00019861326785215394              ,-0.00037490369332176594              ,-0.00054912924581034428              ,-0.00067334879343581644              ,-0.00069330376011540067              ,-0.00056088407236731146              ,-0.00024924997679219205              , 0.00023268700225854028              , 0.00083059858716622958              , 0.0014438159525269095               , 0.0019357892372355494               , 0.0021564714496192399               , 0.0019744395847436204               , 0.0013132380838301292               , 0.00018416033598554499              ,-0.0012929998784534809               ,-0.0028891988459988764               ,-0.0042904688073367231               ,-0.0051469981778850399               ,-0.0051417021285132944               ,-0.0040673825171513795               ,-0.0018963660762109905               , 0.0011728965089911961               , 0.0047082159201215839               , 0.0080833621135018938               , 0.010566503677052865                , 0.011447861235419195                , 0.010188059137018091                , 0.0065611443916458239               , 0.00076359534910708238              ,-0.006537080897928634                ,-0.014232294680559802                ,-0.020886559019304928                ,-0.024926437607175018                ,-0.024879765297296401                ,-0.019628880503951628                ,-0.0086355823173299862               , 0.0079034843332267037               , 0.029003325269304762                , 0.052955920609133719                , 0.077512167422611508                , 0.10015800261556262                 , 0.11844529611235434                 , 0.13032736661496816                 , 0.13444650071762884                 , 0.13032736661496816                 , 0.11844529611235434                 , 0.10015800261556262                 , 0.077512167422611508                , 0.052955920609133719                , 0.029003325269304762                , 0.0079034843332267037               ,-0.0086355823173299862               ,-0.019628880503951628                ,-0.024879765297296401                ,-0.024926437607175018                ,-0.020886559019304928                ,-0.014232294680559802                ,-0.006537080897928634                , 0.00076359534910708238              , 0.0065611443916458239               , 0.010188059137018091                , 0.011447861235419195                , 0.010566503677052865                , 0.0080833621135018938               , 0.0047082159201215839               , 0.0011728965089911961               ,-0.0018963660762109905               ,-0.0040673825171513795               ,-0.0051417021285132944               ,-0.0051469981778850399               ,-0.0042904688073367231               ,-0.0028891988459988764               ,-0.0012929998784534809               , 0.00018416033598554499              , 0.0013132380838301292               , 0.0019744395847436204               , 0.0021564714496192399               , 0.0019357892372355494               , 0.0014438159525269095               , 0.00083059858716622958              , 0.00023268700225854028              ,-0.00024924997679219205              ,-0.00056088407236731146              ,-0.00069330376011540067              ,-0.00067334879343581644              ,-0.00054912924581034428              ,-0.00037490369332176594              ,-0.00019861326785215394              ,-0.000053997721059687007             , 0.000042247253995598687             , 0.000088848685811824282             , 0.000095422919814980634             , 0.000076842919285905327             , 0.000066076126811740601};
        int filterLength = coefficients.length;
        int Yn = 0;
        double[] Xn = new double[filterLength];
        double[] filteredOutput = new double[dataset.length];

        for (int i = 0; i < filterLength; i++) {
            Xn[i] = 0;
        }

        for (int i = 0; i < dataset.length; i++) {
            for (int j = 0; j < filterLength - 1; j++) {
                //shift points one to the left
                Xn[filterLength - j - 1] = Xn[filterLength - j - 2];
            }
            //add next datapoint to front of filter inputs
            Xn[0] = dataset[i];
            Yn = 0;

            for (int j = 0; j < filterLength; j++) {
                Yn += coefficients[j] * Xn[j];
            }
            filteredOutput[i] = Yn;
        }
        return filteredOutput;
    }

    static final public double[] firfilterDCBlocker(double dataset[]){
        double RVal = 0.995;
        int Yn = 0;
        double[] filteredResuls = new double[dataset.length];
        filteredResuls[0] = dataset[0];
        for(int i=1;i<dataset.length;i++){
            filteredResuls[i] = dataset[i] - dataset[i-1] + RVal*filteredResuls[i-1];
        }

        return filteredResuls;

    }

    static final private double[] applyHanningWindow(double dataset[]){
        double wn = 0;
        double[] returnData = new double[dataset.length];
        for(int i=0;i<dataset.length;i++){
            wn = 0.5*(1- Math.cos((2* Math.PI*i)/(dataset.length-1)));
            returnData[i] = dataset[i]*wn;
        }



        return returnData;
    }

    static final public double getFrequency(double[] dataSet, int string){
        double noiseLim = 1.5;
        if(string==1) {
            return getFrequency1(dataSet,noiseLim);
        }else if(string==2){
            return getFrequency2(dataSet,noiseLim);
        }else if(string==3){
            return getFrequency3(dataSet,noiseLim);
        }else if(string==4){
            return getFrequency4(dataSet,noiseLim);
        }else if(string==5){
            return getFrequency5(dataSet,noiseLim);
        }else if(string==6){
            return getFrequency6(dataSet,noiseLim);
        }
        else{
            throw new RuntimeException("String must be a number between 1 and 6");
        }


    }

    static final public double getFrequency1(double[] dataSet, double noiseThreshold){

        /*
            The data used as described above is named as follows:
            dataSet = sound signal. The sound recorded by the microphone.
            fftOfSignal = . Spectral density of the sound signal, windowed by a hanning function for correctness.
            logFFT  = log of spectral density of sound signal
            cepstral = spectral density of log of spectral density of sound signal

            maxVal - a temporary variable used to iterate through cepstral and find the highest peak
            largestFreq - a variable used to hold the dominant frequency found in cepstral.

            Returns: highest frequency
         */

        double retVal = 0;

        double[] maxFreq_magnitude = get_maxFreq_magnitude123(getCepstral123(dataSet),126,147);


        if(maxFreq_magnitude[1]*100>noiseThreshold){
            retVal = maxFreq_magnitude[0];
        }

        return retVal;

    }

    static final public double getFrequency2(double[] dataSet, double noiseThreshold){

        /*
            The data used as described above is named as follows:
            dataSet = sound signal. The sound recorded by the microphone.
            fftOfSignal = . Spectral density of the sound signal, windowed by a hanning function for correctness.
            logFFT  = log of spectral density of sound signal
            cepstral = spectral density of log of spectral density of sound signal

            maxVal - a temporary variable used to iterate through cepstral and find the highest peak
            largestFreq - a variable used to hold the dominant frequency found in cepstral.

            Returns: highest frequency
         */

        double retVal = 0;

        double[] maxFreq_magnitude = get_maxFreq_magnitude123(getCepstral123(dataSet),166,196);


        if(maxFreq_magnitude[1]*100>noiseThreshold){
            retVal = maxFreq_magnitude[0];
        }

        return retVal;

    }

    static final public double getFrequency3(double[] dataSet, double noiseThreshold){

        /*
            The data used as described above is named as follows:
            dataSet = sound signal. The sound recorded by the microphone.
            fftOfSignal = . Spectral density of the sound signal, windowed by a hanning function for correctness.
            logFFT  = log of spectral density of sound signal
            cepstral = spectral density of log of spectral density of sound signal

            maxVal - a temporary variable used to iterate through cepstral and find the highest peak
            largestFreq - a variable used to hold the dominant frequency found in cepstral.

            Returns: highest frequency
         */

        double retVal = 0;

        double[] maxFreq_magnitude = get_maxFreq_magnitude123(getCepstral123(dataSet),205,245);


        if(maxFreq_magnitude[1]*100>noiseThreshold){
            retVal = maxFreq_magnitude[0];
        }

        return retVal;

    }

    static final public double getFrequency4(double[] dataSet, double noiseThreshold){

        /*
            The data used as described above is named as follows:
            dataSet = sound signal. The sound recorded by the microphone.
            fftOfSignal = . Spectral density of the sound signal, windowed by a hanning function for correctness.
            logFFT  = log of spectral density of sound signal
            cepstral = spectral density of log of spectral density of sound signal

            maxVal - a temporary variable used to iterate through cepstral and find the highest peak
            largestFreq - a variable used to hold the dominant frequency found in cepstral.

            Returns: highest frequency
         */

        double retVal = 0;

        double[] maxFreq_magnitude = get_maxFreq_magnitude456(getCepstral456(dataSet),133,170);


        if(maxFreq_magnitude[1]*100>noiseThreshold){
            retVal = maxFreq_magnitude[0];
        }

        return retVal;

    }

    static final public double getFrequency5(double[] dataSet, double noiseThreshold){

        /*
            The data used as described above is named as follows:
            dataSet = sound signal. The sound recorded by the microphone.
            fftOfSignal = . Spectral density of the sound signal, windowed by a hanning function for correctness.
            logFFT  = log of spectral density of sound signal
            cepstral = spectral density of log of spectral density of sound signal

            maxVal - a temporary variable used to iterate through cepstral and find the highest peak
            largestFreq - a variable used to hold the dominant frequency found in cepstral.

            Returns: highest frequency
         */

        double retVal = 0;

        double[] maxFreq_magnitude = get_maxFreq_magnitude456(getCepstral456(dataSet),126,245);


        if(maxFreq_magnitude[1]*100>noiseThreshold){
            retVal = maxFreq_magnitude[0];
        }

        return retVal;

    }

    static final public double getFrequency6(double[] dataSet, double noiseThreshold){

        /*
            The data used as described above is named as follows:
            dataSet = sound signal. The sound recorded by the microphone.
            fftOfSignal = . Spectral density of the sound signal, windowed by a hanning function for correctness.
            logFFT  = log of spectral density of sound signal
            cepstral = spectral density of log of spectral density of sound signal

            maxVal - a temporary variable used to iterate through cepstral and find the highest peak
            largestFreq - a variable used to hold the dominant frequency found in cepstral.

            Returns: highest frequency
         */

        double retVal = 0;

        double[] maxFreq_magnitude = get_maxFreq_magnitude456(getCepstral456(dataSet),245,315);


        if(maxFreq_magnitude[1]*100>noiseThreshold){
            retVal = maxFreq_magnitude[0];
        }

        return retVal;

    }





    static final public double[] getCepstral123(double[] dataSet){
        /*
            The data used as described above is named as follows:
            dataSet = sound signal. The sound recorded by the microphone.
            fftOfSignal = . Spectral density of the sound signal, windowed by a hanning function for correctness.
            logFFT  = log of spectral density of sound signal
            cepstral = spectral density of log of spectral density of sound signal
            maxVal - a temporary variable used to iterate through cepstral and find the highest peak
            largestFreq - a variable used to hold the dominant frequency found in cepstral.
            Returns: highest frequency
         */
        double[] FFTOfSignal;
        double[] logFFT;
        double[] cepstral;
        double[] windowedData;
        int dataSetLength = dataSet.length;


        if(!((dataSetLength > 0) && ((dataSetLength & (dataSetLength - 1)) == 0))){
            throw new RuntimeException("n must be a power of 2");
        }

        windowedData = applyHanningWindow(dataSet);
        //We want the FFT of the sound signal, done with windowing applied. Windowing time domain input helps reduce noise.
        FFTOfSignal = FFT(windowedData,true);
        //Then we want the log of this waveform. make it 2x the length to zero-pad for resolution.
        logFFT = new double[FFTOfSignal.length];


        for(int i=0;i<FFTOfSignal.length;i++){
            logFFT[i]= Math.log(FFTOfSignal[i]);
        }
        logFFT = firfilterDCBlocker(logFFT);
        //then we want to FFT the log of the first FFT, without windowing
        cepstral = FFT(logFFT,false);

        return cepstral;
    }
    static final public double[] getCepstral456(double[] dataSet){
        /*
            The data used as described above is named as follows:
            dataSet = sound signal. The sound recorded by the microphone.
            fftOfSignal = . Spectral density of the sound signal, windowed by a hanning function for correctness.
            logFFT  = log of spectral density of sound signal
            cepstral = spectral density of log of spectral density of sound signal
            maxVal - a temporary variable used to iterate through cepstral and find the highest peak
            largestFreq - a variable used to hold the dominant frequency found in cepstral.
            Returns: highest frequency
         */
        double[] FFTOfSignal;
        double[] logFFT;
        double[] cepstral;
        double[] windowedData;
        int dataSetLength = dataSet.length;


        if(!((dataSetLength > 0) && ((dataSetLength & (dataSetLength - 1)) == 0))){
            throw new RuntimeException("n must be a power of 2");
        }

        windowedData = applyHanningWindow(dataSet);
        //We want the FFT of the sound signal, done with windowing applied. Windowing time domain input helps reduce noise.
        FFTOfSignal = FFT(windowedData,true);
        //Then we want the log of this waveform. make it 2x the length to zero-pad for resolution.
        logFFT = new double[FFTOfSignal.length];


        for(int i=0;i<FFTOfSignal.length/2;i++){
            logFFT[i]= Math.log(FFTOfSignal[i]);
        }

        //then we want to FFT the log of the first FFT, without windowing
        cepstral = FFT(logFFT,false);

        return cepstral;
    }

    static final public double[] get_maxFreq_magnitude123(double[] dataSet,int start,int end){
         /*
            The data used as described above is named as follows:
            dataSet = sound signal. The sound recorded by the microphone.
            fftOfSignal = . Spectral density of the sound signal, windowed by a hanning function for correctness.
            logFFT  = log of spectral density of sound signal
            cepstral = spectral density of log of spectral density of sound signal
            maxVal - a temporary variable used to iterate through cepstral and find the highest peak
            largestFreq - a variable used to hold the dominant frequency found in cepstral.
            Returns: highest frequency
         */

        double maxVal = 0;
        double largestFreq;
        int maxi=0;
        int nPointsToAverage = 15;//number of points around peak to average
        double[] nAroundPeak = new double[2*nPointsToAverage+1];
        double averagedPeak = 0;
        for(int i=start;i<end;i++){
            //fftOfCepstrum[i] = Math.abs(fftOfCepstrum[i])*Math.abs(fftOfCepstrum[i]);
            if(dataSet[i]>maxVal){
                maxVal = dataSet[i];
                maxi = i;
            }
        }
        double[] cepstral = dataSet;

        try {
            averagedPeak = weightedAveragePeak(
                    new double[]{

                            cepstral[maxi - 6],
                            cepstral[maxi - 5],
                            cepstral[maxi - 4],
                            cepstral[maxi - 3],
                            cepstral[maxi - 2],
                            cepstral[maxi - 1],
                            cepstral[maxi],
                            cepstral[maxi + 1],
                            cepstral[maxi + 2],
                            cepstral[maxi + 3],
                            cepstral[maxi + 4],
                            cepstral[maxi + 5]

                    }, maxi);
        }catch(java.lang.ArrayIndexOutOfBoundsException e) {
            averagedPeak = cepstral[maxi];
    }


        largestFreq = sampleRate/(averagedPeak);

        double[] ret = new double[2];
        ret[0] = largestFreq;
        ret[1] = maxVal;

        return ret;
    }
    static final public double[] get_maxFreq_magnitude456(double[] dataSet,int start,int end){
         /*
            The data used as described above is named as follows:
            dataSet = sound signal. The sound recorded by the microphone.
            fftOfSignal = . Spectral density of the sound signal, windowed by a hanning function for correctness.
            logFFT  = log of spectral density of sound signal
            cepstral = spectral density of log of spectral density of sound signal
            maxVal - a temporary variable used to iterate through cepstral and find the highest peak
            largestFreq - a variable used to hold the dominant frequency found in cepstral.
            Returns: highest frequency
         */

        double maxVal = 0;
        double largestFreq;
        int maxi=0;
        int nPointsToAverage = 15;//number of points around peak to average
        double[] nAroundPeak = new double[2*nPointsToAverage+1];
        double averagedPeak = 0;
        for(int i=start;i<end;i++){
            //fftOfCepstrum[i] = Math.abs(fftOfCepstrum[i])*Math.abs(fftOfCepstrum[i]);
            if(dataSet[i]>maxVal){
                maxVal = dataSet[i];
                maxi = i;
            }
        }
        double[] cepstral = dataSet;
        if(maxi>15) {
            averagedPeak = weightedAveragePeak(
                    new double[]{
                            cepstral[maxi - 15],
                            cepstral[maxi - 14],
                            cepstral[maxi - 13],
                            cepstral[maxi - 12],
                            cepstral[maxi - 11],
                            cepstral[maxi - 10],
                            cepstral[maxi - 9],
                            cepstral[maxi - 8],
                            cepstral[maxi - 7],
                            cepstral[maxi - 6],
                            cepstral[maxi - 5],
                            cepstral[maxi - 4],
                            cepstral[maxi - 3],
                            cepstral[maxi - 2],
                            cepstral[maxi - 1],
                            cepstral[maxi],
                            cepstral[maxi + 1],
                            cepstral[maxi + 2],
                            cepstral[maxi + 3],
                            cepstral[maxi + 4],
                            cepstral[maxi + 5],
                            cepstral[maxi + 6],
                            cepstral[maxi + 7],
                            cepstral[maxi + 8],
                            cepstral[maxi + 9],
                            cepstral[maxi + 10],
                            cepstral[maxi + 11],
                            cepstral[maxi + 12],
                            cepstral[maxi + 13],
                            cepstral[maxi + 14],
                            cepstral[maxi + 15]
                    }, maxi);
        }else{
            averagedPeak = cepstral[maxi];
            System.out.println("here");
        }


        largestFreq = .5*sampleRate/(averagedPeak);

        double[] ret = new double[2];
        ret[0] = largestFreq;
        ret[1] = maxVal;

        return ret;
    }

    static final public double[] averagedFFT(double dataset[], int windowWidth, int numWindows){
        if (dataset.length % windowWidth != 0) {
            throw new RuntimeException("periodogram window width must be a multiple of sample size," + windowWidth);
        }
        double[] normalizedFrequencyValues = new double[windowWidth/2];
        double[][] intervalPeriodograms = new double [numWindows][windowWidth/2];
        double[] currentWindowData = new double[windowWidth];
        for(int i=0;i<numWindows;i++){
            for(int j=0;j<windowWidth;j++){
                currentWindowData[j] = dataset[i*((dataset.length-windowWidth)/numWindows)+j];
            }
            //index [1] of Periodogram's return is the FFT values. We dont need the normalized Frequencies.
            intervalPeriodograms[i] = FFT(currentWindowData,true);

        }

        //average values
        double[]finalAveragedValues = new double[windowWidth/2];
        for(int i=0;i<windowWidth/2;i++){
            for(int j=0;j<numWindows;j++) {
                finalAveragedValues[i] = finalAveragedValues[i]+intervalPeriodograms[j][i];
            }
            finalAveragedValues[i] = finalAveragedValues[i]/numWindows;
        }
        double[] valuesFreq = new double[windowWidth/2];
        for (int i = 0; i < windowWidth / 2; i++) {
            valuesFreq[i] = finalAveragedValues[i];
        }

        return valuesFreq;
    }

    static final public double weightedAveragePeak(double dataset[], int midPoint){
        //Weighted average of peak considering center point +/- (len/2)-1 points.
        int len = dataset.length;
        double estimatedFrequency = 0;
        double iweighted = 0;
        double ipow = 0;

        for(int i=0;i<len;i++){
            iweighted += dataset[i]*(midPoint+i-((len-1)/2));
            ipow += dataset[i];
        }

        return iweighted/ipow;
    }

    static final public double[] getPlot(double[] dataSet, String type){
        /*
            The data used as described above is named as follows:
            dataSet = sound signal. The sound recorded by the microphone.
            fftOfSignal = . Spectral density of the sound signal, windowed by a hanning function for correctness.
            logFFT  = log of spectral density of sound signal
            cepstral = spectral density of log of spectral density of sound signal
            maxVal - a temporary variable used to iterate through cepstral and find the highest peak
            largestFreq - a variable used to hold the dominant frequency found in cepstral.
            Returns: highest frequency
         */
        double[] FFTOfSignal;
        double[] logFFT;
        double[] cepstral;
        double[] windowedData;
        int dataSetLength = dataSet.length;



        if(!((dataSetLength > 0) && ((dataSetLength & (dataSetLength - 1)) == 0))){
            throw new RuntimeException("n must be a power of 2");
        }

        windowedData = applyHanningWindow(dataSet);
        //We want the FFT of the sound signal, done with windowing applied. Windowing time domain input helps reduce noise.
        FFTOfSignal = FFT(windowedData,true);
        //Then we want the log of this waveform. make it 2x the length to zero-pad for resolution.
        logFFT = new double[FFTOfSignal.length];


        for(int i=0;i<FFTOfSignal.length;i++){
            logFFT[i]= Math.log(FFTOfSignal[i]);
        }
        logFFT = firfilterDCBlocker(logFFT);
        //then we want to FFT the log of the first FFT, without windowing
        cepstral = FFT(logFFT,false);


        if(type == "cepstral"){
            int start = 90;
            int end = 270;

            double[] ret = new double[end-start];
            for(int i=start;i<end;i++){
                ret[i-start] = cepstral[i];
            }
            return ret;

        }else{
            int start = 0;
            int end = logFFT.length;

            double[] ret = new double[end-start];
            for(int i=start;i<end;i++){
                ret[i-start] = logFFT[i];
            }
            return ret;
        }




    }





    static final public double[][] getPlotSAVE(double[] dataSet, int sampleRate,int start,int end){
        /*
            The data used as described above is named as follows:
            dataSet = sound signal. The sound recorded by the microphone.
            fftOfSignal = . Spectral density of the sound signal, windowed by a hanning function for correctness.
            logFFT  = log of spectral density of sound signal
            cepstral = spectral density of log of spectral density of sound signal
            maxVal - a temporary variable used to iterate through cepstral and find the highest peak
            largestFreq - a variable used to hold the dominant frequency found in cepstral.
            Returns: highest frequency
         */
        double[] FFTOfSignal;
        double[] logFFT;
        double[] cepstral;
        double maxVal = 0;
        double largestFreq = 0.;
        double[] windowedData;
        int maxi=0;
        int dataSetLength = dataSet.length;
        int nPointsToAverage = 15;//number of points around peak to average
        double[] nAroundPeak = new double[2*nPointsToAverage+1];


        if(!((dataSetLength > 0) && ((dataSetLength & (dataSetLength - 1)) == 0))){
            throw new RuntimeException("n must be a power of 2");
        }

        windowedData = applyHanningWindow(dataSet);
        //We want the FFT of the sound signal, done with windowing applied. Windowing time domain input helps reduce noise.
        FFTOfSignal = FFT(windowedData,true);
        //Then we want the log of this waveform
        logFFT = new double[2*FFTOfSignal.length];
        System.out.println(FFTOfSignal.length);

        for(int i=0;i<FFTOfSignal.length/2;i++){
            logFFT[i]= Math.log(FFTOfSignal[i]);
        }

        //then we want to FFT the log of the first FFT, without windowing
        cepstral = FFT(logFFT,false);

        for(int i=start;i<end;i++){
            //fftOfCepstrum[i] = Math.abs(fftOfCepstrum[i])*Math.abs(fftOfCepstrum[i]);
            if(cepstral[i]>maxVal){
                maxVal = cepstral[i];
                maxi = i;
            }
        }


        for(int i=0; i< 2*nPointsToAverage; i++){
            nAroundPeak[i] = cepstral[maxi-i-15];
        }

        double averagedPeak = weightedAveragePeak(
                new double[]{
                        cepstral[maxi-15],
                        cepstral[maxi-14],
                        cepstral[maxi-13],
                        cepstral[maxi-12],
                        cepstral[maxi-11],
                        cepstral[maxi-10],
                        cepstral[maxi-9],
                        cepstral[maxi-8],
                        cepstral[maxi-7],
                        cepstral[maxi-6],
                        cepstral[maxi-5],
                        cepstral[maxi-4],
                        cepstral[maxi-3],
                        cepstral[maxi-2],
                        cepstral[maxi-1],
                        cepstral[maxi],
                        cepstral[maxi+1],
                        cepstral[maxi+2],
                        cepstral[maxi+3],
                        cepstral[maxi+4],
                        cepstral[maxi+5],
                        cepstral[maxi+6],
                        cepstral[maxi+7],
                        cepstral[maxi+8],
                        cepstral[maxi+9],
                        cepstral[maxi+10],
                        cepstral[maxi+11],
                        cepstral[maxi+12],
                        cepstral[maxi+13],
                        cepstral[maxi+14],
                        cepstral[maxi+15]
                }, maxi);


        largestFreq = 44100./(averagedPeak);


        double[][] ret = new double[2][end-start];
        //now we want to pick bin holding the highest peak in the cepstral domain
        for(int i=start;i<end;i++){
            ret[0][i-start] = cepstral[i];
            ret[1][i-start] = largestFreq;
        }

//        double[][] ret = new double[2][logFFT.length];
//        //now we want to pick bin holding the highest peak in the cepstral domain
//        for(int i=0;i<logFFT.length;i++){
//            ret[0][i] = logFFT[i];
//            ret[1][i] = 2*largestFreq;
//        }

        return ret;
    }
}