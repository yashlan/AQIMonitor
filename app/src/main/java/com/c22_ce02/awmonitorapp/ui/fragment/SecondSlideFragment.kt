package com.c22_ce02.awmonitorapp.ui.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.c22_ce02.awmonitorapp.BuildConfig
import com.c22_ce02.awmonitorapp.R
import com.c22_ce02.awmonitorapp.data.preference.CheckHelper
import com.c22_ce02.awmonitorapp.data.preference.CheckPreference
import com.c22_ce02.awmonitorapp.data.preference.UserPreference
import com.c22_ce02.awmonitorapp.databinding.FragmentSecondSlideBinding
import com.c22_ce02.awmonitorapp.ui.activity.HomeActivity
import com.c22_ce02.awmonitorapp.ui.activity.LoginActivity
import com.c22_ce02.awmonitorapp.utils.showToast

class SecondSlideFragment : Fragment(), View.OnClickListener {

    private lateinit var binding: FragmentSecondSlideBinding
    private lateinit var checkHelper: CheckHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkHelper = CheckHelper()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSecondSlideBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val btnNext = binding.btnNext
        val btnSkip = binding.secondBtnSkip

        btnNext.setOnClickListener(this)
        btnSkip.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        if (v?.id == R.id.btn_next) {
            val mThirdSlideFragment = ThirdSlideFragment()
            val mFragmentManager = parentFragmentManager
            mFragmentManager.beginTransaction().apply {
                setCustomAnimations(
                    R.anim.slide_in,
                    R.anim.fade_out
                )
                replace(
                    R.id.frame_container,
                    mThirdSlideFragment,
                    ThirdSlideFragment::class.java.simpleName
                )
                addToBackStack(null)
                commit()
            }
        } else if (v?.id == R.id.second_btn_skip) {
            savePref()
            val user = UserPreference(requireContext())
            if (!user.isSessionEmpty()) {
                startActivity(Intent(requireContext(), LoginActivity::class.java))
                activity?.finish()
            } else {
                startActivity(Intent(requireContext(), HomeActivity::class.java))
                activity?.finish()
            }
        }
    }

    private fun savePref() {
        val checkPreference = CheckPreference(requireContext())
        checkHelper.isUserFinishBoarding = true
        checkPreference.setCheckBoarding(checkHelper)
        if (BuildConfig.DEBUG) {
            showToast("Selamat Datang")
        }
    }
}
