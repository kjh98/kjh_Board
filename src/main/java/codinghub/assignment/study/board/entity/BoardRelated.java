package codinghub.assignment.study.board.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class BoardRelated {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long index; // PK

    private Long boardId; // 게시글 ID

    private Long relatedboardId; // 게시글과 관련된 게시글 ID

    private int wordscount; // 공통 단어의 갯수

    private Double frequency; // 공통 단어의 빈도수

    private char deleteYn;

    @Builder
    public BoardRelated(Long index, Long boardId, Long relatedboardId, int wordscount, Double frequency, char deleteYn) {
        this.index = index;
        this.boardId = boardId;
        this.relatedboardId = relatedboardId;
        this.wordscount = wordscount;
        this.frequency = frequency;
        this.deleteYn = deleteYn;
    }

    public void delete(){
        this.deleteYn = 'Y';
    }
}
