package com.example.speechease.ui.practicedetail

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.speechease.R
import com.example.speechease.data.pref.UserPreference
import com.example.speechease.data.pref.dataStore
import com.example.speechease.data.repository.UserRepository
import com.example.speechease.data.retrofit.ApiConfig
import com.example.speechease.data.retrofit.ApiService
import com.example.speechease.databinding.ActivityPracticeDetailBinding
import com.example.speechease.di.Injection
import com.example.speechease.ui.ViewModelFactory
import kotlinx.coroutines.launch
import okhttp3.internal.and
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlin.concurrent.thread

class PracticeDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPracticeDetailBinding
    private lateinit var audioRecord: AudioRecord
    private lateinit var audioFile: String

    private var isRecording = false // Flag untuk status perekaman

    // Inisialisasi UserPreference
    private val userPreference: UserPreference by lazy {
        UserPreference.getInstance(dataStore)
    }

    // Inisialisasi ApiService
    private val apiService: ApiService by lazy {
        /*ApiConfig.getApiService(applicationContext)*/
        val userPreference = UserPreference.getInstance(applicationContext.dataStore)
        ApiConfig.getApiService(applicationContext, userPreference)
    }

    // Inisialisasi UserRepository
    private val userRepository: UserRepository by lazy {
        UserRepository.getInstance(this, apiService) // Diperbarui: Memberikan context dan apiService
    }

    // Inisialisasi ViewModel dengan ViewModelFactory
    //private lateinit var viewModel: PracticeDetailViewModel
    private val viewModel: PracticeDetailViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPracticeDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Membuat Instance
        val userRepository = Injection.provideRepository(this)
        //val viewModelFactory = ViewModelFactory(userRepository)
        //viewModel = ViewModelProvider(this, viewModelFactory)[PracticeDetailViewModel::class.java]*/
        //viewModel = ViewModelProvider(this, ViewModelFactory.getInstance(this))[PracticeDetailViewModel::class.java]

        binding.btnMic.setOnClickListener {
            if (isRecording) {
                stopRecording()
            } else {
                startRecording()
            }
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.uploadStatus.observe(this) { status ->
            Toast.makeText(this, status, Toast.LENGTH_SHORT).show()
        }

        viewModel.predictedLabel.observe(this) { label ->
            binding.tvDetection.text = label
        }
    }

    private fun startRecording() {
        try {
            // Ubah ekstensi file ke .wav
            audioFile = "${cacheDir.absolutePath}/audio_${System.currentTimeMillis()}.wav"

            val bufferSize = AudioRecord.getMinBufferSize(
                RECORDER_SAMPLE_RATE,
                RECORDER_CHANNELS,
                RECORDER_AUDIO_ENCODING
            )

            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.RECORD_AUDIO
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            audioRecord = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                RECORDER_SAMPLE_RATE,
                RECORDER_CHANNELS,
                RECORDER_AUDIO_ENCODING,
                bufferSize
            )

            audioRecord.startRecording()
            isRecording = true

            binding.btnMic.setIconResource(R.drawable.baseline_stop_24)
            Toast.makeText(this, "Merekam suara...", Toast.LENGTH_SHORT).show()

            // Mulai thread untuk menulis data audio ke file WAV
            thread(true) {
                writeAudioDataToFile(audioFile, bufferSize)
            }

        } catch (e: Exception) {
            Log.e("PracticeDetailActivity", "Gagal merekam audio: ${e.message}", e)
            Toast.makeText(this, "Gagal merekam audio.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun stopRecording() {
        try {
            if (::audioRecord.isInitialized) {
                isRecording = false
                audioRecord.stop()
                audioRecord.release()
            }

            // Ubah ikon tombol kembali ke mic
            binding.btnMic.setIconResource(R.drawable.baseline_mic_24)

            Toast.makeText(this, "Perekaman selesai.", Toast.LENGTH_SHORT).show()

            // Kirim audio ke ViewModel untuk diunggah dalam coroutine
            lifecycleScope.launch {
                viewModel.uploadAudio(audioFile)
            }
        } catch (e: Exception) {
            Log.e("PracticeDetailActivity", "Perekaman gagal dihentikan: ${e.message}", e)
            Toast.makeText(this, "Perekaman gagal dihentikan.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun writeAudioDataToFile(path: String, bufferSize: Int) {
        val sData = ShortArray(BufferElements2Rec)
        var os: FileOutputStream? = null
        try {
            os = FileOutputStream(path)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        val data = arrayListOf<Byte>()

        // Menambahkan header WAV
        for (byte in wavFileHeader()) {
            data.add(byte)
        }

        while (isRecording) {
            // Membaca data dari mikrofon ke array sData
            audioRecord.read(sData, 0, BufferElements2Rec)
            try {
                val bData = short2byte(sData)
                for (byte in bData) {
                    data.add(byte)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        updateHeaderInformation(data)

        os?.write(data.toByteArray())

        try {
            os?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun short2byte(sData: ShortArray): ByteArray {
        val arrSize = sData.size
        val bytes = ByteArray(arrSize * 2)
        for (i in 0 until arrSize) {
            bytes[i * 2] = (sData[i] and 0x00FF).toByte()
            bytes[i * 2 + 1] = (sData[i].toInt() shr 8).toByte()
            sData[i] = 0
        }
        return bytes
    }

    private fun wavFileHeader(): ByteArray {
        val headerSize = 44
        val header = ByteArray(headerSize)

        header[0] = 'R'.code.toByte() // RIFF/WAVE header
        header[1] = 'I'.code.toByte()
        header[2] = 'F'.code.toByte()
        header[3] = 'F'.code.toByte()

        header[4] = (0 and 0xff).toByte() // Size of the overall file, 0 because unknown
        header[5] = (0 shr 8 and 0xff).toByte()
        header[6] = (0 shr 16 and 0xff).toByte()
        header[7] = (0 shr 24 and 0xff).toByte()

        header[8] = 'W'.code.toByte()
        header[9] = 'A'.code.toByte()
        header[10] = 'V'.code.toByte()
        header[11] = 'E'.code.toByte()

        header[12] = 'f'.code.toByte() // 'fmt ' chunk
        header[13] = 'm'.code.toByte()
        header[14] = 't'.code.toByte()
        header[15] = ' '.code.toByte()

        header[16] = 16 // Length of format data
        header[17] = 0
        header[18] = 0
        header[19] = 0

        header[20] = 1 // Type of format (1 is PCM)
        header[21] = 0

        header[22] = NUMBER_CHANNELS.toByte()
        header[23] = 0

        header[24] = (RECORDER_SAMPLE_RATE and 0xff).toByte() // Sampling rate
        header[25] = (RECORDER_SAMPLE_RATE shr 8 and 0xff).toByte()
        header[26] = (RECORDER_SAMPLE_RATE shr 16 and 0xff).toByte()
        header[27] = (RECORDER_SAMPLE_RATE shr 24 and 0xff).toByte()

        header[28] = (BYTE_RATE and 0xff).toByte() // Byte rate = (Sample Rate * BitsPerSample * Channels) / 8
        header[29] = (BYTE_RATE shr 8 and 0xff).toByte()
        header[30] = (BYTE_RATE shr 16 and 0xff).toByte()
        header[31] = (BYTE_RATE shr 24 and 0xff).toByte()

        header[32] = (NUMBER_CHANNELS * BITS_PER_SAMPLE / 8).toByte() // Block align
        header[33] = 0

        header[34] = BITS_PER_SAMPLE.toByte() // Bits per sample
        header[35] = 0

        header[36] = 'd'.code.toByte()
        header[37] = 'a'.code.toByte()
        header[38] = 't'.code.toByte()
        header[39] = 'a'.code.toByte()

        header[40] = (0 and 0xff).toByte() // Size of the data section
        header[41] = (0 shr 8 and 0xff).toByte()
        header[42] = (0 shr 16 and 0xff).toByte()
        header[43] = (0 shr 24 and 0xff).toByte()

        return header
    }

    private fun updateHeaderInformation(data: ArrayList<Byte>) {
        val fileSize = data.size
        val contentSize = fileSize - 44

        data[4] = (fileSize and 0xff).toByte() // Size of the overall file
        data[5] = (fileSize shr 8 and 0xff).toByte()
        data[6] = (fileSize shr 16 and 0xff).toByte()
        data[7] = (fileSize shr 24 and 0xff).toByte()

        data[40] = (contentSize and 0xff).toByte() // Size of the data section
        data[41] = (contentSize shr 8 and 0xff).toByte()
        data[42] = (contentSize shr 16 and 0xff).toByte()
        data[43] = (contentSize shr 24 and 0xff).toByte()
    }

    companion object {
        private const val RECORDER_SAMPLE_RATE = 16000 // Sampling rate (Hz)
        private const val RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO // Audio channel configuration
        private const val RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT // Audio encoding format
        private const val BITS_PER_SAMPLE = 16
        private const val NUMBER_CHANNELS = 1
        private const val BYTE_RATE = RECORDER_SAMPLE_RATE * BITS_PER_SAMPLE * NUMBER_CHANNELS / 8
        private const val BufferElements2Rec = 2048 // Number of short elements to read at a time
    }
}