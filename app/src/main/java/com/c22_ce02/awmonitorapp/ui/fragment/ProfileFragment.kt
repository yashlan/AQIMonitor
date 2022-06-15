package com.c22_ce02.awmonitorapp.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AlphaAnimation
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.c22_ce02.awmonitorapp.R
import com.c22_ce02.awmonitorapp.data.preference.UserPreference
import com.c22_ce02.awmonitorapp.databinding.FragmentProfileBinding
import com.c22_ce02.awmonitorapp.ui.activity.LoginActivity
import com.c22_ce02.awmonitorapp.utils.createCustomAlertDialog
import com.c22_ce02.awmonitorapp.utils.loadImageViaGlide
import com.google.android.material.bottomsheet.BottomSheetDialog
import de.hdodenhof.circleimageview.CircleImageView

class ProfileFragment : Fragment(R.layout.fragment_profile) {


    private val binding by viewBinding(FragmentProfileBinding::bind)
    private lateinit var user: UserPreference

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        user = UserPreference(requireContext())

        with(binding) {

            val id = requireContext().resources.getIdentifier(
                UserPreference(requireContext()).getAvatar() ?: DEFAULT_AVATAR,
                "drawable",
                requireContext().packageName
            )
            loadImageViaGlide(id, binding.imgProfile)

            tvUsername.text = user.getName()
            tvEmail.text = user.getEmail()

            btnEditPhoto.setOnClickListener {
                it.startAnimation(AlphaAnimation(it.alpha, .5f))
                showBottomSheetChangeAvatar()
            }

            btnLogout.setOnClickListener {
                createCustomAlertDialog(
                    title = "Konfirmasi",
                    message = "apakah anda yakin ingin keluar?",
                    gifRes = R.drawable.exit_app,
                    actionPositiveButton = {
                        logout()
                    }
                )
            }
        }
    }

    private fun logout() {
        user.deleteSession { isSessionEmpty ->
            if (isSessionEmpty) {
                startActivity(Intent(requireContext(), LoginActivity::class.java))
                requireActivity().finish()
            }
        }
    }

    private fun showBottomSheetChangeAvatar() {
        val sheet = BottomSheetDialog(requireContext(), R.style.CustomBottomSheetDialog)
        sheet.setContentView(R.layout.bottom_sheet_choose_avatar)

        val avatar1 = sheet.findViewById<CircleImageView>(R.id.avatar_1)
        val avatar2 = sheet.findViewById<CircleImageView>(R.id.avatar_2)
        val avatar3 = sheet.findViewById<CircleImageView>(R.id.avatar_3)
        val avatar4 = sheet.findViewById<CircleImageView>(R.id.avatar_4)
        val avatar5 = sheet.findViewById<CircleImageView>(R.id.avatar_5)
        val avatar6 = sheet.findViewById<CircleImageView>(R.id.avatar_6)

        setAvatar(avatar1, "avatar_1")
        setAvatar(avatar2, "avatar_2")
        setAvatar(avatar3, "avatar_3")
        setAvatar(avatar4, "avatar_4")
        setAvatar(avatar5, "avatar_5")
        setAvatar(avatar6, "avatar_6")

        if (!sheet.isShowing) {
            sheet.show()
        }
    }

    private fun setAvatar(imageView: CircleImageView?, avatarName: String) {
        imageView?.setOnClickListener {
            it.startAnimation(AlphaAnimation(1f, .5f))
            val userPref = UserPreference(requireContext())
            userPref.saveAvatar(avatarName, onSave = { avatarSavedValue ->
                if (avatarSavedValue != null) {
                    val id = requireContext().resources.getIdentifier(
                        avatarSavedValue,
                        "drawable",
                        requireContext().packageName
                    )
                    loadImageViaGlide(id, binding.imgProfile)
                }
            })
        }
    }

    companion object {
        private const val DEFAULT_AVATAR = "avatar_0"
    }
}