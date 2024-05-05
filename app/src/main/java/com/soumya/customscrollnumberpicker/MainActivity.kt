package com.soumya.customscrollnumberpicker

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.soumya.customscrollnumberpicker.Utils.FEET_TEXT
import com.soumya.customscrollnumberpicker.Utils.INCH
import com.soumya.customscrollnumberpicker.Utils.OFF_SET_DEFAULT
import com.soumya.customscrollnumberpicker.Utils.setNumberPickerList
import com.soumya.customscrollnumberpicker.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var inch: String? = "1"
    private var feet: String? = "2"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setNumberPickerList()
        setCustomNumberPicker()
    }

    private fun setCustomNumberPicker() {
        binding.apply {
            scrollFeetView.apply {
                this.properties.offset = OFF_SET_DEFAULT
                setSelection(OFF_SET_DEFAULT)
                setItems(setNumberPickerList().first)
                setOnScrollViewListener { _, item ->
                    feet = item
                    announceForAccessibility(
                        "" + feet.plus(" ").plus(getString(R.string.str_feet))
                            .plus(getString(R.string.text_selected))
                    )
                }
            }

            scrollInchView.apply {
                this.properties.offset = OFF_SET_DEFAULT
                setSelection(OFF_SET_DEFAULT)
                setItems(setNumberPickerList().second)
                setOnScrollViewListener { _, item ->
                    inch = item
                    announceForAccessibility(
                        "" + inch.plus(" ").plus(getString(R.string.str_inches))
                            .plus(getString(R.string.text_selected))
                    )
                }
            }

            submitButton.setOnClickListener {
                textField.text = feet.plus(FEET_TEXT).plus(inch.plus(INCH))
            }
        }
    }

}
