/* A program that simulates Schelling's segregation model
 * given a file that lists out parameters.
 *
 * Time spent: 14 hours
 *
 * Author: Kwasi Buansi
 */
#include <stdlib.h>
#include <unistd.h>
#include <stdio.h>
#include <string.h>
#define VERBOSITY_INDEX 2     // verbosity is at index 2
#define INPUT_FILE_INDEX 1    // input file is at index 1
#define NUMBER_OF_ARGUMENTS 3 // must always have 3 user inputs
#define TWO_SECONDS 200000
#define NUM_PAIRS 2
/*  Initializes the board based on the provided in file.
 *
 *  Parameters:
 *      array - the 2D array to populate with the given values
 *      rows - the number of rows of the array
 *      cols - the number of columns of the array
 *      *in - the input file
 *      area - rows * cols
 */
void initializeBoard(char *array, int rows, int cols, FILE *in, int area)
{
    int m, n, totalCellType;

    for (int i = 0; i < area; i++)
    { // array starts with all empty spaces
        array[i] = ' ';
    }

    if (fscanf(in, "%d", &totalCellType) != 1)
    { // reads the numeber of $s
        printf("ERROR: Illegal Character\n");
        exit(2);
    }

    for (int i = 0; i < totalCellType; i++)
    { // initialize the $s
        if (fscanf(in, "%d%d", &m, &n) != 2 || (m >= rows || n >= cols))
        {
            printf("ERROR: Illegal Character\n");
            exit(2);
        }
        *(array + (m * cols + n)) = '$';
    }

    if (fscanf(in, "%d", &totalCellType) != 1)
    { // reads the number of .s
        printf("ERROR: Illegal Character\n");
        exit(2);
    }

    for (int i = 0; i < totalCellType; i++)
    { // initialize the .s
        if (fscanf(in, "%d%d", &m, &n) != 2 || (m >= rows || n >= cols))
        {
            printf("ERROR: Illegal Character\n");
            exit(2);
        }
        *(array + (m * cols + n)) = '.';
    }
}
/* Prints the current state of the board
 *
 *  Parameters:
 *      rows - the number of rows in the array
 *      cols - the number of columns in the array
 *      array - the 2D array to print
 *
 */
void printBoard(int rows, int cols, char *array)
{
    for (int i = 0; i < rows; ++i)
    {
        for (int j = 0; j < cols; ++j)
        {
            printf("%c", array[i * cols + j]);
        }
        printf("\n");
    }
}
/* Checks an element's adjacent cells and calculates numSimilarNeighbors based
 * on the number of similar neighbors, and calculates the total number of valid
 * neighbors.
 *
 * Parameters:
 *      i - the row of the element being checked
 *      j - the column of the element being checked
 *      array - the 2D array
 *      rows - the total number of rows in the array
 *      cols - the total number of cols in the array
 *      *totalAdjacentCells - the total number of valid adjacent cells
 *      currentSymbol - the symbol of the element being checked
 *      (either '$' or '.')
 *      *numSimilarNeighbors - the satisfaction level of the element, to be
 *      divided by the total number of valid neighbors to find the satisfaction
 *      ratio
 *      threshold - the ratio of similar neighbors, indicated in the input file,
 *      that must be met or exceeded in order for the element to be satisfied
 *      dissatisfiedCells - an array that contains the ordered pairs of the
 *      dissatisfied elements to be shifted after the round is over
 *      indexDissatisfiedCells - the index of the ordered pair values of the
 *      dissatisfied cells
 */
void checkAdjacent(int i, int j, char *array, int rows,
                   int cols, int *totalAdjacentCells, char currentSymbol,
                   float *numSimilarNeighbors, float threshold, int 
                   *dissatisfiedCells, int *indexDissatisfiedCells)
{
    char adjacentSymbol;
    for (int ai = -1; ai <= 1; ++ai)
    { // check adjacent rows
        for (int aj = -1; aj <= 1; ++aj)
        { // check adjacent columns
            if (!(ai == 0 && aj == 0))
            { // if we're at the current index, ignore
                int ni = i + ai;
                int nj = j + aj;
                adjacentSymbol = *(array + ((ni)*cols + (nj)));
                if (ni >= 0 && ni < rows && nj >= 0 && nj < cols 
                    && adjacentSymbol != ' ')
                { // adjacent cell must be valid
                    ++(*totalAdjacentCells);
                    if (currentSymbol == adjacentSymbol)
                    {
                        ++(*numSimilarNeighbors);
                    }
                }
            }
        }
    }
    if (totalAdjacentCells > 0)
    {                                                      // if no valid adjacent cells, it's satisfied
        if ((*numSimilarNeighbors) / (*totalAdjacentCells) // if dissatisfied,
            < threshold)
        { // put the orded pair in dissatisfiedCells
            dissatisfiedCells[*indexDissatisfiedCells] = i;
            dissatisfiedCells[*indexDissatisfiedCells + 1] = j;
            *indexDissatisfiedCells += 2;
        }
    }
}
/* Moves a cell to the next vacant space
 *
 *  Parameters:
 *      totalAdjacentCells - the total number of valid adjacent cells
 *      (not empty or out-of-bounds)
 *      *array - the 2D array
 *      i - the row of the element to shift
 *      j - the column of the element to shift
 *      rows - the number of rows in the array
 *      cols - the number of columns in the array
 *      area - rows * cols
 */
