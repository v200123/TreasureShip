package com.jzz.treasureship.ui.reward

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import com.jzz.treasureship.R

class QuestionsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_questions)
    }
}
