package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.R
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.databinding.FragmentSingOutBinding
import ru.netology.nmedia.viewmodel.IdenticViewModel

@AndroidEntryPoint
class SignOutFragment(
    private val appAuth: AppAuth
) : Fragment() {

    private val identicViewModel: IdenticViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentSingOutBinding.inflate(
            inflater,
            container,
            false
        )

        //  val login = binding.login.text.toString()
        // val pass = binding.pass.text.toString()


        binding.No.setOnClickListener {
            findNavController().navigate(R.id.action_signOutFragment_to_feedFragment)
        }

        binding.Yes.setOnClickListener {
            appAuth.removeUser()
            findNavController().navigate(R.id.action_signOutFragment_to_feedFragment)
        }




        return binding.root
    }
}