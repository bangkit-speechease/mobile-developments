package com.example.speechease.ui.practicedetail

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.speechease.R
import com.example.speechease.databinding.ActivityPracticeDetailBinding
import com.example.speechease.ui.ViewModelFactory
import com.example.speechease.ui.feedback.FeedbackFalseActivity
import com.example.speechease.ui.feedback.FeedbackTrueActivity
import com.example.speechease.ui.practice.PracticeActivity
import kotlinx.coroutines.launch
import okhttp3.internal.and
import java.io.FileOutputStream
import java.io.IOException
import kotlin.concurrent.thread

class PracticeDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPracticeDetailBinding
    private lateinit var audioRecord: AudioRecord
    private lateinit var audioFile: String

    private var isRecording = false // Flag untuk status perekaman

    // Inisialisasi ViewModel dengan ViewModelFactory
    //private lateinit var viewModel: PracticeDetailViewModel
    private val viewModel: PracticeDetailViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPracticeDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val contentId = intent.getStringExtra("CONTENT_ID")
        Log.d("CONTENT_ID","$contentId")
        viewModel.setContentId(contentId)
        contentId?.let {
            viewModel.fetchContentDetail(it)
        }

        binding.backButton.setOnClickListener {
            Intent(this, PracticeActivity::class.java)
            finish()
        }

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

            if (label.isNotEmpty()) {
                Handler(Looper.getMainLooper()).postDelayed({
                    val intent = when (label) {
                        "Bagus, Mari Lanjutkan" -> Intent(this, FeedbackTrueActivity::class.java)
                        else -> Intent(this, FeedbackFalseActivity::class.java)
                    }
                    intent.putExtra("PREDICTED_LABEL", label) // Kirim label ke aktivitas berikutnya
                    startActivity(intent)
                }, 3000) // Delay 3 detik
            }
        }

        viewModel.contentDetail.observe(this) { contentDetail ->
            if (contentDetail != null) {
                binding.tvText.text = contentDetail.textPhrase
                Glide.with(this)
                    .load(contentDetail.imageUrl)
                    .into(binding.imgPractice)
            } else {
                Log.e("PracticeDetailActivity", "Detail konten tidak ditemukan")
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_MIC_PERMISSION) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                Toast.makeText(this, "Izin mikrofon diberikan.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Izin mikrofon ditolak.", Toast.LENGTH_SHORT).show()
            }
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
                Toast.makeText(this, "Izin mikrofon belum diberikan.", Toast.LENGTH_SHORT).show()

                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.RECORD_AUDIO),
                    REQUEST_MIC_PERMISSION
                )
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
                writeAudioDataToFile(audioFile)
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

    private fun writeAudioDataToFile(path: String) {
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
        private const val REQUEST_MIC_PERMISSION = 1
        private const val RECORDER_SAMPLE_RATE = 16000 // Sampling rate (Hz)
        private const val RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO // Audio channel configuration
        private const val RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT // Audio encoding format
        private const val BITS_PER_SAMPLE = 16
        private const val NUMBER_CHANNELS = 1
        private const val BYTE_RATE = RECORDER_SAMPLE_RATE * BITS_PER_SAMPLE * NUMBER_CHANNELS / 8
        private const val BufferElements2Rec = 2048 // Number of short elements to read at a time
    }
}