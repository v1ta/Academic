#include "bank_server.h"

// To print clients that disconnect

//#define MAP_ANONYMOUS MAP_ANON //cause cool kids use OSX
#define ERROR -1

//42968
//49926

int server;
int flag;

char servermsg[512];


static void signal_handler( int signo )
{

	server = 0;

}

/*
static void start_timer( int first_quantum, int next_quantum )
{
	struct itimerval		interval;

	interval.it_interval.tv_sec = next_quantum;
	interval.it_interval.tv_usec = 0;		microseconds
	interval.it_value.tv_sec = first_quantum;
	interval.it_value.tv_usec = 0;
	setitimer( ITIMER_REAL, &interval, 0 );
}
*/

static void child_handler(int sig)
{
    pid_t pid;
    int status;

    while((pid = waitpid(-1, &status, WNOHANG)) > 0){
    	sprintf(servermsg, "SERVER: CLIENT-SESSION-PROCESS %d HAS DISCONNECTED\n", pid);
    	write(1, servermsg, strlen(servermsg));
    	//write(1, "Terminated client service process ", 40);
    	//write(1, pid, 50);
    	//write(1, "\n",2);
    }
        
}

static void timeout_handler( int signo, void * p )
{	
	flag = 1;
}

	/*
		Server      Client

	1	socket()	socket()

	2	bind()   	bind()   (optional for client)
		
	3	listen()    

	4				connect()

	5	accept()

	6	<-->SEND/RECIEVE<-->

	7	close()		close()
	 */



int main( int argc, char ** argv )
{
	/* Pointers for Bank Data Dtructure */
	Repo * repo;  
	Bank * accounts;
	server = 1;
	flag = 0;

	// Signal Handler SIGALARM
	/*
	struct sigaction	bank_timer;

	bank_timer.sa_flags = SA_SIGINFO | SA_RESTART;
	bank_timer.sa_sigaction = timeout_handler;		
	sigemptyset( &bank_timer.sa_mask );			
	sigaction( SIGALRM, &bank_timer, 0 );
	bank_timer.sa_flags = 0;
	bank_timer.sa_handler = signal_handler;		
	sigemptyset( &bank_timer.sa_mask );			
	sigaction( SIGINT, &bank_timer, 0 );

	start_timer( 20, 20 );
	*/

	// Signal handler SIGCHILD

	struct sigaction sa;

	sigemptyset(&sa.sa_mask);
	sa.sa_flags = 0;
	sa.sa_handler = child_handler;

	sigaction(SIGCHLD, &sa, NULL);  


	// Signal handler SIGINT

	struct sigaction action;

	action.sa_flags = 0;
	action.sa_handler = signal_handler;	
	sigemptyset( &action.sa_mask );		

	sigaction( SIGINT, &action, 0 );

	// Socket setup 

	int	sd;
	char message[256];
	socklen_t ic;
	int	fd;
	struct sockaddr_in senderAddr;
	int *	fdptr;
	//int status = 0;
	int i;
	
	/* Shared Memory Set-up */
	accounts = createBank( (sizeof(Bank) * 20) );

	char * str = (char *) malloc (sizeof (char) * 20);

	for(i = 0; i < 20; i++)
	{
		sprintf(str, "account%d", i);
		sem[i] = sem_open (str, O_CREAT | O_EXCL, 0644, 1); 
		sem_unlink (str); 
		memset(accounts[i].account, '\0', 100);
		accounts[i].balance = 0.0;
		accounts[i].in_session = 0;
	}

	rep = sem_open ("Repo", O_CREAT | O_EXCL, 0644, 1); 
	sem_unlink ("Repo"); 
	  
	repo = createMMAP ( sizeof(repo) * 20);

	for(i = 0; i < 20; i++)
	{
		repo[i].avail = 1;
		memset(repo[i].account, '\0', 100);
	}
	
	/* Claim Port */
	if ( (sd = claim_port( "49926" )) == -1 )
	{
		write( 1, message, sprintf( message,  "\x1b[1;31mCould not bind to port %s errno %s\x1b[0m\n", "49926", strerror( errno ) ) );
		return 1;
	}
	else if ( listen( sd, 100 ) == -1 )
	{
		/*
			sets the socket to a passive state which will listen for incoming
			requests via an accept() call(s). 

			sd = socket descriptor

			100 = maximum backlog (pending connections);
		 */

		printf( "listen() failed in file %s line %d\n", __FILE__, __LINE__ );
		close( sd );
		return 0;
	}
	else
	{
		ic = sizeof(senderAddr);

		/* Setup Alarm Process */
		if(fork() == 0){

			print_bank(repo, accounts);
			exit(0);

		}

		while ( server )
		{
			/* Accept new client */
			if( (fd = accept( sd, (struct sockaddr *)&senderAddr, &ic ) ) != -1 ){

				fdptr = (int *)malloc( sizeof(int) );
				*fdptr = fd;				

				/* Fork Client-session-process */
				if( forkChild(client_session,fdptr,repo,accounts) == -1 )
				{
					printf("SERVER: Failed to launch client-session\n");
					continue;

				}else{

					continue;

				}

			}else{

				continue;

			}

		}

		close( sd ); // close server socket
		deleteMMAP ( repo );
		deleteBank ( accounts );
		printf("\nSERVER: SHUTTING DOWN\n");


		return 0;
	}
}