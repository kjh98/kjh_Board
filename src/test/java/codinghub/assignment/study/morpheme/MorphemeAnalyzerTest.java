package codinghub.assignment.study.morpheme;

import java.io.IOException;
import java.io.InputStream;

import codinghub.assignment.study.board.entity.Board;
import codinghub.assignment.study.board.entity.BoardRepository;
import codinghub.assignment.study.board.model.BoardService;
import lombok.RequiredArgsConstructor;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.InputStream;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@SpringBootTest
public class MorphemeAnalyzerTest {
    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private BoardService boardService;
    @Test
    void MorphemeAnalyzerTester() throws IOException {
        // Load POS model
        InputStream posModelIn = MorphemeAnalyzer.class.getResourceAsStream("/en-pos-maxent.bin");
        POSModel posModel = new POSModel(posModelIn);
        POSTaggerME posTagger = new POSTaggerME(posModel);

        // Load tokenizer model
        InputStream tokenModelIn = MorphemeAnalyzer.class.getResourceAsStream("/en-token.bin");
        TokenizerModel tokenModel = new TokenizerModel(tokenModelIn);
        Tokenizer tokenizer = new TokenizerME(tokenModel);

        Pattern pattern = Pattern.compile("[^가-힣a-zA-Z0-9]+");

        // Text to be analyzed
        String text = "John saw the car and smiled.";
        // Tokenize the text
        String[] tokens = tokenizer.tokenize(text);
        // Perform POS tagging
        String[] tags = posTagger.tag(tokens);

        // Print the tokens and their corresponding POS tags
        for (int i = 0; i < tokens.length; i++) {
            System.out.println(tokens[i] + " : " + tags[i]);
        }
        
        String tester = boardRepository.findAll().get(0).getContent();
        System.out.println("tester = " + tester);

        // A와 B의 본문을 입력
        String textA = "안녕하세요, 자연어 처리에 대한 공부를 하고 있습니다.";
        String textB = "안녕하세요, 자연어 자연어 자연어가 안녕하신가요. 자연어가 자연어 하였다.";

//        String textA = "안녕하세요, 자연어 자연어 자연어가 안녕하신가요. 자연어가 자연어 하였다.";
//        String textB = tester;


        String[] tokensA = tokenizer.tokenize(textA);
        String[] tokensB = tokenizer.tokenize(textB);

        String[] tagsA = posTagger.tag(tokensA);
        String[] tagsB = posTagger.tag(tokensB);
        // A와 B의 단어 집합에서 중복된 단어의 개수 계산
        Set<String> setA = new HashSet<String>(Arrays.asList(tokensA));
        Set<String> setB = new HashSet<String>(Arrays.asList(tokensB));
        Set<String> intersection = new HashSet<String>(setA);
        intersection.retainAll(setB); // 교집합
        Set<String> union = new HashSet<String>(setA);
        union.addAll(setB); // 합집합
        // 같은 단어의 비율 계산
        double sameWordRatio = (double) intersection.size() / (double) union.size() * 100.0;

        List wordsAList = new ArrayList();
        List wordsBList = new ArrayList();

        System.out.println("A와 B의 본문에서 같은 단어의 비율: " + sameWordRatio + "%");
        for (int i = 0; i < tokensA.length; i++) {
            System.out.println(tokensA[i] + " : " + tagsA[i]);
            wordsAList.add(tokensA[i]);
        }
        for (int i = 0; i < tokensB.length; i++) {
            System.out.println(tokensB[i] + " : " + tagsB[i]);
            wordsBList.add(tokensB[i]);
        }
        // 형태소 분석을 위한 라이브러리를 사용하여 각각의 형태소 추출
//        Set<String> wordsA = new HashSet<>(Arrays.asList("한국어", "자연어", "처리", "어렵다", "작업"));
//        Set<String> wordsB = new HashSet<>(Arrays.asList("자연어", "처리", "어렵다", "작업", "한국어"));
        Set<String> wordsA = new HashSet<>(wordsAList);
        Set<String> wordsB = new HashSet<>(wordsBList);

        // A와 B의 공통된 단어 찾기
        Set<String> commonWords = new HashSet<>(wordsA);
        commonWords.retainAll(wordsB);
        // A와 B의 공통된 단어 찾기
//        Set<String> commonWords = new HashSet<>(wordsA.stream().map(String::toLowerCase).collect(Collectors.toList()));
//        commonWords.retainAll(wordsB.stream().map(String::toLowerCase).collect(Collectors.toList()));
        // 공통된 단어 출력
//        System.out.println("A와 B의 공통된 단어: " + commonWords.stream().collect(Collectors.joining(", ")));
//        System.out.println("A와 B의 공통된 단어 개수: " + commonWords.size());
        int count = 0;
        for (String word : commonWords) {
            String stripped = pattern.matcher(word).replaceAll("");
            if (!stripped.isEmpty()) {
                count++;
            }
        }
        System.out.println("A와 B의 공통된 단어 개수: " + count);

        // 공통된 단어 출력
        System.out.println("A와 B의 공통된 단어: " + commonWords.stream()
                .map(word -> pattern.matcher(word).replaceAll("")) // 문자와 기호 제거
                .filter(word -> !word.isEmpty()) // 빈 문자열 제거
                .collect(Collectors.joining(", ")));
//        System.out.println("A와 B의 공통된 단어 개수: " + commonWords.size());

    }


