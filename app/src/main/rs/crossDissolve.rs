#pragma version(1)
#pragma rs_fp_relaxed
#pragma rs java_package_name(taylor.matt.imagemorpher)

#include "rs_debug.rsh"

rs_allocation right_image;
float         lWeight;
float         rWeight;

// Saturates channel if over the max value
static float bound (float val) {
 return fmin(1.0f, val);
}

uchar4 __attribute__((kernel)) root(uchar4 in, uint32_t x, uint32_t y) {
 uchar4 rInput = rsGetElementAt_uchar4(right_image, x, y);

 float4 p1 = rsUnpackColor8888(in);
 float4 p2 = rsUnpackColor8888(rInput);

 float r = bound(p1.r * lWeight + p2.r * rWeight);
 float g = bound(p1.g * lWeight + p2.g * rWeight);
 float b = bound(p1.b * lWeight + p2.b * rWeight);
 float a = bound(p1.a * lWeight + p2.a * rWeight);

 return rsPackColorTo8888(r, g, b, a);
}