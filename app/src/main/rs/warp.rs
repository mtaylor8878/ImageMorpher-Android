#pragma version(1)
#pragma rs_fp_relaxed
#pragma rs java_package_name(taylor.matt.imagemorpher)

#include "rs_debug.rsh"

rs_allocation apP, apQ, apP2, apQ2, avPQ, avN, avPQ2, avN2;
rs_allocation inImage;
float         xOff, yOff, width, height;
size_t        num_lines;

// Check if point is within bounds of the given image
static bool checkBounds(float2 pX) {
    return (pX.x >= 0 && pX.x < width && pX.y >= 0 && pX.y < height);
}

uchar4 __attribute__((kernel)) root(uchar4 in, uint32_t x, uint32_t y) {
    float2 vXP, vPX, pX2, avgDelta, twDelta, pP, pQ, pP2, pQ2, vPQ, vN, vPQ2, vN2, wpX;
    float d, fL, percent, weight, tWeight, mPQ, mN2;
    float2 pX = (float2) {(float)x + xOff, (float)y + yOff};

    tWeight = 0;
    twDelta = 0;

    for(int i = 0; i < num_lines; i++) {
        // Read float values into float2 variables for RenderScript math functions
        pP = rsGetElementAt_float2(apP, i);
        pQ = rsGetElementAt_float2(apQ, i);
        pP2 = rsGetElementAt_float2(apP2, i);
        pQ2 = rsGetElementAt_float2(apQ2, i);
        vPQ = rsGetElementAt_float2(avPQ, i);
        vN = rsGetElementAt_float2(avN, i);
        vPQ2 = rsGetElementAt_float2(avPQ2, i);
        vN2 = rsGetElementAt_float2(avN2, i);

        mPQ = sqrt(pown(vPQ.x, 2) + pown(vPQ.y, 2));
        mN2 = sqrt(pown(vN2.x, 2) + pown(vN2.y, 2));
        vXP = (float2) {pP.x - pX.x, pP.y - pX.y};
        vPX = (float2) {pX.x - pP.x, pX.y - pP.y};

        // Calculate distance from pixel to line
        d = dot(vXP, vN) /  mPQ;
        fL = dot(vPX, vPQ) / mPQ;
        percent = fL / mPQ;

        pX2 = (float2){pP2.x + percent * vPQ2.x - d * vN2.x / mN2,
                pP2.y + percent * vPQ2.y - d * vN2.y / mN2};

        // If pixel has no perpindicular intersect to the line, use distance to closest point in weight
        if(percent > 1 || percent < 0) {
            float dp = distance(pX, pP);
            float dq = distance(pX, pQ);
            d = fmin(dp, dq);
        }
        weight = pown(1/(0.001 + d), 2);
        tWeight += weight;
        twDelta.x = twDelta.x + (pX2.x - pX.x) * weight;
        twDelta.y = twDelta.y + (pX2.y - pX.y) * weight;
    }

    avgDelta = (float2){twDelta.x / tWeight, twDelta.y / tWeight};
    wpX = (float2){avgDelta.x + x, avgDelta.y + y};

    // If corresponding pixel is outside the bounds of the image, set as white
    uchar4 outpixel;
    if(checkBounds(wpX)) {
        outpixel = rsGetElementAt_uchar4(inImage, (uint32_t)wpX.x, (uint32_t)wpX.y);
    } else {
        outpixel = rsPackColorTo8888(1,1,1,1);
    }

    return outpixel;
}