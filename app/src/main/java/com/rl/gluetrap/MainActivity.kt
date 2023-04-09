package com.rl.gluetrap

import android.content.ClipboardManager
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.File

const val tag = "NachoNeko"

class MainActivity : AppCompatActivity() {
    private lateinit var clipboardManager: ClipboardManager
    private lateinit var listView: ListView
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var file: File
    private lateinit var list: MutableList<String>
    private  var previousClip: String? = null

    private fun writeRats() {
        var temp = ""
        for(i in list) {
            temp += "$i\n"
        }
        temp = temp.trim('\n')
        file.writeText(temp)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        clipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager

        file = File(filesDir, "rats.txt")
        if(file.exists() && file.readText() != "") {
            list = file.readText().split('\n').toMutableList()
        }
        else {
            file.writeText("")
            list = mutableListOf()
        }

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, list)
        listView = findViewById(R.id.list)
        listView.adapter = adapter

        listView.setOnItemClickListener { _, _, i, _ ->
            val uri: Uri = Uri.parse(list[i])
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener {
            list.clear()
            adapter.notifyDataSetChanged()
            writeRats()
            Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun onResume() {
        super.onResume()

        listView.post {
            val clipData = clipboardManager.primaryClip

            if(clipData == null) {
                Toast.makeText(this, "Null", Toast.LENGTH_SHORT).show()
            }
            else {
                val theClip = clipData.getItemAt(0).text.toString()

                try {
                    if(previousClip==theClip || list.last()==theClip) {
                        Toast.makeText(this, "Repetitive", Toast.LENGTH_SHORT).show()
                    }
                    else {
                        throw Exception()
                    }
                }
                catch(e: Exception) {
                    Toast.makeText(this, "New", Toast.LENGTH_SHORT).show()

                    list.add(theClip)
                    adapter.notifyDataSetChanged()
                    writeRats()
                }
                previousClip = theClip
            }
        }
    }
}