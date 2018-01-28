package com.go26.chatapp.ui


import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.go26.chatapp.MyChatManager
import com.go26.chatapp.NotifyMeInterface

import com.go26.chatapp.R
import com.go26.chatapp.adapter.ParticipantsAdapter
import com.go26.chatapp.constants.AppConstants
import com.go26.chatapp.constants.DataConstants
import com.go26.chatapp.constants.DataConstants.Companion.selectedUserList
import com.go26.chatapp.constants.FirebaseConstants
import com.go26.chatapp.constants.NetworkConstants
import com.go26.chatapp.model.CommunityModel
import com.go26.chatapp.model.UserModel
import com.go26.chatapp.util.MyViewUtils.Companion.loadRoundImage
import com.go26.chatapp.util.SharedPrefManager
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
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
    var communityId: String? = ""
    var storageRef: StorageReference? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
//        storageRef = storage.getReferenceFromUrl(NetworkConstants().URL_STORAGE_REFERENCE).child(NetworkConstants().FOLDER_STORAGE_IMG)



        return inflater!!.inflate(R.layout.fragment_new_community, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        setViews()
    }

    private fun setViews() {
        paticipants?.layoutManager = LinearLayoutManager(context)


        communityId = null

        if (communityId != null) {
            //Group Details page
            btn_creategroup.text = "Update Group Name"
            selectedUserList?.clear()
            et_groupname.setText(DataConstants.communityMap?.get(communityId!!)?.name!!.toString())

            DataConstants.communityMap?.get(communityId!!)?.members?.forEach { member ->
                DataConstants.userMap?.get(member.value.uid)!!.admin = member.value.admin
                DataConstants.userMap?.get(member.value.uid)!!.deleteTill = member.value.deleteTill
                DataConstants.userMap?.get(member.value.uid)!!.unreadCommunityCount = member.value.unreadCommunityCount
                selectedUserList?.add(DataConstants.userMap?.get(member.value.uid)!!)
            }

            loadRoundImage(iv_profile, DataConstants.communityMap?.get(communityId!!)?.imageUrl!!)

            adapter = ParticipantsAdapter(object : NotifyMeInterface {
                override fun handleData(obj: Any, requestCode: Int?) {
                    tv_no_of_participants.setText("" + selectedUserList?.size!! + " Participants")
                }

            }, AppConstants().DETAILS, communityId!!)
            participants.adapter = adapter
            tv_exit_group.visibility = View.VISIBLE
            tv_exit_group.setOnClickListener(this)

        } else {
            // Group Creation Page
            btn_creategroup.text = "Create Group"
            if (selectedUserList.size != 0) {
                adapter = ParticipantsAdapter(object : NotifyMeInterface {
                    override fun handleData(obj: Any, requestCode: Int?) {
                        tv_no_of_participants.setText("" + selectedUserList?.size!! + " Participants")
                    }

                }, AppConstants().CREATION, "23")
                participants.adapter = adapter
                tv_exit_group.visibility = View.GONE
            }
        }

        iv_profile.setOnClickListener(this)
        iv_back.setOnClickListener(this)
        btn_creategroup.setOnClickListener(this)
        tv_no_of_participants.setText("" + selectedUserList?.size!! + " Participants")
        label_hint.setOnClickListener(this)
        label_hint.setText("Add akash.nidhi@interactionone.com to the communities")
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
//                if (communityId != null) {
//                    sendFileFirebase(storageRef, resultUri!!, communityId!!)
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


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_profile -> {
//                cropImage()
            }

            R.id.btn_creategroup -> {
                if (btn_creategroup.text.equals("Create Group")) {
                    createCommunity()
                } else {
                    updateName()
                }

            }

            R.id.iv_back -> {
                fragmentManager.popBackStack()
                fragmentManager.beginTransaction().remove(this).commit()
            }

            R.id.tv_exit_group -> {
                MyChatManager.setmContext(context)
                MyChatManager.removeMemberFromCommunity(object : NotifyMeInterface {
                    override fun handleData(obj: Any, requestCode: Int?) {

                        DataConstants.communityMap?.get(communityId)?.members?.remove(DataConstants.currentUser?.uid)

                        Toast.makeText(context, "You have been exited from communities", Toast.LENGTH_LONG).show()
                        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                        fragmentManager.beginTransaction().remove(this@NewCommunityFragment).commit()
                    }

                }, communityId, DataConstants.currentUser?.uid)
            }
        }
    }

    private fun updateName() {
        if (!et_groupname.text.isBlank() && et_groupname.text.length > 2) {
            var mFirebaseDatabaseReference: DatabaseReference? = FirebaseDatabase.getInstance().reference.child(FirebaseConstants().COMMUNITY).child(communityId)
            mFirebaseDatabaseReference?.child(FirebaseConstants().NAME)?.setValue(et_groupname.text.toString())
            Toast.makeText(context, "Name Updated successful", Toast.LENGTH_LONG).show()
        }
    }

    private fun createCommunity() {
        var isValid = true
        var errorMessage = "Validation Error"

        val communityName: String = et_groupname.text.toString()

        if (communityName.isBlank()) {
            isValid = false
            errorMessage = "Group name is blank"
        }
        if (communityName.length < 3) {
            isValid = false
            errorMessage = "Group name should be more than 2 characters"
        }

        val communityImage = "https://cdn1.iconfinder.com/data/icons/google_jfk_icons_by_carlosjj/128/groups.png"
        val newCommunity = CommunityModel(communityName, communityImage, communityDeleted = false, community = true)
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

            //sendFileFirebase(storageRef, resultUri!!, communityId!!)

            MyChatManager.createCommunity(object : NotifyMeInterface {
                override fun handleData(obj: Any, requestCode: Int?) {
                    Toast.makeText(context, "Group has been created successful", Toast.LENGTH_SHORT).show()
                    fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                    fragmentManager.beginTransaction().remove(this@NewCommunityFragment).commit()
                }

            }, newCommunity, NetworkConstants().CREATE_COMMUNITY)
        } else {
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
        }


    }


//    private fun sendFileFirebase(storageReference: StorageReference?, file: File, communityId: String) {
//        if (storageReference != null) {
//            var mFirebaseDatabaseReference: DatabaseReference? = FirebaseDatabase.getInstance().reference.child(FirebaseConstants().COMMUNITY).child(communityId)
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

//    fun sendFileFirebase(storageReference: StorageReference?, file: Uri, communityId: String) {
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
//                        child(communityId).child(FirebaseConstants().IMAGE_URL)?.setValue(downloadUrl.toString())
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
