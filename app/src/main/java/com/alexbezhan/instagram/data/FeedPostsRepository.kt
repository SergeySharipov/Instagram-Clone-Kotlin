package com.alexbezhan.instagram.data

import androidx.lifecycle.LiveData
import com.alexbezhan.instagram.models.Comment
import com.alexbezhan.instagram.models.FeedPost
import com.alexbezhan.instagram.models.FeedPostLike
import com.google.android.gms.tasks.Task

interface FeedPostsRepository {
    fun createFeedPost(uid: String, feedPost: FeedPost): Task<Unit>
    fun getFeedPost(uid: String, postId: String): LiveData<FeedPost>
    fun getFeedPosts(uid: String): LiveData<List<FeedPost>>
    fun copyFeedPosts(postsAuthorUid: String, uid: String): Task<Unit>
    fun deleteFeedPosts(postsAuthorUid: String, uid: String): Task<Unit>

    fun toggleLike(postId: String, uid: String): Task<Unit>
    fun getLikes(postId: String): LiveData<List<FeedPostLike>>

    fun createComment(postId: String, comment: Comment): Task<Unit>
    fun getComments(postId: String): LiveData<List<Comment>>
}