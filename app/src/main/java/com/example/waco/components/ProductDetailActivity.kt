package com.example.waco.components

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.bumptech.glide.Glide
import com.example.waco.R
import com.example.waco.data.Comment
import com.example.waco.data.ProductDetailResponse
import com.example.waco.network.RetrofitInstance
import com.google.android.material.appbar.MaterialToolbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProductDetailActivity : AppCompatActivity() {

    private lateinit var productImage: ImageView
    private lateinit var productDescription: TextView
    private lateinit var commentsContainer: LinearLayout
    private lateinit var commentEditText: EditText
    private lateinit var addCommentButton: Button
    private lateinit var ratingBarInput: RatingBar
    private lateinit var averageRatingBar: RatingBar
    private lateinit var toolbar: MaterialToolbar

    private lateinit var productCode: String
    private var username: String = "Gość"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)

        toolbar = findViewById(R.id.topAppBar)
        productImage = findViewById(R.id.productImage)
        productDescription = findViewById(R.id.productDescription)
        commentsContainer = findViewById(R.id.commentsContainer)
        commentEditText = findViewById(R.id.commentEditText)
        addCommentButton = findViewById(R.id.addCommentButton)
        ratingBarInput = findViewById(R.id.ratingBarInput)
        averageRatingBar = findViewById(R.id.averageRatingBar)

        productCode = intent.getStringExtra("PRODUCT_CODE") ?: ""

        val prefs = getSharedPreferences("user_data", Context.MODE_PRIVATE)
        username = prefs.getString("username", null)
            ?: getSharedPreferences("user_admin", Context.MODE_PRIVATE)
                .getString("username", "Gość") ?: "Gość"

        toolbar.title = "Szczegóły - $productCode"
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        fetchProductDetails(productCode)

        addCommentButton.setOnClickListener {
            val commentText = commentEditText.text.toString().trim()
            val rating = ratingBarInput.rating.toInt()

            if (commentText.isNotBlank() && rating > 0) {
                postCommentToApi(productCode, commentText, rating, username)
                commentEditText.text.clear()
                ratingBarInput.rating = 0f
            } else {
                Toast.makeText(this, "Wpisz komentarz i ocenę", Toast.LENGTH_SHORT).show()
            }
        }
    }
    fun Context.dpToPx(dp: Int): Int =
        (dp * resources.displayMetrics.density).toInt()

    private fun fetchProductDetails(code: String) {
        RetrofitInstance.api.getProductDetails(code).enqueue(object : Callback<ProductDetailResponse> {
            override fun onResponse(call: Call<ProductDetailResponse>, response: Response<ProductDetailResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let { product ->
                        productDescription.text = product.description

                        Glide.with(this@ProductDetailActivity)
                            .load(product.imageUrl)
                            .placeholder(R.drawable.placeholder)
                            .into(productImage)

                        val average: Float = if (product.comments.isNotEmpty()) {
                            product.comments.map { it.rating }.average().toFloat()
                        } else {
                            0f
                        }

                        averageRatingBar.rating = average
                        showComments(product.comments)
                    }
                } else {
                    Toast.makeText(this@ProductDetailActivity, "Błąd ładowania danych", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ProductDetailResponse>, t: Throwable) {
                Toast.makeText(this@ProductDetailActivity, "Błąd: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun postCommentToApi(code: String, content: String, rating: Int, username: String) {
        RetrofitInstance.api.postComment(code, content, rating, username)
            .enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@ProductDetailActivity, "Dodano komentarz", Toast.LENGTH_SHORT).show()
                        fetchProductDetails(code)
                    } else {
                        Toast.makeText(this@ProductDetailActivity, "Nie udało się dodać komentarza", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(this@ProductDetailActivity, "Błąd: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })

    }

    @SuppressLint("SetTextI18n")
    private fun showComments(comments: List<Comment>) {
        commentsContainer.removeAllViews()

        comments.forEach { comment ->
            val card = CardView(this).apply {
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

            val layout = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
            }

            val commentText = TextView(this).apply {
                text = "${comment.username} (${comment.created_at}):\n${comment.content}"
                textSize = 14f
                setTextColor(Color.DKGRAY)
            }

            val stars = RatingBar(this, null, android.R.attr.ratingBarStyleSmall).apply {
                numStars = 5
                rating = comment.rating.toFloat()
                stepSize = 1f

                layoutParams = LinearLayout.LayoutParams(
                    context.dpToPx(80), // ustalamy szerokość na 5 gwiazdek (~24dp każda)
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }

            fun Context.dpToPx(dp: Int): Int = (dp * resources.displayMetrics.density).toInt()


            layout.addView(commentText)
            layout.addView(stars)
            card.addView(layout)
            commentsContainer.addView(card)
        }
    }
}
