package com.soumya.customscrollnumberpicker

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.soumya.customscrollnumberpicker.Utils.FEET_TEXT
import com.soumya.customscrollnumberpicker.Utils.INCHES
import com.soumya.customscrollnumberpicker.Utils.MAX_FEET_DEFAULT
import com.soumya.customscrollnumberpicker.Utils.MAX_INCH_VALUE
import com.soumya.customscrollnumberpicker.Utils.OFF_SET_DEFAULT
import com.soumya.customscrollnumberpicker.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    var inch: String? = "1"
    var feet: String? = "2"

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

    private fun setNumberPickerList() :Pair<List<String>, List<String>> {
        val feetList = arrayListOf<String>()
        for(i in OFF_SET_DEFAULT..MAX_FEET_DEFAULT){
            feetList.add("$i")
        }

        val inchList = arrayListOf<String>()
        for(i in 0..MAX_INCH_VALUE){
            inchList.add("$i")
        }

        return Pair(feetList, inchList)
    }

    private fun setCustomNumberPicker() {
        binding.apply {
            scrollFeetView.apply {
                offset = OFF_SET_DEFAULT
                setSelection(OFF_SET_DEFAULT)
                setItems(setNumberPickerList().first)
                setOnScrollViewListener(object : CustomNumberPickerScrollView.OnScrollViewListener(){
                    override fun onSelected(selectedIndex: Int, item: String) {
                        super.onSelected(selectedIndex, item)
                        feet = item
                        announceForAccessibility(""+feet.plus(" ").plus(getString(R.string.str_feet)).plus(getString(R.string.text_selected)))
                    }
                })
            }

            scrollInchView.apply {
                offset = OFF_SET_DEFAULT
                setSelection(OFF_SET_DEFAULT)
                setItems(setNumberPickerList().second)
                setOnScrollViewListener(object : CustomNumberPickerScrollView.OnScrollViewListener(){
                    override fun onSelected(selectedIndex: Int, item: String) {
                        super.onSelected(selectedIndex, item)
                        inch = item
                        announceForAccessibility(""+inch.plus(" ").plus(getString(R.string.str_inches)).plus(getString(R.string.text_selected)))
                    }
                })
            }

            submitButton.setOnClickListener {
                textField.text = feet.plus(FEET_TEXT).plus(inch.plus(INCHES))
            }
        }
    }

}