    @Test
    public void TestTwo() throws Exception {
        double WORD_FREQUENCY_THRESHOLD = 0.4;
        int size = boardRepository.findAll().size();
        for (int i=0; i < size; i++) {
            String textA = "자연어";
            String textB = boardRepository.findAll().get(i).getContent();
//            String textB = "안녕하세요, 자연어 처리에 대한 공부를 하고 있습니다.";

            // 토큰화
            TokenizerModel tokenizerModel = new TokenizerModel(MorphemeAnalyzer.class.getResourceAsStream("/en-token.bin"));
            Tokenizer tokenizer = new TokenizerME(tokenizerModel);

            // 형태소 분석
            POSModel posModel = new POSModel(MorphemeAnalyzer.class.getResourceAsStream("/en-pos-maxent.bin"));
            POSTaggerME posTagger = new POSTaggerME(posModel);

            // 문장 감지
            SentenceModel sentenceModel = new SentenceModel(MorphemeAnalyzer.class.getResourceAsStream("/en-sent.bin"));
            SentenceDetectorME sentenceDetector = new SentenceDetectorME(sentenceModel);

            // 문장 A와 B에서 공통된 단어 추출
            List<String> tokensA = getTokens(textA, tokenizer, posTagger, sentenceDetector);
            List<String> tokensB = getTokens(textB, tokenizer, posTagger, sentenceDetector);

            Set<String> commonWords = getCommonWords(tokensA, tokensB);

            // 공통된 단어의 빈도 계산
            int count = 0;
            double frequencyResult = 0;
            for (String word : commonWords) {
//                double frequencyA = getWordFrequency(tokensA, word);
                double frequencyB = getWordFrequency(tokensB, word);
                if (frequencyB <= WORD_FREQUENCY_THRESHOLD) {
                    count++;
                }
                frequencyResult += frequencyB;
            }

            // 결과 출력
            System.out.println("A와 B의 공통된 단어 개수: " + commonWords.size());
            System.out.println("빈도가 40% 이하인 공통된 단어 개수: " + count);
            System.out.println("빈도수: " + frequencyResult);
        }
    }
    public static List<String> getTokens(String text, Tokenizer tokenizer, POSTaggerME posTagger, SentenceDetectorME sentenceDetector) {
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
    private static Set<String> getCommonWords(List<String> tokensA, List<String> tokensB) {
        Set<String> setA = new HashSet<>(tokensA);
        Set<String> setB = new HashSet<>(tokensB);
        setA.retainAll(setB);
        return setA;
    }
    private static double getWordFrequency(List<String> tokens, String word) {
        long count = tokens.stream().filter(token -> token.equalsIgnoreCase(word)).count();
        return (double) count / tokens.size();
    }



}
