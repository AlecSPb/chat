package jp.gr.java_conf.cody.ui.contacts


import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.example.circulardialog.CDialog
import com.example.circulardialog.extras.CDConstants
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.theartofdev.edmodo.cropper.CropImage
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.engine.impl.GlideEngine
import jp.gr.java_conf.cody.MyChatManager
import jp.gr.java_conf.cody.NotifyMeInterface
import jp.gr.java_conf.cody.R
import jp.gr.java_conf.cody.adapter.ParticipantsAdapter
import jp.gr.java_conf.cody.constants.AppConstants
import jp.gr.java_conf.cody.constants.DataConstants
import jp.gr.java_conf.cody.constants.DataConstants.Companion.myFriends
import jp.gr.java_conf.cody.constants.DataConstants.Companion.selectedUserList
import jp.gr.java_conf.cody.constants.NetworkConstants
import jp.gr.java_conf.cody.model.CommunityModel
import jp.gr.java_conf.cody.model.UserModel
import jp.gr.java_conf.cody.util.MyViewUtils.Companion.loadRoundImage
import jp.gr.java_conf.cody.util.NetUtils
import jp.gr.java_conf.cody.util.SharedPrefManager
import kotlinx.android.synthetic.main.fragment_new_community.*

import java.util.*


class NewCommunityFragment : Fragment(), View.OnClickListener {

    private var participants: RecyclerView? = null
    var adapter: ParticipantsAdapter? = null
    private var resultUri: Uri? = null
    var storage = FirebaseStorage.getInstance()
    var communityId: String? = null
    var storageRef: StorageReference? = null
    var cropImageUri: Uri? = null
    var feature_position: Int? = 0
    private val REQUEST_CODE_CHOOSE = 23
    private val REQUEST_STORAGE_PERMISSION = 1


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        storageRef = storage.reference.child(NetworkConstants().FOLDER_STORAGE_IMG)

        return inflater!!.inflate(R.layout.fragment_new_community, container, false)
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
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        activity.supportActionBar?.setDisplayShowTitleEnabled(true)
        activity.supportActionBar?.title = getString(R.string.create_community)
        setHasOptionsMenu(true)

        participants?.layoutManager = LinearLayoutManager(context)

        create_community_button.text = getString(R.string.create_community)

        participants_recycler_view.visibility = View.GONE

        profile_image_view.setOnClickListener(this)
        feature_button.setOnClickListener(this)
        create_community_button.setOnClickListener(this)
        invite_friend_button.setOnClickListener(this)

