package codinghub.assignment.study.board.entity;

import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long> {
    @Query(value = "SELECT b from Board b left join BoardRelated br on b.id = br.relatedboardId where br.boardId = :Id order by br.frequency desc")
    List<Board> FindRelatedPosts(@Param("Id") Long Id);
}
