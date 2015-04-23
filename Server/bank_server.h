#ifndef bank_server_h_
#define bank_server_h_
//#define MAP_ANONYMOUS MAP_ANON //cause cool kids use OSX

#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <errno.h>
#include <string.h>
#include <sys/socket.h>
#include <netdb.h>
#include <sys/types.h>
#include <signal.h>
#include <sys/ipc.h>
#include <sys/time.h>
#include <pthread.h>
#include <semaphore.h>
#include <sys/sem.h>
#include <sys/shm.h>
#include <fcntl.h>
#include <sys/mman.h>

#define ERROR -1

extern int server;
extern int flag;
sem_t * sem[20];
sem_t * rep;

/*
 * Creates a Bank object (wait C has objects?!)
 * fields: char account, string representaiton of account name
 * 		   double balance, current amount of money in the account
 *  	   short in_session, flag that lets bank know account is in use, 0 or 1
 */
typedef struct {

    char account[100];
    double balance;
    short in_session;

} Bank;

/*
 * helper function for SIGALARM
 * args: first_quantum, argument for unix timer
 * 		second_quantim, argument for unix timer
 * return: result of operation 
 */
//void start_timer( int first_quantum, int next_quantum );

/*
 * Repo object, a secondary data structure to seperate account create synchroization with access
 * fields: int avail is a 1 time flag that lets the bank know if this account space has been xalimed
 */
typedef struct {

	int avail;
	char account[100];

} Repo;

/*
 * logic for timer process
 * args: Repo * repo, pointer to repository in shared mem
 * 		Bank * accounts, pointer to accounts in shared memory 
 * return: result of operation 
 */
void print_bank(Repo * repo, Bank * accounts);

/*
 * Attempts to add a new account to the bank
 * args: char * name, account-to-be-created
 * 		Repo * repo, pointer to repository in shared mem
 * 		Bank * accounts, pointer to accounts in shared memory 
 * return: result of operation 
 */
char * create_account(Repo * repo, Bank * accounts, char * name);

/*
 * Encapsulates client-session logic
 * args: int sd, socket descriptor
 * 		Repo * repo, pointer to repository in shared mem
 * 		Bank * accounts, pointer to accounts in shared memory 
 * return: result of an operation 
 */
void serve_account(Bank * accounts, char * account_name, int sd);

/*
 * Wrapper fork function
 * args: void (* function), process logic, (void *, Repo *, Bank *), process arguments
 * 		void * ptr, socket descriptor
 * 		Repo * repo, pointer to repository in shared mem
 * 		Bank * accounts, pointer to accounts in shared memory 
 * return: N/A
 */
pid_t forkChild(void (* function) (void *, Repo *, Bank *), void * ptr, Repo * repo, Bank * accounts);

/*
 * Creates a shared memory region for a Bank struct
 * args: size_t size
 * return: Bank *, terminates program on failure
 */
Bank * createBank(size_t size);

/*
 * Creates a shared memory region for a Repo struct
 * args: size_t size
 * return: Repo *, terminates program on failure
 */
Repo* createMMAP(size_t size);

/*
 * Cleans up a shared memory region for a Bank struct
 * args: void * (Bank)
 * return: N/A
 */
void deleteBank(void * );

/*
 * Cleans up a shared memory region for a Repo struct
 * args: void * (Repo)
 * return: N/A
 */
void deleteMMAP ( void * addr );

/*
 * Attempts to create a socket file descriptor 
 * args: const char * port, string representation of a port
 * return: file descriptor (socket descriptor), -1 on failure, terminates program 
 */
int claim_port( const char * port );

/*
 * Encapsulates the logic which communicates with new client processes 
 * args: void * arg, a file descriptor (socket) in which can be used to communicate to the client
 * return: N/A
 */
void client_session ( void * arg, Repo * repo, Bank * bank );

/*
 * Wrapper for sem_wait(), uninterruptable 
 * args: sem_t * sem, a semaphore
 * return: ERROR VALUE
 */
static inline int sem_wait_nointr(sem_t *sem);

/*
 * Strcmp didn't cut it
 * args: char str1[], string to be compared 
 *		char str2[], string to be compared
 * return: 1 if equal 
 */
int stringCompare(char str1[],char str2[]);

#endif