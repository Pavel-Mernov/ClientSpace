package com.example.clientspace

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

object MyVibrator {

    fun vibrationEffect(context: Context, milliseconds : Long) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // Используем VibratorManager для устройств с API 31+
            val vibratorManager = context.getSystemService(VibratorManager::class.java)
            val vibrator = vibratorManager?.defaultVibrator

            val effect = VibrationEffect.createOneShot(
                milliseconds, // Длительность вибрации в миллисекундах
                VibrationEffect.DEFAULT_AMPLITUDE // Амплитуда вибрации
            )
            vibrator?.vibrate(effect)
        } else {
            // Используем Vibrator для устройств с API ниже 31
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            val effect = VibrationEffect.createOneShot(
                milliseconds,
                VibrationEffect.DEFAULT_AMPLITUDE
            )
            vibrator.vibrate(effect)
        }
    }

}