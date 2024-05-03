public class Main {
    public static void main(String[] args) {
        try {
            ImagesDownloader downloader = new ImagesDownloader();
            downloader.downloadImagesFromUrl("https://skillbox.ru/");
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
