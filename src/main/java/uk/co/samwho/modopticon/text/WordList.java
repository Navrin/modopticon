package uk.co.samwho.modopticon.text;

import org.ahocorasick.trie.Emit;
import org.ahocorasick.trie.Trie;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WordList {
    private final Trie matcher;

    public static WordList from(String path) throws IOException {
        return from(new File(path));
    }

    public static WordList from(File file) throws IOException {
        try (Stream<String> lines = Files.lines(file.toPath())) {
            return from(lines);
        }
    }

    public static WordList from(Stream<String> stream) {
        return new WordList(stream);
    }

    private WordList(Stream<String> words) {
        Trie.TrieBuilder builder = Trie.builder()
            .ignoreOverlaps()
            .onlyWholeWords();

        words.forEach((word) -> builder.addKeyword(word));

        this.matcher = builder.build();
    }

    public int numMatches(String s) {
        return matcher.parseText(s).size();
    }

    public Collection<String> allMatches(String s) {
        return matcher.parseText(s).stream().map(Emit::getKeyword).collect(Collectors.toList());
    }

    public boolean matches(String s) {
        return matcher.containsMatch(s);
    }
}

