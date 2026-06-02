/** Manual Implementation of a shell.
 * 
 * Time spent: 10 hours
 * 
 * Author: Kwasi Buansi
 */
#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include "parser.h"
#include <ctype.h>
#include <unistd.h>
#include <sys/wait.h>
#include <signal.h>

#define MAX_CMD_LENGTH 1000
#define MAX_HISTORY_LENGTH 10

typedef struct
{ // struct to contain command lines along with their associated IDs.
    char command[MAX_CMD_LENGTH];
    unsigned int commandID;
} history_t;
/** Runs the history command, which outputs the previous 10 commands, along with
 *  their associated IDs
 * 
 *  Parameters:
 *      historyBuffer[] = A ring buffer containing the last 10 commands.
 */
void runHistoryCommand(history_t historyBuffer[])
{
    for (int i = 0; i < MAX_HISTORY_LENGTH; ++i)
    {
        history_t currentCommand = historyBuffer[i];
        if (!strcmp(currentCommand.command, ""))
        {
            break;
        }
        printf("%d %s\n", currentCommand.commandID, currentCommand.command);
        fflush(stdout);
    }
}
/** Runs user inputted commands that are not built into the shell
 * 
 *  Parameters:
 *      runInBackground = Determines whether to run the shell in the background.
 *      cmdArgs = The tokenized command to be run.
 */
void runCommand(int runInBackground, char **cmdArgs)
{
    pid_t pid = fork();

    if (pid == -1)
    {
        printf("Error making a fork\n");
        exit(1);
    }
    else if (pid == 0)
    { // child
        execvp(cmdArgs[0], cmdArgs);
        printf("Invalid Command\n"); // reaches here if execvp fails
        fflush(stdout);
    }
    else if (pid)
    { // parent
        if (!runInBackground)
        { // we only wait if the program runs in the foreground
            wait(NULL);
        }
    }
}
/** Runs the history expansion command
 * 
 * 
 *  Parameters:
 *      cmdLineInput = The command line the user previously inputted.
 *      historyBuffer[] = A ring buffer containing the last 10 commands.
 *      *pointRunInBackground = A pointer to the integer that indicates whether to 
 *          run the command in the background
 *      setID = The ID of the most recent command in history. 
 */
void runHistoryExpansion(history_t cmdLineInput, history_t historyBuffer[], int 
    *pointRunInBackground, unsigned int *setID)
{
#define HISTORY_COMMAND "history"
    int eventFound = 0;
    int idCheck = cmdLineInput.command[1] - '0'; // subtract the character next to the '!' by 0 to convert to an integer
    for (int i = 0; i < MAX_HISTORY_LENGTH; ++i)
    {
        history_t currentCommand = historyBuffer[i];
        if (currentCommand.commandID == idCheck)
        {
            eventFound = 1;
            currentCommand.commandID = *setID;
            historyBuffer[(*setID - 1) % MAX_HISTORY_LENGTH] = currentCommand; // assign command to the appropriate place in the history buffer
            if (!strcmp(currentCommand.command, HISTORY_COMMAND))
            {
                runHistoryCommand(historyBuffer);
            }
            else
            {
                char **cmdHistArgs = parseCommand(currentCommand.command, 
                    pointRunInBackground); // the argument in history that we want to run
                runCommand(*pointRunInBackground, cmdHistArgs);
                for (int j = 0; cmdHistArgs[j] != NULL; ++j)
                {
                    free(cmdHistArgs[j]);
                }
                free(cmdHistArgs);
            }
            ++(*setID);
        }
    }
    if (!eventFound)
    {
        printf("Event not found\n");
        fflush(stdout);
    }
}
/** Removes all whitespaces in a parsed command.
 * 
 *  Parameters:
 *      *string = The parsed string to be run as a command.
 */
void removeSpaces(char *string)
{
    if (string == NULL || *string == '\0')
    { // we don't want to do anythint to an empty string
        return;
    }
    char *start = string;
    while (isspace((unsigned char)*start))
    { // move start to the end of the whitespaces
        start++;
    }
    char *end = start + strlen(start) - 1; // end is behind the null term
    while (end > start && isspace((unsigned char)*end))
    { // move end to the fist non-whitespace character
        end--;
    }
    *(end + 1) = '\0'; // add the null term

    if (start != string)
    {
        memmove(string, start, end - start + 2); // replace string with start
    }
}
/** Splits commands based on the ';'.
 *  
 *  Parameters:
 *      *string = the previously inputted command
 *      subCommands = Sublist containing each ';' seperated command in the line.
 *  Returns:
 *      The number of subcommands in the sublist.
 */
