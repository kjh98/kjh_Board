package codinghub.assignment.study.board.model;

import codinghub.assignment.study.board.entity.BoardRelated;
import codinghub.assignment.study.board.entity.BoardRelatedRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class BoardRelatedService {
    private final BoardRelatedRepository boardRelatedRepository;

    /**
     * 게시글 생성
     */
    @Transactional
    public Long save(BoardRelated request) throws IOException {
        BoardRelated entity = boardRelatedRepository.save(request);
        return entity.getIndex();
    }

}
