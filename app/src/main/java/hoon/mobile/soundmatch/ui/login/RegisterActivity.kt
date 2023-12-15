package hoon.mobile.soundmatch.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import hoon.mobile.soundmatch.R
import hoon.mobile.soundmatch.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val backButton = findViewById<ImageButton>(R.id.back_button)
        backButton.setOnClickListener {
            onBackPressed()
        }

        auth = FirebaseAuth.getInstance()

        binding.btnJoin.setOnClickListener {
            val name = binding.name.text.toString().trim()
            val email = binding.email.text.toString().trim()
            val password = binding.password.text.toString().trim()
            val confirmPassword = binding.confirmPassword.text.toString().trim()

            if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && password == confirmPassword) {
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "회원가입 성공", Toast.LENGTH_SHORT).show()

                        // LoginActivity로 이동
                        val loginIntent = Intent(this, LoginActivity::class.java)
                        loginIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(loginIntent)
                        finish()
                    } else {
                        Toast.makeText(this, "회원가입 실패: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
