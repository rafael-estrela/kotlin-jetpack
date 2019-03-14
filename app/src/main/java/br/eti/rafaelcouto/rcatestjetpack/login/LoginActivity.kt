package br.eti.rafaelcouto.rcatestjetpack.login

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import br.eti.rafaelcouto.rcatestjetpack.R
import br.eti.rafaelcouto.rcatestjetpack.result.ResultActivity
import br.eti.rafaelcouto.rcatestjetpack.extension.bindTo
import br.eti.rafaelcouto.rcatestjetpack.extension.withSource
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    private val loader by lazy {
        ProgressDialog(this)
    }

    private val viewModel by lazy {
        ViewModelProviders.of(this).get(LoginViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        bindAndObserve()
    }

    private fun bind() {
        editText.bindTo(viewModel.login)
        editText2.bindTo(viewModel.password)
    }

    private fun observe() {
        viewModel.apply {
            val mediator = MediatorLiveData<Boolean>()

            mediator.withSource(login)
                .and(password)
                .mediate(this@LoginActivity) {
                    mediator.value = shouldEnable()
                }.andObserve { enabled ->
                    button.apply {
                        isEnabled = enabled
                        alpha = if (enabled) 1F else 0.5F
                    }
                }

            /*MediatorLiveData<Boolean>()
                .withSource(login) {
                    this.value = shouldEnable()
                }.withSource(password) {
                    this.value = shouldEnable()
                }.observe(
                    this@LoginActivity,
                    Observer { enabled ->
                        button.apply {
                            isEnabled = enabled
                            alpha = if (enabled) 1F else 0.5F
                        }
                    }
                )*/

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
        this.bind()
        this.observe()
    }
}