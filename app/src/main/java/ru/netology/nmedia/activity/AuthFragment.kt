package ru.netology.nmedia.activity

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.PhotoFragment.Companion.textArg
import ru.netology.nmedia.adapter.OnInteractionListener
import ru.netology.nmedia.adapter.PostsAdapter
import ru.netology.nmedia.databinding.FragmentAuthBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.viewmodel.AuthViewModel
import ru.netology.nmedia.viewmodel.IdenticViewModel
import ru.netology.nmedia.viewmodel.PostViewModel

@AndroidEntryPoint
class AuthFragment : Fragment() {

    private val identicViewModel: IdenticViewModel by viewModels()
    private val viewModel: PostViewModel by activityViewModels()
    private val authViewModel: AuthViewModel by viewModels()

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

        val adapter = PostsAdapter(object : OnInteractionListener {

            override fun previewPhoto(post: Post) {
                findNavController().navigate(R.id.action_feedFragment_to_photoFragment,
                    Bundle().apply { textArg = post.attachment?.url })
            }

            override fun onEdit(post: Post) {
                viewModel.edit(post)
            }

            override fun onLike(post: Post) {
                if (authViewModel.isAuthorized) {
                    viewModel.likeById(post)
                } else {
                    findNavController().navigate(R.id.action_feedFragment_to_authFragment)
                }
            }

            override fun onRemove(post: Post) {
                viewModel.removeById(post.id)
            }

            override fun onShare(post: Post) {
                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, post.content)
                    type = "text/plain"
                }

                val shareIntent =
                    Intent.createChooser(intent, getString(R.string.chooser_share_post))
                startActivity(shareIntent)
            }
        })

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
            adapter.refresh()
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