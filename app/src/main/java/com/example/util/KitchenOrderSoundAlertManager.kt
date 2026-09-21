package com.example.util

import android.content.Context
import android.media.AudioManager
import android.media.Ringtone
import android.media.RingtoneManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import com.example.data.models.OrderEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Data payload for real-time order cancellation siren alert to kitchen
 */
data class CancellationAlertData(
    val order: OrderEntity,
    val reason: String,
    val policyExplanation: String,
    val customerRefund: Double,
    val kitchenSettlement: Double,
    val companyFund: Double,
    val isSameDay: Boolean
)

/**
 * Manages real-time high-urgency audible alarms and ringtones for Kitchen Partners
 * when a new catering order arrives or when an order is cancelled.
 * Continues alerting loudly until explicitly Accepted, Rejected, or Muted.
 */
object KitchenOrderSoundAlertManager {
    private const val TAG = "KitchenOrderSoundAlert"

    private val _isAlertActive = MutableStateFlow(false)
    val isAlertActive: StateFlow<Boolean> = _isAlertActive.asStateFlow()

    private val _isSoundMuted = MutableStateFlow(false)
    val isSoundMuted: StateFlow<Boolean> = _isSoundMuted.asStateFlow()

    private val _currentAlertOrder = MutableStateFlow<OrderEntity?>(null)
    val currentAlertOrder: StateFlow<OrderEntity?> = _currentAlertOrder.asStateFlow()

    private val _currentCancellationAlert = MutableStateFlow<CancellationAlertData?>(null)
    val currentCancellationAlert: StateFlow<CancellationAlertData?> = _currentCancellationAlert.asStateFlow()

    private var currentRingtone: Ringtone? = null
    private var toneGenerator: ToneGenerator? = null
    private var vibrator: Vibrator? = null
    private var loopJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Main)

    /**
     * Start playing persistent high-alert order sound for incoming new order
     */
    fun triggerOrderAlert(context: Context, order: OrderEntity) {
        _currentAlertOrder.value = order
        _isAlertActive.value = true
        _isSoundMuted.value = false
        startSound(context, isEmergency = false)
    }

    /**
     * Start playing loud emergency siren alert when an order is cancelled by customer
     */
    fun triggerCancellationAlert(context: Context, alertData: CancellationAlertData) {
        _currentCancellationAlert.value = alertData
        _isAlertActive.value = true
        _isSoundMuted.value = false
        startSound(context, isEmergency = true)
    }

    private fun startSound(context: Context, isEmergency: Boolean = false) {
        stopSoundOnly()
        try {
            // 1. Play high-priority system ringtone/alarm
            val alertUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

            currentRingtone = RingtoneManager.getRingtone(context.applicationContext, alertUri)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                currentRingtone?.isLooping = true
            }
            currentRingtone?.play()

            // 2. High-urgency physical vibration pattern
            try {
                val vib = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                    vm?.defaultVibrator
                } else {
                    @Suppress("DEPRECATION")
                    context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                }
                vibrator = vib
                val pattern = longArrayOf(0, 500, 200, 500, 200, 500, 600)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vib?.vibrate(VibrationEffect.createWaveform(pattern, 0)) // 0 = loop until cancelled
                } else {
                    @Suppress("DEPRECATION")
                    vib?.vibrate(pattern, 0)
                }
            } catch (_: Exception) {}

            // 3. Concurrently run high-alert piercing tone loop (Zomato/Swiggy style kitchen order chime)
            loopJob = scope.launch {
                try {
                    val tone = ToneGenerator(AudioManager.STREAM_ALARM, 100)
                    toneGenerator = tone
                    while (_isAlertActive.value && !_isSoundMuted.value) {
                        if (isEmergency) {
                            // Emergency cancellation alternating siren
                            tone.startTone(ToneGenerator.TONE_CDMA_EMERGENCY_RINGBACK, 500)
                            delay(550)
                            tone.startTone(ToneGenerator.TONE_PROP_BEEP2, 350)
                            delay(400)
                        } else {
                            // High Alert Kitchen Incoming Order Tone: rapid 3-tone chime sequence
                            tone.startTone(ToneGenerator.TONE_CDMA_HIGH_L, 260)
                            delay(280)
                            tone.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 260)
                            delay(280)
                            tone.startTone(ToneGenerator.TONE_PROP_BEEP2, 320)
                            delay(350)
                            tone.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 250)
                            delay(280)
                            tone.startTone(ToneGenerator.TONE_CDMA_HIGH_L, 400)
                            delay(1100)
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Tone loop exception: ${e.message}")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error starting alert sound: ${e.message}")
        }
    }

    /**
     * Mute the audio tone and stop vibration, but keep the Accept/Reject popup visible
     */
    fun muteSoundOnly() {
        _isSoundMuted.value = true
        stopSoundOnly()
    }

    /**
     * Dismiss cancellation alert and stop alarm siren
     */
    fun dismissCancellationAlert() {
        _currentCancellationAlert.value = null
        stopAlert()
    }

    /**
     * Stop sound, vibration and clear active alerts
     */
    fun stopAlert() {
        _isAlertActive.value = false
        _isSoundMuted.value = false
        _currentAlertOrder.value = null
        _currentCancellationAlert.value = null
        stopSoundOnly()
    }

    private fun stopSoundOnly() {
        try {
            loopJob?.cancel()
            loopJob = null
            toneGenerator?.release()
            toneGenerator = null
            currentRingtone?.stop()
            currentRingtone = null
            vibrator?.cancel()
            vibrator = null
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping sound: ${e.message}")
        }
    }

    /**
     * Play a brief test chime (3 seconds) for the kitchen manager to verify phone volume
     */
    fun playTestChime(context: Context) {
        scope.launch {
            try {
                val notifUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                val testRingtone = RingtoneManager.getRingtone(context.applicationContext, notifUri)
                testRingtone?.play()

                val tone = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
                tone.startTone(ToneGenerator.TONE_PROP_BEEP, 300)
                delay(350)
                tone.startTone(ToneGenerator.TONE_PROP_BEEP2, 400)
                delay(450)
                tone.startTone(ToneGenerator.TONE_PROP_ACK, 500)
                delay(600)
                tone.release()
            } catch (e: Exception) {
                Log.e(TAG, "Test sound exception: ${e.message}")
            }
        }
    }
}
