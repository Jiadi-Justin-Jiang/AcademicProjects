/**
 * File: word-ladder.cpp
 * ---------------------
 * Implements a program to find word ladders connecting pairs of words.
 */

#include <iostream>
using namespace std;

#include "console.h"
#include "lexicon.h"
#include "strlib.h"
#include "simpio.h"
#include "queue.h"
#include "vector.h"

static string getWord(Lexicon& english, string prompt) {
    while (true) {
        string response = trim(toLowerCase(getLine(prompt)));
        if (response.empty() || english.contains(response)) return response;
        cout << "Your response needs to be an English word, so please try again." << endl;
    }
}

static void generateLadder(Lexicon& english, string start, string end) {
    Lexicon usedWord;
    Queue<Vector<string> > queue;
    Vector<string> initialWordLadder;
    initialWordLadder.add(start);
    usedWord.add(start);
    queue.enqueue(initialWordLadder);
    while (!queue.isEmpty()) {
        Vector<string> wordLadder = queue.dequeue();
        string topWord = wordLadder.get(wordLadder.size() - 1);
        if (topWord == end) {
            cout << "Found Ladder: ";
            for (int i = 0; i < wordLadder.size(); i++) {
                cout << wordLadder.get(i) << " ";
            }
            cout << endl << endl;
            break;
        } else {
            for (int i = 0; i < topWord.length(); i++) {
                for (int j = 'a'; j <= 'z'; j++) {
                    if (topWord.at(i) != j) {
                        string newWord = topWord.substr(0, i) + char(j)
                        + topWord.substr(i + 1, topWord.length() - i - 1);
                        if (english.contains(newWord) && !usedWord.contains(newWord)) {
                            Vector<string> wordLadderClone = wordLadder;
                            wordLadderClone.add(newWord);
                            queue.enqueue(wordLadderClone);
                            usedWord.add(newWord);
                        }
                    }
                }
            }
        }
    }
    
}

static const string kEnglishLanguageDatafile = "EnglishWords.dat";
static void playWordLadder() {
    Lexicon english(kEnglishLanguageDatafile);
    while (true) {
        string start = getWord(english, "Please enter the source word [return to quit]: ");
        
        if (start.empty()) break;
        string end = getWord(english, "Please enter the destination word [return to quit]: ");
        if (end.empty()) break;
        generateLadder(english, start, end);
    }
}

int main() {
    cout << "Welcome to the CS106 word ladder application!" << endl << endl;
    playWordLadder();
    cout << "Thanks for playing!" << endl;
    return 0;
}
