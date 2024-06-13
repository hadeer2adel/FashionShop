package com.example.fashionshop.Model

import kotlin.random.Random

data class Review (val name: String, val rate: Float, val description: String)

class Reviews(){
    fun getReviews(): List<Review>{
        val reviews =  listOf(
            Review("Hadeer Adel", 4.2f, "The colour of the pants is a bit darker than I expected, but they fit great and I can't stress enough it has ENORMOUS POCKETS! I can fit a whole book in there! 10/10 would buy again from this brand."),
            Review("Habiba Mohamed", 3.9f, "Such a cute dress! I am so happy with this purchase"),
            Review("Ahmed Khalid", 4.7f, "Excellent Quality, Pretty Under Work Shirt!"),
            Review("Sara Ashraf", 3.2f, "Loved The Color But… The material wasn’t it for me."),
            Review("Omar Ahmed", 2.1f, "The color is a very bland beige and the arm pits are much larger cut outs that it just doesn’t fit well. It also seems like such a pain to return."),
            Review("Aya Mohamed", 4.1f, "Love this shirt! The material is so pretty and elegant."),
            Review("Samy Kamel", 1.5f, "The pictures don’t match what came in the mail."),
            Review("Hassan Mahmoud", 4.9f, "The quality of the item was good. It matched the description and met my expectations. I am pleased with this outfit!"),
            Review("Fatma Ahmed", 3f, "I love this dress in my custom fabric -- corduroy!"),
            Review("Hany Saed", 3.4f, "I bought this for a trip and it was amazing! I’m normally a medium in tops and the medium/large fit perfectly!")
        )
        val randomSize = Random.nextInt(1, reviews.size + 1)
        return reviews.shuffled(Random.Default).take(randomSize)
    }
}