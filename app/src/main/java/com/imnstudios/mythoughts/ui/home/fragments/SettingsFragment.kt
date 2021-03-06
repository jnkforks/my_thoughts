package com.imnstudios.mythoughts.ui.home.fragments

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.Button
import android.widget.CompoundButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.switchmaterial.SwitchMaterial
import com.imnstudios.mythoughts.R
import com.imnstudios.mythoughts.ui.splashScreen.SplashScreenActivity
import com.imnstudios.mythoughts.utils.AppThemeMode
import com.imnstudios.mythoughts.utils.hide
import com.imnstudios.mythoughts.utils.snackbar
import fr.castorflex.android.circularprogressbar.CircularProgressBar


class SettingsFragment : Fragment() {

    private val TAG = "Debug014589"
//    private val TAG = "SettingsFragmentDebug"

    private lateinit var user: TextView
    private lateinit var logOut: Button
    private lateinit var about: Button
    private lateinit var privacyPolicy: Button
    private lateinit var contactBtn: Button
    private lateinit var modeSwitch: SwitchMaterial
    private lateinit var backupBtn: Button

    private var isNightModeOn: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, " onCreate SettingsFragment")

        val appSettingPrefs: SharedPreferences =
            activity!!.getSharedPreferences("AppThemeModePrefs", 0)
        isNightModeOn = appSettingPrefs.getBoolean("NightMode", false)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, " onCreateView SettingsFragment")
        // Inflate the layout for this fragment
        val v: View = inflater.inflate(R.layout.fragment_settings, container, false)

        //init views
        user = v.findViewById(R.id.user)
        logOut = v.findViewById(R.id.log_out_btn)
        about = v.findViewById(R.id.about_btn)
        privacyPolicy = v.findViewById(R.id.privacy_policy_btn)
        contactBtn = v.findViewById(R.id.contact_btn)
        modeSwitch = v.findViewById(R.id.mode_switch)
        backupBtn = v.findViewById(R.id.backup_btn)

        user.append(" ${SplashScreenActivity.auth.currentUser?.displayName}")

        logOut.setOnClickListener {
            val dialog = Dialog(activity!!)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setContentView(R.layout.logout_dialog)
            dialog.setCancelable(true)
            dialog.show()
            val logOut = dialog.findViewById<Button>(R.id.log_out_confirm_btn)
            val cancel = dialog.findViewById<Button>(R.id.cancel_btn)
            logOut.setOnClickListener {
                SplashScreenActivity.auth.signOut()
                dialog.dismiss()
                Intent(activity, SplashScreenActivity::class.java).also {
                    it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(it)
                    activity!!.overridePendingTransition(
                        R.anim.activity_fade_in_animation,
                        R.anim.activity_fade_out_animation
                    )
                }
            }
            cancel.setOnClickListener {
                dialog.dismiss()
            }
        }

        about.setOnClickListener {
            val dialog = Dialog(activity!!)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setContentView(R.layout.about_dialog)
            dialog.setCancelable(true)
            dialog.show()
        }

        privacyPolicy.setOnClickListener {
            val dialog = Dialog(activity!!)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setContentView(R.layout.privacy_policy_dialog)
            dialog.setCancelable(true)
            dialog.show()
            val webView = dialog.findViewById<WebView>(R.id.web_view)
            val progressBar = dialog.findViewById<CircularProgressBar>(R.id.progress_bar)
            webView.loadUrl("https://imnithish.github.io/hellorc-1/")
            val handler = Handler()
            handler.postDelayed({
                progressBar.hide()
                webView.visibility = View.VISIBLE
            }, 3000)
        }

        //setting up the mode switch
        modeSwitch.isChecked = isNightModeOn
        modeSwitch.setOnCheckedChangeListener { _: CompoundButton, isNightModeOnFlag: Boolean ->
            if (isNightModeOnFlag) {
                val appTheme = AppThemeMode(true, activity!!)
                appTheme.setTheme()
            } else {
                val appTheme = AppThemeMode(false, activity!!)
                appTheme.setTheme()
            }
        }

        backupBtn.setOnClickListener {
            if (!isInternetAvailable()) {
                backupBtn.snackbar("Check your network")
            } else {
                backupBtn.snackbar("Syncing is happening fine")
            }
        }

        contactBtn.setOnClickListener {

            openLink()
        }

        return v
    }

    private fun openLink() {
        val link = "https://github.com/imnithish/my_thoughts"
        val uris = Uri.parse(link)
        val intents = Intent(Intent.ACTION_VIEW, uris)
        val b = Bundle()
        b.putBoolean("new_window", true)
        intents.putExtras(b)
        startActivity(intents)
    }

    private fun isInternetAvailable(): Boolean {
        val connectivityManager =
            activity!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val nw = connectivityManager.activeNetwork ?: return false
        val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
        return when {
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            else -> false
        }
    }

}