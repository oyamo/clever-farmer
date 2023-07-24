package com.oyasis.fruity.data.repository

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.oyasis.fruity.data.dao.AuthRepository
import com.oyasis.fruity.data.dto.*
import com.oyasis.fruity.data.model.User
import com.oyasis.fruity.data.model.saveToPersistance
import kotlinx.coroutines.tasks.await

class AuthRepoImpl(var context:Context) : AuthRepository{
    private var auth: FirebaseAuth = Firebase.auth
    private val db = Firebase.firestore

    suspend fun getUserByMail(email: String): User? {
        val userCollection = db.collection("farmers")
        val query = userCollection.whereEqualTo("emailAddress", email).limit(1)
        var userResponse: LoginResponse? = null
        val result = query.get().await()
        if (result.isEmpty) return null
        userResponse = result.firstOrNull()?.toObject(LoginResponse::class.java)
        return userResponse.mapToUser()
    }


    override suspend fun login(req: LoginRequest): LoginResponse? {

        val result = auth.signInWithEmailAndPassword(req.emailAddress, req.password).await()
        if(result.user == null)
            return null
        val user = getUserByMail(req.emailAddress) ?: return null

        user.saveToPersistance(context)

        return LoginResponse(
                user.UserId,
                user.EmailAddress,
                user.FirstName,
                user.LastName,
                user.Role,
                user.Telephone,
        )
    }

    override suspend fun logout() {
        if(auth.currentUser != null) {
            auth.signOut()
        }
    }

    override suspend fun register(req: SignupRequest): SignupResponse? {
        val farmersCollection = db.collection("farmers")
        val authResult = auth.createUserWithEmailAndPassword(req.emailAddress, req.password).await()

        if (authResult.user == null) {
            return null
        }
        req.password = "-"
        req.userId = authResult.user!!.uid

        farmersCollection.document(authResult.user?.uid ?: "").set(req).await()
        val response = SignupResponse(req.userId, req.emailAddress, req.firstName, req.lastName, req.role, req.telephone)
        val user =  response.mapToUser()
        user?.saveToPersistance(context)
        return response
    }

    override suspend fun loggedInUser(): User? {
       val user = User.fromPersistance(context)
        if (user.UserId == "")
            return null
        return user
    }
}