import java.util.*;

class TrieNode {

    Map<Character, TrieNode> children = new HashMap<>();
    boolean isEnd = false;
}

class Autocomplete {

    TrieNode root = new TrieNode();
    HashMap<String, Integer> freq = new HashMap<>();

    public void insert(String query) {

        TrieNode node = root;

        for (char c : query.toCharArray()) {
            node.children.putIfAbsent(c, new TrieNode());
            node = node.children.get(c);
        }

        node.isEnd = true;
        freq.put(query, freq.getOrDefault(query, 0) + 1);
    }

    public List<String> search(String prefix) {

        TrieNode node = root;

        for (char c : prefix.toCharArray()) {
            if (!node.children.containsKey(c))
                return new ArrayList<>();

            node = node.children.get(c);
        }

        List<String> results = new ArrayList<>();
        dfs(node, prefix, results);

        PriorityQueue<String> pq =
                new PriorityQueue<>((a, b) -> freq.get(b) - freq.get(a));

        pq.addAll(results);

        List<String> top = new ArrayList<>();

        int k = 10;
        while (!pq.isEmpty() && k-- > 0)
            top.add(pq.poll());

        return top;
    }

    private void dfs(TrieNode node, String word, List<String> results) {

        if (node.isEnd)
            results.add(word);

        for (char c : node.children.keySet())
            dfs(node.children.get(c), word + c, results);
    }
}

public class Main {

    public static void main(String[] args) {

        Autocomplete ac = new Autocomplete();

        ac.insert("java tutorial");
        ac.insert("javascript");
        ac.insert("java download");

        System.out.println(ac.search("jav"));
    }
}