package br.com.fiap.mob20_android_trabalhoconclusao.data.remote.datasource

import br.com.fiap.mob20_android_trabalhoconclusao.domain.entity.NewUser
import br.com.fiap.mob20_android_trabalhoconclusao.domain.entity.RequestState
import br.com.fiap.mob20_android_trabalhoconclusao.domain.entity.User
import br.com.fiap.mob20_android_trabalhoconclusao.domain.entity.UserLogin
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class UserRemoteFirebaseDataSourceImpl(
    private val mAuth: FirebaseAuth
): UserRemoteDataSource {

    override suspend fun getUserLogged(): RequestState<User> {

        mAuth.currentUser?.reload()
        val firebaseUser = mAuth.currentUser
        return if (firebaseUser == null) {
             RequestState.Error(Exception("Usuário não logado"))
        } else {
             RequestState.Success(User(firebaseUser.displayName ?: ""))
         }
    }

    override suspend fun doLogin(userLogin: UserLogin): RequestState<User> {
        return try {
            mAuth.signInWithEmailAndPassword(userLogin.email.trim(), userLogin.password).await()

            val firebaseUser = mAuth.currentUser
            if (firebaseUser == null) {
                RequestState.Error(Exception("Usuário ou senha inválidos"))
            } else {
                RequestState.Success(User(firebaseUser.displayName ?: ""))
            }

        } catch (e: Exception){
            RequestState.Error(Exception(e))
        }
    }

    override suspend fun resendPassword(email: String):
            RequestState<String> {
        return try {
            mAuth.sendPasswordResetEmail(email).await()

            RequestState.Success("Verifique sua caixa de e-mail")
        } catch (e: java.lang.Exception) {
            RequestState.Error(e)
        }
    }

    override suspend fun create(newUser: NewUser): RequestState<User> {
        return try{
            mAuth.createUserWithEmailAndPassword(newUser.email, newUser.password).await()

//            val userId = mAuth.currentUser?.uid
//            if (userId == null) {
//                RequestState.Error(java.lang.Exception("Não foi possível criar a conta"))
//            } else {
//                val newUserFirebasePayload = NewUserFirebasePayloadMapper.mapToNewUserFirebasePayload(newUser)
//
//                firebaseFirestore
//                    .collection("users")
//                    .document(userId)
//                    .set(newUserFirebasePayload)
//                    .await()
//                RequestState.Success(NewUserFirebasePayloadMapper.mapToUser(newUserFirebasePayload))
//            }

            RequestState.Success(User(newUser.name))
        } catch (e: java.lang.Exception) {
            RequestState.Error(e)
        }
    }
}