/**
 * File: trees.cpp
 * ---------------
 * Draws a recursive tree as specified in the Assignment 3 handout.
 */

#include <string>    // for string
#include <iostream>  // for cout, endl
using namespace std;

#include "console.h" // required of all CS106 C++ programs
#include "gwindow.h" // for GWindow class and its setTitle, setColor, and drawPolarLine methods
#include "gtypes.h"  // for GPoint class
#include "random.h"  // for randomChance function
#include "math.h"

const static double kWindowWidth = 600;
const static double kWindowHeight = 600;
const static string kWindowTitle = "Recursive Trees";
const static double kTrunkLength  = kWindowHeight/4;
const static double kShrinkFactor = 0.70;
const static int kBranchAngleSeparation = 15;
const static int kTrunkStartAngle = 90;
const static double kTrunkStartRadianAngle = (double)kTrunkStartAngle / 180 * M_PI;
const static string kLeafColor = "#2e8b57";
const static string kTrunkColor = "#8b7765";
const static double kBranchProbability = 1.0;
int tempOrder = 0;

static void drawTree(GWindow& window, int order, GPoint point, double angle) {
    int reverseOrder = tempOrder - order;
    window.setColor(order < 2 ? kLeafColor : kTrunkColor);
    if (order == 0) {
        window.drawPolarLine(point, pow(0.7, reverseOrder) * kTrunkLength, angle);
    } else {
        double trunkLength = pow(0.7, reverseOrder) * kTrunkLength;
        window.drawPolarLine(point, trunkLength, angle);
        GPoint newPoint(point.getX() + trunkLength * cos((double)angle / 180 * M_PI), point.getY() - trunkLength * sin((double)angle / 180 * M_PI));
        for (int angleOffset = -45; angleOffset <= 45; angleOffset = angleOffset + 15) {
            drawTree(window, order - 1, newPoint, angle + angleOffset);
        }
    }
}

const static int kHighestOrder = 5;
int main() {
    GWindow window(kWindowWidth, kWindowHeight);
    window.setWindowTitle(kWindowTitle);
    cout << "Repeatedly click the mouse in the graphics window to draw " << endl;
    cout << "recursive trees of higher and higher order." << endl;
    for (int order = 0; order <= kHighestOrder; order++) {
        waitForClick();
        window.clear();
        tempOrder = order;
        drawTree(window, order, GPoint(window.getWidth()/2, window.getHeight()), kTrunkStartAngle);
    }
    
    cout << endl;
    cout << "All trees through order " << kHighestOrder << " have been drawn." << endl;
    cout << "Click the mouse anywhere in the graphics window to quit." << endl;
    waitForClick();
    return 0;
}