        // focus
        new_community_layout.setOnTouchListener{ _, _ ->
            val inputMethodManager = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(new_community_layout.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
            new_community_layout.requestFocus()
            return@setOnTouchListener true
        }

        // back buttonイベント
        view?.isFocusableInTouchMode = true
        view?.setOnKeyListener { _, keyCode, keyEvent ->
            if (keyCode == KeyEvent.KEYCODE_BACK && keyEvent.action == KeyEvent.ACTION_UP) {
                fragmentManager.popBackStack()
                fragmentManager.beginTransaction().remove(this).commit()
            }
            return@setOnKeyListener true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                fragmentManager.beginTransaction().remove(this).commit()
                fragmentManager.popBackStack()
                true
            }
            else -> {
                false
            }
        }
    }

    private fun createCommunity() {
        var isValid = true
        var errorMessage = "Validation Error"

        val communityName: String = community_name_edit_text.text.toString()

        if (communityName.isBlank()) {
            isValid = false
            errorMessage = "Community name is blank"
        }
        if (communityName.length < 3) {
            isValid = false
            errorMessage = "Community name should be more than 2 characters"
        }

        val location: String = location_edit_text.text.toString()
        if (location.isBlank()) {
            isValid = false
            errorMessage = "location is blank"
        }

        val description: String = description_edit_text.text.toString()
        if (description.isBlank()) {
            isValid = false
            errorMessage = "description is blank"
        }

        val communityImage = "https://cdn1.iconfinder.com/data/icons/google_jfk_icons_by_carlosjj/128/groups.png"
        val newCommunity = CommunityModel(communityName, communityImage, communityDeleted = false,
                community = true, description = description, location = location, feature = feature_position)

        val adminUserModel: UserModel? = SharedPrefManager.getInstance(context).savedUserModel
        adminUserModel?.admin = true

        val communityMembers: HashMap<String, UserModel> = hashMapOf()


        if (selectedUserList.size != 0) {
            for (user in selectedUserList) {
                user.myApps = null
                user.age = null
                user.programmingLanguage = null
                communityMembers.put(user.uid!!, user)
            }
        }

        communityMembers.put(adminUserModel?.uid!!, adminUserModel)

        newCommunity.members = communityMembers

        MyChatManager.setmContext(context)

        if (isValid) {
            if (resultUri != null) {
                sendFileFirebase(storageRef, resultUri!!, newCommunity)
            } else {
                MyChatManager.createCommunity(object : NotifyMeInterface {
                    override fun handleData(obj: Any, requestCode: Int?) {
                        Toast.makeText(context, "Community has been created successful", Toast.LENGTH_SHORT).show()
                        activity.supportFragmentManager.beginTransaction().replace(R.id.fragment, ContactsFragment.newInstance()).commit()
                    }
                }, newCommunity, NetworkConstants().CREATE_COMMUNITY)
            }
        } else {
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.profile_image_view -> {
                requestPermission()
            }
            R.id.feature_button -> {
                val featureList: Array<String> = arrayOf(getString(R.string.feature_default), getString(R.string.feature1),
                        getString(R.string.feature2), getString(R.string.feature3), getString(R.string.feature4))

                AlertDialog.Builder(context)
                        .setTitle(getString(R.string.feature))
                        .setSingleChoiceItems(featureList, 0, { _, pos ->
                            feature_position = pos
                        })
                        .setPositiveButton(getString(R.string.setting), { _, _ ->
                            when (feature_position) {
                                0 -> {
                                    feature_button.text = getString(R.string.setting)
                                }
                                1 -> {
                                    feature_button.text = getString(R.string.feature1)
                                }
                                2 -> {
                                    feature_button.text = getString(R.string.feature2)
                                }
                                3 -> {
                                    feature_button.text = getString(R.string.feature3)
                                }
                                4 -> {
                                    feature_button.text = getString(R.string.feature4)
                                }
                            }
                        })
                        .setNegativeButton(getString(R.string.cancel), null)
                        .show()

            }

            R.id.create_community_button -> {
                if (NetUtils(context).isOnline()) {
                    createCommunity()
                } else {
                    CDialog(context)
                            .createAlert(getString(R.string.connection_alert), CDConstants.WARNING, CDConstants.MEDIUM)
                            .setAnimation(CDConstants.SCALE_FROM_BOTTOM_TO_TOP)
                            .setDuration(2000)
                            .setTextSize(CDConstants.NORMAL_TEXT_SIZE)
                            .show()
                }
            }
            R.id.invite_friend_button -> {
                selectedUserList.clear()

                val friendNameList: MutableList<String> = mutableListOf()
                for (myFriend in myFriends) {
                    friendNameList.add(myFriend.name!!)
                }
                val items = friendNameList.toTypedArray()
                AlertDialog.Builder(context)
                        .setTitle(getString(R.string.invite))
                        .setMultiChoiceItems(items, null, { _, pos, isSelected ->
                            if (isSelected) {
                                selectedUserList.add(myFriends[pos])
                            } else {
                                selectedUserList.remove(myFriends[pos])
                            }
                        })
                        .setPositiveButton(getString(R.string.setting), null)
                        .setNegativeButton(getString(R.string.cancel), null)
                        .setOnDismissListener { setAdapter() }
                        .show()
            }
        }
    }

    private fun setAdapter() {
        if (selectedUserList.size != 0) {
            participants_recycler_view.visibility = View.VISIBLE
            val manager = LinearLayoutManager(context)
            manager.orientation = LinearLayoutManager.HORIZONTAL
            participants_recycler_view.layoutManager = manager
            adapter = ParticipantsAdapter(object : NotifyMeInterface {
                override fun handleData(obj: Any, requestCode: Int?) {
                }

            }, AppConstants().CREATION)
            participants_recycler_view.adapter = adapter
        } else {
            participants_recycler_view.visibility = View.GONE
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
                setProfileImage(resultUri)
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
        loadRoundImage(profile_image_view, uri.toString())
    }

    private fun sendFileFirebase(storageReference: StorageReference?, file: Uri, newCommunity: CommunityModel) {
        if (storageReference != null) {

            progress_view.visibility = View.VISIBLE
            avi.show()

            val imageGalleryRef = storageReference.child(DataConstants.currentUser?.uid!!)
            val uploadTask = imageGalleryRef.putFile(file)
            uploadTask.addOnFailureListener { e -> Log.e("", "onFailure sendFileFirebase " + e.message) }.addOnSuccessListener { taskSnapshot ->
                Log.i("", "onSuccess sendFileFirebase")
                val downloadUrl = taskSnapshot.downloadUrl

                newCommunity.imageUrl = downloadUrl.toString()

                MyChatManager.createCommunity(object : NotifyMeInterface {
                    override fun handleData(obj: Any, requestCode: Int?) {
                        progress_view.visibility = View.GONE
                        avi.hide()
                        Toast.makeText(context, "Community has been created successful", Toast.LENGTH_SHORT).show()
                        activity.supportFragmentManager.beginTransaction().replace(R.id.fragment, ContactsFragment.newInstance()).commit()
                    }
                }, newCommunity, NetworkConstants().CREATE_COMMUNITY)
            }
        }
    }

    companion object {

        fun newInstance(): NewCommunityFragment {
            val fragment = NewCommunityFragment()
            val args = Bundle()

            fragment.arguments = args
            return fragment
        }
    }

}// Required empty public constructor
