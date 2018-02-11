package com.go26.chatapp.ui.contacts


import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import com.go26.chatapp.MyChatManager
import com.go26.chatapp.NotifyMeInterface

import com.go26.chatapp.R
import com.go26.chatapp.adapter.ParticipantsAdapter
import com.go26.chatapp.constants.AppConstants
import com.go26.chatapp.constants.DataConstants.Companion.myFriends
import com.go26.chatapp.constants.DataConstants.Companion.selectedUserList
import com.go26.chatapp.constants.NetworkConstants
import com.go26.chatapp.model.CommunityModel
import com.go26.chatapp.model.UserModel
import com.go26.chatapp.util.SharedPrefManager
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.fragment_new_community.*

import java.util.*


class NewCommunityFragment : Fragment(), View.OnClickListener {

    var paticipants: RecyclerView? = null
    var adapter: ParticipantsAdapter? = null
    private var mCropImageUri: Uri? = null
    private var resultUri: Uri? = null
    var storage = FirebaseStorage.getInstance()
    var communityId: String? = null
    var storageRef: StorageReference? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
//        storageRef = storage.getReferenceFromUrl(NetworkConstants().URL_STORAGE_REFERENCE).child(NetworkConstants().FOLDER_STORAGE_IMG)

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

//        activity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        paticipants?.layoutManager = LinearLayoutManager(context)

        // Group Creation Page
        create_group_button.text = "Create Group"

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
                communityMembers.put(user.uid!!, user)
            }
        }

        communityMembers.put(adminUserModel?.uid!!, adminUserModel)

        newCommunity.members = communityMembers

        MyChatManager.setmContext(context)

        if (isValid) {

            //sendFileFirebase(storageRef, resultUri!!, id!!)
            MyChatManager.createCommunity(object : NotifyMeInterface {
                override fun handleData(obj: Any, requestCode: Int?) {
                    Toast.makeText(context, "Community has been created successful", Toast.LENGTH_SHORT).show()
                    activity.supportFragmentManager.beginTransaction().replace(R.id.fragment, ContactsFragment.newInstance()).commit()
                }
            }, newCommunity, NetworkConstants().CREATE_COMMUNITY)
        } else {
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
        }


    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.profile_image_view -> {
//                cropImage()
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
//                            setAdapter()
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

//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
//
//        if (requestCode == CropImage.CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE) {
//            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                CropImage.startPickImageActivity(this)
//            } else {
//                Toast.makeText(this, "Cancelling, required permissions are not granted", Toast.LENGTH_LONG).show()
//            }
//        }
//        if (requestCode == CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE) {
//            if (mCropImageUri != null && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                // mCurrentFragment.setImageUri(mCropImageUri);
//            } else {
//                Toast.makeText(context, "Cancelling, required permissions are not granted", Toast.LENGTH_LONG).show()
//            }
//        }
//
//
//        if (requestCode == ASK_MULTIPLE_PERMISSION_REQUEST_CODE) {
//            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            }
//        }
//    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//
//        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK) {
//            val imageUri = CropImage.getPickImageResultUri(this, data)
//
//            // For API >= 23 we need to check specifically that we have permissions to read external storage,
//            // but we don't know if we need to for the URI so the simplest is to try open the stream and see if we get error.
//            var requirePermissions = false
//            if (CropImage.isReadExternalStoragePermissionsRequired(this, imageUri)) {
//
//                // request permissions and handle the result in onRequestPermissionsResult()
//                requirePermissions = true
//                mCropImageUri = imageUri
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                    requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE)
//                }
//            } else {
//
//                CropImage.activity(imageUri)
//                        .setGuidelines(CropImageView.Guidelines.ON)
//                        .setCropShape(CropImageView.CropShape.RECTANGLE)
//                        .setAspectRatio(1, 1)
//                        .setInitialCropWindowPaddingRatio(0f)
//                        .start(this)
//
//            }
//        }
//        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
//            val result = CropImage.getActivityResult(data)
//            if (resultCode == RESULT_OK) {
//                resultUri = result.uri
//                val img = "data:"
//                val mimeType = getMimeType(resultUri, this) + ";base64,"//data:image/jpeg;base64,
//                val s = img + mimeType + getBase64EncodedImage(resultUri, this) as String
//                //callProfilePictureApi(s)
//                if (id != null) {
//                    sendFileFirebase(storageRef, resultUri!!, id!!)
//                }
//                iv_profile.setImageURI(resultUri)
//            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
//                result.error
//            }
//        }
//
//    }
//
//    private fun getBase64EncodedImage(uri: Uri?, context: Context): String? {
//        return try {
//            val bmp = MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
//            val nh = (bmp.height * (512.0 / bmp.width)).toInt()
//            val scaledBmp = Bitmap.createScaledBitmap(bmp, 512, nh, true)
//            val baos = ByteArrayOutputStream()
//            scaledBmp.compress(Bitmap.CompressFormat.JPEG, 100, baos)
//            val imageBytes = baos.toByteArray()
//            Base64.encodeToString(imageBytes, Base64.DEFAULT)
//        } catch (exception: IOException) {
//            Toast.makeText(context, "Image not found", Toast.LENGTH_LONG).show()
//            null
//        }
//
//    }
//
//
//    private fun getMimeType(uri: Uri?, context: Context): String {
//        return if (uri?.scheme == ContentResolver.SCHEME_CONTENT) {
//            val cr = context.contentResolver
//            cr.getType(uri)
//        } else {
//            val fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri
//                    .toString())
//            MimeTypeMap.getSingleton().getMimeTypeFromExtension(
//                    fileExtension.toLowerCase())
//        }
//    }

    /*
     * Call from fragment to crop image
     **/

