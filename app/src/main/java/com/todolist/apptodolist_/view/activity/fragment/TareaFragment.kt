package com.todolist.apptodolist_.view.activity.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import com.todolist.apptodolist_.R

class TareaFragment: Fragment(), PopupMenu.OnMenuItemClickListener {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_tarea,container,false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val menuDots = view.findViewById<ImageView>(R.id.menuDots)

        menuDots.setOnClickListener{
            showPopup(it)
        }
    }

    private fun showPopup(v:View){
        val context = requireContext()
        val popup = PopupMenu(context,v)
        val inflater: MenuInflater =popup.menuInflater
        inflater.inflate(R.menu.menu_dots,popup.menu)
        popup.setOnMenuItemClickListener ( this )
        popup.show()
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        TODO("Not yet implemented")
    }

    companion object{
        fun newInstance() : TareaFragment = TareaFragment()
    }
}