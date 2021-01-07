package com.alexbezhan.instagram.screens.profile

import androidx.lifecycle.LiveData
import com.alexbezhan.instagram.data.FeedPostsRepository
import com.alexbezhan.instagram.data.UsersRepository
import com.alexbezhan.instagram.models.User
import com.alexbezhan.instagram.screens.common.BaseViewModel
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks

class ProfileViewModel(private val usersRepo: UsersRepository,
                       private val feedPostsRepo: FeedPostsRepository,
                       onFailureListener: OnFailureListener)
    : BaseViewModel(onFailureListener) {
    lateinit var user: LiveData<User>
    lateinit var images: LiveData<List<String>>

    fun init(uid: String) {
        if (!this::user.isInitialized) {
            user = usersRepo.getUser(uid)
        }
        if (!this::images.isInitialized) {
            images = usersRepo.getImages(uid)
        }
    }

    fun setFollow(currentUid: String, followUid: String, follow: Boolean): Task<Void> {
        return (if (follow) {
            Tasks.whenAll(
                usersRepo.addFollow(currentUid, followUid),
                usersRepo.addFollower(currentUid, followUid),
                feedPostsRepo.copyFeedPosts(postsAuthorUid = followUid, uid = currentUid))
        } else {
            Tasks.whenAll(
                usersRepo.deleteFollow(currentUid, followUid),
                usersRepo.deleteFollower(currentUid, followUid),
                feedPostsRepo.deleteFeedPosts(postsAuthorUid = followUid, uid = currentUid))
        }).addOnFailureListener(onFailureListener)
    }
}