package com.example.appcentassignment.views.fragment.curiosity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DefaultItemAnimator
import com.example.appcentassignment.adapter.ImageItemAdapter
import com.example.appcentassignment.base.BaseFragment
import com.example.appcentassignment.databinding.FragmentRecyclerviewBinding
import com.example.appcentassignment.extenssions.showToastMsg
import com.example.appcentassignment.models.response.Photo
import android.widget.ArrayAdapter
import com.example.appcentassignment.utils.DialogUtils
import android.widget.AdapterView
import com.example.appcentassignment.R

class CuriosityFragment : BaseFragment(), ImageItemAdapter.OnItemClickListener,
    CuriosityViewModel.View {

    private lateinit var binding: FragmentRecyclerviewBinding
    private var imageItemAdapter: ImageItemAdapter? = null
    private val curiosityViewModel: CuriosityViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentRecyclerviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        imageItemAdapter = ImageItemAdapter(this)
        binding.fragmentTitle.text = "Curiosity"

        curiosityViewModel.let {
            it.attachView(this)
            it.getCuriosityItemList()
            }

        onObserveItemList()


        curiosityViewModel.getCuriosityItemList()
        binding.spItem.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val spinnerSelectedItem = curiosityViewModel.cameraList.value?.get(position).toString()
                curiosityViewModel.onSearchCamera(spinnerSelectedItem)
            }

        }
        imageItemAdapter.let {
            binding.rvImages.apply {
                itemAnimator = DefaultItemAnimator()
                adapter = it
            }
        }
    }

    override fun clickListener(photo: Photo) {
        DialogUtils.showPopup(
            photo,
            this.layoutInflater.inflate(com.example.appcentassignment.R.layout.popup, null)
        )
    }

    //once we get the data from repo, populate it with the help of the adapter, NewsAdapter()
    private fun onObserveItemList() {
        curiosityViewModel.photoItemList.observe(viewLifecycleOwner) {
            it?.let {
                imageItemAdapter?.setItems(it)
            }
        }

        curiosityViewModel.cameraList.observe(viewLifecycleOwner) {
            it?.let {
                val spinnerAdapter = ArrayAdapter(
                    mainActivity, android.R.layout.simple_spinner_item, it
                )

                spinnerAdapter.setDropDownViewResource(
                    android.R.layout.simple_spinner_dropdown_item
                )
                binding.spItem.adapter = spinnerAdapter
            }
        }

        curiosityViewModel.cameraListData.observe(requireActivity()){
            it.let {
                imageItemAdapter?.setItems(it)
            }
        }
    }

    override fun onUpdateResponse(message: String) {
        showToastMsg(message)
    }

    override fun showProgressBar() {
        progressDialog.show()
    }

    override fun dismissProgressBar() {
        progressDialog.dismiss()
    }
}