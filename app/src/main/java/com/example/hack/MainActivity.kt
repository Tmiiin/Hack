package com.example.hack

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    companion object {
        private const val MAX_ELEMENTS = 4
        private const val EXTRA_COUNT = "extra.count"
    }

    private val gameScene by lazy { findViewById<LinearLayout>(R.id.game_scene) }
    private val rootBackground by lazy { findViewById<View>(R.id.root_background) }
    private val mainCharacter by lazy { findViewById<ImageView>(R.id.main_character) }
    private val answerButton by lazy { findViewById<View>(R.id.answer_button) }

    private val person = Person.values()[Random.nextInt(0, Person.values().size)]
    private val item = Items.values()[Random.nextInt(0, Items.values().size)]

    private var selectedAnswer = 0
    private var itemsCount = 0

    private val listOfButtons by lazy {
        mutableListOf<TextView>().apply {
            add(findViewById<View>(R.id.first_answer_button).setOnClickListenerToChildTextView())
            add(findViewById<View>(R.id.second_answer_button).setOnClickListenerToChildTextView())
            add(findViewById<View>(R.id.third_answer_button).setOnClickListenerToChildTextView())
            add(findViewById<View>(R.id.fourth_answer_button).setOnClickListenerToChildTextView())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setDescriptionText()
        rootBackground.background = ContextCompat.getDrawable(this, item.backgroundId)
        mainCharacter.setImageResource(person.resId)

        generateItemsCount(intent.getIntExtra(EXTRA_COUNT, 0))
        setInterface(itemsCount)
        gameScene.apply {
            weightSum = itemsCount.toFloat()
        }
        for (i in 0..itemsCount) {
            val item = ImageView(this).apply {
                layoutParams = LinearLayout.LayoutParams(0, MATCH_PARENT, 1f).apply {
                    gravity = Gravity.BOTTOM
                }
                setImageResource(item.resId)
            }
            gameScene.addView(item)
        }
        answerButton.setOnClickListener {
            if (selectedAnswer == 0) {
                Toast.makeText(this, "Ты ничего не выбрал :с", LENGTH_SHORT).show()
            } else {
                findViewById<View>(R.id.interface_layout).visibility = GONE
                val congratsLayout =
                    findViewById<View>(R.id.congrats_layout).apply { visibility = VISIBLE }
                if (selectedAnswer == itemsCount) {
                    congratsLayout.findViewById<View>(R.id.wrong_answer).visibility = GONE
                } else {
                    congratsLayout.findViewById<CardView>(R.id.try_again_button)
                        .setCardBackgroundColor(ContextCompat.getColor(this, R.color.wrong_color))
                    congratsLayout.findViewById<View>(R.id.right_answer).visibility = GONE
                }
            }
        }

        findViewById<View>(R.id.try_again_button).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            if (selectedAnswer == itemsCount)
                intent.putExtra(EXTRA_COUNT, selectedAnswer)
            startActivity(intent)
            overridePendingTransition(0, 0)
        }
    }

    private fun generateItemsCount(prevNumber: Int) {
        itemsCount = Random.nextInt(1, if (item == Items.CACTUS) 3 else MAX_ELEMENTS)
        if (itemsCount == prevNumber)
            generateItemsCount(prevNumber)
    }

    private fun setDescriptionText() {
        val descriptionView = findViewById<TextView>(R.id.description)
        val splitted = descriptionView.text.split("%d")
        val text = splitted[0] + person.personName + splitted[1] + item.itemName + splitted[2]
        descriptionView.text = text
    }

    private fun onAnswerSelected(view: TextView) {
        val root = view.parent as CardView
        if (selectedAnswer == 0) {
            this.selectedAnswer = view.text.toString().toInt()
            root.setCardBackgroundColor(ContextCompat.getColor(this, R.color.basic_color))
        } else {
            for (i in listOfButtons) {
                val localRoot = i.parent as CardView
                localRoot.setCardBackgroundColor(
                    ContextCompat.getColor(
                        this,
                        R.color.basic_color_transparent
                    )
                )
            }
            if (selectedAnswer != view.text.toString().toInt()) {
                this.selectedAnswer = view.text.toString().toInt()
                root.setCardBackgroundColor(ContextCompat.getColor(this, R.color.basic_color))
            }
        }
    }

    private fun setInterface(itemsCount: Int) {
        val rightAnswerButtonPosition = Random.nextInt(0, listOfButtons.size)
        val randomNums = generateRandomNumbers()
        listOfButtons[rightAnswerButtonPosition].text = itemsCount.toString()
        var position = 0
        for (i in randomNums) {
            if (position != rightAnswerButtonPosition && i != itemsCount) {
                listOfButtons[position].text = i.toString()
            }
            if (position != rightAnswerButtonPosition && i == itemsCount) {
                continue
            }
            position++
            if (position == 4) break
        }
    }

    private fun generateRandomNumbers(): Set<Int> {
        val set = mutableSetOf<Int>()
        while (set.size <= 4) {
            set.add(Random.nextInt(6) + 1);
        }
        return set
    }

    private fun View.setOnClickListenerToChildTextView(): TextView =
        this.findViewById<TextView>(R.id.answer_button_text)
            .also { textView ->
                textView.setOnClickListener {
                    onAnswerSelected(textView)
                }
            }


    enum class Person(var personName: String, var resId: Int) {
        NUYSHA("Нюше", R.drawable.person_3),
        LOSYASH("Лосяшу", R.drawable.person_1),
        KARYCH("Карычу", R.drawable.person_2)
    }

    enum class Items(
        var itemName: String,
        var resId: Int,
        var backgroundId: Int
    ) {
        CACTUS("кактусов", R.drawable.item_1, R.drawable.sand_background),
        FIR("ёлочек", R.drawable.item_2, R.drawable.snow_background),
    }
}