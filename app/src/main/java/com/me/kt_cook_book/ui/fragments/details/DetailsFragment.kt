package com.me.kt_cook_book.ui.fragments.details

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.activity.addCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.me.kt_cook_book.R
import com.me.kt_cook_book.databinding.FragmentDetailsBinding
import com.me.kt_cook_book.ui.adapters.PagerAdapter
import com.me.kt_cook_book.utility.exhaustive
import com.me.kt_cook_book.viewmodels.DetailsViewModel
import com.me.kt_cook_book.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
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

        getDetailsEvents()

        setHasOptionsMenu(true)
    }

    private fun initFragmentTabs() {
        val tabs = binding.tabLayout
        val viewPager = binding.viewPager
        val fragments = arrayListOf(
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.details_menu, menu)

        val menuItem = menu.findItem(R.id.save_to_favorites_menu)
        changeMenuItemColor(menuItem)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.save_to_favorites_menu -> {
                detailsViewModel.onFavoritesClick()
                changeMenuItemColor(item)
            }

            android.R.id.home -> onBackBtnAction()
        }

        return super.onOptionsItemSelected(item)
    }

    private fun getDetailsEvents() = lifecycleScope.launch {
        detailsViewModel.detailsEvent.collect { event->
            when(event){
                is DetailsViewModel.DetailsEvent.ShowSnackbar -> {
                    Snackbar.make(
                        requireView(),
                        event.message,
                        Snackbar.LENGTH_LONG
                    ).setAction("okay"){

                    }.show()
                }
            }.exhaustive
        }
    }

    private fun changeMenuItemColor(item: MenuItem){
        val color = if(detailsViewModel.isFavorite) R.color.yellow else R.color.white
        item.icon.setTint(ContextCompat.getColor(requireContext(), color))
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}