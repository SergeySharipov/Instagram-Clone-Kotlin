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
        val userId = intent.getStringExtra("EXTRA_USER_ID")
        val navNumber = intent.getIntExtra("EXTRA_NAV_NUMBER", 0)
        Log.d(TAG, "onCreate: EXTRA_USER_ID: ${userId}; EXTRA_NAV_NUMBER: ${navNumber};")

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
        images_recycler.layoutManager = GridLayoutManager(this, 3)
        mAdapter = ImagesAdapter()
        images_recycler.adapter = mAdapter

        setupAuthGuard { uid ->
            mViewModel = initViewModel()
            if (userId != null && userId != uid) {
                edit_profile_btn.visibility = View.GONE
                settings_image.visibility = View.INVISIBLE
                add_friends_image.visibility = View.INVISIBLE

                follow_btn.setOnClickListener {
                    mViewModel.setFollow(uid,userId,true)
                }
                unfollow_btn.setOnClickListener {
                    mViewModel.setFollow(uid,userId,false)
                }

                mViewModel.init(userId)
                setupBottomNavigation(uid, navNumber)
            } else {
                follow_btn.visibility = View.GONE
                unfollow_btn.visibility = View.GONE

                mViewModel.init(uid)
                setupBottomNavigation(uid, 4)
            }
            mViewModel.user.observe(this, {
                it?.let {
                    profile_image.loadUserPhoto(it.photo)
                    username_text.text = it.username
                    followers_count_text.text = it.followers.size.toString()
                    following_count_text.text = it.follows.size.toString()

                    if(userId != null && userId != uid) {
                        val follows = it.followers[uid] ?: false
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

    companion object {
        const val TAG = "ProfileActivity"
    }
}