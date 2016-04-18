#include <opencv2/opencv.hpp>
#include <opencv2/core.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <opencv2/highgui/highgui.hpp>

#include <math.h>
#include <vector>

#include <stdio.h>
#include <stdlib.h>
#include <netinet/in.h>
#include <string.h>
#include <netdb.h>
#include <sys/socket.h>
#include <unistd.h>
#include <iostream>

#define BGR_DETECT
#define RELEASE_MODE

const int RED_THRESHOLD = 105;
const int BLACK_THRESHOLD = 85;
const int FRAME_THRESHOLD = 7;

int board[8][8];
int prevBoard[8][8];
int diffBoard[8][8];
const int xlow = 190; const int xhigh = 450;
const int ylow = 140; const int yhigh = 400;
cv::Point p0(xlow, ylow); cv::Point p1(xhigh, yhigh);
cv::Rect myROI(p0, p1);

const int side = (xhigh - xlow)/8;

int sockfd;
char buffer[256];

void putOnSocks()
{
    int portno;
    struct sockaddr_in serv_addr;
    struct hostent* server;

    std::cout << "1a\n";
    sockfd = socket(AF_INET, SOCK_STREAM, 0);

    if (sockfd < 0) {
        std::cout << "ERROR opening sock"<< std::endl;
    }
    std::cout << "2a\n";
    server = gethostbyname("10.33.50.106");
    std::cout << "3a\n";

    if (server == NULL) {
        std::cout << "ERRROR host not found"<< std::endl;
    }

    std::cout << "4a\n";
    bzero((char*) &serv_addr, sizeof(serv_addr));
    portno = 8080;
    serv_addr.sin_family = AF_INET;
    serv_addr.sin_port = htons(portno);
    std::cout << "5a\n";

    bcopy((char*) server->h_addr, (char*) &serv_addr.sin_addr.s_addr, server->h_length);

    if (connect(sockfd,(struct sockaddr *) &serv_addr, sizeof(serv_addr)) < 0) {
        std::cout << "ERROR connecting to server"<< std::endl;
    }
}

int checkArgs(int argc)
{
    std::cout << "argc = " << argc << std::endl;
    if (argc < 2) {
        std::cout << "Trying to initialize camera..." << std::endl;
        return 0;
    }
    return -1;
}

void checkCamInit(cv::VideoCapture cap)
{
    if (!cap.isOpened()) {
        std::cerr << "Error: Camera could not be opened!" << std::endl;
        std::exit(-1);
    }
}

void checkFrames(const cv::Mat &frame, const cv::Mat &bg)
{
    if (frame.empty()) {
        std::cout << "Error! No frameture found. Check source. \n";
        std::exit(-1);
    }
    if (bg.empty()) {
        std::cout << "Error! No bg found. Check source." << std::endl;
        std::exit(-1);
    }
}

bool isRed(cv::Point center, int radius, cv::Mat diffImg, cv::Mat frame)
{
    int winSize = floor(radius/sqrt(2));
    
    int begPtx = center.x-winSize;
    int endPtx = center.x+winSize;
    int begPty = center.y-winSize;
    int endPty = center.y+winSize;

    int redPix = 0;
    int blackPix = 0;

    cv::Mat frameHSV;
#ifdef BGR_DETECT
    for(int x = begPtx; x < endPtx; ++x) {
        for (int y = begPty; y < endPty; ++y) {
            int redChan = frame.at<cv::Vec3b>(y,x)[2];
            if (redChan > RED_THRESHOLD) {
                redPix++;
            } else if (redChan < BLACK_THRESHOLD) {
                blackPix++;
            }
            
        }
    }
#else

    cv::cvtColor(frame, frameHSV, cv::COLOR_BGR2HSV);
    for(int x = begPtx; x < endPtx; ++x) {
        for (int y = begPty; y < endPty; ++y) {
            int redSat = frameHSV.at<cv::Vec3b>(y,x)[0];
            if (redSat > 170 || redSat < 10) {
                redPix++;
            } else if (redSat < 150 || redSat > 20) {
                blackPix++;
            }
            
        }
    }
#endif
    cv::Point upper(begPtx,begPty);
    cv::Point lower(endPtx,endPty);
    cv::rectangle(diffImg,upper,lower,cv::Scalar(255,255,255),1);
    if (redPix > blackPix) {
        return true;
    }
    return false;
}

void clearBoard()
{
    for (int x = 0; x < 8; ++x) {
        for (int y = 0; y < 8; ++y) {
            board[x][y] = 0;
            //prevBoard[x][y] = 0;
        }
    }
}

void updateBoard(cv::Point center, bool red)
{
#ifndef RELEASE_MODE
    std::cout << "center.x = " << center.x << std::endl;
    std::cout << "center.y = " << center.y << std::endl;
    std::cout << "side = " << side << std::endl;
    int myX = static_cast<int>(center.x/side);
    double myPreciseX = center.x/side;
    std::cout << "myX = " << myX << std::endl;
    std::cout << "myPresiceX = " << myPreciseX << std::endl;
    //cv::waitKey(0);
#endif

    int x = static_cast<int>(center.x/side);
    int y = static_cast<int>(center.y/side);
    if (red) {
        board[x][y] = 1;
    } else {
        board[x][y] = 2;
    }

}

