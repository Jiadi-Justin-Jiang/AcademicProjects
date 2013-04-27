/**
 * File: boggle.cpp
 * ----------------
 * Implements the game of Boggle.
 */
 
#include <cctype>
#include <iostream>
#include <math.h>
using namespace std;

#include "simpio.h"
#include "gwindow.h"
#include "gboggle.h"
#include "grid.h"
#include "random.h"
#include "lexicon.h"
#include "set.h"

const int kBoggleWindowWidth = 650;
const int kBoggleWindowHeight = 350;
const int kStandardBoardSize = 16;
const int kBigBoggleBoardSize = 25;
const int kCubeSize = 6;
const string kEnglishLanguageDatafile = "EnglishWords.dat";
const int kPauseTime = 200;
const int kMinWordLength = 4;
const string kHelperString = "HelperString";

const string kStandardCubes[16] = {
   "AAEEGN", "ABBJOO", "ACHOPS", "AFFKPS",
   "AOOTTW", "CIMOTU", "DEILRX", "DELRVY",
   "DISTTY", "EEGHNW", "EEINSU", "EHRTVW",
   "EIOSST", "ELRTTY", "HIMNQU", "HLNNRZ"
};

const string kBigBoggleCubes[25] = {
   "AAAFRS", "AAEEEE", "AAFIRS", "ADENNN", "AEEEEM",
   "AEEGMU", "AEGMNN", "AFIRSY", "BJKQXZ", "CCNSTW",
   "CEIILT", "CEILPT", "CEIPST", "DDLNOR", "DDHNOT",
   "DHHLOR", "DHLNOR", "EIIITT", "EMOTTT", "ENSSSU",
   "FIPRSY", "GORRVW", "HIPRRY", "NOOTUW", "OOOTTU"
};

static void welcome() {
    cout << "Welcome!  You're about to play an intense game ";
    cout << "of mind-numbing Boggle.  The good news is that ";
    cout << "you might improve your vocabulary a bit.  The ";
    cout << "bad news is that you're probably going to lose ";
    cout << "miserably to this little dictionary-toting hunk ";
    cout << "of silicon.  If only YOU had a gig of RAM..." << endl << endl;
}

static void giveInstructions() {
    cout << endl;
    cout << "The boggle board is a grid onto which I ";
    cout << "I will randomly distribute cubes. These ";
    cout << "6-sided cubes have letters rather than ";
    cout << "numbers on the faces, creating a grid of ";
    cout << "letters on which you try to form words. ";
    cout << "You go first, entering all the words you can ";
    cout << "find that are formed by tracing adjoining ";
    cout << "letters. Two letters adjoin if they are next ";
    cout << "to each other horizontally, vertically, or ";
    cout << "diagonally. A letter can only be used once ";
    cout << "in each word. Words must be at least four ";
    cout << "letters long and can be counted only once. ";
    cout << "You score points based on word length: a ";
    cout << "4-letter word is worth 1 point, 5-letters ";
    cout << "earn 2 points, and so on. After your puny ";
    cout << "brain is exhausted, I, the supercomputer, ";
    cout << "will find all the remaining words and double ";
    cout << "or triple your paltry score." << endl << endl;
    cout << "Hit return when you're ready...";
    getLine();
}

static bool responseIsAffirmative(const string& prompt) {
    while (true) {
        string answer = getLine(prompt);
        if (!answer.empty()) {
            switch (toupper(answer[0])) {
                case 'Y': return true;
                case 'N': return false;
            }
        }
        cout << "Please answer yes or no." << endl;
    }
}

static void customizeBoard(int& boggleBoardSize, Grid<char>& boggleBoard) {
    cout << "Enter a " << boggleBoardSize << "-character string to identify which letters you want on the cubes. ";
    cout << "The first " << sqrt(boggleBoardSize) << " letters are the cubes on the top row from left to right, ";
    cout << "the next " << sqrt(boggleBoardSize) << " letters are the second row, and so on. " << endl;
    while (true) {
        string answer = getLine("Enter a string: ");
        if (answer.length() >= boggleBoardSize) {
            answer = toUpperCase(answer);
            for (int i = 0; i < sqrt(boggleBoardSize); i++) {
                for (int j = 0; j < sqrt(boggleBoardSize); j++) {
                    boggleBoard[i][j] = answer.at(i * sqrt(boggleBoardSize) + j);
                }
            }
            return;
        }
        cout << "String must include " << boggleBoardSize << " characters! Try again: " << endl;
    }
}

