package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.*
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentPhotoBinding
import ru.netology.nmedia.util.StringArg
import ru.netology.nmedia.viewmodel.PostViewModel

@AndroidEntryPoint
class PhotoFragment : Fragment() {

    companion object {
        var Bundle.textArg: String? by StringArg
    }


    private val viewModel: PostViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentPhotoBinding.inflate(
            inflater,
            container,
            false
        )


        activity?.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.new_post_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.save -> {
                        findNavController().navigateUp()
                        true
                    }
                    else -> false

                }
            }

        }, viewLifecycleOwner)

        val urlImage = "http://192.168.31.54:9090/media/${arguments?.textArg}"

        Glide.with(binding.photoPreview)
            .load(urlImage)
            .timeout(10000)
            .circleCrop()
            .into(binding.photoPreview)

        return binding.root
    }
}