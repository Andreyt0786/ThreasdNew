package ru.netology.nmedia.activity

import android.app.Activity
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toFile
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentNewPostBinding
import ru.netology.nmedia.model.PhotoModel
import ru.netology.nmedia.util.AndroidUtils
import ru.netology.nmedia.util.StringArg
import ru.netology.nmedia.viewmodel.PostViewModel

@AndroidEntryPoint
class NewPostFragment : Fragment() {

    companion object {
        var Bundle.textArg: String? by StringArg
    }


    private val viewModel: PostViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentNewPostBinding.inflate(
            inflater,
            container,
            false
        )

        val launch =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                val data = result.data

                when (result.resultCode) {
                    Activity.RESULT_OK -> {
                        //Image Uri will not be null for RESULT_OK
                        val uri = data?.data!!
                        val file = uri.toFile()

                        viewModel.changePhoto(PhotoModel(uri, file))
                    }
                    ImagePicker.RESULT_ERROR -> {
                        Toast.makeText(
                            requireContext(),
                            ImagePicker.getError(data),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    else -> {
                        Toast.makeText(requireContext(), "Task Cancelled", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }


        arguments?.textArg
            ?.let(binding.edit::setText)

        activity?.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.new_post_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.save -> {
                        viewModel.changeContent(binding.edit.text.toString())
                        viewModel.save()
                        AndroidUtils.hideKeyboard(requireView())
                        true
                    }
                    else -> false

                }
            }

        }, viewLifecycleOwner)

        binding.pickPhoto.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(2048)
                .galleryOnly()
                .createIntent { launch.launch(it) }
        }

        binding.takePhoto.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(2048)
                .cameraOnly()
                .createIntent { launch.launch(it) }
        }



        viewModel.photoState.observe(viewLifecycleOwner) { photoState ->
            if (photoState == null) {
                binding.imagePreviewContainer.isVisible = false
                return@observe
            }
            binding.imagePreviewContainer.isVisible = true
            binding.photoPreview.setImageURI(photoState.uri)
        }

        binding.remove.setOnClickListener {
            viewModel.changePhoto(null)
        }

        viewModel.postCreated.observe(viewLifecycleOwner) {
            viewModel.loadPosts()
            findNavController().navigateUp()
        }
        return binding.root
    }
}