static void generateBoard(int& boggleBoardSize, Grid<char>& boggleBoard) {
    Vector<string> boggleCubes;
    switch (boggleBoardSize) {
        case kStandardBoardSize:
            foreach(string cube in kStandardCubes) {
                boggleCubes.add(cube);
            }
            break;
        case kBigBoggleBoardSize:
            foreach(string cube in kBigBoggleCubes) {
                boggleCubes.add(cube);
            }
            break;
        default:
            break;
    }
    for (int i = 0; i < boggleCubes.size(); i++) {
        int randIndex = randomInteger(i, boggleCubes.size() - 1);
        string str = boggleCubes.get(randIndex);
        boggleCubes.set(randIndex, boggleCubes.get(i));
        boggleCubes.set(i, str);
    }
    for (int i = 0; i < sqrt(boggleBoardSize); i++) {
        for (int j = 0; j < sqrt(boggleBoardSize); j++) {
            boggleBoard[i][j] = boggleCubes.get(i * sqrt(boggleBoardSize) + j).at(randomInteger(0, kCubeSize - 1));
        }
    }
}

static void labelAllCubes(int boggleBoardSize, Grid<char>& boggleBoard) {
    for (int i = 0; i < sqrt(boggleBoardSize); i++) {
        for (int j = 0; j < sqrt(boggleBoardSize); j++) {
            labelCube(i, j, boggleBoard[i][j]);
        }
    }
}

static bool isAtLeastFourLetters(string& response) {
    return response.length() >= kMinWordLength;
}

static bool isEnglish(string& response, Lexicon& english) {
    return english.contains(response);
}

static bool isNotUsed(string& response, Set<string>& playerWordList) {
    return !playerWordList.contains(response);
}

static bool isEnglishPrefix(string& word, Lexicon& english) {
    return english.containsPrefix(word);
}

static string getValidWord(Lexicon& english, Set<string>& humanPlayerWordList) {
    while (true) {
        string response = getLine("Enter a word: ");
        response = toUpperCase(response);
        if (response.empty() || (isAtLeastFourLetters(response) &&
            isEnglish(response, english) && isNotUsed(response, humanPlayerWordList))) {
            return response;
        } else if (!isAtLeastFourLetters(response)) {
            cout << "I'm sorry, but we have our standards. " << endl;
            cout << "That word doesn't meet the minimum word length. " << endl;
        } else if (!isEnglish(response, english)) {
            cout << "You can't make that word! " << endl;
        } else if (!isNotUsed(response, humanPlayerWordList)) {
            cout << "You've already guessed that! " << endl;
        }
    }
}

static bool gridInRange(int i, int j, int boggleBoardSize) {
    return (i >= 0 && i < sqrt(boggleBoardSize) && j >= 0 && j < sqrt(boggleBoardSize));
}

static bool nextLetterIsAdjoined(int i, int j, Grid<char>& boggleBoard, string word, int boggleBoardSize, Grid<bool>& boggleBoardMark) {
    if (word.length() == 0) {
        boggleBoardMark[i][j] = true;
        return true;
    }
    for (int m = -1; m <= 1; m++) {
        for (int n = -1; n <= 1; n++) {
            if (gridInRange(i + m, j + n, boggleBoardSize) && !(m == 0 && n == 0) && !boggleBoardMark[i + m][j + n] && word.at(0) == boggleBoard[i + m][j + n]) {
                boggleBoardMark[i + m][j + n] = true;
                if (nextLetterIsAdjoined(i + m, j + n, boggleBoard, word.substr(1), boggleBoardSize, boggleBoardMark)) {
                    return true;
                } else {
                    boggleBoardMark[i + m][j + n] = false;
                }
            }
        }
    }
    return false;
}

static bool wordIsOnBoard(string word, int& boggleBoardSize, Grid<char>& boggleBoard, Grid<bool>& boggleBoardMark) {
    for (int i = 0; i < sqrt(boggleBoardSize); i++) {
        for (int j = 0; j < sqrt(boggleBoardSize); j++) {
            if (word.at(0) == boggleBoard[i][j]) {
                boggleBoardMark[i][j] = true;
                if (nextLetterIsAdjoined(i, j, boggleBoard, word.substr(1), boggleBoardSize, boggleBoardMark)) {
                    return true;
                } else {
                    boggleBoardMark[i][j] = false;
                }
            }
        }
    }
    cout << "You can't make that word! " << endl;
    return false;
}

static void resetBoggleBoardMark(Grid<bool>& boggleBoardMark, int& boggleBoardSize) {
    boggleBoardMark.resize(sqrt(boggleBoardSize), sqrt(boggleBoardSize));
    for (int i = 0; i < sqrt(boggleBoardSize); i++) {
        for (int j = 0; j < sqrt(boggleBoardSize); j++) {
            boggleBoardMark[i][j] = false;
            highlightCube(i, j, boggleBoardMark[i][j]);
        }
    }
}

static void highlightCorrespondingCubes(Grid<bool>& boggleBoardMark, int& boggleBoardSize) {
    for (int i = 0; i < sqrt(boggleBoardSize); i++) {
        for (int j = 0; j < sqrt(boggleBoardSize); j++) {
            highlightCube(i, j, boggleBoardMark[i][j]);
        }
    }
}

