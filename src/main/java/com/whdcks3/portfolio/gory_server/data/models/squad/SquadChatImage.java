package com.whdcks3.portfolio.gory_server.data.models.squad;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;

import org.springframework.format.annotation.DateTimeFormat;

import com.whdcks3.portfolio.gory_server.exception.UnSupportedImageFormatException;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Data
public class SquadChatImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String uniqueName;

    @Column(nullable = true)
    private String originName;

    @Column(nullable = true)
    private String url;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feed_pid", nullable = false)
    private SquadChat squadChat;

    @DateTimeFormat(pattern = "yyyy-mm-dd")
    private LocalDate createDate;

    @PrePersist
    public void createDate() {
        this.createDate = LocalDate.now();
    }

    public SquadChatImage(String originName) {
        this.originName = originName;
        this.uniqueName = generateUniqueName(extractExtension(originName));
    }

    public SquadChatImage(String filename, String url) {
        this.uniqueName = filename;
        this.url = url;
    }

    public void initSquadChat(SquadChat squadChat) {
        if (this.squadChat == null) {
            this.squadChat = squadChat;
        }
    }

    private String extractExtension(String name) {
        if (!name.contains("."))
            throw new UnSupportedImageFormatException();
        String ext = name.substring(name.lastIndexOf('.') + 1);
        if (isSupportedFormat(ext))
            return ext;
        throw new UnSupportedImageFormatException();
    }

    private boolean isSupportedFormat(String ext) {
        String[] supported = { "jpg", "jpeg", "gif", "bmp", "png" };
        return Arrays.stream(supported).anyMatch(e -> e.equalsIgnoreCase(ext));
    }

    private String generateUniqueName(String extension) {
        return UUID.randomUUID().toString() + "." + extension;
    }
}
