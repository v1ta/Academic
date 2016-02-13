#ifndef bank_client_h_
#define bank_client_h_
#include <sys/types.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <errno.h>
#include <string.h>
#include <fcntl.h>
#include <sys/socket.h>
#include <pthread.h>
#include <signal.h>
#include <netdb.h>

extern int sd;
char message[512];

/*
 * Encapsulates communication to and from the bank server
 * args: void * arg, used to pass synchronization variables 
 * return: N/A
 */
void * talk_to_server(void * arg);

/*
 * Attempts to create a socket descriptor for two way communication
 * args: const char * server, a string representing a host name, const char * port, a string representing a port number
 * return: a file descriptor (socket descriptor), terminates program on failure;
 */
int connect_to_server( const char * server, const char * port );

/*
 * Prompts and input for create account/serve account/quite
 * args: N/A
 * return: int > 0, -1 if user wishes to quit
 */
int general_client_session();

/*
 * Prompts and input for withdraw/deposite/query/end
 * args: N/A
 * return: int > 0, -1 if user wishes to quit
 */
int account_client_session();

/*
 * Session toggle function
 * args: NA
 * return: N/A
 */
void in_session();

/*
 * Closes client, restores command-promt freeze.
 * args: NA
 * return: N/A
 */
void close_client();

#endif