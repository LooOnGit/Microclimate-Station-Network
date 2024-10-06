package com.example.loofarm.ui.notification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.loofarm.adapter.NotificationAdapter
import com.example.loofarm.databinding.FragmentNotificationBinding

class NotificationFragment : Fragment() {
    private var binding: FragmentNotificationBinding? = null
    private lateinit var itemAdapter: NotificationAdapter
    private var listNotices: MutableList<String> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNotificationBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    private fun initViews() {
        listNotices.clear()
        listNotices.add("Thông báo 1")
        binding?.apply {
            rcNotification.layoutManager = GridLayoutManager(requireActivity(), 1)
            itemAdapter = NotificationAdapter(listNotices)
            rcNotification.adapter = itemAdapter
        }
    }
}