static void humanPlay(Lexicon& english, int& boggleBoardSize, Grid<char>& boggleBoard, Set<string>& computerPlayerInitialWordList) {
    cout << "Ok, take all the time you want and find all the words you can! ";
    cout << "Signal that you're finished by entering an empty line. " << endl << endl;
    Set<string> humanPlayerWordList;
    Grid<bool> boggleBoardMark;
    while (true) {
        resetBoggleBoardMark(boggleBoardMark, boggleBoardSize);
        highlightCorrespondingCubes(boggleBoardMark, boggleBoardSize);
        string word = getValidWord(english, humanPlayerWordList);
        if (word.empty()) break;
        if (wordIsOnBoard(word, boggleBoardSize, boggleBoard, boggleBoardMark)) {
            humanPlayerWordList.add(word);
            recordWordForPlayer(word, HUMAN);
            highlightCorrespondingCubes(boggleBoardMark, boggleBoardSize);
            pause(kPauseTime);
        }
    }
    computerPlayerInitialWordList = humanPlayerWordList;
}

static void checkAdjoinedCube(Lexicon& english, int& boggleBoardSize, Grid<char>& boggleBoard, string& oldWord, Set<string>& playerWordList, int i, int j, Grid<bool>& boggleBoardMark) {
    for (int m = -1; m <= 1; m++) {
        for (int n = -1; n <= 1; n++) {
            if (gridInRange(i + m, j + n, boggleBoardSize) && !(m == 0 && n == 0) && !boggleBoardMark[i + m][j + n]) {
                string newWord = oldWord + boggleBoard[i + m][j + n];
                if (isAtLeastFourLetters(newWord) && isEnglish(newWord, english) && isNotUsed(newWord, playerWordList)) {
                    recordWordForPlayer(newWord, COMPUTER);
                    playerWordList.add(newWord);
                }
                if (isEnglishPrefix(newWord, english)) {
                    boggleBoardMark[i + m][j + n] = true;
                    checkAdjoinedCube(english, boggleBoardSize, boggleBoard, newWord, playerWordList, i + m, j + n, boggleBoardMark);
                    boggleBoardMark[i + m][j + n] = false;
                }
            }
        }
    }
}

static void computerPlay(Lexicon& english, int& boggleBoardSize, Grid<char>& boggleBoard, Set<string>& computerPlayerInitialWordList) {
    Grid<bool> boggleBoardMark;
    resetBoggleBoardMark(boggleBoardMark, boggleBoardSize);
    for (int i = 0; i < sqrt(boggleBoardSize); i++) {
        for (int j = 0; j < sqrt(boggleBoardSize); j++) {
            string word = kHelperString + boggleBoard[i][j];
            word = word.substr(kHelperString.length());
            boggleBoardMark[i][j] = true;
            checkAdjoinedCube(english, boggleBoardSize, boggleBoard, word, computerPlayerInitialWordList, i, j, boggleBoardMark);
            boggleBoardMark[i][j] = false;
        }
    }
}

int main() {
    Grid<char> boggleBoard;
    GWindow gw(kBoggleWindowWidth, kBoggleWindowHeight);
    initGBoggle(gw);
    welcome();
    if (responseIsAffirmative("Do you need instructions? ")) {
        giveInstructions();
    }
    cout << "You can choose standard Boggle (4x4 grid) or Big Boggle (5x5)." << endl;
    int boggleBoardSize;
    if (responseIsAffirmative("Would you like Big Boggle? ")) {
        boggleBoardSize = kBigBoggleBoardSize;
    } else {
        boggleBoardSize = kStandardBoardSize;
    }
    drawBoard(sqrt(boggleBoardSize), sqrt(boggleBoardSize));
    while (true) {
        initGBoggle(gw);
        drawBoard(sqrt(boggleBoardSize), sqrt(boggleBoardSize));
        cout << "I'll give you a chance to set up the board to your specification, ";
        cout << "which makes it easier to confirm your boggle program is working. " << endl;
        boggleBoard.resize(sqrt(boggleBoardSize), sqrt(boggleBoardSize));
        if (responseIsAffirmative("Do you want to force the board configuration? ")) {
            customizeBoard(boggleBoardSize, boggleBoard);
        } else {
            generateBoard(boggleBoardSize, boggleBoard);
        }
        labelAllCubes(boggleBoardSize, boggleBoard);
        Lexicon english(kEnglishLanguageDatafile);
        Set<string> computerPlayerInitialWordList;
        humanPlay(english, boggleBoardSize, boggleBoard, computerPlayerInitialWordList);
        computerPlay(english, boggleBoardSize, boggleBoard, computerPlayerInitialWordList);
        if (!responseIsAffirmative("Would you like to play again? ")) break;
    }
        return 0;
}
