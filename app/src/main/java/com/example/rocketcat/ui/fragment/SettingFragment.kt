package com.example.rocketcat.ui.fragment

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.example.rocketcat.R


class SettingFragment : PreferenceFragmentCompat() {


    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.setting_preference)
    }

}