package br.eti.rafaelcouto.rcatestjetpack.login

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import br.eti.rafaelcouto.rcatestjetpack.R
import br.eti.rafaelcouto.rcatestjetpack.result.ResultActivity
import br.eti.rafaelcouto.rcatestjetpack.extension.bind
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    private val buttonState = Observer<String> {
        val enabled = viewModel.canEnable

        button.apply {
            isEnabled = viewModel.canEnable
            alpha = if (enabled) 1F else 0.5F
        }
    }

    private val loader by lazy {
        ProgressDialog(this)
    }

    private val viewModel by lazy {
        ViewModelProviders.of(this).get(LoginViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }

    override fun onResume() {
        super.onResume()

        bindAndObserve()
    }

    private fun bind() {
        editText.bind { login ->
            viewModel.login.value = login
        }

        editText2.bind { password ->
            viewModel.password.value = password
        }
    }

    private fun observe() {
        viewModel.apply {
            login.observe(this@LoginActivity, buttonState)
            password.observe(this@LoginActivity, buttonState)

            error.observe(this@LoginActivity, Observer { error ->
                loader.hide()

                Toast.makeText(this@LoginActivity, error, Toast.LENGTH_SHORT).show()
            })

            button.setOnClickListener {
                loader.show()

                login()?.observe(this@LoginActivity, Observer { data ->
                    loader.hide()

                    Intent(this@LoginActivity, ResultActivity::class.java).apply {
                        putExtra("userData", data)
                        startActivity(this)
                    }
                })
            }
        }
    }

    private fun bindAndObserve() {
        bind()
        observe()
    }
}