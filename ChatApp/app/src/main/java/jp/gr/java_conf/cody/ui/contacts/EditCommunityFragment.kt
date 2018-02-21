package jp.gr.java_conf.cody.ui.contacts


import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.*
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.theartofdev.edmodo.cropper.CropImage
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.engine.impl.GlideEngine
import jp.gr.java_conf.cody.MyChatManager
import jp.gr.java_conf.cody.NotifyMeInterface
import jp.gr.java_conf.cody.R
import jp.gr.java_conf.cody.constants.DataConstants.Companion.communityMap
import jp.gr.java_conf.cody.constants.NetworkConstants
import jp.gr.java_conf.cody.model.CommunityModel
import jp.gr.java_conf.cody.util.MyViewUtils.Companion.loadImageFromUrl
import kotlinx.android.synthetic.main.fragment_edit_community.*


class EditCommunityFragment : Fragment() {
    private var id: String? = null
    private var communityModel: CommunityModel? = null
    private var resultUri: Uri? = null
    var storage = FirebaseStorage.getInstance()
    var storageRef: StorageReference? = null
    var cropImageUri: Uri? = null

    private val REQUEST_CODE_CHOOSE = 23
    private val REQUEST_STORAGE_PERMISSION = 1


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        storageRef = storage.reference.child(NetworkConstants().FOLDER_STORAGE_IMG)

        id = arguments.getString("id")
        communityModel = communityMap!![id!!]

        return inflater!!.inflate(R.layout.fragment_edit_community, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
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
        activity.supportActionBar?.setDisplayShowTitleEnabled(false)
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
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
        name_text_view.text = communityModel?.name

        // コミュニティの説明
        if (communityModel?.description != null) {
            description_text_view.visibility = View.VISIBLE
            description_text_view.text = communityModel?.description
        }

        // 活動場所
        if (communityModel?.location != null) {
            location_text_view.visibility = View.VISIBLE
            location_text_view.text = communityModel?.location
        }

        // profile画像
        loadImageFromUrl(profile_image_view, communityModel?.imageUrl!!)

        setButtonClickListener()
    }

    private fun setButtonClickListener() {
        name_edit_button.setOnClickListener {
            val editCommunityNameFragment = EditCommunityNameFragment.newInstance(id!!)
            val fragmentManager: FragmentManager = activity.supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.setCustomAnimations(R.anim.fragment_slide_in_right, R.anim.fragment_slide_out_left)
            fragmentTransaction.replace(R.id.fragment, editCommunityNameFragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }

        description_edit_button.setOnClickListener {
            val editCommunityDescriptionFragment = EditCommunityDescriptionFragment.newInstance(id!!)
            val fragmentManager: FragmentManager = activity.supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.setCustomAnimations(R.anim.fragment_slide_in_right, R.anim.fragment_slide_out_left)
            fragmentTransaction.replace(R.id.fragment, editCommunityDescriptionFragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }

        location_edit_button.setOnClickListener {
            val editCommunityLocationFragment = EditCommunityLocationFragment.newInstance(id!!)
            val fragmentManager: FragmentManager = activity.supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.setCustomAnimations(R.anim.fragment_slide_in_right, R.anim.fragment_slide_out_left)
            fragmentTransaction.replace(R.id.fragment, editCommunityLocationFragment)
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
                requestPermission()
                return true
            }

            else -> {
                return false
            }
        }
    }

    private fun requestPermission() {
        // 権限があるかどうか
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
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
                Toast.makeText(context, "パーミッションが許可されないとギャラリーを開けません。", Toast.LENGTH_LONG).show()
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

            progress_view.visibility = View.VISIBLE
            avi.show()

            val imageGalleryRef = storageReference.child(id!!)
            val uploadTask = imageGalleryRef.putFile(file)
            uploadTask.addOnFailureListener { e -> Log.e("", "onFailure sendFileFirebase " + e.message) }.addOnSuccessListener { taskSnapshot ->
                Log.i("", "onSuccess sendFileFirebase")
                val downloadUrl = taskSnapshot.downloadUrl

                val communityModel = CommunityModel(communityId = id, imageUrl = downloadUrl.toString())

                MyChatManager.updateCommunityImage(object : NotifyMeInterface {
                    override fun handleData(obj: Any, requestCode: Int?) {
                        val isValid = obj as Boolean
                        if (isValid) {
                            progress_view.visibility = View.GONE
                            avi.hide()
                            setProfileImage(file)
                        }
                    }
                }, communityModel, NetworkConstants().UPDATE_INFO)
            }
        }
    }

    companion object {

        fun newInstance(id: String): EditCommunityFragment {
            val fragment = EditCommunityFragment()
            val args = Bundle()
            args.putString("id", id)
            fragment.arguments = args
            return fragment
        }
    }

}// Required empty public constructor
