package zafer.example.com.hurriyetapp;

/**
 * Created by Murat on 1.10.2016.
 */

public class Article {
    private String Title;
    private String Url;
    private String Description;
    private String FileUrl;

    public Article(String title, String description, String url, String fileUrl){
        this.Title=title;
        this.Description=description;
        this.Url=url;
        this.FileUrl=fileUrl;
    }
    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getUrl() {
        return Url;
    }

    public void setUrl(String url) {
        Url = url;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getFileUrl() {
        return FileUrl;
    }

    public void setFileUrl(String fileUrl) {
        FileUrl = fileUrl;
    }
}
