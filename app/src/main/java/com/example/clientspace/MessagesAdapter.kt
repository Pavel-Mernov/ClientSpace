package com.example.clientspace

import android.content.Context
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

import com.example.clientspace.ui.Message
import com.example.clientspace.ui.Reaction
import kotlinx.coroutines.selects.whileSelect
import java.time.format.DateTimeFormatter

class MessagesAdapter(private val messages: MutableList<Message>, private val userId : String,
    private val audioPlayerView: AudioPlayerView, private val startEditMessage : (Int) -> Unit,
    private val inflateReactionMenuAt : (Int) -> Unit, private val onDismiss : () -> Unit,
    private val updateUser : () -> Unit)
    : RecyclerView.Adapter<MessagesAdapter.MessageViewHolder>() {

        private lateinit var popupMenu : PopupMenu

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val layout = R.layout.item_message
        val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        return MessageViewHolder(view, this, updateUser)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages[position]
        holder.bind(message, position, audioPlayerView)

        val context = holder.itemView.context

        val resId: Int



        // Стилизация для отправленных и полученных сообщений
        val layoutParams = holder.messageContainer.layoutParams as ViewGroup.MarginLayoutParams
        if (getItemViewType(position) == VIEW_TYPE_SENT) { // message sent
            layoutParams.marginEnd = 16
            layoutParams.marginStart = 64
            holder.messageContainer.layoutParams = layoutParams
            resId = R.drawable.message_sent_background
            holder.messageContainer.gravity = Gravity.END
        } else { // message received
            layoutParams.marginStart = 16
            layoutParams.marginEnd = 64
            holder.messageContainer.layoutParams = layoutParams
            holder.messageContainer.gravity = Gravity.START
            resId = R.drawable.message_received_background
        }

        holder.messageLayout.background = ContextCompat.getDrawable(context, resId)

        holder.itemView.setOnClickListener{ view ->
            popupMenu = PopupMenu(holder.itemView.context, view)

            // inflateReactionMenuAt(position) // restoring layout to set reaction

            if (message.fromId == userId) {
                popupMenu.menuInflater.inflate(R.menu.message_menu_from_user, popupMenu.menu)
            }
            else {
                popupMenu.menuInflater.inflate(R.menu.message_menu_from_other, popupMenu.menu)
            }


            popupMenu.setOnDismissListener {
                onDismiss()
            }

            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_delete -> {
                        deleteItemAt(position)
                        true
                    }
                    R.id.action_edit -> {
                        startEditMessage(position)
                        true
                    }

                    R.id.action_react -> {
                        popupMenu.dismiss()
                        inflateReactionMenuAt(position)
                        true
                    }

                    else -> false
                }
            }

            popupMenu.show()
        }
    }

    private fun deleteItemAt(position: Int) {
        messages.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, messages.size)
    }

    fun dismissMenu() {
        popupMenu.dismiss()
    }

    override fun getItemCount(): Int = messages.size

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].fromId == userId) {
            VIEW_TYPE_SENT
        } else {
            VIEW_TYPE_RECEIVED
        }
    }

    inner class MessageViewHolder(itemView: View, private val adapter: MessagesAdapter, private val updateUser : () -> Unit) : RecyclerView.ViewHolder(itemView) {
        private val messageTextView: TextView = itemView.findViewById(R.id.messageTextView)
        private val timeTextView: TextView = itemView.findViewById(R.id.timeTextView)
        val messageContainer : LinearLayout = itemView.findViewById(R.id.messageContainer)
        val messageLayout : LinearLayout = itemView.findViewById(R.id.messageLayout)
        private val attachedImageView : ImageView = itemView.findViewById(R.id.attachedImageView)
        private val fileNameTextView : TextView = itemView.findViewById(R.id.fileNameTextView)
        private val reactionImageView : ImageView = itemView.findViewById(R.id.reactionImageView)

        fun bind(message: Message, position : Int, audioPlayerView: AudioPlayerView) {
            if (message.attachment == null) {
                attachedImageView.visibility = View.GONE
            }
            else {


                attachedImageView.visibility = View.VISIBLE

                if (message.attachment!!.isImage) {
                    val imgBitmap = FileConverter.byteArrayToImage(message.attachment!!.bytes)

                    attachedImageView.setImageBitmap(imgBitmap)

                }
                else if (message.attachment!!.isAudio) {
                    attachedImageView.setImageResource(R.drawable.ic_play)
                }
                else {
                    attachedImageView.setImageResource(R.drawable.ic_file)


                }

                attachedImageView.setOnClickListener {
                    if (message.attachment!!.isAudio) {
                        audioPlayerView.setAudioFile(message.attachment!!)
                    } else {
                        FileManager.openFile(itemView.context, message.attachment!!)
                    }
                }

                fileNameTextView.text = if (message.attachment!!.name.length >= 35)
                    message.attachment!!.name.substring(0, 32) + "..."
                    else
                        message.attachment!!.name
            }

            if (message.reaction == null) {
                reactionImageView.visibility = View.GONE
            }
            else {
                val resourceId = when(message.reaction) {
                    (Reaction.LIKE) -> R.drawable.ic_like
                    (Reaction.DISLIKE) -> R.drawable.ic_dislike
                    (Reaction.LOVE) -> R.drawable.ic_heart
                    (Reaction.FIRE) -> R.drawable.ic_fire
                    (Reaction.SAD) -> R.drawable.ic_sad
                    else -> 0
                }

                // Log.d("reaction", message.reaction.toString())

                reactionImageView.visibility = View.VISIBLE
                reactionImageView.setImageResource(resourceId)

                reactionImageView.setOnClickListener {
                    message.reaction = null
                    adapter.notifyItemChanged(position)
                    updateUser()
                }
            }



            // Log.d("reaction", message.text + " " +message.reaction.toString())
            messageTextView.text = message.text
            timeTextView.text = message.time.format(DateTimeFormatter.ofPattern("HH:mm"))
        }
    }

    companion object {
        private const val VIEW_TYPE_SENT = 1
        private const val VIEW_TYPE_RECEIVED = 2
    }
}
