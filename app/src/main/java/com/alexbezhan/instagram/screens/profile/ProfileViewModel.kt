package com.alexbezhan.instagram.screens.profile

import androidx.lifecycle.LiveData
import com.alexbezhan.instagram.data.FeedPostsRepository
import com.alexbezhan.instagram.data.UsersRepository
import com.alexbezhan.instagram.models.User
import com.alexbezhan.instagram.screens.common.BaseViewModel
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks

class ProfileViewModel(
    private val usersRepo: UsersRepository,
    private val feedPostsRepo: FeedPostsRepository,
    onFailureListener: OnFailureListener
) : BaseViewModel(onFailureListener) {
    lateinit var user: LiveData<User>
    lateinit var images: LiveData<List<String>>
    private lateinit var currentUid: String
    private var extraUid: String? = null

    fun init(currentUid: String, extraUid: String?) {
        this.currentUid = currentUid
        this.extraUid = extraUid

        val uid: String = if (isNotCurrentUserAccount()) {
            extraUid!!
        } else {
            currentUid
        }

        if (!this::user.isInitialized) {
            user = usersRepo.getUser(uid)
        }
        if (!this::images.isInitialized) {
            images = usersRepo.getImages(uid)
        }
    }

    fun isNotCurrentUserAccount() = extraUid != null && extraUid != currentUid

    fun setFollow(follow: Boolean): Task<Void> {
        return (when {
            extraUid == null -> Tasks.forCanceled()
            follow -> {
                Tasks.whenAll(
                    usersRepo.addFollow(currentUid, extraUid!!),
                    usersRepo.addFollower(currentUid, extraUid!!),
                    feedPostsRepo.copyFeedPosts(postsAuthorUid = extraUid!!, uid = currentUid)
                )
            }
            else -> {
                Tasks.whenAll(
                    usersRepo.deleteFollow(currentUid, extraUid!!),
                    usersRepo.deleteFollower(currentUid, extraUid!!),
                    feedPostsRepo.deleteFeedPosts(postsAuthorUid = extraUid!!, uid = currentUid)
                )
            }
        }).addOnFailureListener(onFailureListener)
    }
}