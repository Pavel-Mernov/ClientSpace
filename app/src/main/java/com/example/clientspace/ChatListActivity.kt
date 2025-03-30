package com.example.clientspace

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.clientspace.databinding.ActivityChatListBinding
import com.example.clientspace.ui.Chat
import com.example.clientspace.ui.UserRepository


class ChatListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatListBinding

    private lateinit var displayChats : List<Chat>

    private lateinit var curUserId : String

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        curUserId = intent.getStringExtra("curUserId") ?: run {
            val errorString = "No user Id passed"
            Log.e("User id", errorString)
            throw Exception(errorString)
        }

        val curUser = UserRepository.findUserById(curUserId)


        if (curUser == null) {
            Log.e("Start main window", "No user was passed")
            throw Exception("No user was passed")
        }



        val chats = curUser.chats

        // Настройка RecyclerView
        binding.chatListRecyclerView.layoutManager = LinearLayoutManager(this)
        bind(chats)
        displayChats = chats


        val tvProfile = binding.tvProfile
        val chatSearchView = binding.svChat

        tvProfile.setOnClickListener{
            val newIntent = Intent(this, UserDetailsActivity::class.java)
            newIntent.putExtra("userId", curUserId)
            startActivity(newIntent)
        }

        chatSearchView.setOnEditorActionListener{ _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = chatSearchView.text.toString()


                val notContactedUsers = UserRepository.getNotChattedUsers(curUser)

                // Log.d("search", "Not contacted users: ${notContactedUsers.joinToString { it.userName }}")

                val searchChats = notContactedUsers.map{
                    Chat(
                        avatarImage = it.image,
                        name = it.userName,
                        isForTwo = true,
                        otherMembers = listOf(it.userId).toMutableList(),
                    )
                }.toMutableList()

                searchChats.addAll(chats)

                if (query.isNotBlank()) {

                    val queryRegex = Regex(query, RegexOption.IGNORE_CASE)



                    // Log.d("search", "Search chat names: all: ${searchChats.joinToString { it.name }}")

                    val filteredChats = searchChats.filter {
                        /*
                        Log.e("Regex match:",
                            "$queryRegex to ${it.name}: " + queryRegex.containsMatchIn(it.name).toString())
                         */
                        queryRegex.containsMatchIn(it.name)
                    }

                    // Log.d("search", "Filtered chat names: all: ${filteredChats.joinToString { it.name }}")

                    bind(filteredChats)


                    // Log.d("search", "Query: $query contain match in Лунтик: ${queryRegex.containsMatchIn("Лунтик")}")

                    displayChats = filteredChats
                }
                else {
                    bind(searchChats)

                    displayChats = searchChats
                }

                chatSearchView.clearFocus()
                true
            }
            else {
                false
            }
        }

        val searchButton = binding.searchButton
        searchButton.setOnClickListener{
            if (tvProfile.visibility == View.VISIBLE) {
                tvProfile.visibility = View.GONE
                try {
                    chatSearchView.visibility = View.VISIBLE

                    val notContactedUsers = UserRepository.getNotChattedUsers(curUser)

                    val searchChats = notContactedUsers.map{
                        Chat(
                            avatarImage = it.image,
                            name = it.userName,
                            isForTwo = true,
                            otherMembers = listOf(it.userId).toMutableList(),
                        )
                    }.toMutableList()

                    searchChats.addAll(chats)

                    bind(searchChats)

                    displayChats = searchChats
                }
                catch (e : Exception) {
                    e.message?.let { it1 -> Log.e("open search view", it1) }
                }

                searchButton.setImageResource(R.drawable.ic_close)
            }

            else {
                tvProfile.visibility = View.VISIBLE
                chatSearchView.visibility = View.GONE

                chatSearchView.text.clear()

                searchButton.setImageResource(R.drawable.ic_search)
                bind(chats)
            }


        }
    }

    private fun bind(chats : List<Chat>) {
        binding.chatListRecyclerView.adapter = ChatLinkAdapter(chats, curUserId)
    }

    override fun onResume() {


        // if we were searching certain chats, than we should display only filtered chats
        super.onResume()
        bind(displayChats)
    }
}
