package com.example.clientspace

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.ComponentActivity
import com.example.clientspace.ui.UserRepository

class MainActivity : ComponentActivity() {
    private var inputDigits = emptyList<Char>().toMutableList()
    private val maxInputLength = 6

    override fun onCreate(savedInstanceState: Bundle?) {
        try {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.authorization_window)
        }
        catch (e : Exception) {
            Log.e("error", e.message ?: "")
        }

        // Init user repository
        UserRepository.initialize(this)

        // Default User Id
        val userId = UserRepository.defaultUserId

        // Getting Default user
        val user = UserRepository.findUserById(userId)

        if (user == null) {
            val exString = "user not found"
            Log.e("user", exString)
            throw Exception(exString)
        }

        // Find all points
        val circles = listOf(
            findViewById<View>(R.id.circle0),
            findViewById(R.id.circle1),
            findViewById(R.id.circle2),
            findViewById(R.id.circle3),
            findViewById(R.id.circle4),
            findViewById(R.id.circle5)
        )

        // Find buttons
        val buttons = listOf(
            findViewById<Button>(R.id.btn0),
            findViewById(R.id.btn1),
            findViewById(R.id.btn2),
            findViewById(R.id.btn3),
            findViewById(R.id.btn4),
            findViewById(R.id.btn5),
            findViewById(R.id.btn6),
            findViewById(R.id.btn7),
            findViewById(R.id.btn8),
            findViewById(R.id.btn9)
        )

        val tvErrorCode = findViewById<TextView>(R.id.tvErrorCode)

        /*
        if (tvErrorCode == null) {
            Log.e("tvError", "null")
        }
        else {
            Log.e("tvError", "not null")
        }

         */



        // Set handler to all buttons
        buttons.indices.forEach { index ->
            val button = buttons[index]
            button.setOnClickListener {
                if (inputDigits.size < maxInputLength) {
                    inputDigits.add('0' + index)
                    updateCirclesAndError(circles, inputDigits.size, tvErrorCode)
                }
                if (inputDigits.size == maxInputLength) {


                    if (user.enterCode == String(inputDigits.toCharArray())) {
                        val intent = Intent(this, ChatListActivity::class.java).apply {
                            putExtra("curUserId", userId)
                        }
                        try {
                            startActivity(intent)
                        } catch (ex: Throwable) {
                            ex.message?.let { it1 -> Log.v("Transition to chat list", it1) }
                        }

                    }
                    else {
                        // updateErrorCircles(circles)
                        tvErrorCode.visibility = View.VISIBLE

                        // Vibration effect
                        MyVibrator.vibrationEffect(this, 500)

                        // tvErrorCode.visibility = View.INVISIBLE
                        inputDigits.clear()
                        // updateCirclesAndError(circles, 0, tvErrorCode)
                    }


                }
            }
        }

        // Last char deletion
        findViewById<ImageView>(R.id.btnDelete).setOnClickListener {
            if (tvErrorCode.visibility == View.VISIBLE) {
                updateCirclesAndError(circles, 0, tvErrorCode)

            }
            else if (inputDigits.size > 0) {
                inputDigits.removeLast()
                updateCirclesAndError(circles, inputDigits.size, tvErrorCode)
            }
        }
    }

    // Function for updating view of points
    private fun updateCirclesAndError(circles: List<View>, count: Int, tvErrorCode : TextView) {
        tvErrorCode.visibility = View.INVISIBLE

        circles.forEachIndexed { index, view ->
            if (index < count) {
                view.setBackgroundResource(R.drawable.circle_filled) // Filled dot
            } else {
                view.setBackgroundResource(R.drawable.circle_empty) // Empty dot
            }
        }
    }

    /*
    private fun updateErrorCircles(circles: List<View>) {
        circles.forEach { view ->
            view.setBackgroundResource(R.drawable.circle_error) // Colored dot
        }
    }

     */

}

