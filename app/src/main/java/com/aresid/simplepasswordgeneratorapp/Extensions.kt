package com.aresid.simplepasswordgeneratorapp

import android.widget.CheckBox
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.google.android.material.slider.Slider

object Extensions {
    fun CheckBox.bindInputToBoolean(lifecycleOwner: LifecycleOwner, booleanLiveData: MutableLiveData<Boolean>) {
        setOnCheckedChangeListener { _, isChecked ->
            booleanLiveData.value = isChecked
        }

        booleanLiveData.observe(lifecycleOwner) {
            isChecked = it
        }
    }

    fun Slider.bindInputToInteger(lifecycleOwner: LifecycleOwner, integerLiveData: MutableLiveData<Int>) {
        addOnChangeListener { _, value, _ ->
            integerLiveData.value = value.toInt()
        }

        integerLiveData.observe(lifecycleOwner) {
            value = it.toFloat()
        }
    }
}