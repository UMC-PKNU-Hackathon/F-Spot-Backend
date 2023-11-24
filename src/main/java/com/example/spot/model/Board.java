package com.example.spot.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "board")
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "boardId", nullable = false)
    private Long boardId;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    private Integer hits; // 조회수

    @ManyToOne(fetch = FetchType.LAZY)
    private User user; // 작성자

    /**
     *  BOARD @OneToMany 1:N
     *  LIKE - 좋아요
     */
    @OneToMany(mappedBy = "board", orphanRemoval = true)
    private List<BoardLike> boardLikes;       // 좋아요
    private Integer likeCnt;        // 좋아요 수

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    @Column(name = "created_at")
    private Timestamp createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<BoardImage> boardImage = new ArrayList<>();

    @Transient
    private List<String> boardImageUrl;

    @Column(name = "latitude")
    private Double latitude;
    @Column(name = "longitude")
    private Double longitude;

    // BoardImage 객체들의 URL만을 가져와 boardImageUrl 필드를 채우는 메서드
    @PostLoad
    public void fillBoardImageUrl() {
        this.boardImageUrl = new ArrayList<>();
        for (BoardImage image : this.boardImage) {
            this.boardImageUrl.add(image.getUrl());
        }
    }


    @ElementCollection
    @CollectionTable(name = "hashtags", joinColumns = @JoinColumn(name = "boardId"))
    @Column(name = "tags")
    private List<String> tags;  //해시태그


    public void likeChange(Integer likeCnt) {
        this.likeCnt = likeCnt;
    }

}
