package com.e.dreamcardseogu

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import com.e.dreamcardseogu.databinding.ActivityCopyrightBinding

class CopyrightActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_copyright)
        var binding =ActivityCopyrightBinding.inflate(layoutInflater)
        var tvCopyright = binding.tvcopyright
        tvCopyright.movementMethod = ScrollingMovementMethod.getInstance()
    }
}