int splitCommands(const char *string, char subCommands[MAX_HISTORY_LENGTH]
    [MAX_CMD_LENGTH])
{
    if (string == NULL || strlen(string) == 0)
    { // again, ignore if an empty string
        return 0;
    }
    char *temp = strdup(string);
    char *token = strtok(temp, ";");
    int count = 0;

    while (token != NULL && count < MAX_HISTORY_LENGTH)
    {
        removeSpaces(token);
        if (strlen(token) > 0)
        {
            strncpy(subCommands[count], token, MAX_CMD_LENGTH - 1);
            subCommands[count][MAX_CMD_LENGTH - 1] = '\0';
            count++;
        }
        token = strtok(NULL, ";");
    }
    free(temp);
    return count;
}
/** Checks each user-inputted command and determines which function to run.
 *  
 * Returns: 
 *      0 if the exit command is run.    
 */
int main()
{
#define PROMPT "catshell> "
#define EXIT_COMMAND "exit"
#define BACKGROUND_CHECKER '&'
#define HIST_EXPANSION_COMMAND '!'
#define HISTORY_EXPANSION_LENGTH 2
    history_t historyBuffer[MAX_HISTORY_LENGTH] = {0};
    unsigned int setID = 1;
    while (1)
    {
        struct sigaction sa; // make a struct to ignore SIGCHLD - automatically reap children
        sa.sa_handler = SIG_IGN;
        sigemptyset(&sa.sa_mask);
        sa.sa_flags = 0;

        if (sigaction(SIGCHLD, &sa, NULL) == -1)
        {
            printf("Sigaction failed\n");
            exit(1);
        }

        history_t cmdLineInput;

        printf(PROMPT);
        fflush(stdout);

        fgets(cmdLineInput.command, sizeof(cmdLineInput.command), stdin);
        cmdLineInput.command[strcspn(cmdLineInput.command, "\n")] = '\0'; // strip the newline character

        char subCommands[MAX_HISTORY_LENGTH][MAX_CMD_LENGTH] = {0};
        int numSubCommands = splitCommands(cmdLineInput.command, subCommands);
        if (numSubCommands == 0) {continue;} // we skip everything below if an empty string

        for (int i = 0; i < numSubCommands; ++i)
        {
            int runInBackground = 0;                      // reset per command
            int *pointRunInBackground = &runInBackground; // bg input into parseCommand must be a pointer to an integer
            char *currentCommand = subCommands[i];

            char **cmdArgs = parseCommand(currentCommand, pointRunInBackground); // contains individual tokens
            char *firstArgument = cmdArgs[0];
            if (!strcmp(firstArgument, EXIT_COMMAND))
            {
                for (int i = 0; cmdArgs[i] != NULL; ++i)
                { // free memory allocated by paresCommand and end the program
                    free(cmdArgs[i]);
                }
                free(cmdArgs);
                return 0;
            }
            else if (!strcmp(firstArgument, HISTORY_COMMAND))
            {
                cmdLineInput.commandID = setID;                                 // inside the if statement because we don't want to do this when we use history expansion
                historyBuffer[(setID - 1) % MAX_HISTORY_LENGTH] = cmdLineInput; // assign command to the appropriate place in the history buffer
                runHistoryCommand(historyBuffer);
                ++setID;
            }
            else if (strlen(firstArgument) == HISTORY_EXPANSION_LENGTH // check for history expansion command
                     && currentCommand[0] == HIST_EXPANSION_COMMAND && 
                     isdigit(currentCommand[1]))
            {
                runHistoryExpansion(cmdLineInput, historyBuffer, 
                    pointRunInBackground, &setID);
            }
            else
            {
                cmdLineInput.commandID = setID;
                historyBuffer[(setID - 1) % MAX_HISTORY_LENGTH] = cmdLineInput; // assign command to the appropriate place in the history buffer
                runCommand(runInBackground, cmdArgs);
                ++setID;
            }
        }
    }
}