package jp.gr.java_conf.cody.ui.profile


import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.*
import com.bumptech.glide.Glide

import kotlinx.android.synthetic.main.fragment_edit_profile.*
import android.support.v7.app.AlertDialog
import android.widget.RelativeLayout
import android.widget.NumberPicker
import android.widget.Toast
import com.theartofdev.edmodo.cropper.CropImage
import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.net.Uri
import android.content.pm.PackageManager
import android.support.v4.content.ContextCompat.checkSelfPermission
import android.util.Log
import com.example.circulardialog.CDialog
import com.example.circulardialog.extras.CDConstants
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.engine.impl.GlideEngine
import jp.gr.java_conf.cody.MyChatManager
import jp.gr.java_conf.cody.NotifyMeInterface
import jp.gr.java_conf.cody.R
import jp.gr.java_conf.cody.constants.DataConstants.Companion.currentUser
import jp.gr.java_conf.cody.constants.NetworkConstants
import jp.gr.java_conf.cody.model.UserModel
import jp.gr.java_conf.cody.util.MyViewUtils.Companion.loadImageFromUrl
import jp.gr.java_conf.cody.util.NetUtils

class EditProfileFragment : Fragment() {
    private var cropImageUri: Uri? = null
    private var resultUri: Uri? = null
    private var storage = FirebaseStorage.getInstance()
    private var storageRef: StorageReference? = null
    private val REQUEST_CODE_CHOOSE = 23
    private val REQUEST_STORAGE_PERMISSION = 1

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        storageRef = storage.reference.child(NetworkConstants().FOLDER_STORAGE_IMG)

        return inflater!!.inflate(R.layout.fragment_edit_profile, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViews()
    }

    private fun setViews() {
        //bottomNavigationView　非表示
        val bottomNavigationView: BottomNavigationView = activity.findViewById(R.id.navigation)
        bottomNavigationView.visibility = View.GONE

        //actionbar
        val toolbar: Toolbar? = view?.findViewById(R.id.toolbar)
        val activity: AppCompatActivity = activity as AppCompatActivity
        activity.setSupportActionBar(toolbar)
        activity.supportActionBar?.setDisplayShowTitleEnabled(true)
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        activity.supportActionBar?.title = "プロフィールを編集"
        setHasOptionsMenu(true)

        // back buttonイベント
        view?.isFocusableInTouchMode = true
        view?.setOnKeyListener { _, keyCode, keyEvent ->
            if (keyCode == KeyEvent.KEYCODE_BACK && keyEvent.action == KeyEvent.ACTION_UP) {
                fragmentManager.popBackStack()
                fragmentManager.beginTransaction().remove(this).commit()
            }
            return@setOnKeyListener true
        }

        // 名前
        name_text_view.text = currentUser?.name

        // 自己紹介
        if (currentUser?.selfIntroduction != null) {
            self_introduction_text_view.visibility = View.VISIBLE
            self_introduction_text_view.text = currentUser?.selfIntroduction
        }

        // 年齢
        if (currentUser?.age != null) {
            val age = currentUser?.age.toString() + "歳"
            age_edit_button.text = age
        }

        // 開発経験
        if (currentUser?.developmentExperience != null) {
            when (currentUser?.developmentExperience) {
                0 -> {
                    val experience = getString(R.string.setting)
                    experience_edit_button.text = experience
                }
                1 -> {
                    val experience = getString(R.string.experience1)
                    experience_edit_button.text = experience
                }
                2 -> {
                    val experience = getString(R.string.experience2)
                    experience_edit_button.text = experience
                }
                3 -> {
                    val experience = getString(R.string.experience3)
                    experience_edit_button.text = experience
                }
                4 -> {
                    val experience = getString(R.string.experience4)
                    experience_edit_button.text = experience
                }
            }
        } else {
            val experience = getString(R.string.setting)
            experience_edit_button.text = experience
        }

        // プログラミング言語
        if (currentUser?.programmingLanguage != null) {
            language_text_view.visibility = View.VISIBLE
            language_text_view.text = currentUser?.programmingLanguage
        }

        // 作ったアプリ
        if (currentUser?.myApps != null) {
            my_apps_text_view.visibility = View.VISIBLE
            my_apps_text_view.text = currentUser?.myApps
        }

        // profile画像
        loadImageFromUrl(profile_image_view, currentUser?.imageUrl)

        setButtonClickListener()
    }

