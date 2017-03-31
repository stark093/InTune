package capstone.splash;

/**
 * Created by Nick on 2016-11-14.
 *
 * Retrieves data from microphone and sends to AudioDataManager for handling
 *
 */

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Build;
import android.support.annotation.RequiresApi;

public class Audio_Record_Implementation {
    private static final int audio_sample_rate = 44100;
    private static final int audio_source = MediaRecorder.AudioSource.MIC;
    private static final int audio_channel = AudioFormat.CHANNEL_IN_MONO;
    private static final int audio_encoding = AudioFormat.ENCODING_PCM_16BIT;
    private static int audio_buffer_size = AudioRecord.getMinBufferSize(audio_sample_rate,audio_channel,audio_encoding);
    private static boolean shouldRecord = false;
    private double[] accessible_audio_data;
    private AudioRecord audio_record;
    private Thread saveAudioDataThread = null;

    /*
        Using 16-bit encoding, so   audio_buffer_size/2 = #samples/Buffer

                                    time per sample = #samples/buffer / (audio_sample_rate)
     */




    public void startRecording(int buffSize){
        if(buffSize<=1){
            //Do nothing to audio buffer size. Leave it as 1*buffer size
        }else{
            audio_buffer_size = buffSize;
        }

        shouldRecord = true;
        record();
    }


    public double[] getAudioData(){
        return accessible_audio_data;
    }



    public void stopRecording(){
        shouldRecord = false;
    }

    private void record(){
        //Start a seperate thread to save audio data
        saveAudioDataThread = new Thread(new Runnable(){
            @RequiresApi(api = Build.VERSION_CODES.M)
            public void run(){
                android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_AUDIO);
                int count = 0;
                int sizeOfShortArray;

                if (audio_buffer_size == AudioRecord.ERROR || audio_buffer_size == AudioRecord.ERROR_BAD_VALUE){
                    audio_buffer_size = 2* audio_sample_rate;
                }

                sizeOfShortArray = audio_buffer_size/2;

                short[] internal_audio_data = new short[sizeOfShortArray];

                audio_record = new AudioRecord(
                        audio_source,
                        audio_sample_rate,
                        audio_channel,
                        audio_encoding,
                        audio_buffer_size
                );

                audio_record.startRecording();

                while(shouldRecord){
                    audio_record.read(internal_audio_data,0,internal_audio_data.length);
                    accessible_audio_data = makeDouble(internal_audio_data);
                }

                audio_record.stop();
                audio_record.release();
            }

        }, "Thread to save audio data"
        );

        saveAudioDataThread.start();
    }

    private double[] makeDouble(short[] data) {
        double[] returnedArray = new double[data.length];
        for (int i = 0; i < returnedArray.length; i++) {
            returnedArray[i] = data[i];
        }
        return returnedArray;
    }

    public int getSampleRate(){
        return audio_sample_rate;
    }

    public int getAudioBufferSize(){
        return audio_buffer_size;
    }


}
