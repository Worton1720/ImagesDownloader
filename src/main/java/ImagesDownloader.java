import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.*;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImagesDownloader {
    private int number = 1;

    public void downloadImagesFromUrl(String url) {
        try {
            Document doc = Jsoup.connect(url).get();
            Set<String> imageLinks = getImageLinks(doc);

            ExecutorService executor = Executors.newFixedThreadPool(5); // Создаем пул потоков

            for (String link : imageLinks) {
                String extension = link.replaceAll("^.+\\.", "").replace("?\\..+$", "");
                String filePath = "images/" + number++ + "." + extension;

                // Передаем задачу в пул потоков для выполнения
                executor.execute(new ImageDownloadTask(link, filePath));
            }

            executor.shutdown(); // Остановка пула потоков после завершения всех задач
        } catch (IOException e) {
            System.err.println("Error connecting to the URL: " + e.getMessage());
        }
    }

    private Set<String> getImageLinks(Document doc) {
        Set<String> links = new HashSet<>();
        Elements images = doc.select("img");
        for (Element image : images) {
            links.add(image.attr("abs:src"));
        }
        return links;
    }

    private static class ImageDownloadTask implements Runnable {
        private final String link;
        private final String filePath;

        public ImageDownloadTask(String link, String filePath) {
            this.link = link;
            this.filePath = filePath;
        }

        @Override
        public void run() {
            try {
                downloadImage(link, filePath);
            } catch (IOException e) {
                System.err.println("Error downloading " + link + ": " + e.getMessage());
            }
        }

        private void downloadImage(String link, String filePath) throws IOException {
            // Создание папки images, если она не существует
            File directory = new File("images");
            if (!directory.exists()) {
                directory.mkdir();
            }

            URL url = new URL(link);
            try (InputStream inStream = url.openStream();
                 OutputStream outStream = new FileOutputStream(filePath)) {

                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = inStream.read(buffer, 0, 8192)) != -1) {
                    outStream.write(buffer, 0, bytesRead);
                }

                System.out.println("Downloaded: " + filePath);
            }
        }
    }
}