    private fun setButtonClickListener() {
        // 名前
        name_edit_button.setOnClickListener {
            val editUserNameFragment = EditUserNameFragment.newInstance()
            val fragmentManager: FragmentManager = activity.supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.setCustomAnimations(R.anim.fragment_slide_in_right, R.anim.fragment_slide_out_left)
            fragmentTransaction.replace(R.id.fragment, editUserNameFragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }

        // 自己紹介
        self_introduction_edit_button.setOnClickListener {
            val editSelfIntroductionFragment = EditSelfIntroductionFragment.newInstance()
            val fragmentManager: FragmentManager = activity.supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.setCustomAnimations(R.anim.fragment_slide_in_right, R.anim.fragment_slide_out_left)
            fragmentTransaction.replace(R.id.fragment, editSelfIntroductionFragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }

        // 年齢
        age_edit_button.setOnClickListener {
            val linearLayout = RelativeLayout(context)
            val numberPicker = NumberPicker(context)
            numberPicker.maxValue = 100
            numberPicker.minValue = 0

            val params = RelativeLayout.LayoutParams(50, 50)
            val numPicerParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
            numPicerParams.addRule(RelativeLayout.CENTER_HORIZONTAL)

            linearLayout.layoutParams = params
            linearLayout.addView(numberPicker, numPicerParams)

            val alertDialogBuilder = AlertDialog.Builder(context)
            alertDialogBuilder.setTitle(getString(R.string.age_title))
            alertDialogBuilder.setView(linearLayout)
            alertDialogBuilder
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.setting),
                            { _, _ ->
                                val userModel = UserModel(currentUser?.uid)
                                userModel.age = numberPicker.value

                                MyChatManager.updateUserAge(object : NotifyMeInterface {
                                    override fun handleData(obj: Any, requestCode: Int?) {

                                        if (currentUser?.age == numberPicker.value) {
                                            val age = numberPicker.value.toString() + "歳"
                                            age_edit_button.text = age

                                        } else {
                                            var count = 0
                                            val handler = Handler()

                                            handler.postDelayed(object : Runnable {
                                                override fun run() {
                                                    count++
                                                    if (count > 30) {
                                                        Toast.makeText(context, getString(R.string.update_failed), Toast.LENGTH_SHORT).show()
                                                        return
                                                    }
                                                    if (currentUser?.age == numberPicker.value) {
                                                        val age = numberPicker.value.toString() + "歳"
                                                        age_edit_button.text = age
                                                    } else {
                                                        handler.postDelayed(this, 100)
                                                    }
                                                }
                                            }, 100)
                                        }
                                    }
                                }, userModel, NetworkConstants().UPDATE_INFO)
                            })
                    .setNegativeButton(getString(R.string.cancel),
                            { dialog, _ -> dialog.cancel() })
            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()
        }

        // 開発経験
        experience_edit_button.setOnClickListener {
            val list: Array<String> = arrayOf(getString(R.string.experience_default), getString(R.string.experience1),
                    getString(R.string.experience2), getString(R.string.experience3), getString(R.string.experience4))

            var position = 0

            AlertDialog.Builder(context)
                    .setTitle(getString(R.string.experience_title))
                    .setSingleChoiceItems(list, 0, { _, pos ->
                        position = pos
                    })
                    .setPositiveButton(getString(R.string.setting), { _, _ ->
                        val userModel = UserModel(currentUser?.uid)
                        userModel.developmentExperience = position

                        MyChatManager.updateUserDevelopmentExperience(object : NotifyMeInterface {
                            override fun handleData(obj: Any, requestCode: Int?) {

                                if (currentUser?.developmentExperience == position) {
                                    when (currentUser?.developmentExperience) {
                                        0 -> {
                                            val experience = getString(R.string.setting)
                                            experience_edit_button.text = experience
                                        }
                                        1 -> {
                                            val experience = getString(R.string.experience1)
                                            experience_edit_button.text = experience
                                        }
                                        2 -> {
                                            val experience = getString(R.string.experience2)
                                            experience_edit_button.text = experience
                                        }
                                        3 -> {
                                            val experience = getString(R.string.experience3)
                                            experience_edit_button.text = experience
                                        }
                                        4 -> {
                                            val experience = getString(R.string.experience4)
                                            experience_edit_button.text = experience
                                        }
                                    }

                                } else {
                                    var count = 0
                                    val handler = Handler()

                                    handler.postDelayed(object : Runnable {
                                        override fun run() {
                                            count++
                                            if (count > 30) {
                                                Toast.makeText(context, getString(R.string.update_failed), Toast.LENGTH_SHORT).show()
                                                return
                                            }
                                            if (currentUser?.developmentExperience == position - 1) {
                                                when (currentUser?.developmentExperience) {
                                                    0 -> {
                                                        val experience = getString(R.string.setting)
                                                        experience_edit_button.text = experience
                                                    }
                                                    1 -> {
                                                        val experience = getString(R.string.experience1)
                                                        experience_edit_button.text = experience
                                                    }
                                                    2 -> {
                                                        val experience = getString(R.string.experience2)
                                                        experience_edit_button.text = experience
                                                    }
                                                    3 -> {
                                                        val experience = getString(R.string.experience3)
                                                        experience_edit_button.text = experience
                                                    }
                                                    4 -> {
                                                        val experience = getString(R.string.experience4)
                                                        experience_edit_button.text = experience
                                                    }
                                                }
                                            } else {
                                                handler.postDelayed(this, 100)
                                            }
                                        }
                                    }, 100)
                                }
                            }
                        }, userModel, NetworkConstants().UPDATE_INFO)
                    })
                    .setNegativeButton(getString(R.string.cancel), null)
                    .show()

        }

        // プログラミング言語
        language_edit_button.setOnClickListener {
            val editProgrammingLanguageFragment = EditProgrammingLanguageFragment.newInstance()
            val fragmentManager: FragmentManager = activity.supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.setCustomAnimations(R.anim.fragment_slide_in_right, R.anim.fragment_slide_out_left)
            fragmentTransaction.replace(R.id.fragment, editProgrammingLanguageFragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }

        // 作ったアプリ
        my_apps_edit_button.setOnClickListener {
            val editMyAppsFragment = EditMyAppsFragment.newInstance()
            val fragmentManager: FragmentManager = activity.supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.setCustomAnimations(R.anim.fragment_slide_in_right, R.anim.fragment_slide_out_left)
            fragmentTransaction.replace(R.id.fragment, editMyAppsFragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater!!.inflate(R.menu.edit_toolbar_item, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                fragmentManager.beginTransaction().remove(this).commit()
                fragmentManager.popBackStack()
                return true
            }

            R.id.select_photo -> {
                if (NetUtils(context).isOnline()) {
                    requestPermission()
                } else {
                    CDialog(context)
                            .createAlert(getString(R.string.connection_alert), CDConstants.WARNING, CDConstants.MEDIUM)
                            .setAnimation(CDConstants.SCALE_FROM_BOTTOM_TO_TOP)
                            .setDuration(2000)
                            .setTextSize(CDConstants.NORMAL_TEXT_SIZE)
                            .show()
                }
                return true
            }

            else -> {
                return false
            }
        }
    }

    private fun requestPermission() {
        // 権限があるかどうか
        if (checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Matisse.from(this)
                    .choose(MimeType.allOf())
                    .countable(false)
                    .theme(R.style.Matisse_Dracula)
                    .maxSelectable(1)
                    .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                    .thumbnailScale(0.85f)
                    .imageEngine(GlideEngine())
                    .forResult(REQUEST_CODE_CHOOSE)
            return
        }
        // 許可されていない場合
        if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(context, "パーミッションがOFFになっています。", Toast.LENGTH_SHORT).show()
        } else {
            // カメラパーミッションを要求（一度に複数のパーミッションを要求することも可能）
            requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_STORAGE_PERMISSION)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == REQUEST_STORAGE_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Matisse.from(this)
                        .choose(MimeType.allOf())
                        .countable(false)
                        .theme(R.style.Matisse_Dracula)
                        .maxSelectable(1)
                        .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                        .thumbnailScale(0.85f)
                        .imageEngine(GlideEngine())
                        .forResult(REQUEST_CODE_CHOOSE)
            } else {
                Toast.makeText(context, "Cancelling, required permissions are not granted", Toast.LENGTH_LONG).show()
            }
            return
        }
        if (requestCode == CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE) {
            if (cropImageUri != null && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // required permissions granted, start crop image activity
                startCropImageActivity(cropImageUri)
            } else {
                Toast.makeText(context, "Cancelling, required permissions are not granted", Toast.LENGTH_LONG).show()
            }
        }
    }

    @SuppressLint("NewApi")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == AppCompatActivity.RESULT_OK) {
            val selected: List<Uri> = Matisse.obtainResult(data)
            val imageUri = selected.first()
            // For API >= 23 we need to check specifically that we have permissions to read external storage.
            if (CropImage.isReadExternalStoragePermissionsRequired(context, imageUri)) {
                // request permissions and handle the result in onRequestPermissionsResult()
                cropImageUri = imageUri
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE)
            } else {
                // no permissions required or already granted, can start crop image activity
                startCropImageActivity(imageUri)
            }
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode != AppCompatActivity.RESULT_CANCELED) {
            val result: CropImage.ActivityResult = CropImage.getActivityResult(data)
            if (resultCode == AppCompatActivity.RESULT_OK) {
                resultUri = result.uri
                sendFileFirebase(storageRef, resultUri!!)
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error: Exception = result.error
                Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)

    }

    private fun startCropImageActivity(uri: Uri?) {
        CropImage.activity(uri).start(context, this)
    }

    private fun setProfileImage(uri: Uri?) {
        Glide.with(context)
                .load(uri)
                .into(profile_image_view)
    }

    private fun sendFileFirebase(storageReference: StorageReference?, file: Uri) {
        if (storageReference != null) {

            // progress
            progress_view.visibility = View.VISIBLE
            avi.visibility = View.VISIBLE
            avi.show()

            val imageGalleryRef = storageReference.child(currentUser?.uid!!)
            val uploadTask = imageGalleryRef.putFile(file)
            uploadTask.addOnFailureListener { e -> Log.e("", "onFailure sendFileFirebase " + e.message) }.addOnSuccessListener { taskSnapshot ->
                Log.i("", "onSuccess sendFileFirebase")
                val downloadUrl = taskSnapshot.downloadUrl

                MyChatManager.updateProfileImage(object : NotifyMeInterface {
                    override fun handleData(obj: Any, requestCode: Int?) {
                        val isValid = obj as Boolean
                        if (isValid) {
                            progress_view.visibility = View.GONE
                            avi.hide()
                            setProfileImage(file)
                        }
                    }
                }, downloadUrl.toString(), NetworkConstants().UPDATE_INFO)
            }
        }
    }

    companion object {

        fun newInstance(): EditProfileFragment {
            val fragment = EditProfileFragment()
            val args = Bundle()

            fragment.arguments = args
            return fragment
        }
    }

}// Required empty public constructor
