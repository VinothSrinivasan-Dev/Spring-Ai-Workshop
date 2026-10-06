package dev.vinothsrini.Spring_Ai_Workshop.evals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class SentimentAnalysis {

    @Autowired(required=true)
    ReviewService reviewService;

    @Test
    void testPositiveSentiment(){
        String positivieReview="I absolutely loved the hotel, it was amazing!";
        Sentiment sentiment= reviewService.classifySentiment(positivieReview);

    assertEquals(Sentiment.POSITIVE,sentiment,"The sentiment should be classfied as positive");

    }

    @Test
    void testNegativeSentiment(){
        String negativieReview="I am going to be lost!";
        Sentiment sentiment = reviewService.classifySentiment(negativieReview);

        assertEquals(Sentiment.NEGATIVE,sentiment,"The sentiment shouble be classfied as negative");
    }

    @Test
    void testNeutralSentiment(){
        String neutralReview="The product is okay , it does what it says but nothing more!";
        Sentiment sentiment= reviewService.classifySentiment(neutralReview);

        assertEquals(Sentiment.NEUTRAL,sentiment,"The sentiment shouble be classfied as neutral");
    }
}
