package com.example.myapplication

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem

import android.widget.TextView
import android.widget.Button

import kotlinx.android.synthetic.main.activity_main.*

import java.net.Inet4Address
import java.net.NetworkInterface
import java.net.ServerSocket

import java.io.InputStreamReader
import java.io.BufferedReader
import android.util.Log
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        var stopped = false

        val startServerButton = findViewById<Button>(R.id.startServer)
        val serverSocket = ServerSocket(4200)
        startServerButton.setOnClickListener {
            stopped = false
            launch {
                Log.d("SERVER:", "Uped")
                val socket = serverSocket.accept()
                val inputStream = socket.getInputStream()
                val bufferedReader = BufferedReader(InputStreamReader(inputStream))
                while (!stopped && inputStream.available() == 0) {
                    val received = bufferedReader.readLine()
                    Log.d("SERVER:", received)
                    Log.d("SERVER:", stopped.toString())
                }
                socket.close()
                Log.d("SERVER:", socket.isBound.toString() )
            }
        }

        val stopServerButton = findViewById<Button>(R.id.stopServer)
        stopServerButton.setOnClickListener {
            Log.d("SERVER:", "start to stopped")
            stopped = true
        }

        val searchButton = findViewById<Button>(R.id.searchServer)
        searchButton.setOnClickListener {
        }

        var addresses = arrayListOf<String>()
        val en = NetworkInterface.getNetworkInterfaces()
        while (en.hasMoreElements()) {
            val intf = en.nextElement()
            val enumIpAddr = intf.inetAddresses
            while (enumIpAddr.hasMoreElements()) {
                try {
                    val inetAddress = enumIpAddr.nextElement()
                    val networkInterface = NetworkInterface.getByInetAddress(inetAddress)
                    if (!inetAddress.isLoopbackAddress && inetAddress is Inet4Address) {
                        val subnet = networkInterface.interfaceAddresses[1].networkPrefixLength
                        addresses.add(inetAddress.getHostAddress() + "/" + subnet.toString())
                    }
                } catch (e: Exception) {
                }
            }
        }

        val ipAddress = findViewById<TextView>(R.id.ipAddress)
        ipAddress.text = addresses.joinToString(":")
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
