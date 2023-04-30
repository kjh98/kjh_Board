package codinghub.assignment.study.board.model;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import codinghub.assignment.study.board.dto.BoardRequestDto;
import codinghub.assignment.study.board.dto.BoardResponseDto;
import codinghub.assignment.study.board.entity.Board;
import codinghub.assignment.study.board.entity.BoardRelated;
import codinghub.assignment.study.board.entity.BoardRelatedRepository;
import codinghub.assignment.study.board.entity.BoardRepository;
import codinghub.assignment.study.board.exception.CustomException;
import codinghub.assignment.study.board.exception.ErrorCode;
import codinghub.assignment.study.morpheme.MorphemeAnalyzer;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final BoardRelatedRepository boardRelatedRepository;
    private final MorphemeAnalyzer morphemeAnalyzers;

    /**
     * 게시글 생성
     */
    @Transactional
    public Long save(final BoardRequestDto params) throws IOException {
        Board entity = boardRepository.save(params.toEntity());
        morphemeAnalyzers.MorphemeAnalyzers(entity);
        return entity.getId();
    }

    /**
     * 게시글 리스트 조회
     */
    public List<BoardResponseDto> findAll() {
        Sort sort = Sort.by(Direction.DESC, "id", "createdDate");
        List<Board> list = boardRepository.findAll(sort);
        return list.stream().map(BoardResponseDto::new).collect(Collectors.toList());
    }

    /**
     * 게시글 수정
     */
    @Transactional
    public Long update(final Long id, final BoardRequestDto params) {

        Board entity = boardRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.POSTS_NOT_FOUND));
        entity.update(params.getTitle(), params.getContent(), params.getWriter());
        return id;
    }
    /**
     * 게시글 상세정보 조회
     */
    @Transactional
    public BoardResponseDto findById(final Long id) {
        Board entity = boardRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.POSTS_NOT_FOUND));
        entity.increaseHits();
        BoardResponseDto responseDto = new BoardResponseDto(entity);
        List<Board> list = findRelatedById(id);
        responseDto.BoardResponseList(list);
        return responseDto;
    }
    @Transactional
    public Long delete(final Long id) {
        Board entity = boardRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.POSTS_NOT_FOUND));
        entity.delete();
        return id;
    }
    /**
     * 연관 게시글 상세정보 조회
     */
    public List<Board> findRelatedById(final Long id) {
        List<Board> list = boardRepository.FindRelatedPosts(id);
        return list;
    }


}
