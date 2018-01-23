package com.go26.chatapp.ui


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.*

import com.go26.chatapp.R



class ContactsFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view: View? = inflater?.inflate(R.layout.fragment_contacts, container, false)
        setViews(view)
        return view
    }

    private fun setViews(view: View?) {
        //actionbar
        val toolbar: Toolbar? = view?.findViewById(R.id.toolbar)
        val activity: AppCompatActivity = activity as AppCompatActivity
        activity.setSupportActionBar(toolbar)
        activity.supportActionBar?.setDisplayShowTitleEnabled(true)
        activity.supportActionBar?.title = "Contacts"
        setHasOptionsMenu(true)

    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater!!.inflate(R.menu.contacts_toolbar,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.add_community -> {
                val createCommunityFragment = CreateCommunityFragment.newInstance()
                val fragmentManager: FragmentManager = activity.supportFragmentManager
                val fragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.fragment, createCommunityFragment)
                fragmentTransaction.addToBackStack(null)
                fragmentTransaction.commit()
                return true
            }
            else -> {
                return false
            }
        }
    }
    companion object {

        fun newInstance(): ContactsFragment {
            val fragment = ContactsFragment()
            val args = Bundle()

            fragment.arguments = args
            return fragment
        }
    }

}// Required empty public constructor
