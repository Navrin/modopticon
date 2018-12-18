package uk.co.samwho.modopticon.text;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.stream.Stream;

@RunWith(JUnit4.class)
public class WordListTest {
    @Test
    public void testSimpleMatch() {
       WordList list = WordList.from(Stream.of("foo"));
       assertThat(list.matches("what in the foo is this bar")).isTrue();
    }

    @Test
    public void testMultipleMatches() {
        WordList list = WordList.from(Stream.of("foo", "bar"));
        assertThat(list.numMatches("what in the foo is this bar")).isEqualTo(2);
    }

    @Test
    public void testMultipleMatchesMultipleOccurrences() {
        WordList list = WordList.from(Stream.of("foo", "bar"));
        assertThat(list.numMatches("what in the foo is this bar another foo")).isEqualTo(3);
    }

    @Test
    public void testMatchContent() {
        WordList list = WordList.from(Stream.of("foo", "bar"));
        assertThat(list.allMatches("what the foo bar")).containsExactly("foo", "bar");
    }

    @Test
    public void testNoOverlap() {
        WordList list = WordList.from(Stream.of("foo bar", "bar foo"));
        assertThat(list.allMatches("foo bar foo")).containsExactly("foo bar");
    }

    @Test
    public void testWordsOnly() {
        WordList list = WordList.from(Stream.of("a"));
        assertThat(list.allMatches("aaaaa")).isEmpty();
    }

    @Test
    public void testMultipleMatchesOfSameWord() {
        WordList list = WordList.from(Stream.of("foo"));
        assertThat(list.allMatches("foo foo foo foo")).containsExactly("foo", "foo", "foo", "foo");
        assertThat(list.numMatches("foo foo foo foo")).isEqualTo(4);
    }
}
