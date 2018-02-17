package com.go26.chatapp.ui.contacts


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
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import com.bumptech.glide.Glide
import com.go26.chatapp.MyChatManager
import com.go26.chatapp.NotifyMeInterface

import com.go26.chatapp.R
import com.go26.chatapp.adapter.ParticipantsAdapter
import com.go26.chatapp.constants.AppConstants
import com.go26.chatapp.constants.DataConstants
import com.go26.chatapp.constants.DataConstants.Companion.myFriends
import com.go26.chatapp.constants.DataConstants.Companion.selectedUserList
import com.go26.chatapp.constants.NetworkConstants
import com.go26.chatapp.model.CommunityModel
import com.go26.chatapp.model.UserModel
import com.go26.chatapp.util.MyViewUtils.Companion.loadRoundImage
import com.go26.chatapp.util.SharedPrefManager
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.theartofdev.edmodo.cropper.CropImage
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.engine.impl.GlideEngine
import kotlinx.android.synthetic.main.fragment_new_community.*

import java.util.*


class NewCommunityFragment : Fragment(), View.OnClickListener {

    var paticipants: RecyclerView? = null
    var adapter: ParticipantsAdapter? = null
    private var resultUri: Uri? = null
    var storage = FirebaseStorage.getInstance()
    var communityId: String? = null
    var storageRef: StorageReference? = null
    var cropImageUri: Uri? = null
    private val REQUEST_CODE_CHOOSE = 23


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
        activity.supportActionBar?.title = "Create Community"
        setHasOptionsMenu(true)

        paticipants?.layoutManager = LinearLayoutManager(context)

        // Group Creation Page
        create_group_button.text = "Create community"

        participants_recycler_view.visibility = View.GONE

        profile_image_view.setOnClickListener(this)
        create_group_button.setOnClickListener(this)
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
        when (item.itemId) {
            android.R.id.home -> {
                fragmentManager.beginTransaction().remove(this).commit()
                fragmentManager.popBackStack()
                return true
            }
            else -> {
                return false
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
                community = true, description = description, location = location)
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
                Matisse.from(this)
                        .choose(MimeType.allOf())
                        .countable(false)
                        .theme(R.style.Matisse_Dracula)
                        .maxSelectable(1)
                        .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                        .thumbnailScale(0.85f)
                        .imageEngine(GlideEngine())
                        .forResult(REQUEST_CODE_CHOOSE)
            }

            R.id.create_group_button -> {
                createCommunity()
            }

            R.id.invite_friend_button -> {
                selectedUserList.clear()

                val friendNameList: MutableList<String> = mutableListOf()
                for (myFriend in myFriends) {
                    friendNameList.add(myFriend.name!!)
                }

                MaterialDialog.Builder(context)
                        .title("招待")
                        .items(friendNameList)
                        .itemsCallbackMultiChoice(null, MaterialDialog.ListCallbackMultiChoice { _, which, _ ->
                            for (i in which) {
                                selectedUserList.add(myFriends[i])
                            }
                            return@ListCallbackMultiChoice true
                        })
                        .positiveText("招待")
                        .negativeText("キャンセル")
                        .dismissListener { setAdapter() }
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

            val progress = MaterialDialog.Builder(context).content("読み込み中").progress(true, 0).show()

            val imageGalleryRef = storageReference.child(DataConstants.currentUser?.uid!!)
            val uploadTask = imageGalleryRef.putFile(file)
            uploadTask.addOnFailureListener { e -> Log.e("", "onFailure sendFileFirebase " + e.message) }.addOnSuccessListener { taskSnapshot ->
                Log.i("", "onSuccess sendFileFirebase")
                val downloadUrl = taskSnapshot.downloadUrl

                newCommunity.imageUrl = downloadUrl.toString()

                MyChatManager.createCommunity(object : NotifyMeInterface {
                    override fun handleData(obj: Any, requestCode: Int?) {
                        progress.dismiss()
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
