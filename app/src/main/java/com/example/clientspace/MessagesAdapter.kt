package com.example.clientspace

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

import com.example.clientspace.ui.Message
import java.time.format.DateTimeFormatter

class MessagesAdapter(private val messages: List<Message>, private val currentUserId: String,
    private val audioPlayerView: AudioPlayerView) :
    RecyclerView.Adapter<MessagesAdapter.MessageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val layout = R.layout.item_message
        val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages[position]
        holder.bind(message, audioPlayerView)

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
    }

    override fun getItemCount(): Int = messages.size

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].fromId == currentUserId) {
            VIEW_TYPE_SENT
        } else {
            VIEW_TYPE_RECEIVED
        }
    }

    inner class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val messageTextView: TextView = itemView.findViewById(R.id.messageTextView)
        private val timeTextView: TextView = itemView.findViewById(R.id.timeTextView)
        val messageContainer : LinearLayout = itemView.findViewById(R.id.messageContainer)
        val messageLayout : LinearLayout = itemView.findViewById(R.id.messageLayout)
        private val attachedImageView : ImageView = itemView.findViewById(R.id.attachedImageView)

        fun bind(message: Message, audioPlayerView: AudioPlayerView) {
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
            }

            messageTextView.text = message.text
            timeTextView.text = message.time.format(DateTimeFormatter.ofPattern("HH:mm"))
        }
    }

    companion object {
        private const val VIEW_TYPE_SENT = 1
        private const val VIEW_TYPE_RECEIVED = 2
    }
}
