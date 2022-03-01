package com.me.kt_cook_book.ui.fragments.details

import android.os.Bundle
import android.view.MenuItem
import androidx.fragment.app.Fragment
import android.view.View
import androidx.activity.addCallback
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.me.kt_cook_book.R
import com.me.kt_cook_book.databinding.FragmentDetailsBinding
import com.me.kt_cook_book.ui.adapters.PagerAdapter
import com.me.kt_cook_book.viewmodels.DetailsViewModel
import com.me.kt_cook_book.viewmodels.MainViewModel

class DetailsFragment : Fragment(R.layout.fragment_details) {
    private var _binding: FragmentDetailsBinding? = null
    private val binding  get() = _binding!!
    private val mainViewModel by activityViewModels<MainViewModel>()
    private val detailsViewModel by viewModels<DetailsViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainViewModel.setDisplayBottomNavFlow(false)

        _binding = FragmentDetailsBinding.bind(view)

        initFragmentTabs()

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            onBackBtnAction()
        }

        setHasOptionsMenu(true)
    }

    private fun initFragmentTabs() {
        val tabs = binding.tabLayout
        val viewPager = binding.viewPager
        val fragments = arrayListOf<Fragment>(
            OverviewFragment(),
            IngredientsFragment(),
            InstructionsFragment()
        )
        val titles = arrayListOf("Overview", "Ingredients", "Instructions")
        val adapter = PagerAdapter(fragments, titles, this)

        viewPager.adapter = adapter
        tabs.setupWithViewPager(viewPager, titles)

    }

    private fun TabLayout.setupWithViewPager(viewPager: ViewPager2, labels: List<String>) {

        if (labels.size != viewPager.adapter?.itemCount)
            throw Exception("The size of list and the tab count should be equal!")

        TabLayoutMediator(this, viewPager) { tab, position ->
            tab.text = labels[position]
        }.attach()
    }

    private fun onBackBtnAction(){
        mainViewModel.setDisplayBottomNavFlow(true)
        findNavController().popBackStack()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            android.R.id.home -> onBackBtnAction()
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}