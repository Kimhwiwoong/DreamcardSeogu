package com.e.dreamcardseogu

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.ktx.Firebase
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.snapshot.BooleanNode
import java.util.regex.Pattern


class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        var btnLogin : Button = findViewById(R.id.btn_login)
        var btnSignup : Button = findViewById(R.id.btn_signup)
        var emailEditText=findViewById<EditText>(R.id.et_email)
        var passwordEditText=findViewById<EditText>(R.id.et_password)
        var intent = intent
        emailEditText.setText(intent.getStringExtra("failedEmail"))
        passwordEditText.setText(intent.getStringExtra("failedPassword"))

        auth= FirebaseAuth.getInstance()
        if ( auth.currentUser != null ) {
            updateUI(auth.currentUser)
        }

        btnLogin.setOnClickListener {
            signIn(emailEditText.text.toString(),passwordEditText.text.toString(),emailEditText, passwordEditText)
        }

        btnSignup.setOnClickListener {
            createAccount(emailEditText.text.toString(),passwordEditText.text.toString(), emailEditText, passwordEditText)
        }
    }

    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if(currentUser != null){
            //reload();
        }
    }

    private fun createAccount(email: String, password: String, etEmail: EditText, etPwd: EditText) {
        if ( email.isNotEmpty() && password.isNotEmpty()) {
            if (isValidEmail(email) && isValidPwd(password)) {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this,"회원가입 완료!",Toast.LENGTH_LONG).show()
                            val user = auth.currentUser
                            updateUI(user)
                        } else {
                            Log.w("사용자생성", "createUserWithEmail:failure", task.exception)
                        }
                    }
            } else {
                Toast.makeText(this, "${getString(R.string.format_info)}",Toast.LENGTH_LONG).show()
                reload(etEmail.text.toString(), etPwd.text.toString(), etEmail, etPwd)
            }

        } else {
            Toast.makeText(this, "${getString(R.string.empty_info)}",Toast.LENGTH_LONG).show()
            reload(etEmail.text.toString(), etPwd.text.toString(), etEmail, etPwd)
        }

    }

    private fun signIn(email: String, password: String, etEmail: EditText, etPwd: EditText) {
        if ( email.isNotEmpty() && password.isNotEmpty()) {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(baseContext, "${getString(R.string.signin_success)}", Toast.LENGTH_SHORT).show()
                        val user = auth.currentUser

                        updateUI(user)
                    } else {
                        Toast.makeText(baseContext, "${getString(R.string.signin_failed)}", Toast.LENGTH_LONG).show()
                        reload(etEmail.text.toString(), etPwd.text.toString(), etEmail, etPwd)
                    }
                }
        } else {
            Toast.makeText(this, "${getString(R.string.empty_info)}",Toast.LENGTH_LONG).show()
            reload(etEmail.text.toString(), etPwd.text.toString(), etEmail, etPwd)
        }
    }

    /* 비밀번호, 이메일아이디 유효성 검사 */
    private fun isValidEmail(email: String) : Boolean{
        var pattern: Pattern = android.util.Patterns.EMAIL_ADDRESS
        return pattern.matcher(email).matches()
    }

    private fun isValidPwd(password: String) : Boolean{
        var pattern: Pattern = Pattern.compile("^[a-zA-Z0-9!@.#$%^&*?_~]{8,16}$")
        return pattern.matcher(password).matches()
    }

    /* 메인화면으로 이동 및 현재화면 reload(실패시) */
    /* reload시 작성한 id, pw intent로 같이 넘겨줘서 다시 입력시켜주는거 추가 */

    private fun updateUI(user: FirebaseUser?) {
        if( user!= null){
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }
    }

    private fun reload(email: String, password: String, etEmail: EditText, etPwd: EditText) {
        var newIntent = Intent(this,this::class.java)
        newIntent.putExtra("failedEmail",etEmail.text.toString())
        newIntent.putExtra("failedPassword",etPwd.text.toString())
        startActivity(newIntent)
        finish()
    }
}