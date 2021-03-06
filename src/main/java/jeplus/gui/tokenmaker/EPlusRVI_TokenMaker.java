/***************************************************************************
 *   jEPlus - EnergyPlus shell for parametric studies                      *
 *   Copyright (C) 2010  Yi Zhang <yi@jeplus.org>                          *
 *                                                                         *
 *   This program is free software: you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation, either version 3 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 *   This program is distributed in the hope that it will be useful,       *
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 *   GNU General Public License for more details.                          *
 *                                                                         *
 *   You should have received a copy of the GNU General Public License     *
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>. *
 *                                                                         *
 ***************************************************************************/
package jeplus.gui.tokenmaker;

import javax.swing.text.Segment;
import org.fife.ui.rsyntaxtextarea.AbstractTokenMaker;
import org.fife.ui.rsyntaxtextarea.RSyntaxUtilities;
import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rsyntaxtextarea.TokenMap;

/**
 *
 * @author Yi
 */
public class EPlusRVI_TokenMaker extends AbstractTokenMaker {
    int currentTokenStart = 0;
    int currentTokenType = Token.NULL;

    @Override
    public TokenMap getWordsToHighlight() {
        TokenMap tokenMap = new TokenMap();
        tokenMap.put("eplusout.eso", Token.ANNOTATION);
        tokenMap.put("eplusout.mtr", Token.ANNOTATION);
        tokenMap.put("eplusout.csv", Token.ANNOTATION);
        return tokenMap;
    }

    /**
     * Reset wordsToHighlight to default (by getWordsToHighlight()), and then add additional keywords
     * @param words Additional keywords to add
     * @param type Add as type
     */
    public void updateWordsToHighlight(String[] words, int type) {
        wordsToHighlight = getWordsToHighlight();
        if (words != null) {
            for (String word : words) {
                wordsToHighlight.put(word, type);
            }
        }
    }

    @Override
    public void addToken(Segment segment, int start, int end, int tokenType, int startOffset) {
        // This assumes all keywords, etc. were parsed as "identifiers."
        if (tokenType == Token.IDENTIFIER) {
            int value = wordsToHighlight.get(segment, start, end);
            if (value != -1) {
                tokenType = value;
            }
        }
        super.addToken(segment, start, end, tokenType, startOffset);
    }

