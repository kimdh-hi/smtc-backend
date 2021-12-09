package com.sparta.showmethecode.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
public class Notification extends Timestamped {

    @Id
    @GeneratedValue
    private Long id;

    @JoinColumn(name = "fk_notification_to_receiver")
    @ManyToOne(fetch = FetchType.LAZY)
    private User receiver;

    @JsonIgnore
    @JoinColumn(name = "fk_notification_to_review")
    @ManyToOne(fetch = FetchType.LAZY)
    private ReviewRequest review;

    private String content;

    private String url;

    private boolean isRead;


    private String uri;

    @Builder
    public Notification(User receiver, ReviewRequest review, String content, String url, boolean isRead, String uri) {
        this.receiver = receiver;
        this.review = review;
        this.content = content;
        this.url = url;
        this.isRead = isRead;
        this.uri = uri;
    }

    public void read(){
        this.isRead = true;
    }

}
