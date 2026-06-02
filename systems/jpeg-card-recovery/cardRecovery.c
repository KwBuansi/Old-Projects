#include <stdio.h>
#include "io.h"

int recoverFiles() {
    /*  This function converts raw bytes to jpeg files.

        Author: Kwasi Buansi
        Time Spent: 4 Hours
    */
    #define SEQUENCE_SIZE 4
    #define SEQUENCE_1 0xFFD8FFE0
    #define SEQUENCE_2 0xFFD8FFE1
    #define STRING_SIZE 10
    #define FIRST_BYTE 0xFF
    #define SECOND_BYTE 0xD8
    #define TRUE 1
    #define FALSE 0
    #define THREE_BYTES 24
    #define TWO_BYTES 16
    #define ONE_BYTE 8
    #define inputFile "card.raw"
    #define firstEightBits 0xFFFFFFFF


    int counter = 0;
    char fileName[STRING_SIZE];
    unsigned long previousBytes = 0x00000000;
    int currentByte;
    int startNew;

    FILE *out = NULL;
    FILE *in = fopen(inputFile, "rb");

    if (in == NULL) {
        printf("Could not find file");
        exit(1);
    }

    while (TRUE) {
        currentByte = readByte(in);

        previousBytes = firstEightBits &
                            ((previousBytes << ONE_BYTE) | currentByte);
        
        startNew = (previousBytes == SEQUENCE_1) 
        || (previousBytes == SEQUENCE_2); 
        
        if (startNew == TRUE){
            if (counter == 0){
                counter++;
                snprintf(fileName, STRING_SIZE, "%d.jpg", counter);
                out = fopen(fileName, "wb");
            } else{
                fclose(out);
                counter++;
                snprintf(fileName, STRING_SIZE, "%d.jpg", counter);
                out = fopen(fileName, "wb");

            }
        }

        if (feof(in)){
            //this is past EOF
            writeByte(out, previousBytes >> THREE_BYTES);
            writeByte(out, previousBytes >> TWO_BYTES);
            writeByte(out, previousBytes >> ONE_BYTE);
            fclose(in);

            if (out != NULL){
                
                fclose(out);
            }
            break;
        }
        //this is before EOF
        if (counter > 0){
            writeByte(out, previousBytes >> THREE_BYTES);
        }
    }

    return counter;
}

int main() {
    printf("%d", recoverFiles());
    return 0;
}