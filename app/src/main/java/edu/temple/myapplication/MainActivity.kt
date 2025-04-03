package edu.temple.myapplication

import android.content.ComponentName
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.widget.Button
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    private var timerBinder: TimerService.TimerBinder?=null
    private lateinit var timerTextView: TextView
    val handler = Handler(Looper.getMainLooper()){
        timerTextView.text = it.what.toString()
        true
    }
    private val serviceConnection = object: ServiceConnection{
        override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
            timerBinder = p1 as TimerService.TimerBinder
            timerBinder!!.setHandler(handler)
            true
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            timerBinder = null
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val serviceIntent = Intent(this, TimerService::class.java)
        bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE)

       timerTextView = findViewById<TextView>(R.id.textView)

        findViewById<Button>(R.id.startButton).setOnClickListener {
            val binder = timerBinder
            if(binder!=null){
                if(!binder.isRunning && !binder.paused){
                    binder.start(20)
                }else{
                    binder.pause()
                }
            }
        }
        
        findViewById<Button>(R.id.stopButton).setOnClickListener {
            timerBinder?.stop()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(serviceConnection)
    }
}