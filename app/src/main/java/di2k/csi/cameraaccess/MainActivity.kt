package di2k.csi.cameraaccess

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import androidx.core.app.ActivityCompat
import android.content.pm.PackageManager
import android.provider.Settings
import android.net.Uri
import android.widget.Button
import androidx.core.content.ContextCompat


class MainActivity : AppCompatActivity() {
        lateinit var btn: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btn = findViewById(R.id.button)

        btn.setOnClickListener{
            camera_action()
        }
    }

    private fun camera_action(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (getFromPref(this, ALLOW_KEY)) {
                showSettingsAlert()
            } else if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                )

                != PackageManager.PERMISSION_GRANTED
            ) {

                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        Manifest.permission.CAMERA
                    )
                ) {
                    showAlert()
                } else {
                    // No explanation needed, we can request the permission.
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.CAMERA),
                        MY_PERMISSIONS_REQUEST_CAMERA
                    )
                }
            }
        } else {
            openCamera()
        }
    }

    private fun showAlert() {
        val alertDialog = AlertDialog.Builder(this@MainActivity).create()
        alertDialog.setTitle("Alert")
        alertDialog.setMessage("App needs to access the Camera.")

        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "DONT ALLOW"
        ) { dialog, which ->
            dialog.dismiss()
            finish()
        }

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "ALLOW"
        ) { dialog, which ->
            dialog.dismiss()
            ActivityCompat.requestPermissions(
                this@MainActivity,
                arrayOf(Manifest.permission.CAMERA),
                MY_PERMISSIONS_REQUEST_CAMERA
            )
        }
        alertDialog.show()
    }

    private fun showSettingsAlert() {
        val alertDialog = AlertDialog.Builder(this@MainActivity).create()
        alertDialog.setTitle("Alert")
        alertDialog.setMessage("App needs to access the Camera.")

        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "DONT ALLOW"
        ) { dialog, which ->
            dialog.dismiss()
            //finish();
        }

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "SETTINGS"
        ) { dialog, which ->
            dialog.dismiss()
            startInstalledAppDetailsActivity(this@MainActivity)
        }

        alertDialog.show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_CAMERA -> {
                var i = 0
                val len = permissions.size
                while (i < len) {
                    val permission = permissions[i]

                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        val showRationale = ActivityCompat.shouldShowRequestPermissionRationale(
                            this, permission
                        )

                        if (showRationale) {
                            showAlert()
                        } else if (!showRationale) {
                            // user denied flagging NEVER ASK AGAIN
                            // you can either enable some fall back,
                            // disable features of your app
                            // or open another dialog explaining
                            // again the permission and directing to
                            // the app setting
                            saveToPreferences(this@MainActivity, ALLOW_KEY, true)
                        }
                    }
                    i++
                }
            }
        }// other 'case' lines to check for other
        // permissions this app might request
    }

    private fun openCamera() {
        val intent = Intent("android.media.action.IMAGE_CAPTURE")
        startActivity(intent)
    }

    companion object {
        val MY_PERMISSIONS_REQUEST_CAMERA = 100
        val ALLOW_KEY = "ALLOWED"
        val CAMERA_PREF = "camera_pref"
        fun saveToPreferences(context: Context, key: String, allowed: Boolean?) {
            val myPrefs = context.getSharedPreferences(
                CAMERA_PREF,
                Context.MODE_PRIVATE
            )
            val prefsEditor = myPrefs.edit()
            prefsEditor.putBoolean(key, allowed!!)
            prefsEditor.commit()
        }

        fun getFromPref(context: Context, key: String): Boolean {
            val myPrefs = context.getSharedPreferences(
                CAMERA_PREF,
                Context.MODE_PRIVATE
            )
            return myPrefs.getBoolean(key, false)
        }

        fun startInstalledAppDetailsActivity(context: Activity?) {
            if (context == null) {
                return
            }

            val i = Intent()
            i.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            i.addCategory(Intent.CATEGORY_DEFAULT)
            i.data = Uri.parse("package:" + context.packageName)
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
            context.startActivity(i)
        }
    }
}
