package com.example.notify_around.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.notify_around.Adapters.CatAdapter
import com.example.notify_around.Models.CategoriesModel
import com.example.notify_around.R
import com.example.notify_around.databinding.FragmentCategoriesBinding
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CategoriesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CategoriesFragment : Fragment() {
    private var _binding: FragmentCategoriesBinding? = null
    private val binding get() = _binding!!

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var dataholder: ArrayList<CategoriesModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentCategoriesBinding.inflate(inflater, container, false)
        val lm = LinearLayoutManager(context)
        lm.orientation = RecyclerView.HORIZONTAL
        binding.recview.layoutManager = lm //LinearLayoutManager(context)

        dataholder = ArrayList<CategoriesModel>()

        dataholder.add(CategoriesModel(R.drawable.ic_home, "Home"))
        dataholder.add(CategoriesModel(R.drawable.ic_eventnote, "Events"))
        dataholder.add(CategoriesModel(R.drawable.ic_ads, "Ads"))
        dataholder.add(CategoriesModel(R.drawable.ic_skill, "Skills"))
        dataholder.add(CategoriesModel(R.drawable.ic_error, "Problems"))

        binding.recview.adapter = CatAdapter(dataholder)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CategoriesFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CategoriesFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}