//    fun cropImage() {
//        if (CropImage.isExplicitCameraPermissionRequired(this@GroupDetailsActivity)) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                requestPermissions(arrayOf(Manifest.permission.CAMERA), CropImage.CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE)
//            }
//        } else {
//            //CropImage.getPickImageChooserIntent(this)
//            CropImage.startPickImageActivity(this@GroupDetailsActivity)
//        }
//    }



//    private fun sendFileFirebase(storageReference: StorageReference?, file: File, id: String) {
//        if (storageReference != null) {
//            var mFirebaseDatabaseReference: DatabaseReference? = FirebaseDatabase.getInstance().reference.child(FirebaseConstants().COMMUNITY).child(id)
//            val uploadTask = storageReference.putFile(Uri.fromFile(file))
//            uploadTask.addOnFailureListener { e -> Log.e("", "onFailure sendFileFirebase " + e.message) }.addOnSuccessListener { taskSnapshot ->
//                Log.i("", "onSuccess sendFileFirebase")
//                val downloadUrl = taskSnapshot.downloadUrl
//                mFirebaseDatabaseReference?.child(FirebaseConstants().IMAGE_URL)?.setValue(downloadUrl)
//            }
//        } else {
//            //IS NULL
//        }
//
//    }

//    fun sendFileFirebase(storageReference: StorageReference?, file: Uri, id: String) {
//        if (storageReference != null) {
//
//            val name = DateFormat.format("yyyy-MM-dd_hhmmss", Date()).toString()
//            val imageGalleryRef = storageReference.child(name + "_gallery")
//            val uploadTask = imageGalleryRef.putFile(file)
//            uploadTask.addOnFailureListener { e -> Log.e("", "onFailure sendFileFirebase " + e.message) }.addOnSuccessListener { taskSnapshot ->
//                Log.i("", "onSuccess sendFileFirebase")
//                val downloadUrl = taskSnapshot.downloadUrl
//                val fileModel = FileModel("img", downloadUrl!!.toString(), name, "")
//
//                // val chatModel = MessageModel(tfUserModel.getUserId(), ffUserModel.getUserId(), ffUserModel, Calendar.getInstance().time.time.toString() + "", fileModel)
//                FirebaseDatabase.getInstance().reference.child(FirebaseConstants().COMMUNITY).
//                        child(id).child(FirebaseConstants().IMAGE_URL)?.setValue(downloadUrl.toString())
//                Toast.makeText(context, "Group Image Updated successful", Toast.LENGTH_LONG).show()
//            }
//        } else {
//            //IS NULL
//        }
//
//    }

    companion object {
        private const val ASK_MULTIPLE_PERMISSION_REQUEST_CODE: Int = 100

        fun newInstance(): NewCommunityFragment {
            val fragment = NewCommunityFragment()
            val args = Bundle()

            fragment.arguments = args
            return fragment
        }
    }

}// Required empty public constructor