void shiftCells(int totalAdjacentCells, char *array,
                int i, int j, int rows, int cols, int area)
{
    int nextIndex;
    int currentIndex = i * cols + j;
    char currentSymbol = *(array + (currentIndex));
    *(array + (currentIndex)) = ' '; // cell becomes vacant after moving
    for (int in = 1; in < area; ++in)
    { // look for the next empty space
        nextIndex = (currentIndex + in) % area;
        if (*(array + nextIndex) == ' ')
        {
            *(array + nextIndex) = currentSymbol;
            break;
        }
    }
}
/* After calling the initializeBoard function, runs the Schelling algorithm
 *
 *  Parameters:
 *      verbosityLevel - a user inputted value that ranges from 0 - 2. 0 = No
 *      final output will be printed. 1 = Print the final output. 2 = Print the
 *      output after each round of the algortithm along with the initial state.
 *      *in - The input file that contains the parameters for the simulation
 *      to follow
 */
void runAlgorithm(int verbosityLevel, FILE *in)
{
    int rows, cols, iterations;       // iterations = total number of times to run sim
    float threshold;                  // the satisfaction threshold required to be "satisfied"
    float numSimilarNeighbors;        // float b/c it'll be divided eventually
    int totalAdjacentCells;           // must be in-bounds or not empty to count as valid
    char currentSymbol;               // the symbol who's satisfaction currently being checked
    fscanf(in, "%d%d", &rows, &cols); // rows and columns = first line
    fscanf(in, "%d", &iterations);    // iterations = second line
    fscanf(in, "%f", &threshold);     // etc...
    int area = rows * cols;

    int *dissatisfiedCells;                    // array containg coords of dissatisfied elements
    int indexDissatisfiedCells;                // y coord will immediately follow the index of x
    char *array = malloc(sizeof(char) * area); // row x column array
    if (array == NULL)
    {
        printf("ERROR: Could not allocate memory\n");
        exit(3);
    }
    initializeBoard(array, rows, cols, in, area);
    if (verbosityLevel == 2)
    {
        printBoard(rows, cols, array);
    }
    for (int round = 0; round < iterations; ++round)
    { // represents the round
        indexDissatisfiedCells = 0;
        dissatisfiedCells = malloc(sizeof(int) * (area *
                                                  NUM_PAIRS)); // area * 2 to get the maximum possible number of coords
        if (dissatisfiedCells == NULL)
        {
            printf("ERROR: Could not allocate memory\n");
            exit(3);
        }
        for (int i = 0; i < rows; ++i)
        {
            for (int j = 0; j < cols; ++j)
            {
                totalAdjacentCells = 0;
                numSimilarNeighbors = 0.0;
                currentSymbol = *(array + (i * cols + j));
                if (currentSymbol != ' ')
                { // element is ignored if its empty
                    checkAdjacent(i, j, array, rows, cols,
                                  &totalAdjacentCells, currentSymbol, 
                                  &numSimilarNeighbors, threshold, 
                                  dissatisfiedCells, &indexDissatisfiedCells);
                }
            }
        }
        if (indexDissatisfiedCells > 0)
        {
            for (int id = 0; id < indexDissatisfiedCells; id += 2)
            {
                int i = dissatisfiedCells[id];
                int j = dissatisfiedCells[id + 1];
                shiftCells(totalAdjacentCells, array, i, j, rows, cols, area);
            }
        }
        free(dissatisfiedCells); // reset dissatisfied cells after a round
        if (verbosityLevel == 2)
        {
            printBoard(rows, cols, array);
#pragma GCC diagnostic push
#pragma GCC diagnostic ignored "-Wdeprecated-declarations"
            usleep(TWO_SECONDS);
#pragma GCC diagnostic pop
            system("clear");
        }
    }
    if (verbosityLevel >= 1)
    {
        printBoard(rows, cols, array);
    }
    free(array);
    fclose(in);
}
/* Handles the user-inputted file, checks for errors, and calls the function
 *  that runs the algorithm.
 *
 *  Parameters:
 *      argc - the number of arguments the user provides
 *      *argv[] - an array that contains the string names of each user input
 *
 *  Returns:
 *      0 if the output is successful.
 */
int main(int argc, char *argv[])
{
    if (argc != NUMBER_OF_ARGUMENTS)
    {
        printf("ERROR: Must have 3 inputs");
        exit(1);
    }
    if (strcmp(argv[VERBOSITY_INDEX], "0") != 0)
    {
        if (atoi(argv[VERBOSITY_INDEX]) == 0)
        {
            printf("ERROR: Invalid verbosity level");
            exit(1);
        }
    }
    int verbosityLevel = atoi(argv[VERBOSITY_INDEX]);
    FILE *in = fopen(argv[INPUT_FILE_INDEX], "r");
    if (verbosityLevel != 0 // verbosity must be 0, 1, or 2
        && verbosityLevel != 1 && verbosityLevel != 2)
    {
        printf("ERROR: Invalid verbosity level");
        exit(1);
    }
    else if (in == NULL)
    {
        printf("ERROR: Could not find file");
        exit(1);
    }
    runAlgorithm(verbosityLevel, in);
    return 0;
}