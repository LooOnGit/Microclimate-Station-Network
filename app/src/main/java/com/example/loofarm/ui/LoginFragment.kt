package com.example.loofarm.ui

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.loofarm.databinding.FragmentLoginBinding
import com.example.loofarm.R
import com.example.loofarm.model.ManagerUser
import com.example.loofarm.model.SignInWithGoogle
import com.example.loofarm.model.User
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private lateinit var auth: FirebaseAuth

    private lateinit var databaseReference:DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(layoutInflater, container, false)
        initEvents()

        return binding.root
    }


    private fun initEvents() {
//        binding.btnLoginGG.setOnClickListener {
//            signInGoogle()
//        }
    }

    private fun signInGoogle() {
        val signInIntent = SignInWithGoogle(requireContext()).signInIntent
        launcher.launch(signInIntent)
    }

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {

                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                handleResults(task)
            }
        }

    private fun handleResults(task: Task<GoogleSignInAccount>) {
        if (task.isSuccessful) {
            val account: GoogleSignInAccount? = task.result
            if (account != null) {
                updateUI(account)
            }
        } else {
            Toast.makeText(this.context, task.exception.toString(), Toast.LENGTH_SHORT).show()
        }
    }


    private fun updateUI(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        ManagerUser.setId(account.id.toString())
        ManagerUser.setMail(account.email.toString())
        auth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                var ds:MutableList<String> = mutableListOf()

                databaseReference = FirebaseDatabase.getInstance().getReference(account.id.toString()+"/id")
                databaseReference.addValueEventListener(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        ds.clear()
                        if(snapshot.exists()){
//                            for(empSnap in snapshot.children){
//                                ds.add(empSnap.getValue(String::class.java).toString())
//                                Toast.makeText(requireContext(), "$ds", Toast.LENGTH_SHORT).show()
//
//                            }
                        }

                        Toast.makeText(requireContext(), "$ds", Toast.LENGTH_SHORT).show()
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }
                })

//                val databaseReference = FirebaseDatabase.getInstance().reference
//                val userId = "5"
//
//                databaseReference.child(account.id.toString()).child(userId).child("id")
//                    .get()
//                    .addOnSuccessListener { dataSnapshot ->
//                        val displayName = dataSnapshot.value as? String
//                        Toast.makeText(requireContext(), "$displayName", Toast.LENGTH_LONG).show()
//                    }
//                    .addOnFailureListener { e ->
//                        Toast.makeText(requireContext(), "Error getting data: ${e.message}", Toast.LENGTH_LONG).show()
//                    }


                // Tạo Fragment mục tiêu
                val targetFragment = HomeFragment()
                // Thực hiện transaction để chuyển đổi Fragment
                val transaction = requireActivity().supportFragmentManager
                    .beginTransaction().setCustomAnimations(
                        R.anim.slide_in,
                        R.anim.fade_out,
                        R.anim.fade_in,
                        R.anim.slide_out
                    )
                transaction.replace(R.id.frLayout, targetFragment)
                transaction.addToBackStack(null)
                transaction.commit()

                Toast.makeText(requireActivity(), "thad", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this.context, "Not Internet", Toast.LENGTH_LONG).show()

            }
        }
    }
}