package com.samurai.community.util;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;


@Component
public class SensitiveFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(SensitiveFilter.class);

    private static final String REPLACEMENT = "***";

    private TrieNode rootNode = new TrieNode();

    @PostConstruct
    public void init() {
        long start = System.currentTimeMillis();
        try (
                InputStream is = this.getClass().getClassLoader()
                        .getResourceAsStream("sensitive-words.txt");
                BufferedReader reader = new BufferedReader(new InputStreamReader(is))
        ) {
            String keyword;
            while ((keyword = reader.readLine()) != null) {
                // 添加到前缀树
                this.addKeyword(keyword);
            }

        } catch (IOException e) {
            LOGGER.error("Failed to load sensitive word file: " + e.getMessage());
            e.printStackTrace();
        }
        LOGGER.info("Prefix tree construction time: " + (System.currentTimeMillis() - start) + "ms");

    }


/**
     * Adding a sensitive word to the prefix tree
     * @param keyword means the sensitive word
     */
    private void addKeyword(String keyword) {
        TrieNode tempNode = rootNode;
        for (int i = 0; i < keyword.length(); i++) {
            char c = keyword.charAt(i);
            TrieNode subNode = tempNode.getSubNode(c);

            if (subNode == null) {
                subNode = new TrieNode();
                tempNode.addSubNode(c, subNode);
            }

            tempNode = subNode;

            if (i == keyword.length() - 1) {
                tempNode.setKeywordEnd(true);
            }
        }

    }

/**
     * Filter sensitive words
     * Remove symbols first for sensitive words that have symbols in them
     *
     * @param text The text to be filtered
     * @return filtered text
     */

    public String filter(String text) {
        if (StringUtils.isBlank(text))
            return null;

        TrieNode tempNode = rootNode;
        // begin is a pointer pointing to the text
        int begin = 0;
        // position is a pointer pointing to the text
        int position = 0;
        // use sb to store result
        StringBuilder sb = new StringBuilder();
        while (position < text.length()) {
            char c = text.charAt(position);
            // jump the symbol
            if (isSymbol(c)) {
                if (tempNode == rootNode) {
                    sb.append(c);
                    begin++;
                }
                position++;
                continue;
            }
            // if it is not a symbol then check for then sensitive words
            tempNode = tempNode.getSubNode(c);
            if (tempNode == null) {
                // 以begin开头的字符串不是敏感词
                sb.append(text.charAt(begin));
                // enter into next begin point to check for sensitive words
                position = ++begin;
                tempNode = rootNode;
            } else if (tempNode.isKeywordEnd()) {
                // Found the sensitive word, replace the begin-position string
                sb.append(REPLACEMENT);
                begin = ++position;
                tempNode = rootNode;
            } else {
                // Check the next character
                position++;

            }
        }

        sb.append(text.substring(begin));
        return sb.toString();
    }

    private boolean isSymbol(char c) {
        // 0x2E80 to 0x9FFF for East Asian characters
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF);
    }


    private static class TrieNode {
        private boolean isKeywordEnd = false;

        private Map<Character, TrieNode> subNodes = new HashMap<>(16);

        public boolean isKeywordEnd() {
            return isKeywordEnd;
        }

        public void setKeywordEnd(boolean keywordEnd) {
            isKeywordEnd = keywordEnd;
        }

        public void addSubNode(Character c, TrieNode node) {
            subNodes.put(c, node);
        }

        public TrieNode getSubNode(Character c) {
            return subNodes.get(c);
        }
    }
}

