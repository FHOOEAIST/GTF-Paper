package science.aist.gtf.paperexample;

import java.io.IOException;

/**
 * <p>Main Class to start the Gradle Crawler</p>
 *
 * @author Andreas Schuler
 * @author Christoph Praschl
 * @author Andreas Pointner
 * @since 1.0
 */
public class GradleCrawlerMain {
    public static void main(String[] args) throws IOException {
        var crawler = new GradleCrawler();
        crawler.execute();
    }
}
