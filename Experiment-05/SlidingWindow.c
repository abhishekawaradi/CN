#include <stdio.h>

#define MAX_FRAMES 50

int main(void) {
    int windowSize;
    int frameCount;
    int frames[MAX_FRAMES];

    printf("Enter window size: ");
    if (scanf("%d", &windowSize) != 1 || windowSize <= 0) {
        fprintf(stderr, "Window size must be a positive integer.\n");
        return 1;
    }

    printf("Enter number of frames to transmit (1-%d): ", MAX_FRAMES);
    if (scanf("%d", &frameCount) != 1 || frameCount <= 0 || frameCount > MAX_FRAMES) {
        fprintf(stderr, "Frame count must be between 1 and %d.\n", MAX_FRAMES);
        return 1;
    }

    printf("Enter %d frame values: ", frameCount);
    for (int i = 0; i < frameCount; i++) {
        if (scanf("%d", &frames[i]) != 1) {
            fprintf(stderr, "Please enter an integer for every frame.\n");
            return 1;
        }
    }

    printf("\nFrames sent with a sliding window (assuming no frame corruption):\n\n");
    printf("The sender waits for an acknowledgement after each window of at most %d frames.\n\n",
           windowSize);

    for (int i = 0; i < frameCount; i++) {
        printf("%d", frames[i]);
        if ((i + 1) % windowSize == 0 || i == frameCount - 1) {
            printf("\nAcknowledgement received for the frames in this window.\n\n");
        } else {
            printf(" ");
        }
    }

    return 0;
}
