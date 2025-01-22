package com.example.clientspace

import android.app.KeyguardManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.clientspace.ui.User
import com.example.clientspace.ui.UserRepository

class EditCodeActivity : AppCompatActivity() {
    companion object {
        private val REQUEST_CODE_AUTHENTICATION = 1
    }

    private val enteredCode = emptyList<Char>().toMutableList()
    private val savedCode = emptyList<Char>().toMutableList()
    private val maxDigits = 6

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // enableEdgeToEdge()
        setContentView(R.layout.activity_edit_code)

        val keyguardManager = getSystemService(KEYGUARD_SERVICE) as KeyguardManager

        if (keyguardManager.isKeyguardSecure) {
            startAuthentication()
        }

        val userId = intent.getStringExtra("userId") ?: run {
            val exString = "no user id passed"
            Log.e("edit code", exString)
            throw Exception(exString)
        }

        // Getting user from repository
        val user = UserRepository.findUserById(userId) ?: run {
            val exString = "No user with id $userId"
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

        val textInstruction = findViewById<TextView>(R.id.textInstruction)

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
                if (enteredCode.size < maxDigits) {
                    enteredCode.add('0' + index)
                    updateCirclesAndError(circles, enteredCode.size, tvErrorCode)
                }
                if (enteredCode.size == maxDigits) {
                    if (savedCode.isEmpty()) {
                        // saving entered code
                        savedCode.addAll(enteredCode)

                        enteredCode.clear()
                        updateCirclesAndError(circles, 0, tvErrorCode)

                        textInstruction.text = "Повторите код доступа"
                    }
                    else if (String(savedCode.toCharArray()) == String(enteredCode.toCharArray())) {
                        // saving user new enter code
                        user.enterCode = String(enteredCode.toCharArray())
                        UserRepository.updateUser(user)

                        // enter new code finished, going to previous activity
                        finish()

                    }
                    else {
                        // updateErrorCircles(circles)
                        tvErrorCode.visibility = View.VISIBLE

                        // Vibration effect
                        MyVibrator.vibrationEffect(this, 500)

                        // tvErrorCode.visibility = View.INVISIBLE
                        enteredCode.clear()
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
            else if (enteredCode.size > 0) {
                enteredCode.removeLast()
                updateCirclesAndError(circles, enteredCode.size, tvErrorCode)
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

    private fun startAuthentication() {
        val keyguardManager = getSystemService(KeyguardManager::class.java)

        if (keyguardManager.isKeyguardSecure) {
            val intent = keyguardManager.createConfirmDeviceCredentialIntent(
                "Требуется аутентификация",
                ""
            )

            if (intent != null) {
                startActivityForResult(intent, REQUEST_CODE_AUTHENTICATION)
            }
        }
    }

    @Deprecated("This method has been deprecated in favor of using the Activity Result API\n      which brings increased type safety via an {@link ActivityResultContract} and the prebuilt\n      contracts for common intents available in\n      {@link androidx.activity.result.contract.ActivityResultContracts}, provides hooks for\n      testing, and allow receiving results in separate, testable classes independent from your\n      activity. Use\n      {@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)}\n      with the appropriate {@link ActivityResultContract} and handling the result in the\n      {@link ActivityResultCallback#onActivityResult(Object) callback}.")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_AUTHENTICATION) {
            when (resultCode) {
                RESULT_OK -> {
                    // Authentication success
                    Log.d("AuthResult", "Authentication succeeded")

                    // Continue Setting new code
                    return

                }
                RESULT_CANCELED -> {
                    // finishing set code activity
                    finish()
                }
                else -> {
                    // Аутентификация не удалась
                    Log.d("AuthResult", "Authentication failed")
                    // Toast.makeText(this, "Неправильный PIN или пароль", Toast.LENGTH_SHORT).show()

                    // Trying to authenticate again
                    // startAuthentication()
                }
            }
        }
    }
}