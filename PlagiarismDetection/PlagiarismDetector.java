import java.util.*;

public class PlagiarismDetector {

    private final Map<String, List<String>> nGramIndex = new HashMap<>();
    private final int N = 5;


    public void addDocument(String docId, String content) {
        List<String> nGrams = generateNGrams(content);
        for (String gram : nGrams) {
            nGramIndex.computeIfAbsent(gram, k -> new ArrayList<>()).add(docId);
        }
    }

    public void analyzeDocument(String newDocId, String content) {
        List<String> studentNGrams = generateNGrams(content);
        Map<String, Integer> matchCounts = new HashMap<>();

        for (String gram : studentNGrams) {
            if (nGramIndex.containsKey(gram)) {
                for (String originalDocId : nGramIndex.get(gram)) {
                    matchCounts.put(originalDocId, matchCounts.getOrDefault(originalDocId, 0) + 1);
                }
            }
        }

        System.out.println("Analysis for " + newDocId + ":");
        System.out.println("Extracted " + studentNGrams.size() + " n-grams.");

        for (Map.Entry<String, Integer> entry : matchCounts.entrySet()) {
            double similarity = (entry.getValue() * 100.0) / studentNGrams.size();
            String status = (similarity > 50) ? "!!! PLAGIARISM DETECTED !!!" : (similarity > 10 ? "SUSPICIOUS" : "CLEAN");

            System.out.printf("→ Matches with %s: %d n-grams | Similarity: %.1f%% (%s)\n",
                    entry.getKey(), entry.getValue(), similarity, status);
        }
    }

    private List<String> generateNGrams(String text) {
        String[] words = text.toLowerCase().replaceAll("[^a-zA-Z ]", "").split("\\s+");
        List<String> nGrams = new ArrayList<>();
        for (int i = 0; i <= words.length - N; i++) {
            StringBuilder gram = new StringBuilder();
            for (int j = 0; j < N; j++) {
                gram.append(words[i + j]).append(j < N - 1 ? " " : "");
            }
            nGrams.add(gram.toString());
        }
        return nGrams;
    }

    public static void main(String[] args) {
        PlagiarismDetector detector = new PlagiarismDetector();

        detector.addDocument("essay_089.txt", "The quick brown fox jumps over the lazy dog repeatedly.");
        detector.addDocument("essay_092.txt", "Data structures are fundamental to computer science and programming efficiency.");

        String studentSubmission = "Data structures are fundamental to computer science and code efficiency.";
        detector.analyzeDocument("student_work.txt", studentSubmission);
    }
}