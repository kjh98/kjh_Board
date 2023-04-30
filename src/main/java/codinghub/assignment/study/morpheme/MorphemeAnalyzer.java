package codinghub.assignment.study.morpheme;

import codinghub.assignment.study.board.entity.Board;
import codinghub.assignment.study.board.entity.BoardRelated;
import codinghub.assignment.study.board.entity.BoardRepository;
import codinghub.assignment.study.board.model.BoardRelatedService;
import lombok.RequiredArgsConstructor;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class MorphemeAnalyzer {

    private final BoardRepository boardRepository;
    private final BoardRelatedService boardRelatedService;

    public void MorphemeAnalyzers(Board params) throws IOException {
        double WORD_FREQUENCY_THRESHOLD = 0.4;

        // 토큰화
        TokenizerModel tokenizerModel = new TokenizerModel(MorphemeAnalyzer.class.getResourceAsStream("/en-token.bin"));
        Tokenizer tokenizer = new TokenizerME(tokenizerModel);

        // 형태소 분석
        POSModel posModel = new POSModel(MorphemeAnalyzer.class.getResourceAsStream("/en-pos-maxent.bin"));
        POSTaggerME posTagger = new POSTaggerME(posModel);

        // 문장 감지
        SentenceModel sentenceModel = new SentenceModel(MorphemeAnalyzer.class.getResourceAsStream("/en-sent.bin"));
        SentenceDetectorME sentenceDetector = new SentenceDetectorME(sentenceModel);
        int size = boardRepository.findAll().size();

        for (int i=0; i < size; i++) {
            String textA = params.getContent();
            String textB = boardRepository.findAll().get(i).getContent();
            Long getId = boardRepository.findAll().get(i).getId();
            // 문장 A와 B에서 공통된 단어 추출
            List<String> tokensA = getTokens(textA, tokenizer, posTagger, sentenceDetector);
            List<String> tokensB = getTokens(textB, tokenizer, posTagger, sentenceDetector);

            Set<String> commonWords = getCommonWords(tokensA, tokensB);

            // 공통된 단어의 빈도 계산
            int count = 0;
            double frequencyResult = 0;
            boolean save = false;
            for (String word : commonWords) {
                double frequencyB = getWordFrequency(tokensB, word);
                if (frequencyB <= WORD_FREQUENCY_THRESHOLD && params.getId() != getId) {
                    frequencyResult += frequencyB;
                    count++;
                    save = true;
                }
            }
            if(save){
                BoardRelated request = BoardRelated.builder().
                        boardId(params.getId()).
                        relatedboardId(getId).
                        wordscount(count).
                        frequency(frequencyResult).
                        deleteYn('N').
                        build();
                boardRelatedService.save(request);
            }

        }
    }

    private List<String> getTokens(String text, Tokenizer tokenizer, POSTaggerME posTagger, SentenceDetectorME sentenceDetector) {

        // 문장 단위 분리
        String[] sentences = sentenceDetector.sentDetect(text);

        List<String> tokens = new ArrayList<>();

        // 각 문장마다 토큰화 및 품사 태깅
        for (String sentence : sentences) {

            String[] words = tokenizer.tokenize(sentence);

            String[] posTags = posTagger.tag(words);

            // 명사만 추출하여 리스트에 추가
            for (int i = 0; i < words.length; i++) {
                if (posTags[i].startsWith("N")) { // N으로 시작하는 품사 태그는 명사를 의미함
                    tokens.add(words[i]);
                }
            }
        }

        return tokens;
    }

    private Set<String> getCommonWords(List<String> tokensA, List<String> tokensB) {
        Set<String> setA = new HashSet<>(tokensA);
        Set<String> setB = new HashSet<>(tokensB);
        setA.retainAll(setB);
        return setA;
    }
    private double getWordFrequency(List<String> tokens, String word) {
        long count = tokens.stream().filter(token -> token.equalsIgnoreCase(word)).count();
        return (double) count / tokens.size();
    }
}

