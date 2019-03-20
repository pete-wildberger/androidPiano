package com.example.PianoApp

import android.annotation.SuppressLint
import android.media.AudioFormat
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.media.AudioTrack
import android.media.AudioFormat.ENCODING_PCM_16BIT
import android.media.AudioFormat.CHANNEL_OUT_MONO
import android.media.AudioManager
import android.media.AudioFormat.ENCODING_PCM_8BIT
import android.os.Build
import android.media.AudioAttributes
import android.R.layout.*
import android.os.Handler
import com.example.PianoApp.R
import kotlin.experimental.and

private val TAG = "MyActivity"

class MainActivity : AppCompatActivity() {

    private val duration = 1 // seconds
    private val sampleRate = 44100
    private val numSamples = duration * sampleRate
    private val sample = DoubleArray(numSamples)
    private val freqOfTone = 440.0 // hz



    private var handler = Handler()

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        val thread = Thread(Runnable {
//            genTone()
//            handler.post(Runnable { playSound() })
//        })

        val track: AudioTrack = genTrack(genTone(freqOfTone))
        val r = resources
        val name = packageName
        var ref = findViewById<Button>(r.getIdentifier("b3", "id", name))
        ref.setOnClickListener {
            ref.isSoundEffectsEnabled = false
//            thread.start()
            playSound(track)
        }
    }

    private fun genTone(freq: Double): ByteArray {
        val generatedSnd = ByteArray(2 * numSamples)
        // fill out the array
        for (i in 0 until numSamples) {
            // sample[i] = Math.sin(2 * Math.PI * i / (sampleRate/freqOfTone));
            sample[i] = Math.sin((2 * Math.PI - .001) * i / (sampleRate / freq))
        }
        // convert to 16 bit pcm sound array
        // assumes the sample buffer is normalised.
        var idx = 0
        for (dVal in sample) {
            val value = (dVal * 32767).toShort()
            val sixteen: Short = (0xff00).toShort()
            // in 16 bit wav PCM, first byte is the low order byte
            generatedSnd[idx++] = (value and 0x00ff).toByte()
            generatedSnd[idx++] = (value and sixteen).toInt().ushr(8).toByte()
        }
        return generatedSnd
    }
    @SuppressLint("NewApi")
    private fun genTrack(generatedSnd: ByteArray): AudioTrack {
        var mAudioTrack: AudioTrack
        // AudioTrack definition
        val mBufferSize = AudioTrack.getMinBufferSize(
            44100,
            AudioFormat.CHANNEL_OUT_STEREO,
            AudioFormat.ENCODING_PCM_16BIT
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mAudioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(44100)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_STEREO)
                        .build()
                )
//                .setTransferMode(AudioTrack.MODE_STATIC)
                .setBufferSizeInBytes(mBufferSize)
                .build()
            mAudioTrack.setVolume(1.0F)
        }else {
            mAudioTrack = AudioTrack(
                AudioManager.STREAM_MUSIC, 44100,
                AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT,
                mBufferSize, AudioTrack.MODE_STATIC
            )
        }
        mAudioTrack.write(generatedSnd, 0, numSamples)
        return mAudioTrack
    }


    private fun playSound(track: AudioTrack) {
//        track.setLoopPoints(1,mBufferSize, -1)
        Log.v(TAG, track.playState.toString())
        if (track.playState > 1){

            track.stop()
            track.playbackHeadPosition = 0
            Log.v(TAG, track.playState.toString())
            Log.v(TAG, track.playbackHeadPosition.toString())
        }
        track.play()
    }

}
