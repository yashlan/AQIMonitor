package com.c22_ce02.awmonitorapp.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.c22_ce02.awmonitorapp.R
import com.c22_ce02.awmonitorapp.data.preference.UserPreference
import com.c22_ce02.awmonitorapp.databinding.FragmentProfileBinding
import com.c22_ce02.awmonitorapp.ui.activity.LoginActivity
import com.c22_ce02.awmonitorapp.utils.createCustomAlertDialog

class ProfileFragment : Fragment(R.layout.fragment_profile) {


    private val binding by viewBinding(FragmentProfileBinding::bind)
    private lateinit var user: UserPreference

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        user = UserPreference(requireContext())

        with(binding) {
            //loadImageViaGlide(user.photoUrl, imgProfile)
            tvUsername.text = user.getName()
            tvEmail.text = user.getEmail()

            btnLogout.setOnClickListener {
                createCustomAlertDialog(
                    "Konfirmasi",
                    "apakah anda yakin ingin keluar?",
                    actionPositiveButton = {
                        logout()
                    }
                )
            }
        }
    }

    private fun logout() {
        user.deleteSession {
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            requireActivity().finish()
        }
    }
}