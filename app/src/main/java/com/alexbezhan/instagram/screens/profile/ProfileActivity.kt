package com.alexbezhan.instagram.screens.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.alexbezhan.instagram.R
import com.alexbezhan.instagram.screens.addfriends.AddFriendsActivity
import com.alexbezhan.instagram.screens.common.*
import com.alexbezhan.instagram.screens.editprofile.EditProfileActivity
import com.alexbezhan.instagram.screens.profilesettings.ProfileSettingsActivity
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : BaseActivity() {
    private lateinit var mAdapter: ImagesAdapter
    private lateinit var mViewModel: ProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        val extraUId = intent.getStringExtra("EXTRA_USER_ID")
        val navNumber = intent.getIntExtra("EXTRA_NAV_NUMBER", 4)
        Log.d(TAG, "onCreate: EXTRA_USER_ID: ${extraUId}; EXTRA_NAV_NUMBER: ${navNumber};")

        images_recycler.layoutManager = GridLayoutManager(this, 3)
        mAdapter = ImagesAdapter()
        images_recycler.adapter = mAdapter

        setupAuthGuard { currentUid ->
            mViewModel = initViewModel()
            mViewModel.init(currentUid, extraUId)

            if (mViewModel.isNotCurrentUserAccount()) {
                setupOtherUserProfile()
                setupBottomNavigation(currentUid, navNumber)
            } else {
                setupCurrentUserProfile()
                setupBottomNavigation(currentUid, 4)
            }
            mViewModel.user.observe(this, {
                it?.let {
                    profile_image.loadUserPhoto(it.photo)
                    username_text.text = it.username
                    followers_count_text.text = it.followers.size.toString()
                    following_count_text.text = it.follows.size.toString()

                    if(mViewModel.isNotCurrentUserAccount()) {
                        val follows = it.followers[currentUid] ?: false
                        if (follows) {
                            follow_btn.visibility = View.GONE
                            unfollow_btn.visibility = View.VISIBLE
                        } else {
                            follow_btn.visibility = View.VISIBLE
                            unfollow_btn.visibility = View.GONE
                        }
                    }
                }
            })
            mViewModel.images.observe(this, {
                it?.let { images ->
                    mAdapter.updateImages(images)
                    posts_count_text.text = images.size.toString()
                }
            })
        }
    }

    private fun setupCurrentUserProfile(){
        follow_btn.visibility = View.GONE
        unfollow_btn.visibility = View.GONE
        back_image.visibility = View.GONE

        edit_profile_btn.setOnClickListener {
            val intent = Intent(this, EditProfileActivity::class.java)
            startActivity(intent)
        }
        settings_image.setOnClickListener {
            val intent = Intent(this, ProfileSettingsActivity::class.java)
            startActivity(intent)
        }
        add_friends_image.setOnClickListener {
            val intent = Intent(this, AddFriendsActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupOtherUserProfile(){
        edit_profile_btn.visibility = View.GONE
        settings_image.visibility = View.GONE
        add_friends_image.visibility = View.GONE

        follow_btn.setOnClickListener {
            mViewModel.setFollow(true)
        }
        unfollow_btn.setOnClickListener {
            mViewModel.setFollow(false)
        }
        back_image.setOnClickListener { finish() }
    }

    companion object {
        const val TAG = "ProfileActivity"
    }
}