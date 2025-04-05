package com.example.waco.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.waco.R

class UtwardzaczFragment : Fragment() {

    // Ta metoda jest wywoływana, gdy fragment jest tworzony i widok jest inflowany
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflacja layoutu fragmentu
        return inflater.inflate(R.layout.fragment_podklady, container, false)
    }
}
