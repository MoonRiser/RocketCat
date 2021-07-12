package com.example.rocketcat.ui.activity

import android.media.AudioAttributes
import android.media.AudioAttributes.CONTENT_TYPE_MUSIC
import android.media.MediaPlayer
import android.util.Log
import androidx.databinding.Observable
import androidx.databinding.ObservableBoolean
import com.example.common.base.BaseViewModel
import com.example.common.ext.ClickCallback
import com.example.common.ext.showToast

class MainViewModel : BaseViewModel() {

    companion object {
        const val MUSIC_URL =
            "https://www.hrupin.com/wp-content/uploads/mp3/testsong_20_sec.mp3"
    }

    val playOn = ObservableBoolean()
    val enablePlay = ObservableBoolean(false)


    val mediaPlayer: MediaPlayer = MediaPlayer().apply {
        setOnPreparedListener {
            enablePlay.set(true)
        }
        setOnCompletionListener {
            playOn.set(false)
        }
        setAudioAttributes(
            AudioAttributes.Builder()
                .setContentType(CONTENT_TYPE_MUSIC)
                .build()
        )
        setDataSource(MUSIC_URL)
        prepareAsync()
    }


    init {
        playOn.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                Log.i("xres", "ff")
                if (playOn.get()) {
                    mediaPlayer.start()
                } else {
                    mediaPlayer.pause()
                }
            }
        })
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayer.release()
    }

}