void tellJimmy(bool MIA, int x, int y, int color)
{
    bzero(buffer,256);
    if (MIA) {
        buffer[0] = 'M';
        buffer[1] = 'I';
        buffer[2] = 'A';
        buffer[3] = ' ';

    } else {
        buffer[0] = 'Y';
        buffer[1] = 'E';
        buffer[2] = 'S';
        buffer[3] = ' ';
    }
    int n;
#ifndef RELEASE_MODE
    std::cout << "x = " << x << ", y = " << y << std::endl;
#endif
    char charx = '0' + x;
    char chary = '0' + y;
    char col   = '0' + color;
        buffer[4] = charx;
        buffer[5] = ' ';
        buffer[6] = chary;
        buffer[7] = ' ';
        buffer[8] = col;
        buffer[9] = '\n';

    n = send(sockfd, buffer, strlen(buffer), 0);
#ifndef RELEASE_MODE
    std::cout << "How much did you write?" << std::endl;
    std::cout << "Dis many: " << n << std::endl;
#endif

}


void event(int x, int y)
{
    int change = board[x][y] - prevBoard[x][y];
    if (change > 0) {
        // setDown
#ifndef RELEASE_MODE
        std::cout << "set down \n\n\n\n\n\n\n\n\n\n\n set down" << std::endl;
#endif
        tellJimmy(0, x, y, board[x][y]);
        prevBoard[x][y] = board[x][y];
    } else {
        // pickUp
#ifndef RELEASE_MODE
        std::cout << "pick up \n\n\n\n\n\n\n\n\n\n\n pick up" << std::endl;
#endif
        tellJimmy(1, x, y, board[x][y]);
        prevBoard[x][y] = board[x][y];
    }
} 

void checkBoard()
{
    for (int x = 0; x < 8; ++x) {
        for (int y = 0; y < 8; ++y) {
            if (board[x][y] != prevBoard[x][y]) {
                diffBoard[x][y]++;
                if (diffBoard[x][y] > FRAME_THRESHOLD) {
                    event(x, y);
                    diffBoard[x][y] = 0;
                }
            } else {
                diffBoard[x][y] = 0;
            }
        }
    }
}
void idPieces(cv::Mat &frame, cv::Mat &diffImg)
{
    //convert to grayscale
    cv::Mat gray;
    cv::cvtColor(diffImg, gray, cv::COLOR_BGR2GRAY);
    
    // blur to reduce false-positives
    cv::GaussianBlur(gray, gray, cv::Size(9,9), 2, 2);

    std::vector<cv::Vec3f> circles;
    // HoughCircles(src, dstarr, type, 
    //             dp, min_dist, param1, param2,  min_rad, max_rad);
    cv::HoughCircles(gray, circles, CV_HOUGH_GRADIENT, 
                    1, gray.rows/24, 60, 10, 4, 8 );//side/2+5
                                        //   2, 8
#ifdef DEBUG
    std::cout << "Circles found: " << circles.size() << std::endl;
#endif
    clearBoard();
    if (circles.size() == 0) {
#ifdef DEBUG
        std::cout << "No circles detected" << std::endl;
#endif
    } else {
        for (size_t circ = 0; circ < circles.size(); ++circ) {
            int circ_x = circles[circ][0];
            int circ_y = circles[circ][1];
            int circ_radius = circles[circ][2];
            cv::Point center(round(circ_x), round(circ_y));
            int radius = round(circles[circ][2]);
            bool red = isRed(center, radius, diffImg, frame);
            if (red) {
                cv::circle(frame, center, radius, cv::Scalar(0,255,0), 2);
                updateBoard(center, red);
            } else {
                cv::circle(frame, center, radius, cv::Scalar(255,0,0), 2);
                updateBoard(center, red);
            }
        }
    }
}

void runStatic(std::string framePath, std::string bgPath);

void videoFeed(cv::VideoCapture cap)
{
    cv::Mat bg, frame;

    char input;
    std::cout << "Please clear board so base image can be taken...\n";
    std::cin >> input;
    cv::waitKey(2000);
    std::cout << "Please wait while initial board is determined...\n";
    cap >> bg;
    std::cout << "Please set up the board." << std::endl;
    std::cin >> input;

    // crop bg to ROI
    bg = bg(myROI);
    while(1) {
        cap >> frame;
        checkFrames(frame, bg);

        // Get differences from background
        cv::Mat diffImg;

        // crop frames to ROI
        frame = frame(myROI);
        cv::absdiff(bg, frame, diffImg);
        idPieces(frame, diffImg);

        // Funsies to see canny if wanted
        cv::Mat canny;
        cv::Canny(diffImg, canny, 75/2, 75, 3);
#ifndef RELEASE_MODE
        cv::namedWindow("Canny", cv::WINDOW_AUTOSIZE);
        cv::imshow("Canny", canny);
        cv::namedWindow("Background", cv::WINDOW_AUTOSIZE);
        cv::imshow("Background", bg);
        cv::namedWindow("Capture", cv::WINDOW_AUTOSIZE);
        cv::imshow("Capture", frame);
        cv::namedWindow("Difference", cv::WINDOW_AUTOSIZE);
        cv::imshow("Difference", diffImg);
#endif
        checkBoard();
        cv::waitKey(50);

    }
}


int main(int argc, char* argv[])
{
    putOnSocks();
    std::cout << "0\n";
    std::string framePath;
    std::string bgPath;
    cv::VideoCapture cap;
    std::cout << "1\n";
    switch (checkArgs(argc)) {
        case 0: 
            std::cout << "2\n";
#ifdef RELEASE_MODE
            cap.open(1);
#else
            cap.open(0);
#endif
            std::cout << "3\n";
            checkCamInit(cap);
            std::cout << "4\n";
            break;
        default:
            std::cerr << argc << " is an unrecognized value for argc. Exiting program." << std::endl;
            return -1;
    }
        clearBoard();
        videoFeed(cap);

    return 0;
}

