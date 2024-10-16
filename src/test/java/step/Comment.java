package step;

import hook.Start;
import io.cucumber.java.en.Given;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.actions.Open;

import java.io.IOException;
import java.util.List;
import java.util.Random;

public class Comment {

    @Given("Comment step by {actor}")
    public void commentStep(Actor actor) throws IOException {
        String pathExcel = "src/test/resources/data/data-comment.xlsx";
        String pathUrl = "src/test/resources/data/url.txt";

        if (Start.getProperty("listurl") != null && !Start.getProperty("listurl").isEmpty()) {
            pathUrl = Start.getProperty("listurl");
            System.out.println("Path Url: " + pathUrl);
        }

        if (Start.getProperty("commentdata") != null && !Start.getProperty("commentdata").isEmpty()) {
            pathExcel = Start.getProperty("commentdata");
            System.out.println("Path Excel: " + pathExcel);
        }

        List<String> listUrl = CommonUtils.readFileToList(pathUrl);
        int indexUrl = 1;

        for (String tempUrl : listUrl) {
            System.out.println("STT: " + indexUrl);
            // Get url
            String url = handleUrl(tempUrl);

            // Open url
            actor.attemptsTo(Task.where("{0} open url " + url), Open.url(url));
            CommonUtils.pause(3000);

            // Get product name
            String productName = CommonUtils.getText("//h1[@class='page-title']//span");
            String xpathReviewBlock = "//*[@class='block review-add']";
            if (null == CommonUtils.existElement(xpathReviewBlock)) {
                continue;
            }
            CommonUtils.scrollElementIntoMiddle(xpathReviewBlock);
            CommonUtils.waitElement(xpathReviewBlock);

            List<CommentModel> commentData = Excel.readData(pathExcel);

            /*
            * Random data in excel file
            * */
            Random random = new Random();
            int indexCommentData1 = random.nextInt(commentData.size());
            int indexCommentData2 = random.nextInt(commentData.size());

            CommentModel commentRecord1 = commentData.get(indexCommentData1);
            CommentModel commentRecord2 = commentData.get(indexCommentData2);

            // Add comment
            commentStep(productName, commentRecord1, commentRecord2);
            indexUrl++;
            // TODO: Test
//            if (indexUrl == 3) {
//                break;
//            }
        }
    }

    public String handleUrl(String tempUrl) {
        String domain = "https://healthviet.com.vn/";
        return domain + tempUrl.replace("\"", "");
    }

    public void commentStep(String productName, CommentModel commentRecord1, CommentModel commentRecord2) {
        int rating = (int) CommonUtils.randomNumberGenerator(4, 5);
        System.out.println("Rating: " + rating);
        String xpathRating = "//*[@class='rating-" + rating + "']";
        CommonUtils.clickElementByJs(xpathRating);
        CommonUtils.typeText("//*[@id='nickname_field']", commentRecord1.getName());
        CommonUtils.typeText("//*[@id='summary_field']", "Khách hàng đánh giá sản phẩm: " + productName);
        CommonUtils.typeText("//*[@id='review_field']", commentRecord2.getContent());
        CommonUtils.pause(2000);
        CommonUtils.clickElement("//*[@class='block review-add']//button[@type='submit']");
        CommonUtils.pause(3000);
    }

}
