package com.me.kt_cook_book.ui.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class PagerAdapter(
    private val fragments: ArrayList<Fragment>,
    private val titles: ArrayList<String>,
    parent: Fragment
    ): FragmentStateAdapter(parent) {

    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }

    fun getPageTitle(position: Int): CharSequence? {
        return titles[position]
    }
}