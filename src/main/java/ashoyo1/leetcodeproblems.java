package ashoyo1;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;

public class leetcodeproblems {

    //private static final int  max = 100; 

    public static void main(String[] args) throws IOException {

        // List<String[]> problems = new ArrayList<>();
        // int count = 0;
        // int page = 1;  // track pages and use to make to next pg

        // while (count < max) {

        //     Document doc = Jsoup.connect("https://leetcode.com/problemset/all/?page=" + page).get();
        //     Elements rows = doc.select(".reactable-data tr");

        //     for (Element row : rows) {
        //         if (count >= max)
        //          break;

        //         Elements cols = row.select("td");
                
        //         if (cols.size() > 0) {
        //             String title = cols.get(2).text();
        //             String difficulty = cols.get(5).text();
        //             String url = "https://leetcode.com" + cols.get(2).select("a").attr("href");

        //             problems.add(new String[]{title, difficulty, url});
        //             count++;
        //         }
        //     }
        //     page++; // Move to the next page
        // }

  
        // for (String[] problem : problems) {
        //     System.out.println("Title: " + problem[0] + ", Difficulty: " + problem[1] + ", URL: " + problem[2]);
        // }

        Document doc = Jsoup.connect("https://leetcode.com/problemset/all/?page=1").get();
        Elements rows = doc.select(".reactable-data tr");

        for (Element row : rows) {
            Elements cols = row.select("td");
            if (cols.size() > 0) {
                // Print out the HTML of each row to debug
                System.out.println(row.html());
            }




        }
    }
}
