package com.example.alarmapp

import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.alarmapp.ui.theme.AlarmAppTheme
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : ComponentActivity() {

    private lateinit var textViewTemperature: TextView
    private lateinit var textViewHumidity: TextView
    private lateinit var textViewAlarmStatus: TextView
    private lateinit var databaseRef: DatabaseReference
    private lateinit var ringtone: Ringtone


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textViewTemperature = findViewById(R.id.textViewTemperature);
        textViewHumidity = findViewById(R.id.textViewHumidity);
        textViewAlarmStatus = findViewById(R.id.textViewAlarmStatus);

        databaseRef = FirebaseDatabase.getInstance().getReference("sensorData")

        val notification: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        ringtone = RingtoneManager.getRingtone(applicationContext,notification)

        databaseRef.addValueEventListener(object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.M)
            override fun onDataChange(dataSnapshot: DataSnapshot){
                val temperature = dataSnapshot.child("temperature").getValue(Double::class.java)
                val humidity = dataSnapshot.child("humidity").getValue(Double::class.java)


                textViewTemperature.text = "Temperature : $temperature°C"
                textViewHumidity.text = "Humidity: $humidity%"


                if (temperature != null && temperature > 30) {
                    triggerAlarm()
                } else {
                    stopAlarm()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun stopAlarm() {
        textViewAlarmStatus.text = "Alarm Status: Normal"
        textViewAlarmStatus.setTextColor(getColor(android.R.color.holo_green_dark))

        if (ringtone.isPlaying) {
            ringtone.stop()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun triggerAlarm() {
        textViewAlarmStatus.text = "Alarm Status: ALERT!"
        textViewAlarmStatus.setTextColor(getColor(android.R.color.holo_red_dark))

        if (!ringtone.isPlaying) {
            ringtone.play()
        }
    }
}

