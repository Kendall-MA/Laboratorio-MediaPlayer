package cr.ac.baselaboratorio

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.documentfile.provider.DocumentFile

class MainActivity : AppCompatActivity() {

    private lateinit var buttonBack: Button
    private lateinit var buttonPlay: Button
    private lateinit var buttonStop: Button
    private lateinit var buttonForward: Button
    private lateinit var songName: TextView

    private lateinit var musicArray: Array<DocumentFile>

    private lateinit var mediaPlayer: MediaPlayer
    private var songNumber: Int = 1

    // val rootTree = DocumentFile.fromTreeUri(this, directoryUri)

    companion object {
        var OPEN_DIRECTORY_REQUEST_CODE = 1
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        buttonBack = findViewById(R.id.buttonBack)
        buttonPlay = findViewById(R.id.buttonPlay)
        buttonStop = findViewById(R.id.buttonStop)
        buttonForward = findViewById(R.id.buttonForward)
        songName = findViewById(R.id.song_name)

        mediaPlayer = MediaPlayer()

        mediaPlayer.setOnPreparedListener {
            buttonPlay?.isEnabled = true
        }

        setOnClickListeners(this)

        var intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        startActivityForResult(intent, OPEN_DIRECTORY_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == OPEN_DIRECTORY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                var directoryUri = data?.data ?: return
                Log.e("Directorio", directoryUri.toString())
                val rootTree = DocumentFile.fromTreeUri(this, directoryUri)

                musicArray = rootTree!!.listFiles()

                for (i in musicArray.indices) {
                    Log.e("Canci??n", musicArray[i].toString())
                    Log.e("Numero de cancion", i.toString())
                }
            }
        }
    }

    private fun setOnClickListeners(context: Context) {
        buttonBack.setOnClickListener {
            try {
                if (songNumber > 1) {
                    songNumber = songNumber - 1
                    mediaPlayer.stop()
                    mediaPlayer = MediaPlayer()
                    mediaPlayer.setDataSource(this, musicArray[songNumber].uri)
                    mediaPlayer.prepare()
                    mediaPlayer.start()
                    songName.text = musicArray[songNumber].name
                    Toast.makeText(context, "Reproduciendo canci??n anterior", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Ya est?? en el inicio de su playlist", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("Error", "Esta en la primer canci??n, no puede retroceder m??s")
            }

        }

        buttonPlay.setOnClickListener {
            try {
                mediaPlayer.setDataSource(this, musicArray[songNumber].uri)
                mediaPlayer.prepare()
                mediaPlayer.start()
                songName.text = musicArray[songNumber].name
                Toast.makeText(context, "Reproduciendo " + musicArray[songNumber].name, Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Log.e("Error", "Ya hay una canci??n en reproducci??n")
            }
        }

        buttonStop.setOnClickListener {
            mediaPlayer.stop()
            mediaPlayer = MediaPlayer()
            songName.text = "No hay canci??n en reproducci??n"
            Toast.makeText(context, "Parando...", Toast.LENGTH_SHORT).show()
        }

        buttonForward.setOnClickListener {
            try {
                if (songNumber < musicArray.size - 1) {
                    songNumber = songNumber + 1
                    mediaPlayer.stop()
                    mediaPlayer = MediaPlayer()
                    mediaPlayer.setDataSource(this, musicArray[songNumber].uri)
                    mediaPlayer.prepare()
                    mediaPlayer.start()
                    songName.text = musicArray[songNumber].name
                    Log.e("Song Number", songNumber.toString())
                    Toast.makeText(context, "Reproduciendo siguiente canci??n", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Ya lleg?? al final de la lista", Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                Log.e("Error", "Est?? en la ??ltima canci??n, no hay m??s adelante")
            }
        }
    }
}