    /**
     * Returns a list of tokens representing the given text.
     *
     * @param text The text to break into tokens.
     * @param startTokenType The token with which to start tokenizing.
     * @param startOffset The offset at which the line of tokens begins.
     * @return A linked list of tokens representing <code>text</code>.
     */
    @Override
    public Token getTokenList(Segment text, int startTokenType, int startOffset) {
        resetTokenList();
        char[] array = text.array;
        int offset = text.offset;
        int count = text.count;
        int end = offset + count;
        // Token starting offsets are always of the form:
        // 'startOffset + (currentTokenStart-offset)', but since startOffset and
        // offset are constant, tokens' starting positions become:
        // 'newStartOffset+currentTokenStart'.
        int newStartOffset = startOffset - offset;
        currentTokenStart = offset;
        currentTokenType = startTokenType;
        for (int i = offset; i < end; i++) {
            char c = array[i];
            switch (currentTokenType) {
                case Token.NULL:
                    currentTokenStart = i; // Starting a new token here.
                    switch (c) {
                        case ' ':
                        case '\t':
                            currentTokenType = Token.WHITESPACE;
                            break;
                        case ',':
                        case ';':
                            currentTokenType = Token.SEPARATOR;
                            break;
                        case '"':
                            currentTokenType = Token.LITERAL_STRING_DOUBLE_QUOTE;
                            break;
                        case '!':
                            currentTokenType = Token.COMMENT_EOL;
                            break;
                        default:
//                            if (RSyntaxUtilities.isDigit(c) || c == '.' || c == '-') {
//                                currentTokenType = Token.LITERAL_NUMBER_FLOAT;
//                                break;
//                            } else if (RSyntaxUtilities.isLetter(c) || c == '/' || c == '_') {
//                                currentTokenType = Token.IDENTIFIER;
//                                break;
//                            }
                            // Anything not currently handled - mark as an identifier
                            currentTokenType = Token.IDENTIFIER;
                            break;
                    } // End of switch (c).
                    break;
                case Token.WHITESPACE:
                    switch (c) {
                        case ' ':
                        case '\t':
                            break; // Still whitespace.
                        case ',':
                        case ';':
                            addToken(text, currentTokenStart, i - 1, Token.WHITESPACE, newStartOffset + currentTokenStart);
                            currentTokenStart = i;
                            currentTokenType = Token.SEPARATOR;
                            break;
                        case '"':
                            addToken(text, currentTokenStart, i - 1, Token.WHITESPACE, newStartOffset + currentTokenStart);
                            currentTokenStart = i;
                            currentTokenType = Token.LITERAL_STRING_DOUBLE_QUOTE;
                            break;
                        case '!':
                            addToken(text, currentTokenStart, i - 1, Token.WHITESPACE, newStartOffset + currentTokenStart);
                            currentTokenStart = i;
                            currentTokenType = Token.COMMENT_EOL;
                            break;
                        default:
                            // Add the whitespace token and start anew.
                            addToken(text, currentTokenStart, i - 1, Token.WHITESPACE, newStartOffset + currentTokenStart);
                            currentTokenStart = i;
//                            if (RSyntaxUtilities.isDigit(c)) {
//                                currentTokenType = Token.LITERAL_NUMBER_FLOAT;
//                                break;
//                            } else if (RSyntaxUtilities.isLetter(c) || c == '/' || c == '_') {
//                                currentTokenType = Token.IDENTIFIER;
//                                break;
//                            }
                            // Anything not currently handled - mark as identifier
                            currentTokenType = Token.IDENTIFIER;
                    } // End of switch (c).
                    break;
                case Token.SEPARATOR:
                    switch (c) {
                        case ',':
                        case ';':
                            break; // Still separator.
                        case ' ':
                        case '\t':
                            addToken(text, currentTokenStart, i - 1, Token.SEPARATOR, newStartOffset + currentTokenStart);
                            currentTokenStart = i;
                            currentTokenType = Token.WHITESPACE;
                            break; // Still whitespace.
                        case '"':
                            addToken(text, currentTokenStart, i - 1, Token.SEPARATOR, newStartOffset + currentTokenStart);
                            currentTokenStart = i;
                            currentTokenType = Token.LITERAL_STRING_DOUBLE_QUOTE;
                            break;
                        case '!':
                            addToken(text, currentTokenStart, i - 1, Token.SEPARATOR, newStartOffset + currentTokenStart);
                            currentTokenStart = i;
                            currentTokenType = Token.COMMENT_EOL;
                            break;
                        default:
                            // Add the whitespace token and start anew.
                            addToken(text, currentTokenStart, i - 1, Token.SEPARATOR, newStartOffset + currentTokenStart);
                            currentTokenStart = i;
//                            if (RSyntaxUtilities.isDigit(c)) {
//                                currentTokenType = Token.LITERAL_NUMBER_FLOAT;
//                                break;
//                            } else if (RSyntaxUtilities.isLetter(c) || c == '/' || c == '_') {
//                                currentTokenType = Token.IDENTIFIER;
//                                break;
//                            }
                            // Anything not currently handled - mark as identifier
                            currentTokenType = Token.IDENTIFIER;
                    } // End of switch (c).
                    break;
                default:
// Should never happen
                case Token.IDENTIFIER:
                    switch (c) {
                        case ' ':
                        case '\t':
                            addToken(text, currentTokenStart, i - 1, Token.IDENTIFIER, newStartOffset + currentTokenStart);
                            currentTokenStart = i;
                            currentTokenType = Token.WHITESPACE;
                            break;
                        case ',':
                        case ';':
                            addToken(text, currentTokenStart, i - 1, Token.IDENTIFIER, newStartOffset + currentTokenStart);
                            currentTokenStart = i;
                            currentTokenType = Token.SEPARATOR;
                            break;
                        case '"':
                            addToken(text, currentTokenStart, i - 1, Token.IDENTIFIER, newStartOffset + currentTokenStart);
                            currentTokenStart = i;
                            currentTokenType = Token.LITERAL_STRING_DOUBLE_QUOTE;
                            break;
                        default:
                            if (RSyntaxUtilities.isLetterOrDigit(c) || c == '/' || c == '_') {
                                break; // Still an identifier of some type.
                            }
                            // Otherwise, we're still an identifier (?).
                    } // End of switch (c).
                    break;
                case Token.LITERAL_NUMBER_FLOAT:
                    switch (c) {
                        case ' ':
                        case '\t':
                            addToken(text, currentTokenStart, i - 1, Token.LITERAL_NUMBER_FLOAT, newStartOffset + currentTokenStart);
                            currentTokenStart = i;
                            currentTokenType = Token.WHITESPACE;
                            break;
                        case ',':
                        case ';':
                            addToken(text, currentTokenStart, i - 1, Token.LITERAL_NUMBER_FLOAT, newStartOffset + currentTokenStart);
                            currentTokenStart = i;
                            currentTokenType = Token.SEPARATOR;
                            break;
                        case '"':
                            addToken(text, currentTokenStart, i - 1, Token.LITERAL_NUMBER_FLOAT, newStartOffset + currentTokenStart);
                            currentTokenStart = i;
                            currentTokenType = Token.LITERAL_STRING_DOUBLE_QUOTE;
                            break;
                        default:
                            if (RSyntaxUtilities.isDigit(c) || c == '.') {
                                break; // Still a literal number.
                            }
                            // Otherwise, remember this was a number and start over.
                            addToken(text, currentTokenStart, i - 1, Token.LITERAL_NUMBER_FLOAT, newStartOffset + currentTokenStart);
                            i--;
                            currentTokenType = Token.NULL;
                    } // End of switch (c).
                    break;
                case Token.COMMENT_EOL:
                    i = end - 1;
                    addToken(text, currentTokenStart, i, currentTokenType, newStartOffset + currentTokenStart);
                    // We need to set token type to null so at the bottom we don't add one more token.
                    currentTokenType = Token.NULL;
                    break;
                case Token.LITERAL_STRING_DOUBLE_QUOTE:
                    if (c == '"') {
                        addToken(text, currentTokenStart, i, Token.LITERAL_STRING_DOUBLE_QUOTE, newStartOffset + currentTokenStart);
                        currentTokenType = Token.NULL;
                    }
                    break;
            } // End of switch (currentTokenType).
        } // End of for (int i=offset; i<end; i++).
        switch (currentTokenType) {
        // Remember what token type to begin the next line with.
            case Token.LITERAL_STRING_DOUBLE_QUOTE:
                addToken(text, currentTokenStart, end - 1, currentTokenType, newStartOffset + currentTokenStart);
                break;
        // Do nothing if everything was okay.
            case Token.NULL:
                addNullToken();
                break;
        // All other token types don't continue to the next line...
            default:
                addToken(text, currentTokenStart, end - 1, currentTokenType, newStartOffset + currentTokenStart);
                addNullToken();
        }
        // Return the first token in our linked list.
        return firstToken;
    }
    
}
