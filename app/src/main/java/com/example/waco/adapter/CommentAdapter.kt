package com.example.waco.adapter

import android.content.Context
import android.graphics.Color
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.waco.R
import com.example.waco.data.Comment

class CommentAdapter(
    private val context: Context,
    private val comments: List<Comment>
) : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    class CommentViewHolder(val card: CardView) : RecyclerView.ViewHolder(card)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val card = CardView(context).apply {
            layoutParams = ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 16
            }
            radius = 12f
            setContentPadding(24, 16, 24, 16)
            cardElevation = 4f
        }

        return CommentViewHolder(card)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = comments[position]
        val card = holder.card
        card.removeAllViews() // bardzo ważne!

        val container = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        val metaText = TextView(context).apply {
            text = "${comment.username} • ${comment.created_at}"
            textSize = 12f
            setTextColor(Color.GRAY)
        }

        val commentText = TextView(context).apply {
            text = comment.content
            setBackgroundResource(R.drawable.comment_bubble_background)
            setPadding(16, 12, 16, 12)
            setTextColor(Color.DKGRAY)
            textSize = 14f
        }

        // 🛠️ Ustawienia RatingBar – ściśle 5 gwiazdek, tylko do odczytu
        val stars = RatingBar(context, null, android.R.attr.ratingBarStyleSmall).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            numStars = 5
            max = 5
            rating = comment.rating.toFloat().coerceIn(0f, 5f)
            stepSize = 1f
        }

        container.addView(metaText)
        container.addView(commentText)
        container.addView(stars)
        card.addView(container)
    }

    override fun getItemCount() = comments.size
}
