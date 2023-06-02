package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.*
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentAuthBinding
import ru.netology.nmedia.viewmodel.IdenticViewModel

@AndroidEntryPoint
class AuthFragment : Fragment() {

    private val identicViewModel: IdenticViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentAuthBinding.inflate(
            inflater,
            container,
            false
        )

        //  val login = binding.login.text.toString()
        // val pass = binding.pass.text.toString()


        binding.enter.setOnClickListener {
            identicViewModel.getIdToken(
                binding.login.text.toString().trim(),
                binding.pass.text.toString().trim()
            )
        }



        identicViewModel.tokenServer.observe(viewLifecycleOwner) { state ->
            binding.authGroup.isVisible = state.complete
            binding.newPostGroup.isVisible = state.firstView
            binding.apiErrorGroup.isVisible = state.errorApi
            binding.errorException.isVisible = state.error
        }

        binding.complete.setOnClickListener {
            findNavController().navigate(R.id.action_authFragment_to_feedFragment)
        }


        binding.errorButtom.setOnClickListener {
            binding.authGroup.isVisible = false
            binding.newPostGroup.isVisible = true
            binding.apiErrorGroup.isVisible = false
            binding.errorException.isVisible = false
        }

        binding.apiErrorButtom.setOnClickListener {
            binding.authGroup.isVisible = false
            binding.newPostGroup.isVisible = true
            binding.apiErrorGroup.isVisible = false
            binding.errorException.isVisible = false
        }

        return binding.root
    }
}