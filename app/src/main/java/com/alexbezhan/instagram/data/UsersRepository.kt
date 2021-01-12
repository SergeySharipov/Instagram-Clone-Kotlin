package com.alexbezhan.instagram.data

import android.net.Uri
import androidx.lifecycle.LiveData
import com.alexbezhan.instagram.models.User
import com.google.android.gms.tasks.Task

interface UsersRepository {
    fun currentUid(): String?
    fun createUser(user: User, password: String): Task<Unit>
    fun updateUserProfile(currentUser: User, newUser: User): Task<Unit>
    fun getUser(): LiveData<User>
    fun getUser(uid: String): LiveData<User>
    fun getUsers(): LiveData<List<User>>

    fun isUserExistsForEmail(email: String): Task<Boolean>
    fun updateEmail(currentEmail: String, newEmail: String, password: String): Task<Unit>

    fun addFollow(fromUid: String, toUid: String): Task<Unit>
    fun deleteFollow(fromUid: String, toUid: String): Task<Unit>

    fun addFollower(fromUid: String, toUid: String): Task<Unit>
    fun deleteFollower(fromUid: String, toUid: String): Task<Unit>

    fun updateUserPhoto(downloadUrl: Uri): Task<Unit>
    fun uploadUserPhoto(localImage: Uri): Task<Uri>

    fun setUserImage(uid: String, downloadUri: Uri): Task<Unit>
    fun uploadUserImage(uid: String, imageUri: Uri): Task<Uri>
    fun getImages(uid: String): LiveData<List<String>>
}