package comments;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.time.LocalDate;

public class Comment {
    private String author;
    private String content;
    private LocalDate createdAt;

    public Comment(final String author, final String content, final LocalDate createdAt) {
        this.author = author;
        this.content = content;
        this.createdAt = createdAt;
    }

    /**
     *
     * @return author
     */
    public String getAuthor() {
        return author;
    }

    /**
     *
     * @param authorC
     */
    public void setAuthor(final String authorC) {
        this.author = authorC;
    }

    /**
     *
     * @return content
     */
    public String getContent() {
        return content;
    }

    /**
     *
     * @param contentC
     */
    public void setContent(final String contentC) {
        this.content = contentC;
    }

    /**
     *
     * @return created at
     */
    public LocalDate getCreatedAt() {
        return createdAt;
    }

    /**
     *
     * @param createdAtC
     */
    public void setCreatedAt(final LocalDate createdAtC) {
        this.createdAt = createdAtC;
    }

    /**
     *
     * @param mapper
     * @return output node
     */
    public ObjectNode toJson(final ObjectMapper mapper) {
        ObjectNode node = mapper.createObjectNode();
        node.put("author", author);
        node.put("content", content);
        node.put("createdAt", createdAt.toString());
        return node;
    }
}
