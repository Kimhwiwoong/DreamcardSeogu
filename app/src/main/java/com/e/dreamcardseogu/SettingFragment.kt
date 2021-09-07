package com.e.dreamcardseogu

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.preference.*
import com.google.firebase.auth.FirebaseAuth

class SettingFragment : PreferenceFragmentCompat() {
    private var versionName: String = "ver " + BuildConfig.VERSION_NAME
    private var auth: FirebaseAuth? = FirebaseAuth.getInstance()
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.resource)
        var copyInfo: Preference? = findPreference(getString(R.string.key_copyright))
        var appVersion: Preference? = findPreference(getString(R.string.key_app_version))
        var signOut: Preference? = findPreference(getString(R.string.key_signout))
        var delete: Preference? = findPreference(getString(R.string.key_delete))

        appVersion?.summary = versionName

        /* 저작권정보 리스너 */
        copyInfo?.setOnPreferenceClickListener {
            var intent = Intent(activity,CopyrightActivity::class.java)
            startActivity(intent)
            true
        }
        /* 로그아웃 리스너 */
        signOut?.setOnPreferenceClickListener {
            FirebaseAuth.getInstance().signOut()
            Toast.makeText(context,"로그아웃되었습니다.",Toast.LENGTH_LONG).show()
            activity?.finish()
            var intent = Intent(activity,LoginActivity::class.java)
            startActivity(intent)
            true
        }
        /* 회원탈퇴 리스너 */
        delete?.setOnPreferenceClickListener {
            var builder = AlertDialog.Builder(context)
            builder.apply {
                setTitle("회원탈퇴").setMessage("탈퇴하시겠습니까?")
                setPositiveButton("확인"){
                        dialogInterface,i->
                    auth?.currentUser?.delete()
                    Toast.makeText(context,"탈퇴되었습니다.",Toast.LENGTH_LONG).show()
                    activity?.finish()
                    var intent = Intent(activity,LoginActivity::class.java)
                    startActivity(intent)
                }
                setNegativeButton("취소"){
                    dialogInterface,i->
                    Toast.makeText(context,"취소",Toast.LENGTH_SHORT).show()
                }
            }
            var alert = builder.create()
            alert.show()

            true
        }
    }
}