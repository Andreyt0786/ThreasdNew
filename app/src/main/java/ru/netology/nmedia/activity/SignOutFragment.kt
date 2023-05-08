package ru.netology.nmedia.activity

import android.app.Activity
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.edit
import androidx.core.net.toFile
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.netology.nmedia.R
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.databinding.FragmentAuthBinding
import ru.netology.nmedia.databinding.FragmentNewPostBinding
import ru.netology.nmedia.databinding.FragmentSingOutBinding
import ru.netology.nmedia.identic.Identic
import ru.netology.nmedia.model.AuthModel
import ru.netology.nmedia.model.IdenticModel
import ru.netology.nmedia.model.PhotoModel
import ru.netology.nmedia.util.AndroidUtils
import ru.netology.nmedia.util.StringArg
import ru.netology.nmedia.viewmodel.AuthViewModel
import ru.netology.nmedia.viewmodel.IdenticViewModel
import ru.netology.nmedia.viewmodel.PostViewModel

class SignOutFragment : Fragment() {

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
            AppAuth.getInstance().removeUser()
            findNavController().navigate(R.id.action_signOutFragment_to_feedFragment)
        }



      
        return binding.root
    }
}